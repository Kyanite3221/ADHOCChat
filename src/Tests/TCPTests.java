package Tests;

import TCPLayer.SequenceWindow;
import TCPLayer.TCPMessage;
import TCPLayer.TCPStream;

import java.util.LinkedList;

/**
 * Created by freem on 4/7/2017.
 */
public class TCPTests {

    public static void main(String[] args) {
        (new TCPTests()).overTheSequenceSize();

    }

    private void timeOutAndPriorityOverride(){
        byte[] myDat = new byte[10];
        myDat[0] = 0x12;
        myDat[1] = 0x34;
        myDat[2] = (byte)0xf4;
        myDat[3] = 0x52;
        myDat[4] = 0x2d;
        myDat[5] = 0x65;
        myDat[6] = (byte)0xe4;
        myDat[7] = (byte)0xfe;
        myDat[8] = (byte)0xce;
        myDat[9] = 0x12;

        TCPStream tCP = new TCPStream("NO SETUP");
        tCP.createMessageData(myDat);
        LinkedList<TCPMessage> recieved = new LinkedList<TCPMessage>();
        TCPStream reciever = new TCPStream("NO SETUP");
        for (int i = 0; i < 4000; i++) {
            LinkedList<TCPMessage> result = tCP.tick();
            for ( TCPMessage MSG : result) {
                System.out.println(MSG.toString());
                reciever.recievedMessage(MSG.toByte());
                LinkedList<TCPMessage> imediateResult = reciever.tick();
                for (TCPMessage message: imediateResult){
                    recieved.add(message);
                    tCP.recievedMessage(message.toByte());
                }
            }

        }
    }

    public void multipleSmallMessages() {
        byte[] myDat = new byte[4];
        TCPStream tCP = new TCPStream("NO SETUP");
        myDat[0] = (byte)0x0f;
        myDat[1] = (byte)0x0a;
        myDat[2] = (byte)0x0c;
        myDat[3] = (byte)0x0e;
        tCP.createMessageData(myDat);
        myDat[0] = (byte)0x0d;
        myDat[1] = (byte)0x0e;
        myDat[2] = (byte)0x0e;
        myDat[3] = (byte)0x0d;
        tCP.createMessageData(myDat);
        LinkedList<TCPMessage> tickData = tCP.tick();
        TCPMessage result = tickData.removeFirst();
        System.out.println(result.toString());
        for (int i = 0; i < 1000; i++) {
            LinkedList<TCPMessage> currentTick = tCP.tick();
            for (TCPMessage msg: currentTick){
                tickData.add(msg);
            }
        }
        System.out.println(tickData.size());

        TCPStream reciever = new TCPStream("NO SETUP");
        LinkedList<TCPMessage> recieverList = new LinkedList<TCPMessage>();
        for (int i = 0; i < 3000; i++) {
            LinkedList<TCPMessage> currentTick = tCP.tick();
            LinkedList<TCPMessage> recievedList = reciever.tick();
            for (TCPMessage msg: currentTick){
                reciever.recievedMessage(msg.toByte());
            }

            for (TCPMessage msg: recievedList) {
                recieverList.add(msg);
            }
        }
        for (TCPMessage msg: recieverList){
            System.out.println(msg.toString()+"\n");
            tCP.recievedMessage(msg.toByte());
        }

        myDat[0] = (byte)0x0c;
        myDat[1] = (byte)0x0a;
        myDat[2] = (byte)0x0a;
        myDat[3] = (byte)0x0b;
        tCP.createMessageData(myDat);

    }

    public void regularComunicationTest(){
        TCPStream annie = new TCPStream("NO SETUP");
        TCPStream bob = new TCPStream("NO SETUP");
        byte[] data = new byte[8];
        Utilities.BytewiseUtilities.arrayInsertion(Utilities.BytewiseUtilities.intToByteArray((int)(Math.random()*Integer.MAX_VALUE)),data, 0);
        Utilities.BytewiseUtilities.arrayInsertion(Utilities.BytewiseUtilities.intToByteArray((int)(Math.random()*Integer.MAX_VALUE)),data, 4);

        annie.createMessageData(data);
        LinkedList<TCPMessage> annieData = annie.tick();
        for (TCPMessage msg : annieData){
            System.out.println(msg);
            bob.recievedMessage(msg.toByte());
        }
        LinkedList<TCPMessage> bobData = bob.tick();

        for (TCPMessage msg : bobData){
            System.out.println(msg);
            annie.recievedMessage(msg.toByte());
        }

        Utilities.BytewiseUtilities.arrayInsertion(Utilities.BytewiseUtilities.intToByteArray((int)(Math.random()*Integer.MAX_VALUE)),data, 0);
        Utilities.BytewiseUtilities.arrayInsertion(Utilities.BytewiseUtilities.intToByteArray((int)(Math.random()*Integer.MAX_VALUE)),data, 4);

        bob.createMessageData(data);
        annieData = annie.tick();
        for (TCPMessage msg : annieData){
            System.out.println(msg);
            bob.recievedMessage(msg.toByte());
        }

        bobData = bob.tick();

        for (TCPMessage msg : bobData){
            System.out.println(msg);
            annie.recievedMessage(msg.toByte());
        }

        annieData = annie.tick();
        for (TCPMessage msg : annieData){
            System.out.println(msg);
            bob.recievedMessage(msg.toByte());
        }

        bobData = bob.tick();

        for (TCPMessage msg : bobData){
            System.out.println(msg);
            annie.recievedMessage(msg.toByte());
        }
        annieData = annie.tick();
        for (TCPMessage msg : annieData){
            System.out.println(msg);
            bob.recievedMessage(msg.toByte());
        }

        bobData = bob.tick();

        for (TCPMessage msg : bobData){
            System.out.println(msg);
            annie.recievedMessage(msg.toByte());
        }
        annieData = annie.tick();
        for (TCPMessage msg : annieData){
            System.out.println(msg);
            bob.recievedMessage(msg.toByte());
        }

        bobData = bob.tick();

        for (TCPMessage msg : bobData){
            System.out.println(msg);
            annie.recievedMessage(msg.toByte());
        }
        annieData = annie.tick();
        for (TCPMessage msg : annieData){
            System.out.println(msg);
            bob.recievedMessage(msg.toByte());
        }

        bobData = bob.tick();

        for (TCPMessage msg : bobData){
            System.out.println(msg);
            annie.recievedMessage(msg.toByte());
        }

        annieData = annie.tick();
        for (TCPMessage msg : annieData){
            System.out.println(msg);
            bob.recievedMessage(msg.toByte());
        }

        bobData = bob.tick();

        for (TCPMessage msg : bobData){
            System.out.println(msg);
            annie.recievedMessage(msg.toByte());
        }

        Utilities.BytewiseUtilities.arrayInsertion(Utilities.BytewiseUtilities.intToByteArray((int)(Math.random()*Integer.MAX_VALUE)),data, 0);
        Utilities.BytewiseUtilities.arrayInsertion(Utilities.BytewiseUtilities.intToByteArray((int)(Math.random()*Integer.MAX_VALUE)),data, 4);

        annie.createMessageData(data);
        annieData = annie.tick();
        for (TCPMessage msg : annieData){
            System.out.println(msg);
            bob.recievedMessage(msg.toByte());
        }

        Utilities.BytewiseUtilities.arrayInsertion(Utilities.BytewiseUtilities.intToByteArray((int)(Math.random()*Integer.MAX_VALUE)),data, 0);
        Utilities.BytewiseUtilities.arrayInsertion(Utilities.BytewiseUtilities.intToByteArray((int)(Math.random()*Integer.MAX_VALUE)),data, 4);

        bob.createMessageData(data);
        bobData = bob.tick();

        for (TCPMessage msg : bobData){
            System.out.println(msg);
            annie.recievedMessage(msg.toByte());
        }
        annieData = annie.tick();
        for (TCPMessage msg : annieData){
            System.out.println(msg);
            bob.recievedMessage(msg.toByte());
        }

        bobData = bob.tick();

        for (TCPMessage msg : bobData){
            System.out.println(msg);
            annie.recievedMessage(msg.toByte());
        }
    }

    public void sequenceWindowTest(){
        SequenceWindow need = new SequenceWindow(10);
        LinkedList<Integer> numbers = new LinkedList<>();
        for (int i = 0; i < 14; i++) {
            if (need.hasNextAvailible()){
                numbers.add(need.getNextSeqNumber(1));
            }
        }
        System.out.println("Size is: " + numbers.size());

        for (Integer i : numbers){
            System.out.print(i+" ");
        }

        need.recieveAck(numbers.removeFirst());

        LinkedList<Integer> newNumbers = new LinkedList<>();

        for (int i = 0; i < 14; i++) {
            if (need.hasNextAvailible()){
                numbers.add(need.getNextSeqNumber(1));
            }
        }

        System.out.println("\nSize is: " + numbers.size());

        for (Integer i : numbers){
            System.out.print(i+" ");
        }

        System.out.println("\nThis means that if an ack is received and it is the first, a new number is given free");

        need.recieveAck(numbers.remove(1));
        need.recieveAck(numbers.remove(1));


        for (int i = 0; i < 14; i++) {
            if (need.hasNextAvailible()){
                numbers.add(need.getNextSeqNumber(1));
            }
        }

        System.out.println("\nSize is: " + numbers.size());

        for (Integer i : numbers){
            System.out.print(i+" ");
        }

        System.out.println("\nThis means that if an ack is received and it is not the first, nothing changes");

        need.recieveAck(numbers.removeFirst());



        for (int i = 0; i < 14; i++) {
            if (need.hasNextAvailible()){
                numbers.add(need.getNextSeqNumber(1));
            }
        }

        for (Integer i : numbers){
            System.out.print(i+" ");
        }

        System.out.println("\nAnd then, if the first is acked, more are released.");
    }

    public void overTheSequenceSize(){
        TCPStream bananaFace = new TCPStream("NO SETUP", 5);
        TCPStream oreoFace = new TCPStream("NO SETUP", 5);
        byte[] uselessdata = Utilities.BytewiseUtilities.longToByteArray((long)(Math.random()*Long.MAX_VALUE));
        bananaFace.createMessageData(uselessdata);
        uselessdata = Utilities.BytewiseUtilities.longToByteArray((long)(Math.random()*Long.MAX_VALUE));
        bananaFace.createMessageData(uselessdata);
        uselessdata = Utilities.BytewiseUtilities.longToByteArray((long)(Math.random()*Long.MAX_VALUE));
        bananaFace.createMessageData(uselessdata);
         uselessdata = Utilities.BytewiseUtilities.longToByteArray((long)(Math.random()*Long.MAX_VALUE));
        bananaFace.createMessageData(uselessdata);
        uselessdata = Utilities.BytewiseUtilities.longToByteArray((long)(Math.random()*Long.MAX_VALUE));
        bananaFace.createMessageData(uselessdata);
        uselessdata = Utilities.BytewiseUtilities.longToByteArray((long)(Math.random()*Long.MAX_VALUE));
        bananaFace.createMessageData(uselessdata);
        uselessdata = Utilities.BytewiseUtilities.longToByteArray((long)(Math.random()*Long.MAX_VALUE));
        bananaFace.createMessageData(uselessdata);
        uselessdata = Utilities.BytewiseUtilities.longToByteArray((long)(Math.random()*Long.MAX_VALUE));
        bananaFace.createMessageData(uselessdata);
        uselessdata = Utilities.BytewiseUtilities.longToByteArray((long)(Math.random()*Long.MAX_VALUE));
        bananaFace.createMessageData(uselessdata);

        LinkedList<TCPMessage> returned= bananaFace.tick();
        LinkedList<TCPMessage> recieverSent;
        for (TCPMessage b: returned){
            oreoFace.recievedMessage(b.toByte());
        }
        recieverSent = oreoFace.tick();

        System.out.println(returned.size());
        for(TCPMessage b :returned){
            System.out.println(b);

        }

        System.out.println("\n\noreoFace returned " + recieverSent.size() + " messages\nsaying:\n\n");
        for(TCPMessage b :recieverSent){
            System.out.println(b);
        }

        bananaFace.recievedMessage(recieverSent.removeFirst().toByte());

        returned = bananaFace.tick();
        System.out.println("\n\n" + returned.size());
        for(TCPMessage b :returned){
            System.out.println(b);
        }

        for(TCPMessage b :recieverSent){
            bananaFace.recievedMessage(b.toByte());
        }

        returned = bananaFace.tick();
        System.out.println("\n\n" + returned.size());
        for(TCPMessage b :returned){
            System.out.println(b);
        }

    }


}
