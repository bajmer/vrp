package network;

import org.json.JSONObject;
import project.Client;
import project.ClientsDatabase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by mbala on 25.05.17.
 */
public class DistanceMatrix {

    private final String beginOfURL = "http://192.168.56.101:5000/route/v1/driving/";
    private final String endOfURL = "?generate_hints=false&overview=false";

    //private String fullURL = "http://router.project-osrm.org/table/v1/driving/13.388860,52.517037;13.397634,52.529407;13.428555,52.523219";
    //private String fullURL = "http://192.168.56.101:5000/table/v1/driving/20.994873046875,52.50953477032727;22.313232421875,52.14697334064471;22.30224609375,52.696361078274485;21.346435546875,52.82932091031374;20.93994140625,52.74959372674114;20.159912109375,52.82932091031374;21.346435546875,52.5897007687178;21.785888671875,52.44261787120724;21.4013671875,52.0862573323384;21.07177734375,51.984880139916626;20.994873046875,52.50953477032727;22.313232421875,52.14697334064471;22.30224609375,52.696361078274485;21.346435546875,52.82932091031374;20.93994140625,52.74959372674114;20.159912109375,52.82932091031374;21.346435546875,52.5897007687178;21.785888671875,52.44261787120724;21.4013671875,52.0862573323384;21.07177734375,51.984880139916626;20.994873046875,52.50953477032727;22.313232421875,52.14697334064471;22.30224609375,52.696361078274485;21.346435546875,52.82932091031374;20.93994140625,52.74959372674114;20.159912109375,52.82932091031374;21.346435546875,52.5897007687178;21.785888671875,52.44261787120724;21.4013671875,52.0862573323384;21.07177734375,51.984880139916626;20.994873046875,52.50953477032727;22.313232421875,52.14697334064471;22.30224609375,52.696361078274485;21.346435546875,52.82932091031374;20.93994140625,52.74959372674114;20.159912109375,52.82932091031374;21.346435546875,52.5897007687178;21.785888671875,52.44261787120724;21.4013671875,52.0862573323384;21.07177734375,51.984880139916626;20.994873046875,52.50953477032727;22.313232421875,52.14697334064471;22.30224609375,52.696361078274485;21.346435546875,52.82932091031374;20.93994140625,52.74959372674114;20.159912109375,52.82932091031374;21.346435546875,52.5897007687178;21.785888671875,52.44261787120724;21.4013671875,52.0862573323384;21.07177734375,51.984880139916626;20.994873046875,52.50953477032727;22.313232421875,52.14697334064471;22.30224609375,52.696361078274485;21.346435546875,52.82932091031374;20.93994140625,52.74959372674114;20.159912109375,52.82932091031374;21.346435546875,52.5897007687178;21.785888671875,52.44261787120724;21.4013671875,52.0862573323384;21.07177734375,51.984880139916626;20.994873046875,52.50953477032727;22.313232421875,52.14697334064471;22.30224609375,52.696361078274485;21.346435546875,52.82932091031374;20.93994140625,52.74959372674114;20.159912109375,52.82932091031374;21.346435546875,52.5897007687178;21.785888671875,52.44261787120724;21.4013671875,52.0862573323384;21.07177734375,51.984880139916626;20.994873046875,52.50953477032727;22.313232421875,52.14697334064471;22.30224609375,52.696361078274485;21.346435546875,52.82932091031374;20.93994140625,52.74959372674114;20.159912109375,52.82932091031374;21.346435546875,52.5897007687178;21.785888671875,52.44261787120724;21.4013671875,52.0862573323384;21.07177734375,51.984880139916626;20.994873046875,52.50953477032727;22.313232421875,52.14697334064471;22.30224609375,52.696361078274485;21.346435546875,52.82932091031374;20.93994140625,52.74959372674114;20.159912109375,52.82932091031374;21.346435546875,52.5897007687178;21.785888671875,52.44261787120724;21.4013671875,52.0862573323384;21.07177734375,51.984880139916626;20.994873046875,52.50953477032727;22.313232421875,52.14697334064471;22.30224609375,52.696361078274485;21.346435546875,52.82932091031374;20.93994140625,52.74959372674114;20.159912109375,52.82932091031374;21.346435546875,52.5897007687178;21.785888671875,52.44261787120724;21.4013671875,52.0862573323384;21.07177734375,51.984880139916626";
    //private String fullURL = "https://graphhopper.com/api/1/matrix?key=[YOUR_KEY]%22%20-d%20%27{%22elevation%22:false,%22out_arrays%22:[%22weights%22],%22from_points%22:[[-0.087891,51.534377],[-0.090637,51.467697],[-0.171833,51.521241],[-0.211487,51.473685]],%22to_points%22:[[-0.087891,51.534377],[-0.090637,51.467697],[-0.171833,51.521241],[-0.211487,51.473685]],%22vehicle%22:%22car%22}%27";
    public DistanceMatrix() {
    }

    public void calculateDistanceMatrix() {
        try {
            //MockClients mockClients = new MockClients();
            for (Client src : ClientsDatabase.getClientsList()) {
                for (Client dst : ClientsDatabase.getClientsList()) {
                    String routeURL = parseURL(src.getLongitude(), src.getLatitude(), dst.getLongitude(), dst.getLatitude());
                    JSONObject jsonObject = sendRequest(routeURL);
                    if (jsonObject != null) {
                        double distanceInKm = getDistanceInKmFromJSON(jsonObject);
                        System.out.println("Distance " + src.getId() + "-" + dst.getId() + ": " + distanceInKm);

                        src.getDistances().put(dst.getId(), distanceInKm);
                    } else {
                        System.out.println("JSON object is null!");
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error while calculating distance matrix.");
            e.printStackTrace();
        }
    }

    public String parseURL(double srcLong, double srcLat, double dstLong, double dstLat) {
        return beginOfURL + srcLong + "," + srcLat + ";" + dstLong + "," + dstLat + endOfURL;
    }

    public JSONObject sendRequest(String routeURL) throws ConnectException {
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
                System.out.println("Response error: " + connection.getResponseCode() + ", " + connection.getResponseMessage());
            }
        } catch (MalformedURLException e) {
            System.out.println("Bad URL address!");
        } catch (ConnectException e) {
            System.out.println("Application cannot connect to server!");
            throw e;
        } catch (IOException e) {
            System.out.println("Unexpected error while sending request!");
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

    public double getDistanceInKmFromJSON(JSONObject jsonObject) {
        double distance = -1; //jeżeli odległość jest ujemna, wówczas algorytm vrp będzie ją pomijał
        try {
            distance = jsonObject.getJSONArray("routes").getJSONObject(0).getDouble("distance");
        } catch (org.json.JSONException e) {
            System.out.println("Error while getting distance from JSON object!");
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
