package org.vcell.model.rbm;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.util.List;

import org.vcell.util.document.PropertyConstants;

public class Observable extends RbmElement implements PropertyChangeListener {
	public static final String PROPERTY_NAME_TYPE = "type";
	public static final String PROPERTY_NAME_SPECIES_PATTERN = "speciesPattern";

	public enum ObservableType {
		Molecules,
		Species,
	}
	private String name;
	private SpeciesPattern speciesPattern; 
	private ObservableType type;
	
	public Observable(String name) {
		this(name, ObservableType.Molecules);
	}
	
	public Observable(String name, ObservableType t) {
		this.name = name;
		this.type = t;
		speciesPattern = new SpeciesPattern();
		speciesPattern.addPropertyChangeListener(this);
	}
	
	public final String getName() {
		return name;
	}
	
	public void setName(String newValue) throws PropertyVetoException {
		String oldValue = name;
		fireVetoableChange(PropertyConstants.PROPERTY_NAME_NAME, oldValue, newValue);
		name = newValue;
		firePropertyChange(PropertyConstants.PROPERTY_NAME_NAME, oldValue, newValue);
	}
	
	public final ObservableType getType() {
		return type;
	}
	
	public void setType(ObservableType newValue) throws PropertyVetoException {
		ObservableType oldValue = newValue;
		type = newValue;
		firePropertyChange(PROPERTY_NAME_TYPE, oldValue, newValue);
	}
	public final SpeciesPattern getSpeciesPattern() {
		return speciesPattern;
	}
	
	public void setSpeciesPattern(SpeciesPattern newValue) {
		SpeciesPattern oldValue = speciesPattern;
		if (oldValue != null) {
			oldValue.removePropertyChangeListener(this);
		}
		speciesPattern = newValue;
		if (newValue != null) {
			newValue.addPropertyChangeListener(this);
			resolveBonds();
		}
		firePropertyChange(PROPERTY_NAME_SPECIES_PATTERN, oldValue, newValue);
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() == speciesPattern && evt.getPropertyName().equals(SpeciesPattern.PROPERTY_NAME_MOLECULAR_TYPE_PATTERNS)) {
			resolveBonds();
		}		
	}
	
	private void resolveBonds() {
		List<MolecularTypePattern> molecularTypePatterns = speciesPattern.getMolecularTypePatterns();
		for (int i = 0; i < molecularTypePatterns.size(); ++ i) {
			molecularTypePatterns.get(i).setIndex(i+1);
		}
		speciesPattern.resolveBonds();
	}
}
