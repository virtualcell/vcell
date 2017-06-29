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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.vcell.util.Compare;
import org.vcell.util.Matchable;

import cbit.vcell.units.VCUnitDefinition;
import cbit.vcell.units.VCUnitSystem;
import ucar.units.RationalNumber;

public class ModelUnitSystem extends VCUnitSystem implements Matchable {

	public enum UnitCategory {
		primary("Primary"),
		electrical("Electrical"),
		stochastic("Stochastic"),
		otherDerived("Other Derived");
		
		private String description;
		private UnitCategory(String description){
			this.description = description;
		}
		public String getDescription(){
			return description;
		}
	}
	public enum UnitSystemRole {
		length("Length",UnitCategory.primary,true),
		area("Area",UnitCategory.primary,true),
		volume("Volume",UnitCategory.primary,true),
		time("Time",UnitCategory.primary,true),
		volumeSubstance("Volume Substance",UnitCategory.primary,true),
		membraneSubstance("Membrane Substance",UnitCategory.primary,true),
		lumpedReactionSubstance("LumpedReactionSubstance",UnitCategory.primary,true),
		voltage("Voltage",UnitCategory.electrical,false),
		current("Current",UnitCategory.electrical,false),
		capacitance("Capacitance",UnitCategory.electrical,false),
		conductance("Conductance",UnitCategory.electrical,false),
		stochasticSubtance("Stochastic Substance",UnitCategory.stochastic,false),
		bindingRadius("Particle Binding Radius",UnitCategory.stochastic,false),
		volumeConcentration("Volume Concentration",UnitCategory.otherDerived,false),
		membraneConcentration("Membrane Concentration",UnitCategory.otherDerived,false),
		volumeReactionRate("Volume Local Reaction Rate",UnitCategory.otherDerived,false),
		membraneReactionRate("Membrane Local Reaction Rate",UnitCategory.otherDerived,false),
		volumeFluxRate("Volume Flux Rate",UnitCategory.otherDerived,false),
		fluxReactionRate("Flux Reaction Rate",UnitCategory.otherDerived,false),
		lumpedReactionRate("Lumped Reaction Rate",UnitCategory.otherDerived,false),
		currentDensity("Current Density",UnitCategory.otherDerived,false),
		permeability("Membrane Permeability",UnitCategory.otherDerived,false),
		diffusion("Diffusion",UnitCategory.otherDerived,false);
		
		
		private String description;
		private UnitCategory unitCategory;
		boolean bEditable;
		UnitSystemRole(String description, UnitCategory unitCategory, boolean bEditable){
			this.description = description;
			this.unitCategory = unitCategory;
			this.bEditable = bEditable;
		}
		public String getDescription(){
			return description;
		}
		public boolean isEditable(){
			return bEditable;
		}
		public UnitCategory getUnitCategory(){
			return unitCategory;
		}
	};
	public class ModelUnitSystemEntry implements Serializable {
		private UnitSystemRole role;
		private VCUnitDefinition unit;
		private ModelUnitSystemEntry(UnitSystemRole role, VCUnitDefinition unit){
			this.role = role;
			this.unit = unit;
		}
		public UnitSystemRole getUnitSystemRole(){
			return role;
		}
		public VCUnitDefinition getVCUnitDefinition(){
			return unit;
		}	
	};
	
	private List<ModelUnitSystemEntry> modelUnitSystemEntries = null;
		
	// basic units : default values
	private String UNITSYMBOL_lengthUnit = "um";				 
	private String UNITSYMBOL_areaUnit = "um2";					 
	private String UNITSYMBOL_volumeUnit = "um3";				 
	private String UNITSYMBOL_timeUnit = "s";					
	private String UNITSYMBOL_volumeSubstanceUnit = "uM.um3"; // formerly "umol.um3.litre-1";
	private String UNITSYMBOL_membraneSubstanceUnit = "molecules";
	private String UNITSYMBOL_lumpedReactionSubstanceUnit = "molecules";
	// electrical
	private final String UNITSYMBOL_voltageUnit = "mV";				
	private final String UNITSYMBOL_currentUnit = "pA";				
	private final String UNITSYMBOL_capacitanceUnit = "pF";	
	private final String UNITSYMBOL_conductanceUnit = "nS";	
	// stochastic
	private final String UNITSYMBOL_stochasticSubstanceUnit = "molecules";  // should not change		 
	private final String UNITSYMBOL_bindingRadiusUnit = "um";
			
	// Unit symbols for reserved symbols
	private final String UNITSYMBOL_TemperatureUnit 		   	= "K";
	private final String UNITSYMBOL_FaradayConstantUnit 	   	= "C.mol-1";
	private final String uNITSYMBOL_FaradayConstantNMoleUnit 	= "C.nmol-1";
	private final String UNITSYMBOL_N_PMoleUnit 			   	= "molecules.pmol-1";
	private final String UNITSYMBOL_K_GHKUnit 					= "1e9";
	private final String UNITSYMBOL_GasConstantUnit 			= "mV.C.K-1.mol-1";
	private final String UNITSYMBOL_KMillivoltsUnit 			= "mV.V-1";
	private final String UNITSYMBOL_KMoleUnit 					= "uM.um3.molecules-1";
		
		
		private VCUnitDefinition volumeConcentrationUnit = null;
		private VCUnitDefinition membraneConcentrationUnit = null;
		private VCUnitDefinition volumeUnit = null;
		private VCUnitDefinition areaUnit = null;
		private VCUnitDefinition lengthUnit = null;
		private VCUnitDefinition timeUnit = null;
		private VCUnitDefinition voltageUnit = null;
		private VCUnitDefinition currentUnit = null;
		private VCUnitDefinition capacitanceUnit = null;
		private VCUnitDefinition conductanceUnit = null;
		private VCUnitDefinition stochasticSubstanceUnit = null;
		private VCUnitDefinition bindingRadiusUnit = null;
		private VCUnitDefinition volumeReactionRateUnit = null;
		private VCUnitDefinition membraneReactionRateUnit = null;
		private VCUnitDefinition volumeFluxRateUnit = null;
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
		private VCUnitDefinition lumpedReactionSubstanceUnit = null;
		

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
		private ModelUnitSystem() {
			super();
		}

		public List<ModelUnitSystemEntry> getModelUnitSystemEntries() {
			if (modelUnitSystemEntries==null){
				modelUnitSystemEntries = new ArrayList<ModelUnitSystemEntry>();
				
				modelUnitSystemEntries.add(new ModelUnitSystemEntry(UnitSystemRole.length,getLengthUnit()));
				modelUnitSystemEntries.add(new ModelUnitSystemEntry(UnitSystemRole.area,getAreaUnit()));
				modelUnitSystemEntries.add(new ModelUnitSystemEntry(UnitSystemRole.volume,getVolumeUnit()));
				modelUnitSystemEntries.add(new ModelUnitSystemEntry(UnitSystemRole.time,getTimeUnit()));
				modelUnitSystemEntries.add(new ModelUnitSystemEntry(UnitSystemRole.volumeSubstance,getVolumeSubstanceUnit()));
				modelUnitSystemEntries.add(new ModelUnitSystemEntry(UnitSystemRole.membraneSubstance,getMembraneSubstanceUnit()));
				modelUnitSystemEntries.add(new ModelUnitSystemEntry(UnitSystemRole.lumpedReactionSubstance,getLumpedReactionSubstanceUnit()));
				modelUnitSystemEntries.add(new ModelUnitSystemEntry(UnitSystemRole.voltage,getVoltageUnit()));
				modelUnitSystemEntries.add(new ModelUnitSystemEntry(UnitSystemRole.current,getCurrentUnit()));
				modelUnitSystemEntries.add(new ModelUnitSystemEntry(UnitSystemRole.capacitance,getCapacitanceUnit()));
				modelUnitSystemEntries.add(new ModelUnitSystemEntry(UnitSystemRole.conductance,getConductanceUnit()));
				modelUnitSystemEntries.add(new ModelUnitSystemEntry(UnitSystemRole.stochasticSubtance,getStochasticSubstanceUnit()));
				modelUnitSystemEntries.add(new ModelUnitSystemEntry(UnitSystemRole.bindingRadius,getBindingRadiusUnit()));				
				modelUnitSystemEntries.add(new ModelUnitSystemEntry(UnitSystemRole.volumeConcentration,getVolumeConcentrationUnit()));
				modelUnitSystemEntries.add(new ModelUnitSystemEntry(UnitSystemRole.membraneConcentration,getMembraneConcentrationUnit()));
				modelUnitSystemEntries.add(new ModelUnitSystemEntry(UnitSystemRole.volumeReactionRate,getVolumeReactionRateUnit()));
				modelUnitSystemEntries.add(new ModelUnitSystemEntry(UnitSystemRole.membraneReactionRate,getMembraneReactionRateUnit()));
				modelUnitSystemEntries.add(new ModelUnitSystemEntry(UnitSystemRole.volumeFluxRate,getVolumeFluxRateUnit()));
				modelUnitSystemEntries.add(new ModelUnitSystemEntry(UnitSystemRole.fluxReactionRate,getFluxReactionUnit()));
				modelUnitSystemEntries.add(new ModelUnitSystemEntry(UnitSystemRole.lumpedReactionRate,getLumpedReactionRateUnit()));
				modelUnitSystemEntries.add(new ModelUnitSystemEntry(UnitSystemRole.currentDensity,getCurrentDensityUnit()));
				modelUnitSystemEntries.add(new ModelUnitSystemEntry(UnitSystemRole.permeability,getPermeabilityUnit()));
				modelUnitSystemEntries.add(new ModelUnitSystemEntry(UnitSystemRole.diffusion,getDiffusionRateUnit()));

				modelUnitSystemEntries = Collections.unmodifiableList(modelUnitSystemEntries);
			}
			return modelUnitSystemEntries;
		}
		
		public VCUnitDefinition getSubstanceUnit(Structure structure) {
			if (structure instanceof Feature) {
				return getVolumeSubstanceUnit();	
			} else if (structure instanceof Membrane) {
				return getMembraneSubstanceUnit();
			}
			throw new RuntimeException("Unknown Structure type : cannot determine concentration unit");
		}

		public VCUnitDefinition getConcentrationUnit(Structure structure) {
			if (structure instanceof Feature) {
				return getVolumeConcentrationUnit();	
			} else if (structure instanceof Membrane) {
				return getMembraneConcentrationUnit();
			} else if (structure == null){
				throw new RuntimeException("null structure, cannot determine concentration unit");
			}
			throw new RuntimeException("Unknown Structure type : cannot determine concentration unit");
		}

		public VCUnitDefinition getVolumeConcentrationUnit() {
			if (volumeConcentrationUnit == null) {
				volumeConcentrationUnit = getVolumeSubstanceUnit().divideBy(getVolumeUnit());
			}
			return volumeConcentrationUnit;
		}
		public VCUnitDefinition getMembraneConcentrationUnit() {
			if (membraneConcentrationUnit == null) {
				membraneConcentrationUnit = getMembraneSubstanceUnit().divideBy(getAreaUnit());
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
		public VCUnitDefinition getCapacitanceUnit() {
			if (capacitanceUnit == null) {
				capacitanceUnit = getInstance(UNITSYMBOL_capacitanceUnit);
			} 
			return capacitanceUnit;
		}
		public VCUnitDefinition getConductanceUnit() {
			if (conductanceUnit == null) {
				conductanceUnit = getInstance(UNITSYMBOL_conductanceUnit);
			} 
			return conductanceUnit; 
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
		
		public VCUnitDefinition getVolumeSubstanceUnit() {
			if (volumeSubstanceUnit == null) {
				volumeSubstanceUnit = getInstance(UNITSYMBOL_volumeSubstanceUnit);
			}
			return volumeSubstanceUnit;
		}

		public VCUnitDefinition getMembraneSubstanceUnit() {
			if (membraneSubstanceUnit == null) {
				membraneSubstanceUnit = getInstance(UNITSYMBOL_membraneSubstanceUnit);
			}
			return membraneSubstanceUnit;
		}
		
		public VCUnitDefinition getLumpedReactionSubstanceUnit() {
			if (lumpedReactionSubstanceUnit == null) {
				lumpedReactionSubstanceUnit = getInstance(UNITSYMBOL_lumpedReactionSubstanceUnit);
			} 
			return lumpedReactionSubstanceUnit;
		}

		public VCUnitDefinition getFluxReactionUnit() {
			if (fluxReactionUnit == null) {
				fluxReactionUnit = getVolumeConcentrationUnit().multiplyBy(getLengthUnit()).divideBy(getTimeUnit());
			} 
			return fluxReactionUnit;
		}
		public VCUnitDefinition getMembraneReactionRateUnit() {
			if (membraneReactionRateUnit == null) {
				membraneReactionRateUnit = getMembraneConcentrationUnit().divideBy(getTimeUnit());
			} 
			return membraneReactionRateUnit;
		}
		public VCUnitDefinition getCurrentDensityUnit() {
			if (currentDensityUnit == null) {
				currentDensityUnit = getCurrentUnit().divideBy(getAreaUnit());
			}
			return currentDensityUnit;
		}
		public VCUnitDefinition getVolumeReactionRateUnit() {
			if (volumeReactionRateUnit == null) {
				volumeReactionRateUnit = getVolumeConcentrationUnit().divideBy(getTimeUnit());
			} 
			return volumeReactionRateUnit;
		}
		public VCUnitDefinition getVolumeFluxRateUnit() {
			if (volumeFluxRateUnit == null){
				volumeFluxRateUnit = getVolumeConcentrationUnit().multiplyBy(getLengthUnit()).divideBy(getTimeUnit());
			}
			return volumeFluxRateUnit;
		}

		public VCUnitDefinition getPermeabilityUnit() {
			if (permeabilityUnit == null) {
				permeabilityUnit =  getLengthUnit().divideBy(getTimeUnit());
			} 
			return permeabilityUnit;
		}
		public VCUnitDefinition getLumpedReactionRateUnit() {
			if (lumpedReactionRateUnit == null) {
				lumpedReactionRateUnit = getLumpedReactionSubstanceUnit().divideBy(getTimeUnit());
			}
			return lumpedReactionRateUnit;
		}
		public VCUnitDefinition getDiffusionRateUnit() {
			if (diffusionRateUnit == null) {
				diffusionRateUnit = getLengthUnit().raiseTo(new RationalNumber(2)).divideBy(getTimeUnit());
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

		VCUnitDefinition getN_PMoleUnit() {
			if (NPMoleUnit == null) {
				NPMoleUnit = getInstance(UNITSYMBOL_N_PMoleUnit);
			}
			return NPMoleUnit;
		}

		VCUnitDefinition getK_GHKUnit() {
			if (KGHKUnit == null) {
				KGHKUnit = getInstance(UNITSYMBOL_K_GHKUnit);
			}
			return KGHKUnit;
		}

		VCUnitDefinition getGasConstantUnit() {
			if (GasConstantUnit == null) {
				GasConstantUnit = getInstance(UNITSYMBOL_GasConstantUnit);
			}
			return GasConstantUnit;
		}

		VCUnitDefinition getK_mV_perV_Unit() {
			if (KMilliVoltUnit == null) {
				KMilliVoltUnit = getInstance(UNITSYMBOL_KMillivoltsUnit);
			} 
			return KMilliVoltUnit;
		}

		VCUnitDefinition getKMoleUnit() {
			if (KMOLEUnit == null) {
				KMOLEUnit = getInstance(UNITSYMBOL_KMoleUnit);
			}
			return KMOLEUnit;
		}
		
		public static ModelUnitSystem createDefaultSBMLLevel2Units(){
			VCUnitSystem  tempUnitSystem = new VCUnitSystem() {	};
			VCUnitDefinition modelSubstanceUnit = tempUnitSystem.getInstance("mol");
			VCUnitDefinition modelVolumeUnit = tempUnitSystem.getInstance("litre");
			VCUnitDefinition modelAreaUnit = tempUnitSystem.getInstance("m2");
			VCUnitDefinition modelLengthUnit = tempUnitSystem.getInstance("m");
			VCUnitDefinition modelTimeUnit = tempUnitSystem.getInstance("s");
			ModelUnitSystem modelUnitSystem = ModelUnitSystem.createUnitSystem(modelSubstanceUnit, modelSubstanceUnit, modelSubstanceUnit, modelVolumeUnit, modelAreaUnit, modelLengthUnit, modelTimeUnit);

			return modelUnitSystem;
		}

		
		public static boolean isCompatibleWithDefaultSBMLLevel2Units(ModelUnitSystem unitSystem) {
			ModelUnitSystem defaultSBMLLevel2Units = createDefaultSBMLLevel2Units();
			if  (unitSystem.getVolumeSubstanceUnit().isCompatible(defaultSBMLLevel2Units.getVolumeSubstanceUnit()) &&
				 unitSystem.getVolumeUnit().isCompatible(defaultSBMLLevel2Units.getVolumeUnit()) &&
				 unitSystem.getAreaUnit().isCompatible(defaultSBMLLevel2Units.getAreaUnit()) &&
				 unitSystem.getLengthUnit().isCompatible(defaultSBMLLevel2Units.getLengthUnit()) &&
				 unitSystem.getTimeUnit().isCompatible(defaultSBMLLevel2Units.getTimeUnit()) ) {
				return true;
			}
			return false;
		}
		
		private static ModelUnitSystem createUnitSystem(VCUnitDefinition volumeSubstanceUnit, VCUnitDefinition membraneSubstanceUnit, VCUnitDefinition lumpedReactionSubstanceUnit, VCUnitDefinition volumeUnit, VCUnitDefinition areaUnit, VCUnitDefinition lengthUnit, VCUnitDefinition timeUnit) {
			ModelUnitSystem modelUnitSystem = new ModelUnitSystem();
			VCUnitDefinition mole = modelUnitSystem.getInstance("mole");
			VCUnitDefinition molecules = modelUnitSystem.getInstance("molecules");
			if (volumeSubstanceUnit==null || (!volumeSubstanceUnit.isCompatible(mole) && !volumeSubstanceUnit.isCompatible(molecules) && !volumeSubstanceUnit.isCompatible(modelUnitSystem.getInstance_DIMENSIONLESS()))){
				throw new RuntimeException("volume substance unit ["+volumeSubstanceUnit+"] must be compatible with mole or molecules");
			}
			if (membraneSubstanceUnit==null || (!membraneSubstanceUnit.isCompatible(mole) && !membraneSubstanceUnit.isCompatible(molecules) && !membraneSubstanceUnit.isCompatible(modelUnitSystem.getInstance_DIMENSIONLESS()))){
				throw new RuntimeException("membrane substance unit ["+membraneSubstanceUnit+"] must be compatible with mole or molecules");
			}
			if (lumpedReactionSubstanceUnit==null || (!lumpedReactionSubstanceUnit.isCompatible(mole) && !lumpedReactionSubstanceUnit.isCompatible(molecules) && !lumpedReactionSubstanceUnit.isCompatible(modelUnitSystem.getInstance_DIMENSIONLESS()))){
				throw new RuntimeException("lumped reaction substance unit ["+lumpedReactionSubstanceUnit+"] must be compatible with mole or molecules");
			}
			if (lengthUnit==null || (!lengthUnit.isCompatible(modelUnitSystem.getInstance("m")) && !lengthUnit.isCompatible(modelUnitSystem.getInstance_DIMENSIONLESS()))){
				throw new RuntimeException("length unit ["+lengthUnit+"] must be compatible with meter");
			}
			if (areaUnit==null || (!areaUnit.isCompatible(modelUnitSystem.getInstance("m2")) && !areaUnit.isCompatible(modelUnitSystem.getInstance_DIMENSIONLESS()))){
				throw new RuntimeException("area unit ["+areaUnit+"] must be compatible with meter^2");
			}
			if (volumeUnit==null || (!volumeUnit.isCompatible(modelUnitSystem.getInstance("m3")) && !volumeUnit.isCompatible(modelUnitSystem.getInstance_DIMENSIONLESS()))){
				throw new RuntimeException("volume unit ["+volumeUnit+"] must be compatible with meter^3");
			}
			// if vol unit is dimensionless, area and length should also be dimensionless.
			if (volumeUnit.isCompatible(modelUnitSystem.getInstance_DIMENSIONLESS())) {
				if (!lengthUnit.isCompatible(modelUnitSystem.getInstance_DIMENSIONLESS()) && !areaUnit.isCompatible(modelUnitSystem.getInstance_DIMENSIONLESS())) {
					throw new RuntimeException("If volume unit is dimensionless, length and area units should also be dimensionless.");
				}
			}
			
			if (timeUnit==null || (!timeUnit.isCompatible(modelUnitSystem.getInstance("s")) && !timeUnit.isCompatible(modelUnitSystem.getInstance_DIMENSIONLESS()))){
				throw new RuntimeException("time unit ["+timeUnit+"] must be compatible with seconds");
			}
			modelUnitSystem.UNITSYMBOL_lengthUnit = lengthUnit.getSymbol();				 
			modelUnitSystem.UNITSYMBOL_areaUnit = areaUnit.getSymbol();					 
			modelUnitSystem.UNITSYMBOL_volumeUnit = volumeUnit.getSymbol();				 
			modelUnitSystem.UNITSYMBOL_timeUnit = timeUnit.getSymbol();					
			modelUnitSystem.UNITSYMBOL_volumeSubstanceUnit = volumeSubstanceUnit.getSymbol();
			modelUnitSystem.UNITSYMBOL_membraneSubstanceUnit = membraneSubstanceUnit.getSymbol();
			modelUnitSystem.UNITSYMBOL_lumpedReactionSubstanceUnit = lumpedReactionSubstanceUnit.getSymbol();

			return modelUnitSystem;
		}
		
		public static ModelUnitSystem createSBMLUnitSystem(VCUnitDefinition modelSubstanceUnit, VCUnitDefinition modelVolumeUnit, VCUnitDefinition modelAreaUnit, VCUnitDefinition modelLengthUnit, VCUnitDefinition modelTimeUnit) {
			return createUnitSystem(modelSubstanceUnit, modelSubstanceUnit, modelSubstanceUnit, modelVolumeUnit, modelAreaUnit, modelLengthUnit, modelTimeUnit);
		}
		
		public static ModelUnitSystem createVCModelUnitSystem(String volumeSubstanceSymbol, String membraneSubstanceSymbol, String lumpedReactionSubstanceSymbol, String volumeSymbol, String areaSymbol, String lengthSymbol, String timeSymbol) {
			VCUnitSystem tempUnitSystem = new VCUnitSystem() {};
			return createUnitSystem(
					tempUnitSystem.getInstance(volumeSubstanceSymbol),
					tempUnitSystem.getInstance(membraneSubstanceSymbol),
					tempUnitSystem.getInstance(lumpedReactionSubstanceSymbol),
					tempUnitSystem.getInstance(volumeSymbol),
					tempUnitSystem.getInstance(areaSymbol),
					tempUnitSystem.getInstance(lengthSymbol),
					tempUnitSystem.getInstance(timeSymbol));
		}

		public static ModelUnitSystem createDefaultVCModelUnitSystem() {
			ModelUnitSystem modelUnitSystem = new ModelUnitSystem();

			return modelUnitSystem;
		}

		public boolean compareEqual(Matchable obj) {
			if (obj instanceof ModelUnitSystem){
				ModelUnitSystem other = (ModelUnitSystem)obj;
				if (!Compare.isEqual(UNITSYMBOL_lengthUnit,other.UNITSYMBOL_lengthUnit)){
					return false;				 
				}
				if (!Compare.isEqual(UNITSYMBOL_areaUnit,other.UNITSYMBOL_areaUnit)){
					return false;				 
				}
				if (!Compare.isEqual(UNITSYMBOL_volumeUnit,other.UNITSYMBOL_volumeUnit)){
					return false;				 
				}
				if (!Compare.isEqual(UNITSYMBOL_timeUnit,other.UNITSYMBOL_timeUnit)){
					return false;				 
				}
				if (!Compare.isEqual(UNITSYMBOL_volumeSubstanceUnit,other.UNITSYMBOL_volumeSubstanceUnit)){
					return false;				 
				}
				if (!Compare.isEqual(UNITSYMBOL_membraneSubstanceUnit,other.UNITSYMBOL_membraneSubstanceUnit)){
					return false;				 
				}
				if (!Compare.isEqual(UNITSYMBOL_lumpedReactionSubstanceUnit,other.UNITSYMBOL_lumpedReactionSubstanceUnit)){
					return false;				 
				}
				return true;
			}
			return false;
		}

	}