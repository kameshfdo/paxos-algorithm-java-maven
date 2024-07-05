package paxos;
import java.util.List;
/**
 * Acceptor class in the Paxos protocol, responsible for handling proposals.
 */
public class Acceptor extends Node {

    public Acceptor(int nodeId, List<Node> peers, String hostname, int port) {
        super(nodeId, peers, hostname, port);
    }

    /**
     * Handles a prepare message from a proposer.
     *
     * @param proposalId The ID of the proposal.
     * @param proposer   The proposer node.
     */
    public void receivePrepare(int proposalId, Proposer proposer) {
        if (proposalId >= promisedId) {
            promisedId = proposalId;
            NodeServer.sendMessage(proposer.hostname, proposer.port, new Message(Message.Type.PROMISE, proposalId, acceptedValue, nodeId));
        }
    }

    /**
     * Handles an accept request from a proposer.
     *
     * @param proposalId The ID of the proposal.
     * @param value      The value proposed to be accepted.
     * @param proposer   The proposer node.
     */
    public void receiveAcceptRequest(int proposalId, int value, Node proposer) {
        if (proposalId >= promisedId) {
            promisedId = proposalId;
            earlyAcceptedProposalId = proposalId;
            acceptedValue = value;
            System.out.println("I'm acceptor: " + nodeId + " I'm accepting proposal: " + proposalId + " value is: " + value);
            sendMessageToAll(new Message(Message.Type.ACCEPTED, proposalId, value, nodeId));
        }
    }

    @Override
    public void handleMessage(Message message) {
        if (!hasReceived(message)) {
            switch (message.getType()) {
                case PREPARE:
                    receivePrepare(message.getProposalId(), (Proposer) findNodeById(message.getSenderId()));
                    break;
                case ACCEPT_REQUEST:
                    receiveAcceptRequest(message.getProposalId(), message.getValue(), findNodeById(message.getSenderId()));
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
