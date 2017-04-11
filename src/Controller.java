import View.View;

import java.io.*;
import java.net.*;
import java.util.Arrays;

/**
 * Created by thomas on 7-4-17.
 */
public class Controller {
	private static final String ADHOC_ADDRESS = "192.168.5.0";
	private static final byte[] ADHOC_GROUP = new byte[] {
			(byte) 192,
			(byte) 168,
			(byte) 5,
			(byte) 0};

	private static final int DUMMY_PORT = 3000;
	private static final int IP_HEADER_LENGTH = 16;

	public static void main(String[] args) {
		multicastLoop();
	}

	private static void multicastLoop() {
//		View view = new View();
//		Thread viewThread = new Thread(view);
//		viewThread.start();

		try {
			InetAddress group = InetAddress.getByAddress(ADHOC_GROUP);
			MulticastSocket socket = new MulticastSocket(DUMMY_PORT);
			socket.joinGroup(group);

			Runnable r = () -> {
				while (true) {
					byte[] ipBuffer = new byte[IP_HEADER_LENGTH];
					DatagramPacket ipData = new DatagramPacket(ipBuffer, IP_HEADER_LENGTH);
					try {
						socket.receive(ipData);
					} catch (IOException e) {
						e.printStackTrace();
					}
					System.out.println(Arrays.toString(ipData.getData()));

				}
			};

			Thread t = new Thread(r);
			t.start();

			while (true) {
				byte[] outBytes = new byte[]{0, 0, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16};
				DatagramPacket outPacket = new DatagramPacket(outBytes, outBytes.length);
				socket.send(outPacket);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void blockingReceiveLoop(DataInputStream in) {
		try {

			while (true) {
				byte[] packet = readPacket(in);
				System.out.println(Arrays.toString(packet));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static byte[] readPacket(DataInputStream in) throws IOException {
		byte[] ipHeaderBuffer = new byte[IP_HEADER_LENGTH];
		in.read(ipHeaderBuffer);

		int payload = ipHeaderBuffer[0] * 256 + ipHeaderBuffer[1];
		byte[] payloadBuffer = new byte[payload];

		if (payload > 0) {
			in.read(payloadBuffer);
		}

		byte[] packet = new byte[IP_HEADER_LENGTH + payload];
		System.arraycopy(ipHeaderBuffer, 0, packet, 0, IP_HEADER_LENGTH);
		System.arraycopy(payloadBuffer, 0, packet, IP_HEADER_LENGTH, payload);

		return packet;
	}

	private static void clientLoop() {
		InetAddress a = null;
		try {
			a = InetAddress.getByAddress(new byte[] {(byte) 192, (byte) 168, 5, 2});
			Socket s = new Socket(a, DUMMY_PORT);

			DataOutputStream out = new DataOutputStream(s.getOutputStream());
			DataInputStream in = new DataInputStream((s.getInputStream()));

			blockingReceiveLoop(in);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void serverLoop() {
		try {
			ServerSocket ss = new ServerSocket(3000);
			Socket s = ss.accept();

			DataOutputStream out = new DataOutputStream(s.getOutputStream());
			DataInputStream in = new DataInputStream((s.getInputStream()));

			System.out.println("Connection accepted: " + s);
			//blockingReceiveLoop(in);

			try {
				Thread.sleep(1000);
				System.out.println("sending...");
				out.write(new byte[] {0,0,1,2,3,4,5,6,7,8,9,1,2,3,4,5});
				Thread.sleep(10000);
				s.close();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

//	public static void main(String[] args) {
//
//		DatagramSocket socket;
//		try {
//			View view = new View();
//			Thread viewThread = new Thread(view);
//			viewThread.start();
//
//			TCPLayer tcpLayer = new TCPLayer();
//			IPLayer ipLayer = new IPLayer();
//
//
//
////			while (true) {
////				if (view.hasMessage()) {
////					String plaintext = view.pollMessage();
////
////					//TODO: we don't know what reciever entails yet
////					String reciever = "";
////
////					byte[] data = encodeToByteArray(plaintext);
////
////					tcpLayer.createDataMessage(data);
////					tcpLayer.createTCPMessage();
////					LinkedList<TCPMessage> tcpDataList = tcpLayer.tick();
////
////					for(TCPMessage tcpData : tcpDataList){
////						byte[] ipData = ipLayer.addIPHeader(tcpData.toByte());
////						//TODO: figure out how the fuck to send stuff aaaaahhhhh
////
////						socket.send(new DatagramPacket(ipData, ipData.length, addr, DUMMY_PORT));
////					}
////				}
////
////				socket.receive(recieveBuffer);
////				System.out.println(Arrays.toString(recieveBuffer.getData()));
////			}
//
//		} catch (SocketException e) {
//			e.printStackTrace();
//		} catch (UnknownHostException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}

	//TODO: figure out how the fuck to send stuff aaaaahhhhh
	private static void send(byte[] ipData) {

	}

	//TODO: this method needs a better place. we absolutely don't want it as a static method in the controller
	private static byte[] encodeToByteArray(String plaintext) {
		return null;
	}
}
