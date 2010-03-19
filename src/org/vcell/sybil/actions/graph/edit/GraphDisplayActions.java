package org.vcell.sybil.actions.graph.edit;

/*  GraphDisplayActions  ---  Oliver Ruebenacker, UCHC  ---  January 2009 to March 2010
 *  Actions related to the displpay of the graph (layout and edit)
 */

import org.vcell.sybil.actions.ActionSpecs;
import org.vcell.sybil.actions.ActionTree;
import org.vcell.sybil.actions.CoreManager;
import org.vcell.sybil.actions.graph.ModelGraphManager;
import org.vcell.sybil.models.graph.UIGraph;
import org.vcell.sybil.models.graph.UIShape;
import org.vcell.sybil.models.systemproperty.SystemPropertyDevelMode;

public class GraphDisplayActions<S extends UIShape<S>, G extends UIGraph<S, G>> 
extends ActionTree {
	private static final long serialVersionUID = -1981389494197848654L;
	public GraphDisplayActions(CoreManager coreManager, ModelGraphManager<S, G> graphManager) {
		add(new GraphEditHideAction<S, G>(new ActionSpecs
				("Hide", "Hide", "Hide Shape", "edit/hide.gif"), 
				coreManager, graphManager));
		add(new GraphEditUnhideNeighborsAction<S, G>(new ActionSpecs
				("Unhide Neighbors", "Unhide Neighbors", "Unhide Neighboring Shapes", 
						"files/exit.gif"), 
						coreManager, graphManager));
		add(new GraphEditUnhideAllAction<S, G>(new ActionSpecs
				("Unhide All", "Unhide All", "Unhide All Shapes", "files/exit.gif"), 
				coreManager, graphManager));
		if(SystemPropertyDevelMode.develMode()) {
			add(new GraphEditUnhideEdgesAction<S, G>(new ActionSpecs
					("Unhide Edges", "Unhide Edges", "Unhide Edges Attached to Shape", 
							"files/exit.gif"), 
							coreManager, graphManager));
			add(new GraphEditCollapseAction<S, G>(new ActionSpecs
					("Collapse", "Collapse", "Collapse Shapes", "edit/collapse.gif"), 
					coreManager, graphManager));
			add(new GraphEditExplodeAction<S, G>(new ActionSpecs
					("Explode", "Explode", "Explode Shapes", "edit/explode.gif"), 
					coreManager, graphManager));
			add(new GraphEditDeleteAction<S, G>(new ActionSpecs
					("Delete", "Delete", "Delete Shape", "edit/delete.gif"), 
					coreManager, graphManager));
		}
	}
}