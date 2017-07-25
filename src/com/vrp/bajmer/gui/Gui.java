package com.vrp.bajmer.gui;

import com.vrp.bajmer.algorithm.Algorithm;
import com.vrp.bajmer.algorithm.ClarkWrightAlgorithm;
import com.vrp.bajmer.algorithm.Second_Algorithm;
import com.vrp.bajmer.algorithm.Third_Algorithm;
import com.vrp.bajmer.core.*;
import com.vrp.bajmer.io.FileReader;
import com.vrp.bajmer.network.DistanceMatrix;
import com.vrp.bajmer.network.Geolocator;
import com.vrp.bajmer.network.MapImage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.NumberFormat;
import java.util.Vector;

/**
 * Created by mbala on 21.07.17.
 */
public class Gui extends JFrame implements ActionListener, TreeSelectionListener, ListSelectionListener {

    private static final Logger logger = LogManager.getLogger(Gui.class);

    private JPanel mainPanel;
    private JPanel leftPanel;
    private JPanel rightPanel;
    private JPanel bottomPanel;
    private JPanel mapPanel;
    private JLabel mapLabel;
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
    private JTextArea appLog;
    private JScrollPane jspCustomers;
    private JScrollPane jspRouteSegments;
    private JScrollPane jspRouteDetails;
    private JScrollPane jspSolutions;
    private JTextArea fTotalDistanceCost;
    private JTextArea fTotalDurationCost;
    private JTree treeSolutions;
    private JFrame algorithmProperties;

    private Vector<String> customersTableColumns;
    private Vector<String> routeSegmentsTableColumns;
    private Vector<String> routeDetailsTableColumns;
    private String algorithmName;
    private MapImage mapImage;


    public Gui() {
        bLoad.addActionListener(this);
        bGetDistance.addActionListener(this);
        bFindSolution.addActionListener(this);
        boxAlgorithms.addActionListener(this);

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
        this.createSolutionsTree();

        this.add(mainPanel);

        mapImage = new MapImage();
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

                    this.setEmptyTable(tRouteSegments, routeSegmentsTableColumns);
                    this.setEmptyTable(tRouteDetails, routeDetailsTableColumns);
                } else {
                    return;
                }
            } catch (Exception ex) {
                logger.error("Unexpected error while processing the file!", ex);
            }

            try {
                Geolocator geolocator = new Geolocator();
                geolocator.downloadCustomersCoordinates();

                this.fillCustomerTable();
                bGetDistance.setEnabled(true);
            } catch (Exception ex) {
                logger.error("Unexpected error while addresses geolocating!", ex);
            }
        } else if (source == bGetDistance) {
            try {
                DistanceMatrix distanceMatrix = new DistanceMatrix();
                distanceMatrix.downloadDistanceMatrix();
                bGetDistance.setEnabled(false);
                boxAlgorithms.setEnabled(true);
                this.fillCustomerTable();
                this.fillRouteSegmentsTable();
            } catch (Exception ex) {
                logger.error("Unexpected error while downloading the distance matrix from server!", ex);
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
                logger.error("Unexpected error while calculating the solution!", ex);
            }

            try {
                Solution newestSolution = Storage.getSolutionsList().get(Storage.getSolutionsList().size() - 1);
                mapImage.createSolutionImages(newestSolution);
            } catch (Exception ex) {
                logger.error("Unexpected error while drawing the solution!", ex);
            }
        }
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) treeSolutions.getLastSelectedPathComponent();

        if (node == null)
            return;

        if (!node.isRoot()) {
            if (!node.isLeaf()) {
//                this.setEmptyTable(tRouteDetails, routeDetailsTableColumns);
//                mapLabel.setIcon(null);
//                mapLabel.revalidate();
//                wyświetlanie rozwiązania
                Solution s = (Solution) node.getUserObject();
                mapLabel.setIcon(s.getImageIcon());
            } else {
//                wyświetlanie trasy
                Route r = (Route) node.getUserObject();
                mapLabel.setIcon(r.getImageIcon());
//                wypełnienie szczegółów trasy
                this.fillRouteDetailsTable(r);
            }
        } else {
            this.setEmptyTable(tRouteDetails, routeDetailsTableColumns);
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        Object source = e.getSource();
        if (source == tCustomers.getSelectionModel()) {
            String id = (String) tCustomers.getValueAt(tCustomers.getSelectedRow(), 0);
            int index = Integer.parseInt(id);
            Customer c = Storage.getCustomerList().get(index);
            ImageIcon imageIcon = c.getImageIcon();
            if (imageIcon == null) {
                mapImage.createCustomerImage(c);
            } else {
                mapLabel.setIcon(imageIcon);
            }
        } else if (source == tRouteSegments.getSelectionModel()) {
            String sFrom = (String) tRouteSegments.getValueAt(tRouteSegments.getSelectedRow(), 0);
            String sTo = (String) tRouteSegments.getValueAt(tRouteSegments.getSelectedRow(), 1);
            int from = Integer.parseInt(sFrom);
            int to = Integer.parseInt(sTo);
            for (RouteSegment rs : Storage.getRouteSegmentsList()) {
                if (rs.getSrc().getId() == from && rs.getDst().getId() == to) {
                    ImageIcon imageIcon = rs.getImageIcon();
                    if (imageIcon == null) {
                        mapImage.createRouteSegmentImage(rs);
                    } else {
                        mapLabel.setIcon(imageIcon);
                    }
                }
            }
        } else if (source == tRouteDetails.getSelectionModel()) {

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
        tCustomers.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tCustomers.getSelectionModel().addListSelectionListener(this);

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
        tRouteSegments.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tRouteSegments.getSelectionModel().addListSelectionListener(this);

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
        tRouteDetails.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tRouteDetails.getSelectionModel().addListSelectionListener(this);

        jspRouteDetails.setViewportView(tRouteDetails);
    }

    private void fillRouteDetailsTable(Route route) {
        Vector<Vector<String>> data = new Vector<>();
        for (Customer c : route.getCustomersInRoute()) {
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

    private void createSolutionsTree() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Solutions");
        treeSolutions = new JTree(root);
        treeSolutions.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        treeSolutions.addTreeSelectionListener(this);

        jspSolutions.setViewportView(treeSolutions);
    }

    private void showSolutionDetails() {
        Solution newestSolution = Storage.getSolutionsList().get(Storage.getSolutionsList().size() - 1);
//        fTotalDistanceCost.setText(Double.toString(newestSolution.getTotalDistanceCost()) + " km");
//        fTotalDurationCost.setText(Double.toString(newestSolution.getTotalDurationCost()) + " min");

        addNodeToSolutionsTree(newestSolution);
    }

    private void addNodeToSolutionsTree(Solution solution) {
        DefaultMutableTreeNode parent = new DefaultMutableTreeNode(solution);

        for (Route r : solution.getListOfRoutes()) {
            DefaultMutableTreeNode child = new DefaultMutableTreeNode(r);
            parent.insert(child, parent.getChildCount());
        }

        DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeSolutions.getModel().getRoot();
        DefaultTreeModel model = (DefaultTreeModel) treeSolutions.getModel();
        model.insertNodeInto(parent, root, root.getChildCount());

        treeSolutions.expandPath(new TreePath(root.getPath()));
        treeSolutions.scrollPathToVisible(new TreePath(parent.getPath()));
    }


}
