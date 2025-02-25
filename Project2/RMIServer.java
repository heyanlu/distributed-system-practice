package Project2;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RMIServer {
    public static void main(String[] args) {
        try {
            // Create an instance of the KeyValueStore
            KeyValueStore store = new KeyValueStore();

            // Bind the remote object to the RMI registry
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind("KeyValueStore", store);

            System.out.println("RMI Server is running...");
        } catch (Exception e) {
            System.err.println("RMI Server exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}