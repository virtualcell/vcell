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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.vcell.util.geometry2d.Point2D;
import org.vcell.util.geometry2d.Vector2D;
import org.vcell.util.graphlayout.ContainedGraph;
import org.vcell.util.graphlayout.NodesShift;
import org.vcell.util.graphlayout.ContainedGraph.Node;

public interface EnergySum {
	
	public static interface EnergyFunction {
		
		public static interface Factory {
			public EnergyFunction createFunction();
		}
		
		public int getNumberOfParameters();
		public double getEnergy(List<Point2D> ps);
		public Vector2D getForce(List<Point2D> ps, int index);
	}
	
	public static interface EnergyTerm {
		
		public static interface Factory {
			public Set<EnergySum.EnergyTerm> generateTerms(ContainedGraph graph);
		}
		
		public EnergyFunction getFunction();
		public List<Node> getNodes();
		public double getEnergy();
		public double getEnergy(NodesShift shift);
		public double getDifference(NodesShift shift);
		
		public static class Default implements EnergyTerm {
		
			protected final List<Node> nodes;
			protected final EnergyFunction function;
			
			public Default(List<Node> nodes, EnergyFunction function) {
				this.nodes = nodes;
				this.function = function;
			}

			public EnergyFunction getFunction() { return function; }
			public List<Node> getNodes() { return nodes; }
			
			public double getEnergy() { 
				ArrayList<Point2D> ps = new ArrayList<Point2D>();
				for(Node node : nodes) {
					ps.add(node.getCenter());
				}
				return function.getEnergy(ps); 
			}

			public double getEnergy(NodesShift shift) {
				ArrayList<Point2D> ps = new ArrayList<Point2D>();
				for(Node node : nodes) {
					ps.add(shift.getShiftedCenter(node));
				}
				return function.getEnergy(ps); 
			}

			public double getDifference(NodesShift shift) {
				ArrayList<Point2D> ps = new ArrayList<Point2D>();
				ArrayList<Point2D> psShifted = new ArrayList<Point2D>();
				boolean isAffectedByShift = false;
				for(Node node : nodes) {
					if(shift.isMovedNode(node)) { isAffectedByShift = true; }
					ps.add(node.getCenter());
					psShifted.add(shift.getShiftedCenter(node));
				}
				if(isAffectedByShift) {
					return function.getEnergy(psShifted) - function.getEnergy(ps);
				} else {
					return 0;
				}
			}
			
		}
		
	}
	
	public static interface Minimizer {
		public void minimize(EnergySum energySum);
	}
	
	public ContainedGraph getGraph();
	public void generateTerms(EnergyTerm.Factory factory);
	public Set<EnergyTerm> getTerms();
	public double getValue();
	public double getValue(NodesShift shift);
	public double getDifference(NodesShift shift);
	
	public static class Default implements EnergySum {
		
		public static RuntimeException getWrongNUmberOfParametersException(int required, int actual) {
			throw new RuntimeException("Needed " + required + " parameters, but got" + actual + ".");
		}
		
		protected final ContainedGraph graph;
		protected final Set<EnergyTerm> terms = new HashSet<EnergyTerm>();
		
		public Default(ContainedGraph graph) {
			this.graph = graph;
		}
		
		public ContainedGraph getGraph() { return graph; }
		
		public void generateTerms(EnergyTerm.Factory factory) {
			terms.addAll(factory.generateTerms(graph));
		}
		
		public Set<EnergyTerm> getTerms() { return terms; }
		
		public double getValue() {
			double sum = 0;
			for(EnergyTerm term : terms) {
				sum += term.getEnergy();
			}
			return sum;
		}

		public double getValue(NodesShift shift) {
			double sum = 0;
			for(EnergyTerm term : terms) {
				sum += term.getEnergy(shift);
			}
			return sum;
		}

		public double getDifference(NodesShift shift) {
			double sum = 0;
			for(EnergyTerm term : terms) {
				sum += term.getDifference(shift);
			}
			return sum;
		}

	}

}
