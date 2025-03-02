package Project3;

import Project2.KeyValueStoreRMI;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ConcurrentRMIClient implements Runnable {
    private int clientId;
    private List<KeyValueStoreRMI> servers;

    public ConcurrentRMIClient(int clientId, List<KeyValueStoreRMI> servers) {
        this.clientId = clientId;
        this.servers = servers;
    }

    @Override
    public void run() {
        try {
            // Randomly select a server
            KeyValueStoreRMI store = servers.get(new Random().nextInt(servers.size()));

            // Perform operations
            for (int i = 1; i <= 5; i++) {
                String key = "key" + clientId + "-" + i;
                String value = "value" + clientId + "-" + i;

                // PUT
                System.out.println("Client " + clientId + ": " + store.put(key, value));

                // GET
                System.out.println("Client " + clientId + ": " + store.get(key));

                // DELETE
                System.out.println("Client " + clientId + ": " + store.delete(key));
            }
        } catch (Exception e) {
            System.err.println("Client " + clientId + " exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            // Get references to all 5 servers
            List<KeyValueStoreRMI> servers = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                Registry registry = LocateRegistry.getRegistry("localhost", 1099 + i);
                servers.add((KeyValueStoreRMI) registry.lookup("KeyValueStore"));
            }

            // Create and start multiple client threads
            for (int i = 1; i <= 3; i++) { // Simulate 3 concurrent clients
                new Thread(new ConcurrentRMIClient(i, servers)).start();
            }
        } catch (Exception e) {
            System.err.println("Client exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}