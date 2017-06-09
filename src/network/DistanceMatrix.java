package network;

import mock.MockClients;
import org.json.JSONObject;
import project.Client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;

/**
 * Created by mbala on 25.05.17.
 */
public class DistanceMatrix {

    private final String beginOfURL = "http://192.168.56.101:5000/route/v1/driving/";
    private final String endOfURL = "?generate_hints=false&overview=false";
    private HttpURLConnection connection = null;
    //private String fullURL = "http://router.project-osrm.org/table/v1/driving/13.388860,52.517037;13.397634,52.529407;13.428555,52.523219";
    private String fullURL = "http://192.168.56.101:5000/table/v1/driving/20.994873046875,52.50953477032727;22.313232421875,52.14697334064471;22.30224609375,52.696361078274485;21.346435546875,52.82932091031374;20.93994140625,52.74959372674114;20.159912109375,52.82932091031374;21.346435546875,52.5897007687178;21.785888671875,52.44261787120724;21.4013671875,52.0862573323384;21.07177734375,51.984880139916626;20.994873046875,52.50953477032727;22.313232421875,52.14697334064471;22.30224609375,52.696361078274485;21.346435546875,52.82932091031374;20.93994140625,52.74959372674114;20.159912109375,52.82932091031374;21.346435546875,52.5897007687178;21.785888671875,52.44261787120724;21.4013671875,52.0862573323384;21.07177734375,51.984880139916626;20.994873046875,52.50953477032727;22.313232421875,52.14697334064471;22.30224609375,52.696361078274485;21.346435546875,52.82932091031374;20.93994140625,52.74959372674114;20.159912109375,52.82932091031374;21.346435546875,52.5897007687178;21.785888671875,52.44261787120724;21.4013671875,52.0862573323384;21.07177734375,51.984880139916626;20.994873046875,52.50953477032727;22.313232421875,52.14697334064471;22.30224609375,52.696361078274485;21.346435546875,52.82932091031374;20.93994140625,52.74959372674114;20.159912109375,52.82932091031374;21.346435546875,52.5897007687178;21.785888671875,52.44261787120724;21.4013671875,52.0862573323384;21.07177734375,51.984880139916626;20.994873046875,52.50953477032727;22.313232421875,52.14697334064471;22.30224609375,52.696361078274485;21.346435546875,52.82932091031374;20.93994140625,52.74959372674114;20.159912109375,52.82932091031374;21.346435546875,52.5897007687178;21.785888671875,52.44261787120724;21.4013671875,52.0862573323384;21.07177734375,51.984880139916626;20.994873046875,52.50953477032727;22.313232421875,52.14697334064471;22.30224609375,52.696361078274485;21.346435546875,52.82932091031374;20.93994140625,52.74959372674114;20.159912109375,52.82932091031374;21.346435546875,52.5897007687178;21.785888671875,52.44261787120724;21.4013671875,52.0862573323384;21.07177734375,51.984880139916626;20.994873046875,52.50953477032727;22.313232421875,52.14697334064471;22.30224609375,52.696361078274485;21.346435546875,52.82932091031374;20.93994140625,52.74959372674114;20.159912109375,52.82932091031374;21.346435546875,52.5897007687178;21.785888671875,52.44261787120724;21.4013671875,52.0862573323384;21.07177734375,51.984880139916626;20.994873046875,52.50953477032727;22.313232421875,52.14697334064471;22.30224609375,52.696361078274485;21.346435546875,52.82932091031374;20.93994140625,52.74959372674114;20.159912109375,52.82932091031374;21.346435546875,52.5897007687178;21.785888671875,52.44261787120724;21.4013671875,52.0862573323384;21.07177734375,51.984880139916626;20.994873046875,52.50953477032727;22.313232421875,52.14697334064471;22.30224609375,52.696361078274485;21.346435546875,52.82932091031374;20.93994140625,52.74959372674114;20.159912109375,52.82932091031374;21.346435546875,52.5897007687178;21.785888671875,52.44261787120724;21.4013671875,52.0862573323384;21.07177734375,51.984880139916626;20.994873046875,52.50953477032727;22.313232421875,52.14697334064471;22.30224609375,52.696361078274485;21.346435546875,52.82932091031374;20.93994140625,52.74959372674114;20.159912109375,52.82932091031374;21.346435546875,52.5897007687178;21.785888671875,52.44261787120724;21.4013671875,52.0862573323384;21.07177734375,51.984880139916626";
    //private String fullURL = "https://graphhopper.com/api/1/matrix?key=[YOUR_KEY]%22%20-d%20%27{%22elevation%22:false,%22out_arrays%22:[%22weights%22],%22from_points%22:[[-0.087891,51.534377],[-0.090637,51.467697],[-0.171833,51.521241],[-0.211487,51.473685]],%22to_points%22:[[-0.087891,51.534377],[-0.090637,51.467697],[-0.171833,51.521241],[-0.211487,51.473685]],%22vehicle%22:%22car%22}%27";
    public DistanceMatrix() {

    }

    public void parseURL() {
        StringBuilder sb = new StringBuilder();
        sb.append(beginOfURL);
        MockClients mockClients = new MockClients();
        for (int i = 0; i < mockClients.getListOfClients().size(); i++) {
            for (int j = 0; j < mockClients.getListOfClients().size(); j++) {

            }
        }


    }

    public void calculateDistanceMatrix() {
        try {
            connection = (HttpURLConnection) new URL(fullURL).openConnection();
            connection.setRequestMethod("GET");
            connection.setUseCaches(false);
            connection.setReadTimeout(5000);
            connection.connect();

            StringBuilder sb = new StringBuilder();

            if (HttpURLConnection.HTTP_OK == connection.getResponseCode()) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                String line;
                while ((line = in.readLine()) != null) {
                    sb.append(line);
                    sb.append("\n");

                    System.out.println(sb.toString());
                }

            } else {
                System.out.println("Response error: " + connection.getResponseCode() + ", " + connection.getResponseMessage());
            }
        } catch (Exception e) {
            System.out.println("Unexpected network error.");
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public void fakeCalculateDistanceMatrix() {
        try {
            MockClients mockClients = new MockClients();
            int counter = 1;
            for (Client i : mockClients.getListOfClients()) {
                for (Client j : mockClients.getListOfClients()) {
                    StringBuilder sbin = new StringBuilder();
                    sbin.append(beginOfURL);
                    sbin.append(i.getLongitude());
                    sbin.append(",");
                    sbin.append(i.getLatitude());
                    sbin.append(";");
                    sbin.append(j.getLongitude());
                    sbin.append(",");
                    sbin.append(j.getLatitude());
                    sbin.append(endOfURL);
                    connection = (HttpURLConnection) new URL(sbin.toString()).openConnection();
                    connection.setRequestMethod("GET");
                    connection.setUseCaches(false);
                    connection.setReadTimeout(2000);
                    connection.connect();

                    StringBuilder sb = new StringBuilder();

                    if (HttpURLConnection.HTTP_OK == connection.getResponseCode()) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                        String line;
                        while ((line = in.readLine()) != null) {
                            //sb.append(counter);
                            sb.append(line);
                            //sb.append("\n");

                            System.out.println(sb.toString());
                        }

                    } else {
                        System.out.println("Response error: " + connection.getResponseCode() + ", " + connection.getResponseMessage());
                    }
                    JSONObject jsonObject = new JSONObject(sb.toString());
                    Double distance = jsonObject.getJSONArray("routes").getJSONObject(0).getDouble("distance");
                    DecimalFormat f = new DecimalFormat("#.#");
                    Double distanceKm = distance * 0.001;

                    System.out.println(f.format(distanceKm) + " km\n");


                    counter++;
                }
            }

        } catch (Exception e) {
            System.out.println("Unexpected network error.");
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
