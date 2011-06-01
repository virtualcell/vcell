/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.modelopt.gui;
/**
 * Insert the type's description here.
 * Creation date: (8/23/2005 5:20:49 PM)
 * @author: Jim Schaff
 */
public class OptTestPanelTest {
/**
 * OptTestPanelTest constructor comment.
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		OptTestPanel aOptTestPanel;
		aOptTestPanel = new OptTestPanel();
		frame.setContentPane(aOptTestPanel);
		frame.setSize(aOptTestPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.show();
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);

		aOptTestPanel.setParameterEstimationTask(cbit.vcell.modelopt.ParameterEstimationTaskTest.getExample());
		aOptTestPanel.setOptimizationService(new cbit.vcell.opt.solvers.LocalOptimizationService());
		
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}
}
