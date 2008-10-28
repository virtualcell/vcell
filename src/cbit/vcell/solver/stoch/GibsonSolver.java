package cbit.vcell.solver.stoch;
import cbit.vcell.math.AnnotatedFunction;
import cbit.vcell.math.Function;
import cbit.vcell.math.MathException;
import cbit.vcell.math.Variable;
import cbit.vcell.parser.Expression;
import cbit.vcell.simdata.VariableType;
import cbit.vcell.solver.ode.ODESolverResultSetColumnDescription;
import cbit.vcell.solver.ode.StateVariable;
import cbit.vcell.solvers.ApplicationMessage;
import cbit.vcell.solvers.FunctionFileGenerator;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.File;
import java.io.PrintWriter;
import java.util.Vector;

import cbit.vcell.solver.*;

/**
 * Gibson solver 
 * Creation date: (7/13/2006 9:00:41 AM)
 * @author: Tracy LI
 */
public class GibsonSolver extends cbit.vcell.solvers.AbstractCompiledSolver {
	private int saveToFileInterval = 6;	// seconds
	private long lastSavedMS = 0; // milliseconds since last save

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
 * Get data columns and function columns to be ready for plot.
 * Creation date: (8/15/2006 11:36:43 AM)
 * @return cbit.vcell.solver.stoch.StochSolverResultSet
 */
public cbit.vcell.solver.ode.ODESolverResultSet getStochSolverResultSet()
{
	//read .stoch file, this funciton here equals to getODESolverRestultSet()+getStateVariableResultSet()  in ODE.
	cbit.vcell.solver.ode.ODESolverResultSet stSolverResultSet = new cbit.vcell.solver.ode.ODESolverResultSet();

	FileInputStream inputStream = null;
	try {
		inputStream = new FileInputStream(getBaseName() + IDA_DATA_EXTENSION);
		InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
		BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
		//  Read header
		String line = bufferedReader.readLine();
		if (line == null) 
		{
			//  throw exception
			System.out.println("There is no data in output file!");
			return null;
		}
		while (line.indexOf(':') > 0) {
			String name = line.substring(0, line.indexOf(':'));
			stSolverResultSet.addDataColumn(new ODESolverResultSetColumnDescription(name));
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
	
	/*
	Add appropriate Function columns to result set if the stochastic simulation is to display the trajectory.
	No function columns for the results of multiple stochastic trials
	*/
	if(getSimulation().getSolverTaskDescription().getStochOpt().getNumOfTrials() == 1)
	{
		cbit.vcell.math.Function functions[] = getSimulation().getFunctions();
		for (int i = 0; i < functions.length; i++){
			if (isFunctionSaved(functions[i])) 
			{
				cbit.vcell.parser.Expression exp1 = new cbit.vcell.parser.Expression(functions[i].getExpression());
				try {
					exp1 = getSimulation().substituteFunctions(exp1);
				} catch (cbit.vcell.math.MathException e) {
					e.printStackTrace(System.out);
					throw new RuntimeException("Substitute function failed on function "+functions[i].getName()+" "+e.getMessage());
				} catch (cbit.vcell.parser.ExpressionException e) {
					e.printStackTrace(System.out);
					throw new RuntimeException("Substitute function failed on function "+functions[i].getName()+" "+e.getMessage());
				}
				try {
					cbit.vcell.solver.ode.FunctionColumnDescription cd = new cbit.vcell.solver.ode.FunctionColumnDescription(exp1.flatten(),functions[i].getName(), null, functions[i].getName(), false);
					stSolverResultSet.addFunctionColumn(cd);
				}catch (cbit.vcell.parser.ExpressionException e){
					e.printStackTrace(System.out);
				}
			}
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
	writeFunctionsFile();
	writeLogFile();

	String inputFilename = getBaseName() + STOCHINPUT_DATA_EXTENSION;	
	String outputFileName = getBaseName() + IDA_DATA_EXTENSION;
	sessionLog.print("StochSolver.initialize() baseName = " + getBaseName());

	setSolverStatus(new SolverStatus(SolverStatus.SOLVER_RUNNING, "Generating input file..."));
	fireSolverStarting("generating input file...");

	PrintWriter pw = null;
	try {
		pw = new PrintWriter(inputFilename);
		StochFileWriter stFileWriter = new StochFileWriter(pw, getSimulation(), getJobIndex(), true);
		stFileWriter.write();
	} catch (Exception e) {
		setSolverStatus(new SolverStatus(SolverStatus.SOLVER_ABORTED, "Could not generate input file: " + e.getMessage()));
		e.printStackTrace(System.out);
		throw new SolverException("solver input file exception: " + e.getMessage());
	} finally {
		if (pw != null) {
			pw.close();	
		}
	}

	setSolverStatus(new SolverStatus(SolverStatus.SOLVER_RUNNING,"StochSolver starting"));	
	//get executable path+name.
	String executableName = cbit.vcell.server.PropertyLoader.getRequiredProperty(cbit.vcell.server.PropertyLoader.stochExecutableProperty);
	setMathExecutable(new cbit.vcell.solvers.MathExecutable(new String[] {executableName, "gibson", inputFilename, outputFileName}));	
	//setMathExecutable(new cbit.vcell.solvers.MathExecutable(executableName + " gibson " + getBaseName() + ".stochInput" + " " + getBaseName() + ".stoch"));
}


/**
 * Write out the log file and result binary file.
 * Creation date: (8/15/2006 9:44:06 AM)
 */
 //print result to binary file
private final void printStochFile() throws IOException
{
	// executable writes .stoch file, now we write things in .stochbi format
	cbit.vcell.solver.ode.ODESolverResultSet stSolverResultSet = ((GibsonSolver)this).getStochSolverResultSet();
	if (stSolverResultSet == null) {
		return;
	}
	
	//if (getSimulation().getSolverTaskDescription().getOutputTimeSpec().isDefault()) {	
		//stSolverResultSet.trimRows(((DefaultOutputTimeSpec)getSimulation().getSolverTaskDescription().getOutputTimeSpec()).getKeepAtMost());
	//} ????this three sentences give array index out of bounds. at RowColumnResultSet row 540.
//	cbit.vcell.solver.ode.ODESimData stSimData = new cbit.vcell.solver.ode.ODESimData(new VCSimulationDataIdentifier(getSimulation().getSimulationInfo().getAuthoritativeVCSimulationIdentifier(), getJobIndex()), stSolverResultSet);
//	String mathName = stSimData.getMathName();
//	getSessionLog().print("GibsonSolver.printToFile(" + mathName + ")");
//	File logFile = new File(getSaveDirectory(), mathName + LOGFILE_EXTENSION);
//	File dataFile = new File(getSaveDirectory(), mathName + STOCH_DATA_EXTENSION);
//	cbit.vcell.solver.ode.ODESimData.writeODEDataFile(stSimData, dataFile);
//	stSimData.writeODELogFile(logFile, dataFile);
	// fire event to inform that solver has data printed. however, for gibson multiple trial and hybrid solvers, we don't show intermediate results
	if(getSimulation().getSolverTaskDescription().getStochOpt().getNumOfTrials() == 1)
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

private void writeLogFile() throws SolverException {
	String logFile = getBaseName() + LOGFILE_EXTENSION;
	String ideDataFileName = new File(getBaseName() + IDA_DATA_EXTENSION).getName();
	PrintWriter pw = null;
	try {
		pw = new PrintWriter(logFile);
		pw.println(IDA_DATA_IDENTIFIER);
		pw.println(IDA_DATA_FORMAT_ID);
		pw.println(ideDataFileName);
		pw.close();
	} catch (FileNotFoundException e) {
		e.printStackTrace();
		throw new SolverException(e.getMessage());
	} finally {
		if (pw != null) {
			pw.close();
		}
	}
}

private void writeFunctionsFile() {
	// ** Dumping the functions of a simulation into a '.functions' file.
	String functionFileName = getBaseName() + ".functions";
	Vector<AnnotatedFunction> funcList = createFunctionList();
	
	//Try to save existing user defined functions
	FunctionFileGenerator functionFileGenerator = new FunctionFileGenerator(functionFileName, funcList.toArray(new AnnotatedFunction[0]));

	try {
		functionFileGenerator.generateFunctionFile();		
	}catch (Exception e){
		e.printStackTrace(System.out);
		throw new RuntimeException("Error creating .function file for "+functionFileGenerator.getBasefileName()+e.getMessage());
	}		
}

private Vector<AnnotatedFunction> createFunctionList() {
	//
	// add appropriate Function columns to result set
	//
	Vector<AnnotatedFunction> funcList = new Vector<AnnotatedFunction>();
	
	cbit.vcell.math.Function functions[] = getSimulation().getFunctions();
	for (int i = 0; i < functions.length; i++){
		if (isFunctionSaved(functions[i])){
			Expression exp1 = new Expression(functions[i].getExpression());
			try {
				exp1 = getSimulation().substituteFunctions(exp1).flatten();
			} catch (cbit.vcell.math.MathException e) {
				e.printStackTrace(System.out);
				throw new RuntimeException("Substitute function failed on function "+functions[i].getName()+" "+e.getMessage());
			} catch (cbit.vcell.parser.ExpressionException e) {
				e.printStackTrace(System.out);
				throw new RuntimeException("Substitute function failed on function "+functions[i].getName()+" "+e.getMessage());
			}
			
			AnnotatedFunction af = new AnnotatedFunction(functions[i].getName(), exp1, "", VariableType.NONSPATIAL, false);
			funcList.add(af);
		}
	}
	return funcList;
}

}