package org.vcell.util.graphlayout.energybased;

import java.util.HashSet;
import java.util.Set;

import org.vcell.sybil.util.lists.ListOfTwo;
import org.vcell.sybil.util.sets.SetOfTwo;
import org.vcell.util.graphlayout.ContainedGraph;
import org.vcell.util.graphlayout.ContainedGraph.Node;
import org.vcell.util.graphlayout.energybased.EnergySum.EnergyFunction;
import org.vcell.util.graphlayout.energybased.EnergySum.EnergyTerm;

public class AllNodePairsEnergyTermFactory implements EnergyTerm.Factory {
	
	protected final EnergyFunction function;
	
	public AllNodePairsEnergyTermFactory(EnergyFunction functionFactory) {
		this.function = functionFactory;
	}
	
	public Set<EnergyTerm> generateTerms(ContainedGraph graph) {
		Set<SetOfTwo<Node>> nodePairs = new HashSet<SetOfTwo<Node>>();
		for(Node node1 : graph.getNodes()) {
			for(Node node2 : graph.getNodes()) {
				if(node1 != node2) {
					SetOfTwo<Node> nodePair = new SetOfTwo<Node>(node1, node2);
					if(!nodePairs.contains(nodePair)) {
						nodePairs.add(nodePair);
					}					
				}
			}
		}
		HashSet<EnergyTerm> terms = new HashSet<EnergyTerm>();
		for(SetOfTwo<Node> nodePair : nodePairs) {
			terms.add(new EnergyTerm.Default(
					new ListOfTwo<Node>(nodePair.getElement1(), nodePair.getElement2()), function));
		}
		return terms;
	}

}
