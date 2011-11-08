/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.util.graphlayout.test;

import com.genlogic.GraphLayout.GlgCube;
import com.genlogic.GraphLayout.GlgGraphEdge;
import com.genlogic.GraphLayout.GlgGraphLayout;
import com.genlogic.GraphLayout.GlgGraphNode;
import com.genlogic.GraphLayout.GlgPoint;

public class TestGLGGraphLayout {
	
	public static final int TYPE_DEFAULT = 0;
	public static final int TYPE_EXTERNAL = -1;
   
	public static void printEdgeFlags(GlgGraphEdge edge) {
		System.out.println(edge.data +":  " + edge.external + "  " + edge.start_node.external + "  " 
				+ edge.end_node.external);
	}
	
	public static void printNodePos(GlgGraphLayout graph, GlgGraphNode node, int type) {

                GlgPoint nodePos;
                if( type == TYPE_DEFAULT )
                   nodePos = graph.GetNodePosition(node);
                else if( type == TYPE_EXTERNAL )
                   nodePos = node.position;
                else return;

		System.out.println(node.data + ":  " + nodePos.x + "  " + nodePos.y);
	}
	
	public static void main(String[] args) {
		GlgGraphLayout graphRoot = new GlgGraphLayout();
		GlgCube dimsRoot = new GlgCube();
		dimsRoot.p1 = new GlgPoint(0, 0, 0);
		dimsRoot.p2 = new GlgPoint(700, 400, 0);
		graphRoot.dimensions = dimsRoot;
		GlgGraphLayout graph1 = new GlgGraphLayout();
		GlgCube dims1 = new GlgCube();
		dims1.p1 = new GlgPoint(-100, -100, 0);
		dims1.p2 = new GlgPoint(100, 100, 0);
		graph1.dimensions = dims1;
		GlgGraphLayout graph2 = new GlgGraphLayout();
		GlgCube dims2 = new GlgCube();
		dims2.p1 = new GlgPoint(-100, -100, 0);
		dims2.p2 = new GlgPoint(100, 100, 0);
		graph2.dimensions = dims2;
		GlgGraphNode graphNode1 = graphRoot.AddNode(null, TYPE_DEFAULT, graph1);
		graphRoot.SetNodePosition(graphNode1, 200, 200, 0);
		GlgGraphNode graphNode2 = graphRoot.AddNode(null, TYPE_DEFAULT, graph2);
		graphRoot.SetNodePosition(graphNode2, 500, 200, 0);
		GlgGraphNode node1a = graph1.AddNode(null, TYPE_DEFAULT, "Node1a");
		graph1.SetNodePosition(node1a, 50, 0, 0);
		GlgGraphNode node1b = graph1.AddNode(null, TYPE_DEFAULT, "Node1b");
		graph1.SetNodePosition(node1b, 0, 50, 0);
		GlgGraphEdge edge1int = graph1.AddEdge(node1a, node1b, null, TYPE_DEFAULT, "Edge1Int");
		GlgGraphNode node2a = graph2.AddNode(null, TYPE_DEFAULT, "Node2a");
		graph1.SetNodePosition(node2a, 50, 0, 0);
		GlgGraphNode node2b = graph2.AddNode(null, TYPE_DEFAULT, "Node2b");
		graph1.SetNodePosition(node2b, 0, 50, 0);
		GlgGraphEdge edge2int = graph2.AddEdge(node2a, node2b, null, TYPE_DEFAULT, "Edge2Int");
		GlgGraphNode node1ext = graph1.AddNode(null, TYPE_EXTERNAL, "Node1ext");
		//graph1.SetNodePosition(node1ext, 300, 0, 0);

                // External nodes should be positioned using the GlgGraphNode.position 
                // field, instead of using SetNodePosition().
                node1ext.position.x = 300.;
                node1ext.position.y = 0.;
		GlgGraphEdge edge1ext = graph1.AddEdge(node1a, node1ext, null, TYPE_EXTERNAL, "Edge1Ext");
		GlgGraphNode node2ext = graph2.AddNode(null, TYPE_EXTERNAL, "Node2ext");
		//graph2.SetNodePosition(node2ext, -300, 0, 0);
                node2ext.position.x = -300.;
                node2ext.position.y = 0.;
		GlgGraphEdge edge2ext = graph2.AddEdge(node2a, node2ext, null, TYPE_EXTERNAL, "Edge2Ext");
		System.out.println(" === Edge External Flags ===");
		printEdgeFlags(edge1int);
		printEdgeFlags(edge2int);
		printEdgeFlags(edge1ext);
		printEdgeFlags(edge2ext);
		System.out.println(" === Initial Node Positions ===");
		printNodePos(graph1, node1a, TYPE_DEFAULT);
		printNodePos(graph1, node1b, TYPE_DEFAULT);
		printNodePos(graph1, node1ext, TYPE_EXTERNAL);
		printNodePos(graph2, node2a, TYPE_DEFAULT);
		printNodePos(graph2, node2b, TYPE_DEFAULT);
		printNodePos(graph2, node2ext, TYPE_EXTERNAL);
		while(!graph1.SpringIterate()) {}
		System.out.println(" === Node Positions After Graph 1 Spring Layout ===");
		printNodePos(graph1, node1a, TYPE_DEFAULT);
		printNodePos(graph1, node1b, TYPE_DEFAULT);
		printNodePos(graph1, node1ext, TYPE_EXTERNAL);
		printNodePos(graph2, node2a, TYPE_DEFAULT);
		printNodePos(graph2, node2b, TYPE_DEFAULT);
		printNodePos(graph2, node2ext, TYPE_EXTERNAL);
		while(!graph2.SpringIterate()) {}
		System.out.println(" === Node Positions After Graph 2 Spring Layout ===");
		printNodePos(graph1, node1a, TYPE_DEFAULT);
		printNodePos(graph1, node1b, TYPE_DEFAULT);
		printNodePos(graph1, node1ext, TYPE_EXTERNAL);

		printNodePos(graph2, node2a, TYPE_DEFAULT);
		printNodePos(graph2, node2b, TYPE_DEFAULT);
		printNodePos(graph2, node2ext, TYPE_EXTERNAL);
		System.out.println(" === End ===");
	}

}
