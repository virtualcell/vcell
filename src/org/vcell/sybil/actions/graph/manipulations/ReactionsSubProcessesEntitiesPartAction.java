package org.vcell.sybil.actions.graph.manipulations;

/*   ReactionsSubprocessesEntitiesPartAction  
 * --- by Oliver Ruebenacker, UCHC --- January 2008 to January 2009
 *   An EvaluatorManipAction for showing processes, subprocesses, entities and participants
 */

import org.vcell.sybil.actions.ActionSpecs;
import org.vcell.sybil.actions.CoreManager;
import org.vcell.sybil.actions.graph.ModelGraphManager;
import org.vcell.sybil.models.graph.UIGraph;
import org.vcell.sybil.models.graph.UIShape;
import org.vcell.sybil.models.graph.manipulator.categorizer.ReactionsManipulator;

public class ReactionsSubProcessesEntitiesPartAction<S extends UIShape<S>, G extends UIGraph<S, G>> 
extends EvaluatorManipulationAction<S, G, ReactionsManipulator<S, G>> {

	private static final long serialVersionUID = -6021979079887226290L;

	public ReactionsSubProcessesEntitiesPartAction(ActionSpecs newSpecs, CoreManager coreManager,
			ModelGraphManager<S, G> graphManager) {
		super(newSpecs, coreManager, graphManager);
	}

	public ReactionsManipulator<S, G> graphManipulation(G graph) {
		ReactionsManipulator<S, G> manip = new ReactionsManipulator<S, G>(evaluator(graph));
		manip.setWithReactants(true);
		manip.setWithComponents(false);
		manip.setCollapseSubProcesses(false);
		manip.setCollapseParticipants(false);
		return manip;
	}

}
