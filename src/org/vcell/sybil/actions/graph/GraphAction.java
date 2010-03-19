package org.vcell.sybil.actions.graph;

/*   SybilGraphAction  --- by Oliver Ruebenacker, UCHC --- November 2007 to March 2010
 *   Any SybilAction targeting a GraphPane
 */

import org.vcell.sybil.actions.ActionSpecs;
import org.vcell.sybil.actions.BaseAction;
import org.vcell.sybil.actions.CoreManager;
import org.vcell.sybil.models.graph.UIGraph;
import org.vcell.sybil.models.graph.UIShape;

public abstract class GraphAction<S extends UIShape<S>, G extends UIGraph<S, G>> 
extends BaseAction {

	private static final long serialVersionUID = 3386716713861791972L;
	protected ModelGraphManager<S, G> graphManager;
	
	public GraphAction(ActionSpecs newSpecs, CoreManager coreManager, 
			ModelGraphManager<S, G> graphManager) {
		super(newSpecs, coreManager);
		this.graphManager = graphManager;
	}

	public G graph() { return graphManager.graph(); }
	public void updateUI()  { graphManager.graphSpace().updateUI(); }
	
}
