package org.vcell.pathway;

import java.util.ArrayList;

public class BiochemicalReactionImpl extends ConversionImpl implements BiochemicalReaction {

	private ArrayList<DeltaG> deltaGs;

	private ArrayList<Double> deltaH;

	private ArrayList<Double> deltaS;

	private ArrayList<String> ECNumbers;

	private ArrayList<KPrime> kEQs;

	public BiochemicalReactionImpl(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	public ArrayList<DeltaG> getDeltaGs() {
		return deltaGs;
	}

	public ArrayList<Double> getDeltaH() {
		return deltaH;
	}

	public ArrayList<Double> getDeltaS() {
		return deltaS;
	}

	public ArrayList<String> getECNumbers() {
		return ECNumbers;
	}

	public ArrayList<KPrime> getkEQs() {
		return kEQs;
	}
	public void setDeltaGs(ArrayList<DeltaG> deltaGs) {
		this.deltaGs = deltaGs;
	}
	public void setDeltaH(ArrayList<Double> deltaH) {
		this.deltaH = deltaH;
	}
	public void setDeltaS(ArrayList<Double> deltaS) {
		this.deltaS = deltaS;
	}
	public void setECNumbers(ArrayList<String> eCNumbers) {
		ECNumbers = eCNumbers;
	}
	
	public void setkEQs(ArrayList<KPrime> kEQs) {
		this.kEQs = kEQs;
	}

}
