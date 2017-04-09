package Tests;

import TCPLayer.*;

import java.util.LinkedList;

/**
 * Created by freem on 4/7/2017.
 */
public class TCPTests {

    public static void main(String[] args) {
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
}
