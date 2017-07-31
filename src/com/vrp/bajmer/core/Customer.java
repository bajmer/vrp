package com.vrp.bajmer.core;

import javax.swing.*;
import java.time.Duration;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mbala on 22.05.17.
 */
public class Customer {

    private static final Duration serviceTime = Duration.ofMinutes(10); //czas obsługi klienta w minutach
    private static int customerID;
    private int id;
    private String address;
    private double latitude = 0;
    private double longitude = 0;
    private double packageWeight;
    private double packageSize;
    private LocalTime minDeliveryHour;
    private LocalTime maxDeliveryHour;
    private Map<Integer, Double> distances = new HashMap<>();
    private Map<Integer, Duration> durations = new HashMap<>();
    private ImageIcon imageIcon;

    public Customer(String address, double packageWeight, double packageSize, LocalTime minDeliveryHour, LocalTime maxDeliveryHour) {
        this.id = customerID;
        customerID++;
        this.address = address;
        this.packageWeight = packageWeight;
        this.packageSize = packageSize;
        this.minDeliveryHour = minDeliveryHour;
        this.maxDeliveryHour = maxDeliveryHour;
        this.imageIcon = null;
    }

    public static int getCustomerID() {
        return customerID;
    }

    public static void setCustomerID(int customerID) {
        Customer.customerID = customerID;
    }

    public static Duration getServiceTime() {
        return serviceTime;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Map<Integer, Double> getDistances() {
        return distances;
    }

    public void setDistances(Map<Integer, Double> distances) {
        this.distances = distances;
    }

    public Map<Integer, Duration> getDurations() {
        return durations;
    }

    public void setDurations(Map<Integer, Duration> durations) {
        this.durations = durations;
    }

    public double getPackageWeight() {
        return packageWeight;
    }

    public void setPackageWeight(double packageWeight) {
        this.packageWeight = packageWeight;
    }

    public double getPackageSize() {
        return packageSize;
    }

    public void setPackageSize(double packageSize) {
        this.packageSize = packageSize;
    }

    public LocalTime getMinDeliveryHour() {
        return minDeliveryHour;
    }

    public void setMinDeliveryHour(LocalTime minDeliveryHour) {
        this.minDeliveryHour = minDeliveryHour;
    }

    public LocalTime getMaxDeliveryHour() {
        return maxDeliveryHour;
    }

    public void setMaxDeliveryHour(LocalTime maxDeliveryHour) {
        this.maxDeliveryHour = maxDeliveryHour;
    }

    public ImageIcon getImageIcon() {
        return imageIcon;
    }

    public void setImageIcon(ImageIcon imageIcon) {
        this.imageIcon = imageIcon;
    }
}
