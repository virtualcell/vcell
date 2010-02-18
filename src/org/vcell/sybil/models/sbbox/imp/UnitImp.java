package org.vcell.sybil.models.sbbox.imp;

/*   UnitImp  --- by Oliver Ruebenacker, UCHC --- June 2009
 *   A view of a resource representing an SBPAX Unit
 */

import org.vcell.sybil.models.sbbox.SBBox;
import com.hp.hpl.jena.rdf.model.Resource;

public class UnitImp extends SBWrapper implements SBBox.Unit {
	
	public UnitImp(SBBox box, Resource resource) { super(box, resource); }
	
}