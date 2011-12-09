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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.sbpax.schemas.UOMECore;
import org.sbpax.schemas.UOMEList;

public class UnitOfMeasurementFactory {

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
	
	public static UnitOfMeasurement getUnitById(String id) {
		UnitOfMeasurement unit = unitsById.get(id);
		if(unit == null) {
			unit = new UnitOfMeasurement();
			unit.setID(id);
			addUnit(unit);
		}
		return unit;
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
	
	public static void addUnit(String localName, String symbol) {
		String id = BASE_URI + localName;
		UnitOfMeasurement unit = getUnitById(id);
		String oldID = BASE_URI_OLD + localName;
		unitsById.put(oldID, unit);
		unit.getSymbols().add(symbol);
	}

	public static void addUnit(URI uri) {
		String id = uri.getLocalName();
		Iterator<Statement> iter = UOMEList.schema.match(uri, UOMECore.unitSymbol, null);
		if(iter.hasNext()) { 
			// TODO
			iter.next().getObject();
		}
	}
	
}
