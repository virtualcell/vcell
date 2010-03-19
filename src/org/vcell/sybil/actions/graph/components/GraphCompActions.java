package org.vcell.sybil.actions.graph.components;

/*  GraphCompActions  ---  Oliver Ruebenacker, UCHC  ---  February 2009
 *  Actions related to graph components
 */

import org.vcell.sybil.actions.ActionTree;
import org.vcell.sybil.actions.CoreManager;
import org.vcell.sybil.actions.graph.ModelGraphManager;
import org.vcell.sybil.models.graph.UIGraph;
import org.vcell.sybil.models.graph.UIShape;

public class GraphCompActions<S extends UIShape<S>, G extends UIGraph<S, G>> 
extends ActionTree {
	private static final long serialVersionUID = 5766711072680731688L;
	public GraphCompActions(CoreManager coreManager, ModelGraphManager<S, G> graphManager) {
		add(new GraphInteractionCompActions<S, G>(coreManager, graphManager));
		add(new GraphOtherCompActions<S, G>(coreManager, graphManager));
	}
}