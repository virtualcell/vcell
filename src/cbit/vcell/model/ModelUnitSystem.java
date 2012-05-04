/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */


package cbit.vcell.model;

import java.io.Serializable;
import cbit.vcell.units.VCUnitSystem;
import cbit.vcell.units.VCUnitDefinition;

public class ModelUnitSystem extends VCUnitSystem {

		public static final String UNITSYMBOL_umol_um3_per_L = "umol.um3.litre-1";
		public static final String UNITSYMBOL_uM = "uM";
		public static final String UNITSYMBOL_um = "um";
		public static final String UNITSYMBOL_M = "M";
		public static final String UNITSYMBOL_per_s = "s-1";
		public static final String UNITSYMBOL_s = "s";
		
		private String UNITSYMBOL_volumeConcentrationUnit = "uM";			
		private String UNITSYMBOL_membraneConcentrationUnit = "molecules.um-2";			
		private String UNITSYMBOL_lengthUnit = "um";				 
		private String UNITSYMBOL_areaUnit = "um2";					 
		private String UNITSYMBOL_volumeUnit = "um3";				 
		private String UNITSYMBOL_timeUnit = "s";					
		private String UNITSYMBOL_voltageUnit = "mV";				
		private String UNITSYMBOL_currentUnit = "pA";				
		private String UNITSYMBOL_specificCapacitanceUnit = "pF/um2";	
		private String UNITSYMBOL_membraneConductivityUnit = "nS/um2";	
		private String UNITSYMBOL_lumpedReactionSubstanceUnit = "molecules";	
		private String UNITSYMBOL_stochasticSubstanceUnit = "molecules";		 
		private String UNITSYMBOL_bindingRadiusUnit = "um";					
		private String UNITSYMBOL_volumeSubstanceUnit = "mol";				
		private String UNITSYMBOL_membraneSubstanceUnit = "molecules";
//		private String UNITSYMBOL_substanceUnit = null;
		
		// Unit symbols for reserved symbols
		private String UNITSYMBOL_TemperatureUnit 		   	= "K";
		private String UNITSYMBOL_FaradayConstantUnit 	   	= "C.mol-1";
		private String uNITSYMBOL_FaradayConstantNMoleUnit 	= "C.nmol-1";
		private String UNITSYMBOL_N_PMoleUnit 			   	= "molecules.pmol-1";
		private String UNITSYMBOL_K_GHKUnit 				= "1e9";
		private String UNITSYMBOL_GasConstantUnit 			= "mV.C.K-1.mol-1";
		private String UNITSYMBOL_KMillivoltsUnit 			= "mV.V-1";
		private String UNITSYMBOL_KMoleUnit 				= "uM.um3.molecules-1";
		
		
		private VCUnitDefinition volumeConcentrationUnit = null;
		private VCUnitDefinition membraneConcentrationUnit = null;
		private VCUnitDefinition volumeUnit = null;
		private VCUnitDefinition areaUnit = null;
		private VCUnitDefinition lengthUnit = null;
		private VCUnitDefinition timeUnit = null;
		private VCUnitDefinition voltageUnit = null;
		private VCUnitDefinition currentUnit = null;
		private VCUnitDefinition specificCapacitanceUnit = null;
		private VCUnitDefinition membraneConductivityUnit = null;
		private VCUnitDefinition stochasticSubstanceUnit = null;
		private VCUnitDefinition bindingRadiusUnit = null;
		private VCUnitDefinition volumeReactionRateUnit = null;
		private VCUnitDefinition membraneReactionRateUnit = null;
		private VCUnitDefinition fluxReactionUnit = null;
		private VCUnitDefinition lumpedReactionRateUnit = null;
		private VCUnitDefinition currentDensityUnit = null;
		private VCUnitDefinition permeabilityUnit = null;
		private VCUnitDefinition diffusionRateUnit = null;

		private VCUnitDefinition TemperatureUnit = null;
		private VCUnitDefinition FaradayUnit = null;
		private VCUnitDefinition FaradayNMoleUnit = null;
		private VCUnitDefinition NPMoleUnit = null;
		private VCUnitDefinition KGHKUnit = null;
		private VCUnitDefinition GasConstantUnit = null;
		private VCUnitDefinition KMilliVoltUnit = null;
		private VCUnitDefinition KMOLEUnit = null;
		
		/** For SBML **/
		private VCUnitDefinition volumeSubstanceUnit = null;
		private VCUnitDefinition membraneSubstanceUnit = null;
		private VCUnitDefinition lumpedSubstanceUnit = null;
		

		/** 
		 * Default constructor : initializes a default VCell unit system with the following units:
		 * 
		 * UNITSYMBOL_volumeConcentrationUnit = "uM";			
		 * UNITSYMBOL_membraneConcentrationUnit = "molecules/um2";	
		 * UNITSYMBOL_lengthUnit = "um";
		 * UNITSYMBOL_areaUnit = "um2";
		 * UNITSYMBOL_volumeUnit = "um3";
		 * UNITSYMBOL_timeUnit = "s";
		 * UNITSYMBOL_voltageUnit = "mV";
		 * UNITSYMBOL_currentUnit = "pA";
		 * UNITSYMBOL_specificCapacitanceUnit = "pF/um2";
		 * UNITSYMBOL_membraneConductivityUnit = "nS/um2";
		 * UNITSYMBOL_lumpedReactionSubstanceUnit = "molecules";
		 * UNITSYMBOL_stochasticSubstanceUnit = "molecules";
		 * UNITSYMBOL_bindingRadiusUnit = "um";
		 * UNITSYMBOL_volumeSubstanceUnit = "mol";
		 * UNITSYMBOL_membraneSubstanceUnit = "molecules";	
		 * 
		 */
		public ModelUnitSystem() {
			super();
		}

		public VCUnitDefinition getConcentrationUnit(Structure structure) {
			if (structure instanceof Feature) {
				return getVolumeConcentrationUnit();	
			} else if (structure instanceof Membrane) {
				return getMembraneConcentrationUnit();
			}
			throw new RuntimeException("Unknown Structure type : cannot determine concentration unit");
		}

		public VCUnitDefinition getVolumeConcentrationUnit() {
			if (volumeConcentrationUnit == null) {
				volumeConcentrationUnit = getInstance(UNITSYMBOL_volumeConcentrationUnit);
			}
			return volumeConcentrationUnit;
		}
		public VCUnitDefinition getMembraneConcentrationUnit() {
			if (membraneConcentrationUnit == null) {
				membraneConcentrationUnit = getInstance(UNITSYMBOL_membraneConcentrationUnit);
			}
			return membraneConcentrationUnit;
		}
		public VCUnitDefinition getVolumeUnit() {
			if (volumeUnit == null) {
				volumeUnit = getInstance(UNITSYMBOL_volumeUnit);
			} 
			return volumeUnit;
		}
		public VCUnitDefinition getAreaUnit() {
			if (areaUnit == null) {
				areaUnit = getInstance(UNITSYMBOL_areaUnit);
			} 
			return areaUnit;
		}
		public VCUnitDefinition getLengthUnit() {
			if (lengthUnit == null) {
				lengthUnit = getInstance(UNITSYMBOL_lengthUnit);
			} 
			return lengthUnit;
		}
		public VCUnitDefinition getTimeUnit() {
			if (timeUnit == null) {
				timeUnit = getInstance(UNITSYMBOL_timeUnit);
			} 
			return timeUnit;
		}
		public VCUnitDefinition getVoltageUnit() {
			if (voltageUnit == null) {
				voltageUnit = getInstance(UNITSYMBOL_voltageUnit);
			} 
			return voltageUnit; 
		}
		public VCUnitDefinition getCurrentUnit() {
			if (currentUnit == null) {
				currentUnit = getInstance(UNITSYMBOL_currentUnit);
			}
			return currentUnit;
		}
		public VCUnitDefinition getSpecificCapacitanceUnit() {
			if (specificCapacitanceUnit == null) {
				specificCapacitanceUnit = getInstance(UNITSYMBOL_specificCapacitanceUnit);
			} 
			return specificCapacitanceUnit;
		}
		public VCUnitDefinition getMembraneConductivityUnit() {
			if (membraneConductivityUnit == null) {
				membraneConductivityUnit = getInstance(UNITSYMBOL_membraneConductivityUnit);
			} 
			return membraneConductivityUnit; 
		}
		public VCUnitDefinition getStochasticSubstanceUnit() {
			if (stochasticSubstanceUnit == null) {
				stochasticSubstanceUnit = getInstance(UNITSYMBOL_stochasticSubstanceUnit);
			} 
			return stochasticSubstanceUnit;
		}
		public VCUnitDefinition getBindingRadiusUnit() {
			if (bindingRadiusUnit == null) {
				bindingRadiusUnit = getInstance(UNITSYMBOL_bindingRadiusUnit);
			}
			return bindingRadiusUnit;
		}
		
		/** @deprecated :Use This ONLY in SBML **/  
		public VCUnitDefinition getVolumeSubstanceUnit() {
			if (volumeSubstanceUnit == null) {
				volumeSubstanceUnit = getInstance(UNITSYMBOL_volumeSubstanceUnit);
			}
			return volumeSubstanceUnit;
		}
		/** @deprecated :Use This ONLY in SBML **/
		public VCUnitDefinition getMembraneSubstanceUnit() {
			if (membraneSubstanceUnit == null) {
				membraneSubstanceUnit = getInstance(UNITSYMBOL_membraneSubstanceUnit);
			}
			return membraneSubstanceUnit;
		}
		
		/** @deprecated :Use This ONLY in SBML **/  
		public VCUnitDefinition getLumpedSubstanceUnit() {
			if (lumpedSubstanceUnit == null) {
				lumpedSubstanceUnit = getInstance(UNITSYMBOL_lumpedReactionSubstanceUnit);
			} 
			return lumpedSubstanceUnit;
		}

		public VCUnitDefinition getFluxReactionUnit() {
			if (fluxReactionUnit == null) {
				String fluxReactionUnitSymbol = UNITSYMBOL_volumeConcentrationUnit+ "." + UNITSYMBOL_lengthUnit + "." + UNITSYMBOL_timeUnit + "-1";
				fluxReactionUnit = getInstance(fluxReactionUnitSymbol);
			} 
			return fluxReactionUnit;
		}
		public VCUnitDefinition getMembraneReactionRateUnit() {
			if (membraneReactionRateUnit == null) {
				String membraneReactionRateUnitSymbol = UNITSYMBOL_membraneConcentrationUnit + "." +  UNITSYMBOL_timeUnit + "-1";
				membraneReactionRateUnit = getInstance(membraneReactionRateUnitSymbol);
			} 
			return membraneReactionRateUnit;
		}
		public VCUnitDefinition getCurrentDensityUnit() {
			if (currentDensityUnit == null) {
				String currentDensityUnitSymbol = UNITSYMBOL_currentUnit + "/" + UNITSYMBOL_areaUnit;
				currentDensityUnit = getInstance(currentDensityUnitSymbol);
			}
			return currentDensityUnit;
		}
		public VCUnitDefinition getVolumeReactionRateUnit() {
			if (volumeReactionRateUnit == null) {
				String volReactionRateUnitSymbol = UNITSYMBOL_volumeConcentrationUnit+ "." + UNITSYMBOL_timeUnit + "-1";
				volumeReactionRateUnit = getInstance(volReactionRateUnitSymbol);
			} 
			return volumeReactionRateUnit;
		}
		public VCUnitDefinition getPermeabilityUnit() {
			if (permeabilityUnit == null) {
				String permeabilityUnitSymbol =  UNITSYMBOL_lengthUnit + "." + UNITSYMBOL_timeUnit + "-1";
				permeabilityUnit =  getInstance(permeabilityUnitSymbol);
			} 
			return permeabilityUnit;
		}
		public VCUnitDefinition getLumpedReactionRateUnit() {
			if (lumpedReactionRateUnit == null) {
				String lumpedRxnRateUnitsymbol = UNITSYMBOL_lumpedReactionSubstanceUnit + "." + UNITSYMBOL_timeUnit + "-1";
				lumpedReactionRateUnit = getInstance(lumpedRxnRateUnitsymbol);
			}
			return lumpedReactionRateUnit;
		}
		public VCUnitDefinition getDiffusionRateUnit() {
			if (diffusionRateUnit == null) {
				String diffusionRateUnitSymbol = UNITSYMBOL_areaUnit + "." + UNITSYMBOL_timeUnit + "-1";
				diffusionRateUnit = getInstance(diffusionRateUnitSymbol);
			} 
			return diffusionRateUnit;
		}

		public VCUnitDefinition getTemperatureUnit() {
			if (TemperatureUnit == null) {
				TemperatureUnit =  getInstance(UNITSYMBOL_TemperatureUnit);
			}
			return TemperatureUnit;
		}

		public VCUnitDefinition getFaradayConstantUnit() {
			if (FaradayUnit == null) {
				FaradayUnit = getInstance(UNITSYMBOL_FaradayConstantUnit);
			}
			return FaradayUnit;
		}

		public VCUnitDefinition getFaradayConstantNMoleUnit() {
			if (FaradayNMoleUnit == null) {
				FaradayNMoleUnit = getInstance(uNITSYMBOL_FaradayConstantNMoleUnit);
			}
			return FaradayNMoleUnit;
		}

		public VCUnitDefinition getN_PMoleUnit() {
			if (NPMoleUnit == null) {
				NPMoleUnit = getInstance(UNITSYMBOL_N_PMoleUnit);
			}
			return NPMoleUnit;
		}

		public VCUnitDefinition getK_GHKUnit() {
			if (KGHKUnit == null) {
				KGHKUnit = getInstance(UNITSYMBOL_K_GHKUnit);
			}
			return KGHKUnit;
		}

		public VCUnitDefinition getGasConstantUnit() {
			if (GasConstantUnit == null) {
				GasConstantUnit = getInstance(UNITSYMBOL_GasConstantUnit);
			}
			return GasConstantUnit;
		}

		public VCUnitDefinition getK_mV_perV_Unit() {
			if (KMilliVoltUnit == null) {
				KMilliVoltUnit = getInstance(UNITSYMBOL_KMillivoltsUnit);
			} 
			return KMilliVoltUnit;
		}

		public VCUnitDefinition getKMoleUnit() {
			if (KMOLEUnit == null) {
				KMOLEUnit = getInstance(UNITSYMBOL_KMoleUnit);
			}
			return KMOLEUnit;
		}
	}