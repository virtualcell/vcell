package org.vcell.sybil.actions.graph.manipulations;

/*   ReactionsAction  --- by Oliver Ruebenacker, UCHC --- January 2008 to January 2009
 *   A customizable GraphEvaluatorManipAction for showing processes and
 *   varying levels of related participants and entities
 */

import org.vcell.sybil.actions.ActionSpecs;
import org.vcell.sybil.actions.CoreManager;
import org.vcell.sybil.models.graph.UIGraph;
import org.vcell.sybil.models.graph.UIShape;
import org.vcell.sybil.models.graph.manipulator.categorizer.ReactionsManipulator;

import org.vcell.sybil.actions.graph.ModelGraphManager;
import org.vcell.sybil.actions.graph.manipulations.EvaluatorManipulationAction;

public class ReactionsAction<S extends UIShape<S>, G extends UIGraph<S, G>> 
extends EvaluatorManipulationAction<S, G, ReactionsManipulator<S, G>> {

	private static final long serialVersionUID = 4448725821844146173L;

	public ReactionsAction(ActionSpecs newSpecs, CoreManager coreManager,
			ModelGraphManager<S, G> graphManager) {
		super(newSpecs, coreManager, graphManager);
	}

	@Override
	public ReactionsManipulator<S, G> graphManipulation(G graph) {
		return new ReactionsManipulator<S, G>(evaluator(graph));
	}

}
