/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.solver.stoch;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.vcell.util.PropertyLoader;
import org.vcell.util.SessionLog;

import ucar.ma2.ArrayDouble;
import cbit.vcell.math.Function;
import cbit.vcell.math.FunctionColumnDescription;
import cbit.vcell.math.MathException;
import cbit.vcell.math.ODESolverResultSetColumnDescription;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.NonspatialStochHybridOptions;
import cbit.vcell.solver.NonspatialStochSimOptions;
import cbit.vcell.solver.SimulationSymbolTable;
import cbit.vcell.solver.SolverDescription;
import cbit.vcell.solver.SolverException;
import cbit.vcell.solver.SolverTaskDescription;
import cbit.vcell.solver.ode.ODESolverResultSet;
import cbit.vcell.solver.server.SimulationMessage;
import cbit.vcell.solver.server.SolverStatus;
import cbit.vcell.solvers.ApplicationMessage;
import cbit.vcell.solvers.MathExecutable;
import cbit.vcell.solvers.SimpleCompiledSolver;
/**
 * The HybridSolver is used to solve stochastic stiff problem.
 * Two types of solvers are provided in this class, which are fixed time step methods (Gibson_Euler
 * and gibson_Milstein) and adaptive time step Method(Adaptive gibson_Milstein). Those methods are proposed by Howard Salis.
 * The basic idea is to separate the whole system into fast and slow sub-systems.
 * Chemical Langvine Equation is used  to solve fast sub-system, and SSA is used to solve
 * slow sub-systme.
 *
 * @author Tracy LI
 * Created in June 2007.
 * @version 1.0
 */
public class HybridSolver extends SimpleCompiledSolver {
	public static final int EMIntegrator = 1;
	public static final int MilsteinIntegrator = 2;
	public static final int AdaptiveMilsteinIntegrator = 3;
	private int saveToFileInterval = 6;	// seconds
	private long lastSavedMS = 0; // milliseconds since last save
	private int integratorType = EMIntegrator;


public HybridSolver(SimulationTask simTask, java.io.File directory, SessionLog sessionLog, int type, boolean b_Msging) throws cbit.vcell.solver.SolverException {
	super(simTask, directory, sessionLog, b_Msging);
	integratorType = type;
}


/**
 * Insert the method's description here.
 * Creation date: (7/13/2006 9:00:41 AM)
 */
public void cleanup() 
{
	if (getMathExecutable()==null || !getMathExecutable().getStatus().equals(org.vcell.util.ExecutableStatus.COMPLETE)) {
		return;
	}
	try
	{
		printStochFile();
	}catch (Throwable e){
		e.printStackTrace(System.out);
		fireSolverAborted(SimulationMessage.solverAborted(e.getMessage()));
	}
}


/**
 * show progress.
 * Creation date: (7/13/2006 9:00:41 AM)
 * @return cbit.vcell.solvers.ApplicationMessage
 * @param message java.lang.String
 */
protected ApplicationMessage getApplicationMessage(String message) {
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
		return new ApplicationMessage(ApplicationMessage.PROGRESS_MESSAGE,progress,-1,null,message);
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
private ODESolverResultSet getHybridSolverResultSet()
{
	//read .stoch file, this funciton here equals to getODESolverRestultSet()+getStateVariableResultSet()  in ODE.
	ODESolverResultSet stSolverResultSet = new ODESolverResultSet();

	try{
		String filename = getBaseName() + NETCDF_DATA_EXTENSION;
		NetCDFEvaluator ncEva = new NetCDFEvaluator();
		NetCDFReader ncReader = null;
		try
		{
			ncEva.setNetCDFTarget(filename);
			ncReader = ncEva.getNetCDFReader();
		}catch (Exception e) {
			e.printStackTrace(System.err);
			throw new RuntimeException("Cannot open simulation result file: "+ filename +"!");
		}
		
		//  Read result according to trial number
		if(ncReader.getNumTrials() == 1)
		{
			//Read header
			String[] varNames = ncReader.getSpeciesNames_val();
			//first column will be time t.
			stSolverResultSet.addDataColumn(new ODESolverResultSetColumnDescription("t"));
			//following columns are stoch variables
			for(int i=0; i< varNames.length; i++)
			{
				stSolverResultSet.addDataColumn(new ODESolverResultSetColumnDescription(varNames[i]));
			}
			
			//Read data
			ArrayDouble data = (ArrayDouble)ncEva.getTimeSeriesData(1);//data only, no time points
			double timePoints[] = ncReader.getTimePoints();
			System.out.println("time points length is "+timePoints.length);
			//shape[0]:num of timepoints, shape[1]: num of species
			int[] shape = data.getShape();
            
			if(shape.length == 1) //one species
			{
				ArrayDouble.D1 temData = (ArrayDouble.D1)data;
				System.out.println("one species in time series data and size is "+temData.getSize());
				for(int k=0; k<timePoints.length; k++)//rows
				{
					double[] values = new double[stSolverResultSet.getDataColumnCount()];
					values[0]=timePoints[k];
					for(int i=1; i< stSolverResultSet.getDataColumnCount(); i++)
					{
						values[i]=temData.get(k);
					}
					stSolverResultSet.addRow(values);
				}
			}
			
			if(shape.length == 2) //more than one species
			{
				ArrayDouble.D2 temData = (ArrayDouble.D2)data;
				System.out.println("multiple species in time series, the length of time series is :"+data.getShape()[0]+", and the total number of speceis is: "+data.getShape()[1]);
				for(int k=0; k<timePoints.length; k++)//rows
				{
					double[] values = new double[stSolverResultSet.getDataColumnCount()];
					values[0]=timePoints[k];
					for(int i=1; i<stSolverResultSet.getDataColumnCount(); i++)
					{
						values[i]=temData.get(k, i-1);
					}
					stSolverResultSet.addRow(values);
				}
			}
		}
		else if(ncReader.getNumTrials() > 1)
		{
			//Read header
			String[] varNames = ncReader.getSpeciesNames_val();
			//first column will be time t.
			stSolverResultSet.addDataColumn(new ODESolverResultSetColumnDescription("TrialNo"));
			//following columns are stoch variables
			for(int i=0; i< varNames.length; i++)
			{
				stSolverResultSet.addDataColumn(new ODESolverResultSetColumnDescription(varNames[i]));
			}
			
			//Read data
			ArrayDouble data = (ArrayDouble)ncEva.getDataOverTrials(ncReader.getTimePoints().length-1);//data only, no trial numbers
			int trialNum[] = ncEva.getNetCDFReader().getTrialNumbers();
			//System.out.println("total trials are "+trialNum.length);
			//shape[0]:number of trials, shape[1]: num of species
			int[] shape = data.getShape();
            
			if(shape.length == 1) //one species
			{
				ArrayDouble.D1 temData = (ArrayDouble.D1)data;
				//System.out.println("one species over trials, size is: "+temData.getSize());
				for(int k=0; k<trialNum.length; k++)//rows
				{
					double[] values = new double[stSolverResultSet.getDataColumnCount()];
					values[0]=trialNum[k];
					for(int i=1; i<stSolverResultSet.getDataColumnCount(); i++)
					{
						values[i]=temData.get(k);
					}
					stSolverResultSet.addRow(values);
				}
			}
			
			if(shape.length == 2) //more than one species
			{
				ArrayDouble.D2 temData = (ArrayDouble.D2)data;
				//System.out.println("multiple species in multiple trials, the length of trials is :"+data.getShape()[0]+", and the total number of speceis is: "+data.getShape()[1]);
				for(int k=0; k<trialNum.length; k++)//rows
				{
					double[] values = new double[stSolverResultSet.getDataColumnCount()];
					values[0]=trialNum[k];
					for(int i=1; i<stSolverResultSet.getDataColumnCount(); i++)
					{
						values[i]=temData.get(k, i-1);
					}
					stSolverResultSet.addRow(values);
				}
			}
		}
		else
		{
			throw new RuntimeException("Number of trials should be a countable positive value, from 1 to N.");
		}
		
	} catch (Exception e) {
		e.printStackTrace(System.err);
		throw new RuntimeException("Problem encountered in parsing hybrid simulation results.\n"+e.getMessage());
	} 
	
	/*
	 *Add appropriate Function columns to result set if the stochastic simulation is to display the trajectory.
	 *No function columns for the results of multiple stochastic trials.
	 *In stochastic simulation the functions include probability functions and clamped variable.
	 */
	SimulationSymbolTable simSymbolTable = simTask.getSimulationJob().getSimulationSymbolTable();
	if(simSymbolTable.getSimulation().getSolverTaskDescription().getStochOpt().getNumOfTrials() == 1)
	{
		Function functions[] = simSymbolTable.getFunctions();
		for (int i = 0; i < functions.length; i++){
			if (SimulationSymbolTable.isFunctionSaved(functions[i])) 
			{
				Expression exp1 = new Expression(functions[i].getExpression());
				try {
					exp1 = simSymbolTable.substituteFunctions(exp1);
				} catch (MathException e) {
					e.printStackTrace(System.out);
					throw new RuntimeException("Substitute function failed on function "+functions[i].getName()+" "+e.getMessage());
				} catch (ExpressionException e) {
					e.printStackTrace(System.out);
					throw new RuntimeException("Substitute function failed on function "+functions[i].getName()+" "+e.getMessage());
				}
				try {
					FunctionColumnDescription cd = new FunctionColumnDescription(exp1.flatten(),functions[i].getName(), null, functions[i].getName(), false);
					stSolverResultSet.addFunctionColumn(cd);
				}catch (ExpressionException e){
					e.printStackTrace(System.out);
				}
			}
		}
	}	
	return stSolverResultSet;
	
}

private void writeLogFile() throws SolverException {
	String logFile = getBaseName() + LOGFILE_EXTENSION;
	String netCDFFileName = new File(getBaseName() + NETCDF_DATA_EXTENSION).getName();
	PrintWriter pw = null;
	try {
		pw = new PrintWriter(logFile);
		pw.println(NETCDF_DATA_IDENTIFIER);
		pw.println(NETCDF_DATA_FORMAT_ID);
		pw.println(netCDFFileName);
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


/**
 *  This method takes the place of the old runUnsteady()...
 */
protected void initialize() throws SolverException 
{
	SessionLog sessionLog = getSessionLog();
	sessionLog.print("HybridSolver.initialize()");
	fireSolverStarting(SimulationMessage.MESSAGE_SOLVEREVENT_STARTING_INIT);
	writeFunctionsFile();
	writeLogFile();
	
	//
	String inputFilename = getInputFilename();//file used by precompiled solver.
	//
	sessionLog.print("HybridSolver.initialize() baseName = " + getBaseName());
	//
	NetCDFWriter ncWriter = new NetCDFWriter(simTask,inputFilename, bMessaging);
	try {
		ncWriter.initialize();
	} catch (Exception e) {
		setSolverStatus(new cbit.vcell.solver.server.SolverStatus(SolverStatus.SOLVER_ABORTED, SimulationMessage.solverAborted("Could not initialize StochFileWriter...")));
		e.printStackTrace(System.out);
		throw new SolverException("autocode init exception: " + e.getMessage());
	}
	setSolverStatus(new SolverStatus(SolverStatus.SOLVER_RUNNING, SimulationMessage.MESSAGE_SOLVER_RUNNING_INPUT_FILE));
	fireSolverStarting(SimulationMessage.MESSAGE_SOLVEREVENT_STARTING_INPUT_FILE);
	//
	try{
		ncWriter.writeHybridInputFile();
	}catch (Exception e){
		setSolverStatus(new SolverStatus(SolverStatus.SOLVER_ABORTED, SimulationMessage.solverAborted("Could not generate input file: " + e.getMessage())));
		e.printStackTrace(System.err);
		throw new SolverException("solver input file exception: " + e.getMessage());
	}
	//
	//
	setSolverStatus(new SolverStatus(SolverStatus.SOLVER_RUNNING,SimulationMessage.MESSAGE_SOLVER_RUNNING_START));	
	//get executable path+name.
	//Hybrid solver's usage: ProgramName <NetCDF Filename> <epsilon> <lambda> <MSR_Tolerance> <SDE_Tolerance> <SDE_dt> [-R <Random Seed>] [-OV]
	setMathExecutable(new MathExecutable(getMathExecutableCommand()));
}

private String getInputFilename(){
	return getBaseName() + ".nc";//file used by precompiled solver.
}

@Override
protected String[] getMathExecutableCommand() {
	String executableName = "";
	String randomNumber = "";
	//if one of the following paras is applied, all the paras in front of it must be set.
	String epsilon = " 100";
	String lambda = " 10";
	String MSR_Tolerance = " 0.01";
	String SDE_Tolerance = " 1e-4";
	String SDE_dt = " 0.1";
	String paraString = "";

	SolverTaskDescription solverTaskDescription = simTask.getSimulation().getSolverTaskDescription();
	NonspatialStochSimOptions stochOpts = solverTaskDescription.getStochOpt(); 
	NonspatialStochHybridOptions hybridOpts = solverTaskDescription.getStochHybridOpt();
	if (hybridOpts==null){
		throw new RuntimeException("expecting StochHybridOptions for solver type "+solverTaskDescription.getSolverDescription().getDisplayLabel());
	}
	epsilon = " "+String.valueOf(hybridOpts.getEpsilon());
    lambda = " "+String.valueOf(hybridOpts.getLambda());
    MSR_Tolerance = " "+String.valueOf(hybridOpts.getMSRTolerance());
    if(solverTaskDescription.getSolverDescription().equals(SolverDescription.HybridMilAdaptive))
    	SDE_Tolerance = " "+String.valueOf(hybridOpts.getSDETolerance());
    else SDE_Tolerance = "";
    SDE_dt = " "+String.valueOf(solverTaskDescription.getTimeStep().getDefaultTimeStep());
    paraString = epsilon + lambda + MSR_Tolerance + SDE_Tolerance + SDE_dt;
    if(stochOpts.isUseCustomSeed())
    	randomNumber = " -R "+String.valueOf(stochOpts.getCustomSeed());
	
	if(getIntegratorType() == HybridSolver.EMIntegrator)
	{
		executableName = PropertyLoader.getRequiredProperty(org.vcell.util.PropertyLoader.hybridEMExecutableProperty);
	}
	else if (getIntegratorType() == HybridSolver.MilsteinIntegrator)
	{
		executableName = PropertyLoader.getRequiredProperty(org.vcell.util.PropertyLoader.hybridMilExecutableProperty);
	}
	else 
	{
		executableName = PropertyLoader.getRequiredProperty(org.vcell.util.PropertyLoader.hybridMilAdaptiveExecutableProperty);
	}
	
	ArrayList<String> commandList = new ArrayList<String>();
	commandList.add(executableName);
	commandList.add(getInputFilename());
	String argumentString = paraString.toLowerCase() +randomNumber + " -OV";
	StringTokenizer st = new StringTokenizer(argumentString);
	while (st.hasMoreTokens()) {
		commandList.add(st.nextToken());
	}	
	return commandList.toArray(new String[0]);
}


/**
 * Write out the log file and result binary file.
 * Creation date: (8/15/2006 9:44:06 AM)
 */
 //print result to binary file
private final void printStochFile() throws IOException
{
	// executable writes .stoch file, now we write things in .stochbi format
//	cbit.vcell.solver.ode.ODESolverResultSet stSolverResultSet = ((HybridSolver)this).getHybridSolverResultSet();
//	if (stSolverResultSet == null) {
//		return;
//	}
	
	//	cbit.vcell.solver.ode.ODESimData stSimData = new cbit.vcell.solver.ode.ODESimData(new VCSimulationDataIdentifier(getSimulation().getSimulationInfo().getAuthoritativeVCSimulationIdentifier(), getJobIndex()), stSolverResultSet);
//	String mathName = stSimData.getMathName();
//	getSessionLog().print("AbstractJavaSolver.printToFile(" + mathName + ")");
//	File logFile = new File(getSaveDirectory(), mathName + LOGFILE_EXTENSION);
//	File dataFile = new File(getSaveDirectory(), mathName + STOCH_DATA_EXTENSION);
//	cbit.vcell.solver.ode.ODESimData.writeODEDataFile(stSimData, dataFile);
//	stSimData.writeODELogFile(logFile, dataFile);
	
	// we don't show intermediate data for hybrid solvers. so, event shouldn't be fired.
	//fireSolverPrinted(getCurrentTime());
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

public int getIntegratorType() {
	return integratorType;
}

public void setIntegratorType(int integratorType) {
	this.integratorType = integratorType;
}

/**
 * for testing
 * @param args
 */
public static void main(String[] args) {
	try{
	HybridSolver hs = new HybridSolver(null,null,null,HybridSolver.EMIntegrator, false);
	hs.getHybridSolverResultSet(); //put file name to be open in getHybridSolverResultSet()
	}catch(Exception e){
		e.printStackTrace(System.err);
	}
	
}

}
