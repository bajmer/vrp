package com.vrp.bajmer.algorithm.macs;

import com.vrp.bajmer.core.Customer;
import com.vrp.bajmer.core.Route;
import com.vrp.bajmer.core.RouteSegment;
import com.vrp.bajmer.core.Storage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marcin on 2017-08-27.
 */
public class Ant {

    private static int antID = 1;
    private int id;
    private List<Integer> unvisitedCustomers;
    private List<Integer> feasibleNodes;

    public Ant() {
        id = antID;
        antID++;

        feasibleNodes = new ArrayList<>();
        unvisitedCustomers = new ArrayList<>();

//        tworzona jest lista nieodwiedzonych klientów
        for (Customer c : Storage.getCustomerList()) {
            unvisitedCustomers.add(c.getId());
        }

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

    public List<Integer> getFeasibleNodes() {
        return feasibleNodes;
    }

    public void setFeasibleNodes(List<Integer> feasibleNodes) {
        this.feasibleNodes = feasibleNodes;
    }

    public boolean updateFeasibleNodes(int tmpNodeId, List<RouteSegment> routeSegments, Route route, double weightLimit, double sizeLimit) {
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
                    feasibleNodes.add(c.getId());
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

    //    usuwa odwiedzonego klienta z listy nieodwiedzonych
    public void removeFromUnvisitedCustomers(int id) {
        if (id != 0) {
            for (int i : unvisitedCustomers) {
                if (i == id) {
                    unvisitedCustomers.remove(i);
                    return;
                }
            }
        }
    }

    public int chooseNextNode(List<RouteSegment> routeSegments) {
        int nextNodeId = 0;
        for (int i : feasibleNodes) {
            calculateProbability();
        }
        return nextNodeId;

    }

    private void calculateProbability() {

    }
}
