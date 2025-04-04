package Project_4;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Learner {
    private final ConcurrentHashMap<String, String> store;
    private final ConcurrentHashMap<String, AtomicInteger> operationSequence;
    private final int id;

    public Learner(ConcurrentHashMap<String, String> store) {
        this(0, store);
    }

    public Learner(int id, ConcurrentHashMap<String, String> store) {
        this.id = id;
        this.store = store;
        this.operationSequence = new ConcurrentHashMap<>();
    }


    public synchronized void learn(String key, String value, String operation) {
        // Track operation sequence to ensure idempotency
        AtomicInteger sequence = operationSequence.computeIfAbsent(key, k -> new AtomicInteger(0));
        int currentSeq = sequence.incrementAndGet();

        System.out.println("Learner " + id + ": Learning operation " + operation +
                " for key " + key + " (sequence " + currentSeq + ")");

        if ("PUT".equals(operation)) {
            store.put(key, value);
            System.out.println("Learner " + id + ": Applied PUT for key " + key +
                    " with value " + value + " (sequence " + currentSeq + ")");
        } else if ("DELETE".equals(operation)) {
            store.remove(key);
            System.out.println("Learner " + id + ": Applied DELETE for key " + key +
                    " (sequence " + currentSeq + ")");
        } else {
            System.out.println("Learner " + id + ": Unknown operation " + operation);
        }
    }


    public String get(String key) {
        String value = store.get(key);
        if (value == null) {
            System.out.println("Learner " + id + ": GET for key " + key + " returned KEY_NOT_FOUND");
            return "ERROR: Key not found";
        }

        System.out.println("Learner " + id + ": GET for key " + key + " returned " + value);
        return value;
    }


    public ConcurrentHashMap<String, String> getStore() {
        return store;
    }
}