package org.vcell.sybil.rdf.smelt;

/*   BP2PartSplitter  --- by Oliver Ruebenacker, UCHC --- July 2009
 *   Multiplies physical entity participants used by multiple interactions to have one for
 *   each.
 */

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.vcell.sybil.rdf.schemas.BioPAX2;
import org.vcell.sybil.util.sets.SetOfFour;
import org.vcell.sybil.util.sets.SetOfOne;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public class BP2ParticipantSplitter implements RDFSmelter {
	
	protected Map<Resource, Model> partToStmts = new HashMap<Resource, Model>();
	protected Map<Statement, Statement> mapStmt = new HashMap<Statement, Statement>();
	protected Map<Resource, Set<Resource>> partToParts = new HashMap<Resource, Set<Resource>>();
	protected Model stmtsIgnore = ModelFactory.createDefaultModel();
	
	public Model smelt(Model rdfOld) {
		Model rdf = ModelFactory.createDefaultModel();
		rdf.setNsPrefixes(rdfOld);
		Set<Property> partProps = new SetOfFour<Property>(BioPAX2.PARTICIPANTS, BioPAX2.CONTROLLER,
				BioPAX2.LEFT, BioPAX2.RIGHT);
		Property partPropRoot = BioPAX2.PARTICIPANTS;
		for(Property partProp : partProps) {
			StmtIterator stmtIter = rdfOld.listStatements(null, partProp, (RDFNode) null);
			while(stmtIter.hasNext()) {
				Statement statement = stmtIter.nextStatement();
				RDFNode partObject = statement.getObject();
				if(partObject instanceof Resource) {
					Resource part = (Resource) partObject;
					Model stmts = partToStmts.get(part);
					if(stmts == null) {
						stmts = ModelFactory.createDefaultModel();
						partToStmts.put(part, stmts);
					}
					if(!stmts.contains(statement)) {
						stmts.add(statement);
						if(!partPropRoot.equals(partProp)) {
							Statement stmtRoot = 
								rdfOld.createStatement(statement.getSubject(), partPropRoot, 
										statement.getObject());
							stmts.remove(stmtRoot);
							stmtsIgnore.add(stmtRoot);
						}
					}
				}
			}
			for(Map.Entry<Resource, Model> entry : partToStmts.entrySet()) {
				Resource part = entry.getKey();
				Set<Resource> parts = new HashSet<Resource>();
				parts.add(part);
				partToParts.put(part, parts);
				Model legs = entry.getValue();
				boolean firstRound = true;
				StmtIterator stmtIter2 = legs.listStatements();
				while(stmtIter2.hasNext()) {
					Statement stmt = stmtIter2.nextStatement();
					if(firstRound) { 
						mapStmt.put(stmt, stmt); 
					} else {
						Resource partNew = rdf.createResource();
						Statement stmtNew = 
							rdf.createStatement(stmt.getSubject(), stmt.getPredicate(), partNew);
						mapStmt.put(stmt, stmtNew); 
						partToParts.get(part).add(partNew);
					}
					firstRound = false;
				}
			}
		}
		StmtIterator stmtIter = rdfOld.listStatements();
		while(stmtIter.hasNext()) {
			Statement statement = stmtIter.nextStatement();
			if(!stmtsIgnore.contains(statement)) {
				Statement statement2 = mapStmt.get(statement);
				if(statement2 != null) {
					rdf.add(statement2);
				} else {
					Resource subject = statement.getSubject();
					Property predicate = statement.getPredicate();
					RDFNode object = statement.getObject();
					Set<Resource> subjects = partToParts.get(subject);
					if(subjects == null) { subjects = new SetOfOne<Resource>(subject); }
					Set<Resource> objectResources = partToParts.get(object);
					Set<RDFNode> objects = null;
					if(objectResources != null) {
						objects = Collections.<RDFNode>unmodifiableSet(objectResources);
					} else {
						objects = new SetOfOne<RDFNode>(object);
					}
					for(Resource subjectNew : subjects) {
						for(RDFNode objectNew : objects) {
							rdf.add(subjectNew, predicate, objectNew);
						}
					}
				}
			}
		}
		return rdf;
	}	

}
