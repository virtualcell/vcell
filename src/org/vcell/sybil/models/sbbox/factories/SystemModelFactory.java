package org.vcell.sybil.models.sbbox.factories;

/*   SystemModelFactory  --- by Oliver Ruebenacker, UCHC --- June to December 2009
 *   A factory for system models
 */

import org.openrdf.model.Resource;
import org.vcell.sybil.models.sbbox.SBBox;
import org.vcell.sybil.models.sbbox.imp.SystemModelImp;

@SuppressWarnings("serial")
public class SystemModelFactory extends ThingFactory<SBBox.NamedThing> {

	public SystemModelFactory(SBBox box) { super(box); }
	@Override
	public SBBox.NamedThing newThing(Resource node) { return new SystemModelImp(box, node); }

}
