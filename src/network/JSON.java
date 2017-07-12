package network;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Marcin on 2017-07-10.
 */
public class JSON {

    private static final Logger logger = LogManager.getLogger(JSON.class);

    public JSON() {
    }

    public String parseURL(String beginOfURL, double srcLong, double srcLat, double dstLong, double dstLat, String endOfURL) {
        return beginOfURL + srcLong + "," + srcLat + ";" + dstLong + "," + dstLat + endOfURL;
    }

    public String parseURL(String beginOfURL, String[] addressFields) {
        String streetAndNumber = addressFields[0];
        streetAndNumber = streetAndNumber.replace("ul.", "");
        streetAndNumber = streetAndNumber.replace(" ", "%20");
        String postalCodeAndCity = addressFields[1];
        Pattern patternCode = Pattern.compile("[0-9]{2}-[0-9]{3}");
        Matcher matcher = patternCode.matcher(postalCodeAndCity);
        String postalCode = null;
        if (matcher.find()) {
            postalCode = matcher.group();
        }
        String city = postalCodeAndCity.substring(8);
        city = city.replace(" ", "%20");

        return beginOfURL + "&street=" + streetAndNumber + "&postalcode=" + postalCode
                + "&city=" + city + "&country=Polska";
    }

    public JSONObject sendRequest(String url) throws ConnectException {
        logger.debug("Sending a request to URL: " + url + " ...");
        HttpURLConnection connection = null;
        String response = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            connection.setUseCaches(false);
            connection.setReadTimeout(5000);
            connection.connect();

            if (HttpURLConnection.HTTP_OK == connection.getResponseCode()) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                response = in.readLine();
            } else {
                logger.warn("Response error: " + connection.getResponseCode() + ", " + connection.getResponseMessage());
            }
        } catch (MalformedURLException e) {
            logger.error("Bad URL address!");
        } catch (ConnectException e) {
            logger.error("Application cannot connect to server!");
            throw e;
        } catch (IOException e) {
            logger.error("Unexpected error while sending request!");
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        if (response != null && response.length() > 2) {
            if (response.startsWith("[") && response.endsWith("]")) {
                response = response.substring(1, response.length() - 1);
            }
            return new JSONObject(response);
        } else {
            return null;
        }
    }
}
