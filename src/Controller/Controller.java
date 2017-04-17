package Controller;

import IPLayer.AddressMap;
import IPLayer.IPLayer;
import Routing.RoutingProtocol;
import TCPLayer.TCPLayer;
import TCPLayer.TCPMessage;
import View.Message;
import View.View;

import java.io.IOException;
import java.net.InetAddress;
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

	private static int PC_NUMBER;

	private static final int DUMMY_PORT = 3000;

	private static LinkLayer linkLayer;
	private static IPLayer ipLayer;
	private static TCPLayer tCPLayer;
	private static RoutingProtocol routing;
	private static View view;

	private static ScheduledExecutorService timer = Executors.newScheduledThreadPool(1);

	private static AddressMap addressMap;

	public static void main(String[] args) {
		PC_NUMBER = Integer.parseInt(args[0]);

		byte[] myIP = IPLayer.ipStringToByteArray(ADHOC_ADDRESS);
		myIP[3] = (byte) PC_NUMBER;

		addressMap = new AddressMap();
		view = new View(addressMap);
		String name = view.getName();

		try {
			InetAddress inetAddress = InetAddress.getByAddress(ADHOC_GROUP);
			linkLayer = new LinkLayer(inetAddress, DUMMY_PORT);
			ipLayer = new IPLayer(myIP);
			tCPLayer = new TCPLayer();
			routing = new RoutingProtocol(name, myIP, addressMap);
		} catch (IOException e) {
			e.printStackTrace();
		}

		Thread viewThread = new Thread(view);
		viewThread.start();

		timer.scheduleAtFixedRate(() -> {
			receiveFromLinkLayer();
			sendFromApplicationLayer();
			sendFromTCPLayer();
		}, 0, 10, TimeUnit.MILLISECONDS);

		timer.scheduleAtFixedRate(() -> {
			sendPing();
		}, 0, 1000, TimeUnit.MILLISECONDS);
	}

	private static void sendPing() {
		byte[] routingPacket = routing.send();
		tCPLayer.createPingMessage(routingPacket);
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

					TCPMessage tcpMessage;
					if (ipLayer.isBroadcast(ipLayer.getDestination(incoming))) {
						tcpMessage = tCPLayer.recievedMessage(payloadArray);
					} else {
						tcpMessage = tCPLayer.recievedMessage(payloadArray, sourceString);
					}

					if (tcpMessage != null) {
						switch (tcpMessage.getPort()) {
							case 0:
								routing.update(tcpMessage.getPayload(), source);

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
			//System.out.println(message.toString());
			tCPLayer.createMessageData(messageBytes, message.getIp());
		}
	}

	public static void sendFromTCPLayer() {
		List<TCPMessage> broadcastList = tCPLayer.tick();
		for (TCPMessage message : broadcastList) { //this exclusively sends data that was send to the "broadcast" TCPstream.
//			System.out.println("tcpd ping:"+Arrays.toString(message.toByte()));
			byte[] ipMessage = ipLayer.addIPHeader(message.toByte(), IPLayer.ipStringToByteArray(ADHOC_ADDRESS));
//			System.out.println("ipMessage:" +Arrays.toString(ipMessage));
			linkLayer.send(ipMessage);
		}

		HashMap<String, LinkedList<TCPMessage>> list = tCPLayer.allTick();
		if (list != null) {
			for (Map.Entry<String, LinkedList<TCPMessage>> pair : list.entrySet()) {
				if (pair.getValue() != null) {
					for(TCPMessage message : pair.getValue()){
						byte[] ipAddress = IPLayer.ipStringToByteArray(pair.getKey());
						byte[] ipMessage = ipLayer.addIPHeader(message.toByte(), ipAddress);
						linkLayer.send(ipMessage);

					}
				}
			}
		}
	}
}