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

	public final static int[] DefaultNPart = { 10, 10, 10 };

	public final static int DefaultRandomSeed = 1;
	public final static int DefaultNumTrials = 1;

	protected int numTrials = DefaultNumTrials;	// how many runs of the solver for this simulation
	protected int numOfParallelLocalRuns = 1;	// how many instances of the solver run in parallel
	protected int runIndex = 0;					// run index, will result in Run0 (followed by Run1, 2,...)
	protected Integer randomSeed = DefaultRandomSeed;

	protected double intervalSpring = 1.00E-9;	// default: dtspring: 1.00E-9	- from advanced
	protected double intervalImage = 1.00E-4;	// default: dtimage: 1.00E-4	- from advanced
	protected int[] npart = {DefaultNPart[0], DefaultNPart[1], DefaultNPart[2]};	// number of partitions on each axis

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
		numTrials = langevinSimulationOptions.numTrials;
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
		if(numTrials != langevinSimulationOptions.numTrials) {
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

	// can be between 0 and numOfTrials-1
	public int getRunIndex() {	// for multiple trials the runIndex must be incremented for each run, dynamically
		if(runIndex > numTrials -1) {
			throw new RuntimeException("Max run index must be smaller than the number of trials.");
		}
		int currentRunIndex = runIndex;
		runIndex++;
		return currentRunIndex;
	}
	public int getNumTrials() {
		return numTrials;
	}
	public int getNumOfParallelLocalRuns() {
		return numOfParallelLocalRuns;
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
	public Integer getRandomSeed() {
		return randomSeed;
	}

	public final void setRunIndex(int newValue) {
		this.runIndex = newValue;
	}
	public final void setNumTrials(int newValue) {
		this.numTrials = newValue;
	}
	public final void setNumOfParallelLocalRuns(int newValue) {
		this.numOfParallelLocalRuns = newValue;
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
	public final void setNPart(int index, int npart) {
		this.npart[index] = npart;
	}
	public void setRandomSeed(Integer randomSeed) {
		this.randomSeed = randomSeed;
	}

	// ------------------------------------------------------------------------------------------

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

	// used for VCML export
	public String getVCML() {		
		StringBuffer buffer = new StringBuffer();
		buffer.append("\t" + VCML.LangevinSimulationOptions + " " + VCML.BeginBlock + "\n");
		buffer.append("\t\t" + VCML.LangevinSimulationOptions_numOfTrials + " " + numTrials + "\n");
		buffer.append("\t\t" + VCML.LangevinSimulationOptions_runIndex + " " + runIndex + "\n");			
		buffer.append("\t\t" + VCML.LangevinSimulationOptions_intervalSpring + " " + intervalSpring + "\n");
		buffer.append("\t\t" + VCML.LangevinSimulationOptions_intervalImage + " " + intervalImage + "\n");
		buffer.append("\t\t" + VCML.LangevinSimulationOptions_Partition_Nx + " " + npart[0] + "\n");
		buffer.append("\t\t" + VCML.LangevinSimulationOptions_Partition_Ny + " " + npart[1] + "\n");
		buffer.append("\t\t" + VCML.LangevinSimulationOptions_Partition_Nz + " " + npart[2] + "\n");
		buffer.append("\t\t" + VCML.LangevinSimulationOptions_numOfParallelLocalRuns + " " + numOfParallelLocalRuns + "\n");
		buffer.append("\t" + VCML.EndBlock + "\n");
		return buffer.toString();
	}

	// used for VCML import
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
			} else if(token.equalsIgnoreCase(VCML.LangevinSimulationOptions_numOfParallelLocalRuns)) {
				token = tokens.nextToken();
				numOfParallelLocalRuns = Integer.parseInt(token);
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
					numTrials = val2;
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
