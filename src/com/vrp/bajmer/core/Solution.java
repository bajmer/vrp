package com.vrp.bajmer.core;

import javax.swing.*;
import java.util.ArrayList;

/**
 * Created by mbala on 03.07.17.
 */
public class Solution {
    private int solutionID;
    private String usedAlgorithm;
    private ArrayList<Route> listOfRoutes;
    private Customer depot;
    private double totalDistanceCost;
    private double totalDurationCost;
    private ImageIcon imageIcon;

    public Solution(int problemID, String usedAlgorithm, Customer depot) {
        this.solutionID = problemID;
        this.usedAlgorithm = usedAlgorithm;
        this.depot = depot;
        this.listOfRoutes = new ArrayList<>();
        this.totalDistanceCost = 0.0;
        this.totalDurationCost = 0.0;
    }

    public int getSolutionID() {
        return solutionID;
    }

    public void setSolutionID(int solutionID) {
        this.solutionID = solutionID;
    }

    public ArrayList<Route> getListOfRoutes() {
        return listOfRoutes;
    }

    public void setListOfRoutes(ArrayList<Route> listOfRoutes) {
        this.listOfRoutes = listOfRoutes;
    }

    public Customer getDepot() {
        return depot;
    }

    public void setDepot(Customer depot) {
        this.depot = depot;
    }

    public String getUsedAlgorithm() {
        return usedAlgorithm;
    }

    public void setUsedAlgorithm(String usedAlgorithm) {
        this.usedAlgorithm = usedAlgorithm;
    }

    public double getTotalDistanceCost() {
        return totalDistanceCost;
    }

    public void setTotalDistanceCost(double totalDistanceCost) {
        this.totalDistanceCost = totalDistanceCost;
    }

    public double getTotalDurationCost() {
        return totalDurationCost;
    }

    public void setTotalDurationCost(double totalDurationCost) {
        this.totalDurationCost = totalDurationCost;
    }

    public ImageIcon getImageIcon() {
        return imageIcon;
    }

    public void setImageIcon(ImageIcon imageIcon) {
        this.imageIcon = imageIcon;
    }

    @Override
    public String toString() {
        return "S" + solutionID + ", "
                + usedAlgorithm + ", "
                + totalDistanceCost + "km, "
                + totalDurationCost + "min";
    }
}
