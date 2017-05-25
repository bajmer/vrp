package project;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mbala on 22.05.17.
 */
public class Client {

    private static int clientID = 1;
    private int id;
    private double latitude;
    private double longitude;
    private Map<Integer, Double> distances = new HashMap<>();

    public Client(double latitude, double longitude) {
        this.id = clientID;
        clientID++;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public static int getClientID() {
        return clientID;
    }

    public static void setClientID(int clientID) {
        Client.clientID = clientID;
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
}
