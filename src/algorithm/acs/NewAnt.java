package algorithm.acs;

import core.Customer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private List<Integer> unvisitedCustomers;

    /**
     * Lista dostepnych klientow, do ktorych moze isc mrowka w najblizszym kroku
     */
    private List<Customer> feasibleNodes;

    private Map<Integer, BigDecimal> explorationProbabilities;

    private Map<Integer, BigDecimal> exploitationRates;

    /**
     * Tworzy mrowke
     */
    NewAnt() {
        feasibleNodes = new ArrayList<>();
        unvisitedCustomers = new ArrayList<>();
        explorationProbabilities = new HashMap<>();
        exploitationRates = new HashMap<>();
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

    public List<Integer> getUnvisitedCustomers() {
        return unvisitedCustomers;
    }

    public void setUnvisitedCustomers(List<Integer> unvisitedCustomers) {
        this.unvisitedCustomers = unvisitedCustomers;
    }

    public List<Customer> getFeasibleNodes() {
        return feasibleNodes;
    }

    public void setFeasibleNodes(List<Customer> feasibleNodes) {
        this.feasibleNodes = feasibleNodes;
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
}
