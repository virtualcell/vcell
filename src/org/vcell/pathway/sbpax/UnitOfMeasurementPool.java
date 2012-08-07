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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class UnitOfMeasurementPool {

	public static final String BASE_URI = "http://www.sbpax.org/uome/list.owl#";
	public static final String BASE_URI_OLD = "http://vcell.org/uome/list#";
	
	protected static Map<String, UnitOfMeasurement> unitsById = new HashMap<String, UnitOfMeasurement>();
	protected static Map<String, UnitOfMeasurement> unitsBySymbol = new HashMap<String, UnitOfMeasurement>();
	
	protected static void addUnit(UnitOfMeasurement unit) {
		unitsById.put(unit.getID(), unit);
		for(String symbol : unit.getSymbols()) {
			if(!unitsBySymbol.containsKey(symbol)) {
				unitsBySymbol.put(symbol, unit);
			}
		}
	}
		
	public static boolean hasUnitWithId(String id) {
		if(unitsById.containsKey(id)) { return true; }
		id = id.replace(BASE_URI_OLD, BASE_URI);
		if(unitsById.containsKey(id)) { return true; }
		return false;
	}
	
	public static UnitOfMeasurement getUnitById(String id) {
		if(id.startsWith(BASE_URI_OLD)) {
			id = id.replace(BASE_URI_OLD, BASE_URI);
		}
		UnitOfMeasurement unit = unitsById.get(id);
		if(unit == null) {
			unit = new UnitOfMeasurement();
			unit.setID(id);
			addUnit(unit);
		}
		return unit;
	}
	
	public static boolean hasUnitWithSymbol(String symbol) {
		return unitsBySymbol.containsKey(symbol);
	}
	
	public static UnitOfMeasurement getUnitBySymbol(String symbol) {
		UnitOfMeasurement unit = unitsBySymbol.get(symbol);
		if(unit == null) {
			unit = new UnitOfMeasurement();
			unit.getSymbols().add(symbol);
			addUnit(unit);
		}
		return unit;
	}
	
	public static UnitOfMeasurement getUnitByIdOrSymbol(String id, Collection<String> symbols) {
		UnitOfMeasurement unit = null;
		if(id != null && hasUnitWithId(id)) {
			unit = getUnitById(id);
		} else {
			for(String symbol : symbols) {
				if(hasUnitWithSymbol(symbol)) {
					unit = getUnitBySymbol(symbol);
					break;
				}
			}
		}
		if(unit == null) {
			if(id != null) {
				if(symbols.isEmpty()) {
					unit = getUnitById(id);
				} else {
					unit = addUnit(id, symbols.iterator().next());
				}
			} else {
				unit = new UnitOfMeasurement();
			}
			addUnit(unit);
		}
		for(String symbol : symbols) {
			if(!unit.getSymbols().contains(symbol)) {
				unit.getSymbols().add(symbol);
			}
		}
		return unit;
	}
	
	public static UnitOfMeasurement addUnit(String id, String symbol) {
		UnitOfMeasurement unit = getUnitById(id);
		unit.getSymbols().add(symbol);
		if(!unitsBySymbol.containsKey(symbol)) {
			unitsBySymbol.put(symbol, unit);
		}
		return unit;
	}

	public static final String MU = "\u03bc";
	
//	public static final UnitOfMeasurement MICROMOLAR = addUnit(BASE_URI + "Micromolar", MU + "M");
//	public static final UnitOfMeasurement MOLAR = addUnit(BASE_URI + "Molar", "M");
	public static final UnitOfMeasurement MOLE_PER_SECOND = addUnit(BASE_URI + "MolePerSecond", "mol/s");
	public static final UnitOfMeasurement PH = addUnit(BASE_URI + "PH", "pH");
	public static final UnitOfMeasurement PER_MOLAR_SECOND = addUnit(BASE_URI + "PerMolarSecond", "M^(-1)*s^(-1)");
//	public static final UnitOfMeasurement PER_SECOND = addUnit(BASE_URI + "PerSecond", "s^(-1)");
//	public static final UnitOfMeasurement SECOND = addUnit(BASE_URI + "Second", "s");
	
}
