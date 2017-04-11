package TCPLayer;

import java.util.LinkedList;

/**
 * Created by freem on 4/11/2017.
 */
public class TCPMapTest {

    public static void main(String[] args) {
        TCPLayerMap mapOne = new TCPLayerMap();
        TCPLayerMap mapTwo = new TCPLayerMap();
        LinkedList<TCPMessage> responseOne = new LinkedList<TCPMessage>();
        byte[] uselessdata = Utilities.BytewiseUtilities.longToByteArray((long)(Math.random()*Long.MAX_VALUE));

        TCPMessage messageOne = mapOne.connectToNewHost("mapTwo");
        mapOne.createDataMessage(uselessdata,"mapTwo");
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
}
