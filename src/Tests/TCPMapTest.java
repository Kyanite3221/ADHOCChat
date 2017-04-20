package Tests;

import TCPLayer.TCPLayer;
import TCPLayer.TCPMessage;

import java.util.LinkedList;

/**
 * Created by freem on 4/11/2017.
 */
public class TCPMapTest {

    public static void main(String[] args) {
        testPriorityHierarchy();
    }

    private static void connectionSetup() {
        TCPLayer mapOne = new TCPLayer();
        TCPLayer mapTwo = new TCPLayer();
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

    public static void initialContact (TCPLayer one, TCPLayer two) {
        one.connectToNewHost("two");
        TCPMessage communicationChannel = one.tick("two").getFirst();
        two.recievedMessage(communicationChannel.toByte(), "one");
        communicationChannel = two.tick("one").getFirst();
        one.recievedMessage(communicationChannel.toByte(), "two");
        communicationChannel = one.tick("two").getFirst();
    }

    public static void testPriorityHierarchy(){
        TCPLayer hostOne = new TCPLayer();
        TCPLayer hostTwo = new TCPLayer();
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

    public static void pingTest(){
        TCPLayer one = new TCPLayer();
        TCPLayer two = new TCPLayer();

        byte[] bla = new byte[4];
        bla[0] = 0x0b;
        bla[1] = 0x0e;
        bla[2] = 0x0e;
        bla[3] = 0x0f;

        one.createPingMessage(bla);
        LinkedList<TCPMessage> msg = one.tick();
        TCPMessage data = two.recievedMessage(msg.getFirst().toByte());
        System.out.println(Utilities.BytewiseUtilities.printBytes(data.getPayload()));
    }

    public static void duplicateSequence(){
        TCPLayer hostOne = new TCPLayer();
        TCPLayer hostTwo = new TCPLayer();
        initialContact(hostOne, hostTwo);

        byte[] bla = new byte[4];
        bla[0] = 0x01;
        bla[1] = 0x0b;
        bla[2] = 0x0a;
        bla[3] = 0x0f;

        hostOne.createMessageData(bla, "two");
        bla[0] = 0x02;
        hostOne.createMessageData(bla, "two");
        bla[0] = 0x03;
        hostOne.createMessageData(bla, "two");
        bla[0] = 0x04;
        hostOne.createMessageData(bla, "two");
        bla[0] = 0x05;
        hostOne.createMessageData(bla, "two");
        bla[0] = 0x06;
        hostOne.createMessageData(bla, "two");
    }
}
