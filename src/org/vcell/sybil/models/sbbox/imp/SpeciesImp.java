package org.vcell.sybil.models.sbbox.imp;

/*   SpeciesImp  --- by Oliver Ruebenacker, UCHC --- June to November 2009
 *   A view of a resource representing an SBPAX Species
 */

import org.openrdf.model.Resource;
import org.vcell.sybil.models.sbbox.SBBox;

public class SpeciesImp extends SBWrapper implements SBBox.NamedThing {

	public SpeciesImp(SBBox man, Resource resource) { super(man, resource); }

}