/**
 * 
 */
package org.vcell.physics.math;

import org.vcell.physics.component.OOModel;
import org.vcell.physics.component.StronglyConnectedComponent;
import org.vcell.physics.component.VarEquationAssignment;

import cbit.util.graph.Graph;
import cbit.vcell.math.MathDescription;

public class ModelAnalysisResults {
	public OOModel oOModel = null;
	public MathSystem mathSystem = null;
	public Graph partitionGraph = null;
	public Graph connectivityGraph = null;
	public Graph sccGraph = null;
	public BipartiteMatchings.Matching matching = null;
	public StronglyConnectedComponent[] sccArray = null;
	public VarEquationAssignment[] varEqnAssignments = null;
	public MathDescription mathDescription = null;
}