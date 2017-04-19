package Tests;

import EncryptionLayer.EncrytionModule;
import Utilities.BytewiseUtilities;

/**
 * Created by freem on 4/13/2017.
 */
public class EncryptionTest {
    public static void main(String[] args) {
        EncrytionModule moduleOne = new EncrytionModule();
        EncrytionModule moduleTwo = new EncrytionModule();
        initialConnect(moduleOne, moduleTwo);
        furterCommunicationTesting(moduleOne, moduleTwo);

        keyLengthTest(moduleOne);


    }

    public static void initialConnect(EncrytionModule moduleOne, EncrytionModule moduleTwo){

        byte[] data = new byte[4];
        data[0] = 0x0F;
        data[1] = 0x0A;
        data[2] = 0x0C;
        data[3] = 0x0E;

        byte[] messageOne = moduleOne.encryptMessage(data, "two");//message with only the public key
        System.out.println(moduleTwo.handleIncomming(messageOne));
        byte[] messageTwo = moduleTwo.recievedMessage(messageOne,"one");//message containing the key for symmetric communication,
        System.out.println(moduleTwo.handleIncomming(messageTwo)); //encrypted with 1's public key.
        moduleOne.recievedMessage(messageTwo, "two");// null, as there is no response from message2.
        System.out.println(moduleOne.isBufferEmpty());
        if (!moduleOne.isBufferEmpty()){
            System.out.println(BytewiseUtilities.printBytes(moduleTwo.recievedMessage(moduleOne.encodeFirstBufferItem().getMessage().getBytes(),"one")));
        }
        System.out.println(moduleOne.isBufferEmpty());
    }

    public static void furterCommunicationTesting(EncrytionModule moduleOne, EncrytionModule moduleTwo){
        byte[] data = new byte[4];
        data[0] = 0x0d;
        data[1] = 0x0e;
        data[2] = 0x0e;
        data[3] = 0x0d;

        byte[] channel = moduleOne.encryptMessage(data, "two");
        System.out.println("This should be giberish "+Utilities.BytewiseUtilities.printBytes(channel));
        byte[] info = moduleTwo.recievedMessage(channel, "one");
        System.out.println("This should be back to deed " + Utilities.BytewiseUtilities.printBytes(info));

        data = new byte[4];
        data[0] = 0x0a;
        data[1] = 0x0b;
        data[2] = 0x0b;
        data[3] = 0x0a;

        channel = moduleTwo.encryptMessage(data, "one");
        System.out.println("This should be giberish "+Utilities.BytewiseUtilities.printBytes(channel));
        info = moduleOne.recievedMessage(channel, "two");
        System.out.println("This should be back to abba " + Utilities.BytewiseUtilities.printBytes(info));
    }

    public static void keyLengthTest(EncrytionModule moduleOne){
        byte[] data = new byte[4000];
        for (int i = 0; i < data.length; i++) {
            data[i] = (byte)0xAA;
        }
        System.out.println(Utilities.BytewiseUtilities.printBytes(moduleOne.encryptMessage(data, "two")));
    }
}
