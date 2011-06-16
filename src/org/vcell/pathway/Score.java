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

public class Score extends BioPaxObjectImpl implements UtilityClass {
	private Provenance scoreSource;
	private String value;
	
	public Provenance getScoreSource() {
		return scoreSource;
	}
	public String getValue() {
		return value;
	}
	public void setScoreSource(Provenance scoreSource) {
		this.scoreSource = scoreSource;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	public void showChildren(StringBuffer sb, int level){
		super.showChildren(sb, level);
		printObject(sb, "scoreSource",scoreSource,level);
		printString(sb, "value",value,level);
	}

}
