package org.vcell.ncbc.physics.component;

/**
 * Insert the type's description here.
 * Creation date: (1/6/2004 12:52:00 PM)
 * @author: Jim Schaff
 */
public abstract class MembraneSpecies extends Device {
	public static final String CONNECTOR_NAME_SURFACEDENSITY = "source";
	public static final String CONNECTOR_NAME_SURFACEFLUXDENSITY = "flux"; // membrane diffusion
/**
 * ResolvedVolumeSpecies constructor comment.
 */
public MembraneSpecies(String argSpeciesContextName, SurfaceLocation argSurfaceLocation) {
	super(argSpeciesContextName, argSurfaceLocation);
	addConnector(new SpeciesSurfaceDensityConnector(this,CONNECTOR_NAME_SURFACEDENSITY,Port.ROLE_DEFINES,Port.ROLE_USES));
}
/**
 * Insert the method's description here.
 * Creation date: (1/14/2004 4:53:49 PM)
 * @return ncbc.physics.component.ConcentrationConnector
 */
public SpeciesSurfaceDensityConnector getSpeciesSurfaceDensityConnector() {
	return (SpeciesSurfaceDensityConnector)getConnectorByName(CONNECTOR_NAME_SURFACEDENSITY);
}
}
