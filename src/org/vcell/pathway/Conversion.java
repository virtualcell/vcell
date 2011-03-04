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
