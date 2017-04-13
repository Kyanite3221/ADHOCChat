package IPLayer;


import java.lang.reflect.Array;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

/**
 * Created by thomas on 7-4-17.
 */
public class IPLayer {

	public static final int HEADER_SIZE = 16;
	public static final int MAX_PACKET_SIZE = 2;
	private static final short TTL = 5;

	/**
	 * bytes 0-1: 	payload length
	 * byte 2:		header length
	 * byte 3:		TTL
	 *
	 * bytes 4-7:	next hop ip
	 * bytes 8-11:	destination ip
	 * byte 12-15:	source ip
	 */
	public byte[] addIPHeader(byte[] tcpData, byte[] destination) {
		byte[] dataWithIPHeader = new byte[HEADER_SIZE + tcpData.length];
		dataWithIPHeader[0] = (byte) (tcpData.length / 256);
		dataWithIPHeader[1] = (byte) (tcpData.length % 256);
		dataWithIPHeader[2] = (byte) HEADER_SIZE;
		dataWithIPHeader[3] = (byte) TTL;

		//nexthop
		System.arraycopy(destination, 0, dataWithIPHeader, 8, 4);
		System.arraycopy(ipStringToByteArray(getOwnIP()), 0, dataWithIPHeader, 12, 4);

		System.arraycopy(tcpData, 0, dataWithIPHeader, 16, tcpData.length);

		return dataWithIPHeader;
	}

	public String getOwnIP() {
		try {
			return InetAddress.getLocalHost().toString();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static byte[] ipStringToByteArray(String inet) {
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

	public static String getIPByteArrayAsString(byte[] incoming) {
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

	public byte[] addHeader(byte[] packet, String destinationString) {
		int payloadLength = packet.length;
		short headerSize = HEADER_SIZE;
		short ttl = TTL;

		//byte[] nextHop = routingData.getNextHop(destinationString);
		byte[] destination = ipStringToByteArray(destinationString);
		byte[] source = ipStringToByteArray(getOwnIP());

		byte[] packetWithIPHeader = new byte[payloadLength + headerSize];
		packetWithIPHeader[0] = (byte) (payloadLength / 256);
		packetWithIPHeader[1] = (byte) (payloadLength % 256);
		packetWithIPHeader[2] = (byte) headerSize;
		packetWithIPHeader[3] = (byte) ttl;

		//System.arraycopy(nextHop, 0, packetWithIPHeader, 4, 4);
		System.arraycopy(destination, 0, packetWithIPHeader, 8, 4);
		System.arraycopy(source, 0, packetWithIPHeader, 12, 4);

		System.arraycopy(packet, 0, packetWithIPHeader, 16, payloadLength);

		return packetWithIPHeader;
	}

	public byte[] getSource(byte[] packet) {
		return Arrays.copyOfRange(packet, 12, 16);
	}
}