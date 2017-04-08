package Tests;

import TCPLayer.*;

import java.util.LinkedList;

/**
 * Created by freem on 4/7/2017.
 */
public class TCPTests {

    public static void main(String[] args) {
        byte[] myDat = new byte[10];
        myDat[0] = 0x00;
        myDat[1] = 0x00;
        myDat[2] = 0x01;
        myDat[3] = 0x00;
        myDat[4] = 0x00;
        myDat[5] = 0x01;
        myDat[6] = 0x00;
        myDat[7] = 0x00;
        myDat[8] = 0x01;
        myDat[9] = 0x01;

        TCPLayer tCP = new TCPLayer();
        tCP.createDataMessage(myDat);
        tCP.createTCPMessage();
        for (int i = 0; i < 3000; i++) {
            LinkedList<TCPMessage> result = tCP.tick();
            for ( TCPMessage MSG : result) {
                MSG.toString();
            }
        }
    }
}
