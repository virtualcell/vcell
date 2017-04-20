/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.util.graphlayout;

import java.util.Random;

import org.vcell.util.graphlayout.ContainedGraph.Container;

public class ExpandCanvasLayouter extends ContainedGraphLayouter {

	public static final double SCALE = 1.1;
	public static final String LAYOUT_NAME = "Contained Graph Layout Expand Canvas";
	
	protected Random random = new Random();
	protected StretchToBoundaryLayouter stretchLayouter = new StretchToBoundaryLayouter();
	
	@Override
	public void layout(ContainedGraph graph) {
		for(Container container : graph.getContainers()) {
			container.setWidth(SCALE*container.getWidth());
			container.setHeight(SCALE*container.getHeight());
		}
		stretchLayouter.layout(graph);
	}

	@Override
	public String getLayoutName() {
		return LAYOUT_NAME;
	}
	
}
