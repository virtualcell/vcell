/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */


package cbit.vcell.units;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import cbit.vcell.units.parser.UnitSymbol;
import ucar.units_vcell.Unit;

public abstract class VCUnitSystem implements Serializable {

	// define the 'TBD' and 'DIMENSIONLESS' units 
	private final static String UNIT_TBD = InternalUnitDefinition.TBD_SYMBOL;
	private final static String UNIT_DIMENSIONLESS = "1";
		
	private HashMap<String, VCUnitDefinition> vcUnitDefinitionsHash = new HashMap<String, VCUnitDefinition>();

	public VCUnitDefinition getInstance(String symbol) {
		if (symbol.equals("")) {
			return getInstance_TBD();
		}
		VCUnitDefinition vcUnitDefinition = vcUnitDefinitionsHash.get(symbol);
		if (vcUnitDefinition != null) {
			return vcUnitDefinition;
		}
		
		vcUnitDefinition = new VCUnitDefinition(InternalUnitDefinition.getInstance(symbol), this, new UnitSymbol(symbol));
		vcUnitDefinitionsHash.put(symbol, vcUnitDefinition);
		return vcUnitDefinition;
	}
	
	public VCUnitDefinition getInstance(Unit unit) {
		return null;
	}

	public Iterator<VCUnitDefinition> getKnownUnits() {
		Collection<VCUnitDefinition> v = vcUnitDefinitionsHash.values();
		return v.iterator();
	}
	
	public VCUnitDefinition getInstance_TBD() {
		return getInstance(UNIT_TBD);
	}
	
	public VCUnitDefinition getInstance_DIMENSIONLESS() {
		return getInstance(UNIT_DIMENSIONLESS);
	}
	
}