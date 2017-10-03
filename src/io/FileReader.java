package io;

import core.Customer;
import core.Database;
import core.RouteSegment;
import gui.Gui;
import network.Geolocator;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class FileReader {

    private static final Logger logger = LogManager.getLogger(FileReader.class);

    private static final String FIELDS_SEPARATOR = ";";
    private static final String ADDRESS_SEPARATOR = ",";
    private static final double DEFAULT_PACKAGE_WEIGHT = 0.0;
    private static final double DEFAULT_PACKAGE_CAPACITY = 0.0;
    private final LocalTime defaultMinDeliveryHour = LocalTime.of(8, 0);
    private final LocalTime defaultMaxDeliveryHour = LocalTime.of(18, 0);
    private Geolocator geolocator;

    public FileReader() {

    }

    public FileReader(Geolocator geolocator) {
        this.geolocator = geolocator;
    }

    public File chooseFile(Gui gui) {
        logger.info("Choosing file with customers data...");
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir") + "/input"));
        int result = fileChooser.showOpenDialog(gui);
        File selectedFile = null;
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            logger.info("Selected file: " + selectedFile.getAbsolutePath());
        } else {
            logger.warn("File is not selected.");
        }
        return selectedFile;
    }

    public void readFile(File file) throws IOException {
        logger.info("Reading file...");
        try (BufferedReader br = new BufferedReader(new java.io.FileReader(file))) {
            Database.getCustomerList().clear();
            Database.getRouteSegmentsList().clear();
            String line;
            int lineNumber = 0;
            while ((line = br.readLine()) != null) {
                lineNumber++;
                String[] fields = StringUtils.splitByWholeSeparatorPreserveAllTokens(line, FIELDS_SEPARATOR);
                if (fields.length == 5) {
                    String address = fields[0];
                    String[] addressFields = splitFullAddress(address);

                    String streetAndNumber = addressFields[0];
                    String postalCode = addressFields[1];
                    String city = addressFields[2];

                    double latitude;
                    double longitude;

                    try {
                        List<Double> coordinates = geolocator.downloadCoordinates(streetAndNumber, postalCode, city, lineNumber);
                        if (coordinates != null) {
                            latitude = coordinates.get(0);
                            longitude = coordinates.get(1);
                        } else {
                            logger.warn("Coordinates for customer in line " + lineNumber + " are null!");
                            continue;
                        }
                    } catch (Exception e) {
                        logger.error("Unexpected error while address geolocating!");
                        logger.warn("Cannot create customer in line " + lineNumber);
                        continue;
                    }

                    double weight = DEFAULT_PACKAGE_WEIGHT;
                    if (NumberUtils.isParsable(fields[1])) {
                        weight = Double.parseDouble(fields[1]);
                    }

                    double capacity = DEFAULT_PACKAGE_CAPACITY;
                    if (NumberUtils.isParsable(fields[2])) {
                        capacity = Double.parseDouble(fields[2]);
                    }

                    String minDeliveryHour = fields[3];
                    String maxDeliveryHour = fields[4];
                    LocalTime begin;
                    LocalTime end;
                    try {
                        begin = LocalTime.parse(minDeliveryHour);
                        end = LocalTime.parse(maxDeliveryHour);
                    } catch (DateTimeParseException e) {
                        begin = defaultMinDeliveryHour;
                        end = defaultMaxDeliveryHour;
                        logger.warn("Cannot parse delivery hours in line " + lineNumber + "! Delivery hours set for 08:00-18:00.");
                    }

                    if (begin.isBefore(defaultMinDeliveryHour)) {
                        begin = defaultMinDeliveryHour;
                        logger.warn("Min delivery hour is before 08:00 in line " + lineNumber + "! Min delivery hour set for 08:00.");
                    }
                    if (end.isAfter(defaultMaxDeliveryHour)) {
                        end = defaultMaxDeliveryHour;
                        logger.warn("Max delivery hour is after 18:00 in line " + lineNumber + "! Max delivery hour set for 18:00.");
                    }

                    Customer customer = new Customer(address, addressFields[0], addressFields[1], addressFields[2], latitude, longitude, weight, capacity, begin, end);
                    Database.getCustomerList().add(customer);
                    logger.debug("ID: " + customer.getId()
                            + ", Address: " + customer.getFullAddress()
                            + ", Latitude: " + customer.getLatitude()
                            + ", Longitude: " + customer.getLongitude()
                            + ", Package weight: " + customer.getPackageWeight()
                            + ", Package size: " + customer.getPackageSize()
                            + ", Time window: " + customer.getMinDeliveryHour().toString() + "-" + customer.getMaxDeliveryHour().toString());
                } else {
                    logger.warn("Line " + lineNumber + " has incorrect number of fields!");
                }
            }
        } catch (IOException e) {
            logger.error("Unexpected error while reading the file!");
            throw e;
        }
        logger.info("Reading file has been completed.");
    }

    private String[] splitFullAddress(String fullAddress) {
        ArrayList<String> fields = new ArrayList<>();
        String[] tmpFields = StringUtils.splitByWholeSeparatorPreserveAllTokens(fullAddress, ADDRESS_SEPARATOR);

        String streetAndNumber = tmpFields[0].replace("ul.", "");
        fields.add(streetAndNumber);

        String postalCodeAndCity = tmpFields[1];
        Pattern patternCode = Pattern.compile("[0-9]{2}-[0-9]{3}");
        Matcher matcher = patternCode.matcher(postalCodeAndCity);
        if (matcher.find()) {
            String postalCode = matcher.group();
            fields.add(postalCode);
        }

        String city = postalCodeAndCity.substring(8);
        fields.add(city);

        return fields.toArray(new String[fields.size()]);
    }

    public void readTestFile(File file) throws IOException {
        logger.info("Reading test file...");
        try (BufferedReader br = new BufferedReader(new java.io.FileReader(file))) {
            Database.getCustomerList().clear();
            Database.getRouteSegmentsList().clear();
            String line;
            while ((line = br.readLine()) != null) {
                String[] fields = StringUtils.splitByWholeSeparatorPreserveAllTokens(line, FIELDS_SEPARATOR);

                double x = Double.parseDouble(fields[0]);
                double y = Double.parseDouble(fields[1]);
                double demand = Double.parseDouble(fields[2]);

                Customer customer = new Customer(x, y, demand);
                Database.getCustomerList().add(customer);
            }
        } catch (IOException e) {
            logger.error("Unexpected error while reading the file!");
            throw e;
        }
        logger.info("Reading file has been completed.");

        logger.info("Calculating distance matrix for test file...");
        try {
            for (int i = 0; i < Database.getCustomerList().size(); i++) {
                for (int j = i; j < Database.getCustomerList().size(); j++) {
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
                        dst.getDistances().put(src.getId(), distance);
                        dst.getDurations().put(src.getId(), Duration.ZERO);
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
