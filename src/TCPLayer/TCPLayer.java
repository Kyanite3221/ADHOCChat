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

    public enum TCPNextAction{
        SEND_TO_APLICATION,
        SEND_TO_ROUTING,
        NO_FURTHER_ACTION_THIS_CYCLE
    }

    public TCPNextAction handleIncomming(byte[] data) {
        try{
            byte port = data[20];
            byte flag = data[21];
            switch (port){
                case 0x00:
                    return TCPNextAction.SEND_TO_ROUTING;

                case 0x01:
                    return TCPNextAction.NO_FURTHER_ACTION_THIS_CYCLE;

                case 0x02:
                    return TCPNextAction.SEND_TO_APLICATION;

                case 0x03:
                    return TCPNextAction.SEND_TO_APLICATION;

                case 0x06:
                    return TCPNextAction.NO_FURTHER_ACTION_THIS_CYCLE;
            }
        } catch (ArrayIndexOutOfBoundsException e){
            System.out.println("Not a proper byte representation of a TCP message");
        } catch (NullPointerException ex){
            System.out.println("argument was null");
        }
        return TCPNextAction.NO_FURTHER_ACTION_THIS_CYCLE;
    }



}
