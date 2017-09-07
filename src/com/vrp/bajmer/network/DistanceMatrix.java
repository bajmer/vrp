package com.vrp.bajmer.network;

import com.vrp.bajmer.core.Customer;
import com.vrp.bajmer.core.Database;
import com.vrp.bajmer.core.RouteSegment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.time.Duration;

/**
 * Created by mbala on 25.05.17.
 */
public class DistanceMatrix extends JSON {

    private static final Logger logger = LogManager.getLogger(DistanceMatrix.class);
    private static final String beginOfURL = "http://127.0.0.1:5000/route/v1/driving/";
    private static final String endOfURL = "?generate_hints=false";
//    private final String beginOfURL = "http://192.168.56.101:5000/route/v1/driving/";

    public DistanceMatrix() {
    }

    public void downloadDistanceMatrix() throws Exception {
        logger.info("Downloading distance matrix...");
        try {
            for (int i = 0; i < Database.getCustomerList().size(); i++) {
                for (int j = i; j < Database.getCustomerList().size(); j++) {
                    Customer src = Database.getCustomerList().get(i);
                    Customer dst = Database.getCustomerList().get(j);
                    logger.debug("Calculating distance for " + src.getId() + " and " + dst.getId() + "...");
                    if (j != i) {
                        double srcLat = src.getLatitude();
                        double srcLon = src.getLongitude();
                        double dstLat = dst.getLatitude();
                        double dstLon = dst.getLongitude();
                        if (srcLat != 0 && srcLon != 0) {
                            if (dstLat != 0 && dstLon != 0) {
                                String routeURL = parseURL(beginOfURL, src.getLongitude(), src.getLatitude(), dst.getLongitude(), dst.getLatitude(), endOfURL);
                                JSONObject jsonObject = sendRequest(routeURL);
                                if (jsonObject != null) {
                                    double distanceInKm = getDistanceFromJSON(jsonObject);
                                    Duration duration = getDurationFromJSON(jsonObject);
                                    String geometry = getGeometryFromJSON(jsonObject);
//                            zawsze srcID < dstID!!!
                                    if (distanceInKm > 0) {
                                        Database.getRouteSegmentsList().add(new RouteSegment(src, dst, distanceInKm, duration, geometry));
                                        src.getDistances().put(dst.getId(), distanceInKm);
                                        src.getDurations().put(dst.getId(), duration);
                                        dst.getDistances().put(src.getId(), distanceInKm);
                                        dst.getDurations().put(src.getId(), duration);
                                        logger.debug("New route segment " + src.getId() + "-" + dst.getId() + ": " + distanceInKm + " km, " + duration.toMinutes() + " min.");
                                    } else {
                                        logger.warn("There is incorrect distance for customers " + src.getId() + " and " + dst.getId() + ". New route segment is not created!");
                                    }
                                } else {
                                    logger.warn("Response from server for customers " + src.getId() + " and " + dst.getId() + " contain NULL JSON object!");
                                }
                            } else {
                                logger.warn("Customer " + dst.getId() + " has got incorrect coordinates!");
                            }
                        } else {
                            logger.warn("Customer " + src.getId() + " has got incorrect coordinates!");
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error while downloading distance matrix!");
            throw e;
        }
        logger.info("Downloading distance matrix has been completed.");
    }

    private double getDistanceFromJSON(JSONObject jsonObject) {
        double distance = -1; //jeżeli odległość jest ujemna, wówczas algorytm vrp będzie ją pomijał
        try {
            distance = jsonObject.getJSONArray("routes").getJSONObject(0).getDouble("distance");
        } catch (org.json.JSONException e) {
            logger.error("Error while getting distance from JSON object!");
        }
        if (distance >= 0) {
            double distanceKm = distance * 0.001;
            return new BigDecimal(distanceKm).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
        } else {
            return distance;
        }
    }

    private Duration getDurationFromJSON(JSONObject jsonObject) {
        Duration duration = Duration.ZERO;
        try {
            Double durationDouble = jsonObject.getJSONArray("routes").getJSONObject(0).getDouble("duration");
            duration = Duration.ofSeconds(durationDouble.longValue());
        } catch (org.json.JSONException e) {
            logger.error("Error while getting duration from JSON object!");
        }
        return duration;

    }

    private String getGeometryFromJSON(JSONObject jsonObject) {
        String geometry = "";
        try {
            geometry = jsonObject.getJSONArray("routes").getJSONObject(0).getString("geometry");
        } catch (org.json.JSONException e) {
            logger.info("Error while getting geometry from JSON object!");
        }
        return geometry;
    }
}
