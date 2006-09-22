package org.vcell.physics.component.gui;

import org.vcell.physics.component.ModelAnalysisResults;

import cbit.vcell.modelapp.physics.MappingUtilities;

/**
 * Insert the type's description here.
 * Creation date: (7/8/2003 3:32:30 PM)
 * @author: Jim Schaff
 */
public class OOModelingPanelTest {
/**
 * Starts the application.
 * @param args an array of command-line arguments
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		OOModelingPanel aOOModelingPanel;
		aOOModelingPanel = new OOModelingPanel();
		frame.setContentPane(aOOModelingPanel);
		frame.setSize(aOOModelingPanel.getSize());
		//frame.addWindowListener(new java.awt.event.WindowAdapter() {
			//public void windowClosing(java.awt.event.WindowEvent e) {
				//System.exit(0);
			//};
		//});
		frame.show();
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		
		//PhysicalModel physicalModel = PhysicalModelTest.getExample();
		org.vcell.physics.component.OOModel physicalModel = org.vcell.physics.component.OOModelTest.getExampleTriangle_h_a();
		ModelAnalysisResults modelAnalysisResults = MappingUtilities.analyzeMathSystem(physicalModel);
		aOOModelingPanel.setPhysicalModelGraphPanelModel(modelAnalysisResults.oOModel);
		aOOModelingPanel.setPartitionGraphPanelGraph(modelAnalysisResults.partitionGraph);
		aOOModelingPanel.setSccGraphModelPanelGraph(modelAnalysisResults.sccGraph);
		aOOModelingPanel.setStronglyConnectedComponents(modelAnalysisResults.sccArray);
		aOOModelingPanel.setVarEquationAssignments(modelAnalysisResults.varEqnAssignments);
		aOOModelingPanel.setConnectivityGraphPanelGraph(modelAnalysisResults.connectivityGraph);
		
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}
}
