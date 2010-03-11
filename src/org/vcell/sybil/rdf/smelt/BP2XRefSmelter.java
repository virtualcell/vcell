package org.vcell.sybil.rdf.smelt;

/*   BP2XRefSmelter  --- by Oliver Ruebenacker, UCHC --- July 2009
 *   Merges (makes owl:sameAs) equivalent XRefs in BioPAX Level 2 data
 */

import java.util.HashMap;
import java.util.Map;

import org.vcell.sybil.rdf.schemas.BioPAX2;
import org.vcell.sybil.util.keys.KeyOfTwo;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.OWL;

public class BP2XRefSmelter implements RDFSmelter {
	
	protected static class Key extends KeyOfTwo<String, String> {
		public Key(String db, String id) { super(db, id); }
		public String db() { return a(); }
		public String id() { return b(); }
	}
	
	protected Map<Key, Resource> map = new HashMap<Key, Resource>();
	
	public Model smelt(Model rdf) {
		Model rdfNew = ModelFactory.createDefaultModel();
		rdfNew.add(rdf);
		rdfNew.setNsPrefixes(rdf);
		StmtIterator stmtIterDB = rdfNew.listStatements(null, BioPAX2.DB, (RDFNode) null);
		while(stmtIterDB.hasNext()) {
			Statement statementDB = stmtIterDB.nextStatement();
			Resource xref = statementDB.getSubject();
			RDFNode dbNode = statementDB.getObject();
			if(dbNode instanceof Literal) {
				String db = ((Literal) dbNode).getString();
				if(db != null && db.length() > 0) {
					int rank = rank(xref);
					StmtIterator stmtIterID = xref.listProperties(BioPAX2.ID);
					while(stmtIterID.hasNext()) {
						Statement statementID = stmtIterID.nextStatement();
						RDFNode idNode = statementID.getObject();
						if(idNode instanceof Literal) {
							String id = ((Literal) idNode).getString();
							if(id != null && id.length() > 0) {
								Key key = new Key(db, id);
								Resource xref2 = map.get(key);
								int rank2 = rank(xref2);
								if(rank > rank2) {
									map.put(key, xref);
									if(xref != null && xref2 != null && !xref.equals(xref2)) {
										rdfNew.add(xref, OWL.sameAs, xref2);
									}
								} else if(xref != null && xref2 != null && !xref.equals(xref2)) {
									rdfNew.add(xref2, OWL.sameAs, xref);
								}
							}
						}
					}
				}
			}
		}
		return rdfNew;
	}	
	
	protected int rank(Resource resource) {
		int rank = 0;
		if(resource == null) { rank = 0; }
		else if(resource.isURIResource()) { rank = 2; }
		else { rank = 1; }
		return rank;
	}
	
}
