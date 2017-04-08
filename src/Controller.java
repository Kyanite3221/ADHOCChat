import IPLayer.IPLayer;
import TCPLayer.*;
import View.View;

import java.util.LinkedList;

/**
 * Created by thomas on 7-4-17.
 */
public class Controller {
	public static void main(String[] args) {
		View view = new View();
		Thread viewThread = new Thread(view);

		TCPLayer tcpLayer = new TCPLayer();
		IPLayer ipLayer = new IPLayer();

		while (true) {
			if (view.hasMessage()) {
				String plaintext = view.pollMessage();

				//TODO: we don't know what reciever entails yet
				String receiver = "";

				byte[] data = encodeToByteArray(plaintext);

				tcpLayer.createDataMessage(data);
				tcpLayer.createTCPMessage();
				LinkedList<TCPMessage> tcpDataList = tcpLayer.tick();

				for(TCPMessage tcpData : tcpDataList){
					byte[] ipData = ipLayer.addIPHeader(tcpData.toByte());
					//TODO: figure out how the fuck to send stuff aaaaahhhhh
					send(ipData);
				}


			}
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
