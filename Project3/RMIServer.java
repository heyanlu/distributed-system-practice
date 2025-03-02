package Project3;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;

public class RMIServer {
    public static void main(String[] args) {
        try {
            int port = 1099; // Start with port 1099
            List<KeyValueStoreRMI> replicas = new ArrayList<>();

            // Create and bind 5 servers
            for (int i = 0; i < 5; i++) {
                KeyValueStore store = new KeyValueStore(replicas);
                Registry registry = LocateRegistry.createRegistry(port + i);
                registry.rebind("KeyValueStore", store);
                replicas.add(store);
                System.out.println("RMI Server " + i + " is running on port " + (port + i));
            }
        } catch (Exception e) {
            System.err.println("RMI Server exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}