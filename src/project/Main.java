package project;

import gui.*;

import javax.swing.JFrame;

public class Main {

    public static void main(String[] args) {

        MyWindow window = new MyWindow();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setVisible(true);
    }
}
