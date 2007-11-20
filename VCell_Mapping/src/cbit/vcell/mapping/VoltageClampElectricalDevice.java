package cbit.vcell.mapping;
import org.vcell.expression.ExpressionException;
import org.vcell.expression.ExpressionFactory;

import cbit.vcell.modelapp.ElectricalStimulus;
import cbit.vcell.modelapp.VoltageClampStimulus;
import cbit.vcell.modelapp.ElectricalStimulus.ElectricalStimulusParameter;
/**
 * Insert the type's description here.
 * Creation date: (4/7/2004 11:00:15 AM)
 * @author: Jim Schaff
 */
public class VoltageClampElectricalDevice extends ElectricalDevice {
	private cbit.vcell.modelapp.VoltageClampStimulus voltageClampStimulus = null;

/**
 * Insert the method's description here.
 * Creation date: (4/7/2004 11:00:30 AM)
 */
public VoltageClampElectricalDevice(cbit.vcell.modelapp.VoltageClampStimulus argVoltageClampStimulus, cbit.vcell.mapping.MathMapping argMathMapping) throws ExpressionException {
	super("device_"+argVoltageClampStimulus.getName(), argMathMapping);
	this.voltageClampStimulus = argVoltageClampStimulus;
	
	ElectricalDevice.ElectricalDeviceParameter parameters[] = new ElectricalDevice.ElectricalDeviceParameter[3];
	
	parameters[0] = new ElectricalDeviceParameter(
							DefaultNames[ROLE_TotalCurrentDensity],
							ExpressionFactory.createExpression(DefaultNames[ROLE_TransmembraneCurrentDensity]),
							ROLE_TotalCurrentDensity,
							org.vcell.units.VCUnitDefinition.UNIT_pA_per_um2);
	
	parameters[1] = new ElectricalDeviceParameter(
							DefaultNames[ROLE_TransmembraneCurrentDensity],
							null,
							ROLE_TransmembraneCurrentDensity,
							org.vcell.units.VCUnitDefinition.UNIT_pA_per_um2);

	ElectricalStimulus.ElectricalStimulusParameter voltageParm = voltageClampStimulus.getVoltageParameter();
	parameters[2] = new ElectricalDeviceParameter(
							voltageParm.getName(),
							ExpressionFactory.createExpression(voltageParm.getExpression()),
							ROLE_Voltage,
							voltageParm.getUnitDefinition());
	//
	// add any user-defined parameters
	//
	cbit.vcell.modelapp.ElectricalStimulus.ElectricalStimulusParameter[] stimulusParameters = voltageClampStimulus.getElectricalStimulusParameters();
	for (int i = 0;stimulusParameters!=null && i <stimulusParameters.length; i++){
		int role = stimulusParameters[i].getRole();
		if (role==ElectricalStimulus.ROLE_UserDefined){
			ElectricalDeviceParameter newParam = new ElectricalDeviceParameter(stimulusParameters[i].getName(),ExpressionFactory.createExpression(stimulusParameters[i].getExpression()),ROLE_UserDefined,stimulusParameters[i].getUnitDefinition());
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
public java.lang.String getVName() {
	return mathMapping.getNameScope().getSymbolName(getParameterFromRole(ROLE_Voltage));
}


/**
 * Insert the method's description here.
 * Creation date: (4/7/2004 11:38:39 AM)
 * @return cbit.vcell.mapping.VoltageClampStimulus
 */
public cbit.vcell.modelapp.VoltageClampStimulus getVoltageClampStimulus() {
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