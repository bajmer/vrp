package gui;

import algorithm.*;
import network.DistanceMatrix;
import network.Geolocation;
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

//    String imageUrl = "https://maps.googleapis.com/maps/api/staticmap?size=640x640&maptype=roadmap&region=pl" +
//            "&markers=color:blue%7Clabel:12%7C52.16149,20.91405|52.15313995,21.0413007247795|52.6305844,20.3774021|" +
//            "52.68144,20.25678|52.7839375,20.1182809|52.4006751,20.312020291701|52.1264653,20.6650247|" +
//            "52.1746017,22.2779659516626|52.29114745,21.1263683577007|53.1129202,20.3743188|52.7062653,21.088928|" +
//            "52.8759448,20.6182627|51.4145421,21.1787179|52.86584,21.09744|53.0860305,21.57039905|" +
//            "&path=color:0xff0000ff|weight:3|52.16149,20.91405|52.15313995,21.0413007247795|52.6305844,20.3774021|52.68144,20.25678|52.7839375,20.1182809|52.4006751,20.312020291701|52.1264653,20.6650247|52.1746017,22.2779659516626|52.29114745,21.1263683577007|53.1129202,20.3743188|52.7062653,21.088928|52.8759448,20.6182627|51.4145421,21.1787179|52.86584,21.09744|53.0860305,21.57039905" +
//            "&key=AIzaSyC-Nh-HTfhZ_KeuVwiF0XSGqeoJopBonRA";
//    https://stackoverflow.com/questions/17598074/google-map-in-java-swing

    private JButton bLoad, bGetDistance, bCalculate, bExit;
    private JComboBox<String> boxAlgorithm;
    private String algorithmName;
    private JFormattedTextField algorithmID;
    private JFormattedTextField numberOfVehicles;
    private JFormattedTextField vehicleCapacity;

    public MyWindow() {
        setSize(255, 300);
        setTitle("VRP System");
        setLayout(null);

        bLoad = new JButton("Load data");
        bLoad.setBounds(20, 20, 200, 20);
        add(bLoad);
        bLoad.addActionListener(this);

        bGetDistance = new JButton("Get distance matrix");
        bGetDistance.setBounds(20, 50, 200, 20);
        add(bGetDistance);
        bGetDistance.addActionListener(this);
        bGetDistance.setEnabled(false);

        boxAlgorithm = new JComboBox<String>();
        boxAlgorithm.addItem("Clark-Wright");
        boxAlgorithm.addItem("Second");
        boxAlgorithm.addItem("Third");
        boxAlgorithm.setBounds(20, 80, 200, 20);
        boxAlgorithm.setSelectedIndex(0);
        add(boxAlgorithm);
        boxAlgorithm.addActionListener(this);
        boxAlgorithm.setEnabled(false);

        NumberFormat integerFormat = NumberFormat.getIntegerInstance();
        integerFormat.setGroupingUsed(false);
        NumberFormatter numberFormatter = new NumberFormatter(integerFormat);
        numberFormatter.setValueClass(Integer.class);
        numberFormatter.setAllowsInvalid(false);

        JLabel algorithmIDText = new JLabel("Algorithm ID:");
        algorithmIDText.setBounds(20, 110, 160, 20);
        algorithmIDText.setHorizontalAlignment(SwingConstants.LEFT);
        add(algorithmIDText);
        algorithmID = new JFormattedTextField(numberFormatter);
        algorithmID.setBounds(180, 110, 40, 20);
        add(algorithmID);
        algorithmID.setEnabled(false);

        JLabel numberOfVehiclesText = new JLabel("Number of vehicles:");
        numberOfVehiclesText.setBounds(20, 140, 160, 20);
        numberOfVehiclesText.setHorizontalAlignment(SwingConstants.LEFT);
        add(numberOfVehiclesText);
        numberOfVehicles = new JFormattedTextField(numberFormatter);
        numberOfVehicles.setBounds(180, 140, 40, 20);
        add(numberOfVehicles);
        numberOfVehicles.setEnabled(false);

        JLabel vehicleCapacityText = new JLabel("Vehicle capacity [kg]:");
        vehicleCapacityText.setBounds(20, 170, 160, 20);
        vehicleCapacityText.setHorizontalAlignment(SwingConstants.LEFT);
        add(vehicleCapacityText);
        vehicleCapacity = new JFormattedTextField(numberFormatter);
        vehicleCapacity.setBounds(180, 170, 40, 20);
        add(vehicleCapacity);
        vehicleCapacity.setEnabled(false);

        bCalculate = new JButton("Calculate");
        bCalculate.setBounds(20, 200, 200, 20);
        add(bCalculate);
        bCalculate.addActionListener(this);
        bCalculate.setEnabled(false);

        bExit = new JButton("Exit");
        bExit.setBounds(20, 230, 200, 20);
        add(bExit);
        bExit.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == bLoad) {
            bGetDistance.setEnabled(false);
            boxAlgorithm.setEnabled(false);
            algorithmID.setEnabled(false);
            numberOfVehicles.setEnabled(false);
            vehicleCapacity.setEnabled(false);
            bCalculate.setEnabled(false);
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
            algorithmID.setEnabled(true);
            numberOfVehicles.setEnabled(true);
            vehicleCapacity.setEnabled(true);
            bCalculate.setEnabled(true);
        } else if (source == bCalculate) {
//            algorithmID.setEnabled(false);
//            numberOfVehicles.setEnabled(false);
//            vehicleCapacity.setEnabled(false);
            try {
                int algorithmIDInt = Integer.parseInt(algorithmID.getText());
                int numberOfVehiclesInt = Integer.parseInt(numberOfVehicles.getText());
                int vehicleCapacityInt = Integer.parseInt(vehicleCapacity.getText());
                Problem problem = new Problem(algorithmIDInt, numberOfVehiclesInt, vehicleCapacityInt);
                if (algorithmName.equals("Clark-Wright")) {
                    Algorithm clark_wright_algorithm = new ClarkWrightAlgorithm(problem);
                    clark_wright_algorithm.runAlgorithm();
                } else if (algorithmName.equals("Second algorithm")) {
                    Algorithm second_algorithm = new Second_Algorithm(problem);
                    second_algorithm.runAlgorithm();
                } else if (algorithmName.equals("Third algorithm")) {
                    Algorithm third_algorithm = new Third_Algorithm(problem);
                    third_algorithm.runAlgorithm();
                }
            } catch (Exception ex) {
                logger.error("Unexpected error while calculating a solution!", ex);
            }
        } else if (source == bExit) {
            dispose();
            logger.info("Application stopped.");
            logger.info("*********************************************************************************************************************************************");
        }
    }
}
