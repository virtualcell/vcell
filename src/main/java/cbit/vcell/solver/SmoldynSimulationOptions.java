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

@SuppressWarnings("serial")
public class SmoldynSimulationOptions implements Serializable, Matchable, VetoableChangeListener {

	private Integer randomSeed = null;
	private double accuracy = 10.0;;
	private int gaussianTableSize = 4096;
	private boolean useHighResolutionSample = true;
	private boolean saveParticleLocations = false;

	protected transient PropertyChangeSupport propertyChange;
	protected transient VetoableChangeSupport vetoChange;
	
	public static final String PROPERTY_NAME_RANDOM_SEED = "randomSeed";
	public static final String PROPERTY_NAME_ACCURACY = "accuracy";
	public static final String PROPERTY_NAME_GAUSSIAN_TABLE_SIZE = "gaussianTableSize";
	public static final String PROPERTY_NAME_USE_HIGH_RES = "useHighResolutionSample";
	public static final String PROPERTY_NAME_SAVE_PARTICLE_LOCS = "saveParticleLocations";
	
	public SmoldynSimulationOptions() {
		removeVetoableChangeListener(this);
		addVetoableChangeListener(this);
	}
	
	/**
	 * @param smoldynSimulationOptions if null, same as {@link #SmoldynSimulationOptions()}
	 */
	public SmoldynSimulationOptions(SmoldynSimulationOptions smoldynSimulationOptions) {
		this();
		if (smoldynSimulationOptions != null) {
			randomSeed = smoldynSimulationOptions.randomSeed;
			accuracy = smoldynSimulationOptions.accuracy;
			gaussianTableSize = smoldynSimulationOptions.gaussianTableSize;
			useHighResolutionSample = smoldynSimulationOptions.useHighResolutionSample;
			saveParticleLocations = smoldynSimulationOptions.saveParticleLocations;
		}
	}

	public SmoldynSimulationOptions(CommentStringTokenizer tokens) throws DataAccessException {
		this();
		readVCML(tokens);
	}

	public boolean isUseHighResolutionSample() {
		return useHighResolutionSample;
	}
	public boolean isSaveParticleLocations() {
		return saveParticleLocations;
	}

	public void setUseHighResolutionSample(boolean newValue) {
		boolean oldValue = this.useHighResolutionSample;
		this.useHighResolutionSample = newValue;
		firePropertyChange(PROPERTY_NAME_USE_HIGH_RES, oldValue, newValue);
	}
	public void setSaveParticleLocations(boolean newValue) {
		boolean oldValue = this.saveParticleLocations;
		this.saveParticleLocations = newValue;
		firePropertyChange(PROPERTY_NAME_SAVE_PARTICLE_LOCS, oldValue, newValue);
	}
	
	public boolean compareEqual(Matchable obj) {
		if (!(obj instanceof SmoldynSimulationOptions)) {
			return false;
		}
		SmoldynSimulationOptions smoldynSimulationOptions = (SmoldynSimulationOptions)obj;
		if (!Compare.isEqualOrNull(randomSeed, smoldynSimulationOptions.randomSeed)) {
			return false;
		}
		if (accuracy != smoldynSimulationOptions.accuracy) {
			return false;
		}
		if (gaussianTableSize != smoldynSimulationOptions.gaussianTableSize) {
			return false;
		}
		if (useHighResolutionSample != smoldynSimulationOptions.useHighResolutionSample) {
			return false;
		}
		if (saveParticleLocations != smoldynSimulationOptions.saveParticleLocations) {
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

	public final double getAccuracy() {
		return accuracy;
	}

	public final void setAccuracy(double newValue) {
		double oldValue = this.accuracy;		
		this.accuracy = newValue;
		firePropertyChange(PROPERTY_NAME_ACCURACY, oldValue, newValue);
	}

	public int getGaussianTableSize() {
		return gaussianTableSize;
	}

	public final void setGaussianTableSize(int newValue) throws PropertyVetoException {
		int oldValue = this.gaussianTableSize;
		fireVetoableChange(PROPERTY_NAME_GAUSSIAN_TABLE_SIZE, oldValue, newValue);
		this.gaussianTableSize = newValue;		
		firePropertyChange(PROPERTY_NAME_GAUSSIAN_TABLE_SIZE, oldValue, newValue);
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
		buffer.append("\t" + VCML.SmoldynSimulationOptions + " " + VCML.BeginBlock + "\n");
		buffer.append("\t\t" + VCML.SmoldynSimulationOptions_accuracy + " " + accuracy + "\n");
		buffer.append("\t\t" + VCML.SmoldynSimulationOptions_gaussianTableSize + " " + gaussianTableSize + "\n");
		buffer.append("\t\t" + VCML.SmoldynSimulationOptions_useHighResolutionSample + " " + useHighResolutionSample + "\n");
		if (randomSeed != null) {
			buffer.append("\t\t" + VCML.SmoldynSimulationOptions_randomSeed + " " + randomSeed + "\n");			
		}
		buffer.append("\t\t" + VCML.SmoldynSimulationOptions_saveParticleLocations + " " + saveParticleLocations + "\n");
		buffer.append("\t" + VCML.EndBlock + "\n");
		
		return buffer.toString();
	}
	
	private void readVCML(CommentStringTokenizer tokens) throws DataAccessException {
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
			if (token.equalsIgnoreCase(VCML.SmoldynSimulationOptions_accuracy)) {
				token = tokens.nextToken();
				accuracy = Double.parseDouble(token);
			} else if (token.equalsIgnoreCase(VCML.SmoldynSimulationOptions_randomSeed)) {
				token = tokens.nextToken();
				randomSeed = new Integer(token);
			} else if (token.equalsIgnoreCase(VCML.SmoldynSimulationOptions_gaussianTableSize)) {
				token = tokens.nextToken();
				gaussianTableSize = Integer.parseInt(token);
			} else if (token.equalsIgnoreCase(VCML.SmoldynSimulationOptions_useHighResolutionSample)) {
				token = tokens.nextToken();
				useHighResolutionSample = Boolean.parseBoolean(token);
			} else if (token.equalsIgnoreCase(VCML.SmoldynSimulationOptions_saveParticleLocations)) {
				token = tokens.nextToken();
				saveParticleLocations = Boolean.parseBoolean(token);
			}  else { 
				throw new DataAccessException("unexpected identifier " + token);
			}
		}
	}

	public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException {
		if (evt.getPropertyName().equals(PROPERTY_NAME_GAUSSIAN_TABLE_SIZE)) {
			int newValue = (Integer)evt.getNewValue();
			if (!SolverUtilities.isPowerOf2(newValue)) {
				throw new PropertyVetoException("Gaussian table size must be an integer power of 2.", evt);
			}
		}
		
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
