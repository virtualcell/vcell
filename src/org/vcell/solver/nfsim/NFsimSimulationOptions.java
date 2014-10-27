/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.solver.nfsim;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.beans.VetoableChangeSupport;
import java.io.Serializable;

import org.vcell.util.CommentStringTokenizer;
import org.vcell.util.Compare;
import org.vcell.util.DataAccessException;
import org.vcell.util.Matchable;

import cbit.vcell.math.VCML;

public class NFsimSimulationOptions implements Serializable, Matchable, VetoableChangeListener {

	private Integer randomSeed = null;

	protected transient PropertyChangeSupport propertyChange;
	protected transient VetoableChangeSupport vetoChange;
	
	public static final String PROPERTY_NAME_RANDOM_SEED = "randomSeed";
	
	public NFsimSimulationOptions() {
		removeVetoableChangeListener(this);
		addVetoableChangeListener(this);
	}
	
	public NFsimSimulationOptions(NFsimSimulationOptions smoldynSimulationOptions) {
		this();
		randomSeed = smoldynSimulationOptions.randomSeed;
	}

	public NFsimSimulationOptions(CommentStringTokenizer tokens) throws DataAccessException {
		this();
		readVCML(tokens);
	}
	
	public boolean compareEqual(Matchable obj) {
		if (!(obj instanceof NFsimSimulationOptions)) {
			return false;
		}
		NFsimSimulationOptions smoldynSimulationOptions = (NFsimSimulationOptions)obj;
		if (!Compare.isEqualOrNull(randomSeed, smoldynSimulationOptions.randomSeed)) {
			return false;
		}
		return true;
	}

	public final Integer getRandomSeed() {
		return randomSeed;
	}

	public final void setRandomSeed(Integer newValue) {
		Integer oldValue = this.randomSeed;
		this.randomSeed = newValue;
		firePropertyChange(PROPERTY_NAME_RANDOM_SEED, oldValue, newValue);
	}

	public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
		getPropertyChange().addPropertyChangeListener(listener);
	}
	
	public synchronized void addVetoableChangeListener(java.beans.VetoableChangeListener listener) {
		getVetoChange().addVetoableChangeListener(listener);
	}
	
	private java.beans.PropertyChangeSupport getPropertyChange() {
		if (propertyChange == null) {
			propertyChange = new java.beans.PropertyChangeSupport(this);
		};
		return propertyChange;
	}
	
	private java.beans.VetoableChangeSupport getVetoChange() {
		if (vetoChange == null) {
			vetoChange = new java.beans.VetoableChangeSupport(this);
		};
		return vetoChange;
	}
	
	private void firePropertyChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue) {
		getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
	}

	private void fireVetoableChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue) throws PropertyVetoException {
		getVetoChange().fireVetoableChange(propertyName, oldValue, newValue);
	}
	
	public String getVCML() {		
		StringBuffer buffer = new StringBuffer();
		buffer.append("\t" + VCML.NFSimSimulationOptions + " " + VCML.BeginBlock + "\n");
		if (randomSeed != null) {
			buffer.append("\t\t" + VCML.NFSimSimulationOptions_randomSeed + " " + randomSeed + "\n");			
		}
		buffer.append("\t" + VCML.EndBlock + "\n");
		
		return buffer.toString();
	}
	
	public void readVCML(CommentStringTokenizer tokens) throws DataAccessException {
		String token = tokens.nextToken();
		if (token.equalsIgnoreCase(VCML.SmoldynSimulationOptions)) {
			token = tokens.nextToken();
			if (!token.equalsIgnoreCase(VCML.BeginBlock)) {
				throw new DataAccessException("unexpected token " + token + " expecting " + VCML.BeginBlock); 
			}
		}
		
		while (tokens.hasMoreTokens()) {
			token = tokens.nextToken();
			if (token.equalsIgnoreCase(VCML.EndBlock)) {
				break;
			}
			if (token.equalsIgnoreCase(VCML.NFSimSimulationOptions_randomSeed)) {
				token = tokens.nextToken();
				randomSeed = new Integer(token);
			}  else { 
				throw new DataAccessException("unexpected identifier " + token);
			}
		}
	}

	public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException {
	}
	
	public synchronized void removeVetoableChangeListener(java.beans.VetoableChangeListener listener) {
		getVetoChange().removeVetoableChangeListener(listener);
	}

	public synchronized void removePropertyChangeListener(PropertyChangeListener listener) {
		getPropertyChange().removePropertyChangeListener(listener);
	}
	
	public void refreshDependencies() {
		removeVetoableChangeListener(this);
		addVetoableChangeListener(this);
	}
}
