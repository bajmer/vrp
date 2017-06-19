package gui;

import algorithm.Problem;
import network.DistanceMatrix;
import project.Client;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Created by mbala on 26.04.17.
 */
public class MyWindow extends JFrame implements ActionListener {

    private JButton bLoad, bGetDistance, bCalculate, bExit;

    public MyWindow() {
        setSize(400, 300);
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

        bCalculate = new JButton("Calculate");
        bCalculate.setBounds(50, 150, 300, 30);
        add(bCalculate);
        bCalculate.addActionListener(this);
        bCalculate.setEnabled(false);

        bExit = new JButton("Exit");
        bExit.setBounds(50, 200, 300, 30);
        add(bExit);
        bExit.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == bLoad) {
            bGetDistance.setEnabled(false);
            bCalculate.setEnabled(false);
            Client.setClientID(1);
            try {
                FileHandler fileHandler = new FileHandler();
                File clientsInputFile = fileHandler.chooseFile(this);
                if (clientsInputFile != null) {
                    fileHandler.readFile(clientsInputFile);
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
            bCalculate.setEnabled(true);
        } else if (source == bCalculate) {
//            for (Client client : ClientsDatabase.getClientsList()) {
//                client.getDistances().forEach((k, v) -> System.out.println(client.getId() + "-" + k + " Distance: " + v + " km"));
//            }
            Problem problem = new Problem();
        } else if (source == bExit) {
            dispose();
        }
    }
}
