package org.vcell.pathway;

import java.util.ArrayList;

public interface BiochemicalReaction extends Conversion {

	public ArrayList<DeltaG> getDeltaGs();

	public ArrayList<Double> getDeltaH();

	public ArrayList<Double> getDeltaS();

	public ArrayList<String> getECNumbers();

	public ArrayList<KPrime> getkEQs();
	
	public void setDeltaGs(ArrayList<DeltaG> deltaGs);
	
	public void setDeltaH(ArrayList<Double> deltaH);
	
	public void setDeltaS(ArrayList<Double> deltaS);
	
	public void setECNumbers(ArrayList<String> eCNumbers);
	
	public void setkEQs(ArrayList<KPrime> kEQs);

}
