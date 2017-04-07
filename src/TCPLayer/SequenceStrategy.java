package TCPLayer;

/**
 * Created by freem on 4/7/2017.
 */
public interface SequenceStrategy {
    public int getNextSeqNumber();
    public void recieveAck(int ackNumber);
    public boolean hasNextAvailible();
    public void reset();
}
