package org.vcell.sybil.models.graph;

/*   GraphListener  --- by Oliver Ruebenacker, UCHC --- October 2007 to March 2010
 *   Interface for classes displaying a graph, such as GraphPane
 */

import org.vcell.sybil.util.graphlayout.LayoutType;

public interface GraphListener<S extends UIShape<S>, G extends UIGraph<S, G>> {

	public void setGraph(G graph);
	public G graph();
	public void layoutGraph(LayoutType newLayout);
	public void updateView();

}
