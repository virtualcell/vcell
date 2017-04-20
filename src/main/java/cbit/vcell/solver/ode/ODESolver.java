/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.solver.ode;

import cbit.vcell.solver.server.Solver;

/**
 * Interface that must be implemented by all ODESolver classes.
 * Later we might want to include getMathOverrides().
 * Creation date: (8/19/2000 8:58:11 PM)
 * @author: John Wagner
 */
public interface ODESolver extends Solver {
	ODESolverResultSet getODESolverResultSet();
}
