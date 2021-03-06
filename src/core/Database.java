package core;

import java.util.ArrayList;
import java.util.List;

/**
 * Klasa przechowująca listę klientow, liste odcinkow i liste rozwiazan
 */
public class Database {

    /**
     * Lista klientów
     */
    private static List<Customer> customerList = new ArrayList<>();

    /**
     * Lista odcinkow trasy
     */
    private static List<RouteSegment> routeSegmentsList = new ArrayList<>();

    /**
     * Lista rozwiazan
     */
    private static List<Solution> solutionsList = new ArrayList<>();

    /**
     * Domyslny konstruktor klasy
     */
    public Database() {
    }

    public static List<Customer> getCustomerList() {
        return customerList;
    }

    public static void setCustomerList(List<Customer> customerList) {
        Database.customerList = customerList;
    }

    public static List<RouteSegment> getRouteSegmentsList() {
        return routeSegmentsList;
    }

    public static void setRouteSegmentsList(List<RouteSegment> routeSegmentsList) {
        Database.routeSegmentsList = routeSegmentsList;
    }

    public static List<Solution> getSolutionsList() {
        return solutionsList;
    }

    public static void setSolutionsList(List<Solution> solutionsList) {
        Database.solutionsList = solutionsList;
    }
}
