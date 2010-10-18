package org.vcell.sybil.models.sbbox.factories;

/*   ParticipantRightFactory  --- by Oliver Ruebenacker, UCHC --- June to December 2009
 *   A factory for right process participants
 */

import org.vcell.sybil.models.sbbox.SBBox;
import org.vcell.sybil.models.sbbox.imp.ParticipantRightImp;
import org.vcell.sybil.rdf.schemas.SBPAX;

import com.hp.hpl.jena.rdf.model.Resource;

@SuppressWarnings("serial")
public class ParticipantRightFactory extends ThingFactory<SBBox.MutableParticipantRight> {

	public ParticipantRightFactory(SBBox box) { super(box, SBPAX.ProcessParticipantRight); }
	
	public SBBox.MutableParticipantRight newThing(Resource node) { 
		return new ParticipantRightImp(box, node); 
	}
	
	public String baseLabel() { return "ParticipantRight"; }

}
