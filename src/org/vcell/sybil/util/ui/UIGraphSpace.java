package org.vcell.sybil.util.ui;

/*   UIGraphSpace  --- by Oliver Ruebenacker, UCHC --- November 2007 to March 2010
 *   Any SybilAction targetting a GraphPane
 */

import org.vcell.sybil.models.graph.GraphModel;
import org.vcell.sybil.util.graphlayout.LayoutType;

public interface UIGraphSpace extends UISpace {

	public GraphModel.Listener graph();
	public void layoutGraph(LayoutType newLayout);
	public void setZoomPercent(int newZoomPercent);
	public int zoomPercent();
	public void updateUI();
	
}
