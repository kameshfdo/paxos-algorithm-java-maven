
# Paxos Implementation

This repository contains an implementation of the Paxos algorithm in Java. The implementation includes the main components of the Paxos protocol: Proposers, Acceptors, and Learners. The communication between nodes is handled using Java sockets.

## Files

- `Message.java`: Defines the `Message` class used for communication between nodes.
- `Node.java`: Abstract class representing a node in the Paxos protocol.
- `NodeServer.java`: Handles network communication between nodes.
- `Proposer.java`: Implements the proposer role in the Paxos protocol.
- `Acceptor.java`: Implements the acceptor role in the Paxos protocol.
- `Learner.java`: Implements the learner role in the Paxos protocol.

## How to Run

1. Compile all Java files:
   ```sh
   javac paxos/*.java
