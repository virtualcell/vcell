package org.vcell.sybil.models.sbbox.factories;

/*   InteractionFactory  --- by Oliver Ruebenacker, UCHC --- June to December 2009
 *   A factory for interactions
 */

import org.vcell.sybil.models.sbbox.SBBox;
import org.vcell.sybil.models.sbbox.imp.InteractionImp;
import org.vcell.sybil.rdf.schemas.SBPAX;

import com.hp.hpl.jena.rdf.model.Resource;

@SuppressWarnings("serial")
public class InteractionFactory extends ThingFactory<SBBox.MutableInteraction> {

	public InteractionFactory(SBBox box) { super(box, SBPAX.Interaction); }
	public SBBox.MutableInteraction newThing(Resource node) { return new InteractionImp(box, node); }
	public String baseLabel() { return "Interaction"; }

}
