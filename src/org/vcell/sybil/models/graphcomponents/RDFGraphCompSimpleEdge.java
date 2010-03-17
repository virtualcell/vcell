package org.vcell.sybil.models.graphcomponents;

/*   SyCoSimpleEdge  --- by Oliver Ruebenacker, UCHC --- March 2008 to November 2009
 *   A simple implementation of an edge
 */

import java.util.Set;

import org.vcell.sybil.models.graphcomponents.tag.RDFGraphCompTag;
import org.vcell.sybil.models.sbbox.SBBox.NamedThing;
import org.vcell.sybil.util.sets.SetOfOne;
import org.vcell.sybil.util.sets.SetOfTwo;


import com.hp.hpl.jena.rdf.model.Statement;

public class RDFGraphCompSimpleEdge implements RDFGraphCompEdge {

	protected RDFGraphComponent startComp;
	protected RDFGraphComponent endComp;
	protected RDFGraphCompRelation edgeComp;
	protected RDFGraphCompTag tag;

	public RDFGraphCompSimpleEdge(RDFGraphComponent newNode1Comp, RDFGraphComponent newNode2Comp, RDFGraphCompRelation newEdgeComp, 
			RDFGraphCompTag tagNew) {
		startComp = newNode1Comp; 
		endComp = newNode2Comp; 
		edgeComp = newEdgeComp;
		tag = tagNew;
	}

	public RDFGraphCompTag tag() { return tag; }
	public Set<RDFGraphComponent> dependencies() { return new SetOfTwo<RDFGraphComponent>(startComp, endComp); }
	public RDFGraphComponent startComp() { return startComp; }
	public RDFGraphComponent endComp() { return endComp; }
	public RDFGraphCompRelation edgeComp() { return edgeComp; }
	public Set<RDFGraphComponent> children() { return new SetOfOne<RDFGraphComponent>(edgeComp); };
	public Set<NamedThing> things() { return edgeComp.things(); }
	public Statement statement() { return edgeComp.statement(); }
	public Set<Statement> statements() { return edgeComp.statements(); 	};

	public boolean equals(Object object) {
		if(object instanceof RDFGraphCompSimpleEdge) {
			RDFGraphCompSimpleEdge edge = (RDFGraphCompSimpleEdge) object;
			return edge.edgeComp().equals(edgeComp);
		}
		return false;
	}
	
	public int hashCode() { return edgeComp.hashCode(); }
	public String label() { return edgeComp.label(); }
	public String name() { return toString(); }

}
