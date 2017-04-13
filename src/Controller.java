import IPLayer.IPLayer;
import TCPLayer.TCPLayer;
import View.View;
import View.Message;
import TCPLayer.TCPMessage;

import IPLayer.AddressMap;

import java.io.*;
import java.net.*;
import java.util.List;

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
	private static View view;

	private static AddressMap addressMap;

	public static void main(String[] args) {
		addressMap = new AddressMap();

		addressMap.setIpNameTable(new byte[] {(byte) 198, (byte) 168, (byte) 5, (byte) 1}, "OldShittyPc");
		addressMap.setIpNameTable(new byte[] {(byte) 198, (byte) 168, (byte) 5, (byte) 2}, "BetterLaptop");


		view = new View();
		String name = view.getName();

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

	public static void receiveFromLinkLayer() {
		byte[] incoming = linkLayer.receive();

		if (incoming != null) {
			byte[] source = ipLayer.getSource(incoming);
			String sourceString = IPLayer.getIPByteArrayAsString(source);

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
					TCPMessage tcpMessage = tcpLayer.recievedMessage(payloadArray, sourceString);

					if (tcpMessage != null) {
						addressMap.setIpNameTable(ipLayer.getSource(incoming));
						switch (tcpMessage.getPort()) {
							case 0:
								//routing code
							case 2:
								Message message = new Message(sourceString, addressMap.getName(sourceString),
										new String(tcpMessage.getPayload()));
							case 3:
								//file share code
						}
					}
					break;
			}
		}
	}

	public static void sendFromApplicationLayer() {
		if (view.hasMessage()) {
			Message message = view.pollMessage();
			byte[] messageBytes = message.getMessage().getBytes();
			TCPMessage tcpMessage = tcpLayer.createMessageData(messageBytes, message.getIp());
		}
	}

	public static void sendFromTCPLayer() {
		List<TCPMessage> broadcastList = tcpLayer.tick();
		for (TCPMessage message : broadcastList) {
			byte[] ipMessage = ipLayer.addIPHeader(message.toByte(), IPLayer.ipStringToByteArray(ADHOC_ADDRESS));
			linkLayer.send(ipMessage);
		}

		for (byte[] ipAddress : addressMap.allIPAddresses()) {
			String ipString = IPLayer.getIPByteArrayAsString(ipAddress);
			List<TCPMessage> list = tcpLayer.tick(ipString);
			for (TCPMessage message : list) {
				byte[] ipMessage = ipLayer.addIPHeader(message.toByte(), ipAddress);
				linkLayer.send(ipMessage);
			}
		}
	}
}
