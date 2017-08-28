package com.vrp.bajmer.algorithm.macs;

import com.vrp.bajmer.core.Customer;
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
    private List<Customer> feasibleNodes;

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

    public void setUnvisitedCustomers(List<Customer> unvisitedCustomers) {
        this.unvisitedCustomers = unvisitedCustomers;
    }

    public List<Customer> getFeasibleNodes() {
        return feasibleNodes;
    }

    public void setFeasibleNodes(List<Customer> feasibleNodes) {
        this.feasibleNodes = feasibleNodes;
    }

    public void updateFeasibleNodes() {

    }

    //    usuwa odwiedzonego klienta z listy nieodwiedzonych
    public void updateUnvisitedCustomers(int id) {
        if (id != 0) {
            for (Integer i : unvisitedCustomers) {
                if (i == id) {
                    unvisitedCustomers.remove(i);
                    return;
                }
            }
        }
    }

    public Customer chooseNextNode() {

    }
}
