import IPLayer.IPLayer;
import TCPLayer.TCPLayer;
import View.View;
import View.Message;
import TCPLayer.TCPMessage;

import IPLayer.AddressMap;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
	private static TCPLayer tCPLayer;
	private static View view;

	private static ScheduledExecutorService timer = Executors.newScheduledThreadPool(1);

	private static AddressMap addressMap;

	public static void main(String[] args) {
		addressMap = new AddressMap();
		try {
			InetAddress inetAddress = InetAddress.getByAddress(ADHOC_GROUP);
			linkLayer = new LinkLayer(inetAddress, DUMMY_PORT);
			ipLayer = new IPLayer();
			tCPLayer = new TCPLayer();
		} catch (IOException e) {
			e.printStackTrace();
		}

		addressMap.setIpNameTable(new byte[] {(byte) 198, (byte) 168, (byte) 5, (byte) 1}, "OldPC___");
		addressMap.setIpNameTable(new byte[] {(byte) 198, (byte) 168, (byte) 5, (byte) 2}, "GoodPC__");

		view = new View(addressMap);
		String name = view.getName();

		Thread viewThread = new Thread(view);
		viewThread.start();

//		for (byte[] address: addressMap.allIPAddresses()) {
//
//		}

		timer.scheduleAtFixedRate(() -> {
			receiveFromLinkLayer();
			sendFromApplicationLayer();
			sendFromTCPLayer();
		}, 0, 10, TimeUnit.MILLISECONDS);
	}

	public static void receiveFromLinkLayer() {
		byte[] incoming = linkLayer.receive();

		if (incoming != null) {
			byte[] source = ipLayer.getSource(incoming);
			String sourceString = IPLayer.ipByteArrayToString(source);

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
					TCPMessage tcpMessage = tCPLayer.recievedMessage(payloadArray, sourceString);

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
			System.out.println(message.toString());
			tCPLayer.createMessageData(messageBytes, message.getIp());
		}
	}

	public static void sendFromTCPLayer() {
		List<TCPMessage> broadcastList = tCPLayer.tick();
		for (TCPMessage message : broadcastList) { //this exclusively sends data that was send to the "broadcast" TCPstream.
			byte[] ipMessage = ipLayer.addIPHeader(message.toByte(), IPLayer.ipStringToByteArray(ADHOC_ADDRESS));
			linkLayer.send(ipMessage);
		}

		HashMap<String, LinkedList<TCPMessage>> list = tCPLayer.allTick();
		if (list != null) {
			for (Map.Entry<String, LinkedList<TCPMessage>> pair : list.entrySet()) {
				if (pair.getValue() != null) {
//					Iterator<TCPMessage> iter = pair.getValue().iterator();
//
//					while (iter.hasNext()) {
//						TCPMessage message = iter.next();
//						byte[] ipAddress = IPLayer.ipStringToByteArray(pair.getKey());
//						byte[] ipMessage = ipLayer.addIPHeader(message.toByte(), ipAddress);
//
//						//If there was a TCPMessage, it would be printed here
//						System.out.println(Arrays.toString(ipMessage));
//						System.out.println(Arrays.toString(message.getPayload()));
//
//						//linkLayer.send(ipMessage);
//						iter.remove();
//					}

					for(TCPMessage message : pair.getValue()){
						System.out.println(message);
					}
				}
			}
		}
	}
}