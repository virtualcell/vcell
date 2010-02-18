package org.vcell.sybil.models.sbbox.factories;

/*   ProcessFactory  --- by Oliver Ruebenacker, UCHC --- June to December 2009
 *   A factory for processes
 */

import org.vcell.sybil.models.sbbox.SBBox;
import org.vcell.sybil.models.sbbox.imp.ProcessImp;
import org.vcell.sybil.rdf.schemas.SBPAX;

import com.hp.hpl.jena.rdf.model.Resource;

public class ProcessFactory extends ThingFactory<SBBox.MutableProcess> {

	public ProcessFactory(SBBox box) { super(box, SBPAX.Process); }
	public SBBox.MutableProcess newThing(Resource node) { return new ProcessImp(box, node); }
	public String baseLabel() { return "Process"; }

}
