package gui;

import javax.swing.*;

/**
 * Created by mbala on 26.04.17.
 */
public class myWindow extends JFrame {

    public myWindow() {
        setSize(800,600);
        setTitle("VRP System");
        setLayout(null);
        JButton button = new JButton("Load data");
        button.setBounds(50,50,300,30);
        add(button);
    }
}
