package IPLayer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Georg on 10-Apr-17.
 */
public class AddressMap {
    public HashMap<String, String> ipNameTable = new HashMap<>();
    private int count = 1;
    private final String FIXED = "Unknown Name ";

    public void setIpNameTable(byte[] ipaddress, String name) {
        ipNameTable.put(IPLayer.ipByteArrayToString(ipaddress),name);
    }
    
    public void setIpNameTable(byte[] ipaddress) {
        if(!ipNameTable.containsKey(ipaddress)) {
        	ipNameTable.put(IPLayer.ipByteArrayToString(ipaddress), FIXED + count);
        	count++;
        }
    }

    public boolean checkName (String name) {
        return ipNameTable.containsValue(name);
    }

    public byte[] getIpaddress(String name) {
        for (Map.Entry<String,String> entry: ipNameTable.entrySet()) {
            if (Objects.equals(entry.getValue(),name)) {
                return IPLayer.ipStringToByteArray(entry.getKey());
            }
        }
        return null;
    }

    public String getName(String ipaddress) {
        return ipNameTable.get(ipaddress);
    }

    public Collection<String> allnames() {
        return ipNameTable.values();
    }

    public Collection<String> allIPAddresses() {
        return ipNameTable.keySet();
    }

    public void removeIP(String s) {
        System.out.println("removing " + s);
        ipNameTable.remove(s);
    }
}