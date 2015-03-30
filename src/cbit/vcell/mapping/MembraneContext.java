/*
 * Copyright (C) 2015 UConn Health 
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.mapping;
import java.beans.PropertyChangeEvent;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.beans.VetoableChangeSupport;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.vcell.util.Compare;
import org.vcell.util.Matchable;

import cbit.vcell.model.Membrane;
import cbit.vcell.model.Model;
@SuppressWarnings("serial")
public  class MembraneContext implements Serializable, Matchable, PropertyChangeListener, VetoableChangeListener {

	/**
	 * 
	 */
	private final SimulationContext simContext; 
	private List<MembraneSpec>  specList;
	
	protected transient java.beans.VetoableChangeSupport vetoPropertyChange;
	protected transient java.beans.PropertyChangeSupport propertyChange;


	public MembraneContext(SimulationContext simContext) {
		this.simContext = simContext;
		specList = new ArrayList<>();
		vetoPropertyChange = new VetoableChangeSupport(this);
		propertyChange = new PropertyChangeSupport(this);
		Model mdl = simContext.getModel();
		specList = membranesOf(mdl);
	}
	
	public List<MembraneSpec> getMembraneSpecs( ) {
		return specList; 
	}

	@Override
	public void vetoableChange(PropertyChangeEvent evt)
			throws PropertyVetoException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean compareEqual(Matchable obj) {
		if (obj instanceof MembraneContext) {
			MembraneContext rhs = (MembraneContext) obj; 
			return simContext.compareEqual(rhs.simContext) && Compare.areEquivalent(specList, rhs.specList);
		}
		return false;
	}
	
	
	private static List<MembraneSpec> membranesOf(Model mdl) {
		ArrayList<MembraneSpec> rval = new ArrayList<>();
		for (Membrane m : mdl.getMembranes()) {
			rval.add(new MembraneSpec(m));
		}
		return rval;
	}
	
	
	
	
	
	


}
