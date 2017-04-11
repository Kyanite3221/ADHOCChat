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

	public Byte[] inettobyte(String inet) {
		String[] strings = new String[4];
		strings = inet.split(".");
		Byte[] bytes = new Byte[strings.length];
		for (int i = 0; i < strings.length; i++) {
			bytes[i] =Byte.parseByte(strings[i]);
		}
		return bytes;
	}

}
