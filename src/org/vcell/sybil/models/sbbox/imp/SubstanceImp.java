package org.vcell.sybil.models.sbbox.imp;

/*   SubstanceImp  --- by Oliver Ruebenacker, UCHC --- June to November 2009
 *   A view of a resource representing an SBPAX Substance
 */

import org.openrdf.model.Resource;
import org.vcell.sybil.models.sbbox.SBBox;

public class SubstanceImp extends SBWrapper implements SBBox.NamedThing {

	public SubstanceImp(SBBox man, Resource resource) { super(man, resource); }

}