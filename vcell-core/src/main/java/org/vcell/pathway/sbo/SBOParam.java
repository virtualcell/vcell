/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.pathway.sbo;

public class SBOParam {

	protected final String index;
	protected final String name;
	protected int role;
	protected boolean hasNumber;
	protected double number;
	protected boolean hasUnit;
	protected String unit;
			
	public int hashCode() { return index.hashCode(); }
	public boolean equals(Object obj) {
		if(obj instanceof SBOParam) { 
			return getIndex().equals(((SBOParam) obj).getIndex()); 
		}
		return false;
	}
	public SBOParam(String index, String name, int role) { 
		this.index = index; 
		this.name = name; 
		this.role = role; 
		this.hasNumber = false;
		this.hasUnit = false;
	}
	public SBOParam(SBOParam other) {
		this.index = other.index;
		this.name = other.name;
		this.role = other.role;
		this.hasNumber = other.hasNumber;
		this.number = other.number;
		this.hasUnit = other.hasUnit;
		this.unit = other.unit;
	}
	
	public void setNumber(double number) {
		this.hasNumber = true;
		this.number = number;
	}
	public boolean setUnit(String unit) {
		if(unit != null && !unit.isEmpty()) {
			this.hasUnit = true;
			this.unit = unit;
			return true;
		}
		return false;
	}
	
	public String getIndex() { return index; }
	public String getName() { return name; }
	public int getRole() { return role; }
	public double getNumber() { return number; }
	public String getUnit() { return unit; }
	
	public boolean hasNumber() { return hasNumber; }
	public boolean hasUnit() { return hasUnit; }

	// make unit string compatible with vCell conventions
	static public String formatUnit(String unit) {
		String formattedUnit = new String(unit);
		formattedUnit = formattedUnit.replace("(", "");
		formattedUnit = formattedUnit.replace(")", "");
		return formattedUnit;
	}
}
