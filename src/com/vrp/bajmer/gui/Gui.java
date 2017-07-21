package com.vrp.bajmer.gui;

import com.vrp.bajmer.algorithm.Algorithm;
import com.vrp.bajmer.algorithm.ClarkWrightAlgorithm;
import com.vrp.bajmer.algorithm.Second_Algorithm;
import com.vrp.bajmer.algorithm.Third_Algorithm;
import com.vrp.bajmer.core.Customer;
import com.vrp.bajmer.core.Problem;
import com.vrp.bajmer.io.FileReader;
import com.vrp.bajmer.network.DistanceMatrix;
import com.vrp.bajmer.network.Geolocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.NumberFormat;

/**
 * Created by mbala on 21.07.17.
 */
public class Gui extends JFrame implements ActionListener {

    private static final Logger logger = LogManager.getLogger(Gui.class);

    private JPanel mainPanel;
    private JPanel leftPanel;
    private JPanel rightPanel;
    private JPanel bottomPanel;
    private JPanel mapPanel;
    private JButton bLoad;
    private JButton bGetDistance;
    private JButton bFindSolution;
    private JTable tCustomers;
    private JTable tRouteSegments;
    private JTable tRouteDetails;
    private JFormattedTextField fAlgorithmId;
    private JFormattedTextField fNumberOfVehicles;
    private JFormattedTextField fWeightLimit;
    private JFormattedTextField fSizeLimit;
    private JComboBox boxAlgorithms;
    private JComboBox boxSolutions;
    private JTextArea appLog;
    private JFrame algorithmProperties;

    private String algorithmName;

    public Gui() {
        bLoad.addActionListener(this);
        bGetDistance.addActionListener(this);
        bFindSolution.addActionListener(this);
        boxAlgorithms.addActionListener(this);
        boxSolutions.addActionListener(this);

        JTextAreaAppender.addTextArea(this.appLog);

        NumberFormat integerFormat = NumberFormat.getIntegerInstance();
        integerFormat.setGroupingUsed(false);
        NumberFormatter numberFormatter = new NumberFormatter(integerFormat);
        numberFormatter.setValueClass(Integer.class);
        numberFormatter.setAllowsInvalid(false);
        fAlgorithmId.setFormatterFactory(new DefaultFormatterFactory(numberFormatter));
        fNumberOfVehicles.setFormatterFactory(new DefaultFormatterFactory(numberFormatter));
        fWeightLimit.setFormatterFactory(new DefaultFormatterFactory(numberFormatter));
        fSizeLimit.setFormatterFactory(new DefaultFormatterFactory(numberFormatter));

        boxAlgorithms.addItem("Clark-Wright");
        boxAlgorithms.addItem("Second");
        boxAlgorithms.addItem("Third");
        boxAlgorithms.setSelectedIndex(0);

        this.add(mainPanel);
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
        } else if (source == boxAlgorithms) {
            algorithmName = boxAlgorithms.getSelectedItem().toString();
            bFindSolution.setEnabled(true);
        } else if (source == bFindSolution) {
//            fAlgorithmID.setEnabled(false);
//            fNumberOfVehicles.setEnabled(false);
//            fWeightLimit.setEnabled(false);
            try {
                int algorithmIDInt = Integer.parseInt(fAlgorithmId.getText());
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
            } catch (Exception ex) {
                logger.error("Unexpected error while calculating a solution!", ex);
            }
        }
    }
}
