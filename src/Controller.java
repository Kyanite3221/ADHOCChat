import IPLayer.IPLayer;
import TCPLayer.TCPLayer;
import View.View;

import java.io.*;
import java.net.*;

/**
 * Created by thomas on 7-4-17.
 */
public class Controller {
	private static final String ADHOC_ADDRESS = "192.168.5.0";
	private static final byte[] ADHOC_GROUP = new byte[] {
			(byte) 228,
			(byte) 0,
			(byte) 0,
			(byte) 0};

	private static final int DUMMY_PORT = 3000;
	private static final int IP_HEADER_LENGTH = 16;

	private static LinkLayer linkLayer;
	private static IPLayer ipLayer;
	private static TCPLayer tcpLayer;

	public static void main(String[] args) {
		View view = new View();
		Thread viewThread = new Thread(view);
		viewThread.start();

		try {
			InetAddress adhocGroup = InetAddress.getByAddress(ADHOC_GROUP);
			LinkLayer linkLayer = new LinkLayer(adhocGroup, DUMMY_PORT);

			while (true) {

			}

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void receiveFromLinkLayer(byte[] packet) {
		byte[] incoming = linkLayer.receive();
		if (incoming != null) {
			switch (ipLayer.handlePacket(incoming)) {
				case IGNORE:
					//this packet is not for us
					break;
				case FORWARD:
					//this packet is not for us, but we can forward it
					//iplayer.forward SOMEHOW
					break;
				case DELIVER:
					byte[] payloadArray = ipLayer.removeHeader(incoming);
					tcpLayer.recievedMessage(payloadArray, ipLayer.getSourceAsString(incoming));
					break;
			}
		}
	}

	private static byte[] encodeToByteArray(String plaintext) {
		return null;
	}
}
