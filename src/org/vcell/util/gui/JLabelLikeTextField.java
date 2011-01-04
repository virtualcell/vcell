package org.vcell.util.gui;

import java.awt.Color;

import javax.swing.JTextField;
import javax.swing.UIManager;

@SuppressWarnings("serial")
public class JLabelLikeTextField extends JTextField {
	public JLabelLikeTextField() {
		this(null);
	}
	public JLabelLikeTextField(String text) {
		super(text);
		setEditable(false);
		setBorder(null);
		setForeground(UIManager.getColor("Label.foreground"));
		setBackground(Color.white);
		setFont(UIManager.getFont("Label.font"));
	}
}
