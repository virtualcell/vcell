package org.vcell.sybil.actions.graph.layout;

/*  GraphLayoutActions  ---  Oliver Ruebenacker, UCHC  ---  May 2007 to March 2010
 *  Actions for changing the global appearance of the graph
 */

import org.vcell.sybil.actions.ActionSpecs;
import org.vcell.sybil.actions.ActionTree;
import org.vcell.sybil.actions.CoreManager;
import org.vcell.sybil.actions.graph.ModelGraphManager;
import org.vcell.sybil.actions.graph.manipulations.GraphManipulationActions;
import org.vcell.sybil.models.graph.UIGraph;
import org.vcell.sybil.models.graph.UIShape;

public class GraphLayoutActions<S extends UIShape<S>, G extends UIGraph<S, G>> 
extends ActionTree {
	private static final long serialVersionUID = 5766711072680731688L;
	public GraphLayoutActions(CoreManager coreManager, ModelGraphManager<S, G> graphManager) {
		add(new GraphSelectAction(new ActionSpecs
				("Select", "Select", "Select parts of the graph", "layout/select.gif"), 
				coreManager));
		add(new GraphZoomActions(coreManager));
		add(new GraphRenderLayoutActions(coreManager));
		add(new GraphManipulationActions<S, G>(coreManager, graphManager));
//		if(SystemPropertyDevelMode.develMode()) {
//			add(new GraphTreePrintAction<S, G>(new ActionSpecs
//			("Print Tree", "Print Tree", "Print Graph Tree", "images/layout/select.gif"), 
//			modSysNew, modSysModelGraphNew));				
//		}
	}
}