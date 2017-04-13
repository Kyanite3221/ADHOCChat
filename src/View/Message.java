package View;

public class Message {
	private String ip;
	private String name;
	private String message;
	
	public Message(String ip, String name, String message) {
		this.ip = ip;
		this.name = name;
		this.message = message;
	}
	
	public String getIp() {
		return ip;
	}
	
	public String getName() {
		return name;
	}
	
	public String getMessage() {
		return message;
	}
	
	public boolean emptyIp() {
		return (ip=="");
	}
	
	@Override
	public String toString() {
		return ip + " " + name + " " + message;
	}

}
