/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.pathway.commons;

public class PathwayEntryObject {

	private String ID;
	private String name;
	
	public void setID(String iD) {
		ID = iD;
	}
	public String getID() {
		return ID;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}

	public final void show(StringBuffer sb) {
		sb.append(getPad(0) + toString() + "\n");
		showChildren(sb, 1);
	}

	public void showChildren(StringBuffer sb, int level) {
		printString(sb, "name", name, level);
	}
	
	public final String getPad(int level) {
		final String spaces = "                                                                                        ";
		if (level == 0) {
			return "";
		}
		if (level > 10){
			throw new RuntimeException("unchecked recursion in pathway.show()");
		}
		return spaces.substring(0, 3*level);
	}
	public void printString(StringBuffer sb, String name, String value, int level) {
		if (name != null && value != null){
			sb.append(getPad(level) + name + " = " + value + "\n");
		}
	}
	public String toString() {
		String suffix = "";
		if (getName().length() > 0){
			suffix = suffix + " : \"" + getName() + "\"";
		}
		if (ID != null){
			suffix = suffix + "  ID='" + ID+ "'";
		}
		if (suffix.length() > 0){
			suffix = suffix + " ";
		}
		return getTypeLabel() + suffix;
	}
	public String getTypeLabel() {
		String typeName = getClass().getName();
		typeName = typeName.replace(getClass().getPackage().getName(), "");
		typeName = typeName.replace(".", "");
		return typeName;
	}
}
