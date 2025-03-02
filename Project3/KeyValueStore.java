package Project3;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class KeyValueStore extends UnicastRemoteObject implements KeyValueStoreRMI {
    private ConcurrentHashMap<String, String> store;
    private List<KeyValueStoreRMI> replicas;
    private ExecutorService threadPool;

    // Make replicas
    public KeyValueStore(List<KeyValueStoreRMI> replicas) throws RemoteException {
        store = new ConcurrentHashMap<>();
        this.replicas = replicas;
        threadPool = Executors.newFixedThreadPool(10);
    }


    // Executes a Two-Phase Commit (2PC) protocol for PUT and DELETE operations.
    private String executeTwoPhaseCommit(String key, String value, String operation) {
        // Phase 1: Prepare
        boolean allPrepared = true;
        for (KeyValueStoreRMI replica : replicas) {
            try {
                if (!replica.prepare(key, value, operation)) {
                    allPrepared = false;
                    break;
                }
            } catch (RemoteException e) {
                System.err.println("Prepare phase failed for replica: " + e.getMessage());
                allPrepared = false;
                break;
            }
        }

        if (!allPrepared) {
            // Abort
            for (KeyValueStoreRMI replica : replicas) {
                try {
                    replica.abort(key, operation);
                } catch (RemoteException e) {
                    System.err.println("Abort failed for replica: " + e.getMessage());
                }
            }
            return "ERROR: Operation aborted due to prepare phase failure";
        }

        // Phase 2: Commit
        for (KeyValueStoreRMI replica : replicas) {
            try {
                replica.commit(key, value, operation);
            } catch (RemoteException e) {
                System.err.println("Commit failed for replica: " + e.getMessage());
                return "ERROR: Operation failed during commit phase";
            }
        }

        // Apply locally
        if (operation.equals("PUT")) {
            store.put(key, value);
        } else if (operation.equals("DELETE")) {
            store.remove(key);
        }

        return "SUCCESS: Operation committed";
    }

    @Override
    public String put(String key, String value) throws RemoteException {
        return executeTwoPhaseCommit(key, value, "PUT");
    }

    @Override
    public String get(String key) throws RemoteException {
        return executeTask(() -> {
            String value = store.get(key);
            return (value != null) ? value : "ERROR: Key not found: " + key;
        });
    }

    @Override
    public String delete(String key) throws RemoteException {
        return executeTwoPhaseCommit(key, null, "DELETE");
    }

    @Override
    public boolean prepare(String key, String value, String operation) throws RemoteException {
        // Simulate ACK
        return true;
    }

    @Override
    public boolean commit(String key, String value, String operation) throws RemoteException {
        // Simulate ACK
        if (operation.equals("PUT")) {
            store.put(key, value);
        } else if (operation.equals("DELETE")) {
            store.remove(key);
        }
        return true;
    }

    @Override
    public boolean abort(String key, String operation) throws RemoteException {
        // No action needed
        return true;
    }


     // Executes a task in the thread pool with a timeout.
    private String executeTask(Task task) {
        try {
            return threadPool.submit(task::execute).get(5, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            return "ERROR: Operation timed out";
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }

     // Shuts down the thread pool gracefully
    public void shutdown() {
        threadPool.shutdown();
        try {
            if (!threadPool.awaitTermination(5, TimeUnit.SECONDS)) {
                threadPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            threadPool.shutdownNow();
        }
    }

    @FunctionalInterface
    private interface Task {
        String execute();
    }
}