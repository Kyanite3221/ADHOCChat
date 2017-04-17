package Controller;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.sql.Time;
import java.util.Queue;
import java.util.concurrent.*;

/**
 * Created by thomas on 12-4-17.
 */
public class LinkLayer {
	private static final int IP_HEADER_LENGTH = 16;
	private static final long DELAY_MILLIS = 300;
	private static final int PAYLOAD_BYTE = 0;

	private BlockingQueue<byte[]> inboundQueue = new LinkedBlockingQueue<>();

	private MulticastSocket socket;
	private ScheduledExecutorService timer = Executors.newScheduledThreadPool(4);

	private final InetAddress group;

	public LinkLayer(InetAddress adhocGroup, int port) throws IOException {
		this.group = adhocGroup;

		socket = new MulticastSocket(port);
		socket.joinGroup(adhocGroup);
		socket.setLoopbackMode(false);
		System.out.println(socket.getLoopbackMode());
		socket.setBroadcast(true);

		Thread receiveLoopThread = new Thread(() -> {
			while (true) {
				readPacketToQueue();
			}
		});
		receiveLoopThread.start();
	}

	public void readPacketToQueue() {
		byte[] ipBuffer = readBytes(IP_HEADER_LENGTH);

		int payload = ipBuffer[PAYLOAD_BYTE] * 256 + ipBuffer[PAYLOAD_BYTE + 1];
		if (payload <= 0) {
			//handle no payload
		}

		byte[] payloadBuffer = readBytes(payload);

		byte[] data = new byte[IP_HEADER_LENGTH + payload];
		System.arraycopy(ipBuffer, 0, data, 0, IP_HEADER_LENGTH);
		System.arraycopy(payloadBuffer, 0, data, IP_HEADER_LENGTH, payload);

		System.out.println("incoming packet added to linklayer queue");
		inboundQueue.add(data);
	}

	public byte[] readBytes(int length) {
		byte[] buffer = new byte[length];
		DatagramPacket packet = new DatagramPacket(buffer, length);

		try {
			//Receive will block until enough data is available
			socket.receive(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return buffer;
	}

	public byte[] receive() {
		return inboundQueue.poll();
	}

	public void send(byte[] bytesToSend) {
		try {

			DatagramPacket packet = new DatagramPacket(bytesToSend, bytesToSend.length, group, 3000);
			packet.setAddress(InetAddress.getByName("192.168.5.0"));
			System.out.println("sending");
			socket.send(packet);
			System.out.println("sent");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
