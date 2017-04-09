package TCPLayer;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by freem on 4/7/2017.
 */
public class TCPLayer {

    private static final int TIMEOUT = 1000;
    private static final int MAX_PAYLOAD_SIZE = 230;

    private SequenceStrategy sequenceGetter;
    private AcknowledgementStrategy ackGetter;

    private LinkedList<TCPMessage> sendingQueue;
    private HashMap<TCPMessage, Integer> waitingForAck;
    private HashMap<Integer, TCPMessage> sequenceToTCP;
    private TCPMessage priorityMessage;
    private LinkedList<byte[]> messageData;

    private String name;
    private boolean connectionEstablished;


    //Richard's class, waaraan ik de messageData moet geven

    public TCPLayer(){
        sequenceGetter = new StopAndWait();
        ackGetter = new AcknowledgementStrategy();
        sendingQueue = new LinkedList<TCPMessage>();
        waitingForAck = new HashMap<TCPMessage, Integer>();
        sequenceToTCP = new HashMap<Integer, TCPMessage>();
        priorityMessage = null;
        messageData =  new LinkedList<>();
        name = "no Name";
        connectionEstablished = false;

    }

    public TCPLayer(String name){
        sequenceGetter = new StopAndWait();
        ackGetter = new AcknowledgementStrategy();
        sendingQueue = new LinkedList<TCPMessage>();
        waitingForAck = new HashMap<TCPMessage, Integer>();
        sequenceToTCP = new HashMap<Integer, TCPMessage>();
        priorityMessage = null;
        messageData =  new LinkedList<>();
        this.name = name;
        connectionEstablished = false;

    }


    public void createDataMessage (byte[] data){ //works with any amount of messageData.
        int chunckCounter = 0;
        byte[] chunck = new byte[MAX_PAYLOAD_SIZE];
        while ((chunckCounter+1)*MAX_PAYLOAD_SIZE <= data.length){
            for (int i = 0; i < MAX_PAYLOAD_SIZE; i++) {
                chunck[i] = data[(chunckCounter*MAX_PAYLOAD_SIZE)+i];
            }
            //System.arraycopy(data, chunckCounter*MAX_PAYLOAD_SIZE, chunck, 0, MAX_PAYLOAD_SIZE);

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
        priorityMessage = new TCPMessage( 0,0,0, hashData(tableInformation), (byte)0x00, (byte)0x08, tableInformation);
    }

    /**
     * creates a new TCP message from the data that is currently stored which is then queued for sending
     */
    public void createTCPMessage() {//works with the size limit of the messageData
        while (sequenceGetter.hasNextAvailible() && messageData.size() > 0){
            byte[] toCreate = messageData.removeFirst();
            TCPMessage msg = new TCPMessage(sequenceGetter.getNextSeqNumber(toCreate.length), ackGetter.nextAck(), System.currentTimeMillis(), hashData(toCreate), (byte)0x02, (byte)0x02, toCreate);
            sendingQueue.add(msg);
        }
    }

    public LinkedList<TCPMessage> tick(){

        LinkedList<TCPMessage> toReturn = new LinkedList<TCPMessage>();

        createTCPMessage();

        if (priorityMessage != null){
            toReturn.add(priorityMessage);
            System.out.println("!!!! SENDING PRIORITY MESSAGE !!!!!");
            priorityMessage = null;
            return toReturn;
        }

        for (TCPMessage toSend : sendingQueue) {
           toReturn.add(toSend);
           sequenceToTCP.put(toSend.getSequenceNumber(),toSend);
           waitingForAck.put(toSend, TIMEOUT);
        }
        sendingQueue.clear();

        for (TCPMessage keys : waitingForAck.keySet()) {
           int timeOut;
            timeOut = waitingForAck.get(keys);
            if (timeOut == 0) {
                toReturn.add(keys);
                timeOut = TIMEOUT + 1;
            }
            waitingForAck.put(keys, timeOut-1);

        }
        if (toReturn.size() == 0 && (ackGetter.moreToAck() || ackGetter.trippleSeq)){
            ackGetter.trippleSeq = false;
            priorityMessage = new TCPMessage(0,ackGetter.nextAck(),System.currentTimeMillis(),0,(byte)0x06,(byte)0x02,null);
        }
        return toReturn;
    }

    public TCPMessage recievedMessage(byte[] networkInfo){
        TCPMessage received = new TCPMessage(networkInfo);//data is converted into a sensible data format.

        if (sequenceToTCP.containsKey(received.getAcknowledgeNumber())){ //if the acknowledgement number is one for a message that we sent,
                                                                        //remove it from the waiting list and make sure that
                                                                       // the sequence getter can continue.
            System.out.println("Key was contained...??????????????\n\n");
            waitingForAck.remove(sequenceToTCP.get(received.getAcknowledgeNumber()));
            sequenceGetter.recieveAck(received.getAcknowledgeNumber());
        }
        // make sure that we ack the next message if it was a regular message to be Ack'ed
        if (received.getPort()==2 || received.getPort()==3){
            ackGetter.recievedMSG(received.getSequenceNumber());
            System.out.println("Data message recieved.\n\n");
        } else if (received.getPort() == 1){ // if it is a connection message, we must also reply appropriately
            if (received.getFlags()== 1){ //SYN
                priorityMessage = new TCPMessage(0,0,System.currentTimeMillis(),0,(byte)0x01,(byte)0x03,null);
            } else if (received.getFlags() == 3){ //SYN/ACK
                priorityMessage = new TCPMessage(0,0,System.currentTimeMillis(),0,(byte)0x01,(byte)0x02,null);
                connectionEstablished = true;
            }
        } else if (received.getPort() == 6){//Ack-only message
            sequenceGetter.recieveAck(received.getAcknowledgeNumber());
        }
        return received;
    }

    public int hashData(byte[] data){
        return data[0]*17;
    }//to be properly implemented
}
