package org.vcell.sybil.actions.graph.manipulations;

/*  ReactionsComponentsAction  --- by Oliver Ruebenacker, UCHC --- January 2008 to January 2009
 *   An EvaluatorManipAction for showing processes, entities and components (Subprocesses collapsed)
 */

import org.vcell.sybil.actions.ActionSpecs;
import org.vcell.sybil.actions.CoreManager;
import org.vcell.sybil.models.graph.UIGraph;
import org.vcell.sybil.models.graph.UIShape;
import org.vcell.sybil.models.graph.manipulator.categorizer.ReactionsManipulator;

import org.vcell.sybil.actions.graph.ModelGraphManager;
import org.vcell.sybil.actions.graph.manipulations.EvaluatorManipulationAction;

public class ReactionsComponentsAction<S extends UIShape<S>, G extends UIGraph<S, G>> 
extends EvaluatorManipulationAction<S, G, ReactionsManipulator<S, G>> {

	private static final long serialVersionUID = 5674393748863571384L;

	public ReactionsComponentsAction(ActionSpecs newSpecs, CoreManager coreManager,
			ModelGraphManager<S, G> graphManager) {
		super(newSpecs, coreManager, graphManager);
	}

	public ReactionsManipulator<S, G> graphManipulation(G graph) {
		ReactionsManipulator<S, G> manip = new ReactionsManipulator<S, G>(evaluator(graph));
		manip.setWithReactants(true);
		manip.setWithComponents(true);
		manip.setCollapseSubProcesses(true);
		manip.setCollapseParticipants(true);
		return manip;
	}

}
