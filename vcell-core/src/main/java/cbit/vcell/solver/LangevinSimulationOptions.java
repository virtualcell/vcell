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

public class LangevinSimulationOptions implements Serializable, Matchable, VetoableChangeListener {


	protected int numOfTrials = 1;
	protected int runIndex = 0;				// run index, will result in Run0 (fillowed by Run1, 2,...)
	protected double intervalSpring = 1.00E-9;	// default: dt_spring: 1.00E-9	- from advanced
	protected double intervalImage = 1.00E-4;	// default: dt_image: 1.00E-4	- from advanced

	protected transient PropertyChangeSupport propertyChange;
	protected transient VetoableChangeSupport vetoChange;
	
	public LangevinSimulationOptions() {
		removeVetoableChangeListener(this);
		addVetoableChangeListener(this);
	}
	
	public LangevinSimulationOptions(LangevinSimulationOptions smoldynSimulationOptions) {
		this();				// TODO: properly implement copy constructor
	}

	public LangevinSimulationOptions(CommentStringTokenizer tokens) throws DataAccessException {
		this();
		readVCML(tokens);	// TODO: implement read
	}
	
	public boolean compareEqual(Matchable obj) {
		if (!(obj instanceof LangevinSimulationOptions)) {
			return false;
		}
		LangevinSimulationOptions langevinSimulationOptions = (LangevinSimulationOptions)obj;
		if(numOfTrials != langevinSimulationOptions.numOfTrials) {
			return false;
		}
		if(runIndex != langevinSimulationOptions.runIndex) {
			return false;
		}
		if(intervalSpring != langevinSimulationOptions.intervalSpring) {
			return false;
		}
		if(intervalImage != langevinSimulationOptions.intervalImage) {
			return false;
		}
		return true;
	}
// -----------------------------------------------------------------------------------

	public int getRunIndex() {
		return runIndex;
	}
	public int getNumOfTrials() {
		return numOfTrials;
	}
	public double getIntervalSpring() {
		return intervalSpring;
	}
	public double getIntervalImage() {
		return intervalImage;
	}
	public final void setRunIndex(int newValue) {
		this.runIndex = newValue;
	}
	public final void setNumOfTrials(int newValue) {
		this.numOfTrials = newValue;
	}
	public final void setIntervalSpring(double newValue) {
		this.intervalSpring = newValue;
	}
	public final void setIntervalImage(double newValue) {
		this.intervalImage = newValue;
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
//		buffer.append("\t" + VCML.NFSimSimulationOptions + " " + VCML.BeginBlock + "\n");
//		buffer.append("\t\t" + VCML.NFSimSimulationOptions_observableComputationOff + " " + observableComputationOff + "\n");			
//		buffer.append("\t\t" + VCML.NFSimSimulationOptions_preventIntraBonds + " " + preventIntraBonds + "\n");			
//		buffer.append("\t\t" + VCML.NFSimSimulationOptions_aggregateBookkeeping + " " + aggregateBookkeeping + "\n");			
//		buffer.append("\t\t" + VCML.NFSimSimulationOptions_matchComplexes + " " + matchComplexes + "\n");			
//		if (moleculeDistance != null) {
//			buffer.append("\t\t" + VCML.NFSimSimulationOptions_moleculeDistance + " " + moleculeDistance + "\n");			
//		}
//		if (maxMoleculesPerType != null) {
//			buffer.append("\t\t" + VCML.NFSimSimulationOptions_maxMoleculesPerType + " " + maxMoleculesPerType + "\n");			
//		}
//		if (equilibrateTime != null) {
//			buffer.append("\t\t" + VCML.NFSimSimulationOptions_equilibrateTime + " " + equilibrateTime + "\n");			
//		}
//		if (randomSeed != null) {
//			buffer.append("\t\t" + VCML.NFSimSimulationOptions_randomSeed + " " + randomSeed + "\n");			
//		}
//		buffer.append("\t\t" + VCML.NFSimSimulationOptions_NumOfTrials + " " + numOfTrials + "\n");
//
//		buffer.append("\t" + VCML.EndBlock + "\n");
		return buffer.toString();
	}
	
	public void readVCML(CommentStringTokenizer tokens) throws DataAccessException {
//		String token = tokens.nextToken();
//		if (token.equalsIgnoreCase(VCML.SmoldynSimulationOptions)) {
//			token = tokens.nextToken();
//			if (!token.equalsIgnoreCase(VCML.BeginBlock)) {
//				throw new DataAccessException("unexpected token " + token + " expecting " + VCML.BeginBlock); 
//			}
//		}
//		while (tokens.hasMoreTokens()) {
//			token = tokens.nextToken();
//			if (token.equalsIgnoreCase(VCML.EndBlock)) {
//				break;
//			}
//			if(token.equalsIgnoreCase(VCML.NFSimSimulationOptions_observableComputationOff)) {
//				token = tokens.nextToken();
//				observableComputationOff = Boolean.parseBoolean(token);
//			} else if(token.equalsIgnoreCase(VCML.NFSimSimulationOptions_moleculeDistance)) {
//				token = tokens.nextToken();
//				moleculeDistance = new Integer(token);
//			} else if(token.equalsIgnoreCase(VCML.NFSimSimulationOptions_aggregateBookkeeping)) {
//				token = tokens.nextToken();
//				aggregateBookkeeping = Boolean.parseBoolean(token);
//			} else if(token.equalsIgnoreCase(VCML.NFSimSimulationOptions_maxMoleculesPerType)) {
//				token = tokens.nextToken();
//				maxMoleculesPerType = new Integer(token);
//			} else if(token.equalsIgnoreCase(VCML.NFSimSimulationOptions_equilibrateTime)) {
//				token = tokens.nextToken();
//				equilibrateTime = new Integer(token);
//			} else if(token.equalsIgnoreCase(VCML.NFSimSimulationOptions_randomSeed)) {
//				token = tokens.nextToken();
//				randomSeed = new Integer(token);
//			} else if(token.equalsIgnoreCase(VCML.NFSimSimulationOptions_preventIntraBonds)) {
//				token = tokens.nextToken();
//				preventIntraBonds = Boolean.parseBoolean(token);
//			} else if(token.equalsIgnoreCase(VCML.NFSimSimulationOptions_matchComplexes)) {
//				token = tokens.nextToken();
//				matchComplexes = Boolean.parseBoolean(token);
//			} else if (token.equalsIgnoreCase(VCML.NFSimSimulationOptions_NumOfTrials)) {
//				token = tokens.nextToken();
//				int val2 = Integer.parseInt(token);
//				if(val2 < 1 ) {
//					throw new DataAccessException("unexpected token " + token + ", num of trials is requied to be at least 1. ");
//				} else {
//					numOfTrials = val2;
//				}
//			} else { 
//				throw new DataAccessException("unexpected identifier " + token);
//			}
//		}
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
