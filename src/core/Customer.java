package core;

import javax.swing.*;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private List<RouteSegment> routeSegmentsFromCustomer = new ArrayList<>();
    private ImageIcon imageIcon;
    private double acsChoiceProbability;

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

    public Customer(double x, double y, double demand) {
        this.id = customerID;
        customerID++;
        this.fullAddress = "";
        this.streetAndNumber = "";
        this.postalCode = "";
        this.city = "";
        this.latitude = x;
        this.longitude = y;
        this.packageWeight = demand;
        this.packageSize = 0;
        this.minDeliveryHour = LocalTime.of(8, 0);
        this.maxDeliveryHour = LocalTime.of(18, 0);
        this.imageIcon = null;
    }

    public static int getCustomerID() {
        return customerID;
    }

    public static void setCustomerID(int customerID) {
        Customer.customerID = customerID;
    }

    static Duration getServiceTime() {
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

    public List<RouteSegment> getRouteSegmentsFromCustomer() {
        return routeSegmentsFromCustomer;
    }

    public void setRouteSegmentsFromCustomer(List<RouteSegment> routeSegmentsFromCustomer) {
        this.routeSegmentsFromCustomer = routeSegmentsFromCustomer;
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

    public double getAcsChoiceProbability() {
        return acsChoiceProbability;
    }

    public void setAcsChoiceProbability(double acsChoiceProbability) {
        this.acsChoiceProbability = acsChoiceProbability;
    }
}
