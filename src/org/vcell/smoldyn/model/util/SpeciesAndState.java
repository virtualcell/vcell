package org.vcell.smoldyn.model.util;

import org.vcell.smoldyn.model.Species.StateType;
import org.vcell.smoldyn.simulation.SimulationUtilities;

/**
 * @author mfenwick
 *
 */
public class SpeciesAndState {

	
	private final String speciesname;
	private final StateType statetype;
	
	
	/**
	 * @param speciesname -- not null
	 * @param statetype -- not null
	 */
	public SpeciesAndState(String speciesname, StateType statetype) {
		SimulationUtilities.checkForNull("species or state", speciesname, statetype);
		this.speciesname = speciesname;
		this.statetype = statetype;
	}


	public String getSpeciesname() {
		return speciesname;
	}


	public StateType getStatetype() {
		return statetype;
	}
}
