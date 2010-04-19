package org.vcell.sybil.models.miriam.imp;

/*   RefGroupImp  --- by Oliver Ruebenacker, UCHC --- March 2010
 *   A group of MIRIAM references (data type plus value, e.g. UniProt P00533)
 */

import java.util.HashSet;
import java.util.Set;

import org.vcell.sybil.models.miriam.MIRIAMRef;
import org.vcell.sybil.models.miriam.RefGroup;
import org.vcell.sybil.models.miriam.MIRIAMRef.URNParseFailureException;
import org.vcell.sybil.rdf.RDFBagUtil;
import org.vcell.sybil.rdf.RDFBagWrapper;
import org.vcell.sybil.rdf.RDFBox;

import com.hp.hpl.jena.rdf.model.Bag;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public class RefGroupImp extends RDFBagWrapper implements RefGroup {

	public RefGroupImp(RDFBox box, Bag bag) { super(box, bag); }

	public RefGroupImp add(MIRIAMRef ref) {
		Resource rRef = box().getRdf().createResource(ref.urn());
		bag().add(rRef);
		return this;
	}

	public boolean contains(MIRIAMRef ref) {
		Resource rRef = box().getRdf().createResource(ref.urn());
		return bag().contains(rRef);
	}

	public RefGroupImp removeAll() {
		StmtIterator stmtIter = box().getRdf().listStatements(bag(), null, (RDFNode) null);
		Set<Statement> statements = new HashSet<Statement>();
		while(stmtIter.hasNext()) {
			Statement statement = stmtIter.nextStatement();
			if(RDFBagUtil.isRDFBagMemberProperty(statement.getPredicate())) { 
				statements.add(statement); 
			}
		}
		for(Statement statement : statements) { box().getRdf().remove(statement); }
		return this;
	}

	public Set<MIRIAMRef> refs() {
		StmtIterator stmtIter = box().getRdf().listStatements(bag(), null, (RDFNode) null);
		Set<MIRIAMRef> refs = new HashSet<MIRIAMRef>();
		while(stmtIter.hasNext()) {
			Statement statement = stmtIter.nextStatement();
			if(RDFBagUtil.isRDFBagMemberProperty(statement.getPredicate())) { 
				RDFNode object = statement.getObject();
				if(object instanceof Resource) {
					Resource resourceRef = (Resource) object;
					if(resourceRef.isURIResource()) {
						try { 
							refs.add(MIRIAMRef.createFromURN(resourceRef.getURI()));
						} catch (URNParseFailureException e) { e.printStackTrace(); }
					}
				}
			}
		}
		return refs;
	}

	public RefGroupImp remove(MIRIAMRef ref) {
		Resource resourceRef = box().getRdf().createResource(ref.urn());
		StmtIterator stmtIter = box().getRdf().listStatements(bag(), null, resourceRef);
		Set<Statement> statements = new HashSet<Statement>();
		while(stmtIter.hasNext()) { statements.add(stmtIter.nextStatement()); }
		for(Statement statement : statements) { box().getRdf().remove(statement); }
		return this;
	}

	public void delete() {
		box().getRdf().removeAll(bag(), null, (RDFNode) null);
		box().getRdf().removeAll(null, null, bag());
	}
	
}