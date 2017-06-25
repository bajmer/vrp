package project;

/**
 * Created by Marcin on 2017-06-25.
 */
public class RouteSegment {

    private static int routeSegmentID = 1;
    private int id;
    private int srcCustomerID;
    private int dstCustomerID;
    private double distance;
    private String timeOfTravel;
    private double clarkWrightSaving;

    public RouteSegment(int srcCustomerID, int dstCustomerID, double distance) {
        this.id = routeSegmentID;
        routeSegmentID++;
        this.srcCustomerID = srcCustomerID;
        this.dstCustomerID = dstCustomerID;
        this.distance = distance;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSrcCustomerID() {
        return srcCustomerID;
    }

    public void setSrcCustomerID(int srcCustomerID) {
        this.srcCustomerID = srcCustomerID;
    }

    public int getDstCustomerID() {
        return dstCustomerID;
    }

    public void setDstCustomerID(int dstCustomerID) {
        this.dstCustomerID = dstCustomerID;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getTimeOfTravel() {
        return timeOfTravel;
    }

    public void setTimeOfTravel(String timeOfTravel) {
        this.timeOfTravel = timeOfTravel;
    }

    public double getSaving() {
        return clarkWrightSaving;
    }

    public void setSaving(double saving) {
        this.clarkWrightSaving = saving;
    }
}
