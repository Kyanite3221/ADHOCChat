package View;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GUIPanel extends JPanel implements ActionListener {
	
	private static final long serialVersionUID = 1L;
	protected JTextField textField;
	protected JTextArea textArea;
	protected JButton button;
	private View view;
	private String name = "";
	private String receiver = "";
	
	public GUIPanel(View view) {
		super(new GridBagLayout());
		
		this.view = view;
		
		textField = new JTextField(20);
        textField.addActionListener(this);

        textArea = new JTextArea(5, 20);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        
        button = new JButton();
        button.addActionListener(this);
        
        GridBagConstraints c = new GridBagConstraints();
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.fill = GridBagConstraints.RELATIVE;
        
        c.fill = GridBagConstraints.BOTH;
        c.gridheight = 20;
        c.gridwidth = 1;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.gridx = 0;
        c.gridy = 0;
        add(button, c);
        
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.weightx = 4.0;
        c.ipady = 40;
        c.gridx = 1;
        c.gridy = 19;
        add(textField, c);
        
        c.fill = GridBagConstraints.BOTH;
        c.gridheight = 19;
        c.gridwidth = 1;
        c.weightx = 4.0;
        c.weighty = 19.0;
        c.gridx = 1;
        c.gridy = 0;
        add(scrollPane, c);
        
        textArea.append("Please give your name\n");
        
		setFocusable(true);
	}
	
	public void actionPerformed(ActionEvent evt) {
		if(name == "") {
			name = textField.getText();
			if(name.length()<8) {
				while (name.length() < 8) {
					name += " ";
				}
			}
			else {
				name = name.substring(0, 8);
			}
			view.setName(name);
			textArea.append("name set, name is: " + name + "\n");
		}
		else {
			String text = textField.getText();
			textField.setText("");
			checkInput(text);
		}
		textField.setText("");
    }
	
	private void checkInput(String line) {
		boolean send = true;
		boolean exit = false;
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
				textArea.append("Here is a list:\n");
				textArea.append(view.getList().toString() + "\n");
				break;
			case "/NAME":
				boolean name_change = true;
				try {
					name = line.split(" ")[1];
				}
				catch (ArrayIndexOutOfBoundsException e) {
					textArea.append("Error: no name given after command\n");
					name_change = false;
				}
				if(name_change) {
					if(name.length()<8) {
						while (name.length() < 8) {
							name += " ";
						}
					}
					else {
						name = name.substring(0, 8);
					}
					view.setName(name);
					textArea.append("name set, name is: " + name + "\n");
				}
				break;
			case "/CONNECT":
				boolean rec_change = true;
				try {
					receiver = line.split(" ")[1];
				}
				catch (ArrayIndexOutOfBoundsException e) {
					textArea.append("Error: no name given after command\n");
					rec_change = false;
					receiver = "";
				}
				if(rec_change) {
					if(receiver.length()<8) {
						while (receiver.length() < 8) {
							receiver += " ";
						}
					}
					else {
						receiver = receiver.substring(0, 8);
						if(!view.containsName(receiver)) {
							textArea.append("no valid name, please give a valid name\n");
							receiver = "";
						}
					}
				}
				break;
			case "/EXIT":
				textArea.append("Leaving chat\n");
				exit = true;
				break;
			default:
				textArea.append("Unknown command\n");
				break;
			}
		}
		else if(send) {
			if(receiver != "" && view.containsName(receiver)) {
				view.addMessage(receiver, line);
				textArea.append(name + ": " + line + "\n");
				textField.selectAll();
				textArea.setCaretPosition(textArea.getDocument().getLength());
			}
			else {
				textArea.append("Error: Receiver not set or receiver left the chat\n");
			}
		}
		if(exit) {
			System.exit(0);
		}
	}
	
	public void writeMessage(String name, String message) {
		textArea.append(name + ": " + message + "\n");
	}
}
