package network;

import core.Customer;
import core.Database;
import core.RouteSegment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.time.Duration;

/**
 * Klasa pobierajaca odległości i czasy przejazdu między klientami z lokalnego serwera OSRM
 */
public class DistanceMatrix extends JSON {

    /**
     * Logger klasy
     */
    private static final Logger logger = LogManager.getLogger(DistanceMatrix.class);

    /**
     * Poczatek adresu URL
     */
    private static final String BEGIN_OF_URL = "http://127.0.0.1:5000/route/v1/driving/";
//    private static final String BEGIN_OF_URL = "http://192.168.56.101:5000/route/v1/driving/";

    /**
     * Koncowka adresu URL
     */
    private static final String END_OF_URL = "?generate_hints=false";

    /**
     * Tworzy instancje klasy
     */
    public DistanceMatrix() {
    }

    /**
     * Pobiera odległości i czasy przejazdu między wszystkimi klientami z lokalnego serwera OSRM
     *
     * @throws Exception Wyjatek bledu pobierania
     */
    public void downloadDistanceMatrix() throws Exception {
        logger.info("Downloading distance matrix...");
        try {
            for (int i = 0; i < Database.getCustomerList().size(); i++) {
                for (int j = 0; j < Database.getCustomerList().size(); j++) {
                    Customer src = Database.getCustomerList().get(i);
                    Customer dst = Database.getCustomerList().get(j);
                    logger.debug("Calculating distance for " + src.getId() + " and " + dst.getId() + "...");
                    if (j != i) {
                        double srcLat = src.getLatitude();
                        double srcLon = src.getLongitude();
                        double dstLat = dst.getLatitude();
                        double dstLon = dst.getLongitude();
                        if (srcLat != 0 && srcLon != 0 && dstLat != 0 && dstLon != 0) {
                            String routeURL = parseURL(BEGIN_OF_URL, src.getLongitude(), src.getLatitude(), dst.getLongitude(), dst.getLatitude(), END_OF_URL);
                            JSONObject jsonObject = sendRequest(routeURL);
                            if (jsonObject != null) {
                                double distanceInKm = getDistanceFromJSON(jsonObject);
                                Duration duration = getDurationFromJSON(jsonObject);
                                String geometry = getGeometryFromJSON(jsonObject);
                                if (distanceInKm > 0) {
                                    Database.getRouteSegmentsList().add(new RouteSegment(src, dst, distanceInKm, duration, geometry));
                                    src.getDistances().put(dst.getId(), distanceInKm);
                                    src.getDurations().put(dst.getId(), duration);
                                    logger.debug("New route segment " + src.getId() + "-" + dst.getId() + ": " + distanceInKm + " km, " + duration.toMinutes() + " min.");
                                } else {
                                    logger.warn("There is incorrect distance for customers " + src.getId() + " and " + dst.getId() + ". New route segment is not created!");
                                }
                            } else {
                                logger.warn("Response from server for customers " + src.getId() + " and " + dst.getId() + " contain NULL JSON object!");
                            }
                        } else {
                            logger.warn("Customers have got incorrect coordinates!");
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error while downloading distance matrix!");
            throw e;
        }
        logger.info("Downloading distance matrix has been completed.");
    }

    /**
     * Odczytuje odleglosc miedzy klientami z obiektu JSON
     *
     * @param jsonObject Obiekt JSON, z ktorego nalezy odczytac dane
     * @return Zwraca odleglosc miedzy klientami w kilometrach
     */
    private double getDistanceFromJSON(JSONObject jsonObject) {
        double distance = -1; //jeżeli odległość jest ujemna, wówczas algorytm vrp będzie ją pomijał
        try {
            distance = jsonObject.getJSONArray("routes").getJSONObject(0).getDouble("distance");
        } catch (org.json.JSONException e) {
            logger.error("Error while getting distance from JSON object!");
        }
        if (distance >= 0) {
            double distanceKm = distance * 0.001;
            return new BigDecimal(distanceKm).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
        } else {
            return distance;
        }
    }

    /**
     * Odczytuje czas przejazdu miedzy klientami z obiektu JSON
     *
     * @param jsonObject Obiekt JSON, z ktorego nalezy odczytac dane
     * @return Zwraca czas przejazdu miedzy klientami w sekundach
     */
    private Duration getDurationFromJSON(JSONObject jsonObject) {
        Duration duration = Duration.ZERO;
        try {
            Double durationDouble = jsonObject.getJSONArray("routes").getJSONObject(0).getDouble("duration");
            duration = Duration.ofSeconds(durationDouble.longValue());
        } catch (org.json.JSONException e) {
            logger.error("Error while getting duration from JSON object!");
        }
        return duration;

    }

    /**
     * Odczytuje ksztalt odcinka w postaci kolejnych wspolrzednych zakodowanych kodem ASCII z obiektu JSON
     *
     * @param jsonObject Obiekt JSON, z ktorego nalezy odczytac dane
     * @return Zwraca ksztalt odcinka w postaci kolejnych wspolrzednych zakodowanych kodem ASCII
     */
    private String getGeometryFromJSON(JSONObject jsonObject) {
        String geometry = "";
        try {
            geometry = jsonObject.getJSONArray("routes").getJSONObject(0).getString("geometry");
        } catch (org.json.JSONException e) {
            logger.info("Error while getting geometry from JSON object!");
        }
        return geometry;
    }

    public void calculateEuc2DDistanceMatrix() throws Exception {
        logger.info("Calculating distance matrix for test file...");
        try {
            for (int i = 0; i < Database.getCustomerList().size(); i++) {
                for (int j = 0; j < Database.getCustomerList().size(); j++) {
                    Customer src = Database.getCustomerList().get(i);
                    Customer dst = Database.getCustomerList().get(j);
                    logger.debug("Calculating distance for " + src.getId() + " and " + dst.getId() + "...");
                    if (j != i) {
                        double srcLat = src.getLatitude();
                        double srcLon = src.getLongitude();
                        double dstLat = dst.getLatitude();
                        double dstLon = dst.getLongitude();

                        double xd = srcLat - dstLat;
                        double yd = srcLon - dstLon;
                        double distance = (double) Math.round(Math.sqrt(xd * xd + yd * yd));

                        Database.getRouteSegmentsList().add(new RouteSegment(src, dst, distance, Duration.ZERO, null));
                        src.getDistances().put(dst.getId(), distance);
                        src.getDurations().put(dst.getId(), Duration.ZERO);
                        logger.debug("New route segment " + src.getId() + "-" + dst.getId() + ": " + distance + " km");
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error while calculating distance matrix for test file!");
            throw e;
        }
        logger.info("Calculating distance matrix for test file has been completed.");
    }
}
