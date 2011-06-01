/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.solver.ode.gui;

/**
 * Insert the type's description here.
 * Creation date: (4/5/01 3:37:02 PM)
 * @author: Jim Schaff
 */
public class TimeBoundsPanelTest {
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		TimeBoundsPanel aTimeBoundsPanel;
		aTimeBoundsPanel = new TimeBoundsPanel();
		frame.setContentPane(aTimeBoundsPanel);
		frame.setSize(aTimeBoundsPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.show();
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);
		cbit.vcell.solver.Simulation simulation = new cbit.vcell.solver.Simulation(
			cbit.vcell.math.MathDescriptionTest.getOdeExampleWagner());
		aTimeBoundsPanel.setTimeBounds(simulation.getSolverTaskDescription().getTimeBounds());
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}
}
