package org.vcell.sybil.models.sbbox.factories;

/*   ParticipantFactory  --- by Oliver Ruebenacker, UCHC --- June to December 2009
 *   A factory for process participants
 */

import org.vcell.sybil.models.sbbox.SBBox;
import org.vcell.sybil.models.sbbox.imp.ParticipantImp;
import org.vcell.sybil.rdf.schemas.SBPAX;

import com.hp.hpl.jena.rdf.model.Resource;

@SuppressWarnings("serial")
public class ParticipantFactory extends ThingFactory<SBBox.MutableParticipant> {

	public ParticipantFactory(SBBox box) { super(box, SBPAX.ProcessParticipant); }
	public SBBox.MutableParticipant newThing(Resource node) { return new ParticipantImp(box, node); }
	public String baseLabel() { return "Participant"; }

}
