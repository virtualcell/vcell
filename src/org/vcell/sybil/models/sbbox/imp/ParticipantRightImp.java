package org.vcell.sybil.models.sbbox.imp;

/*   ParticipantImp  --- by Oliver Ruebenacker, UCHC --- June 2009
 *   A view of a resource representing an SBPAX process participant
 */

import org.vcell.sybil.models.sbbox.SBBox;
import com.hp.hpl.jena.rdf.model.Resource;

public class ParticipantRightImp extends ParticipantImp implements SBBox.MutableParticipantRight {

	public ParticipantRightImp(SBBox man, Resource resource) { super(man, resource); }

	public SBBox.MutableParticipantRight setSpecies(SBBox.Species species) { 
		super.setSpecies(species);		
		return this;
	}

	public SBBox.MutableParticipantRight setStoichiometry(SBBox.Stoichiometry stoichiometry) {
		super.setStoichiometry(stoichiometry);
		return this;
	}

}