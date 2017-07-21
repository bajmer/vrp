//package com.vrp.bajmer.gui;
//
//import com.vrp.bajmer.algorithm.Algorithm;
//import com.vrp.bajmer.algorithm.ClarkWrightAlgorithm;
//import com.vrp.bajmer.algorithm.Second_Algorithm;
//import com.vrp.bajmer.algorithm.Third_Algorithm;
//import com.vrp.bajmer.core.Customer;
//import com.vrp.bajmer.core.Problem;
//import com.vrp.bajmer.core.RouteSegment;
//import com.vrp.bajmer.core.Storage;
//import com.vrp.bajmer.io.FileReader;
//import com.vrp.bajmer.network.DistanceMatrix;
//import com.vrp.bajmer.network.Geolocation;
//import com.vrp.bajmer.network.Map;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//
//import javax.swing.*;
//import javax.swing.event.ListSelectionEvent;
//import javax.swing.event.ListSelectionListener;
//import javax.swing.table.DefaultTableModel;
//import javax.swing.text.NumberFormatter;
//import java.awt.*;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.io.File;
//import java.text.NumberFormat;
//import java.util.Vector;
//
///**
// * Created by mbala on 26.04.17.
// */
//public class Window extends JFrame implements ActionListener {
//
//    private static final Logger logger = LogManager.getLogger(Window.class);
//
//    private JButton bLoad, bGetDistance, bCalculate, bShowMap, bExit;
//    private JComboBox<String> boxAlgorithm;
//    private String algorithmName;
//    private JLabel algorithmIDText;
//    private JLabel numberOfVehiclesText;
//    private JLabel weightLimitText;
//    private JLabel sizeLimitText;
//    private JFormattedTextField fAlgorithmID;
//    private JFormattedTextField fNumberOfVehicles;
//    private JFormattedTextField fWeightLimit;
//    private JFormattedTextField fSizeLimit;
//    private JTable tCustomersData;
//    private JTable tRouteSegments;
//    private JFrame mapWindow;
//    private JLabel mapImage = new JLabel();
//    private String mapWindowName;
//
//    public Window() {
//        this.setSize(340, 400);
//        this.setTitle("VRP System");
//
//        JPanel mainPanel = new JPanel(new GridBagLayout());
//        GridBagConstraints c1 = new GridBagConstraints();
//        c1.anchor = GridBagConstraints.CENTER;
//        c1.insets = new Insets(5, 5, 5, 5);
//        c1.fill = GridBagConstraints.HORIZONTAL;
//
//        //**********button "Load Data"***************
//        c1.gridx = 0;
//        c1.gridy = 0;
//        c1.gridwidth = 2;
//        bLoad = new JButton("Load data");
//        bLoad.addActionListener(this);
//        mainPanel.add(bLoad, c1);
//
////**********button "Get distance matrix"**************
//        c1.gridx = 0;
//        c1.gridy = 1;
//        c1.gridwidth = 2;
//        bGetDistance = new JButton("Get distance matrix");
//        bGetDistance.addActionListener(this);
//        bGetDistance.setEnabled(false);
//        mainPanel.add(bGetDistance, c1);
//
////************General parameters**********************
//        NumberFormat integerFormat = NumberFormat.getIntegerInstance();
//        integerFormat.setGroupingUsed(false);
//        NumberFormatter numberFormatter = new NumberFormatter(integerFormat);
//        numberFormatter.setValueClass(Integer.class);
//        numberFormatter.setAllowsInvalid(false);
//
//        Font propertiesFont = new Font(Font.DIALOG, Font.PLAIN, 12);
//
//        c1.insets = new Insets(1, 5, 1, 5);
//        c1.gridx = 0;
//        c1.gridy = 2;
//        c1.gridwidth = 1;
//        algorithmIDText = new JLabel("Algorithm ID:");
//        algorithmIDText.setFont(propertiesFont);
//        mainPanel.add(algorithmIDText, c1);
//
//        c1.gridx = 1;
//        c1.gridy = 2;
//        c1.gridwidth = 1;
//        fAlgorithmID = new JFormattedTextField(numberFormatter);
//        mainPanel.add(fAlgorithmID, c1);
//
//        c1.gridx = 0;
//        c1.gridy = 3;
//        c1.gridwidth = 1;
//        numberOfVehiclesText = new JLabel("Number of vehicles:");
//        numberOfVehiclesText.setFont(propertiesFont);
//        mainPanel.add(numberOfVehiclesText, c1);
//
//        c1.gridx = 1;
//        c1.gridy = 3;
//        c1.gridwidth = 1;
//        fNumberOfVehicles = new JFormattedTextField(numberFormatter);
//        mainPanel.add(fNumberOfVehicles, c1);
//
//        c1.gridx = 0;
//        c1.gridy = 4;
//        c1.gridwidth = 1;
//        weightLimitText = new JLabel("Vehicle capacity [kg]:");
//        weightLimitText.setFont(propertiesFont);
//        mainPanel.add(weightLimitText, c1);
//
//        c1.gridx = 1;
//        c1.gridy = 4;
//        c1.gridwidth = 1;
//        fWeightLimit = new JFormattedTextField(numberFormatter);
//        mainPanel.add(fWeightLimit, c1);
//
//        c1.gridx = 0;
//        c1.gridy = 5;
//        c1.gridwidth = 1;
//        sizeLimitText = new JLabel("Vehicle capacity [m3]:");
//        sizeLimitText.setFont(propertiesFont);
//        mainPanel.add(sizeLimitText, c1);
//
//        c1.gridx = 1;
//        c1.gridy = 5;
//        c1.gridwidth = 1;
//        fSizeLimit = new JFormattedTextField(numberFormatter);
//        mainPanel.add(fSizeLimit, c1);
//
////************Text fields mock****************************
//        if (true) {
//            fAlgorithmID.setText("1");
//            fNumberOfVehicles.setText("1");
//            fWeightLimit.setText("1400"); //dla Mercedes Sprinter 2017, Standard z rozstawem osi 3665 mm, z dachem normalnym
//            fSizeLimit.setText("9"); //dla Mercedes Sprinter 2017, Standard z rozstawem osi 3665 mm, z dachem normalnym
//        }
//
////**********algorithms list*******************************
//        c1.insets = new Insets(5, 5, 5, 5);
//        c1.gridx = 0;
//        c1.gridy = 6;
//        c1.gridwidth = 2;
//        boxAlgorithm = new JComboBox<>();
//        boxAlgorithm.addItem("Clark-Wright");
//        boxAlgorithm.addItem("Second");
//        boxAlgorithm.addItem("Third");
//        boxAlgorithm.setSelectedIndex(0);
//        boxAlgorithm.addActionListener(this);
//        boxAlgorithm.setEnabled(false);
//        mainPanel.add(boxAlgorithm, c1);
//
////************Button "Calculate"*********************************
//        c1.gridx = 0;
//        c1.gridy = 7;
//        c1.gridwidth = 2;
//        bCalculate = new JButton("Calculate");
//        bCalculate.addActionListener(this);
//        bCalculate.setEnabled(false);
//        mainPanel.add(bCalculate, c1);
//
////***********Button "Show solution on map"************************
//        c1.gridx = 0;
//        c1.gridy = 8;
//        c1.gridwidth = 2;
//        bShowMap = new JButton("Show solution on map");
//        bShowMap.addActionListener(this);
//        bShowMap.setEnabled(false);
//        mainPanel.add(bShowMap, c1);
//
////**********Button "Exit"********************************
//        c1.gridx = 0;
//        c1.gridy = 9;
//        c1.gridwidth = 2;
//        bExit = new JButton("Exit");
//        bExit.addActionListener(this);
//        mainPanel.add(bExit, c1);
//
//        createCustomersWindow();
//        createRouteSegmentsWindow();
//        createMapWindow();
//
//        ListSelectionModel model = tCustomersData.getSelectionModel();
//        model.addListSelectionListener(new ListSelectionListener() {
//            @Override
//            public void valueChanged(ListSelectionEvent e) {
//                int row = tCustomersData.getSelectedRow();
//                Map map = new Map();
//                map.createMapForSingleCustomer(mapImage, row);
//                /*mapImage = map.createMapForSingleCustomer(row);
//                mapWindow.remove(mapImage);
//                mapWindow.add(mapImage);
//                mapWindow.repaint();*/
//            }
//        });
//
//        this.add(mainPanel);
//        this.pack();
//    }
//
//    @Override
//    public void actionPerformed(ActionEvent e) {
//        Object source = e.getSource();
//        if (source == bLoad) {
//            bGetDistance.setEnabled(false);
//            boxAlgorithm.setEnabled(false);
//            fAlgorithmID.setEnabled(false);
//            fNumberOfVehicles.setEnabled(false);
//            fWeightLimit.setEnabled(false);
//            fSizeLimit.setEnabled(false);
//            bCalculate.setEnabled(false);
//            bShowMap.setEnabled(false);
//
//            Customer.setCustomerID(0);
//            try {
//                FileReader fileReader = new FileReader();
//                File customersInputFile = fileReader.chooseFile(this);
//                if (customersInputFile != null) {
//                    fileReader.readFile(customersInputFile);
//                    bGetDistance.setEnabled(true);
//                }
//            } catch (Exception ex) {
//                logger.error("Unexpected error while processing the file!", ex);
//            }
//        } else if (source == bGetDistance) {
//            try {
//                Geolocation geolocation = new Geolocation();
//                geolocation.downloadCustomersCoordinates();
//                DistanceMatrix distanceMatrix = new DistanceMatrix();
//                distanceMatrix.downloadDistanceMatrix();
//                updateCustomersWindow();
//                updateRouteSegmentsWindow();
//                bGetDistance.setEnabled(false);
//                boxAlgorithm.setEnabled(true);
//
//            } catch (Exception ex) {
//                logger.error("Unexpected error while addresses geolocating and downloading the distance matrix from server!", ex);
//            }
//        } else if (source == boxAlgorithm) {
//            algorithmName = boxAlgorithm.getSelectedItem().toString();
//            fAlgorithmID.setEnabled(true);
//            fNumberOfVehicles.setEnabled(true);
//            fWeightLimit.setEnabled(true);
//            fSizeLimit.setEnabled(true);
//            bCalculate.setEnabled(true);
//        } else if (source == bCalculate) {
////            fAlgorithmID.setEnabled(false);
////            fNumberOfVehicles.setEnabled(false);
////            fWeightLimit.setEnabled(false);
//            try {
//                int algorithmIDInt = Integer.parseInt(fAlgorithmID.getText());
//                int numberOfVehiclesInt = Integer.parseInt(fNumberOfVehicles.getText());
//                double weightLimitDouble = Double.parseDouble(fWeightLimit.getText());
//                double sizeLimitDouble = Double.parseDouble(fSizeLimit.getText());
//                Problem problem = new Problem(algorithmIDInt, numberOfVehiclesInt, weightLimitDouble, sizeLimitDouble);
//                switch (algorithmName) {
//                    case "Clark-Wright":
//                        Algorithm clark_wright_algorithm = new ClarkWrightAlgorithm(problem);
//                        clark_wright_algorithm.runAlgorithm();
//                        break;
//                    case "Second com.vrp.bajmer.algorithm":
//                        Algorithm second_algorithm = new Second_Algorithm(problem);
//                        second_algorithm.runAlgorithm();
//                        break;
//                    case "Third com.vrp.bajmer.algorithm":
//                        Algorithm third_algorithm = new Third_Algorithm(problem);
//                        third_algorithm.runAlgorithm();
//                        break;
//                }
//                bShowMap.setEnabled(true);
//            } catch (Exception ex) {
//                logger.error("Unexpected error while calculating a solution!", ex);
//            }
//
//            try {
//                Map map = new Map();
//                mapImage = map.createSolutionImages();
//                mapWindowName = map.getImageName();
//            } catch (Exception ex) {
//                logger.error("Unexpected error while displaying solution on the screen!", ex);
//            }
//
//        } else if (source == bShowMap) {
////            createMapWindow();
//        } else if (source == bExit) {
//            dispose();
//            logger.info("Application stopped.");
//            logger.info("*********************************************************************************************************************************************");
//            System.exit(0);
//        }
//    }
//
//    private void createMapWindow() {
//        mapWindow = new JFrame("Map");
//        mapWindow.setSize(640, 640);
//        mapWindow.setLocation(240, 0);
//        mapWindow.setVisible(true);
//        mapWindow.add(mapImage);
//    }
//
//    private void createCustomersWindow() {
//        Vector<String> columns = new Vector<>();
//        columns.add("ID");
//        columns.add("Address");
//        columns.add("Latitude");
//        columns.add("Longitude");
//        columns.add("Pack. weight");
//        columns.add("Pack. size");
//        columns.add("Min. hour");
//        columns.add("Max. hour");
//
//        Vector<Vector<String>> data = new Vector<>();
//
//        for (Customer c : Storage.getCustomerList()) {
//            Vector<String> row = new Vector<>();
//            row.add(Integer.toString(c.getId()));
//            row.add(c.getAddress());
//            row.add(Double.toString(c.getLatitude()));
//            row.add(Double.toString(c.getLongitude()));
//            row.add(Double.toString(c.getPackageWeight()));
//            row.add(Double.toString(c.getPackageSize()));
//            row.add(c.getMinDeliveryHour());
//            row.add(c.getMaxDeliveryHour());
//
//            data.add(row);
//        }
//
//        tCustomersData = new JTable(data, columns);
//        tCustomersData.setFont(new Font(Font.DIALOG, Font.PLAIN, 10));
//        tCustomersData.getColumnModel().getColumn(0).setPreferredWidth(10);
//        tCustomersData.getColumnModel().getColumn(1).setPreferredWidth(250);
//        tCustomersData.getColumnModel().getColumn(2).setPreferredWidth(20);
//        tCustomersData.getColumnModel().getColumn(3).setPreferredWidth(20);
//        tCustomersData.getColumnModel().getColumn(4).setPreferredWidth(20);
//        tCustomersData.getColumnModel().getColumn(5).setPreferredWidth(20);
//        tCustomersData.getColumnModel().getColumn(6).setPreferredWidth(20);
//        tCustomersData.getColumnModel().getColumn(7).setPreferredWidth(20);
//
//        JScrollPane spCustomersData = new JScrollPane(tCustomersData);
//
//        JFrame customersWindow = new JFrame("Customers");
//        customersWindow.setSize(800, 135);
//        customersWindow.setLocation(201, 0);
//        customersWindow.setVisible(true);
//        customersWindow.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
//        customersWindow.add(spCustomersData);
//    }
//
//    private void updateCustomersWindow() {
//        DefaultTableModel tableModel = (DefaultTableModel) tCustomersData.getModel();
//        for (Customer c : Storage.getCustomerList()) {
//            Vector<String> row = new Vector<>();
//            row.add(Integer.toString(c.getId()));
//            row.add(c.getAddress());
//            row.add(Double.toString(c.getLatitude()));
//            row.add(Double.toString(c.getLongitude()));
//            row.add(Double.toString(c.getPackageWeight()));
//            row.add(Double.toString(c.getPackageSize()));
//            row.add(c.getMinDeliveryHour());
//            row.add(c.getMaxDeliveryHour());
//
//            tableModel.addRow(row);
//        }
//    }
//
//    private void createRouteSegmentsWindow() {
//        Vector<String> columns = new Vector<>();
//        columns.add("From");
//        columns.add("To");
//        columns.add("Distance [km]");
//        columns.add("Duration [min]");
//
//        Vector<Vector<String>> data = new Vector<>();
//
//        for (RouteSegment r : Storage.getRouteSegmentsList()) {
//            Vector<String> row = new Vector<>();
//            row.add(r.getSrc().getAddress());
//            row.add(r.getDst().getAddress());
//            row.add(Double.toString(r.getDistance()));
//            row.add(Double.toString(r.getDuration()));
//
//            data.add(row);
//        }
//
//        tRouteSegments = new JTable(data, columns);
//        tRouteSegments.setFont(new Font(Font.DIALOG, Font.PLAIN, 10));
//        tRouteSegments.getColumnModel().getColumn(0).setPreferredWidth(210);
//        tRouteSegments.getColumnModel().getColumn(1).setPreferredWidth(210);
//        tRouteSegments.getColumnModel().getColumn(2).setPreferredWidth(50);
//        tRouteSegments.getColumnModel().getColumn(3).setPreferredWidth(50);
//
//        JScrollPane spCustomersData = new JScrollPane(tRouteSegments);
//
//        JFrame customersWindow = new JFrame("Route segments");
//        customersWindow.setSize(800, 130);
//        customersWindow.setLocation(201, 186);
//        customersWindow.setVisible(true);
//        customersWindow.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
//        customersWindow.add(spCustomersData);
//    }
//
//    private void updateRouteSegmentsWindow() {
//        DefaultTableModel tableModel = (DefaultTableModel) tRouteSegments.getModel();
//        for (RouteSegment r : Storage.getRouteSegmentsList()) {
//            Vector<String> row = new Vector<>();
//            row.add(r.getSrc().getAddress());
//            row.add(r.getDst().getAddress());
//            row.add(Double.toString(r.getDistance()));
//            row.add(Double.toString(r.getDuration()));
//
//            tableModel.addRow(row);
//        }
//    }
//}