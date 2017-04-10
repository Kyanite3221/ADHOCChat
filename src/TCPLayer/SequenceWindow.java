package TCPLayer;

import sun.awt.image.ImageWatched;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by freem on 4/10/2017.
 */
public class SequenceWindow implements SequenceStrategy{

        int maxSize;
        int nextToSendValue;
        LinkedList<Integer> outstanding;
        HashSet<Integer> ached;

    public SequenceWindow(int size){
        maxSize = size;
        nextToSendValue = 42;
        outstanding = new LinkedList<>();
        ached = new HashSet<>();
    }

    public SequenceWindow(int size, int initialValue){
        maxSize = size;
        nextToSendValue = initialValue;
        outstanding = new LinkedList<>();
        ached = new HashSet<>();
    }

    @Override
    public int getNextSeqNumber(int payloadLength) {
        nextToSendValue++;
        return nextToSendValue-1;

    }

    @Override
    public void recieveAck(int ackNumber) {

    }

    @Override
    public boolean hasNextAvailible() {
        return outstanding.size() < maxSize;
    }

    @Override
    public void reset() {

    }
}
