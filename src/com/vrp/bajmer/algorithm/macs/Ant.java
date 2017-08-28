package com.vrp.bajmer.algorithm.macs;

import com.vrp.bajmer.core.Customer;

import java.util.List;

/**
 * Created by Marcin on 2017-08-27.
 */
public class Ant {

    private static int antID = 1;
    private int id;
    private List<Customer> unvisitedCustomers;
    private List<Node> feasibleNodes;

    public Ant() {
        id = antID;
        antID++;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Customer> getUnvisitedCustomers() {
        return unvisitedCustomers;
    }

    public void setUnvisitedCustomers(List<Customer> unvisitedCustomers) {
        this.unvisitedCustomers = unvisitedCustomers;
    }

    public List<Node> getFeasibleNodes() {
        return feasibleNodes;
    }

    public void setFeasibleNodes(List<Node> feasibleNodes) {
        this.feasibleNodes = feasibleNodes;
    }
}
