package cbit.vcell.solver.ode.gui;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.util.*;
/**
 * Insert the type's description here.
 * Creation date: (4/5/01 2:46:44 PM)
 * @author: Jim Schaff
 */
public class SolverTaskDescriptionPanelTest {
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	//  JMW 10/26/2000 Commented out because I just don't want to
	//  fix the SolverTaskDescription constructor call right now.
	//
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		SolverTaskDescriptionPanel aSolverTaskDescriptionPanel;
		aSolverTaskDescriptionPanel = new SolverTaskDescriptionPanel();
		frame.setContentPane(aSolverTaskDescriptionPanel);
		frame.setSize(aSolverTaskDescriptionPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.show();
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);
		
		cbit.vcell.math.MathDescription mathDescription = cbit.vcell.math.MathDescriptionTest.getOdeExampleWagner();
		cbit.vcell.solver.Simulation simulation = new cbit.vcell.solver.Simulation (mathDescription);
		cbit.vcell.solver.SolverTaskDescription solverTaskDescription =
			new cbit.vcell.solver.SolverTaskDescription(simulation);
		//odeSolverTaskDescription.setIntegrator(2);
		solverTaskDescription.setTimeBounds(new cbit.vcell.solver.TimeBounds(0.0, 1.0));
		solverTaskDescription.setTimeStep(new cbit.vcell.solver.TimeStep(1.0E-8, 0.01, 1.0));
		solverTaskDescription.setErrorTolerance(new cbit.vcell.solver.ErrorTolerance(1.0E-8, 10.E-8));
		aSolverTaskDescriptionPanel.setSolverTaskDescription(solverTaskDescription);
		//
		try {
			Thread.sleep(4000);
		}catch (InterruptedException e){
		}

		solverTaskDescription.setSensitivityParameter((cbit.vcell.math.Constant)simulation.getMathDescription().getConstants().nextElement());
		try {
			Thread.sleep(2000);
		}catch (InterruptedException e){
		}

		solverTaskDescription.setSensitivityParameter(null);
		try {
			Thread.sleep(2000);
		}catch (InterruptedException e){
		}

		Enumeration enum1 = simulation.getMathDescription().getConstants();
		enum1.nextElement();
		enum1.nextElement();
		solverTaskDescription.setSensitivityParameter((cbit.vcell.math.Constant)enum1.nextElement());
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of java.awt.Panel");
		exception.printStackTrace(System.out);
	}//
}
}
