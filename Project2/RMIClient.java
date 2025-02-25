package Project2;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RMIClient {
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);

            KeyValueStoreRMI store = (KeyValueStoreRMI) registry.lookup("KeyValueStore");

            for (int i = 1; i <= 5; i++) {
                System.out.println(store.put("key" + i, "value" + i));
            }

            for (int i = 1; i <= 5; i++) {
                System.out.println(store.get("key" + i));
            }

            for (int i = 1; i <= 5; i++) {
                System.out.println(store.delete("key" + i));
            }
        } catch (Exception e) {
            System.err.println("RMI Client exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
