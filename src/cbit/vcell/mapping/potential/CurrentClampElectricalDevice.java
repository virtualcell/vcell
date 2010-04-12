package cbit.vcell.mapping.potential;
import org.vcell.util.BeanUtils;

import cbit.vcell.mapping.CurrentDensityClampStimulus;
import cbit.vcell.mapping.ElectricalStimulus;
import cbit.vcell.mapping.MathMapping;
import cbit.vcell.mapping.MembraneMapping;
import cbit.vcell.mapping.TotalCurrentClampStimulus;
import cbit.vcell.mapping.ParameterContext.LocalParameter;
import cbit.vcell.mapping.StructureMapping.StructureMappingParameter;
import cbit.vcell.model.Feature;
import cbit.vcell.model.Membrane;
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

public CurrentClampElectricalDevice(TotalCurrentClampStimulus argTotalCurrentClampStimulus, MathMapping argMathMapping) throws ExpressionException {
	super("device_"+argTotalCurrentClampStimulus.getName(), argMathMapping);
	this.currentClampStimulus = argTotalCurrentClampStimulus;

	initializeParameters();
}

public CurrentClampElectricalDevice(CurrentDensityClampStimulus argCurrentDensityClampStimulus, MathMapping argMathMapping) throws ExpressionException {
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
	if (currentClampStimulus instanceof TotalCurrentClampStimulus){
		TotalCurrentClampStimulus stimulus = (TotalCurrentClampStimulus)currentClampStimulus;
		LocalParameter currentParameter = stimulus.getCurrentParameter();
		transMembraneCurrent = new ElectricalDeviceParameter(
				DefaultNames[ROLE_TransmembraneCurrent],
				new Expression(currentParameter.getExpression()),
				ROLE_TransmembraneCurrent,
				VCUnitDefinition.UNIT_pA);
	}else if (currentClampStimulus instanceof CurrentDensityClampStimulus){
		CurrentDensityClampStimulus stimulus = (CurrentDensityClampStimulus)currentClampStimulus;
		LocalParameter currentDensityParameter = stimulus.getCurrentDensityParameter();
		//
		// here we have to determine the expression for current (from current density).
		//
		Feature feature1 = currentClampStimulus.getElectrode().getFeature();
		Feature feature2 = mathMapping.getSimulationContext().getGroundElectrode().getFeature();
		Membrane membrane = null;
		if (feature1.getParentStructure()!=null && ((Membrane)feature1.getParentStructure()).getOutsideFeature()==feature2){
			membrane = ((Membrane)feature1.getParentStructure());
		} else if (feature2.getParentStructure()!=null && ((Membrane)feature2.getParentStructure()).getOutsideFeature()==feature1){
			membrane = ((Membrane)feature2.getParentStructure());
		}
		if (membrane==null){
			throw new RuntimeException("current clamp based on current density crosses multiple membranes, unable to " 
				+ "determine single membrane to convert current density into current in Application '" + mathMapping.getSimulationContext().getName() + "'.");
		}
		MembraneMapping membraneMapping = (MembraneMapping)mathMapping.getSimulationContext().getGeometryContext().getStructureMapping(membrane);
		StructureMappingParameter sizeParameter = membraneMapping.getSizeParameter();
		Expression area = null;
		if (sizeParameter.getExpression() == null || sizeParameter.getExpression().isZero()) {
			area = membraneMapping.getNullSizeParameterValue();
		} else {
			area = new Expression(sizeParameter,mathMapping.getNameScope());
		}
		transMembraneCurrent = new ElectricalDeviceParameter(
				DefaultNames[ROLE_TransmembraneCurrent],
				Expression.mult(new Expression(currentDensityParameter.getExpression()),area),
				ROLE_TransmembraneCurrent,
				VCUnitDefinition.UNIT_pA);
		
	}else{
		throw new RuntimeException("unexpected current clamp stimulus type : "+currentClampStimulus.getClass().getName());
	}
	ElectricalDeviceParameter totalCurrent = new ElectricalDeviceParameter(
			DefaultNames[ROLE_TotalCurrent],
			new Expression(transMembraneCurrent, getNameScope()),
			ROLE_TotalCurrent,
			VCUnitDefinition.UNIT_pA);
	
	ElectricalDeviceParameter voltage = new ElectricalDeviceParameter(
			DefaultNames[ROLE_Voltage],
			null,
			ROLE_Voltage,
			VCUnitDefinition.UNIT_mV);
	
	parameters[0] = totalCurrent;
	parameters[1] = transMembraneCurrent;
	parameters[2] = voltage;

	//
	// add any user-defined parameters
	//
	LocalParameter[] stimulusParameters = currentClampStimulus.getLocalParameters();
	for (int i = 0;stimulusParameters!=null && i <stimulusParameters.length; i++){
		int role = stimulusParameters[i].getRole();
		if (role==ElectricalStimulus.ROLE_UserDefined){
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