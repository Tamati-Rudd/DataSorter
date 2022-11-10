package Application;

import Tasks.DataTask;
import Tasks.DataTaskObserver;
import Tasks.Task;
import Tasks.TaskObserver;
import ThreadPool.ThreadPool;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/**
 * TCP Server for the DataSorter application 
 * Listens for client data, creates Tasks to sort the data in the ThreadPool, and uses TaskObservers to return the results
 * @author Tamati Rudd 18045626
 */
public class TcpServer implements Runnable {
    private boolean stopRequested;
    public static final int PORT = 4000;
    private ThreadPool pool;
    private ServerSocket serverSocket;

    /**
     * Construct a new TCP server
     */
    public TcpServer() {
        stopRequested = false;
        pool = new ThreadPool(10);
    }

    /**
     * Start the server and accept incoming client connections
     */
    public void startServer() {
        stopRequested = false;
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("TCP Server started at " + InetAddress.getLocalHost() + " on port " + PORT);
            Thread serverCommandThread = new Thread(this);
            serverCommandThread.start();
        } catch (IOException ex) { //Also catches INetException        
            System.err.println("Server failed to start on port: " + ex);
            System.exit(-1);
        }
        try {
            while (!stopRequested) { //Listen for incoming client connections
                Socket socket = serverSocket.accept();
                System.out.println("New connection: " + socket.getInetAddress());
                Connection conn = new Connection(socket, this);
                Thread connThread = new Thread(conn);
                connThread.start();
            }
        System.out.println("No longer listening for connections");
        } catch (IOException ex) {}
    }

    /**
     * Listen for input in the server console to manage the ThreadPool
     */
    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        while (!stopRequested) {
            String serverCommand = scanner.nextLine();
            if (serverCommand.equalsIgnoreCase("stop")) {
                requestStop();
            } else if (serverCommand.equalsIgnoreCase("resize up")) {  
                System.out.println("Doubling the size of the thread pool");
                pool.resize(pool.getAvailable()*2);
                System.out.println("Thread pool resized to "+pool.getAvailable());
            } else if (serverCommand.equalsIgnoreCase("resize down")) {
                System.out.println("Halving the size of the thread pool");
                pool.resize(pool.getAvailable()/2);
                System.out.println("Thread pool resized to "+pool.getAvailable());
            } else {
                System.out.println("Unknown command");
            }
        }
    }

    /**
     * Request that the server be shut down & the thread pool closed
     */
    public void requestStop() {
        stopRequested = true;
        pool.destroyPool();

        while (!serverSocket.isClosed()) {
            synchronized (pool.taskQueue) {
                if (pool.taskQueue.isEmpty()) {
                    try {
                        serverSocket.close();
                        System.out.println("Server socket closed");
                    } catch (IOException ex) {
                        System.err.println("Error closing server socket: " + ex);
                    }
                } else {
                    System.out.println("Not ready to close");
                    pool.taskQueue.notifyAll();
                }
            }
        }
    }

    /**
     * Entry point - create and start a new TCP server
     *
     * @param args - the command line arguments
     */
    public static void main(String[] args) {
        TcpServer server = new TcpServer();
        server.startServer();
    }

    /**
     * Inner class to represent a connection with a TCP client
     */
    public class Connection implements Runnable {
        private Socket clientConnection;
        private ObjectOutputStream output;

        /**
         * Construct a new Connection object
         *
         * @param clientConnection Socket connection with the client
         */
        private Connection(Socket clientConnection, TcpServer serverConnection) {
            this.clientConnection = clientConnection;
            try {
                output = new ObjectOutputStream(clientConnection.getOutputStream());
            } catch (IOException ex) {
                System.err.println("Error creating output stream " + ex);
            }
        }

        /**
         * Listen for incoming data from the client Upon receiving data, create
         * a Task, TaskObserver, and run the Task in the server ThreadPool
         */
        @Override
        public void run() {
            ObjectInputStream input;
            try {
                input = new ObjectInputStream(clientConnection.getInputStream());
                while (!stopRequested) { //Listen for client data
                    Data clientData = (Data) input.readObject();
                    System.out.println("Received client data of size " + clientData.getSize());
                    System.out.println(clientData.toString());
                    //Create a Task, TaskObserver & add the Task to the ThreadPool queue
                    Task sortTask = new DataTask(clientData);
                    TaskObserver observer = new DataTaskObserver(this);
                    sortTask.addListener(observer);
                    pool.perform(sortTask);
                }
            } catch (IOException | ClassNotFoundException ex) {}
        }

        /**
         * Return sorted data to the client (who originally sent in the data)
         *
         * @param sortedData Data that has been sorted
         */
        public void returnSortedData(Object sortedData) {
            try {
                if (sortedData instanceof Data)
                    output.writeObject(sortedData);
                else if (sortedData instanceof String && stopRequested) //Stop client socket if Task is finished and server is to stop
                    clientConnection.close();
            } catch (IOException ex) {
                System.err.println("Error returning sorted data: " + ex);
            }
        }
    }
}
