package TCPLayer;

/**
 * Created by freem on 4/7/2017.
 */
public class StopAndWait implements SequenceStrategy {

    private int nextNumber;
    private boolean hasNextAvailible;
    private int payloadSize;

    public StopAndWait(){
        nextNumber = 42;
        hasNextAvailible = true;
        payloadSize = 1;
    }

    @Override
    public int getNextSeqNumber(int payloadLength) {
        if (hasNextAvailible) {
            hasNextAvailible=false;
            payloadSize = payloadLength;
            if (payloadSize==0){
                payloadSize=1;
            }
            return nextNumber;
        } else {
            return -42;
        }
    }

    @Override
    public void recieveAck(int ackNumber) {
        if (ackNumber == nextNumber){
            nextNumber += 1;
            hasNextAvailible = true;
        }
    }

    @Override
    public boolean hasNextAvailible() {
        return hasNextAvailible;
    }

    @Override
    public int getWindowSize() {
        return 1;
    }


    @Override
    public void reset() {
        nextNumber = 1;
        hasNextAvailible = true;
    }
}
