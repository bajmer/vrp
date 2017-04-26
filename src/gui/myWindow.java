package gui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

/**
 * Created by mbala on 26.04.17.
 */
public class myWindow extends JFrame implements ActionListener {

    private JButton bLoad, bExit;

    public myWindow() {
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
            System.out.println(new Date());
        } else if (source == bExit) {
            dispose();
        }

    }
}
