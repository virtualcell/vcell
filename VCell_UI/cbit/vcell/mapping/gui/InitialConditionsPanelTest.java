package cbit.vcell.mapping.gui;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/

import cbit.vcell.modelapp.SimulationContext;
import cbit.vcell.modelapp.SimulationContextTest;
/**
 * This type was created in VisualAge.
 */
public class InitialConditionsPanelTest {
/**
 * This method was created in VisualAge.
 * @param args java.lang.String[]
 */
public static void main(String args[]) {
	try {
		java.awt.Frame frame;
		try {
			Class aFrameClass = Class.forName("com.ibm.uvm.abt.edit.TestFrame");
			frame = (java.awt.Frame)aFrameClass.newInstance();
		} catch (java.lang.Throwable ivjExc) {
			frame = new java.awt.Frame();
		}
		new org.vcell.util.gui.WindowCloser(frame,true);
		InitialConditionsPanel aInitialConditionsPanel;
		aInitialConditionsPanel = new InitialConditionsPanel();
		frame.add("Center", aInitialConditionsPanel);
		frame.setSize(aInitialConditionsPanel.getSize());
		SimulationContext simContext = SimulationContextTest.getExample(2);
		frame.setVisible(true);
		aInitialConditionsPanel.setSimulationContext(simContext);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of java.awt.Panel");
		exception.printStackTrace(System.out);
	}
}
}
