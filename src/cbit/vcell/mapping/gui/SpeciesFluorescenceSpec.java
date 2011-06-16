/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

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
