package View;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

import IPLayer.AddressMap;

public class View implements Runnable {
	private Queue<Message> messageStack = new LinkedBlockingQueue<>();
	private String name;
	private String ip = "";
	private Scanner in = new Scanner(System.in);
	private AddressMap map;

	public View(AddressMap map) {
		this.map = map;
		//new GUI().run();
		System.out.println("Please give your name");
		name = in.nextLine();
		if(name.length()<8) {
			for(int i = name.length(); i < 8; i++) {
				name.concat(" ");
			}
		}
		else {
			name = name.substring(0, 7);
		}
	}

	@Override
	public void run() {
		waitForInput();
	}

	private void waitForInput() {
		boolean exit = false;
		while(true) {
			if(in.hasNextLine()) {
				boolean send = true;
				String line = in.nextLine();
				char first;
				try {
					first = line.toCharArray()[0];
				}
				catch (ArrayIndexOutOfBoundsException e) {
					line = " ";	//give something to write
					first = 'a';	//set first to something other than '/'
					send = false;
				}
				if(first=='/') {
					String command = line.split(" ")[0];
					switch(command) {
					case "/LIST":
						System.out.println("Here is a list:");
						System.out.println(map.allnames());
						break;
					case "/NAME":
						System.out.println("Please give a new name");
						name = in.nextLine();
						break;
					case "/CONNECT":
						System.out.println("Give the name of the person to contact");
						ip = in.nextLine();
						ip = (map.checkName(ip)) ? Arrays.toString(map.getIpaddress(ip)): "";
						break;
					case "/EXIT":
						System.out.println("Leaving chat");
						exit = true;
						break;
					default:
						System.out.println("Sorry, I do not know that command.....");
						break;
					}
				}
				else if(send) {
					messageStack.add(new Message(ip, name, line));
					System.out.println(name + ": " + line);
				}
			}
			if(exit) {
				System.exit(0);
				break;
			}
		}
		in.close();
	}

	public void writeMessage(Message message) {
		System.out.println(message.getName() + ": " + message.getMessage());
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

}
