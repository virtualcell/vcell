/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

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
import org.vcell.sybil.util.graphlayout.LayoutType;

public class ReactionsProcessesOnlyAction<S extends UIShape<S>, G extends UIGraph<S, G>> 
extends EvaluatorManipulationAction<S, G, ReactionsManipulator<S, G>> {

	private static final long serialVersionUID = -2098785582039697319L;

	public ReactionsProcessesOnlyAction(ActionSpecs newSpecs, CoreManager coreManager, 
			ModelGraphManager<S, G> graphManager) {
		super(newSpecs, coreManager, graphManager);
	}

	@Override
	public ReactionsManipulator<S, G> graphManipulation(G graph) {
		ReactionsManipulator<S, G> manip = new ReactionsManipulator<S, G>(evaluator(graph));
		manip.setWithReactants(false);
		manip.setWithComponents(false);
		manip.setCollapseSubProcesses(true);
		manip.setCollapseParticipants(false);
		// wei's code: show in random layout by default
		super.coreManager.graphSpace().layoutGraph(LayoutType.Randomizer);
		return manip;
	}

}
