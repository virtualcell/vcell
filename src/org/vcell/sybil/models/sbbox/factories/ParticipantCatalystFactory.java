package org.vcell.sybil.models.sbbox.factories;

/*   ParticipantCatalystFactory  --- by Oliver Ruebenacker, UCHC --- June to December 2009
 *   A factory for catalyst process participants
 */

import org.vcell.sybil.models.sbbox.SBBox;
import org.vcell.sybil.models.sbbox.imp.ParticipantCatalystImp;
import org.vcell.sybil.rdf.schemas.SBPAX;

import com.hp.hpl.jena.rdf.model.Resource;

@SuppressWarnings("serial")
public class ParticipantCatalystFactory extends ThingFactory<SBBox.MutableParticipantCatalyst> {

	public ParticipantCatalystFactory(SBBox box) { super(box, SBPAX.ProcessParticipantCatalyst); }
	
	public SBBox.MutableParticipantCatalyst newThing(Resource node) { 
		return new ParticipantCatalystImp(box, node); 
	}
	
	public String baseLabel() { return "ParticipantCatalyst"; }

}
