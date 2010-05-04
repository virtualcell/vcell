package cbit.vcell.simdata;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/

import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationJob;
import cbit.vcell.solver.SimulationMessage;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationDataIdentifierOldStyle;
import cbit.vcell.solver.ode.ODESimData;
import cbit.vcell.client.data.OutputContext;
import cbit.vcell.field.FieldDataIdentifierSpec;
import cbit.vcell.field.FieldFunctionArguments;
import cbit.vcell.field.SimResampleInfoProvider;
import cbit.vcell.math.*;
import cbit.vcell.math.Variable.Domain;

import java.io.*;
import java.util.*;

import cbit.vcell.solvers.*;
import cbit.vcell.parser.*;

import java.util.zip.*;

import org.vcell.util.BeanUtils;
import org.vcell.util.DataAccessException;
import org.vcell.util.FileUtils;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.VCDataIdentifier;
/**
 * This type was created in VisualAge.
 */
public class SimulationData extends VCData {
	private final static long SizeInBytes = 2000;  // a guess

	private VCDataIdentifier vcDataId = null;
	private File userDirectory = null;
	
	private double dataTimes[] = null;
	private String dataFilenames[] = null;
	private String zipFilenames[] = null;
	private Vector<AnnotatedFunction> annotatedFunctionList = new Vector<AnnotatedFunction>();
	private Vector<DataSetIdentifier> dataSetIdentifierList = new Vector<DataSetIdentifier>();
	
	private long logFileLastModified = 0;
	private long logFileLength = 0;
	// we check first job functions file for user defined functions in the parameter scan,
	// then append user defined functions  
	// this is the file that will be changed when users add functions
	private long meshFileLastModified = 0;
	
	private CartesianMesh mesh = null;

	private boolean particleDataExists = false;
	private boolean isODEData = false;
	
	private boolean bZipFormat2 = false;
	private boolean bZipFormat1 = false;
	private int odeKeepMost = 0;
	private String odeIdentifier = null;

	private DataMoverThread dataMover = null;

	private HashMap<String, SymbolTableFunctionEntry> functionHashTable = null;
	
	public class DataMoverThread implements Runnable {
		private File serverDirectory = null;
		private LocalSolverController localSolverController = null;
		private boolean meshCopied = false;
		private boolean keepRunning = true;
		private double[] copiedDataTimepoints = new double[0];
		private Vector<File> v = new Vector<File>();
		private DataMoverThread() {
			// just so it doesn't get instantiated elsewhere by mistake
		}
		public String getSimulationDataIdentifier() {
			return vcDataId.getID();
		}
		private File getServerDirectory() {
			return serverDirectory;
		}
		private LocalSolverController getLocalSolverController() {
			return localSolverController;
		}
		public void setLocalSolverController(LocalSolverController localSolverController) {
			this.localSolverController = localSolverController;
		}
		public void setServerDirectory(File serverDirectory) {
			this.serverDirectory = serverDirectory;
		}
		public void stopRunning() {
			keepRunning = false;
		}
		private File[] getDataFilesToBeCopied() {
			try {
				refreshLogFile();
				if (isODEData) {
					return new File[] {getODEDataFile()};
				} else {
					if (dataTimes != null) {
						v.clear();
						for (int i=copiedDataTimepoints.length;i<dataTimes.length;i++){
							v.add(getPDEDataFile(dataTimes[i]));
							if (particleDataExists) {
								v.add(getParticleDataFile(dataTimes[i]));
							}
						}
						return (File[])BeanUtils.getArray(v, File.class);
					} else {
						return new File[0];
					}
				}
			} catch (DataAccessException exc) {
				getLocalSolverController().getSessionLog().alert("DataMoverThread:getDataFilesToBeCopied() " + exc.getMessage());
				return new File[0];
			}
		}
		private synchronized void copyMeshFile() throws IOException {
			// mesh file - only once and only for PDE's
			try {
				if ((!meshCopied) && (!getIsODEData())) {
					FileUtils.copyFile(getMeshFile(), new File(getServerDirectory(), getMeshFile().getName()), true, true, 4 * 1024);
					meshCopied = true;
				}
			} catch (DataAccessException exc1) {
				getLocalSolverController().getSessionLog().alert("DataMoverThread:copyMeshFile() " + exc1.getMessage());
			} catch (FileNotFoundException exc2) {
				getLocalSolverController().getSessionLog().alert("DataMoverThread:copyMeshFile() " + exc2.getMessage());
			}
		}
		private synchronized void copyData() throws IOException, DataAccessException {
			// datafile(s), particledata
			File[] files = getDataFilesToBeCopied();
			for (int i=0;i<files.length;i++){
				FileUtils.copyFile(files[i], new File(getServerDirectory(), files[i].getName()), true, true, 4 * 1024);
			}
			if (files.length > 0) {
				// update our tally
				double times[] = getDataTimes();
				copiedDataTimepoints = new double[times.length];
				System.arraycopy(times, 0, copiedDataTimepoints, 0, times.length);
			}
		}
		private synchronized void copyLogFile() throws IOException, DataAccessException {
			// logfile - always
			try {
				FileUtils.copyFile(getLogFile(), new File(getServerDirectory(), getLogFile().getName()), true, true, 4 * 1024);
				double times[] = getDataTimes();
				getLocalSolverController().dataMoved(times[times.length - 1],null, SimulationMessage.MESSAGE_DATAMOVEREVENT_MOVED);
			} catch (FileNotFoundException exc) {
				getLocalSolverController().getSessionLog().alert("DataMoverThread:copyLogFile() " + exc.getMessage());
			}
		}
		public void reset() {
			// in case the same instance is started more then once...
			meshCopied = false;
			keepRunning = true;
			copiedDataTimepoints = new double[0];
		}
		public void run() {
			System.out.println("DataMover starting");
			// while solver is running, move data to server
			while(keepRunning) {
				try {
					Thread.sleep(1000);
					copyMeshFile();
					copyData();
					copyLogFile();
				} catch (Exception exc) {
					getLocalSolverController().getSessionLog().alert(exc.getMessage());
				}
			}
			clean();
		}
		private void clean() {
			// once more, but with feeling... (final cleanup)
			boolean dirty = true;
			while (dirty) {
				dirty = false;
				try {
					copyMeshFile();
				} catch (FileNotFoundException e){
				} catch (Throwable e){
					dirty = true;
					getLocalSolverController().getSessionLog().alert(e.getMessage());
				}
				try {
					copyData();
				} catch (FileNotFoundException e){
				} catch (Throwable e){
					dirty = true;
					getLocalSolverController().getSessionLog().alert(e.getMessage());
				}					
				try {
					copyLogFile();
				} catch (FileNotFoundException e){
				} catch (Throwable e){
					dirty = true;
					getLocalSolverController().getSessionLog().alert(e.getMessage());
				}

				try {
					removeAllResults();
				}catch (Throwable e){
					dirty = true;
					getLocalSolverController().getSessionLog().alert(e.getMessage());
				}
				try {Thread.sleep(5000);} catch (InterruptedException iexc) {}
			}
		}
	}
	
/**
 * SimResults constructor comment.
 */
public SimulationData(VCDataIdentifier argVCDataID, File primaryUserDir, File secondaryUserDir) throws IOException, DataAccessException {	
	try {
		this.vcDataId = argVCDataID;
		this.userDirectory = primaryUserDir;
		checkLogFile();
	} catch (FileNotFoundException exc) {		 
		if (secondaryUserDir == null) {
			throw new FileNotFoundException("simulation data for [" + vcDataId + "] does not exist in primary user directory " + primaryUserDir + ". secondaryUserDir not defined.");
		}
		this.vcDataId = argVCDataID;
		userDirectory = secondaryUserDir;
		checkLogFile();
	}
	getVarAndFunctionDataIdentifiers(null);
}
private void checkLogFile() throws FileNotFoundException {
	try {
		// must exist for constructor to succeed
		getLogFile();
	} catch (FileNotFoundException ex) {
		// maybe we are being asked for pre-parameter scans data files, try old style
		vcDataId = createScanFriendlyVCDataID(vcDataId);
		getLogFile();
	}
}

private void checkSelfReference(AnnotatedFunction function) throws ExpressionException{
	String[] existingUserDefFunctionSymbols = function.getExpression().getSymbols();
	for (int j = 0; existingUserDefFunctionSymbols != null && j< existingUserDefFunctionSymbols.length; j++) {
		if (existingUserDefFunctionSymbols[j].equals(function.getName())){
			throw new ExpressionException("Error adding function '"+function.getName() + "', cannot refer to self");
		}				
	}
}

public static VCDataIdentifier createScanFriendlyVCDataID(VCDataIdentifier inVCDID){
	VCDataIdentifier outVCDID = inVCDID;
	if (inVCDID instanceof VCSimulationDataIdentifier) {
		VCSimulationDataIdentifier vcSimDataID = (VCSimulationDataIdentifier)inVCDID;
		if (vcSimDataID.getJobIndex() == 0) {
			outVCDID = VCSimulationDataIdentifierOldStyle.createVCSimulationDataIdentifierOldStyle(vcSimDataID);
		}
	}
	return outVCDID;
}

/**
 * Insert the method's description here.
 * Creation date: (10/11/00 1:28:51 PM)
 * @param function cbit.vcell.math.Function
 */
private synchronized void addFunctionToList(AnnotatedFunction function) throws ExpressionException {
	// throw exception if already in list (with same name and different expression)
	for (int i=0;i<annotatedFunctionList.size();i++){
		if (annotatedFunctionList.elementAt(i).getName().equals(function.getName())){
			throw new RuntimeException("Error adding function '"+function.getName() + "', already defined");
		}
	}
	for (int i = 0; i < dataSetIdentifierList.size(); i++) {
		if (dataSetIdentifierList.elementAt(i).getName().equals(function.getName())){
			throw new RuntimeException("Error adding function '"+function.getName() + "', identifier exists with same name");
		}
		
	}
	
	checkSelfReference(function);
	function.getExpression().bindExpression(this);
	addFunctionToListInternal(function);
	
}

private void addFunctionToListInternal(AnnotatedFunction function){
	
	DataSetIdentifier dsi = new DataSetIdentifier(function.getName(),function.getFunctionType(), function.getDomain(), true);	
	// add the new function to dataSetIndentifierList so that other functions can bind this function
	dataSetIdentifierList.addElement(dsi);
	//Add new func
	annotatedFunctionList.addElement((AnnotatedFunction)function);
}


public AnnotatedFunction simplifyFunction(AnnotatedFunction function) throws ExpressionException {
	// attempt to bind function and substitute
	AnnotatedFunction simpleFunction = null;
	try {
		simpleFunction = (AnnotatedFunction)BeanUtils.cloneSerializable(function);
		Expression exp = simpleFunction.getExpression();
		exp = MathFunctionDefinitions.substituteSizeFunctions(exp, function.getFunctionType().getVariableDomain());
		exp.bindExpression(this);
		String[] symbols = exp.getSymbols();
		if (symbols != null) {
			for (int i = 0; i < symbols.length; i ++){
				Expression oldExp = new Expression(symbols[i]);
				Expression newExp = null;
				SymbolTableEntry ste = getEntry(symbols[i]);
				if (ste != null) {
					if (!(ste instanceof DataSetIdentifier)) {
						continue;
					}
								
					DataSetIdentifier dsi = (DataSetIdentifier)ste;
					if (!dsi.isFunction()) {
						continue;
					}
					for (int j = 0; j < annotatedFunctionList.size(); j ++){
						AnnotatedFunction mathFunction = (AnnotatedFunction)annotatedFunctionList.elementAt(j);
						if (mathFunction.getName().equals(symbols[i])) {
							newExp = mathFunction.getExpression();
							break;
						}
					}
				} 
				if (ste == null || newExp == null) {
					throw new RuntimeException("dependencies for function '" + function + "' not found"); 
				}
				exp.substituteInPlace(oldExp, newExp);
			}
		}
		exp = exp.flatten();
		exp.bindExpression(this);
		simpleFunction.setExpression(exp);
	} catch (Exception ex) {
		ex.printStackTrace(System.out);
		throw new ExpressionException(ex.getMessage());
	}
	return simpleFunction;
}

/**
 * Insert the method's description here.
 * Creation date: (1/19/00 11:52:22 AM)
 * @return long
 * @param dataType int
 * @param timepoint double
 * @exception org.vcell.util.DataAccessException The exception description.
 */
public synchronized long getDataBlockTimeStamp(int dataType, double timepoint) throws DataAccessException {
	switch (dataType) {
		case PDE_DATA:
			return getPDEDataFile(timepoint).lastModified();
		case ODE_DATA:
			return getODEDataFile().lastModified();
		case PARTICLE_DATA:
			File particleFile = getParticleDataFile(timepoint);
			if (particleFile!=null){
				return particleFile.lastModified();
			}else{
				throw new DataAccessException("particle data doesn't exist for time = "+timepoint);
			}
		default:
			throw new DataAccessException("Bad datatype requested");
	}
}


/**
 * Insert the method's description here.
 * Creation date: (10/9/2002 11:25:31 AM)
 * @return cbit.vcell.simdata.SimResults.DataMoverThread
 */
public SimulationData.DataMoverThread getDataMover() {
	if (dataMover == null) {
		dataMover = new DataMoverThread();
	}
	return dataMover;
}


/**
 * Insert the method's description here.
 * Creation date: (10/11/00 4:44:52 PM)
 * @return cbit.vcell.simdata.DataSetIdentifier
 * @param identifier java.lang.String
 */
private DataSetIdentifier getDataSetIdentifier(String identifier) {
	for (int i = 0; i < dataSetIdentifierList.size();i ++){
		DataSetIdentifier dsi = (DataSetIdentifier)dataSetIdentifierList.elementAt(i);
		if (dsi.getName().equals(identifier)){
			return dsi;
		}
	}
	return null;
}


/**
 * This method was created in VisualAge.
 * @return double[]
 */
public synchronized double[] getDataTimes() throws DataAccessException {

	if (getIsODEData()){ // this will refresh log file; enough for PDE's (refresh method rebuilds time array)
		// for ODE's we need to rebuild
		try {
			ODESimData odeSimData = null;
			if (odeIdentifier.equals(ODE_DATA_IDENTIFIER)) {
				odeSimData = ODESimData.readODEDataFile(getODEDataFile());
			} else if(odeIdentifier.equals(IDA_DATA_IDENTIFIER))
			{
				odeSimData = ODESimData.readIDADataFile(vcDataId, getODEDataFile(), odeKeepMost, getJobFunctionsFile());
			}
			else if (odeIdentifier.equals(NETCDF_DATA_IDENTIFIER))
			{
				odeSimData = ODESimData.readNCDataFile(vcDataId, getODEDataFile(), getJobFunctionsFile());
			}
			else
			{
				throw new DataAccessException("Unexpected data format:"+odeIdentifier);
			}
			if (odeSimData == null) {
				return null;
			}
			dataTimes = odeSimData.extractColumn(odeSimData.findColumn("t"));
		}catch (ExpressionException e){
			e.printStackTrace(System.out);
			throw new DataAccessException("error getting dataset times: "+e.getMessage());
		} catch (FileNotFoundException e) {			
			e.printStackTrace();
			throw new DataAccessException("error getting dataset times: "+e.getMessage());
		}
	}
	return dataTimes;
}


/**
 * Insert the method's description here.
 * Creation date: (10/11/00 1:34:51 PM)
 * @return cbit.vcell.parser.SymbolTableEntry
 * @param identifier java.lang.String
 */
public SymbolTableEntry getEntry(String identifier) {
	SymbolTableEntry entry = null;
	
	entry = ReservedVariable.fromString(identifier);
	if (entry != null){
		return entry;
	}
		
	entry = getDataSetIdentifier(identifier);
	if (entry != null){
		return entry;
	}

	if (identifier.endsWith(OutsideVariable.OUTSIDE_VARIABLE_SUFFIX) || identifier.endsWith(InsideVariable.INSIDE_VARIABLE_SUFFIX)){
		int index = identifier.lastIndexOf("_");		
		String realvar = identifier.substring(0, index);
		DataSetIdentifier dsi = getDataSetIdentifier(realvar);
		if (dsi != null) {
			DataSetIdentifier adsi = new DataSetIdentifier(identifier, dsi.getVariableType(), dsi.getDomain(), dsi.isFunction());
			dataSetIdentifierList.addElement(adsi);
			return adsi;
		}
	}
	
	entry = getFunctionHashTable().get(identifier);
	if (entry!=null){
		return entry;
	}
	
	return null;
}

private HashMap<String, SymbolTableFunctionEntry> getFunctionHashTable() {
	if (functionHashTable == null) {
		functionHashTable = new HashMap<String, SymbolTableFunctionEntry>();
		functionHashTable.put(MathFunctionDefinitions.Function_regionArea_indexed.getName(),MathFunctionDefinitions.Function_regionArea_indexed);
		functionHashTable.put(MathFunctionDefinitions.Function_regionArea_current.getName(),MathFunctionDefinitions.Function_regionArea_current);
		functionHashTable.put(MathFunctionDefinitions.Function_regionVolume_indexed.getName(),MathFunctionDefinitions.Function_regionVolume_indexed);
		functionHashTable.put(MathFunctionDefinitions.Function_regionVolume_current.getName(),MathFunctionDefinitions.Function_regionVolume_current);
		functionHashTable.put(MathFunctionDefinitions.Function_field.getName(),MathFunctionDefinitions.Function_field);
	}
	return functionHashTable;
}


/**
 * Insert the method's description here.
 * Creation date: (10/11/00 5:16:06 PM)
 * @return cbit.vcell.math.Function
 * @param name java.lang.String
 */
public AnnotatedFunction getFunction(OutputContext outputContext,String identifier) {
	try {
		getFunctionDataIdentifiers(outputContext);
	} catch (Exception ex) {
		ex.printStackTrace(System.out);
	}
		
	// Get the function from each annotatedFunction in annotatedFunctionList, check if name is same as 'identifier' argument
	for (int i=0;i<annotatedFunctionList.size();i++){
		AnnotatedFunction function = (AnnotatedFunction)annotatedFunctionList.elementAt(i);
		if (function.getName().equals(identifier)){
			return function;
		}
	}
	return null;
	
}


/**
 * Insert the method's description here.
 * Creation date: (8/2/2004 10:14:40 AM)
 */
private void getFunctionDataIdentifiers(OutputContext outputContext) throws DataAccessException, FileNotFoundException, IOException {
	//
	// add function names to VarName list that is returned
	//
	if (dataSetIdentifierList.size() != 0 && !getIsODEData()){
		
		// remove functions from dataIdentifiers since we are reading functions again
		for (int i = 0; i < dataSetIdentifierList.size(); i ++) {
			DataSetIdentifier dsi = dataSetIdentifierList.elementAt(i);
			if (dsi.isFunction()) {
				dataSetIdentifierList.removeElement(dsi);
				i --;
			}
		}
	
		readFunctions(outputContext);
	}
}



/**
 * Insert the method's description here.
 * Creation date: (10/11/00 5:16:06 PM)
 * @return cbit.vcell.math.Function
 * @param name java.lang.String
 */
public AnnotatedFunction[] getFunctions(OutputContext outputContext) {
	try {
		getFunctionDataIdentifiers(outputContext);
	} catch (Exception ex) {
		ex.printStackTrace(System.out);
	}
	
	AnnotatedFunction functions[] = new AnnotatedFunction[annotatedFunctionList.size()];
	// Get the functions in annotatedFunctionsList
	for (int i = 0; i < annotatedFunctionList.size(); i++){
		//AnnotatedFunction annotatedFunc = (AnnotatedFunction)annotatedFunctionList.elementAt(i);
		//functions[i] = new Function(annotatedFunc.getName(), annotatedFunc.getExpression());
		functions[i] = (AnnotatedFunction)annotatedFunctionList.elementAt(i);
	}
	
	return functions;
}


/**
 * This method was created in VisualAge.
 * @return java.io.File
 * @param user cbit.vcell.server.User
 * @param simID java.lang.String
 */
private synchronized File getFirstJobFunctionsFile() throws FileNotFoundException {
	if (!(vcDataId instanceof VCSimulationDataIdentifier)) {
		return getJobFunctionsFile();
	}
	// always use the functions file from the first simulation in the scan 
	File functionsFile = new File(userDirectory, SimulationData.createCanonicalFunctionsFileName(((VCSimulationDataIdentifier)vcDataId).getSimulationKey(), 0, false));	
	if (functionsFile.exists()){
		return functionsFile;
	}else{
		throw new FileNotFoundException("functions file "+functionsFile.getPath()+" not found");
	}
}


private synchronized File getJobFunctionsFile() throws FileNotFoundException {
	File functionsFile = null;
	functionsFile = new File(userDirectory,vcDataId.getID()+".functions");
	if (functionsFile.exists()){
		return functionsFile;
	}else{
		throw new FileNotFoundException("functions file "+functionsFile.getPath()+" not found");
	}
}

/**
 * This method was created in VisualAge.
 * @return boolean
 */
public synchronized boolean getIsODEData() throws DataAccessException {
	refreshLogFile();
	return isODEData;
}


/**
 * Insert the method's description here.
 * Creation date: (5/25/2004 10:58:18 AM)
 * @return long
 * @param time double
 */
private long getLastModified(File pdeFile, File zipFile) throws IOException {
	if (zipFile == null) {
		return pdeFile.lastModified();
	}

	ZipFile	zipZipFile = DataSet.openZipFile(zipFile);
	ZipEntry logEntry = zipZipFile.getEntry(pdeFile.getName());
	long lastModified = logEntry.getTime();
	try {
		zipZipFile.close();
	} catch (IOException ex) {
	}

	return lastModified;
}


/**
 * This method was created in VisualAge.
 * @return java.io.File
 * @param user cbit.vcell.server.User
 * @param simID java.lang.String
 */
public File getLogFile() throws FileNotFoundException {
	File logFile = new File(userDirectory,vcDataId.getID()+".log");
	if (logFile.exists()){
		return logFile;
	}else{
		throw new FileNotFoundException("log file "+logFile.getPath()+" not found");
	}
}


/**
 * This method was created in VisualAge.
 * @return java.io.File
 * @param user cbit.vcell.server.User
 * @param simID java.lang.String
 */
private synchronized File getMembraneMeshMetricsFile() throws FileNotFoundException {
	File meshMetricsFile = new File(userDirectory,vcDataId.getID()+".meshmetrics");
	if (meshMetricsFile.exists()){
		return meshMetricsFile;
	}

	return null;
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.solvers.CartesianMesh
 */
public synchronized CartesianMesh getMesh() throws DataAccessException, MathException {
	refreshMeshFile();
	return mesh;
}


/**
 * This method was created in VisualAge.
 * @return java.io.File
 * @param user cbit.vcell.server.User
 * @param simID java.lang.String
 */
private synchronized File getMeshFile() throws FileNotFoundException {
	File meshFile = new File(userDirectory,vcDataId.getID()+".mesh");
	if (meshFile.exists()){
		return meshFile;
	}else{
		throw new FileNotFoundException("mesh file "+meshFile.getPath()+" not found");
	}
}


/**
 * Insert the method's description here.
 * Creation date: (1/14/00 2:28:47 PM)
 * @return cbit.vcell.simdata.ODEDataBlock
 */
public synchronized ODEDataBlock getODEDataBlock() throws DataAccessException {
	File file = getODEDataFile();
	long lastModified = file.lastModified();
	ODESimData odeSimData = null;
	try {
		
		if (odeIdentifier.equals(ODE_DATA_IDENTIFIER)) {
			odeSimData = ODESimData.readODEDataFile(getODEDataFile());
		} else if(odeIdentifier.equals(IDA_DATA_IDENTIFIER))
		{
			odeSimData = ODESimData.readIDADataFile(vcDataId, getODEDataFile(), odeKeepMost, getJobFunctionsFile());
		}
		else if (odeIdentifier.equals(NETCDF_DATA_IDENTIFIER))
		{
			odeSimData = ODESimData.readNCDataFile(vcDataId, getODEDataFile(), getJobFunctionsFile());
		}
		else
		{
			throw new DataAccessException("Unexpected data format:"+odeIdentifier);
		}
		if (odeSimData == null) {
			return null;
		}
		int colIndex = odeSimData.findColumn(ReservedVariable.TIME.getName()); //look for 't' first
		//if not time serie data, it should be multiple trial data. get the trial no as fake time data. let it run since we will not need it when displaying histogram
		if(colIndex == -1)
			colIndex = odeSimData.findColumn(SimDataConstants.HISTOGRAM_INDEX_NAME); 
		dataTimes = odeSimData.extractColumn(colIndex);
	} catch (ExpressionException e){
		e.printStackTrace(System.out);
		throw new DataAccessException("error getting data times: "+e.getMessage());
	} catch (FileNotFoundException e) {			
		e.printStackTrace();
		throw new DataAccessException("error getting dataset times: "+e.getMessage());
	}
	ODEDataInfo odeDataInfo = new ODEDataInfo(vcDataId.getOwner(), vcDataId.getID(), lastModified);
	if (odeSimData != null) {
		return new ODEDataBlock(odeDataInfo, odeSimData);
	} else {
		return null;
	}
}


/**
 * This method was created in VisualAge.
 * @return File
 */
private synchronized File getODEDataFile() throws DataAccessException {
	refreshLogFile();
	if (dataFilenames == null) {
		throw new DataAccessException("ODE data filename not read from logfile");
	}
	File odeFile = new File(userDirectory, dataFilenames[0]);
	if (odeFile.exists()) {
		return odeFile;
	}
	throw new DataAccessException("no results are available yet, please wait and try again...");
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.simdata.ParticleDataBlock
 * @param double time
 */
public synchronized ParticleDataBlock getParticleDataBlock(double time) throws DataAccessException, IOException {
	refreshLogFile();
	File particleFile = getParticleDataFile(time);
	if (particleFile==null){
		throw new DataAccessException("particle data doesn't exist for time = "+time);
	}
	File zipFile = null;
	try {
		zipFile = getPDEDataZipFile(time);
	} catch (DataAccessException ex) {
		zipFile = null;
	}
	
	ParticleDataBlock particleDataBlock = new ParticleDataBlock(vcDataId.getOwner(), vcDataId.getID(), time, particleFile, zipFile);
	if (particleDataBlock.getParticleData() != null) {
		return particleDataBlock;
	} else {
		return null;
	}
}


/**
 * This method was created in VisualAge.
 * @return boolean
 */
public synchronized boolean getParticleDataExists() throws DataAccessException {
	refreshLogFile();
	return particleDataExists;
}


/**
 * This method was created in VisualAge.
 * @return File
 * @param time double
 */
private synchronized File getParticleDataFile(double time) throws DataAccessException {
	File simFile = getPDEDataFile(time);
	File particleFile = new File(simFile.getPath() + PARTICLE_DATA_EXTENSION);
	if (particleFile.exists()){
		return particleFile;
	}else{
		return null;
	}
}


/**
 * This method was created by a SmartGuide.
 * @return java.lang.String
 * @param time double
 */
private synchronized File getPDEDataFile(double time) throws DataAccessException {
	//
	// take a snapshot in time
	//
	double times[] = getDataTimes();
	
	if (times == null){
		return null;
	}
	if (times.length != dataFilenames.length){
		throw new DataAccessException("dataTime array and dataFilename Array are corrupted");
	}	
	for (int i=0;i<times.length;i++){
		if (times[i] == time){
			String dataFileName = dataFilenames[i];
			int index = dataFileName.lastIndexOf("\\");
			if (index>=0){
				dataFileName = dataFileName.substring(index + 1);
			}
			index = dataFileName.lastIndexOf("/");
			if (index>=0){
				dataFileName = dataFileName.substring(index + 1);
			}
			return new File(userDirectory,dataFileName);
		}
	}
	throw new DataAccessException("data at time="+time+" not found");
}


/**
 * This method was created by a SmartGuide.
 * @return java.lang.String
 * @param time double
 */
private synchronized DataSet getPDEDataSet(File pdeFile, double time) throws DataAccessException {	
	DataSet dataSet = new DataSet();
	File zipFile = null;
	
	try  {
		zipFile = getPDEDataZipFile(time);
	} catch (DataAccessException ex) {
		zipFile = null;
	}
	
	try {
		dataSet.read(pdeFile, zipFile);
	} catch (IOException ex) {
		ex.printStackTrace();
		throw new DataAccessException("data at time="+time+" read error");
	}

	return dataSet;
}


/**
 * This method was created by a SmartGuide.
 * @return java.lang.String
 * @param time double
 */
private synchronized File getPDEDataZipFile(double time) throws DataAccessException {
	//
	// take a snapshot in time
	//
	if (bZipFormat1) {
		File zipFile = new File(userDirectory,vcDataId.getID()+".zip");
		if (zipFile.exists()) {
			return zipFile;
		} else {
			return null;
		}
	} 

	if (!bZipFormat2) {
		return null;
	}
	
	double times[] = getDataTimes();
	
	if (times == null){
		return null;
	}
	if (zipFilenames == null || times.length != zipFilenames.length){
		return null;
	}	
	for (int i=0;i<times.length;i++){
		if (times[i] == time){
			String zipFileName = zipFilenames[i];
			int index = zipFileName.lastIndexOf("\\");
			if (index>=0){
				zipFileName = zipFileName.substring(index + 1);
			}
			index = zipFileName.lastIndexOf("/");
			if (index>=0){
				zipFileName = zipFileName.substring(index + 1);
			}
			File zipFile = new File(userDirectory,zipFileName);
			if (zipFile.exists()) {
				return zipFile;
			} else {
				return null;
			}
		}
	}
	return null;
}

/**
 * This method was created in VisualAge.
 * @return cbit.vcell.simdata.SimResultsInfo
 */
public synchronized VCDataIdentifier getResultsInfoObject() {
	return vcDataId;
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.simdata.DataBlock
 * @param user cbit.vcell.server.User
 * @param simID java.lang.String
 */
public synchronized SimDataBlock getSimDataBlock(OutputContext outputContext, String varName, double time) throws DataAccessException, IOException {
	refreshLogFile();
	try {
		getFunctionDataIdentifiers(outputContext);
	} catch (Exception ex) {
		ex.printStackTrace(System.out);
	}
		
	File pdeFile = getPDEDataFile(time);
	if (pdeFile==null){
		return null;
	}
	DataSet dataSet = getPDEDataSet(pdeFile, time);

	File zipFile = null;
	
	try  {
		zipFile = getPDEDataZipFile(time);
	} catch (DataAccessException ex) {
		zipFile = null;
	}
		
	long lastModified = getLastModified(pdeFile, zipFile);
	DataSetIdentifier dsi = getDataSetIdentifier(varName);
	if (dsi == null) {
		throw new DataAccessException("data not found for variable " + varName);
	}
	final String varNameInDataSet = dsi.getQualifiedName();
	double data[] = dataSet.getData(varNameInDataSet, zipFile);
	int varTypeInt = dataSet.getVariableTypeInteger(varNameInDataSet);
	VariableType variableType = null;
	try {
		variableType = VariableType.getVariableTypeFromInteger(varTypeInt);
	}catch (IllegalArgumentException e){
		e.printStackTrace(System.out);
		System.out.println("invalid varTypeInt = "+varTypeInt+" for variable "+varName+" at time "+time);
		try {
			variableType = VariableType.getVariableTypeFromLength(getMesh(),data.length);
		} catch (MathException ex) {
			ex.printStackTrace(System.out);
			throw new DataAccessException(ex.getMessage());
		}
	}
	PDEDataInfo pdeDataInfo = new PDEDataInfo(vcDataId.getOwner(),vcDataId.getID(),varName,time,lastModified);
	if (data!=null){
		return new SimDataBlock(pdeDataInfo,data,variableType);
	}else{
		return null;
	}
}

/**
 * This method was created in VisualAge.
 * @return cbit.vcell.simdata.DataBlock
 * @param user cbit.vcell.server.User
 * @param simID java.lang.String
 */
synchronized double[][][] getSimDataTimeSeries0(
		OutputContext outputContext,
		String varNames[],
		int[][] indexes,
		boolean[] wantsThisTime,
		DataSetControllerImpl.SpatialStatsInfo spatialStatsInfo,
		DataSetControllerImpl.ProgressListener progressListener) throws DataAccessException,IOException{

	
	refreshLogFile();
	try {
		getFunctionDataIdentifiers(outputContext);
	} catch (Exception ex) {
		ex.printStackTrace(System.out);
	}

	// Setup parameters for SimDataReader
	int resultsCounter = 0;
	for(int i=0;i<wantsThisTime.length;i+= 1){
		if(wantsThisTime[i]){
			resultsCounter+= 1;
		}
	}
	double[] tempDataTimes = dataTimes.clone();
	
	String[] tempZipFileNames = null;
	if (bZipFormat2) {
		tempZipFileNames = new String[tempDataTimes.length];
	}
	String[] tempSimDataFileNames = new String[tempDataTimes.length];
	for(int i=0;i<tempDataTimes.length;i+= 1){
		if (bZipFormat2 || bZipFormat1) {
			if(bZipFormat2){
				tempZipFileNames[i] = getPDEDataZipFile(tempDataTimes[i]).getAbsolutePath();
			}
			tempSimDataFileNames[i] = dataFilenames[i];
		} else {
			tempSimDataFileNames[i] = userDirectory.getAbsolutePath()+"\\"+dataFilenames[i];//getPDEDataFile(dataTimes[i]).getAbsolutePath();
		}

	}
	SimDataReader sdr = null;

	final int NUM_STATS = 4;//min,max,mean,wmean
	//Create results buffer
	double[][][] results = new double[resultsCounter][][];

	for(int i=0;i<results.length;i+= 1){
		results[i] = new double[varNames.length][];
		for(int j=0;j<results[i].length;j+= 1){
			if(spatialStatsInfo != null){
				results[i][j] = new double[NUM_STATS];//min,max.mean,wmean
			}else{
				results[i][j] = new double[indexes[j].length];
			}
		}
	}

	
	double[][] singleTimePointResultsBuffer = new double[varNames.length][];
	for(int i=0;i<singleTimePointResultsBuffer.length;i+= 1){
		singleTimePointResultsBuffer[i] = new double[indexes[i].length];
	}
	
	//In case sim files have been updated since "wantsThisTime" was calculated
	if(wantsThisTime.length < tempDataTimes.length){
		double[] tempTempDataTimes = new double[wantsThisTime.length];
		System.arraycopy(tempDataTimes, 0, tempTempDataTimes, 0, wantsThisTime.length);
		tempDataTimes = tempTempDataTimes;
		String[] tempTempZipFileNames = new String[wantsThisTime.length];
		System.arraycopy(tempZipFileNames, 0, tempTempZipFileNames, 0, wantsThisTime.length);
		tempZipFileNames = tempTempZipFileNames;
		String[] tempTempSimDataFileNames = new String[wantsThisTime.length];
		System.arraycopy(tempSimDataFileNames, 0, tempTempSimDataFileNames, 0, wantsThisTime.length);
		tempSimDataFileNames = tempTempSimDataFileNames;
	}
	try{
		sdr =
			new SimDataReader(
				wantsThisTime,
				tempDataTimes,
				tempZipFileNames,
				tempSimDataFileNames,
				varNames,
				indexes
			);
		int counter = 0;
		int progressCounter = 0;
		while(sdr.hasMoreData()){
			sdr.getNextDataAtCurrentTime(singleTimePointResultsBuffer);
			// Copy data to timeSeries format
			if(wantsThisTime[counter]){
				for(int i=0;i<varNames.length;i+= 1){
					if(spatialStatsInfo != null){
						results[progressCounter][i] = calcSpaceStats(singleTimePointResultsBuffer[i],i,spatialStatsInfo);
					}else{
						for(int j=0;j<indexes[i].length;j+= 1){
							results[progressCounter][i][j] = singleTimePointResultsBuffer[i][j];
						}
					}
				}
				progressCounter+= 1;
				if(progressListener != null){
					progressListener.updateProgress(100.0 * (double)progressCounter / (double)resultsCounter);
				}
			}
			counter+= 1;
		}
		return results;
	}catch(DataAccessException e){
		throw e;
	}catch(IOException e){
		throw e;
	}catch(Throwable e){
		throw new DataAccessException(e.getMessage(),e);
	}finally{
		if(sdr != null){
			sdr.close();
		}
	}
}


/**
 * This method was created in VisualAge.
 * @return long
 */
public synchronized long getSizeInBytes() {
	return SizeInBytes;
}


/**
 * This method was created in VisualAge.
 * @return java.lang.String[]
 */
public synchronized DataIdentifier[] getVarAndFunctionDataIdentifiers(OutputContext outputContext) throws IOException, DataAccessException {
	// Is this zip format?
	File zipFile1 = new File(userDirectory,vcDataId.getID()+".zip");
	File zipFile2 = new File(userDirectory,vcDataId.getID()+"00.zip");
	bZipFormat1 = false;
	bZipFormat2 = false;
	if (zipFile1.exists()) {
		bZipFormat1 = true;
	} else if (zipFile2.exists()){
		bZipFormat2 = true;
	}
	
	refreshLogFile();
	try {
		refreshMeshFile();
	}catch (MathException e){
		e.printStackTrace(System.out);
		throw new DataAccessException(e.getMessage());
	}
	
	if (!getIsODEData() && dataFilenames != null) {
		// read variables only when I have never read the file since variables don't change
		if (dataSetIdentifierList.size() == 0) {
			File file = getPDEDataFile(0.0);
			DataSet dataSet = getPDEDataSet(file, 0.0);
				
			String[] varNames = dataSet.getDataNames();
			int varTypeInts[] = dataSet.getVariableTypeIntegers();

			if (varNames == null){
				return null;
			}
			
			dataSetIdentifierList.clear();
			for (int i = 0; i < varNames.length; i++){
				VariableType varType = null;
				try {
					varType = VariableType.getVariableTypeFromInteger(varTypeInts[i]);
				}catch (Throwable e){
					varType = VariableType.getVariableTypeFromLength(mesh,dataSet.getDataLength(varNames[i]));
				}
				Domain domain = Variable.getDomainFromCombinedIdentifier(varNames[i]);
				String varName = Variable.getNameFromCombinedIdentifier(varNames[i]);
				dataSetIdentifierList.addElement(new DataSetIdentifier(varName,varType,domain));
			}
		} 

		// always read functions file since functions might change
		getFunctionDataIdentifiers(outputContext);
	}

	if (getIsODEData() && dataSetIdentifierList.size() == 0){
		ODEDataBlock odeDataBlock = getODEDataBlock();
		if (odeDataBlock == null) {
			throw new DataAccessException("Results are not availabe yet. Please try again later.");
		}
		ODESimData odeSimData = odeDataBlock.getODESimData();
		int colCount = odeSimData.getColumnDescriptionsCount();
		// assume index=0 is time "t"
		int DATA_OFFSET = 1;
		dataSetIdentifierList.clear();
		for (int i=0;i<(colCount-DATA_OFFSET);i++){
			String varName = odeSimData.getColumnDescriptions(i+DATA_OFFSET).getDisplayName();
			Domain domain = null; //TODO domain
			dataSetIdentifierList.addElement(new DataSetIdentifier(varName,VariableType.NONSPATIAL,domain));
		}		
	}	
	
	DataIdentifier[] dis = new DataIdentifier[dataSetIdentifierList.size()];
	for (int i = 0; i < dataSetIdentifierList.size(); i ++){
		DataSetIdentifier dsi = (DataSetIdentifier)dataSetIdentifierList.elementAt(i);
		String displayName = dsi.getName();
		if (dsi.isFunction()) {
			AnnotatedFunction f = null;
			for (int j=0;j<annotatedFunctionList.size();j++){
				AnnotatedFunction function = (AnnotatedFunction)annotatedFunctionList.elementAt(j);
				if (function.getName().equals(dsi.getName())){
					f = function;
					break;
				}
			}
			if (f != null) {
				displayName = f.getDisplayName();
			}
		}
		dis[i] = new DataIdentifier(dsi.getName(), dsi.getVariableType(), dsi.getDomain(), dsi.isFunction(), displayName);
	}		
	return dis;
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.solvers.CartesianMesh
 */
synchronized int[] getVolumeSize() throws IOException, DataAccessException {

	/*
	DataSet dataSet = new DataSet();
	File file = getPDEDataFile(0.0);
	if (file==null){
		return null;
	}
	dataSet.read(file);
	*/
	
	File file = getPDEDataFile(0.0);
	DataSet dataSet = getPDEDataSet(file, 0.0);
	
	int size[] = new int[3];
	
	size[0] = dataSet.getSizeX();
	size[1] = dataSet.getSizeY();
	size[2] = dataSet.getSizeZ();
	
	return size;
}


/**
 * Insert the method's description here.
 * Creation date: (1/15/2004 11:48:25 AM)
 */
private void readFunctions(OutputContext outputContext) throws FileNotFoundException, IOException {

	File firstJobFunctionsFile = getFirstJobFunctionsFile();
	File jobFunctionsFile = getJobFunctionsFile();
	// only dataset functions
	Vector<AnnotatedFunction> annotatedFuncsVector = FunctionFileGenerator.readFunctionsFile(jobFunctionsFile, vcDataId.getID());
	/* not required as long as we are skipping any legacy user-defined functions from the functions file */
	if (!firstJobFunctionsFile.equals(jobFunctionsFile)) {
		Vector <AnnotatedFunction> f1 = FunctionFileGenerator.readFunctionsFile(firstJobFunctionsFile, vcDataId.getID());
		for (AnnotatedFunction f : f1) {
			if (f.isOldUserDefined()) {
				annotatedFuncsVector.add(f);
			}
		}
	}
	// add user-defined functions from output context, if any
	if (outputContext != null) {
		for (int i = 0; i < outputContext.getOutputFunctions().length; i++) {
			annotatedFuncsVector.add(outputContext.getOutputFunctions()[i]);
		}
	}
	//
	// Convert this annotatedfunctionsVector into the field annotatedFunctionsList.
	//

	annotatedFunctionList.clear();
	for (int i = 0; i < annotatedFuncsVector.size(); i++){
		AnnotatedFunction annotatedFunction = (AnnotatedFunction)annotatedFuncsVector.elementAt(i);
		try {
			addFunctionToList(annotatedFunction);
		} catch (ExpressionException e) {
			e.printStackTrace(System.out);
			throw new RuntimeException("Could not add function "+annotatedFunction.getName()+" to annotatedFunctionList");
		}
	}
}


/**
 * This method was created by a SmartGuide.
 */
private synchronized void readLog(File logFile) throws FileNotFoundException, DataAccessException, IOException {

	if (logFile.exists()){
		long length = logFile.length();
		long lastModified = logFile.lastModified();
		if (lastModified == logFileLastModified && logFileLength == length){
//System.out.println("<<<SYSOUT ALERT>>>SimResults.readLog("+info.getSimID()+") lastModified and fileLength unchanged (no re-read), logFile.lastModified() = "+(new java.util.Date(lastModified)).toString());
			return;
		}else{
//String status = "";
//status += (lastModified == logFileLastModified)?"":" lastModified=("+(new java.util.Date(lastModified)).toString()+")";
//status += (logFileLength == length)?"":" fileLength=("+length+")";
//System.out.println("<<<SYSOUT ALERT>>>SimResults.readLog("+info.getSimID()+") "+status+" changed (forced a re-read)");
			dataFilenames = null;
			zipFilenames = null;
			dataTimes = null;
			logFileLastModified = lastModified;
			logFileLength = length;
		}	
	}else{
		dataFilenames = null;
		zipFilenames = null;
		dataTimes = null;
		throw new FileNotFoundException("log file "+logFile.getPath()+" not found");
	}

	//
	// read log file and check whether ODE or PDE data
	//
	StringBuffer stringBuffer = new StringBuffer();
	FileInputStream is = null;
	try {
		is = new FileInputStream(logFile);
		InputStreamReader reader = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(reader);
		char charArray[] = new char[10000];
		while (true) {
			int numRead = br.read(charArray, 0, charArray.length);
			if (numRead > 0) {
				stringBuffer.append(charArray,0,numRead);
			} else if (numRead == -1) {
				break;
			}
		}
	}finally{
		if (is != null){
			is.close();
		}
	}
	String logfileContent = stringBuffer.toString();
	if (logfileContent.length() != logFileLength){
		System.out.println("<<<SYSOUT ALERT>>>SimResults.readLog(), read "+stringBuffer.length()+" of "+logFileLength+" bytes of log file");
	}
	if ((logfileContent.startsWith(IDA_DATA_IDENTIFIER)) || (logfileContent.startsWith(ODE_DATA_IDENTIFIER)) || (logfileContent.startsWith(NETCDF_DATA_IDENTIFIER)))
	{
		String newLineDelimiters = "\n\r";
		StringTokenizer lineTokenizer = new StringTokenizer(logfileContent,newLineDelimiters);
		isODEData = true;
		//memorize format
		odeIdentifier = lineTokenizer.nextToken(); // br.readLine();
		
		String odeDataFormat = lineTokenizer.nextToken(); // br.readLine();
		dataFilenames = new String[] { lineTokenizer.nextToken() }; // {br.readLine()};
		if (lineTokenizer.hasMoreTokens()) {
			String keepMostLine = lineTokenizer.nextToken();
			StringTokenizer st = new StringTokenizer(keepMostLine);
			String token = st.nextToken();
			if (token.equals(KEEP_MOST)) {
				odeKeepMost = Integer.parseInt(st.nextToken());
			}
		}		
	} else {
		StringTokenizer st = new StringTokenizer(logfileContent);
		// PDE, so parse into 'dataFilenames' and 'dataTimes' arrays
		isODEData = false;
		if (!( (bZipFormat2 && st.countTokens()%4 == 0) || (!bZipFormat2 && st.countTokens()%3 == 0))) {
			dataTimes = null;
			dataFilenames = null;
			zipFilenames = null;
			throw new DataAccessException("SimResults.readLog(), tokens should be factor of 3 or 4, bad parsing");
		}

		int numFiles = st.countTokens()/3;
		if (bZipFormat2) {
			numFiles = st.countTokens()/4;
			zipFilenames = new String[numFiles];
		}else{
			zipFilenames = null;
		}
		dataTimes = new double[numFiles];
		dataFilenames = new String[numFiles];
		int index = 0;
		while (st.hasMoreTokens()){
			String iteration = st.nextToken();
			String filename = st.nextToken();
			String time = null;
			if (bZipFormat2) {
				String zipname = st.nextToken();
				time = st.nextToken();
				zipFilenames[index] = (new File(zipname)).getName();
			} else {
				time = st.nextToken();
			}
			dataTimes[index] = (new Double(time)).doubleValue();
			dataFilenames[index] = (new File(filename)).getName();
			index++;
		}
		// now check if .particle files also exist
		try {
			File firstFile = getParticleDataFile(dataTimes[0]);
			if (firstFile!=null){
				particleDataExists = true;
			}else{
				particleDataExists = false;
			}
		} catch (Exception exc) {
			particleDataExists = false;
		}
	}
}


/**
 * This method was created in VisualAge.
 * @param logFile java.io.File
 */
private synchronized void readMesh(File meshFile,File membraneMeshMetricsFile) throws FileNotFoundException, IOException, MathException {
	if (meshFile.exists()){
		long lastModified = meshFile.lastModified();
		if (lastModified == meshFileLastModified){
			return;
		}else{
			mesh = null;
			meshFileLastModified = lastModified;
		}	
	}else{
		throw new FileNotFoundException("mesh file "+meshFile.getPath()+" not found");
	}

	//
	// read meshFile,MembraneMeshMetrics and parse into 'mesh' object
	//
	mesh = CartesianMesh.readFromFiles(meshFile, membraneMeshMetricsFile);
}


/**
 * This method was created in VisualAge.
 */
private synchronized void refreshLogFile() throws DataAccessException {
	//
	// (re)read the log file if necessary
	//
	try {
		readLog(getLogFile());
	} catch (FileNotFoundException e) {
	} catch (IOException e) {
		e.printStackTrace(System.out);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * This method was created in VisualAge.
 */
private synchronized void refreshMeshFile() throws DataAccessException, MathException {
	//
	// (re)read the log file if necessary
	//
	try {
		readMesh(getMeshFile(),getMembraneMeshMetricsFile());
	} catch (FileNotFoundException e) {
	} catch (IOException e) {
		e.printStackTrace(System.out);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * This method was created in VisualAge.
 */
public synchronized void removeAllResults() throws DataAccessException {
	File logFile = null;
	try {
		logFile = getLogFile();
	}catch (FileNotFoundException e){
	}
	File meshFile = null;
	try {
		meshFile = getMeshFile();
	}catch (FileNotFoundException e){
	}
		
	removeAllResults(logFile,meshFile);
}


/**
 * This method was created in VisualAge.
 */
private synchronized void removeAllResults(File logFile, File meshFile) {
	//
	// remove data files
	//
	double[] times = null;
	try {
		times = getDataTimes();
	} catch (DataAccessException exc) {
		System.out.println("<<EXCEPTION>> SimResults:removeAllResults() " + exc.getMessage());
	}
	//
	// if ODE, remove .ode
	//
	if (isODEData){
		File odeFile = null;
		try {
			odeFile = getODEDataFile();
		}catch (DataAccessException e){
			System.out.println("<<EXCEPTION>> SimResults:removeAllResults() removing .ode file: " + e.getMessage());
		}
		if (odeFile!=null && odeFile.exists()){
			odeFile.delete();
		}
	}else{
		//
		// must be PDE
		//
		// remove data files and particle files (if they exist)
		//
		if (times!=null){
			for (int i = 0; i < times.length; i++) {
				File zipFile = null;
				try {
					zipFile = getPDEDataZipFile(times[i]);
				} catch (DataAccessException exc) {
					System.out.println("<<EXCEPTION>> SimResults:removeAllResults() removing .zip file " + exc.getMessage());
				}
				if (zipFile != null && zipFile.exists()) {
					zipFile.delete();
				}				
				File dataFile = null;
				try {
					dataFile = getPDEDataFile(times[i]);
				} catch (DataAccessException exc) {
					System.out.println("<<EXCEPTION>> SimResults:removeAllResults() removing .sim file " + exc.getMessage());
				}
				if (dataFile!=null && dataFile.exists()){
					dataFile.delete();
				}
				File particleFile = null;
				try {
					particleFile = getParticleDataFile(times[i]);
				} catch (DataAccessException exc) {
					System.out.println("<<EXCEPTION>> SimResults:removeAllResults() removing .particle file " + exc.getMessage());
				}
				if (particleFile!=null && particleFile.exists()){
					particleFile.delete();
				}
			}
		}
	}
	//
	// remove log file
	//
	if (logFile!=null){
		if (logFile.exists()){
			logFile.delete();
		}
	}
	//
	// remove mesh file
	//
	if (meshFile!=null){
		if (meshFile.exists()){
			meshFile.delete();
		}
	}
	//
	// clear cached info
	//
	dataFilenames = null;
	zipFilenames = null;
	dataTimes = null;
	annotatedFunctionList.removeAllElements();
	mesh = null;
	logFileLastModified = 0;
	logFileLength = 0;
	meshFileLastModified = 0;
}


/**
 * FieldDataIdentifier constructor comment.
 */


public static String createCanonicalSimFilePathName(KeyValue fieldDataKey,int timeIndex,int jobIndex,boolean isOldStyle){
	return
		createSimIDWithJobIndex(fieldDataKey,jobIndex,isOldStyle)+
		(timeIndex>=1000?timeIndex+"":"")+
		(timeIndex>=100 && timeIndex<1000?"0"+timeIndex:"")+
		(timeIndex>=10 && timeIndex<100?"00"+timeIndex:"")+
		(timeIndex<10?"000"+timeIndex:"")+SimDataConstants.PDE_DATA_EXTENSION;
}

public static String createCanonicalFieldDataLogFileName(KeyValue fieldDataKey){
	return
	createSimIDWithJobIndex(fieldDataKey,0,false)+
	SimDataConstants.LOGFILE_EXTENSION;
}

public static String createCanonicalFieldFunctionSyntax(String externalDataIdentifierName,String varName,double beginTime,String extDataIdVariableTypeName){	
	VariableType vt = VariableType.getVariableTypeFromVariableTypeName(extDataIdVariableTypeName);
	return ASTFuncNode.getFunctionNames()[ASTFuncNode.FIELD] + "("+
		externalDataIdentifierName+","+varName+","+beginTime+
		(vt.equals(VariableType.UNKNOWN)?"": ","+vt.getTypeName())+")";
}

public static String createCanonicalSimZipFileName(KeyValue fieldDataKey,int zipIndex,int jobIndex,boolean isOldStyle){
	return
	createSimIDWithJobIndex(fieldDataKey,jobIndex,isOldStyle)+
	(zipIndex<10?"0":"")+zipIndex+SimDataConstants.ZIPFILE_EXTENSION;
}

public static String createCanonicalSimLogFileName(KeyValue fieldDataKey,int jobIndex,boolean isOldStyle){
	return
	createSimIDWithJobIndex(fieldDataKey,jobIndex,isOldStyle)+
	SimDataConstants.LOGFILE_EXTENSION;
}

public static String createCanonicalMeshFileName(KeyValue fieldDataKey,int jobIndex,boolean isOldStyle){
	return
	createSimIDWithJobIndex(fieldDataKey,jobIndex,isOldStyle)+
	SimDataConstants.MESHFILE_EXTENSION;
}

public static String createCanonicalFunctionsFileName(KeyValue fieldDataKey,int jobIndex,boolean isOldStyle){
	return
		createSimIDWithJobIndex(fieldDataKey,jobIndex,isOldStyle)+
		SimDataConstants.FUNCTIONFILE_EXTENSION;
}

public static String createCanonicalResampleFileName(SimResampleInfoProvider simResampleInfoProvider,FieldFunctionArguments fieldFuncArgs){
	return
		createSimIDWithJobIndex(
				simResampleInfoProvider.getSimulationKey(),
				simResampleInfoProvider.getJobIndex(),
				!simResampleInfoProvider.isParameterScanType())+
		FieldDataIdentifierSpec.getDefaultFieldDataFileNameForSimulation(fieldFuncArgs);
}

public static String createSimIDWithJobIndex(KeyValue fieldDataKey,int jobIndex,boolean isOldStyle){
	if(isOldStyle && jobIndex != 0){
		throw new IllegalArgumentException("Job index must be 0 for Old Style names");
	}
	String name = Simulation.createSimulationID(fieldDataKey);
	if(!isOldStyle){
		name = SimulationJob.createSimulationJobID(name, jobIndex);
	}
	return name;
}

public void getEntries(Map<String, SymbolTableEntry> entryMap) {
	ReservedVariable.getAll(entryMap);
	for (DataSetIdentifier dsi : dataSetIdentifierList) {
		entryMap.put(dsi.getName(), dsi);
	}
}

}