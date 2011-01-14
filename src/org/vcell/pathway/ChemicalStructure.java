package org.vcell.pathway;

public class ChemicalStructure extends BioPaxObjectImpl implements UtilityClass {
	private String structureData;
	private String structureFormat;
	
	public String getStructureData() {
		return structureData;
	}
	public String getStructureFormat() {
		return structureFormat;
	}
	public void setStructureData(String structureData) {
		this.structureData = structureData;
	}
	public void setStructureFormat(String structureFormat) {
		this.structureFormat = structureFormat;
	}
	public void showChildren(StringBuffer sb, int level){
		super.showChildren(sb,level);
		printString(sb,"structureData",structureData,level);
		printString(sb,"structureFormat",structureFormat,level);
	}
	
}
