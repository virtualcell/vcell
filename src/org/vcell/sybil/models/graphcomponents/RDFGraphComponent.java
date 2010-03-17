package org.vcell.sybil.models.graphcomponents;

/*   SyCo  --- by Oliver Ruebenacker, UCHC --- August 2007 to November 2009
 *   A component of a graph, corresponding to a set of Resources and Statements
 */

import java.util.Set ;

import org.vcell.sybil.models.graphcomponents.tag.RDFGraphCompTag;
import org.vcell.sybil.models.sbbox.SBBox.NamedThing;

import com.hp.hpl.jena.rdf.model.Statement;

public interface RDFGraphComponent {
	
	public RDFGraphCompTag tag();
	public Set<RDFGraphComponent> dependencies();
	public Set<RDFGraphComponent> children();
	public Set<NamedThing> things();
	public Set<Statement> statements();	
	public String name();
	public String label();
}
