/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.numericstest;

/**
 * Insert the type's description here.
 * Creation date: (3/26/01 1:43:01 PM)
 * @author: Jim Schaff
 */
public class SolutionTemplatePanelTest {
/**
 * Starts the application.
 * @param args an array of command-line arguments
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		cbit.vcell.numericstest.gui.SolutionTemplatePanel solnTemplatePanel = new cbit.vcell.numericstest.gui.SolutionTemplatePanel();
		frame.setContentPane(solnTemplatePanel);
		frame.setSize(solnTemplatePanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		cbit.vcell.math.MathDescription mathDescription = cbit.vcell.math.MathDescriptionTest.getExample();
		cbit.vcell.numericstest.ConstructedSolutionTemplate constructedSolutionTemplate = new cbit.vcell.numericstest.ConstructedSolutionTemplate(mathDescription);
		solnTemplatePanel.setConstructedSolutionTemplate(constructedSolutionTemplate);
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}

}
}
