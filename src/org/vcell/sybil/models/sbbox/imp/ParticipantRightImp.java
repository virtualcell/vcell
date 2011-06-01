/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

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
