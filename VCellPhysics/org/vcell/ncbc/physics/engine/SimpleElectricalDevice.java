package org.vcell.ncbc.physics.engine;
import org.vcell.expression.ExpressionBindingException;
import org.vcell.expression.ExpressionFactory;
import org.vcell.expression.IExpression;

import cbit.vcell.math.Function;
import cbit.vcell.math.VolVariable;
import edu.uchc.vcell.expression.internal.*;
/**
 * Insert the type's description here.
 * Creation date: (2/12/2002 1:01:13 PM)
 * @author: Jim Schaff
 */
public class SimpleElectricalDevice extends ElectricalDevice {
	private double capacitancePicoFarads = 0; // eg. 4.3pL Cell has a 12.5pF capacitance 
	// (specific capacitance is 1uF/sqcm from Hille book, or 0.01pF/squm)
	// 

	// eg. 6.8 T-Type calcium channel for whole cell - gonadatroph.
	// eg. 0.008nS for whole cell leak.
	private IExpression currentSourceExp = ExpressionFactory.createExpression(0.0);
	private boolean fieldResolved = false;
	private boolean fieldCalculateVoltage = true;
	private boolean fieldIsVoltageSource = false;

public SimpleElectricalDevice(String argName, String argVoltageName, Function initialVoltageFunction, double argCapacitance_pF, IExpression argCurrentSource, boolean argResolved, boolean argCalculateVoltage, boolean argIsVoltageSource) throws ExpressionBindingException {
	super(argName,argVoltageName,initialVoltageFunction);
	this.capacitancePicoFarads = argCapacitance_pF;
	setCurrentSourceExpression(argCurrentSource);
	fieldResolved = argResolved;
	fieldCalculateVoltage = argCalculateVoltage;
	fieldIsVoltageSource = argIsVoltageSource;
}


/**
 * Insert the method's description here.
 * Creation date: (3/2/2002 9:42:55 PM)
 * @return boolean
 */
public boolean getCalculateVoltage() {
	return fieldCalculateVoltage;
}


/**
 * Insert the method's description here.
 * Creation date: (2/12/2002 1:29:31 PM)
 * @return double
 */
public double getCapacitance_pF() {
	return capacitancePicoFarads;
}


/**
 * Insert the method's description here.
 * Creation date: (2/12/2002 1:31:24 PM)
 * @return cbit.vcell.parser.Expression
 */
public IExpression getCurrentSourceExpression() {
	return currentSourceExp;
}


/**
 * Insert the method's description here.
 * Creation date: (3/2/2002 9:39:39 PM)
 * @return boolean
 */
public boolean getResolved() {
	return fieldResolved;
}


/**
 * Insert the method's description here.
 * Creation date: (2/19/2002 11:37:04 AM)
 * @return boolean
 */
public boolean hasCapacitance() {
	return capacitancePicoFarads!=0.0;
}


/**
 * Insert the method's description here.
 * Creation date: (4/22/2002 5:40:05 PM)
 * @return boolean
 */
public boolean isVoltageSource() {
	return fieldIsVoltageSource;
}


/**
 * Insert the method's description here.
 * Creation date: (2/12/2002 1:30:52 PM)
 * @param exp cbit.vcell.parser.Expression
 */
private void setCurrentSourceExpression(IExpression argExp) throws ExpressionBindingException {
	if (argExp != null){
		IExpression exp = ExpressionFactory.createExpression(argExp);
		exp.bindExpression(this);
		this.currentSourceExp = exp;
	}else{
		this.currentSourceExp = null;
	}
}


/**
 * Insert the method's description here.
 * Creation date: (2/12/2002 4:02:31 PM)
 * @return java.lang.String
 */
public String toString() {
	return getName();
}
}