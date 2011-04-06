package org.vcell.sybil.rdf;

/*   RDFToken  --- by Oliver Ruebenacker, UCHC --- March 2009
 *   A token (subject, predicate of object) of a statement
 */

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;

public abstract class RDFToken {

	protected Statement statement;
	
	public RDFToken(Statement statementNew) { statement = statementNew; }
	
	public abstract RDFNode node();
	public Statement statement() { return statement; }
	
	public static abstract class ResourceToken extends RDFToken {
		public ResourceToken(Statement statementNew) { super(statementNew); }
		@Override
		public abstract Resource node();
	}
	
	public static class SubjectToken extends ResourceToken {
		public SubjectToken(Statement statementNew) { super(statementNew); }
		@Override
		public Resource node() { return statement.getSubject(); }

		@Override
		public boolean equals(Object o) {
			if(o instanceof SubjectToken) { return statement.equals(((SubjectToken) o).statement()); } 
			else { return false; }
		}
		
	}
	
	public static class PredicateToken extends ResourceToken {
		public PredicateToken(Statement statementNew) { super(statementNew); }
		@Override
		public Property node() { return statement.getPredicate(); }

		@Override
		public boolean equals(Object o) {
			if(o instanceof PredicateToken) { return statement.equals(((PredicateToken) o).statement()); } 
			else { return false; }
		}
		
	}
	
	public static class ObjectToken extends RDFToken {
		public ObjectToken(Statement statementNew) { super(statementNew); }
		@Override
		public RDFNode node() { return statement.getObject(); }
		@Override
		public boolean equals(Object o) {
			if(o instanceof ObjectToken) { return statement.equals(((ObjectToken) o).statement()); } 
			else { return false; }
		}		
	}
	
	@Override
	public int hashCode() { return statement.hashCode(); }
	
}
