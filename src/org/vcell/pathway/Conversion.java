package org.vcell.pathway;

import java.util.ArrayList;

public interface Conversion extends Interaction {
	
	public String getConversionDirection();

	public ArrayList<PhysicalEntity> getLeftSide();

	public ArrayList<Stoichiometry> getParticipantStoichiometry();

	public ArrayList<PhysicalEntity> getRightSide();

	public Boolean getSpontaneous();
	
	public void setConversionDirection(String conversionDirection);
	
	public void setLeftSide(ArrayList<PhysicalEntity> leftSide);
	
	public void setParticipantStoichiometry(ArrayList<Stoichiometry> participantStoichiometry);
	
	public void setRightSide(ArrayList<PhysicalEntity> rightSide);
	
	public void setSpontaneous(Boolean spontaneous);
}
