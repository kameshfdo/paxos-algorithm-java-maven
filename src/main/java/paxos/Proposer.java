package paxos;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Proposer class in the Paxos protocol, responsible for initiating proposals.
 */
public class Proposer extends Node {

    private int proposalId;
    private int proposedValue;
    private int majority;
    private Map<Node, Integer> promises = new HashMap<>();
    private Map<Node, Integer> acceptedValues = new HashMap<>();
    private boolean acceptSent = false;

    public Proposer(int nodeId, List<Node> peers, String hostname, int port) {
        super(nodeId, peers, hostname, port);
        this.majority = (peers.size() / 2) + 1;
    }

    private int generateProposalId() {
        return (int) (Math.random() * 1000);
    }

    /**
     * Initiates a new proposal with the specified value.
     *
     * @param value The value to propose.
     */
    public void propose(int value) {
        this.proposalId = generateProposalId();
        this.proposedValue = value;
        System.out.println("I'm proposer, my id: " + nodeId);
        System.out.println("In the propose | proposal id is: " + proposalId);
        sendMessageToAll(new Message(Message.Type.PREPARE, proposalId, value, nodeId));
    }

    /**
     * Processes the promise messages from acceptors.
     *
     * @param acceptor     The acceptor node that sent the promise.
     * @param proposalId   The ID of the proposal for which the promise was made.
     * @param acceptedId   The ID of the previously accepted proposal (if any).
     * @param acceptedValue The value of the previously accepted proposal (if any).
     */
    public void receivePromise(Acceptor acceptor, int proposalId, int acceptedId, int acceptedValue) {
        if (proposalId == this.proposalId) {
            promises.put(acceptor, acceptedId);
            if (acceptedId != -1) {
                acceptedValues.put(acceptor, acceptedValue);
            }

            if (promises.size() >= majority && !acceptSent) {
                acceptSent = true;
                int maxAcceptedId = this.proposalId;
                int maxAcceptedValue = this.proposedValue;

                for (Map.Entry<Node, Integer> entry : acceptedValues.entrySet()) {
                    if (entry.getKey().earlyAcceptedProposalId > maxAcceptedId) {
                        maxAcceptedId = entry.getKey().earlyAcceptedProposalId;
                        maxAcceptedValue = entry.getValue();
                    }
                }

                sendMessageToAll(new Message(Message.Type.ACCEPT_REQUEST, maxAcceptedId, maxAcceptedValue, nodeId));
            }
        }
    }

    @Override
    public void handleMessage(Message message) {
        if (!hasReceived(message)) {
            switch (message.getType()) {
                case PROMISE:
                    receivePromise((Acceptor) findNodeById(message.getSenderId()), message.getProposalId(), message.getProposalId(), message.getValue());
                    break;
                default:
                    break;
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
