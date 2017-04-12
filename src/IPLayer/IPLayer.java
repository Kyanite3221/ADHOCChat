package IPLayer;


import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

/**
 * Created by thomas on 7-4-17.
 */
public class IPLayer {

	public static final int HEADER_SIZE = 16;
	public static final int MAX_PACKET_SIZE = 2;

	//TODO
	public byte[] addIPHeader(byte[] tcpData) {
		return null;
	}

	public String getOwnIP() {
		try {
			return InetAddress.getLocalHost().toString();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return null;
	}

	public byte[] ipStringToByteArray(String inet) {
		String[] strings = new String[4];
		strings = inet.split(".");
		byte[] bytes = new byte[strings.length];
		for (int i = 0; i < strings.length; i++) {
			bytes[i] = Byte.parseByte(strings[i]);
		}
		return bytes;
	}

	public boolean isOwnIP(byte[] address) {
		byte[] ownIp = ipStringToByteArray(getOwnIP());
		boolean differenceFound = false;
		for (int i = 0; i < 4; i++) {
			if (address[i] != ownIp[i]) {
				differenceFound = true;
			}
		}
		return !differenceFound;
	}

	public String getSourceAsString(byte[] incoming) {
		String ip = "";
		for (int i = 0; i < 4; i++) {
			int ipByte = incoming[12 + i];
			ip += ipByte;
			if (i < 3) {
				ip += ".";
			}
		}
		return ip;
	}

	public enum IPDecision {
		IGNORE,
		FORWARD,
		DELIVER
	}

	public IPDecision handlePacket(byte[] packet) {
		byte[] nextHop = Arrays.copyOfRange(packet, 4, 8);
		byte[] destination = Arrays.copyOfRange(packet, 8, 12);

		if (isOwnIP(destination)) {
			return IPDecision.DELIVER;
		} else if (isOwnIP(nextHop)) {
			return IPDecision.FORWARD;
		} else {
			return IPDecision.IGNORE;
		}
	}

	public byte[] removeHeader(byte[] packet) {
		return Arrays.copyOfRange(packet, HEADER_SIZE, packet.length);
	}
}