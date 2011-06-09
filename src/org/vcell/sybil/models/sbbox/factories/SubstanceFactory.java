package org.vcell.sybil.models.sbbox.factories;

/*   LocationFactory  --- by Oliver Ruebenacker, UCHC --- June to November 2009
 *   A factory for locations
 */

import org.vcell.sybil.models.sbbox.SBBox;
import org.vcell.sybil.models.sbbox.imp.SubstanceImp;
import com.hp.hpl.jena.rdf.model.Resource;

@SuppressWarnings("serial")
public class SubstanceFactory extends ThingFactory<SBBox.NamedThing> {

	public SubstanceFactory(SBBox box) { super(box); }
	@Override
	public SBBox.NamedThing newThing(Resource node) { return new SubstanceImp(box, node); }

}
