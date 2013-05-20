package org.vcell.model.rbm;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.vcell.util.document.PropertyConstants;

import cbit.vcell.model.ModelException;

public class RbmModelContainer extends RbmElement implements RBMSymbolTable, VetoableChangeListener {
	public static final String PROPERTY_NAME_MOLECULAR_TYPE_LIST = "molecularTypeList";
	public static final String PROPERTY_NAME_OBSERVABLE_LIST = "observableList";
	public static final String PROPERTY_NAME_REACTION_RULE_LIST = "reactionRuleList";
	public static final String PROPERTY_NAME_SEED_SPECIES_LIST = "seedSpeciesList";
	public static final String PROPERTY_NAME_RBM_PARAMETER_LIST = "rbmParameterList";
	private List<MolecularType> molecularTypeList = new ArrayList<MolecularType>();
	private List<ReactionRule> reactionRuleList = new ArrayList<ReactionRule>();
	private List<Observable> observableList = new ArrayList<Observable>();
	private List<SeedSpecies> seedSpeciesList = new ArrayList<SeedSpecies>();
	private List<RbmParameter> parameterList = new ArrayList<RbmParameter>();
	private NetworkConstraints networkConstraints = new NetworkConstraints();
	
	public void addMolecularType(MolecularType molecularType) throws ModelException {
		if (getMolecularType(molecularType.getName()) != null) {
			throw new ModelException("Molecular type '" + molecularType.getName() + "' already exists!");
		}
		ArrayList<MolecularType> newValue = new ArrayList<MolecularType>(molecularTypeList);
		newValue.add(molecularType);
		setMolecularTypeList(newValue);
	}
	
	public boolean removeMolecularType(MolecularType molecularType) {
		if (!molecularTypeList.contains(molecularType)) {
			return false;
		}
		ArrayList<MolecularType> newValue = new ArrayList<MolecularType>(molecularTypeList);
		newValue.remove(molecularType);
		setMolecularTypeList(newValue);
		return true;
	}
	
	public void deleteMolecularType(String molecularTypeName) {
		MolecularType molecularType = getMolecularType(molecularTypeName);
		if (molecularType == null) {
			return;
		}
		ArrayList<MolecularType> newValue = new ArrayList<MolecularType>(molecularTypeList);
		newValue.remove(molecularType);
		setMolecularTypeList(newValue);
	}
	
	public boolean removeReactionRule(ReactionRule reactionRule) {
		if (!reactionRuleList.contains(reactionRule)) {
			return false;
		}
		ArrayList<ReactionRule> newValue = new ArrayList<ReactionRule>(reactionRuleList);
		newValue.remove(reactionRule);
		setReactionRules(newValue);
		return true;
	}
	
	public MolecularType getMolecularType(String molecularTypeName){
		for (MolecularType molecularType : this.molecularTypeList){
			if (molecularType.getName().equals(molecularTypeName)){
				return molecularType;
			}
		}
		return null;
	}
	
	public List<Observable> getObservableList() {
		return observableList;
	}
	
	public List<SeedSpecies> getSeedSpeciesList() {
		return seedSpeciesList;
	}

	private final void setMolecularTypeList(List<MolecularType> newValue) {
		List<MolecularType> oldValue = molecularTypeList;
		if (oldValue != null) {
			for (MolecularType mt : oldValue) {
				mt.removeVetoableChangeListener(this);
			}
		}
		this.molecularTypeList = newValue;
		if (newValue != null) {
			for (MolecularType mt : newValue) {
				mt.addVetoableChangeListener(this);
			}
		}
		firePropertyChange(PROPERTY_NAME_MOLECULAR_TYPE_LIST, oldValue, newValue);
	}

	private final void setReactionRules(List<ReactionRule> newValue) {
		List<ReactionRule> oldValue = reactionRuleList;
		if (oldValue != null) {
			for (ReactionRule reactionRule : oldValue) {
				reactionRule.removeVetoableChangeListener(this);
			}
		}
		this.reactionRuleList = newValue;
		if (newValue != null) {
			for (ReactionRule reactionRule : newValue) {
				reactionRule.addVetoableChangeListener(this);
			}
		}
		firePropertyChange(PROPERTY_NAME_REACTION_RULE_LIST, oldValue, newValue);
	}
	
	public MolecularType createMolecularType() {
		int count=0;
		String name = null;
		while (true) {
			name = "molecular" + count;	
			if (getMolecularType(name) == null) {
				break;
			}	
			count++;
		}
		return new MolecularType(name);
	}

	public Observable createObservable() {
		int count=0;
		String name = null;
		while (true) {
			name = "observable" + count;	
			if (getMolecularType(name) == null) {
				break;
			}	
			count++;
		}
		return new Observable(name);
	}
	
	public RbmParameter createParameter() {
		int count=0;
		String name = null;
		while (true) {
			name = "param" + count;	
			if (getParameter(name) == null) {
				break;
			}	
			count++;
		}
		return new RbmParameter(name, null);
	}
	
	public SeedSpecies createSeedSpecies() {		
		return new SeedSpecies();
	}
	
	public ReactionRule createReactionRule() {
		return createReactionRule(true);
	}
	
	public ReactionRule createReactionRule(boolean bReversible) {		
		return new ReactionRule(bReversible);
	}
	
	public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException {
		if (evt.getPropertyName().equals(PropertyConstants.PROPERTY_NAME_NAME)) {
			if (evt.getSource() instanceof MolecularType) {
				String newName = (String) evt.getNewValue();
				for (MolecularType molecularType : molecularTypeList) {
					if (molecularType != evt.getSource()) {
						if (molecularType.getName().equals(newName)) {
							throw new PropertyVetoException("Molecular Type '" + newName + "' already exists!", evt);
						}
					}
				}
				for (Observable observable : observableList) {
					if (observable != evt.getSource()) {
						if (observable.getName().equals(newName)) {
							throw new PropertyVetoException("'" + newName + "' is already used for an observable!", evt);
						}
					}
				}
			} else if (evt.getSource() instanceof Observable) {
				String newName = (String) evt.getNewValue();
				for (MolecularType molecularType : molecularTypeList) {
					if (molecularType != evt.getSource()) {
						if (molecularType.getName().equals(newName)) {
							throw new PropertyVetoException("'" + newName + "' is already used for a molecular Type!", evt);
						}
					}
				}
				for (Observable observable : observableList) {
					if (observable != evt.getSource()) {
						if (observable.getName().equals(newName)) {
							throw new PropertyVetoException("Observable '" + newName + "' already exists!", evt);
						}
					}
				}
			}
		}
	}

	public void clear() {
		setMolecularTypeList(new ArrayList<MolecularType>());
		setReactionRules(new ArrayList<ReactionRule>());
		setObservableList(new ArrayList<Observable>());
		setSeedSpeciesList(new ArrayList<SeedSpecies>());
		setParameterList(new ArrayList<RbmParameter>());
		networkConstraints.clear();
	}

	private final void setObservableList(List<Observable> newValue) {
		List<Observable> oldValue = observableList;
		if (oldValue != null) {
			for (Observable mt : oldValue) {
				mt.removeVetoableChangeListener(this);
			}
		}
		this.observableList = newValue;
		if (newValue != null) {
			for (Observable mt : newValue) {
				mt.addVetoableChangeListener(this);
			}
		}
		firePropertyChange(PROPERTY_NAME_OBSERVABLE_LIST, oldValue, newValue);
	}
	
	private final void setSeedSpeciesList(List<SeedSpecies> newValue) {
		List<SeedSpecies> oldValue = seedSpeciesList;
//		if (oldValue != null) {
//			for (Observable mt : oldValue) {
//				mt.removeVetoableChangeListener(this);
//			}
//		}
		this.seedSpeciesList = newValue;
//		if (newValue != null) {
//			for (Observable mt : newValue) {
//				mt.addVetoableChangeListener(this);
//			}
//		}
		firePropertyChange(PROPERTY_NAME_SEED_SPECIES_LIST, oldValue, newValue);
	}
	
	private final void setParameterList(List<RbmParameter> newValue) {
		List<RbmParameter> oldValue = parameterList;
//		if (oldValue != null) {
//			for (Observable mt : oldValue) {
//				mt.removeVetoableChangeListener(this);
//			}
//		}
		this.parameterList = newValue;
//		if (newValue != null) {
//			for (Observable mt : newValue) {
//				mt.addVetoableChangeListener(this);
//			}
//		}
		firePropertyChange(PROPERTY_NAME_RBM_PARAMETER_LIST, oldValue, newValue);
	}

	public List<MolecularType> getMolecularTypeList() {
		return molecularTypeList;
	}

	public void addObservable(Observable observable) throws ModelException {
		if (getObservable(observable.getName()) != null) {
			throw new ModelException("Observable '" + observable.getName() + "' already exists!");
		}
		List<Observable> newValue = new ArrayList<Observable>(observableList);
		newValue.add(observable);
		setObservableList(newValue);
	}
	
	public void addSeedSpecies(SeedSpecies seedSpecies) throws ModelException {		
		List<SeedSpecies> newValue = new ArrayList<SeedSpecies>(seedSpeciesList);
		newValue.add(seedSpecies);
		setSeedSpeciesList(newValue);
	}
	
	public void addParameter(RbmParameter rbmParameter) throws ModelException {		
		List<RbmParameter> newValue = new ArrayList<RbmParameter>(parameterList);
		newValue.add(rbmParameter);
		setParameterList(newValue);
	}
	
	public Observable getObservable(String obName){
		for (Observable	observable : this.observableList){
			if (observable.getName().equals(obName)){
				return observable;
			}
		}
		return null;
	}
	
	public RbmParameter getParameter(String obName){
		for (RbmParameter rbmParameter : this.parameterList){
			if (rbmParameter.getName().equals(obName)){
				return rbmParameter;
			}
		}
		return null;
	}
	
	public boolean removeObservable(Observable observable) {
		if (!observableList.contains(observable)) {
			return false;
		}
		ArrayList<Observable> newValue = new ArrayList<Observable>(observableList);
		newValue.remove(observable);
		setObservableList(newValue);
		return true;
	}
	
	public boolean removeSeedSpecies(SeedSpecies seedSpecies) {
		if (!seedSpeciesList.contains(seedSpecies)) {
			return false;
		}
		ArrayList<SeedSpecies> newValue = new ArrayList<SeedSpecies>(seedSpeciesList);
		newValue.remove(seedSpecies);
		setSeedSpeciesList(newValue);
		return true;
	}
	
	public boolean removeParameter(RbmParameter rbmParameter) {
		if (!parameterList.contains(rbmParameter)) {
			return false;
		}
		ArrayList<RbmParameter> newValue = new ArrayList<RbmParameter>(parameterList);
		newValue.remove(rbmParameter);
		setParameterList(newValue);
		return true;
	}
	
	public void addReactionRule(ReactionRule reactionRule) {		
		List<ReactionRule> newValue = new ArrayList<ReactionRule>(reactionRuleList);
		newValue.add(reactionRule);
		setReactionRules(newValue);
	}

	public List<ReactionRule> getReactionRuleList() {
		return reactionRuleList;
	}
	public List<RbmParameter> getParameterList() {
		return parameterList;
	}

	public final NetworkConstraints getNetworkConstraints() {
		return networkConstraints;
	}
	
}
