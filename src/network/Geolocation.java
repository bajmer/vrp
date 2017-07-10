package network;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import project.Customer;
import project.Database;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mbala on 10.07.17.
 */
public class Geolocation extends JSON {
    private static final Logger logger = LogManager.getLogger(Geolocation.class);

    private final String separator = ",";
    private final String beginOfURL = "http://nominatim.openstreetmap.org/search?format=json";
    //    http://nominatim.openstreetmap.org/search?format=json&street=10%20Spokojna&postalcode=07-200&city=Wyszk%C3%B3w&country=Polska


    public Geolocation() {
    }

    public void getCustomersCoordinatesFromAddresses() {
        try {
            for (Customer customer : Database.getCustomerList()) {
                String fullAddress = customer.getAddress();
                String[] addressFields = StringUtils.splitByWholeSeparatorPreserveAllTokens(fullAddress, separator);
                String URL = parseURL(beginOfURL, addressFields);
                logger.info(URL);
                JSONObject jsonObject = sendRequest(URL);
                if (jsonObject != null) {
                    List<Double> coordinates = getCoordinatesFromJSON(jsonObject);
                    customer.setLatitude(coordinates.get(0));
                    customer.setLongitude(coordinates.get(1));
                    logger.info("Coordinates for customer " + customer.getId() + ": latitude-" + customer.getLatitude() + ", longitude-" + customer.getLongitude());
                } else {
                    logger.info("JSON object is null!");
                }
            }
        } catch (Exception e) {
            logger.error("Error while getting customers coordinates.", e);
        }
    }

    private List<Double> getCoordinatesFromJSON(JSONObject jsonObject) {
        List<Double> coordinates = new ArrayList<Double>();
        try {
            double lat = jsonObject.getJSONArray("place").getJSONObject(0).getDouble("lat");
            logger.info("Latitude: " + lat);
            double lon = jsonObject.getJSONArray("place").getJSONObject(0).getDouble("lon");
            logger.info("Longitude: " + lon);
            coordinates.add(lat);
            coordinates.add(lon);
        } catch (org.json.JSONException e) {
            logger.error("Error while getting distance from JSON object!", e);
        }
        return coordinates;
    }
}
