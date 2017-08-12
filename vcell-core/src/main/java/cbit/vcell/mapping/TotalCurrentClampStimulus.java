/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.mapping;
import java.beans.PropertyVetoException;

import cbit.vcell.mapping.ParameterContext.LocalParameter;
import cbit.vcell.model.Parameter;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.units.VCUnitDefinition;
/**
 * Insert the type's description here.
 * Creation date: (4/8/2002 1:39:52 PM)
 * @author: Anuradha Lakshminarayana
 */
public class TotalCurrentClampStimulus extends ElectricalStimulus {
/**
 * CurrentClampStimulus constructor comment.
 * @param argElectrode cbit.vcell.mapping.Electrode
 * @param argName java.lang.String
 * @param argVoltName java.lang.String
 * @param argCurrName java.lang.String
 * @param argAnnotation java.lang.String
 */
	public TotalCurrentClampStimulus(Electrode argElectrode, String argName, Expression argCurrExpr, SimulationContext argSimulationContext) {
		super(argElectrode, argName, argSimulationContext);


		try {
			argCurrExpr.bindExpression(parameterContext);
		}catch (ExpressionBindingException e){
			e.printStackTrace(System.out);
			//throw new RuntimeException(e.getMessage());
		}
		LocalParameter[] localParameters = new LocalParameter[1];
		VCUnitDefinition currentUnit = argSimulationContext.getModel().getUnitSystem().getCurrentUnit();
		localParameters[0] = parameterContext.new LocalParameter(
				ElectricalStimulusParameterType.TotalCurrent.defaultName, argCurrExpr, 
				ElectricalStimulusParameterType.TotalCurrent, currentUnit, 
				"applied current");
		
		try {
			parameterContext.setLocalParameters(localParameters);
		} catch (PropertyVetoException | ExpressionBindingException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}


	public TotalCurrentClampStimulus(TotalCurrentClampStimulus otherStimulus, SimulationContext argSimulationContext) {
		super(otherStimulus, argSimulationContext);
	}


/**
 * Checks for internal representation of objects, not keys from database
 * @return boolean
 * @param obj java.lang.Object
 */
public boolean compareEqual(org.vcell.util.Matchable obj) {
	if (obj instanceof TotalCurrentClampStimulus){
		TotalCurrentClampStimulus ccs = (TotalCurrentClampStimulus)obj;

		if (!super.compareEqual0(obj)){
			return false;
		}
		
		return true;
	}else{
		return false;
	}

}


public LocalParameter getCurrentParameter() {
	return parameterContext.getLocalParameterFromRole(ElectricalStimulusParameterType.TotalCurrent);
}


@Override
public Parameter getProtocolParameter() {	
	return getCurrentParameter();
}
}
