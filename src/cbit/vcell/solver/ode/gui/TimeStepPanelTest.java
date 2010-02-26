package cbit.vcell.solver.ode.gui;

import cbit.vcell.math.MathDescriptionTest;
import cbit.vcell.solver.Simulation;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * Insert the type's description here.
 * Creation date: (4/5/01 3:33:10 PM)
 * @author: Jim Schaff
 */
public class TimeStepPanelTest {
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		TimeStepPanel aTimeStepPanel;
		aTimeStepPanel = new TimeStepPanel();
		frame.setContentPane(aTimeStepPanel);
		frame.setSize(aTimeStepPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);
		Simulation simulation = new Simulation(MathDescriptionTest.getOdeExampleWagner());
		aTimeStepPanel.setSolverTaskDescription(simulation.getSolverTaskDescription());
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}
}
