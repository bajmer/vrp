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
        return beginOfURL + "&street=" + addressFields[0] + "&postalcode=" + addressFields[1]
                + "&city=" + addressFields[2] + "&country=" + addressFields[3];
    }

    public JSONObject sendRequest(String routeURL) throws ConnectException {
        HttpURLConnection connection = null;
        String response = null;
        try {
            connection = (HttpURLConnection) new URL(routeURL).openConnection();
            connection.setRequestMethod("GET");
            connection.setUseCaches(false);
            connection.setReadTimeout(10000);
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
}
