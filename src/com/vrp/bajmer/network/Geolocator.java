package com.vrp.bajmer.network;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mbala on 10.07.17.
 */
public class Geolocator extends JSON {

    private static final Logger logger = LogManager.getLogger(Geolocator.class);

    private static final String separator = ",";
    private static final String beginOfURL = "http://nominatim.openstreetmap.org/search?format=json";
    //    http://nominatim.openstreetmap.org/search?format=json&street=10%20Spokojna&postalcode=07-200&city=Wyszk%C3%B3w&country=Polska


    public Geolocator() {
    }

    public List<Double> downloadCoordinates(String streetAndNumber, String postalCode, String city, int lineNumber) throws Exception {
        logger.debug("Downloading coordinates for customer in line " + lineNumber + "...");
        try {
            String URL = parseURL(beginOfURL, streetAndNumber, postalCode, city);
            JSONObject jsonObject = sendRequest(URL);
            if (jsonObject != null) {
                List<Double> coordinates = getCoordinatesFromJSON(jsonObject);
                if (coordinates.size() == 2) {
                    logger.debug("Downloading coordinates for customer in line " + lineNumber + " has been completed.");
                    logger.debug("New coordinates have been downloaded (latitude,longitude): " + coordinates.get(0) + "," + coordinates.get(1));
                    return coordinates;
                } else {
                    logger.warn("Failed to fetch coordinates");
                }
            } else {
                logger.warn("Response from server for customer contain NULL JSON object!");
            }
        } catch (Exception e) {
            logger.error("Error while addresses geolocating!");
            throw e;
        }

        return null;
    }

    private List<Double> getCoordinatesFromJSON(JSONObject jsonObject) {
        List<Double> coordinates = new ArrayList<>();
        try {
            double lat = jsonObject.getDouble("lat");
            double lon = jsonObject.getDouble("lon");
            coordinates.add(lat);
            coordinates.add(lon);
        } catch (org.json.JSONException e) {
            logger.error("Error while getting coordinates from JSON object!");
        }
        return coordinates;
    }
}
