package org.vcell.physics.math;
import org.vcell.physics.component.Capacitor;
import org.vcell.physics.component.Connection;
import org.vcell.physics.component.Connector;
import org.vcell.physics.component.Ground;
import org.vcell.physics.component.Inductor;
import org.vcell.physics.component.ModelAnalysisResults;
import org.vcell.physics.component.ModelComponent;
import org.vcell.physics.component.ModelReader;
import org.vcell.physics.component.OOModel;
import org.vcell.physics.component.PointSource;
import org.vcell.physics.component.Reaction;
import org.vcell.physics.component.Resistor;
import org.vcell.physics.component.Species;
import org.vcell.physics.component.StronglyConnectedComponent;
import org.vcell.physics.component.Symbol;
import org.vcell.physics.component.VarEquationAssignment;
import org.vcell.physics.component.Variable;
import org.vcell.physics.component.VoltageSource;
import org.vcell.physics.component.gui.OOModelingPanel;

import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
/**
 * Insert the type's description here.
 * Creation date: (1/16/2006 11:33:57 PM)
 * @author: Jim Schaff
 */
public class MappingUtilitiesTest {
	/**
 * Starts the application.
 * @param args an array of command-line arguments
 */
public static void main(java.lang.String[] args) {
	try {
		//Model model = ModelReader.parse(null);
		//Model model = getExampleTriangle_h_a();
		OOModel oOModel = org.vcell.physics.component.OOModelTest.getExample3();
		
		ModelAnalysisResults modelAnalysisResults = MappingUtilities.analyzeMathSystem(oOModel);

		OOModelingPanel ooModelPanel = new OOModelingPanel();
		
		ooModelPanel.setPartitionGraphPanelGraph(modelAnalysisResults.partitionGraph);
		ooModelPanel.setSccGraphModelPanelGraph(modelAnalysisResults.sccGraph);
		ooModelPanel.setStronglyConnectedComponents(modelAnalysisResults.sccArray);
		ooModelPanel.setVarEquationAssignments(modelAnalysisResults.varEqnAssignments);

		ooModelPanel.setPhysicalModelGraphPanelModel(oOModel);
		ooModelPanel.setConnectivityGraphPanelGraph(modelAnalysisResults.connectivityGraph);
		ooModelPanel.setBipartiteMatchings(modelAnalysisResults.matching);

		ooModelPanel.setPreferredSize(new java.awt.Dimension(800,800));
		ooModelPanel.setMinimumSize(new java.awt.Dimension(800,800));

		cbit.gui.DialogUtils.showComponentOKCancelDialog(null,ooModelPanel,"graph");

	} catch (Throwable e) {
		e.printStackTrace(System.out);
	}
}
}