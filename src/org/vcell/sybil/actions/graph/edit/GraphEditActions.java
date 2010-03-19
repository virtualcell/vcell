package org.vcell.sybil.actions.graph.edit;

/*  ActionBase  ---  Oliver Ruebenacker, UCHC  ---  January 2009
 *  Actions to edit the graph
 */

import org.vcell.sybil.actions.ActionTree;
import org.vcell.sybil.actions.CoreManager;
import org.vcell.sybil.actions.graph.ModelGraphManager;
import org.vcell.sybil.models.graph.UIGraph;
import org.vcell.sybil.models.graph.UIShape;

public class GraphEditActions<S extends UIShape<S>, G extends UIGraph<S, G>> 
extends ActionTree {
	private static final long serialVersionUID = -1981389494197848654L;
	public GraphEditActions(CoreManager coreManager, ModelGraphManager<S, G> graphManager) {
		add(new GraphDisplayActions<S, G>(coreManager, graphManager));
	}
}