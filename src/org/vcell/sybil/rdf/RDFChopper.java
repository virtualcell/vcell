package org.vcell.sybil.rdf;

/*   RDFChopper  --- by Oliver Ruebenacker, UCHC --- June 2009
 *   Chops a model into smaller models according to closeness of a set of Resources
 */

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public class RDFChopper {
	
	protected Map<Resource, Model> chops = new HashMap<Resource, Model>();
	protected Map<Resource, Set<Resource>> neighborhoods = new HashMap<Resource, Set<Resource>>();
	protected Model remains = ModelFactory.createDefaultModel();

	public RDFChopper(Model model, Set<Resource> resources) {
		HashMap<Resource, Model> chopsNew = new HashMap<Resource, Model>();
		HashMap<Resource, Set<Resource>> neighborhoodsNew = new HashMap<Resource, Set<Resource>>();
		for(Resource resource : resources) {
			chops.put(resource, ModelFactory.createDefaultModel());
			neighborhoods.put(resource, new HashSet<Resource>());
			chopsNew.put(resource, ModelFactory.createDefaultModel());
			Set<Resource> neighborhoodNew = new HashSet<Resource>();
			neighborhoodNew.add(resource);
			neighborhoodsNew.put(resource, neighborhoodNew);
		}
		remains.add(model);
		boolean seenProgress = true;
		while(seenProgress) {
			seenProgress = false;
			// add statements pertaining to your "neighbors" (initially yourself) ... then the neighbor list grows via the new statements.
			for(Resource resource : resources) {
				Set<Resource> neighborhoodNew = neighborhoodsNew.get(resource);
				Model chopNew = chopsNew.get(resource);
				for(Resource neighborNew : neighborhoodNew) {
					StmtIterator stmtIter = remains.listStatements(neighborNew, null, (RDFNode) null);
					while(stmtIter.hasNext()) {
						Statement stmt = stmtIter.nextStatement();
						chopNew.add(stmt);
					}
					stmtIter = remains.listStatements(null, null, neighborNew);
					while(stmtIter.hasNext()) { 
						Statement stmt = stmtIter.nextStatement();
						chopNew.add(stmt);
					}					
				}
				remains.remove(chopNew);
				neighborhoods.get(resource).addAll(neighborhoodNew);
				if(!neighborhoodNew.isEmpty()) { 
					seenProgress = true; 
				}
				neighborhoodNew.clear();
			}
			for(Resource resource : resources) {
				Set<Resource> neighborhood = neighborhoods.get(resource);
				Set<Resource> neighborhoodNew = neighborhoodsNew.get(resource);
				Model chopNew = chopsNew.get(resource);
				// add neighbors from all statements in this chunk.
				StmtIterator stmtIter = chopNew.listStatements();
				if(stmtIter.hasNext()) { 
					seenProgress = true; 
				}
				while(stmtIter.hasNext()) {
					Statement statement = stmtIter.nextStatement();
					neighborhoodNew.add(statement.getSubject());
					RDFNode object = statement.getObject();
					if(object instanceof Resource) { 
						neighborhoodNew.add((Resource) object); 
					}					
				}
				neighborhoodNew.removeAll(neighborhood); // keep only truly "new" resources
				neighborhood.addAll(neighborhoodNew);    // keep total list of all resources (new and old).
				chops.get(resource).add(chopNew);
				chopNew.removeAll();
			}
		}
		// remove statements that have been added to "resources"
		//	remains.remove(chopNew);
		System.out.println("done");
	}
	
	public Map<Resource, Model> getChops() {
		return chops; 
	}
	public Map<Resource, Set<Resource>> getNeighborhoods() {
		return neighborhoods; 
	}
	public Model getRemains() { 
		return remains; 
	}
	
}
