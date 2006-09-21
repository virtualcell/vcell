package cbit.vcell.solver.ode.gui;

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
		frame.show();
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);
		cbit.vcell.simulation.Simulation simulation = new cbit.vcell.simulation.Simulation(
			cbit.vcell.math.MathDescriptionTest.getOdeExampleWagner());
		aTimeStepPanel.setTimeStep(simulation.getSolverTaskDescription().getTimeStep());
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}
}
