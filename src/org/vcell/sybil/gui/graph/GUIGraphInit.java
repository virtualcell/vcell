package org.vcell.sybil.gui.graph;

/*   GUIMain  --- by Oliver Ruebenacker, UCHC --- January 2009
 *   Provides the universe in which the graph-related GUI lives. To be instantiated exactly once.
 */

import org.vcell.sybil.actions.ActionsGraphInit;
import org.vcell.sybil.actions.graph.ModelGraphManager;
import org.vcell.sybil.gui.GUIMainInit;
import org.vcell.sybil.util.ui.UserInterfaceGraph;

public class GUIGraphInit extends ActionsGraphInit<Shape, Graph> implements GUIMainInit.SubInitGraph {

	protected ModelGraphManager<Shape, Graph> graphManager;
	
	public GUIGraphInit() { 
		graphManager = new ModelGraphManager<Shape, Graph>();
	}
	
	@Override
	public ModelGraphManager<Shape, Graph> graphManager() {
		return graphManager;
	}

	public void setUI(UserInterfaceGraph<Shape, Graph> newUI) {
		graphManager.setUI(newUI);
	}
	
}
