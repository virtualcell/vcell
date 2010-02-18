package org.vcell.sybil.models.sbbox.imp;

/*   ParticipantCatalystImp  --- by Oliver Ruebenacker, UCHC --- June 2009
 *   A view of a resource representing an SBPAX catalyst process participant
 */

import org.vcell.sybil.models.sbbox.SBBox;
import com.hp.hpl.jena.rdf.model.Resource;

public class ParticipantCatalystImp extends ParticipantImp implements SBBox.MutableParticipantCatalyst {

	public ParticipantCatalystImp(SBBox man, Resource resource) { super(man, resource); }

	public SBBox.MutableParticipantCatalyst setSpecies(SBBox.Species species) { 
		super.setSpecies(species);		
		return this;
	}

	public SBBox.MutableParticipantCatalyst setStoichiometry(SBBox.Stoichiometry stoichiometry) {
		super.setStoichiometry(stoichiometry);
		return this;
	}

}