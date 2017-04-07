import TCPLayer.TCPLayer;
import View.View;

/**
 * Created by thomas on 7-4-17.
 */
public class Controller {
	public static void main(String[] args) {
		View view = new View();
		Thread viewThread = new Thread(view);
		TCPLayer tcpLayer = new TCPLayer();

		while (true) {
			if (view.hasMessage()) {
				String plaintext = view.pollMessage();

				//TODO: we don't know what reciever entails yet
				String receiver = "";

				byte[] data = encodeToByteArray(plaintext);
				byte[] tcpData = tcpLayer.createDataMessage(data, receiver);
			}
		}
	}

	//TODO: this method needs a better place. we absolutely don't want it as a static method in the controller
	private static byte[] encodeToByteArray(String plaintext) {
		return null;
	}
}
