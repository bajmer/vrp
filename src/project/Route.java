package project;

import java.util.ArrayList;

/**
 * Created by mbala on 03.07.17.
 */
public class Route {

    private static int routeID = 0;
    private int id;
    private ArrayList<Customer> customersInRoute;
    private double totalDistance;
    private double currentPackagesWeight;
    //private double currentPackagesCapacity;

    public Route() {
        routeID++;
        id = routeID;
        customersInRoute = new ArrayList<Customer>();
        totalDistance = 0.0;
        currentPackagesWeight = 0.0;
        //currentPackagesCapacity = 0.0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<Customer> getCustomersInRoute() {
        return customersInRoute;
    }

    public void setCustomersInRoute(ArrayList<Customer> customersInRoute) {
        this.customersInRoute = customersInRoute;
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(double totalDistance) {
        this.totalDistance = totalDistance;
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
public void addCustomer(Customer customer) {
    customersInRoute.add(customer);
    currentPackagesWeight += customer.getPackageWeight();
}

    public void addCustomerFirstPlace(Customer customer) {
        int firstPlace = 0;
        customersInRoute.add(firstPlace, customer);
        currentPackagesWeight += customer.getPackageWeight();
    }

    public void mergeRoute(Route route) {
        this.customersInRoute.addAll(route.getCustomersInRoute());
        currentPackagesWeight += route.getCurrentPackagesWeight();
    }

    public boolean canAddCustomer(double packageWeight, double weightLimit) {
        return canAddWeight(packageWeight, weightLimit);
    }

    private boolean canAddWeight(double packageWeight, double weightLimit) {
        return currentPackagesWeight + packageWeight <= weightLimit;
    }

    public boolean isCustomerFirstInRoute(Customer customer) {
        return customersInRoute.get(0).equals(customer);
    }

    public boolean isCustomerLastInRoute(Customer customer) {
        return customersInRoute.get(customersInRoute.size() - 1).equals(customer);
    }
}
