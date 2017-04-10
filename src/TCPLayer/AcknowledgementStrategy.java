package TCPLayer;

import java.util.LinkedList;

/**
 * Created by freem on 4/8/2017.
 */
public class AcknowledgementStrategy {

    private LinkedList<Integer> acks;
    private boolean doubleSeq;
    public boolean trippleSeq;

    public AcknowledgementStrategy(){
        acks = new LinkedList<>();
        acks.add(0);
        doubleSeq = false;
        trippleSeq = false;
    }


    public int nextAck(){
        int toReturn = acks.getFirst();
        if (acks.size() > 1){
            acks.removeFirst();
        } else {
            acks.add(0);
            acks.removeFirst();
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
}
