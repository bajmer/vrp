package com.vrp.bajmer.core;

import javax.swing.*;
import java.time.Duration;
import java.time.LocalTime;

/**
 * Created by Marcin on 2017-06-25.
 */
public class RouteSegment {

    private static int routeSegmentID = 1;
    private int id;
    private Customer src;
    private Customer dst;
    private LocalTime departure = LocalTime.MIDNIGHT;
    private LocalTime arrival = LocalTime.MIDNIGHT;
    private double distance;
    private Duration duration;
    private double clarkWrightSaving;
    private String geometry;
    private ImageIcon imageIcon;

    public RouteSegment(Customer src, Customer dst, double distance, Duration duration, String geometry) {
        this.id = routeSegmentID;
        routeSegmentID++;
        this.src = src;
        this.dst = dst;
        this.distance = distance;
        this.duration = duration;
        this.geometry = geometry;
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

    public LocalTime getDeparture() {
        return departure;
    }

    public void setDeparture(LocalTime departure) {
        this.departure = departure;
    }

    public LocalTime getArrival() {
        return arrival;
    }

    public void setArrival(LocalTime arrival) {
        this.arrival = arrival;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public double getClarkWrightSaving() {
        return clarkWrightSaving;
    }

    public void setClarkWrightSaving(double clarkWrightSaving) {
        this.clarkWrightSaving = clarkWrightSaving;
    }

    public String getGeometry() {
        return geometry;
    }

    public void setGeometry(String geometry) {
        this.geometry = geometry;
    }

    public ImageIcon getImageIcon() {
        return imageIcon;
    }

    public void setImageIcon(ImageIcon imageIcon) {
        this.imageIcon = imageIcon;
    }

    public void swapSrcDst() {
        Customer tmp = this.src;
        this.src = this.dst;
        this.dst = tmp;
    }

    @Override
    public String toString() {
        long minutes = duration.toMinutes() % 60;
        String sMinutes;
        if (minutes < 10) {
            sMinutes = "0" + Long.toString(minutes);
        } else {
            sMinutes = Long.toString(minutes);
        }
        return "From: " + src.getId()
                + ", To: " + dst.getId()
                + ", Distance: " + distance
                + "km, Duration: " + duration.toHours() + ":" + sMinutes + "h";
    }
}
