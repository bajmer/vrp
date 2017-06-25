package project;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mbala on 24.05.17.
 */
public class Database {
    private static List<Customer> customerList = new ArrayList<>();
    private static List<RouteSegment> routeSegmentsList = new ArrayList<>();

    public Database() {
    }

    public static List<Customer> getCustomerList() {
        return customerList;
    }

    public static void setCustomerList() {
        Database.customerList = customerList;
    }

    public static List<RouteSegment> getRouteSegmentsList() {
        return routeSegmentsList;
    }

    public static void setRouteSegmentsList(List<RouteSegment> routeSegmentsList) {
        Database.routeSegmentsList = routeSegmentsList;
    }
}
