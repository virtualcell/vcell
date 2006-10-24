package org.vcell.ncbc.physics.component;

/**
 * Insert the type's description here.
 * Creation date: (1/6/2004 12:52:00 PM)
 * @author: Jim Schaff
 */
public abstract class VolumeSpecies extends Device {
	public static final String CONNECTOR_NAME_CONCENTRATION = "source";
	public static final String CONNECTOR_NAME_FLUX = "flux";
/**
 * ResolvedVolumeSpecies constructor comment.
 */
public VolumeSpecies(String argSpeciesContextName, VolumeLocation argVolumeLocation) {
	super(argSpeciesContextName, argVolumeLocation);
	addConnector(new ConcentrationConnector(this,CONNECTOR_NAME_CONCENTRATION,Port.ROLE_DEFINES,Port.ROLE_USES));
}
/**
 * Insert the method's description here.
 * Creation date: (1/14/2004 4:53:49 PM)
 * @return ncbc.physics.component.ConcentrationConnector
 */
public ConcentrationConnector getConcentrationConnector() {
	return (ConcentrationConnector)getConnectorByName(CONNECTOR_NAME_CONCENTRATION);
}
}
