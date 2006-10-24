package org.vcell.ncbc.physics.component;
/**
 * Insert the type's description here.
 * Creation date: (1/6/2004 12:54:00 PM)
 * @author: Jim Schaff
 */
public class SpeciesSurfaceDensityConnector extends Connector {
/**
 * Insert the method's description here.
 * Creation date: (1/13/2004 11:33:00 AM)
 * @param name java.lang.String
 */
public SpeciesSurfaceDensityConnector(Device argDevice, String argName, int densityRole, int rateRole) {
	super(argDevice, argName, new Port[] { new SpeciesSurfaceDensityPort(densityRole), new SpeciesSurfaceDensityRatePort(rateRole) } );
}


/**
 * Insert the method's description here.
 * Creation date: (1/13/2004 4:44:46 PM)
 * @return ncbc.physics.component.ConcentrationRatePort
 */
public SpeciesSurfaceDensityPort getSpeciesSurfaceDensityPort() {
	return (SpeciesSurfaceDensityPort)getPortByVariableName(SpeciesSurfaceDensityPort.NAME);
}


/**
 * Insert the method's description here.
 * Creation date: (1/13/2004 4:44:46 PM)
 * @return ncbc.physics.component.ConcentrationRatePort
 */
public SpeciesSurfaceDensityRatePort getSpeciesSurfaceDensityRatePort() {
	return (SpeciesSurfaceDensityRatePort)getPortByVariableName(SpeciesSurfaceDensityRatePort.NAME);
}
}