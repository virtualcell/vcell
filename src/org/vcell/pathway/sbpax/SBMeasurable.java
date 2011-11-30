/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.pathway.sbpax;

import java.util.ArrayList;

import org.vcell.pathway.BioPaxObject;
import org.vcell.pathway.persistence.BiopaxProxy.RdfObjectProxy;

public class SBMeasurable extends SBEntityImpl {

	private ArrayList<Double> number = new ArrayList<Double>();
	private ArrayList<UnitOfMeasurement> unit = new ArrayList<UnitOfMeasurement>();

	public ArrayList<Double> getNumber() {
		return number;
	}
	public ArrayList<UnitOfMeasurement> getUnit() {
		return unit;
	}

	public void setNumber(ArrayList<Double> number) {
		this.number = number;
	}
	
	public void setUnit(ArrayList<UnitOfMeasurement> unit) {
		this.unit = unit;
	}

	@Override
	public void replace(RdfObjectProxy objectProxy, BioPaxObject concreteObject){
		super.replace(objectProxy, concreteObject);
		
		for (int i=0; i<unit.size(); i++) {
			UnitOfMeasurement thing = unit.get(i);
			if(thing == objectProxy) {
				unit.set(i, (UnitOfMeasurement)concreteObject);
			}
		}
	}
	
	public void showChildren(StringBuffer sb, int level){
		super.showChildren(sb, level);
		printDoubles(sb,"number", number, level);
		printObjects(sb,"UnitOfMeasurement", unit, level);
	}
}
