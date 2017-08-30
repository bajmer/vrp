package com.vrp.bajmer.gui;

import com.vrp.bajmer.algorithm.Algorithm;
import com.vrp.bajmer.algorithm.Third_Algorithm;
import com.vrp.bajmer.algorithm.clarke_wright.ClarkeWrightAlgorithm;
import com.vrp.bajmer.algorithm.macs.MACSAlgorithm;
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
import java.util.Objects;
import java.util.Vector;

/**
 * Created by mbala on 21.07.17.
 */
public class Gui extends JFrame implements ActionListener, TreeSelectionListener, ListSelectionListener {

    private static final Logger logger = LogManager.getLogger(Gui.class);

    private JPanel mainPanel;
    private JLabel mapLabel;
    private JButton bLoad;
    private JButton bGetDistance;
    private JButton bFindSolution;
    private JTable tCustomers;
    private JTable tRouteDetails;
    private JFormattedTextField fAlgorithmId;
    private JFormattedTextField fNumberOfVehicles;
    private JFormattedTextField fWeightLimit;
    private JFormattedTextField fSizeLimit;
    private JComboBox boxAlgorithms;
    private JTextArea appLog;
    private JScrollPane jspCustomers;
    private JScrollPane jspRouteDetails;
    private JScrollPane jspSolutions;
    private JFormattedTextField fNumberOfAnts;
    private JFormattedTextField fAlfa;
    private JFormattedTextField fBeta;
    private JFormattedTextField fGamma;
    private JTree treeSolutions;

    private Vector<String> customersTableColumns;
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
        NumberFormatter intFormatter = new NumberFormatter(integerFormat);
        intFormatter.setValueClass(Integer.class);
        intFormatter.setAllowsInvalid(false);
        fAlgorithmId.setFormatterFactory(new DefaultFormatterFactory(intFormatter));
        fNumberOfVehicles.setFormatterFactory(new DefaultFormatterFactory(intFormatter));
        fWeightLimit.setFormatterFactory(new DefaultFormatterFactory(intFormatter));
        fSizeLimit.setFormatterFactory(new DefaultFormatterFactory(intFormatter));
        fNumberOfAnts.setFormatterFactory(new DefaultFormatterFactory(intFormatter));

        NumberFormat doubleFormat = NumberFormat.getNumberInstance();
        integerFormat.setGroupingUsed(false);
        NumberFormatter doubleFormatter = new NumberFormatter(doubleFormat);
        doubleFormatter.setValueClass(Integer.class);
        doubleFormatter.setAllowsInvalid(false);
        fAlfa.setFormatterFactory(new DefaultFormatterFactory(doubleFormatter));
        fBeta.setFormatterFactory(new DefaultFormatterFactory(doubleFormatter));
        fGamma.setFormatterFactory(new DefaultFormatterFactory(doubleFormatter));

        boxAlgorithms.addItem("Clark-Wright");
        boxAlgorithms.addItem("MACS");
        boxAlgorithms.addItem("Third");
        boxAlgorithms.setSelectedIndex(0);

        bGetDistance.setEnabled(false);
        boxAlgorithms.setEnabled(false);
        bFindSolution.setEnabled(false);
        fNumberOfAnts.setEnabled(false);
        fAlfa.setEnabled(false);
        fBeta.setEnabled(false);
        fGamma.setEnabled(false);

        this.createCustomerTable();
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
                Geolocator geolocator = new Geolocator();
                FileReader fileReader = new FileReader(geolocator);
                File customersInputFile = fileReader.chooseFile(this);
                if (customersInputFile != null) {
                    fileReader.readFile(customersInputFile);

                    this.fillCustomerTable();
                    bGetDistance.setEnabled(true);
                }
            } catch (Exception ex) {
                logger.error("Unexpected error while processing the file!", ex);
            }
        } else if (source == bGetDistance) {
            try {
                DistanceMatrix distanceMatrix = new DistanceMatrix();
                distanceMatrix.downloadDistanceMatrix();
                bGetDistance.setEnabled(false);
                boxAlgorithms.setEnabled(true);
            } catch (Exception ex) {
                logger.error("Unexpected error while downloading the distance matrix from server!", ex);
            }
        } else if (source == boxAlgorithms) {
            algorithmName = boxAlgorithms.getSelectedItem().toString();
            bFindSolution.setEnabled(true);
            if (Objects.equals(algorithmName, "MACS")) {
                fNumberOfAnts.setEnabled(true);
                fAlfa.setEnabled(true);
                fBeta.setEnabled(true);
                fGamma.setEnabled(true);
            } else {
                fNumberOfAnts.setEnabled(false);
                fAlfa.setEnabled(false);
                fBeta.setEnabled(false);
                fGamma.setEnabled(false);
            }

        } else if (source == bFindSolution) {
            try {
                int algorithmIDInt = Integer.parseInt(fAlgorithmId.getText());
                int numberOfVehiclesInt = Integer.parseInt(fNumberOfVehicles.getText());
                double weightLimitDouble = Double.parseDouble(fWeightLimit.getText());
                double sizeLimitDouble = Double.parseDouble(fSizeLimit.getText());
                Problem problem = new Problem(algorithmIDInt, numberOfVehiclesInt, weightLimitDouble, sizeLimitDouble);
                switch (algorithmName) {
                    case "Clark-Wright":
                        Algorithm clark_wright_algorithm = new ClarkeWrightAlgorithm(problem);
                        clark_wright_algorithm.runAlgorithm();
                        break;
                    case "MACS":
                        int numberOfAnts = Integer.parseInt(fNumberOfAnts.getText());
                        double alfa = Double.parseDouble(fAlfa.getText());
                        double beta = Double.parseDouble(fBeta.getText());
                        double gamma = Double.parseDouble(fGamma.getText());
                        Algorithm macs_algorithm = new MACSAlgorithm(problem, numberOfAnts, alfa, beta, gamma);
                        macs_algorithm.runAlgorithm();
                        break;
                    case "Third com.vrp.bajmer.algorithm":
                        Algorithm third_algorithm = new Third_Algorithm(problem);
                        third_algorithm.runAlgorithm();
                        break;
                }
                this.addNodeToSolutionsTree();
            } catch (Exception ex) {
                logger.error("Unexpected error while calculating the solution!", ex);
            }
        }
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        DefaultMutableTreeNode choosedNode = (DefaultMutableTreeNode) treeSolutions.getLastSelectedPathComponent();
        if (choosedNode == null)
            return;

        if (choosedNode.getLevel() == 0) {
            //root
            setEmptyTable(tRouteDetails, routeDetailsTableColumns);
        } else if (choosedNode.getLevel() == 1) {
            //solution
            Solution s = (Solution) choosedNode.getUserObject();
            try {
                ImageIcon imageIcon = s.getImageIcon();
                if (imageIcon == null) {
                    mapImage.createSolutionImage(s);
                }
                mapLabel.setIcon(s.getImageIcon());
            } catch (Exception ex) {
                logger.warn("Cannot create an image of the solution!");
                logger.debug(ex);
            }
        } else if (choosedNode.getLevel() == 2) {
            //route
            DefaultMutableTreeNode solutionNode = (DefaultMutableTreeNode) choosedNode.getParent();
            Solution s = (Solution) solutionNode.getUserObject();
            Route r = (Route) choosedNode.getUserObject();
            fillRouteDetailsTable(r);
            try {
                ImageIcon imageIcon = r.getImageIcon();
                if (imageIcon == null) {
                    mapImage.createRouteImage(s, r);
                }
                mapLabel.setIcon(r.getImageIcon());
            } catch (Exception ex) {
                logger.warn("Cannot create an image of the route!");
                logger.debug(ex);
            }
        } else if (choosedNode.getLevel() == 3) {
            //route segment
            DefaultMutableTreeNode routeNode = (DefaultMutableTreeNode) choosedNode.getParent();
            DefaultMutableTreeNode solutionNode = (DefaultMutableTreeNode) routeNode.getParent();
            Solution s = (Solution) solutionNode.getUserObject();
            Route r = (Route) routeNode.getUserObject();
            fillRouteDetailsTable(r);
            int position = choosedNode.getParent().getIndex(choosedNode);
            tRouteDetails.getSelectionModel().setSelectionInterval(position, position);
            RouteSegment rs = (RouteSegment) choosedNode.getUserObject();
            try {
                ImageIcon imageIcon = rs.getImageIcon();
                if (imageIcon == null) {
                    mapImage.createSegmentImage(s, r, rs);
                }
                mapLabel.setIcon(rs.getImageIcon());
            } catch (Exception ex) {
                logger.warn("Cannot create an image of the route segment!");
                logger.debug(ex);
            }
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        Object source = e.getSource();
        if (source == tCustomers.getSelectionModel()) {
            String id = (String) tCustomers.getValueAt(tCustomers.getSelectedRow(), 0);
            int index = Integer.parseInt(id);
            Customer c = Storage.getCustomerList().get(index);
            try {
                ImageIcon imageIcon = c.getImageIcon();
                if (imageIcon == null) {
                    mapImage.createCustomerImage(c);
                } else {
                    mapLabel.setIcon(imageIcon);
                }
            } catch (Exception ex) {
                logger.warn("Cannot create an image of customers!");
                logger.debug(ex);
            }
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
            row.add(c.getFullAddress());
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
            row.add(c.getMinDeliveryHour().toString());
            row.add(c.getMaxDeliveryHour().toString());

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

    private void createRouteDetailsTable() {
        routeDetailsTableColumns = new Vector<>();
        routeDetailsTableColumns.add("From");
        routeDetailsTableColumns.add("Depar.");
        routeDetailsTableColumns.add("To");
        routeDetailsTableColumns.add("Arriv.");
        routeDetailsTableColumns.add("Dist.[km]");
        routeDetailsTableColumns.add("Dur.[hh:mm]");

        setEmptyTable(tRouteDetails, routeDetailsTableColumns);

        jspRouteDetails.setViewportView(tRouteDetails);
    }

    private void fillRouteDetailsTable(Route route) {
        Vector<Vector<String>> data = new Vector<>();
        for (RouteSegment rs : route.getRouteSegments()) {
            Vector<String> row = new Vector<>();
            row.add(rs.getSrc().getCity());

            int hourDep = rs.getDeparture().getHour();
            String sHourDep;
            sHourDep = hourDep < 10 ? "0" + Long.toString(hourDep) : Long.toString(hourDep);
            int minDep = rs.getDeparture().getMinute();
            String sMinDep;
            sMinDep = minDep < 10 ? "0" + Long.toString(minDep) : Long.toString(minDep);
            row.add(sHourDep + ":" + sMinDep);

            row.add(rs.getDst().getCity());

            int hourArr = rs.getArrival().getHour();
            String sHourArr;
            sHourArr = hourArr < 10 ? "0" + Long.toString(hourArr) : Long.toString(hourArr);
            int minArr = rs.getArrival().getMinute();
            String sMinArr;
            sMinArr = minArr < 10 ? "0" + Long.toString(minArr) : Long.toString(minArr);
            row.add(sHourArr + ":" + sMinArr);

            row.add(Double.toString(rs.getDistance()));

            long minutes = rs.getDuration().toMinutes() % 60;
            String sMinutes;
            sMinutes = minutes < 10 ? "0" + Long.toString(minutes) : Long.toString(minutes);
            String duration = rs.getDuration().toHours() + ":" + sMinutes;
            row.add(duration);

            data.add(row);
        }

        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.setDataVector(data, routeDetailsTableColumns);
        tRouteDetails.setModel(tableModel);
        tRouteDetails.setFont(new Font(Font.DIALOG, Font.PLAIN, 10));
        tRouteDetails.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tRouteDetails.getColumnModel().getColumn(0).setPreferredWidth(80);
        tRouteDetails.getColumnModel().getColumn(1).setPreferredWidth(80);
        tRouteDetails.getColumnModel().getColumn(2).setPreferredWidth(70);
        tRouteDetails.getColumnModel().getColumn(3).setPreferredWidth(70);
        tRouteDetails.getColumnModel().getColumn(4).setPreferredWidth(70);
        tRouteDetails.getColumnModel().getColumn(5).setPreferredWidth(80);
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

    private void addNodeToSolutionsTree() {
        Solution newestSolution = Storage.getSolutionsList().get(Storage.getSolutionsList().size() - 1);

        DefaultMutableTreeNode solutionNode = new DefaultMutableTreeNode(newestSolution);

        for (Route r : newestSolution.getListOfRoutes()) {
            DefaultMutableTreeNode routeNode = new DefaultMutableTreeNode(r);
            for (RouteSegment rs : r.getRouteSegments()) {
                DefaultMutableTreeNode routeSegmentNode = new DefaultMutableTreeNode(rs);
                routeNode.insert(routeSegmentNode, routeNode.getChildCount());
            }
            solutionNode.insert(routeNode, solutionNode.getChildCount());
        }

        DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeSolutions.getModel().getRoot();
        DefaultTreeModel model = (DefaultTreeModel) treeSolutions.getModel();
        model.insertNodeInto(solutionNode, root, root.getChildCount());

        treeSolutions.expandPath(new TreePath(root.getPath()));
        treeSolutions.scrollPathToVisible(new TreePath(solutionNode.getPath()));
    }
}
