/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.simdata;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.Vector;
import java.util.zip.ZipEntry;

import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.vcell.util.BeanUtils;
import org.vcell.util.Compare;
import org.vcell.util.DataAccessException;
import org.vcell.util.Extent;
import org.vcell.util.FileUtils;
import org.vcell.util.ISize;
import org.vcell.util.Origin;
import org.vcell.util.VCAssert;
import org.vcell.util.document.ExternalDataIdentifier;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.SimResampleInfoProvider;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDataIdentifier;
import org.vcell.vis.io.ChomboFiles;
import org.vcell.vis.io.ComsolSimFiles;
import org.vcell.vis.io.MovingBoundarySimFiles;
import org.vcell.vis.io.VCellSimFiles;

import cbit.image.VCImage;
import cbit.image.VCImageUncompressed;
import cbit.vcell.field.FieldFunctionArguments;
import cbit.vcell.geometry.RegionImage;
import cbit.vcell.math.CompartmentSubDomain;
import cbit.vcell.math.Constant;
import cbit.vcell.math.FieldFunctionDefinition;
import cbit.vcell.math.FilamentSubDomain;
import cbit.vcell.math.Function;
import cbit.vcell.math.InsideVariable;
import cbit.vcell.math.MathException;
import cbit.vcell.math.MemVariable;
import cbit.vcell.math.MembraneSubDomain;
import cbit.vcell.math.OutsideVariable;
import cbit.vcell.math.PointSubDomain;
import cbit.vcell.math.ReservedMathSymbolEntries;
import cbit.vcell.math.ReservedVariable;
import cbit.vcell.math.SubDomain;
import cbit.vcell.math.Variable;
import cbit.vcell.math.Variable.Domain;
import cbit.vcell.math.VariableType;
import cbit.vcell.math.VolVariable;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.mongodb.VCMongoMessage;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.simdata.DataOperation.DataProcessingOutputDataValuesOP;
import cbit.vcell.simdata.DataOperation.DataProcessingOutputDataValuesOP.DataIndexHelper;
import cbit.vcell.simdata.DataOperation.DataProcessingOutputDataValuesOP.TimePointHelper;
import cbit.vcell.simdata.DataOperation.DataProcessingOutputInfoOP;
import cbit.vcell.simdata.DataOperationResults.DataProcessingOutputDataValues;
import cbit.vcell.simdata.DataOperationResults.DataProcessingOutputInfo;
import cbit.vcell.solver.AnnotatedFunction;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationJob;
import cbit.vcell.solver.SolverUtilities;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationDataIdentifierOldStyle;
import cbit.vcell.solver.ode.ODESimData;
import cbit.vcell.solvers.CartesianMesh;
import cbit.vcell.solvers.CartesianMeshChombo;
import cbit.vcell.solvers.CartesianMeshChombo.FeaturePhaseVol;
import cbit.vcell.solvers.CartesianMeshMovingBoundary;
import cbit.vcell.solvers.FunctionFileGenerator;
import cbit.vcell.util.AmplistorUtils;
import cbit.vcell.util.AmplistorUtils.AmplistorCredential;
import cbit.vcell.xml.XmlHelper;
import cbit.vcell.xml.XmlParseException;
/**
 * This type was created in VisualAge.
 */
public class SimulationData extends VCData {
	public enum SolverDataType
	{
		MBSData,
		COMSOL
	}
	
	public static class AmplistorHelper{
		private File userDirectory;
		private VCDataIdentifier ahvcDataId;
		private KeyValue simulationKey;
		private int jobIndex = 0;
		private TreeSet<String> amplistorNotfoundSet = new TreeSet<String>();
		boolean bNonSpatial = false;
		private SolverDataType solverDataType = null;
		
		SimDataAmplistorInfo simDataAmplistorInfo;
		public AmplistorHelper(VCDataIdentifier argVCDataID, File primaryUserDir, File secondaryUserDir,SimDataAmplistorInfo simDataAmplistorInfo) throws FileNotFoundException{
			this.simDataAmplistorInfo = simDataAmplistorInfo;
			if(primaryUserDir == null || !primaryUserDir.exists() || !primaryUserDir.isDirectory()){
				throw new FileNotFoundException("PrimaryUserDir required and it must exist and be a directory");
			}
			if(secondaryUserDir != null && (!secondaryUserDir.exists() || !secondaryUserDir.isDirectory())){
				throw new FileNotFoundException("If secondaryUserDir defined it must exist and be a directory");
			}
//			this.amplistorVCellUsersRootPath = amplistorVCellUserRootPath;
			String logFileName = null;
			String logFileNameOldStyle = null;
			if(argVCDataID instanceof VCSimulationDataIdentifier){
				simulationKey = ((VCSimulationDataIdentifier)argVCDataID).getSimulationKey();
				jobIndex = ((VCSimulationDataIdentifier)argVCDataID).getJobIndex();
				logFileName = createCanonicalSimLogFileName(simulationKey, jobIndex, false);
				logFileNameOldStyle = createCanonicalSimLogFileName(simulationKey, 0, true);
			}else if(argVCDataID instanceof VCSimulationDataIdentifierOldStyle){
				simulationKey = ((VCSimulationDataIdentifierOldStyle)argVCDataID).getSimulationKey();
				logFileName = createCanonicalSimLogFileName(simulationKey, 0, false);
				logFileNameOldStyle = createCanonicalSimLogFileName(simulationKey, 0, true);
			}else if(argVCDataID instanceof ExternalDataIdentifier){
				simulationKey = ((ExternalDataIdentifier)argVCDataID).getKey();
				logFileName = createCanonicalSimLogFileName(simulationKey, 0, false);
				logFileNameOldStyle = createCanonicalSimLogFileName(((ExternalDataIdentifier)argVCDataID).getKey(), 0, true);
			}else{
				throw new IllegalArgumentException("AmplistorHelper unexpected VCDataIdentifier type "+argVCDataID.getClass().getName());
			}
			boolean bNotFound = false;
			if(new File(primaryUserDir,logFileName).exists()){
				this.userDirectory = primaryUserDir;
				this.ahvcDataId = argVCDataID;
			}else if(new File(primaryUserDir,logFileNameOldStyle).exists()){
				this.userDirectory = primaryUserDir;
				this.ahvcDataId = convertVCDataIDToOldStyle(argVCDataID);
			}else if(secondaryUserDir != null && new File(secondaryUserDir,logFileName).exists()){
				this.userDirectory = secondaryUserDir;
				this.ahvcDataId = argVCDataID;
			}else if(secondaryUserDir != null && new File(secondaryUserDir,logFileNameOldStyle).exists()){
				this.userDirectory = secondaryUserDir;
				this.ahvcDataId = convertVCDataIDToOldStyle(argVCDataID);
			}else if(simDataAmplistorInfo != null && simDataAmplistorInfo.getAmplistorVCellUsersRootPath() != null){
				try{
					if(amplistorFileExists(simDataAmplistorInfo,argVCDataID.getOwner().getName(), logFileName)){
						this.userDirectory = primaryUserDir;//use primary by default for amplistor
						this.ahvcDataId = argVCDataID;
					}else if(amplistorFileExists(simDataAmplistorInfo,argVCDataID.getOwner().getName(), logFileNameOldStyle)){
						this.userDirectory = primaryUserDir;//use primary by default for amplistor
						this.ahvcDataId = convertVCDataIDToOldStyle(argVCDataID);
					}else{
						bNotFound = true;
					}
				}catch(Exception e){
					bNotFound = true;
					e.printStackTrace();
			}
			}else{
				bNotFound = true;
			}
			if(bNotFound){
				throw new FileNotFoundException(
					"simulation data for [" + argVCDataID + "] newStyle="+logFileName+" oldStyle="+logFileNameOldStyle+" not exist in primary " + primaryUserDir + " dir or secondary " + secondaryUserDir+" dir or Archive server.");
			}else{
				FileInputStream fis = null;
				try{
					File logfile = getLogFile();
					fis = new FileInputStream(logfile);
					String log3Bytes = new String(new char[] {(char)fis.read(),(char)fis.read(),(char)fis.read()});
					if(log3Bytes.equals("IDA") || log3Bytes.equals("ODE") || log3Bytes.equals("NFS")){
						bNonSpatial = true;
					}
					else if (SolverDataType.MBSData.name().startsWith(log3Bytes))
					{
						solverDataType = SolverDataType.MBSData;
					}
					else if (SolverDataType.COMSOL.name().startsWith(log3Bytes))
					{
						solverDataType = SolverDataType.COMSOL;
					}
				}catch(Exception e){
					e.printStackTrace();
				}finally{
					if(fis != null){try{fis.close();}catch(Exception e){e.printStackTrace();}}
				}
			}

		}
		public static boolean hasLogFileAnywhere(SimDataAmplistorInfo simDataAmplistorInfo,KeyValue simKey,User user,int jobIndex,File destinationUserDir) throws Exception{
			HttpURLConnection urlCon = null;
			try{
				String simLogFileName = SimulationData.createCanonicalSimLogFileName(simKey, jobIndex, false);
				File logfile = new File(destinationUserDir,simLogFileName);
				if(logfile.exists()){
					return true;
				}
				return amplistorFileExists(simDataAmplistorInfo, user.getName(), simLogFileName);
			}finally{
				if(urlCon != null){try{urlCon.disconnect();}catch(Exception e){e.printStackTrace();}}
			}
		}

		public boolean isNonSpatial(){
			return bNonSpatial;
		}
		private static VCDataIdentifier convertVCDataIDToOldStyle(VCDataIdentifier argVCDataID){
			if(argVCDataID instanceof VCSimulationDataIdentifier){
				return VCSimulationDataIdentifierOldStyle.createVCSimulationDataIdentifierOldStyle((VCSimulationDataIdentifier)argVCDataID);
			}else if(argVCDataID instanceof VCSimulationDataIdentifierOldStyle){
				return argVCDataID;
			}else{
				throw new IllegalArgumentException("Convert unexpected VCDataIdentifier type "+argVCDataID.getClass().getName());
			}
		}

		private static boolean amplistorFileExists(SimDataAmplistorInfo simDataAmplistorInfo,String userid,String fileName) throws Exception{
			String urlStr = simDataAmplistorInfo.getAmplistorVCellUsersRootPath()+"/"+userid+"/"+fileName;
			return AmplistorUtils.bFileExists(new URL(urlStr), simDataAmplistorInfo.getAmplistorCredential());
		}
		public VCDataIdentifier getVCDataiDataIdentifier(){
			return ahvcDataId;
		}
		private int getJobIndex(){
			return jobIndex;
		}
		private KeyValue getsimulationKey(){
			return simulationKey;
		}
		public File getFunctionsFile(boolean bFirst){
			return getFile(SimulationData.createCanonicalFunctionsFileName(getsimulationKey(),(bFirst?0:getJobIndex()), isOldStyle()));
		}
		public File getLogFile(){
			return getFile(SimulationData.createCanonicalSimLogFileName(getsimulationKey(),getJobIndex(), isOldStyle()));
		}
		public File getSmoldynOutputFile(int timeIndex){
			return getFile(SimulationData.createCanonicalSmoldynOutputFileName(((VCSimulationDataIdentifier)getVCDataiDataIdentifier()).getSimulationKey(), ((VCSimulationDataIdentifier)getVCDataiDataIdentifier()).getJobIndex(), timeIndex));
		}
		public File getMeshMetricsFile(){
			return getFile(SimulationData.createCanonicalMeshMetricsFileName(getsimulationKey(),getJobIndex(), isOldStyle()));
		}
		public File getMovingBoundaryOutputFile(){
			return getFile(SimulationData.createCanonicalMovingBoundaryOutputFileName(getsimulationKey(),getJobIndex(), isOldStyle()));
		}
		public File getComsolOutputFile(){
			return getFile(SimulationData.createCanonicalComsolOutputFileName(getsimulationKey(),getJobIndex(), isOldStyle()));
		}
		public File getSubdomainFile(){
			return getFile(SimulationData.createCanonicalSubdomainFileName(getsimulationKey(),getJobIndex(), isOldStyle()));
		}
		public File getMeshFile(boolean bHDF5){
			String meshFileName = SimulationData.createCanonicalMeshFileName(getsimulationKey(),getJobIndex(), isOldStyle())+(bHDF5?".hdf5":"");
			if (solverDataType == SolverDataType.MBSData)
			{
				meshFileName = createSimIDWithJobIndex(getsimulationKey(), getJobIndex(), isOldStyle()) + ".h5";
			}else if (solverDataType == SolverDataType.COMSOL){
				meshFileName = createSimIDWithJobIndex(getsimulationKey(), getJobIndex(), isOldStyle()) + ".comsoldat";
			}
			if(isNonSpatial()){
				//this never exists, no need to look up in amplistor
				return getLocalFilePath(meshFileName);
			}
			return getFile(meshFileName);
		}
		public File getZipFile(boolean bHDF5,Integer zipIndex){
			String zipFileName = SimulationData.createCanonicalSimZipFileName(getsimulationKey(),zipIndex,getJobIndex(), isOldStyle(),bHDF5);
			if(isNonSpatial() || (zipIndex == null && !isOldStyle()) || (zipIndex != null && isOldStyle())){
				//this never exists, no need to look up in amplistor
				return getLocalFilePath(zipFileName);
			}
			return getFile(zipFileName);
		}
		public File getPostProcessFile(){
			return getFile(SimulationData.createCanonicalPostProcessFileName(getVCDataiDataIdentifier()));
		}
		public File getFieldDataFile(SimResampleInfoProvider simResampleInfoProvider,FieldFunctionArguments fieldFunctionArguments){
			return getFile(SimulationData.createCanonicalResampleFileName(simResampleInfoProvider,fieldFunctionArguments));
		}
		public File getFile(String fileName){
			File file = getLocalFilePath(fileName);
			xferAmplistor(file);
			return file;
		}
		public File getLocalFilePath(String fileName){
			return new File(userDirectory,fileName);
		}
		private void xferAmplistor(File file){
			if(simDataAmplistorInfo != null && !amplistorNotfoundSet.contains(file.getName()) && !file.exists()){//we may have already xferred it
				try{
					AmplistorUtils.getObjectDataPutInFile(
						simDataAmplistorInfo.amplistorVCellUsersRootPath+"/"+getVCDataiDataIdentifier().getOwner().getName()+"/"+file.getName(),
						simDataAmplistorInfo.amplistorCredential,
						file);
				}catch(Exception e){
					if(e instanceof FileNotFoundException){
						amplistorNotfoundSet.add(file.getName());
						if(getVCDataiDataIdentifier() != null && getVCDataiDataIdentifier().getOwner() != null){
							System.out.println("FileNotFoundException: SimulationData.AmplistorHelper.xferAmplistor(...) "+(getVCDataiDataIdentifier().getOwner().getName()+"/"+file.getName())+".  This may not be an error.");
						}
					}else{
						e.printStackTrace();
					}
				}
			}
		}
		private boolean isOldStyle(){
			return getVCDataiDataIdentifier() instanceof VCSimulationDataIdentifierOldStyle;
		}
		public File getSimTaskXMLFile() {
			return getFile(SimulationData.createCanonicalSimTaskXMLFilePathName(getsimulationKey(),getJobIndex(), isOldStyle()));
		}
	}

	private final static long SizeInBytes = 2000;  // a guess

	AmplistorHelper amplistorHelper;
	private VCDataIdentifier vcDataId = null;

	private int chomboFileIterationIndices[] = null;
	private double dataTimes[] = null;
	private String dataFilenames[] = null;
	private String zipFilenames[] = null;
	/**
	 * index times to indexes in {@link #dataFilenames} array
	 */
	private Map<Double,Integer> pdeDataMap = null;
	/**
	 * Smoldyn particle file names
	 */
	private String particleFilenames[] = null;

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
	private boolean isRulesData = false;

	private boolean bZipFormat2 = false;
	private boolean bZipFormat1 = false;
	private int odeKeepMost = 0;
	private String odeIdentifier = null;


	private static Logger lg = Logger.getLogger(SimulationData.class);
	public static class SimDataAmplistorInfo {
		private String amplistorVCellUsersRootPath;
		private AmplistorCredential amplistorCredential;
		public SimDataAmplistorInfo(String amplistorVCellUsersRootPath,AmplistorCredential amplistorCredential) {
			this.amplistorVCellUsersRootPath = amplistorVCellUsersRootPath;
			this.amplistorCredential = amplistorCredential;
		}
		public String getAmplistorVCellUsersRootPath() {
			return amplistorVCellUsersRootPath;
		}
		public AmplistorCredential getAmplistorCredential() {
			return amplistorCredential;
		}
	}
/**
 * SimResults constructor comment.
 */
public SimulationData(VCDataIdentifier argVCDataID, File primaryUserDir, File secondaryUserDir,SimDataAmplistorInfo simDataAmplistorInfo) throws IOException, DataAccessException {
	VCMongoMessage.sendTrace("SimulationData.SimulationData() <<ENTER>>");
	amplistorHelper = new AmplistorHelper(argVCDataID, primaryUserDir, secondaryUserDir,simDataAmplistorInfo);
	this.vcDataId = amplistorHelper.getVCDataiDataIdentifier();
	VCMongoMessage.sendTrace("SimulationData.SimulationData() getting var and function identifiers");
	getVarAndFunctionDataIdentifiers(null);
	VCMongoMessage.sendTrace("SimulationData.SimulationData() <<EXIT>>");
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
	if (!function.isPostProcessFunction()){
		function.getExpression().bindExpression(this);
	}
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
		exp = SolverUtilities.substituteSizeAndNormalFunctions(exp, function.getFunctionType().getVariableDomain());
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
			try{
			return getPDEDataFile(timepoint).lastModified();
			}catch(DataAccessException e){
				//PostProcess times don't always match exactly
				if(getDataProcessingOutputSourceFileHDF5().exists()){
					return getDataProcessingOutputSourceFileHDF5().lastModified();
				}else{
					throw e;
				}
			}
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
	String varName = Variable.getNameFromCombinedIdentifier(identifier);
	for (int i = 0; i < dataSetIdentifierList.size();i ++){
		DataSetIdentifier dsi = (DataSetIdentifier)dataSetIdentifierList.elementAt(i);
		if (dsi.getName().equals(varName)){
			return dsi;
		}
	}
	return null;
}

public synchronized double[] getDataTimesPostProcess(OutputContext outputContext) throws DataAccessException {
	refreshDataProcessingOutputInfo(outputContext);
	return dataProcessingOutputInfo.getVariableTimePoints();
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
			int timeIndex = odeSimData.findColumn("t");
			if (timeIndex < 0){
				timeIndex = 0; // ok, time probably doesn't exist like for a histogram dataset ... just use first index.
				LG.warn("cannot find time ('t') column in ODESimData, assuming first column holds the independent variable (maybe not time)");
			}
			dataTimes = odeSimData.extractColumn(timeIndex);
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

	entry = ReservedMathSymbolEntries.getEntry(identifier,false);
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
	return null;
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
		Iterator<DataSetIdentifier> iter = dataSetIdentifierList.iterator();
		while (iter.hasNext()) {
			DataSetIdentifier dsi = iter.next();
			if (dsi.isFunction()) {
				iter.remove();
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
	File functionsFile = amplistorHelper.getFunctionsFile(true);
	if (functionsFile.exists()){
		return functionsFile;
	}else{
		throw new FileNotFoundException("functions file "+functionsFile.getPath()+" not found");
	}
}


private synchronized File getJobFunctionsFile() throws FileNotFoundException {
	File functionsFile = null;
	functionsFile = amplistorHelper.getFunctionsFile(false);
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
public File getLogFilePrivate() throws FileNotFoundException {
	File logFile = getLogFile();
	VCMongoMessage.sendTrace("SimulationData.getLogFile() <<ENTER>> calling logFile.exists()");
	if (logFile.exists()){
		VCMongoMessage.sendTrace("SimulationData.getLogFile() <<EXIT>> file found");
		return logFile;
	}else{
		VCMongoMessage.sendTrace("SimulationData.getLogFile() <<EXIT>> file not found");
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
	File meshMetricsFile = amplistorHelper.getMeshMetricsFile();
	VCMongoMessage.sendTrace("SimulationData.getMembraneMeshMetricsFile() <<ENTER>> calling meshMetricsFile.exists()");
	if (meshMetricsFile.exists()){
		VCMongoMessage.sendTrace("SimulationData.getMembraneMeshMetricsFile() <<ENTER>> file found");
		return meshMetricsFile;
	}
	VCMongoMessage.sendTrace("SimulationData.getMembraneMeshMetricsFile() <<ENTER>> file not found");
	return null;
}
/**
 * This method was created in VisualAge.
 * @return java.io.File
 * @param user cbit.vcell.server.User
 * @param simID java.lang.String
 */
private synchronized File getSubdomainFilePrivate() throws FileNotFoundException {
	File subdomainFile = getSubdomainFile();
	VCMongoMessage.sendTrace("SimulationData.getSubdomainFile() <<ENTER>> calling subdomain.exists()");
	if (subdomainFile.exists()){
		VCMongoMessage.sendTrace("SimulationData.getSubdomainFile() <<ENTER>> file found");
		return subdomainFile;
	}
	VCMongoMessage.sendTrace("SimulationData.getSubdomainFile() <<ENTER>> file not found");
	return null;
}

public synchronized CartesianMesh getPostProcessingMesh(String varName,OutputContext outputContext) throws DataAccessException{
	refreshDataProcessingOutputInfo(outputContext);
	return createPostProcessingMesh(varName, outputContext);
}
private CartesianMesh createPostProcessingMesh(String varName,OutputContext outputContext) throws DataAccessException{
	try{
		refreshDataProcessingOutputInfo(outputContext);
		//create mesh here because we know the variablename
		ISize varISize = dataProcessingOutputInfo.getVariableISize(varName);
		Extent varExtent = dataProcessingOutputInfo.getVariableExtent(varName);
		Origin varOrigin = dataProcessingOutputInfo.getVariableOrigin(varName);
		VCImage vcImage = new VCImageUncompressed(null,
			new byte[varISize.getXYZ()],
			varExtent,
			varISize.getX(),
			varISize.getY(),
			varISize.getZ());
		RegionImage regionImage = new RegionImage(vcImage,
			1+(varISize.getY()>0?1:0)+(varISize.getZ()>0?1:0),
			varExtent,
			varOrigin,
			RegionImage.NO_SMOOTHING);
		return CartesianMesh.createSimpleCartesianMesh(
			dataProcessingOutputInfo.getVariableOrigin(varName),
			varExtent,
			varISize, regionImage);
	}catch(DataAccessException dae){
		throw dae;
	}catch(Exception e){
		throw new DataAccessException(e.getMessage(),(e.getCause()==null?e:e.getCause()));
	}
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
	VCMongoMessage.sendTrace("SimulationData.getMeshFile() <<BEGIN>>");
	File meshFile = amplistorHelper.getMeshFile(false);
	if (meshFile.exists()){
		VCMongoMessage.sendTrace("SimulationData.getMeshFile() <<EXIT-meshfile>>");
		return meshFile;
	}else{
		meshFile = amplistorHelper.getMeshFile(true);
		if(meshFile.exists()){
			return meshFile;
		}
		VCMongoMessage.sendTrace("SimulationData.getMeshFile() <<EXIT-mesh file not found>>");
		throw new FileNotFoundException("mesh file "+meshFile.getPath()+" not found");
	}
}


/**
 * Insert the method's description here.
 * Creation date: (1/14/00 2:28:47 PM)
 * @return cbit.vcell.simdata.ODEDataBlock
 */
public synchronized ODEDataBlock getODEDataBlock() throws DataAccessException, IOException {
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
		else if (odeIdentifier.equals(NFSIM_DATA_IDENTIFIER))
		{
			odeSimData = ODESimData.readNFSIMDataFile(vcDataId, getODEDataFile(), getJobFunctionsFile());
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
	VCAssert.assertFalse(odeSimData == null, "should have returned null already");
	return new ODEDataBlock(odeDataInfo, odeSimData);
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
	File odeFile = amplistorHelper.getFile(dataFilenames[0]);
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
		return particleDataBlock;
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
	Integer index = pdeDataMap.get(time);
	if (index != null) {
		String pfName = particleFilenames[index];
		File particleFile = amplistorHelper.getLocalFilePath(pfName);
		if (particleFile.exists()){
			return particleFile;
		}
	}
	return null;
	/*
	File simFile = getPDEDataFile(time);
	File particleFile = amplistorHelper.getLocalFilePath(simFile.getName() + PARTICLE_DATA_EXTENSION);
	if (particleFile.exists()){
		return particleFile;
	}else{
		return null;
	}
	*/
}

/**
 * build {@link #pdeDataMap}
 * @throws DataAccessException
 */
private synchronized void indexPDEdataTimes( ) throws DataAccessException {
	VCAssert.assertFalse(isODEData, "PDE only");
	final int dataLength = dataFilenames.length;
	if (dataTimes.length != dataLength){
		throw new DataAccessException("dataTime array and dataFilename Array are corrupted");
	}
	pdeDataMap = new HashMap<Double, Integer>(dataTimes.length);
	for (int i=0;i<dataTimes.length;i++){
		pdeDataMap.put(dataTimes[i], i);
	}
	KeyValue kv = amplistorHelper.getsimulationKey();
	int jobIndex = amplistorHelper.getJobIndex();
	String firstParticleFile = createCanonicalSmoldynOutputFileName(kv, jobIndex, 1);
	File f = amplistorHelper.getFile(firstParticleFile);
	if (f != null) {
		particleFilenames = new String[dataLength];
		for (int i = 0; i < dataLength; i++) {
			final int smoldynIndex = i + 1; //smoldyn one-indexed
			particleFilenames[i] = createCanonicalSmoldynOutputFileName(kv, jobIndex, smoldynIndex);
		}
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
	Integer index = pdeDataMap.get(time);
	if (index != null) {
		String dataFileName = FilenameUtils.getName( dataFilenames[index]);
			return amplistorHelper.getLocalFilePath(dataFileName);
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
		dataSet.read(pdeFile, zipFile, amplistorHelper.solverDataType);
	} catch (IOException ex) {
		ex.printStackTrace();
		throw new DataAccessException("data at time="+time+" read error",ex);
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
		File zipFile = getZipFile(false, null);
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
	Integer index = pdeDataMap.get(time);
	if (index != null) {
		String zipFileName = FilenameUtils.getName(zipFilenames[index]);
			File zipFile = amplistorHelper.getFile(zipFileName);//new File(userDirectory,zipFileName);
			if (zipFile.exists()) {
				return zipFile;
			} else {
				return null;
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


private double extractClosestPostProcessTime(double vcellTimePoint){
	double closestPostProcessTimePoint = dataProcessingOutputInfo.getVariableTimePoints()[0];
	for (int i = 0; i < dataProcessingOutputInfo.getVariableTimePoints().length; i++) {
		double postProcessTimePoint = dataProcessingOutputInfo.getVariableTimePoints()[i];
		if (postProcessTimePoint == vcellTimePoint){
			return vcellTimePoint;
		}else if(Math.abs(vcellTimePoint-postProcessTimePoint) < Math.abs(vcellTimePoint-closestPostProcessTimePoint)){
			closestPostProcessTimePoint = postProcessTimePoint;
		}
	}
	return closestPostProcessTimePoint;
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
	try{
		if(isPostProcessing(outputContext, varName)){
			PDEDataInfo pdeDataInfo = new PDEDataInfo(vcDataId.getOwner(),vcDataId.getID(),varName,time,lastDataProcessingOutputInfoTime);
			DataProcessingOutputDataValuesOP dataProcessingOutputDataValuesOP =
				new DataProcessingOutputDataValuesOP(vcDataId, varName, TimePointHelper.createSingleTimeTimePointHelper(extractClosestPostProcessTime(time)), DataIndexHelper.createAllDataIndexesDataIndexHelper(), outputContext, null);
			DataProcessingOutputDataValues dataProcessingOutputDataValues =
				(DataProcessingOutputDataValues)DataSetControllerImpl.getDataProcessingOutput(dataProcessingOutputDataValuesOP, getDataProcessingOutputSourceFileHDF5());
			return new SimDataBlock(pdeDataInfo,dataProcessingOutputDataValues.getDataValues()[0]/*1 time only*/,VariableType.POSTPROCESSING);
		}
	}catch(Exception e){
		//ignore
		e.printStackTrace();
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
	double data[] = dataSet.getData(varNameInDataSet, zipFile, time, amplistorHelper.solverDataType);
	int varTypeInt = dataSet.getVariableTypeInteger(varNameInDataSet);
	VariableType variableType = null;
	try {
		variableType = VariableType.getVariableTypeFromInteger(varTypeInt);
	}catch (IllegalArgumentException e){
		e.printStackTrace(System.out);
		System.out.println("invalid varTypeInt = "+varTypeInt+" for variable "+varName+" at time "+time);
		try {
			variableType = SimulationData.getVariableTypeFromLength(getMesh(),data.length);
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

	int resultsCounter = 0;
	for(int i=0;i<wantsThisTime.length;i+= 1){
		if(wantsThisTime[i]){
			resultsCounter+= 1;
		}
	}
	final int NUM_STATS = 4;//min,max,mean,wmean
	//Create results buffer
	double[][][] results = new double[resultsCounter][][];//[timePoints][varNames][dataIndexes]

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
	try{
		if(isPostProcessing(outputContext, varNames[0])){
			double[] specificTimePoints = new double[results.length];
			int counter = 0;
			for (int i = 0; i < dataProcessingOutputInfo.getVariableTimePoints().length; i++) {
				if(wantsThisTime[i]){
					specificTimePoints[counter] = dataProcessingOutputInfo.getVariableTimePoints()[i];
					counter++;
				}
			}
//			PDEDataInfo pdeDataInfo = new PDEDataInfo(vcDataId.getOwner(),vcDataId.getID(),varName,time,lastDataProcessingOutputInfoTime);
			for (int i = 0; i < varNames.length; i++) {
				DataProcessingOutputDataValuesOP dataProcessingOutputDataValuesOP =
					new DataProcessingOutputDataValuesOP(vcDataId, varNames[i],
						TimePointHelper.createSpecificTimePointHelper(specificTimePoints), DataIndexHelper.createSpecificDataIndexHelper(indexes[i]), outputContext, null);
				DataProcessingOutputDataValues dataProcessingOutputDataValues =
					(DataProcessingOutputDataValues)DataSetControllerImpl.getDataProcessingOutput(dataProcessingOutputDataValuesOP, getDataProcessingOutputSourceFileHDF5());
				for (int j = 0; j < specificTimePoints.length; j++) {
					results[j][i] = dataProcessingOutputDataValues.getDataValues()[j];
				}
			}
			return results;
		}
	}catch(Exception e){
		//ignore
		e.printStackTrace();
	}

	String varNamesInDataSet[] = new String[varNames.length];
	for (int i = 0; i < varNamesInDataSet.length; i++) {
		varNamesInDataSet[i] = getDataSetIdentifier(varNames[i]).getQualifiedName();
	}
	// Setup parameters for SimDataReader
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
			tempSimDataFileNames[i] = amplistorHelper.getFile(dataFilenames[i]).getName();//userDirectory.getAbsolutePath()+"\\"+dataFilenames[i];//getPDEDataFile(dataTimes[i]).getAbsolutePath();
		}

	}
	SimDataReader sdr = null;



	double[][] singleTimePointResultsBuffer = new double[varNamesInDataSet.length][];
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
				varNamesInDataSet,
				indexes,
				isChombo()
			);
		int counter = 0;
		int progressCounter = 0;
		while(sdr.hasMoreData()){
			sdr.getNextDataAtCurrentTime(singleTimePointResultsBuffer);
			// Copy data to timeSeries format
			if(wantsThisTime[counter]){
				for(int i=0;i<varNamesInDataSet.length;i+= 1){
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
	boolean bIsChombo = false;
	try {
		bIsChombo = isChombo();
	}catch (FileNotFoundException e){
		e.printStackTrace(System.out);
	}
	File zipFile1 = getZipFile(bIsChombo, null);
	File zipFile2 = getZipFile(bIsChombo, 0);
	bZipFormat1 = false;
	bZipFormat2 = false;
	if (zipFile1.exists()) {
		bZipFormat1 = true;
	} else if (zipFile2.exists()){
		bZipFormat2 = true;
	}

	refreshLogFile();
	if (!isComsol()){
		try {
			refreshMeshFile();
		}catch (MathException e){
			e.printStackTrace(System.out);
			throw new DataAccessException(e.getMessage());
		}
	}
	
	if (!isRulesData && !getIsODEData() && !isComsol() && dataFilenames != null) {
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
				}catch (IllegalArgumentException e){
					if (LG.isEnabledFor(Level.WARN)) {
						LG.warn("Exception typing " + varNames[i] + " has unsupported type " + varTypeInts[i] + ": " + e.getMessage());
					}
					varType = SimulationData.getVariableTypeFromLength(mesh,dataSet.getDataLength(varNames[i]));
				}
				Domain domain = Variable.getDomainFromCombinedIdentifier(varNames[i]);
				String varName = Variable.getNameFromCombinedIdentifier(varNames[i]);
				dataSetIdentifierList.addElement(new DataSetIdentifier(varName,varType,domain));
			}
			refreshDataProcessingOutputInfo(outputContext);
			if(dataProcessingOutputInfo != null){
				for (int i = 0; i < dataProcessingOutputInfo.getVariableNames().length; i++) {
					if(dataProcessingOutputInfo.getPostProcessDataType(dataProcessingOutputInfo.getVariableNames()[i]).equals(DataProcessingOutputInfo.PostProcessDataType.image)){
						dataSetIdentifierList.addElement(new DataSetIdentifier(dataProcessingOutputInfo.getVariableNames()[i],VariableType.POSTPROCESSING,null));
					}
				}
			}
		}

		// always read functions file since functions might change
		getFunctionDataIdentifiers(outputContext);
	}

	if ((isRulesData || getIsODEData()) && dataSetIdentifierList.size() == 0){
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

	if (isComsol() && dataSetIdentifierList.size() == 0){
		ComsolSimFiles comsolSimFiles = getComsolSimFiles();
		if (comsolSimFiles.simTaskXMLFile!=null){
			try {
				String xmlString = FileUtils.readFileToString(comsolSimFiles.simTaskXMLFile);
				SimulationTask simTask = XmlHelper.XMLToSimTask(xmlString);
				Enumeration<Variable> variablesEnum = simTask.getSimulation().getMathDescription().getVariables();
				while (variablesEnum.hasMoreElements()){
					Variable var = variablesEnum.nextElement();
					if (var instanceof VolVariable){
						dataSetIdentifierList.addElement(new DataSetIdentifier(var.getName(), VariableType.VOLUME, var.getDomain()));
					}else if (var instanceof MemVariable){
						dataSetIdentifierList.addElement(new DataSetIdentifier(var.getName(), VariableType.MEMBRANE, var.getDomain()));
					}else if (var instanceof Function){
						VariableType varType = VariableType.UNKNOWN;
						if (var.getDomain()!=null && var.getDomain().getName()!=null){
							SubDomain subDomain = simTask.getSimulation().getMathDescription().getSubDomain(var.getDomain().getName());
							if (subDomain instanceof CompartmentSubDomain){
								varType = VariableType.VOLUME;
							}else if (subDomain instanceof MembraneSubDomain){
								varType = VariableType.MEMBRANE;
							}else if (subDomain instanceof FilamentSubDomain){
								throw new RuntimeException("filament subdomains not supported");
							}else if (subDomain instanceof PointSubDomain){
								varType = VariableType.POINT_VARIABLE;
							}
						}
						dataSetIdentifierList.addElement(new DataSetIdentifier(var.getName(), varType, var.getDomain()));
					}else if (var instanceof Constant){
						System.out.println("ignoring Constant "+var.getName());
					}else if (var instanceof InsideVariable){
						System.out.println("ignoring InsideVariable "+var.getName());
					}else if (var instanceof OutsideVariable){
						System.out.println("ignoring OutsideVariable "+var.getName());
					}else{
						throw new RuntimeException("unexpected variable "+var.getName()+" of type "+var.getClass().getName());
					}
				}
			} catch (XmlParseException | ExpressionException e) {
				e.printStackTrace();
				throw new RuntimeException("failed to read sim task file, msg: "+e.getMessage(),e);
			}
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
	VCMongoMessage.sendTrace("SimulationData.readLog() <<ENTER>>");
	if (logFile.exists()){
		VCMongoMessage.sendTrace("SimulationData.readLog() logFile exists");
		long length = logFile.length();
		long lastModified = logFile.lastModified();
		if (lastModified == logFileLastModified && logFileLength == length){
//System.out.println("<<<SYSOUT ALERT>>>SimResults.readLog("+info.getSimID()+") lastModified and fileLength unchanged (no re-read), logFile.lastModified() = "+(new java.util.Date(lastModified)).toString());
			VCMongoMessage.sendTrace("SimulationData.readLog() hasn't been modified ... <<EXIT>>");
			return;
		}else{
//String status = "";
//status += (lastModified == logFileLastModified)?"":" lastModified=("+(new java.util.Date(lastModified)).toString()+")";
//status += (logFileLength == length)?"":" fileLength=("+length+")";
//System.out.println("<<<SYSOUT ALERT>>>SimResults.readLog("+info.getSimID()+") "+status+" changed (forced a re-read)");
			dataFilenames = null;
			zipFilenames = null;
			dataTimes = null;
			chomboFileIterationIndices = null;
			logFileLastModified = lastModified;
			logFileLength = length;
		}
	}else{
		dataFilenames = null;
		zipFilenames = null;
		dataTimes = null;
		chomboFileIterationIndices = null;
		VCMongoMessage.sendTrace("SimulationData.readLog() log file not found <<EXIT-Exception>>");
		throw new FileNotFoundException("log file "+logFile.getPath()+" not found");
	}

	//
	// read log file and check whether ODE or PDE data
	//
	StringBuffer stringBuffer = new StringBuffer();
	try ( FileInputStream is = new FileInputStream(logFile);
		InputStreamReader reader = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(reader)) {
		char charArray[] = new char[10000];
		while (true) {
			int numRead = br.read(charArray, 0, charArray.length);
			if (numRead > 0) {
				stringBuffer.append(charArray,0,numRead);
			} else if (numRead == -1) {
				break;
			}
		}
		}
	VCMongoMessage.sendTrace("SimulationData.readLog() log file read into string buffer");
	String logfileContent = stringBuffer.toString();
	if (logfileContent.length() != logFileLength){
		System.out.println("<<<SYSOUT ALERT>>>SimResults.readLog(), read "+stringBuffer.length()+" of "+logFileLength+" bytes of log file");
	}
	if ((logfileContent.startsWith(IDA_DATA_IDENTIFIER)) || (logfileContent.startsWith(ODE_DATA_IDENTIFIER)) || (logfileContent.startsWith(NETCDF_DATA_IDENTIFIER)))
	{
		String newLineDelimiters = "\n\r";
		StringTokenizer lineTokenizer = new StringTokenizer(logfileContent,newLineDelimiters);
		isODEData = true;
		isRulesData = false;
		//memorize format
		odeIdentifier = lineTokenizer.nextToken(); // br.readLine();

		@SuppressWarnings("unused")
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
	} else 	if ((logfileContent.startsWith(NFSIM_DATA_IDENTIFIER))) {
//		String newLineDelimiters = "\n\r";
//		StringTokenizer lineTokenizer = new StringTokenizer(logfileContent,newLineDelimiters);
		StringTokenizer st = new StringTokenizer(logfileContent);
		isODEData = false;
		isRulesData = true;
		//memorize format
		st.nextToken();
		odeIdentifier = NFSIM_DATA_IDENTIFIER;
		@SuppressWarnings("unused")
		String s2 = st.nextToken();
		String filename = st.nextToken();
		System.out.println("filename: " + filename);
		String xxx = (new File(filename)).getName();
		dataFilenames = new String[1];
		dataFilenames[0] = xxx;
	}
	else if (logfileContent.startsWith(SolverDataType.MBSData.name())) 
	{
		StringTokenizer st = new StringTokenizer(logfileContent);
		if (st.hasMoreTokens())
		{
			st.nextToken(); // skip the first line
			
			int numTimes = st.countTokens() / 3;
			dataTimes = new double[numTimes];
			dataFilenames = new String[numTimes];
			int index = 0;
			while (st.hasMoreTokens())
			{
				String iteration = st.nextToken();
				String filename = st.nextToken();
				String time = st.nextToken();
				dataFilenames[index] = (new File(filename)).getName();
				dataTimes[index] = (new Double(time)).doubleValue();
				index++;
			}
		}
		indexPDEdataTimes( );
	}
	else if (logfileContent.startsWith(SolverDataType.COMSOL.name())) 
	{
		StringTokenizer st = new StringTokenizer(logfileContent);
		if (st.hasMoreTokens())
		{
			dataTimes = new double[] { 0.0 };
			dataFilenames = new String[] { logFile.getAbsolutePath().replaceAll(".log", ".comsoldat") };
		}
		indexPDEdataTimes( );
	}
	else {
		StringTokenizer st = new StringTokenizer(logfileContent);
		// PDE, so parse into 'dataFilenames' and 'dataTimes' arrays
		isODEData = false;
			dataTimes = null;
			chomboFileIterationIndices = null;
			dataFilenames = null;
			zipFilenames = null;
		int tokensPerFile;
		if (bZipFormat2) {
			tokensPerFile = 4;
		}
		else {
			tokensPerFile = 3;
		}
		final int numTokens = st.countTokens();
		if (numTokens%tokensPerFile != 0) {
			throw new DataAccessException("SimResults.readLog(), zipped = " + bZipFormat2 + ", should have multiple of " + tokensPerFile + " tokens, bad parse");
		}

		int numFiles = numTokens / tokensPerFile;
		if (bZipFormat2) {
			zipFilenames = new String[numFiles];
		}
		chomboFileIterationIndices = new int[numFiles];
		dataTimes = new double[numFiles];
		dataFilenames = new String[numFiles];
		int index = 0;
		VCMongoMessage.sendTrace("SimulationData.readLog() parsing zip files and times from log <<BEGIN>>");
		while (st.hasMoreTokens()){
			String iteration = st.nextToken();
			String filename = st.nextToken();
			String time = null;
			if (bZipFormat2) {
				String zipname = st.nextToken();
				zipFilenames[index] = (new File(zipname)).getName();
			}
				time = st.nextToken();
			chomboFileIterationIndices[index] = Integer.parseInt(iteration);
			dataTimes[index] = (new Double(time)).doubleValue();
			dataFilenames[index] = (new File(filename)).getName();
			index++;
		}
		indexPDEdataTimes( );
		VCMongoMessage.sendTrace("SimulationData.readLog() parsing zip files and times from log <<END>>");
		// now check if .particle files also exist
		try {
			particleDataExists = false;
			VCMongoMessage.sendTrace("SimulationData.readLog() getting particle data file <<BEGIN>>");
			File firstFile = getParticleDataFile(dataTimes[0]);
			VCMongoMessage.sendTrace("SimulationData.readLog() getting particle data file <<END>>");
			particleDataExists = ( firstFile != null );
		} catch (Exception exc) {
			lg.warn("checking particle data", exc);
		}
	}
	VCMongoMessage.sendTrace("SimulationData.readLog() <<EXIT>>");
}


/**
 * This method was created in VisualAge.
 * @param logFile java.io.File
 */
private synchronized void readMesh(File meshFile,File membraneMeshMetricsFile) throws Exception {
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
	if(isChombo()){
//		SimulationDataSpatialHdf5 simulationDataSpatialHdf5 = new SimulationDataSpatialHdf5(vcDataId,userDirectory,null);
//		simulationDataSpatialHdf5.readVarAndFunctionDataIdentifiers();
		mesh = CartesianMeshChombo.readMeshFile(meshFile);
		// test serialization
		//byte[] byteArray = BeanUtils.toSerialized(mesh);
		//mesh = (CartesianMeshChombo) BeanUtils.fromSerialized(byteArray);
	}
	else if (amplistorHelper.solverDataType == SolverDataType.MBSData)
	{
		mesh = CartesianMeshMovingBoundary.readMeshFile(meshFile);
	}
//	else if (amplistorHelper.solverDataType == SolverDataType.COMSOL)
//	{
//		mesh = new CartesianMeshCOMSOL();
//	}
	else
	{
		mesh = CartesianMesh.readFromFiles(meshFile, membraneMeshMetricsFile, getSubdomainFilePrivate());
	}
}

public static class CartesianMeshCOMSOL extends CartesianMesh {
	public CartesianMeshCOMSOL(){
		super();
	}
}

public boolean isChombo() throws FileNotFoundException{
	return !amplistorHelper.isNonSpatial() && getMeshFile().getName().endsWith(SimDataConstants.DATA_PROCESSING_OUTPUT_EXTENSION_HDF5);
}

@Override
public boolean isMovingBoundary() throws FileNotFoundException{
	return !amplistorHelper.isNonSpatial() && getMeshFile().getName().endsWith(SimDataConstants.MOVINGBOUNDARY_OUTPUT_EXTENSION);
}


@Override
public boolean isComsol() throws FileNotFoundException{
	return !amplistorHelper.isNonSpatial() && getMeshFile().getName().endsWith(SimDataConstants.COMSOL_OUTPUT_EXTENSION);
}



/**
 * This method was created in VisualAge.
 */
private synchronized void refreshLogFile() throws DataAccessException {
	VCMongoMessage.sendTrace("SimulationData.refreshLogFile() <<ENTER>>");
	//
	// (re)read the log file if necessary
	//
	try {
		readLog(getLogFilePrivate());
	} catch (FileNotFoundException e) {
	} catch (IOException e) {
		e.printStackTrace(System.out);
		throw new DataAccessException(e.getMessage(),e);
	}catch (SecurityException e){
		//Added because "new FileInputStream(...) throws security exception when file permissions wrong
		e.printStackTrace(System.out);
		throw new DataAccessException(e.getMessage(),e);
	}catch (RuntimeException e){
		//log exceptions we are passing on
		e.printStackTrace(System.out);
		throw e;
	}
	VCMongoMessage.sendTrace("SimulationData.refreshLogFile() <<EXIT>>");
}


/**
 * This method was created in VisualAge.
 */
private synchronized void refreshMeshFile() throws DataAccessException, MathException {
	//
	// (re)read the log file if necessary
	//
	VCMongoMessage.sendTrace("SimulationData.refreshMeshFile() <<BEGIN>>");
	try {
		readMesh(getMeshFile(),getMembraneMeshMetricsFile());
		VCMongoMessage.sendTrace("SimulationData.refreshMeshFile() <<EXIT normally>>");
	} catch (FileNotFoundException e) {
		VCMongoMessage.sendTrace("SimulationData.refreshMeshFile() <<EXIT-file not found>>");
	} catch (Exception e) {
		e.printStackTrace(System.out);
		VCMongoMessage.sendTrace("SimulationData.refreshMeshFile() <<EXIT-IOException>>");
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * This method was created in VisualAge.
 */
public synchronized void removeAllResults() throws DataAccessException {
	File logFile = null;
	try {
		logFile = getLogFilePrivate();
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
	chomboFileIterationIndices = null;
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

public static String createCanonicalSimTaskXMLFilePathName(KeyValue fieldDataKey,int jobIndex,boolean isOldStyle){
	return createSimIDWithJobIndex(fieldDataKey,jobIndex,isOldStyle)+"_"+jobIndex+SimDataConstants.SIMTASKXML_EXTENSION;
}

public static String createCanonicalFieldDataLogFileName(KeyValue fieldDataKey){
	return
	createSimIDWithJobIndex(fieldDataKey,0,false)+
	SimDataConstants.LOGFILE_EXTENSION;
}

public static String createCanonicalFieldFunctionSyntax(String externalDataIdentifierName,String varName,double beginTime,String extDataIdVariableTypeName){
	VariableType vt = VariableType.getVariableTypeFromVariableTypeName(extDataIdVariableTypeName);
	return FieldFunctionDefinition.FUNCTION_name + "('"+
		externalDataIdentifierName+"','"+varName+"',"+beginTime+",'"+vt.getTypeName()+"')";
}

public static String createCanonicalSimZipFileName(KeyValue fieldDataKey,Integer zipIndex,int jobIndex,boolean isOldStyle,boolean bHDF5){
	return
	createSimIDWithJobIndex(fieldDataKey,jobIndex,isOldStyle)+
	(zipIndex==null?"":(zipIndex<10?"0":"")+zipIndex)+(bHDF5?SimDataConstants.DATA_PROCESSING_OUTPUT_EXTENSION_HDF5:"")+SimDataConstants.ZIPFILE_EXTENSION;
}

public static String createCanonicalSimLogFileName(KeyValue fieldDataKey,int jobIndex,boolean isOldStyle){
	return
	createSimIDWithJobIndex(fieldDataKey,jobIndex,isOldStyle)+
	SimDataConstants.LOGFILE_EXTENSION;
}
public static String createCanonicalSmoldynOutputFileName(KeyValue fieldDataKey,int jobIndex,int timeIndex){
	if (timeIndex > 0) {
		String rval = createSimIDWithJobIndex(fieldDataKey,jobIndex,false) + String.format("_%03d",timeIndex)
			+ SimDataConstants.SMOLDYN_OUTPUT_FILE_EXTENSION;
		return rval;
	}
	throw new IllegalArgumentException("smoldyn output index must be > 0");

}

public static String createCanonicalPostProcessFileName(VCDataIdentifier vcdID){
	return vcdID.getID()+SimDataConstants.DATA_PROCESSING_OUTPUT_EXTENSION_HDF5;
}

public static String createCanonicalMeshFileName(KeyValue fieldDataKey,int jobIndex,boolean isOldStyle){
	return
	createSimIDWithJobIndex(fieldDataKey,jobIndex,isOldStyle)+
	SimDataConstants.MESHFILE_EXTENSION;
}
public static String createCanonicalMeshMetricsFileName(KeyValue fieldDataKey,int jobIndex,boolean isOldStyle){
	return
	createSimIDWithJobIndex(fieldDataKey,jobIndex,isOldStyle)+
	SimDataConstants.MESHMETRICSFILE_EXTENSION;
}
public static String createCanonicalMovingBoundaryOutputFileName(KeyValue fieldDataKey,int jobIndex,boolean isOldStyle){
	return
	createSimIDWithJobIndex(fieldDataKey,jobIndex,isOldStyle)+
	SimDataConstants.MOVINGBOUNDARY_OUTPUT_EXTENSION;
}

public static String createCanonicalComsolOutputFileName(KeyValue fieldDataKey,int jobIndex,boolean isOldStyle){
	return
	createSimIDWithJobIndex(fieldDataKey,jobIndex,isOldStyle)+
	SimDataConstants.COMSOL_OUTPUT_EXTENSION;
}

public static String createCanonicalFunctionsFileName(KeyValue fieldDataKey,int jobIndex,boolean isOldStyle){
	return
		createSimIDWithJobIndex(fieldDataKey,jobIndex,isOldStyle)+
		SimDataConstants.FUNCTIONFILE_EXTENSION;
}

public static String createCanonicalSubdomainFileName(KeyValue fieldDataKey,int jobIndex,boolean isOldStyle){
	return
	createSimIDWithJobIndex(fieldDataKey,jobIndex,isOldStyle)+
	SimDataConstants.SUBDOMAINS_FILE_SUFFIX;
}

public static String createCanonicalResampleFileName(SimResampleInfoProvider simResampleInfoProvider,FieldFunctionArguments fieldFuncArgs){
	return
		createSimIDWithJobIndex(
				simResampleInfoProvider.getSimulationKey(),
				simResampleInfoProvider.getJobIndex(),
				!simResampleInfoProvider.isParameterScanType())+
		SimulationData.getDefaultFieldDataFileNameForSimulation(fieldFuncArgs);
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
	ReservedMathSymbolEntries.getAll(entryMap, false);
	for (DataSetIdentifier dsi : dataSetIdentifierList) {
		entryMap.put(dsi.getName(), dsi);
	}
}

private DataProcessingOutputInfo dataProcessingOutputInfo;
private OutputContext lastOutputContext;
private long lastDataProcessingOutputInfoTime = 0;
private void refreshDataProcessingOutputInfo(OutputContext outputContext) throws DataAccessException{
	File dataProcessingOutputFile = getDataProcessingOutputSourceFileHDF5();
	if(dataProcessingOutputInfo == null && !dataProcessingOutputFile.exists()){
		return;
	}
	AnnotatedFunction[] lastFunctions = (lastOutputContext==null?null:lastOutputContext.getOutputFunctions());
	AnnotatedFunction[] currentFunctions = (outputContext==null?null:outputContext.getOutputFunctions());
	boolean bFunctionsEqual = Compare.isEqualOrNull(lastFunctions, currentFunctions);
	if(!bFunctionsEqual || dataProcessingOutputInfo == null || lastDataProcessingOutputInfoTime != dataProcessingOutputFile.lastModified()){
		lastDataProcessingOutputInfoTime = dataProcessingOutputFile.lastModified();
		lastOutputContext = outputContext;
		DataProcessingOutputInfoOP dataProcessingOutputInfoOP = new DataProcessingOutputInfoOP(vcDataId, false, outputContext);
		try{
			dataProcessingOutputInfo = (DataProcessingOutputInfo)DataSetControllerImpl.getDataProcessingOutput(dataProcessingOutputInfoOP, dataProcessingOutputFile);
		}catch(Exception e){
			throw new DataAccessException(e.getMessage(),(e.getCause()==null?e:e.getCause()));
		}
	}
}
public boolean isPostProcessing(OutputContext outputContext,String varName) throws Exception{
	refreshDataProcessingOutputInfo(outputContext);
	if(dataProcessingOutputInfo != null){
		for (int i = 0; i < dataProcessingOutputInfo.getVariableNames().length; i++) {
			if(dataProcessingOutputInfo.getVariableNames()[i].equals(varName) && dataProcessingOutputInfo.getPostProcessDataType(varName).equals(DataProcessingOutputInfo.PostProcessDataType.image)){
				return true;
			}
		}
	}
	if(outputContext != null){
		for (int i = 0; i < outputContext.getOutputFunctions().length; i++) {
			if(outputContext.getOutputFunctions()[i].getFunctionType().equals(VariableType.POSTPROCESSING) && outputContext.getOutputFunctions()[i].getName().equals(varName)){
				return true;
			}
		}
	}
	return false;
}

public File getDataProcessingOutputSourceFileHDF5(){
	return amplistorHelper.getPostProcessFile();
}
public File getFieldDataFile(SimResampleInfoProvider simResampleInfoProvider,FieldFunctionArguments fieldFunctionArguments){
	return amplistorHelper.getFieldDataFile(simResampleInfoProvider,fieldFunctionArguments);
}
public File getMeshFile(boolean bHDF5){
	return amplistorHelper.getMeshFile(bHDF5);
}
public File getFunctionsFile(boolean bFirst){
	return amplistorHelper.getFunctionsFile(bFirst);
}
public File getZipFile(boolean bHDF5,Integer zipIndex){
	return amplistorHelper.getZipFile(bHDF5, zipIndex);
}
public File getSubdomainFile(){
	return amplistorHelper.getSubdomainFile();
}
public File getLogFile(){
	return amplistorHelper.getLogFile();
}
public File getSmoldynOutputFile(int timeIndex){
	return amplistorHelper.getSmoldynOutputFile(timeIndex);
}

private void findChomboFeatureVolFile(ChomboFiles chomboFiles, VCSimulationDataIdentifier vcDataID, String subDomain, int ivol, int timeIndex)
{
	String expectedFile = vcDataID.getID()+String.format("%06d", timeIndex)+".feature_"+subDomain+".vol" + ivol + ".hdf5";
	File file = amplistorHelper.getFile(expectedFile);
	if (file.exists()){
		chomboFiles.addDataFile(subDomain, ivol, timeIndex, file);
	}else{
		// I changed the file name
		expectedFile = vcDataID.getID()+String.format("%06d", timeIndex)+"_"+subDomain+"_vol" + ivol + ".hdf5";
	  file = amplistorHelper.getFile(expectedFile);
	  if (file.exists()){
			chomboFiles.addDataFile(subDomain, ivol, timeIndex, file);
	  }
	  else
	  {
	  	LG.warn("can't find expected chombo file : "+file.getAbsolutePath());
	  }
	}
}

@Override
public ChomboFiles getChomboFiles() throws IOException, XmlParseException, ExpressionException {
	if (chomboFileIterationIndices==null){
		throw new RuntimeException("SimulationData.chomboFileIterationIndices is null, can't process Chombo HDF5 files");
	}
	if (!(getVcDataId() instanceof VCSimulationDataIdentifier)){
		throw new RuntimeException("SimulationData.getVcDataId() is not a VCSimulationDataIdentifier (type is "+getVcDataId().getClass().getName()+"), can't process chombo HDF5 files");
	}
	VCSimulationDataIdentifier vcDataID = (VCSimulationDataIdentifier)getVcDataId();

	String expectedMeshfile = vcDataID.getID()+".mesh.hdf5";
	File meshFile = amplistorHelper.getFile(expectedMeshfile);

	ChomboFiles chomboFiles = new ChomboFiles(vcDataID.getSimulationKey(), vcDataID.getJobIndex(), meshFile);

	String simtaskFilePath = vcDataID.getID()+"_0.simtask.xml";
	File simtaskFile = amplistorHelper.getFile(simtaskFilePath);
	if (!simtaskFile.exists()){
		throw new RuntimeException("Chombo dataset mission .simtask.xml file, please rerun");
	}
	String xmlString = FileUtils.readFileToString(simtaskFile);
	SimulationTask simTask = XmlHelper.XMLToSimTask(xmlString);
	if (!simTask.getSimulation().getSolverTaskDescription().getChomboSolverSpec().isSaveChomboOutput()
			&& !simTask.getSimulation().getSolverTaskDescription().isParallel()){
		throw new RuntimeException("Export of Chombo simulations to VTK requires chombo data, select 'Chombo' data format in simulation solver options and rerun simulation.");
	}
	CartesianMeshChombo chomboMesh = (CartesianMeshChombo) mesh;
	FeaturePhaseVol[] featurePhaseVols = chomboMesh.getFeaturePhaseVols();
	for (int timeIndex : chomboFileIterationIndices)
	{
		if (featurePhaseVols == null)
		{
			// for old format which doesn't have featurephasevols, we need to try ivol up to 20 for each feature
			Enumeration<SubDomain> subdomainEnum = simTask.getSimulation().getMathDescription().getSubDomains();
			while (subdomainEnum.hasMoreElements()){
				SubDomain subDomain = subdomainEnum.nextElement();
				if (subDomain instanceof CompartmentSubDomain){
					for (int ivol = 0; ivol < 20; ++ ivol)
					{
						// can be many vol, let us try 20
						findChomboFeatureVolFile(chomboFiles, vcDataID, subDomain.getName(), ivol, timeIndex);
					}
				}
			}
		}
		else
		{
		  // for new format which has featurephasevols, we only need to try this many times.
			// note: some feature + ivol doesn't have a file if there are no variables defined in that feature
			for (FeaturePhaseVol pfv : featurePhaseVols)
			{
				findChomboFeatureVolFile(chomboFiles, vcDataID, pfv.feature, pfv.ivol, timeIndex);
			}
		}
	}
	return chomboFiles;
}

@Override
public VCellSimFiles getVCellSimFiles() throws FileNotFoundException, DataAccessException {
	File cartesianMeshFile = getMeshFile();
	File meshMetricsFile = getMembraneMeshMetricsFile();
	File subdomainFile = getSubdomainFile();
	File logFile = getLogFile();
	File postprocessingFile = getDataProcessingOutputSourceFileHDF5();
	if (!(vcDataId instanceof VCSimulationDataIdentifier)){
		throw new RuntimeException("cannot process vtk, vcDataId not "+VCSimulationDataIdentifier.class.getSimpleName());
	}
	VCSimulationDataIdentifier vcSimDataID = (VCSimulationDataIdentifier)vcDataId;
	VCellSimFiles vcellSimFiles = new VCellSimFiles(vcSimDataID.getSimulationKey(),vcSimDataID.getJobIndex(),cartesianMeshFile, meshMetricsFile, subdomainFile, logFile, postprocessingFile);
	refreshLogFile();
	double[] times = getDataTimes();
	for (int i=0;i<times.length;i++){
		File pdeDataFile = getPDEDataFile(times[i]);
		File zipFile = getPDEDataZipFile(times[i]);
		vcellSimFiles.addDataFileEntry(zipFile, pdeDataFile, times[i]);
	}

	return vcellSimFiles;
}

@Override
public MovingBoundarySimFiles getMovingBoundarySimFiles() throws FileNotFoundException, DataAccessException {
	File movingBoundaryOutputFile = amplistorHelper.getMovingBoundaryOutputFile(); //retrieveExistingFile(SimFileTypeStandard.MovingBoundaryOutputFile);
	VCSimulationDataIdentifier vcSimDataID = (VCSimulationDataIdentifier)vcDataId;
	MovingBoundarySimFiles movingBoundarySimFiles = new MovingBoundarySimFiles(vcSimDataID.getSimulationKey(),vcSimDataID.getJobIndex(),movingBoundaryOutputFile);
	return movingBoundarySimFiles;
}


@Override
public ComsolSimFiles getComsolSimFiles() throws FileNotFoundException, DataAccessException {
	File comsolOutputFile = amplistorHelper.getComsolOutputFile(); //retrieveExistingFile(SimFileTypeStandard.MovingBoundaryOutputFile);
	File simTaskXMLFile = amplistorHelper.getSimTaskXMLFile();
	File logFile = amplistorHelper.getLogFile();
	VCSimulationDataIdentifier vcSimDataID = (VCSimulationDataIdentifier)vcDataId;
	ComsolSimFiles comsolSimFiles = new ComsolSimFiles(vcSimDataID.getSimulationKey(),vcSimDataID.getJobIndex(),comsolOutputFile,simTaskXMLFile,logFile);
	return comsolSimFiles;
}


public VCDataIdentifier getVcDataId() {
	return vcDataId;
}

	public synchronized SimDataBlock getChomboExtrapolatedValues(String varName, double time) throws DataAccessException, IOException
	{
		refreshLogFile();

		File pdeFile = getPDEDataFile(time);
		if (pdeFile==null){
			return null;
		}
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
			throw new DataAccessException("data at time="+time+" read error",ex);
		}

		long lastModified = getLastModified(pdeFile, zipFile);
		DataSetIdentifier dsi = getDataSetIdentifier(varName);
		if (dsi == null) {
			throw new DataAccessException("data not found for variable " + varName);
		}
		final String varNameInDataSet = dsi.getQualifiedName();
		double data[] = DataSet.readChomboExtrapolatedValues(varNameInDataSet, pdeFile, zipFile);
		VariableType variableType = VariableType.MEMBRANE;
		PDEDataInfo pdeDataInfo = new PDEDataInfo(vcDataId.getOwner(),vcDataId.getID(),varName,time,lastModified);
		return data == null ? null : new SimDataBlock(pdeDataInfo,data,variableType);
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (10/3/00 2:48:55 PM)
	 * @return cbit.vcell.simdata.PDEVariableType
	 * @param mesh cbit.vcell.solvers.CartesianMesh
	 * @param dataLength int
	 */
	public static final VariableType getVariableTypeFromLength(CartesianMesh mesh, int dataLength) {
		VariableType result = null;
		if (mesh.getDataLength(VariableType.VOLUME) == dataLength) {
			result = VariableType.VOLUME;
		} else if (mesh.getDataLength(VariableType.MEMBRANE) == dataLength) {
			result = VariableType.MEMBRANE;
		} else if (mesh.getDataLength(VariableType.CONTOUR) == dataLength) {
			result = VariableType.CONTOUR;
		} else if (mesh.getDataLength(VariableType.VOLUME_REGION) == dataLength) {
			result = VariableType.VOLUME_REGION;
		} else if (mesh.getDataLength(VariableType.MEMBRANE_REGION) == dataLength) {
			result = VariableType.MEMBRANE_REGION;
		} else if (mesh.getDataLength(VariableType.CONTOUR_REGION) == dataLength) {
			result = VariableType.CONTOUR_REGION;
		}
		return result;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (9/21/2006 2:51:03 PM)
	 * @return java.lang.String
	 */
	public static String getDefaultFieldDataFileNameForSimulation(FieldFunctionArguments fieldFuncArgs) {
		return fieldFuncArgs.getUniqueID() + SimDataConstants.FIELDDATARESAMP_EXTENSION;
	}

}
