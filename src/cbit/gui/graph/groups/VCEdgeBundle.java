/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

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
