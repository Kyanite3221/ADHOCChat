package View;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

public class View implements Runnable {
	private Queue<Message> messageStack = new LinkedBlockingQueue<>();
	private String name;
	private String ip = "";

	public View() {}

	@Override
	public void run() {
		waitForInput();
	}

	private void waitForInput() {
		boolean exit = false;
		Scanner in = new Scanner(System.in);
		System.out.println("Please give your name");
		name = in.nextLine();
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
						System.out.println("Here is a list");
						break;
					case "/NAME":
						System.out.println("Please give a new name");
						name = in.nextLine();
						break;
					case "/CONNECT":
						System.out.println("Give the ip of the person to contact");
						ip = in.nextLine();
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

}
