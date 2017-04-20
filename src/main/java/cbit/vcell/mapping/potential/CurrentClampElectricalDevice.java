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
import org.vcell.util.BeanUtils;

import cbit.vcell.mapping.AbstractMathMapping;
import cbit.vcell.mapping.CurrentDensityClampStimulus;
import cbit.vcell.mapping.ElectricalStimulus;
import cbit.vcell.mapping.MembraneMapping;
import cbit.vcell.mapping.ParameterContext.LocalParameter;
import cbit.vcell.mapping.StructureMapping.StructureMappingParameter;
import cbit.vcell.mapping.TotalCurrentClampStimulus;
import cbit.vcell.model.Feature;
import cbit.vcell.model.Membrane;
import cbit.vcell.model.Model.StructureTopology;
import cbit.vcell.model.ModelUnitSystem;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.units.VCUnitDefinition;

/**
 * Insert the type's description here.
 * Creation date: (4/7/2004 11:00:45 AM)
 * @author: Jim Schaff
 */
public class CurrentClampElectricalDevice extends ElectricalDevice {
	private ElectricalStimulus currentClampStimulus = null;

public CurrentClampElectricalDevice(TotalCurrentClampStimulus argTotalCurrentClampStimulus, AbstractMathMapping argMathMapping) throws ExpressionException {
	super("device_"+argTotalCurrentClampStimulus.getName(), argMathMapping);
	this.currentClampStimulus = argTotalCurrentClampStimulus;

	initializeParameters();
}

public CurrentClampElectricalDevice(CurrentDensityClampStimulus argCurrentDensityClampStimulus, AbstractMathMapping argMathMapping) throws ExpressionException {
	super("device_"+argCurrentDensityClampStimulus.getName(), argMathMapping);
	this.currentClampStimulus = argCurrentDensityClampStimulus;

	initializeParameters();
}


private void initializeParameters() throws ExpressionException {
	ElectricalDevice.ElectricalDeviceParameter parameters[] = new ElectricalDevice.ElectricalDeviceParameter[3];
	
	//
	// set the transmembrane current (total current, if necessary derive it from the current density).
	//
	ElectricalDeviceParameter transMembraneCurrent = null;
	ModelUnitSystem modelUnitSystem = mathMapping.getSimulationContext().getModel().getUnitSystem();
	VCUnitDefinition currentUnit = modelUnitSystem.getCurrentUnit();
	if (currentClampStimulus instanceof TotalCurrentClampStimulus){
		TotalCurrentClampStimulus stimulus = (TotalCurrentClampStimulus)currentClampStimulus;
		LocalParameter currentParameter = stimulus.getCurrentParameter();
		transMembraneCurrent = new ElectricalDeviceParameter(
				DefaultNames[ROLE_TransmembraneCurrent],
				new Expression(currentParameter.getExpression()),
				ROLE_TransmembraneCurrent,
				currentUnit);
	}else if (currentClampStimulus instanceof CurrentDensityClampStimulus){
		CurrentDensityClampStimulus stimulus = (CurrentDensityClampStimulus)currentClampStimulus;
		LocalParameter currentDensityParameter = stimulus.getCurrentDensityParameter();
		//
		// here we have to determine the expression for current (from current density).
		//
		Feature feature1 = currentClampStimulus.getElectrode().getFeature();
		Feature feature2 = mathMapping.getSimulationContext().getGroundElectrode().getFeature();
		Membrane membrane = null;
		StructureTopology structTopology = mathMapping.getSimulationContext().getModel().getStructureTopology();
		if (structTopology.getParentStructure(feature1)!=null && structTopology.getOutsideFeature((Membrane)structTopology.getParentStructure(feature1))==feature2){
			membrane = ((Membrane)structTopology.getParentStructure(feature1));
		} else if (structTopology.getParentStructure(feature2)!=null && structTopology.getOutsideFeature((Membrane)structTopology.getParentStructure(feature2))==feature1){
			membrane = ((Membrane)structTopology.getParentStructure(feature2));
		}
		if (membrane==null){
			throw new RuntimeException("current clamp based on current density crosses multiple membranes, unable to " 
				+ "determine single membrane to convert current density into current in Application '" + mathMapping.getSimulationContext().getName() + "'.");
		}
		MembraneMapping membraneMapping = (MembraneMapping)mathMapping.getSimulationContext().getGeometryContext().getStructureMapping(membrane);
		StructureMappingParameter sizeParameter = membraneMapping.getSizeParameter();
		Expression area = null;
		if (mathMapping.getSimulationContext().getGeometry().getDimension() == 0 && (sizeParameter.getExpression() == null || sizeParameter.getExpression().isZero())) {
			area = membraneMapping.getNullSizeParameterValue();
		} else {
			area = new Expression(sizeParameter,mathMapping.getNameScope());
		}
		transMembraneCurrent = new ElectricalDeviceParameter(
				DefaultNames[ROLE_TransmembraneCurrent],
				Expression.mult(new Expression(currentDensityParameter.getExpression()),area),
				ROLE_TransmembraneCurrent,
				currentUnit);
		
	}else{
		throw new RuntimeException("unexpected current clamp stimulus type : "+currentClampStimulus.getClass().getName());
	}
	ElectricalDeviceParameter totalCurrent = new ElectricalDeviceParameter(
			DefaultNames[ROLE_TotalCurrent],
			new Expression(transMembraneCurrent, getNameScope()),
			ROLE_TotalCurrent,
			currentUnit);
	
	ElectricalDeviceParameter voltage = new ElectricalDeviceParameter(
			DefaultNames[ROLE_Voltage],
			null,
			ROLE_Voltage,
			modelUnitSystem.getVoltageUnit());
	
	parameters[0] = totalCurrent;
	parameters[1] = transMembraneCurrent;
	parameters[2] = voltage;

	//
	// add any user-defined parameters
	//
	LocalParameter[] stimulusParameters = currentClampStimulus.getLocalParameters();
	for (int i = 0;stimulusParameters!=null && i <stimulusParameters.length; i++){
		if (stimulusParameters[i].getRole() == ElectricalStimulus.ElectricalStimulusParameterType.UserDefined){
			ElectricalDeviceParameter newParam = new ElectricalDeviceParameter(stimulusParameters[i].getName(),new Expression(stimulusParameters[i].getExpression()),ROLE_UserDefined,stimulusParameters[i].getUnitDefinition());
			parameters = (ElectricalDeviceParameter[])BeanUtils.addElement(parameters,newParam);
		}
	}
	setParameters(parameters);
}

/**
 * Insert the method's description here.
 * Creation date: (4/7/2004 11:19:13 AM)
 * @return boolean
 */
public boolean getCalculateVoltage() {
	return true;
}


/**
 * Insert the method's description here.
 * Creation date: (4/7/2004 11:39:09 AM)
 * @return cbit.vcell.mapping.CurrentClampStimulus
 */
public cbit.vcell.mapping.ElectricalStimulus getCurrentClampStimulus() {
	return currentClampStimulus;
}


/**
 * Insert the method's description here.
 * Creation date: (4/7/2004 11:31:25 AM)
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
 * Creation date: (4/7/2004 11:19:13 AM)
 * @return boolean
 */
public boolean hasCapacitance() {
	return false;
}


/**
 * Insert the method's description here.
 * Creation date: (4/7/2004 11:19:13 AM)
 * @return boolean
 */
public boolean isVoltageSource() {
	return false;
}
}
