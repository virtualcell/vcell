package org.vcell.smoldyn.model;


import org.vcell.smoldyn.model.util.Point;
import org.vcell.smoldyn.simulation.SimulationUtilities;


/**
 * A Molecule, for Smoldyn, is a particle that moves, is associated with a {@link SpeciesState}, has a location,
 * and can undergo reactions.
 * 
 * Invariants:
 * 		species is not null
 * 		neither compartment, species, nor point may be changed
 * 
 * @author mfenwick
 *
 */
public class VolumeMolecule implements SmoldynMolecule {
	
	private final Compartment compartment;
	private final Species species;
	private final Point point;
	private final int count;
	
	
	/**
	 * Initialize a molecule with a SpeciesState and Point. The SpeciesState should already be present in the model,
	 * and the dimensionality of the Point at this moment must be 3.
	 * 
	 * @param compartment null implies not localized to a compartment
	 * @param species must not be null
	 * @param point null implies random distribution
	 * @throws SmoldynNullException 
	 */
	public VolumeMolecule(Compartment compartment, Species species, Point point, int count){
		SimulationUtilities.checkForNull("species, point or compartment", species, point, compartment);
		SimulationUtilities.checkForPositive("count", count);
		this.compartment = compartment;
		this.species = species;
		this.point = point;
		this.count = count;
	}
	
	
	
	public Compartment getCompartment() {
		return this.compartment;
	}
	
	public Point getPoint() {
		return this.point;
	}
	
	public Species getSpecies() {
		return this.species;
	}
	
	public int getCount() {
		return this.count;
	}

}
