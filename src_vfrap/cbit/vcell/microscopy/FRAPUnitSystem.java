/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */


package cbit.vcell.microscopy;

import cbit.vcell.units.VCUnitDefinition;
import cbit.vcell.units.VCUnitSystem;

public class FRAPUnitSystem extends VCUnitSystem {

		public static final String UNITSYMBOL_um2_per_s = "um2.s-1";
		public static final String UNITSYMBOL_per_s = "s-1";
		public static final String UNITSYMBOL_per_um_per_s = "um-1.s-1";
		
		public FRAPUnitSystem() {
			super();
		}
		
		public VCUnitDefinition getDiffusionRateUnit() { 
			return getInstance(UNITSYMBOL_um2_per_s); 
		}

		public VCUnitDefinition getBleachingWhileMonitoringRateUnit() { 
			return getInstance(UNITSYMBOL_per_s); 
		}

		public VCUnitDefinition getReactionOnRateUnit() { 
//			return getInstance(UNITSYMBOL_per_um_per_s);
			// For a pseudo-first order reaction, the reactionOnRate is 1/s.
			return getInstance(UNITSYMBOL_per_s); 
		}

		public VCUnitDefinition getReactionOffRateUnit() { 
			return getInstance(UNITSYMBOL_per_s); 
		}
		
}
