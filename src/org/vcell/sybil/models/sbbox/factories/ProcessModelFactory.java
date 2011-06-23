package org.vcell.sybil.models.sbbox.factories;

/*   ProcessModelFactory  --- by Oliver Ruebenacker, UCHC --- June to December 2009
 *   A factory for process models
 */

import org.openrdf.model.Resource;
import org .vcell.sybil.models.sbbox.SBBox;
import org.vcell.sybil.models.sbbox.imp.ProcessModelImp;

@SuppressWarnings("serial")
public class ProcessModelFactory extends ThingFactory<SBBox.NamedThing> {

	public ProcessModelFactory(SBBox box) { super(box); }
	@Override
	public SBBox.NamedThing newThing(Resource node) { return new ProcessModelImp(box, node); }

}
