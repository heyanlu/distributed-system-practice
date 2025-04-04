package Project_4;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

// Implements the RMI interface and routes client requests to the appropriate Paxos components
public class KeyValueStore extends UnicastRemoteObject implements KeyValueStoreRMI {
    private final int serverId;
    private final ConcurrentHashMap<String, String> store;
    private final Proposer proposer;
    private final Learner learner;
    private final List<Acceptor> acceptors;

    public KeyValueStore(int serverId, List<Acceptor> acceptors, Proposer proposer, Learner learner) throws RemoteException {
        super();
        this.serverId = serverId;
        this.acceptors = acceptors;

        if (learner != null) {
            this.learner = learner;
            this.store = learner.getStore();
        } else {
            this.store = new ConcurrentHashMap<>();
            this.learner = new Learner(serverId, store);
        }

        this.proposer = proposer != null ? proposer : new Proposer(serverId, acceptors, this.learner);
    }


    public KeyValueStore(List<Acceptor> acceptors) throws RemoteException {
        this(0, acceptors, null, null);
    }

    @Override
    public String put(String key, String value) throws RemoteException {
        System.out.println("KeyValueStore " + serverId + ": PUT request for key " + key);
        return proposer.propose(key, value, "PUT");
    }

    @Override
    public String get(String key) throws RemoteException {
        System.out.println("KeyValueStore " + serverId + ": GET request for key " + key);
        return learner.get(key);
    }

    @Override
    public String delete(String key) throws RemoteException {
        System.out.println("KeyValueStore " + serverId + ": DELETE request for key " + key);
        return proposer.propose(key, null, "DELETE");
    }
}