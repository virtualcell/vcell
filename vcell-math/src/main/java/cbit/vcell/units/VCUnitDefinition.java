/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.units;

import java.io.Serializable;
import java.util.HashMap;

import org.vcell.util.Matchable;

import cbit.vcell.matrix.RationalExp;
import cbit.vcell.matrix.RationalNumber;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.RationalExpUtils;
import cbit.vcell.units.parser.UnitSymbol;

public class VCUnitDefinition implements Matchable, Serializable{
	
	private InternalUnitDefinition fieldUnitDefinition = null;
	private VCUnitSystem fieldVCUnitSystem = null;
	private UnitSymbol fieldUnitSymbol = null;
	
	//
	// each unit should be a singleton within the unit system it came from
	// because they are singletons, it is ok for each unit to cache it's own derived units locally (for divide, mult, power).
	//
	private transient HashMap<VCUnitDefinition, VCUnitDefinition> divideByMap = null;
	private transient HashMap<VCUnitDefinition, VCUnitDefinition> multiplyByMap = null;
	private transient HashMap<ucar.units_vcell.RationalNumber, VCUnitDefinition> powerMap = null;
	
	VCUnitDefinition(InternalUnitDefinition unitDefinition, VCUnitSystem vcUnitSystem, UnitSymbol unitsymbol) {
		super();
		this.fieldUnitDefinition = unitDefinition;
		this.fieldVCUnitSystem = vcUnitSystem;
		this.fieldUnitSymbol = unitsymbol;
	}

	public String getSymbol() {
		return fieldUnitSymbol.getUnitSymbol();
	}

	public String getSymbolUnicode() {
		return fieldUnitSymbol.getUnitSymbolUnicode();
	}

	public String getSymbolHtml() {
		return fieldUnitSymbol.getUnitSymbolHtml();
	}

	public VCUnitDefinition getInverse() {
		double newNumericScale = 1.0/fieldUnitSymbol.getNumericScale();
		return getUnit(newNumericScale,getSymbolRationalExpWithoutNumericScale(this.fieldUnitSymbol).inverse());
	}

	public VCUnitDefinition divideBy(VCUnitDefinition childUnit) {
		VCUnitDefinition ratioUnit = null;
		if (divideByMap != null){
			ratioUnit = divideByMap.get(childUnit);
			if (ratioUnit != null){
				return ratioUnit;
			}
		}else{
			divideByMap = new HashMap<VCUnitDefinition, VCUnitDefinition>();
		}

		double newNumericScale = fieldUnitSymbol.getNumericScale()/childUnit.fieldUnitSymbol.getNumericScale();
		RationalExp thisNonnumericUnit = getSymbolRationalExpWithoutNumericScale(this.fieldUnitSymbol);
		RationalExp otherNonnumericUnit = getSymbolRationalExpWithoutNumericScale(childUnit.fieldUnitSymbol);
		ratioUnit = getUnit(newNumericScale, thisNonnumericUnit.div(otherNonnumericUnit));
		divideByMap.put(childUnit,ratioUnit);
		return ratioUnit;
	}

	public boolean isTBD() {
		return fieldUnitDefinition.isTBD();
	}
	
	private static RationalExp getSymbolRationalExpWithoutNumericScale(UnitSymbol unitSymbol) {
		String unitString = unitSymbol.getUnitSymbolAsInfixWithoutFloatScale();
		RationalExp rationalExp;
		try {
			rationalExp = RationalExpUtils.getRationalExp(new cbit.vcell.parser.Expression(unitString));
		} catch (ExpressionException e) {
			e.printStackTrace();
			throw new RuntimeException("unit exception "+e.getMessage());
		}
		return rationalExp;
	}
		
	private VCUnitDefinition getUnit(double numericScale, RationalExp rationalExp){
		if (numericScale==1.0){
			UnitSymbol unitSymbol = new UnitSymbol(rationalExp.infixString());
			return fieldVCUnitSystem.getInstance(unitSymbol.getUnitSymbol());
		}else{
			UnitSymbol unitSymbol = new UnitSymbol(rationalExp.infixString());
			return fieldVCUnitSystem.getInstance(numericScale + " " + unitSymbol.getUnitSymbol());
		}
	}

	public VCUnitDefinition raiseTo(ucar.units_vcell.RationalNumber rationalNumber) {
		VCUnitDefinition powerUnit = null;
		if (powerMap != null){
			powerUnit = powerMap.get(rationalNumber);
			if (powerUnit != null){
				return powerUnit;
			}
		}else{
			powerMap = new HashMap<ucar.units_vcell.RationalNumber, VCUnitDefinition>();
		}
		
		RationalExp origRationalExpWithoutNumericScale = getSymbolRationalExpWithoutNumericScale(fieldUnitSymbol);
		RationalExp rationalExpWithoutNumericScale = origRationalExpWithoutNumericScale;
		//
		// if a negative exponent, take reciprocal of the base and transform to a positive exponent
		//
		if (rationalNumber.doubleValue()<0){
			rationalExpWithoutNumericScale = rationalExpWithoutNumericScale.inverse();
			rationalNumber = rationalNumber.minus();
		}
		//
		// if the exponent is integer valued, raise power by repeated multiplication
		//
		if (rationalNumber.intValue()==rationalNumber.doubleValue()){
			for (int i=1;i<Math.abs(rationalNumber.intValue());i++){
				rationalExpWithoutNumericScale = rationalExpWithoutNumericScale.mult(origRationalExpWithoutNumericScale);
			}
			double newNumericScale = Math.pow(fieldUnitSymbol.getNumericScale(),rationalNumber.doubleValue());
			powerUnit = getUnit(newNumericScale, rationalExpWithoutNumericScale);
			powerMap.put(rationalNumber, powerUnit);
			return powerUnit;
		}else{
			throw new RuntimeException("raiseTo( non-integer ) not yet supported");
		}
	}

	public VCUnitDefinition multiplyBy(VCUnitDefinition unit) {
		VCUnitDefinition productUnit = null;
		if (multiplyByMap != null){
			productUnit = multiplyByMap.get(unit);
			if (productUnit != null){
				return productUnit;
			}
		}else{
			multiplyByMap = new HashMap<VCUnitDefinition, VCUnitDefinition>();
		}

		double newNumericScale = fieldUnitSymbol.getNumericScale() * unit.fieldUnitSymbol.getNumericScale();
		productUnit = getUnit(newNumericScale, getSymbolRationalExpWithoutNumericScale(this.fieldUnitSymbol).mult(getSymbolRationalExpWithoutNumericScale(unit.fieldUnitSymbol)));
		multiplyByMap.put(unit, productUnit);
		return productUnit;
	}

	public boolean compareEqual(Matchable matchable) {
		if (this == matchable){
			return true;
		}
		if ( !(matchable instanceof VCUnitDefinition) ) {
			return false;
		}
		VCUnitDefinition matchableUnitDefn = (VCUnitDefinition)matchable;
		if (!this.fieldUnitDefinition.compareEqual(matchableUnitDefn.fieldUnitDefinition)) {
			return false;
		}
		if (!fieldUnitSymbol.getUnitSymbol().equals(matchableUnitDefn.fieldUnitSymbol.getUnitSymbol())) {
			return false;
		}
		return true;
	}

	public boolean isCompatible(VCUnitDefinition targetUnit) {
		return fieldUnitDefinition.isCompatible(targetUnit.fieldUnitDefinition);
	}

	public double convertTo(double conversionFactor, VCUnitDefinition targetUnit) {
		return fieldUnitDefinition.convertTo(conversionFactor, targetUnit.fieldUnitDefinition);
	}

	public ucar.units_vcell.Unit getUcarUnit() {
		return fieldUnitDefinition.getUcarUnit();
	}
	
	public String toString(){
		return getSymbol();
	}

	public boolean isEquivalent(VCUnitDefinition otherUnit) {
		return this.fieldUnitDefinition.compareEqual(otherUnit.fieldUnitDefinition);
	}

	public RationalNumber getDimensionlessScale() {
// System.err.println("VCUnitDefinition.getDimensionlessScale(): this unit = "+getSymbol());
		VCUnitDefinition dimensionless = fieldVCUnitSystem.getInstance_DIMENSIONLESS();
		if (isEquivalent(dimensionless)){
			RationalNumber rationalConversionScale = new RationalNumber(1);
// System.err.println("VCUnitDefinition.getDimensionlessScale(): conversion scale = "+rationalConversionScale.toString());
			return rationalConversionScale;
		}
		if (isCompatible(dimensionless)){
			double conversionScale = dimensionless.convertTo(1.0, this);
			RationalNumber rationalConversionScale =  RationalNumber.getApproximateFraction(conversionScale);
// System.err.println("VCUnitDefinition.getDimensionlessScale(): conversion scale = "+rationalConversionScale.toString());
			return rationalConversionScale;
		}
// System.err.println("VCUnitDefinition.getDimensionlessScale(): not compatable with dimensionless");
		//
		// this is not strictly a dimensionless since we do not automatically convert between molecules and moles)
		// so have to explicitly look for such cases.
		//
		final VCUnitDefinition molecules_per_uM_um3 = fieldVCUnitSystem.getInstance("molecules.uM-1.um-3");
		final RationalNumber value_molecules_per_uM_um3 = new RationalNumber(602214179,1000000);
		
		RationalNumber tempValue = value_molecules_per_uM_um3;
		VCUnitDefinition tempUnit = molecules_per_uM_um3;
		for (int i=0;i<10;i++){
//			System.err.println("VCUnitDefinition.getDimensionlessScale(): mult unit = "+this.multiplyBy(tempUnit).getSymbol());
			if (this.multiplyBy(tempUnit).isCompatible(dimensionless)){
				double conversionScale = dimensionless.convertTo(1.0, this.multiplyBy(tempUnit));
				RationalNumber rationalConversionScale = RationalNumber.getApproximateFraction(conversionScale).div(tempValue);
//				System.err.println("VCUnitDefinition.getDimensionlessScale(): mult unit = "+this.multiplyBy(tempUnit).getSymbol()+" worked, conversion scale = "+rationalConversionScale.toString());
				return rationalConversionScale;
			}
//			System.err.println("VCUnitDefinition.getDimensionlessScale(): div unit = "+this.divideBy(tempUnit).getSymbol());
			if (this.divideBy(tempUnit).isCompatible(dimensionless)){
				double conversionScale = dimensionless.convertTo(1.0, this.divideBy(tempUnit));
				RationalNumber rationalConversionScale = RationalNumber.getApproximateFraction(conversionScale).mult(tempValue);
//				System.err.println("VCUnitDefinition.getDimensionlessScale(): div unit = "+this.divideBy(tempUnit).getSymbol()+" worked, conversion scale = "+rationalConversionScale.toString());
				return rationalConversionScale;
			}
			tempValue = tempValue.mult(value_molecules_per_uM_um3);
			tempUnit = tempUnit.multiplyBy(molecules_per_uM_um3);
		}
		throw new RuntimeException("unit "+getSymbol()+" is not dimensionless, cannot be a unit conversion factor");
	}
}
