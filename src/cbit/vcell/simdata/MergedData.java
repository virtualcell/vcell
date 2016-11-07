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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;

import org.vcell.util.DataAccessException;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDataIdentifier;
import org.vcell.vis.io.ChomboFiles;
import org.vcell.vis.io.VCellSimFiles;

import cbit.vcell.math.FunctionColumnDescription;
import cbit.vcell.math.InsideVariable;
import cbit.vcell.math.MathException;
import cbit.vcell.math.ODESolverResultSetColumnDescription;
import cbit.vcell.math.OutsideVariable;
import cbit.vcell.math.ReservedMathSymbolEntries;
import cbit.vcell.math.VariableType;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.simdata.DataSetControllerImpl.ProgressListener;
import cbit.vcell.solver.AnnotatedFunction;
import cbit.vcell.solver.ode.ODESimData;
import cbit.vcell.solver.ode.ODESolverResultSet;
import cbit.vcell.solver.test.MathTestingUtilities;
import cbit.vcell.solvers.CartesianMesh;
import cbit.vcell.solvers.FunctionFileGenerator;
import cbit.vcell.util.ColumnDescription;
import cbit.vcell.xml.XmlParseException;
/**
 * This type was created in VisualAge.
 */
public class MergedData extends VCData {

	private double dataTimes[] = null;
	private String[] dataSetPrefix = null;
	private Vector<DataSetIdentifier> dataSetIdentifierList = new Vector<DataSetIdentifier>();
	private Vector<AnnotatedFunction> annotatedFunctionList = new Vector<AnnotatedFunction>();
	private VCDataIdentifier[] datasetsIDList=null;
	private MergedDataInfo dataInfo=null;
	private final static long SizeInBytes = 2000;  // a guess
	private DataSetControllerImpl dataSetControllerImpl=null;
	private long functionFileLastModified = 0;
	private long functionFileLength = 0;
	private File userDirectory = null;
	private int[] membraneIndexMapping = null;

/**
 * Insert the method's description here.
 * Creation date: (9/29/2003 5:45:44 PM)
 */
public MergedData(User argUser, File argPrimaryUserDir, File argSecondaryUserDir, DataSetControllerImpl argDatasetContrlrImpl, VCDataIdentifier[] argDatasetIDs, String[] dsPrefix) throws DataAccessException, IOException  {
	dataSetControllerImpl = argDatasetContrlrImpl;
	dataSetControllerImpl.setAllowOptimizedTimeDataRetrieval(false);
	if (argDatasetIDs.length < 2) {
		throw new RuntimeException("\nLess than 2 datasets, no comparison!!\n");
	} 
	dataInfo = new MergedDataInfo(argUser,argDatasetIDs,dsPrefix);	
	datasetsIDList = argDatasetIDs;
	try {
		userDirectory = argPrimaryUserDir;
		getFunctionsFile();
	} catch (FileNotFoundException exc) {		 
		if (argSecondaryUserDir == null) {
			throw new FileNotFoundException("secondarySimDataDirProperty not specified, primary user directory, " + argPrimaryUserDir + " doesn't exist.");
		}
		userDirectory = argSecondaryUserDir;
		getFunctionsFile();
	}
	if(dsPrefix == null)
	{
		dataSetPrefix = new String[argDatasetIDs.length];
		for (int i = 0; i < argDatasetIDs.length; i++)
		{
			dataSetPrefix[i] = "Data"+(i+1); 
		}
	}
	else
	{
		dataSetPrefix = dsPrefix;
	}
	mergeDatasets();
	getDataTimes();
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

	functionBindAndSubstitute(function);
	
	addFunctionToListInternal(function);
	
}

private void functionBindAndSubstitute(AnnotatedFunction function) throws ExpressionException {

	// attempt to bind function and substitute
	Expression simExp = function.getExpression();	
	if (simExp == null) {
		Expression exp = new Expression(function.getExpression());
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
		simExp = exp.flatten();
		function.setExpression(simExp);	
	}
	simExp.bindExpression(this);
	function.getExpression().bindExpression(this);
	
}

private void addFunctionToListInternal(AnnotatedFunction function){
	
	DataSetIdentifier dsi = new DataSetIdentifier(function.getName(),function.getFunctionType(),function.getDomain(),true);	
	// add the new function to dataSetIndentifierList so that other functions can bind this function
	dataSetIdentifierList.addElement(dsi);
	//Add new func
	annotatedFunctionList.addElement((AnnotatedFunction)function);
}


/**
 * Insert the method's description here.
 * Creation date: (9/29/2003 5:50:24 PM)
 * @return cbit.vcell.simdata.VCData
 */
private boolean checkTimeArrays(double[] timeArray) {
	boolean bTimesEqual = true;
	//
	// Compare the time arrays for the reference and given dtasets. If the lengths don't match,
	// the time arrays are not equal. If the lengths are equal, compare individual elements of arrays
	// to see if they match.
	//
	if (timeArray.length != dataTimes.length) {
		bTimesEqual = false;
		// throw new RuntimeException("Data times for reference and test simulations don't match, cannot compare the two simulations!");
	} else {
		for (int i = 0; i < timeArray.length; i++) {
			if (timeArray[i] != dataTimes[i]) {
				bTimesEqual = false;
			}
		}
	}
	return bTimesEqual;
}


/**
 * Insert the method's description here.
 * Creation date: (1/19/00 11:52:22 AM)
 * @return long
 * @param dataType int
 * @param timepoint double
 * @exception org.vcell.util.DataAccessException The exception description.
 */
public long getDataBlockTimeStamp(int dataType, double timepoint) throws DataAccessException {
	long latestTimeStamp = Long.MIN_VALUE;

	// Get the maximum datatimeStamp of the datasets in the datasetsIDlist.
	// If the time arrays of the datasets are not equal, find the indices of the timearray
	// between which the argument 'timepoint' lies, choose the dataTimeStamp of the larger time index,
	// use that value to find the max.
	try {
		for (int i = 0; i < datasetsIDList.length; i++) {
			VCData vcdata = getDatasetControllerImpl().getVCData(datasetsIDList[i]);
			double[] timeArray = getDatasetControllerImpl().getDataSetTimes(datasetsIDList[i]);
		 	boolean bTimesEqual = checkTimeArrays(timeArray);
	 	 	int timeArrayCounter = 0;
			long datatimeStamp;

			if (bTimesEqual) {	
				datatimeStamp = vcdata.getDataBlockTimeStamp(dataType, timepoint);
		 	} else {
				while ((timeArrayCounter < timeArray.length-2) && (timepoint > timeArray[timeArrayCounter+1])) {
					timeArrayCounter++;
				}
				datatimeStamp = vcdata.getDataBlockTimeStamp(dataType, timeArray[Math.min(timeArray.length-1,timeArrayCounter+1)]);			 	
		 	}
			latestTimeStamp = Math.max(latestTimeStamp, datatimeStamp);
		}
	} catch (IOException e) {
		e.printStackTrace(System.out);
		throw new DataAccessException("\n Error computing Timestamp for CompositeData! " + e.getMessage());
	}
 	
	return latestTimeStamp;
}


/**
 * Insert the method's description here.
 * Creation date: (10/8/2003 2:53:44 PM)
 * @return cbit.vcell.simdata.DataSetControllerImpl
 */
private DataSetControllerImpl getDatasetControllerImpl() {
	return dataSetControllerImpl;
}


/**
 * Insert the method's description here.
 * Creation date: (10/11/00 4:44:52 PM)
 * @return cbit.vcell.simdata.DataSetIdentifier
 * @param identifier java.lang.String
 */
private DataSetIdentifier getDataSetIdentifier(String identifier) {
	for (int i=0;i<dataSetIdentifierList.size();i++){
		DataSetIdentifier dsi = (DataSetIdentifier)dataSetIdentifierList.elementAt(i);
		if (dsi.getName().equals(identifier)){
			return dsi;
		}
	}
//	if (annotatedFunctionList.size()==0){
//		try {
//			// read functions and try again
//			readFunctions();
//			for (int i=0;i<dataSetIdentifierList.size();i++){
//				DataSetIdentifier dsi = (DataSetIdentifier)dataSetIdentifierList.elementAt(i);
//				if (dsi.getName().equals(identifier)){
//					return dsi;
//				}
//			}
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	return null;
}


/**
 * Insert the method's description here.
 * Creation date: (9/29/2003 5:50:24 PM)
 * @return cbit.vcell.simdata.VCData
 */
public double[] getDataTimes() {
	try {
		dataTimes = getDatasetControllerImpl().getDataSetTimes(datasetsIDList[0]);
	} catch (DataAccessException e) {
		e.printStackTrace(System.out);
		throw new RuntimeException("Failed to obtain dataTimes from referenceSimInfo "+datasetsIDList[0].getID()+"\n");
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
	
	entry = ReservedMathSymbolEntries.getEntry(identifier, false);
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
	if (dataSetIdentifierList.size() != 0){


		// remove functions from dataIdentifiers since we are reading functions again
		for (int i = 0; i < dataSetIdentifierList.size(); i ++) {
			DataSetIdentifier dsi = (DataSetIdentifier)dataSetIdentifierList.elementAt(i);
			if (dsi.isFunction()) {
				boolean bTopLevelFunction = true;
				for (int j = 0; j < dataSetPrefix.length; j++) {			
					if (dsi.getName().startsWith(dataSetPrefix[j]+".")) {
						bTopLevelFunction = false;
						break;
					}
				}				
				// merged data function file only contains user-defined "merged data" functions
				// so don't remove Data1 or Data2 functions, because rereading this function file will not repopulated those.
				if (bTopLevelFunction) {
					dataSetIdentifierList.removeElement(dsi);
					i --;
				}
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
	
	ArrayList<AnnotatedFunction> functionsArr = new ArrayList<>();
//	AnnotatedFunction functions[] = new AnnotatedFunction[annotatedFunctionList.size()];
	// Get the functions in annotatedFunctionsList
	for (int i = 0; i < annotatedFunctionList.size(); i++){
		//AnnotatedFunction annotatedFunc = (AnnotatedFunction)annotatedFunctionList.elementAt(i);
		//functions[i] = new Function(annotatedFunc.getName(), annotatedFunc.getExpression());
		functionsArr.add((AnnotatedFunction)annotatedFunctionList.elementAt(i));
	}
	for (int i = 0; i < datasetsIDList.length; i++) {
		VCDataIdentifier vcdid = datasetsIDList[i];
		try {
			AnnotatedFunction[] myFuncs = getDatasetControllerImpl().getFunctions(outputContext, vcdid);
			for (int j = 0; j < myFuncs.length; j++) {
				AnnotatedFunction myfunc = new AnnotatedFunction(dataSetPrefix[i]+"."+myFuncs[j].getName(),myFuncs[j].getExpression(),myFuncs[j].getDomain(),myFuncs[j].getErrorString(),myFuncs[j].getFunctionType(),myFuncs[j].getFunctionCatogery());
				functionsArr.add(myfunc);
			}
		} catch (ExpressionBindingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	return functionsArr.toArray(new AnnotatedFunction[0]);
}


/**
 * This method was created in VisualAge.
 * @return java.io.File
 * @param user cbit.vcell.server.User
 * @param simID java.lang.String
 */
private synchronized File getFunctionsFile() throws FileNotFoundException, IOException {
	File functionsFile = new File(userDirectory,dataInfo.getID()+".functions");
	if (functionsFile.exists()){
		return functionsFile;
	}else{
		functionsFile.createNewFile();
		if (functionsFile.exists()){
			return functionsFile;
		}else{
			throw new FileNotFoundException("functions file "+functionsFile.getPath()+" not found");
		}
	}
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.solvers.CartesianMesh
 */
public CartesianMesh getMesh() throws DataAccessException, MathException {
	try {
		return getDatasetControllerImpl().getMesh(datasetsIDList[0]);
	} catch (IOException e) {
		e.printStackTrace(System.out);
		throw new RuntimeException(e.getMessage());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (1/14/00 2:28:47 PM)
 * @return cbit.vcell.simdata.ODEDataBlock
 */
public ODEDataBlock getODEDataBlock() throws DataAccessException {

	ODESolverResultSet combinedODESolverRSet = new ODESolverResultSet();

	ODEDataBlock refDataBlock = dataSetControllerImpl.getODEDataBlock(datasetsIDList[0]);
	ODESimData refSimData = refDataBlock.getODESimData();

	// Can use dataTimes field later (for genuine SimulationData), but for now, obtain it on the fly
	double times[] = null;
	try {
		times = refSimData.extractColumn(refSimData.findColumn("t"));
	} catch (ExpressionException e) {
		e.printStackTrace(System.out);
	}	

	// Adding data/function columns to new resultSet
	ODESolverResultSet[] resultSetList = new ODESolverResultSet[datasetsIDList.length];
	for (int i = 0; i < datasetsIDList.length; i++) {
		ODEDataBlock dataBlock = dataSetControllerImpl.getODEDataBlock(datasetsIDList[i]);
		ODESimData simData = dataBlock.getODESimData();

		ODESolverResultSet newODErset = new ODESolverResultSet();
		// First resultSet is reference resultSet. From the second onwards, resample the resultSet wrt the reference.
		if (i > 0) {
			try {
				newODErset = resampleODEData(refSimData, simData);
			} catch (ExpressionException e) {
				e.printStackTrace(System.out);
				throw new RuntimeException("\n >>>> Could not resample data! <<<<\n");
			}
		} else {
			newODErset = simData;
		}
		resultSetList[i] = newODErset;
		// Add data columns
		String[] newVarNames = new String[newODErset.getDataColumnCount()];
		for (int j = 0; j < newODErset.getDataColumnCount(); j++) {
			// If 'time' column is present in combinedResultSet, continue with next data column
			if ( (combinedODESolverRSet.findColumn("t") > -1) && (newODErset.getDataColumnDescriptions()[j].getName().equals("t")) ) {
				newVarNames[j] = newODErset.getDataColumnDescriptions()[j].getName();
				continue;
			}
			// Retain 'time' column as 't', without using datasetID as prefix to avoid multiple time columns in combinedODEResultSet.
			// Adding the time column from the first dataset to combinedResultSet, since it is treated as the reference dataset.
			String newColName = null;
			if (j == 0 && newODErset.getDataColumnDescriptions()[j].getName().equals("t")) {
				newColName = newODErset.getDataColumnDescriptions()[j].getName();
			} else {
				newColName = dataSetPrefix[i]+"."+newODErset.getDataColumnDescriptions()[j].getName();
			}
			newVarNames[j] = newColName;
			ColumnDescription cd = newODErset.getDataColumnDescriptions()[j];
			if(cd instanceof ODESolverResultSetColumnDescription)
			{
				ODESolverResultSetColumnDescription newCD = new ODESolverResultSetColumnDescription(newColName, cd.getParameterName(), newColName);
				combinedODESolverRSet.addDataColumn(newCD);
			}
		}
		// Add function columns
		for (int j = 0; j < newODErset.getFunctionColumnCount(); j++) {
			try {
				String newColName = dataSetPrefix[i]+"."+newODErset.getFunctionColumnDescriptions()[j].getName();
				FunctionColumnDescription fcd = newODErset.getFunctionColumnDescriptions()[j];
				Expression newExp = new Expression(fcd.getExpression());
				String symbols[] = newExp.getSymbols();
				if (symbols != null && (symbols.length > 0)) {
					for (int jj = 0; jj < symbols.length; jj++) {
						for (int kk = 0; kk < newVarNames.length; kk++) {
							if (newVarNames[kk].equals(dataSetPrefix[i]+"."+symbols[jj])) {
								newExp.substituteInPlace(new Expression(symbols[jj]), new Expression(newVarNames[kk]));
								break;
							}
						}
					}
				}
				FunctionColumnDescription newFcd = new FunctionColumnDescription(newExp, newColName, fcd.getParameterName(), newColName,fcd.getIsUserDefined());
				combinedODESolverRSet.addFunctionColumn(newFcd);
			} catch (ExpressionException e) {
				e.printStackTrace(System.out);
			}
		}	
	}

	// Populating new dataset
	for (int i = 0; i < times.length; i++) {
		double[] newRow = new double[combinedODESolverRSet.getDataColumnCount()];
		int indx = 0;
		for (int j = 0; j < resultSetList.length; j++) {
			ODESolverResultSet resultSet = resultSetList[j];
			double[] tempRow = resultSet.getRow(i);
			int startIndx = 0;
			int arrayLen = tempRow.length;
			if (j > 0) {
				// From the second dataset onwards, we do not want to copy the time column, hence skip to
				// the next element/col in dataset, that reduces the # of elements in the row by 1.
				startIndx = 1;
				arrayLen = tempRow.length-1;
			}
			System.arraycopy(tempRow, startIndx, newRow, indx, arrayLen);
			indx += tempRow.length;
		}
		combinedODESolverRSet.addRow(newRow);
	}

	ODEDataInfo odeDataInfo = new ODEDataInfo(getResultsInfoObject().getOwner(), getResultsInfoObject().getID(), 0);
	ODESimData odeSimData = new ODESimData(getResultsInfoObject(), combinedODESolverRSet);
	return new ODEDataBlock(odeDataInfo, odeSimData);
}

/**
 * This method was created in VisualAge.
 * @return cbit.vcell.simdata.ParticleDataBlock
 * @param double time
 */
public ParticleDataBlock getParticleDataBlock(double time) throws DataAccessException, IOException {
	throw new RuntimeException("Not yet implemented!");
}


/**
 * This method was created in VisualAge.
 * @return boolean
 */
public boolean getParticleDataExists() throws DataAccessException {
	try {
		return getDatasetControllerImpl().getParticleDataExists(datasetsIDList[0]);
	} catch (IOException e) {
		e.printStackTrace(System.out);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.simdata.SimResultsInfo
 */
public VCDataIdentifier getResultsInfoObject() {
	return dataInfo;
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.simdata.DataBlock
 * @param user cbit.vcell.server.User
 * @param simID java.lang.String
 */
public SimDataBlock getSimDataBlock(OutputContext outputContext,String varName, double time) throws DataAccessException, IOException {
	VCDataIdentifier vcDataID = getVCDataIdentifierFromDataId(varName);
	if (vcDataID == null) {
		return null;
	}
	DataSetIdentifier varDatasetID = getDataSetIdentifier(varName);

	int actualVarNameIndx = varName.indexOf(".");
	String actualVarName = varName.substring(actualVarNameIndx+1);
	SimDataBlock simDataBlk = null;

	//
	// If the vcDataID that is extracted from the expanded varName is the same as the first item in the 
	// datasetsIDList, it is the reference dataset, so return the extracted SimDataBlock. If it is not the
	// first element of the datasetsIDList array, we might need to resample the datablock in time and space.
	//
	if (vcDataID.getID().equals(datasetsIDList[0].getID())) {
		simDataBlk = getDatasetControllerImpl().getSimDataBlock(outputContext,vcDataID, actualVarName, time);
		return simDataBlk;
	} else {
		// TIME INTERPOLATION of datablock
 		double[] timeArray = getDatasetControllerImpl().getDataSetTimes(vcDataID);
 		boolean bTimesEqual = checkTimeArrays(timeArray);
 		double[] timeResampledData = null;
 		int timeArrayCounter = 0;
 		long lastModified = Long.MIN_VALUE;

		if (bTimesEqual) {
			// If time arrays for both datasets are equal, no need to resample/interpolate in time, just obtain the datablock
			simDataBlk = getDatasetControllerImpl().getSimDataBlock(outputContext,vcDataID, actualVarName, time);
			timeResampledData = simDataBlk.getData();
		} else { 		
			while ((timeArrayCounter < timeArray.length-2) && (time > timeArray[timeArrayCounter+1])) {
				timeArrayCounter++;
			}
			SimDataBlock simDataBlock_1 = getDatasetControllerImpl().getSimDataBlock(outputContext,vcDataID, actualVarName, timeArray[timeArrayCounter]);
			
			double[] data_1 = simDataBlock_1.getData();
			if ((timeArrayCounter+1)<(timeArray.length-1)){
				SimDataBlock simDataBlock_2 = getDatasetControllerImpl().getSimDataBlock(outputContext,vcDataID, actualVarName, timeArray[timeArrayCounter+1]);
				lastModified = simDataBlock_2.getPDEDataInfo().getTimeStamp();
				double[] data_2 = simDataBlock_2.getData();			
				timeResampledData = new double[data_1.length];
				//
				// apply first order linear basis for test data interpolation. Interpolate for each indx in the datablock.
				//
				for (int m = 0; m < data_1.length; m++) {
					timeResampledData[m] = data_1[m] + (data_2[m]-data_1[m])*(time - timeArray[timeArrayCounter])/(timeArray[timeArrayCounter+1] - timeArray[timeArrayCounter]);
				}
			}else{ // past end of array, zero order interpolation
				lastModified = simDataBlock_1.getPDEDataInfo().getTimeStamp();
				timeResampledData = data_1;
			}
		}

		// SPATIAL INTERPOLATION
		CartesianMesh refMesh = null;
		CartesianMesh mesh = null;
		try {
			refMesh = getDatasetControllerImpl().getMesh(datasetsIDList[0]);
			mesh = getDatasetControllerImpl().getMesh(vcDataID);
		} catch (MathException e) {
			e.printStackTrace(System.out);
			throw new RuntimeException("Could not get Mesh for reference or given dataID");
		}
		
		double[] spaceResampledData = null;

		// Check origin and extent of reference and given dataset. If they don't match, cannot resample spatially
		if (!mesh.getExtent().compareEqual(refMesh.getExtent()) || !mesh.getOrigin().compareEqual(refMesh.getOrigin()) )  {
			throw new RuntimeException("Different origins and/or extents for the 2 geometries. Cannot compare the 2 simulations");
		}

		// Get dimension of geometry for reference and given datasets (using mesh variables!) to use appropriate
		// resampling algorithm. Check if mesh sizes for the 2 datasets are equal, then there is no need to 
		// spatially resample the second dataset.

		int dimension = mesh.getGeometryDimension();
		int refDimension = refMesh.getGeometryDimension();
		
		if (mesh.getSizeX() != refMesh.getSizeX() || mesh.getSizeY() != refMesh.getSizeY() || mesh.getSizeZ() != refMesh.getSizeZ()) {
			if (varDatasetID.getVariableType().equals(VariableType.VOLUME)){
				if (dimension == 1 && refDimension == 1) {
					spaceResampledData = MathTestingUtilities.resample1DSpatial(timeResampledData, mesh, refMesh);
				} else if (dimension == 2 && refDimension == 2) {
					spaceResampledData = MathTestingUtilities.resample2DSpatial(timeResampledData, mesh, refMesh);
				} else if (dimension == 3 && refDimension == 3) {
					spaceResampledData = MathTestingUtilities.resample3DSpatial(timeResampledData, mesh, refMesh);
				} else {
					throw new RuntimeException("Comparison of 2 simulations with different geometry dimensions are not handled at this time!");
				}
			}else{
				throw new RuntimeException("spatial resampling for variable type: "+varDatasetID.getVariableType().getTypeName()+" not supported");
			}
		} else {
			//
			// no space resampling required
			//
			if (varDatasetID.getVariableType().equals(VariableType.MEMBRANE)){
				//
				// membrane variables may need to be reordered to correspond to the reference mesh.
				//
				if (membraneIndexMapping==null){
					membraneIndexMapping = mesh.getMembraneIndexMapping(refMesh);
				}
				spaceResampledData = new double[timeResampledData.length];
				for (int i = 0; i < timeResampledData.length; i++){
					spaceResampledData[i] = timeResampledData[membraneIndexMapping[i]];
				}
			}else{
				//
				// no reordering needed for other variable types.
				//
				spaceResampledData = timeResampledData;
			}
		}

		if (simDataBlk != null) {
			lastModified = simDataBlk.getPDEDataInfo().getTimeStamp();
		}
		PDEDataInfo pdeDataInfo = new PDEDataInfo(vcDataID.getOwner(),vcDataID.getID(),varName,time,lastModified);
		if (spaceResampledData!=null){
			return new SimDataBlock(pdeDataInfo,spaceResampledData,varDatasetID.getVariableType());
		}else{
			return null;
		}		
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
		ProgressListener progressListener) throws DataAccessException,IOException{

//	int wantedTimecount = 0;
//	for (int i = 0; i < wantsThisTime.length; i++) {
//		if(wantsThisTime[i]){
//			wantedTimecount++;
//		}
//	}
//	double[][][] results = new double[wantedTimecount][varNames.length][];
//	for (int i = 0; i < wantedTimecount; i++) {
//		for (int j = 0; j < varNames.length; j++) {
//			results[i][j] = new double[indexes[j].length];
//		}
//	}
//	wantedTimecount = 0;
//	for (int i = 0; i < wantsThisTime.length; i++) {
//		if(wantsThisTime[i]){
//			for (int j = 0; j < varNames.length; j++) {
//				SimDataBlock simDataBlock = getSimDataBlock(varNames[j], getDataTimes()[i]);
//				double[] data = simDataBlock.getData();
//				for (int k = 0; k < indexes[j].length; k++) {
//					results[wantedTimecount][j][k] = data[indexes[j][k]];
//				}
//			}
//			wantedTimecount++;
//		}
//	}
//	return results;
	
	
//	boolean bAllTimesSame = true;
//	double[] lastTimes = null;
//	for (int i = 0; i < varNames.length; i++) {
//		VCDataIdentifier tempVCDataIdentifier = getVCDataIdentifierFromDataId(varNames[i]);
//		VCData vcData = dataSetControllerImpl.getVCData(tempVCDataIdentifier);
//		double[] tempTimes = vcData.getDataTimes();
//		if(lastTimes == null){
//			lastTimes = tempTimes;
//			continue;
//		}
//		if(tempTimes.length != lastTimes.length){
//			bAllTimesSame = false;
//			break;
//		}
//		for (int j = 0; j < tempTimes.length; j++) {
//			if(tempTimes[j] != lastTimes[j]){
//				bAllTimesSame = false;
//				break;
//			}
//		}
//		if(!bAllTimesSame){
//			break;
//		}
//	}
//	if(bAllTimesSame){
//		for (int i = 0; i < varNames.length; i++) {
//			getSimDataBlock(varName, time)			
//		}
//	}
	
	
	
	//
	// gather list of where to find each variable
	//
	VCDataIdentifier[] varVCDataIDs = new VCDataIdentifier[varNames.length];
	DataSetIdentifier[] varDataSetIDs = new DataSetIdentifier[varNames.length];
	for (int i = 0; i < varNames.length; i++) {
		varVCDataIDs[i] = getVCDataIdentifierFromDataId(varNames[i]);
		varDataSetIDs[i] = getDataSetIdentifier(varNames[i]);
	}
	
	//
	// length of times to be returned.
	//
	int resultsCount = 0;
	for(int i=0;i<wantsThisTime.length;i+= 1){
		if(wantsThisTime[i]){
			resultsCount+= 1;
		}
	}
	double[] timeArray = getDataTimes();
	double[] globalTimesActuallyUsed = new double[resultsCount];
	int globalTimesActuallyUsedCounter = 0;
	for(int i=0;i<wantsThisTime.length;i+= 1){
		if(wantsThisTime[i]){
			globalTimesActuallyUsed[globalTimesActuallyUsedCounter++] = timeArray[i];
		}
	}

	//
	// for each Dataset, create subtask
	//
	double[][][] results = new double[resultsCount][varNames.length][];
	for (int i = 0; i < varVCDataIDs.length; i++) {
		VCData vcData = dataSetControllerImpl.getVCData(varVCDataIDs[i]);
		// get data specific time array
		double[] localTimeArray = vcData.getDataTimes();
		// trim time array and set booleans
		boolean[] localWantsThisTime = new boolean[localTimeArray.length];
		for (int j = 0; j < wantsThisTime.length; j++) {
			if (wantsThisTime[j]){
				boolean found = false;
				// look for previous index in local array and set previous and next to true
				for (int k = 0; k < localTimeArray.length; k++) {
					if (localTimeArray[k]==timeArray[j]){
						localWantsThisTime[k]=true;
						found = true;
						break;
					}
					if (k<localTimeArray.length-2 && localTimeArray[k]<timeArray[j] && localTimeArray[k+1]>timeArray[j]){
						localWantsThisTime[k]=true;
						localWantsThisTime[k+1]=true;
						found = true;
					}
				}
				if (!found){
					if (timeArray[0]<localTimeArray[0]){
						localWantsThisTime[0] = true;
					}else if (timeArray[timeArray.length-1]<localTimeArray[localWantsThisTime.length-1]){
						localWantsThisTime[localWantsThisTime.length-1] = true;
					}
				}
			}
		}	
		String fullyQualifiedName = varDataSetIDs[i].getName();
		int indx = fullyQualifiedName.indexOf(".");
		String dataIDString = fullyQualifiedName.substring(indx+1, fullyQualifiedName.length());
		double[][][] partialResults = vcData.getSimDataTimeSeries0(outputContext,new String[] { dataIDString }, new int[][] { indexes[i] }, localWantsThisTime, spatialStatsInfo, progressListener);

		//
		// get local results times array
		//
		int localResultsCount = 0;
		for(int ii=0;ii<localWantsThisTime.length;ii+= 1){
			if(localWantsThisTime[ii]){
				localResultsCount+= 1;
			}
		}
		double[] localTimesActuallyUsed = new double[localResultsCount];
		int localTimesActuallyUsedCounter = 0;
		for(int ii=0;ii<localWantsThisTime.length;ii+= 1){
			if(localWantsThisTime[ii]){
				localTimesActuallyUsed[localTimesActuallyUsedCounter++] = localTimeArray[ii];
			}
		}
		
		//
		// based on local and global times arrays (that are actually used)
		// iterpolate local results back to the global times
		//
		for (int k = 0; k < globalTimesActuallyUsed.length; k++) {
			// find value in local results
			int leftIndex = -1;  // greatest index where localTimesActuallyUsed[leftIndex] <= globalTimesActuallyUsed[k]
			int rightIndex = -1;
			for (int j = 0; j < localTimesActuallyUsed.length; j++) {
				if (localTimesActuallyUsed[j] <= globalTimesActuallyUsed[k]){
					leftIndex=j;
				}
				if (rightIndex==-1 && localTimesActuallyUsed[j] > globalTimesActuallyUsed[k]){
					rightIndex=j;
					break;
				}
			}
			if (leftIndex>-1 && rightIndex>-1){
				// 1st order interpolation ... value = (alpha)*results[left] + (1-alpha)*results[right]
				// alpha is zero if time is on left, is one if time is on right
				double alpha = (globalTimesActuallyUsed[k]-localTimesActuallyUsed[leftIndex]) / 
								(localTimesActuallyUsed[rightIndex]-localTimesActuallyUsed[leftIndex]);
				int partialResultsCount = partialResults[0][0].length;
				results[k][i] = new double[partialResultsCount];
				for (int index = 0; index < partialResultsCount; index++) {
					double leftResults = partialResults[leftIndex][0][index];
					double rightResults = partialResults[rightIndex][0][index];
					results[k][i][index] = alpha*leftResults + (1.0-alpha)*rightResults;
				}
			}else if (leftIndex>-1 && rightIndex==-1){
				// just use leftIndex
				results[k][i] = partialResults[leftIndex][0];
			}else if (leftIndex==-1 && rightIndex>-1){
				// just use rightIndex
				results[k][i] = partialResults[rightIndex][0];
			}else{
				throw new RuntimeException("error interpolating results in MergedData");
			}
		}
	}
	return results;
}



/**
 * This method was created in VisualAge.
 * @return long
 */
public long getSizeInBytes() {
	return SizeInBytes;
}


/**
 * This method was created in VisualAge.
 * @return java.lang.String[]
 */
public DataIdentifier[] getVarAndFunctionDataIdentifiers(OutputContext outputContext) throws IOException, DataAccessException {
	getFunctionDataIdentifiers(outputContext);
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
 * Insert the method's description here.
 * Creation date: (10/11/00 5:16:06 PM)
 * @return cbit.vcell.math.Function
 * @param name java.lang.String
 */
private VCDataIdentifier getVCDataIdentifierFromDataId(String dataID){
	//
	// Given the composite dataIdentifier name for a variable in the merged data, extract the
	// corresponding dataset.
	//
	int indx = dataID.indexOf(".");
	String dataIDString = dataID.substring(0, indx);

	for (int i = 0; i < datasetsIDList.length; i++){
		if (dataSetPrefix[i].equals(dataIDString)) {
			return datasetsIDList[i];
		}
	}
	return null;
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.solvers.CartesianMesh
 */
int[] getVolumeSize() throws IOException, DataAccessException {
	return getDatasetControllerImpl().getVCData(datasetsIDList[0]).getVolumeSize();
}


/**
 * Insert the method's description here.
 * Creation date: (9/29/2003 2:04:19 PM)
 * @param sim1 cbit.vcell.simdata.SimulationData
 * @param sim2 cbit.vcell.simdata.SimulationData
 */
private void mergeDatasets() throws DataAccessException, IOException {

	// Method to merge 2 datasets. 

	/*if ((!data1.getIsODEData() && data2.getIsODEData()) || (data1.getIsODEData() && !data2.getIsODEData()) ) {
		// REMOVE THIS CHECK IN FUTURE - LATER , WE NEED TO BE ABLE TO COMPARE PDE AGAINST ODE ALSO.
		throw new RuntimeException("\n >>>> The two datasets are of different types!! <<<<\n");
	}*/

	
	//
	// Add dataIdentifiers from all datasetIDs in the merged data, update dataSetIdentifiers and dataIdentifiers
	//
	dataSetIdentifierList.clear();
	for (int i = 0; i < datasetsIDList.length; i++) {
		DataIdentifier[] dataIDs = getDatasetControllerImpl().getDataIdentifiers(null,datasetsIDList[i]);
		for (int j = 0; j < dataIDs.length; j++) {
			String newdataIDName = dataSetPrefix[i]+"."+dataIDs[j].getName();
			DataSetIdentifier newDataSetID = new DataSetIdentifier(newdataIDName,dataIDs[j].getVariableType(), dataIDs[j].getDomain(), dataIDs[j].isFunction());
			if (!dataSetIdentifierList.contains(newDataSetID)) {
				dataSetIdentifierList.addElement(newDataSetID);
			}
		}
	}
}


/**
 * Insert the method's description here.
 * Creation date: (1/15/2004 11:48:25 AM)
 */
private void readFunctions(OutputContext outputContext) throws FileNotFoundException, IOException {

	Vector<AnnotatedFunction> annotatedFuncsVector = FunctionFileGenerator.readFunctionsFile(getFunctionsFile(), dataInfo.getID());

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
		AnnotatedFunction annotatedFunction = annotatedFuncsVector.elementAt(i);
		try {
			functionBindAndSubstitute(annotatedFunction);
			addFunctionToList(annotatedFunction);
		} catch (ExpressionException e) {
			throw new RuntimeException("Could not add function "+annotatedFunction.getName()+" to annotatedFunctionList");
		}
	}
}


/**
 * Insert the method's description here.
 * Creation date: (10/11/00 1:28:51 PM)
 * @param function cbit.vcell.math.Function
 */
private ODESolverResultSet resampleODEData(ODESimData refSimdata, ODESimData simData) throws ExpressionException {

	// If simData and refSimdata times are equal, return simData without resampling.
	// Else resampling is necessary.

	double[] refTimeArray = refSimdata.extractColumn(refSimdata.findColumn("t"));
	double[] timeArray    = simData.extractColumn(simData.findColumn("t"));

	if (refTimeArray.length == timeArray.length) {
		boolean bEqual = true;
		for (int i = 0; i < refTimeArray.length; i++) {
			if (refTimeArray[i] == timeArray[i]) {
				bEqual = bEqual && true;
			} else {
				bEqual = bEqual && false;
			}
		}
		if (bEqual) {
			return simData;
		}
	}

	ODESolverResultSet newODEresultSet = new ODESolverResultSet();
	for (int i = 0; i < simData.getDataColumnCount(); i++) {
		if(simData.getDataColumnDescriptions()[i] instanceof ODESolverResultSetColumnDescription)
		{
			ODESolverResultSetColumnDescription colDesc = ((ODESolverResultSetColumnDescription)simData.getDataColumnDescriptions()[i]);
			newODEresultSet.addDataColumn(colDesc);
		}
	}
	for (int i = 0; i < simData.getFunctionColumnCount(); i++) {
		FunctionColumnDescription colDesc = simData.getFunctionColumnDescriptions()[i];
		newODEresultSet.addFunctionColumn(colDesc);
	}

	double[][] resampledData = new double[refTimeArray.length][simData.getDataColumnCount()];
	for (int i = 0; i < simData.getDataColumnCount(); i++){
		ColumnDescription colDesc = simData.getDataColumnDescriptions()[i];

		// If it is the first column (time), set value in new SimData to the timeArray values in refSimData.
		if (i == 0 && colDesc.getName().equals("t")) {
			for (int j = 0; j < refTimeArray.length; j++) {
				resampledData[j][i] = refTimeArray[j];
			}
			continue;
		}

		double[] data = simData.extractColumn(i);
		int k = 0;
		for (int j = 0; j < refTimeArray.length; j++) {  // CHECK IF refTimeArray or timeArry has to be used here,
			while ((k < timeArray.length-2) && (refTimeArray[j] > timeArray[k+1])) {
				k++;
			}
			// apply first order linear basis for reference data interpolation.
			resampledData[j][i] = data[k] + (data[k+1]-data[k])*(refTimeArray[j] - timeArray[k])/(timeArray[k+1] - timeArray[k]);
			
		}
	}

	for (int i = 0; i < refTimeArray.length; i++) {
		newODEresultSet.addRow(resampledData[i]);
	}

	return newODEresultSet;
}

public void getEntries(Map<String, SymbolTableEntry> entryMap) {
	ReservedMathSymbolEntries.getAll(entryMap, false);
	for (DataSetIdentifier dsi : dataSetIdentifierList) {
		entryMap.put(dsi.getName(), dsi);
	}
}

public boolean isChombo() throws DataAccessException, IOException {
	try {
		return getDatasetControllerImpl().getIsChombo(datasetsIDList[0]);
	} catch (DataAccessException e) {
		e.printStackTrace(System.out);
		throw e;
	} catch (FileNotFoundException e) {
		e.printStackTrace(System.out);
		throw e;
	}
}

@Override
public ChomboFiles getChomboFiles() throws IOException, XmlParseException, ExpressionException {
	throw new RuntimeException("MergedData.getChomboFiles() not yet implemented");
}

@Override
public VCellSimFiles getVCellSimFiles() throws FileNotFoundException, DataAccessException {
	throw new RuntimeException("MergedData.getVCellSimFiles() not yet implemented");
}


}
