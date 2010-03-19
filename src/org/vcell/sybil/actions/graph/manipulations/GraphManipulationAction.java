package org.vcell.sybil.actions.graph.manipulations;

/*   ManipulationAction  --- by Oliver Ruebenacker, UCHC --- January 2008 to May 2010
 *   Any SybilAction targeting a GraphPane using a GraphManipulator
 */

import java.awt.event.ActionEvent;

import org.vcell.sybil.actions.ActionSpecs;
import org.vcell.sybil.actions.CoreManager;
import org.vcell.sybil.actions.graph.ModelGraphManager;
import org.vcell.sybil.actions.graph.GraphAction;
import org.vcell.sybil.models.graph.UIGraph;
import org.vcell.sybil.models.graph.UIShape;
import org.vcell.sybil.models.graph.manipulator.GraphManipulationException;
import org.vcell.sybil.models.graph.manipulator.GraphManipulator;
import org.vcell.sybil.util.exception.CatchUtil;

public abstract class GraphManipulationAction
<S extends UIShape<S>, G extends UIGraph<S, G>, M extends GraphManipulator<S, G>> 
extends GraphAction<S, G> {

	private static final long serialVersionUID = 5447342774360735198L;

	public GraphManipulationAction(ActionSpecs newSpecs, CoreManager coreManager,
			ModelGraphManager<S, G> graphManager) {
		super(newSpecs, coreManager, graphManager);
	}
	
	public abstract M graphManipulation(G graph);

	public void actionPerformed(ActionEvent event) {
		if(graph() != null) {
			M manip = graphManipulation(graph());
			try { manip.applyToGraph(graph()); } 
			catch (GraphManipulationException e) { CatchUtil.handle(e); }
			updateUI();
		} 
	}
	
}
