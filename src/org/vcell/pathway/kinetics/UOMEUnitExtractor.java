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

public class UOMEUnitExtractor {

	public static VCUnitDefinition extractVCUnitDefinition(SBMeasurable measurable) {
		VCUnitDefinition unit = VCUnitDefinition.UNIT_TBD;
		for(UnitOfMeasurement uomeUnit : measurable.getUnit()) {
			VCUnitDefinition unitNew = UOMEToVCUnitMapper.getVCUnit(uomeUnit);
			if(unitNew != null) { unit = unitNew; }
		}
		return unit;
	}
	
}
