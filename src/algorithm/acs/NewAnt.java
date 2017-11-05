package algorithm.acs;

import core.Customer;
import core.Route;
import core.RouteSegment;

import java.util.*;

public class NewAnt {
    /**
     * Parametr okreslajacy proporcje miedzy eksploatacja najlepszej krawedzi i eksploracja nowej
     */
    private static double q0;

    /**
     * Parametr regulujacy wpływ ni (odwrotnosc odległosci)
     */
    private static double beta; //preferowana wartość to 2-5

    /**
     * Lista nieodwiedzonych jeszcze klientow
     */
    private List<Integer> unvisitedCustomersID;

    /**
     * Lista dostepnych klientow, do ktorych moze isc mrowka w najblizszym kroku
     */
    private List<RouteSegment> feasibleRouteSegments;

    private Map<Integer, Double> explorationProbabilities;

    private Map<Integer, Double> exploitationRates;

    private double exploitationRateSum;

    /**
     * Tworzy mrowke
     */
    NewAnt() {
        feasibleRouteSegments = new ArrayList<>();
        unvisitedCustomersID = new ArrayList<>();
        explorationProbabilities = new HashMap<>();
        exploitationRates = new HashMap<>();
        exploitationRateSum = 0.0;
    }

    public static double getQ0() {
        return q0;
    }

    public static void setQ0(double q0) {
        NewAnt.q0 = q0;
    }

    public static double getBeta() {
        return beta;
    }

    public static void setBeta(double beta) {
        NewAnt.beta = beta;
    }

    public List<Integer> getUnvisitedCustomersID() {
        return unvisitedCustomersID;
    }

    public void setUnvisitedCustomersID(List<Integer> unvisitedCustomersID) {
        this.unvisitedCustomersID = unvisitedCustomersID;
    }

    public List<RouteSegment> getFeasibleRouteSegments() {
        return feasibleRouteSegments;
    }

    public void setFeasibleRouteSegments(List<RouteSegment> feasibleRouteSegments) {
        this.feasibleRouteSegments = feasibleRouteSegments;
    }

    public Map<Integer, Double> getExplorationProbabilities() {
        return explorationProbabilities;
    }

    public void setExplorationProbabilities(Map<Integer, Double> explorationProbabilities) {
        this.explorationProbabilities = explorationProbabilities;
    }

    public Map<Integer, Double> getExploitationRates() {
        return exploitationRates;
    }

    public void setExploitationRates(Map<Integer, Double> exploitationRates) {
        this.exploitationRates = exploitationRates;
    }

    public void updateFeasibleRouteSegments(Customer tmpCustomer, Route tmpRoute, double weightLimit, double sizeLimit) {
        feasibleRouteSegments.clear();
        for (RouteSegment rs : tmpCustomer.getRouteSegmentsFromCustomer()) {
            Customer dst = rs.getDst();
            if (unvisitedCustomersID.contains(dst.getId())) { //jeśli klient jest na liście nieodwiedzonych klientów
                if (tmpRoute.canAdd(dst.getPackageWeight(), weightLimit, dst.getPackageSize(), sizeLimit)) { //jeśli klient może zostać dodany do trasy (dopisać warunki czasowe dla VRPTW)
                    feasibleRouteSegments.add(rs);
                }
            }
        }
    }

    /**
     * Usuwa klienta z listy nieodwiedzonych klientow
     *
     * @param idToRemove Id klienta, ktorego nalezy usunac
     */
    void removeFromUnvisitedCustomers(int idToRemove) {
        Integer id = idToRemove;
        unvisitedCustomersID.remove(id);
    }

    public RouteSegment chooseNextRouteSegment(Customer tmpCustomer, Map<Integer, Double> localPheromoneLevel, boolean isNearestNeighbourSearch) {
        exploitationRateSum = 0.0;
        exploitationRates.clear();
        explorationProbabilities.clear();

        RouteSegment nextRouteSegment;

        if (isNearestNeighbourSearch) {
            nextRouteSegment = chooseNearestNeighbour();
        } else {
            if (tmpCustomer.getId() == 0) { //jeżeli mrówka znajduje się w magazynie wybiera losowo miasto z listy dostępnych miast
                nextRouteSegment = chooseRandomRouteSegment();
            } else { //jeżeli mrówka jest w dowolnym węźle (ale nie w magazynie), wówczas wybiera kolejne miasto zgodnie z zasadami ACS
                RouteSegment bestRouteSegment = chooseBestRouteSegment(localPheromoneLevel);
                if (new Random().nextDouble() <= q0) {
                    nextRouteSegment = bestRouteSegment; //eksploatacja najlepszego odcinka
                } else {
                    nextRouteSegment = chooseProbabilityRandomRouteSegment(); //losowy wybór odcinka uwzględniając prawdopodobieństwo
                }
            }
        }
        return nextRouteSegment;
    }

    private RouteSegment chooseNearestNeighbour() {
        RouteSegment nearestRouteSegment = null;
        double minDistance = 0;
        for (RouteSegment rs : feasibleRouteSegments) {
            if (minDistance == 0 || rs.getDistance() < minDistance) {
                minDistance = rs.getDistance();
                nearestRouteSegment = rs;
            }
        }
        return nearestRouteSegment;
    }

    private RouteSegment chooseRandomRouteSegment() {
        RouteSegment randomRouteSegment;

        int randomValue = new Random().nextInt(feasibleRouteSegments.size());
        randomRouteSegment = feasibleRouteSegments.get(randomValue);

        return randomRouteSegment;
    }

    private RouteSegment chooseBestRouteSegment(Map<Integer, Double> localPheromoneLevel) {
        RouteSegment bestRouteSegment = null;
        double bestExploitationRate = 0.0;
        for (RouteSegment rs : feasibleRouteSegments) {
            double ni = 1 / rs.getDistance();
            double tau = localPheromoneLevel.get(rs.getId());
            double exploitationRate = tau * (Math.pow(ni, beta)); //licznik
            exploitationRates.put(rs.getId(), exploitationRate);
            exploitationRateSum = exploitationRateSum + exploitationRate; //mianownik
            if (exploitationRate > bestExploitationRate) {
                bestExploitationRate = exploitationRate;
                bestRouteSegment = rs;
            }
        }
        return bestRouteSegment;
    }

    private RouteSegment chooseProbabilityRandomRouteSegment() {
        double weightSum = calculateProbabilitiesForAllFeasibleRouteSegments();

        RouteSegment choosedRouteSegment = null;
        double value = new Random().nextDouble() * weightSum; //zakres 0-1 * suma wag
        for (RouteSegment rs : feasibleRouteSegments) {
            if (explorationProbabilities.containsKey(rs.getId())) {
                value = value - explorationProbabilities.get(rs.getId());
                if (value <= 0) {
                    choosedRouteSegment = rs;
                    break;
                }
            }
        }
        return choosedRouteSegment;
    }

    private double calculateProbabilitiesForAllFeasibleRouteSegments() {
        double probabilitiesSum = 0.0;
        for (Map.Entry<Integer, Double> entry : exploitationRates.entrySet()) {
            double probability = entry.getValue() / exploitationRateSum;
            explorationProbabilities.put(entry.getKey(), probability);
            probabilitiesSum = probabilitiesSum + probability;
        }
        return probabilitiesSum;
    }
}
