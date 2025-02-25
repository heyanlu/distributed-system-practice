# Project 2: Concurrent RMI Key-Value Store

This project demonstrates a simple concurrent key-value store using Java RMI (Remote Method Invocation). The system consists of a server that hosts a key-value store and multiple clients that can perform concurrent operations (PUT, GET, DELETE) on the store.

## Project Structure
The project is organized into the following classes:

KeyValueStoreRMI: An interface defining the remote methods for the key-value store.

KeyValueStore: The server-side implementation of the key-value store, which uses a ConcurrentHashMap to store key-value pairs and a thread pool to handle concurrent requests.

RMIServer: The server application that binds the key-value store to the RMI registry.

RMIClient: A simple client application that performs a sequence of PUT, GET, and DELETE operations on the key-value store.

ConcurrentRMIClient: A client application that simulates multiple concurrent clients performing operations on the key-value store.


## How to Run the Project
Prerequisites
Java Development Kit (JDK) installed.

Basic understanding of Java and RMI.

Steps to Run
Compile the Project:

Open a terminal and navigate to the project directory.

### Compile all Java files using the following command:

```bash
javac Project2/*.java
```

### Start the RMI Registry:

In a new terminal window, start the RMI registry on port 1099:
```bash
rmiregistry 1099
```


### Run the RMIServer:

In another terminal window, run the RMIServer class:
```bash
java Project2.RMIServer
```

### Run the RMIClient or ConcurrentRMIClient:

To run the simple client, use:
```bash
java Project2.RMIClient
```

To run the concurrent client simulation, use:
```bash
java Project2.ConcurrentRMIClient
```

## Executive Summary 
### Key Features 
Concurrency: The key-value store uses a ConcurrentHashMap and a thread pool to handle multiple client requests concurrently.

Remote Method Invocation (RMI): The server and clients communicate using Java RMI, allowing remote access to the key-value store.

Error Handling: The system includes basic error handling for cases such as key not found or RMI communication issues.

### Expected Output
#### RMIClient:

The client will perform a sequence of PUT, GET, and DELETE operations and print the results to the console.

#### ConcurrentRMIClient:

The client will simulate multiple concurrent clients (3 by default) performing operations on the key-value store. Each client will print its operations and results to the console.
