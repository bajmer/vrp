package project;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by mbala on 24.05.17.
 */
public class ClientsDatabase {
    private static List<Client> clientsList = new LinkedList<Client>();

    public ClientsDatabase() {
    }

    public static List<Client> getClientsList() {
        return clientsList;
    }

    public static void setClientsList() {
        ClientsDatabase.clientsList = clientsList;
    }

}
