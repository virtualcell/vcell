package org.vcell.model.rbm;

import java.util.HashMap;
import java.util.Map;

public class NetworkConstraints extends RbmElement {
	public static final String PROPERTY_NAME_MAX_STOICHIOMETRY = "maxStoichiometry";
	public static final String PROPERTY_NAME_MAX_ITERATION = "maxIteration";
	public static final String PROPERTY_NAME_MOLECULES_PER_SPECIES = "maxMoleculesPerSpecies";
	
	private int maxIteration = 10;
	private int maxMoleculesPerSpecies = 10;
	private Map<MolecularType, Integer> maxStoichiometryMap = new HashMap<MolecularType, Integer>();
	
	public NetworkConstraints() {		
	}

	public final int getMaxIteration() {
		return maxIteration;
	}

	public final void setMaxIteration(int newValue) {
		int oldValue = maxIteration;
		this.maxIteration = newValue;
		firePropertyChange(PROPERTY_NAME_MAX_ITERATION, oldValue, newValue);
	}

	public final int getMaxMoleculesPerSpecies() {
		return maxMoleculesPerSpecies;
	}

	public final void setMaxMoleculesPerSpecies(int newValue) {
		int oldValue = maxMoleculesPerSpecies;
		this.maxMoleculesPerSpecies = newValue;
		firePropertyChange(PROPERTY_NAME_MOLECULES_PER_SPECIES, oldValue, newValue);
	}
	
	public void setMaxStoichiometry(MolecularType molecularType, Integer newValue) {
		Integer oldValue;
		if (newValue == null) {
			oldValue = maxStoichiometryMap.remove(molecularType);
		} else {
			oldValue = maxStoichiometryMap.get(molecularType);
			maxStoichiometryMap.put(molecularType, newValue);
		}
		firePropertyChange(PROPERTY_NAME_MAX_STOICHIOMETRY, oldValue, newValue);
	}
	
	public Integer getMaxStoichiometry(MolecularType molecularType) {
		return maxStoichiometryMap.get(molecularType);
	}
	
	public void clear() {
		maxStoichiometryMap.clear();
		maxIteration = 10;
		maxMoleculesPerSpecies = 10;
	}
}
