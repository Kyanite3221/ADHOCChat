package TCPLayer;

import com.sun.istack.internal.NotNull;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by freem on 4/11/2017.
 */
public class TCPLayer {
    private HashMap<String, TCPStream> tcpMap;

    public TCPLayer(){
        tcpMap = new HashMap<>();
        tcpMap.put("Broadcast", new TCPStream("NO SETUP"));
    }

    public TCPMessage connectToNewHost(String ipAdress){
        tcpMap.put(ipAdress, new TCPStream(ipAdress));
        return tcpMap.get(ipAdress).establishConnection();
    }

    public void createPingMessage(byte[] tableInformation, String receiver){
        tcpMap.get(receiver).createPingMessage(tableInformation);
    }

    public void createPingMessage(byte[] tableInformation){
        tcpMap.get("Broadcast").createPingMessage(tableInformation);
    }

    public void createMessageData(@NotNull byte[] data, String receiver){
        if (tcpMap.containsKey(receiver)){
            tcpMap.get(receiver).createMessageData(data);
        } else {
            tcpMap.put(receiver, new TCPStream(receiver));
            tcpMap.get(receiver).createMessageData(data);
            tcpMap.get(receiver).establishConnection();
        }
    }

    public TCPMessage createFileData(@NotNull byte[] data, String receiver) {
        if (tcpMap.containsKey(receiver)){
            tcpMap.get(receiver).createFileData(data);
            return null;
        } else {
            tcpMap.put(receiver, new TCPStream(receiver));
            tcpMap.get(receiver).createMessageData(data);
            return tcpMap.get(receiver).establishConnection();
        }


    }

    public void createTCPMessage(String receiver) {
        tcpMap.get(receiver).createTCPMessage();
    }

    public LinkedList<TCPMessage> tick(String receiver){
        return tcpMap.get(receiver).tick();
    }

    public HashMap<String, LinkedList<TCPMessage>> allTick(){
        HashMap<String, LinkedList<TCPMessage>> result = new HashMap<>();
        for(TCPStream stream : tcpMap.values()){
            result.put(stream.getName(),stream.tick());
        }
        return result;
    }

    public LinkedList<TCPMessage> tick(){
        return tcpMap.get("Broadcast").tick();
    }

    public TCPMessage recievedMessage(@NotNull byte[] networkInfo, String receiver){
        if (tcpMap.keySet().contains(receiver)){
            return tcpMap.get(receiver).recievedMessage(networkInfo);
        }
        tcpMap.put(receiver, new TCPStream(receiver));
        return tcpMap.get(receiver).recievedMessage(networkInfo);
    }

    public TCPMessage recievedMessage(@NotNull byte[] networkInfo){
        return tcpMap.get("Broadcast").recievedMessage(networkInfo);
    }



}
