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

	// TODO: add the partition definitions in the LangevinOptionsPanel

	public final static String Partition_Nx = "Partition Nx: ";
	public final static String Partition_Ny = "Partition Ny: ";
	public final static String Partition_Nz = "Partition Nz: ";

	public final static double DefaultEndingTime = 1E-2;				// default: totalTime
	public final static double DefaultIntervalOutputTime = 1.00E-4;		// default: dtdata

	public final static double DefaultMinimumTimeStep = 1.00E-8;
	public final static double DefaultDefaultTimeStep = 1.00E-8;		// default: dt
	public final static double DefaultMaximumTimeStep = 1.00E-8;

	protected int numOfTrials = 1;
	protected int runIndex = 0;				// run index, will result in Run0 (followed by Run1, 2,...)
	protected double intervalSpring = 1.00E-9;	// default: dtspring: 1.00E-9	- from advanced
	protected double intervalImage = 1.00E-4;	// default: dtimage: 1.00E-4	- from advanced
	protected int[] npart = { 10, 10, 10 };		// default number of partitions on each axis

	protected transient PropertyChangeSupport propertyChange;
	protected transient VetoableChangeSupport vetoChange;
	
	public LangevinSimulationOptions() {
		removeVetoableChangeListener(this);
		addVetoableChangeListener(this);
	}
	
	public LangevinSimulationOptions(LangevinSimulationOptions langevinSimulationOptions) {
		this();				// TODO: properly implement copy constructor
		intervalSpring = langevinSimulationOptions.intervalSpring;
		intervalImage = langevinSimulationOptions.intervalImage;
		numOfTrials = langevinSimulationOptions.numOfTrials;
		runIndex = langevinSimulationOptions.runIndex;
		npart[0] = langevinSimulationOptions.npart[0];
		npart[1] = langevinSimulationOptions.npart[1];
		npart[2] = langevinSimulationOptions.npart[2];
	}

	public LangevinSimulationOptions(CommentStringTokenizer tokens) throws DataAccessException {
		this();
		readVCML(tokens);
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
		if(npart[0] != langevinSimulationOptions.npart[0] ||
				npart[1] != langevinSimulationOptions.npart[1] ||
				npart[2] != langevinSimulationOptions.npart[2]) {
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
	public int getNPart(int index) {
		return npart[index];
	}
	public int[] getNPart() {
		return npart;
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
	public final void setNPart(int[] npart) {
		this.npart[0] = npart[0];
		this.npart[1] = npart[1];
		this.npart[2] = npart[2];
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
		buffer.append("\t" + VCML.LangevinSimulationOptions + " " + VCML.BeginBlock + "\n");
		buffer.append("\t\t" + VCML.LangevinSimulationOptions_numOfTrials + " " + numOfTrials + "\n");			
		buffer.append("\t\t" + VCML.LangevinSimulationOptions_runIndex + " " + runIndex + "\n");			
		buffer.append("\t\t" + VCML.LangevinSimulationOptions_intervalSpring + " " + intervalSpring + "\n");
		buffer.append("\t\t" + VCML.LangevinSimulationOptions_intervalImage + " " + intervalImage + "\n");
		buffer.append("\t\t" + VCML.LangevinSimulationOptions_Partition_Nx + " " + npart[0] + "\n");
		buffer.append("\t\t" + VCML.LangevinSimulationOptions_Partition_Ny + " " + npart[1] + "\n");
		buffer.append("\t\t" + VCML.LangevinSimulationOptions_Partition_Nz + " " + npart[2] + "\n");
		buffer.append("\t" + VCML.EndBlock + "\n");
		return buffer.toString();
	}
	
	public void readVCML(CommentStringTokenizer tokens) throws DataAccessException {
		String token = tokens.nextToken();
		if (token.equalsIgnoreCase(VCML.LangevinSimulationOptions)) {
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
			if(token.equalsIgnoreCase(VCML.LangevinSimulationOptions_runIndex)) {
				token = tokens.nextToken();
				runIndex = Integer.parseInt(token);
			} else if(token.equalsIgnoreCase(VCML.LangevinSimulationOptions_intervalSpring)) {
				token = tokens.nextToken();
				intervalSpring = Double.parseDouble(token);
			} else if(token.equalsIgnoreCase(VCML.LangevinSimulationOptions_intervalImage)) {
				token = tokens.nextToken();
				intervalImage = Double.parseDouble(token);
			} else if(token.equalsIgnoreCase(VCML.LangevinSimulationOptions_Partition_Nx)) {
				token = tokens.nextToken();
				npart[0] = Integer.parseInt(token);
			} else if(token.equalsIgnoreCase(VCML.LangevinSimulationOptions_Partition_Ny)) {
				token = tokens.nextToken();
				npart[1] = Integer.parseInt(token);
			} else if(token.equalsIgnoreCase(VCML.LangevinSimulationOptions_Partition_Nz)) {
				token = tokens.nextToken();
				npart[2] = Integer.parseInt(token);
			} else if (token.equalsIgnoreCase(VCML.LangevinSimulationOptions_numOfTrials)) {
				token = tokens.nextToken();
				int val2 = Integer.parseInt(token);
				if(val2 < 1 ) {
					throw new DataAccessException("unexpected token " + token + ", num of trials is requied to be at least 1. ");
				} else {
					numOfTrials = val2;
				}
			} else { 
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
