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

import org.sbpax.schemas.UOMEList;
import org.vcell.pathway.id.URIUtil;
import org.vcell.pathway.sbpax.SBMeasurable;
import org.vcell.pathway.sbpax.UnitOfMeasurement;

import cbit.vcell.units.VCUnitDefinition;

public class UOMEUnitExtractor {

	public static VCUnitDefinition extractVCUnitDefinition(SBMeasurable measurable) {
		VCUnitDefinition unit = VCUnitDefinition.UNIT_TBD;
		for(UnitOfMeasurement uomeUnit : measurable.getUnit()) {
			String unitURI = uomeUnit.getID();
			if(unitURI != null && URIUtil.isAbsoluteURI(unitURI)) {
				String localName = URIUtil.getLocalName(unitURI);
				if(UOMEList.Micromolar.getLocalName().equals(localName)) {
					unit = VCUnitDefinition.UNIT_uM;
				}
			}
		}
		return unit;
	}
	
}
