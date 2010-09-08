package cbit.gui.graph.groups;

/*  A set of bundled edges
 *  September 2010
 */

import java.util.Set;

public class VCEdgeBundle {

	protected final String name;
	protected final Set<VCEdge> edges;
	
	public VCEdgeBundle(String name, Set<VCEdge> edges) {
		this.name = name;
		this.edges = edges;
	}

	public String getName() { return name; }
	public Set<VCEdge> getEdges() { return edges; }
	
}
