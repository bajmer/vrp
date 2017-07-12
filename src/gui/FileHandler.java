package gui;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import project.Customer;
import project.Database;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by mbala on 24.05.17.
 */

public class FileHandler {

    private static final Logger logger = LogManager.getLogger(FileHandler.class);

    private final String separator = ";";
    private final double defaultPackageWeight = 0.0;
    private final double defaultPackageCapacity = 0.0;
    private final String defaultMinDeliveryHour = "08:00";
    private final String defaultMaxDeliveryHour = "18:00";
    private final String defaultServiceTime = "00:15";

    public FileHandler() {

    }

    public File chooseFile(MyWindow parentWindow) {
        logger.info("Choosing file with customers data...");
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
        int result = fileChooser.showOpenDialog(parentWindow);
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
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            Database.getCustomerList().clear();
            Database.getRouteSegmentsList().clear();
            String line;
            int lineNumber = 0;
            while ((line = br.readLine()) != null) {
                lineNumber++;
                String[] fields = StringUtils.splitByWholeSeparatorPreserveAllTokens(line, separator);

                String address = fields[0];
                double lat;
                double lon;

//                współrzędne do usunięcia, docelowo nie będą pobierane współrzędne z pliku
//                if (NumberUtils.isParsable(fields[1]) && NumberUtils.isParsable(fields[2])) {
//                    lat = Double.parseDouble(fields[1]);
//                    lon = Double.parseDouble(fields[2]);
//                    if (lat < -90 || lat > 90 || lon < -180 || lon > 180) {
//                        logger.warn("Coordinates are out of range in line " + lineNumber);
//                        continue;
//                    }
//                } else {
//                    logger.warn("Cannot parse coordinates in line " + lineNumber);
//                    continue;
//                }

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
                try {
                    DateFormat dateFormat = new SimpleDateFormat("HH:mm");
                    Date begin = dateFormat.parse(minDeliveryHour);
                    Date end = dateFormat.parse(maxDeliveryHour);

                    if (begin.before(dateFormat.parse(defaultMinDeliveryHour))) {
                        minDeliveryHour = defaultMinDeliveryHour;
                    }
                    if (end.after(dateFormat.parse(defaultMaxDeliveryHour))) {
                        maxDeliveryHour = defaultMaxDeliveryHour;
                    }

                } catch (ParseException e) {
                    minDeliveryHour = defaultMinDeliveryHour;
                    maxDeliveryHour = defaultMaxDeliveryHour;
                    logger.warn("Cannot parse delivery hours in line " + lineNumber + "! Delivery hours set for 08:00-18:00.");
                }

                Customer customer = new Customer(address, weight, capacity, minDeliveryHour, maxDeliveryHour);
                Database.getCustomerList().add(customer);
                logger.debug("ID: " + customer.getId()
                        + ", Adres: " + customer.getAddress()
                        + ", Masa: " + customer.getPackageWeight()
                        + ", Objetosc: " + customer.getPackageCapacity()
                        + ", Okno czasowe: " + customer.getMinDeliveryHour() + "-" + customer.getMaxDeliveryHour());
            }
        } catch (IOException e) {
            logger.error("Unexpected error while reading the file!");
            throw e;
        }
        logger.info("Reading file has been completed.");
    }
}
