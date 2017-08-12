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
import java.util.Map;

public class UOMEToVCUnitMapper {

	protected static Map<UnitOfMeasurement, String> uome2vcell = 
			new HashMap<UnitOfMeasurement, String>();
	protected static Map<String, UnitOfMeasurement> vcell2uome = 
			new HashMap<String, UnitOfMeasurement>();
	
	public static String getVCUnitSymbol(UnitOfMeasurement uomeUnit) {
		return uome2vcell.get(uomeUnit);
	}
	
	public static UnitOfMeasurement getUOMEUnit(String vcellUnit) {
		return vcell2uome.get(vcellUnit);
	}
	
	public static void put(UnitOfMeasurement uomeUnit, String vcellUnit) {
		uome2vcell.put(uomeUnit, vcellUnit);
		vcell2uome.put(vcellUnit, uomeUnit);
	}
	
//	static {
//		put(UnitOfMeasurementPool.MICROMOLAR, ModelUnitSystem.UNITSYMBOL_uM);
//		put(UnitOfMeasurementPool.MOLAR, ModelUnitSystem.UNITSYMBOL_M);
//		put(UnitOfMeasurementPool.PER_SECOND, ModelUnitSystem.UNITSYMBOL_per_s);
//		put(UnitOfMeasurementPool.SECOND, ModelUnitSystem.UNITSYMBOL_s);
//	}
	
}
