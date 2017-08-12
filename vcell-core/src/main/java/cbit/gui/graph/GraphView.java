/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

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
	public void saveNodePositions();
	
}
