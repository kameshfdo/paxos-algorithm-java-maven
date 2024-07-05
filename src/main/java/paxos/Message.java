package paxos;

import java.io.Serializable;
/**
 * The Message class represents the messages exchanged between nodes in the Paxos algorithm.
 */
public class Message implements Serializable {
    /**
     * Enum representing the types of messages in the Paxos protocol.
     */
    public enum Type {
        PREPARE, PROMISE, ACCEPT_REQUEST, ACCEPTED
    }

    private Type type;
    private int proposalId;
    private int value;
    private int senderId;


    /**
     * Constructs a Message object.
     *
     * @param type       The type of the message.
     * @param proposalId The ID of the proposal.
     * @param value      The value proposed.
     * @param senderId   The ID of the sender.
     */
    public Message(Type type, int proposalId, int value, int senderId) {
        this.type = type;
        this.proposalId = proposalId;
        this.value = value;
        this.senderId = senderId;
    }

    public Type getType() {
        return type;
    }

    public int getProposalId() {
        return proposalId;
    }

    public int getValue() {
        return value;
    }

    public int getSenderId() {
        return senderId;
    }
}
