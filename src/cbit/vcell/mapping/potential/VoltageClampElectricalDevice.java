/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.mapping.potential;
import cbit.vcell.mapping.AbstractMathMapping;
import cbit.vcell.mapping.ElectricalStimulus.ElectricalStimulusParameterType;
import cbit.vcell.mapping.ParameterContext.LocalParameter;
import cbit.vcell.mapping.VoltageClampStimulus;
import cbit.vcell.model.ModelUnitSystem;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.units.VCUnitDefinition;
/**
 * Insert the type's description here.
 * Creation date: (4/7/2004 11:00:15 AM)
 * @author: Jim Schaff
 */
public class VoltageClampElectricalDevice extends ElectricalDevice {
	private cbit.vcell.mapping.VoltageClampStimulus voltageClampStimulus = null;

/**
 * Insert the method's description here.
 * Creation date: (4/7/2004 11:00:30 AM)
 */
public VoltageClampElectricalDevice(VoltageClampStimulus argVoltageClampStimulus, AbstractMathMapping argMathMapping) throws ExpressionException {
	super("device_"+argVoltageClampStimulus.getName(), argMathMapping);
	this.voltageClampStimulus = argVoltageClampStimulus;
	
	ElectricalDevice.ElectricalDeviceParameter parameters[] = new ElectricalDevice.ElectricalDeviceParameter[3];
	
	ModelUnitSystem modelUnitSystem = mathMapping.getSimulationContext().getModel().getUnitSystem();
	VCUnitDefinition currentUnit = modelUnitSystem.getCurrentUnit();
	parameters[0] = new ElectricalDeviceParameter(
							DefaultNames[ROLE_TotalCurrent],
							new Expression(DefaultNames[ROLE_TransmembraneCurrent]),
							ROLE_TotalCurrent,
							currentUnit);
	
	parameters[1] = new ElectricalDeviceParameter(
							DefaultNames[ROLE_TransmembraneCurrent],
							null,
							ROLE_TransmembraneCurrent,
							currentUnit);

	LocalParameter voltageParm = voltageClampStimulus.getVoltageParameter();
	parameters[2] = new ElectricalDeviceParameter(
							voltageParm.getName(),
							new Expression(voltageParm.getExpression()),
							ROLE_Voltage,
							voltageParm.getUnitDefinition());
	//
	// add any user-defined parameters
	//
	LocalParameter[] stimulusParameters = voltageClampStimulus.getLocalParameters();
	for (int i = 0;stimulusParameters!=null && i <stimulusParameters.length; i++){
		if (stimulusParameters[i].getRole() == ElectricalStimulusParameterType.UserDefined){
			ElectricalDeviceParameter newParam = new ElectricalDeviceParameter(stimulusParameters[i].getName(),new Expression(stimulusParameters[i].getExpression()),ROLE_UserDefined,stimulusParameters[i].getUnitDefinition());
			parameters = (ElectricalDeviceParameter[])org.vcell.util.BeanUtils.addElement(parameters,newParam);
		}
	}
	
	setParameters(parameters);
}


/**
 * Insert the method's description here.
 * Creation date: (4/7/2004 11:32:53 AM)
 * @return boolean
 */
public boolean getCalculateVoltage() {
	return true;
}


/**
 * Insert the method's description here.
 * Creation date: (4/7/2004 11:32:53 AM)
 * @return boolean
 */
public boolean getResolved() {
	return false;
}


/**
 * Insert the method's description here.
 * Creation date: (4/7/2004 2:51:28 PM)
 * @return java.lang.String
 */
public SymbolTableEntry getVoltageSymbol() {
	return getParameterFromRole(ROLE_Voltage);
}


/**
 * Insert the method's description here.
 * Creation date: (4/7/2004 11:38:39 AM)
 * @return cbit.vcell.mapping.VoltageClampStimulus
 */
public cbit.vcell.mapping.VoltageClampStimulus getVoltageClampStimulus() {
	return voltageClampStimulus;
}


/**
 * Insert the method's description here.
 * Creation date: (4/7/2004 11:32:53 AM)
 * @return boolean
 */
public boolean hasCapacitance() {
	return false;
}


/**
 * Insert the method's description here.
 * Creation date: (4/7/2004 11:32:53 AM)
 * @return boolean
 */
public boolean isVoltageSource() {
	return true;
}
}
