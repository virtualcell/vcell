package cbit.vcell.mapping;
import java.beans.PropertyVetoException;
import java.util.ArrayList;

import cbit.vcell.mapping.ParameterContext.LocalParameter;
import cbit.vcell.parser.*;
import cbit.vcell.units.VCUnitDefinition;
/**
 * Insert the type's description here.
 * Creation date: (4/8/2002 1:39:52 PM)
 * @author: Anuradha Lakshminarayana
 */
public class CurrentDensityClampStimulus extends ElectricalStimulus {
/**
 * CurrentClampStimulus constructor comment.
 * @param argElectrode cbit.vcell.mapping.Electrode
 * @param argName java.lang.String
 * @param argVoltName java.lang.String
 * @param argCurrName java.lang.String
 * @param argAnnotation java.lang.String
 */
	public CurrentDensityClampStimulus(Electrode argElectrode, String argName, Expression argCurrExpr, SimulationContext argSimulationContext) {
		super(argElectrode, argName, argSimulationContext);


		try {
			argCurrExpr.bindExpression(parameterContext);
		}catch (ExpressionBindingException e){
			e.printStackTrace(System.out);
			//throw new RuntimeException(e.getMessage());
		}
		LocalParameter[] localParameters = new LocalParameter[1];
		
		localParameters[0] = parameterContext.new LocalParameter(
				DefaultNames[ROLE_CurrentDensity], argCurrExpr, 
				ROLE_CurrentDensity, VCUnitDefinition.UNIT_pA_per_um2, 
				"applied current density (deprecated)");
		

		try {
			parameterContext.setLocalParameters(localParameters);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}


	public CurrentDensityClampStimulus(CurrentDensityClampStimulus otherStimulus, SimulationContext argSimulationContext) {
		super(otherStimulus, argSimulationContext);
	}


/**
 * Checks for internal representation of objects, not keys from database
 * @return boolean
 * @param obj java.lang.Object
 */
public boolean compareEqual(org.vcell.util.Matchable obj) {
	if (obj instanceof CurrentDensityClampStimulus){
		CurrentDensityClampStimulus ccs = (CurrentDensityClampStimulus)obj;

		if (!super.compareEqual0(obj)){
			return false;
		}
		
		return true;
	}else{
		return false;
	}

}


public LocalParameter getCurrentDensityParameter() {
	return parameterContext.getLocalParameterFromRole(ElectricalStimulus.ROLE_CurrentDensity);
}
}