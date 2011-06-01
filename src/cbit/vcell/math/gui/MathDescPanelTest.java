/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.math.gui;

import cbit.vcell.math.*;
/**
 * This type was created in VisualAge.
 */
public class MathDescPanelTest {
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		MathDescPanel aMathDescPanel;
		aMathDescPanel = new MathDescPanel();
		frame.setContentPane(aMathDescPanel);
		frame.setSize(aMathDescPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.setVisible(true);
		MathDescription mathDesc = MathDescriptionTest.getExample();
		aMathDescPanel.setMathDescription(mathDesc);
	} catch (Throwable exception) {
		exception.printStackTrace(System.out);
		System.err.println("Exception occurred in main() of MathDescPanel");
	}
}
}
