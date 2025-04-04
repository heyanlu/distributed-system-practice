# Project 4: Fault-Tolerant Key-Value Store with Paxos

This project implements a distributed Key-Value store that achieves fault tolerance using the Paxos consensus algorithm as described in Leslie Lamport's paper "Paxos Made Simple". The system can continue operating correctly despite server failures.
## Project Structure
### KeyValueStore: 
Implements the RMI interface and delegates operations to Paxos components

### Acceptor: 
Votes on proposals and randomly fails/restarts to simulate real-world failures

### Proposer: 
Coordinates the two-phase Paxos protocol to achieve consensus

### Learner: 
Applies consensus decisions to the actual data store

### RMIServer:

Initializes and coordinates the distributed system
### RMIClient:

Tests the system by performing operations and verifying consistency

## How to Run the Project
## Prerequisites
Java Development Kit (JDK) installed.

Basic understanding of Java and RMI.

## Steps to Run
Compile the Project:

Open a terminal and navigate to the project directory.

### Compile all Java files using the following command:

```bash
javac Project_4/*.java
```

### Start the server:

```bash
java Project_4.RMIServer
```


### Run the Client:

In another terminal window, run the RMIServer class:
```bash
java Project_4.RMIServer
```



## Executive Summary 
### Features
Distributed Key-Value store supporting PUT, GET, and DELETE operations

Replication across 5 distinct servers

Fault tolerance using Paxos consensus algorithm

Random acceptor failures and automatic recovery

Consistent data views across all operational servers