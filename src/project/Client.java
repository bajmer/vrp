package project;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mbala on 22.05.17.
 */
public class Client {

    private static int clientID = 1;
    private int id;
    private String name;
    private double latitude;
    private double longitude;
    private double packageWeight;
    private double packageCapacity;
    private String minDeliveryHour;
    private String maxDeliveryHour;

    private Map<Integer, Double> distances = new HashMap<>();

    public Client(String name, double latitude, double longitude, double packageWeight, double packageCapacity, String minDeliveryHour, String maxDeliveryHour) {
        this.id = clientID;
        clientID++;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.packageWeight = packageWeight;
        this.packageCapacity = packageCapacity;
        this.minDeliveryHour = minDeliveryHour;
        this.maxDeliveryHour = maxDeliveryHour;
    }

    public static int getClientID() {
        return clientID;
    }

    public static void setClientID(int clientID) {
        Client.clientID = clientID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public double getPackageWeight() {
        return packageWeight;
    }

    public void setPackageWeight(double packageWeight) {
        this.packageWeight = packageWeight;
    }

    public double getPackageCapacity() {
        return packageCapacity;
    }

    public void setPackageCapacity(double packageCapacity) {
        this.packageCapacity = packageCapacity;
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
}
