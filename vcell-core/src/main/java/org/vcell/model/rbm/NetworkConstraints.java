package org.vcell.model.rbm;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.vcell.model.rbm.common.NetworkConstraintsEntity;
import org.vcell.util.Compare;
import org.vcell.util.Issue;
import org.vcell.util.IssueContext;
import org.vcell.util.Matchable;

import cbit.vcell.mapping.NetworkTransformer;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.model.Model;
import cbit.vcell.model.Model.RbmModelContainer;

@SuppressWarnings("serial")
public class NetworkConstraints extends RbmElementAbstract implements Matchable, Serializable {
	public static final String PROPERTY_NAME_MAX_STOICHIOMETRY = "maxStoichiometry";
	public static final String PROPERTY_NAME_MAX_ITERATION = "maxIteration";
	public static final String PROPERTY_NAME_MOLECULES_PER_SPECIES = "maxMoleculesPerSpecies";
	public static final String PROPERTY_NAME_SPECIES_LIMIT = "speciesLimit";
	public static final String PROPERTY_NAME_REACTIONS_LIMIT = "reactionsLimit";
	
	public static final String SPECIES_LIMIT_PARAMETER = "vcellReservedParameter_speciesLimit";
	public static final String REACTIONS_LIMIT_PARAMETER = "vcellReservedParameter_reactionsLimit";
	
	public static final int defaultMaxStoichiometry = 20;	// default max molecules of each type per species
	
	private int maxIteration = NetworkTransformer.defaultMaxIteration;
	private int maxMoleculesPerSpecies = NetworkTransformer.defaultMaxMoleculesPerSpecies;
	private int speciesLimit = NetworkTransformer.defaultSpeciesLimit;
	private int reactionsLimit = NetworkTransformer.defaultReactionsLimit;
	private Map<MolecularType, Integer> maxStoichiometryMap = new LinkedHashMap<>();
	
	private transient int testMaxIteration = maxIteration;
	private transient int testMaxMoleculesPerSpecies = maxMoleculesPerSpecies;
	private transient int testSpeciesLimit = speciesLimit;
	private transient int testReactionsLimit = reactionsLimit;
	private transient Map<MolecularType, Integer> testMaxStoichiometryMap = new LinkedHashMap<>();

	public NetworkConstraints() {		
	}
	public NetworkConstraints(NetworkConstraints that) {
		this();
		this.maxIteration = that.maxIteration;
		this.maxMoleculesPerSpecies = that.maxMoleculesPerSpecies;
		this.speciesLimit = that.speciesLimit;
		this.reactionsLimit = that.reactionsLimit;
		this.testMaxIteration = that.testMaxIteration;
		this.testMaxMoleculesPerSpecies = that.testMaxMoleculesPerSpecies;
		this.testSpeciesLimit = that.testSpeciesLimit;
		this.testReactionsLimit = that.testReactionsLimit;
		
		for (Map.Entry<MolecularType, Integer> entry : that.maxStoichiometryMap.entrySet()) {
			MolecularType key = entry.getKey();		// use the same instances of the molecular type 
			int value = entry.getValue();
			this.maxStoichiometryMap.put(key, value);
		}
	}
	
	public void setTestConstraints(int testMaxIteration, int testMaxMoleculesPerSpecies, 
			int testSpeciesLimit, int testReactionsLimit, Map<MolecularType, Integer> testMaxStoichiometryMap) {
		this.testMaxIteration = testMaxIteration;
		this.testMaxMoleculesPerSpecies = testMaxMoleculesPerSpecies;
		this.testSpeciesLimit = testSpeciesLimit;
		this.testReactionsLimit = testReactionsLimit;
		this.testMaxStoichiometryMap = testMaxStoichiometryMap;
	}
	public void updateConstraintsFromTest() {
		setMaxIteration(testMaxIteration);
		setMaxMoleculesPerSpecies(testMaxMoleculesPerSpecies);
		setSpeciesLimit(testSpeciesLimit);
		setReactionsLimit(testReactionsLimit);
		
		for (Map.Entry<MolecularType, Integer> entry : testMaxStoichiometryMap.entrySet()) {
			MolecularType key = entry.getKey();		// use the same instances of the molecular type 
			int value = entry.getValue();
			this.maxStoichiometryMap.put(key, value);
		}
	}
	public boolean isTestConstraintsDifferent() {
		if(testMaxIteration != maxIteration || testMaxMoleculesPerSpecies != maxMoleculesPerSpecies || testSpeciesLimit != speciesLimit || testReactionsLimit != reactionsLimit) {
			return true;
		}
		for(Map.Entry<MolecularType, Integer> var1 : maxStoichiometryMap.entrySet()) {
			boolean different = true;
			for(Map.Entry<MolecularType, Integer> var2 : testMaxStoichiometryMap.entrySet()) {
				if(Compare.isEqual(var1.getKey(),var2.getKey()) && Compare.isEqual(var1.getValue(),var2.getValue())) {
					different = false;
					break;
				}
			}
			if(different == true) {
				return true;
			}
		}
		return false;
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
	
	public final int getSpeciesLimit() {
		return speciesLimit;
	}
	public final void setSpeciesLimit(Integer newValue) {
		int oldValue = speciesLimit;
		this.speciesLimit = newValue;
		firePropertyChange(PROPERTY_NAME_SPECIES_LIMIT, oldValue, newValue);
	}
	public final int getReactionsLimit() {
		return reactionsLimit;
	}
	public final void setReactionsLimit(Integer newValue) {
		int oldValue = reactionsLimit;
		this.reactionsLimit = newValue;
		firePropertyChange(PROPERTY_NAME_REACTIONS_LIMIT, oldValue, newValue);
	}
	
	public void setMaxStoichiometry(MolecularType molecularType, Integer newValue) {
		Integer oldValue;
		if (newValue == null || newValue < 1) {
			throw new RuntimeException("Invalid stoichiometry value!");
		} else {
			oldValue = maxStoichiometryMap.get(molecularType);
			maxStoichiometryMap.put(molecularType, newValue);
		}
		firePropertyChange(PROPERTY_NAME_MAX_STOICHIOMETRY, oldValue, newValue);
	}
	
	public final int getTestMaxIteration() {
		return testMaxIteration;
	}
	public final int getTestMaxMoleculesPerSpecies() {
		return testMaxMoleculesPerSpecies;
	}
	public final int getTestSpeciesLimit() {
		return testSpeciesLimit;
	}
	public final int getTestReactionsLimit() {
		return testReactionsLimit;
	}

	public Map<MolecularType, Integer> getMaxStoichiometry(SimulationContext sc) {
		updateStoichiometryMaps(sc);
		return maxStoichiometryMap;
	}
	public Integer getMaxStoichiometry(MolecularType molecularType, SimulationContext sc) {
		updateStoichiometryMaps(sc);
		return maxStoichiometryMap.get(molecularType);
	}
	public Integer getTestMaxStoichiometry(MolecularType molecularType, SimulationContext sc) {
		updateStoichiometryMaps(sc);
		return testMaxStoichiometryMap.get(molecularType);
	}
	public void updateStoichiometryMaps(SimulationContext simContext) {
		if(simContext == null || simContext.getModel() == null || simContext.getModel().getRbmModelContainer() == null) {
			maxStoichiometryMap.clear();
			testMaxStoichiometryMap.clear();
			return;
		}
		// make certain we are consistent
		Model model = simContext.getModel();
		RbmModelContainer rbmModelContainer = model.getRbmModelContainer();
		Iterator<Entry<MolecularType, Integer>> it = maxStoichiometryMap.entrySet().iterator();
		while (it.hasNext()) {
			// first clean any entry that doesn't exist anymore
			MolecularType mt = it.next().getKey();
			if(rbmModelContainer.getMolecularType(mt.getName()) == null) {
				it.remove();
			}
		}
		it = testMaxStoichiometryMap.entrySet().iterator();
		while (it.hasNext()) {
			MolecularType mt = it.next().getKey();
			if(rbmModelContainer.getMolecularType(mt.getName()) == null) {
				it.remove();
			}
		}
		for(MolecularType mt : rbmModelContainer.getMolecularTypeList()) {
			// add any new molecule that's not already there, with the default max stoichiometry
			if(!maxStoichiometryMap.containsKey(mt)) {
				maxStoichiometryMap.put(mt, defaultMaxStoichiometry);
			}
		}
		for(MolecularType mt : rbmModelContainer.getMolecularTypeList()) {
			// add any new molecule that's not already there, with the value from maxStoichiometryMap
			if(!testMaxStoichiometryMap.containsKey(mt)) {
				testMaxStoichiometryMap.put(mt, maxStoichiometryMap.get(mt));
			}
		}
	}
	public void clear() {
		maxStoichiometryMap.clear();
		maxIteration = NetworkTransformer.defaultMaxIteration;
		maxMoleculesPerSpecies = NetworkTransformer.defaultMaxMoleculesPerSpecies;
		speciesLimit = NetworkTransformer.defaultSpeciesLimit;
		reactionsLimit = NetworkTransformer.defaultReactionsLimit;
	}
	
	@Override
	public boolean compareEqual(Matchable aThat) {
		if (this == aThat) {
			return true;
		}
		if (!(aThat instanceof NetworkConstraints)) {
			return false;
		}
		NetworkConstraints that = (NetworkConstraints)aThat;

		if (!Compare.isEqual(maxIteration, that.maxIteration)) {
			return false;
		}
		if (!Compare.isEqual(maxMoleculesPerSpecies, that.maxMoleculesPerSpecies)) {
			return false;
		}
		if (!Compare.isEqual(speciesLimit, that.speciesLimit)) {
			return false;
		}
		if (!Compare.isEqual(reactionsLimit, that.reactionsLimit)) {
			return false;
		}
		Map<MolecularType, Integer> thatMaxStoichiometryMap = new HashMap<MolecularType, Integer>(that.maxStoichiometryMap);
		for(Map.Entry<MolecularType, Integer> var1 : maxStoichiometryMap.entrySet()) {
			boolean found = false;
			for(Map.Entry<MolecularType, Integer> var2 : thatMaxStoichiometryMap.entrySet()) {
				if(Compare.isEqual(var1.getKey(),var2.getKey()) && Compare.isEqual(var1.getValue(),var2.getValue())) {
					found = true;
					thatMaxStoichiometryMap.remove(var2);
					break;
				}
			}
			if(found == false) {
				return false;
			}
		}
		if(!thatMaxStoichiometryMap.isEmpty()) {
			return false;
		}
		return true;
	}
	
	@Override
	public void gatherIssues(IssueContext issueContext, List<Issue> issueList) {

	}

}
