package TCPLayer;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by freem on 4/7/2017.
 */
public class TCPLayer {

    private static final int TIMEOUT = 1000;
    private static final int MAX_PAYLOAD_SIZE = 230;

    SequenceStrategy sequenceGetter;
    AcknowledgementStrategy ackGetter;

    LinkedList<TCPMessage> sendingQueue;
    HashMap<TCPMessage, Integer> waitingForAck;
    TCPMessage tableMessage;
    LinkedList<byte[]> messageData;


    //Richard's class, waaraan ik de messageData moet geven

    public TCPLayer(){
        sequenceGetter = new StopAndWait();
        ackGetter = new AcknowledgementStrategy();
        sendingQueue = new LinkedList<TCPMessage>();
        waitingForAck = new HashMap<TCPMessage, Integer>();

    }


    public void createDataMessage (byte[] data){ //works with any amount of messageData.
        int chunckCounter = 0;
        byte[] chunck = new byte[MAX_PAYLOAD_SIZE];
        while ((chunckCounter+1)*MAX_PAYLOAD_SIZE < data.length){
            for (int i = 0; i < MAX_PAYLOAD_SIZE; i++) {
                chunck[i] = data[(chunckCounter*MAX_PAYLOAD_SIZE)+i];
            }

            this.messageData.add(chunck);
            chunckCounter++;
        }

        chunck = new byte[data.length%MAX_PAYLOAD_SIZE];
        for (int i = 0; i+(chunckCounter*MAX_PAYLOAD_SIZE) < data.length; i++) {
            chunck[i] = data[(chunckCounter*MAX_PAYLOAD_SIZE) + i];
        }

        this.messageData.add(chunck);

    }

    /**
     * Creates a new TCPMessage which will be sent with priority in the next tick.
     * @param tableInformation the messageData which is to be sent.
     */
    public void createPingMessage(byte[] tableInformation){ //works with any amount of messageData, but the tables shoudln't
                                                        // be too large, as it will all be fitted into a single packet
        tableMessage = new TCPMessage( 0,0,0, hashData(tableInformation), (byte)0x00, (byte)0x08, tableInformation);
    }

    /**
     * creates a new TCP message which is then queued for sending
     * @param data the payload of the package.
     */
    public void createTCPMessage() {//works with the size limit of the messageData
        while (sequenceGetter.hasNextAvailible()){
            byte[] toCreate = messageData.removeFirst();
            TCPMessage msg = new TCPMessage(sequenceGetter.getNextSeqNumber(), ackGetter.nextAck(), System.currentTimeMillis(), hashData(toCreate), (byte)0x02, (byte)0x02, toCreate);
            sendingQueue.add(msg);
        }
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
        return null;
    }

    public byte[] recievedMessage(byte[] networkInfo){
        TCPMessage recieved = new TCPMessage(networkInfo);

        return null;
    }

    public int hashData(byte[] data){
        return data[0]*17;
    }//to be properly implemented
}
