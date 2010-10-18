package org.vcell.sybil.models.sbbox.factories;

/*   ProcessModelFactory  --- by Oliver Ruebenacker, UCHC --- June to December 2009
 *   A factory for process models
 */

import org .vcell.sybil.models.sbbox.SBBox;
import org.vcell.sybil.models.sbbox.imp.ProcessModelImp;
import org.vcell.sybil.rdf.schemas.SBPAX;

import com.hp.hpl.jena.rdf.model.Resource;

@SuppressWarnings("serial")
public class ProcessModelFactory extends ThingFactory<SBBox.MutableProcessModel> {

	public ProcessModelFactory(SBBox box) { super(box, SBPAX.ProcessModel); }
	public SBBox.MutableProcessModel newThing(Resource node) { return new ProcessModelImp(box, node); }

	public String baseLabel() { return "ProcessModel"; }
	
}
