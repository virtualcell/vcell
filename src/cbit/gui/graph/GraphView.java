package cbit.gui.graph;

/*  A view of a graph
 *  September 2010
 */

import java.awt.Dimension;

import cbit.gui.graph.groups.VCGroupManager;

public interface GraphView {

	public GraphModel getGraphModel();
	public VCGroupManager getGroupManager();
	public int getWidth();
	public int getHeight();
	public void setSize(Dimension graphSize);
	public void repaint();
	
}
