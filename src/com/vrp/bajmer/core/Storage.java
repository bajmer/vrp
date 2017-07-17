package com.vrp.bajmer.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mbala on 24.05.17.
 */
public class Storage {
    private static List<Customer> customerList = new ArrayList<>();
    private static List<RouteSegment> routeSegmentsList = new ArrayList<>();
    private static List<Solution> solutionsList = new ArrayList<>();

    public Storage() {
    }

    public static List<Customer> getCustomerList() {
        return customerList;
    }

    public static void setCustomerList(List<Customer> customerList) {
        Storage.customerList = customerList;
    }

    public static List<RouteSegment> getRouteSegmentsList() {
        return routeSegmentsList;
    }

    public static void setRouteSegmentsList(List<RouteSegment> routeSegmentsList) {
        Storage.routeSegmentsList = routeSegmentsList;
    }

    public static List<Solution> getSolutionsList() {
        return solutionsList;
    }

    public static void setSolutionsList(List<Solution> solutionsList) {
        Storage.solutionsList = solutionsList;
    }
}
