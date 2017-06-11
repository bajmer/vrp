package project;

import gui.MyWindow;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {

        MyWindow window = new MyWindow();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setVisible(true);
    }
}
