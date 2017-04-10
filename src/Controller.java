import IPLayer.*;
import TCPLayer.*;
//import View.View;

import java.io.IOException;
import java.net.*;

import java.util.Arrays;
import java.util.LinkedList;

/**
 * Created by thomas on 7-4-17.
 */
public class Controller {
	private static final String ADHOC_ADDRESS = "192.168.5.0";
	private static final int DUMMY_PORT = 3000;

	public static void main(String[] args) {

		DatagramSocket socket;
		try {
			InetAddress addr = InetAddress.getByName(ADHOC_ADDRESS);
			socket = new DatagramSocket();
			socket.connect(addr, DUMMY_PORT);

			DatagramPacket recieveBuffer =
					new DatagramPacket(new byte[IPLayer.MAX_PACKET_SIZE], IPLayer.MAX_PACKET_SIZE);

			//View view = new View();
			//Thread viewThread = new Thread(view);

			TCPLayer tcpLayer = new TCPLayer();
			IPLayer ipLayer = new IPLayer();
			AddressMap addressmap = new AddressMap();



//			while (true) {
//				if (view.hasMessage()) {
//					String plaintext = view.pollMessage();
//
//					//TODO: we don't know what reciever entails yet
//					String reciever = "";
//
//					byte[] data = encodeToByteArray(plaintext);
//
//					tcpLayer.createDataMessage(data);
//					tcpLayer.createTCPMessage();
//					LinkedList<TCPMessage> tcpDataList = tcpLayer.tick();
//
//					for(TCPMessage tcpData : tcpDataList){
//						byte[] ipData = ipLayer.addIPHeader(tcpData.toByte());
//						//TODO: figure out how the fuck to send stuff aaaaahhhhh
//
//						socket.send(new DatagramPacket(ipData, ipData.length, addr, DUMMY_PORT));
//					}
//				}
//
//				socket.receive(recieveBuffer);
//				System.out.println(Arrays.toString(recieveBuffer.getData()));
//			}
			socket.send(new DatagramPacket(new byte[] {0,0,0,0,0}, 5, addr, DUMMY_PORT));

			while (true) {
				socket.receive(recieveBuffer);
				System.out.println("Hello world");
				System.out.println("la");
				System.out.println(Arrays.toString(recieveBuffer.getData()));
			}
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//TODO: figure out how the fuck to send stuff aaaaahhhhh
	private static void send(byte[] ipData) {

	}

	//TODO: this method needs a better place. we absolutely don't want it as a static method in the controller
	private static byte[] encodeToByteArray(String plaintext) {
		return null;
	}
}
