package Project_4;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class RMIClient {
    private static final int NUM_SERVERS = 5;
    private static final int BASE_PORT = 1099;
    private static final Random random = new Random();

    public static void main(String[] args) {
        try {
            List<KeyValueStoreRMI> servers = connectToServers();

            System.out.println("Connected to " + servers.size() + " KV-Store servers");

            System.out.println("\n=== Pre-populating KV-Store ===");
            populateKVStore(servers);

            TimeUnit.SECONDS.sleep(2);

            System.out.println("\n=== Performing Operations ===");
            performOperations(servers);

            System.out.println("\nClient operations completed successfully");
        } catch (Exception e) {
            System.err.println("RMI Client exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static List<KeyValueStoreRMI> connectToServers() throws Exception {
        List<KeyValueStoreRMI> servers = new ArrayList<>();

        for (int i = 0; i < NUM_SERVERS; i++) {
            try {
                Registry registry = LocateRegistry.getRegistry("localhost", BASE_PORT + i);
                KeyValueStoreRMI store = (KeyValueStoreRMI) registry.lookup("KeyValueStore");
                servers.add(store);
                System.out.println("Connected to server " + i + " on port " + (BASE_PORT + i));
            } catch (Exception e) {
                System.err.println("Failed to connect to server " + i + ": " + e.getMessage());
            }
        }

        if (servers.isEmpty()) {
            throw new Exception("Failed to connect to any servers");
        }

        return servers;
    }

    private static void populateKVStore(List<KeyValueStoreRMI> servers) throws RemoteException {
        // Populate with initial data
        for (int i = 1; i <= 10; i++) {
            String key = "init" + i;
            String value = "value" + i;

            KeyValueStoreRMI store = getRandomServer(servers);

            try {
                String result = store.put(key, value);
                System.out.println("Populated: " + key + " -> " + value + " | Result: " + result);
            } catch (RemoteException e) {
                System.out.println("Server failed during PUT. Retrying with another server...");
                store = getRandomServer(servers);
                String result = store.put(key, value);
                System.out.println("Populated (retry): " + key + " -> " + value + " | Result: " + result);
            }
        }
    }

    private static void performOperations(List<KeyValueStoreRMI> servers) throws RemoteException, InterruptedException {
        // Perform 5 PUT operations
        System.out.println("\n--- Performing 5 PUT operations ---");
        for (int i = 1; i <= 5; i++) {
            String key = "key" + i;
            String value = "updated_value" + i;

            KeyValueStoreRMI store = getRandomServer(servers);
            try {
                String result = store.put(key, value);
                System.out.println("PUT: " + key + " -> " + value + " | Result: " + result);
            } catch (RemoteException e) {
                handleServerFailure(servers, "PUT", key, value, null);
            }

            TimeUnit.MILLISECONDS.sleep(200);
        }

        System.out.println("\n--- Performing 5 GET operations ---");
        for (int i = 1; i <= 5; i++) {
            String key = "key" + i;

            KeyValueStoreRMI store = getRandomServer(servers);
            try {
                String result = store.get(key);
                System.out.println("GET: " + key + " | Result: " + result);
            } catch (RemoteException e) {
                handleServerFailure(servers, "GET", key, null, null);
            }

            TimeUnit.MILLISECONDS.sleep(200);
        }

        System.out.println("\n--- Performing 5 DELETE operations ---");
        for (int i = 1; i <= 5; i++) {
            String key = "key" + i;

            KeyValueStoreRMI store = getRandomServer(servers);
            try {
                String result = store.delete(key);
                System.out.println("DELETE: " + key + " | Result: " + result);
            } catch (RemoteException e) {
                handleServerFailure(servers, "DELETE", key, null, null);
            }

            TimeUnit.MILLISECONDS.sleep(200);
        }

        verifyConsistency(servers);
    }

    private static void handleServerFailure(List<KeyValueStoreRMI> servers, String operation,
                                            String key, String value, KeyValueStoreRMI failedServer)
            throws RemoteException {
        System.out.println("Server failed during " + operation + " operation. Retrying with another server...");

        KeyValueStoreRMI newServer = getRandomServer(servers);
        while (newServer == failedServer && servers.size() > 1) {
            newServer = getRandomServer(servers);
        }

        String result;
        switch (operation) {
            case "PUT":
                result = newServer.put(key, value);
                System.out.println("PUT (retry): " + key + " -> " + value + " | Result: " + result);
                break;
            case "GET":
                result = newServer.get(key);
                System.out.println("GET (retry): " + key + " | Result: " + result);
                break;
            case "DELETE":
                result = newServer.delete(key);
                System.out.println("DELETE (retry): " + key + " | Result: " + result);
                break;
        }
    }

    private static void verifyConsistency(List<KeyValueStoreRMI> servers) throws RemoteException {
        System.out.println("\n--- Verifying Consistency Across Servers ---");

        for (int i = 1; i <= 5; i++) {
            String key = "key" + i;
            boolean consistent = true;

            for (int j = 0; j < servers.size(); j++) {
                try {
                    String result = servers.get(j).get(key);
                    if (!result.contains("ERROR")) {
                        consistent = false;
                        System.out.println("Inconsistency detected on server " + j + " for key " + key + ": " + result);
                    }
                } catch (RemoteException e) {
                    System.out.println("Server " + j + " failed during consistency check");
                }
            }

            if (consistent) {
                System.out.println("Key " + key + " deleted consistently across all servers");
            }
        }

        for (int i = 1; i <= 10; i++) {
            String key = "init" + i;
            String expectedValue = null;
            boolean consistent = true;

            for (KeyValueStoreRMI server : servers) {
                try {
                    String result = server.get(key);
                    if (!result.contains("ERROR")) {
                        expectedValue = result;
                        break;
                    }
                } catch (RemoteException e) {
                    // Skip failed server
                }
            }

            if (expectedValue == null) {
                System.out.println("Could not find value for key " + key + " on any server");
                continue;
            }

            for (int j = 0; j < servers.size(); j++) {
                try {
                    String result = servers.get(j).get(key);
                    if (!result.equals(expectedValue)) {
                        consistent = false;
                        System.out.println("Inconsistency detected on server " + j + " for key " + key +
                                ": Expected '" + expectedValue + "', got '" + result + "'");
                    }
                } catch (RemoteException e) {
                    System.out.println("Server " + j + " failed during consistency check");
                }
            }

            if (consistent) {
                System.out.println("Key " + key + " is consistent across all servers with value '" + expectedValue + "'");
            }
        }
    }

    private static KeyValueStoreRMI getRandomServer(List<KeyValueStoreRMI> servers) {
        int index = random.nextInt(servers.size());
        return servers.get(index);
    }
}
