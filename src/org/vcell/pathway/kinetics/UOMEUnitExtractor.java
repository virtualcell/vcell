/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */
package org.vcell.pathway.kinetics;

import org.vcell.pathway.sbpax.SBMeasurable;
import org.vcell.pathway.sbpax.UOMEToVCUnitMapper;
import org.vcell.pathway.sbpax.UnitOfMeasurement;

import cbit.vcell.units.VCUnitDefinition;
import cbit.vcell.units.VCUnitSystem;

public class UOMEUnitExtractor {

	public static VCUnitDefinition extractVCUnitDefinition(SBMeasurable measurable, VCUnitSystem vcUnitSystem) {
		String unit = vcUnitSystem.getInstance_TBD().getSymbol();
		for(UnitOfMeasurement uomeUnit : measurable.getUnit()) {
			String unitNew = UOMEToVCUnitMapper.getVCUnitSymbol(uomeUnit);
			if(unitNew != null) { unit = unitNew; }
		}
		return vcUnitSystem.getInstance(unit);
	}
	
}
