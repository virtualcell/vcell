package org.vcell.ncbc.physics.component;
/**
 * Insert the type's description here.
 * Creation date: (1/6/2004 12:54:00 PM)
 * @author: Jim Schaff
 */
public class VolumeFluxDensityConnector extends Connector {
/**
 * Insert the method's description here.
 * Creation date: (1/13/2004 11:33:00 AM)
 * @param name java.lang.String
 */
public VolumeFluxDensityConnector(Device argDevice, String argName, int concRole, int fluxRole) {
	super(argDevice, argName, new Port[] { new ConcentrationPort(concRole), new ConcentrationFluxDensityPort(fluxRole) } );
}


/**
 * Insert the method's description here.
 * Creation date: (1/13/2004 4:44:46 PM)
 * @return ncbc.physics.component.ConcentrationRatePort
 */
public ConcentrationFluxDensityPort getConcentrationFluxDensityPort() {
	return (ConcentrationFluxDensityPort)getPortByVariableName(ConcentrationFluxDensityPort.NAME);
}


/**
 * Insert the method's description here.
 * Creation date: (1/13/2004 4:44:46 PM)
 * @return ncbc.physics.component.ConcentrationRatePort
 */
public ConcentrationPort getConcentrationPort() {
	return (ConcentrationPort)getPortByVariableName(ConcentrationPort.NAME);
}
}