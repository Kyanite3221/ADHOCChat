package TCPLayer;

import com.sun.istack.internal.NotNull;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by freem on 4/7/2017.
 */
public class TCPStream {

    private static final int TIMEOUT = 10;
    private static final int MAX_PAYLOAD_SIZE = 900;// one less than the actual value you want

    public static final byte TCP_CONNECTION_INFORMATION_PORT = 0x01;
    public static final byte MESSAGE_DELIVERY_PORT = 0x02;
    public static final byte FILE_DELIVERY_PORT = 0x03;
    public static final byte ACK_ONLY_PORT = 0x06;

    public static final byte FIRST_AND_MORE_DATA_TO_COME = 0x00;
    public static final byte MORE_DATA_TO_COME = 0x01;
    public static final byte NO_MORE_DATA_TO_COME = 0x02;
    public static final byte SELF_CONTAINED_DATA_MESSAGE = 0x03;


    public static final byte SYN_FLAG = 0x01;
    public static final byte ACK_FLAG = 0x02;
    public static final byte SYN_ACK_FLAG = 0x03;
    public static final byte FIN_FLAG = 0x04;
    public static final byte FIN_ACK_FLAG = 0x06;
    public static final byte ROUTING_FLAG = 0x08;

    private SequenceStrategy sequenceGetter;
    private AcknowledgementStrategy ackGetter;

    private LinkedList<TCPMessage> sendingQueue;
    private HashMap<TCPMessage, Integer> waitingForAck;
    private HashMap<Integer, TCPMessage> sequenceToTCP;
    private TCPMessage priorityMessage;
    private LinkedList<byte[]> messageData;
    private LinkedList<byte[]> fileData;

    private String name;
    private boolean connectionEstablished;
    public boolean connectionTerminated;
    private int connectionTimeOut;

    private LinkedList<byte[]> recievedMessageData;
    private LinkedList<byte[]> recievedFileData;
    private HashMap<Integer, TCPMessage> misplacedData;
    private int lastSequenceProcessed;

    //Richard's class, waaraan ik de messageData moet geven

    public TCPStream(){
        sequenceGetter = new SequenceWindow(5,42);
        ackGetter = new AcknowledgementStrategy();
        sendingQueue = new LinkedList<>();
        waitingForAck = new HashMap<>();
        sequenceToTCP = new HashMap<>();
        recievedMessageData = new LinkedList<>();
        recievedFileData = new LinkedList<>();
        misplacedData = new HashMap<>();

        priorityMessage = null;
        messageData =  new LinkedList<>();
        fileData = new LinkedList<>();
        name = "no Name";
        connectionEstablished = false;
        connectionTimeOut = TIMEOUT;
        connectionTerminated = false;


    }

    public TCPStream(String name){
        sequenceGetter = new SequenceWindow(5,42);
        ackGetter = new AcknowledgementStrategy();
        sendingQueue = new LinkedList<>();
        waitingForAck = new HashMap<>();
        sequenceToTCP = new HashMap<>();
        recievedMessageData = new LinkedList<>();
        recievedFileData = new LinkedList<>();
        misplacedData = new HashMap<>();

        priorityMessage = null;
        messageData =  new LinkedList<>();
        fileData = new LinkedList<>();
        connectionEstablished = false;
        connectionTimeOut = TIMEOUT;
        this.name = name;

        if (name.equals("NO SETUP")){
            connectionEstablished = true;
        }
        connectionTerminated = false;
    }

    public TCPStream(String name, int sequenceWindowSize){
        sequenceGetter = new SequenceWindow(sequenceWindowSize, 42);
        ackGetter = new AcknowledgementStrategy();
        sendingQueue = new LinkedList<>();
        waitingForAck = new HashMap<>();
        sequenceToTCP = new HashMap<>();
        recievedMessageData = new LinkedList<>();
        recievedFileData = new LinkedList<>();
        misplacedData = new HashMap<>();

        priorityMessage = null;
        messageData =  new LinkedList<>();
        fileData = new LinkedList<>();
        connectionEstablished = false;
        connectionTimeOut = TIMEOUT;
        this.name = name;

        if (name.equals("NO SETUP")){
            connectionEstablished = true;
        }
        connectionTerminated = false;
    }



    public void createMessageData(@NotNull byte[] data ){ //works with any amount of messageData.
        /**
         * determine the first data chunck and add the apropriate data marks
         */
        if(data.length <= MAX_PAYLOAD_SIZE){
            byte[] chunck = new byte[data.length+1];
            System.arraycopy(data, 0, chunck, 1, data.length);
            chunck[0] = SELF_CONTAINED_DATA_MESSAGE;
            this.messageData.add(chunck);
            return;
        } else {
            byte[] chunck = new byte[MAX_PAYLOAD_SIZE+1];
            chunck[0] = FIRST_AND_MORE_DATA_TO_COME;
            System.arraycopy(data, 0, chunck, 1, MAX_PAYLOAD_SIZE);
            this.messageData.add(chunck);
        }

        /**
         * determines the next whole chunck(s) of data and adds the MORE_DATA_TO_COME marks
         */
        int chunckCounter = 1;
        byte[] chunck = new byte[MAX_PAYLOAD_SIZE+1];//because we want to add flags for the end of data or if there is no more data.
        while ((chunckCounter+1)*MAX_PAYLOAD_SIZE <= data.length){
            chunck[0] = MORE_DATA_TO_COME;
            for (int i = 0; i < MAX_PAYLOAD_SIZE; i++) {
                chunck[i+1] = data[(chunckCounter*MAX_PAYLOAD_SIZE)+i];
            }

            this.messageData.add(chunck);
            chunckCounter++;
        }

        /**
         * if there is no final data left to add the EOF mark, it must be inserted into the last one
         */
        if (data.length%(MAX_PAYLOAD_SIZE+1)==0){//if there is no "leftover message"
            byte[] repairs = this.messageData.removeLast();
            repairs[0] = NO_MORE_DATA_TO_COME;
            this.messageData.addLast(repairs);
        }

        chunck = new byte[1+(data.length%MAX_PAYLOAD_SIZE)];
        chunck[0] = NO_MORE_DATA_TO_COME;
        for (int i = 0; i+(chunckCounter*MAX_PAYLOAD_SIZE) < data.length; i++) {
            chunck[i+1] = data[(chunckCounter*MAX_PAYLOAD_SIZE) + i];
        }

        this.messageData.add(chunck);

    }

    /**
     * Does exactly the same as createMessageData, but purposed for files. this is done so that you could still send
     * text messages while transfering a large file.
     * Recieves an array of bytes, makes it to be an apropriate size and queue them for being made into full TCP messages
     *
     * @param data the byte[] of data that is to be sent
     */
    public void createFileData(@NotNull byte[] data ){ //works with any amount of messageData.
        /**
         * determine the first data chunck and add the apropriate data marks
         */
        if(data.length <= MAX_PAYLOAD_SIZE){
            byte[] chunck = new byte[data.length+1];
            System.arraycopy(data, 0, chunck, 1, data.length);
            chunck[0] = SELF_CONTAINED_DATA_MESSAGE;
            this.fileData.add(chunck);
            return;
        } else {
            byte[] chunck = new byte[MAX_PAYLOAD_SIZE+1];
            chunck[0] = FIRST_AND_MORE_DATA_TO_COME;
            System.arraycopy(data, 0, chunck, 1, MAX_PAYLOAD_SIZE);
            this.fileData.add(chunck);
        }

        /**
         * determines the next whole chunck(s) of data and adds the MORE_DATA_TO_COME marks
         */
        int chunckCounter = 1;
        byte[] chunck = new byte[MAX_PAYLOAD_SIZE+1];//because we want to add flags for the end of data or if there is no more data.
        while ((chunckCounter+1)*MAX_PAYLOAD_SIZE <= data.length){
            chunck[0] = MORE_DATA_TO_COME;
            for (int i = 0; i < MAX_PAYLOAD_SIZE; i++) {
                chunck[i+1] = data[(chunckCounter*MAX_PAYLOAD_SIZE)+i];
            }

            this.fileData.add(chunck);
            chunckCounter++;
        }

        /**
         * if there is no final data left to add the EOF mark, it must be inserted into the last one
         */
        if (data.length%(MAX_PAYLOAD_SIZE+1)==0){//if there is no "leftover message"
            byte[] repairs = this.fileData.removeLast();
            repairs[0] = NO_MORE_DATA_TO_COME;
            this.fileData.addLast(repairs);
        }

        chunck = new byte[1+(data.length%MAX_PAYLOAD_SIZE)];
        chunck[0] = NO_MORE_DATA_TO_COME;
        for (int i = 0; i+(chunckCounter*MAX_PAYLOAD_SIZE) < data.length; i++) {
            chunck[i+1] = data[(chunckCounter*MAX_PAYLOAD_SIZE) + i];
        }

        this.fileData.add(chunck);
    }


    /**
     * Creates a new TCPMessage which will be sent with priority in the next tick.
     * @param tableInformation the messageData which is to be sent.
     */
    public void createPingMessage(byte[] tableInformation){ //works with any amount of messageData, but the tables shoudln't
                                                        // be too large, as it will all be fitted into a single packet
        priorityMessage = new TCPMessage( 0,0,0, hashData(tableInformation), (byte)0x00, ROUTING_FLAG , tableInformation);
    }

    /**
     * creates a new TCP message from the data that is currently stored which is then queued for sending
     */
    public void createTCPMessage() {//works with the size limit of the messageData
        while (sequenceGetter.hasNextAvailible() && messageData.size() > 0){
            byte[] toCreate = messageData.removeFirst();
            TCPMessage msg;
            if (ackGetter.moreToAck()){
                msg = new TCPMessage(sequenceGetter.getNextSeqNumber(toCreate.length), ackGetter.nextAck(), System.currentTimeMillis(), hashData(toCreate), MESSAGE_DELIVERY_PORT, ACK_FLAG, toCreate);
            } else {
                msg = new TCPMessage(sequenceGetter.getNextSeqNumber(toCreate.length), 0, System.currentTimeMillis(), hashData(toCreate), MESSAGE_DELIVERY_PORT, (byte)0, toCreate);
            }
            sendingQueue.add(msg);
        }

        while (sequenceGetter.hasNextAvailible() && fileData.size() > 0){
            byte[] toCreate = fileData.getFirst();
            fileData.removeFirst();
            TCPMessage msgTwo;
            if (ackGetter.moreToAck()){
                msgTwo = new TCPMessage(sequenceGetter.getNextSeqNumber(toCreate.length), ackGetter.nextAck(), System.currentTimeMillis(), hashData(toCreate), FILE_DELIVERY_PORT, ACK_FLAG, toCreate);
            } else {
                msgTwo = new TCPMessage(sequenceGetter.getNextSeqNumber(toCreate.length), 0, System.currentTimeMillis(), hashData(toCreate), FILE_DELIVERY_PORT, (byte)0, toCreate);
            }
            sendingQueue.add(msgTwo);
        }
    }

    /**
     * the function that will be called every iteration of the controler object. It first checks if there are any
     * priority messages to be sent, if there are it returns those.
     * if not, it adds as much data as there are sequenceNumbers availible to the sending list.
     * it also places any regular data that has been sent in a map with the timeout as the value, which it decrements every tick,
     * when that counter reaches 0, it resends the message.
     * finaly, if after that there is nothing to send, it checks if it has to ack anything anyway, if it does, it sends an ack only message.
     * @return a TCPMessage with the data to send, to be slightly processed by the IP-layer and then to be sent
     */
    public LinkedList<TCPMessage> tick(){

        if (!connectionEstablished){
            if (connectionTimeOut < 1){
                connectionTimeOut = TIMEOUT;
                LinkedList<TCPMessage> returnAble = new LinkedList<TCPMessage>();
                returnAble.add(new TCPMessage(0,0,System.currentTimeMillis(), 0, TCP_CONNECTION_INFORMATION_PORT, SYN_FLAG, null));
                return returnAble;
            } else {
                connectionTimeOut--;
                return null;
            }
        }

        LinkedList<TCPMessage> toReturn = new LinkedList<TCPMessage>();

        createTCPMessage();

        if (priorityMessage != null){
            toReturn.add(priorityMessage);
            //System.out.println("!!!! SENDING PRIORITY MESSAGE !!!!!");
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

        while (toReturn.size() < sequenceGetter.getWindowSize() && (ackGetter.moreToAck() || ackGetter.trippleSeq)){
            ackGetter.trippleSeq = false;
            toReturn.add(new TCPMessage(0,ackGetter.nextAck(),System.currentTimeMillis(),0,ACK_ONLY_PORT,ACK_FLAG,null));
        }
        return toReturn;
    }

    /**
     * This function handles incomming messages and replies apropriately if nececairy.
     * @param networkInfo the TCPMessage in byte from that was supplied by the network layer.
     * @return a properly formed TCP message with therein all the relevant data.
     */
    public TCPMessage recievedMessage(@NotNull byte[] networkInfo){
        TCPMessage received = new TCPMessage(networkInfo);//data is converted into a sensible data format.



        if (sequenceToTCP.containsKey(received.getAcknowledgeNumber())){ //if the acknowledgement number is one for a message that we sent,
                                                                        //remove it from the waiting list and make sure that
                                                                       // the sequence getter can continue.
            waitingForAck.remove(sequenceToTCP.get(received.getAcknowledgeNumber()));
            sequenceGetter.recieveAck(received.getAcknowledgeNumber());
        }
        // make sure that we ack the next message if it was a regular message to be Ack'ed
        if (received.getPort() == 1){ // if it is a connection message, we must also reply appropriately
            if (received.getFlags()== SYN_FLAG){ //SYN
                connectionEstablished = true;
                priorityMessage = new TCPMessage(0,0,System.currentTimeMillis(),0, TCP_CONNECTION_INFORMATION_PORT,SYN_ACK_FLAG,null);

            } else if (received.getFlags() == SYN_ACK_FLAG){ //SYN/ACK
                connectionEstablished = true;

                priorityMessage = new TCPMessage(0,0,System.currentTimeMillis(),0, TCP_CONNECTION_INFORMATION_PORT,ACK_FLAG,null);

            } else if (received.getFlags() == FIN_FLAG){
                priorityMessage = new TCPMessage(0, ackGetter.nextAck(), System.currentTimeMillis(), 0, TCP_CONNECTION_INFORMATION_PORT, FIN_ACK_FLAG, null);
                connectionTerminated = true;
            } else if (received.getFlags() == FIN_ACK_FLAG){
                connectionTerminated = true;
            }

        } else if (received.getPort() == 6){//Ack-only message
            sequenceGetter.recieveAck(received.getAcknowledgeNumber());
        }
        int recievedHash = hashData(received.getPayload());
        if (recievedHash != received.getDataHash()){
            System.out.println("\n!!!!WRONG HASH DATA!!!!\n");
            return null;
        } else if (received.getPort()==2&&ackGetter.hasNotBeenRecievedBefore(received.getSequenceNumber())){

            ackGetter.recievedMSG(received.getSequenceNumber());
            System.out.println("Data message recieved.\n\n");

            if (received.getPayload()[0]==SELF_CONTAINED_DATA_MESSAGE){//the message that just came in is not part of a larger amount of data
                return received; //and can thus be forwarded to the controller

            } else if (received.getPayload()[0]==FIRST_AND_MORE_DATA_TO_COME) {//the message that just came in is the first of a larger ammount of data
                recievedMessageData.clear(); //so a record needs to be kept of the order of this data.
                byte[] midStep = new byte[received.getPayload().length-1];
                System.arraycopy(received.getPayload(), 1, midStep, 0, midStep.length);
                misplacedData.clear();
                recievedMessageData.add(midStep);
                lastSequenceProcessed = received.getSequenceNumber();
                return null;

            } else if (received.getPayload()[0] == NO_MORE_DATA_TO_COME){//this is the last packet of a sequence of data
                byte[] midStep = new byte[received.getPayload().length-1];
                System.arraycopy(received.getPayload(), 1, midStep, 0, midStep.length);
                recievedMessageData.add(midStep);
                int finalSize=0;
                for (byte[] b : recievedMessageData) {
                    finalSize += b.length;
                }
                midStep = new byte[finalSize];
                int counter = 0;
                for(byte[] b: recievedMessageData) {
                    for (byte byt: b){
                        midStep[counter] = byt;
                        counter++;
                    }
                }
                return new TCPMessage(received.getSequenceNumber(), received.getAcknowledgeNumber(), received.getTimeStamp(), 0, received.getPort(), received.getFlags(), midStep);

            } else if (lastSequenceProcessed + 1 == received.getSequenceNumber()){ //the message is part of a larger ammount of data
                byte[] midStep = new byte[received.getPayload().length-1];
                System.arraycopy(received.getPayload(), 1, midStep, 0, midStep.length);
                recievedMessageData.add(midStep); //and needs to be placed in the correct order in that data.
                lastSequenceProcessed++;
                while (misplacedData.keySet().contains(lastSequenceProcessed)){ //this might have enabled more packets to be added into the
                    midStep = new byte[misplacedData.get(lastSequenceProcessed).getPayload().length-1];
                    System.arraycopy(misplacedData.get(lastSequenceProcessed).getPayload(), 1, midStep, 0, midStep.length);
                    recievedMessageData.add(midStep); //data order.
                    misplacedData.remove(lastSequenceProcessed);
                    lastSequenceProcessed++;
                }
            } else { //the message was not delivered in the right order, thus will be queued.
                misplacedData.put(received.getSequenceNumber(), received);
            }


        } else if (received.getPort()==3 && ackGetter.hasNotBeenRecievedBefore(received.getSequenceNumber())) {

            ackGetter.recievedMSG(received.getSequenceNumber());
            System.out.println("Data message recieved.\n\n");

            if (received.getPayload()[0]==SELF_CONTAINED_DATA_MESSAGE){//the message that just came in is not part of a larger amount of data
                return received; //and can thus be forwarded to the controller

            } else if (received.getPayload()[0]==FIRST_AND_MORE_DATA_TO_COME) {//the message that just came in is the first of a larger ammount of data
                recievedFileData.clear(); //so a record needs to be kept of the order of this data.
                byte[] midStep = new byte[received.getPayload().length-1];
                System.arraycopy(received.getPayload(), 1, midStep, 0, midStep.length);
                recievedFileData.add(midStep);
                misplacedData.clear();
                lastSequenceProcessed = received.getSequenceNumber();
                return null;

            } else if (received.getPayload()[0] == NO_MORE_DATA_TO_COME){//this is the last packet of a sequence of data
                byte[] midStep = new byte[received.getPayload().length-1];
                System.arraycopy(received.getPayload(), 1, midStep, 0, midStep.length);
                int finalSize=0;
                recievedFileData.add(midStep);
                for (byte[] b : recievedFileData) {
                    finalSize += b.length;
                }
                midStep = new byte[finalSize];
                int counter = 0;
                for(byte[] b: recievedFileData) {
                    for (byte byt: b){
                        midStep[counter] = byt;
                        counter++;
                    }
                } //so the entire amount of data should be forwarded.
                return new TCPMessage(received.getSequenceNumber(), received.getAcknowledgeNumber(), received.getTimeStamp(), 0, received.getPort(), received.getFlags(), midStep);

            } else if (lastSequenceProcessed + 1 == received.getSequenceNumber()){ //the message is part of a larger ammount of data
                byte[] midStep = new byte[received.getPayload().length-1];
                System.arraycopy(received.getPayload(), 1, midStep, 0, midStep.length);
                lastSequenceProcessed++;
                recievedFileData.add(midStep); //and needs to be placed in the correct order in that data.
                while (misplacedData.keySet().contains(lastSequenceProcessed)){ //this might have enabled more packets to be added into the
                    midStep = new byte[misplacedData.get(lastSequenceProcessed).getPayload().length-1];
                    System.arraycopy(misplacedData.get(lastSequenceProcessed).getPayload(), 1, midStep, 0, midStep.length);
                    recievedFileData.add(midStep); //data order.
                    misplacedData.remove(lastSequenceProcessed);
                    lastSequenceProcessed += 1;
                }
            } else { //the message was not delivered in the right order, thus will be queued.
                misplacedData.put(received.getSequenceNumber(), received);
            }


        } else if (received.getPort() == 0) {
            return received;
        } else if (received.getPort()==3 || received.getPort() == 2){
            ackGetter.recievedMSG(received.getSequenceNumber());
            System.out.println("duplicate Data message received.\n\n");
        }
        return null;
    }

    public TCPMessage establishConnection(){
        connectionEstablished = false;
        connectionTimeOut = 0;
        return new TCPMessage(0,0,System.currentTimeMillis(), 0, TCP_CONNECTION_INFORMATION_PORT, SYN_FLAG, null);
    }

    public int hashData(byte[] data){
        if (data == null){
            return 0;
        }
        int result = 0;
        for (int i = 0; i < data.length; i++) {
            result += data[i];
        }
        result = Integer.hashCode(result);
        return result;
    }//to be properly implemented

    public String getName() {
        return name;
    }

    public void terminateConnection(){
        priorityMessage = new TCPMessage(sequenceGetter.getNextSeqNumber(1),ackGetter.nextAck(), System.currentTimeMillis(), 0, TCP_CONNECTION_INFORMATION_PORT, FIN_FLAG, null );
    }

    public boolean isConnectionTerminated(){
        return connectionTerminated;
    }


}
