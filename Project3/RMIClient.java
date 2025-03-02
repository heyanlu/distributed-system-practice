package Project3;


import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class RMIClient {
    public static void main(String[] args) {
        try {
            // Get references to all 5 servers
            List<KeyValueStoreRMI> servers = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                Registry registry = LocateRegistry.getRegistry("localhost", 1099 + i);
                servers.add((KeyValueStoreRMI) registry.lookup("KeyValueStore"));
            }

            // Pre-populate the key-value store
            prePopulateKeyValueStore(servers);

            // Perform operations
            performOperations(servers);
        } catch (Exception e) {
            System.err.println("RMI Client exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Pre-populates the key-value store with initial data.
     */
    private static void prePopulateKeyValueStore(List<KeyValueStoreRMI> servers) throws RemoteException {
        System.out.println("Pre-populating the key-value store...");
        KeyValueStoreRMI store = servers.get(0);

        for (int i = 1; i <= 10; i++) {
            String key = "initKey" + i;
            String value = "initValue" + i;
            System.out.println("PUT: " + store.put(key, value));
        }
        System.out.println("Pre-population complete.\n");
    }

    /**
     * Performs 5 PUT, 5 GET, and 5 DELETE operations on a random server.
     */
    private static void performOperations(List<KeyValueStoreRMI> servers) throws RemoteException {
        Random random = new Random();

        // Perform 5 PUT operations
        for (int i = 1; i <= 5; i++) {
            String key = "key" + i;
            String value = "value" + i;
            // Randomly select a server
            KeyValueStoreRMI store = servers.get(random.nextInt(servers.size()));
            System.out.println("PUT: " + store.put(key, value));
        }

        // Perform 5 GET operations
        for (int i = 1; i <= 5; i++) {
            String key = "key" + i;
            // Randomly select a server
            KeyValueStoreRMI store = servers.get(random.nextInt(servers.size()));
            System.out.println("GET: " + store.get(key));
        }

        // Perform 5 DELETE operations
        for (int i = 1; i <= 5; i++) {
            String key = "key" + i;
            // Randomly select a server
            KeyValueStoreRMI store = servers.get(random.nextInt(servers.size()));
            System.out.println("DELETE: " + store.delete(key));
        }
    }
}