package org.vcell.sybil.models.sbbox.imp;

/*   ProcessModelImp  --- by Oliver Ruebenacker, UCHC --- June to November 2009
 *   A view of a resource representing an SBPAX process model
 */

import org.vcell.sybil.models.sbbox.SBBox;
import com.hp.hpl.jena.rdf.model.Resource;

public class ProcessModelImp extends SBWrapper implements SBBox.NamedThing {

	public ProcessModelImp(SBBox box, Resource resource) { super(box, resource); }

}