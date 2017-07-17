package com.vrp.bajmer.core;

import java.util.ArrayList;

/**
 * Created by mbala on 03.07.17.
 */
public class Route {

    private static int routeID = 0;
    private int id;
    private ArrayList<Customer> customersInRoute;
    private double totalDistance;
    private double totalDuration;
    private double currentPackagesWeight;
    private double currentPackagesSize;

    public Route() {
        routeID++;
        id = routeID;
        customersInRoute = new ArrayList<>();
        totalDistance = 0.0;
        totalDuration = 0.0;
        currentPackagesWeight = 0.0;
        currentPackagesSize = 0.0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<Customer> getCustomersInRoute() {
        return customersInRoute;
    }

    public void setCustomersInRoute(ArrayList<Customer> customersInRoute) {
        this.customersInRoute = customersInRoute;
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(double totalDistance) {
        this.totalDistance = totalDistance;
    }

    public double getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(double totalDuration) {
        this.totalDuration = totalDuration;
    }

    public double getCurrentPackagesWeight() {
        return currentPackagesWeight;
    }

    public void setCurrentPackagesWeight(double currentPackagesWeight) {
        this.currentPackagesWeight = currentPackagesWeight;
    }

    public double getCurrentPackagesSize() {
        return currentPackagesSize;
    }

    public void setCurrentPackagesSize(double currentPackagesSize) {
        this.currentPackagesSize = currentPackagesSize;
    }

    public void addCustomerToFirstPosition(Customer customer, double distance, double duration) {
        int firstPosition = 0;
        customersInRoute.add(firstPosition, customer);
        currentPackagesWeight += customer.getPackageWeight();
        currentPackagesSize += customer.getPackageSize();
        totalDistance += distance;
        totalDuration += duration + customer.getServiceTime();
    }

    public void addCustomerToLastPosition(Customer customer, double distance, double duration) {
        customersInRoute.add(customer);
        currentPackagesWeight += customer.getPackageWeight();
        currentPackagesSize += customer.getPackageSize();
        totalDistance += distance;
        totalDuration += duration + customer.getServiceTime();
    }

    public void mergeRoute(Route route) {
        customersInRoute.addAll(route.getCustomersInRoute());
        currentPackagesWeight += route.getCurrentPackagesWeight();
        currentPackagesSize += route.getCurrentPackagesSize();
        totalDistance += route.getTotalDistance();
        totalDuration += route.getTotalDuration();
    }

    //    funkcja sprawdzająca warunki dodania klienta do trasy
    public boolean canAddCustomer(double packageWeight, double weightLimit, double packageSize, double sizeLimit) {
        return canAddWeight(packageWeight, weightLimit) && canAddSize(packageSize, sizeLimit);
    }

    private boolean canAddWeight(double packageWeight, double weightLimit) {
        return currentPackagesWeight + packageWeight <= weightLimit;
    }

    private boolean canAddSize(double packageSize, double sizeLimit) {
        return currentPackagesSize + packageSize <= sizeLimit;
    }

    public boolean isCustomerOnFirstPosition(Customer customer) {
        return customersInRoute.get(0).equals(customer);
    }

    public boolean isCustomerOnLastPosition(Customer customer) {
        return customersInRoute.get(customersInRoute.size() - 1).equals(customer);
    }
}
