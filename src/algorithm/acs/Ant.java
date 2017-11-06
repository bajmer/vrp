package algorithm.acs;

import core.Customer;
import core.Route;
import core.RouteSegment;

import java.util.*;

public class Ant {
    /**
     * Parametr okreslajacy proporcje miedzy eksploatacja najlepszej krawedzi i eksploracja nowej
     */
    private static double q0;

    /**
     * Parametr regulujacy wpływ ni (odwrotnosc odległosci)
     */
    private static int beta;

    /**
     * Lista numerow ID nieodwiedzonych jeszcze przez mrowke klientow
     */
    private List<Integer> unvisitedCustomersID;

    /**
     * Lista dostepnych odcinkow, ktore moze wybrac mrowka w najblizszym kroku
     */
    private List<RouteSegment> feasibleRouteSegments;

    /**
     * Mapa okreslajaca prawdopodobienstwo eksploracji dostepnych odcinkow, klucz: ID odcinka, wartosc: prawdopodobienstwo wylosowania tego odcinka
     */
    private Map<Integer, Double> explorationProbabilities;

    /**
     * Mapa okreslajaca wspolczynniki eksploatacji dostepnych odcinkow, klucz: ID odcinka, wartosc: wspolczynnik okreslajacy jak dobry jest odcinek do eksploatacji zalezny od ilosci feromonu i dlugosci odcinka
     */
    private Map<Integer, Double> exploitationRates;

    /**
     * Suma wspolczynnikow eksploatacji odcinkow wykorzystywana do obliczenia pradopodobienstwa eksploracji odcinkow
     */
    private double exploitationRatesSum;

    /**
     * Tworzy mrowke
     */
    Ant() {
        feasibleRouteSegments = new ArrayList<>();
        unvisitedCustomersID = new ArrayList<>();
        explorationProbabilities = new HashMap<>();
        exploitationRates = new HashMap<>();
        exploitationRatesSum = 0.0;
    }

    public static double getQ0() {
        return q0;
    }

    public static void setQ0(double q0) {
        Ant.q0 = q0;
    }

    public static int getBeta() {
        return beta;
    }

    static void setBeta(int beta) {
        Ant.beta = beta;
    }

    List<Integer> getUnvisitedCustomersID() {
        return unvisitedCustomersID;
    }

    public void setUnvisitedCustomersID(List<Integer> unvisitedCustomersID) {
        this.unvisitedCustomersID = unvisitedCustomersID;
    }

    List<RouteSegment> getFeasibleRouteSegments() {
        return feasibleRouteSegments;
    }

    public void setFeasibleRouteSegments(List<RouteSegment> feasibleRouteSegments) {
        this.feasibleRouteSegments = feasibleRouteSegments;
    }

    Map<Integer, Double> getExplorationProbabilities() {
        return explorationProbabilities;
    }

    public void setExplorationProbabilities(Map<Integer, Double> explorationProbabilities) {
        this.explorationProbabilities = explorationProbabilities;
    }

    Map<Integer, Double> getExploitationRates() {
        return exploitationRates;
    }

    public void setExploitationRates(Map<Integer, Double> exploitationRates) {
        this.exploitationRates = exploitationRates;
    }

    /**
     * Aktualizuje dostepne odcinki, ktorymi moze poruszac sie mrowka w kolejnym kroku
     *
     * @param tmpCustomer Biezacy klient, u ktorego znajduje sie mrowka
     * @param tmpRoute    Biezaca trasa
     * @param weightLimit Maksymalna dopuszczalna masa ladunku
     * @param sizeLimit   Maksymalna dopuszczalna objetosc ladunku
     */
    void updateFeasibleRouteSegments(Customer tmpCustomer, Route tmpRoute, double weightLimit, double sizeLimit) {
        feasibleRouteSegments.clear();
        for (RouteSegment rs : tmpCustomer.getRouteSegmentsFromCustomer()) {
            Customer dst = rs.getDst();
            if (unvisitedCustomersID.contains(dst.getId())) {
                if (tmpRoute.canAdd(dst.getPackageWeight(), weightLimit, dst.getPackageSize(), sizeLimit)) {
                    feasibleRouteSegments.add(rs);
                }
            }
        }
    }

    /**
     * Usuwa ID klienta z listy numerow ID nieodwiedzonych jeszcze przez mrowke klientow
     *
     * @param idToRemove Numer ID klienta, ktory nalezy usunac
     */
    void removeFromUnvisitedCustomers(int idToRemove) {
        Integer id = idToRemove;
        unvisitedCustomersID.remove(id);
    }

    /**
     * Wybiera kolejny odcinek, ktory zostanie dodany przez mrowke do trasy
     * @param tmpCustomer Biezacy klient
     * @param pheromoneLevel Mapa okreslajaca poziom feromonu na odcinkach
     * @param isNearestNeighbourSearch Flaga okreslajaca, czy obecnie trwa budowanie trasy przez mrowke w trybie najblizszego klienta
     * @return Zwraca kolejny odcinek, ktory zostanie dodany przez mrowke do trasy
     */
    RouteSegment chooseNextRouteSegment(Customer tmpCustomer, Map<Integer, Double> pheromoneLevel, boolean isNearestNeighbourSearch) {
        exploitationRatesSum = 0.0;
        exploitationRates.clear();
        explorationProbabilities.clear();

        RouteSegment nextRouteSegment;

        if (isNearestNeighbourSearch) {
            nextRouteSegment = chooseNearestNeighbour();
        } else {
            if (tmpCustomer.getId() == 0) { //jeżeli mrówka znajduje się w magazynie, wybiera losowo miasto z listy dostępnych miast
                nextRouteSegment = chooseRandomRouteSegment();
            } else { //jeżeli mrówka jest w dowolnym węźle (ale nie w magazynie), wówczas wybiera kolejne miasto zgodnie z zasadami ACS
                RouteSegment bestRouteSegment = chooseBestRouteSegment(pheromoneLevel);
                if (new Random().nextDouble() <= q0) {
                    nextRouteSegment = bestRouteSegment; //eksploatacja najlepszego odcinka
                } else {
                    nextRouteSegment = chooseProbabilityRandomRouteSegment(); //losowy wybór odcinka uwzględniając prawdopodobieństwo
                }
            }
        }
        return nextRouteSegment;
    }

    /**
     * Wybiera kolejny odcinek trasy w oparciu o najblizszego klienta
     * @return Zwraca najkrotszy dostepny odcinek trasy
     */
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

    /**
     * Wybiera losowo pierwszy odcinek trasy
     * @return Zwraca losowy odcinek, ktory bedzie pierwszym odcinkiem trasy
     */
    private RouteSegment chooseRandomRouteSegment() {
        RouteSegment randomRouteSegment;
        int randomValue = new Random().nextInt(feasibleRouteSegments.size());
        randomRouteSegment = feasibleRouteSegments.get(randomValue);

        return randomRouteSegment;
    }

    /**
     * Oblicza wspolczynniki eksploatacji dla dostepnych odcinkow i wybiera odcinek o najlepszym wspolczynniku
     * @param pheromoneLevel Mapa okreslajaca ilosc feromonu na odcinkach
     * @return Zwraca odcinek o najlepszym wspolczynniku eksploatacji
     */
    private RouteSegment chooseBestRouteSegment(Map<Integer, Double> pheromoneLevel) {
        RouteSegment bestRouteSegment = null;
        double bestExploitationRate = 0.0;
        for (RouteSegment rs : feasibleRouteSegments) {
            double ni = 1 / rs.getDistance();
            double tau = pheromoneLevel.get(rs.getId());
            double exploitationRate = tau * (Math.pow(ni, beta));
            exploitationRates.put(rs.getId(), exploitationRate);
            exploitationRatesSum = exploitationRatesSum + exploitationRate;
            if (exploitationRate > bestExploitationRate) {
                bestExploitationRate = exploitationRate;
                bestRouteSegment = rs;
            }
        }
        return bestRouteSegment;
    }

    /**
     * Wybiera losowo kolejny odcinek trasy w oparciu o prawdopodobienstwo
     * @return Zwraca losowy odcinek wylosowany w oparciu o prawdopodobienstwo
     */
    private RouteSegment chooseProbabilityRandomRouteSegment() {
        calculateProbabilitiesForAllFeasibleRouteSegments();

        RouteSegment chosenRouteSegment = null;
        double value = new Random().nextDouble();
        for (RouteSegment rs : feasibleRouteSegments) {
            if (explorationProbabilities.containsKey(rs.getId())) {
                value = value - explorationProbabilities.get(rs.getId());
                if (value <= 0) {
                    chosenRouteSegment = rs;
                    break;
                }
            }
        }
        return chosenRouteSegment;
    }

    /**
     * Oblicza prawdopodobienstwo eksploracji odcinkow
     */
    private void calculateProbabilitiesForAllFeasibleRouteSegments() {
        for (Map.Entry<Integer, Double> entry : exploitationRates.entrySet()) {
            double probability = entry.getValue() / exploitationRatesSum;
            explorationProbabilities.put(entry.getKey(), probability);
        }
    }
}
