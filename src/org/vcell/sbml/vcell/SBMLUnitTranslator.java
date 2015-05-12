/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sbml.vcell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.sbml.libsbml.Model;
import org.sbml.libsbml.SBMLConstructorException;
import org.sbml.libsbml.SBMLDocument;
import org.sbml.libsbml.SBMLReader;
import org.sbml.libsbml.SBMLWriter;
import org.sbml.libsbml.Unit;
import org.sbml.libsbml.UnitDefinition;
import org.sbml.libsbml.libsbml;
import org.sbml.libsbml.libsbmlConstants;
import org.vcell.sbml.SbmlException;
import org.vcell.sbml.vcell.UnitRepresentation.Fundamental;
import org.vcell.util.TokenMangler;

import cbit.vcell.model.ModelUnitSystem;
import cbit.vcell.resource.NativeLib;
import cbit.vcell.units.VCUnitDefinition;
import cbit.vcell.units.VCUnitSystem;

/**
 * Insert the type's description here.
 * Creation date: (2/28/2006 5:22:58 PM)
 * @author: Anuradha Lakshminarayana
 */
public class SBMLUnitTranslator extends LibSBMLClient {
	private static class UnitD extends Unit {

		UnitD(long level, long version) throws SBMLConstructorException, SbmlException {
			super(level, version);
			validate( super.setScale(0) );
			validate( super.setMultiplier(1) );
			validate( super.setExponent(1) );
			validate( super.setKind(libsbmlConstants.UNIT_KIND_DIMENSIONLESS) );
		}
		
		UnitD(UnitRepresentation.Fundamental f, long level, long version) throws SBMLConstructorException, SbmlException {
			super(level, version);
			validate( super.setScale(0) );
			validate( super.setMultiplier(1) );
			validate( super.setExponent(f.power) );
			setKindString(f.unit);
		}
		
		void setKindString(String k) throws SbmlException {
			int kind = libsbml.UnitKind_forName(k);
			if ( !valid( setKind(kind)) ) {
				//throw new SbmlException("unsupported type name " +k);
				System.err.println("unsupported type name " + k);
			}
		}
		
		
	}

	// special units
	public static final String DIMENSIONLESS = "dimensionless";
	public static final String ITEM = "item";
	//default built-in units
	public static final String SUBSTANCE = "substance";
	public static final String VOLUME = "volume";
	public static final String AREA = "area";
	public static final String LENGTH = "length";
	public static final String TIME = "time";
	
	private static Map<String, String> SbmlDefaultUnits = new TreeMap<String, String>();  
	static {            
		SbmlDefaultUnits.put("substance", "mol");
		SbmlDefaultUnits.put("volume", "litre");
		SbmlDefaultUnits.put("area", "m2");
		SbmlDefaultUnits.put("length", "m");
		SbmlDefaultUnits.put("time", "s");
//		SbmlDefaultUnits.put("substance", "mole");
//		SbmlDefaultUnits.put("volume", "litre");
//		SbmlDefaultUnits.put("area", "metre2");
//		SbmlDefaultUnits.put("length", "metre");
//		SbmlDefaultUnits.put("time", "second");
	}

	private static ArrayList<String> SbmlBaseUnits = new ArrayList<String>();
    static {
		SbmlBaseUnits.add("ampere");
		SbmlBaseUnits.add("avogadro");
		SbmlBaseUnits.add("becquerel");
		SbmlBaseUnits.add("candela");
		SbmlBaseUnits.add("coulomb");
		SbmlBaseUnits.add("dimensionless");
		SbmlBaseUnits.add("farad");
		SbmlBaseUnits.add("gram");
		SbmlBaseUnits.add("gray");
		SbmlBaseUnits.add("henry");
		SbmlBaseUnits.add("hertz");
		SbmlBaseUnits.add("item");
		SbmlBaseUnits.add("joule");
		SbmlBaseUnits.add("katal");
		SbmlBaseUnits.add("kelvin");
		SbmlBaseUnits.add("kilogram");
		SbmlBaseUnits.add("litre");
		SbmlBaseUnits.add("lumen");
		SbmlBaseUnits.add("lux");
		SbmlBaseUnits.add("metre");
		SbmlBaseUnits.add("mole");
		SbmlBaseUnits.add("newton");
		SbmlBaseUnits.add("ohm");
		SbmlBaseUnits.add("pascal");
		SbmlBaseUnits.add("radian");
		SbmlBaseUnits.add("second");
		SbmlBaseUnits.add("siemens");
		SbmlBaseUnits.add("sievert");
		SbmlBaseUnits.add("steradian");
		SbmlBaseUnits.add("tesla");
		SbmlBaseUnits.add("volt");
		SbmlBaseUnits.add("watt");
		SbmlBaseUnits.add("weber");	
    }	
    
	private static Map<String, String> ucarToSbml = new HashMap<String, String>(4);  
	static {
		ucarToSbml.put("m","metre");
		ucarToSbml.put("A","ampere");
		ucarToSbml.put("mol","mole");	
		ucarToSbml.put("s","second");	
		ucarToSbml.put("M","1000 mole.m3");
	}
	
	/*
	private static String translate(String ucar) {
		
		String out = ucarToSbml.get(ucar);
		if (out == null) {
			return ucar;
		}
		return out;
	}
	*/
    
	
private static List<Unit> convert(ucar.units.Unit vcUcarUnit,  long level, long version) throws SbmlException {
	ArrayList<Unit> allSbmlUnitsList = new ArrayList<>();
	String ucarString = vcUcarUnit.toString();
	try {
		UnitRepresentation unitRep = UnitRepresentation.parseUcar(ucarString,ucarToSbml);
		final int unitScale = unitRep.getScale();
		List<Fundamental> fUnits = unitRep.getFundamentals();

		//if single unit, add scale to that
		if (fUnits.size() == 1) {
			UnitD unit = new UnitD(fUnits.get(0), level,version);
			unit.setScale(unitScale);
			allSbmlUnitsList.add(unit);
			return allSbmlUnitsList;

		}

		//otherwise, put scale (if present) in dimensonless unit
		if (unitScale != 0) {
			UnitD scaleUnit = new UnitD(level,version);
			scaleUnit.setScale(unitScale);
			allSbmlUnitsList.add(scaleUnit);
		}
		for (Fundamental f: fUnits) {
			UnitD unit = new UnitD(f, level,version);
			allSbmlUnitsList.add(unit);
		}
		return allSbmlUnitsList;
	} catch (SBMLConstructorException ce) {
		throw new SbmlException("unable to convert " + ucarString, ce);
	}
}
/*
 *  convertVCUnitsToSbmlUnits :
 *  --------- !!!! Ignoring OFFSET for UNITS, since SBML L2V2 gets rid of the offset field. !!!! ---------
 */
private static ArrayList<Unit> convertVCUnitsToSbmlUnits(double unitMultiplier, ucar.units.Unit vcUcarUnit, ArrayList<Unit> allSbmlUnitsList, long level, long version) {
	int unitScale = 0;
	if (vcUcarUnit instanceof ucar.units.UnitImpl) {
		ucar.units.UnitImpl unitImpl = (ucar.units.UnitImpl)vcUcarUnit;
		if (unitImpl instanceof ucar.units.DerivedUnitImpl) {
			ucar.units.DerivedUnitImpl baseUnit = (ucar.units.DerivedUnitImpl)unitImpl;
			ucar.units.Factor factors [] = baseUnit.getDimension().getFactors();
			for (int i = 0; i < factors.length; i++) {
				ucar.units.RationalNumber exponent = factors[i].getExponent();
				String baseName  = ((ucar.units.BaseUnit)factors[i].getBase()).getName();
				Unit sbmlUnit = null;
				if (factors.length > 1) {
					// Units override each other's mult/multiplier before getting to the level of derived unit. 
					// To avoid that, add a dimensionless unit. 
					if (i == 0) {
						Unit dimensionlessUnit = new Unit(level, version);
						dimensionlessUnit.setKind(libsbmlConstants.UNIT_KIND_DIMENSIONLESS);
						dimensionlessUnit.setExponent(1);
						dimensionlessUnit.setScale(unitScale);
						dimensionlessUnit.setMultiplier(unitMultiplier);
	 					allSbmlUnitsList.add(dimensionlessUnit);
					}
					sbmlUnit = new Unit(level, version);
					int kind = libsbml.UnitKind_forName(baseName);
					sbmlUnit.setKind(kind);
					sbmlUnit.setExponent(exponent.intValue());
					sbmlUnit.setScale(unitScale);
					sbmlUnit.setMultiplier(1.0);
					allSbmlUnitsList.add(sbmlUnit);
				} else {
					sbmlUnit = new Unit(level, version);
					int kind = libsbml.UnitKind_forName(baseName);
					sbmlUnit.setKind(kind);
					sbmlUnit.setExponent(exponent.intValue());
					sbmlUnit.setScale(unitScale);
					sbmlUnit.setMultiplier(Math.pow(unitMultiplier, exponent.inverse().doubleValue()));
					allSbmlUnitsList.add(sbmlUnit);
				}
			}
			return allSbmlUnitsList;
		} else if (unitImpl instanceof ucar.units.ScaledUnit) {
			ucar.units.ScaledUnit multdUnit = (ucar.units.ScaledUnit)unitImpl;
			unitMultiplier *= multdUnit.getScale();
			if (multdUnit.getUnit() != multdUnit.getDerivedUnit()){
				return convertVCUnitsToSbmlUnits(unitMultiplier, multdUnit.getUnit(), allSbmlUnitsList, level, version);
			}
		} 
		/***** COMMENTED OUT SINCE OFFSET IS NOT GOING TO BE USED FROM SBML L2 V2 ... ****
		  else if (unitImpl instanceof ucar.units.OffsetUnit) {
			ucar.units.OffsetUnit offsetUnit = (ucar.units.OffsetUnit)unitImpl;
			unitOffset += offsetUnit.getOffset();
			if (offsetUnit.getUnit() != offsetUnit.getDerivedUnit()){
				return convertVCUnitsToSbmlUnits(unitMultiplier, offsetUnit.getUnit(), allSbmlUnitsList);
			}
		} */
		if (unitImpl.getDerivedUnit() != vcUcarUnit) {                           //i.e. we have not reached the base unit, yet
			return convertVCUnitsToSbmlUnits(unitMultiplier, unitImpl.getDerivedUnit(), allSbmlUnitsList, level, version);
		} 
	} else {
		System.err.println("Unable to process unit translation for CellML: " + " " + vcUcarUnit.getSymbol());
	}

	return null;
}


public static String getDefaultSBMLUnitSymbol(String builtInName) {
	return SbmlDefaultUnits.get(builtInName);
}


public static UnitDefinition getSBMLUnitDefinition(VCUnitDefinition vcUnitDefn, long level, long version, VCUnitSystem vcUnitSystem) throws SbmlException {
	UnitDefinition sbmlUnitDefn = null;
	String sbmlUnitSymbol = TokenMangler.mangleToSName(vcUnitDefn.getSymbol());

	// If VC unit is DIMENSIONLESS ...
	if (vcUnitDefn.isTBD()) {
		throw new RuntimeException("TBD unit has no SBML equivalent");
	} else if (vcUnitDefn.isCompatible(vcUnitSystem.getInstance_DIMENSIONLESS())) {
		double multiplier = 1.0;
		multiplier = vcUnitDefn.convertTo(multiplier, vcUnitSystem.getInstance_DIMENSIONLESS());
		sbmlUnitDefn = new UnitDefinition(level, version);
		sbmlUnitDefn.setId(TokenMangler.mangleToSName(TokenMangler.mangleToSName(vcUnitDefn.getSymbol())));
		Unit dimensionlessUnit = new UnitD(level, version);
		dimensionlessUnit.setMultiplier(multiplier);
		sbmlUnitDefn.addUnit(dimensionlessUnit);
	} else {
		// Translate the VCUnitDef into libSBML UnitDef : convert the units of VCUnitDef into libSBML units and add them to sbmlUnitDefn
		
		sbmlUnitDefn = new UnitDefinition(level, version);
		sbmlUnitDefn.setId(TokenMangler.mangleToSName(TokenMangler.mangleToSName(sbmlUnitSymbol))); 
		ucar.units.Unit vcUcarUnit = vcUnitDefn.getUcarUnit();
		//ArrayList<Unit> sbmlUnitsList = convertVCUnitsToSbmlUnits(1.0, vcUcarUnit, new ArrayList<Unit>(), level, version);
		List<Unit> sbmlUnitsList = convert(vcUcarUnit, level, version);

		for (int i = 0; i < sbmlUnitsList.size(); i++){
			Unit sbmlUnit = sbmlUnitsList.get(i);
			sbmlUnitDefn.addUnit(sbmlUnit);
		}
	}

	return sbmlUnitDefn;
}


	/*
	 *  getVCUnit : 
	 */
	private static VCUnitDefinition getVCUnit(org.sbml.libsbml.Unit unit, VCUnitSystem vcUnitSystem) {
		// Get the attributes of the unit 'element', 'kind', 'multiplier', 'scale', 'offset', etc.
		String unitKind = org.sbml.libsbml.libsbml.UnitKind_toString(unit.getKind());
		int unitExponent = unit.getExponent();
		int unitScale = unit.getScale();
		double unitMultiplier = unit.getMultiplier();
		String vcScaleStr = Double.toString(Math.pow((unitMultiplier*Math.pow(10, unitScale)), unitExponent));

		VCUnitDefinition vcUnit = null;

		// convert the sbmlUnit into a vcell unit with the appropriate multiplier, scale, exponent, offset, etc ..
		if (unit.isDimensionless()) {        //'dimensionless' can be part of a bigger unit definition     
			vcUnit = vcUnitSystem.getInstance(vcScaleStr);
			return vcUnit;
		} else {
			if (unit.isItem()) {
				System.out.println("SBML 'item' unit found, interpreted as 'molecule'");
				vcUnit = vcUnitSystem.getInstance(vcScaleStr + " molecules" + unitExponent);
			} else {
				if (unit.isMetre()){
					unitKind = "m";
					if (unitMultiplier==1){
						if (unitScale==-3){
							return vcUnitSystem.getInstance("mm" + unitExponent);
						}else if (unitScale==-6){
							return vcUnitSystem.getInstance("um" + unitExponent);
						}else if (unitScale==-9){
							return vcUnitSystem.getInstance("nm" + unitExponent);
						}
					}
				}
				if (unit.isSecond()){
					unitKind = "s";
					if (unitMultiplier==1){
						if (unitScale==-3){
							return vcUnitSystem.getInstance("ms" + unitExponent);
						}else if (unitScale==-6){
							return vcUnitSystem.getInstance("us" + unitExponent);
						}
					}else if (unitMultiplier==60 && unitScale==0){
						return vcUnitSystem.getInstance("minute");
					}else if (unitMultiplier==3600 && unitScale==0){
						return vcUnitSystem.getInstance("hour");
					}else if (unitMultiplier==86400 && unitScale==0){
						return vcUnitSystem.getInstance("day");
					}
				}
				if (unit.isMole()){
					unitKind = "mol";
					if (unitMultiplier==1){
						if (unitScale==0){
							return vcUnitSystem.getInstance("mol" + unitExponent);
						}
						if (unitScale==-3){
							return vcUnitSystem.getInstance("mmol" + unitExponent);
						}
						if (unitScale==-6){
							return vcUnitSystem.getInstance("umol" + unitExponent);
						}
						if (unitScale==-9){
							return vcUnitSystem.getInstance("nmol" + unitExponent);
						}
						if (unitScale==-12){
							return vcUnitSystem.getInstance("pmol" + unitExponent);
						}
					}
				}
				if (unit.isItem()){
					unitKind = "molecules";
				}
				
				vcUnit = vcUnitSystem.getInstance(vcScaleStr + " " + unitKind + unitExponent);
			}
		}
		return vcUnit;
	}


public static VCUnitDefinition getVCUnitDefinition(org.sbml.libsbml.UnitDefinition sbmlUnitDefn, VCUnitSystem vcUnitSystem) {
	// Each SBML UnitDefinition contains a list of Units, the total unit (VC unit) as represented by
	// an SBML UnitDefinition is the product of the list of units it contains.
	VCUnitDefinition vcUnitDefn = null;
	org.sbml.libsbml.ListOf listofUnits = sbmlUnitDefn.getListOfUnits();
	for (int j = 0; j < sbmlUnitDefn.getNumUnits(); j++) {
		org.sbml.libsbml.Unit sbmlUnit = (org.sbml.libsbml.Unit)listofUnits.get(j);
		VCUnitDefinition vcUnit = getVCUnit(sbmlUnit, vcUnitSystem);
		if (vcUnitDefn == null) {
			vcUnitDefn = vcUnit;
		} else {
			vcUnitDefn = vcUnitDefn.multiplyBy(vcUnit);        //?
		}
	}
	return vcUnitDefn;
}

public static boolean isSbmlBaseUnit(String symbol) {
	return SbmlBaseUnits.contains(symbol);
}

/**
 * convert sbml string into XML (SBML) document, add unit definitions from above selected unitSystem and re-convert to string.
 * @param sbmlStr
 * @return
 * @throws SbmlException 
 */
public static String addUnitDefinitionsToSbmlModel(String sbmlStr, ModelUnitSystem vcModelUnitSystem) throws SbmlException {
	if (sbmlStr == null || sbmlStr.length() == 0) {
		throw new RuntimeException("SBMl string is empty, cannot add unit definitions to SBML model.");
	}
	NativeLib.SBML.load( );

	// create a libsbml (sbml) model object, create sbml unitDefinitions for the units above, add to model.
	SBMLReader sbmlReader = new SBMLReader();
	SBMLDocument sbmlDocument = sbmlReader.readSBMLFromString(sbmlStr);
		
	Model sbmlModel = sbmlDocument.getModel();
	long sbmlLevel = sbmlModel.getLevel();
	long sbmlVersion = sbmlModel.getVersion();
	
	// Define SUBSTANCE
	UnitDefinition subsUnitDefn = getSBMLUnitDefinition(vcModelUnitSystem.getVolumeSubstanceUnit(), sbmlLevel, sbmlVersion, vcModelUnitSystem);
	subsUnitDefn.setId(SUBSTANCE);
	sbmlModel.addUnitDefinition(subsUnitDefn);

	// Define VOLUME
	UnitDefinition volUnitDefn = getSBMLUnitDefinition(vcModelUnitSystem.getVolumeUnit(), sbmlLevel, sbmlVersion, vcModelUnitSystem);
	volUnitDefn.setId(VOLUME);
	sbmlModel.addUnitDefinition(volUnitDefn);

	// Define AREA
	UnitDefinition areaUnitDefn = getSBMLUnitDefinition(vcModelUnitSystem.getAreaUnit(), sbmlLevel, sbmlVersion, vcModelUnitSystem);
	areaUnitDefn.setId(AREA);
	sbmlModel.addUnitDefinition(areaUnitDefn);

	// Define LENGTH
	UnitDefinition lengthUnitDefn = getSBMLUnitDefinition(vcModelUnitSystem.getLengthUnit(), sbmlLevel, sbmlVersion, vcModelUnitSystem);
	lengthUnitDefn.setId(LENGTH);
	sbmlModel.addUnitDefinition(lengthUnitDefn);

	// Define TIME
	UnitDefinition timeUnitDefn = getSBMLUnitDefinition(vcModelUnitSystem.getTimeUnit(), sbmlLevel, sbmlVersion, vcModelUnitSystem);
	timeUnitDefn.setId(TIME);
	sbmlModel.addUnitDefinition(timeUnitDefn);
	
	// convert sbml model to string and return
	SBMLWriter sbmlWriter = new SBMLWriter();
	String modifiedSbmlStr = sbmlWriter.writeToString(sbmlDocument);

	// cleanup
	sbmlModel.delete();
	sbmlDocument.delete();
	sbmlReader.delete();
	sbmlWriter.delete();	

	return modifiedSbmlStr;
	
}

}
