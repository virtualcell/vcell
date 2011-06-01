/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.model.gui;

import cbit.vcell.model.*;
/**
 * This type was created in VisualAge.
 */
public class ParameterPanelTest {
/**
 * ParameterPanelTest constructor comment.
 */
public ParameterPanelTest() {
	super();
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		ParameterPanel aParameterPanel;
		aParameterPanel = new ParameterPanel();
		frame.setContentPane(aParameterPanel);
		frame.setSize(aParameterPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.setVisible(true);
		Model model = ModelTest.getExample();
		SimpleReaction sr = (SimpleReaction)model.getReactionSteps()[0];
		Kinetics kinetics = sr.getKinetics();
		aParameterPanel.setKinetics(kinetics);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of java.awt.Panel");
		exception.printStackTrace(System.out);
	}
}
}
