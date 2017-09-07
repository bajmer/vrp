package com.vrp.bajmer.gui;

import com.vrp.bajmer.algorithm.Algorithm;
import com.vrp.bajmer.algorithm.clarke_wright.ClarkeWrightAlgorithm;
import com.vrp.bajmer.algorithm.macs.ACSAlgorithm;
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
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Hashtable;
import java.util.Objects;
import java.util.Vector;

/**
 * Created by mbala on 21.07.17.
 */
public class Gui extends JFrame implements ActionListener, TreeSelectionListener, ListSelectionListener {

    private static final Logger logger = LogManager.getLogger(Gui.class);
    private static final String CW_ALG = "Clarke-Wright";
    private static final String ACS_ALG = "Ant Colony System";
    private JPanel mainPanel;
    private JPanel mapPanel;
    private JPanel leftPanel;
    private JPanel rightPanel;
    private JPanel bottomPanel;
    private JLabel mapLabel;
    private JButton bLoad;
    private JButton bGetDistance;
    private JButton bFindSolution;
    private String algorithmName;
    private JComboBox<String> boxAlgorithms;
    private JTextArea appLog;
    private JScrollPane jspCustomers;
    private JScrollPane jspRouteDetails;
    private JScrollPane jspSolutions;
    private JFormattedTextField fWeightLimit;
    private JFormattedTextField fSizeLimit;
    private JFormattedTextField fAcsParam_i;
    private JFormattedTextField fAcsParam_m;
    private JFormattedTextField fAcsParam_q0;
    private JFormattedTextField fAcsParam_beta;
    private JFormattedTextField fAcsParam_ro;
    private JSlider sWeightLimit;
    private JSlider sSizeLimit;
    private JSlider sAcsParam_i;
    private JSlider sAcsParam_m;
    private JSlider sAcsParam_q0;
    private JSlider sAcsParam_beta;
    private JSlider sAcsParam_ro;
    private JTable tCustomers;
    private JTable tRouteDetails;
    private JTree treeSolutions;
    private Vector<String> customersTableColumns;
    private Vector<String> routeDetailsTableColumns;
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
        fWeightLimit.setFormatterFactory(new DefaultFormatterFactory(intFormatter));
        fSizeLimit.setFormatterFactory(new DefaultFormatterFactory(intFormatter));
        fAcsParam_i.setFormatterFactory(new DefaultFormatterFactory(intFormatter));
        fAcsParam_m.setFormatterFactory(new DefaultFormatterFactory(intFormatter));

        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setDecimalSeparator('.');
        DecimalFormat doubleFormat = new DecimalFormat("###.##", dfs);
        doubleFormat.setGroupingUsed(false);
        NumberFormatter doubleFormatter = new NumberFormatter(doubleFormat);
        doubleFormatter.setValueClass(Double.class);
        fAcsParam_q0.setFormatterFactory(new DefaultFormatterFactory(doubleFormatter));
        fAcsParam_beta.setFormatterFactory(new DefaultFormatterFactory(doubleFormatter));
        fAcsParam_ro.setFormatterFactory(new DefaultFormatterFactory(doubleFormatter));

        fWeightLimit.setText("1500");
        fSizeLimit.setText("9");
        fAcsParam_i.setText("100");
        fAcsParam_m.setText("20");
        fAcsParam_q0.setText("0.9");
        fAcsParam_beta.setText("3");
        fAcsParam_ro.setText("0.5");

        sWeightLimit.addChangeListener(e -> fWeightLimit.setText(integerFormat.format(sWeightLimit.getValue())));
        sSizeLimit.addChangeListener(e -> fSizeLimit.setText(integerFormat.format(sSizeLimit.getValue())));
        sAcsParam_i.addChangeListener(e -> fAcsParam_i.setText(integerFormat.format(sAcsParam_i.getValue())));
        sAcsParam_m.addChangeListener(e -> fAcsParam_m.setText(integerFormat.format(sAcsParam_m.getValue())));
        sAcsParam_q0.addChangeListener(e -> fAcsParam_q0.setText(doubleFormat.format((double) sAcsParam_q0.getValue() / 10)));
        sAcsParam_beta.addChangeListener(e -> fAcsParam_beta.setText(integerFormat.format(sAcsParam_beta.getValue())));
        sAcsParam_ro.addChangeListener(e -> fAcsParam_ro.setText(doubleFormat.format((double) sAcsParam_ro.getValue() / 10)));

        Hashtable<Integer, JLabel> labelTable = new Hashtable<>();
        labelTable.put(10, new JLabel("1.0"));
        labelTable.put(5, new JLabel("0.5"));
        labelTable.put(0, new JLabel("0.0"));
        sAcsParam_ro.setLabelTable(labelTable);
        sAcsParam_q0.setLabelTable(labelTable);

        boxAlgorithms.addItem(CW_ALG);
        boxAlgorithms.addItem(ACS_ALG);
        boxAlgorithms.setSelectedIndex(0);

        bGetDistance.setEnabled(false);
        boxAlgorithms.setEnabled(false);
        bFindSolution.setEnabled(false);
        fWeightLimit.setEditable(false);
        fSizeLimit.setEditable(false);
        fAcsParam_i.setEditable(false);
        fAcsParam_m.setEditable(false);
        fAcsParam_q0.setEditable(false);
        fAcsParam_beta.setEditable(false);
        fAcsParam_ro.setEditable(false);
        sAcsParam_i.setEnabled(false);
        sAcsParam_m.setEnabled(false);
        sAcsParam_q0.setEnabled(false);
        sAcsParam_beta.setEnabled(false);
        sAcsParam_ro.setEnabled(false);

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
            if (Objects.equals(algorithmName, ACS_ALG)) {
                sAcsParam_i.setEnabled(true);
                sAcsParam_m.setEnabled(true);
                sAcsParam_q0.setEnabled(true);
                sAcsParam_beta.setEnabled(true);
                sAcsParam_ro.setEnabled(true);
            } else {
                sAcsParam_i.setEnabled(false);
                sAcsParam_m.setEnabled(false);
                sAcsParam_q0.setEnabled(false);
                sAcsParam_beta.setEnabled(false);
                sAcsParam_ro.setEnabled(false);
            }

        } else if (source == bFindSolution) {
            try {
                double weightLimitDouble = Double.parseDouble(fWeightLimit.getText());
                double sizeLimitDouble = Double.parseDouble(fSizeLimit.getText());
                Problem problem = new Problem(weightLimitDouble, sizeLimitDouble);
                switch (algorithmName) {
                    case CW_ALG:
                        Algorithm clark_wright_algorithm = new ClarkeWrightAlgorithm(problem);
                        clark_wright_algorithm.runAlgorithm();
                        break;
                    case ACS_ALG:
                        int numberOfIterations = Integer.parseInt(fAcsParam_i.getText());
                        int numberOfAnts = Integer.parseInt(fAcsParam_m.getText());
                        double alfa = Double.parseDouble(fAcsParam_q0.getText());
                        double beta = Double.parseDouble(fAcsParam_beta.getText());
                        double gamma = Double.parseDouble(fAcsParam_ro.getText());
                        Algorithm macs_algorithm = new ACSAlgorithm(problem, numberOfIterations, numberOfAnts, alfa, beta, gamma);
                        macs_algorithm.runAlgorithm();
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

        if (choosedNode.getLevel() == 0) { //ROOT level
            setEmptyTable(tRouteDetails, routeDetailsTableColumns);
        } else if (choosedNode.getLevel() == 1) { //SOLUTION level
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
        } else if (choosedNode.getLevel() == 2) { //ROUTE level
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
        } else if (choosedNode.getLevel() == 3) { //ROUTE SEGMENT level
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
            Customer c = Database.getCustomerList().get(index);
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
        for (Customer c : Database.getCustomerList()) {
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
        Solution newestSolution = Database.getSolutionsList().get(Database.getSolutionsList().size() - 1);

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

        TreePath rootPath = new TreePath(root.getPath());
        TreePath solutionPath = new TreePath(solutionNode.getPath());
        treeSolutions.expandPath(rootPath);
        treeSolutions.scrollPathToVisible(solutionPath);
        treeSolutions.setSelectionPath(solutionPath);
    }
}
