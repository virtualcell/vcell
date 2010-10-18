package org.vcell.sybil.models.sbbox.factories;

/*   ParticipanteftFactory  --- by Oliver Ruebenacker, UCHC --- June to December 2009
 *   A factory for left process participants
 */

import org.vcell.sybil.models.sbbox.SBBox;
import org.vcell.sybil.models.sbbox.imp.ParticipantLeftImp;
import org.vcell.sybil.rdf.schemas.SBPAX;

import com.hp.hpl.jena.rdf.model.Resource;

@SuppressWarnings("serial")
public class ParticipantLeftFactory extends ThingFactory<SBBox.MutableParticipantLeft> {

	public ParticipantLeftFactory(SBBox box) { super(box, SBPAX.ProcessParticipantLeft); }
	
	public SBBox.MutableParticipantLeft newThing(Resource node) { 
		return new ParticipantLeftImp(box, node); 
	}
	
	public String baseLabel() { return "ParticipantLeft"; }

}
