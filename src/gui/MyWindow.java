package gui;

import network.DistanceMatrix;
import project.Customer;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Created by mbala on 26.04.17.
 */
public class MyWindow extends JFrame implements ActionListener {

    private final String[] algorithmsList = {"Clark-Wright", "Second", "Third"};
    private JButton bLoad, bGetDistance, bCalculate, bExit;
    private JComboBox boxAlgorithm;
    private String algorithmName = "Clark-Wright";

    public MyWindow() {
        setSize(400, 400);
        setTitle("VRP System");
        setLayout(null);

        bLoad = new JButton("Load data");
        bLoad.setBounds(50, 50, 300, 30);
        add(bLoad);
        bLoad.addActionListener(this);

        bGetDistance = new JButton("Get distance matrix");
        bGetDistance.setBounds(50, 100, 300, 30);
        add(bGetDistance);
        bGetDistance.addActionListener(this);
        bGetDistance.setEnabled(false);

        boxAlgorithm = new JComboBox(algorithmsList);
        boxAlgorithm.setBounds(50, 150, 300, 30);
        boxAlgorithm.setSelectedIndex(0);
        add(boxAlgorithm);
        boxAlgorithm.addActionListener(this);
        boxAlgorithm.setEnabled(false);

        bCalculate = new JButton("Calculate");
        bCalculate.setBounds(50, 200, 300, 30);
        add(bCalculate);
        bCalculate.addActionListener(this);
        bCalculate.setEnabled(false);

        bExit = new JButton("Exit");
        bExit.setBounds(50, 250, 300, 30);
        add(bExit);
        bExit.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == bLoad) {
            bGetDistance.setEnabled(false);
            boxAlgorithm.setEnabled(false);
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
            bCalculate.setEnabled(true);
            System.out.println("Algorithm name: " + algorithmName);
        } else if (source == bCalculate) {
//            for (Customer customer : CustomerDatabase.getCustomerList()) {
//                customer.getDistances().forEach((k, v) -> System.out.println(customer.getId() + "-" + k + " Distance: " + v + " km"));
//            }
//            Problem problem = new Problem();
            if (algorithmName.equals("Clark-Wright")) {
                System.out.println("Odpalam algorytm " + algorithmName);
            } else if (algorithmName.equals("Second")) {
                System.out.println("Odpalam algorytm " + algorithmName);
            } else if (algorithmName.equals("Third")) {
                System.out.println("Odpalam algorytm " + algorithmName);
            }
        } else if (source == bExit) {
            dispose();
        }
    }
}
