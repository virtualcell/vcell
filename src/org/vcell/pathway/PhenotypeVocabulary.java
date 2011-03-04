package org.vcell.pathway;

import org.vcell.pathway.persistence.BiopaxProxy.RdfObjectProxy;

public class PhenotypeVocabulary extends ControlledVocabulary {
	private String patoData;

	public String getPatoData() {
		return patoData;
	}

	public void setPatoData(String patoData) {
		this.patoData = patoData;
	}
	
	@Override
	public void replace(RdfObjectProxy objectProxy, BioPaxObject concreteObject){
		super.replace(objectProxy, concreteObject);
	}

	public void showChildren(StringBuffer sb, int level){
		super.showChildren(sb, level);
		printString(sb, "patoData",patoData,level);
	}

}
