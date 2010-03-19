package org.vcell.sybil.actions.graph.manipulations;

/*   EvaluatorManipAction  --- by Oliver Ruebenacker, UCHC --- January 2008 to May 2009
 *   Any SybilAction targeting a GraphPane using a GraphManip using an Evaluator provided by ModSys
 */

import org.vcell.sybil.actions.ActionSpecs;
import org.vcell.sybil.actions.CoreManager;
import org.vcell.sybil.models.ontology.Evaluator;

import org.vcell.sybil.actions.graph.ModelGraphManager;
import org.vcell.sybil.actions.graph.manipulations.GraphManipulationAction;
import org.vcell.sybil.models.graph.UIGraph;
import org.vcell.sybil.models.graph.UIShape;
import org.vcell.sybil.models.graph.manipulator.categorizer.EvaluatorGraphManipulator;

public abstract class EvaluatorManipulationAction
<S extends UIShape<S>, G extends UIGraph<S, G>, M extends EvaluatorGraphManipulator<S, G>> 
extends GraphManipulationAction<S, G, M> {

	private static final long serialVersionUID = 4159996637166511105L;

	public EvaluatorManipulationAction(ActionSpecs newSpecs, CoreManager coreManager, 
			ModelGraphManager<S, G> graphManager) {
		super(newSpecs, coreManager, graphManager);
	}

	public Evaluator evaluator(UIGraph<S, G> graph) {
		return coreManager().fileManager().evaluator();
	}
	
}
