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
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.util.Map;

import cbit.vcell.geometry.Geometry;
import org.vcell.util.Issue;
import org.vcell.util.IssueContext;

import cbit.vcell.geometry.GeometryOwner;
import cbit.vcell.math.MathDescription;
/**
 * Insert the type's description here.
 * Creation date: (6/4/2004 1:56:12 AM)
 * @author: Ion Moraru
 */
public interface SimulationOwner extends GeometryOwner {

	public static class StandaloneSimulationOwner implements SimulationOwner {

		@Override
		public Geometry getGeometry() {
			return null;
		}

		@Override
		public void addPropertyChangeListener(PropertyChangeListener listener) {

		}

		@Override
		public void removePropertyChangeListener(PropertyChangeListener listener) {

		}

		@Override
		public Simulation copySimulation(Simulation simulation) throws PropertyVetoException {
			return null;
		}

		@Override
		public Simulation[] getSimulations() {
			return new Simulation[0];
		}

		@Override
		public void removeSimulation(Simulation simulation) throws PropertyVetoException {

		}

		@Override
		public OutputFunctionContext getOutputFunctionContext() {
			return null;
		}

		@Override
		public MathOverridesResolver getMathOverridesResolver() {
			return null;
		}

		@Override
		public MathDescription getMathDescription() {
			return null;
		}

		@Override
		public String getName() {
			return null;
		}

		@Override
		public Issue gatherIssueForMathOverride(IssueContext issueContext, Simulation simulation, String overriddenConstantName) {
			return null;
		}

		@Override
		public UnitInfo getUnitInfo() throws UnsupportedOperationException {
			return null;
		}
	}
	public static String DEFAULT_SIM_NAME_PREFIX = "Simulation";
	public interface FieldDataSimOwner extends SimulationOwner{}	// Typed simulationOwner for FieldData
	
	Simulation copySimulation(Simulation simulation) throws PropertyVetoException;
	Simulation[] getSimulations();
	void removeSimulation(Simulation simulation) throws PropertyVetoException;
	OutputFunctionContext getOutputFunctionContext();
	MathOverridesResolver getMathOverridesResolver();
	MathDescription getMathDescription();
	String getName();
	public Issue gatherIssueForMathOverride(IssueContext issueContext, Simulation simulation, String overriddenConstantName);
	
	/**
	 * @return UnitInfo (not null)
	 * @throws UnsupportedOperationException if not implemented
	 */
	public UnitInfo getUnitInfo( ) throws UnsupportedOperationException;
	/**
	 * provide unit information for display purposes
	 */
	public static class UnitInfo {
		private final String timeUnitString;

		public UnitInfo(String timeUnitString) {
			super();
			this.timeUnitString = timeUnitString;
		}

		public String getTimeUnitString() {
			return timeUnitString;
		}
	}
}

