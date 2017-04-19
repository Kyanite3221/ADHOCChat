package TCPLayer;

import java.util.LinkedList;

/**
 * Created by freem on 4/8/2017.
 */
public class AcknowledgementStrategy {

    private LinkedList<Integer> acks;
    private LinkedList<Integer> previousAcks;
    private boolean doubleSeq;
    public boolean trippleSeq;

    public AcknowledgementStrategy(){
        acks = new LinkedList<>();
        previousAcks = new LinkedList<>();
        for (int i = 0; i < 10; i++) {
            previousAcks.add(0);
        }
        acks.add(0);
        doubleSeq = false;
        trippleSeq = false;
    }


    public int nextAck(){
        int toReturn = acks.getFirst();
        if (acks.size() > 1){
            previousAcks.add(acks.removeFirst());
            previousAcks.removeFirst();
        } else {
            acks.add(0);
            previousAcks.add(acks.removeFirst());
            previousAcks.removeFirst();
        }
        doubleSeq = false;
        trippleSeq = false;
        return toReturn;
    }

    public void recievedMSG(int seq){
        if (acks.contains(seq) && doubleSeq){
            trippleSeq = true;
            doubleSeq = false;
        } else if (acks.contains(seq)){
            doubleSeq = true;
        }
        if (acks.size() == 1 && acks.contains(0)){
            acks.clear();
        }
        acks.add(seq);
    }

    public boolean moreToAck(){
        return (acks.size()>1 || (acks.size() == 1 && !acks.contains(0)));
    }

    public boolean hasNotBeenRecievedBefore(int seq){
        return (!(acks.contains(seq)&&previousAcks.contains(seq)));
    }
}
