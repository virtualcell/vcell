/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.gui.graph;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

import org.vcell.util.graphlayout.ContainedGraph;
import org.vcell.util.graphlayout.ContainedGraph.Container;
import org.vcell.util.graphlayout.ContainedGraph.Node;

public class VCellGraphToContainedGraphMapper {

	protected final GraphModel vcellGraph;
	protected final ContainedGraph containedGraph;
	
	public VCellGraphToContainedGraphMapper(GraphModel vcellGraph, ContainedGraph containedGraph) {
		this.vcellGraph = vcellGraph;
		this.containedGraph = containedGraph;
	}

	public VCellGraphToContainedGraphMapper(GraphModel vcellGraph) {
		this(vcellGraph, new ContainedGraph.Default());
	}
	
	public GraphModel getVCellGraph() { return vcellGraph; }
	public ContainedGraph getContainedGraph() { return containedGraph; }
	
	public void updateContainedGraphFromVCellGraph() {
		containedGraph.clear();
		for(Shape shape : vcellGraph.getShapes()) {
			GraphContainerLayout containerLayout = vcellGraph.getContainerLayout();
			if(containerLayout.isContainerForAutomaticLayout(shape)) {
				Rectangle boundary = containerLayout.getBoundaryForAutomaticLayout(shape);
				Container container = containedGraph.addContainer(shape, boundary.getX(), boundary.getY(),
						boundary.getWidth(), boundary.getHeight());
				for(Shape child : shape.getChildren()) {
					if(containerLayout.isNodeForAutomaticLayout(child)) {							
						Point absLoc = child.getSpaceManager().getAbsLoc();
						Dimension size = child.getSpaceManager().getSize();
						containedGraph.addNode(child, container, absLoc.getX(), absLoc.getY(), 
								size.getWidth(), size.getHeight());
					}
				}					
			}
		}
		for(Shape shape : vcellGraph.getShapes()) {
			if(shape instanceof EdgeShape) {
				EdgeShape edgeShape = (EdgeShape) shape;
				Node nodeStart = containedGraph.getNode(edgeShape.getStartShape());
				Node nodeEnd = containedGraph.getNode(edgeShape.getEndShape());
				if(nodeStart != null && nodeEnd != null) {
					containedGraph.addEdge(edgeShape, nodeStart, nodeEnd);
				}
			}
		}
	}
	
	public void updateVCellGraphFromContainedGraph() {
		for(Node node : containedGraph.getNodes()) {
			Object object = node.getObject();
			if(object instanceof Shape) {
				Shape shape = (Shape) object;
				shape.getSpaceManager().setAbsLoc((int) node.getX(), (int) node.getY());
			}
		}
	}
	
}
