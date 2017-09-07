package com.vrp.bajmer.io;

import com.vrp.bajmer.core.Customer;
import com.vrp.bajmer.core.Database;
import com.vrp.bajmer.gui.Gui;
import com.vrp.bajmer.network.Geolocator;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by mbala on 24.05.17.
 */

public class FileReader {

    private static final Logger logger = LogManager.getLogger(FileReader.class);

    private final String fieldsSeparator = ";";
    private final String addressSeparator = ",";
    private final double defaultPackageWeight = 0.0;
    private final double defaultPackageCapacity = 0.0;
    private final LocalTime defaultMinDeliveryHour = LocalTime.of(8, 0);
    private final LocalTime defaultMaxDeliveryHour = LocalTime.of(18, 0);
    private Geolocator geolocator;

    public FileReader(Geolocator geolocator) {
        this.geolocator = geolocator;
    }

    public File chooseFile(Gui gui) {
        logger.info("Choosing file with customers data...");
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
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
                String[] fields = StringUtils.splitByWholeSeparatorPreserveAllTokens(line, fieldsSeparator);

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
                        logger.warn("Cannot create customer in line " + lineNumber);
                        continue;
                    }
                } catch (Exception e) {
                    logger.error("Unexpected error while address geolocating!");
                    logger.warn("Cannot create customer in line " + lineNumber);
                    continue;
                }

                double weight = defaultPackageWeight;
                if (NumberUtils.isParsable(fields[1])) {
                    weight = Double.parseDouble(fields[1]);
                }

                double capacity = defaultPackageCapacity;
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
                        + ", Adres: " + customer.getFullAddress()
                        + ", Latitude: " + customer.getLatitude()
                        + ", Longitude: " + customer.getLongitude()
                        + ", Masa: " + customer.getPackageWeight()
                        + ", Objetosc: " + customer.getPackageSize()
                        + ", Okno czasowe: " + customer.getMinDeliveryHour().toString() + "-" + customer.getMaxDeliveryHour().toString());
            }
        } catch (IOException e) {
            logger.error("Unexpected error while reading the file!");
            throw e;
        }
        logger.info("Reading file has been completed.");
    }

    private String[] splitFullAddress(String fullAddress) {
        ArrayList<String> fields = new ArrayList<>();
        String[] tmpFields = StringUtils.splitByWholeSeparatorPreserveAllTokens(fullAddress, addressSeparator);

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
}
