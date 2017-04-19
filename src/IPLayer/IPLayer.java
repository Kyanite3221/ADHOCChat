package IPLayer;


import Routing.RoutingProtocol;

import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * Created by thomas on 7-4-17.
 */
public class IPLayer {

	public static final int HEADER_SIZE = 16;
	public static final int MAX_PACKET_SIZE = 2;
	private static final short TTL = 3;

	private byte[] ownIP;
	private RoutingProtocol routing;

	public IPLayer(byte[] ownIP, RoutingProtocol routing) {
		this.ownIP = ownIP;
		this.routing = routing;
	}

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


		byte[] nextHop = routing.getnhop(ipByteArrayToString(destination));
		if (nextHop != null) {
			System.arraycopy(nextHop, 0, dataWithIPHeader, 4, 4);
		}
		System.arraycopy(destination, 0, dataWithIPHeader, 8, 4);
		System.arraycopy(getOwnIPAsByteArray(), 0, dataWithIPHeader, 12, 4);

		System.arraycopy(tcpData, 0, dataWithIPHeader, 16, tcpData.length);

		return dataWithIPHeader;
	}

	public String getOwnIPAsString() {
		return ipByteArrayToString(ownIP);
	}

	public byte[] getOwnIPAsByteArray() {
		return ownIP;
	}

	public static byte[] ipStringToByteArray(String inet) {
		String[] strings = inet.split(Pattern.quote("."));
		byte[] bytes = new byte[strings.length];
		for (int i = 0; i < strings.length; i++) {
			bytes[i] = (byte) Integer.parseInt(strings[i]);
		}
		return bytes;
	}

	public static String ipByteArrayToString(byte[] ip) {
		String ipString = "";
		for (int i = 0; i < 4; i++) {
			ipString += ip[i];
			if (i != 3) {
				ipString += ".";
			}
		}
		return ipString;
	}

	public boolean isOwnIP(byte[] address) {
		boolean differenceFound = false;
		for (int i = 0; i < 4; i++) {
			if (address[i] != ownIP[i]) {
				differenceFound = true;
			}
		}
		return !differenceFound;
	}

	public boolean isBroadcast(byte[] address) {
		boolean differenceFound = false;
		for (int i = 0; i < 3; i++) {
			if (address[i] != ownIP[i]) {
				differenceFound = true;
			}
		}
		if (address[3] != 0) {
			differenceFound = true;
		}
		return !differenceFound;
	}

	public enum IPDecision {
		IGNORE,
		FORWARD,
		DELIVER
	}

	public IPDecision handlePacket(byte[] packet) {
		byte[] nextHop = Arrays.copyOfRange(packet, 4, 8);
		byte[] destination = Arrays.copyOfRange(packet, 8, 12);
		byte[] sender = Arrays.copyOfRange(packet,12, 16);

		//System.out.println("we have recieved a packet with nexthop" + Arrays.toString(nextHop));

		if (isOwnIP(sender) || getTTL(packet) <= 0) {
			return IPDecision.IGNORE;
		} else if (isOwnIP(destination) || isBroadcast(destination)) {
			return IPDecision.DELIVER;
		//} else if (isOwnIP(nextHop)) {
		//	return IPDecision.FORWARD;
		} else {
			return IPDecision.FORWARD;
		}
	}

	private int getTTL(byte[] packet) {
		return (int) packet[3];
	}

	public byte[] removeHeader(byte[] packet) {
		return Arrays.copyOfRange(packet, HEADER_SIZE, packet.length);
	}

	public void forward(byte[] packet) {
		byte[] destination = getDestination(packet);
		byte[] nextHop = routing.getnhop(IPLayer.ipByteArrayToString(destination));
		packet[3] = (byte) (((int) packet[3]) -1);
		System.arraycopy(nextHop, 0, packet, 4, 4);

	}

	public byte[] addHeader(byte[] packet, String destinationString) {
		int payloadLength = packet.length;
		short headerSize = HEADER_SIZE;
		short ttl = TTL;

		//byte[] nextHop = routingData.getNextHop(destinationString);
		byte[] destination = ipStringToByteArray(destinationString);
		byte[] source = ownIP;

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

	public byte[] getDestination(byte[] packet) {
		return Arrays.copyOfRange(packet, 8, 12);
	}
}