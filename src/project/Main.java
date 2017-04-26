package project;

import gui.*;

import javax.swing.JFrame;

public class Main {

    public static void main(String[] args) {

        myWindow window = new myWindow();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setVisible(true);
    }
}
