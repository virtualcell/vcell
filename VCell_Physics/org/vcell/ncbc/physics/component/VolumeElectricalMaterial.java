package org.vcell.ncbc.physics.component;
/**
 * Insert the type's description here.
 * Creation date: (1/14/2004 8:52:59 PM)
 * @author: Jim Schaff
 */
public abstract class VolumeElectricalMaterial extends ElectricalMaterial {
	public static final String CONNECTOR_NAME_ELECTRICAL = "conn";

/**
 * SurfaceElectricalMaterial constructor comment.
 * @param argName java.lang.String
 * @param argLocation ncbc.physics.component.Location
 */
public VolumeElectricalMaterial(String argName, VolumeLocation argVolumeLocation) {
	super(argName, argVolumeLocation);
	addConnector(new ElectricalConnector(this, CONNECTOR_NAME_ELECTRICAL, Port.ROLE_INFLUENCES, Port.ROLE_INFLUENCES));
}


/**
 * Insert the method's description here.
 * Creation date: (1/15/2004 7:30:41 PM)
 * @return ncbc.physics.component.ElectricalConnector
 */
public ElectricalConnector getElectricalConnector() {
	return (ElectricalConnector)getConnectorByName(CONNECTOR_NAME_ELECTRICAL);
}
}