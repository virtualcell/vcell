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
import java.util.ArrayList;
import java.util.Iterator;

import org.vcell.util.Matchable;

import ucar.units_vcell.Base;
import ucar.units_vcell.BaseQuantity;
import ucar.units_vcell.BaseUnit;
import ucar.units_vcell.DerivedUnitImpl;
import ucar.units_vcell.Dimension;
import ucar.units_vcell.Factor;
import ucar.units_vcell.OffsetUnit;
import ucar.units_vcell.ParseException;
import ucar.units_vcell.QuantityDimension;
import ucar.units_vcell.RationalNumber;
import ucar.units_vcell.ScaledUnit;
import ucar.units_vcell.StandardUnitFormat;
import ucar.units_vcell.TimeScaleUnit;
import ucar.units_vcell.Unit;
import ucar.units_vcell.UnitDimension;
import ucar.units_vcell.UnitException;
import ucar.units_vcell.UnitName;

/**
 The wrapper around a ucar unit.
 
 * Creation date: (3/3/2004 6:20:39 PM)
 * @author: Rashad Badrawi
 */
@SuppressWarnings("serial")
class InternalUnitDefinition implements Matchable, Serializable {

    public static final String TBD_SYMBOL = "tbd";
    private static final java.text.NumberFormat numberFormatForRounding =
        new java.text.DecimalFormat("#0.0#E0#");

    //VC standard Unit definitions
    public static final InternalUnitDefinition UNIT_s;
    public static final InternalUnitDefinition UNIT_M;
    public static final InternalUnitDefinition UNIT_um;
    public static final InternalUnitDefinition UNIT_per_um;
    public static final InternalUnitDefinition UNIT_per_um_per_s;
    public static final InternalUnitDefinition UNIT_per_s;
    public static final InternalUnitDefinition UNIT_um2;
    public static final InternalUnitDefinition UNIT_um2_per_s;
    public static final InternalUnitDefinition UNIT_um3;
    public static final InternalUnitDefinition UNIT_uM;
    public static final InternalUnitDefinition UNIT_uM2;
    public static final InternalUnitDefinition UNIT_per_uM;
    public static final InternalUnitDefinition UNIT_per_uM2;
    public static final InternalUnitDefinition UNIT_uM_per_s;
    public static final InternalUnitDefinition UNIT_mM_per_s;
    public static final InternalUnitDefinition UNIT_per_uM_per_s;
    public static final InternalUnitDefinition UNIT_uM_um_per_s;
    public static final InternalUnitDefinition UNIT_uM_um3_per_molecules_per_s;
    public static final InternalUnitDefinition UNIT_uM_um3_per_molecules; // for KMOLE
    public static final InternalUnitDefinition UNIT_uM_um2_per_molecules;
    public static final InternalUnitDefinition UNIT_molecules_per_um2_per_uM;
    public static final InternalUnitDefinition UNIT_mV;
    public static final InternalUnitDefinition UNIT_mV_per_s;
    public static final InternalUnitDefinition UNIT_pF;
    public static final InternalUnitDefinition UNIT_pF_per_um2;
    public static final InternalUnitDefinition UNIT_pA;
    public static final InternalUnitDefinition UNIT_pA_per_um2;
    public static final InternalUnitDefinition UNIT_molecules;
    public static final InternalUnitDefinition UNIT_molecules_per_s;
    public static final InternalUnitDefinition UNIT_molecules_per_um2;
    public static final InternalUnitDefinition UNIT_molecules_per_um3;
    public static final InternalUnitDefinition UNIT_um2_per_molecules_per_s;
    public static final InternalUnitDefinition UNIT_um3_per_molecules_per_s;
    public static final InternalUnitDefinition UNIT_molecules_per_um2_per_s;
    public static final InternalUnitDefinition UNIT_molecules_per_um2_per_uM_per_s;
    public static final InternalUnitDefinition UNIT_molecules_per_um2_per_uM2_per_s;
    public static final InternalUnitDefinition UNIT_molecules_per_um3_per_uM;
    public static final InternalUnitDefinition UNIT_molecules_per_um_per_s;
    public static final InternalUnitDefinition UNIT_molecules_per_pmol;
    public static final InternalUnitDefinition UNIT_nS;
    public static final InternalUnitDefinition UNIT_nS_per_um2;
    public static final InternalUnitDefinition UNIT_K;
    public static final InternalUnitDefinition UNIT_C_per_mol;
    public static final InternalUnitDefinition UNIT_C_per_nmol;
    public static final InternalUnitDefinition UNIT_mV_C_per_K_per_mol;
    public static final InternalUnitDefinition UNIT_um_per_s;
    public static final InternalUnitDefinition UNIT_cm_per_s;
    public static final InternalUnitDefinition UNIT_per_L;
    public static final InternalUnitDefinition UNIT_L;
    public static final InternalUnitDefinition UNIT_mol;
    public static final InternalUnitDefinition UNIT_umol;
    
    public static final InternalUnitDefinition UNIT_umol_um3_per_L;
    public static final InternalUnitDefinition UNIT_DIMENSIONLESS;
    public static final InternalUnitDefinition UNIT_TBD;

    private static ArrayList<InternalUnitDefinition> defs;
    private final Unit ucarUnit;
    private final String forcedSymbol;

    //private static ucar.units_vcell.PrefixDB prefixDB = null;

    static {
        numberFormatForRounding.setMaximumFractionDigits(12);

        defs = new ArrayList<InternalUnitDefinition>();

        InternalUnitDefinition unit_s = null;
        InternalUnitDefinition unit_M = null;
        InternalUnitDefinition unit_um = null;
        InternalUnitDefinition unit_per_um = null;
        InternalUnitDefinition unit_per_um_per_s = null;
        InternalUnitDefinition unit_per_s = null;
        InternalUnitDefinition unit_um2 = null;
        InternalUnitDefinition unit_um2_per_s = null;
        InternalUnitDefinition unit_um3 = null;
        InternalUnitDefinition unit_uM = null;
        InternalUnitDefinition unit_uM2 = null;
        InternalUnitDefinition unit_per_uM = null;
        InternalUnitDefinition unit_per_uM2 = null;
        InternalUnitDefinition unit_uM_per_s = null;
        InternalUnitDefinition unit_mM_per_s = null;
        InternalUnitDefinition unit_per_uM_per_s = null;
        InternalUnitDefinition unit_uM_um_per_s = null;
        InternalUnitDefinition unit_uM_um3_per_molecules_per_s = null;
        InternalUnitDefinition unit_uM_um3_per_molecules = null;
        InternalUnitDefinition unit_uM_um2_per_molecules = null;
        InternalUnitDefinition unit_molecules_per_um2_per_uM = null;
        InternalUnitDefinition unit_mV = null;
        InternalUnitDefinition unit_mV_per_s = null;
        InternalUnitDefinition unit_pF = null;
        InternalUnitDefinition unit_pF_per_um2 = null;
        InternalUnitDefinition unit_pA = null;
        InternalUnitDefinition unit_pA_per_um2 = null;
        InternalUnitDefinition unit_molecules = null;
        InternalUnitDefinition unit_molecules_per_s = null;
        InternalUnitDefinition unit_molecules_per_um2 = null;
        InternalUnitDefinition unit_molecules_per_um3 = null;
        InternalUnitDefinition unit_um2_per_molecules_per_s = null;
        InternalUnitDefinition unit_um3_per_molecules_per_s = null;
        InternalUnitDefinition unit_molecules_per_um2_per_s = null;
        InternalUnitDefinition unit_molecules_per_um2_per_uM_per_s = null;
        InternalUnitDefinition unit_molecules_per_um2_per_uM2_per_s = null;
        InternalUnitDefinition unit_molecules_per_um3_per_uM = null;
        InternalUnitDefinition unit_molecules_per_um_per_s = null;
        InternalUnitDefinition unit_molecules_per_pmol = null;
        InternalUnitDefinition unit_DIMENSIONLESS = null;
        InternalUnitDefinition unit_TBD = null;
        InternalUnitDefinition unit_nS = null;
        InternalUnitDefinition unit_nS_per_um2 = null;
        InternalUnitDefinition unit_K = null;
        InternalUnitDefinition unit_C_per_mol = null;
        InternalUnitDefinition unit_C_per_nmol = null;
        InternalUnitDefinition unit_mV_C_per_K_per_mol = null;
        InternalUnitDefinition unit_um_per_s = null;
        InternalUnitDefinition unit_cm_per_s = null;
        InternalUnitDefinition unit_per_L = null;
        InternalUnitDefinition unit_L = null;
        InternalUnitDefinition unit_mol = null;
        InternalUnitDefinition unit_umol = null;
        InternalUnitDefinition unit_umol_um3_per_L = null;
   
        try {
            //prefixDB = ucar.units_vcell.PrefixDBManager.instance();
            ucar.units_vcell.UnitDB unitDB = ucar.units_vcell.UnitSystemManager.instance().getUnitDB();
            unitDB.addUnit(
                du("molar", "M", ucar.units_vcell.SI.MOLE.divideBy(ucar.units_vcell.SI.LITER)));
            //unitDB.addUnit(du("molecules", "molecules", new ScaledUnit(1, SI.ITEM)));
            unitDB.addAlias("molecules", "item", "molecules");

            unit_s = new InternalUnitDefinition("s");
            unit_M = new InternalUnitDefinition("M");
            unit_um = new InternalUnitDefinition("um");
            unit_per_um = new InternalUnitDefinition("um-1");
            unit_per_um_per_s = new InternalUnitDefinition("um-1.s-1");
            unit_per_s = new InternalUnitDefinition("s-1");
            unit_um2 = new InternalUnitDefinition("um2");
            unit_um2_per_s = new InternalUnitDefinition("um2.s-1");
            unit_um3 = new InternalUnitDefinition("um3");
            unit_uM = new InternalUnitDefinition("uM");
            unit_uM2 = new InternalUnitDefinition("uM2");
            unit_per_uM = new InternalUnitDefinition("uM-1");
            unit_per_uM2 = new InternalUnitDefinition("uM-2");
            unit_uM_per_s = new InternalUnitDefinition("uM.s-1");
            unit_mM_per_s = new InternalUnitDefinition("mM.s-1");
            unit_per_uM_per_s = new InternalUnitDefinition("uM-1.s-1");
            unit_uM_um_per_s = new InternalUnitDefinition("uM.um.s-1");
            unit_uM_um3_per_molecules_per_s = new InternalUnitDefinition("uM.um3.molecules-1.s-1");
            unit_uM_um3_per_molecules = new InternalUnitDefinition("uM.um3.molecules-1");
            unit_uM_um2_per_molecules = new InternalUnitDefinition("uM.um2.molecules-1");
            unit_molecules_per_um2_per_uM = new InternalUnitDefinition("molecules.um-2.uM-1");
            unit_mV = new InternalUnitDefinition("mV");
            unit_mV_per_s = new InternalUnitDefinition("mV.s-1");
            unit_pF = new InternalUnitDefinition("pF");
            unit_pF_per_um2 = new InternalUnitDefinition("pF.um-2");
            unit_pA = new InternalUnitDefinition("pA");
            unit_pA_per_um2 = new InternalUnitDefinition("pA.um-2");
            unit_molecules = new InternalUnitDefinition("molecules");
            unit_molecules_per_s = new InternalUnitDefinition("molecules.s-1");
            unit_molecules_per_um2 = new InternalUnitDefinition("molecules.um-2");
            unit_molecules_per_um3 = new InternalUnitDefinition("molecules.um-3");
            unit_um2_per_molecules_per_s = new InternalUnitDefinition("um2.molecules-1.s-1");
            unit_um3_per_molecules_per_s = new InternalUnitDefinition("um3.molecules-1.s-1");
            unit_molecules_per_um2_per_s = new InternalUnitDefinition("molecules.um-2.s-1");
            unit_molecules_per_um2_per_uM_per_s = new InternalUnitDefinition("molecules.um-2.uM-1.s-1");
            unit_molecules_per_um2_per_uM2_per_s = new InternalUnitDefinition("molecules.um-2.uM-2.s-1");
            unit_molecules_per_um3_per_uM = new InternalUnitDefinition("molecules.um-3.uM-1");
            unit_molecules_per_um_per_s = new InternalUnitDefinition("molecules.um-1.s-1");
            unit_molecules_per_pmol = new InternalUnitDefinition("molecules.pmol-1");
            unit_DIMENSIONLESS = new InternalUnitDefinition("1");
            unit_TBD = new InternalUnitDefinition(TBD_SYMBOL);
            unit_nS = new InternalUnitDefinition("nS");
            unit_nS_per_um2 = new InternalUnitDefinition("nS.um-2");
            unit_K = new InternalUnitDefinition("K");
            unit_C_per_mol = new InternalUnitDefinition("C.mol-1");
            unit_C_per_nmol = new InternalUnitDefinition("C.nmol-1");
            unit_mV_C_per_K_per_mol = new InternalUnitDefinition("mV.C.K-1.mol-1");
            unit_um_per_s = new InternalUnitDefinition("um.s-1");
            unit_cm_per_s = new InternalUnitDefinition("cm.s-1");
            unit_per_L = new InternalUnitDefinition("litre-1");
            unit_L = new InternalUnitDefinition("litre");
            unit_mol = new InternalUnitDefinition("mol");
            unit_umol = new InternalUnitDefinition("umol");
            unit_umol_um3_per_L = new InternalUnitDefinition("umol.um3.litre-1");
        } catch (Throwable e) {
            e.printStackTrace(System.out);
        }
        UNIT_s = unit_s;
        UNIT_M = unit_M;
        UNIT_um = unit_um;
        UNIT_per_um = unit_per_um;
        UNIT_per_um_per_s = unit_per_um_per_s;
        UNIT_per_s = unit_per_s;
        UNIT_um2 = unit_um2;
        UNIT_um2_per_s = unit_um2_per_s;
        UNIT_um3 = unit_um3;
        UNIT_uM = unit_uM;
        UNIT_uM2 = unit_uM2;
        UNIT_per_uM = unit_per_uM;
        UNIT_per_uM2 = unit_per_uM2;
        UNIT_uM_per_s = unit_uM_per_s;
        UNIT_mM_per_s = unit_mM_per_s;
        UNIT_per_uM_per_s = unit_per_uM_per_s;
        UNIT_uM_um_per_s = unit_uM_um_per_s;
        UNIT_uM_um3_per_molecules_per_s = unit_uM_um3_per_molecules_per_s;
        UNIT_uM_um3_per_molecules = unit_uM_um3_per_molecules;
        UNIT_uM_um2_per_molecules = unit_uM_um2_per_molecules;
        UNIT_molecules_per_um2_per_uM = unit_molecules_per_um2_per_uM;
        UNIT_mV = unit_mV;
        UNIT_mV_per_s = unit_mV_per_s;
        UNIT_pF = unit_pF;
        UNIT_pF_per_um2 = unit_pF_per_um2;
        UNIT_pA = unit_pA;
        UNIT_pA_per_um2 = unit_pA_per_um2;
        UNIT_molecules = unit_molecules;
        UNIT_molecules_per_s = unit_molecules_per_s;
        UNIT_molecules_per_um2 = unit_molecules_per_um2;
        UNIT_molecules_per_um3 = unit_molecules_per_um3;
        UNIT_um2_per_molecules_per_s = unit_um2_per_molecules_per_s;
        UNIT_um3_per_molecules_per_s = unit_um3_per_molecules_per_s;
        UNIT_molecules_per_um2_per_s = unit_molecules_per_um2_per_s;
        UNIT_molecules_per_um2_per_uM_per_s = unit_molecules_per_um2_per_uM_per_s;
        UNIT_molecules_per_um2_per_uM2_per_s = unit_molecules_per_um2_per_uM2_per_s;
        UNIT_molecules_per_um3_per_uM = unit_molecules_per_um3_per_uM;
        UNIT_molecules_per_um_per_s = unit_molecules_per_um_per_s;
        UNIT_molecules_per_pmol = unit_molecules_per_pmol;
        UNIT_DIMENSIONLESS = unit_DIMENSIONLESS;
        UNIT_TBD = unit_TBD;
        UNIT_nS = unit_nS;
        UNIT_nS_per_um2 = unit_nS_per_um2;
        UNIT_K = unit_K;
        UNIT_C_per_mol = unit_C_per_mol;
        UNIT_C_per_nmol = unit_C_per_nmol;
        UNIT_mV_C_per_K_per_mol = unit_mV_C_per_K_per_mol;
        UNIT_um_per_s = unit_um_per_s;
        UNIT_cm_per_s = unit_cm_per_s;
        UNIT_per_L = unit_per_L;
        UNIT_L = unit_L;
        UNIT_mol = unit_mol;
        UNIT_umol = unit_umol;
        UNIT_umol_um3_per_L = unit_umol_um3_per_L;
    }

//
// Constructor for "standard" units.
//
private InternalUnitDefinition(String argSymbol) {
	if (argSymbol == null){
		throw new IllegalArgumentException("symbol was null");
	}
	Unit dUnit = null;
	if (TBD_SYMBOL.equals(argSymbol)) {
		dUnit = null;
	}else{
		try {
			StandardUnitFormat standardUnitFormat = new StandardUnitFormat(new java.io.ByteArrayInputStream(argSymbol.trim().getBytes()));
			dUnit = standardUnitFormat.unitSpec(ucar.units_vcell.UnitSystemManager.instance().getUnitDB());
			ucar.units_vcell.Factor factors[] = dUnit.getDerivedUnit().getDimension().getFactors();
			for (int i = 0; i < factors.length; i++){
				if (factors[i].getBase() instanceof ucar.units_vcell.UnknownUnit){
					throw new VCUnitException("invalid symbol '"+factors[i].getBase()+"'");
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
			throw new VCUnitException("Unable to get unit: " + argSymbol + ": " + e.getMessage());
		} catch (UnitException e) {
			e.printStackTrace();
			throw new VCUnitException("Unable to get unit: " + argSymbol + ": " + e.getMessage());
		}
	}

	this.ucarUnit = dUnit;
	this.forcedSymbol = argSymbol;
	//
	// add to standard Units list (don't add TBD)
	//
	if (!argSymbol.equals(TBD_SYMBOL)){
		defs.add(this);
	}
}


	private InternalUnitDefinition(Unit argUcarUnit) {
		this.ucarUnit = argUcarUnit;
		this.forcedSymbol = null;
	}

	public static Iterator<InternalUnitDefinition> getKnownUnits() {
		return defs.iterator();
	}
	
	public boolean compareEqual(Matchable matchUnitDef) {

		if (this == matchUnitDef){
			return true;
		}
		if ( !(matchUnitDef instanceof InternalUnitDefinition) ) {
			return false;
		}
		Unit ucarUnit2 = ((InternalUnitDefinition)matchUnitDef).getUcarUnit();
		if (ucarUnit == null && ucarUnit2 == null) {        //for TBDs
			return true;
		} else if (ucarUnit == null || ucarUnit2 == null) {
			return false;
		}
		return equivalent(ucarUnit,ucarUnit2);
	}


	public double convertTo(double amount, InternalUnitDefinition anotherUnit) {

		if (!this.isCompatible(anotherUnit)) {                
			throw new VCUnitException ("Unable to convert from: " + getSymbol() + " to: " + anotherUnit.getSymbol());
		}
		try {
			Unit ucar2 = anotherUnit.getUcarUnit();
			return ucarUnit.convertTo(amount, ucar2);
		} catch (UnitException e) {
			e.printStackTrace();
			throw new VCUnitException ("Unable to convert from: " + getSymbol() + " to: " + anotherUnit.getSymbol());
		}
	}


	public InternalUnitDefinition divideBy(InternalUnitDefinition otherUnitDef) throws VCUnitException {

		if (isTBD() || otherUnitDef.isTBD()){
			throw new VCUnitException("cannot divide with TBD units");
		}
		Unit newUcarUnit;
		try {
			newUcarUnit = getUcarUnit().divideBy(otherUnitDef.getUcarUnit());
		} catch (UnitException e) {
			e.printStackTrace();
			throw new VCUnitException("Unable to divide units: " + ucarUnit.getSymbol() + " and " + otherUnitDef.getSymbol() + ": " + e.getMessage());
		}

		return getInstance(newUcarUnit);
	}


    /**
     * Factory method for constructing a derived unit.
     * @param name		The name of the unit.
     * @param symbol		The symbol for the unit.
     * @param quantity		The definition of the unit.
     * @return			The derived unit corresponding to the arguments.
     */
    private static ucar.units_vcell.Unit du(String name, String symbol, ucar.units_vcell.Unit definition) throws ucar.units_vcell.NameException {
		return definition.clone(ucar.units_vcell.UnitName.newUnitName(name, null, symbol));
    }


/**
 * Insert the method's description here.
 * Creation date: (4/15/2004 1:54:00 PM)
 * @return boolean
 * @param ucarUnit1 ucar.units_vcell.Unit
 * @param ucarUnit2 ucar.units_vcell.Unit
 */
private static boolean equivalent(Unit ucarUnit1, Unit ucarUnit2) {
	
	if (ucarUnit2.isDimensionless() && ucarUnit1.isDimensionless()){
		double scale1 = 1.0;
		Unit temp1 = ucarUnit1;
		while (temp1 instanceof ScaledUnit){
			scale1 *= ((ScaledUnit)temp1).getScale();
			temp1 = ((ScaledUnit)temp1).getUnit();
		}
		double scale2 = 1.0;
		Unit temp2 = ucarUnit2;
		while (temp2 instanceof ScaledUnit){
			scale2 *= ((ScaledUnit)temp2).getScale();
			temp2 = ((ScaledUnit)temp2).getUnit();
		}
		if (scale1==scale2){
			return true;
		}else{
			double maxAbs = Math.max(Math.abs(scale1),Math.abs(scale2));
			if (Math.abs(scale1-scale2)/maxAbs > 1e-10){
				return false;
			}else{
				return true;
			}
		} 
	}
	
	if (ucarUnit2.isCompatible(ucarUnit1)){
		//
		// checks for scaling differences
		//
		try {
			float mult = ucarUnit2.convertTo(2.0f, ucarUnit1);
			if (mult != 2.0) {
				return false;
			}
		} catch (UnitException e) {
			e.printStackTrace();
			throw new VCUnitException("Unable to convert units: " + ucarUnit2 + " to " + ucarUnit1.getSymbol() + ": " + e.getMessage());
		}
		//
		// checks for reciprocals ... oddly, isCompatable() returns true for reciprocals.
		//
		if (ucarUnit1.getDerivedUnit().getQuantityDimension().isReciprocalOf(ucarUnit2.getDerivedUnit().getQuantityDimension())){
			return false;
		}		
		return true;
	} else {
		return false;
	}
}


static InternalUnitDefinition getInstance(String symbol) {
		if (symbol == null){
			throw new IllegalArgumentException("symbol was null");
		}
		if (TBD_SYMBOL.equals(symbol)) {
			return UNIT_TBD;
		}
		if (symbol.equalsIgnoreCase("dimensionless")) {
			return UNIT_DIMENSIONLESS;
		}
		Unit dUnit;
		try {
			StandardUnitFormat standardUnitFormat = new StandardUnitFormat(new java.io.ByteArrayInputStream(symbol.trim().getBytes()));
            ucar.units_vcell.UnitDB unitDB = ucar.units_vcell.UnitSystemManager.instance().getUnitDB();
			dUnit = standardUnitFormat.unitSpec(unitDB);
			ucar.units_vcell.Factor factors[] = dUnit.getDerivedUnit().getDimension().getFactors();
			for (int i = 0; i < factors.length; i++){
				if (factors[i].getBase() instanceof ucar.units_vcell.UnknownUnit){
					throw new VCUnitException("invalid symbol '"+factors[i].getBase()+"'");
				}
			}
		} catch (ucar.units_vcell.ParseException e) {
			e.printStackTrace();
			throw new VCUnitException("Unable to get unit: " + symbol + ": " + e.getMessage());
		} catch (UnitException e) {
			e.printStackTrace();
			throw new VCUnitException("Unable to get unit: " + symbol + ": " + e.getMessage());
		}
		return getInstance(dUnit);
	}


/**
 * Creation date: (4/15/2004 1:39:26 PM)
 * @return cbit.vcell.units.InternalUnitDefinition
 * @param InternalUnitDefinition cbit.vcell.units.InternalUnitDefinition
 * @deprecated please don't use, we should hide the underlying implementation (CellML translator needs it now)
 */
static InternalUnitDefinition getInstance(Unit ucarUnit) {
	//
	// look up in list of "standard" unit definitions
	//
	for (int i = 0; i < defs.size(); i++) {
		InternalUnitDefinition temp = (InternalUnitDefinition)defs.get(i);
		if (equivalent(ucarUnit,temp.getUcarUnit())){
			return temp;
		}
	}
	//
	// cleaning up scales of the form:
	//
	//      "9.999999999999999E-31 1000000 m.s" to have the form "1E-24 m.s"
	//
	// thus, it combines multiple scales and rounds to 12 significant decimal digits
	//
	try {
		if (ucarUnit instanceof ScaledUnit) {
			java.util.StringTokenizer tokenizer = new java.util.StringTokenizer(ucarUnit.toString(), " ");
			double scale = 1;  
			int cnt = 0;
			String nonNum = null;
			while (tokenizer.hasMoreTokens()) {
				String token = tokenizer.nextToken();
				try {
					scale *= Double.parseDouble(token);
					cnt++;
				} catch (NumberFormatException e) {
					nonNum = token;
					break;
				}
			}
			double roundedScale = round(scale);

			if (roundedScale != scale || cnt > 1) {
				String newSymbol = String.valueOf(roundedScale);
				if (nonNum!=null){
					newSymbol += " "+nonNum;
				}
				StandardUnitFormat standardUnitFormat = new StandardUnitFormat(new java.io.ByteArrayInputStream(newSymbol.trim().getBytes()));
	            ucar.units_vcell.UnitDB unitDB = ucar.units_vcell.UnitSystemManager.instance().getUnitDB();
				ucarUnit = standardUnitFormat.unitSpec(unitDB);
			}
		}
	} catch (ParseException e) {
		e.printStackTrace();
		throw new VCUnitException("Unable to get unit: " + ucarUnit.toString() + ": " + e.getMessage());
	} catch (UnitException e) {
		e.printStackTrace();
		throw new VCUnitException("Unable to get unit: " + ucarUnit.toString() + ": " + e.getMessage());
	}
	//
	// not found, make new InternalUnitDefinition
	//
	return new InternalUnitDefinition(ucarUnit);
}


	public InternalUnitDefinition getInverse() {
		
		if (isTBD()){
			throw new VCUnitException("cannot invert TBD units");
		}
		Unit newUcarUnit;
		try {
			newUcarUnit = getUcarUnit().raiseTo(new RationalNumber(-1));
		} catch (UnitException e) {
			e.printStackTrace();
			throw new VCUnitException("Unable to get inverse: " + ucarUnit.getSymbol() + ": " + e.getMessage());
		}

		return getInstance(newUcarUnit);
	}


	public String getSymbol() {

		if (isTBD()) {
			return TBD_SYMBOL;
		}
		if (forcedSymbol!=null){
			return forcedSymbol;
		}
		return ucarUnit.toString();
		//String symbol = "";
		//if (ucarUnit instanceof ScaledUnit){
			//try {
				//symbol += prefixDB.getPrefixByValue(((ScaledUnit)ucarUnit).getScale());
			//}catch (ucar.units_vcell.PrefixDBAccessException e){
				//e.printStackTrace(System.out);
			//}
			//return symbol + ((ScaledUnit)ucarUnit).getUnit().getSymbol();
		//}
		//return ucarUnit.getDerivedUnit().getSymbol();
	}


/**
 * @deprecated please don't use, CellML translator needs this, but it will be removed when possible
 */
public Unit getUcarUnit() {
	return ucarUnit;
}


	//allows a milder approach to unit conversion. 
	public boolean isCompatible(InternalUnitDefinition anotherUnit) {
		
		Unit ucarUnit1 = getUcarUnit();
		Unit ucarUnit2 = anotherUnit.getUcarUnit();

		if (isTBD() || anotherUnit.isTBD()) {
			return false;
		}
		if (ucarUnit2.isDimensionless() && ucarUnit1.isDimensionless()){
			return true;
		}
		if (ucarUnit2.isDimensionless() || ucarUnit1.isDimensionless()){
			return false;
		}
		if (ucarUnit2.isCompatible(ucarUnit1)){
			//
			// checks for reciprocals ... oddly, isCompatable() returns true for reciprocals.
			//
			if (ucarUnit1.getDerivedUnit().getQuantityDimension().isReciprocalOf(ucarUnit2.getDerivedUnit().getQuantityDimension())){
				return false;
			}		
			return true;
		} else {
			return false;
		}
	}


public boolean isTBD() {
	return (ucarUnit == null);
}


	public InternalUnitDefinition multiplyBy(InternalUnitDefinition otherUnitDef) throws VCUnitException {

		if (isTBD() || otherUnitDef.isTBD()){
			throw new VCUnitException("cannot multiply TBD units");
		}
		Unit newUcarUnit;
		try {
			newUcarUnit = getUcarUnit().multiplyBy(otherUnitDef.getUcarUnit());
		} catch (UnitException e) {
			e.printStackTrace();
			throw new VCUnitException("Unable to multiply units: " + ucarUnit.getSymbol() + " and " + otherUnitDef.getSymbol() + ": " + e.getMessage());
		}

		return getInstance(newUcarUnit);
	}


	public InternalUnitDefinition raiseTo(RationalNumber exp) {

		if (isTBD()){
			throw new VCUnitException("cannot raiseTo with TBD units");
		}
		Unit newUcarUnit;
		try {
			newUcarUnit =  getUcarUnit().raiseTo(exp);
		} catch (UnitException e) {
			e.printStackTrace();
			throw new VCUnitException("Failed to change exponent for: " + ucarUnit.getSymbol() + " " + exp + ": " + e.getMessage());
		}

		return getInstance(newUcarUnit);        
	}


/**
 * Insert the method's description here.
 * Creation date: (6/13/2004 3:03:36 PM)
 * @return double
 * @param value double
 */
public static double round(double value) {
	try {
		double roundedValue = numberFormatForRounding.parse(numberFormatForRounding.format(value)).doubleValue();
		return roundedValue;
	}catch (java.text.ParseException e){
		e.printStackTrace(System.out);
		return value;
	}
}


/**
 * Insert the method's description here.
 * Creation date: (4/15/2004 12:12:09 AM)
 */
public void show() {
	System.out.println("");
	System.out.println("InternalUnitDefinition@"+hashCode());
	System.out.println("symbol="+getSymbol());
	showUChar("","unit",getUcarUnit());
	System.out.println("");
}


/**
 * Insert the method's description here.
 * Creation date: (4/14/2004 5:48:29 PM)
 * @param obj java.lang.Object
 */
private static void showUChar(String pad, String attribute, Object obj) {
	if (obj == null || 
		obj instanceof String || 
		obj instanceof Boolean || 
		obj instanceof Double || 
		obj instanceof Number ||
		obj instanceof Factor ||           // to make much less verbose
		obj instanceof QuantityDimension){ // to make much less verbose
		System.out.println(pad+attribute+" = "+obj);
		return;
	}else{
		System.out.println(pad+attribute+" = "+((obj!=null)?(obj.getClass().getName()+"@"+Integer.toHexString(obj.hashCode())+" '"+obj+"'"):("null")));
	}
	pad += "   ";
	if (obj instanceof ucar.units_vcell.UnitImpl){
		ucar.units_vcell.UnitImpl unitImpl = (ucar.units_vcell.UnitImpl)obj;
		if (unitImpl.getDerivedUnit()!=obj){                           // stops infinite recursion
			showUChar(pad,"derivedUnit",unitImpl.getDerivedUnit());
		}
		showUChar(pad,"name",unitImpl.getName());
		//showUChar(pad,"plural",unitImpl.getPlural());
		showUChar(pad,"symbol",unitImpl.getSymbol());
		showUChar(pad,"unitname",unitImpl.getUnitName());
		showUChar(pad,"isDimensionless",new Boolean(unitImpl.isDimensionless()));
		if (unitImpl instanceof DerivedUnitImpl){
			DerivedUnitImpl derivedUnitImpl = (DerivedUnitImpl)unitImpl;
			if (!(derivedUnitImpl instanceof BaseUnit)){                  // stops infinite recursion
				showUChar(pad,"unitDimension",derivedUnitImpl.getDimension());
			}
			showUChar(pad,"quantityDimension",derivedUnitImpl.getQuantityDimension());
			if (derivedUnitImpl instanceof ucar.units_vcell.BaseUnit){
				ucar.units_vcell.BaseUnit baseUnit = (ucar.units_vcell.BaseUnit)derivedUnitImpl;
				showUChar(pad,"baseQuantity",baseUnit.getBaseQuantity());
				showUChar(pad,"id",baseUnit.getID());
			}
		}else if (unitImpl instanceof OffsetUnit){
			OffsetUnit offsetUnit = (OffsetUnit)unitImpl;
			showUChar(pad,"offset",new Double(offsetUnit.getOffset()));
			if (offsetUnit.getUnit() == offsetUnit.getDerivedUnit()){
				showUChar(pad,"unit",offsetUnit.getUnit().getClass().getName()+"@"+Integer.toHexString(offsetUnit.hashCode())+"  '"+obj+"'  <<<<REPEATED>>>>");
			}else{
				showUChar(pad,"unit",offsetUnit.getUnit());
			}
		}else if (unitImpl instanceof ScaledUnit){
			ScaledUnit scaledUnit = (ScaledUnit)unitImpl;
			showUChar(pad,"scale",new Double(scaledUnit.getScale()));
			if (scaledUnit.getUnit() == scaledUnit.getDerivedUnit()){
				showUChar(pad,"unit",scaledUnit.getUnit().getClass().getName()+"@"+Integer.toHexString(scaledUnit.hashCode())+"  '"+obj+"'  <<<<REPEATED>>>>");
			}else{
				showUChar(pad,"unit",scaledUnit.getUnit());
			}
		}else if (unitImpl instanceof TimeScaleUnit){
			TimeScaleUnit timeScaleUnit = (TimeScaleUnit)unitImpl;
			showUChar(pad,"origin",timeScaleUnit.getOrigin());
			if (timeScaleUnit.getUnit() == timeScaleUnit.getDerivedUnit()){
				showUChar(pad,"unit",timeScaleUnit.getUnit().getClass().getName()+"@"+Integer.toHexString(timeScaleUnit.hashCode())+"  '"+obj+"'  <<<<REPEATED>>>>");
			}else{
				showUChar(pad,"unit",timeScaleUnit.getUnit());
			}
		}
	}else if (obj instanceof UnitName){
		UnitName unitName = (UnitName)obj;
		showUChar(pad,"name",unitName.getName());
		//showUChar(pad,"plural",unitName.getPlural());
		showUChar(pad,"symbol",unitName.getSymbol());
	}else if (obj instanceof Dimension){
		Dimension dimension = (Dimension)obj;
		Factor factors[] = dimension.getFactors();
		if (factors==null || factors.length==0){
			showUChar(pad,"factors",null);
		}else{
			for (int i = 0; i < factors.length; i++){
				showUChar(pad,"factors["+i+"]",factors[i]);
			}
		}
		//showUChar(pad,"rank",new Integer(dimension.getRank()));  // adds nothing
		showUChar(pad,"isDimensionless",new Boolean(dimension.isDimensionless()));
		if (dimension instanceof UnitDimension){
			UnitDimension unitDimension = (UnitDimension)dimension;
			showUChar(pad,"quantityDimension",unitDimension.getQuantityDimension());
		}			
	}else if (obj instanceof Factor){
		Factor factor = (Factor)obj;
		showUChar(pad,"base",factor.getBase().toString());  // to make much less verbose
		showUChar(pad,"exponent",factor.getExponent());
		showUChar(pad,"ID",factor.getID());
		showUChar(pad,"isDimensionless",new Boolean(factor.isDimensionless()));
	}else if (obj instanceof Base){
		Base base = (Base)obj;
		showUChar(pad,"ID",base.getID());
		showUChar(pad,"isDimensionless",new Boolean(base.isDimensionless()));
		if (base instanceof BaseQuantity){
			BaseQuantity baseQuantity = (BaseQuantity)base;
			showUChar(pad,"name",baseQuantity.getName());
			showUChar(pad,"symbol",baseQuantity.getSymbol());
		}
	}
		
}

public String toString() {
	return getClass().getName() + "@" + Integer.toHexString(hashCode()) +" : symbol=["+getSymbol()+"]";
}

}
