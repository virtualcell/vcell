package cbit.vcell.solvers;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;

import org.vcell.util.SessionLog;

import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.resource.ResourceUtil;
import cbit.vcell.solver.SolverException;

/**
 * base class for straightforward (simple call, non parallel) solvers
 * @author gweatherby
 *
 */
public abstract class SimpleCompiledSolver extends AbstractCompiledSolver {


	public SimpleCompiledSolver(SimulationTask simTask, File directory,
			SessionLog sessionLog, boolean bMsging) throws SolverException {
		super(simTask, directory, sessionLog, bMsging);
	}

	/**
	 * implement via {@link #getMathExecutableCommand()}
	 */
	@Override
	public Collection<ExecutableCommand> getCommands( ) {
		return Arrays.asList(new ExecutableCommand(new ExecutableCommand.LibraryPath(ResourceUtil.getLocalSolversDirectory().getAbsolutePath()), 
							getMathExecutableCommand()));
	}

	protected abstract String[] getMathExecutableCommand();

}
