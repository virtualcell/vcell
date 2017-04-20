/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.util.graphlayout.energybased;

import java.util.HashSet;
import java.util.Set;

import org.vcell.sybil.util.lists.ListOfTwo;
import org.vcell.util.graphlayout.ContainedGraph;
import org.vcell.util.graphlayout.ContainedGraph.Edge;
import org.vcell.util.graphlayout.ContainedGraph.Node;
import org.vcell.util.graphlayout.energybased.EnergySum.EnergyFunction;
import org.vcell.util.graphlayout.energybased.EnergySum.EnergyTerm;

public class AllEdgesEnergyTermFactory implements EnergyTerm.Factory {
	
	protected final EnergyFunction function;
	
	public AllEdgesEnergyTermFactory(EnergyFunction functionFactory) {
		this.function = functionFactory;
	}
	
	public Set<EnergyTerm> generateTerms(ContainedGraph graph) {
		HashSet<EnergyTerm> terms = new HashSet<EnergyTerm>();
		for(Edge edge : graph.getEdges()) {
			terms.add(new EnergyTerm.Default(
					new ListOfTwo<Node>(edge.getNode1(), edge.getNode2()), function));
		}
		return terms;
	}

}
