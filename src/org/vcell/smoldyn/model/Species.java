package org.vcell.smoldyn.model;

import org.vcell.smoldyn.simulation.SimulationUtilities;



/**
 * A species is a group of molecules which are treated as having a certain identity by Smoldyn, upon which the behavior of the molecule is
 * based.  A good way to think of a species is as a chemical compound.  A molecule may change state (be adsorbed or desorbed) without
 * changing its species.  Smoldyn determines molecular behavior on the basis of species.
 * 
 * Invariants:
 * 		name is not null
 * 		name may not be changed
 * 
 * @author mfenwick
 *
 */
public class Species {

	private final String name;
	
	
	/**
	 * Instantiates a new species with the given name.
	 * 
	 * @param name -- must not be null
	 */
	public Species(String name) {
		SimulationUtilities.checkForNull(name, "species name");
		this.name = name;
	}

	
	public String getName() {
		return this.name;
	}

}
