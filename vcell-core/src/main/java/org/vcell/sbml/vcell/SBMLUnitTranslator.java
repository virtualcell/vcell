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

import org.sbml.jsbml.Unit;
import org.sbml.jsbml.Unit.Kind;
import org.sbml.jsbml.UnitDefinition;
import org.vcell.sbml.SbmlException;
import org.vcell.util.TokenMangler;

import cbit.vcell.units.VCUnitDefinition;
import cbit.vcell.units.VCUnitSystem;

/**
 * Insert the type's description here.
 * Creation date: (2/28/2006 5:22:58 PM)
 * @author: Anuradha Lakshminarayana
 */
public class SBMLUnitTranslator {
	
/**
 *
 *   BaseUnit definitions copied from ucar.units_vcell.SI (as modified for Virtual Cell and SBML ... including item and molecules)
 *   
 *   (PRIMARY BASE SI UNITS)
 *   AMPERE = 		bu("ampere",    "A",   BaseQuantity.ELECTRIC_CURRENT)
 *   CANDELA = 		bu("candela",   "cd",  BaseQuantity.LUMINOUS_INTENSITY)
 *   KELVIN = 		bu("kelvin",    "K",   BaseQuantity.THERMODYNAMIC_TEMPERATURE)
 *   KILOGRAM = 	bu("kilogram",  "kg",  BaseQuantity.MASS)
 *   METER = 		bu("metre",     "m",   BaseQuantity.LENGTH)	// NIST
 *   METRE = 		bu("metre",     "m",   BaseQuantity.LENGTH)	// ISO
 *   MOLE = 		bu("mole",      "mol", BaseQuantity.AMOUNT_OF_SUBSTANCE)
 *   SECOND = 		bu("second",    "s",   BaseQuantity.TIME)
 *   
 *   (SUPPLEMENTARY BASE SI UNITS ... DIMENSIONLESS DERIVED UNITS) ... note that BaseUnit
 *   RADIAN = 		bu("radian",    "rad", BaseQuantity.PLANE_ANGLE)
 *   STERADIAN = 	bu("steradian", "sr",  BaseQuantity.SOLID_ANGLE)
 *   
 *   (VCELL/SBML introduced base SI unit for items/molecules)
 *   ITEM =			bu("item",      "item", BaseQuantity.ITEM)
 */


/**
 * @param unitMultiplier
 * @param vcUcarUnit
 * @param allSbmlUnitsList
 * @param level
 * @param version
 * @return
 */
/*
 *  convertVCUnitsToSbmlUnits :
 *  --------- !!!! Ignoring OFFSET for UNITS, since SBML L2V2 gets rid of the offset field. !!!! ---------
 */
private static ArrayList<Unit> convertVCUnitsToSbmlUnits_NOT_USED(double unitMultiplier, ucar.units_vcell.Unit vcUcarUnit, ArrayList<Unit> allSbmlUnitsList, int level, int version) {
	int unitScale = 0;
	if (vcUcarUnit instanceof ucar.units_vcell.UnitImpl) {
		ucar.units_vcell.UnitImpl unitImpl = (ucar.units_vcell.UnitImpl)vcUcarUnit;
		if (unitImpl instanceof ucar.units_vcell.DerivedUnitImpl) {
			ucar.units_vcell.DerivedUnitImpl baseUnit = (ucar.units_vcell.DerivedUnitImpl)unitImpl;
			ucar.units_vcell.Factor factors [] = baseUnit.getDimension().getFactors();
			for (int i = 0; i < factors.length; i++) {
				ucar.units_vcell.RationalNumber exponent = factors[i].getExponent();
				String baseName  = ((ucar.units_vcell.BaseUnit)factors[i].getBase()).getName();
				Unit sbmlUnit = null;
				if (factors.length > 1) {
					// Units override each other's mult/multiplier before getting to the level of derived unit. 
					// To avoid that, add a dimensionless unit. 
					if (i == 0) {
						Unit dimensionlessUnit = new Unit(level, version);
						dimensionlessUnit.setKind(Kind.DIMENSIONLESS);
						dimensionlessUnit.setExponent(1);
						dimensionlessUnit.setScale(unitScale);
						dimensionlessUnit.setMultiplier(unitMultiplier);
	 					allSbmlUnitsList.add(dimensionlessUnit);
					}
					sbmlUnit = new Unit(level, version);
					Kind kind = Kind.valueOf(baseName);
					sbmlUnit.setKind(kind);
					sbmlUnit.setExponent(exponent.intValue());
					sbmlUnit.setScale(unitScale);
					sbmlUnit.setMultiplier(1.0);
					allSbmlUnitsList.add(sbmlUnit);
				} else {
					sbmlUnit = new Unit(level, version);
					Kind kind = Kind.valueOf(baseName);
					sbmlUnit.setKind(kind);
					sbmlUnit.setExponent(exponent.intValue());
					sbmlUnit.setScale(unitScale);
					sbmlUnit.setMultiplier(Math.pow(unitMultiplier, exponent.inverse().doubleValue()));
					allSbmlUnitsList.add(sbmlUnit);
				}
			}
			return allSbmlUnitsList;
		} else if (unitImpl instanceof ucar.units_vcell.ScaledUnit) {
			ucar.units_vcell.ScaledUnit multdUnit = (ucar.units_vcell.ScaledUnit)unitImpl;
			unitMultiplier *= multdUnit.getScale();
			if (multdUnit.getUnit() != multdUnit.getDerivedUnit()){
				return convertVCUnitsToSbmlUnits_NOT_USED(unitMultiplier, multdUnit.getUnit(), allSbmlUnitsList, level, version);
			}
		} 
		/***** COMMENTED OUT SINCE OFFSET IS NOT GOING TO BE USED FROM SBML L2 V2 ... ****
		  else if (unitImpl instanceof ucar.units_vcell.OffsetUnit) {
			ucar.units_vcell.OffsetUnit offsetUnit = (ucar.units_vcell.OffsetUnit)unitImpl;
			unitOffset += offsetUnit.getOffset();
			if (offsetUnit.getUnit() != offsetUnit.getDerivedUnit()){
				return convertVCUnitsToSbmlUnits(unitMultiplier, offsetUnit.getUnit(), allSbmlUnitsList);
			}
		} */
		if (unitImpl.getDerivedUnit() != vcUcarUnit) {                           //i.e. we have not reached the base unit, yet
			return convertVCUnitsToSbmlUnits_NOT_USED(unitMultiplier, unitImpl.getDerivedUnit(), allSbmlUnitsList, level, version);
		} 
	} else {
		System.err.println("Unable to process unit translation: " + " " + vcUcarUnit.getSymbol());
	}

	throw new RuntimeException("unexpected vcell unit during transformation to sbml units: "+vcUcarUnit);
//	return null;
}

public static boolean isDouble(String s) {
	try {
		Double.parseDouble(s);
		return true;
	} catch(NumberFormatException e) {
		return false;
	}
}

public static UnitDefinition getSBMLUnitDefinition(VCUnitDefinition vcUnitDefn, int level, int version, VCUnitSystem vcUnitSystem) throws SbmlException {
	final UnitDefinition sbmlUnitDefn = new UnitDefinition(level,version);
	if (vcUnitDefn.isCompatible(vcUnitSystem.getInstance_DIMENSIONLESS())){
		double multiplier = 1.0;
		String symbol = vcUnitDefn.getSymbol();
		if(isDouble(symbol)) {
			multiplier = Double.parseDouble(symbol);
		}
		sbmlUnitDefn.addUnit(new Unit(multiplier,0,Kind.DIMENSIONLESS,1.0,level,version));
		return sbmlUnitDefn;
	}
	String vcSymbol = vcUnitDefn.getSymbol();
	double overallMultiplier = 1.0;
	if (vcSymbol.contains(" ")){
		String[] unitParts = vcSymbol.split(" ");
		overallMultiplier = Double.parseDouble(unitParts[0]);
		vcSymbol = unitParts[1];
	}
//	String sbmlUnitSymbol = TokenMangler.mangleToSName(vcSymbol);
	String[] symbols = vcSymbol.split("\\.");
	if (symbols.length==0){
		symbols = new String[] { vcSymbol };
	}
	for (int i=0;i<symbols.length;i++){
		double multiplier = 1.0;
		if (i==0){
			multiplier *= overallMultiplier;
		}
		double exponent = 1.0;
		int scale = 0;
		String symbol = symbols[i];
//		if (symbol.contains(" ")){
//			String[] unitParts = symbol.split(" ");
//			multiplier = Double.parseDouble(unitParts[0]);
//			symbol = unitParts[1];
//		}
		if (symbol.contains("-")){
			String[] symbolAndExp = symbol.split("\\-");
			symbol = symbolAndExp[0];
			exponent = -1 * Integer.parseInt(symbolAndExp[1]);
		}else if (Character.isDigit(symbol.charAt(symbol.length()-1))){
			exponent = Integer.parseInt(symbol.substring(symbol.length()-1));
			symbol = symbol.substring(0, symbol.length()-1);
		}
		VCUnitDefinition vcUnit = vcUnitSystem.getInstance(symbol);
		boolean bFoundMatch = false;
		// check sbml builtin units (base SI and supported derived units) first.
		for (Kind kind : Kind.values()){
			String kindSymbol = kind.getSymbol();
			if (kind==Kind.AVOGADRO || kind==Kind.CELSIUS || kind==Kind.INVALID || kind==Kind.BECQUEREL || kind==Kind.HERTZ){
				continue;
			}
			if (kind==Kind.OHM){
				kindSymbol = "Ohm";
			}
			if (kind==Kind.ITEM){
				kindSymbol = "molecules";
			}
			if(kind == Kind.METER) {
				kind = Kind.METRE;
			}
			if(kind == Kind.LITER) {
				kind = Kind.LITRE;
			}
			
			VCUnitDefinition kindVcUnit = vcUnitSystem.getInstance(kindSymbol);
			if (kindVcUnit.isCompatible(vcUnit)){
				if (kindVcUnit.isEquivalent(vcUnit)){
					sbmlUnitDefn.addUnit(new Unit(multiplier, scale, kind, exponent, level, version));
				}else{
					double factor = vcUnit.convertTo(1.0, kindVcUnit);
					double logFactor = Math.log10(factor);
					if (logFactor == (int)logFactor){
						scale = (int)logFactor;
					}else{
						scale = 0;
						multiplier = multiplier*factor;
					}
					Unit sbmlUnit = new Unit(multiplier, scale, kind, exponent, level, version);
					sbmlUnitDefn.addUnit(sbmlUnit);
					System.err.println("kind = "+kind.name()+" is equivalent to vcUnit = "+vcUnit.getSymbol()+",  SBML unit is "+sbmlUnit);
				}
				bFoundMatch = true;
				break;
			}
		}
		if (!bFoundMatch){
			// check for molar, kind of crazy that this one is missing.
			VCUnitDefinition kindVcUnit = vcUnitSystem.getInstance("molar");
			if (kindVcUnit.isCompatible(vcUnit)){
				if (kindVcUnit.isEquivalent(vcUnit)){
					sbmlUnitDefn.addUnit(new Unit(multiplier, scale, Kind.MOLE, exponent, level, version));
					sbmlUnitDefn.addUnit(new Unit(1, 0, Kind.LITRE, -exponent, level, version));
				}else{
					double factor = vcUnit.convertTo(1.0, kindVcUnit);
					double logFactor = Math.log10(factor);
					if (logFactor == (int)logFactor){
						scale = (int)logFactor;
					}else{
						scale = 0;
						multiplier = multiplier*factor;
					}
					sbmlUnitDefn.addUnit(new Unit(multiplier, scale, Kind.MOLE, exponent, level, version));
					sbmlUnitDefn.addUnit(new Unit(1, 0, Kind.LITRE, -exponent, level, version));
					System.err.println("matched to liter ... had to create a replacement for molar, vcUnit = "+vcUnit.getSymbol()+",  SBML unit is "+sbmlUnitDefn);
				}
				bFoundMatch = true;
			}
		}
		if (!bFoundMatch){
			throw new RuntimeException("didn't find a match for vcUnit "+vcUnit.getSymbol());
			//System.out.println("Still didn't find a match for vcUnit "+vcUnit.getSymbol());
		}
//		ucar.units_vcell.Unit ucarUnit = vcUnit.getUcarUnit();
//		if (ucarUnit instanceof ScaledUnit){
//			ScaledUnit scaledUnit = (ScaledUnit)ucarUnit;
//			double parsedScale = scaledUnit.getScale();
//			double logScale = Math.log10(parsedScale);
//			if (logScale == (int)logScale){
//				scale = (int)logScale;
//			}else{
//				scale = 0;
//				multiplier = multiplier*parsedScale;
//			}
//			ucar.units_vcell.Unit insideUnit = scaledUnit.getUnit();
//			boolean bFoundMatch = false;
//			for (Kind kind : Kind.values()){
//				String kindSymbol = kind.getSymbol();
//				if (kind==Kind.AVOGADRO){
//					continue;
//				}
//				if (kind==Kind.CELSIUS){
//					continue;
//				}
//				if (kind==Kind.INVALID){
//					continue;
//				}
//				if (kind==Kind.OHM){
//					kindSymbol = "Ohm";
//				}
//				String sym = insideUnit.toString();
//				if (vcUnitSystem.getInstance(kindSymbol).isEquivalent(vcUnitSystem.getInstance(sym))){
//					sbmlUnitDefn.addUnit(new Unit(multiplier, scale, kind, exponent, 3, 1));
//					bFoundMatch = true;
//					break;
//				}
//			}
//			if (!bFoundMatch){
//				System.err.println("couldn't find an SBML unit for vcUnit "+vcUnit.getSymbol());
//				System.err.println("couldn't find an SBML unit for vcUnit "+vcUnit.getSymbol());
//			}
//		}
//		System.err.println("vcUnit is "+symbols[i]+",  ucarUnit is "+ucarUnit.getSymbol());
	}
	sbmlUnitDefn.setId(TokenMangler.mangleToSName(vcSymbol));
	return sbmlUnitDefn;
//
//	// If VC unit is DIMENSIONLESS ...
//	if (vcUnitDefn.isTBD()) {
//		throw new RuntimeException("TBD unit has no SBML equivalent");
//	} else if (vcUnitDefn.isCompatible(vcUnitSystem.getInstance_DIMENSIONLESS())) {
//		double multiplier = 1.0;
//		multiplier = vcUnitDefn.convertTo(multiplier, vcUnitSystem.getInstance_DIMENSIONLESS());
//		sbmlUnitDefn = new UnitDefinition(level, version);
//		sbmlUnitDefn.setId(TokenMangler.mangleToSName(TokenMangler.mangleToSName(vcSymbol)));
//		Unit dimensionlessUnit = new UnitD(level, version);
//		dimensionlessUnit.setMultiplier(multiplier);
//		sbmlUnitDefn.addUnit(dimensionlessUnit);
//	} else {
//		// Translate the VCUnitDef into libSBML UnitDef : convert the units of VCUnitDef into libSBML units and add them to sbmlUnitDefn
//		
//		sbmlUnitDefn = new UnitDefinition(level, version);
//		sbmlUnitDefn.setId(TokenMangler.mangleToSName(TokenMangler.mangleToSName(sbmlUnitSymbol))); 
//		ucar.units_vcell.Unit vcUcarUnit = vcUnitDefn.getUcarUnit();
//		//ArrayList<Unit> sbmlUnitsList = convertVCUnitsToSbmlUnits(1.0, vcUcarUnit, new ArrayList<Unit>(), level, version);
//		List<Unit> sbmlUnitsList = convert(vcUcarUnit, level, version);
//
//		for (int i = 0; i < sbmlUnitsList.size(); i++){
//			Unit sbmlUnit = sbmlUnitsList.get(i);
//			sbmlUnitDefn.addUnit(sbmlUnit);
//		}
//	}
//
//	return sbmlUnitDefn;
}


	/*
	 *  getVCUnit : 
	 */
	private static VCUnitDefinition getVCUnit(org.sbml.jsbml.Unit unit, VCUnitSystem vcUnitSystem) {
		// Get the attributes of the unit 'element', 'kind', 'multiplier', 'scale', 'offset', etc.
		Kind unitKind = unit.getKind();

		if (unit.getExponent() != (int)unit.getExponent()){
			throw new RuntimeException("non-integer units not supported for VCell/SBML unit translation: sbml unit = "+unit.toString());
		}
		int unitExponent = (int)unit.getExponent();
		int unitScale = unit.getScale();
		double unitMultiplier = unit.getMultiplier();
		
		
		String unitKindSymbol = unitKind.getName();
		if (unit.getKind().getSymbol()!=null && "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".contains(unit.getKind().getSymbol().substring(0,1))){
			unitKindSymbol = unitKind.getSymbol();
		}
		if (unit.isSecond() && ((unitMultiplier==60 && unitScale==0) || (unitMultiplier==6 && unitScale==1))){
			unitKindSymbol = "min";
			unitMultiplier = 1;
			unitScale = 0;
		}
		if (unit.isSecond() && ((unitMultiplier==3600 && unitScale==0) || (unitMultiplier==3.6 && unitScale==3))){
			unitKindSymbol = "hour";
			unitMultiplier = 1;
			unitScale = 0;
		}
		if (unit.isSecond() && ((unitMultiplier==86400 && unitScale==0) || (unitMultiplier==86.4 && unitScale==3) || (unitMultiplier==8.64 && unitScale==4))){
			unitKindSymbol = "day";
			unitMultiplier = 1;
			unitScale = 0;
		}
		if (unit.isItem()){
//			System.out.println("SBML 'item' unit found, interpreted as 'molecule'");
			unitKindSymbol = "molecules";
		}

		// convert the sbmlUnit into a vcell unit with the appropriate multiplier, scale, exponent, offset, etc ..
		if (unit.getKind() == Kind.DIMENSIONLESS) {  //'dimensionless' can be part of a bigger unit definition     
			String vcScaleStr = Double.toString(Math.pow((unitMultiplier*Math.pow(10, unitScale)), unitExponent));
			VCUnitDefinition vcUnit = vcUnitSystem.getInstance(vcScaleStr);
			return vcUnit;
		} else {
			String prefix = "";
			switch (unitScale){
			case -3:{
				prefix = "m";
				unitScale = 0;
				break;
			}
			case -6:{
				prefix = "u";
				unitScale = 0;
				break;
			}
			case -9:{
				prefix = "n";
				unitScale = 0;
				break;
			}
			case -12:{
				prefix = "p";
				unitScale = 0;
				break;
			}
			}
			String vcScaleStr = Double.toString(unitMultiplier*Math.pow((Math.pow(10, unitScale)), unitExponent));
			String symbol = vcScaleStr + " " + prefix+unitKindSymbol + unitExponent;
//			System.out.println("symbol: " + symbol);
			VCUnitDefinition vcUnit = vcUnitSystem.getInstance(symbol);
			return vcUnit;
		}
	}


public static VCUnitDefinition getVCUnitDefinition(org.sbml.jsbml.UnitDefinition sbmlUnitDefn, VCUnitSystem vcUnitSystem) {
	// Each SBML UnitDefinition contains a list of Units, the total unit (VC unit) as represented by
	// an SBML UnitDefinition is the product of the list of units it contains.
	VCUnitDefinition vcUnitDefn = null;
	for (Unit sbmlUnit : sbmlUnitDefn.getListOfUnits()){
		VCUnitDefinition vcUnit = getVCUnit(sbmlUnit, vcUnitSystem);
		if (vcUnitDefn == null) {
			vcUnitDefn = vcUnit;
		} else {
			vcUnitDefn = vcUnitDefn.multiplyBy(vcUnit);
		}
	}
	String originalSymbol = vcUnitDefn.getSymbol();
	String symbol = "."+originalSymbol+".";
	final String[] moleSymbols = new String[] {  "umol", "nmol", "mol", "mmol", "pmol" };
	final String[] molarSymbols = new String[] { "uM",   "nM",   "M",   "mM",   "pM" };
	for (int i=0;i<moleSymbols.length;i++){
		String mol = moleSymbols[i];
		String M = molarSymbols[i];
		if (symbol.contains(mol)){
			symbol = symbol.replace("."+mol+".l-1.", "."+M+".");
			symbol = symbol.replace(".l-1."+mol+".", "."+M+".");
			symbol = symbol.replace("."+mol+"-1.l.", "."+M+"-1.");
			symbol = symbol.replace(".l."+mol+"-1.", "."+M+"-1.");
			symbol = symbol.replace("."+mol+"2.l-2.", "."+M+"2.");
			symbol = symbol.replace(".l-2."+mol+"2.", "."+M+"2.");
			symbol = symbol.replace("."+mol+"-2.l2.", "."+M+"-2.");
			symbol = symbol.replace(".l2."+mol+"-2.", "."+M+"-2.");
		}
	}
	symbol = symbol.substring(1,symbol.length()-1);
	if (!symbol.equals(vcUnitDefn.getSymbol())){
		System.err.println("new symbol is "+symbol+",   old symbol is "+vcUnitDefn.getSymbol());
		VCUnitDefinition new_vcUnitDefn = vcUnitSystem.getInstance(symbol);
		if (!new_vcUnitDefn.isEquivalent(vcUnitDefn)){
			throw new RuntimeException("failed to simplify unit "+vcUnitDefn.getSymbol()+", created wrong symbol "+symbol);
		}
		return new_vcUnitDefn;
	}else{
		return vcUnitDefn;
	}
}

///**
// * convert sbml string into XML (SBML) document, add unit definitions from above selected unitSystem and re-convert to string.
// * @param sbmlStr
// * @return
// * @throws SbmlException 
// * @throws XMLStreamException 
// * @throws SBMLException 
// */
//public static String addUnitDefinitionsToSbmlModel(String sbmlStr, ModelUnitSystem vcModelUnitSystem) throws SbmlException, SBMLException, XMLStreamException {
//	if (sbmlStr == null || sbmlStr.length() == 0) {
//		throw new RuntimeException("SBMl string is empty, cannot add unit definitions to SBML model.");
//	}
//	NativeLib.SBML.load( );
//
//	// create a libsbml (sbml) model object, create sbml unitDefinitions for the units above, add to model.
//	SBMLReader sbmlReader = new SBMLReader();
//	SBMLDocument sbmlDocument = sbmlReader.readSBMLFromString(sbmlStr);
//		
//	Model sbmlModel = sbmlDocument.getModel();
//	int sbmlLevel = sbmlModel.getLevel();
//	int sbmlVersion = sbmlModel.getVersion();
//	
//	// Define SUBSTANCE
//	UnitDefinition subsUnitDefn = getSBMLUnitDefinition(vcModelUnitSystem.getVolumeSubstanceUnit(), sbmlLevel, sbmlVersion, vcModelUnitSystem);
//	subsUnitDefn.setId(SUBSTANCE);
//	sbmlModel.addUnitDefinition(subsUnitDefn);
//
//	// Define VOLUME
//	UnitDefinition volUnitDefn = getSBMLUnitDefinition(vcModelUnitSystem.getVolumeUnit(), sbmlLevel, sbmlVersion, vcModelUnitSystem);
//	volUnitDefn.setId(VOLUME);
//	sbmlModel.addUnitDefinition(volUnitDefn);
//
//	// Define AREA
//	UnitDefinition areaUnitDefn = getSBMLUnitDefinition(vcModelUnitSystem.getAreaUnit(), sbmlLevel, sbmlVersion, vcModelUnitSystem);
//	areaUnitDefn.setId(AREA);
//	sbmlModel.addUnitDefinition(areaUnitDefn);
//
//	// Define LENGTH
//	UnitDefinition lengthUnitDefn = getSBMLUnitDefinition(vcModelUnitSystem.getLengthUnit(), sbmlLevel, sbmlVersion, vcModelUnitSystem);
//	lengthUnitDefn.setId(LENGTH);
//	sbmlModel.addUnitDefinition(lengthUnitDefn);
//
//	// Define TIME
//	UnitDefinition timeUnitDefn = getSBMLUnitDefinition(vcModelUnitSystem.getTimeUnit(), sbmlLevel, sbmlVersion, vcModelUnitSystem);
//	timeUnitDefn.setId(TIME);
//	sbmlModel.addUnitDefinition(timeUnitDefn);
//	
//	// convert sbml model to string and return
//	SBMLWriter sbmlWriter = new SBMLWriter();
//	String modifiedSbmlStr = sbmlWriter.writeSBMLToString(sbmlDocument);
//
//	return modifiedSbmlStr;
//	
//}

}
