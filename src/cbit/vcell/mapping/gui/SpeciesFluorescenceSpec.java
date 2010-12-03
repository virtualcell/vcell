package cbit.vcell.mapping.gui;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import cbit.vcell.model.SpeciesContext;

public class SpeciesFluorescenceSpec {
	public final static String PROPERTYNAME_FLUORESCENCE = "fluorescent";
	private transient PropertyChangeSupport propertyChangeSupport;
	
	private SpeciesContext speciesContext = null;

	private boolean fluorescent = false;
	
	public SpeciesFluorescenceSpec(SpeciesContext speciesContext, boolean bSelected){
		this.speciesContext = speciesContext;
		this.fluorescent = bSelected;
	}

	public boolean isFluorescent() {
		return fluorescent;
	}
	
	public void setFluorescent(boolean argFluorescent) {
		boolean oldValue = this.fluorescent;
		this.fluorescent = argFluorescent;
		getPropertyChangeSupport().firePropertyChange(PROPERTYNAME_FLUORESCENCE, oldValue, fluorescent);
	}
	
	public SpeciesContext getSpeciesContext() {
		return speciesContext;
	}
	
	public void addPropertyChangeListener(PropertyChangeListener listener){
		getPropertyChangeSupport().addPropertyChangeListener(listener);
	}
	
	public void removePropertyChangeListener(PropertyChangeListener listener){
		getPropertyChangeSupport().removePropertyChangeListener(listener);
	}
	
	private PropertyChangeSupport getPropertyChangeSupport(){
		if (propertyChangeSupport==null){
			propertyChangeSupport = new PropertyChangeSupport(this);
		}
		return propertyChangeSupport;
	}
}
