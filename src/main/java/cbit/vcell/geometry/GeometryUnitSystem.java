/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.geometry;

import cbit.vcell.units.VCUnitDefinition;
import cbit.vcell.units.VCUnitSystem;

public class GeometryUnitSystem extends VCUnitSystem {

		public static final String UNITSYMBOL_um = "um";
		
		private VCUnitDefinition lengthUnit = null;			// um
		
		public GeometryUnitSystem() {
			this.lengthUnit = getInstance(UNITSYMBOL_um);
		}

		public GeometryUnitSystem(VCUnitDefinition argLengthUnit) {
			super();
			this.lengthUnit = argLengthUnit;
		}
		public VCUnitDefinition getLengthUnit() {
			return lengthUnit;
		}
		public VCUnitDefinition getAreaUnit() { 
			return lengthUnit.multiplyBy(lengthUnit); 
		}
		public VCUnitDefinition getVolumeUnit() { 
			return lengthUnit.multiplyBy(lengthUnit).multiplyBy(lengthUnit); 
		}		
}
