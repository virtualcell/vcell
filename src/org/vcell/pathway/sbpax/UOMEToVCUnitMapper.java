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

import cbit.vcell.units.VCUnitDefinition;

public class UOMEToVCUnitMapper {

	protected static Map<UnitOfMeasurement, VCUnitDefinition> uome2vcell = 
			new HashMap<UnitOfMeasurement, VCUnitDefinition>();
	protected static Map<VCUnitDefinition, UnitOfMeasurement> vcell2uome = 
			new HashMap<VCUnitDefinition, UnitOfMeasurement>();
	
	public static VCUnitDefinition getVCUnit(UnitOfMeasurement uomeUnit) {
		return uome2vcell.get(uomeUnit);
	}
	
	public static UnitOfMeasurement getUOMEUnit(VCUnitDefinition vcellUnit) {
		return vcell2uome.get(vcellUnit);
	}
	
	public static void put(UnitOfMeasurement uomeUnit, VCUnitDefinition vcellUnit) {
		uome2vcell.put(uomeUnit, vcellUnit);
		vcell2uome.put(vcellUnit, uomeUnit);
	}
	
	static {
		put(UnitOfMeasurementPool.MICROMOLAR, VCUnitDefinition.UNIT_uM);
		put(UnitOfMeasurementPool.MOLAR, VCUnitDefinition.UNIT_M);
		put(UnitOfMeasurementPool.PER_SECOND, VCUnitDefinition.UNIT_per_s);
		put(UnitOfMeasurementPool.SECOND, VCUnitDefinition.UNIT_s);
	}
	
}
