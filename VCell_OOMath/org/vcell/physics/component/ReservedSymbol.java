package org.vcell.physics.component;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/

import java.io.*;

import org.vcell.expression.ExpressionException;
import org.vcell.expression.ExpressionFactory;
import org.vcell.expression.IExpression;
import org.vcell.expression.NameScope;
import org.vcell.expression.SymbolTableEntry;
import org.vcell.units.VCUnitDefinition;

import edu.uchc.vcell.expression.internal.*;

public class ReservedSymbol extends PhysicalSymbol {
   public final static ReservedSymbol TIME 	 = new ReservedSymbol("t","time",VCUnitDefinition.UNIT_s,null);
   public final static ReservedSymbol X    	 = new ReservedSymbol("x","x coord",VCUnitDefinition.UNIT_um,null);
   public final static ReservedSymbol Y    	 = new ReservedSymbol("y","y coord",VCUnitDefinition.UNIT_um,null);
   public final static ReservedSymbol Z    	 = new ReservedSymbol("z","z coord",VCUnitDefinition.UNIT_um,null);
   public final static ReservedSymbol TEMPERATURE = new ReservedSymbol("_T_","temperature",VCUnitDefinition.UNIT_K,null);
   public final static ReservedSymbol FARADAY_CONSTANT = new ReservedSymbol("_F_","Faraday const",VCUnitDefinition.UNIT_C_per_mol,new Double(9.648e4));
   public final static ReservedSymbol FARADAY_CONSTANT_NMOLE = new ReservedSymbol("_F_nmol_","Faraday const",VCUnitDefinition.UNIT_C_per_nmol,new Double(9.648e-5));
   public final static ReservedSymbol N_PMOLE = new ReservedSymbol("_N_pmol_","Avagadro Num (scaled)",VCUnitDefinition.UNIT_molecules_per_pmol,new Double(6.02e11));
   public final static ReservedSymbol K_GHK = new ReservedSymbol("_K_GHK_","GHK unit scale",VCUnitDefinition.getInstance("1e9"),new Double(1e-9));
   public final static ReservedSymbol GAS_CONSTANT = new ReservedSymbol("_R_","Gas Constant",VCUnitDefinition.UNIT_mV_C_per_K_per_mol,new Double(8314.0));
   public final static ReservedSymbol KMOLE = new ReservedSymbol("KMOLE","Flux unit conversion",VCUnitDefinition.UNIT_uM_um3_per_molecules,new Double(1.0/602.0));
   public final static ReservedSymbol KMILLIVOLTS = new ReservedSymbol("K_millivolts_per_volt","voltage scale",VCUnitDefinition.getInstance("1e-3"),new Double(1000));
   private Double constantValue = null;
   private String description = null;


private ReservedSymbol(String argName, String argDescription, org.vcell.units.VCUnitDefinition argUnitDefinition, Double argConstantValue){
	super(argName, argUnitDefinition);
	this.constantValue = argConstantValue;
	this.description = argDescription;
}         

/**
 * Insert the method's description here.
 * Creation date: (2/19/2002 4:23:00 PM)
 * @return boolean
 * @param obj java.lang.Object
 */
public boolean equals(Object obj) {
	if (!(obj instanceof ReservedSymbol)){
		return false;
	}
	ReservedSymbol rs = (ReservedSymbol)obj;
	if (!rs.getName().equals(getName())){
		return false;
	}
	return true;
}


public static ReservedSymbol fromString(String symbolName) {
	if (symbolName==null){
		return null;
	}else if (symbolName.equals(TIME.getName())){
		return TIME;
	}else if (symbolName.equals(X.getName())){
		return X;
	}else if (symbolName.equals(Y.getName())){
		return Y;
	}else if (symbolName.equals(Z.getName())){
		return Z;
	}else if (symbolName.equals(TEMPERATURE.getName())){
		return TEMPERATURE;
	}else if (symbolName.equals(GAS_CONSTANT.getName())){
		return GAS_CONSTANT;
	}else if (symbolName.equals(FARADAY_CONSTANT.getName())){
		return FARADAY_CONSTANT;
	}else if (symbolName.equals(FARADAY_CONSTANT_NMOLE.getName())){
		return FARADAY_CONSTANT_NMOLE;
	}else if (symbolName.equals(KMOLE.getName())){
		return KMOLE;
	}else if (symbolName.equals(N_PMOLE.getName())){
		return N_PMOLE;
	}else if (symbolName.equals(KMILLIVOLTS.getName())){
		return KMILLIVOLTS;
	}else if (symbolName.equals(K_GHK.getName())){
		return K_GHK;
	}else{
		return null;
	}
}         


/**
 * This method was created in VisualAge.
 * @return double
 */
public double getConstantValue() throws ExpressionException {
//	if (constantValue==null){
		throw new ExpressionException(getName()+" is not constant");
//	}else{
//		return constantValue.doubleValue();
//	}
}


   public final String getDescription() 
   { 
	  return description; 
   }      


/**
 * Insert the method's description here.
 * Creation date: (2/19/2002 4:24:46 PM)
 * @return int
 */
public int hashCode() {
	return getName().hashCode();
}


/**
 * This method was created by a SmartGuide.
 * @return boolean
 */
public boolean isConstant() {
	return false;  //constantValue!=null;
}


/**
 * This method was created by a SmartGuide.
 * @return boolean
 */
public boolean isFARADAY_CONSTANT() {
	if (getName().equals(FARADAY_CONSTANT.getName())){
		return true;
	}else{
		return false;
	}		
}


/**
 * This method was created by a SmartGuide.
 * @return boolean
 */
public boolean isFARADAY_CONSTANT_NMOLE() {
	if (getName().equals(FARADAY_CONSTANT_NMOLE.getName())){
		return true;
	}else{
		return false;
	}		
}


/**
 * This method was created by a SmartGuide.
 * @return boolean
 */
public boolean isGAS_CONSTANT() {
	if (getName().equals(GAS_CONSTANT.getName())){
		return true;
	}else{
		return false;
	}		
}


/**
 * This method was created by a SmartGuide.
 * @return boolean
 */
public boolean isK_GHK() {
	if (getName().equals(K_GHK.getName())){
		return true;
	}else{
		return false;
	}		
}


/**
 * This method was created by a SmartGuide.
 * @return boolean
 */
public boolean isKMILLIVOLTS() {
	if (getName().equals(KMILLIVOLTS.getName())){
		return true;
	}else{
		return false;
	}		
}


/**
 * This method was created by a SmartGuide.
 * @return boolean
 */
public boolean isKMOLE() {
	if (getName().equals(KMOLE.getName())){
		return true;
	}else{
		return false;
	}		
}


/**
 * This method was created by a SmartGuide.
 * @return boolean
 */
public boolean isN_PMOLE() {
	if (getName().equals(N_PMOLE.getName())){
		return true;
	}else{
		return false;
	}		
}


/**
 * This method was created by a SmartGuide.
 * @return boolean
 */
public boolean isTEMPERATURE() {
	if (getName().equals(TEMPERATURE.getName())){
		return true;
	}else{
		return false;
	}		
}


/**
 * This method was created by a SmartGuide.
 * @return boolean
 */
public boolean isTIME() {
	if (getName().equals(TIME.getName())){
		return true;
	}else{
		return false;
	}		
}


/**
 * This method was created by a SmartGuide.
 * @return boolean
 */
public boolean isX() {
	if (getName().equals(X.getName())){
		return true;
	}else{
		return false;
	}		
}


/**
 * This method was created by a SmartGuide.
 * @return boolean
 */
public boolean isY() {
	if (getName().equals(Y.getName())){
		return true;
	}else{
		return false;
	}		
}


/**
 * This method was created by a SmartGuide.
 * @return boolean
 */
public boolean isZ() {
	if (getName().equals(Z.getName())){
		return true;
	}else{
		return false;
	}		
}


   public String toString()
   {
	   return getName();
   }      
}