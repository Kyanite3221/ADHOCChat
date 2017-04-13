package Routing;

import java.util.*;
/**
 * Created by Georg on 11-Apr-17.
 */
public class RoutingProtocol {
    private HashMap<byte[], MyRoute> forwardingTable= new HashMap<>();
    private byte[] myAddress;
    private MyRoute ownLocation;
    private String myname;
    private byte[] data;

    public RoutingProtocol (String name, byte[] ownip){
        this.myname = name;
        this.myAddress = ownip;
        this.ownLocation = new MyRoute(myAddress,myAddress,0,"name");
    }

    public void update(byte[] data, byte[] source) {
        MyRoute[] processedData = readdata(data, source);
        if (!forwardingTable.containsKey(myAddress)) {
            forwardingTable.put(myAddress,ownLocation);
        }
        for (int i = 0; i < processedData.length; i++){
            if (!forwardingTable.containsKey(processedData[i].getDestination())) {
                forwardingTable.put(processedData[i].getDestination(),processedData[i]);
            }
            if (forwardingTable.get(processedData[i].getDestination()).getCost() > processedData[i].getCost()) {
                forwardingTable.replace(processedData[i].getDestination(),processedData[i]);
            }
        }
    }

    public byte[] send() {
        List<Byte> packetlist = new ArrayList<>();
        for (int i = 0; i < forwardingTable.size(); i++) {
            for (int j = 0; j < 4; j++) {
                packetlist.add(forwardingTable.get(i).getDestination()[j]);
            }
            for (int k = 0; k < 4; k++) {
                packetlist.add(forwardingTable.get(i).getNexthop()[k]);
            }
            packetlist.add(forwardingTable.get(i).getCostAsByte());
            for (int l = 0; l < 8; l++) {
                packetlist.add(forwardingTable.get(i).getNameAsByte()[l]);
            }
        }
        byte[] packet = new byte[packetlist.size()];
        Byte[] rpacket = new Byte[packetlist.size()];
        rpacket = (Byte[]) packetlist.toArray();
        packet = toPrimitivesbytes(rpacket);
        return packet;
    }

    public MyRoute[] readdata(byte[] data, byte[] source) {
        MyRoute[] readdata = new MyRoute[data.length/17];
        for (int i = 0; i < data.length/17;i++){
            byte[] tempdest = new byte[4];
            for (int j = 0; j < 4; j++) {
                tempdest[j] = data[i*17+j];
            }
            byte[] tempnhop = source;
            int tempcost = (int) data[i*17+8] + 1;
            byte[] tempname = new byte[8];
            for (int l = 0; l<8; l++) {
                tempname[l] = data[i*17+9+l];
            }
            MyRoute tempRoute = new MyRoute(tempdest,tempnhop,tempcost,tempname);
            readdata[i] = tempRoute;
        }
        return readdata;
    }


    public byte[] toPrimitivesbytes (Byte[] rbytes) {
        byte[] bytes = new byte[rbytes.length];
        for(int i = 0;i < rbytes.length; i++) {
            bytes[i] = rbytes[i];
        }
        return bytes;
    }
}
