package org.vcell.sybil.models.sbbox.imp;

/*   LocationImp  --- by Oliver Ruebenacker, UCHC --- June 2009 to April 2010
 *   A view of a resource representing an SBPAX Location
 */

import org.vcell.sybil.models.sbbox.SBBox;
import com.hp.hpl.jena.rdf.model.Resource;

public class LocationImp extends SBWrapper implements SBBox.NamedThing {

	public LocationImp(SBBox box, Resource resource) { super(box, resource); }

}