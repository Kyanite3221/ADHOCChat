package TCPLayer;

import java.util.LinkedList;

/**
 * Created by freem on 4/8/2017.
 */
public class AcknowledgementStrategy {

    LinkedList<Integer> acks;

    public AcknowledgementStrategy(){
        acks = new LinkedList<>();
        acks.add(1);
    }


    public int nextAck(){
        int toReturn = acks.getFirst();
        if (acks.size() > 1){
            acks.removeFirst();
        }
        return toReturn;
    }

    public void recievedMSG(int seq){
        acks.add(seq);
    }
}
