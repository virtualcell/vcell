package org.vcell.sybil.util.gui;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

public class ButtonFormatter {

	public static JButton format(JButton button) {
		button.setContentAreaFilled(true);
		button.setBorderPainted(true);
		button.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createCompoundBorder(new EtchedBorder(EtchedBorder.RAISED), new EtchedBorder()), new EmptyBorder(3,5,3,5)));
		return button;
	}
	
}
