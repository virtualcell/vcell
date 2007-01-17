package cbit.vcell.solver.stoch;
import cbit.vcell.server.*;
import cbit.vcell.math.*;
import java.util.*;

/**
 * Stochastic result data set. 
 * Creation date: (7/26/2006 5:29:20 PM)
 * @author: Tracy LI
 */

public class StochSolverResultSet extends cbit.vcell.util.RowColumnResultSet implements cbit.vcell.simdata.SimDataConstants, java.io.Serializable 
{
	public static final String TIME_COLUMN = ReservedVariable.TIME.getName();

/**
 * StochSolverResultSet constructor comment.
 */
public StochSolverResultSet() {
	super();
}
}