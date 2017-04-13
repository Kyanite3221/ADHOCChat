import java.util.HashMap;

/**
 * Created by thomas on 12-4-17.
 */
public class DummyIPMap {
	private HashMap<String, String> ipToName;
	private HashMap<String, String> nameToIP;

	public DummyIPMap() {
		ipToName.put("192.168.5.1", "OldShittyPc");
		nameToIP.put("OldShittyPc", "192.168.5.1");

		ipToName.put("192.168.5.2", "ThomasLinux");
		nameToIP.put("ThomasLinux", "192.168.5.2");
	}

	public String getIP(String name) {
		return nameToIP.get(name);
	}

	public String getName(String ip) {
		return ipToName.get(ip);
	}
}
