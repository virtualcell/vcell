package cbit.vcell.document;
import java.beans.PropertyVetoException;

import cbit.vcell.math.MathDescription;
import cbit.vcell.math.OutputFunctionContext;
import cbit.vcell.solver.Simulation;
/**
 * Insert the type's description here.
 * Creation date: (6/4/2004 1:56:12 AM)
 * @author: Ion Moraru
 */
public interface SimulationOwner extends GeometryOwner {
	Simulation addNewSimulation() throws PropertyVetoException;
	Simulation copySimulation(Simulation simulation) throws PropertyVetoException;
	Simulation[] getSimulations();
	void removeSimulation(Simulation simulation) throws PropertyVetoException;
	OutputFunctionContext getOutputFunctionContext();
	MathDescription getMathDescription();
	void refreshMathDescription();
	String getName();
}

