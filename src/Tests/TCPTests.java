package Tests;

import TCPLayer.TCPMessage;
import Utilities.BytewiseUtilities;

/**
 * Created by freem on 4/7/2017.
 */
public class TCPTests {

    public static void main(String[] args) {
        byte[] myDat = new byte[3];
        myDat[0] = 0x00;
        myDat[1] = 0x00;
        myDat[2] = 0x01;
        long ttime = System.currentTimeMillis();
        TCPMessage myTCPMessage = new TCPMessage(Integer.MAX_VALUE,Integer.MAX_VALUE-1, ttime,14, (byte)0xf3, (byte)0xd5, myDat);
        System.out.println(myTCPMessage.toString());
        byte[] byteTCP = myTCPMessage.toByte();
        TCPMessage recreatedTCPMessage = new TCPMessage(byteTCP);
        System.out.println(recreatedTCPMessage.toString());
    }
}
