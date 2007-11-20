package cbit.vcell.mapping;

import cbit.vcell.modelapp.MembraneMapping;


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
public MembraneElectricalDevice(MembraneMapping argMembraneMapping, cbit.vcell.mapping.MathMapping argMathMapping) {
	super("device_"+argMembraneMapping.getMembrane().getName(), argMathMapping);
	this.membraneMapping = argMembraneMapping;

	ElectricalDevice.ElectricalDeviceParameter parameters[] = new ElectricalDevice.ElectricalDeviceParameter[2];

	parameters[0] = new ElectricalDeviceParameter(
							DefaultNames[ROLE_TotalCurrentDensity],
							null, // (need to calculate)
							ROLE_TotalCurrentDensity,
							org.vcell.units.VCUnitDefinition.UNIT_pA_per_um2);

    parameters[1] = new ElectricalDeviceParameter(
						    DefaultNames[ROLE_TransmembraneCurrentDensity],
							null, // given
							ROLE_TransmembraneCurrentDensity,
							org.vcell.units.VCUnitDefinition.UNIT_pA_per_um2);

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
public String getCapName() {
	return mathMapping.getNameScope().getSymbolName(membraneMapping.getSpecificCapacitanceParameter());
}
/**
 * Insert the method's description here.
 * Creation date: (4/7/2004 11:42:18 AM)
 * @return cbit.vcell.mapping.MembraneMapping
 */
public cbit.vcell.modelapp.MembraneMapping getMembraneMapping() {
	return membraneMapping;
}
/**
 * Insert the method's description here.
 * Creation date: (4/7/2004 11:19:13 AM)
 * @return boolean
 */
public boolean getResolved() {
	return membraneMapping.getResolved(mathMapping.getSimulationContext());
}
/**
 * Insert the method's description here.
 * Creation date: (4/7/2004 2:55:34 PM)
 * @return java.lang.String
 */
public java.lang.String getVName() {
	return mathMapping.getNameScope().getSymbolName(membraneMapping.getMembrane().getMembraneVoltage());
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
