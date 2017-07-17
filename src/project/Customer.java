package project;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mbala on 22.05.17.
 */
public class Customer {

    private static final double serviceTime = 5; //czas obsługi klienta w minutach
    private static int customerID;
    private int id;
    private String address;
    private double latitude = 0;
    private double longitude = 0;
    private double packageWeight;
    private double packageSize;
    private String minDeliveryHour;
    private String maxDeliveryHour;
    private Map<Integer, Double> distances = new HashMap<>();
    private Map<Integer, Double> durations = new HashMap<>();

    public Customer(String address, double packageWeight, double packageSize, String minDeliveryHour, String maxDeliveryHour) {
        this.id = customerID;
        customerID++;
        this.address = address;
        this.packageWeight = packageWeight;
        this.packageSize = packageSize;
        this.minDeliveryHour = minDeliveryHour;
        this.maxDeliveryHour = maxDeliveryHour;
    }

    public static int getCustomerID() {
        return customerID;
    }

    public static void setCustomerID(int customerID) {
        Customer.customerID = customerID;
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

    public Map<Integer, Double> getDurations() {
        return durations;
    }

    public void setDurations(Map<Integer, Double> durations) {
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

    public String getMinDeliveryHour() {
        return minDeliveryHour;
    }

    public void setMinDeliveryHour(String minDeliveryHour) {
        this.minDeliveryHour = minDeliveryHour;
    }

    public String getMaxDeliveryHour() {
        return maxDeliveryHour;
    }

    public void setMaxDeliveryHour(String maxDeliveryHour) {
        this.maxDeliveryHour = maxDeliveryHour;
    }

    public double getServiceTime() {
        return serviceTime;
    }
}
