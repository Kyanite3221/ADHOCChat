package IPLayer;

import java.util.*;

/**
 * Created by Georg on 10-Apr-17.
 */
public class AddressMap {
    public HashMap<byte[], String> ipNameTable = new HashMap<>();

    public void setIpNameTable(byte[] ipaddress, String name) {
        ipNameTable.put(ipaddress,name);
    }

    public boolean checkName (String name) {
        return ipNameTable.containsValue(name);
    }

    public byte[] getIpaddress(String name) {
        for (Map.Entry<byte[],String> entry: ipNameTable.entrySet()) {
            if (Objects.equals(entry.getValue(),name)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public String getName(String ipaddress) {
        return ipNameTable.get(ipaddress);
    }

    public Collection<String> allnames() {
        System.out.println(1+1);
        return ipNameTable.values();
    }
}
