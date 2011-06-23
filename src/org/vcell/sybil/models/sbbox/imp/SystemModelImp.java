package org.vcell.sybil.models.sbbox.imp;

/*   SystemModelImp  --- by Oliver Ruebenacker, UCHC --- June to November 2009
 *   A view of a resource representing an SBPAX SystemModel
 */

import org.openrdf.model.Resource;
import org.vcell.sybil.models.sbbox.SBBox;

public class SystemModelImp extends SBWrapper implements SBBox.NamedThing {

	public SystemModelImp(SBBox box, Resource resource) { super(box, resource); }

}