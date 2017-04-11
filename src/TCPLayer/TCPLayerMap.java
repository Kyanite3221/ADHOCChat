package TCPLayer;

import com.sun.istack.internal.NotNull;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by freem on 4/11/2017.
 */
public class TCPLayerMap {
    private HashMap<String, TCPLayer> tcpMap;

    public TCPLayerMap(){
        tcpMap = new HashMap<String, TCPLayer>();
    }

    public TCPMessage connectToNewHost(String ipAdress){
        tcpMap.put(ipAdress, new TCPLayer(ipAdress));
        return tcpMap.get(ipAdress).establishConnection();
    }

    public void createPingMessage(byte[] tableInformation, String receiver){
        tcpMap.get(receiver).createPingMessage(tableInformation);
    }

    public void createDataMessage (@NotNull byte[] data, String receiver){
        tcpMap.get(receiver).createDataMessage(data);
    }

    public void createTCPMessage(String receiver) {
        tcpMap.get(receiver).createTCPMessage();
    }

    public LinkedList<TCPMessage> tick(String receiver){
        return tcpMap.get(receiver).tick();
    }

    public TCPMessage recievedMessage(@NotNull byte[] networkInfo, String receiver){
        return tcpMap.get(receiver).recievedMessage(networkInfo);
    }



}
