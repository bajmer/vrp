package core;

import javax.swing.*;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayList;

public class Solution {
    private int solutionID;
    private String usedAlgorithm;
    private ArrayList<Route> listOfRoutes;
    private Customer depot;
    private double totalDistanceCost;
    private Duration totalDurationCost;
    private ImageIcon imageIcon;
    private boolean feasible;

    public Solution(int problemID, String usedAlgorithm, Customer depot) {
        this.solutionID = problemID;
        this.usedAlgorithm = usedAlgorithm;
        this.depot = depot;
        this.listOfRoutes = new ArrayList<>();
        this.totalDistanceCost = 100000.0;
        this.totalDurationCost = Duration.ofSeconds(1000000);
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

    public Duration getTotalDurationCost() {
        return totalDurationCost;
    }

    public void setTotalDurationCost(Duration totalDurationCost) {
        this.totalDurationCost = totalDurationCost;
    }

    public ImageIcon getImageIcon() {
        return imageIcon;
    }

    public void setImageIcon(ImageIcon imageIcon) {
        this.imageIcon = imageIcon;
    }

    public boolean isFeasible() {
        return feasible;
    }

    public void setFeasible(boolean feasible) {
        this.feasible = feasible;
    }

    private double round(double x) {
        return new BigDecimal(x).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    @Override
    public String toString() {
        long minutes = totalDurationCost.toMinutes() % 60;
        String sMinutes;
        sMinutes = minutes < 10 ? "0" + Long.toString(minutes) : Long.toString(minutes);
        return "S" + solutionID + ", "
                + usedAlgorithm + ", "
                + round(totalDistanceCost) + "km, "
                + totalDurationCost.toHours() + ":" + sMinutes + "h";
    }
}
