package cbit.vcell.solver.ode;

import cbit.vcell.solver.Solver;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * Interface that must be implemented by all ODESolver classes.
 * Later we might want to include getMathOverrides().
 * Creation date: (8/19/2000 8:58:11 PM)
 * @author: John Wagner
 */
public interface ODESolver extends Solver {
	ODESolverResultSet getODESolverResultSet();
}
