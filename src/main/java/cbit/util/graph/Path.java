/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.util.graph;
import java.util.*;
/**
 * Insert the type's description here.
 * Creation date: (2/11/2002 12:19:18 AM)
 * @author: Jim Schaff
 */
public class Path extends Trail {
	private boolean bClosed = false;

/**
 * Path constructor comment.
 */
public Path() {
	super();
}


/**
 * Insert the method's description here.
 * Creation date: (2/11/2002 1:15:04 AM)
 * @param path cbit.vcell.mapping.potential.Path
 */
public Path(Path path) {
	super(path);
}


/**
 * Insert the method's description here.
 * Creation date: (2/11/2002 1:15:04 AM)
 * @param path cbit.vcell.mapping.potential.Path
 */
public Path(Path path, Edge edge) {
	super(path);
	addEdge(edge);
}


/**
 * Insert the method's description here.
 * Creation date: (2/10/2002 10:49:12 PM)
 * @param newNode cbit.vcell.mapping.potential.Node
 */
public void addEdge(Edge newEdge) {
	if (bClosed){
		throw new RuntimeException("path is already closed, can't add another edge");
	}
	//
	// "Paths" are "Trails" without repeated vertices (except if path is closed).
	//
	if (contains(newEdge.getNode1()) && contains(newEdge.getNode2())){
		//
		// if newEdge is adjacent to edge[0] and edge[N-1], then it's ok, mark closed.
		//
		if (newEdge.isAdjacent(getFirstEdge()) && newEdge.isAdjacent(getLastEdge())){
			bClosed = true;
		}else{
			throw new RuntimeException("edge "+newEdge+" already exists in list");
		}
	}
	super.addEdge(newEdge);
}

//public Node[] getNodesTraversed_original() {
//	Edge edges[] = getEdges();
//	if (edges.length==1){
//		return new Node[] { edges[0].getNode1(), edges[0].getNode2() };
//	}else if (edges.length == 2){
//		if (edges[1].getNode1().equals(edges[0].getNode2())){
//			return new Node[] { edges[0].getNode1(), edges[0].getNode2(), edges[1].getNode2() };
//		}else{
//			return new Node[] { edges[0].getNode2(), edges[0].getNode1(), edges[1].getNode2() };
//		}
//	}else{
//		Vector<Node> nodeList = new Vector<Node>();
//		//
//		// look ahead to see which node is in common with the next edge, choose the other.
//		//
//		for (int i = 0; i < edges.length-1; i++){
//			if (edges[i+1].hasEndPoint(edges[i].getNode2())){
//				nodeList.add(edges[i].getNode1());
//			}else{
//				nodeList.add(edges[i].getNode2());
//			}
//		}
//		//
//		// look back to see which node is in common with the previous edge, choose that one.
//		//
//		if (edges[edges.length-2].hasEndPoint(edges[edges.length-1].getNode1())){
//			nodeList.add(edges[edges.length-1].getNode1());
//			nodeList.add(edges[edges.length-1].getNode2());
//		}else{
//			nodeList.add(edges[edges.length-1].getNode2());
//			nodeList.add(edges[edges.length-1].getNode1());
//		}
//		Node nodes[] = new Node[nodeList.size()];
//		nodeList.copyInto(nodes);
//		return nodes;
//	}
//}
//
//public Node[] getNodesTraversed_current() {
//	Edge edges[] = getEdges();
//	if (edges.length==1){
//		return new Node[] { edges[0].getNode1(), edges[0].getNode2() };
//	}else{
//		Vector<Node> nodeList = new Vector<Node>();
//		//
//		// look ahead to see which node is in common with the next edge, choose the other.
//		//
//		for (int i = 0; i < edges.length-1; i++){
//			if (edges[i+1].hasEndPoint(edges[i].getNode2())){
//				nodeList.add(edges[i].getNode1());
//			}else{
//				nodeList.add(edges[i].getNode2());
//			}
//		}
//		//
//		// look back to see which node is in common with the previous edge, choose that one.
//		//
//		if (edges[edges.length-2].hasEndPoint(edges[edges.length-1].getNode1())){
//			nodeList.add(edges[edges.length-1].getNode1());
//			nodeList.add(edges[edges.length-1].getNode2());
//		}else{
//			nodeList.add(edges[edges.length-1].getNode2());
//			nodeList.add(edges[edges.length-1].getNode1());
//		}
//		Node nodes[] = new Node[nodeList.size()];
//		nodeList.copyInto(nodes);
//		return nodes;
//	}
//}

/**
 * Insert the method's description here.
 * Creation date: (2/12/2002 2:49:29 PM)
 * @return cbit.vcell.mapping.potential.Node[]
 */
public Node[] getNodesTraversed() {
	Edge edges[] = getEdges();
	if (edges.length==1){
		return new Node[] { edges[0].getNode1(), edges[0].getNode2() };
	}else{
		Vector<Node> nodeList = new Vector<Node>();
		// consider the first edge is {A,B}
		boolean bEdge1HasNodeA = edges[1].hasEndPoint(edges[0].getNode1());
		boolean bEdge1HasNodeB = edges[1].hasEndPoint(edges[0].getNode2());
		
		if (bEdge1HasNodeA && bEdge1HasNodeB){
			// simple loop
			if (edges.length==2){
				return new Node[] { edges[0].getNode1(), edges[0].getNode2(), edges[0].getNode1() };
			}else{
				throw new RuntimeException("path loops back on itself before last edge");
			}
		}
		
		// initialize node list with either {A,B} or {B,A}
		if (bEdge1HasNodeA){
			// {A,B} {A,C}||{C,A}  ==> B,A,...
			nodeList.add(edges[0].getNode2());
			nodeList.add(edges[0].getNode1());
		}else if (bEdge1HasNodeB){
			// {A,B} {B,C}||{C,B}  ==> A,B,...
			nodeList.add(edges[0].getNode1());
			nodeList.add(edges[0].getNode2());
		}else{
			throw new RuntimeException("first two edges are not adjacent");
		}
		//
		// look at the remaining edges to see which node is different than the last node in the list.
		//
		for (int i = 1; i < edges.length; i++){
			if (edges[i].getNode1().equals(nodeList.lastElement())){
				nodeList.add(edges[i].getNode2());
			}else if (edges[i].getNode2().equals(nodeList.lastElement())){
				nodeList.add(edges[i].getNode1());
			}else{
				throw new RuntimeException("consecutive edges are not adjacent");
			}
		}
		Node nodes[] = new Node[nodeList.size()];
		nodeList.copyInto(nodes);
		return nodes;
	}
}

public static void test(){
	Node node1 = new Node("A");
	Node node2 = new Node("B");
	Node node3 = new Node("C");
	Node node4 = new Node("D");
	Edge edge12 = new Edge(node1,node2,new Object());
	Edge edge21 = new Edge(node2,node1,new Object());
	Edge edge12_ = new Edge(node1,node2,new Object());
	Edge edge21_ = new Edge(node2,node1,new Object());
	Edge edge13_ = new Edge(node1,node3,new Object());
	Edge edge31_ = new Edge(node3,node1,new Object());
	Edge edge23 = new Edge(node2,node3,new Object());
	Edge edge32 = new Edge(node3,node2,new Object());
	Edge edge34 = new Edge(node3,node4,new Object());
	Edge edge43 = new Edge(node4,node3,new Object());
	//
	// edges arranged with nodes in alphabetical order, all combinations of edge reversals up to path length 4
	//
	Edge[][] edges = new Edge[][] {
			 // ==> A,B
			 { edge12 },
			 // ==> A,B,C
			 { edge12, edge23 },
			 { edge12, edge32 },
			 { edge21, edge23 },
			 { edge21, edge32 },
			 // ==> A,B,A (two edges in a loop)
			 { edge12, edge12_ }, // same direction
			 { edge12, edge21_ }, // opposite direction
			 // ==> A,B,C,D
			 { edge12, edge23, edge34 },
			 { edge12, edge23, edge43 },
			 { edge12, edge32, edge34 },
			 { edge12, edge32, edge43 },
			 { edge21, edge23, edge34 },
			 { edge21, edge23, edge43 },
			 { edge21, edge32, edge34 },
			 { edge21, edge32, edge43 },
			 // ==> A,B,C,A
			 { edge12, edge23, edge13_ },
			 { edge12, edge23, edge31_ },
			 { edge12, edge32, edge13_ },
			 { edge12, edge32, edge31_ },
			 { edge12, edge32, edge21_ }, // Exception: 3rd edge doesn't connect to loose end 2nd edge
	};
	System.out.println("==== Path.test() good/bad ====");
	for (int trial=0; trial<edges.length; trial++){
		//
		// print each path and computed nodesTraversed[] ... should be in alphabetical order
		//
		Path path = new Path();
		Edge[] pathEdges = edges[trial];
		for (int i=0;i<pathEdges.length;i++){
			System.out.print("("+pathEdges[i].getNode1().getName()+","+pathEdges[i].getNode2().getName()+") ");
			path.addEdge(pathEdges[i]);
		}
//		System.out.print(" ==original==> ");
//		try {
//			Node[] nodesTraversed2 = path.getNodesTraversed_original();
//			for (int i=0;i<nodesTraversed2.length; i++){
//				System.out.print(" "+nodesTraversed2[i].getName());
//			}
//		}catch (Exception e){
//			System.out.print(e.getMessage());
//		}
//		System.out.print(" ==current==> ");
//		try {
//			Node[] nodesTraversed3 = path.getNodesTraversed_current();
//			for (int i=0;i<nodesTraversed3.length; i++){
//				System.out.print(" "+nodesTraversed3[i].getName());
//			}
//		}catch (Exception e){
//			System.out.print(e.getMessage());
//		}
//		System.out.print(" ==new==> ");
		try {
			Node[] nodesTraversed1 = path.getNodesTraversed();
			for (int i=0;i<nodesTraversed1.length; i++){
				System.out.print(" "+nodesTraversed1[i].getName());
			}
		}catch (Exception e){
			System.out.print(e.getMessage());
		}
		System.out.println();
	}
}

/**
 * Insert the method's description here.
 * Creation date: (2/11/2002 1:27:57 AM)
 * @return boolean
 */
public boolean isClosed() {
	return bClosed;
}
}
