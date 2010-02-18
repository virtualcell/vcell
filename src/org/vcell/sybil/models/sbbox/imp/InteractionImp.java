package org.vcell.sybil.models.sbbox.imp;

/*   InteractionImp  --- by Oliver Ruebenacker, UCHC --- June to November 2009
 *   A view of a resource representing an SBPAX Interaction
 */

import org.vcell.sybil.models.sbbox.SBBox;
import com.hp.hpl.jena.rdf.model.Resource;

public class InteractionImp extends SBWrapper implements SBBox.MutableInteraction {

	public InteractionImp(SBBox box, Resource resource) { super(box, resource); }

}