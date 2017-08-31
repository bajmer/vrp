package com.vrp.bajmer.algorithm.macs;

import com.vrp.bajmer.core.Customer;
import com.vrp.bajmer.core.Route;
import com.vrp.bajmer.core.RouteSegment;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Marcin on 2017-08-27.
 */
class Ant {

    private static int antID = 1;
    private static double alfa; //parametr regulujący wpływ tau (ilości feromonu), preferowana wartość to "1"
    private static double beta; //parametr regulujący wpływ ni (odwrotność odległości), preferowana wartość to 2-5
    private int id;
    private List<Integer> unvisitedCustomers;
    private List<Customer> feasibleNodes;


    Ant(List<Customer> customers) {
        id = antID;
        antID++;

        feasibleNodes = new ArrayList<>();
        unvisitedCustomers = new ArrayList<>();

//        tworzona jest lista nieodwiedzonych klientów
        for (Customer c : customers) {
            unvisitedCustomers.add(c.getId());
        }

    }

    static double getAlfa() {
        return alfa;
    }

    static void setAlfa(double alfa) {
        Ant.alfa = alfa;
    }

    static double getBeta() {
        return beta;
    }

    static void setBeta(double beta) {
        Ant.beta = beta;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    boolean updateFeasibleNodes(int tmpNodeId, List<RouteSegment> routeSegments, Route route, double weightLimit, double sizeLimit) {
        feasibleNodes.clear();
        for (RouteSegment rs : routeSegments) {
            Customer c = null;
            if (rs.getSrc().getId() == tmpNodeId) {
                c = rs.getDst();
            } else if (rs.getDst().getId() == tmpNodeId) {
                c = rs.getSrc();
            }

//            if c is on unvisited list
            if (c != null && isUnvisited(c.getId())) {
//                if customer can be added (dopisać warunki czasowe)
                if (route.canAdd(c.getPackageWeight(), weightLimit, c.getPackageSize(), sizeLimit)) {
                    feasibleNodes.add(c);
                }
            }
        }
        return feasibleNodes.size() != 0;
    }

    private boolean isUnvisited(int id) {
        for (int i : unvisitedCustomers) {
            if (i == id) {
                return true;
            }
        }
        return false;
    }

    void removeFromUnvisitedCustomers(int idToRemove) {
//        if (idToRemove != 0) {
        for (int i = 0; i < unvisitedCustomers.size(); i++) {
            int id = unvisitedCustomers.get(i);
            if (id == idToRemove) {
                    unvisitedCustomers.remove(i);
                    return;
                }
            }
//        }
    }

    int chooseNextNode(int currentNodeId, List<RouteSegment> routeSegments) {
        calculateProbabilityForAllFeasibleNodes(currentNodeId, routeSegments);
        double weightSum = 0;
        for (Customer c : feasibleNodes) {
            weightSum += c.getMacsChoiceProbability();
        }
        double value = new Random().nextDouble() * weightSum; //zakres 0-1 * suma wag

        for (Customer c : feasibleNodes) {
            value -= c.getMacsChoiceProbability();
            if (value <= 0) {
                return c.getId();
            }
        }
        return feasibleNodes.get(0).getId(); //jeśli błąd losowania, funkcja zwraca pierwszy węzeł z dostępnych na liście feasibleNodes
    }

    private void calculateProbabilityForAllFeasibleNodes(int currentNodeId, List<RouteSegment> routeSegments) {
        double downNumber = 0;

//        iteracja po wszystkich możliwych segmentach, zapisywanie liczników oraz sumowanie ich
        for (Customer nextCustomer : feasibleNodes) {
            for (RouteSegment rs : routeSegments) {
                if (rs.isSegmentExist(currentNodeId, nextCustomer.getId())) {
                    double distance = rs.getDistance();
//                    Duration duration = rs.getDuration();
                    double ni = 1 / distance;
                    double tau = rs.getMacsPheromoneLevel(); //pheromone level on segment
                    double upNumber = Math.pow(tau, alfa) * Math.pow(ni, beta); //licznik
                    rs.setMacsUpNumber(upNumber);

                    downNumber = downNumber + upNumber; //mianownik

                    break;
                }
            }
        }

        for (Customer nextCustomer : feasibleNodes) {
            for (RouteSegment rs : routeSegments) {
                if (rs.isSegmentExist(currentNodeId, nextCustomer.getId())) {
                    double probability = rs.getMacsUpNumber() / downNumber; //dzielenie
                    nextCustomer.setMacsChoiceProbability(probability);
                }
            }
        }
    }
}
