package cbit.vcell.solver.ode;
import java.io.File;
import java.io.IOException;

import cbit.rmi.event.VCSimulationDataIdentifier;
import cbit.util.SessionLog;
import cbit.vcell.simdata.ODESimData;
import cbit.vcell.simdata.ODESolverResultSet;
import cbit.vcell.simdata.SimDataConstants;
import cbit.vcell.simulation.DefaultOutputTimeSpec;
import cbit.vcell.solvers.SimulationJob;
import cbit.vcell.solvers.SolverException;
import cbit.vcell.solvers.SolverStatus;
/**
 * Some of this class' stuff could/should go into an AbstractSolver
 * base class...just have to wait until we finalize Solver's interface.
 * Creation date: (8/19/2000 8:55:19 PM)
 * @author: John Wagner
 */
public abstract class AbstractJavaSolver extends cbit.vcell.solvers.AbstractSolver {
	private boolean bUserStopRequest = false;
	private transient boolean fieldRunning = false;
	private transient Thread fieldThread = null;
	// how often should we save data to file during a long computation
	private int saveToFileInterval = 10; // seconds
	private int progressIncrement = 1; // percent
	private long lastSavedMS = 0; // milliseconds 'time' when last saved
	private double lastFiredProgress = 0.0;  // progress when last saved

/**
 * AbstractIntegrator constructor comment.
 */
public AbstractJavaSolver(SimulationJob simulationJob, File directory, SessionLog sessionLog) throws SolverException {
	super(simulationJob, directory, sessionLog);
}


public double calculateMachineEpsilon () {
	double epsilon = 1.0;
	while ((1.0 + epsilon) > 1.0) epsilon = 0.5*epsilon;
	return (2.0*epsilon);
}

/**
 * Insert the method's description here.
 * Creation date: (4/24/01 9:40:35 AM)
 * @exception cbit.vcell.solvers.SolverException The exception description.
 */
protected final void checkForUserStop() throws UserStopException {
	if (bUserStopRequest){
		bUserStopRequest = false; // DUH !!! so that we don't keep stopping if a previous run was stopped...
		throw new UserStopException("User initiated stop");
	}
}


/**
 * Insert the method's description here.
 * Creation date: (9/25/2002 1:06:11 PM)
 * @return int
 */
public int getProgressIncrement() {
	return progressIncrement;
}


/**
 * Insert the method's description here.
 * Creation date: (6/27/2001 12:22:10 PM)
 * @return int
 */
public int getSaveToFileInterval() {
	return saveToFileInterval;
}


/**
 *  This method takes the place of the old runUnsteady()...
 */
protected abstract void initialize() throws cbit.vcell.solvers.SolverException;


/**
 *  This method takes the place of the old runUnsteady()...
 */
protected abstract void integrate() throws SolverException, UserStopException, IOException;


/**
 * Insert the method's description here.
 * Creation date: (6/27/2001 12:17:36 PM)
 */
protected final void printToFile(double progress) throws IOException {

	long currentTime = System.currentTimeMillis();

	// decide whether to fire progress event
	boolean shouldFire = false;
	if (progress <= 0 || progress >= 1) {
		// always fire at begin and end
		shouldFire = true;
	} else {
		// only fire at specified increment
		shouldFire = (progress - lastFiredProgress) * 100 > getProgressIncrement();
	}
	if (shouldFire) {
		lastFiredProgress = progress;
		fireSolverProgress(progress);
	}
		
	// decide whether to save to file
	boolean shouldSave = false;
	// only if enabled
	if (isSaveEnabled()) {
		// check to see whether we need to save
		if (progress <= 0) {
			// a new run just got initialized; save 0 datapoint
			shouldSave = true;
		} else if (progress >= 1) {
			// a run finished; save last datapoint
			shouldSave = true;
		} else {
			// in the middle of a run; only save at specified time interval
			shouldSave = currentTime - lastSavedMS > 1000 * getSaveToFileInterval();
		}
		if (shouldSave) {
			// write file and fire event
			if (this instanceof ODESolver) {
				ODESolverResultSet odeSolverResultSet = ((ODESolver)this).getODESolverResultSet();
				if (getSimulation().getSolverTaskDescription().getOutputTimeSpec().isDefault()) {
					odeSolverResultSet.trimRows(((DefaultOutputTimeSpec)getSimulation().getSolverTaskDescription().getOutputTimeSpec()).getKeepAtMost());
				}
				ODESimData odeSimData = new ODESimData(new VCSimulationDataIdentifier(getSimulation().getSimulationInfo().getAuthoritativeVCSimulationIdentifier(), getJobIndex()), odeSolverResultSet);
				String mathName = odeSimData.getMathName();
				getSessionLog().print("AbstractJavaSolver.printToFile(" + mathName + ")");
				File logFile = new File(getBaseName() + SimDataConstants.LOGFILE_EXTENSION);
				File dataFile = new File(getBaseName() + SimDataConstants.ODE_DATA_EXTENSION);
				ODESimData.writeODEDataFile(odeSimData, dataFile);
				odeSimData.writeODELogFile(logFile, dataFile);
			}
			lastSavedMS = System.currentTimeMillis();
			fireSolverPrinted(getCurrentTime());
		}
	}
}


/**
 * Insert the method's description here.
 * Creation date: (6/26/2001 3:08:31 PM)
 */
public void runSolver() {
	try {
		fieldRunning = true;
		setSolverStatus(new SolverStatus(SolverStatus.SOLVER_STARTING, "initializing"));
		fireSolverStarting("initializing");
		initialize();
		setSolverStatus(new SolverStatus(SolverStatus.SOLVER_RUNNING, "starting"));
		fireSolverProgress(getProgress());
		integrate();
		setSolverStatus(new SolverStatus(SolverStatus.SOLVER_FINISHED, "finished"));
		fireSolverFinished();
	} catch (SolverException integratorException) {
		getSessionLog().exception(integratorException);
		setSolverStatus(new SolverStatus (SolverStatus.SOLVER_ABORTED, integratorException.getMessage()));
		fireSolverAborted(integratorException.getMessage());
	} catch (IOException ioException) {
		getSessionLog().exception(ioException);
		setSolverStatus(new SolverStatus (SolverStatus.SOLVER_ABORTED, ioException.getMessage()));
		fireSolverAborted(ioException.getMessage());
	} catch (UserStopException userStopException) {
		getSessionLog().exception(userStopException);
		setSolverStatus(new SolverStatus (SolverStatus.SOLVER_STOPPED, userStopException.getMessage()));
		fireSolverStopped();
	} catch (Throwable throwable) {
		getSessionLog().alert("AbstractJavaSolver.runSolver() : Caught Throwable instead of SolverException -- THIS EXCEPTION SHOULD NOT HAPPEN!");
		getSessionLog().exception(throwable);
		setSolverStatus(new SolverStatus (SolverStatus.SOLVER_ABORTED, throwable.getMessage()));
		fireSolverAborted(throwable.getMessage());
	} finally {
		fieldRunning = false;
	}
}


/**
 * Insert the method's description here.
 * Creation date: (9/25/2002 1:06:11 PM)
 * @param newProgressIncrement int
 */
public void setProgressIncrement(int newProgressIncrement) {
	progressIncrement = newProgressIncrement;
}


/**
 * Insert the method's description here.
 * Creation date: (6/27/2001 12:22:10 PM)
 * @param newSaveToFileInterval int
 */
public void setSaveToFileInterval(int newSaveToFileInterval) {
	saveToFileInterval = newSaveToFileInterval;
}


public final void startSolver() {
	if (!fieldRunning) {
		fieldThread = new Thread() {
			public void run() {
				runSolver();
			}
		};
		fieldThread.setName("Java Solver (" + getClass().getName() + ")");
		fieldThread.start();
	}
}


/**
 */
public final void stopSolver() {
	if (fieldRunning) {
		fieldRunning = false;
		bUserStopRequest = true;
	}
}
}