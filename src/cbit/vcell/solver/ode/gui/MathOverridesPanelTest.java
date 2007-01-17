package cbit.vcell.solver.ode.gui;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * Insert the type's description here.
 * Creation date: (3/26/01 1:43:01 PM)
 * @author: Jim Schaff
 */
public class MathOverridesPanelTest {
/**
 * Starts the application.
 * @param args an array of command-line arguments
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		MathOverridesPanel mathOverridesPanel = new MathOverridesPanel();
		frame.setContentPane(mathOverridesPanel);
		frame.setSize(mathOverridesPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		cbit.vcell.math.MathDescription mathDescription = cbit.vcell.math.MathDescriptionTest.getExample();
//		System.out.println(mathDescription.getXML());
		cbit.vcell.solver.MathOverrides mathOverrides = new cbit.vcell.solver.MathOverrides(
			new cbit.vcell.solver.Simulation(mathDescription));
		mathOverridesPanel.setMathOverrides(mathOverrides);
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}

}
}
