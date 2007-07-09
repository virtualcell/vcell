package cbit.vcell.solver.stoch;
import cbit.rmi.event.VCSimulationDataIdentifier;
import cbit.vcell.simdata.SimDataConstants;
import cbit.vcell.solvers.ApplicationMessage;
import cbit.vcell.solvers.SimulationJob;
import cbit.vcell.solvers.SolverException;
import cbit.vcell.solvers.SolverStatus;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.File;

import org.vcell.util.PropertyLoader;
import org.vcell.util.SessionLog;

/**
 * Insert the type's description here.
 * Creation date: (7/13/2006 9:00:41 AM)
 * @author: Tracy LI
 */
public class GibsonSolver extends cbit.vcell.solvers.AbstractCompiledSolver {
	private int saveToFileInterval = 6;	// seconds
	private long lastSavedMS = 0; // milliseconds since last save

public GibsonSolver(SimulationJob simulationJob, java.io.File directory, SessionLog sessionLog) throws SolverException {
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
public cbit.vcell.solvers.ApplicationMessage getApplicationMessage(String message) {
	String SEPARATOR = ":";
	String DATA_PREFIX = "data:";
	String PROGRESS_PREFIX = "progress:";
	if (message.startsWith(DATA_PREFIX)){
		double timepoint = Double.parseDouble(message.substring(message.lastIndexOf(SEPARATOR)+1));
		setCurrentTime(timepoint);
		return new ApplicationMessage(ApplicationMessage.DATA_MESSAGE,getProgress(),timepoint,null,message);
	}else if (message.startsWith(PROGRESS_PREFIX)){
		String progressString = message.substring(message.lastIndexOf(SEPARATOR)+1,message.indexOf("%"));
		double progress = Double.parseDouble(progressString)/100.0;
		//double startTime = getSimulation().getSolverTaskDescription().getTimeBounds().getStartingTime();
		//double endTime = getSimulation().getSolverTaskDescription().getTimeBounds().getEndingTime();
		//setCurrentTime(startTime + (endTime-startTime)*progress);
		return new ApplicationMessage(cbit.vcell.solvers.ApplicationMessage.PROGRESS_MESSAGE,progress,-1,null,message);
	}else{
		throw new RuntimeException("unrecognized message");
	}
}


/**
 * Insert the method's description here.
 * Creation date: (10/11/2006 11:24:18 AM)
 * @return int
 */
public int getSaveToFileInterval() {
	return saveToFileInterval;
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
public void initialize() throws SolverException 
{
	SessionLog sessionLog = getSessionLog();
	sessionLog.print("StochSolver.initialize()");
	fireSolverStarting("StochSolver initializing...");
	//
	String inputFilename = getBaseName() + ".stochInput";
	String ExeFilename = getBaseName() + System.getProperty(PropertyLoader.exesuffixProperty);
	//
	sessionLog.print("StochSolver.initialize() baseName = " + getBaseName());
	//
	cbit.vcell.solver.stoch.StochFileWriter stFileWriter = new StochFileWriter(getSimulation());
	try {
		stFileWriter.initialize();
	} catch (Exception e) {
		setSolverStatus(new SolverStatus(SolverStatus.SOLVER_ABORTED, "Could not initialize StochFileWriter..."));
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
	String executableName = PropertyLoader.getRequiredProperty(PropertyLoader.stochExecutableProperty);
	setMathExecutable(new cbit.vcell.solvers.MathExecutable(executableName + " gibson " + getBaseName() + ".stochInput" + " " + getBaseName() + ".stoch"));
}


/**
 * Write out the log file and result binary file.
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
	File logFile = new File(getSaveDirectory(), mathName + SimDataConstants.LOGFILE_EXTENSION);
	File dataFile = new File(getSaveDirectory(), mathName + SimDataConstants.STOCH_DATA_EXTENSION);
	StochSimData.writeStochDataFile(stSimData, dataFile);
	stSimData.writeStochLogFile(logFile, dataFile);
	// fire event
	fireSolverPrinted(getCurrentTime());
}


/**
 * Progressly print log and result binary files before finish running the whole simulation.
 * Used for displaying the progress and result on the way.
 * Creation date: (10/11/2006 11:19:43 AM)
 */
protected final void printToFile(double progress) throws IOException
{
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
			// in the middle of a run; only save at specified interval
			long currentTime = System.currentTimeMillis();
			shouldSave = currentTime - lastSavedMS > 1000 * getSaveToFileInterval();
		}
		if (shouldSave) {
			// write out Stoch file
			System.out.println("<<>><<>><<>><<>><<>>    printing at progress = "+progress);
			printStochFile();
			lastSavedMS = System.currentTimeMillis();
		}
	}
}


/**
 * Insert the method's description here.
 * Creation date: (10/11/2006 11:16:02 AM)
 */
public void propertyChange(java.beans.PropertyChangeEvent event)
{
	super.propertyChange(event);
	
	if (event.getSource() == getMathExecutable() && event.getPropertyName().equals("applicationMessage")) {
		String messageString = (String)event.getNewValue();
		if (messageString==null || messageString.length()==0){
			return;
		}
		ApplicationMessage appMessage = getApplicationMessage(messageString);
		if (appMessage!=null && appMessage.getMessageType() == ApplicationMessage.PROGRESS_MESSAGE) {
			try {
				printToFile(appMessage.getProgress());
			}catch (IOException e){
				e.printStackTrace(System.out);
			}
		}
	}
}
}