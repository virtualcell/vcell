package org.vcell.sybil.models.sbbox.factories;

/*   LocationFactory  --- by Oliver Ruebenacker, UCHC --- June to November 2009
 *   A factory for locations
 */

import org.vcell.sybil.models.sbbox.SBBox;
import org.vcell.sybil.models.sbbox.imp.SubstanceImp;
import org.vcell.sybil.rdf.schemas.SBPAX;

import com.hp.hpl.jena.rdf.model.Resource;

public class SubstanceFactory extends ThingFactory<SBBox.MutableSubstance> {

	public SubstanceFactory(SBBox box) { super(box, SBPAX.Substance); }
	public SBBox.MutableSubstance newThing(Resource node) { return new SubstanceImp(box, node); }
	public String baseLabel() { return "Substance"; }

}
