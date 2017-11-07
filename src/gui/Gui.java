package gui;

import algorithm.Algorithm;
import algorithm.acs.ACSAlgorithm;
import algorithm.clarke_wright.ClarkeWrightAlgorithm;
import core.*;
import io.FileReader;
import network.DistanceMatrix;
import network.Geolocator;
import network.MapImage;
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
import java.util.Vector;

/**
 * Klasa odpowiadajaca za tworzenie interfejsu uzytkownika oraz wywyolywanie odpowiednich funkcji przy klikaniu w rozne elementy
 */
public class Gui extends JFrame implements ActionListener, TreeSelectionListener, ListSelectionListener {

    /**
     * Logger klasy
     */
    private static final Logger logger = LogManager.getLogger(Gui.class);

    /**
     * Stala okreslajaca nazwe algorytmu
     */
    private static final String CW_ALG = "Clarke-Wright";

    /**
     * Stala okreslajaca nazwe algorytmu
     */
    private static final String ACS_ALG = "Ant Colony System";

    /**
     * Instancja klasy mapImage
     */
    private final MapImage mapImage;

    /**
     * Panel glowny
     */
    private JPanel mainPanel;

    /**
     * Panel mapy
     */
    private JPanel mapPanel;

    /**
     * Panel lewy
     */
    private JPanel leftPanel;

    /**
     * Panel prawy
     */
    private JPanel rightPanel;

    /**
     * Panel dolny
     */
    private JPanel bottomPanel;

    /**
     * Etykieta mapy
     */
    private JLabel mapLabel;

    /**
     * Przycisk "Load customers"
     */
    private JButton bLoad;

    /**
     * Przycisk "Load test"
     */
    private JButton bTest;

    /**
     * Przycisk "Get distance matrix"
     */
    private JButton bGetDistance;

    /**
     * Przycisk "Find solution"
     */
    private JButton bFindSolution;

    /**
     * Nazwa algorytmu
     */
    private String algorithmName;

    /**
     * Lista algorytmow
     */
    private JComboBox<String> boxAlgorithms;

    /**
     * Obszar wyswietlania logow
     */
    private JTextArea appLog;

    /**
     * Suwak przewijajacy tabele klientow
     */
    private JScrollPane jspCustomers;

    /**
     * Suwak przewijajacy tabele szczegolow trasy
     */
    private JScrollPane jspRouteDetails;

    /**
     * Suwak przewijajacy okno rozwiazan
     */
    private JScrollPane jspSolutions;

    /**
     * Pole tekstowe maksymalnego dopuszczalnego obciazenia pojazdu
     */
    private JFormattedTextField fWeightLimit;

    /**
     * Pole tekstowe maksymalnej objetosci pojazdu
     */
    private JFormattedTextField fSizeLimit;

    /**
     * Pole tekstowe liczby iteracji algorytmu mrowkowego
     */
    private JFormattedTextField fAcsParam_i;

    /**
     * Pole tekstowe liczby mrowek w algorytmie mrowkowym
     */
    private JFormattedTextField fAcsParam_m;

    /**
     * Pole tekstowe parametru okreslajacego proporcje miedzy eksploatacja najlepszej krawedzi i eksploracja nowej
     */
    private JFormattedTextField fAcsParam_q0;

    /**
     * Pole tekstowe okreslajace wpływ odwrotnosci odleglosci
     */
    private JFormattedTextField fAcsParam_beta;

    /**
     * Pole tekstowe parametru określającego ilość wyparowanego feromonu
     */
    private JFormattedTextField fAcsParam_ro;

    /**
     * Suwak wyboru maksymalnego dopuszczalnego obciazenia pojazdu
     */
    private JSlider sWeightLimit;

    /**
     * Suwak wyboru maksymalnej objetosci pojazdu
     */
    private JSlider sSizeLimit;

    /**
     * Suwak wyboru liczby iteracji algorytmu mrowkowego
     */
    private JSlider sAcsParam_i;

    /**
     * Suwak wyboru liczby mrowek w algorytmie mrowkowym
     */
    private JSlider sAcsParam_m;

    /**
     * Suwak wyboru parametru okreslajacego proporcje miedzy eksploatacja najlepszej krawedzi i eksploracja nowej
     */
    private JSlider sAcsParam_q0;

    /**
     * Suwak wyboru parametru okreslajacego wpływ odwrotnosci odleglosci
     */
    private JSlider sAcsParam_beta;

    /**
     * Suwak wyboru parametru określającego ilość wyparowanego feromonu
     */
    private JSlider sAcsParam_ro;

    /**
     * Tabela klientow
     */
    private JTable tCustomers;

    /**
     * Tabela szczegolow trasy
     */
    private JTable tRouteDetails;

    /**
     * Drzewo rozwiazan
     */
    private JTree treeSolutions;

    /**
     * Wektor nazw kolumn tabeli klientow
     */
    private Vector<String> customersTableColumns;

    /**
     * Wektor nazw kolumn tabeli szczegolow trasy
     */
    private Vector<String> routeDetailsTableColumns;

    /**
     * Flaga oznaczajaca, czy do aplikacji wczytano zestawy testowe
     */
    private Boolean TEST = false;

    /**
     * Tworzy interfejs uzytkownika
     */
    public Gui() {
        bLoad.addActionListener(this);
        bTest.addActionListener(this);
        bGetDistance.addActionListener(this);
        bFindSolution.addActionListener(this);
        boxAlgorithms.addActionListener(this);

        JTextAreaAppender.addTextArea(this.appLog);

        NumberFormat integerFormat = NumberFormat.getIntegerInstance();
        integerFormat.setGroupingUsed(false);
        NumberFormatter intFormatter = new NumberFormatter(integerFormat);
        intFormatter.setValueClass(Integer.class);
        intFormatter.setAllowsInvalid(false);
        fSizeLimit.setFormatterFactory(new DefaultFormatterFactory(intFormatter));
        fAcsParam_i.setFormatterFactory(new DefaultFormatterFactory(intFormatter));
        fAcsParam_m.setFormatterFactory(new DefaultFormatterFactory(intFormatter));
        fAcsParam_beta.setFormatterFactory(new DefaultFormatterFactory(intFormatter));

        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setDecimalSeparator('.');
        DecimalFormat doubleFormat = new DecimalFormat("###.##", dfs);
        doubleFormat.setGroupingUsed(false);
        NumberFormatter doubleFormatter = new NumberFormatter(doubleFormat);
        doubleFormatter.setValueClass(Double.class);
        fWeightLimit.setFormatterFactory(new DefaultFormatterFactory(doubleFormatter));
        fAcsParam_q0.setFormatterFactory(new DefaultFormatterFactory(doubleFormatter));
        fAcsParam_ro.setFormatterFactory(new DefaultFormatterFactory(doubleFormatter));

        fWeightLimit.setText("1.5");
        fSizeLimit.setText("10");
        fAcsParam_i.setText("500");
        fAcsParam_m.setText("20");
        fAcsParam_q0.setText("0.8");
        fAcsParam_beta.setText("3");
        fAcsParam_ro.setText("0.1");

        sWeightLimit.addChangeListener(e -> fWeightLimit.setText(doubleFormat.format((double) sWeightLimit.getValue() / 2)));
        sSizeLimit.addChangeListener(e -> fSizeLimit.setText(integerFormat.format(sSizeLimit.getValue())));
        sAcsParam_i.addChangeListener(e -> fAcsParam_i.setText(integerFormat.format(sAcsParam_i.getValue())));
        sAcsParam_m.addChangeListener(e -> fAcsParam_m.setText(integerFormat.format(sAcsParam_m.getValue())));
        sAcsParam_q0.addChangeListener(e -> fAcsParam_q0.setText(doubleFormat.format((double) sAcsParam_q0.getValue() / 10)));
        sAcsParam_beta.addChangeListener(e -> fAcsParam_beta.setText(integerFormat.format(sAcsParam_beta.getValue())));
        sAcsParam_ro.addChangeListener(e -> fAcsParam_ro.setText(doubleFormat.format((double) sAcsParam_ro.getValue() / 10)));

        Hashtable<Integer, JLabel> labelWeightLimitTable = new Hashtable<>();
        labelWeightLimitTable.put(0, new JLabel("0"));
        labelWeightLimitTable.put(6, new JLabel("3"));
        labelWeightLimitTable.put(12, new JLabel("6"));
        labelWeightLimitTable.put(18, new JLabel("9"));
        labelWeightLimitTable.put(24, new JLabel("12"));
        labelWeightLimitTable.put(30, new JLabel("15"));
        labelWeightLimitTable.put(36, new JLabel("18"));
        labelWeightLimitTable.put(42, new JLabel("21"));
        labelWeightLimitTable.put(48, new JLabel("24"));
        sWeightLimit.setLabelTable(labelWeightLimitTable);

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
        bFindSolution.setEnabled(false);

        this.createCustomerTable();
        this.createRouteDetailsTable();
        this.createSolutionsTree();

        this.add(mainPanel);

        mapImage = new MapImage();
    }

    /**
     * Obsluguje wciskanie przyciskow
     *
     * @param e Zdarzenie klikniecia przycisku
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == bLoad) {
            try {
                Geolocator geolocator = new Geolocator();
                FileReader fileReader = new FileReader(geolocator);
                File customersInputFile = fileReader.chooseFile(this);
                if (customersInputFile != null) {
                    Customer.setCustomerID(0);
                    TEST = false;
                    fWeightLimit.setEditable(false);
                    fSizeLimit.setEditable(false);
                    bGetDistance.setEnabled(false);
                    bFindSolution.setEnabled(false);

                    fileReader.readFile(customersInputFile);

                    this.fillCustomerTable();
                    bGetDistance.setEnabled(true);
                }
            } catch (Exception ex) {
                logger.error("Unexpected error while processing the file!", ex);
            }
        } else if (source == bTest) {
            try {
                FileReader fileReader = new FileReader();
                File customersInputFile = fileReader.chooseFile(this);
                if (customersInputFile != null) {
                    Customer.setCustomerID(0);
                    TEST = true;
                    fWeightLimit.setEditable(true);
                    bGetDistance.setEnabled(false);
                    bFindSolution.setEnabled(false);

                    fileReader.readTestFile(customersInputFile);

                    this.fillCustomerTable();
                    bGetDistance.setEnabled(true);
                }
            } catch (Exception ex) {
                logger.error("Unexpected error while processing the test file!", ex);
            }

        } else if (source == bGetDistance) {
            try {
                DistanceMatrix distanceMatrix = new DistanceMatrix();
                if (TEST) {
                    distanceMatrix.calculateEuc2DDistanceMatrix();
                } else {
                    distanceMatrix.downloadDistanceMatrix();
                }
                bGetDistance.setEnabled(false);
                bFindSolution.setEnabled(true);
            } catch (Exception ex) {
                logger.error("Unexpected error while downloading the distance matrix from server!", ex);
            }
        } else if (source == boxAlgorithms) {
            algorithmName = boxAlgorithms.getSelectedItem().toString();

        } else if (source == bFindSolution) {
            try {
                double weightLimitDouble = Double.parseDouble(fWeightLimit.getText()) * 1000;
                double sizeLimitDouble = Double.parseDouble(fSizeLimit.getText());
                Problem problem = new Problem(weightLimitDouble, sizeLimitDouble, TEST);
                switch (algorithmName) {
                    case CW_ALG:
                        Algorithm clark_wright_algorithm = new ClarkeWrightAlgorithm(problem);
                        clark_wright_algorithm.runAlgorithm();
                        break;
                    case ACS_ALG:
                        int numberOfIterations = Integer.parseInt(fAcsParam_i.getText());
                        int numberOfAnts = Integer.parseInt(fAcsParam_m.getText());
                        double alfa = Double.parseDouble(fAcsParam_q0.getText());
                        int beta = Integer.parseInt(fAcsParam_beta.getText());
                        double gamma = Double.parseDouble(fAcsParam_ro.getText());
                        Algorithm acs_algorithm = new ACSAlgorithm(problem, numberOfIterations, numberOfAnts, alfa, beta, gamma);
                        acs_algorithm.runAlgorithm();
                        break;
                }
                this.addNodeToSolutionsTree();
            } catch (Exception ex) {
                logger.error("Unexpected error while calculating the solution!", ex);
            }
        }
    }

    /**
     * Obsluguje klikanie wezlow drzewa rozwiazan
     *
     * @param e Zdarzenie wyboru wezla drzewa
     */
    @Override
    public void valueChanged(TreeSelectionEvent e) {
        DefaultMutableTreeNode choosedNode = (DefaultMutableTreeNode) treeSolutions.getLastSelectedPathComponent();
        if (choosedNode == null)
            return;

        if (choosedNode.getLevel() == 0) { //ROOT level
            setEmptyTable(tRouteDetails, routeDetailsTableColumns);
            mapLabel.setIcon(null);
        } else if (choosedNode.getLevel() == 1) { //SOLUTION level
            setEmptyTable(tRouteDetails, routeDetailsTableColumns);
            Solution s = (Solution) choosedNode.getUserObject();
            try {
                ImageIcon imageIcon = s.getImageIcon();
                if (imageIcon == null && !s.isTest()) {
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
                if (imageIcon == null && !s.isTest()) {
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
            tRouteDetails.scrollRectToVisible(new Rectangle(tRouteDetails.getCellRect(position, 0, true)));
            RouteSegment rs = (RouteSegment) choosedNode.getUserObject();
            try {
                ImageIcon imageIcon = rs.getImageIcon();
                if (imageIcon == null && !s.isTest()) {
                    mapImage.createSegmentImage(s, r, rs);
                }
                mapLabel.setIcon(rs.getImageIcon());
            } catch (Exception ex) {
                logger.warn("Cannot create an image of the route segment!");
                logger.debug(ex);
            }
        }
    }

    /**
     * Obsluguje wybieranie wierszy w tabeli klientow
     *
     * @param e Zdarzenie wyboru wiersza w tabeli
     */
    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            Object source = e.getSource();
            if (source == tCustomers.getSelectionModel()) {
                if (tCustomers.getSelectedRow() >= 0) {
                    DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeSolutions.getModel().getRoot();
                    TreePath rootPath = new TreePath(root.getPath());
                    for (int i = 0; i < root.getChildCount(); i++) {
                        treeSolutions.collapsePath(new TreePath(((DefaultMutableTreeNode) root.getChildAt(i)).getPath()));
                    }
                    treeSolutions.setSelectionPath(rootPath);

                    String id = (String) tCustomers.getValueAt(tCustomers.getSelectedRow(), 0);
                    int index = Integer.parseInt(id);
                    Customer c = Database.getCustomerList().get(index);
                    try {
                        ImageIcon imageIcon = c.getImageIcon();
                        if (imageIcon == null && !TEST) {
                            mapImage.createCustomerImage(c);
                        }
                        mapLabel.setIcon(c.getImageIcon());
                    } catch (Exception ex) {
                        logger.warn("Cannot create an image of customers!");
                        logger.debug(ex);
                    }
                }
            }
        }
    }

    /**
     * Tworzy tabele klientow
     */
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

    /**
     * Wypelnia tabele klientow danymi
     */
    private void fillCustomerTable() {
        Vector<Vector<String>> data = new Vector<>();
        for (Customer c : Database.getCustomerList()) {
            Vector<String> row = new Vector<>();
            row.add(Integer.toString(c.getId()));
            row.add(c.getStreetAndNumber() + ", " + c.getPostalCode() + " " + c.getCity());
            row.add(Double.toString(c.getLatitude()));
            row.add(Double.toString(c.getLongitude()));
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

        tCustomers.changeSelection(0, 0, false, false);
    }

    /**
     * Tworzy tabele szczegolow trasy
     */
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

    /**
     * Wypelnia tabele szczegolow trasy danymi
     *
     * @param route Trasa, ktorej szczegoly maja zostac wyswietlone
     */
    private void fillRouteDetailsTable(Route route) {
        Vector<Vector<String>> data = new Vector<>();
        for (RouteSegment rs : route.getRouteSegments()) {
            Vector<String> row = new Vector<>();
            if (!TEST) {
                row.add(rs.getSrc().getCity() + ", " + rs.getSrc().getStreetAndNumber());

                int hourDep = rs.getDeparture().getHour();
                String sHourDep;
                sHourDep = hourDep < 10 ? "0" + Long.toString(hourDep) : Long.toString(hourDep);
                int minDep = rs.getDeparture().getMinute();
                String sMinDep;
                sMinDep = minDep < 10 ? "0" + Long.toString(minDep) : Long.toString(minDep);
                row.add(sHourDep + ":" + sMinDep);

                row.add(rs.getDst().getCity() + ", " + rs.getDst().getStreetAndNumber());

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
            } else {
                row.add(Integer.toString(rs.getSrc().getId()));
                row.add("");
                row.add(Integer.toString(rs.getDst().getId()));
                row.add("");
                row.add(Double.toString(rs.getDistance()));
                row.add("");
            }
            data.add(row);
        }

        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.setDataVector(data, routeDetailsTableColumns);
        tRouteDetails.setModel(tableModel);
        tRouteDetails.setFont(new Font(Font.DIALOG, Font.PLAIN, 10));
        tRouteDetails.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        tRouteDetails.getColumnModel().getColumn(0).setPreferredWidth(100);
        tRouteDetails.getColumnModel().getColumn(1).setPreferredWidth(40);
        tRouteDetails.getColumnModel().getColumn(2).setPreferredWidth(100);
        tRouteDetails.getColumnModel().getColumn(3).setPreferredWidth(40);
        tRouteDetails.getColumnModel().getColumn(4).setPreferredWidth(40);
        tRouteDetails.getColumnModel().getColumn(5).setPreferredWidth(70);
    }

    /**
     * Czysci tabele
     *
     * @param table   Tabela do wyczyszczenia
     * @param columns Nazwy kolumn czyszczonej tabeli
     */
    private void setEmptyTable(JTable table, Vector<String> columns) {
        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.setDataVector(null, columns);
        table.setModel(tableModel);
    }

    /**
     * Tworzy drzewo rozwiazan
     */
    private void createSolutionsTree() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Solutions");
        treeSolutions = new JTree(root);
        treeSolutions.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        treeSolutions.addTreeSelectionListener(this);

        jspSolutions.setViewportView(treeSolutions);
    }

    /**
     * Dodaje wezel rozwiazania do drzewa rozwiazan
     */
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
