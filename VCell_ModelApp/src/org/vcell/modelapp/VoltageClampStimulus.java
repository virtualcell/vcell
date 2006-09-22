package org.vcell.modelapp;
import cbit.vcell.parser.*;
/**
 * Insert the type's description here.
 * Creation date: (4/8/2002 11:45:12 AM)
 * @author: Anuradha Lakshminarayana
 */
public class VoltageClampStimulus extends ElectricalStimulus {
/**
 * VoltageClampStimulus constructor comment.
 * @param argElectrode cbit.vcell.mapping.Electrode
 * @param argName java.lang.String
 * @param argVoltName java.lang.String
 * @param argCurrName java.lang.String
 * @param argAnnotation java.lang.String
 */
public VoltageClampStimulus(Electrode argElectrode, String argName, Expression argVoltExpr, SimulationContext argSimulationContext) {
	super(argElectrode, argName, argSimulationContext);
	try {
		getCurrentParameter().setExpression(new Expression(0.0));
		getCurrentParameter().setDescription("measured current density");
		getVoltageParameter().setExpression(argVoltExpr);
		getVoltageParameter().setDescription("applied voltage");
		argVoltExpr.bindExpression(this);
	}catch (ExpressionBindingException e){
		e.printStackTrace(System.out);
		//throw new RuntimeException(e.getMessage());
	}catch (java.beans.PropertyVetoException e){
		e.printStackTrace(System.out);
		throw new RuntimeException(e.getMessage());
	}
}


/**
 * Checks for internal representation of objects, not keys from database
 * @return boolean
 * @param obj java.lang.Object
 */
public boolean compareEqual(cbit.util.Matchable obj) {
	if (obj instanceof VoltageClampStimulus){
		VoltageClampStimulus vcs = (VoltageClampStimulus)obj;

		if (!super.compareEqual0(obj)){
			return false;
		}
		
		return true;
	}else{
		return false;
	}

}
}