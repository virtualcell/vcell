package cbit.vcell.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cbit.util.graph.Edge;
import cbit.util.graph.Graph;
import cbit.util.graph.Node;
import cbit.util.graph.Path;
import cbit.util.graph.Tree;

public class StructureSorter {

	private static class StructureRelationshipEdge extends Edge {
		public int numConnections = 0;
		public boolean bParentChild = false;
		
		public StructureRelationshipEdge(Node node1, Node node2) {
			super(node1, node2);
		}
		
		public String toString(){
			return super.toString() + ", nc="+numConnections+", p="+bParentChild;
		}
	}

	public static Structure[] sortStructures(Model model) {
		Graph graph = new Graph();
		// add all structures to the graph
		for (Structure structure : model.getStructures()){
			graph.addNode(new Node(structure.getName(), structure));
		}
		// add an edge
		for (Structure structure : model.getStructures()){
			Structure parentStructure = null;
			if (structure.getSbmlParentStructure()!=null){
				parentStructure = structure.getSbmlParentStructure();
			}else if (model.getStructureTopology().getParentStructure(structure)!=null){
				parentStructure = model.getStructureTopology().getParentStructure(structure);
			}
			if (parentStructure!=null){
				int index1 = graph.getIndex(graph.getNode(structure.getName()));
				int index2 = graph.getIndex(graph.getNode(parentStructure.getName()));
				StructureRelationshipEdge edge = getStructureRelationshipEdge(graph, index1, index2);
				edge.bParentChild = true;
			}
		}
		for (ReactionStep rs : model.getReactionSteps()){
			int index1 = graph.getIndex(graph.getNode(rs.getStructure().getName()));
			for (ReactionParticipant rp : rs.getReactionParticipants()){
				if (rp.getStructure()!=rs.getStructure()){
					int index2 = graph.getIndex(graph.getNode(rp.getStructure().getName()));
					StructureRelationshipEdge edge = getStructureRelationshipEdge(graph, index2, index1);
					edge.numConnections++;
				}
			}
		}
		// remove edges such that all nodes have degree <= 2
		for (Node node : graph.getNodes()){
			Edge[] adjacentEdges = graph.getAdjacentEdges(node);
			while (adjacentEdges.length>2){
				StructureRelationshipEdge lowestCostEdge = getLowestCostEdge(Arrays.asList(adjacentEdges));
				graph.remove(lowestCostEdge);
				adjacentEdges = graph.getAdjacentEdges(node);
			}
		}
		
		// for each dependency loop, remove edge with lowest cost
		Path[] fundamentalCycles = graph.getFundamentalCycles();
		while (fundamentalCycles.length>0){
			StructureRelationshipEdge lowestCostEdge = getLowestCostEdge(Arrays.asList(fundamentalCycles[0].getEdges()));
			graph.remove(lowestCostEdge);
			fundamentalCycles = graph.getFundamentalCycles();
		}
		
		// all graphs in the forest are linear now ... find path from first to last and line up all trees in list.
		Tree[] spanningForest = graph.getSpanningForest();
		ArrayList<Structure> structures = new ArrayList<Structure>();
		for (Tree tree : spanningForest){
			if (tree.getNodes().length>1){
				Node start = null;
				Node end = null;
				for (Node node : tree.getNodes()){
					if (tree.getDegree(node) == 1){
						if (start == null){
							start = node;
						}else if (end == null){
							end = node;
						}else{
							break;
						}
					}
				}
				Path path = tree.getTreePath(start, end);
				for (Node node : path.getNodesTraversed()){
					structures.add((Structure)node.getData());
				}
			}else{
				structures.add((Structure)tree.getNodes()[0].getData());
			}
		}
		return structures.toArray(new Structure[structures.size()]);
	}

	private static StructureRelationshipEdge getLowestCostEdge(List<Edge> structureRelationshipEdgeList){
		StructureRelationshipEdge lowestCostEdge = null;
		for (Edge e : structureRelationshipEdgeList){
			StructureRelationshipEdge currentEdge = (StructureRelationshipEdge)e;
			if (lowestCostEdge==null){
				lowestCostEdge = currentEdge;
			}else{
				if (lowestCostEdge.bParentChild && !currentEdge.bParentChild){
					lowestCostEdge = currentEdge;
				}else if (lowestCostEdge.numConnections > currentEdge.numConnections){
					lowestCostEdge = currentEdge;
				}
			}
		}
		return lowestCostEdge;
	}

	private static StructureRelationshipEdge getStructureRelationshipEdge(Graph graph,	int index1, int index2) {
		StructureRelationshipEdge edge = null;	
		if (index1 > index2){
			edge = (StructureRelationshipEdge)graph.getEdge(index2, index1);
			if (edge==null){
				edge = new StructureRelationshipEdge(graph.getNodes()[index2],graph.getNodes()[index1]);
				graph.addEdge(edge);
			}
		}else{
			edge = (StructureRelationshipEdge)graph.getEdge(index1, index2);		
			if (edge==null){
				edge = new StructureRelationshipEdge(graph.getNodes()[index1],graph.getNodes()[index2]);
				graph.addEdge(edge);
			}
		}
		return edge;
	}

}
