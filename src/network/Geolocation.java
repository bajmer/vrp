package network;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import project.Customer;
import project.Database;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Created by mbala on 10.07.17.
 */
public class Geolocation {
    private static final Logger logger = LogManager.getLogger(DistanceMatrix.class);

    private final String separator = ",";
    private final String beginOfURL = "http://nominatim.openstreetmap.org/search?format=json&";
    //    http://nominatim.openstreetmap.org/search?format=json&street=10%20Spokojna&postalcode=07-200&city=Wyszk%C3%B3w&country=Polska


    public Geolocation() {
    }

    public void getCustomersCoordinatesFromAddresses() {
        try {
            for (Customer customer : Database.getCustomerList()) {
                String fullAddress = customer.getAddress();
                String[] addressFields = StringUtils.splitByWholeSeparatorPreserveAllTokens(fullAddress, separator);
                String URL = beginOfURL
                        + "street=" + addressFields[0]
                        + "postalcode=" + addressFields[1]
                        + "city=" + addressFields[2]
                        + "country=" + addressFields[3];

                JSONObject jsonObject = sendRequest(URL);
                if (jsonObject != null) {
                    List<Double> coordinates = getCoordinatesFromJSON(jsonObject);
                    customer.setLatitude(coordinates.get(0));
                    customer.setLongitude(coordinates.get(1));
                    logger.info("Coordinates for customer " + customer.getId() + "founded: latitude-" + customer.getLatitude() + ", longitude-" + customer.getLongitude());
                } else {
                    logger.info("JSON object is null!");
                }
            }
        } catch (Exception e) {
            logger.error("Error while getting customers coordinates.", e);
        }
    }

    private JSONObject sendRequest(String routeURL) throws ConnectException {
        HttpURLConnection connection = null;
        String response = null;
        try {
            connection = (HttpURLConnection) new URL(routeURL).openConnection();
            connection.setRequestMethod("GET");
            connection.setUseCaches(false);
            connection.setReadTimeout(1000);
            connection.connect();

            if (HttpURLConnection.HTTP_OK == connection.getResponseCode()) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                response = in.readLine();
            } else {
                logger.warn("Response error: " + connection.getResponseCode() + ", " + connection.getResponseMessage());
            }
        } catch (MalformedURLException e) {
            logger.error("Bad URL address!", e);
        } catch (ConnectException e) {
            logger.error("Application cannot connect to server!", e);
            throw e;
        } catch (IOException e) {
            logger.error("Unexpected error while sending request!", e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        if (response != null) {
            return new JSONObject(response);
        } else {
            return null;
        }
    }

    private List<Double> getCoordinatesFromJSON(JSONObject jsonObject) {
        double lat = -1; //jeżeli odległość jest ujemna, wówczas algorytm vrp będzie ją pomijał
        double lon = -1;
        try {
            lat = jsonObject.getJSONArray("routes").getJSONObject(0).getDouble("distance");
        } catch (org.json.JSONException e) {
            logger.error("Error while getting distance from JSON object!", e);
        }
        if (distance >= 0) {
            double distanceKm = distance * 0.001;
            double roundedDistanceKm = new BigDecimal(distanceKm).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
            return roundedDistanceKm;
        } else {
            return distance;
        }
    }
}
