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

import cbit.gui.graph.VCellGraphToContainedGraphMapper;

public abstract class ContainedGraphLayouter implements GraphLayouter {

	public abstract String getLayoutName();

	public void layout(Client client) {
		VCellGraphToContainedGraphMapper mapper = 
			new VCellGraphToContainedGraphMapper(client.getGraphModel());
		mapper.updateContainedGraphFromVCellGraph();
		layout(mapper.getContainedGraph());
		mapper.updateVCellGraphFromContainedGraph();
	}
	
	public abstract void layout(ContainedGraph graph);
	
}
