package org.vcell.sybil.models.sbbox.factories;

/*   LocationFactory  --- by Oliver Ruebenacker, UCHC --- June to December 2009
 *   A factory for locations
 */

import org.vcell.sybil.models.sbbox.SBBox;
import org.vcell.sybil.models.sbbox.imp.LocationImp;
import com.hp.hpl.jena.rdf.model.Resource;

@SuppressWarnings("serial")
public class LocationFactory extends ThingFactory<SBBox.NamedThing> {

	public LocationFactory(SBBox box) { super(box); }
	@Override
	public SBBox.NamedThing newThing(Resource node) { return new LocationImp(box, node); }

}
