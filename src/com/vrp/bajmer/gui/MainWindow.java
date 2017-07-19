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
import com.vrp.bajmer.network.SolutionImage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.NumberFormat;

/**
 * Created by mbala on 26.04.17.
 */
public class MainWindow extends JFrame implements ActionListener {

    private static final Logger logger = LogManager.getLogger(MainWindow.class);

    /*private static final int xPos = 20;
    private static final int xDiff = 0;
    private static final int yPos = 20;
    private static final int yDiff = 30;
    private static final int xSize = 300;
    private static final int ySize = 20;*/


    private JButton bLoad, bGetDistance, bCalculate, bShowMap, bExit;
    private JComboBox<String> boxAlgorithm;
    private String algorithmName;
    private JLabel algorithmIDText;
    private JLabel numberOfVehiclesText;
    private JLabel weightLimitText;
    private JLabel sizeLimitText;
    private JFormattedTextField fAlgorithmID;
    private JFormattedTextField fNumberOfVehicles;
    private JFormattedTextField fWeightLimit;
    private JFormattedTextField fSizeLimit;
    private JLabel mapImage;
    private String mapWindowName;

    public MainWindow() {
        this.setSize(340, 400);
        this.setTitle("VRP System");

        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c1 = new GridBagConstraints();
        c1.anchor = GridBagConstraints.CENTER;
        c1.insets = new Insets(5, 1, 5, 1);
        c1.fill = GridBagConstraints.HORIZONTAL;

        c1.gridx = 0;
        c1.gridy = 0;
        c1.gridwidth = 2;
        bLoad = new JButton("Load data");
        bLoad.addActionListener(this);
        mainPanel.add(bLoad, c1);

        c1.gridx = 0;
        c1.gridy = 1;
        c1.gridwidth = 2;
        bGetDistance = new JButton("Get distance matrix");
        bGetDistance.addActionListener(this);
        bGetDistance.setEnabled(false);
        mainPanel.add(bGetDistance, c1);

//        JPanel paramPanel = new JPanel();
//        paramPanel.setBorder(BorderFactory.createTitledBorder("General parameters"));
//        paramPanel.setLayout(new GridBagLayout());

        NumberFormat integerFormat = NumberFormat.getIntegerInstance();
        integerFormat.setGroupingUsed(false);
        NumberFormatter numberFormatter = new NumberFormatter(integerFormat);
        numberFormatter.setValueClass(Integer.class);
        numberFormatter.setAllowsInvalid(false);

        c1.insets = new Insets(1, 1, 1, 1);
        c1.gridx = 0;
        c1.gridy = 2;
        c1.gridwidth = 1;
        algorithmIDText = new JLabel("Algorithm ID:");
        mainPanel.add(algorithmIDText, c1);

        c1.gridx = 1;
        c1.gridy = 2;
        c1.gridwidth = 1;
        fAlgorithmID = new JFormattedTextField(numberFormatter);
        mainPanel.add(fAlgorithmID, c1);

        c1.gridx = 0;
        c1.gridy = 3;
        c1.gridwidth = 1;
        numberOfVehiclesText = new JLabel("Number of vehicles:");
        mainPanel.add(numberOfVehiclesText, c1);

        c1.gridx = 1;
        c1.gridy = 3;
        c1.gridwidth = 1;
        fNumberOfVehicles = new JFormattedTextField(numberFormatter);
        mainPanel.add(fNumberOfVehicles, c1);

        c1.gridx = 0;
        c1.gridy = 4;
        c1.gridwidth = 1;
        weightLimitText = new JLabel("Vehicle capacity [kg]:");
        mainPanel.add(weightLimitText, c1);

        c1.gridx = 1;
        c1.gridy = 4;
        c1.gridwidth = 1;
        fWeightLimit = new JFormattedTextField(numberFormatter);
        mainPanel.add(fWeightLimit, c1);

        c1.gridx = 0;
        c1.gridy = 5;
        c1.gridwidth = 1;
        sizeLimitText = new JLabel("Vehicle capacity [m3]:");
        mainPanel.add(sizeLimitText, c1);

        c1.gridx = 1;
        c1.gridy = 5;
        c1.gridwidth = 1;
        fSizeLimit = new JFormattedTextField(numberFormatter);
        mainPanel.add(fSizeLimit, c1);

        c1.insets = new Insets(5, 1, 5, 1);
        c1.gridx = 0;
        c1.gridy = 6;
        c1.gridwidth = 2;
        boxAlgorithm = new JComboBox<>();
        boxAlgorithm.addItem("Clark-Wright");
        boxAlgorithm.addItem("Second");
        boxAlgorithm.addItem("Third");
        boxAlgorithm.setSelectedIndex(0);
        boxAlgorithm.addActionListener(this);
        boxAlgorithm.setEnabled(false);
        mainPanel.add(boxAlgorithm, c1);

        c1.gridx = 0;
        c1.gridy = 7;
        c1.gridwidth = 2;
        bCalculate = new JButton("Calculate");
        bCalculate.addActionListener(this);
        bCalculate.setEnabled(false);
        mainPanel.add(bCalculate, c1);

//***********Button "Show solution on map"************************
        c1.gridx = 0;
        c1.gridy = 8;
        c1.gridwidth = 2;
        bShowMap = new JButton("Show solution on map");
        bShowMap.addActionListener(this);
        bShowMap.setEnabled(false);
        mainPanel.add(bShowMap, c1);

//**********Button "Exit"********************************
        c1.gridx = 0;
        c1.gridy = 9;
        c1.gridwidth = 2;
        bExit = new JButton("Exit");
        bExit.addActionListener(this);
        mainPanel.add(bExit, c1);



        /*c1.gridx = 0;
        c1.gridy = 1;
        newPanel.add(labelPassword, c1);

        c1.gridx = 1;
        newPanel.add(fieldPassword, c1);

        c1.gridx = 0;
        c1.gridy = 2;
        c1.gridwidth = 2;
        c1.anchor = GridBagConstraints.CENTER;
        newPanel.add(buttonLogin, c1);*/

        // set border for the panel
//        newPanel.setBorder(BorderFactory.createTitledBorder(
//                BorderFactory.createEtchedBorder(), "Login Panel"));

        // add the panel to this frame
        add(mainPanel);
        /*
//**********button "Load Data"***************
        bLoad = new JButton("Load data");
//        bLoad.setBounds(xPos, yPos, xSize, ySize);

        add(bLoad);

//**********button "Get distance matrix"**************
        bGetDistance = new JButton("Get distance matrix");
        bGetDistance.setBounds(xPos, yPos + yDiff, xSize, ySize);
        bGetDistance.addActionListener(this);
        bGetDistance.setEnabled(false);
        add(bGetDistance);

//************General parameters******************
        paramPanel = new JPanel();
        paramPanel.setBorder(BorderFactory.createTitledBorder("General parameters"));
        paramPanel.setBounds(xPos, yPos + 2 * yDiff, xSize, 130);
        //paramPanel.setEnabled(true);
        add(paramPanel);

        NumberFormat integerFormat = NumberFormat.getIntegerInstance();
        integerFormat.setGroupingUsed(false);
        NumberFormatter numberFormatter = new NumberFormatter(integerFormat);
        numberFormatter.setValueClass(Integer.class);
        numberFormatter.setAllowsInvalid(false);

        JLabel algorithmIDText = new JLabel("Algorithm ID:");
        algorithmIDText.setBounds(xPos, yPos + 3 * yDiff, 160, 20);
        algorithmIDText.setHorizontalAlignment(SwingConstants.LEFT);
        paramPanel.add(algorithmIDText);
//        add(algorithmIDText);
        fAlgorithmID = new JFormattedTextField(numberFormatter);
        fAlgorithmID.setBounds(180, yPos + 3 * yDiff, 40, 20);
        fAlgorithmID.setEnabled(false);
        paramPanel.add(fAlgorithmID);
//        add(fAlgorithmID);

        JLabel numberOfVehiclesText = new JLabel("Number of vehicles:");
        numberOfVehiclesText.setBounds(xPos, yPos + 4 * yDiff, 160, 20);
        numberOfVehiclesText.setHorizontalAlignment(SwingConstants.LEFT);
        paramPanel.add(numberOfVehiclesText);
//        add(numberOfVehiclesText);
        fNumberOfVehicles = new JFormattedTextField(numberFormatter);
        fNumberOfVehicles.setBounds(180, yPos + 4 * yDiff, 40, 20);
        fNumberOfVehicles.setEnabled(false);
        paramPanel.add(fNumberOfVehicles);
//        add(fNumberOfVehicles);

        JLabel weightLimitText = new JLabel("Vehicle capacity [kg]:");
        weightLimitText.setBounds(xPos, yPos + 5 * yDiff, 160, 20);
        weightLimitText.setHorizontalAlignment(SwingConstants.LEFT);
        paramPanel.add(weightLimitText);
//        add(weightLimitText);
        fWeightLimit = new JFormattedTextField(numberFormatter);
        fWeightLimit.setBounds(180, yPos + 5 * yDiff, 40, 20);
        fWeightLimit.setEnabled(false);
        paramPanel.add(fWeightLimit);
//        add(fWeightLimit);

        JLabel sizeLimitText = new JLabel("Vehicle capacity [m3]:");
        sizeLimitText.setBounds(xPos, yPos + 6 * yDiff, 160, 20);
        sizeLimitText.setHorizontalAlignment(SwingConstants.LEFT);
        paramPanel.add(sizeLimitText);
//        add(sizeLimitText);
        fSizeLimit = new JFormattedTextField(numberFormatter);
        fSizeLimit.setBounds(180, yPos + 6 * yDiff, 40, 20);
        fSizeLimit.setEnabled(false);
        paramPanel.add(fSizeLimit);
//        add(fSizeLimit);
//        add(paramPanel);

//**********algorithms list********************
        boxAlgorithm = new JComboBox<>();
        boxAlgorithm.addItem("Clark-Wright");
        boxAlgorithm.addItem("Second");
        boxAlgorithm.addItem("Third");
        boxAlgorithm.setBounds(xPos, yPos + 6 * yDiff, xSize, ySize);
        boxAlgorithm.setSelectedIndex(0);
        boxAlgorithm.addActionListener(this);
        boxAlgorithm.setEnabled(false);
        add(boxAlgorithm);

//************Text fields mock****************************
        if (false) {
            fAlgorithmID.setText("1");
            fNumberOfVehicles.setText("1");
            fWeightLimit.setText("1400"); //dla Mercedes Sprinter 2017, Standard z rozstawem osi 3665 mm, z dachem normalnym
            fSizeLimit.setText("9"); //dla Mercedes Sprinter 2017, Standard z rozstawem osi 3665 mm, z dachem normalnym
        }
//************Button "Calculate"*********************************
        bCalculate = new JButton("Calculate");
        bCalculate.setBounds(xPos, yPos + 7 * yDiff, xSize, ySize);
        bCalculate.addActionListener(this);
        bCalculate.setEnabled(false);
        add(bCalculate);

//***********Button "Show solution on map"************************
        bShowMap = new JButton("Show solution on map");
        bShowMap.setBounds(xPos, yPos + 8 * yDiff, xSize, ySize);
        bShowMap.addActionListener(this);
        bShowMap.setEnabled(false);
        add(bShowMap);

//**********Button "Exit"********************************
        bExit = new JButton("Exit");
        bExit.setBounds(xPos, yPos + 9 * yDiff, xSize, ySize);
        bExit.addActionListener(this);
        add(bExit);*/
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == bLoad) {
            bGetDistance.setEnabled(false);
            boxAlgorithm.setEnabled(false);
            fAlgorithmID.setEnabled(false);
            fNumberOfVehicles.setEnabled(false);
            fWeightLimit.setEnabled(false);
            fSizeLimit.setEnabled(false);
            bCalculate.setEnabled(false);
            bShowMap.setEnabled(false);

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
                boxAlgorithm.setEnabled(true);

            } catch (Exception ex) {
                logger.error("Unexpected error while addresses geolocating and downloading the distance matrix from server!", ex);
            }
        } else if (source == boxAlgorithm) {
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
                SolutionImage solutionImage = new SolutionImage();
                mapImage = solutionImage.createSolutionImages();
                mapWindowName = solutionImage.getImageName();
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