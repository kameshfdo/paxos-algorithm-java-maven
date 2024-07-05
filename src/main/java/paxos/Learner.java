package paxos;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Learner class in the Paxos protocol, responsible for learning the chosen value.
 */
public class Learner extends Node {

    private int majority;
    private Map<Integer, Integer> acceptedCount = new HashMap<>();
    private int finalValue = -1;
    private boolean learned = false;

    /**
     * Constructs a Learner object.
     *
     * @param nodeId   The ID of the node.
     * @param peers    List of peer nodes.
     * @param hostname Hostname for communication.
     * @param port     Port number for communication.
     */
    public Learner(int nodeId, List<Node> peers, String hostname, int port) {
        super(nodeId, peers, hostname, port);
        this.majority = (peers.size() / 2) + 1;
    }

    /**
     * Handles an accepted message from an acceptor.
     *
     * @param proposalId The ID of the proposal.
     * @param value      The accepted value.
     * @param accepter   The acceptor node.
     */
    public void receiveAccepted(int proposalId, int value, Node accepter) {
        if (!learned) {
            acceptedCount.put(value, acceptedCount.getOrDefault(value, 0) + 1);
            if (acceptedCount.get(value) >= majority) {
                finalValue = value;
                learned = true;
                System.out.println("Learner " + nodeId + " learned the value: " + finalValue);
                System.out.println("Consensus reached on value: " + finalValue);
            }
        }
    }

    @Override
    public void handleMessage(Message message) {
        if (!hasReceived(message)) {
            if (message.getType() == Message.Type.ACCEPTED) {
                receiveAccepted(message.getProposalId(), message.getValue(), findNodeById(message.getSenderId()));
            }
        }
    }

    /**
     * Finds a node by its ID from the list of peers.
     *
     * @param nodeId The ID of the node to find.
     * @return The node with the specified ID, or null if not found.
     */
    private Node findNodeById(int nodeId) {
        for (Node node : peers) {
            if (node.nodeId == nodeId) {
                return node;
            }
        }
        return null;
    }
}
