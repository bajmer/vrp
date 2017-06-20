package project;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by mbala on 24.05.17.
 */
public class CustomerDatabase {
    private static List<Customer> customerList = new LinkedList<Customer>();

    public CustomerDatabase() {
    }

    public static List<Customer> getCustomerList() {
        return customerList;
    }

    public static void setCustomerList() {
        CustomerDatabase.customerList = customerList;
    }

}
