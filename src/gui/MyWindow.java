package gui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Created by mbala on 26.04.17.
 */
public class MyWindow extends JFrame implements ActionListener {

    private JButton bLoad, bExit;

    public MyWindow() {
        setSize(800,600);
        setTitle("VRP System");
        setLayout(null);

        bLoad = new JButton("Load data");
        bLoad.setBounds(50,50,300,30);
        add(bLoad);
        bLoad.addActionListener(this);

        bExit = new JButton("Exit");
        bExit.setBounds(50,100,300,30);
        add(bExit);
        bExit.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == bLoad) {
            try {
                FileHandler fileHandler = new FileHandler();
                File clientsInput = fileHandler.chooseFile(this);
                fileHandler.readFile(clientsInput);
            } catch (Exception ex) {
                System.out.println("Unexpected error with file.");
                ex.printStackTrace();
            }
        } else if (source == bExit) {
            dispose();
        }

    }
}
