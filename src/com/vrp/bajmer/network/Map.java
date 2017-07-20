package com.vrp.bajmer.network;

import com.vrp.bajmer.core.Customer;
import com.vrp.bajmer.core.Route;
import com.vrp.bajmer.core.Solution;
import com.vrp.bajmer.core.Storage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Marcin on 2017-07-12.
 */
public class Map {

    private static final Logger logger = LogManager.getLogger(Map.class);

    private static final String defaultBeginOfURL = "https://maps.googleapis.com/maps/api/staticmap?center=52.23,21.2&zoom=8&size=640x640&maptype=roadmap&language=pl";
    private static final String beginOfURLForSingleCustomer = "https://maps.googleapis.com/maps/api/staticmap?size=640x640&maptype=roadmap&language=pl";
    private static final String endOfURL = "&key=AIzaSyC-Nh-HTfhZ_KeuVwiF0XSGqeoJopBonRA";
    private static final List<String> colours = Arrays.asList(
            "0x000000FF", //black
            "0xFF0000FF", //red
            "0x00FF00FF", //lime
            "0x0000FFFF", //blue
            "0xFFFF00FF", //yellow
            "0x00FFFFFF", //aqua
            "0xFF00FFFF", //magenta
            "0xA52A2AFF", //brown
            "0x8B008BFF", //dark magenta
            "0x808000FF", //olive
            "0xFFA500FF", //orange
            "0x9ACD32FF", //yellow green
            "0x006400FF"); //dark green
    private String imageName;

    public Map() {
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public JLabel createMapForSingleCustomer(int id) {
        imageName = "dupa";
        String url = parseURLForSingleCustomer(beginOfURLForSingleCustomer, endOfURL, id);
        sendRequestToGoogleMaps(url, imageName);

        JLabel map = new JLabel(new ImageIcon((new ImageIcon(imageName)).getImage()));
        map.setBounds(0, 0, 640, 640);
        map.setVisible(true);
        return map;
    }

    public JLabel createSolutionImages() {
        logger.info("Creating an images of solution...");
        Solution newestSolution = Storage.getSolutionsList().get(Storage.getSolutionsList().size() - 1);
        int lastSolutionID = newestSolution.getSolutionID();
        String lastSolutionAlgorithm = newestSolution.getUsedAlgorithm();

        imageName = "img/Solution_" + lastSolutionID + "_" + lastSolutionAlgorithm;
        String url = parseURLForAll(defaultBeginOfURL, endOfURL);
        sendRequestToGoogleMaps(url, imageName);

        for (Route route : newestSolution.getListOfRoutes()) {
            String imageName = "img/Solution_" + lastSolutionID + "_" + lastSolutionAlgorithm + "_route_" + route.getId();
            String urlForSingleRoute = parseURLForSingleRoute(defaultBeginOfURL, endOfURL, route);
            sendRequestToGoogleMaps(urlForSingleRoute, imageName);
        }

        JLabel map = new JLabel(new ImageIcon((new ImageIcon(imageName)).getImage()));
        map.setBounds(250, 20, 640, 640);
        map.setVisible(true);

        logger.info("Creating an images of solution has been completed.");
        return map;
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
            logger.error("Unexpected error while connecting to server!");
        }
        logger.debug("Sending request to Google Maps has been completed.");
    }

    private String parseURLForAll(String beginOfURL, String endOfURL) {
        StringBuilder path = new StringBuilder();
        StringBuilder marker = new StringBuilder();
        Storage.getCustomerList().get(0);
        marker.append("&markers=size:small|color:red|label:0");
        marker.append("|").append(Storage.getCustomerList().get(0).getLatitude()).append(",").append(Storage.getCustomerList().get(0).getLongitude());
        marker.append("&markers=size:small|color:blue|label:1");
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
            String colour = colours.get(colourIndex);
            path.append("&path=color:");
            path.append(colour);
            path.append("|weight:3");
            for (Customer c : route.getCustomersInRoute()) {
                path.append("|").append(c.getLatitude()).append(",").append(c.getLongitude());
            }
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
        marker.append("&markers=size:small|color:red|label:0");
        marker.append("|").append(Storage.getCustomerList().get(0).getLatitude()).append(",").append(Storage.getCustomerList().get(0).getLongitude());
        marker.append("&markers=size:small|color:blue|label:1");
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
        path.append("&path=color:0xFF0000FF|weight:2");
        for (Customer c : route.getCustomersInRoute()) {
            path.append("|").append(c.getLatitude()).append(",").append(c.getLongitude());
        }
        return beginOfURL + marker.toString() + path.toString() + endOfURL;
    }

    private String parseURLForSingleCustomer(String beginOfURL, String endOfURL, int id) {
        String marker = "&markers=size:small|color:red|label:0" +
                "|" + Storage.getCustomerList().get(id).getLatitude() + "," + Storage.getCustomerList().get(id).getLongitude();

        return beginOfURL + marker + endOfURL;
    }

    //    String imageUrl = "https://maps.googleapis.com/maps/api/staticmap?zoom=8&size=640x640&maptype=roadmap&region=pl" +
//            "&markers=color:blue%7Clabel:12%7C52.16149,20.91405|52.15313995,21.0413007247795|52.6305844,20.3774021|" +
//            "52.68144,20.25678|52.7839375,20.1182809|52.4006751,20.312020291701|52.1264653,20.6650247|" +
//            "52.1746017,22.2779659516626|52.29114745,21.1263683577007|53.1129202,20.3743188|52.7062653,21.088928|" +
//            "52.8759448,20.6182627|51.4145421,21.1787179|52.86584,21.09744|53.0860305,21.57039905|" +
//            "&path=color:0xff0000ff|weight:3|52.16149,20.91405|52.15313995,21.0413007247795|52.6305844,20.3774021|52.68144,20.25678|52.7839375,20.1182809|52.4006751,20.312020291701|52.1264653,20.6650247|52.1746017,22.2779659516626|52.29114745,21.1263683577007|53.1129202,20.3743188|52.7062653,21.088928|52.8759448,20.6182627|51.4145421,21.1787179|52.86584,21.09744|53.0860305,21.57039905" +
//            "&key=AIzaSyC-Nh-HTfhZ_KeuVwiF0XSGqeoJopBonRA";
}
