package network;

import core.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Klasa pobierajaca mapy statyczne z serwera Google
 */
public class MapImage {

    /**
     * Logger klasy
     */
    private static final Logger logger = LogManager.getLogger(MapImage.class);

    /**
     * Domyslny poczatek adresu URL
     */
    private static final String DEFAULT_BEGIN_OF_URL = "https://maps.googleapis.com/maps/api/staticmap?size=640x640&maptype=roadmap&language=pl";

    /**
     * Poczatek adresu URL
     */
    private static final String BEGIN_OF_URL = "https://maps.googleapis.com/maps/api/staticmap?size=640x640&maptype=roadmap&language=pl";

    /**
     * Koncowka adresu URL
     */
    private static final String END_OF_URL = "&key=AIzaSyC-Nh-HTfhZ_KeuVwiF0XSGqeoJopBonRA";

    /**
     * Fragment adresu URL odpowiadajacy domyslnemu znacznikowi magazynu
     */
    private static final String DEFAULT_DEPOT_MARKER = "&markers=size:small|color:yellow|";

    /**
     * Fragment adresu URL odpowiadajacy domyslnemu znacznikowi klienta
     */
    private static final String DEFAULT_CUSTOMER_MARKER = "&markers=size:small|color:blue";

    /**
     * Fragment adresu URL odpowiadajacy wiekszemu znacznikowi magazynu
     */
    private static final String BIGGER_DEPOT_MARKER = "&markers=size:normal|color:yellow|";

    /**
     * Fragment adresu URL odpowiadajacy wiekszemu znacznikowi klienta
     */
    private static final String BIGGER_CUSTOMER_MARKER = "&markers=size:normal|color:blue";

    /**
     * Fragment adresu URL odpowiadajacy wybranemu aktualnie klientowi
     */
    private static final String CHOOSEN_MARKER = "&markers=size:normal|color:red|";

    /**
     * Fragment adresu URL odpowiadajacy poczatkowemu klientowi odcinka
     */
    private static final String FROM_MARKER = "&markers=size:normal|color:green|";

    /**
     * Fragment adresu URL odpowiadajacy docelowemu klientowi odcinka
     */
    private static final String TO_MARKER = "&markers=size:normal|color:red|";

    /**
     * Mapa kolorow
     */
    private static final Map<String, String> COLOURS = fillColours();

    /**
     * Tworzy obiekt klasy
     */
    public MapImage() {
    }

    /**
     * Tworzy nowa mape kolorow
     *
     * @return Zwraca wypelniona mape kolorow
     */
    private static Map<String, String> fillColours() {
        Map<String, String> colours = new LinkedHashMap<>();
        colours.put("red", "0xFF0000FF");
        colours.put("lime", "0x00FF00FF");
        colours.put("blue", "0x0000FFFF");
        colours.put("yellow", "0xFFFF00FF");
        colours.put("aqua", "0x00FFFFFF");
        colours.put("magenta", "0xFF00FFFF");
        colours.put("black", "0x000000FF");
        colours.put("brown", "0xA52A2AFF");
        colours.put("dark magenta", "0x8B008BFF");
        colours.put("olive", "0x808000FF");
        colours.put("orange", "0xFFA500FF");
        colours.put("yellow green", "0x9ACD32FF");
        colours.put("dark green", "0x006400FF");
        return colours;
    }

    /**
     * Pobiera obraz mapy z narysowanym rozwiazaniem
     *
     * @param s Rozwiazanie, ktore zostanie naniesione na mape
     * @throws IOException Wyjatek bledu wejscia/wyjscia
     */
    public void createSolutionImage(Solution s) throws IOException {
        String solutionImageName = "S" + s.getSolutionID() + "_" + s.getUsedAlgorithm();
        logger.info("Creating an image of solution " + solutionImageName + "...");
        ImageIcon mapIcon = null;
        try {
            String fullURL = parseURL(s, false);
            if (fullURL.length() <= 8192) {
                mapIcon = sendRequestToGoogleMaps(fullURL);
            } else {
                String simpleURL = parseURL(s, true);
                if (simpleURL.length() <= 8192) {
                    mapIcon = sendRequestToGoogleMaps(simpleURL);
                } else {
                    logger.warn("Cannot create an image of the solution because of too long URL address!");
                }
            }
            s.setImageIcon(mapIcon);
        } catch (Exception e) {
            logger.warn("Unexpected error while creating an image of solution!");
        }

        logger.info("Creating an image of solution " + solutionImageName + " has been completed.");
    }

    /**
     * Pobiera obraz mapy z narysowana trasa
     *
     * @param s Rozwiazanie
     * @param r Trasa, ktora zostanie naniesiona na mape
     * @throws IOException Wyjatek bledu wejscia/wyjscia
     */
    public void createRouteImage(Solution s, Route r) throws IOException {
        String routeImageName = "S" + s.getSolutionID() + "_R" + r.getId();
        logger.info("Creating an image of route " + routeImageName + "...");
        ImageIcon mapIcon = null;
        try {
            String fullUrlForSingleRoute = parseURL(r, false);
            if (fullUrlForSingleRoute.length() <= 8192) {
                mapIcon = sendRequestToGoogleMaps(fullUrlForSingleRoute);
            } else {
                String simpleUrlForSingleRoute = parseURL(r, true);
                if (simpleUrlForSingleRoute.length() <= 8192) {
                    mapIcon = sendRequestToGoogleMaps(simpleUrlForSingleRoute);
                } else {
                    logger.warn("Cannot create an image of the route because of too long URL address!");
                }
            }
            r.setImageIcon(mapIcon);
        } catch (Exception e) {
            logger.warn("Unexpected error while creating an image of solution!");
        }

        logger.info("Creating an image of route " + routeImageName + " has been completed.");
    }

    /**
     * Pobiera obraz mapy z narysowanym odcinkiem trasy
     *
     * @param s  Rozwiazanie
     * @param r  Trasa
     * @param rs Odcinek trasy, ktory zostanie naniesiony na mape
     * @throws IOException Wyjatek bledu wejscia/wyjscia
     */
    public void createSegmentImage(Solution s, Route r, RouteSegment rs) throws IOException {
        String routeSegmentImageName = "S" + s.getSolutionID() + "_R" + r.getId() + "_RS" + rs.getSrc().getId() + "-" + rs.getDst().getId();
        logger.info("Creating an image of route segment " + routeSegmentImageName + "...");

        String url = parseURL(rs);
        ImageIcon mapIcon = sendRequestToGoogleMaps(url);
        rs.setImageIcon(mapIcon);

        logger.info("Creating an image of route segment " + routeSegmentImageName + " has been completed.");
    }

    /**
     * Pobiera obraz mapy zawierajacej magazyn i wszystkich klientow, z wyroznionym aktualnie wybranym klientem
     *
     * @param c Aktualnie wybrany klient
     * @throws IOException Wyjatek bledu wejscia/wyjscia
     */
    public void createCustomerImage(Customer c) throws IOException {
        String customerImageName = "C" + c.getId();
        logger.info("Creating an image of customer " + customerImageName + "...");

        String url = parseURL(c);
        ImageIcon mapIcon = sendRequestToGoogleMaps(url);
        c.setImageIcon(mapIcon);

        logger.info("Creating an image of customer " + customerImageName + " has been completed.");
    }

    /**
     * Parsuje adres URL, na ktory zostanie wyslane zapytanie
     *
     * @param s         Rozwiazanie, ktore zostanie naniesione na mape
     * @param simpleURL Flaga okreslajaca, czy rozwiazanie ma zostac przedstawione w uproszczony sposob za pomoca prostuch linii
     * @return Zwraca sparsowany adres URL
     */
    private String parseURL(Solution s, boolean simpleURL) {
        StringBuilder paths = new StringBuilder();
        StringBuilder markers = new StringBuilder();

        markers.append(DEFAULT_DEPOT_MARKER);
        markers.append(s.getDepot().getLatitude()).append(",").append(s.getDepot().getLongitude());
        markers.append(DEFAULT_CUSTOMER_MARKER);

        for (Customer c : Database.getCustomerList()) {
            if (c.getId() == 0) {
                continue;
            }
            double lat = c.getLatitude();
            double lon = c.getLongitude();
            if (lat != 0 && lon != 0) {
                markers.append("|").append(lat).append(",").append(lon);
            }
        }

        int colourIndex = 0;
        for (Route r : s.getListOfRoutes()) {
            String colour = (new ArrayList<>(COLOURS.values())).get(colourIndex);
            if (!simpleURL) {
                for (RouteSegment rs : r.getRouteSegments()) {
                    paths.append("&path=color:").append(colour).append("|weight:2|enc:").append(rs.getGeometry());
                }
            } else {
                paths.append("&path=color:").append(colour).append("|weight:2");
                for (Customer c : r.getCustomersInRoute()) {
                    paths.append("|").append(c.getLatitude()).append(",").append(c.getLongitude());
                }
            }
            colourIndex++;
            if (colourIndex == COLOURS.size()) {
                colourIndex = 0;
            }
        }
        return DEFAULT_BEGIN_OF_URL + markers.toString() + paths.toString() + END_OF_URL;
    }

    /**
     * Parsuje adres URL, na ktory zostanie wyslane zapytanie
     *
     * @param r Trasa, ktora zostanie naniesiona na mape
     * @return Zwraca sparsowany adres URL
     */
    private String parseURL(Route r, boolean simpleURL) {
        StringBuilder path = new StringBuilder();
        StringBuilder markers = new StringBuilder();

        markers.append(BIGGER_DEPOT_MARKER);
        markers.append(r.getCustomersInRoute().get(0).getLatitude()).append(",").append(r.getCustomersInRoute().get(0).getLongitude());
        markers.append(BIGGER_CUSTOMER_MARKER);
        for (Customer c : r.getCustomersInRoute()) {
            if (c.getId() == 0) {
                continue;
            }
            double lat = c.getLatitude();
            double lon = c.getLongitude();
            if (lat != 0 && lon != 0) {
                markers.append("|").append(lat).append(",").append(lon);
            }
        }

        if (!simpleURL) {
            for (RouteSegment rs : r.getRouteSegments()) {
                path.append("&path=color:").append(COLOURS.get("blue")).append("|weight:2|enc:").append(rs.getGeometry());
            }
        } else {
            path.append("&path=color:").append(COLOURS.get("blue")).append("|weight:2");
            for (Customer c : r.getCustomersInRoute()) {
                path.append("|").append(c.getLatitude()).append(",").append(c.getLongitude());
            }
        }

        return BEGIN_OF_URL + markers.toString() + path.toString() + END_OF_URL;
    }

    /**
     * Parsuje adres URL, na ktory zostanie wyslane zapytanie
     *
     * @param rs Odcinek trasy, ktory zostanie naniesiony na mape
     * @return Zwraca sparsowany adres URL
     */
    private String parseURL(RouteSegment rs) {
        String srcMarker = FROM_MARKER + rs.getSrc().getLatitude() + "," + rs.getSrc().getLongitude();
        String dstMarker = TO_MARKER + rs.getDst().getLatitude() + "," + rs.getDst().getLongitude();
        String path = "&path=color:" + COLOURS.get("blue") + "|weight:2|enc:" + rs.getGeometry();

        return BEGIN_OF_URL + srcMarker + dstMarker + path + END_OF_URL;
    }

    /**
     * Parsuje adres URL, na ktory zostanie wyslane zapytanie
     *
     * @param c Aktualnie wybrany klient, ktory zostanie naniesiony na mape w postaci wiekszego znacznika
     * @return Zwraca sparsowany adres URL
     */
    private String parseURL(Customer c) {
        StringBuilder markers = new StringBuilder();
        markers.append(CHOOSEN_MARKER).append(c.getLatitude()).append(",").append(c.getLongitude());
        for (Customer customer : Database.getCustomerList()) {
            if (!customer.equals(c)) {
                if (customer.getId() == 0) {
                    markers.append(DEFAULT_DEPOT_MARKER).append(customer.getLatitude()).append(",").append(customer.getLongitude());
                    break;
                }
            }
        }

        markers.append(DEFAULT_CUSTOMER_MARKER);
        for (Customer customer : Database.getCustomerList()) {
            if (!customer.equals(c)) {
                if (customer.getId() == 0) {
                    continue;
                }
                double lat = customer.getLatitude();
                double lon = customer.getLongitude();
                if (lat != 0 && lon != 0) {
                    markers.append("|").append(lat).append(",").append(lon);
                }
            }
        }

        return DEFAULT_BEGIN_OF_URL + markers + END_OF_URL;
    }

    /**
     * Wysyla zapytanie HTTP na serwer, a nastepnie pobiera odpowiedz i opakowuje ja w obiekt JSON
     *
     * @param url Adres URL, na ktory bedzie wyslane zapytanie
     * @throws IOException Wyjatek bledu wejscia/wyjscia
     */
    private ImageIcon sendRequestToGoogleMaps(String url) throws IOException {
        logger.debug("Sending request to Google Maps...");
        ImageIcon imageIcon;
        try {
            InputStream inputStream = new URL(url).openStream();
            Image image = ImageIO.read(inputStream);

            imageIcon = new ImageIcon(image);

            inputStream.close();

        } catch (MalformedURLException e) {
            logger.error("Invalid URL address!");
            throw e;
        } catch (IOException e) {
            logger.error("Unexpected error while connecting to GoogleMaps server!");
            throw e;
        }
        logger.debug("Sending request to Google Maps has been completed.");

        return imageIcon;
    }
}
