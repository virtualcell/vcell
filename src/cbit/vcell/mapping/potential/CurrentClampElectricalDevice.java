package cbit.vcell.mapping.potential;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.mapping.CurrentClampStimulus;
import cbit.vcell.mapping.ElectricalStimulus;

/**
 * Insert the type's description here.
 * Creation date: (4/7/2004 11:00:45 AM)
 * @author: Jim Schaff
 */
public class CurrentClampElectricalDevice extends ElectricalDevice {
	private CurrentClampStimulus currentClampStimulus = null;

/**
 * Insert the method's description here.
 * Creation date: (4/7/2004 11:01:08 AM)
 */
public CurrentClampElectricalDevice(CurrentClampStimulus argCurrentClamStimulus, cbit.vcell.mapping.MathMapping argMathMapping) throws cbit.vcell.parser.ExpressionException {
	super("device_"+argCurrentClamStimulus.getName(), argMathMapping);
	this.currentClampStimulus = argCurrentClamStimulus;

	ElectricalDevice.ElectricalDeviceParameter parameters[] = new ElectricalDevice.ElectricalDeviceParameter[2];

	parameters[0] = new ElectricalDeviceParameter(
							DefaultNames[ROLE_TotalCurrentDensity],
							new Expression(DefaultNames[ROLE_TransmembraneCurrentDensity]),
							ROLE_TotalCurrentDensity,
							cbit.vcell.units.VCUnitDefinition.UNIT_pA_per_um2);

	parameters[1] = new ElectricalDeviceParameter(
							DefaultNames[ROLE_TransmembraneCurrentDensity],
							new Expression(argCurrentClamStimulus.getCurrentParameter().getExpression()),
							ROLE_TransmembraneCurrentDensity,
							cbit.vcell.units.VCUnitDefinition.UNIT_pA_per_um2);

	//
	// add any user-defined parameters
	//
	cbit.vcell.mapping.ElectricalStimulus.ElectricalStimulusParameter[] stimulusParameters = currentClampStimulus.getElectricalStimulusParameters();
	for (int i = 0;stimulusParameters!=null && i <stimulusParameters.length; i++){
		int role = stimulusParameters[i].getRole();
		if (role==ElectricalStimulus.ROLE_UserDefined){
			ElectricalDeviceParameter newParam = new ElectricalDeviceParameter(stimulusParameters[i].getName(),new Expression(stimulusParameters[i].getExpression()),ROLE_UserDefined,stimulusParameters[i].getUnitDefinition());
			parameters = (ElectricalDeviceParameter[])cbit.util.BeanUtils.addElement(parameters,newParam);
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
public cbit.vcell.mapping.CurrentClampStimulus getCurrentClampStimulus() {
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
	return currentClampStimulus.getVoltageParameter();
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