/**
 * 
 */
package org.vcell.physics.component;

import org.vcell.physics.math.BipartiteMatchings;
import org.vcell.physics.math.MathSystem;

import cbit.util.graph.Graph;

public class ModelAnalysisResults {
	public OOModel oOModel = null;
	public MathSystem mathSystem = null;
	public Graph partitionGraph = null;
	public Graph connectivityGraph = null;
	public Graph sccGraph = null;
	public BipartiteMatchings.Matching matching = null;
	public StronglyConnectedComponent[] sccArray = null;
	public VarEquationAssignment[] varEqnAssignments = null;
}