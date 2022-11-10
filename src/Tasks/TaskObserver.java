package Tasks;

/**
 * Interface that uses the observer pattern to receive updates on the progress of a Task
 * @author Tamati Rudd 18045626
 */
public interface TaskObserver<F> {
    public void process(F progress);
}
