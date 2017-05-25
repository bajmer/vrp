package network;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by mbala on 25.05.17.
 */
public class DistanceMatrix {

    private final String baseURL = "aaaaaaa";
    private HttpURLConnection connection = null;
    private String fullURL = "http://router.project-osrm.org/table/v1/driving/13.388860,52.517037;13.397634,52.529407;13.428555,52.523219";

    public DistanceMatrix() {

    }

    public void parseURL() {

    }

    public void calculateDistanceMatrix() {
        try {
            connection = (HttpURLConnection) new URL(fullURL).openConnection();
            connection.setRequestMethod("GET");
            connection.setUseCaches(false);
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
}
