package cbit.vcell.solver.ode.gui;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.util.*;
/**
 * Insert the type's description here.
 * Creation date: (4/5/01 3:38:09 PM)
 * @author: Jim Schaff
 */
public class SolverTaskDescriptionAdvancedPanelTest {
public static void main(java.lang.String[] args) {
	//  JMW 10/26/2000 Commented out because I just don't want to
	//  fix the SolverTaskDescription constructor call right now.
	//
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		SolverTaskDescriptionAdvancedPanel aSolverTaskDescriptionAdvancedPanel =
			new SolverTaskDescriptionAdvancedPanel();
		frame.getContentPane().add("Center", aSolverTaskDescriptionAdvancedPanel);
		frame.setSize(aSolverTaskDescriptionAdvancedPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.setVisible(true);

		cbit.vcell.math.MathDescription mathDescription = cbit.vcell.math.MathDescriptionTest.getOdeExampleWagner();
		cbit.vcell.simulation.Simulation simulation = new cbit.vcell.simulation.Simulation (mathDescription);
		cbit.vcell.simulation.SolverTaskDescription solverTaskDescription = simulation.getSolverTaskDescription();
		//odeSolverTaskDescription.setIntegrator(2);
		//solverTaskDescription.setTimeBounds(new cbit.vcell.solver.TimeBounds(0.0, 1.0));
		//solverTaskDescription.setTimeStep(new cbit.vcell.solver.TimeStep(1.0E-8, 0.01, 1.0));
		//solverTaskDescription.setErrorTolerance(new cbit.vcell.solver.ErrorTolerance(1.0E-8, 10.E-8));
		solverTaskDescription.setSolverDescription(cbit.vcell.simulation.SolverDescription.ForwardEuler);
		//
		aSolverTaskDescriptionAdvancedPanel.setSolverTaskDescription(solverTaskDescription);

		try {
			Thread.sleep(4000);
		}catch (InterruptedException e){
		}

		solverTaskDescription.setSensitivityParameter((cbit.vcell.math.Constant)simulation.getMathDescription().getConstants().nextElement());
		try {
			Thread.sleep(2000);
		}catch (InterruptedException e){
		}

		solverTaskDescription.setUseSymbolicJacobian(true);
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
