package com.vrp.bajmer.core;

import com.vrp.bajmer.gui.Gui;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;

public class Main {

    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {

//        Window window = new Window();
//        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        window.setVisible(true);
        Gui gui = new Gui();
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gui.setExtendedState(JFrame.MAXIMIZED_BOTH);
        gui.setTitle("VRP System");
        gui.setVisible(true);
        logger.info("*********************************************************************************************************************************************");
        logger.info("Application started.");
    }
}
