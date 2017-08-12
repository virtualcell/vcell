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
import cbit.vcell.mapping.MembraneMapping;
import cbit.vcell.model.ModelUnitSystem;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.SymbolTableEntry;

/**
 * Insert the type's description here.
 * Creation date: (4/7/2004 10:57:40 AM)
 * @author: Jim Schaff
 */
public class MembraneElectricalDevice extends ElectricalDevice {
	private MembraneMapping membraneMapping = null;
/**
 * Insert the method's description here.
 * Creation date: (4/7/2004 10:58:47 AM)
 */
public MembraneElectricalDevice(MembraneMapping argMembraneMapping, AbstractMathMapping argMathMapping) throws ExpressionException {
	super("device_"+argMembraneMapping.getMembrane().getName(), argMathMapping);
	this.membraneMapping = argMembraneMapping;

	ElectricalDevice.ElectricalDeviceParameter parameters[] = new ElectricalDevice.ElectricalDeviceParameter[3];

	ModelUnitSystem modelUnitSystem = mathMapping.getSimulationContext().getModel().getUnitSystem();
	parameters[0] = new ElectricalDeviceParameter(
							DefaultNames[ROLE_TotalCurrent],
							null, // (need to calculate)
							ROLE_TotalCurrent,
							modelUnitSystem.getCurrentUnit());

    parameters[1] = new ElectricalDeviceParameter(
						    DefaultNames[ROLE_TransmembraneCurrent],
							null, // given
							ROLE_TransmembraneCurrent,
							modelUnitSystem.getCurrentUnit());
    
	parameters[2] = new ElectricalDeviceParameter(
							DefaultNames[ROLE_Capacitance],
							Expression.mult(new Expression(membraneMapping.getSpecificCapacitanceParameter(),mathMapping.getNameScope()), new Expression(membraneMapping.getSizeParameter(),mathMapping.getNameScope())), // given
							ROLE_Capacitance,
							modelUnitSystem.getCapacitanceUnit());

	setParameters(parameters);
}
/**
 * Insert the method's description here.
 * Creation date: (4/7/2004 11:27:55 AM)
 * @return boolean
 */
public boolean getCalculateVoltage() {
	return getMembraneMapping().getCalculateVoltage();
}
/**
 * Insert the method's description here.
 * Creation date: (4/7/2004 2:57:26 PM)
 * @return java.lang.String
 */
public SymbolTableEntry getCapacitanceParameter() {
	return getParameterFromRole(ROLE_Capacitance);
}
/**
 * Insert the method's description here.
 * Creation date: (4/7/2004 11:42:18 AM)
 * @return cbit.vcell.mapping.MembraneMapping
 */
public cbit.vcell.mapping.MembraneMapping getMembraneMapping() {
	return membraneMapping;
}
/**
 * Insert the method's description here.
 * Creation date: (4/7/2004 2:55:34 PM)
 * @return java.lang.String
 */
public SymbolTableEntry getVoltageSymbol() {
	return membraneMapping.getMembrane().getMembraneVoltage();
}
/**
 * Insert the method's description here.
 * Creation date: (4/7/2004 11:27:55 AM)
 * @return boolean
 */
public boolean hasCapacitance() {
	return true;
}
/**
 * Insert the method's description here.
 * Creation date: (4/7/2004 11:27:55 AM)
 * @return boolean
 */
public boolean isVoltageSource() {
	return false;
}
}
