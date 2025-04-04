package Project_4;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


public class RMIServer {
    private static final int BASE_PORT = 1099;
    private static final int NUM_SERVERS = 5;

    public static void main(String[] args) {
        try {
            ConcurrentHashMap<String, String> sharedStore = new ConcurrentHashMap<>();

            List<Acceptor> acceptors = new ArrayList<>();

            System.out.println("Starting KV-Store cluster with Paxos consensus...");

            for (int i = 0; i < NUM_SERVERS; i++) {
                Acceptor acceptor = new Acceptor(i);
                Thread acceptorThread = new Thread(acceptor);
                acceptorThread.setDaemon(true);
                acceptorThread.start();
                acceptors.add(acceptor);
                System.out.println("Started Acceptor " + i);
            }

            for (int i = 0; i < NUM_SERVERS; i++) {
                Learner learner = new Learner(i, sharedStore);

                Proposer proposer = new Proposer(i, acceptors, learner);

                KeyValueStore store = new KeyValueStore(i, acceptors, proposer, learner);

                Registry registry = LocateRegistry.createRegistry(BASE_PORT + i);
                registry.rebind("KeyValueStore", store);

                System.out.println("RMI Server " + i + " is running on port " + (BASE_PORT + i));
            }

            System.out.println("All " + NUM_SERVERS + " servers started successfully.");
            System.out.println("KV-Store cluster is ready to accept client requests.");

            Thread.currentThread().join();

        } catch (Exception e) {
            System.err.println("RMI Server exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}