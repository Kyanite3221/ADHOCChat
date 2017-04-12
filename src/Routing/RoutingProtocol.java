package Routing;


import IPLayer.IPLayer;

import java.util.HashMap;

/**
 * Created by Georg on 11-Apr-17.
 */
public class RoutingProtocol {
    private HashMap<byte[], MyRoute> forwardingTable= new HashMap<>();
    IPLayer ipLayer = new IPLayer();
    byte[] myAddress = ipLayer.ipStringToByteArray(ipLayer.getOwnIP());
    private MyRoute ownLocation = new MyRoute(myAddress,myAddress,0,"name");

    public void tick() {
        forwardingTable.put(myAddress,ownLocation);
    }

}
