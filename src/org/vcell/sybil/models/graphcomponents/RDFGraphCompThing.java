package org.vcell.sybil.models.graphcomponents;

/*   SyCoResourceLike  --- by Oliver Ruebenacker, UCHC --- December 2007 to November 2009
 *   A component of a graph, corresponding to a single RDFNode
 */

import org.vcell.sybil.models.sbbox.SBBox.NamedThing;

import com.hp.hpl.jena.rdf.model.Resource;

public interface RDFGraphCompThing extends RDFGraphComponent {

	public NamedThing thing();
	public Resource type();
}
