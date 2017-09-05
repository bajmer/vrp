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
    private static double q0; //parametr określający proporcję między eksploatacją najlepszej krawędzi i eksploracją nowej
    private static double beta; //parametr regulujący wpływ ni (odwrotność odległości), preferowana wartość to 2-5
    private List<Integer> unvisitedCustomers;
    private List<Customer> feasibleNodes;

    Ant(List<Customer> customers) {
        feasibleNodes = new ArrayList<>();
        unvisitedCustomers = new ArrayList<>();
        for (Customer c : customers) {
            if (c.getId() != 0) {
                unvisitedCustomers.add(c.getId()); //utworzenie listy nieodwiedzonych klientów (bez magazynu)
            }
        }
    }

    static double getQ0() {
        return q0;
    }

    static void setQ0(double q0) {
        Ant.q0 = q0;
    }

    static double getBeta() {
        return beta;
    }

    static void setBeta(double beta) {
        Ant.beta = beta;
    }

    List<Integer> getUnvisitedCustomers() {
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

    boolean updateFeasibleCustomers(int tmpNodeId, List<RouteSegment> routeSegments, Route route, double weightLimit, double sizeLimit) {
        feasibleNodes.clear();
        for (RouteSegment rs : routeSegments) {
            if (rs.getSrc().getId() == tmpNodeId) {
                Customer c = rs.getDst();
                if (isCustomerUnvisited(c.getId())) { //jeśli klient jest na liście nieodwiedzonych klientów
                    if (route.canAdd(c.getPackageWeight(), weightLimit, c.getPackageSize(), sizeLimit)) { //jeśli klient może zostać dodany do trasy (dopisać warunki czasowe dla VRPTW)
                        feasibleNodes.add(c);
                    }
                }
            }
        }
        return feasibleNodes.size() != 0;
    }

    private boolean isCustomerUnvisited(int id) {
        for (int i : unvisitedCustomers) {
            if (i == id) {
                return true;
            }
        }
        return false;
    }

    void removeFromUnvisitedCustomers(int idToRemove) {
        for (int i = 0; i < unvisitedCustomers.size(); i++) {
            int id = unvisitedCustomers.get(i);
            if (id == idToRemove) {
                unvisitedCustomers.remove(i);
                return;
            }
        }
    }

    int chooseNextNode(int currentNodeId, List<RouteSegment> routeSegments) {
        if (currentNodeId == 0) {
            //jeżeli mrówka jest w magazynie wybiera losowo miasto z listy dostępnych miast
            int randomValue = new Random().nextInt(feasibleNodes.size());
            return feasibleNodes.get(randomValue).getId();
        } else {
            //jeżeli mrówka jest w dowolnym węźle (ale nie w magazynie), wówczas wybiera kolejne miasto zgodnie z zasadami ACS
            double q = new Random().nextDouble();
            calculateProbabilityForAllFeasibleNodes(currentNodeId, routeSegments);

            if (q <= q0) {
                //eksploatacja klienta, dla którego wartość  licznika "tau*(1/distance)^beta" jest największa
                int tmpNextCustomerID = 0;
                double tmpUpNumber = 0;

//                ZOPTYMALIZOWAĆ
                for (Customer nextCustomer : feasibleNodes) {
                    for (RouteSegment rs : routeSegments) {
                        if (rs.isSegmentExist(currentNodeId, nextCustomer.getId())) {
                            double rsUpNumber = rs.getAcsUpNumber();
                            if (rsUpNumber > tmpUpNumber) {
                                tmpUpNumber = rsUpNumber;
                                tmpNextCustomerID = nextCustomer.getId();
                                break;
                            }
                        }
                    }
                }
                return tmpNextCustomerID;
            } else {
                //wylosowanie klienta uwzględniając prawdopodobieństwo
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
                return 0;
            }
        }
    }

    //    ZOPTYMALIZOWAć!!!!!!!!!!!
    private void calculateProbabilityForAllFeasibleNodes(int currentNodeId, List<RouteSegment> routeSegments) {
        double downNumber = 0;

//        iteracja po wszystkich odcinkach trasy łączących dostępnych klientów, zapisywanie liczników oraz sumowanie ich
        for (Customer nextCustomer : feasibleNodes) {
            for (RouteSegment rs : routeSegments) {
                if (rs.isSegmentExist(currentNodeId, nextCustomer.getId())) {
                    double distance = rs.getDistance();
//                    Duration duration = rs.getDuration();
                    double ni = 1 / distance;
                    double tau = rs.getAcsPheromoneLevel(); //pheromone level on segment
                    double upNumber = tau * Math.pow(ni, beta); //licznik
                    rs.setAcsUpNumber(upNumber);

                    downNumber += upNumber; //mianownik

                    break;
                }
            }
        }

        for (Customer nextCustomer : feasibleNodes) {
            for (RouteSegment rs : routeSegments) {
                if (rs.isSegmentExist(currentNodeId, nextCustomer.getId())) {
                    double probability = rs.getAcsUpNumber() / downNumber; //dzielenie
                    nextCustomer.setMacsChoiceProbability(probability);
                }
            }
        }
    }
}
