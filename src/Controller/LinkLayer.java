package Controller;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.sql.Time;
import java.util.Arrays;
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
		socket.setBroadcast(true);

		Thread receiveLoopThread = new Thread(() -> {
			while (true) {
				readPacketToQueue();
			}
		});
		receiveLoopThread.start();
	}

	public void readPacketToQueue() {
		byte[] ipBuffer = readBytes(1000);

		int payload = ((ipBuffer[PAYLOAD_BYTE] + 256) % 256) * 256 + ((ipBuffer[PAYLOAD_BYTE + 1] + 256) % 256);
		if (payload <= 0) {
			//handle no payload
		}

		inboundQueue.add(Arrays.copyOfRange(ipBuffer, 0, IP_HEADER_LENGTH + payload));
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
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
