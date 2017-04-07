package TCPLayer;

/**
 * Created by freem on 4/7/2017.
 */
public class StopAndWait implements SequenceStrategy {

    private int nextNumber;
    private boolean hasNextAvailible;

    public StopAndWait(){
        nextNumber = 1;
        hasNextAvailible = true;
    }

    @Override
    public int getNextSeqNumber() {
        if (hasNextAvailible) {
            return nextNumber;
        } else {
            return -42;
        }
    }

    @Override
    public void recieveAck(int ackNumber) {
        if (ackNumber == nextNumber){
            nextNumber++;
            hasNextAvailible = true;
        }
    }

    @Override
    public boolean hasNextAvailible() {
        return hasNextAvailible;
    }

    @Override
    public void reset() {
        nextNumber = 1;
        hasNextAvailible = true;
    }
}
