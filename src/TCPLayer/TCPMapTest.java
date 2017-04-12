package TCPLayer;

import java.util.LinkedList;

/**
 * Created by freem on 4/11/2017.
 */
public class TCPMapTest {

    public static void main(String[] args) {
        testPriorityHierarchy();
    }

    private static void connectionSetup() {
        TCPLayerMap mapOne = new TCPLayerMap();
        TCPLayerMap mapTwo = new TCPLayerMap();
        LinkedList<TCPMessage> responseOne = new LinkedList<TCPMessage>();
        byte[] uselessdata = Utilities.BytewiseUtilities.longToByteArray((long)(Math.random()*Long.MAX_VALUE));

        TCPMessage messageOne = mapOne.connectToNewHost("mapTwo");
        mapOne.createMessageData(uselessdata,"mapTwo");
        responseOne = mapOne.tick("mapTwo");
        if (responseOne == null) {
            System.out.println("The first tick on mapOne, pre connect, was empty");
        } else {
            System.out.println("You should worry here");
        }

        System.out.println(messageOne);
        TCPMessage messageTwo = mapTwo.recievedMessage(messageOne.toByte(), "mapOne");
        responseOne = mapOne.tick("mapTwo");
        if (responseOne == null) {
            System.out.println("\n\nThe second tick on mapOne, pre connect, post first SYN, was empty\n");
        } else {
            System.out.println("\n!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!You should worry here!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n\n");
            System.exit(0);
        }
        System.out.println(messageTwo);
        mapOne.recievedMessage(messageTwo.toByte(), "mapTwo");
        responseOne = mapOne.tick("mapTwo");
        if (responseOne == null) {
            System.out.println("\n!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!You should worry here!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n\n");
            System.exit(0);
        } else {
            System.out.println("\n\nThe third tick on mapOne, post connect, was not empty\n");
            for (TCPMessage msg : responseOne){
                System.out.println(msg);
            }
        }
    }

    public static void initialContact (TCPLayerMap one, TCPLayerMap two) {
        TCPMessage communicationChannel = one.connectToNewHost("two");
        communicationChannel = two.recievedMessage(communicationChannel.toByte(), "one");
        communicationChannel = one.recievedMessage(communicationChannel.toByte(), "two");
    }

    public static void testPriorityHierarchy(){
        TCPLayerMap hostOne = new TCPLayerMap();
        TCPLayerMap hostTwo = new TCPLayerMap();
        initialContact(hostOne, hostTwo);

        byte[] messageData = new byte[1000];
        for (int i = 0; i < messageData.length/4; i++) {
            messageData[i*4] = (byte) 0x0F;
            messageData[(i*4)+1] = (byte) 0x0A;
            messageData[(i*4)+2] = (byte) 0x0C;
            messageData[(i*4)+3] = (byte) 0x0E;
        }
        hostOne.createPingMessage(messageData, "two");
        messageData = new byte[1000];
        for (int i = 0; i < messageData.length/4; i++) {
            messageData[i*4] = (byte) 0x0D;
            messageData[(i*4)+1] = (byte) 0x0E;
            messageData[(i*4)+2] = (byte) 0x0E;
            messageData[(i*4)+3] = (byte) 0x0D;
        }
        hostOne.createFileData(messageData, "two");
        messageData = new byte[1000];
        for (int i = 0; i < messageData.length/4; i++) {
            messageData[i*4] = (byte) 0x0A;
            messageData[(i*4)+1] = (byte) 0x0B;
            messageData[(i*4)+2] = (byte) 0x0B;
            messageData[(i*4)+3] = (byte) 0x0A;
        }
        hostOne.createMessageData(messageData, "two");

        for (int i = 0; i < 50000; i++) {
            LinkedList<TCPMessage> thing = hostOne.tick("two");
            LinkedList<TCPMessage> result = new LinkedList<>();
            LinkedList<TCPMessage> thingTwo = hostTwo.tick("one");
            for (TCPMessage msg : thing){
                result.add(hostTwo.recievedMessage(msg.toByte(), "one"));
            }
            for (TCPMessage bleb : result) {
                if (bleb!=null){
                    System.out.println(Utilities.BytewiseUtilities.printBytes(bleb.getPayload()));
                }
            }

            for (TCPMessage msg : thingTwo){
                hostOne.recievedMessage(msg.toByte(), "two");
            }
        }
    }
}
