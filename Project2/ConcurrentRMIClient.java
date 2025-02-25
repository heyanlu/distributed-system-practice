package Project2;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ConcurrentRMIClient implements Runnable {
    private int clientId;

    public ConcurrentRMIClient(int clientId) {
        this.clientId = clientId;
    }

    @Override
    public void run() {
        try {
            // Get the RMI registry
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);

            // Look up the remote object
            KeyValueStoreRMI store = (KeyValueStoreRMI) registry.lookup("KeyValueStore");

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
        // Create and start multiple client threads
        for (int i = 1; i <= 3; i++) { // Simulate 3 concurrent clients
            new Thread(new ConcurrentRMIClient(i)).start();
        }
    }
}