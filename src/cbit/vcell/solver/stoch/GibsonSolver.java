package cbit.vcell.solver.stoch;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.File;
import cbit.vcell.solver.*;
import cbit.vcell.server.*;

/**
 * Insert the type's description here.
 * Creation date: (7/13/2006 9:00:41 AM)
 * @author: Tracy LI
 */
public class GibsonSolver extends cbit.vcell.solvers.AbstractCompiledSolver {
	
public GibsonSolver(cbit.vcell.solver.SimulationJob simulationJob, java.io.File directory, cbit.vcell.server.SessionLog sessionLog) throws cbit.vcell.solver.SolverException {
	super(simulationJob, directory, sessionLog);
}


/**
 * Insert the method's description here.
 * Creation date: (7/13/2006 9:00:41 AM)
 */
public void cleanup() 
{
	try
	{
		printStochFile();
	}catch (Throwable e){
		e.printStackTrace(System.out);
		fireSolverAborted(e.getMessage());
	}
}


/**
 * show progress.
 * Creation date: (7/13/2006 9:00:41 AM)
 * @return cbit.vcell.solvers.ApplicationMessage
 * @param message java.lang.String
 */
protected cbit.vcell.solvers.ApplicationMessage getApplicationMessage(String message) {
	String SEPARATOR = ":";
	String PROGRESS_PREFIX = "progress:";
	if (message.startsWith(PROGRESS_PREFIX)){
		String progressString = message.substring(message.lastIndexOf(SEPARATOR)+1,message.indexOf("%"));
		double progress = Double.parseDouble(progressString)/100.0;
		double startTime = getSimulation().getSolverTaskDescription().getTimeBounds().getStartingTime();
		double endTime = getSimulation().getSolverTaskDescription().getTimeBounds().getEndingTime();
		setCurrentTime(startTime + (endTime-startTime)*progress);
		return new cbit.vcell.solvers.ApplicationMessage(cbit.vcell.solvers.ApplicationMessage.PROGRESS_MESSAGE,progress,-1,null,message);
	}else{
		throw new RuntimeException("unrecognized message");
	}
}


/**
 * Insert the method's description here.
 * Creation date: (8/15/2006 11:36:43 AM)
 * @return cbit.vcell.solver.stoch.StochSolverResultSet
 */
public StochSolverResultSet getStochSolverResultSet()
{
	//read .stoch file, this funciton here equals to getODESolverRestultSet()+getStateVariableResultSet()  in ODE.
	StochSolverResultSet stSolverResultSet = new StochSolverResultSet();

	FileInputStream inputStream = null;
	try {
		inputStream = new FileInputStream(getBaseName() + ".stoch");
		InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
		BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
		//  Read header
		String line = bufferedReader.readLine();
		if (line == null) 
		{
			//  throw exception
			System.out.println("There is no data in output file!");
		}
		while (line.indexOf(':') > 0) {
			String name = line.substring(0, line.indexOf(':'));
			stSolverResultSet.addDataColumn(new StochSolverResultSetColumnDescription(name));
			line = line.substring(line.indexOf(':') + 1);
		}
		//  Read data
		while ((line = bufferedReader.readLine()) != null) {
			line = line + "\t";
			double[] values = new double[stSolverResultSet.getDataColumnCount()];
			boolean bCompleteRow = true;
			for (int i = 0; i < stSolverResultSet.getDataColumnCount(); i++) {
				if (line.indexOf('\t')==-1){
					bCompleteRow = false;
					break;
				}else{
					String value = line.substring(0, line.indexOf('\t')).trim();
					values[i] = Double.valueOf(value).doubleValue();
					line = line.substring(line.indexOf('\t') + 1);
				}
			}
			if (bCompleteRow){
				stSolverResultSet.addRow(values);
			}else{
				break;
			}
		}
		//
	} catch (Exception e) {
		e.printStackTrace(System.out);
	} finally {
		try {
			if (inputStream != null) {
				inputStream.close();
			}
		} catch (Exception ex) {
			getSessionLog().exception(ex);
		}
	}
		
	return stSolverResultSet;
	
}


/**
 *  This method takes the place of the old runUnsteady()...
 */
protected void initialize() throws cbit.vcell.solver.SolverException 
{
	cbit.vcell.server.SessionLog sessionLog = getSessionLog();
	sessionLog.print("StochSolver.initialize()");
	fireSolverStarting("StochSolver initializing...");
	//
	String inputFilename = getBaseName() + ".stochInput";
	String ExeFilename = getBaseName() + System.getProperty(cbit.vcell.server.PropertyLoader.exesuffixProperty);
	//
	sessionLog.print("StochSolver.initialize() baseName = " + getBaseName());
	//
	cbit.vcell.solver.stoch.StochFileWriter stFileWriter = new cbit.vcell.solver.stoch.StochFileWriter(getSimulation());
	try {
		stFileWriter.initialize();
	} catch (Exception e) {
		setSolverStatus(new cbit.vcell.solver.SolverStatus(SolverStatus.SOLVER_ABORTED, "Could not initialize StochFileWriter..."));
		e.printStackTrace(System.out);
		throw new SolverException("autocode init exception: " + e.getMessage());
	}
	setSolverStatus(new SolverStatus(SolverStatus.SOLVER_RUNNING, "Generating input file..."));
	fireSolverStarting("generating input file...");
	//
	try {
		java.io.FileWriter fw = new java.io.FileWriter(inputFilename);
		java.io.PrintWriter pw = new java.io.PrintWriter(fw);
		
		try{
			stFileWriter.writeStochInputFile(pw);	
		}catch(java.lang.Exception e){e.printStackTrace();}
		pw.close();
	} catch (Exception e) {
		setSolverStatus(new SolverStatus(SolverStatus.SOLVER_ABORTED, "Could not generate input file: " + e.getMessage()));
		e.printStackTrace(System.out);
		throw new SolverException("solver input file exception: " + e.getMessage());
	}
	//
	//
	setSolverStatus(new SolverStatus(SolverStatus.SOLVER_RUNNING,"StochSolver starting"));	
	//get executable path+name.
	String executableName = cbit.vcell.server.PropertyLoader.getRequiredProperty(cbit.vcell.server.PropertyLoader.stochExecutableProperty);
	setMathExecutable(new cbit.vcell.solvers.MathExecutable(executableName + " gibson " + getBaseName() + ".stochInput" + " " + getBaseName() + ".stoch"));
}


/**
 * Insert the method's description here.
 * Creation date: (8/15/2006 9:44:06 AM)
 */
 //print result to binary file
private final void printStochFile() throws IOException
{
	// executable writes .stoch file, now we write things in .stochbi format
	StochSolverResultSet stSolverResultSet = ((GibsonSolver)this).getStochSolverResultSet();
	//if (getSimulation().getSolverTaskDescription().getOutputTimeSpec().isDefault()) {	
		//stSolverResultSet.trimRows(((DefaultOutputTimeSpec)getSimulation().getSolverTaskDescription().getOutputTimeSpec()).getKeepAtMost());
	//} ????this three sentences give array index out of bounds. at RowColumnResultSet row 540.
	StochSimData stSimData = new StochSimData(new VCSimulationDataIdentifier(getSimulation().getSimulationInfo().getAuthoritativeVCSimulationIdentifier(), getJobIndex()), stSolverResultSet);
	String mathName = stSimData.getMathName();
	getSessionLog().print("AbstractJavaSolver.printToFile(" + mathName + ")");
	File logFile = new File(getSaveDirectory(), mathName + LOGFILE_EXTENSION);
	File dataFile = new File(getSaveDirectory(), mathName + STOCH_DATA_EXTENSION);
	StochSimData.writeStochDataFile(stSimData, dataFile);
	stSimData.writeStochLogFile(logFile, dataFile);
	// fire event
	fireSolverPrinted(getCurrentTime());
}
}