package org.vcell.sybil.actions.graph;

/*   ModSys  --- by Oliver Ruebenacker, UCHC --- April 2007 to May 2009
 *   Provides an environment in which the models live
 */

import org.vcell.sybil.util.event.Bounded;
import org.vcell.sybil.util.ui.UIModelGraphSpace;
import org.vcell.sybil.util.ui.UserInterfaceGraph;

import org.vcell.sybil.models.updater.BoundedUpdater;
import org.vcell.sybil.models.updater.GraphModelUpdater;
import org.vcell.sybil.models.graph.GraphListener;
import org.vcell.sybil.models.graph.UIGraph;
import org.vcell.sybil.models.graph.UIShape;

public class ModelGraphManager<S extends UIShape<S>, G extends UIGraph<S, G>> 
implements GraphManager {

	protected UserInterfaceGraph<S, G> ui;
	protected UIModelGraphSpace<S, G> graphSpace;
	protected Bounded<GraphListener<S, G>> graphListener = new Bounded<GraphListener<S, G>>();
 	
	public void setUI(UserInterfaceGraph<S, G> newUI) {
		ui = newUI;
		graphSpace = ui().createGraphSpace();
		graphSpace.graphViewer().listeners().add(new BoundedUpdater<GraphListener<S, G>>(graphListener));
	}
	
	public UserInterfaceGraph<S, G> ui() { return ui; }

	public GraphModelUpdater<S, G> graphModelUpdater() { 
		return new GraphModelUpdater<S, G>(graph());
	}
	
	public G graph() { return graphSpace.graph(); }
	public UIModelGraphSpace<S, G> graphSpace() { return graphSpace; }

	public Bounded<GraphListener<S, G>> graphListener() { return graphListener; }
	
} // end class 
