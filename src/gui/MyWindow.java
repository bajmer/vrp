package gui;

import algorithm.Problem;
import network.DistanceMatrix;
import project.Customer;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.NumberFormat;

/**
 * Created by mbala on 26.04.17.
 */
public class MyWindow extends JFrame implements ActionListener {

    private JButton bLoad, bGetDistance, bCalculate, bExit;
    private JComboBox boxAlgorithm;
    private String algorithmName;
    private JFormattedTextField algorithmID;
    private JFormattedTextField numberOfVehicles;
    private JFormattedTextField vehicleCapacity;

    public MyWindow() {
        setSize(255, 300);
        setTitle("VRP System");
        setLayout(null);

        bLoad = new JButton("Load data");
        bLoad.setBounds(20, 20, 200, 20);
        add(bLoad);
        bLoad.addActionListener(this);

        bGetDistance = new JButton("Get distance matrix");
        bGetDistance.setBounds(20, 50, 200, 20);
        add(bGetDistance);
        bGetDistance.addActionListener(this);
        bGetDistance.setEnabled(false);

        String[] algorithmsList = {"Clark-Wright", "Second", "Third"};
        boxAlgorithm = new JComboBox(algorithmsList);
        boxAlgorithm.setBounds(20, 80, 200, 20);
        boxAlgorithm.setSelectedIndex(0);
        add(boxAlgorithm);
        boxAlgorithm.addActionListener(this);
        boxAlgorithm.setEnabled(false);

        NumberFormat integerFormat = NumberFormat.getIntegerInstance();
        integerFormat.setGroupingUsed(false);
        NumberFormatter numberFormatter = new NumberFormatter(integerFormat);
        numberFormatter.setValueClass(Integer.class);
        numberFormatter.setAllowsInvalid(false);

        JLabel algorithmIDText = new JLabel("Algorithm ID:");
        algorithmIDText.setBounds(20, 110, 160, 20);
        algorithmIDText.setHorizontalAlignment(SwingConstants.LEFT);
        add(algorithmIDText);
        algorithmID = new JFormattedTextField(numberFormatter);
        algorithmID.setBounds(180, 110, 40, 20);
        add(algorithmID);
        algorithmID.setEnabled(false);

        JLabel numberOfVehiclesText = new JLabel("Number of vehicles:");
        numberOfVehiclesText.setBounds(20, 140, 160, 20);
        numberOfVehiclesText.setHorizontalAlignment(SwingConstants.LEFT);
        add(numberOfVehiclesText);
        numberOfVehicles = new JFormattedTextField(numberFormatter);
        numberOfVehicles.setBounds(180, 140, 40, 20);
        add(numberOfVehicles);
        numberOfVehicles.setEnabled(false);

        JLabel vehicleCapacityText = new JLabel("Vehicle capacity [kg]:");
        vehicleCapacityText.setBounds(20, 170, 160, 20);
        vehicleCapacityText.setHorizontalAlignment(SwingConstants.LEFT);
        add(vehicleCapacityText);
        vehicleCapacity = new JFormattedTextField(numberFormatter);
        vehicleCapacity.setBounds(180, 170, 40, 20);
        add(vehicleCapacity);
        vehicleCapacity.setEnabled(false);

        bCalculate = new JButton("Calculate");
        bCalculate.setBounds(20, 200, 200, 20);
        add(bCalculate);
        bCalculate.addActionListener(this);
        bCalculate.setEnabled(false);

        bExit = new JButton("Exit");
        bExit.setBounds(20, 230, 200, 20);
        add(bExit);
        bExit.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == bLoad) {
            bGetDistance.setEnabled(false);
            boxAlgorithm.setEnabled(false);
            algorithmID.setEnabled(false);
            numberOfVehicles.setEnabled(false);
            vehicleCapacity.setEnabled(false);
            bCalculate.setEnabled(false);
            Customer.setCustomerID(1);
            try {
                FileHandler fileHandler = new FileHandler();
                File customersInputFile = fileHandler.chooseFile(this);
                if (customersInputFile != null) {
                    fileHandler.readFile(customersInputFile);
                    bGetDistance.setEnabled(true);
                }
            } catch (Exception ex) {
                System.out.println("Unexpected error while processing the file.");
                ex.printStackTrace();
            }
        } else if (source == bGetDistance) {
            DistanceMatrix distanceMatrix = new DistanceMatrix();
            distanceMatrix.calculateDistanceMatrix();
            bGetDistance.setEnabled(false);
            boxAlgorithm.setEnabled(true);
        } else if (source == boxAlgorithm) {
            algorithmName = boxAlgorithm.getSelectedItem().toString();
            algorithmID.setEnabled(true);
            numberOfVehicles.setEnabled(true);
            vehicleCapacity.setEnabled(true);
            bCalculate.setEnabled(true);
        } else if (source == bCalculate) {
//            for (Customer customer : CustomerDatabase.getCustomerList()) {
//                customer.getDistances().forEach((k, v) -> System.out.println(customer.getId() + "-" + k + " Distance: " + v + " km"));
//            }
            try {
                int algorithmIDInt = Integer.parseInt(algorithmID.getText());
                int numberOfVehiclesInt = Integer.parseInt(numberOfVehicles.getText());
                int vehicleCapacityInt = Integer.parseInt(vehicleCapacity.getText());
                Problem problem = new Problem(algorithmIDInt, numberOfVehiclesInt, vehicleCapacityInt);
                if (algorithmName.equals("Clark-Wright")) {
                    System.out.println("Running the Clark-Wright algorithm...");
                } else if (algorithmName.equals("Second algorithm")) {

                } else if (algorithmName.equals("Third algorithm")) {

                }
            } catch (Exception ex) {
                System.out.println("Unexpected error while calculating a solution.");
                ex.printStackTrace();
            }
        } else if (source == bExit) {
            dispose();
        }
    }
}
