/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.pathway;

import java.util.List;

import org.vcell.pathway.InteractionParticipant.Type;
import org.vcell.util.Displayable;

public interface Interaction extends Entity, Displayable {
	
	public List<InteractionVocabulary> getInteractionTypes();
	public List<InteractionParticipant> getParticipants();
	public List<InteractionParticipant> getParticipants(InteractionParticipant.Type type);
	
	public void setInteractionTypes(List<InteractionVocabulary> interactionTypes);
	public void setParticipants(List<InteractionParticipant> participants);
	public void addParticipant(InteractionParticipant participant);
	
	public void addPhysicalEntityAsParticipant(PhysicalEntity entity, Type type);
}
