package org.vcell.sybil.util.gui;

import java.awt.Insets;

import javax.swing.JButton;

public class ButtonFormatter {

	public static JButton format(JButton button) {
		button.setBorderPainted(true);
		button.setMargin(new Insets(2,2,2,2));
		return button;
	}
	
}
