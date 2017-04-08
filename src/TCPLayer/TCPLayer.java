package TCPLayer;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by freem on 4/7/2017.
 */
public class TCPLayer {

    private static final int TIMEOUT = 1000
    SequenceStrategy SequenceGetter;
    LinkedList<TCPMessage> sendingQueue;
    HashMap<TCPMessage, Integer> waitingForAck;
    LinkedList<byte[]> data;
    //Richard's class, waaraan ik de data moet geven

    public TCPLayer(){

    }
    public byte[] createDataMessage (byte[] data){

        return null;
    }

    public byte[] createPingMessage(){

        return null;
    }

    public void createTCPMessage() {

    }

    public LinkedList<TCPMessage> tick(){
        //Method if I have direct sending access
//        for (TCPMessage toSend : sendingQueue) {
//            networkLayer.Send(toSend.toByte());
//            waitingForAck.put(toSend, TIMEOUT);
//        }
//        for (TCPMessage keys : waitingForAck.keySet()) {
//            int timeOut;
//            timeOut = waitingForAck.get(keys);
//            if (timeOut == 0) {
//                networkLayer.Send(keys.toByte());
//                timeOut = TIMEOUT + 1;
//            }
//            waitingForAck.put(keys, timeOut-1);
//
//        }
        //Method if I can return a whole LinkedList<TCPMessage>
//        LinkedList<TCPMessage> toReturn = new LinkedList<TCPMessage>();
//        for (TCPMessage toSend : sendingQueue) {
//           toReturn.add(toSend);
//           waitingForAck.put(toSend, TIMEOUT);
//        }
//
//        for (TCPMessage keys : waitingForAck.keySet()) {
//           int timeOut;
//            timeOut = waitingForAck.get(keys);
//            if (timeOut == 0) {
//                toReturn.add(keys);
//                timeOut = TIMEOUT + 1;
//            }
//            waitingForAck.put(keys, timeOut-1);
//
//        }
//        return toReturn;
    }

    public byte[] recievedMessage(byte[] networkInfo){
        TCPMessage recieved = new TCPMessage(networkInfo);

    }
}
