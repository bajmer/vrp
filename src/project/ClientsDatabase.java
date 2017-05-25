package project;

import java.util.LinkedList;

/**
 * Created by mbala on 24.05.17.
 */
public class ClientsDatabase {

    private static LinkedList<Client> clientsList = new LinkedList<Client>();

    public ClientsDatabase() {
    }

    public static LinkedList<Client> getClientsList() {
        return clientsList;
    }

    public static void setClientsList(LinkedList<Client> clientsList) {
        ClientsDatabase.clientsList = clientsList;
    }
}
