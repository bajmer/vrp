package com.vrp.bajmer.network;

import com.vrp.bajmer.core.Customer;
import com.vrp.bajmer.core.Storage;
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

    public void downloadCustomersCoordinates() throws Exception {
        logger.info("Downloading customers coordinates...");
        try {
            for (Customer customer : Storage.getCustomerList()) {
                String URL = parseURL(beginOfURL, customer.getStreetAndNumber(), customer.getPostalCode(), customer.getCity());
                JSONObject jsonObject = sendRequest(URL);
                if (jsonObject != null) {
                    List<Double> coordinates = getCoordinatesFromJSON(jsonObject);
                    if (coordinates.size() == 2) {
                        customer.setLatitude(coordinates.get(0));
                        customer.setLongitude(coordinates.get(1));
                        logger.debug("Customer " + customer.getId() + "--" + customer.getFullAddress() + " has new coordinates: (latitude,longitude): " + customer.getLatitude() + "," + customer.getLongitude());
                    } else {
                        logger.warn("Failed to fetch coordinates for customer " + customer.getId() + "--" + customer.getFullAddress());
                    }
                } else {
                    logger.warn("Response from server for customer " + customer.getId() + " contain NULL JSON object!");
                }
            }
        } catch (Exception e) {
            logger.error("Error while addresses geolocating!");
            throw e;
        }
        logger.info("Downloading customers coordinates has been completed.");
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
