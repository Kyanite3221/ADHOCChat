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
        if (!forwardingTable.containsKey(myAddress)) {
            forwardingTable.put(myAddress,ownLocation);

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
            for (int l = 0; l < forwardingTable.get(i).getNameAsByte().length; l++) {
                packetlist.add(forwardingTable.get(i).getNameAsByte()[l]);
            }
        }
        Byte[] packet = new Byte[packetlist.size()];
        packet = (Byte[]) packetlist.toArray();

        return null;
    }

}
