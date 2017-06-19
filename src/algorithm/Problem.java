package algorithm;

import project.Client;
import project.ClientsDatabase;

/**
 * Created by Marcin on 2017-06-17.
 */
public class Problem {

    private int problemID;
    private Client depot;

    public Problem() {
        depot = ClientsDatabase.getClientsList().get(0);
        System.out.println("depot ID: " + depot.getId());
    }

}
