package cbit.vcell.math.gui;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.math.*;
import cbit.vcell.solver.*;
/**
 * This type was created in VisualAge.
 */
public class MeshSpecificationPanelTest {
/**
 * Starts the application.
 * @param args an array of command-line arguments
 */
public static void main(java.lang.String[] args) {
	try {
		java.awt.Frame frame = new java.awt.Frame();
		MeshSpecificationPanel aSpatialSpecificationPanel;
		aSpatialSpecificationPanel = new MeshSpecificationPanel();
		frame.add("Center", aSpatialSpecificationPanel);
		frame.setSize(aSpatialSpecificationPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		MathDescription mathDesc = MathDescriptionTest.getExample();
		Simulation sim = new Simulation(mathDesc);
		aSpatialSpecificationPanel.setMeshSpecification(sim.getMeshSpecification());
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of java.awt.Panel");
		exception.printStackTrace(System.out);
	}
}
}
