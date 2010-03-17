package org.vcell.sybil.models.graphcomponents;

/*   SyCoSimpleContainer  --- by Oliver Ruebenacker, UCHC --- March 2008 to November 2009
 *   A simple implementation of a mutable component of a graph, corresponding to a set of RDFNodes 
 *   and Statements
 */

import java.util.HashSet;
import java.util.Set;

import org.vcell.sybil.models.graphcomponents.tag.RDFGraphCompTag;
import org.vcell.sybil.models.sbbox.SBBox.NamedThing;
import org.vcell.sybil.util.sets.SetOfNone;


import com.hp.hpl.jena.rdf.model.Statement;

public class RDFGraphCompSimpleContainer implements RDFGraphCompContainer {

	protected Set<RDFGraphComponent> children;
	protected RDFGraphCompTag tag;
	
	public RDFGraphCompSimpleContainer(RDFGraphCompTag tagNew) {
		children = new HashSet<RDFGraphComponent>();
		tag = tagNew; 
	}

	public RDFGraphCompTag tag() { return tag; }
	public Set<RDFGraphComponent> children() { return children; };
	
	public Set<NamedThing> things() {
		HashSet<NamedThing> things = new HashSet<NamedThing>();
		for(RDFGraphComponent child : children) { things.addAll(child.things()); }
		return things;
	}
	
	public Set<Statement> statements() {
		HashSet<Statement> statements = new HashSet<Statement>();
		for(RDFGraphComponent child : children) { statements.addAll(child.statements()); }
		return statements; 		
	}

	public Set<RDFGraphComponent> dependencies() { return new SetOfNone<RDFGraphComponent>(); }
	public String name() { return toString(); }
	public String label() { return ""; }

}
