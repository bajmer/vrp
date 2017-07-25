package com.vrp.bajmer.core;

import javax.swing.*;
import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * Created by mbala on 03.07.17.
 */
public class Route {

    private static int routeID = 0;
    private int id;
    private ArrayList<Customer> customersInRoute;
    private ArrayList<RouteSegment> routeSegments;
    private double totalDistance;
    private double totalDuration;
    private double currentPackagesWeight;
    private double currentPackagesSize;
    //    private String geometry;
    private ImageIcon imageIcon;

    public Route() {
        routeID++;
        this.id = routeID;
        this.customersInRoute = new ArrayList<>();
        this.routeSegments = new ArrayList<>();
        this.totalDistance = 0.0;
        this.totalDuration = 0.0;
        this.currentPackagesWeight = 0.0;
        this.currentPackagesSize = 0.0;
//        this.geometry = "";
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

    public ArrayList<RouteSegment> getRouteSegments() {
        return routeSegments;
    }

    public void setRouteSegments(ArrayList<RouteSegment> routeSegments) {
        this.routeSegments = routeSegments;
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

//    public String getGeometry() {
//        return geometry;
//    }
//
//    public void setGeometry(String geometry) {
//        this.geometry = geometry;
//    }

    public ImageIcon getImageIcon() {
        return imageIcon;
    }

    public void setImageIcon(ImageIcon imageIcon) {
        this.imageIcon = imageIcon;
    }

    public void addCustomerToFirstPosition(Customer customer, double distance, double duration) {
        int firstPosition = 0;
        customersInRoute.add(firstPosition, customer);
        currentPackagesWeight += customer.getPackageWeight();
        currentPackagesSize += customer.getPackageSize();
        totalDistance += distance;
        totalDuration += duration + customer.getServiceTime();
        totalDistance = round(totalDistance);
        totalDuration = round(totalDuration);
//        geometry = segmentGeometry + geometry;
    }

    public void addCustomerToLastPosition(Customer customer, double distance, double duration) {
        customersInRoute.add(customer);
        currentPackagesWeight += customer.getPackageWeight();
        currentPackagesSize += customer.getPackageSize();
        totalDistance += distance;
        totalDuration += duration + customer.getServiceTime();
        totalDistance = round(totalDistance);
        totalDuration = round(totalDuration);
//        geometry = geometry + segmentGeometry;
    }

    public void mergeRoute(Route route) {
        customersInRoute.addAll(route.getCustomersInRoute());
        currentPackagesWeight += route.getCurrentPackagesWeight();
        currentPackagesSize += route.getCurrentPackagesSize();
        totalDistance += route.getTotalDistance();
        totalDuration += route.getTotalDuration();
        totalDistance = round(totalDistance);
        totalDuration = round(totalDuration);
//        geometry = geometry + route.getGeometry();
        for (RouteSegment rs : route.getRouteSegments()) {
            routeSegments.add(rs);
        }
    }

    public void addRouteSegmentToBegin(RouteSegment routeSegment) {
        routeSegments.add(0, routeSegment);
    }

    public void addRouteSegmentToEnd(RouteSegment routeSegment) {
        routeSegments.add(routeSegment);
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

    private double round(double x) {
        return new BigDecimal(x).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    @Override
    public String toString() {
        return "Route " + id + ", "
                + totalDistance + "km, "
                + totalDuration + "min, "
                + currentPackagesWeight + "kg, "
                + currentPackagesSize + "m3";
    }
}
