package org.vcell.pathway;

import java.util.ArrayList;

public class BioSource implements UtilityClass {
	private CellVocabulary cellType;
	private ArrayList<String> name;
	private TissueVocabulary tissue;
	public CellVocabulary getCellType() {
		return cellType;
	}
	public ArrayList<String> getName() {
		return name;
	}
	public TissueVocabulary getTissue() {
		return tissue;
	}
	public void setCellType(CellVocabulary cellType) {
		this.cellType = cellType;
	}
	public void setName(ArrayList<String> name) {
		this.name = name;
	}
	public void setTissue(TissueVocabulary tissue) {
		this.tissue = tissue;
	}

}
