package Application;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

/**
 * TCP client for the DataSorter application
 * Allows the user to specify the size of randomly generated Data sets, sends them to the server for sorting, and listens for server responses
 * @author Tamati Rudd 18045626
 */
public class TcpClient implements Runnable {
    public static final String HOST_NAME = "localhost"; //Server host
    public static final int HOST_PORT = 4000; //Server port 
    private boolean stopRequested = false;
    private Socket clientSocket;
    private Scanner scanner;

    /**
     * Construct a new TCP client
     */
    public TcpClient() {
        scanner = new Scanner(System.in);
    }

    /**
     * Start the client & connect to the server
     */
    public void startClient() {
        try {
            clientSocket = new Socket(HOST_NAME, HOST_PORT);
            ResponseListener receiver = new ResponseListener();
            Thread userInputThread = new Thread(this);
            userInputThread.start();
            Thread ListenerThread = new Thread(receiver);
            ListenerThread.start();
        } catch (IOException ex) {
            System.err.println("Client failed to start: " + ex);
            System.exit(-1);
        }
    }

    /**
     * Request that the client be shut down
     */
    public void requestStop() {
        stopRequested = true;
        try {
            clientSocket.close();
        } catch (IOException ex) {
            System.err.println("Error closing client socket: "+ex);
        }
    }

    /**
     * Entry point - create and start a new TCP client
     * @param args - the command line arguments
     */
    public static void main(String[] args) {
        TcpClient client = new TcpClient();
        client.startClient();
    }

    /**
     * While running, Generate data sets of sizes determined by user input, and send them to the server for sorting
     */
    @Override
    public void run() {
        ObjectOutputStream output;
        try {
            output = new ObjectOutputStream(clientSocket.getOutputStream());
            while (!stopRequested) {
                System.out.println("Data Sorter> Enter size of the data set to generate (must be positive)");
                System.out.print("Data Sorter> ");
                if (scanner.hasNextInt()) {
                    int size = scanner.nextInt();
                    if (size > 0) {
                        Data data = new Data(size);
                        System.out.println("Data Sorter> Sending generated data set of size "+size+" to server");
                        output.writeObject(data);
                    } else
                        System.out.println("Data Sorter> Invalid input: please enter a positive integer");
                } else {
                    String input = scanner.nextLine();
                    if (input.equalsIgnoreCase("stop"))
                        requestStop();
                    else
                        System.out.println("Data Sorter> Invalid input: please enter a positive integer");
                }
            }
        } catch (IOException ex) {
            System.err.println("Error reading user input: " + ex);
        }
    }

    /**
     * Inner class to listen for responses containing sorted Data from the TCP server
     */
    private class ResponseListener implements Runnable {
        /**
         * Construct a ResponseListener
         */
        private ResponseListener() {}

        /**
         * While running, listen for responses containing sorted Data from the server on loop
         */
        @Override
        public void run() {
            ObjectInputStream input;
            try {
                input = new ObjectInputStream(clientSocket.getInputStream());
                while (!stopRequested) {
                    Data responseData = (Data) input.readObject();
                    if (responseData != null) {
                        System.out.println("Received Sorted Data: "+responseData.getData());
                        System.out.print("Data Sorter> ");
                    }
                }
            } catch (IOException | ClassNotFoundException ex) {
                System.err.println("Error while listening for server responses "+ex);
            }
        }
    }
}
