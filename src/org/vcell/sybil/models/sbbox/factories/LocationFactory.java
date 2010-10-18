package org.vcell.sybil.models.sbbox.factories;

/*   LocationFactory  --- by Oliver Ruebenacker, UCHC --- June to December 2009
 *   A factory for locations
 */

import org.vcell.sybil.models.sbbox.SBBox;
import org.vcell.sybil.models.sbbox.SBBox.MutableLocation;
import org.vcell.sybil.models.sbbox.imp.LocationImp;
import org.vcell.sybil.rdf.schemas.SBPAX;

import com.hp.hpl.jena.rdf.model.Resource;

@SuppressWarnings("serial")
public class LocationFactory extends ThingFactory<SBBox.MutableLocation> {

	public LocationFactory(SBBox box) { super(box, SBPAX.Location); }
	public MutableLocation newThing(Resource node) { return new LocationImp(box, node); }
	public String baseLabel() { return "Location"; }

}
