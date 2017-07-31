package com.vrp.bajmer.core;

import javax.swing.*;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mbala on 03.07.17.
 */
public class Route {

    private static int routeID = 0;
    private int id;
    private ArrayList<Customer> customersInRoute;
    private ArrayList<RouteSegment> routeSegments;
    private Map<Customer, ImageIcon> customersIcons;
    private double totalDistance;
    private Duration totalDuration;
    private LocalTime startTime;
    private double currentPackagesWeight;
    private double currentPackagesSize;
    private ImageIcon imageIcon;

    public Route() {
        routeID++;
        this.id = routeID;
        this.customersInRoute = new ArrayList<>();
        this.routeSegments = new ArrayList<>();
        this.customersIcons = new HashMap<>();
        this.totalDistance = 0.0;
        this.totalDuration = Duration.ZERO;
        this.startTime = LocalTime.of(8, 0);
        this.currentPackagesWeight = 0.0;
        this.currentPackagesSize = 0.0;
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

    public Map<Customer, ImageIcon> getCustomersIcons() {
        return customersIcons;
    }

    public void setCustomersIcons(Map<Customer, ImageIcon> customersIcons) {
        this.customersIcons = customersIcons;
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(double totalDistance) {
        this.totalDistance = totalDistance;
    }

    public Duration getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(Duration totalDuration) {
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

    public ImageIcon getImageIcon() {
        return imageIcon;
    }

    public void setImageIcon(ImageIcon imageIcon) {
        this.imageIcon = imageIcon;
    }

    public void addCustomerAsFirst(Customer customer) {
        int firstPosition = 0;
        customersInRoute.add(firstPosition, customer);
        currentPackagesWeight += customer.getPackageWeight();
        currentPackagesSize += customer.getPackageSize();
    }

    public void addCustomerAsLast(Customer customer) {
        customersInRoute.add(customer);
        currentPackagesWeight += customer.getPackageWeight();
        currentPackagesSize += customer.getPackageSize();
    }

    public void mergeRoute(Route route) {
        customersInRoute.addAll(route.getCustomersInRoute());
        currentPackagesWeight += route.getCurrentPackagesWeight();
        currentPackagesSize += route.getCurrentPackagesSize();
        totalDistance += route.getTotalDistance();
        totalDuration = totalDuration.plus(route.getTotalDuration());
        totalDistance = round(totalDistance);

        for (RouteSegment rs : route.getRouteSegments()) {
            routeSegments.add(rs);
        }
    }

    public void addSegmentAsFirst(RouteSegment routeSegment) {
        int firstPosition = 0;
        routeSegments.add(firstPosition, routeSegment);
        totalDistance += routeSegment.getDistance();
        totalDuration = totalDuration.plus(routeSegment.getDuration());
    }

    public void addSegmentAsLast(RouteSegment routeSegment) {
        routeSegments.add(routeSegment);
        totalDistance += routeSegment.getDistance();
        totalDuration = totalDuration.plus(routeSegment.getDuration());
    }

    public boolean canAdd(double packageWeight, double weightLimit, double packageSize, double sizeLimit) {
        return canAddWeight(packageWeight, weightLimit) && canAddSize(packageSize, sizeLimit);
    }

    private boolean canAddWeight(double packageWeight, double weightLimit) {
        return currentPackagesWeight + packageWeight <= weightLimit;
    }

    private boolean canAddSize(double packageSize, double sizeLimit) {
        return currentPackagesSize + packageSize <= sizeLimit;
    }

    public boolean isCustomerFirst(Customer customer) {
        return customersInRoute.get(0).equals(customer);
    }

    public boolean isCustomerLast(Customer customer) {
        return customersInRoute.get(customersInRoute.size() - 1).equals(customer);
    }

    private double round(double x) {
        return new BigDecimal(x).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public void rotate() {
        Collections.reverse(customersInRoute);
        Collections.reverse(routeSegments);
        for (RouteSegment rs : routeSegments) {
            rs.swapSrcDst();
        }
    }

    public void setArrivalAndDepartureTimeForCustomers() {
        LocalTime departute = startTime;
        LocalTime arrival = startTime;
        for (RouteSegment rs : routeSegments) {
            Duration duration = rs.getDuration();
            if (rs.getSrc().getId() == 0) {
                rs.setDeparture(departute);
                arrival = arrival.plus(duration);
                rs.setArrival(arrival);
            } else {
                departute = arrival.plus(Customer.getServiceTime());
                rs.setDeparture(departute);
                arrival = departute.plus(duration);
                rs.setArrival(arrival);
            }
        }
    }

    @Override
    public String toString() {
        long minutes = totalDuration.toMinutes() % 60;
        String sMinutes;
        sMinutes = minutes < 10 ? "0" + Long.toString(minutes) : Long.toString(minutes);
        return "Route " + id + ", "
                + totalDistance + "km, "
                + totalDuration.toHours() + ":" + sMinutes + "h, "
                + currentPackagesWeight + "kg, "
                + currentPackagesSize + "m3";
    }
}
