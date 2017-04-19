package View;

//import java.awt.Color;
//import java.awt.Graphics;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GUIPanel extends JPanel implements ActionListener {
	
	private static final long serialVersionUID = 1L;
	protected JTextField textField;
	protected JTextArea textArea;
	protected JButton button;
	
	public GUIPanel() {
		super(new GridBagLayout());
		
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
        
		setFocusable(true);
	}
	
	@Override
    /*protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(Color.CYAN);
		g.fillRect(50, 50, 20, 20);
	}*/
	
	public void actionPerformed(ActionEvent evt) {
		String text = textField.getText();
        textArea.append("Richard: " + text + "\n");
        textField.selectAll();
        textArea.setCaretPosition(textArea.getDocument().getLength());
    }
}
