/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.solver;

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

	boolean observableComputationOff = false;
	Integer moleculeDistance = null;
	boolean aggregateBookkeeping = false;
	Integer maxMoleculesPerType = null;
	Integer equilibrateTime = null;
	private Integer randomSeed = null;
	boolean preventIntraBonds = false;

	protected transient PropertyChangeSupport propertyChange;
	protected transient VetoableChangeSupport vetoChange;
	
	public static final String PROPERTY_NAME_RANDOM_SEED = "randomSeed";
	
	public NFsimSimulationOptions() {
		removeVetoableChangeListener(this);
		addVetoableChangeListener(this);
	}
	
	public NFsimSimulationOptions(NFsimSimulationOptions smoldynSimulationOptions) {
		this();
		observableComputationOff = smoldynSimulationOptions.observableComputationOff;
		moleculeDistance = smoldynSimulationOptions.moleculeDistance;
		aggregateBookkeeping = smoldynSimulationOptions.aggregateBookkeeping;
		maxMoleculesPerType = smoldynSimulationOptions.maxMoleculesPerType;
		equilibrateTime = smoldynSimulationOptions.equilibrateTime;
		randomSeed = smoldynSimulationOptions.randomSeed;
		preventIntraBonds = smoldynSimulationOptions.preventIntraBonds;
	}

	public NFsimSimulationOptions(CommentStringTokenizer tokens) throws DataAccessException {
		this();
		readVCML(tokens);
	}
	
	public boolean compareEqual(Matchable obj) {
		if (!(obj instanceof NFsimSimulationOptions)) {
			return false;
		}
		NFsimSimulationOptions nfsimSimulationOptions = (NFsimSimulationOptions)obj;
		if (observableComputationOff != nfsimSimulationOptions.observableComputationOff) {
			return false;
		}
		if (!Compare.isEqualOrNull(moleculeDistance, nfsimSimulationOptions.moleculeDistance)) {
			return false;
		}
		if (aggregateBookkeeping != nfsimSimulationOptions.aggregateBookkeeping) {
			return false;
		}
		if (!Compare.isEqualOrNull(maxMoleculesPerType, nfsimSimulationOptions.maxMoleculesPerType)) {
			return false;
		}
		if (!Compare.isEqualOrNull(equilibrateTime, nfsimSimulationOptions.equilibrateTime)) {
			return false;
		}
		if (!Compare.isEqualOrNull(randomSeed, nfsimSimulationOptions.randomSeed)) {
			return false;
		}
		if (preventIntraBonds != nfsimSimulationOptions.preventIntraBonds) {
			return false;
		}
		return true;
	}
// -----------------------------------------------------------------------------------
	public final boolean getObservableComputationOff() {
		return observableComputationOff;
	}
	public final void setObservableComputationOff(boolean newValue) {
		this.observableComputationOff = newValue;
	}
	public final Integer getMoleculeDistance() {
		return moleculeDistance;
	}
	public final void setMoleculeDistance(Integer newValue) {
		this.moleculeDistance = newValue;
	}
	public final boolean getAggregateBookkeeping() {
		return aggregateBookkeeping;
	}
	public final void setAggregateBookkeeping(boolean newValue) {
		this.aggregateBookkeeping = newValue;
	}
	public final Integer getMaxMoleculesPerType() {
		return maxMoleculesPerType;
	}
	public final void setMaxMoleculesPerType(Integer newValue) {
		this.maxMoleculesPerType = newValue;
	}
	public final Integer getEquilibrateTime() {
		return equilibrateTime;
	}
	public final void setEquilibrateTime(Integer newValue) {
		this.equilibrateTime = newValue;
	}
	public final Integer getRandomSeed() {
		return randomSeed;
	}
	public final void setRandomSeed(Integer newValue) {
		Integer oldValue = this.randomSeed;
		this.randomSeed = newValue;
		firePropertyChange(PROPERTY_NAME_RANDOM_SEED, oldValue, newValue);
	}
	public final boolean getPreventIntraBonds() {
		return preventIntraBonds;
	}
	public final void setPreventIntraBonds(boolean newValue) {
		this.preventIntraBonds = newValue;
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
