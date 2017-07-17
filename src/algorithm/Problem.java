package algorithm;

import project.Customer;
import project.Database;

/**
 * Created by Marcin on 2017-06-17.
 */
public class Problem {
    private static int ID = 0;
    private int problemID;
    private int problemTypeID;
    private Customer depot;
    private int numberOfCustomers;
    private int numberOfVehicles;
    private double weightLimitPerVehicle;
    private double sizeLimitPerVehicle;

    public Problem(int problemTypeID, int numberOfVehicles, double weightLimitPerVehicle, double sizeLimitPerVehicle) {
        ID++;
        this.problemID = ID;
        this.problemTypeID = problemTypeID;
        this.depot = Database.getCustomerList().get(0);
        this.numberOfCustomers = Database.getCustomerList().size() - 1;
        this.numberOfVehicles = numberOfVehicles;
        this.weightLimitPerVehicle = weightLimitPerVehicle;
        this.sizeLimitPerVehicle = sizeLimitPerVehicle;
    }

    public int getProblemID() {
        return problemID;
    }

    public void setProblemID(int problemID) {
        this.problemID = problemID;
    }

    public int getProblemTypeID() {
        return problemTypeID;
    }

    public void setProblemTypeID(int problemTypeID) {
        this.problemTypeID = problemTypeID;
    }

    public Customer getDepot() {
        return depot;
    }

    public void setDepot(Customer depot) {
        this.depot = depot;
    }

    public int getNumberOfCustomers() {
        return numberOfCustomers;
    }

    public void setNumberOfCustomers(int numberOfCustomers) {
        this.numberOfCustomers = numberOfCustomers;
    }

    public int getNumberOfVehicles() {
        return numberOfVehicles;
    }

    public void setNumberOfVehicles(int numberOfVehicles) {
        this.numberOfVehicles = numberOfVehicles;
    }

    public double getWeightLimitPerVehicle() {
        return weightLimitPerVehicle;
    }

    public void setWeightLimitPerVehicle(double weightLimitPerVehicle) {
        this.weightLimitPerVehicle = weightLimitPerVehicle;
    }

    public double getSizeLimitPerVehicle() {
        return sizeLimitPerVehicle;
    }

    public void setSizeLimitPerVehicle(double sizeLimitPerVehicle) {
        this.sizeLimitPerVehicle = sizeLimitPerVehicle;
    }
}
