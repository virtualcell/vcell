package org.vcell.sybil.rdf.smelt;

/*   BP2XRefSmelter  --- by Oliver Ruebenacker, UCHC --- July 2009
 *   Merges (makes owl:sameAs) equivalent Open Controlled Vocabulary in BioPAX Level 2 data
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

public class BP2OCVSmelter implements RDFSmelter {
	
	protected static class Key extends KeyOfTwo<String, Resource> {
		public Key(String term, Resource xref) { super(term, xref); }
		public String term() { return a(); }
		public Resource xref() { return b(); }
	}
	
	protected Map<Key, Resource> map = new HashMap<Key, Resource>();
	
	public Model smelt(Model rdf) {
		Model rdfNew = ModelFactory.createDefaultModel();
		rdfNew.add(rdf);
		rdfNew.setNsPrefixes(rdf);
		StmtIterator stmtIterTerm = rdfNew.listStatements(null, BioPAX2.TERM, (RDFNode) null);
		while(stmtIterTerm.hasNext()) {
			Statement statementTerm = stmtIterTerm.nextStatement();
			Resource ocv = statementTerm.getSubject();
			RDFNode termNode = statementTerm.getObject();
			if(termNode instanceof Literal) {
				String term = ((Literal) termNode).getString();
				if(term != null && term.length() > 0) {
					int rank = rank(ocv);
					StmtIterator stmtIterXRef = ocv.listProperties(BioPAX2.XREF);
					while(stmtIterXRef.hasNext()) {
						Statement statementXRef = stmtIterXRef.nextStatement();
						RDFNode xRefNode = statementXRef.getObject();
						if(xRefNode instanceof Resource) {
							Resource xRef = ((Resource) xRefNode);
							if(xRef != null) {
								Key key = new Key(term, xRef);
								Resource ocv2 = map.get(key);
								int rank2 = rank(ocv2);
								if(rank > rank2) {
									map.put(key, ocv);
									if(ocv != null && ocv2 != null && !ocv.equals(ocv2)) {
										rdfNew.add(ocv, OWL.sameAs, ocv2);
									}
								} else if(ocv != null && ocv2 != null && !ocv.equals(ocv2)) {
									rdfNew.add(ocv2, OWL.sameAs, ocv);
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
