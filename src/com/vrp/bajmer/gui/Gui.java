package com.vrp.bajmer.gui;

import com.vrp.bajmer.core.Customer;
import com.vrp.bajmer.io.FileReader;
import com.vrp.bajmer.network.DistanceMatrix;
import com.vrp.bajmer.network.Geolocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Created by mbala on 21.07.17.
 */
public class Gui extends JFrame implements ActionListener {

    private static final Logger logger = LogManager.getLogger(Gui.class);

    private JPanel panel;
    private JTable tCustomers;
    private JTable tRouteSegments;
    private JButton bLoad;
    private JButton bGetDistance;
    private JFormattedTextField fAlgorithmId;
    private JFormattedTextField fNumberOfVehicles;
    private JFormattedTextField fWeightLimit;
    private JFormattedTextField fSizeLimit;
    private JComboBox boxAlgorithms;
    private JButton bFindSolution;
    private JComboBox boxSolutions;
    private JTable tRouteDetails;
    private JTextArea textArea1;

    public Gui() {
        bLoad.addActionListener(this);
        bGetDistance.addActionListener(this);

        add(panel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == bLoad) {
            bGetDistance.setEnabled(false);
            boxAlgorithms.setEnabled(false);
            bFindSolution.setEnabled(false);
            Customer.setCustomerID(0);
            try {
                FileReader fileReader = new FileReader();
                File customersInputFile = fileReader.chooseFile(this);
                if (customersInputFile != null) {
                    fileReader.readFile(customersInputFile);
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
                boxAlgorithms.setEnabled(true);

            } catch (Exception ex) {
                logger.error("Unexpected error while addresses geolocating and downloading the distance matrix from server!", ex);
            }
        } /*else if (source == boxAlgorithm) {
            algorithmName = boxAlgorithm.getSelectedItem().toString();
            fAlgorithmID.setEnabled(true);
            fNumberOfVehicles.setEnabled(true);
            fWeightLimit.setEnabled(true);
            fSizeLimit.setEnabled(true);
            bCalculate.setEnabled(true);
        } else if (source == bCalculate) {
//            fAlgorithmID.setEnabled(false);
//            fNumberOfVehicles.setEnabled(false);
//            fWeightLimit.setEnabled(false);
            try {
                int algorithmIDInt = Integer.parseInt(fAlgorithmID.getText());
                int numberOfVehiclesInt = Integer.parseInt(fNumberOfVehicles.getText());
                double weightLimitDouble = Double.parseDouble(fWeightLimit.getText());
                double sizeLimitDouble = Double.parseDouble(fSizeLimit.getText());
                Problem problem = new Problem(algorithmIDInt, numberOfVehiclesInt, weightLimitDouble, sizeLimitDouble);
                switch (algorithmName) {
                    case "Clark-Wright":
                        Algorithm clark_wright_algorithm = new ClarkWrightAlgorithm(problem);
                        clark_wright_algorithm.runAlgorithm();
                        break;
                    case "Second com.vrp.bajmer.algorithm":
                        Algorithm second_algorithm = new Second_Algorithm(problem);
                        second_algorithm.runAlgorithm();
                        break;
                    case "Third com.vrp.bajmer.algorithm":
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
//            createMapWindow();
        } */
    }
}
