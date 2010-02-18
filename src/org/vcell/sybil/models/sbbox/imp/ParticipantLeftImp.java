package org.vcell.sybil.models.sbbox.imp;

/*   ParticipantImp  --- by Oliver Ruebenacker, UCHC --- June 2009
 *   A view of a resource representing an SBPAX process participant
 */

import org.vcell.sybil.models.sbbox.SBBox;
import com.hp.hpl.jena.rdf.model.Resource;

public class ParticipantLeftImp extends ParticipantImp implements SBBox.MutableParticipantLeft {

	public ParticipantLeftImp(SBBox man, Resource resource) { super(man, resource); }

	public SBBox.MutableParticipantLeft setSpecies(SBBox.Species species) { 
		super.setSpecies(species);		
		return this;
	}

	public SBBox.MutableParticipantLeft setStoichiometry(SBBox.Stoichiometry stoichiometry) {
		super.setStoichiometry(stoichiometry);
		return this;
	}

}