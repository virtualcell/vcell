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

import org.vcell.util.Matchable;

import ucar.units.RationalNumber;
import cbit.vcell.matrix.RationalExp;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.RationalExpUtils;
import cbit.vcell.units.parser.UnitSymbol;

public class VCUnitDefinition implements Matchable, Serializable{
	
	private InternalUnitDefinition fieldUnitDefinition = null;
	private VCUnitSystem fieldVCUnitSystem = null;
	private UnitSymbol fieldUnitSymbol = null;
	
	VCUnitDefinition(InternalUnitDefinition unitDefinition, VCUnitSystem vcUnitSystem, UnitSymbol unitsymbol) {
		super();
		this.fieldUnitDefinition = unitDefinition;
		this.fieldVCUnitSystem = vcUnitSystem;
		this.fieldUnitSymbol = unitsymbol;
	}

	public String getSymbol() {
		return fieldUnitSymbol.getUnitSymbol();
	}

	public VCUnitDefinition getInverse() {
		double newNumericScale = 1.0/fieldUnitSymbol.getNumericScale();
		return getUnit(newNumericScale,getSymbolRationalExpWithoutNumericScale(this.fieldUnitSymbol).inverse());
	}

	public VCUnitDefinition divideBy(VCUnitDefinition childUnit) {
		double newNumericScale = fieldUnitSymbol.getNumericScale()/childUnit.fieldUnitSymbol.getNumericScale();
		RationalExp thisNonnumericUnit = getSymbolRationalExpWithoutNumericScale(this.fieldUnitSymbol);
		RationalExp otherNonnumericUnit = getSymbolRationalExpWithoutNumericScale(childUnit.fieldUnitSymbol);
		return getUnit(newNumericScale, thisNonnumericUnit.div(otherNonnumericUnit));
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

	public VCUnitDefinition raiseTo(RationalNumber rationalNumber) {
		RationalExp rationalExpWithoutNumericScale = getSymbolRationalExpWithoutNumericScale(fieldUnitSymbol);
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
				rationalExpWithoutNumericScale = rationalExpWithoutNumericScale.mult(rationalExpWithoutNumericScale);
			}
			double newNumericScale = Math.pow(fieldUnitSymbol.getNumericScale(),rationalNumber.doubleValue());
			return getUnit(newNumericScale, rationalExpWithoutNumericScale);
		}else{
			throw new RuntimeException("raiseTo( non-integer ) not yet supported");
		}
	}

	public VCUnitDefinition multiplyBy(VCUnitDefinition unit) {
		double newNumericScale = fieldUnitSymbol.getNumericScale() * unit.fieldUnitSymbol.getNumericScale();
		return getUnit(newNumericScale, getSymbolRationalExpWithoutNumericScale(this.fieldUnitSymbol).mult(getSymbolRationalExpWithoutNumericScale(unit.fieldUnitSymbol)));
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

	public ucar.units.Unit getUcarUnit() {
		return fieldUnitDefinition.getUcarUnit();
	}
	
	public String toString(){
		return super.toString()+" "+getSymbol();
	}
}
