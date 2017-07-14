package gui;

import algorithm.*;
import network.DistanceMatrix;
import network.Geolocation;
import network.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import project.Customer;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.NumberFormat;

/**
 * Created by mbala on 26.04.17.
 */
public class MyWindow extends JFrame implements ActionListener {

    private static final Logger logger = LogManager.getLogger(MyWindow.class);

//    https://stackoverflow.com/questions/17598074/google-map-in-java-swing

    private JButton bLoad, bGetDistance, bCalculate, bShowMap, bExit;
    private JComboBox<String> boxAlgorithm;
    private String algorithmName;
    private JFormattedTextField fAlgorithmID;
    private JFormattedTextField fNumberOfVehicles;
    private JFormattedTextField fVehicleCapacity;
    private JLabel mapImage;
    private String mapWindowName;

    public MyWindow() {
        setSize(240, 300);
        setTitle("VRP System");
        setLayout(null);

        bLoad = new JButton("Load data");
        bLoad.setBounds(20, 20, 200, 20);
        bLoad.addActionListener(this);
        add(bLoad);

        bGetDistance = new JButton("Get distance matrix");
        bGetDistance.setBounds(20, 50, 200, 20);
        bGetDistance.addActionListener(this);
        bGetDistance.setEnabled(false);
        add(bGetDistance);

        boxAlgorithm = new JComboBox<String>();
        boxAlgorithm.addItem("Clark-Wright");
        boxAlgorithm.addItem("Second");
        boxAlgorithm.addItem("Third");
        boxAlgorithm.setBounds(20, 80, 200, 20);
        boxAlgorithm.setSelectedIndex(0);
        boxAlgorithm.addActionListener(this);
        boxAlgorithm.setEnabled(false);
        add(boxAlgorithm);

        NumberFormat integerFormat = NumberFormat.getIntegerInstance();
        integerFormat.setGroupingUsed(false);
        NumberFormatter numberFormatter = new NumberFormatter(integerFormat);
        numberFormatter.setValueClass(Integer.class);
        numberFormatter.setAllowsInvalid(false);

        JLabel algorithmIDText = new JLabel("Algorithm ID:");
        algorithmIDText.setBounds(20, 110, 160, 20);
        algorithmIDText.setHorizontalAlignment(SwingConstants.LEFT);
        add(algorithmIDText);
        fAlgorithmID = new JFormattedTextField(numberFormatter);
        fAlgorithmID.setBounds(180, 110, 40, 20);
        fAlgorithmID.setEnabled(false);
        add(fAlgorithmID);

        JLabel numberOfVehiclesText = new JLabel("Number of vehicles:");
        numberOfVehiclesText.setBounds(20, 140, 160, 20);
        numberOfVehiclesText.setHorizontalAlignment(SwingConstants.LEFT);
        add(numberOfVehiclesText);
        fNumberOfVehicles = new JFormattedTextField(numberFormatter);
        fNumberOfVehicles.setBounds(180, 140, 40, 20);
        fNumberOfVehicles.setEnabled(false);
        add(fNumberOfVehicles);

        JLabel vehicleCapacityText = new JLabel("Vehicle capacity [kg]:");
        vehicleCapacityText.setBounds(20, 170, 160, 20);
        vehicleCapacityText.setHorizontalAlignment(SwingConstants.LEFT);
        add(vehicleCapacityText);
        fVehicleCapacity = new JFormattedTextField(numberFormatter);
        fVehicleCapacity.setBounds(180, 170, 40, 20);
        fVehicleCapacity.setEnabled(false);
        add(fVehicleCapacity);

        bCalculate = new JButton("Calculate");
        bCalculate.setBounds(20, 200, 200, 20);
        bCalculate.addActionListener(this);
        bCalculate.setEnabled(false);
        add(bCalculate);

        bShowMap = new JButton("Show solution on map");
        bShowMap.setBounds(20, 230, 200, 20);
        bShowMap.addActionListener(this);
        bShowMap.setEnabled(false);
        add(bShowMap);

        bExit = new JButton("Exit");
        bExit.setBounds(20, 260, 200, 20);
        bExit.addActionListener(this);
        add(bExit);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == bLoad) {
            bGetDistance.setEnabled(false);
            boxAlgorithm.setEnabled(false);
            fAlgorithmID.setEnabled(false);
            fNumberOfVehicles.setEnabled(false);
            fVehicleCapacity.setEnabled(false);
            bCalculate.setEnabled(false);
            bShowMap.setEnabled(false);

            Customer.setCustomerID(0);
            try {
                FileHandler fileHandler = new FileHandler();
                File customersInputFile = fileHandler.chooseFile(this);
                if (customersInputFile != null) {
                    fileHandler.readFile(customersInputFile);
                    bGetDistance.setEnabled(true);
                }
            } catch (Exception ex) {
                logger.error("Unexpected error while processing the file!", ex);
            }
        } else if (source == bGetDistance) {
            try {
                Geolocation geolocation = new Geolocation();
                geolocation.downloadCustomersCoordinates();
                DistanceMatrix distanceMatrix = new DistanceMatrix();
                distanceMatrix.downloadDistanceMatrix();
                bGetDistance.setEnabled(false);
                boxAlgorithm.setEnabled(true);

            } catch (Exception ex) {
                logger.error("Unexpected error while addresses geolocating and downloading the distance matrix from server!", ex);
            }
        } else if (source == boxAlgorithm) {
            algorithmName = boxAlgorithm.getSelectedItem().toString();
            fAlgorithmID.setEnabled(true);
            fNumberOfVehicles.setEnabled(true);
            fVehicleCapacity.setEnabled(true);
            bCalculate.setEnabled(true);
        } else if (source == bCalculate) {
//            fAlgorithmID.setEnabled(false);
//            fNumberOfVehicles.setEnabled(false);
//            fVehicleCapacity.setEnabled(false);
            try {
                int algorithmIDInt = Integer.parseInt(fAlgorithmID.getText());
                int numberOfVehiclesInt = Integer.parseInt(fNumberOfVehicles.getText());
                int vehicleCapacityInt = Integer.parseInt(fVehicleCapacity.getText());
                Problem problem = new Problem(algorithmIDInt, numberOfVehiclesInt, vehicleCapacityInt);
                switch (algorithmName) {
                    case "Clark-Wright":
                        Algorithm clark_wright_algorithm = new ClarkWrightAlgorithm(problem);
                        clark_wright_algorithm.runAlgorithm();
                        break;
                    case "Second algorithm":
                        Algorithm second_algorithm = new Second_Algorithm(problem);
                        second_algorithm.runAlgorithm();
                        break;
                    case "Third algorithm":
                        Algorithm third_algorithm = new Third_Algorithm(problem);
                        third_algorithm.runAlgorithm();
                        break;
                }
                bShowMap.setEnabled(true);
            } catch (Exception ex) {
                logger.error("Unexpected error while calculating a solution!", ex);
            }

            try {
                Map map = new Map();
                mapImage = map.createSolutionImages();
                mapWindowName = map.getImageName();
            } catch (Exception ex) {
                logger.error("Unexpected error while displaying solution on the screen!", ex);
            }

        } else if (source == bShowMap) {
            JFrame mapWindow = new JFrame(mapWindowName);
            mapWindow.setSize(640, 640);
            mapWindow.setLocation(240, 0);
            mapWindow.setVisible(true);
            mapWindow.add(mapImage);
        } else if (source == bExit) {
            dispose();
            logger.info("Application stopped.");
            logger.info("*********************************************************************************************************************************************");
            System.exit(0);
        }
    }
}