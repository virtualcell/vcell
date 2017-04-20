package cbit.vcell.model;

import org.vcell.model.rbm.SpeciesPattern;
import org.vcell.util.Compare;
import org.vcell.util.Displayable;
import org.vcell.util.Matchable;

import cbit.vcell.units.VCUnitDefinition;

public abstract class ReactionRuleParticipant implements Displayable, ModelProcessParticipant {
	private final SpeciesPattern speciesPattern;
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private Structure structure;
	
	public ReactionRuleParticipant(SpeciesPattern speciesPattern, Structure structure){
		this.speciesPattern = speciesPattern;
		this.structure = structure;
		if (structure == null){
			throw new RuntimeException("Null structure for ReactionRule Participant");
		}
	}
	
	protected java.beans.PropertyChangeSupport getPropertyChange() {
		if (propertyChange == null) {
			propertyChange = new java.beans.PropertyChangeSupport(this);
		};
		return propertyChange;
	}

	public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
		getPropertyChange().addPropertyChangeListener(listener);
	}
	public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
		getPropertyChange().removePropertyChangeListener(listener);
	}
	
	public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
	}

	public SpeciesPattern getSpeciesPattern(){
		return this.speciesPattern;
	}
	
	public Structure getStructure(){
		return this.structure;
	}
	public void setStructure(Structure structure){
		Structure oldStructure = this.structure;
		if (structure == null){
			throw new RuntimeException("Null structure for ReactionRule Participant");
		}
		this.structure = structure;
		firePropertyChange("structure",oldStructure,structure);
	}
	
	public VCUnitDefinition getUnitDefinition(){
		Model model = structure.getModel();
		if (model!=null){
			return model.getUnitSystem().getConcentrationUnit(structure);
		}
		System.err.println("ReactionRuleParticipant.getUnitDefinition() returned null, structure.getModel() for "+structure.getName()+" was null");
		return null;
	}

	public String getDisplayNameShort() {
		return getSpeciesPattern().getNameShort();
	}
	
	@Override
	public boolean compareEqual(Matchable obj) {
		if (getClass().equals(obj.getClass())){
			ReactionRuleParticipant other = (ReactionRuleParticipant)obj;
			if (!Compare.isEqual(speciesPattern, other.speciesPattern)){
				return false;
			}
			return true;
		}
		return false;
	}

	public static boolean matchesSignature(ReactionRuleParticipant participant, RuleParticipantSignature signature, RuleParticipantSignature.Criteria crit) {
		if(signature == null) {
			return false;
		}
		if(participant.getStructure() != signature.getStructure()) {
			return false;
		}
		if(signature.compareByCriteria(participant.getSpeciesPattern(), crit)) {
			return true;
		}
		return false;
	}
	
}
