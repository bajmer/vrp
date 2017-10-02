package network;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Geolocator extends JSON {

    private static final Logger logger = LogManager.getLogger(Geolocator.class);

    private static final String BEGIN_OF_URL = "https://maps.googleapis.com/maps/api/geocode/json?address=";
    private static final String END_OF_URL = "&key=AIzaSyC-Nh-HTfhZ_KeuVwiF0XSGqeoJopBonRA";

    public Geolocator() {
    }

    public List<Double> downloadCoordinates(String streetAndNumber, String postalCode, String city, int lineNumber) throws Exception {
        logger.debug("Downloading coordinates for customer in line " + lineNumber + "...");
        try {
            String URL = parseURL(BEGIN_OF_URL, streetAndNumber, postalCode, city, END_OF_URL);
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
            double lat = jsonObject.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lat");
            double lon = jsonObject.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lng");
            coordinates.add(lat);
            coordinates.add(lon);
        } catch (org.json.JSONException e) {
            logger.error("Error while getting coordinates from JSON object!");
        }
        return coordinates;
    }
}
