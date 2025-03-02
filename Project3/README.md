# Project 3: Distributed Key-Value Store with Two-Phase Commit (2PC)

This project implements a distributed key-value store using Java RMI (Remote Method Invocation) and a Two-Phase Commit (2PC) protocol to ensure consistency across multiple server replicas. The system supports concurrent client operations and handles PUT, GET, and DELETE requests.

## Project Structure
### KeyValueStoreRMI: 
Defines the remote methods (put, get, delete, prepare, commit, abort) for the key-value store.

### KeyValueStore: 
Implements the KeyValueStoreRMI interface.

Uses a ConcurrentHashMap to store key-value pairs.

Implements the Two-Phase Commit (2PC) protocol for PUT and DELETE operations.

### RMIServer:

Starts 5 instances of the KeyValueStore server, each running on a different port.

### RMIClient:

Connects to one of the server replicas and performs PUT, GET, and DELETE operations.

## How to Run the Project
## Prerequisites
Java Development Kit (JDK) installed.

Basic understanding of Java and RMI.

## Steps to Run
Compile the Project:

Open a terminal and navigate to the project directory.

### Compile all Java files using the following command:

```bash
javac Project3/*.java
```

### Start the Servers:

Open 5 terminal windows and start the server instances on different ports:

```bash
# Terminal 1 (Server 1 on port 1099)
java Project3.RMIServer

# Terminal 2 (Server 2 on port 1100)
java Project3.RMIServer

# Terminal 3 (Server 3 on port 1101)
java Project3.RMIServer

# Terminal 4 (Server 4 on port 1102)
java Project3.RMIServer

# Terminal 5 (Server 5 on port 1103)
java Project3.RMIServer
```


### Run the Client:

In another terminal window, run the RMIServer class:
```bash
java Project3.RMIServer
```

## Executive Summary 
### Features
Replication: The key-value store is replicated across 5 server instances for high availability and fault tolerance.

Consistency: The Two-Phase Commit (2PC) protocol ensures that all replicas remain consistent during PUT and DELETE operations.

Concurrency: The server uses a thread pool to handle multiple client requests concurrently.

Fault Tolerance: The system gracefully handles replica failures during the 2PC protocol.
