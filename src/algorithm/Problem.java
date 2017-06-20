package algorithm;

import project.Customer;
import project.CustomerDatabase;

/**
 * Created by Marcin on 2017-06-17.
 */
public class Problem {

    private int problemID;
    //private int
    private Customer depot;

    public Problem() {
        depot = CustomerDatabase.getCustomerList().get(0);
        System.out.println("depot ID: " + depot.getId());
    }

}
