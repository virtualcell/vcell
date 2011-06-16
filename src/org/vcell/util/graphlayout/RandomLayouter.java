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
import org.vcell.util.graphlayout.ContainedGraph.Node;

public class RandomLayouter extends ContainedGraphLayouter {

	public static final String LAYOUT_NAME = "Contained Graph Layout Random";
	
	protected Random random = new Random();
	protected StretchToBoundaryLayouter stretchLayouter = new StretchToBoundaryLayouter();
	
	@Override
	public void layout(ContainedGraph graph) {
		for(Node node : graph.getNodes()) {
			Container container = node.getContainer();
			node.setPos(container.getX() + random.nextDouble()*(container.getWidth() - node.getWidth()), 
					container.getY() + random.nextDouble()*(container.getHeight() - node.getHeight()));
		}
		stretchLayouter.layout(graph);
	}

	@Override
	public String getLayoutName() {
		return LAYOUT_NAME;
	}
	
}
