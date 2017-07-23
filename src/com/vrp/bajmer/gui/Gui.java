package com.vrp.bajmer.gui;

import com.vrp.bajmer.algorithm.Algorithm;
import com.vrp.bajmer.algorithm.ClarkWrightAlgorithm;
import com.vrp.bajmer.algorithm.Second_Algorithm;
import com.vrp.bajmer.algorithm.Third_Algorithm;
import com.vrp.bajmer.core.*;
import com.vrp.bajmer.io.FileReader;
import com.vrp.bajmer.network.DistanceMatrix;
import com.vrp.bajmer.network.Geolocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.NumberFormat;
import java.util.Vector;

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
    private JScrollPane jspCustomers;
    private JScrollPane jspRouteSegments;
    private JScrollPane jspRouteDetails;
    private JTextArea fTotalDistanceCost;
    private JTextArea fTotalDurationCost;
    private JFrame algorithmProperties;

    private Vector<String> customersTableColumns;
    private Vector<String> routeSegmentsTableColumns;
    private Vector<String> routeDetailsTableColumns;
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

        this.createCustomerTable();
        this.createRouteSegmentsTable();
        this.createRouteDetailsTable();

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
                    this.fillCustomerTable();
                    this.setEmptyTable(tRouteSegments, routeSegmentsTableColumns);
                    this.setEmptyTable(tRouteDetails, routeDetailsTableColumns);
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
                this.fillCustomerTable();
                this.fillRouteSegmentsTable();
            } catch (Exception ex) {
                logger.error("Unexpected error while addresses geolocating and downloading the distance matrix from server!", ex);
            }
        } else if (source == boxAlgorithms) {
            algorithmName = boxAlgorithms.getSelectedItem().toString();
            bFindSolution.setEnabled(true);
        } else if (source == bFindSolution) {
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
                this.showSolutionDetails();
            } catch (Exception ex) {
                logger.error("Unexpected error while calculating a solution!", ex);
            }
        } else if (source == boxSolutions) {
            fillRouteDetailsTable(boxSolutions.getSelectedIndex());
        }
    }

    private void createCustomerTable() {
        customersTableColumns = new Vector<>();
        customersTableColumns.add("ID");
        customersTableColumns.add("Address");
        customersTableColumns.add("Latitude");
        customersTableColumns.add("Longitude");
        customersTableColumns.add("Pack. weight [kg]");
        customersTableColumns.add("Pack. size [m3]");
        customersTableColumns.add("Min. hour");
        customersTableColumns.add("Max. hour");

        setEmptyTable(tCustomers, customersTableColumns);

        jspCustomers.setViewportView(tCustomers);
    }

    private void fillCustomerTable() {
        Vector<Vector<String>> data = new Vector<>();
        for (Customer c : Storage.getCustomerList()) {
            Vector<String> row = new Vector<>();
            row.add(Integer.toString(c.getId()));
            row.add(c.getAddress());
            if (c.getLatitude() == 0.0) {
                row.add("null");
            } else {
                row.add(Double.toString(c.getLatitude()));
            }
            if (c.getLongitude() == 0.0) {
                row.add("null");
            } else {
                row.add(Double.toString(c.getLongitude()));
            }
            row.add(Double.toString(c.getPackageWeight()));
            row.add(Double.toString(c.getPackageSize()));
            row.add(c.getMinDeliveryHour());
            row.add(c.getMaxDeliveryHour());

            data.add(row);
        }

        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.setDataVector(data, customersTableColumns);
        tCustomers.setModel(tableModel);
        tCustomers.setFont(new Font(Font.DIALOG, Font.PLAIN, 10));
        tCustomers.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tCustomers.getColumnModel().getColumn(0).setPreferredWidth(15);
        tCustomers.getColumnModel().getColumn(1).setPreferredWidth(200);
        tCustomers.getColumnModel().getColumn(2).setPreferredWidth(50);
        tCustomers.getColumnModel().getColumn(3).setPreferredWidth(50);
        tCustomers.getColumnModel().getColumn(4).setPreferredWidth(100);
        tCustomers.getColumnModel().getColumn(5).setPreferredWidth(100);
        tCustomers.getColumnModel().getColumn(6).setPreferredWidth(80);
        tCustomers.getColumnModel().getColumn(7).setPreferredWidth(80);
    }

    private void createRouteSegmentsTable() {
        routeSegmentsTableColumns = new Vector<>();
        routeSegmentsTableColumns.add("From");
        routeSegmentsTableColumns.add("To");
        routeSegmentsTableColumns.add("Distance [km]");
        routeSegmentsTableColumns.add("Duration [min]");

        setEmptyTable(tRouteSegments, routeSegmentsTableColumns);

        jspRouteSegments.setViewportView(tRouteSegments);
    }

    private void fillRouteSegmentsTable() {
        Vector<Vector<String>> data = new Vector<>();
        for (RouteSegment r : Storage.getRouteSegmentsList()) {
            Vector<String> row = new Vector<>();
            row.add(Integer.toString(r.getSrc().getId()));
            row.add(Integer.toString(r.getDst().getId()));
            row.add(Double.toString(r.getDistance()));
            row.add(Double.toString(r.getDuration()));

            data.add(row);
        }

        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.setDataVector(data, routeSegmentsTableColumns);
        tRouteSegments.setModel(tableModel);
        tRouteSegments.setFont(new Font(Font.DIALOG, Font.PLAIN, 10));
        tRouteSegments.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tRouteSegments.getColumnModel().getColumn(0).setPreferredWidth(60);
        tRouteSegments.getColumnModel().getColumn(1).setPreferredWidth(60);
        tRouteSegments.getColumnModel().getColumn(2).setPreferredWidth(100);
        tRouteSegments.getColumnModel().getColumn(3).setPreferredWidth(100);
    }

    private void createRouteDetailsTable() {
        routeDetailsTableColumns = new Vector<>();
        routeDetailsTableColumns.add("Customer ID");
        routeDetailsTableColumns.add("Address");
        routeDetailsTableColumns.add("Arrival time");

        setEmptyTable(tRouteDetails, routeDetailsTableColumns);

        jspRouteDetails.setViewportView(tRouteDetails);
    }

    private void fillRouteDetailsTable(int index) {
        Solution newestSolution = Storage.getSolutionsList().get(Storage.getSolutionsList().size() - 1);
        Vector<Vector<String>> data = new Vector<>();
        for (Customer c : newestSolution.getListOfRoutes().get(index).getCustomersInRoute()) {
            Vector<String> row = new Vector<>();
            row.add(Integer.toString(c.getId()));
            row.add(c.getAddress());
            row.add(c.getArrivalTime());

            data.add(row);
        }

        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.setDataVector(data, routeDetailsTableColumns);
        tRouteDetails.setModel(tableModel);
        tRouteDetails.setFont(new Font(Font.DIALOG, Font.PLAIN, 10));
        tRouteDetails.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tRouteDetails.getColumnModel().getColumn(0).setPreferredWidth(35);
        tRouteDetails.getColumnModel().getColumn(1).setPreferredWidth(200);
        tRouteDetails.getColumnModel().getColumn(2).setPreferredWidth(80);
    }

    private void setEmptyTable(JTable table, Vector<String> columns) {
        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.setDataVector(null, columns);
        table.setModel(tableModel);
    }

    private void showSolutionDetails() {
        Solution newestSolution = Storage.getSolutionsList().get(Storage.getSolutionsList().size() - 1);
        fTotalDistanceCost.setText(Double.toString(newestSolution.getTotalDistanceCost()) + " km");
        fTotalDurationCost.setText(Double.toString(newestSolution.getTotalDurationCost()) + " min");

        for (Route r : newestSolution.getListOfRoutes()) {
            boxSolutions.addItem("Route ID: " + r.getId()
                    + ", " + r.getTotalDistance() + "km"
                    + ", " + r.getTotalDuration() + "min"
                    + ", " + r.getCurrentPackagesWeight() + "kg"
                    + ", " + r.getCurrentPackagesSize() + "m3");
        }
    }
}
