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
    //    private static final String beginOfURLForSingleCustomer = "https://maps.googleapis.com/maps/api/staticmap?zoom=8&size=640x640&maptype=roadmap&language=pl";
    private static final String endOfURL = "&key=AIzaSyC-Nh-HTfhZ_KeuVwiF0XSGqeoJopBonRA";
    private static final Map<String, String> colours = createMap();

    public MapImage() {
    }

    private static Map<String, String> createMap() {
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

    public void createSolutionImages(Solution solution) {
        logger.info("Creating an images of solution...");

        String solutionImageName = "solution_images/S" + solution.getSolutionID() + "_" + solution.getUsedAlgorithm();
        String url = parseURLForAll(defaultBeginOfURL, endOfURL);
        sendRequestToGoogleMaps(url, solutionImageName);
        solution.setImageIcon(new ImageIcon(solutionImageName));

        for (Route route : solution.getListOfRoutes()) {
            String routeImageName = solutionImageName + "_R_" + route.getId();
            String urlForSingleRoute = parseURLForSingleRoute(defaultBeginOfURL, endOfURL, route);
            sendRequestToGoogleMaps(urlForSingleRoute, routeImageName);
            route.setImageIcon(new ImageIcon(routeImageName));
        }
        logger.info("Creating an images of solution has been completed.");
    }

    public void createCustomerImage(Customer customer) {
        logger.debug("Creating an images of customer...");
        String customerImageName = "solution_images/C" + customer.getId();
        String url = parseURLForSingleCustomer(defaultBeginOfURL, endOfURL, customer);
        sendRequestToGoogleMaps(url, customerImageName);

        customer.setImageIcon(new ImageIcon(customerImageName));
        logger.debug("Creating an images of customer has been completed.");
    }

    public void createRouteSegmentImage(RouteSegment routeSegment) {
        logger.debug("Creating an images of route segment...");
        String routeSegmentImageName = "solution_images/RS" + routeSegment.getSrc().getId() + "-" + routeSegment.getDst().getId();
        String url = parseURLForRouteSegment(defaultBeginOfURL, endOfURL, routeSegment);
        sendRequestToGoogleMaps(url, routeSegmentImageName);

        routeSegment.setImageIcon(new ImageIcon(routeSegmentImageName));
        logger.debug("Creating an images of customer has been completed.");
    }

    private void sendRequestToGoogleMaps(String url, String imageName) {
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
        } catch (IOException e) {
            logger.error("Unexpected error while connecting to server!", e);
        }
        logger.debug("Sending request to Google Maps has been completed.");
    }

    private String parseURLForAll(String beginOfURL, String endOfURL) {
        StringBuilder path = new StringBuilder();
        StringBuilder marker = new StringBuilder();
        Storage.getCustomerList().get(0);
        marker.append("&markers=size:mid|color:green|label:D");
        marker.append("|").append(Storage.getCustomerList().get(0).getLatitude()).append(",").append(Storage.getCustomerList().get(0).getLongitude());
        marker.append("&markers=size:mid|color:blue|label:C");
        for (Customer c : Storage.getCustomerList()) {
            if (c.getId() == 0) {
                continue;
            }
            double lat = c.getLatitude();
            double lon = c.getLongitude();
            if (lat != 0 && lon != 0) {
                marker.append("|").append(lat).append(",").append(lon);
            }
        }

        Solution solution = Storage.getSolutionsList().get(Storage.getSolutionsList().size() - 1);
        int colourIndex = 0;
        for (Route route : solution.getListOfRoutes()) {
            String colour = (new ArrayList<String>(colours.values())).get(colourIndex);
            for (RouteSegment rs : route.getRouteSegments()) {
                path.append("&path=color:");
                path.append(colour);
                path.append("|weight:2|enc:");
                path.append(rs.getGeometry());
            }
//            path.append(route.getGeometry());
//            for (Customer c : route.getCustomersInRoute()) {
//                path.append("|").append(c.getLatitude()).append(",").append(c.getLongitude());
//            }
            colourIndex++;
            if (colourIndex == 13) {
                colourIndex = 0;
            }
        }
        return beginOfURL + marker.toString() + path.toString() + endOfURL;
    }

    private String parseURLForSingleRoute(String beginOfURL, String endOfURL, Route route) {
        StringBuilder path = new StringBuilder();
        StringBuilder marker = new StringBuilder();
        Storage.getCustomerList().get(0);
        marker.append("&markers=size:mid|color:green|label:D");
        marker.append("|").append(Storage.getCustomerList().get(0).getLatitude()).append(",").append(Storage.getCustomerList().get(0).getLongitude());
        marker.append("&markers=size:mid|color:blue|label:C");
        for (Customer c : Storage.getCustomerList()) {
            if (c.getId() == 0) {
                continue;
            }
            double lat = c.getLatitude();
            double lon = c.getLongitude();
            if (lat != 0 && lon != 0) {
                marker.append("|").append(lat).append(",").append(lon);
            }
        }
        for (RouteSegment rs : route.getRouteSegments()) {
            path.append("&path=color:" + colours.get("blue") + "|weight:2|enc:"); //+ route.getGeometry());
            path.append(rs.getGeometry());
        }
//        for (Customer c : route.getCustomersInRoute()) {
//            path.append("|").append(c.getLatitude()).append(",").append(c.getLongitude());
//        }
        return beginOfURL + marker.toString() + path.toString() + endOfURL;
    }

    private String parseURLForSingleCustomer(String beginOfURL, String endOfURL, Customer customer) {
        StringBuilder marker = new StringBuilder();
        Storage.getCustomerList().get(0);
        marker.append("&markers=size:mid|color:green|label:D");
        marker.append("|").append(Storage.getCustomerList().get(0).getLatitude()).append(",").append(Storage.getCustomerList().get(0).getLongitude());
        marker.append("&markers=size:mid|color:blue|label:C");
        for (Customer c : Storage.getCustomerList()) {
            if (!c.equals(customer)) {
                if (c.getId() == 0) {
                    continue;
                }
                double lat = c.getLatitude();
                double lon = c.getLongitude();
                if (lat != 0 && lon != 0) {
                    marker.append("|").append(lat).append(",").append(lon);
                }
            }
        }
        String customerMarker = "&markers=size:normal|color:red|label:C" +
                "|" + customer.getLatitude() + "," + customer.getLongitude();

        return beginOfURL + marker + customerMarker + endOfURL;
    }

    private String parseURLForRouteSegment(String beginOfURL, String endOfURL, RouteSegment routeSegment) {
        Customer src = routeSegment.getSrc();
        Customer dst = routeSegment.getDst();
        StringBuilder marker = new StringBuilder();
        Storage.getCustomerList().get(0);
        marker.append("&markers=size:mid|color:green|label:D");
        marker.append("|").append(Storage.getCustomerList().get(0).getLatitude()).append(",").append(Storage.getCustomerList().get(0).getLongitude());
        marker.append("&markers=size:mid|color:blue|label:C");
        for (Customer c : Storage.getCustomerList()) {
            if (!c.equals(src) && !c.equals(dst)) {
                if (c.getId() == 0) {
                    continue;
                }
                double lat = c.getLatitude();
                double lon = c.getLongitude();
                if (lat != 0 && lon != 0) {
                    marker.append("|").append(lat).append(",").append(lon);
                }
            }
        }
        String srcMarker = "&markers=size:normal|color:green|label:C" +
                "|" + src.getLatitude() + "," + src.getLongitude();
        String dstMarker = "&markers=size:normal|color:red|label:C" +
                "|" + dst.getLatitude() + "," + dst.getLongitude();

        String path = "&path=color:" + colours.get("blue") + "|weight:2|enc:" + routeSegment.getGeometry();

        return beginOfURL + marker.toString() + srcMarker + dstMarker + path + endOfURL;
    }

    //    String imageUrl = "https://maps.googleapis.com/maps/api/staticmap?zoom=8&size=640x640&maptype=roadmap&region=pl" +
//            "&markers=color:blue%7Clabel:12%7C52.16149,20.91405|52.15313995,21.0413007247795|52.6305844,20.3774021|" +
//            "52.68144,20.25678|52.7839375,20.1182809|52.4006751,20.312020291701|52.1264653,20.6650247|" +
//            "52.1746017,22.2779659516626|52.29114745,21.1263683577007|53.1129202,20.3743188|52.7062653,21.088928|" +
//            "52.8759448,20.6182627|51.4145421,21.1787179|52.86584,21.09744|53.0860305,21.57039905|" +
//            "&path=color:0xff0000ff|weight:3|52.16149,20.91405|52.15313995,21.0413007247795|52.6305844,20.3774021|52.68144,20.25678|52.7839375,20.1182809|52.4006751,20.312020291701|52.1264653,20.6650247|52.1746017,22.2779659516626|52.29114745,21.1263683577007|53.1129202,20.3743188|52.7062653,21.088928|52.8759448,20.6182627|51.4145421,21.1787179|52.86584,21.09744|53.0860305,21.57039905" +
//            "&key=AIzaSyC-Nh-HTfhZ_KeuVwiF0XSGqeoJopBonRA";
}
