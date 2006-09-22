package org.vcell.ncbc.physics.component.gui;

import org.vcell.ncbc.physics.component.PhysicalModel;
import org.vcell.ncbc.physics.component.PhysicalModelTest;

import cbit.vcell.modelapp.SimulationContext;
import cbit.vcell.modelapp.SimulationContextTest;

/**
 * Insert the type's description here.
 * Creation date: (7/8/2003 3:32:30 PM)
 * @author: Jim Schaff
 */
public class PhysicalModelGraphPanelTest {
/**
 * Starts the application.
 * @param args an array of command-line arguments
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		PhysicalModelGraphPanel aPhysicalModelGraphPanel;
		aPhysicalModelGraphPanel = new PhysicalModelGraphPanel();
		frame.setContentPane(aPhysicalModelGraphPanel);
		frame.setSize(aPhysicalModelGraphPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.show();
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		
		//PhysicalModel physicalModel = PhysicalModelTest.getExample();
		SimulationContext simContext = SimulationContextTest.getExampleElectrical(0);
		PhysicalModel physicalModel = org.vcell.ncbc.physics.engine.MappingUtilities.createFromSimulationContext(simContext);
		aPhysicalModelGraphPanel.setPhysicalModel(physicalModel);
		
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}
}
