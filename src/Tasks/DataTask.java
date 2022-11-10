package Tasks;

import Application.Data;
import java.util.Collections;

/**
 * Concrete task class that sorts a Data set of integers in ascending and descending order
 * @author Tamati Rudd 18045626
 */
public class DataTask<E, F> extends Task {
    private Data clientData;
    
    /**
     * Construct a DataTask
     * @param params Data object to sort
     */
    public DataTask(E params) {
        super(params);
        clientData = (Data) params;
    }
    
    /**
     * Perform the task
     */
    @Override
    public void run() {
        System.out.println("Task "+this.getId()+" Started at "+System.currentTimeMillis());
        //Sort data: ascending (smallest to largest)
        Data ascending = clientData;
        Collections.sort(ascending.getData(), Collections.reverseOrder());
        notifyAll(ascending);
        //Sort data: descending (largest to smallest)
        Data descending = clientData;
        Collections.sort(descending.getData());
        notifyAll(descending);
        System.out.println("Task "+this.getId()+" Finished at "+System.currentTimeMillis());
        notifyAll("Finished");
    }    
}
