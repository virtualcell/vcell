package org.vcell.sbml;

import cbit.vcell.units.VCUnitDefinition;

/**
 * Insert the type's description here.
 * Creation date: (2/28/2006 5:22:58 PM)
 * @author: Anuradha Lakshminarayana
 */
public class SBMLUnitTranslator {

	// special units
	public static final String DIMENSIONLESS = "dimensionless";
	public static final String ITEM = "item";
	//default built-in units
	public static final String SUBSTANCE = "substance";
	public static final String VOLUME = "volume";
	public static final String AREA = "area";
	public static final String LENGTH = "length";
	public static final String TIME = "time";
	
	private static java.util.TreeMap SbmlDefaultUnits = new java.util.TreeMap();  
	static {            
		SbmlDefaultUnits.put("substance", VCUnitDefinition.getInstance("mole"));
		SbmlDefaultUnits.put("volume", VCUnitDefinition.UNIT_L);
		SbmlDefaultUnits.put("area", VCUnitDefinition.getInstance("metre2"));
		SbmlDefaultUnits.put("length", cbit.vcell.units.VCUnitDefinition.getInstance("metre"));
		SbmlDefaultUnits.put("time", VCUnitDefinition.UNIT_s);
	}

	private static java.util.ArrayList SbmlBaseUnits = new java.util.ArrayList();
    static {
		SbmlBaseUnits.add("ampere");
		SbmlBaseUnits.add("becquerel");
		SbmlBaseUnits.add("candela");
		SbmlBaseUnits.add("Celsius");
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
	
/*
 *  convertVCUnitsToSbmlUnits :
 *  --------- !!!! Ignoring OFFSET for UNITS, since SBML L2V2 gets rid of the offset field. !!!! ---------
 */
private static java.util.ArrayList convertVCUnitsToSbmlUnits(double unitMultiplier, ucar.units.Unit vcUcarUnit, java.util.ArrayList allSbmlUnitsList) {
	int unitScale = 0;
	if (vcUcarUnit instanceof ucar.units.UnitImpl) {
		ucar.units.UnitImpl unitImpl = (ucar.units.UnitImpl)vcUcarUnit;
		if (unitImpl instanceof ucar.units.DerivedUnitImpl) {
			ucar.units.DerivedUnitImpl baseUnit = (ucar.units.DerivedUnitImpl)unitImpl;
			ucar.units.Factor factors [] = baseUnit.getDimension().getFactors();
			for (int i = 0; i < factors.length; i++) {
				ucar.units.RationalNumber exponent = factors[i].getExponent();
				String baseName  = ((ucar.units.BaseUnit)factors[i].getBase()).getName();
				org.sbml.libsbml.Unit sbmlUnit = null;
				if (factors.length > 1) {
					// Units override each other's mult/multiplier before getting to the level of derived unit. 
					// To avoid that, add a dimensionless unit. 
					if (i == 0) {
						org.sbml.libsbml.Unit dimensionlessUnit = new org.sbml.libsbml.Unit(SBMLUnitTranslator.DIMENSIONLESS, 1, unitScale, unitMultiplier);
	 					allSbmlUnitsList.add(dimensionlessUnit);
					}
					sbmlUnit = new org.sbml.libsbml.Unit(baseName, exponent.intValue(), unitScale, 1.0);
					allSbmlUnitsList.add(sbmlUnit);
				} else {
					sbmlUnit = new org.sbml.libsbml.Unit(baseName, exponent.intValue(), unitScale, unitMultiplier);
					allSbmlUnitsList.add(sbmlUnit);
				}
			}
			return allSbmlUnitsList;
		} else if (unitImpl instanceof ucar.units.ScaledUnit) {
			ucar.units.ScaledUnit multdUnit = (ucar.units.ScaledUnit)unitImpl;
			unitMultiplier *= multdUnit.getScale();
			if (multdUnit.getUnit() != multdUnit.getDerivedUnit()){
				return convertVCUnitsToSbmlUnits(unitMultiplier, multdUnit.getUnit(), allSbmlUnitsList);
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
			return convertVCUnitsToSbmlUnits(unitMultiplier, unitImpl.getDerivedUnit(), allSbmlUnitsList);
		} 
	} else {
		System.err.println("Unable to process unit translation for CellML: " + " " + vcUcarUnit.getSymbol());
	}

	return null;
}


public static VCUnitDefinition getDefaultSBMLUnit(String builtInName) {
	return (VCUnitDefinition)SbmlDefaultUnits.get(builtInName);
}


public static org.sbml.libsbml.UnitDefinition getSBMLUnitDefinition(VCUnitDefinition vcUnitDefn) {
	org.sbml.libsbml.UnitDefinition sbmlUnitDefn = null;
	String sbmlUnitSymbol = cbit.util.TokenMangler.mangleToSName(vcUnitDefn.getSymbol());

	// If VC unit is DIMENSIONLESS ...
	if (vcUnitDefn.isTBD()) {
		throw new RuntimeException("TBD unit has no SBML equivalent");
	} else if (vcUnitDefn.isCompatible(VCUnitDefinition.UNIT_DIMENSIONLESS)) {
		double scale = 1.0;
		scale = vcUnitDefn.convertTo(scale, VCUnitDefinition.UNIT_DIMENSIONLESS);
		sbmlUnitDefn = new org.sbml.libsbml.UnitDefinition(cbit.util.TokenMangler.mangleToSName(vcUnitDefn.getSymbol()));
		sbmlUnitDefn.addUnit(new org.sbml.libsbml.Unit(SBMLUnitTranslator.DIMENSIONLESS, 1, 0, scale));
	} else {
		// Translate the VCUnitDef into libSBML UnitDef : convert the units of VCUnitDef into libSBML units and add them to sbmlUnitDefn
		sbmlUnitDefn = new org.sbml.libsbml.UnitDefinition(sbmlUnitSymbol);
		ucar.units.Unit vcUcarUnit = vcUnitDefn.getUcarUnit();
		java.util.ArrayList sbmlUnitsList = convertVCUnitsToSbmlUnits(1.0, vcUcarUnit, new java.util.ArrayList());

		for (int i = 0; i < sbmlUnitsList.size(); i++){
			org.sbml.libsbml.Unit sbmlUnit = (org.sbml.libsbml.Unit)sbmlUnitsList.get(i);
			sbmlUnitDefn.addUnit(sbmlUnit);
		}
	}

	return sbmlUnitDefn;
}


	/*
	 *  getVCUnit : 
	 */
	private static ucar.units.Unit getVCUnit(org.sbml.libsbml.Unit unit) {
		// Get the attributes of the unit 'element', 'kind', 'multiplier', 'scale', 'offset', etc.
		String unitKind = org.sbml.libsbml.libsbml.UnitKind_toString(unit.getKind());
		int unitExponent = unit.getExponent();
		int unitScale = unit.getScale();
		double unitMultiplier = unit.getMultiplier();
		double unitOffset = unit.getOffset();

		ucar.units.Unit vcUnit = null;
		ucar.units.UnitName uName;

		// convert the sbmlUnit into a vcell unit with the appropriate multiplier, scale, exponent, offset, etc ..
		try { 
            ucar.units.UnitDB unitDB = ucar.units.UnitSystemManager.instance().getUnitDB();
			if (unit.isDimensionless()) {        //'dimensionless' can be part of a bigger unit definition     
				String dimensionlessStr = Double.toString(Math.pow((unitMultiplier*Math.pow(10, unitScale)), unitExponent));
				vcUnit = VCUnitDefinition.getInstance(dimensionlessStr).getUcarUnit();
				return vcUnit;
			} else {
				if (unit.isItem()) {
					System.out.println("SBML 'item' unit found, interpreted as 'molecule'");
					vcUnit = unitDB.get("molecules");
					uName = ucar.units.UnitName.newUnitName("molecules");
				} else {
					vcUnit = unitDB.get(unitKind);
					uName = ucar.units.UnitName.newUnitName(unitKind); 
				}
				
				if (unitScale != 0) {                        
					vcUnit = new ucar.units.ScaledUnit(Double.parseDouble("1.0e" + unitScale), vcUnit, uName);
				}
				if (unitMultiplier != 1) {
					vcUnit = new ucar.units.ScaledUnit(unitMultiplier, vcUnit, uName);
				}
				if (unitExponent != 1) {
					vcUnit = vcUnit.raiseTo(new ucar.units.RationalNumber(unitExponent));
				}
				if (unitOffset  != 0) {
					vcUnit = new ucar.units.OffsetUnit(vcUnit, unitOffset, uName);
				}
			}
		} catch (ucar.units.UnitException e) {
			e.printStackTrace();
			throw new cbit.vcell.units.VCUnitException("Unable to set unit value: "+e.getMessage());
		}

		return vcUnit;
	}


public static VCUnitDefinition getVCUnitDefinition(org.sbml.libsbml.UnitDefinition sbmlUnitDefn) {
	// Each SBML UnitDefinition contains a list of Units, the total unit (VC unit) as represented by
	// an SBML UnitDefinition is the product of the list of units it contains.
	VCUnitDefinition vcUnitDefn = null;
	org.sbml.libsbml.ListOf listofUnits = sbmlUnitDefn.getListOfUnits();
	for (int j = 0; j < sbmlUnitDefn.getNumUnits(); j++) {
		org.sbml.libsbml.Unit sbmlUnit = (org.sbml.libsbml.Unit)listofUnits.get(j);
		ucar.units.Unit vcUnit = getVCUnit(sbmlUnit);
		if (vcUnitDefn == null) {
			vcUnitDefn = VCUnitDefinition.getInstance(vcUnit);
		} else {
			vcUnitDefn = vcUnitDefn.multiplyBy(VCUnitDefinition.getInstance(vcUnit));        //?
		}
	}
	System.out.println("sbmlUnit : " + sbmlUnitDefn.toString() + ";\t VC Unit : " + vcUnitDefn.toString());
	return vcUnitDefn;
}


public static boolean isSbmlBaseUnit(String symbol) {
	return SbmlBaseUnits.contains(symbol);
}
}