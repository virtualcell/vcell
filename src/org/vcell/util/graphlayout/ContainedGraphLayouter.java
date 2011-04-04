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
