package cbit.vcell.solver.ode;
import cbit.vcell.math.*;
import cbit.vcell.math.Variable;
import cbit.vcell.math.Constant;
import cbit.vcell.solvers.*;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.parser.SymbolTable;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.simdata.DataSetIdentifier;
import cbit.vcell.parser.Expression;
import cbit.vcell.math.Function;
import java.util.*;
import java.io.*;
import cbit.vcell.math.MathDescription;
import cbit.vcell.server.SessionLog;
import cbit.vcell.server.PropertyLoader;
import cbit.vcell.solver.*;
/**
 * Insert the type's description here.
 * Creation date: (3/9/2001 3:04:39 PM)
 * @author: John Wagner
 */
public abstract class IDASolver extends cbit.vcell.solvers.AbstractCompiledSolver implements ODESolver {
	private int saveToFileInterval = 6;	// seconds
	private long lastSavedMS = 0; // milliseconds since last save

/**
 * IDASolver constructor comment.
 * @param sessionLog cbit.vcell.server.SessionLog
 * @param simulation cbit.vcell.solver.Simulation
 */
public IDASolver(cbit.vcell.solver.SimulationJob simulationJob, File directory, cbit.vcell.server.SessionLog sessionLog) throws SolverException {
	super(simulationJob, directory, sessionLog);
	if (getSimulation().getIsSpatial()) {
		throw new SolverException("Cannot use IDASolver on spatial simulation");
	}
	if (getSimulation().getMathDescription().hasFastSystems()) {
		throw new SolverException("Cannot use IDASolver with Fast Systems (pseudo-steady approximations)");
	}
}


/**
 * Insert the method's description here.
 * Creation date: (12/9/2002 4:54:11 PM)
 */
public void cleanup() {
	try {
		printODEFile();
	}catch (Throwable e){
		e.printStackTrace(System.out);
		fireSolverAborted(e.getMessage());
	}
}


/*
	This method was created in Visual Age
*/

private StateVariable[] createStateVariables() throws cbit.vcell.math.MathException, cbit.vcell.parser.ExpressionException {
	Vector stateVariables = new Vector();
	// get Ode's from MathDescription and create ODEStateVariables
	Enumeration enum1 = ((SubDomain)getSimulation().getMathDescription().getSubDomains().nextElement()).getEquations();
	while (enum1.hasMoreElements()) {
		Equation equation = (Equation) enum1.nextElement();
		if (equation instanceof OdeEquation) {
			stateVariables.addElement(new ODEStateVariable((OdeEquation) equation, getSimulation()));
		} else {
			throw new MathException("encountered non-ode equation, unsupported");
		}
	}

	//  Get sensitivity variables
	Variable variables[] = getSimulation().getVariables(); 
	Vector sensVariables = new Vector();
	if (getSensitivityParameter() != null) {
		for (int i = 0; i < variables.length; i++){
			if (variables[i] instanceof VolVariable){
				VolVariable volVariable = (VolVariable)variables[i];
				SensVariable sv = new SensVariable(volVariable, getSensitivityParameter());
				sensVariables.addElement(sv);
			}
		}
	}
	
	// get Jacobian and RateSensitivities from MathDescription and create SensStateVariables
	for (int v = 0; v < sensVariables.size(); v++) {
		stateVariables.addElement(
			new SensStateVariable((SensVariable) sensVariables.elementAt(v),
									getSimulation().getMathDescription().getRateSensitivity(), 
									getSimulation().getMathDescription().getJacobian(),
									sensVariables, 
									getSimulation()));
	}
	if (stateVariables.size() == 0) {
		throw new MathException("there are no equations defined");
	}

	StateVariable stateVars[] = (StateVariable[])cbit.util.BeanUtils.getArray(stateVariables,StateVariable.class);
	return(stateVars);
}


/**
 * Insert the method's description here.
 * Creation date: (6/27/01 3:25:11 PM)
 * @return cbit.vcell.solvers.ApplicationMessage
 * @param message java.lang.String
 */
protected ApplicationMessage getApplicationMessage(String message) {
	//
	// "progress:xx.x%"        .... sent every 1% for IDASolver
	//
	//
	String SEPARATOR = ":";
	String PROGRESS_PREFIX = "progress:";
	if (message.startsWith(PROGRESS_PREFIX)){
		String progressString = message.substring(message.lastIndexOf(SEPARATOR)+1,message.indexOf("%"));
		double progress = Double.parseDouble(progressString)/100.0;
		double startTime = getSimulation().getSolverTaskDescription().getTimeBounds().getStartingTime();
		double endTime = getSimulation().getSolverTaskDescription().getTimeBounds().getEndingTime();
		setCurrentTime(startTime + (endTime-startTime)*progress);
		return new ApplicationMessage(ApplicationMessage.PROGRESS_MESSAGE,progress,-1,null,message);
	}else{
		throw new RuntimeException("unrecognized message");
	}
}


/**
 * This method was created in VisualAge.
 * @return double[]
 * @param vectorIndex int
 */
public ODESolverResultSet getODESolverResultSet()  {
	//
	// read .ida file
	//
	ODESolverResultSet odeSolverResultSet = getStateVariableResultSet();

	//
	// add appropriate Function columns to result set
	//
	cbit.vcell.math.Function functions[] = getSimulation().getFunctions();
	for (int i = 0; i < functions.length; i++){
		if (isFunctionSaved(functions[i])){
			Expression exp1 = new Expression(functions[i].getExpression());
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
				FunctionColumnDescription cd = new FunctionColumnDescription(exp1.flatten(),functions[i].getName(), null, functions[i].getName(), false);
				odeSolverResultSet.addFunctionColumn(cd);
			}catch (cbit.vcell.parser.ExpressionException e){
				e.printStackTrace(System.out);
			}
		}
	}
	//
	// add dependent sensitivity function columns to new result set
	//

	if (getSensitivityParameter() != null) {
		try {
			if (odeSolverResultSet.findColumn(getSensitivityParameter().getName()) == -1) {
				FunctionColumnDescription fcd = new FunctionColumnDescription(new Expression(getSensitivityParameter().getConstantValue()), getSensitivityParameter().getName(), null, getSensitivityParameter().getName(), false);
				odeSolverResultSet.addFunctionColumn(fcd);
			}
			Variable variables[] = getSimulation().getVariables();
			StateVariable stateVars[] = createStateVariables();
						
			for (int i = 0; i < variables.length; i++){
				if (variables[i] instanceof Function && isFunctionSaved((Function)variables[i])){
					Function depSensFunction = (Function)variables[i];
					Expression depSensFnExpr = new Expression(depSensFunction.getExpression());
					depSensFnExpr = getSimulation().substituteFunctions(depSensFnExpr);
					
					depSensFnExpr = getFunctionSensitivity(depSensFnExpr, getSensitivityParameter(), stateVars);
					// depSensFnExpr = depSensFnExpr.flatten(); 	// already bound and flattened in getFunctionSensitivity, no need here.....
					
					String depSensFnName = new String("sens_"+depSensFunction.getName()+"_wrt_"+getSensitivityParameter().getName());
					
					if (depSensFunction != null) {
						FunctionColumnDescription cd = new FunctionColumnDescription(depSensFnExpr.flatten(),depSensFnName,getSensitivityParameter().getName(),depSensFnName, false);
						odeSolverResultSet.addFunctionColumn(cd);
					}
				}
			}
		} catch (MathException e) {
			e.printStackTrace(System.out);
			throw new RuntimeException("Error adding function to resultSet: "+e.getMessage());
		} catch (cbit.vcell.parser.ExpressionException e) {
			e.printStackTrace(System.out);
			throw new RuntimeException("Error adding function to resultSet: "+e.getMessage());
		}
	} 

	return odeSolverResultSet;
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
 * Insert the method's description here.
 * Creation date: (6/27/2001 12:22:10 PM)
 * @return int
 */
public Constant getSensitivityParameter() {
	Constant origSensParam = getSimulation().getSolverTaskDescription().getSensitivityParameter();
	//
	// sensitivity parameter from solverTaskDescription will have the non-overridden nominal value.
	// ask the Simulation for the updated Constant object (with the proper overridden value).
	//
	if (origSensParam!=null){
		return (Constant)getSimulation().getVariable(origSensParam.getName());
	}else{
		return null;
	}
}


/**
 * This method was created in VisualAge.
 * @return double[]
 * @param vectorIndex int
 */
private ODESolverResultSet getStateVariableResultSet() {
	ODESolverResultSet odeSolverResultSet = new ODESolverResultSet();
	FileInputStream inputStream = null;
	try {
		inputStream = new FileInputStream(getBaseName() + ".ida");
		InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
		BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
		//  Read header
		String line = bufferedReader.readLine();
		if (line == null) {
			//  throw exception
		}
		while (line.indexOf(':') > 0) {
			String name = line.substring(0, line.indexOf(':'));
			odeSolverResultSet.addDataColumn(new ODESolverResultSetColumnDescription(name));
			line = line.substring(line.indexOf(':') + 1);
		}
		//  Read data
		while ((line = bufferedReader.readLine()) != null) {
			line = line + "\t";
			double[] values = new double[odeSolverResultSet.getDataColumnCount()];
			boolean bCompleteRow = true;
			for (int i = 0; i < odeSolverResultSet.getDataColumnCount(); i++) {
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
				odeSolverResultSet.addRow(values);
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
	return (odeSolverResultSet);
}


/**
 * Insert the method's description here.
 * Creation date: (6/27/2001 12:17:36 PM)
 */
private final void printODEFile() throws IOException {
	// executable writes .ida file, now we write things in .ode format
	ODESolverResultSet odeSolverResultSet = ((ODESolver)this).getODESolverResultSet();
	if (getSimulation().getSolverTaskDescription().getOutputTimeSpec().isDefault()) {	
		odeSolverResultSet.trimRows(((DefaultOutputTimeSpec)getSimulation().getSolverTaskDescription().getOutputTimeSpec()).getKeepAtMost());
	}
	ODESimData odeSimData = new ODESimData(new VCSimulationDataIdentifier(getSimulation().getSimulationInfo().getAuthoritativeVCSimulationIdentifier(), getJobIndex()), odeSolverResultSet);
	String mathName = odeSimData.getMathName();
	getSessionLog().print("AbstractJavaSolver.printToFile(" + mathName + ")");
	File logFile = new File(getSaveDirectory(), mathName + LOGFILE_EXTENSION);
	File dataFile = new File(getSaveDirectory(), mathName + ODE_DATA_EXTENSION);
	ODESimData.writeODEDataFile(odeSimData, dataFile);
	odeSimData.writeODELogFile(logFile, dataFile);
	// fire event
	fireSolverPrinted(getCurrentTime());
}


/**
 * Insert the method's description here.
 * Creation date: (6/27/2001 12:17:36 PM)
 */
protected final void printToFile(double progress) throws IOException {
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
			// write out ODE file
			System.out.println("<<>><<>><<>><<>><<>>    printing at progress = "+progress);
			printODEFile();
			lastSavedMS = System.currentTimeMillis();
		}
	}
}


/**
 * Insert the method's description here.
 * Creation date: (6/27/2001 2:33:03 PM)
 */
public void propertyChange(java.beans.PropertyChangeEvent event) {
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


/**
 * Insert the method's description here.
 * Creation date: (6/27/2001 12:22:10 PM)
 * @param newSaveToFileInterval int
 */
public void setSaveToFileInterval(int newSaveToFileInterval) {
	saveToFileInterval = newSaveToFileInterval;
}
}