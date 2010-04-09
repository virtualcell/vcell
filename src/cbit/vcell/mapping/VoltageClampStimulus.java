package cbit.vcell.mapping;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;

import cbit.vcell.mapping.ParameterContext.LocalParameter;
import cbit.vcell.model.Parameter;
import cbit.vcell.parser.*;
import cbit.vcell.units.VCUnitDefinition;
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
		argVoltExpr.bindExpression(parameterContext);
	}catch (ExpressionBindingException e){
		e.printStackTrace(System.out);
		//throw new RuntimeException(e.getMessage());
	}
	LocalParameter[] localParameters = new LocalParameter[1];
	
	localParameters[0] = parameterContext.new LocalParameter(
			DefaultNames[ROLE_Voltage], argVoltExpr, 
			ROLE_Voltage, VCUnitDefinition.UNIT_mV, 
			"applied voltage");
	
	try {
		parameterContext.setLocalParameters(localParameters);
	} catch (PropertyVetoException e) {
		e.printStackTrace();
		throw new RuntimeException(e.getMessage());
	}
}


public VoltageClampStimulus(VoltageClampStimulus otherStimulus, SimulationContext argSimulationContext) {
	super(otherStimulus, argSimulationContext);
}


/**
 * Checks for internal representation of objects, not keys from database
 * @return boolean
 * @param obj java.lang.Object
 */
public boolean compareEqual(org.vcell.util.Matchable obj) {
	if (obj instanceof VoltageClampStimulus){
		if (!super.compareEqual0(obj)){
			return false;
		}
		
		return true;
	}else{
		return false;
	}

}

public LocalParameter getVoltageParameter() {
	return parameterContext.getLocalParameterFromRole(ROLE_Voltage);
}


@Override
public Parameter getProtocolParameter() {
	return getVoltageParameter();
}


}