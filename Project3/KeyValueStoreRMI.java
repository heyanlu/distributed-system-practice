package Project3;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface KeyValueStoreRMI extends Remote {
    String put(String key, String value) throws RemoteException;
    String get(String key) throws RemoteException;
    String delete(String key) throws RemoteException;

    // 2PC methods
    boolean prepare(String key, String value, String operation) throws RemoteException;
    boolean commit(String key, String value, String operation) throws RemoteException;
    boolean abort(String key, String operation) throws RemoteException;
}