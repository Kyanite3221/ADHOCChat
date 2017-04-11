package TCPLayer;

/**
 * Created by freem on 4/7/2017.
 */
public interface SequenceStrategy {
    public int getNextSeqNumber(int payloadLength);
    public void recieveAck(int ackNumber);
    public boolean hasNextAvailible();
    public int getWindowSize();
    public void reset();
}
