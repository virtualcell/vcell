package org.vcell.pathway;

import java.util.ArrayList;

public interface Conversion extends Interaction {
	
	public String getConversionDirection();

	public ArrayList<Stoichiometry> getParticipantStoichiometry();

	public Boolean getSpontaneous();
	
	public void setConversionDirection(String conversionDirection);
	
	public void setParticipantStoichiometry(ArrayList<Stoichiometry> participantStoichiometry);
	
	public void setSpontaneous(Boolean spontaneous);
}
