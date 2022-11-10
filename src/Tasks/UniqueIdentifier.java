package Tasks;

/**
 * Utility class that uses the singleton pattern to assign unique IDs to Tasks
 * @author Tamati Rudd 18045626
 */
public class UniqueIdentifier {
    private static UniqueIdentifier instance;
    private int nextId;
    
    /**
     * Construct the UniqueIdentifier singleton class
     */
    private UniqueIdentifier() {
        nextId = 1;
    }
    
    /**
     * Get the unique instance of UniqueIdentifer, or create it if it doesn't exist
     * Access to create a new UniqueIdentifier instance is synchronized to enforce the singleton pattern
     * @return the singleton UniqueIdentifer instance
     */
    public static UniqueIdentifier getInstance() {
        if (instance == null) {
            synchronized(UniqueIdentifier.class) {
                System.out.println("Creating singleton");
                instance = new UniqueIdentifier();
            }
        }
        return instance;
    }
    
    
    /**
     * Assign (return) a unique ID to a Task
     * @return a unique ID
     */
    public int assignId() {
        int id;
        synchronized(UniqueIdentifier.class) {
            id = nextId;
            nextId++;
        }
        return id;
    }
}
