package algorithm.acs;

import core.Customer;
import core.Route;
import core.RouteSegment;

import java.math.BigDecimal;
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

    private Map<Integer, BigDecimal> explorationProbabilities;

    private Map<Integer, BigDecimal> exploitationRates;

    private BigDecimal exploitationRateSum;

    /**
     * Tworzy mrowke
     */
    NewAnt() {
        feasibleRouteSegments = new ArrayList<>();
        unvisitedCustomersID = new ArrayList<>();
        explorationProbabilities = new HashMap<>();
        exploitationRates = new HashMap<>();
        exploitationRateSum = BigDecimal.ZERO;
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

    public Map<Integer, BigDecimal> getExplorationProbabilities() {
        return explorationProbabilities;
    }

    public void setExplorationProbabilities(Map<Integer, BigDecimal> explorationProbabilities) {
        this.explorationProbabilities = explorationProbabilities;
    }

    public Map<Integer, BigDecimal> getExploitationRates() {
        return exploitationRates;
    }

    public void setExploitationRates(Map<Integer, BigDecimal> exploitationRates) {
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

    public RouteSegment chooseNextRouteSegment(Customer tmpCustomer, Map<Integer, BigDecimal> localPheromoneLevel) {
        exploitationRateSum = BigDecimal.ZERO;
        exploitationRates.clear();
        explorationProbabilities.clear();

        RouteSegment nextRouteSegment;

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
        return nextRouteSegment;
    }

    private RouteSegment chooseRandomRouteSegment() {
        RouteSegment nextRouteSegment;

        int randomValue = new Random().nextInt(feasibleRouteSegments.size());
        nextRouteSegment = feasibleRouteSegments.get(randomValue);

        return nextRouteSegment;
    }

    private RouteSegment chooseBestRouteSegment(Map<Integer, BigDecimal> localPheromoneLevel) {
        RouteSegment bestRouteSegment = null;
        BigDecimal bestExploitationRate = BigDecimal.ZERO;
        for (RouteSegment rs : feasibleRouteSegments) {
            double ni = 1 / rs.getDistance();
            BigDecimal tau = localPheromoneLevel.get(rs.getId());
            BigDecimal exploitationRate = tau.multiply(BigDecimal.valueOf(Math.pow(ni, beta))); //licznik
            exploitationRates.put(rs.getId(), exploitationRate);
            exploitationRateSum = exploitationRateSum.add(exploitationRate); //mianownik
            if (exploitationRate.compareTo(bestExploitationRate) > 0) {
                bestExploitationRate = exploitationRate;
                bestRouteSegment = rs;
            }
        }
        return bestRouteSegment;
    }

    private RouteSegment chooseProbabilityRandomRouteSegment() {
        BigDecimal weightSum = calculateProbabilitiesForAllFeasibleRouteSegments();

        RouteSegment choosedRouteSegment = null;
        BigDecimal value = BigDecimal.valueOf(new Random().nextDouble()).multiply(weightSum); //zakres 0-1 * suma wag
        for (RouteSegment rs : feasibleRouteSegments) {
            if (explorationProbabilities.containsKey(rs.getId())) {
                value = value.subtract(explorationProbabilities.get(rs.getId()));
                if (value.compareTo(BigDecimal.ZERO) <= 0) {
                    choosedRouteSegment = rs;
                    break;
                }
            }
        }
        return choosedRouteSegment;
    }

    private BigDecimal calculateProbabilitiesForAllFeasibleRouteSegments() {
        BigDecimal probabilitiesSum = BigDecimal.ZERO;
        for (Map.Entry<Integer, BigDecimal> entry : exploitationRates.entrySet()) {
            BigDecimal probability = entry.getValue().divide(exploitationRateSum, BigDecimal.ROUND_HALF_UP);
            explorationProbabilities.put(entry.getKey(), probability);
            probabilitiesSum = probabilitiesSum.add(probability);
        }

        return probabilitiesSum;
    }
}
