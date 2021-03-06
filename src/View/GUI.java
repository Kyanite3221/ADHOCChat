package View;

import javax.swing.*;

import IPLayer.AddressMap;

public class GUI extends JFrame implements Runnable{
	
	private static final long serialVersionUID = 1L;
	private final int WINDOW = 640;
	private View view;
	private GUIPanel panel;
	
	public static void main(String[] args) {
		new Thread(new GUI(new View(new AddressMap()))).start();
	}
	
	public GUI(View view) {
		this.view = view;
	}
	
	@Override
	public void run() {
		setSize(WINDOW, WINDOW);
		panel = new GUIPanel(view);
        add(panel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
	}
	
	public void writeMessage(String name, String message) {
		panel.writeMessage(name, message);
	}
	
}
