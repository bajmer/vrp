package com.vrp.bajmer.network;

import com.vrp.bajmer.core.Customer;
import com.vrp.bajmer.core.RouteSegment;
import com.vrp.bajmer.core.Storage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.math.BigDecimal;

/**
 * Created by mbala on 25.05.17.
 */
public class DistanceMatrix extends JSON {

    private static final Logger logger = LogManager.getLogger(DistanceMatrix.class);
    private static final String beginOfURL = "http://127.0.0.1:5000/route/v1/driving/";
    private static final String endOfURL = "?generate_hints=false&overview=false";
//    private final String beginOfURL = "http://192.168.56.101:5000/route/v1/driving/";

    //private String fullURL = "http://router.project-osrm.org/table/v1/driving/13.388860,52.517037;13.397634,52.529407;13.428555,52.523219";
    //private String fullURL = "http://192.168.56.101:5000/table/v1/driving/20.994873046875,52.50953477032727;22.313232421875,52.14697334064471;22.30224609375,52.696361078274485;21.346435546875,52.82932091031374;20.93994140625,52.74959372674114;20.159912109375,52.82932091031374;21.346435546875,52.5897007687178;21.785888671875,52.44261787120724;21.4013671875,52.0862573323384;21.07177734375,51.984880139916626;20.994873046875,52.50953477032727;22.313232421875,52.14697334064471;22.30224609375,52.696361078274485;21.346435546875,52.82932091031374;20.93994140625,52.74959372674114;20.159912109375,52.82932091031374;21.346435546875,52.5897007687178;21.785888671875,52.44261787120724;21.4013671875,52.0862573323384;21.07177734375,51.984880139916626;20.994873046875,52.50953477032727;22.313232421875,52.14697334064471;22.30224609375,52.696361078274485;21.346435546875,52.82932091031374;20.93994140625,52.74959372674114;20.159912109375,52.82932091031374;21.346435546875,52.5897007687178;21.785888671875,52.44261787120724;21.4013671875,52.0862573323384;21.07177734375,51.984880139916626;20.994873046875,52.50953477032727;22.313232421875,52.14697334064471;22.30224609375,52.696361078274485;21.346435546875,52.82932091031374;20.93994140625,52.74959372674114;20.159912109375,52.82932091031374;21.346435546875,52.5897007687178;21.785888671875,52.44261787120724;21.4013671875,52.0862573323384;21.07177734375,51.984880139916626;20.994873046875,52.50953477032727;22.313232421875,52.14697334064471;22.30224609375,52.696361078274485;21.346435546875,52.82932091031374;20.93994140625,52.74959372674114;20.159912109375,52.82932091031374;21.346435546875,52.5897007687178;21.785888671875,52.44261787120724;21.4013671875,52.0862573323384;21.07177734375,51.984880139916626;20.994873046875,52.50953477032727;22.313232421875,52.14697334064471;22.30224609375,52.696361078274485;21.346435546875,52.82932091031374;20.93994140625,52.74959372674114;20.159912109375,52.82932091031374;21.346435546875,52.5897007687178;21.785888671875,52.44261787120724;21.4013671875,52.0862573323384;21.07177734375,51.984880139916626;20.994873046875,52.50953477032727;22.313232421875,52.14697334064471;22.30224609375,52.696361078274485;21.346435546875,52.82932091031374;20.93994140625,52.74959372674114;20.159912109375,52.82932091031374;21.346435546875,52.5897007687178;21.785888671875,52.44261787120724;21.4013671875,52.0862573323384;21.07177734375,51.984880139916626;20.994873046875,52.50953477032727;22.313232421875,52.14697334064471;22.30224609375,52.696361078274485;21.346435546875,52.82932091031374;20.93994140625,52.74959372674114;20.159912109375,52.82932091031374;21.346435546875,52.5897007687178;21.785888671875,52.44261787120724;21.4013671875,52.0862573323384;21.07177734375,51.984880139916626;20.994873046875,52.50953477032727;22.313232421875,52.14697334064471;22.30224609375,52.696361078274485;21.346435546875,52.82932091031374;20.93994140625,52.74959372674114;20.159912109375,52.82932091031374;21.346435546875,52.5897007687178;21.785888671875,52.44261787120724;21.4013671875,52.0862573323384;21.07177734375,51.984880139916626;20.994873046875,52.50953477032727;22.313232421875,52.14697334064471;22.30224609375,52.696361078274485;21.346435546875,52.82932091031374;20.93994140625,52.74959372674114;20.159912109375,52.82932091031374;21.346435546875,52.5897007687178;21.785888671875,52.44261787120724;21.4013671875,52.0862573323384;21.07177734375,51.984880139916626";
    //private String fullURL = "https://graphhopper.com/api/1/matrix?key=[YOUR_KEY]%22%20-d%20%27{%22elevation%22:false,%22out_arrays%22:[%22weights%22],%22from_points%22:[[-0.087891,51.534377],[-0.090637,51.467697],[-0.171833,51.521241],[-0.211487,51.473685]],%22to_points%22:[[-0.087891,51.534377],[-0.090637,51.467697],[-0.171833,51.521241],[-0.211487,51.473685]],%22vehicle%22:%22car%22}%27";
    public DistanceMatrix() {
    }

    public void downloadDistanceMatrix() throws Exception {
        logger.info("Downloading distance matrix...");
        try {
            for (int i = 0; i < Storage.getCustomerList().size(); i++) {
                for (int j = i; j < Storage.getCustomerList().size(); j++) {
                    Customer src = Storage.getCustomerList().get(i);
                    Customer dst = Storage.getCustomerList().get(j);
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
                                    double durationInMin = getDurationFromJSON(jsonObject);
//                            zawsze srcID < dstID!!!
                                    if (distanceInKm > 0) {
                                        Storage.getRouteSegmentsList().add(new RouteSegment(src, dst, distanceInKm, durationInMin));
                                        src.getDistances().put(dst.getId(), distanceInKm);
                                        src.getDurations().put(dst.getId(), durationInMin);
                                        dst.getDistances().put(src.getId(), distanceInKm);
                                        dst.getDurations().put(src.getId(), durationInMin);
                                        logger.debug("New route segment " + src.getId() + "-" + dst.getId() + ": " + distanceInKm + " km, " + durationInMin + " min.");
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

    private double getDurationFromJSON(JSONObject jsonObject) {
        double duration = -1; //jeżeli czas jest ujemny, wówczas algorytm vrp będzie ją pomijał
        try {
            duration = jsonObject.getJSONArray("routes").getJSONObject(0).getDouble("duration");
        } catch (org.json.JSONException e) {
            logger.error("Error while getting duration from JSON object!");
        }
        if (duration >= 0) {
            double durationInMinutes = duration / 60;
            return new BigDecimal(durationInMinutes).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
        } else {
            return duration;
        }
    }
}
