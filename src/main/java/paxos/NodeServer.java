package paxos;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * The NodeServer class handles network communication for Paxos nodes.
 * It listens for incoming connections and processes messages using NodeHandler.
 */
public class NodeServer extends Thread {
    private Node node;
    private int port;
    private ServerSocket serverSocket;
    private boolean running;

    /**
     * Constructs a NodeServer object.
     *
     * @param node The node that will handle incoming messages.
     * @param port The port on which to listen for incoming connections.
     */
    public NodeServer(Node node, int port) {
        this.node = node;
        this.port = port;
        this.running = true;
    }

    /**
     * Runs the server, listening for incoming connections and spawning NodeHandler threads.
     */
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            this.serverSocket = serverSocket;
            while (running) {
                if (!node.isActive()) {
                    continue; // Skip processing if the node is inactive
                }
                Socket socket = serverSocket.accept();
                new NodeHandler(socket, node).start();
            }
        } catch (Exception e) {
            if (running) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Stops the server by closing the server socket.
     */
    public void stopServer() {
        running = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends a message to a specified hostname and port.
     *
     * @param hostname The hostname of the recipient.
     * @param port     The port of the recipient.
     * @param message  The message to send.
     */
    public static void sendMessage(String hostname, int port, Message message) {
        try (Socket socket = new Socket(hostname, port);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {
            out.writeObject(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

/**
 * The NodeHandler class processes incoming messages for a node.
 */
class NodeHandler extends Thread {
    private Socket socket;
    private Node node;

    /**
     * Constructs a NodeHandler object.
     *
     * @param socket The socket connected to the sender.
     * @param node   The node that will handle the message.
     */
    public NodeHandler(Socket socket, Node node) {
        this.socket = socket;
        this.node = node;
    }

    /**
     * Runs the handler, reading a message from the socket and passing it to the node.
     */
    public void run() {
        try (ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
            Message message = (Message) in.readObject();
            node.handleMessage(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
