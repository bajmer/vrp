package com.vrp.bajmer.network;

import com.vrp.bajmer.core.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Marcin on 2017-07-12.
 */
public class MapImage {

    private static final Logger logger = LogManager.getLogger(MapImage.class);

    private static final String defaultBeginOfURL = "https://maps.googleapis.com/maps/api/staticmap?center=52.23,21.2&zoom=8&size=640x640&maptype=roadmap&language=pl";
    private static final String beginOfURL = "https://maps.googleapis.com/maps/api/staticmap?size=640x640&maptype=roadmap&language=pl";
    private static final String endOfURL = "&key=AIzaSyC-Nh-HTfhZ_KeuVwiF0XSGqeoJopBonRA";
    private static final String imagePath = "solution_images/";

    private static final String defaultDepotMarker = "&markers=size:small|color:green|";
    private static final String defaultCustomerMarker = "&markers=size:small|color:blue";
    private static final String biggerDepotMarker = "&markers=size:normal|color:green|";
    private static final String biggerCustomerMarker = "&markers=size:normal|color:blue";
    private static final String choosenMarker = "&markers=size:normal|color:red|";
    private static final String fromMarker = "&markers=size:normal|color:green|";
    private static final String toMarker = "&markers=size:normal|color:red|";
    private static final Map<String, String> colours = fillColours();

    public MapImage() {
    }

    private static Map<String, String> fillColours() {
        Map<String, String> colours = new LinkedHashMap<String, String>();
        colours.put("black", "0x000000FF");
        colours.put("red", "0xFF0000FF");
        colours.put("lime", "0x00FF00FF");
        colours.put("blue", "0x0000FFFF");
        colours.put("yellow", "0xFFFF00FF");
        colours.put("aqua", "0x00FFFFFF");
        colours.put("magenta", "0xFF00FFFF");
        colours.put("brown", "0xA52A2AFF");
        colours.put("dark magenta", "0x8B008BFF");
        colours.put("olive", "0x808000FF");
        colours.put("orange", "0xFFA500FF");
        colours.put("yellow green", "0x9ACD32FF");
        colours.put("dark green", "0x006400FF");
        return colours;
    }

    public void createSolutionImage(Solution s) throws IOException {
        String solutionImageName = imagePath + "S" + s.getSolutionID() + "_" + s.getUsedAlgorithm();
        logger.debug("Creating an images of solution " + solutionImageName + "...");

        String url = parseURL(s);
        sendRequestToGoogleMaps(url, solutionImageName);
        s.setImageIcon(new ImageIcon(solutionImageName));

        logger.debug("Creating an images of solution " + solutionImageName + " has been completed.");
    }

    public void createRouteImage(Solution s, Route r) throws IOException {
        String routeImageName = imagePath + "S" + s.getSolutionID() + "_R" + r.getId();
        logger.debug("Creating an images of route" + routeImageName + "...");

        String urlForSingleRoute = parseURL(r);
        sendRequestToGoogleMaps(urlForSingleRoute, routeImageName);
        r.setImageIcon(new ImageIcon(routeImageName));

        logger.debug("Creating an images of route" + routeImageName + " has been completed.");
    }

    public void createSegmentImage(Solution s, Route r, RouteSegment rs) throws IOException {
        String routeSegmentImageName = imagePath + "S" + s.getSolutionID() + "_R" + r.getId() + "_RS" + rs.getSrc().getId() + "-" + rs.getDst().getId();
        logger.debug("Creating an images of route segment" + routeSegmentImageName + "...");

        String url = parseURL(rs);
        sendRequestToGoogleMaps(url, routeSegmentImageName);
        rs.setImageIcon(new ImageIcon(routeSegmentImageName));

        logger.debug("Creating an images of route segment " + routeSegmentImageName + " has been completed.");
    }

    public void createCustomerImage(Customer c) throws IOException {
        String customerImageName = imagePath + "C" + c.getId();
        logger.debug("Creating an images of customer" + customerImageName + "...");
        String url = parseURL(c);
        sendRequestToGoogleMaps(url, customerImageName);

        c.setImageIcon(new ImageIcon(customerImageName));
        logger.debug("Creating an images of customer has been completed.");
    }

    private String parseURL(Solution s) {
        StringBuilder paths = new StringBuilder();
        StringBuilder markers = new StringBuilder();
        Storage.getCustomerList().get(0);
        markers.append(defaultDepotMarker);
        markers.append(s.getDepot().getLatitude()).append(",").append(s.getDepot().getLongitude());
        markers.append(defaultCustomerMarker);
        for (Customer c : Storage.getCustomerList()) {
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
            String colour = (new ArrayList<String>(colours.values())).get(colourIndex);
            for (RouteSegment rs : r.getRouteSegments()) {
                paths.append("&path=color:");
                paths.append(colour);
                paths.append("|weight:2|enc:");
                paths.append(rs.getGeometry());
            }
            colourIndex++;
            if (colourIndex == colours.size()) {
                colourIndex = 0;
            }
        }
        return defaultBeginOfURL + markers.toString() + paths.toString() + endOfURL;
    }

    private String parseURL(Route r) {
        StringBuilder path = new StringBuilder();
        StringBuilder markers = new StringBuilder();

        markers.append(biggerDepotMarker);
        markers.append(r.getCustomersInRoute().get(0).getLatitude()).append(",").append(r.getCustomersInRoute().get(0).getLongitude());
        markers.append(biggerCustomerMarker);
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

        for (RouteSegment rs : r.getRouteSegments()) {
            path.append("&path=color:").append(colours.get("blue")).append("|weight:2|enc:").append(rs.getGeometry());
        }

        return beginOfURL + markers.toString() + path.toString() + endOfURL;
    }

    private String parseURL(RouteSegment rs) {
        String srcMarker = fromMarker + rs.getSrc().getLatitude() + "," + rs.getSrc().getLongitude();
        String dstMarker = toMarker + rs.getDst().getLatitude() + "," + rs.getDst().getLongitude();
        String path = "&path=color:" + colours.get("blue") + "|weight:2|enc:" + rs.getGeometry();

        return beginOfURL + srcMarker + dstMarker + path + endOfURL;
    }

    private String parseURL(Customer c) {
        StringBuilder markers = new StringBuilder();
        Storage.getCustomerList().get(0);
        markers.append(choosenMarker).append(c.getLatitude()).append(",").append(c.getLongitude());
        for (Customer customer : Storage.getCustomerList()) {
            if (!customer.equals(c)) {
                if (customer.getId() == 0) {
                    markers.append(defaultDepotMarker).append(customer.getLatitude()).append(",").append(customer.getLongitude());
                    break;
                }
            }
        }

        markers.append(defaultCustomerMarker);
        for (Customer customer : Storage.getCustomerList()) {
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

        return defaultBeginOfURL + markers + endOfURL;
    }

    private void sendRequestToGoogleMaps(String url, String imageName) throws IOException {
        logger.debug("Sending request to Google Maps...");
        try {
            InputStream inputStream = new URL(url).openStream();
            OutputStream outputStream = new FileOutputStream(imageName);

            byte[] b = new byte[2048];
            int length;

            while ((length = inputStream.read(b)) != -1) {
                outputStream.write(b, 0, length);
            }

            inputStream.close();
            outputStream.close();

        } catch (MalformedURLException e) {
            logger.error("Bad URL address!");
            throw e;
        } catch (IOException e) {
            logger.error("Unexpected error while connecting to server!");
            throw e;
        }
        logger.debug("Sending request to Google Maps has been completed.");
    }
}
