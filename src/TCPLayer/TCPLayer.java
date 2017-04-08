package TCPLayer;

import java.util.LinkedList;

/**
 * Created by freem on 4/7/2017.
 */
public class TCPLayer {

    SequenceStrategy SequenceGetter;
    LinkedList<TCPMessage> sendingQueue;
    //Richard's class, waaraan ik de data moet geven

    public TCPLayer(){

    }
    public byte[] createDataMessage (byte[] data, String reciever){

        return null;
    }

    public byte[] createPingMessage(){

        return null;
    }

    public void createTCPMessage() {

        return null;
    }

    public TCPMessage ping(){

    }

}
