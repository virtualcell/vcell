package org.vcell.sybil.rdf;

/*   ModelComparer  --- by Oliver Ruebenacker, UCHC --- May 2008 to November 2009
 *   Compares two models, calculating two differences, which are models
 *   containing statement of one models impossible to match with the other one
 *   for nay mapping of blank nodes
 */

import org.vcell.sybil.util.collections.MultiHashMap;
import org.vcell.sybil.util.collections.MultiMap;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;

public class ModelComparer {
	
	protected Model model1, model2, diff1, diff2;
	
	public ModelComparer(Model model1New, Model model2New) {
		model1 = model1New;
		model2 = model2New;
		diff1 = model1.difference(model2);
		diff1.setNsPrefixes(model1.getNsPrefixMap());
		diff2 = model2.difference(model1);
		diff2.setNsPrefixes(model2.getNsPrefixMap());
		Set<Model> subModels1 = new HashSet<Model>();
		extractSubModels(diff1, subModels1);
		Set<Model> subModels2 = new HashSet<Model>();
		extractSubModels(diff2, subModels2);
		Set<Model> matched1 = new HashSet<Model>();
		Set<Model> matched2 = new HashSet<Model>();
		for(Model subModel1 : subModels1) {
			if(matched1.contains(subModel1)) { continue; }
			for(Model subModel2 : subModels2) {
				if(matched2.contains(subModel2)) { continue; }				
				if(subModel1.isIsomorphicWith(subModel2)) {
					diff1.remove(subModel1);
					diff2.remove(subModel2);
					matched1.add(subModel1);
					matched2.add(subModel2);
				}
			}			

		}
	}

	private void extractSubModels(Model diff, Set<Model> subModels) {
		MultiMap<Resource, Resource> bNodesMap = new MultiHashMap<Resource, Resource>();
		MultiMap<Resource, Statement> triples = new MultiHashMap<Resource, Statement>();
		StmtIterator iter = diff.listStatements();
		while(iter.hasNext()) {
			Statement statement = iter.nextStatement();
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
			Model model = ModelFactory.createDefaultModel();
			for(Resource bNode : bNodes) {
				for(Statement statement : triples.get(bNode)) {
					model.add(statement);
				}
			}
			subModels.add(model);
		}
	}

	private void recordIfBlankNode(RDFNode node, Statement statement,
			Set<Resource> bNodes, MultiMap<Resource, Statement> triples) {
		if(node.isAnon()) {
			Resource resource = (Resource) node;
			bNodes.add(resource);
			triples.add(resource, statement);
		}
	}
	
	public Model model1() { return model1; }
	public Model model2() { return model2; }
	public Model diff1() { return diff1; }
	public Model diff2() { return diff2; }
	
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
	
	public void printReport(PrintStream out) {
		out.println("### Begin Lacking (" + getStats() + ") ###");
		diff1().write(out, "N3");
		out.println("### End Lacking (" + getStats() + ") ###");
		if(diff2().size() > 0) {
			out.println("### Begin Erroneous (" + getStats() + ") ###");
			diff2().write(out, "N3");
			out.println("### End Erroneous (" + getStats() + ") ###");
		}
	}
	

	
}
