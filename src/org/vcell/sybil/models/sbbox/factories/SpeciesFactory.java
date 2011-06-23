package org.vcell.sybil.models.sbbox.factories;

/*   SpeciesFactory  --- by Oliver Ruebenacker, UCHC --- June to December 2009
 *   A factory for specieses
 */

import org.openrdf.model.Resource;
import org.vcell.sybil.models.sbbox.SBBox;
import org.vcell.sybil.models.sbbox.imp.SpeciesImp;

@SuppressWarnings("serial")
public class SpeciesFactory extends ThingFactory<SBBox.NamedThing> {

	public SpeciesFactory(SBBox box) { super(box); }
	@Override
	public SBBox.NamedThing newThing(Resource node) { return new SpeciesImp(box, node); }

}
