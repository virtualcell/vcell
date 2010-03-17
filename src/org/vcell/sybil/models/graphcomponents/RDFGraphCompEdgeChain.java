package org.vcell.sybil.models.graphcomponents;

/*   SyCoExtendedEdge  --- by Oliver Ruebenacker, UCHC --- April 2008 to November 2009
 *   A simple implementation of an edge
 */

import java.util.HashSet;
import java.util.Set;

import org.vcell.sybil.models.graphcomponents.tag.RDFGraphCompTag;
import org.vcell.sybil.models.sbbox.SBBox.NamedThing;
import org.vcell.sybil.util.sets.SetOfThree;
import org.vcell.sybil.util.sets.SetOfTwo;

import com.hp.hpl.jena.rdf.model.Statement;

public class RDFGraphCompEdgeChain implements RDFGraphCompEdge {

	protected RDFGraphCompEdge edgeComp;
	protected RDFGraphCompEdge edgeMinorComp;
	protected RDFGraphCompTag tag;
	
	public RDFGraphCompEdgeChain(RDFGraphCompEdge edgeCompNew, RDFGraphCompEdge edgeMinorCompNew, RDFGraphCompTag tagNew) {
		edgeComp = edgeCompNew;
		edgeMinorComp = edgeMinorCompNew;
		tag = tagNew;
	}
	
	public RDFGraphCompRelation edgeComp() { return edgeComp; }
	public RDFGraphComponent startComp() { return edgeComp.startComp(); }
	public RDFGraphComponent endComp() { return edgeMinorComp.endComp(); }
	
	public Set<RDFGraphComponent> children() { 
		return new SetOfThree<RDFGraphComponent>(edgeComp, edgeMinorComp, edgeComp.endComp()); 
	}

	public Statement statement() { return edgeComp.statement(); }
	public String label() { return edgeComp.label(); }
	public String name() { return edgeComp.name(); }

	public Set<RDFGraphComponent> dependencies() { 
		return new SetOfTwo<RDFGraphComponent>(edgeComp.startComp(), edgeMinorComp.endComp()); 
	}

	public Set<NamedThing> things() {
		HashSet<NamedThing> things = new HashSet<NamedThing>(edgeComp.things());
		things.addAll(edgeMinorComp.things());
		return things;
	}
	
	public Set<Statement> statements() {
		Set<Statement> statements = new HashSet<Statement>(edgeComp.statements());
		statements.addAll(edgeMinorComp.statements());
		return statements;
	}

	public RDFGraphCompTag tag() { return tag; }

}
