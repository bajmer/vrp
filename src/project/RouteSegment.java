package project;

/**
 * Created by Marcin on 2017-06-25.
 */
public class RouteSegment {

    private static int routeSegmentID = 1;
    private int id;
    private Customer src;
    private Customer dst;
    private double distance;
    private double duration;
    private double clarkWrightSaving;

    public RouteSegment(Customer src, Customer dst, double distance, double duration) {
        this.id = routeSegmentID;
        routeSegmentID++;
        this.src = src;
        this.dst = dst;
        this.distance = distance;
        this.duration = duration;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Customer getSrc() {
        return src;
    }

    public void setSrc(Customer src) {
        this.src = src;
    }

    public Customer getDst() {
        return dst;
    }

    public void setDst(Customer dst) {
        this.dst = dst;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public double getClarkWrightSaving() {
        return clarkWrightSaving;
    }

    public void setClarkWrightSaving(double clarkWrightSaving) {
        this.clarkWrightSaving = clarkWrightSaving;
    }
}
