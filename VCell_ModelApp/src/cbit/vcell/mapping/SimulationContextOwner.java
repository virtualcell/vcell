package cbit.vcell.mapping;

import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;

import cbit.vcell.simulation.Simulation;

public interface SimulationContextOwner {

	void addSimulation(Simulation newSimulation) throws java.beans.PropertyVetoException;

	Simulation[] getSimulations();

	void removeSimulation(Simulation simulation) throws PropertyVetoException;

	void removePropertyChangeListener(PropertyChangeListener listener);

	void removeVetoableChangeListener(VetoableChangeListener listener);

	void addPropertyChangeListener(PropertyChangeListener listener);

	void addVetoableChangeListener(VetoableChangeListener listener);
	
	String getName();

}
