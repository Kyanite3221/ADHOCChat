package View;

import IPLayer.AddressMap;
import IPLayer.IPLayer;

import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class View implements Runnable {
	private Queue<Message> messageStack = new LinkedBlockingQueue<>();
	private String name;
	private String ip = "";
	private AddressMap map;
	private GUI gui;

	public View(AddressMap map) {
		this.map = map;
		gui = new GUI(this);
		new Thread(gui).start();
	}

	@Override
	public void run() {
	}

	public void writeMessage(Message message) {
		gui.writeMessage(message.getName(), message.getMessage());
	}

	public boolean hasMessage() {
		return messageStack.size() > 0;
	}


	public Message pollMessage() {
		return messageStack.poll();
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void addMessage(String receiver, String message) {
		ip = (map.checkName(name)) ? IPLayer.ipByteArrayToString(map.getIpaddress(name)): "";
		messageStack.add(new Message(ip, name, message));
	}
	
	public Collection<String> getList() {
		return map.allnames();
	}
	
	public boolean containsName(String receiver) {
		return map.checkName(receiver);
	}

}
