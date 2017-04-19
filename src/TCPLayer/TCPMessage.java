package TCPLayer;

/**
 * Created by freem on 4/7/2017.
 */
public class TCPMessage {


    private int sequenceNumber;
    private int acknowledgeNumber;
    private long timeStamp;
    private int dataHash;
    private byte port;
    /**
     * Ports:       meaning
     * 00            Table Synchronization
     * 01            Connection Establishment
     * 02            Message Delivery
     * 03            (File Delivery)
     * 06            Ack only(no message)
     */
    //private int headerLength; not nececairy, because it's always the same.
    private byte flags;
    /**
     * flags:       meaning
     * 01            SYN
     * 02            ACK
     * 03            SYN/ACK
     * 04            FIN
     * 06            FIN/ACK
     * 08            ROUTING DATA
     */
    private int payloadSize;
    private byte[] payload;

    public TCPMessage (int seq, int ack, long time, int hashData, byte poort, byte flaggen, byte[] data) {
        sequenceNumber = seq;
        acknowledgeNumber = ack;
        timeStamp = time;
        dataHash = hashData;
        port = poort;
        flags = flaggen;
        if (data != null){
            payloadSize = data.length;
        } else {
            payloadSize = 0;
        }
        payload = data;
    }

    public TCPMessage (byte[] TCPayload){

        byte[] intBytes = new byte[4];
        for (int i = 0; i < 4; i++) {
            intBytes[i] = TCPayload[i];
        }
        sequenceNumber = Utilities.BytewiseUtilities.byteArrayToInt(intBytes);

        for (int i = 0; i < 4; i++) {
            intBytes[i] = TCPayload[i+4];
        }
        acknowledgeNumber = Utilities.BytewiseUtilities.byteArrayToInt(intBytes);


        byte[] timeBytes = new byte[8];
        for (int i = 0; i < 8; i++) {
            timeBytes[i] = TCPayload[i+8];
        }
        timeStamp = Utilities.BytewiseUtilities.byteArrayToLong(timeBytes);

        for (int i = 0; i < 4; i++) {
            intBytes[i] = TCPayload[i+16];
        }
        dataHash = Utilities.BytewiseUtilities.byteArrayToInt(intBytes);

        port = TCPayload[20];
        flags = TCPayload[21];

        for (int i = 0; i < 4; i++) {
            intBytes[i] = TCPayload[i+22];
        }
        payloadSize = Utilities.BytewiseUtilities.byteArrayToInt(intBytes);

        payload = new byte[TCPayload.length-26];

        for (int i = 26; i < TCPayload.length ; i++) {
            payload[i-26] = TCPayload[i];
        }

        payloadSize = payload.length;
    }

    public byte[] toByte(){
        byte[] finalArray = new byte[26 + payloadSize];
        /**
         * Byte #           use
         * 0                Sequence number
         * 1                |
         * 2                |
         * 3                Sequence number
         * 4                Acknowledgement number
         * 5                |
         * 6                |
         * 7                Acknowledgement number
         * 8                Timestamp
         * 9                |
         * 10               |
         * 11               |
         * 12               |
         * 13               |
         * 14               |
         * 15               Timestamp
         * 16               Data Hash
         * 17               |
         * 18               |
         * 19               Data Hash
         * 20               port
         * 21               flags
         * 22               Payload Size
         * 23               |
         * 24               |
         * 25               Payload Size
         * 26+              messageData
         */
        byte[] intByte = Utilities.BytewiseUtilities.intToByteArray(sequenceNumber);
        finalArray = Utilities.BytewiseUtilities.arrayInsertion(intByte, finalArray, 0);
        //converting the sequence number into bytes and then entering that into the final array.

        intByte = Utilities.BytewiseUtilities.intToByteArray(acknowledgeNumber);

        finalArray = Utilities.BytewiseUtilities.arrayInsertion(intByte, finalArray, 4);
        //converting the acknowledgement number into bytes and then entering that into the final array.

        byte[] longToByteArray = Utilities.BytewiseUtilities.longToByteArray(timeStamp);
        finalArray = Utilities.BytewiseUtilities.arrayInsertion(longToByteArray, finalArray, 8);
        //converting the timestamp into bytes and then entering that into the final array.

        intByte = Utilities.BytewiseUtilities.intToByteArray(dataHash);
        finalArray = Utilities.BytewiseUtilities.arrayInsertion(intByte, finalArray, 16);
        //converting the messageData hash into bytes and then entering that into the final array.

        finalArray[20] = port;
        finalArray[21] = flags;
        //entering the port and flag information into the final array.

        intByte = Utilities.BytewiseUtilities.intToByteArray(payloadSize);
        finalArray = Utilities.BytewiseUtilities.arrayInsertion(intByte, finalArray, 22);
        //converting the payloadSize into bytes and then entering that into the final array.

        if (payloadSize > 0){
            finalArray = Utilities.BytewiseUtilities.arrayInsertion(payload, finalArray, 26);
        }

        return finalArray;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public int getAcknowledgeNumber() {
        return acknowledgeNumber;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public int getDataHash() {
        return dataHash;
    }

    public byte getPort() {
        return port;
    }

    public byte getFlags() {
        return flags;
    }

    public int getPayloadSize() {
        return payloadSize;
    }

    public byte[] getPayload() {
        return payload;
    }

    public String toString() {
        return "This package has:\nSequence Number: " + sequenceNumber + "\nAcknowledgement Number: "
                + acknowledgeNumber + "\nTimestamp: " + timeStamp + "\nData Hash: " + dataHash + "\nPort: " + port
                + "\nFlags: " + flags + "\nPayload size: " + payloadSize + "\nfor the payload, call getPayload\n";

    }
}
