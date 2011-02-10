package org.vcell.pathway;

import java.util.ArrayList;

public interface Conversion extends Interaction {
	
	public String getConversionDirection();
	public ArrayList<Stoichiometry> getParticipantStoichiometry();
	public Boolean getSpontaneous();
	public ArrayList<PhysicalEntity> getLeft();
	public ArrayList<PhysicalEntity> getRight();
	
	public void setConversionDirection(String conversionDirection);
	public void setParticipantStoichiometry(ArrayList<Stoichiometry> participantStoichiometry);
	public void setSpontaneous(Boolean spontaneous);
	public void setLeft(ArrayList<PhysicalEntity> left);
	public void setRight(ArrayList<PhysicalEntity> right);
}
