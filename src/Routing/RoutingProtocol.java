package Routing;

import IPLayer.IPLayer;
import IPLayer.AddressMap;

import java.util.*;
/**
 * Created by Georg on 11-Apr-17.
 */
public class RoutingProtocol {
    private HashMap<String, MyRoute> forwardingTable= new HashMap<>();
    private byte[] myAddress;
    private AddressMap addressMap;
    private MyRoute ownLocation;
    private String myname;
    private byte[] data;

    public RoutingProtocol(String myname, byte[] myAddress, AddressMap addressMap){
        this.myname = myname;
        this.myAddress = myAddress;
        this.addressMap = addressMap;
        this.ownLocation = new MyRoute(myAddress,myAddress,0, myname);
        forwardingTable.put(IPLayer.ipByteArrayToString(myAddress), ownLocation);
        addressMap.setIpNameTable(myAddress, myname);
    }

    public void update(byte[] data, byte[] source) {
        MyRoute[] processedData = readdata(data, source);
        for (int i = 0; i < processedData.length; i++){
            String destination = IPLayer.ipByteArrayToString(processedData[i].getDestination());
            if (!forwardingTable.containsKey(destination)) {
                forwardingTable.put(destination,processedData[i]);
                addressMap.setIpNameTable(processedData[i].getDestination(), processedData[i].getName());
            }
            if (forwardingTable.get(destination).getCost() > processedData[i].getCost()) {
                forwardingTable.replace(destination,processedData[i]);
            }
        }
    }

    public byte[] send() {
        List<Byte> packetlist = new ArrayList<>();
        for (MyRoute route: forwardingTable.values()) {
            for (int j = 0; j < 4; j++) {
                packetlist.add(route.getDestination()[j]);
            }
            for (int k = 0; k < 4; k++) {
                packetlist.add(myAddress[k]);
            }
            packetlist.add(route.getCostAsByte());
            for (int l = 0; l < 8; l++) {
                packetlist.add(route.getNameAsByte()[l]);
            }
        }
        byte[] packet = new byte[packetlist.size()];
        Byte[] rpacket = new Byte[packetlist.size()];
        Object[] objectPacket = packetlist.toArray();
        packet = toPrimitivesbytes(objectPacket);
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


    public byte[] toPrimitivesbytes (Object[] rbytes) {
        byte[] bytes = new byte[rbytes.length];
        for(int i = 0;i < rbytes.length; i++) {
            Byte b = (Byte) rbytes[i];
            bytes[i] = b;
        }
        return bytes;
    }

    @Override
    public String toString() {
        String s = "";
        s += "table: \n";
        for (Map.Entry<String, MyRoute> entry : forwardingTable.entrySet()) {
            s += entry.getKey();
            s += ": ";
            s += entry.getValue().toString();
            s += "\n";
        }
        return s;
    }
    public byte[] getnhop(String ip) {
        if (! ip.equals("-64.-88.5.0")) {
            System.out.println(Arrays.toString(forwardingTable.get(ip).getNexthop()));
        }

        if (forwardingTable.containsKey(ip)){
            return forwardingTable.get(ip).getNexthop();
        }
        return null;
    }
}

