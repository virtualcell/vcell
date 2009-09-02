package cbit.vcell.document;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.util.ArrayList;

import cbit.vcell.math.Function;
import cbit.vcell.parser.NameScope;
import cbit.vcell.solver.Simulation;
/**
 * Insert the type's description here.
 * Creation date: (6/4/2004 1:56:12 AM)
 * @author: Ion Moraru
 */
public interface SimulationOwner {

	Simulation addNewSimulation() throws PropertyVetoException;

	void addPropertyChangeListener(PropertyChangeListener listener);

	Simulation copySimulation(Simulation simulation) throws PropertyVetoException;

	Simulation[] getSimulations();

	void removePropertyChangeListener(PropertyChangeListener listener);

	void removeSimulation(Simulation simulation) throws PropertyVetoException;

	ArrayList<Function> getObservableFunctionsList();
	
	void addObservableFunction(Function obsFunction) throws PropertyVetoException;
	
	void removeObservableFunction(Function obsFunction) throws PropertyVetoException;

	void replaceObservableFunction(Function oldObsFunction, Function newObsFunction) throws PropertyVetoException;
	
	NameScope getNameScope(); 

}