/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.rdf;

import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;

/*   ModelComparer  --- by Oliver Ruebenacker, UCHC --- May 2008 to November 2009
 *   Compares two models, calculating two differences, which are models
 *   containing statement of one models impossible to match with the other one
 *   for nay mapping of blank nodes
 */

import org.openrdf.model.BNode;
import org.openrdf.model.Graph;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;
import org.openrdf.model.util.ModelUtil;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.sbpax.impl.HashGraph;
import org.sbpax.schemas.util.DefaultNameSpaces;
import org.sbpax.util.MultiHashMap;
import org.sbpax.util.MultiMap;
import org.sbpax.util.SesameRioUtil;

public class ModelComparer {
	
	protected Graph model1, model2, diff1, diff2;

	public static Graph createDiff(Graph model1, Graph model2) {
		Graph graph = new HashGraph();
		graph.addAll(model1);
		graph.removeAll(model2);
		return graph;
	}
	
	public ModelComparer(Graph model1New, Graph model2New) {
		model1 = model1New;
		model2 = model2New;
		diff1 = createDiff(model1, model2);
		diff2 = createDiff(model2, model1);
		Set<Graph> subModels1 = new HashSet<Graph>();
		extractSubModels(diff1, subModels1);
		Set<Graph> subModels2 = new HashSet<Graph>();
		extractSubModels(diff2, subModels2);
		Set<Graph> matched1 = new HashSet<Graph>();
		Set<Graph> matched2 = new HashSet<Graph>();
		for(Graph subModel1 : subModels1) {
			if(matched1.contains(subModel1)) { continue; }
			for(Graph subModel2 : subModels2) {
				if(matched2.contains(subModel2)) { continue; }		
				if(ModelUtil.equals(subModel1, subModel2)) {
					diff1.remove(subModel1);
					diff2.remove(subModel2);
					matched1.add(subModel1);
					matched2.add(subModel2);
				}
			}			

		}
	}

	private void extractSubModels(Graph diff, Set<Graph> subModels) {
		MultiMap<Resource, Resource> bNodesMap = new MultiHashMap<Resource, Resource>();
		MultiMap<Resource, Statement> triples = new MultiHashMap<Resource, Statement>();
		for(Statement statement : diff) {
			Set<Resource> bNodesInTriple = new HashSet<Resource>();
			recordIfBlankNode(statement.getSubject(), statement, bNodesInTriple, triples);
			recordIfBlankNode(statement.getPredicate(), statement, bNodesInTriple, triples);
			recordIfBlankNode(statement.getObject(), statement, bNodesInTriple, triples);
			Set<Resource> bNodesUnion = new HashSet<Resource>();
			bNodesUnion.addAll(bNodesInTriple);
			for(Resource bNode : bNodesInTriple) { 
				Set<Resource> bNodes = bNodesMap.get(bNode);
				if(bNodes != null) { bNodesUnion.addAll(bNodes); }
			}
			for(Resource bNode : bNodesUnion) { bNodesMap.put(bNode, bNodesUnion); }
		}
		for(Set<Resource> bNodes : bNodesMap.values()) {
			Graph model = new HashGraph();
			for(Resource bNode : bNodes) {
				for(Statement statement : triples.get(bNode)) {
					model.add(statement);
				}
			}
			subModels.add(model);
		}
	}

	private void recordIfBlankNode(Value node, Statement statement,
			Set<Resource> bNodes, MultiMap<Resource, Statement> triples) {
		if(node instanceof BNode) {
			Resource resource = (Resource) node;
			bNodes.add(resource);
			triples.add(resource, statement);
		}
	}
	
	public Graph model1() { return model1; }
	public Graph model2() { return model2; }
	public Graph diff1() { return diff1; }
	public Graph diff2() { return diff2; }
	
	public String getStats() {
		long nm1 = model1().size();
		long nm2 = model2().size();
		long nd1 = diff1().size();
		long nd2 = diff2().size();
		long nMax = Math.max(Math.max(nm1, nm2), Math.max(nd1, nd2));
		long pm1 = (100*nm1) / nMax;
		long pm2 = (100*nm2) / nMax;
		long pd1 = (100*nd1) / nMax;
		long pd2 = (100*nd2) / nMax;
		return "(m1, m2, d1, d2) = (" + nm1 + ", " + nm2 + ", " + nd1 + ", " + nd2 + ") = (" + 
		pm1 + "%, " + pm2 + "%, " + pd1 + "%, " + pd2 + "%)"; 
	}
	
	public void printReport(PrintStream out) throws RDFHandlerException {
		out.println("### Begin Lacking (" + getStats() + ") ###");
		SesameRioUtil.writeRDFToStream(out, diff1(), DefaultNameSpaces.defaultMap.convertToMap(), RDFFormat.N3);
		out.println("### End Lacking (" + getStats() + ") ###");
		if(diff2().size() > 0) {
			out.println("### Begin Erroneous (" + getStats() + ") ###");
			SesameRioUtil.writeRDFToStream(out, diff2(), DefaultNameSpaces.defaultMap.convertToMap(), RDFFormat.N3);
			out.println("### End Erroneous (" + getStats() + ") ###");
		}
	}
	

	
}
