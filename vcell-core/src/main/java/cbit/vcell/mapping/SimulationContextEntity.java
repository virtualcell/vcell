package cbit.vcell.mapping;


// Allows easier categorization of application related entities 
// as being part of the geometry, initial conditions, protocols, aso.
// Note that checking for the absence of this class is not reliable, implementing it 
// is not mandatory for all application-related entities.

public interface SimulationContextEntity {
	
public SimulationContext.Kind getSimulationContextKind();	// kind could be Geometry, Specifications, Protocols...

}
