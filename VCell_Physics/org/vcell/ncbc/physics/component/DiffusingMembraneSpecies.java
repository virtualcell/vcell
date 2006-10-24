package org.vcell.ncbc.physics.component;

/**
 * Insert the type's description here.
 * Creation date: (1/6/2004 12:52:00 PM)
 * @author: Jim Schaff
 */
public class DiffusingMembraneSpecies extends MembraneSpecies {
/**
 * ResolvedVolumeSpecies constructor comment.
 */
public DiffusingMembraneSpecies(String argSpeciesContextName, SurfaceLocation argSurfaceLocation) {
	super(argSpeciesContextName, argSurfaceLocation);
	addConnector(new SpeciesSurfaceDensityFluxConnector(this,CONNECTOR_NAME_SURFACEFLUXDENSITY,Port.ROLE_DEFINES,Port.ROLE_USES));
}
/**
 * Insert the method's description here.
 * Creation date: (1/14/2004 4:53:49 PM)
 * @return ncbc.physics.component.ConcentrationConnector
 */
public SpeciesSurfaceDensityFluxConnector getSurfaceDensityFluxConnector() {
	return (SpeciesSurfaceDensityFluxConnector)getConnectorByName(CONNECTOR_NAME_SURFACEFLUXDENSITY);
}
}
