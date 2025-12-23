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
import java.math.BigInteger;

import org.vcell.util.CommentStringTokenizer;
import org.vcell.util.DataAccessException;
import org.vcell.util.Matchable;

import cbit.vcell.math.VCML;

public class LangevinSimulationOptions implements Serializable, Matchable, VetoableChangeListener {

	public final static int DefaultNumberOfConcurrentJobs = 20;	// used for multiple runs on the cluster
	public final static int DefaultTotalNumberOfJobs = 100;

	public final static String Partition_Nx = "Partition Nx: ";
	public final static String Partition_Ny = "Partition Ny: ";
	public final static String Partition_Nz = "Partition Nz: ";

	public final static double DefaultEndingTime = 1E-2;				// default: totalTime
	public final static double DefaultIntervalOutputTime = 1.00E-4;		// default: dtdata

	public final static double DefaultMinimumTimeStep = 1.00E-8;
	public final static double DefaultDefaultTimeStep = 1.00E-8;		// default: dt
	public final static double DefaultMaximumTimeStep = 1.00E-8;

	public final static int[] DefaultNPart = { 10, 10, 10 };

	public final static BigInteger DefaultRandomSeed = new BigInteger("164200191287356961681");
	// randomSeed may be null, in which case the solver will generate its own randomSeed as it already does
	protected BigInteger randomSeed = null;

	// both initialized to 1 - only one job will be run on the cluster
	protected int totalNumberOfJobs	= 1;			// how many jobs will be run on the cluster
	protected int numberOfConcurrentJobs = 1;		// how many instances of the solver may run concurrently on the cluster
//	@Deprecated
	protected int numOfParallelLocalRuns = 1;		// replaced by numberOfConcurrentJobs but kept for backward compatibility

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
		this();
		randomSeed = langevinSimulationOptions.randomSeed;
//		numOfParallelLocalRuns = langevinSimulationOptions.numOfParallelLocalRuns;
		totalNumberOfJobs = langevinSimulationOptions.totalNumberOfJobs;
		numberOfConcurrentJobs = langevinSimulationOptions.numberOfConcurrentJobs;
		intervalSpring = langevinSimulationOptions.intervalSpring;
		intervalImage = langevinSimulationOptions.intervalImage;
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
		if(randomSeed != langevinSimulationOptions.randomSeed) {
			return false;
		}
		if(totalNumberOfJobs != langevinSimulationOptions.totalNumberOfJobs) {
			return false;
		}
		if(numberOfConcurrentJobs != langevinSimulationOptions.numberOfConcurrentJobs) {
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

//	@Deprecated
//	public int getNumOfParallelLocalRuns() {	// // can be between 0 and numOfTrials-1
//		return numOfParallelLocalRuns;
//	}
	public int getTotalNumberOfJobs() {
		return totalNumberOfJobs;
	}
	public int getNumberOfConcurrentJobs() {	// // can be between 0 and totalNumberOfJobs-1
		return numberOfConcurrentJobs;
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
	public BigInteger getRandomSeed() {
		return randomSeed;
	}

//	@Deprecated
//	public final void setNumOfParallelLocalRuns(int newValue) {
//		this.numOfParallelLocalRuns = newValue;
//	}
public final void setTotalNumberOfJobs(int newValue) {
	this.totalNumberOfJobs = newValue;
}
	public final void setNumberOfConcurrentJobs(int newValue) {
		this.numberOfConcurrentJobs = newValue;
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
	public void setRandomSeed(BigInteger randomSeed) {
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
		if(randomSeed != null) {
			buffer.append("\t\t" + VCML.LangevinSimulationOptions_randomSeed + " " + randomSeed + "\n");
		}
		buffer.append("\t\t" + VCML.LangevinSimulationOptions_intervalSpring + " " + intervalSpring + "\n");
		buffer.append("\t\t" + VCML.LangevinSimulationOptions_intervalImage + " " + intervalImage + "\n");
		buffer.append("\t\t" + VCML.LangevinSimulationOptions_Partition_Nx + " " + npart[0] + "\n");
		buffer.append("\t\t" + VCML.LangevinSimulationOptions_Partition_Ny + " " + npart[1] + "\n");
		buffer.append("\t\t" + VCML.LangevinSimulationOptions_Partition_Nz + " " + npart[2] + "\n");
		buffer.append("\t\t" + VCML.LangevinSimulationOptions_numberOfConcurrentJobs + " " + numberOfConcurrentJobs + "\n");
		buffer.append("\t\t" + VCML.LangevinSimulationOptions_totalNumberOfJobs + " " + totalNumberOfJobs + "\n");
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
			if(token.equalsIgnoreCase(VCML.LangevinSimulationOptions_randomSeed)) {
				token = tokens.nextToken();
				randomSeed = new BigInteger(token);
			} else if(token.equalsIgnoreCase(VCML.LangevinSimulationOptions_numOfParallelLocalRuns)) {
				token = tokens.nextToken();
				numOfParallelLocalRuns = Integer.parseInt(token);	// not in use anymore, may be present in some old VCML files
			} else if(token.equalsIgnoreCase(VCML.LangevinSimulationOptions_totalNumberOfJobs)) {
				token = tokens.nextToken();
				totalNumberOfJobs = Integer.parseInt(token);
			} else if(token.equalsIgnoreCase(VCML.LangevinSimulationOptions_numberOfConcurrentJobs)) {
				token = tokens.nextToken();
				numberOfConcurrentJobs = Integer.parseInt(token);
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
