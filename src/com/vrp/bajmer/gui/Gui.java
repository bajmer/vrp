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
                    this.createAndShowCustomerTable();
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
                this.createAndShowRouteSegmentsTable();

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
                this.showSolutionDetails();
            } catch (Exception ex) {
                logger.error("Unexpected error while calculating a solution!", ex);
            }
        }
    }

    private void createAndShowCustomerTable() {
        Vector<String> columns = new Vector<>();
        columns.add("ID");
        columns.add("Address");
        columns.add("Latitude");
        columns.add("Longitude");
        columns.add("Pack. weight [kg]");
        columns.add("Pack. size [m3]");
        columns.add("Min. hour");
        columns.add("Max. hour");

        Vector<Vector<String>> data = new Vector<>();
        for (Customer c : Storage.getCustomerList()) {
            Vector<String> row = new Vector<>();
            row.add(Integer.toString(c.getId()));
            row.add(c.getAddress());
            row.add(Double.toString(c.getLatitude()));
            row.add(Double.toString(c.getLongitude()));
            row.add(Double.toString(c.getPackageWeight()));
            row.add(Double.toString(c.getPackageSize()));
            row.add(c.getMinDeliveryHour());
            row.add(c.getMaxDeliveryHour());

            data.add(row);
        }

        tCustomers = new JTable(data, columns);
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

        jspCustomers.getViewport().add(tCustomers);
    }

    private void createAndShowRouteSegmentsTable() {
        Vector<String> columns = new Vector<>();
        columns.add("From");
        columns.add("To");
        columns.add("Distance [km]");
        columns.add("Duration [min]");

        Vector<Vector<String>> data = new Vector<>();
        for (RouteSegment r : Storage.getRouteSegmentsList()) {
            Vector<String> row = new Vector<>();
            row.add(Integer.toString(r.getSrc().getId()));
            row.add(Integer.toString(r.getDst().getId()));
            row.add(Double.toString(r.getDistance()));
            row.add(Double.toString(r.getDuration()));

            data.add(row);
        }

        tRouteSegments = new JTable(data, columns);
        tRouteSegments.setFont(new Font(Font.DIALOG, Font.PLAIN, 10));
        tRouteSegments.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tRouteSegments.getColumnModel().getColumn(0).setPreferredWidth(35);
        tRouteSegments.getColumnModel().getColumn(1).setPreferredWidth(35);
        tRouteSegments.getColumnModel().getColumn(2).setPreferredWidth(100);
        tRouteSegments.getColumnModel().getColumn(3).setPreferredWidth(100);

        jspRouteSegments.getViewport().add(tRouteSegments);
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
