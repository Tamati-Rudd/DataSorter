package Application;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

/**
 * Utility class encapsulating generated data to be passed to the TCP server, and operated on by Tasks
 * @author Tamati Rudd 18045626
 */
public class Data implements Serializable {
    private ArrayList<Integer> data;
    private int size;
    
    /**
     * Construct a new Data object
     * @param size - the size of the data set
     */
    public Data(int size) {
        this.size = size;
        data = new ArrayList<>();
        generateData(size);
    }

    /**
     * Randomly generate a list of data (range is Java's allowed integer values) of a particular size
     * @param size - the size of the data set to generate
     */
    private void generateData(int size) {
        Random rand = new Random();
        for (int i = 0; i < size; i++) {
            data.add(rand.nextInt(Integer.MIN_VALUE, Integer.MAX_VALUE));
        }
    }
    
    /**
     * Get the data set
     * @return the ArrayList containing the data
     */
    public ArrayList getData() {
        return data;
    }
    
     /**
     * Get the data set size
     * @return the size of the data set
     */
    public int getSize() {
        return size;
    }
    
    /**
     * Build a string representation of the data set for printing
     * @return a string representation of the data
     */
    @Override
    public String toString() {
        String dataDisplay = "Data: [ ";
        
        for (int i = 0; i < size; i++) {
            dataDisplay += data.get(i).toString();
            if (i < size-1)
                dataDisplay += ", ";
        }
        
        dataDisplay += " ]";
        return dataDisplay;
    }
}
