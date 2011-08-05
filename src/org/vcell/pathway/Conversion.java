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

import java.util.ArrayList;
import java.util.List;

public interface Conversion extends Interaction {
	
	public String getConversionDirection();
	public ArrayList<Stoichiometry> getParticipantStoichiometry();
	public Boolean getSpontaneous();
	public List<PhysicalEntity> getLeft();
	public List<PhysicalEntity> getRight();
	
	public void setConversionDirection(String conversionDirection);
	public void setParticipantStoichiometry(ArrayList<Stoichiometry> participantStoichiometry);
	public void setSpontaneous(Boolean spontaneous);
	public void addLeft(PhysicalEntity left);
	public void addRight(PhysicalEntity right);
}
