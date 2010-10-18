package org.vcell.sybil.models.sbbox.factories;

/*   USTAssumptionFactory  --- by Oliver Ruebenacker, UCHC --- June to December 2009
 *   A factory for unmodifiable substance class assumptions
 */

import org.vcell.sybil.models.sbbox.SBBox;
import org.vcell.sybil.models.sbbox.imp.USTAssumptionImp;
import org.vcell.sybil.rdf.reason.SYBREAMO;
import com.hp.hpl.jena.rdf.model.Resource;

@SuppressWarnings("serial")
public class USTAssumptionFactory 
extends ThingFactory<SBBox.MutableUSTAssumption> {

	public USTAssumptionFactory(SBBox box) { super(box, SYBREAMO.UnmodifiableSubstancesClass); }
	public SBBox.MutableUSTAssumption newThing(Resource node) { 
		return new USTAssumptionImp(box, node); 
	}
	public String baseLabel() { return "UnmodSubstanceTypeAssumption"; }

}
