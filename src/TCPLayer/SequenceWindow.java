package TCPLayer;

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
    public int getNextSeqNumber(int payloadLength) {//simply adds the given number to the list of numbers
        outstanding.add(nextToSendValue); //and returns it. this function must always be called after a call to hasNextAvailible()
        nextToSendValue++;
        return nextToSendValue-1;

    }

    @Override
    public void recieveAck(int ackNumber) {
        if (outstanding.size() < 1 ){//we get an ack that has no meaning, as we have no outstanding messages
            return;
        }

        if (ackNumber == outstanding.getFirst()) {//if the ack we recieved was the first that we sent,
            outstanding.removeFirst();              //we want to remove it from the outstanding list, to clear up room for others.
            while (outstanding.size() > 0 && ached.contains(outstanding.getFirst())){ //if the next outstanding has already been acked
                ached.remove(outstanding.getFirst()); // we also want to clear that one up
                outstanding.removeFirst();
            }
        } else { // if it wasn't the first, we want to save the fact that it has been ack'ed
            ached.add(ackNumber);
        }
    }

    @Override
    public boolean hasNextAvailible() {
        return outstanding.size() < maxSize;
    }

    @Override@Deprecated
    public void reset() {
        maxSize = 10;
        nextToSendValue = 42;
        outstanding = new LinkedList<>();
        ached = new HashSet<>();
    }
}
