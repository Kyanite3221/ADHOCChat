package IPLayer;


import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by thomas on 7-4-17.
 */
public class IPLayer {

	public static final int HEADER_SIZE = 1;
	public static final int MAX_PACKET_SIZE = 2;

	//TODO
	public byte[] addIPHeader(byte[] tcpData) {
		return null;
	}

	public String getInetAddress() {
		try {
			return InetAddress.getLocalHost().toString();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return null;
	}

}
