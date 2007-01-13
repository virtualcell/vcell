package org.vcell.physics.test;

import org.vcell.physics.component.gui.OOModelGraphPanel;

/**
 * Insert the type's description here.
 * Creation date: (7/8/2003 3:32:30 PM)
 * @author: Jim Schaff
 */
public class OOModelGraphPanelTest {
/**
 * Starts the application.
 * @param args an array of command-line arguments
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		OOModelGraphPanel aPhysicalModelGraphPanel;
		aPhysicalModelGraphPanel = new OOModelGraphPanel();
		frame.setContentPane(aPhysicalModelGraphPanel);
		frame.setSize(aPhysicalModelGraphPanel.getSize());
		//frame.addWindowListener(new java.awt.event.WindowAdapter() {
			//public void windowClosing(java.awt.event.WindowEvent e) {
				//System.exit(0);
			//};
		//});
		frame.show();
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		
		//PhysicalModel physicalModel = PhysicalModelTest.getExample();
		//org.vcell.physics.component.OOModel physicalModel = org.vcell.physics.component.OOModelTest.getExampleTriangle_h_a();
		org.vcell.physics.component.OOModel physicalModel = org.vcell.physics.component.OOModelTest.getPlanarPendulumExample();
		aPhysicalModelGraphPanel.setModel(physicalModel);
		
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}
}