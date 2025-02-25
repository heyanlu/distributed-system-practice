package Project2;

import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class KeyValueStore extends UnicastRemoteObject implements KeyValueStoreRMI {
    private ConcurrentHashMap<String, String> store;
    private ExecutorService threadPool;

    public KeyValueStore() throws RemoteException {
        store = new ConcurrentHashMap<>();
        threadPool = Executors.newFixedThreadPool(10);
    }

    @Override
    public String put(String key, String value) throws RemoteException {
        return executeTask(() -> {
            store.put(key, value);
            return "PUT successful for key: " + key;
        });
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
        return executeTask(() -> {
            String value = store.remove(key);
            return (value != null) ? "DELETE successful for key: " + key : "ERROR: Key not found: " + key;
        });
    }

    // execute tasks in the thread pool
    private String executeTask(Task task) {
        try {
            return threadPool.submit(task::execute).get();
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }

    @FunctionalInterface
    private interface Task {
        String execute();
    }
}
