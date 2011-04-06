package org.vcell.sybil.models.sbbox.factories;

/*   UnitFactory  --- by Oliver Ruebenacker, UCHC --- June to December 2009
 *   A factory for units
 */

import org.vcell.sybil.models.sbbox.SBBox;
import org.vcell.sybil.models.sbbox.imp.UnitImp;
import org.vcell.sybil.rdf.schemas.SBPAX;

import com.hp.hpl.jena.rdf.model.Resource;

@SuppressWarnings("serial")
public class UnitFactory extends ThingFactory<SBBox.Unit> {

	public UnitFactory(SBBox box) { super(box, SBPAX.Unit); }
	@Override
	public SBBox.Unit newThing(Resource node) { return new UnitImp(box, node); }
	@Override
	public String baseLabel() { return "Unit"; }

}
