package Tests;

import TCPLayer.*;

import java.util.LinkedList;

/**
 * Created by freem on 4/7/2017.
 */
public class TCPTests {

    public static void main(String[] args) {
        (new TCPTests()).multipleSmallMessages();

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

        TCPLayer tCP = new TCPLayer();
        tCP.createDataMessage(myDat);
        LinkedList<TCPMessage> recieved = new LinkedList<TCPMessage>();
        TCPLayer reciever = new TCPLayer("Bob");
        for (int i = 0; i < 4000; i++) {
            LinkedList<TCPMessage> result = tCP.tick();
            for ( TCPMessage MSG : result) {
                System.out.println(MSG.toString());
                reciever.recievedMessage(MSG.toByte());
                LinkedList<TCPMessage> imediateResult = reciever.tick();
                for (TCPMessage message: imediateResult){
                    recieved.add(message);
                    System.out.println(message);
                    tCP.recievedMessage(message.toByte());
                }
            }

        }
    }

    public void multipleSmallMessages() {
        byte[] myDat = new byte[4];
        TCPLayer tCP = new TCPLayer();
        myDat[0] = (byte)0x0f;
        myDat[1] = (byte)0x0a;
        myDat[2] = (byte)0x0c;
        myDat[3] = (byte)0x0e;
        tCP.createDataMessage(myDat);
        myDat[0] = (byte)0x0d;
        myDat[1] = (byte)0x0e;
        myDat[2] = (byte)0x0e;
        myDat[3] = (byte)0x0d;
        tCP.createDataMessage(myDat);
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

        TCPLayer reciever = new TCPLayer();
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

        TCPMessage firstPacket = tCP.tick().getFirst();
        System.out.println(firstPacket.toString());

        myDat[0] = (byte)0x0c;
        myDat[1] = (byte)0x0a;
        myDat[2] = (byte)0x0a;
        myDat[3] = (byte)0x0b;
        tCP.createDataMessage(myDat);

    }
}
