package paxos;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        int numNodes = 5;
        List<Node> nodes = new ArrayList<>();
        List<NodeServer> nodeServers = new ArrayList<>();
        List<Node> peers = new ArrayList<>();
        String hostname = "localhost";
        int basePort = 8000;

        for (int i = 0; i < numNodes; i++) {
            Node node;
            int port = basePort + i;
            if (i == 0) {
                node = new Proposer(i, peers, hostname, port);
            } else if (i == 1) {
                node = new Learner(i, peers, hostname, port);
            } else {
                node = new Acceptor(i, peers, hostname, port);
            }
            peers.add(node);
            nodes.add(node);
            NodeServer nodeServer = new NodeServer(node, port);
            nodeServers.add(nodeServer);
            nodeServer.start();
        }

        // Simulate proposing a value
        Proposer proposer = (Proposer) nodes.get(0);
        proposer.propose(42);

        // Give some time for asynchronous messages
        try {
            Thread.sleep(1000); // Sleep for 1 second to simulate the time taken for messages
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Simulate node failure
        nodes.get(2).fail();
        nodes.get(3).fail();
        nodeServers.get(2).stopServer();
        nodeServers.get(3).stopServer();

        // Give some time for messages and to simulate downtime
        try {
            Thread.sleep(1000); // Sleep for 1 second to simulate the time taken for messages
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Simulate node recovery
        nodes.get(2).recover();
        nodes.get(3).recover();
        NodeServer recoveredNodeServer2 = new NodeServer(nodes.get(2), basePort + 2);
        NodeServer recoveredNodeServer3 = new NodeServer(nodes.get(3), basePort + 3);
        nodeServers.set(2, recoveredNodeServer2);
        nodeServers.set(3, recoveredNodeServer3);
        recoveredNodeServer2.start();
        recoveredNodeServer3.start();

        // Give some more time for the nodes to recover and for messages to propagate
        try {
            Thread.sleep(1000); // Sleep for 1 second to simulate the time taken for messages
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Propose another value to see if consensus can be reached after recovery
        proposer.propose(23);

        // Final sleep to ensure all messages are processed
        try {
            Thread.sleep(1000); // Sleep for 1 second to simulate the time taken for messages
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
