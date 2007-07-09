package cbit.vcell.simdata;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.math.*;
import cbit.vcell.mesh.CartesianMesh;

import java.io.*;
import java.util.*;

import java.util.zip.*;

import org.vcell.expression.ExpressionException;
import org.vcell.expression.ExpressionFactory;
import org.vcell.expression.IExpression;
import org.vcell.expression.SymbolTable;
import org.vcell.expression.SymbolTableEntry;
import org.vcell.util.*;

import edu.uchc.vcell.expression.internal.*;
/**
 * This type was created in VisualAge.
 */
public class SimulationData extends VCData implements SymbolTable {
	private final static long SizeInBytes = 2000;  // a guess

	private VCDataIdentifier info = null;
	private File userDirectory = null;
	
	private double dataTimes[] = null;
	private String dataFilenames[] = null;
	private String zipFilenames[] = null;
	private Vector annotatedFunctionList = new Vector();
	private Vector dataSetIdentifierList = new Vector();
	
	private long logFileLastModified = 0;
	private long functionFileLastModified = 0;
	private long logFileLength = 0;
	private long functionFileLength = 0;
	private long meshFileLastModified = 0;
	
	private CartesianMesh mesh = null;

	private boolean particleDataExists = false;
	private boolean isODEData = false;

	private boolean bZipFormat2 = false;
	private boolean bZipFormat1 = false;

/**
 * SimResults constructor comment.
 */
public SimulationData(VCDataIdentifier argVCDataID, File userDir) throws IOException, DataAccessException {
	this.info = argVCDataID;
	this.userDirectory = userDir;
	try {
		// must exist for constructor to succeed
		getLogFile();
	} catch (FileNotFoundException exc) {
		// maybe we are being asked for pre-parameter scans data files, try old style
//		if (info instanceof VCSimulationDataIdentifier) {
//			VCSimulationDataIdentifier vcSimDataID = (VCSimulationDataIdentifier)info;
//			if (vcSimDataID.getJobIndex() == 0) {
//				VCDataIdentifier vcDataID = VCSimulationDataIdentifierOldStyle.createVCSimulationDataIdentifierOldStyle(vcSimDataID);
//				info = vcDataID;
//			}
//		}
		throw new RuntimeException("failed to find log file, maybe it is an old sytle simulation data identifier ... pre-scans");
	}
	getVarAndFunctionDataIdentifiers();
}


/**
 * Insert the method's description here.
 * Creation date: (10/11/00 1:28:51 PM)
 * @param function cbit.vcell.math.Function
 */
public synchronized void addFunction(AnnotatedFunction function) throws ExpressionException {
	try {
		getFunctionDataIdentifiers();
	} catch (Exception ex) {
		ex.printStackTrace(System.out);
	}
	
	addFunctionToList(function);

	AnnotatedFunction annotatedFunctions[] = new AnnotatedFunction[annotatedFunctionList.size()];
	annotatedFunctionList.copyInto(annotatedFunctions);
	try {
		FunctionFileGenerator ffg = new FunctionFileGenerator(getFunctionsFile().getPath(), annotatedFunctions);
		ffg.generateFunctionFile();

		// my lastModified and length should be changed because I just rewrote the file.
		File funcFile = getFunctionsFile();		
		functionFileLength = funcFile.length();
		functionFileLastModified = funcFile.lastModified();
	} catch (ExpressionException e) {
		e.printStackTrace(System.out);
		throw e;
	} catch (Exception e){
		e.printStackTrace(System.out);
		throw new RuntimeException(e.getMessage());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (10/11/00 1:28:51 PM)
 * @param function cbit.vcell.math.Function
 */
private synchronized void addFunctionToList(AnnotatedFunction function) throws ExpressionException {
	// throw exception if already in list (with same name and different expression)
	for (int i=0;i<annotatedFunctionList.size();i++){
		AnnotatedFunction mathFunction = (AnnotatedFunction)annotatedFunctionList.elementAt(i);
		if (mathFunction.getName().equals(function.getName())){
			// has same name, if different expression, complain, else ignore.
			if (mathFunction.compareEqual(function)){
				// same function added twice, ignore.
				return;
			} else {
				throw new RuntimeException("function '"+function.getName()+" = "+function.getExpression()+"' already defined");
			}
		}
	}

	// attempt to bind function and substitute
	IExpression simExp = function.getSimplifiedExpression();	
	if (simExp == null) {
		IExpression exp = ExpressionFactory.createExpression(function.getExpression());
		exp.bindExpression(this);
		String[] symbols = exp.getSymbols();
		if (symbols != null) {
			for (int i = 0; i < symbols.length; i ++){
				IExpression oldExp = ExpressionFactory.createExpression(symbols[i]);
				IExpression newExp = null;
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
							newExp = mathFunction.getSimplifiedExpression();
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
		simExp = exp.flatten();
		function.setSimplifiedExpression(simExp);	
	}
	simExp.bindExpression(this);
	
	DataSetIdentifier dsi = new DataSetIdentifier(function.getName(),function.getFunctionType(), true);	
	// add the new function to dataSetIndentifierList so that other functions can bind this function
	if (!dataSetIdentifierList.contains(dsi)) {
		dataSetIdentifierList.addElement(dsi);
	}
	//function.setExpression(MathUtilities.substituteFunctions(function.getExpression(), this));
	// Add function to annotatedFunctionList if binding function doesn't fail.
	annotatedFunctionList.addElement((AnnotatedFunction)function);
}


/**
 * Insert the method's description here.
 * Creation date: (3/20/2006 11:37:48 PM)
 * @return double[]
 * @param rawVals double[]
 */
private double[] calcSpaceStats(double[] rawVals,int varIndex,DataSetControllerImpl.SpatialStatsInfo spatialStatsInfo) {
	
    double min = Double.POSITIVE_INFINITY;
    double max = Double.NEGATIVE_INFINITY;
    double mean = 0;
    double wmean = 0;
    double val;
    for(int j=0;j<rawVals.length;j+= 1){
	    val = rawVals[j];
	    if(val < min){min=val;}
	    if(val > max){max=val;}
	    mean+= val;
	    if(spatialStatsInfo.bWeightsValid){wmean+= val*spatialStatsInfo.spaceWeight[varIndex][j];}
    }
    mean/= rawVals.length;
    if(spatialStatsInfo.bWeightsValid){wmean/= spatialStatsInfo.totalSpace[varIndex];}

    return new double[] {min,max,mean,wmean};
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
		ODESimData odeSimData = ODESimData.readODEDataFile(getODEDataFile());
		try {
			dataTimes = odeSimData.extractColumn(odeSimData.findColumn("t"));
		}catch (ExpressionException e){
			e.printStackTrace(System.out);
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

	if (identifier.endsWith("_OUTSIDE") || identifier.endsWith("_INSIDE")){
		int index = identifier.lastIndexOf("_");		
		String realvar = identifier.substring(0, index);
		DataSetIdentifier dsi = getDataSetIdentifier(realvar);
		if (dsi != null) {
			DataSetIdentifier adsi = new DataSetIdentifier(identifier, dsi.getVariableType(), dsi.isFunction());
			dataSetIdentifierList.addElement(adsi);
			return adsi;
		}
	}
	
	return null;
}


/**
 * Insert the method's description here.
 * Creation date: (10/11/00 5:16:06 PM)
 * @return cbit.vcell.math.Function
 * @param name java.lang.String
 */
public AnnotatedFunction getFunction(String identifier) {
	try {
		getFunctionDataIdentifiers();
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
private void getFunctionDataIdentifiers() throws DataAccessException, FileNotFoundException, IOException {
	//
	// add function names to VarName list that is returned
	//
	if (dataSetIdentifierList.size() != 0 && !getIsODEData()){
		File funcFile = getFunctionsFile();
		
		long length = funcFile.length();
		long lastModified = funcFile.lastModified();
		if (length == functionFileLength && lastModified == functionFileLastModified) {
			return;
		}
		
		functionFileLength = length;
		functionFileLastModified = lastModified;

		// remove functions from dataIdentifiers since we are reading functions again
		for (int i = 0; i < dataSetIdentifierList.size(); i ++) {
			DataSetIdentifier dsi = (DataSetIdentifier)dataSetIdentifierList.elementAt(i);
			if (dsi.isFunction()) {
				dataSetIdentifierList.removeElement(dsi);
				i --;
			}
		}
	
		readFunctions();
	}
}


/**
 * Insert the method's description here.
 * Creation date: (10/11/00 5:16:06 PM)
 * @return cbit.vcell.math.Function
 * @param name java.lang.String
 */
public AnnotatedFunction[] getFunctions() {
	try {
		getFunctionDataIdentifiers();
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
private synchronized File getFunctionsFile() throws FileNotFoundException {
	File functionsFile = new File(userDirectory,info.getID()+".functions");
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
	File logFile = new File(userDirectory,info.getID()+".log");
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
	File meshMetricsFile = new File(userDirectory,info.getID()+".meshmetrics");
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
	File meshFile = new File(userDirectory,info.getID()+".mesh");
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
	cbit.vcell.simdata.ODESimData odeSimData = cbit.vcell.simdata.ODESimData.readODEDataFile(file);
	try {
		dataTimes = odeSimData.extractColumn(odeSimData.findColumn("t"));
	}catch (ExpressionException e){
		e.printStackTrace(System.out);
		throw new DataAccessException("error getting data times: "+e.getMessage());
	}
	ODEDataInfo odeDataInfo = new ODEDataInfo(info.getOwner(), info.getID(), lastModified);
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
	//check if stochastic result file exists or not, if so return the file. added 27th July 2006.
	String sFileName = info.getID()+"_0_.stoch";
	File stochFile = new File(userDirectory, sFileName);
	if(stochFile.exists()) return stochFile;
	refreshLogFile();
	if (dataFilenames == null) throw new DataAccessException("ODE data filename not read from logfile");
	File odeFile = new File(userDirectory, dataFilenames[0]);
	if (odeFile.exists()) return odeFile;
	throw new DataAccessException("ODE data file not found");
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
	
	ParticleDataBlock particleDataBlock = new ParticleDataBlock(info.getOwner(), info.getID(), time, particleFile, zipFile);
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
		File zipFile = new File(userDirectory,info.getID()+".zip");
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
	return info;
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.simdata.DataBlock
 * @param user cbit.vcell.server.User
 * @param simID java.lang.String
 */
public synchronized SimDataBlock getSimDataBlock(String varName, double time) throws DataAccessException, IOException {
	refreshLogFile();
	
	try {
		getFunctionDataIdentifiers();
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
	double data[] = dataSet.getData(varName, zipFile);
	int varTypeInt = dataSet.getVariableTypeInteger(varName);
	VariableType variableType = null;
	try {
		variableType = VariableType.getVariableTypeFromInteger(varTypeInt);
	}catch (IllegalArgumentException e){
		e.printStackTrace(System.out);
		System.out.println("invalid varTypeInt = "+varTypeInt+" for variable "+varName+" at time "+time);
		try {
			variableType = CartesianMesh.getVariableTypeFromLength(getMesh(),data.length);
		} catch (MathException ex) {
			ex.printStackTrace(System.out);
			throw new DataAccessException(ex.getMessage());
		}
	}
	PDEDataInfo pdeDataInfo = new PDEDataInfo(info.getOwner(),info.getID(),varName,time,lastModified);
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
public synchronized double[][] getSimDataLineScan(String[] varNames,int[][] indexes,double desiredTime) throws DataAccessException,IOException{

	
	refreshLogFile();

	//try {
		//getFunctionDataIdentifiers();
	//} catch (Exception ex) {
		//ex.printStackTrace(System.out);
	//}

	// Setup parameters for SimDataReader
	boolean[] wantsThisTime = new boolean[this.dataTimes.length];
	Arrays.fill(wantsThisTime,false);
	for(int i=0;i<dataTimes.length;i+= 1){
		if(dataTimes[i] == desiredTime){
			wantsThisTime[i] = true;
			break;
		}
	}

	double[][][] timeResults = getSimDataTimeSeries(varNames,indexes,wantsThisTime);
	double[][] results = new double[varNames.length][];
	for(int i=0;i<varNames.length;i+= 1){
		results[i] = new double[indexes[i].length];
		for( int j=0;j<indexes[i].length;j+= 1){
			results[i][j] = timeResults[0][i][j];
		}
	}
	return results;
	/*
	String[] tempZipFileNames = new String[zipFilenames.length];
	String[] tempSimDataFileNames = new String[dataFilenames.length];
	for(int i=0;i<dataFilenames.length;i+= 1){
		tempZipFileNames[i] = getPDEDataZipFile(dataTimes[i]).getAbsolutePath();
		tempSimDataFileNames[i] = getPDEDataFile(dataTimes[i]).getAbsolutePath();
	}
	SimDataReader sdr = null;

	double[] results = new double[indexes.length];
	
	try{
		sdr =
			new SimDataReader(
				wantsThisTime,
				dataTimes,
				tempZipFileNames,
				tempSimDataFileNames,
				new String[]{varName},
				new int[][]{indexes}
			);
		double[][] currentVals = new double[1][indexes.length];
		int counter = 0;
		while(sdr.hasMoreData()){
			sdr.getNextDataAtCurrentTime(currentVals);
			// Copy data to results
			if(wantsThisTime[counter]){
				System.arraycopy(currentVals[0],0,results,0,indexes.length);
				return results;
			}
			counter+= 1;
		}
		return null;
	}catch(Throwable e){
		throw new DataAccessException(e.getMessage());
	}finally{
		if(sdr != null){
			sdr.close();
		}
	}
	*/
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.simdata.DataBlock
 * @param user cbit.vcell.server.User
 * @param simID java.lang.String
 */
public double[][][] getSimDataTimeSeries(String varNames[],int[][] indexes,boolean[] wantsThisTime) throws DataAccessException,IOException{

	return getSimDataTimeSeries0(varNames,indexes,wantsThisTime,null);
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.simdata.DataBlock
 * @param user cbit.vcell.server.User
 * @param simID java.lang.String
 */
public double[][][] getSimDataTimeSeries(
		String varNames[],
		int[][] indexes,
		boolean[] wantsThisTime,
		DataSetControllerImpl.SpatialStatsInfo spatialStatsInfo) throws DataAccessException,IOException{

	return getSimDataTimeSeries0(varNames,indexes,wantsThisTime,spatialStatsInfo);

}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.simdata.DataBlock
 * @param user cbit.vcell.server.User
 * @param simID java.lang.String
 */
private synchronized double[][][] getSimDataTimeSeries0(
		String varNames[],
		int[][] indexes,
		boolean[] wantsThisTime,
		DataSetControllerImpl.SpatialStatsInfo spatialStatsInfo) throws DataAccessException,IOException{

	
	refreshLogFile();

	try {
		getFunctionDataIdentifiers();
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
	
	String[] tempZipFileNames = null;
	if (bZipFormat2) {
		tempZipFileNames = new String[zipFilenames.length];
	}
	String[] tempSimDataFileNames = new String[dataFilenames.length];
	for(int i=0;i<dataFilenames.length;i+= 1){
		if (bZipFormat2 || bZipFormat1) {
			if(bZipFormat2){
				tempZipFileNames[i] = getPDEDataZipFile(dataTimes[i]).getAbsolutePath();
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
	try{
		sdr =
			new SimDataReader(
				wantsThisTime,
				dataTimes,
				tempZipFileNames,
				tempSimDataFileNames,
				varNames,
				indexes
			);
		int counter = 0;
		resultsCounter = 0;
		while(sdr.hasMoreData()){
			sdr.getNextDataAtCurrentTime(singleTimePointResultsBuffer);
			// Copy data to timeSeries format
			if(wantsThisTime[counter]){
				for(int i=0;i<varNames.length;i+= 1){
					if(spatialStatsInfo != null){
						results[resultsCounter][i] = calcSpaceStats(singleTimePointResultsBuffer[i],i,spatialStatsInfo);
					}else{
						for(int j=0;j<indexes[i].length;j+= 1){
							results[resultsCounter][i][j] = singleTimePointResultsBuffer[i][j];
						}
					}
				}
				resultsCounter+= 1;
			}
			counter+= 1;
		}
		return results;
	}catch(Throwable e){
		throw new DataAccessException(e.getMessage());
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
public synchronized DataIdentifier[] getVarAndFunctionDataIdentifiers() throws IOException, DataAccessException {
	// Is this zip format?
	File zipFile1 = new File(userDirectory,info.getID()+".zip");
	File zipFile2 = new File(userDirectory,info.getID()+"00.zip");
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
					varType = CartesianMesh.getVariableTypeFromLength(mesh,dataSet.getDataLength(varNames[i]));
				}
				dataSetIdentifierList.addElement(new DataSetIdentifier(varNames[i],varType));
			}
		} 

		// always read functions file since functions might change
		getFunctionDataIdentifiers();
	}

	if (getIsODEData() && dataSetIdentifierList.size() == 0){
		ODEDataBlock odeDataBlock = getODEDataBlock();
		ODESimData odeSimData = odeDataBlock.getODESimData();
		int colCount = odeSimData.getColumnDescriptionsCount();
		// assume index=0 is time "t"
		int DATA_OFFSET = 1;
		dataSetIdentifierList.clear();
		for (int i=0;i<(colCount-DATA_OFFSET);i++){
			String varName = odeSimData.getColumnDescriptions(i+DATA_OFFSET).getDisplayName();
			dataSetIdentifierList.addElement(new DataSetIdentifier(varName,VariableType.NONSPATIAL));
		}
	}	
	
	DataIdentifier[] dis = new DataIdentifier[dataSetIdentifierList.size()];
	for (int i = 0; i < dataSetIdentifierList.size(); i ++){
		DataSetIdentifier dsi = (DataSetIdentifier)dataSetIdentifierList.elementAt(i);
		dis[i] = new DataIdentifier(dsi.getName(), dsi.getVariableType(), dsi.isFunction());
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
private void readFunctions() throws FileNotFoundException, IOException {

	Vector annotatedFuncsVector = FunctionFileGenerator.readFunctionsFile(getFunctionsFile());

	//
	// Convert this annotatedfunctionsVector into the field annotatedFunctionsList.
	//

	annotatedFunctionList.clear();
	for (int i = 0; i < annotatedFuncsVector.size(); i++){
		AnnotatedFunction annotatedFunction = (AnnotatedFunction)annotatedFuncsVector.elementAt(i);
		try {
			addFunctionToList(annotatedFunction);
		} catch (ExpressionException e) {
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
	if (stringBuffer.length() != logFileLength){
		System.out.println("<<<SYSOUT ALERT>>>SimResults.readLog(), read "+stringBuffer.length()+" of "+logFileLength+" bytes of log file");
	}
	if ((stringBuffer.toString().startsWith(ODE_DATA_IDENTIFIER)) || (stringBuffer.toString().startsWith(STOCH_DATA_IDENTIFIER))) {
		String newLineDelimiters = "\n\r";
		StringTokenizer lineTokenizer = new StringTokenizer(stringBuffer.toString(),newLineDelimiters);
		isODEData = true;
		String odeIdentifier = lineTokenizer.nextToken(); // br.readLine();
		String odeDataFormat = lineTokenizer.nextToken(); // br.readLine();
		dataFilenames = new String[] { lineTokenizer.nextToken() }; // {br.readLine()};
	} else {
		CommentStringTokenizer st = new CommentStringTokenizer(stringBuffer.toString());
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
private synchronized void readMesh(File meshFile,File membraneMeshMetricsFile) throws FileNotFoundException, IOException, cbit.vcell.math.MathException {
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
	InputStreamReader reader = null;
	try {
		FileInputStream is = new FileInputStream(meshFile);
		reader = new InputStreamReader(is);
		char buffer[] = new char[(int)meshFile.length()];
		int length = reader.read(buffer,0,buffer.length);
		String meshString = new String(buffer,0,length);
		reader.close();
		CommentStringTokenizer meshST = new CommentStringTokenizer(meshString);

		CommentStringTokenizer membraneMeshMetricsST = null;
		if(membraneMeshMetricsFile != null){
			is = new FileInputStream(membraneMeshMetricsFile);
			reader = new InputStreamReader(is);
			buffer = new char[(int)membraneMeshMetricsFile.length()];
			length = reader.read(buffer,0,buffer.length);
			String mmmString = new String(buffer,0,length);
			reader.close();
			membraneMeshMetricsST = new CommentStringTokenizer(mmmString);
		}

		mesh = CartesianMesh.fromTokens(meshST,membraneMeshMetricsST);
	}finally{
		if (reader != null){
			reader.close();
		}
	}
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
	functionFileLastModified = 0;
	functionFileLength = 0;
}


/**
 * Insert the method's description here.
 * Creation date: (10/11/00 1:28:51 PM)
 * @param function cbit.vcell.math.Function
 */
public synchronized void removeFunction(AnnotatedFunction function) {
	try {
		getFunctionDataIdentifiers();
	} catch (Exception ex) {
		ex.printStackTrace(System.out);
	}
	
	boolean bFoundAndRemoved = false;
	for (int i=0;i<annotatedFunctionList.size();i++){
		AnnotatedFunction annotatedFunc = (AnnotatedFunction)annotatedFunctionList.elementAt(i);
		if (annotatedFunc.getName().equals(function.getName())){
			annotatedFunctionList.removeElementAt(i);
			bFoundAndRemoved = true;
			// We should also update varNames and dataIdentifiers here, since it cannot be done elsewhere.

			// Update dataSetIdentifierList. check if function  exists, remove it.
			for (int j = 0; j < dataSetIdentifierList.size(); j++) {
				DataSetIdentifier dsi = (DataSetIdentifier)dataSetIdentifierList.elementAt(j);
				if (dsi.getName().equals(annotatedFunc.getName())) {
					dataSetIdentifierList.removeElement(dsi);
				}				
			}
		}
	}

	if (bFoundAndRemoved) {
		// if function was found in annotatedFuncslist and removed, the function file has to be updated.
		AnnotatedFunction annotatedFunctions[] = new AnnotatedFunction[annotatedFunctionList.size()];
		annotatedFunctionList.copyInto(annotatedFunctions);
		try {
			FunctionFileGenerator ffg = new FunctionFileGenerator(getFunctionsFile().getPath(), annotatedFunctions);
			ffg.generateFunctionFile();

			// my lastModified and length should be changed because I just rewrote the file.
			File funcFile = getFunctionsFile();		
			functionFileLength = funcFile.length();
			functionFileLastModified = funcFile.lastModified();			
		} catch (Exception e) {
			e.printStackTrace(System.out);
			throw new RuntimeException(e.getMessage());
		}
		return;
	}

	//
	// throw exception if not already in list (with same name)
	//
	throw new RuntimeException("can't remove function "+function.getName()+", not found");
}
}