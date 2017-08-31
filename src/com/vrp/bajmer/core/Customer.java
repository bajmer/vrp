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
    private String fullAddress;
    private String streetAndNumber;
    private String postalCode;
    private String city;
    private double latitude;
    private double longitude;
    private double packageWeight;
    private double packageSize;
    private LocalTime minDeliveryHour;
    private LocalTime maxDeliveryHour;
    private Map<Integer, Double> distances = new HashMap<>();
    private Map<Integer, Duration> durations = new HashMap<>();
    private ImageIcon imageIcon;
    private double macsChoiceProbability;

    public Customer(String fullAddress, String streetAndNumber, String postalCode, String city, double latitude, double longitude,
                    double packageWeight, double packageSize, LocalTime minDeliveryHour, LocalTime maxDeliveryHour) {
        this.id = customerID;
        customerID++;
        this.fullAddress = fullAddress;
        this.streetAndNumber = streetAndNumber;
        this.postalCode = postalCode;
        this.city = city;
        this.latitude = latitude;
        this.longitude = longitude;
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

    public String getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    public String getStreetAndNumber() {
        return streetAndNumber;
    }

    public void setStreetAndNumber(String streetAndNumber) {
        this.streetAndNumber = streetAndNumber;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
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

    public double getMacsChoiceProbability() {
        return macsChoiceProbability;
    }

    public void setMacsChoiceProbability(double macsChoiceProbability) {
        this.macsChoiceProbability = macsChoiceProbability;
    }
}
