package Tasks;

import Application.Data;
import Application.TcpServer.Connection;

/**
 * Concrete class that uses the observer pattern to receive progress updates from a DataTask
 * @author Tamati Rudd 18045626
 */
public class DataTaskObserver implements TaskObserver {
    private Connection connection;
    
    /**
     * Construct a DataTaskObserver
     * @param connection the connection object between TCP client & server that is creating this observer
     */
    public DataTaskObserver(Connection connection) {
        this.connection = connection;
    }
    
    /**
     * Respond to a progress update from a DataTask - print the data on the server & return it to the client
     * @param progress the sorted data passed with the progress update
     */
    @Override
    public void process(Object progress) {
        if (progress instanceof Data)
            System.out.println("DataTaskObserver: Sorted "+progress.toString());
        connection.returnSortedData(progress);
    }
}
