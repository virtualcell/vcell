package org.vcell.sybil.actions.graph.manipulations;

/*   ReactionsProcessesOnlyAction  --- by Oliver Ruebenacker, UCHC --- January 2008 to January 2009
 *   An EvaluatorManipAction for showing processes showing only Processes (Subprocesses collapsed)
 */

import org.vcell.sybil.actions.ActionSpecs;
import org.vcell.sybil.actions.CoreManager;
import org.vcell.sybil.actions.graph.ModelGraphManager;
import org.vcell.sybil.models.graph.UIGraph;
import org.vcell.sybil.models.graph.UIShape;
import org.vcell.sybil.models.graph.manipulator.categorizer.ReactionsManipulator;

public class ReactionsProcessesOnlyAction<S extends UIShape<S>, G extends UIGraph<S, G>> 
extends EvaluatorManipulationAction<S, G, ReactionsManipulator<S, G>> {

	private static final long serialVersionUID = -2098785582039697319L;

	public ReactionsProcessesOnlyAction(ActionSpecs newSpecs, CoreManager coreManager, 
			ModelGraphManager<S, G> graphManager) {
		super(newSpecs, coreManager, graphManager);
	}

	public ReactionsManipulator<S, G> graphManipulation(G graph) {
		ReactionsManipulator<S, G> manip = new ReactionsManipulator<S, G>(evaluator(graph));
		manip.setWithReactants(false);
		manip.setWithComponents(false);
		manip.setCollapseSubProcesses(true);
		manip.setCollapseParticipants(false);
		return manip;
	}

}
