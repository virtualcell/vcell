package org.vcell.sybil.models.graphcomponents;

/*   SyCoStatement  --- by Oliver Ruebenacker, UCHC --- March 2008
 *   A component of a graph, corresponding to a single Statement
 */

import java.util.Set;

import org.vcell.sybil.models.graphcomponents.tag.RDFGraphCompTag;
import org.vcell.sybil.models.sbbox.SBBox;
import org.vcell.sybil.models.sbbox.SBBox.NamedThing;
import org.vcell.sybil.models.sbbox.imp.SBWrapper;
import org.vcell.sybil.util.sets.SetOfNone;
import org.vcell.sybil.util.sets.SetOfOne;
import org.vcell.sybil.util.sets.SetOfThree;
import org.vcell.sybil.util.sets.SetOfTwo;


import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;

public class RDFGraphCompSimpleRelation implements RDFGraphCompRelation {

	protected Statement statement = null;
	protected RDFGraphCompTag tag;
	protected Set<NamedThing> things;

	public RDFGraphCompSimpleRelation(SBBox box, Statement statement, RDFGraphCompTag tagNew) { 
		this.statement = statement; 
		tag = tagNew; 
		Resource subject = statement.getSubject();
		Property predicate = statement.getPredicate();
		RDFNode object = statement.getObject();
		if(object instanceof Resource) {
			things = new SetOfThree<NamedThing>(new SBWrapper(box, subject), new SBWrapper(box, predicate),
					new SBWrapper(box, (Resource) object));
		} else {
			things = new SetOfTwo<NamedThing>(new SBWrapper(box, subject), new SBWrapper(box, predicate));			
		}
	}

	public RDFGraphCompTag tag() { return tag; }
	public Set<RDFGraphComponent> dependencies() { return new SetOfNone<RDFGraphComponent>(); }
	public Set<RDFGraphComponent> children() { return new SetOfNone<RDFGraphComponent>(); }
	
	public Set<NamedThing> things() { return things; }
	
	public String name() { return toString(); }
	public Statement statement() { return statement; }
	public Set<Statement> statements() { return new SetOfOne<Statement>(statement); }

	@Override
	public boolean equals(Object object) {
		if(object instanceof RDFGraphCompSimpleRelation) {
			return ((RDFGraphCompSimpleRelation)object).statement().equals(this.statement());
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() { return statement().hashCode(); }

	public String label() { return statement().getPredicate().getLocalName(); }
	
}
