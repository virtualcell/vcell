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

/*   RDFChopper  --- by Oliver Ruebenacker, UCHC --- June 2009
 *   Chops a model into smaller models according to closeness of a set of Resources
 */

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.openrdf.model.Graph;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;
import org.sbpax.impl.HashGraph;

public class RDFChopper {
	
	protected Map<Resource, Graph> chops = new HashMap<Resource, Graph>();
	protected Map<Resource, Set<Resource>> neighborhoods = new HashMap<Resource, Set<Resource>>();
	protected Graph remains = new HashGraph();

	public RDFChopper(Graph model, Set<Resource> resources) {
		HashMap<Resource, Graph> chopsNew = new HashMap<Resource, Graph>();
		HashMap<Resource, Set<Resource>> neighborhoodsNew = new HashMap<Resource, Set<Resource>>();
		for(Resource resource : resources) {
			chops.put(resource, new HashGraph());
			neighborhoods.put(resource, new HashSet<Resource>());
			chopsNew.put(resource, new HashGraph());
			Set<Resource> neighborhoodNew = new HashSet<Resource>();
			neighborhoodNew.add(resource);
			neighborhoodsNew.put(resource, neighborhoodNew);
		}
		remains.addAll(model);
		boolean seenProgress = true;
		while(seenProgress) {
			seenProgress = false;
			// add statements pertaining to your "neighbors" (initially yourself) ... then the neighbor list grows via the new statements.
			for(Resource resource : resources) {
				Set<Resource> neighborhoodNew = neighborhoodsNew.get(resource);
				Graph chopNew = chopsNew.get(resource);
				for(Resource neighborNew : neighborhoodNew) {
					Iterator<Statement> stmtIter = remains.match(neighborNew, null, null);
					while(stmtIter.hasNext()) {
						Statement stmt = stmtIter.next();
						chopNew.add(stmt);
					}
					stmtIter = remains.match(null, null, neighborNew);
					while(stmtIter.hasNext()) { 
						Statement stmt = stmtIter.next();
						chopNew.add(stmt);
					}					
				}
				remains.removeAll(chopNew);
				neighborhoods.get(resource).addAll(neighborhoodNew);
				if(!neighborhoodNew.isEmpty()) { 
					seenProgress = true; 
				}
				neighborhoodNew.clear();
			}
			for(Resource resource : resources) {
				Set<Resource> neighborhood = neighborhoods.get(resource);
				Set<Resource> neighborhoodNew = neighborhoodsNew.get(resource);
				Graph chopNew = chopsNew.get(resource);
				// add neighbors from all statements in this chunk.
				Iterator<Statement> stmtIter = chopNew.iterator();
				if(stmtIter.hasNext()) { 
					seenProgress = true; 
				}
				while(stmtIter.hasNext()) {
					Statement statement = stmtIter.next();
					neighborhoodNew.add(statement.getSubject());
					Value object = statement.getObject();
					if(object instanceof Resource) { 
						neighborhoodNew.add((Resource) object); 
					}					
				}
				neighborhoodNew.removeAll(neighborhood); // keep only truly "new" resources
				neighborhood.addAll(neighborhoodNew);    // keep total list of all resources (new and old).
				chops.get(resource).addAll(chopNew);
				chopNew.clear();
			}
		}
		// remove statements that have been added to "resources"
		//	remains.remove(chopNew);
	}
	
	public Map<Resource, Graph> getChops() {
		return chops; 
	}
	public Map<Resource, Set<Resource>> getNeighborhoods() {
		return neighborhoods; 
	}
	public Graph getRemains() { 
		return remains; 
	}
	
}
