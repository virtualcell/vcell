package org.vcell.sybil.models.sbbox.factories;

/*   SystemModelFactory  --- by Oliver Ruebenacker, UCHC --- June to December 2009
 *   A factory for system models
 */

import org.vcell.sybil.models.sbbox.SBBox;
import org.vcell.sybil.models.sbbox.imp.SystemModelImp;
import org.vcell.sybil.rdf.schemas.SBPAX;

import com.hp.hpl.jena.rdf.model.Resource;

@SuppressWarnings("serial")
public class SystemModelFactory extends ThingFactory<SBBox.MutableSystemModel> {

	public SystemModelFactory(SBBox box) { super(box, SBPAX.SystemModel); }
	public SBBox.MutableSystemModel newThing(Resource node) { return new SystemModelImp(box, node); }
	public String baseLabel() { return "SystemModel"; }

}
