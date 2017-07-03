package project;

import java.util.ArrayList;

/**
 * Created by mbala on 03.07.17.
 */
public class Route {

    private ArrayList<Customer> nodes;
    private double currentPackagesWeight;
    //private double currentPackagesCapacity;

    public Route() {
        nodes = new ArrayList<Customer>();
        currentPackagesWeight = 0.0;
        //currentPackagesCapacity = 0.0;
    }

    public ArrayList<Customer> getNodes() {
        return nodes;
    }

    public void setNodes(ArrayList<Customer> nodes) {
        this.nodes = nodes;
    }

    public double getCurrentPackagesWeight() {
        return currentPackagesWeight;
    }

    public void setCurrentPackagesWeight(double currentPackagesWeight) {
        this.currentPackagesWeight = currentPackagesWeight;
    }

//    public double getCurrentPackagesCapacity() {
//        return currentPackagesCapacity;
//    }
//
//    public void setCurrentPackagesCapacity(double currentPackagesCapacity) {
//        this.currentPackagesCapacity = currentPackagesCapacity;
//    }

    public boolean canAddNode(double packageWeight, double weightLimit) {
        return canAddWeight(packageWeight, weightLimit);
    }

    private boolean canAddWeight(double packageWeight, double weightLimit) {
        return currentPackagesWeight + packageWeight <= weightLimit;
    }
}
