package org.vcell.pathway;

public class PhenotypeVocabulary extends ControlledVocabulary {
	private String patoData;

	public String getPatoData() {
		return patoData;
	}

	public void setPatoData(String patoData) {
		this.patoData = patoData;
	}
	
	public void showChildren(StringBuffer sb, int level){
		super.showChildren(sb, level);
		printString(sb, "patoData",patoData,level);
	}

}
