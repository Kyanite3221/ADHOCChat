package Routing;


import IPLayer.IPLayer;

import java.util.HashMap;

/**
 * Created by Georg on 11-Apr-17.
 */
public class RoutingProtocol {
    private HashMap<Byte[], MyRoute> forwardingTable= new HashMap<>();
    IPLayer ipLayer = new IPLayer();
    byte[] myAddress = ipLayer.ipStringToByteArray(ipLayer.getOwnIP());
    MyRoute ownLocation = new MyRoute();
}
