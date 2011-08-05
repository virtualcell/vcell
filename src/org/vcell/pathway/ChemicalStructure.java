/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

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
