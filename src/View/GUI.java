package View;

import javax.swing.JFrame;

public class GUI extends JFrame implements Runnable{
	
	private static final long serialVersionUID = 1L;
	private final int WINDOW = 640;
	
	public static void main(String[] args) {
		new Thread(new GUI()).start();
	}
	
	public GUI() {
	}
	
	@Override
	public void run() {
		setSize(WINDOW, WINDOW);
        add(new GUIPanel());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
	}
	
}
