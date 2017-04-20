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
import java.util.HashMap;
import java.util.HashSet;

import org.vcell.pathway.BioPaxObject;
import org.vcell.pathway.id.URIUtil;
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
	
	public String extractSBOTermAsString() {	// returns a sting like SBO:0000xxx
		if(getSBTerm().isEmpty()) {
			return new String("");		// more fault-tolerant than returning null
		}
		String sboTermName = getSBTerm().get(0).toString();
		sboTermName = sboTermName.substring(sboTermName.lastIndexOf('#'));
		sboTermName = sboTermName.replace('#', ' ');
		sboTermName = sboTermName.replace('\'', ' ');
		sboTermName = sboTermName.replace("(proxy)", "");
		sboTermName = sboTermName.replace(" ", "");
		return sboTermName;
	}
	public String extractSBOUnitAsString() {
		String sboUnit = new String("");
		if(!hasUnit()) {
			return sboUnit;
		}
		UnitOfMeasurement u = getUnit().get(0);
		if(u.getSymbols().isEmpty()) {
			sboUnit += u.getIDShort();
		} else {
			sboUnit += u.getSymbols().get(0);
		}
		return sboUnit;
	}
	
	public boolean hasTerm() {
		return !getSBTerm().isEmpty();
	}
	public boolean hasNumber() {
		return !number.isEmpty();
	}
	public boolean hasUnit() {
		return !unit.isEmpty();
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
	
	public void replace(HashMap<String, BioPaxObject> resourceMap, HashSet<BioPaxObject> replacedBPObjects) {
		super.replace(resourceMap, replacedBPObjects);
		for(int i = 0; i < unit.size(); ++i) {
			UnitOfMeasurement aUnit = unit.get(i);
			if(!URIUtil.isAbsoluteURI(aUnit.getID())) {
				for(String symbol : aUnit.getSymbols()) {
					UnitOfMeasurement aUnit2 = UnitOfMeasurementPool.getUnitBySymbol(symbol);
					if(URIUtil.isAbsoluteURI(aUnit2.getID())) {
						unit.set(i, aUnit2);
						break;
					}
				}
			}
		}
		
	}
	
	public void showChildren(StringBuffer sb, int level){
		super.showChildren(sb, level);
		printDoubles(sb,"number", number, level);
		printObjects(sb,"UnitOfMeasurement", unit, level);
	}
}
