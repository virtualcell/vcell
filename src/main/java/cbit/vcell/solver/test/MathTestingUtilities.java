/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.solver.test;
import java.util.Enumeration;
import java.util.Vector;

import org.vcell.util.BeanUtils;
import org.vcell.util.Coordinate;
import org.vcell.util.CoordinateIndex;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.VCDocument;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.data.PDEDataViewer;
import cbit.vcell.geometry.AnalyticSubVolume;
import cbit.vcell.geometry.SubVolume;
import cbit.vcell.mapping.AbstractMathMapping;
import cbit.vcell.mapping.DiffEquMathMapping;
import cbit.vcell.mapping.MappingException;
import cbit.vcell.math.CompartmentSubDomain;
import cbit.vcell.math.Constant;
import cbit.vcell.math.Equation;
import cbit.vcell.math.FastInvariant;
import cbit.vcell.math.FastRate;
import cbit.vcell.math.FastSystem;
import cbit.vcell.math.FilamentRegionVariable;
import cbit.vcell.math.FilamentVariable;
import cbit.vcell.math.Function;
import cbit.vcell.math.FunctionColumnDescription;
import cbit.vcell.math.InsideVariable;
import cbit.vcell.math.JumpCondition;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.MathException;
import cbit.vcell.math.MathUtilities;
import cbit.vcell.math.MemVariable;
import cbit.vcell.math.MembraneRegionVariable;
import cbit.vcell.math.MembraneSubDomain;
import cbit.vcell.math.ODESolverResultSetColumnDescription;
import cbit.vcell.math.OdeEquation;
import cbit.vcell.math.OutsideVariable;
import cbit.vcell.math.PdeEquation;
import cbit.vcell.math.ReservedVariable;
import cbit.vcell.math.SubDomain;
import cbit.vcell.math.Variable;
import cbit.vcell.math.Variable.Domain;
import cbit.vcell.math.VariableHash;
import cbit.vcell.math.VariableType;
import cbit.vcell.math.VolVariable;
import cbit.vcell.math.VolumeRegionVariable;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.numericstest.ConstructedSolutionTemplate;
import cbit.vcell.numericstest.SolutionTemplate;
import cbit.vcell.numericstest.TestCaseNew;
import cbit.vcell.opt.ReferenceData;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.ExpressionTerm;
import cbit.vcell.parser.SimpleSymbolTable;
import cbit.vcell.simdata.DataIdentifier;
import cbit.vcell.simdata.PDEDataManager;
import cbit.vcell.simdata.SimDataBlock;
import cbit.vcell.simdata.SimDataConstants;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationSymbolTable;
import cbit.vcell.solver.ode.ODESolverResultSet;
import cbit.vcell.solver.ode.SensVariable;
import cbit.vcell.solvers.CartesianMesh;
import cbit.vcell.util.ColumnDescription;
/**
 * Insert the type's description here.
 * Creation date: (1/16/2003 2:31:28 PM)
 * @author: Jim Schaff
 */
public class MathTestingUtilities {

/**
 * Insert the method's description here.
 * Creation date: (1/17/2003 3:48:36 PM)
 * @param testResultSet cbit.vcell.solver.ode.ODESolverResultSet
 * @param referenceResultSet cbit.vcell.solver.ode.ODESolverResultSet
 */
public static double calcWeightedSquaredError(ODESolverResultSet testResultSet, ReferenceData referenceData)  {
	try {
		double[] testRSTimes = testResultSet.extractColumn(testResultSet.findColumn("t"));
		double[] refRSTimes = referenceData.getDataByColumn(referenceData.findColumn("t"));
		double L2Error = 0.0;
		
		String[] varsToTest = referenceData.getColumnNames();
		for (int i = 0; i < varsToTest.length; i++){
			if (varsToTest[i].equals("t")) {
				continue;
			}
			double weight = referenceData.getColumnWeights()[i];
			int refRSIndex = referenceData.findColumn(varsToTest[i]);
			if (refRSIndex==-1){
				throw new RuntimeException("variable '"+varsToTest[i]+"' not found in reference dataset");
			}
			double refData[] = referenceData.getDataByColumn(refRSIndex);
			
			int testRSIndex = testResultSet.findColumn(varsToTest[i]);
			if (testRSIndex==-1){
				throw new RuntimeException("variable '"+varsToTest[i]+"' not found in test dataset");
			}
			double testData[] = testResultSet.extractColumn(testRSIndex);

			// Resampling test data
			int k = 0; 	
			for (int j = 0; j < refRSTimes.length; j++) {
				//
				// choose two points (testData[k] and testData[k+1]) in test data for interpolation (or extrapolation if outside the data)
				//
				// a)  extrapolate backward (in time) for points which are before testData
				// b)  interpolate the values that fall within the test dataset.
				// c)  extrapolate forward (in time) for points which are after testData
				//
				while ((k < testData.length-2) && (refRSTimes[j] > testRSTimes[k+1])) {
					k++;
				}
				//
				// apply first order linear basis for reference data interpolation.
				//
				double resampledTestData = testData[k] + (testData[k+1]-testData[k])*(refRSTimes[j] - testRSTimes[k])/(testRSTimes[k+1] - testRSTimes[k]);
				double diff = (resampledTestData-refData[j]);				
				L2Error += weight*diff*diff;
			}
		}
		return L2Error;
	}catch (ExpressionException e){
		e.printStackTrace(System.out);
		throw new RuntimeException(e.getMessage());
	}
}

/**
 * Insert the method's description here.
 * Creation date: (8/20/2003 12:58:10 PM)
 */
public static SimulationComparisonSummary comparePDEResults(SimulationSymbolTable testSimSymbolTable, 
		PDEDataManager testDataManager,SimulationSymbolTable refSimSymbolTable, PDEDataManager refDataManager, String varsToCompare[],
		double absErrorThreshold, double relErrorThreshold,
		VCDocument refDocument,PDEDataViewer.DataInfoProvider refDataInfoProvider,
		VCDocument testDocument,PDEDataViewer.DataInfoProvider testDataInfoProvider) throws DataAccessException, ExpressionException {

	java.util.Hashtable<String, DataErrorSummary> tempVarHash = new java.util.Hashtable<String, DataErrorSummary>();
	boolean bTimesEqual = true;
	
	double[] testTimeArray = testDataManager.getDataSetTimes();
	double[] refTimeArray = refDataManager.getDataSetTimes();

	if (testTimeArray.length != refTimeArray.length) {
		bTimesEqual = false;
		// throw new RuntimeException("Data times for reference and test simulations don't match, cannot compare the two simulations!");
	} else {
		for (int i = 0; i < testTimeArray.length; i++) {
			if (testTimeArray[i] != refTimeArray[i]) {
				bTimesEqual = false;
			}
		}
	}

	//if (!checkSimVars(testSim, refSim)) {
		////String errorS = "TestVars - ";
		//Variable[] testVars = testSim.getVariables();
		////for(int i =0;i<vars.length;i+= 1){
			////errorS+= vars[i].getName()+" ";
		////}
		////errorS+=" <<>> RefVars - ";
		//Variable[] refVars = refSim.getVariables();
		////for(int i =0;i<vars.length;i+= 1){
			////errorS+= vars[i].getName()+" ";
		////}
		//throw new RuntimeException(
			//"VarNotMatch testLength="+(testVars != null?testVars.length+"":"null")+" refLength="+(refVars != null?refVars.length+"":"null"));
	//}
		
	CartesianMesh testMesh = testDataManager.getMesh();
	MathDescription testMathDesc = testSimSymbolTable.getSimulation().getMathDescription();
	
	//Variable[] refVars = refSim.getVariables();	
	MathDescription refMathDesc = refSimSymbolTable.getSimulation().getMathDescription();
	CartesianMesh refMesh = refDataManager.getMesh();

	int[] membraneIndexMapping = null;
	
	// Get volumeSubdomains from mathDesc/mesh and store in lookupTable for testSimulation
	int testNumVol = testMesh.getSizeX()*testMesh.getSizeY()*testMesh.getSizeZ();
	CompartmentSubDomain[] testVolSubDomainLookup = new CompartmentSubDomain[testNumVol];
	for (int i = 0; i < testNumVol; i++){
		int subVolumeIndex = testMesh.getSubVolumeFromVolumeIndex(i);
		SubVolume subVolume = testMathDesc.getGeometry().getGeometrySpec().getSubVolume(subVolumeIndex);
		CompartmentSubDomain compSubDomain = testMathDesc.getCompartmentSubDomain(subVolume.getName());	
		testVolSubDomainLookup[i] = compSubDomain;	
	}

	// Get membraneSubdomains from mathDesc/mesh and store in lookupTable for testSimulation
	int testNumMem = testMesh.getMembraneElements().length;
	MembraneSubDomain[] testMemSubDomainLookup = new MembraneSubDomain[testNumMem];
	for (int i = 0; i < testNumMem; i++){
		int insideVolIndex = testMesh.getMembraneElements()[i].getInsideVolumeIndex();
		int outsideVolIndex = testMesh.getMembraneElements()[i].getOutsideVolumeIndex();
		MembraneSubDomain memSubDomain = testMathDesc.getMembraneSubDomain(testVolSubDomainLookup[insideVolIndex],testVolSubDomainLookup[outsideVolIndex]);
		testMemSubDomainLookup[i] = memSubDomain;	
	}

	// Get volumeSubdomains from mathDesc/mesh and store in lookupTable for refSimulation
	int refNumVol = refMesh.getSizeX()*refMesh.getSizeY()*refMesh.getSizeZ();
	CompartmentSubDomain[] refVolSubDomainLookup = new CompartmentSubDomain[refNumVol];
	for (int i = 0; i < refNumVol; i++){
		int subVolumeIndex = refMesh.getSubVolumeFromVolumeIndex(i);
		SubVolume subVolume = refMathDesc.getGeometry().getGeometrySpec().getSubVolume(subVolumeIndex);
		CompartmentSubDomain compSubDomain = refMathDesc.getCompartmentSubDomain(subVolume.getName());	
		refVolSubDomainLookup[i] = compSubDomain;	
	}

	// Get membraneSubdomains from mathDesc/mesh and store in lookupTable for refSimulation
	int refNumMem = refMesh.getMembraneElements().length;
	MembraneSubDomain[] refMemSubDomainLookup = new MembraneSubDomain[refNumMem];
	for (int i = 0; i < refNumMem; i++){
		int insideVolIndex = refMesh.getMembraneElements()[i].getInsideVolumeIndex();
		int outsideVolIndex = refMesh.getMembraneElements()[i].getOutsideVolumeIndex();
		MembraneSubDomain memSubDomain = refMathDesc.getMembraneSubDomain(refVolSubDomainLookup[insideVolIndex],refVolSubDomainLookup[outsideVolIndex]);
		refMemSubDomainLookup[i] = memSubDomain;	
	}	

	SimulationComparisonSummary simComparisonSummary = new SimulationComparisonSummary();
	String hashKey = new String("");
	DataErrorSummary tempVar = null;
	
	DataIdentifier[] refDataIDs = refDataManager.getDataIdentifiers();
	// for each var, do the following :
	for (int i = 0; i < varsToCompare.length; i++){
		DataIdentifier refDataID = null;
		for (int j = 0; j < refDataIDs.length; j++){
			if (refDataIDs[j].getName().equals(varsToCompare[i])){
				refDataID = refDataIDs[j];
				break;
			}
		}
		//
		//Find REFERENCE variable
		//
		Variable refVar = getSimVar(refSimSymbolTable, varsToCompare[i]);
		if(refVar == null){//Should only happen if TEST sims were generated 'post-domains' and REFERENCE sims were generated 'pre-domains'
			if(refDataID != null){
				throw new RuntimeException("Unexpected reference condition: '"+varsToCompare[i]+"' not found in symboltable but was found in dataidentifiers");
			}
			if(testDocument instanceof BioModel){//Only BioModels need to be checked
				//Look in TEST for a speciescontext with matching species name
				System.out.println("ReferenceVariable: using alternate method to find '"+varsToCompare[i]+"'");
				BioModel refBioModel = (BioModel)refDocument;
				BioModel testBioModel = (BioModel)testDocument;
				SpeciesContext testSpeciesContext = testBioModel.getModel().getSpeciesContext(varsToCompare[i]);
				if(testSpeciesContext != null){
					refVar = refSimSymbolTable.getVariable(testSpeciesContext.getSpecies().getCommonName());
					for (int j = 0; j < refDataIDs.length; j++){
						if (refDataIDs[j].getName().equals(testSpeciesContext.getSpecies().getCommonName())){
							refDataID = refDataIDs[j];
							break;
						}
					}
				}
			}
			if(refVar == null || refDataID == null){
				Simulation refSim = refSimSymbolTable.getSimulation();
				throw new RuntimeException("The variable "+varsToCompare[i]+" was not found in Simulation ("+refSim.getName()+" "+refSim.getVersion().getDate()+")\n");
			}
		}
		//
		//Find TEST variable (assumed to have sims generated with a software version later than REFERENCE)
		//
		Variable testVar = getSimVar(testSimSymbolTable, varsToCompare[i]);
		if(testVar == null){//Should only happen if TEST sims were generated 'post-domains' and REFERENCE sims were generated 'pre-domains'
			System.out.println("TestVariable: using alternate method to find '"+varsToCompare[i]+"'");
			BioModel testBioModel = (BioModel)testDocument;
			SpeciesContext[] speciesContexts = testBioModel.getModel().getSpeciesContexts();
			boolean bSkip = false;
			for (int j = 0; j < speciesContexts.length; j++) {
				if(speciesContexts[j].getSpecies().getCommonName().equals(varsToCompare[i])){
					testVar = testSimSymbolTable.getVariable(speciesContexts[j].getName());
					if(testVar == null){
						throw new RuntimeException("Speciescontext name '"+speciesContexts[j].getName()+"' not found in testsimsymboltable");
					}
					//If we got here it means at least one matching speciescontext was found in TEST with
					//a species name matching varsToCompare[i].  We can skip because the matching speciesconetext
					//will be used to do a comparison at some point.
					bSkip = true;
					break;
				}
			}
			if(bSkip){//these are tested already using full simcontext names
				System.out.println("Skipping '"+varsToCompare[i]+"' as lookup in testSimSymbolTable");
				continue;
			}
			Simulation refSim = refSimSymbolTable.getSimulation();
			throw new RuntimeException("The variable "+varsToCompare[i]+" was not found in Simulation ("+refSim.getName()+" "+refSim.getVersion().getDate()+")\n");
		}
		// for each time in timeArray. ('t' is used to index the testTimeArray, for interpolation purposes.)
		int t = 0;
		for (int j = 0; j < refTimeArray.length; j++){
			// get data block from varName, data from datablock
			SimDataBlock refSimDataBlock = refDataManager.getSimDataBlock(refVar.getName(), refTimeArray[j]);
			double[] refData = refSimDataBlock.getData();
			double[] resampledTestData = null;

			if (bTimesEqual) {
				// If time arrays for both sims are equal, no need to resample/interpolate, just obtain the datablock from dataManager
				SimDataBlock testSimDataBlock = testDataManager.getSimDataBlock(testVar.getName(), testTimeArray[j]);
				resampledTestData = testSimDataBlock.getData();
			} else {
				// Time resampling (interpolation) needed.
				while ((t < testTimeArray.length-2) && (refTimeArray[j] >= testTimeArray[t+1])) {
					t++;
				}
				SimDataBlock testSimDataBlock_1 = testDataManager.getSimDataBlock(testVar.getName(), testTimeArray[t]);
				double[] testData_1 = testSimDataBlock_1.getData();
				SimDataBlock testSimDataBlock_2 = testDataManager.getSimDataBlock(testVar.getName(), testTimeArray[t+1]);
				double[] testData_2 = testSimDataBlock_2.getData();			
				resampledTestData = new double[testData_1.length];
				//
				// apply first order linear basis for test data interpolation. Interpolate for each indx in the datablock.
				//
				for (int m = 0; m < testData_1.length; m++) {
					resampledTestData[m] = testData_1[m] + (testData_2[m]-testData_1[m])*(refTimeArray[j] - testTimeArray[t])/(testTimeArray[t+1] - testTimeArray[t]);
				}
			}

			// Spatial resampling (interpolation) ...
			double[] spaceResampledData = new double[refData.length];
			if (!testMathDesc.getGeometry().getExtent().compareEqual(refMathDesc.getGeometry().getExtent()) || 
					!testMathDesc.getGeometry().getOrigin().compareEqual(refMathDesc.getGeometry().getOrigin()) )  {
				throw new RuntimeException("Different origins and/or extents for the 2 geometries. Cannot compare the 2 simulations");
			}
				
			if (testMesh.getSizeX() != refMesh.getSizeX() || testMesh.getSizeY() != refMesh.getSizeY() || testMesh.getSizeZ() != refMesh.getSizeZ()) {
				if (testVar instanceof VolVariable){
					if (testMathDesc.getGeometry().getDimension() == 1 && refMathDesc.getGeometry().getDimension() == 1) {
						spaceResampledData = resample1DSpatial(resampledTestData, testMesh, refMesh);
					} else if (testMathDesc.getGeometry().getDimension() == 2 && refMathDesc.getGeometry().getDimension() == 2) {
						spaceResampledData = resample2DSpatial(resampledTestData, testMesh, refMesh);
					} else if (testMathDesc.getGeometry().getDimension() == 3 && refMathDesc.getGeometry().getDimension() == 3) {
						spaceResampledData = resample3DSpatial(resampledTestData, testMesh, refMesh);
					} else {
						throw new RuntimeException("Comparison of 2 simulations with different geometry dimensions are not handled at this time!");
					}
				}else{
					throw new RuntimeException("spatial resampling for variable type: "+testVar.getClass().getName()+" not supported");
				}
			} else {
				// no space resampling required
				if (testVar instanceof MemVariable){
					//
					// membrane variables may need to be reordered to correspond to the reference mesh.
					//
					if (membraneIndexMapping == null){
						membraneIndexMapping = testMesh.getMembraneIndexMapping(refMesh);
					}
					spaceResampledData = new double[resampledTestData.length];
					for (int k = 0; k < resampledTestData.length; k++){
						spaceResampledData[k] = resampledTestData[membraneIndexMapping[k]];
					}
				}else{
					//
					// no reordering needed for other variable types.
					//
					spaceResampledData = resampledTestData;
				}
			}

			// for each point in data block ...
			testDataInfoProvider.getPDEDataContext().setVariableName(testVar.getName());
			for (int k = 0; k < refData.length; k++) {
				if(!testDataInfoProvider.isDefined(k)){
					continue;
				}
				// Determine maxRef, minRef, maxAbsErr for variable
				//SubDomain testSubDomain = null;
				String sn = null;
				VariableType refVarType = refDataID.getVariableType();
				if (refVarType.equals(VariableType.VOLUME)){
					//testSubDomain = refVolSubDomainLookup[k];
					sn = refVolSubDomainLookup[k].getName();
				} else if (refVarType.equals(VariableType.MEMBRANE)){
					//testSubDomain = refMemSubDomainLookup[k];
					sn = refMemSubDomainLookup[k].getName();
				} else if (refVarType.equals(VariableType.MEMBRANE_REGION)){
					sn = "MRV_"+i;
				} else if (refVarType.equals(VariableType.VOLUME_REGION)){
					sn = "VRV_"+i;
				} else{
					throw new RuntimeException("Var "+refVar.getName()+" not supported yet!");
				}

				//hashKey = refVar.getName()+":"+testSubDomain.getName();
				hashKey = refVar.getName()+":"+sn;
				tempVar = tempVarHash.get(hashKey);
				if (tempVar == null) {
					tempVar = new DataErrorSummary(null);
					tempVarHash.put(hashKey, tempVar);
				}
				tempVar.addDataValues(refData[k], spaceResampledData[k], refTimeArray[j], k,absErrorThreshold,relErrorThreshold);
			}  // end for (k)
		} // end for (j)
	} // end for (i)

	Enumeration<String> enumKeys = tempVarHash.keys();
	while (enumKeys.hasMoreElements()){
		String key = enumKeys.nextElement();
		DataErrorSummary tempVarSummary = tempVarHash.get(key);
		simComparisonSummary.addVariableComparisonSummary(
			new VariableComparisonSummary(key,
				tempVarSummary.getMinRef(), tempVarSummary.getMaxRef(), 
				tempVarSummary.getMaxAbsoluteError(), tempVarSummary.getMaxRelativeError(), tempVarSummary.getL2Norm(),
				tempVarSummary.getTimeAtMaxAbsoluteError(), tempVarSummary.getIndexAtMaxAbsoluteError(),
				tempVarSummary.getTimeAtMaxRelativeError(), tempVarSummary.getIndexAtMaxRelativeError())
		);
	}		
	return simComparisonSummary;
}


/**
 * Insert the method's description here.
 * Creation date: (8/20/2003 12:58:10 PM)
 */
public static SimulationComparisonSummary comparePDEResultsWithExact(SimulationSymbolTable simSymbolTable, PDEDataManager dataManager,String type,
		double absErrorThreshold, double relErrorThreshold) throws DataAccessException, ExpressionException {

	java.util.Hashtable<String, DataErrorSummary> tempVarHash = new java.util.Hashtable<String, DataErrorSummary>();
	
	double[] timeArray = dataManager.getDataSetTimes();
	Variable[] vars = simSymbolTable.getVariables();
	CartesianMesh mesh = dataManager.getMesh();
	MathDescription mathDesc = simSymbolTable.getSimulation().getMathDescription();

	// Get volumeSubdomains from mathDesc/mesh and store in lookupTable
	int numVol = mesh.getSizeX()*mesh.getSizeY()*mesh.getSizeZ();
	CompartmentSubDomain[] volSubDomainLookup = new CompartmentSubDomain[numVol];
	for (int i = 0; i < numVol; i++){
		int subVolumeIndex = mesh.getSubVolumeFromVolumeIndex(i);
		SubVolume subVolume = mathDesc.getGeometry().getGeometrySpec().getSubVolume(subVolumeIndex);
		CompartmentSubDomain compSubDomain = mathDesc.getCompartmentSubDomain(subVolume.getName());	
		volSubDomainLookup[i] = compSubDomain;	
	}

	// Get membraneSubdomains from mathDesc/mesh and store in lookupTable
	int numMem = mesh.getMembraneElements().length;
	MembraneSubDomain[] memSubDomainLookup = new MembraneSubDomain[numMem];
	for (int i = 0; i < numMem; i++){
		int insideVolIndex = mesh.getMembraneElements()[i].getInsideVolumeIndex();
		int outsideVolIndex = mesh.getMembraneElements()[i].getOutsideVolumeIndex();
		MembraneSubDomain memSubDomain = mathDesc.getMembraneSubDomain(volSubDomainLookup[insideVolIndex],volSubDomainLookup[outsideVolIndex]);
		memSubDomainLookup[i] = memSubDomain;	
	}
	
	double valueArray[] = new double[4];
	SimpleSymbolTable symbolTable = new SimpleSymbolTable(new String[] {"t", "x", "y", "z"});
	int tIndex = symbolTable.getEntry("t").getIndex();
	int xIndex = symbolTable.getEntry("x").getIndex();
	int yIndex = symbolTable.getEntry("y").getIndex();
	int zIndex = symbolTable.getEntry("z").getIndex();

	SimulationComparisonSummary simComparisonSummary = new SimulationComparisonSummary();
	String hashKey = new String("");
	long dataLength = 0;
	// for each var, do the following :
	for (int i = 0; i < vars.length; i++){
		if (vars[i] instanceof VolVariable || vars[i] instanceof MemVariable || vars[i] instanceof FilamentVariable ||
				vars[i] instanceof VolumeRegionVariable || vars[i] instanceof MembraneRegionVariable || vars[i] instanceof FilamentRegionVariable) {	
			// for each time in timeArray, 
			for (int j = 0; j < timeArray.length; j++){
				if(type.equals(TestCaseNew.EXACT_STEADY)){
					if(j != (timeArray.length-1)){
						continue;
					}
				}
				// get data block from varName, data from datablock
				SimDataBlock simDataBlock = dataManager.getSimDataBlock(vars[i].getName(), timeArray[j]);
				double[] data = simDataBlock.getData();
				dataLength = data.length;
				SubDomain subDomain = null;
				Coordinate subDomainCoord = null;
				
				// for each point in data block ...
				for (int k = 0; k < dataLength; k++) {
					// 	Get subdomain from mesh (from the lookupTable), get coordinates (x,y,z) from mesh, evaluate EXACT SOLN at that coord
					if (vars[i] instanceof VolVariable) {
						subDomain = volSubDomainLookup[k];   
						subDomainCoord = mesh.getCoordinateFromVolumeIndex(k);
					} else if (vars[i] instanceof MemVariable) {
						subDomain = memSubDomainLookup[k];
						subDomainCoord = mesh.getCoordinateFromMembraneIndex(k);
					} else {
						throw new RuntimeException("Var "+vars[i].getName()+" not supported yet!");
					}
					hashKey = vars[i].getName()+":"+subDomain.getName();
					DataErrorSummary tempVar = (DataErrorSummary)tempVarHash.get(hashKey);
					if (tempVar == null) {
						Expression exp = new Expression(subDomain.getEquation(vars[i]).getExactSolution());
						exp.bindExpression(simSymbolTable);
						exp = MathUtilities.substituteFunctions(exp, simSymbolTable);
						exp = exp.flatten();
						exp.bindExpression(symbolTable);
						tempVar = new DataErrorSummary(exp);
						tempVarHash.put(hashKey, tempVar);
					}
					valueArray[tIndex] = timeArray[j];				// time
					valueArray[xIndex] = subDomainCoord.getX();		// x
					valueArray[yIndex] = subDomainCoord.getY();		// y
					valueArray[zIndex] = subDomainCoord.getZ();		// z
					double value = tempVar.getExactExp().evaluateVector(valueArray);  // EXACT soln at coord subDomainCoord
					tempVar.addDataValues(value, data[k], timeArray[j], k,absErrorThreshold,relErrorThreshold);
				}  // end for (k)
			} // end for (j)
		} // end - if (var)
	} // end for (i)
	Enumeration<String> enumKeys = tempVarHash.keys();
	while (enumKeys.hasMoreElements()){
		String key = enumKeys.nextElement();
		DataErrorSummary tempVarSummary = tempVarHash.get(key);
		simComparisonSummary.addVariableComparisonSummary(
			new VariableComparisonSummary(key,
				tempVarSummary.getMinRef(), tempVarSummary.getMaxRef(), 
				tempVarSummary.getMaxAbsoluteError(), tempVarSummary.getMaxRelativeError(), tempVarSummary.getL2Norm(),
				tempVarSummary.getTimeAtMaxAbsoluteError(), tempVarSummary.getIndexAtMaxAbsoluteError(),
				tempVarSummary.getTimeAtMaxRelativeError(), tempVarSummary.getIndexAtMaxRelativeError())
		);
	}	
	
	return simComparisonSummary;
}


/**
 * Insert the method's description here.
 * Creation date: (1/17/2003 3:48:36 PM)
 * @param testResultSet cbit.vcell.solver.ode.ODESolverResultSet
 * @param referenceResultSet cbit.vcell.solver.ode.ODESolverResultSet
 */
public static SimulationComparisonSummary compareResultSets(ODESolverResultSet testResultSet, ODESolverResultSet referenceResultSet, 
		String varsToTest[],String type,double absErrorThreshold, double relErrorThreshold) throws Exception {

	if (varsToTest==null){
		throw new IllegalArgumentException("varsToTest must not be null");
	}
	
	SimulationComparisonSummary simComparisonSummary = new SimulationComparisonSummary();
	double timeData[] = referenceResultSet.extractColumn(referenceResultSet.findColumn(ODESolverResultSet.TIME_COLUMN));
	
	for (int i = 0; i < varsToTest.length; i++){
		int refRSIndex = referenceResultSet.findColumn(varsToTest[i]);
		if (refRSIndex==-1){
			throw new RuntimeException("variable '"+varsToTest[i]+"' not found in reference dataset");
		}
		ColumnDescription refColDesc = referenceResultSet.getColumnDescriptions(refRSIndex);
		double refData[] = referenceResultSet.extractColumn(refRSIndex);
		
		int testRSIndex = testResultSet.findColumn(varsToTest[i]);
		if (testRSIndex==-1){
			throw new RuntimeException("variable '"+varsToTest[i]+"' not found in test dataset");
		}
		double testData[] = testResultSet.extractColumn(testRSIndex);
		
		DataErrorSummary tempVar = new DataErrorSummary();
		for (int j = 0; j < refData.length; j++){
			if(type.equals(TestCaseNew.EXACT_STEADY)){
				if(j != (refData.length-1)){
					continue;
				}
			}
			tempVar.addDataValues(refData[j], testData[j], timeData[j], 0,absErrorThreshold,relErrorThreshold);
		}
		simComparisonSummary.addVariableComparisonSummary(new VariableComparisonSummary(refColDesc.getName(),
			tempVar.getMinRef(),tempVar.getMaxRef(),tempVar.getMaxAbsoluteError(),tempVar.getMaxRelativeError(), tempVar.getL2Norm(),
			tempVar.getTimeAtMaxAbsoluteError(), tempVar.getIndexAtMaxAbsoluteError(),
			tempVar.getTimeAtMaxRelativeError(), tempVar.getIndexAtMaxRelativeError()));
	}
	return simComparisonSummary;
}

public static double[] interpolateArray(double[] sampleTimes, double[] dataTimes, double[] dataValues, int interpolationOrder) {

		// Resampling test data
	double sampledData[] = new double[sampleTimes.length];
	int k = 0; 	
	for (int j = 0; j < sampleTimes.length; j++) {
		while ((k < dataTimes.length-2) && (sampleTimes[j] >= dataTimes[k+1])) {
			k++;
		}
		if (interpolationOrder == 1) {
			//
			// choose two points (testData[k] and testData[k+1]) in test data for interpolation (or extrapolation if outside the data)
			//
			// a)  extrapolate backward (in time) for points which are before testData
			// b)  interpolate the values that fall within the test dataset.
			// c)  extrapolate forward (in time) for points which are after testData
			//
			//
			// apply first order linear basis for reference data interpolation.
			//
			sampledData[j] = dataValues[k] + (dataValues[k+1]-dataValues[k]) * ((sampleTimes[j] - dataTimes[k])/(dataTimes[k+1] - dataTimes[k]));
		} else {
			int minusIndex = k;
			int plusIndex = k;
			int count = 1; 
			while (count < interpolationOrder + 1) {
				if (minusIndex > 0) {
					if (plusIndex < dataTimes.length - 1) {						
						if (Math.abs(dataTimes[minusIndex-1] - sampleTimes[j]) < Math.abs(dataTimes[plusIndex+1] - sampleTimes[j])) {
							minusIndex --;
							count ++;
						} else {
							plusIndex ++;
							count ++;
						}							
					} else {
						minusIndex --;
						count ++;
					}
				} else if (plusIndex < dataTimes.length - 1) {
					plusIndex ++;
					count ++;
				} else {
					break;
				}
			}

			double[] neighboringTimePts = new double[count];
			double[] neighboringValues = new double[count];
			for (int n = minusIndex; n <= plusIndex; n ++) {
				neighboringTimePts[n - minusIndex] = dataTimes[n];
				neighboringValues[n - minusIndex] = dataValues[n];
			}
			sampledData[j] = MathTestingUtilities.taylorInterpolation(sampleTimes[j], neighboringTimePts, neighboringValues);
		}
	}

	return sampledData;
}
/**
 * Insert the method's description here.
 * Creation date: (1/17/2003 3:48:36 PM)
 * @param testResultSet cbit.vcell.solver.ode.ODESolverResultSet
 * @param referenceResultSet cbit.vcell.solver.ode.ODESolverResultSet
 */
public static SimulationComparisonSummary compareUnEqualResultSets(ODESolverResultSet testResultSet, ODESolverResultSet referenceResultSet, 
		String varsToTest[],double absErrorThreshold, double relErrorThreshold, int interpolationOrder) throws Exception {
	
	if (varsToTest==null){
		throw new IllegalArgumentException("varsToTest must not be null");
	}
	
	SimulationComparisonSummary simComparisonSummary = new SimulationComparisonSummary();

	int testTimeOrHistogramIndex = testResultSet.findColumn(ReservedVariable.TIME.getName());
	int refTimeOrHistogramIndex = referenceResultSet.findColumn(ReservedVariable.TIME.getName());
	if (testTimeOrHistogramIndex < 0 && refTimeOrHistogramIndex < 0){
		testTimeOrHistogramIndex = testResultSet.findColumn(SimDataConstants.HISTOGRAM_INDEX_NAME);
		refTimeOrHistogramIndex = referenceResultSet.findColumn(SimDataConstants.HISTOGRAM_INDEX_NAME);
	}
	double[] testRSTimes = testResultSet.extractColumn(testTimeOrHistogramIndex);
	double[] refRSTimes = referenceResultSet.extractColumn(refTimeOrHistogramIndex);
	for (int i = 0; i < varsToTest.length; i++){
		int refRSIndex = referenceResultSet.findColumn(varsToTest[i]);
		if (refRSIndex==-1){
			if (varsToTest[i].endsWith(AbstractMathMapping.MATH_VAR_SUFFIX_SPECIES_COUNT)){
				String varNameWithoutCount = varsToTest[i].replace(AbstractMathMapping.MATH_VAR_SUFFIX_SPECIES_COUNT, "");
				refRSIndex = referenceResultSet.findColumn(varNameWithoutCount);
				if (refRSIndex==-1){
					throw new RuntimeException("neither variable '"+varsToTest[i]+"' nor '"+varNameWithoutCount+"' found in reference dataset");
				}
			}else{
				throw new RuntimeException("variable '"+varsToTest[i]+"' not found in reference dataset");
			}
		}
		ColumnDescription refColDesc = referenceResultSet.getColumnDescriptions(refRSIndex);
		double refData[] = referenceResultSet.extractColumn(refRSIndex);
		
		int testRSIndex = testResultSet.findColumn(varsToTest[i]);
		if (testRSIndex==-1){
			throw new RuntimeException("variable '"+varsToTest[i]+"' not found in test dataset");
		}
		double testData[] = testResultSet.extractColumn(testRSIndex);

		// Resampling test data
		double resampledTestData[] = interpolateArray(refRSTimes, testRSTimes, testData, interpolationOrder);
		DataErrorSummary tempVar = new DataErrorSummary();
		for (int j = 0; j < refData.length; j++){
			tempVar.addDataValues(refData[j], resampledTestData[j], refRSTimes[j], 0, absErrorThreshold, relErrorThreshold);
		}
		simComparisonSummary.addVariableComparisonSummary(new VariableComparisonSummary(refColDesc.getName(),
			tempVar.getMinRef(),tempVar.getMaxRef(),tempVar.getMaxAbsoluteError(),tempVar.getMaxRelativeError(), tempVar.getL2Norm(),
			tempVar.getTimeAtMaxAbsoluteError(), tempVar.getIndexAtMaxAbsoluteError(),
			tempVar.getTimeAtMaxRelativeError(), tempVar.getIndexAtMaxRelativeError()));
	}

	return simComparisonSummary;
}


/**
 *
 * constructExactMath() 
 *
 * take an equation of the form:
 *
 *
 *     d A
 *     --- = F(A,t)
 *     d t
 *
 * and create a new equation with a known exact solution 'A_exact' by adding a forcing function R(t)
 *
 *     d A
 *     --- = F(A,t) + R(t)
 *     d t
 *
 * where:
 *
 *             d A_exact
 *      R(t) = --------- - F(A_exact,t)
 *                d t
 *
 * solving for R(t) is done analytically.
 *
 *
 *
 * Creation date: (1/21/2003 10:47:54 AM)
 * @return cbit.vcell.math.MathDescription
 * @param mathDesc cbit.vcell.math.MathDescription
 */
public static MathDescription constructExactMath(MathDescription mathDesc, java.util.Random random, 
		ConstructedSolutionTemplate constructedSolutionTemplate) throws ExpressionException, MathException, MappingException {
	if (mathDesc.hasFastSystems()){
		throw new RuntimeException("SolverTest.constructExactMath() suppport for fastSystems not yet implemented.");
	}
	MathDescription exactMath = null;
	try {
		exactMath = (MathDescription)BeanUtils.cloneSerializable(mathDesc);
		exactMath.setDescription("constructed exact solution from MathDescription ("+mathDesc.getName()+")");
		exactMath.setName("exact from "+mathDesc.getName());
	}catch (Throwable e){
		e.printStackTrace(System.out);
		throw new RuntimeException("error cloning MathDescription: "+e.getMessage());
	}
	//
	// preload the VariableHash with existing Variables (and Constants,Functions,etc) and then sort all at once.
	//
	VariableHash varHash = new VariableHash();
	Enumeration<Variable> enumVar = exactMath.getVariables();
	while (enumVar.hasMoreElements()){
		varHash.addVariable(enumVar.nextElement());
	}
	
	
	java.util.Enumeration<SubDomain> subDomainEnum = exactMath.getSubDomains();
	while (subDomainEnum.hasMoreElements()){
		SubDomain subDomain = subDomainEnum.nextElement();
		Domain domain = new Domain(subDomain);
		java.util.Enumeration<Equation> equationEnum = subDomain.getEquations();
		if (subDomain instanceof MembraneSubDomain){
			MembraneSubDomain memSubDomain = (MembraneSubDomain)subDomain;
			AnalyticSubVolume insideAnalyticSubVolume = (AnalyticSubVolume)exactMath.getGeometry().getGeometrySpec().getSubVolume(memSubDomain.getInsideCompartment().getName());
			Function outwardNormalFunctions[] = getOutwardNormal(insideAnalyticSubVolume.getExpression(),"_"+insideAnalyticSubVolume.getName());
			for (int i = 0; i < outwardNormalFunctions.length; i++){
				varHash.addVariable(outwardNormalFunctions[i]);
			}
		}
		while (equationEnum.hasMoreElements()){
			Equation equation = equationEnum.nextElement();
			if (equation.getExactSolution()!=null){
				throw new RuntimeException("exact solution already exists");
			}
			Enumeration<Constant> origMathConstants = mathDesc.getConstants();
			if (equation instanceof OdeEquation){
				OdeEquation odeEquation = (OdeEquation)equation;
				Expression substitutedRateExp = substituteWithExactSolution(odeEquation.getRateExpression(),(CompartmentSubDomain)subDomain,exactMath);

				SolutionTemplate solutionTemplate = constructedSolutionTemplate.getSolutionTemplate(equation.getVariable().getName(),subDomain.getName());
				
				String varName = odeEquation.getVariable().getName();
				String initName = null;
				
				while (origMathConstants.hasMoreElements()){
					Constant constant = origMathConstants.nextElement();
					if (constant.getName().startsWith(varName+"_"+subDomain.getName()+DiffEquMathMapping.MATH_FUNC_SUFFIX_SPECIES_INIT_CONC_UNIT_PREFIX)){
						initName = constant.getName();
					}
				}
				String exactName = varName+"_"+subDomain.getName()+"_exact";
				String errorName = varName+"_"+subDomain.getName()+"_error";
				String origRateName = "_"+varName+"_"+subDomain.getName()+"_origRate";
				String substitutedRateName = "_"+varName+"_"+subDomain.getName()+"_substitutedRate";
				String exactTimeDerivativeName = "_"+varName+"_"+subDomain.getName()+"_exact_dt";
				Expression exactExp = solutionTemplate.getTemplateExpression();
				Expression errorExp = new Expression(exactName+" - "+varName);
				Expression origRateExp = new Expression(odeEquation.getRateExpression());
				Expression exactTimeDerivativeExp = exactExp.differentiate("t").flatten();
				Expression newRate = new Expression(origRateName+" - "+substitutedRateName+" + "+exactTimeDerivativeName);


				Constant constants[] = solutionTemplate.getConstants();
				for (int i = 0; i < constants.length; i++){
					varHash.addVariable(constants[i]);
				}
				Expression initExp = new Expression(exactExp);
				initExp.substituteInPlace(new Expression("t"),new Expression(0.0));
				varHash.addVariable(new Function(initName,initExp.flatten(),domain));
				varHash.addVariable(new Function(exactName,exactExp,domain));
				varHash.addVariable(new Function(errorName,errorExp,domain));
				varHash.addVariable(new Function(exactTimeDerivativeName,exactTimeDerivativeExp,domain));
				varHash.addVariable(new Function(origRateName,origRateExp,domain));
				varHash.addVariable(new Function(substitutedRateName,substitutedRateExp,domain));
				
				
				odeEquation.setRateExpression(newRate);
				odeEquation.setInitialExpression(new Expression(initName));
				odeEquation.setExactSolution(new Expression(exactName));
				
			}else if (equation instanceof PdeEquation){
				PdeEquation pdeEquation = (PdeEquation)equation;
				Expression substitutedRateExp = substituteWithExactSolution(pdeEquation.getRateExpression(),(CompartmentSubDomain)subDomain,exactMath);
				
				SolutionTemplate solutionTemplate = constructedSolutionTemplate.getSolutionTemplate(equation.getVariable().getName(),subDomain.getName());
				
				String varName = pdeEquation.getVariable().getName();
				String initName = null;
				while (origMathConstants.hasMoreElements()){
					Constant constant = origMathConstants.nextElement();
					if (constant.getName().startsWith(varName+"_"+subDomain.getName()+DiffEquMathMapping.MATH_FUNC_SUFFIX_SPECIES_INIT_CONC_UNIT_PREFIX)){
						initName = constant.getName();
					}
				}
				String diffusionRateName = "_"+varName+"_"+subDomain.getName()+"_diffusionRate";
				String exactName = varName+"_"+subDomain.getName()+"_exact";
				String errorName = varName+"_"+subDomain.getName()+"_error";
				String origRateName = "_"+varName+"_"+subDomain.getName()+"_origRate";
				String substitutedRateName = "_"+varName+"_"+subDomain.getName()+"_substitutedRate";
				String exactTimeDerivativeName = "_"+varName+"_"+subDomain.getName()+"_exact_dt";
				String exactDxName = "_"+varName+"_"+subDomain.getName()+"_exact_dx";
				String exactDyName = "_"+varName+"_"+subDomain.getName()+"_exact_dy";
				String exactDzName = "_"+varName+"_"+subDomain.getName()+"_exact_dz";
				String exactDx2Name = "_"+varName+"_"+subDomain.getName()+"_exact_dx2";
				String exactDy2Name = "_"+varName+"_"+subDomain.getName()+"_exact_dy2";
				String exactDz2Name = "_"+varName+"_"+subDomain.getName()+"_exact_dz2";
				String exactLaplacianName = "_"+varName+"_"+subDomain.getName()+"_exact_laplacian";
				Expression exactExp = solutionTemplate.getTemplateExpression();
				Expression errorExp = new Expression(exactName+" - "+varName);
				Expression origRateExp = new Expression(pdeEquation.getRateExpression());
				Expression initExp = new Expression(exactExp);
				initExp.substituteInPlace(new Expression("t"),new Expression(0.0));
				initExp = initExp.flatten();
				Expression exactTimeDerivativeExp = exactExp.differentiate("t").flatten();
				Expression exactDxExp = exactExp.differentiate("x").flatten();
				Expression exactDx2Exp = exactDxExp.differentiate("x").flatten();
				Expression exactDyExp = exactExp.differentiate("y").flatten();
				Expression exactDy2Exp = exactDxExp.differentiate("y").flatten();
				Expression exactDzExp = exactExp.differentiate("z").flatten();
				Expression exactDz2Exp = exactDxExp.differentiate("z").flatten();
				Expression exactLaplacianExp = Expression.add(Expression.add(exactDx2Exp,exactDy2Exp),exactDz2Exp).flatten();
				Expression newRate = new Expression(origRateName+
													" - "+substitutedRateName+
													" - (("+diffusionRateName+")*"+exactLaplacianName+")"+
													" + "+exactTimeDerivativeName);
				
				Constant constants[] = solutionTemplate.getConstants();
				for (int i = 0; i < constants.length; i++){
					varHash.addVariable(constants[i]);
				}

				varHash.addVariable(new Function(initName,initExp,domain));
				varHash.addVariable(new Function(diffusionRateName,new Expression(pdeEquation.getDiffusionExpression()),domain));
				varHash.addVariable(new Function(exactName,exactExp,domain));
				varHash.addVariable(new Function(errorName,errorExp,domain));
				varHash.addVariable(new Function(exactTimeDerivativeName,exactTimeDerivativeExp,domain));
				varHash.addVariable(new Function(origRateName,origRateExp,domain));
				varHash.addVariable(new Function(substitutedRateName,substitutedRateExp,domain));
				varHash.addVariable(new Function(exactDxName,exactDxExp,domain));
				varHash.addVariable(new Function(exactDyName,exactDyExp,domain));
				varHash.addVariable(new Function(exactDzName,exactDzExp,domain));
				varHash.addVariable(new Function(exactDx2Name,exactDx2Exp,domain));
				varHash.addVariable(new Function(exactDy2Name,exactDy2Exp,domain));
				varHash.addVariable(new Function(exactDz2Name,exactDz2Exp,domain));
				varHash.addVariable(new Function(exactLaplacianName,exactLaplacianExp,domain));
				
				
				pdeEquation.setRateExpression(newRate);
				pdeEquation.setInitialExpression(new Expression(initName));
				pdeEquation.setDiffusionExpression(new Expression(diffusionRateName));
				pdeEquation.setExactSolution(new Expression(exactName));

				CompartmentSubDomain compartmentSubDomain = (CompartmentSubDomain)subDomain;
				if (compartmentSubDomain.getBoundaryConditionXm().isDIRICHLET()){
					Expression origExp = pdeEquation.getBoundaryXm();
					if (origExp != null){
						Expression substitutedExp = substituteWithExactSolution(origExp,compartmentSubDomain,exactMath);
						pdeEquation.setBoundaryXm(new Expression(origExp+"-"+substitutedExp+"+"+exactExp));
					}else{
						pdeEquation.setBoundaryXm(exactExp);
					}
				}else if (compartmentSubDomain.getBoundaryConditionXm().isNEUMANN()){
					Expression origExp = pdeEquation.getBoundaryXm();
					if (origExp != null){
						Expression substitutedExp = substituteWithExactSolution(origExp,compartmentSubDomain,exactMath);
						pdeEquation.setBoundaryXm(new Expression(origExp+"-"+substitutedExp+"-"+diffusionRateName+"*"+exactDxName));
					}else{
						pdeEquation.setBoundaryXm(new Expression("-"+diffusionRateName+"*"+exactDxName));
					}
				}else{
					throw new RuntimeException("unsupported boundary condition type "+compartmentSubDomain.getBoundaryConditionXm());
				}
				if (compartmentSubDomain.getBoundaryConditionXp().isDIRICHLET()){
					Expression origExp = pdeEquation.getBoundaryXp();
					if (origExp != null){
						Expression substitutedExp = substituteWithExactSolution(origExp,compartmentSubDomain,exactMath);
						pdeEquation.setBoundaryXp(new Expression(origExp+"-"+substitutedExp+"+"+exactExp));
					}else{
						pdeEquation.setBoundaryXp(exactExp);
					}
				}else if (compartmentSubDomain.getBoundaryConditionXp().isNEUMANN()){
					Expression origExp = pdeEquation.getBoundaryXp();
					if (origExp != null){
						Expression substitutedExp = substituteWithExactSolution(origExp,compartmentSubDomain,exactMath);
						pdeEquation.setBoundaryXp(new Expression(origExp+"-"+substitutedExp+"+"+diffusionRateName+"*"+exactDxName));
					}else{
						pdeEquation.setBoundaryXp(new Expression(diffusionRateName+"*"+exactDxName));
					}
				}else{
					throw new RuntimeException("unsupported boundary condition type "+compartmentSubDomain.getBoundaryConditionXp());
				}
				
				if (compartmentSubDomain.getBoundaryConditionYm().isDIRICHLET()){
					Expression origExp = pdeEquation.getBoundaryYm();
					if (origExp != null){
						Expression substitutedExp = substituteWithExactSolution(origExp,compartmentSubDomain,exactMath);
						pdeEquation.setBoundaryYm(new Expression(origExp+"-"+substitutedExp+"+"+exactExp));
					}else{
						pdeEquation.setBoundaryYm(exactExp);
					}
				}else if (compartmentSubDomain.getBoundaryConditionYm().isNEUMANN()){
					Expression origExp = pdeEquation.getBoundaryYm();
					if (origExp != null){
						Expression substitutedExp = substituteWithExactSolution(origExp,compartmentSubDomain,exactMath);
						pdeEquation.setBoundaryYm(new Expression(origExp+"-"+substitutedExp+"-"+diffusionRateName+"*"+exactDyName));
					}else{
						pdeEquation.setBoundaryYm(new Expression("-"+diffusionRateName+"*"+exactDyName));
					}
				}else{
					throw new RuntimeException("unsupported boundary condition type "+compartmentSubDomain.getBoundaryConditionYm());
				}
				if (compartmentSubDomain.getBoundaryConditionYp().isDIRICHLET()){
					Expression origExp = pdeEquation.getBoundaryYp();
					if (origExp != null){
						Expression substitutedExp = substituteWithExactSolution(origExp,compartmentSubDomain,exactMath);
						pdeEquation.setBoundaryYp(new Expression(origExp+"-"+substitutedExp+"+"+exactExp));
					}else{
						pdeEquation.setBoundaryYp(exactExp);
					}
				}else if (compartmentSubDomain.getBoundaryConditionYp().isNEUMANN()){
					Expression origExp = pdeEquation.getBoundaryYp();
					if (origExp != null){
						Expression substitutedExp = substituteWithExactSolution(origExp,compartmentSubDomain,exactMath);
						pdeEquation.setBoundaryYp(new Expression(origExp+"-"+substitutedExp+"+"+diffusionRateName+"*"+exactDyName));
					}else{
						pdeEquation.setBoundaryYp(new Expression(diffusionRateName+"*"+exactDyName));
					}
				}else{
					throw new RuntimeException("unsupported boundary condition type "+compartmentSubDomain.getBoundaryConditionYp());
				}
				
				if (compartmentSubDomain.getBoundaryConditionZm().isDIRICHLET()){
					Expression origExp = pdeEquation.getBoundaryZm();
					if (origExp != null){
						Expression substitutedExp = substituteWithExactSolution(origExp,compartmentSubDomain,exactMath);
						pdeEquation.setBoundaryZm(new Expression(origExp+"-"+substitutedExp+"+"+exactExp));
					}else{
						pdeEquation.setBoundaryZm(exactExp);
					}
				}else if (compartmentSubDomain.getBoundaryConditionZm().isNEUMANN()){
					Expression origExp = pdeEquation.getBoundaryZm();
					if (origExp != null){
						Expression substitutedExp = substituteWithExactSolution(origExp,compartmentSubDomain,exactMath);
						pdeEquation.setBoundaryZm(new Expression(origExp+"-"+substitutedExp+"-"+diffusionRateName+"*"+exactDzName));
					}else{
						pdeEquation.setBoundaryZm(new Expression("-"+diffusionRateName+"*"+exactDzName));
					}
				}else{
					throw new RuntimeException("unsupported boundary condition type "+compartmentSubDomain.getBoundaryConditionXm());
				}
				if (compartmentSubDomain.getBoundaryConditionZp().isDIRICHLET()){
					Expression origExp = pdeEquation.getBoundaryZp();
					if (origExp != null){
						Expression substitutedExp = substituteWithExactSolution(origExp,compartmentSubDomain,exactMath);
						pdeEquation.setBoundaryZp(new Expression(origExp+"-"+substitutedExp+"+"+exactExp));
					}else{
						pdeEquation.setBoundaryZp(exactExp);
					}
				}else if (compartmentSubDomain.getBoundaryConditionZp().isNEUMANN()){
					Expression origExp = pdeEquation.getBoundaryZp();
					if (origExp != null){
						Expression substitutedExp = substituteWithExactSolution(origExp,compartmentSubDomain,exactMath);
						pdeEquation.setBoundaryZp(new Expression(origExp+"-"+substitutedExp+"+"+diffusionRateName+"*"+exactDzName));
					}else{
						pdeEquation.setBoundaryZp(new Expression(diffusionRateName+"*"+exactDzName));
					}
				}else{
					throw new RuntimeException("unsupported boundary condition type "+compartmentSubDomain.getBoundaryConditionZp());
				}
			}else{
				throw new RuntimeException("SolverTest.constructedExactMath(): equation type "+equation.getClass().getName()+" not yet implemented");
			}
		}
		if (subDomain instanceof MembraneSubDomain){
			MembraneSubDomain membraneSubDomain = (MembraneSubDomain)subDomain;
			Enumeration<JumpCondition> enumJumpConditions = membraneSubDomain.getJumpConditions();
			while (enumJumpConditions.hasMoreElements()){
				JumpCondition jumpCondition = enumJumpConditions.nextElement();
				Expression origInfluxExp = jumpCondition.getInFluxExpression();
				Expression origOutfluxExp = jumpCondition.getOutFluxExpression();
				Expression substitutedInfluxExp = substituteWithExactSolution(origInfluxExp,membraneSubDomain,exactMath);
				Expression substitutedOutfluxExp = substituteWithExactSolution(origOutfluxExp,membraneSubDomain,exactMath);
				
				String varName = jumpCondition.getVariable().getName();
				String origInfluxName = "_"+varName+"_"+subDomain.getName()+"_origInflux";
				String origOutfluxName = "_"+varName+"_"+subDomain.getName()+"_origOutflux";
				String substitutedInfluxName = "_"+varName+"_"+subDomain.getName()+"_substitutedInflux";
				String substitutedOutfluxName = "_"+varName+"_"+subDomain.getName()+"_substitutedOutflux";
				String diffusionRateInsideName = "_"+varName+"_"+membraneSubDomain.getInsideCompartment().getName()+"_diffusionRate";
				String diffusionRateOutsideName = "_"+varName+"_"+membraneSubDomain.getOutsideCompartment().getName()+"_diffusionRate";
				String exactInsideDxName = "_"+varName+"_"+membraneSubDomain.getInsideCompartment().getName()+"_exact_dx";
				String exactInsideDyName = "_"+varName+"_"+membraneSubDomain.getInsideCompartment().getName()+"_exact_dy";
				String exactInsideDzName = "_"+varName+"_"+membraneSubDomain.getInsideCompartment().getName()+"_exact_dz";
				String exactOutsideDxName = "_"+varName+"_"+membraneSubDomain.getOutsideCompartment().getName()+"_exact_dx";
				String exactOutsideDyName = "_"+varName+"_"+membraneSubDomain.getOutsideCompartment().getName()+"_exact_dy";
				String exactOutsideDzName = "_"+varName+"_"+membraneSubDomain.getOutsideCompartment().getName()+"_exact_dz";
				String outwardNormalXName = "_"+membraneSubDomain.getInsideCompartment().getName()+"_Nx";
				String outwardNormalYName = "_"+membraneSubDomain.getInsideCompartment().getName()+"_Ny";
				String outwardNormalZName = "_"+membraneSubDomain.getInsideCompartment().getName()+"_Nz";
				String exactInfluxName = "_"+varName+"_"+membraneSubDomain.getName()+"_exactInflux";
				String exactOutfluxName = "_"+varName+"_"+membraneSubDomain.getName()+"_exactOutflux";
				Expression exactInfluxExp = new Expression(diffusionRateInsideName+" * ("+
															outwardNormalXName+"*"+exactInsideDxName+" + "+
															outwardNormalYName+"*"+exactInsideDyName+" + "+
															outwardNormalZName+"*"+exactInsideDzName+")");
				Expression exactOutfluxExp = new Expression("-"+diffusionRateOutsideName+" * ("+
															outwardNormalXName+"*"+exactOutsideDxName+" + "+
															outwardNormalYName+"*"+exactOutsideDyName+" + "+
															outwardNormalZName+"*"+exactOutsideDzName+")");
				Expression newInfluxExp = new Expression(origInfluxName+" - "+substitutedInfluxName+" + "+exactInfluxName);
				Expression newOutfluxExp = new Expression(origOutfluxName+" - "+substitutedOutfluxName+" + "+exactOutfluxName);
				
				varHash.addVariable(new Function(origInfluxName,origInfluxExp,domain));
				varHash.addVariable(new Function(origOutfluxName,origOutfluxExp,domain));
				varHash.addVariable(new Function(exactInfluxName,exactInfluxExp,domain));
				varHash.addVariable(new Function(exactOutfluxName,exactOutfluxExp,domain));
				varHash.addVariable(new Function(substitutedInfluxName,substitutedInfluxExp,domain));
				varHash.addVariable(new Function(substitutedOutfluxName,substitutedOutfluxExp,domain));
								
				jumpCondition.setInFlux(newInfluxExp);
				jumpCondition.setOutFlux(newOutfluxExp);
			}
		}
	}
	exactMath.setAllVariables(varHash.getAlphabeticallyOrderedVariables());

	if (!exactMath.isValid()){
		throw new RuntimeException("generated Math is not valid: "+exactMath.getWarning());
	}
	return exactMath;
}


//
// This method is used to solve for sensitivity of variables to a given parameter.
// The mathDescription and the sensitivity parameter are passed as arguments.
// New variables and ODEs are constructed according to the rule listed below and are added to the mathDescription.
// The method returns the modified mathDescription.
//
public static MathDescription constructOdesForSensitivity(MathDescription mathDesc, Constant sensParam) throws ExpressionException, MathException, MappingException {
	//
	// For each ODE :
	//  	
	//			dX/dt = F(X, P)
	//			
	//					(where P is the sensitivity parameter)
	//
	//	we create two other ODEs :
	//
	//			dX_1/dt = F(X_1, P_1)    and
	//   	
	//			dX_2/dt = F(X_2, P_2)
	//
	//					where P_1 = P + epsilon, and
	//						  P_2 = P - epsilon.
	//
	//	We keep the initial conditions for both the new ODEs to be the same, 
	// 	i.e., X_1_init = X_2_init.
	//
	//	Then, solving for X_1 & X_2, sensitivity of X wrt P can be computed as :
	//
	//			dX = (X_1 - X_2)
	//			--	 -----------   .
	//			dP	 (P_1 - P_2)
	//
	//

	// REMOVE PRINTS AFTER CHECKING !!!
	System.out.println(" \n\n------------  Old Math Description -----------------");
	System.out.println(mathDesc.getVCML_database());

	/*if (mathDesc.hasFastSystems()){
		throw new RuntimeException("SolverTest.constructExactMath() suppport for fastSystems not yet implemented.");
	}*/

	if (mathDesc.getGeometry().getDimension() > 0){
		throw new RuntimeException("Suppport for Spatial systems not yet implemented.");
	}

	VariableHash varHash = new VariableHash();
	Enumeration<Variable> enumVar = mathDesc.getVariables();
	while (enumVar.hasMoreElements()){
		varHash.addVariable(enumVar.nextElement());
	}

	//
	// Get 2 values of senstivity parameter (P + epsilon) & (P - epsilon)
	//
	
	Constant epsilon = new Constant("epsilon", new Expression(sensParam.getConstantValue()*1e-3));
	Constant sensParam1 = new Constant(sensParam.getName()+"_1", new Expression(sensParam.getConstantValue() + epsilon.getConstantValue()));
	Constant sensParam2 = new Constant(sensParam.getName()+"_2", new Expression(sensParam.getConstantValue() - epsilon.getConstantValue()));

	//
	// Iterate through each subdomain (only 1 in compartmental case), and each equation in the subdomain
	// 
	Enumeration<SubDomain> subDomainEnum = mathDesc.getSubDomains();
	//
	// Create a vector of equations to store the 2 equations for each ODE variable in the subdomain.
	// Later, add it to the equations list in the subdomain.
	//
	Vector<Equation> equnsVector = new Vector<Equation>();
	Vector<Variable> varsVector = new Vector<Variable>();
	Vector<Variable> var1s = new Vector<Variable>();
	Vector<Variable> var2s = new Vector<Variable>();
	
	while (subDomainEnum.hasMoreElements()){
		SubDomain subDomain = subDomainEnum.nextElement();
		Enumeration<Equation> equationEnum = subDomain.getEquations();
		Domain domain = new Domain(subDomain);
		
		while (equationEnum.hasMoreElements()){
			Equation equation = equationEnum.nextElement();

			if (equation instanceof OdeEquation){
				OdeEquation odeEquation = (OdeEquation)equation;

				// Similar to substituteWithExactSolutions, to bind and substitute functions in the ODE
				Expression substitutedRateExp = substituteFunctions(odeEquation.getRateExpression(), mathDesc);
				String varName = odeEquation.getVariable().getName();
				VolVariable var = new VolVariable(varName,domain);
				varsVector.addElement(var);

				//
				// Create the variable var1, and get the initExpr and rateExpr from the original ODE.
				// Substitute the new vars (var1 and param1) in the old initExpr and rateExpr and create a new ODE
				//
				String varName1 = new String("__"+varName+"_1");
				Expression initExpr1 = odeEquation.getInitialExpression();
				Expression rateExpr1 = new Expression(substitutedRateExp);
				rateExpr1.substituteInPlace(new Expression(varName), new Expression(varName1));
				rateExpr1.substituteInPlace(new Expression(sensParam.getName()), new Expression(sensParam1.getName()));
				VolVariable var1 = new VolVariable(varName1,domain);
				var1s.addElement(var1);
				OdeEquation odeEqun1 = new OdeEquation(var1, initExpr1, rateExpr1);
				equnsVector.addElement(odeEqun1);

				//
				// Create the variable var2, and get the initExpr and rateExpr from the original ODE.
				// Substitute the new vars (var2 and param2) in the old initExpr and rateExpr and create a new ODE
				//
				String varName2 = new String("__"+varName+"_2");
				Expression initExpr2 = odeEquation.getInitialExpression();
				Expression rateExpr2 = new Expression(substitutedRateExp);
				rateExpr2.substituteInPlace(new Expression(varName), new Expression(varName2));
				rateExpr2.substituteInPlace(new Expression(sensParam.getName()), new Expression(sensParam2.getName()));
				VolVariable var2 = new VolVariable(varName2,domain);
				var2s.addElement(var2);
				OdeEquation odeEqun2 = new OdeEquation(var2, initExpr2, rateExpr2);
				equnsVector.addElement(odeEqun2);

				// 
				// Create a function for the sensitivity function expression (X1-X2)/(P1-P2), and save in varHash
				//
				Expression diffVar = Expression.add(new Expression(var1.getName()), Expression.negate(new Expression(var2.getName())));
				Expression diffParam = Expression.add(new Expression(sensParam1.getName()), Expression.negate(new Expression(sensParam2.getName())));
				Expression sensitivityExpr = Expression.mult(diffVar, Expression.invert(diffParam));
				Function sens_Func = new Function("__sens"+varName+"_wrt_"+sensParam.getName(), sensitivityExpr, domain);

				varHash.addVariable(epsilon);
				varHash.addVariable(sensParam1);
				varHash.addVariable(sensParam2);
				varHash.addVariable(var1);
				varHash.addVariable(var2);
				varHash.addVariable(sens_Func);
			}else{
				// sensitivity not implemented for PDEs or other equation types.
				throw new RuntimeException("SolverTest.constructedExactMath(): equation type "+equation.getClass().getName()+" not yet implemented");
			}
		}

		//
		// Need to substitute the new variables in the new ODEs.
		// 		i.e., if Rate Expr for ODE_1 for variable X_1 contains variable Y, variable Z, etc.
		//		Rate Expr is already substituted with X_1, but it also needs substitute Y with Y_1, Z with Z_1, etc.
		// So get the volume variables, from the vectors for vars, var_1s and var_2s
		// Substitute the rate expressions for the newly added ODEs in equnsVector.
		//

		Variable vars[] = (Variable[])BeanUtils.getArray(varsVector, Variable.class);		
		Variable var_1s[] = (Variable[])BeanUtils.getArray(var1s, Variable.class);
		Variable var_2s[] = (Variable[])BeanUtils.getArray(var2s, Variable.class);

		Vector<Equation> newEqunsVector = new Vector<Equation>();
		for (int i = 0; i < equnsVector.size(); i++) {
			Equation equn = equnsVector.elementAt(i);
			Expression initEx = equn.getInitialExpression();
			Expression rateEx = equn.getRateExpression();
			for (int j = 0; j < vars.length ; j++){
				if (equn.getVariable().getName().endsWith("_1")) {
					rateEx.substituteInPlace(new Expression(vars[j].getName()),new Expression(var_1s[j].getName()));
				} else if (equn.getVariable().getName().endsWith("_2")) {
					rateEx.substituteInPlace(new Expression(vars[j].getName()),new Expression(var_2s[j].getName()));
				}
			}
			OdeEquation odeEqun = new OdeEquation(equn.getVariable(), initEx, rateEx);
			newEqunsVector.addElement(odeEqun);
		}
		

		//
		// Add all the extra ODEs created for each original ODE variable to the equations in the subdomain
		//
		for (int i = 0; i < newEqunsVector.size(); i++){
			mathDesc.getSubDomain(subDomain.getName()).addEquation((Equation)newEqunsVector.elementAt(i))	;
		}

		//
		// FAST SYSTEM
		// If the subdomain has a fast system, create a new fast system by substituting the high-low variables/parameters
		// in the expressions for the fastInvariants and fastRates and adding them to the fast system.
		//
		Vector<FastInvariant> invarsVector = new Vector<FastInvariant>();
		Vector<FastRate> ratesVector = new Vector<FastRate>();
		Enumeration<FastInvariant> fastInvarsEnum = null;
		Enumeration<FastRate> fastRatesEnum = null;

		// Get the fast invariants and fast rates in the system.	
		FastSystem fastSystem = subDomain.getFastSystem();
		if (fastSystem != null) {
			fastInvarsEnum = fastSystem.getFastInvariants();
			fastRatesEnum = fastSystem.getFastRates();

			//
			// For each fast invariant expression, substitute the variables(X) with their high and low values (X_1 & X_2)
			// and sensitivity parameter (P) with its high and low values (P_1 & P_2) to get 2 new fast invariants. 
			//
			while (fastInvarsEnum.hasMoreElements()) {
				FastInvariant fastInvar = fastInvarsEnum.nextElement();
				Expression fastInvarExpr = fastInvar.getFunction();
				fastInvarExpr = MathUtilities.substituteFunctions(fastInvarExpr, mathDesc);

				Expression fastInvarExpr1 = new Expression(fastInvarExpr);
				Expression fastInvarExpr2 = new Expression(fastInvarExpr);
				
				for (int i = 0; i < vars.length; i++){
					fastInvarExpr1.substituteInPlace(new Expression(vars[i].getName()),new Expression(var_1s[i].getName()));
					fastInvarExpr2.substituteInPlace(new Expression(vars[i].getName()),new Expression(var_2s[i].getName()));
				}

				fastInvarExpr1.substituteInPlace(new Expression(sensParam.getName()), new Expression(sensParam1.getName()));
				FastInvariant fastInvar1 = new FastInvariant(fastInvarExpr1);
				invarsVector.addElement(fastInvar1);
					
				fastInvarExpr2.substituteInPlace(new Expression(sensParam.getName()), new Expression(sensParam2.getName()));			
				FastInvariant fastInvar2 = new FastInvariant(fastInvarExpr2);
				invarsVector.addElement(fastInvar2);
			}
			
			// Add the newly created fast invariants to the existing list of fast invariants in the fast system.
			for (int i = 0; i < invarsVector.size(); i++){
				FastInvariant inVar = (FastInvariant)invarsVector.elementAt(i);
				fastSystem.addFastInvariant(inVar);			
			}

			//
			// For each fast rate expression, substitute the variables(X) with their high and low values (X_1 & X_2)
			// and sensitivity parameter (P) with its high and low values (P_1 & P_2) to get 2 new fast rates. 
			//
			while (fastRatesEnum.hasMoreElements()) {
				FastRate fastRate = fastRatesEnum.nextElement();
				Expression fastRateExpr = fastRate.getFunction();
				fastRateExpr = MathUtilities.substituteFunctions(fastRateExpr, mathDesc);

				Expression fastRateExpr1 = new Expression(fastRateExpr);
				Expression fastRateExpr2 = new Expression(fastRateExpr);
				
				for (int i = 0; i < vars.length; i++){
					fastRateExpr1.substituteInPlace(new Expression(vars[i].getName()),new Expression(var_1s[i].getName()));
					fastRateExpr2.substituteInPlace(new Expression(vars[i].getName()),new Expression(var_2s[i].getName()));
				}

				fastRateExpr1.substituteInPlace(new Expression(sensParam.getName()), new Expression(sensParam1.getName()));
				FastRate fastRate1 = new FastRate(fastRateExpr1);
				ratesVector.addElement(fastRate1);
				
				fastRateExpr2.substituteInPlace(new Expression(sensParam.getName()), new Expression(sensParam2.getName()));			
				FastRate fastRate2 = new FastRate(fastRateExpr2);
				ratesVector.addElement(fastRate2);
			}

			// Add the newly created fast rates to the existing list of fast rates in the fast system.
			for (int i = 0; i < ratesVector.size(); i++){
				FastRate rate = (FastRate)ratesVector.elementAt(i);
				fastSystem.addFastRate(rate);			
			}
		}
	}

	// Reset all variables in mathDesc.
	mathDesc.setAllVariables(varHash.getAlphabeticallyOrderedVariables());

	// REMOVE PRINTS AFTER CHECKING
	System.out.println(" \n\n------------  New Math Description -----------------");
	System.out.println(mathDesc.getVCML_database());

	return mathDesc;
}


/**
 * Insert the method's description here.
 * Creation date: (1/17/2003 3:47:43 PM)
 * @return cbit.vcell.solver.ode.ODESolverResultSet
 * @param sim cbit.vcell.solver.Simulation
 */
public static ODESolverResultSet getConstructedResultSet(MathDescription mathDesc, double time[]) throws Exception {
	if (mathDesc.getGeometry().getDimension()!=0){
		throw new RuntimeException("can only handle non-spatial simulations.");
	}
	Simulation sim = new Simulation(mathDesc);
	SimulationSymbolTable simSymbolTable = new SimulationSymbolTable(sim, 0);
	ODESolverResultSet resultSet = new ODESolverResultSet();
	resultSet.addDataColumn(new ODESolverResultSetColumnDescription("t"));
	for (int i = 0; i < time.length; i++){
		resultSet.addRow(new double[] { time[i] });
	}
	java.util.Enumeration<SubDomain> subDomainEnum = mathDesc.getSubDomains();
	String errorString = "Variable(s) : ";
	while (subDomainEnum.hasMoreElements()) {
		SubDomain subDomain = subDomainEnum.nextElement();
		java.util.Enumeration<Equation> enumEquations = subDomain.getEquations();
		while (enumEquations.hasMoreElements()) {
			Equation equation = enumEquations.nextElement();
			Expression constructedSolution = equation.getExactSolution();
			if (constructedSolution!=null){
				constructedSolution = new Expression(constructedSolution);
				constructedSolution.bindExpression(simSymbolTable);
				constructedSolution = simSymbolTable.substituteFunctions(constructedSolution);
				constructedSolution = constructedSolution.flatten();
				resultSet.addFunctionColumn(new FunctionColumnDescription(constructedSolution,equation.getVariable().getName(),null,equation.getVariable().getName(),false));
			}else{
				errorString = errorString+equation.getVariable().getName()+", ";
			}
		}
	}
	if (!errorString.equals("Variable(s) : ")) {
		throw new RuntimeException(errorString+" don't have a constructed solution");
	}
	return resultSet;
}


/**
 * Insert the method's description here.
 * Creation date: (1/17/2003 3:47:43 PM)
 * @return cbit.vcell.solver.ode.ODESolverResultSet
 * @param sim cbit.vcell.solver.Simulation
 */
public static ODESolverResultSet getExactResultSet(MathDescription mathDesc, double time[], Constant sensitivityParam) throws Exception {
	if (mathDesc.getGeometry().getDimension()!=0){
		throw new RuntimeException("can only handle non-spatial simulations.");
	}
	Simulation sim = new Simulation(mathDesc);
	SimulationSymbolTable simSymbolTable = new SimulationSymbolTable(sim, 0);
	ODESolverResultSet resultSet = new ODESolverResultSet();
	resultSet.addDataColumn(new ODESolverResultSetColumnDescription("t"));
	for (int i = 0; i < time.length; i++){
		resultSet.addRow(new double[] { time[i] });
	}
	java.util.Enumeration<SubDomain> subDomainEnum = mathDesc.getSubDomains();
	while (subDomainEnum.hasMoreElements()) {
		SubDomain subDomain = subDomainEnum.nextElement();
		java.util.Enumeration<Equation> enumEquations = subDomain.getEquations();
		while (enumEquations.hasMoreElements()) {
			Equation equation = enumEquations.nextElement();
			Expression exactSolution = equation.getExactSolution();
			if (exactSolution!=null){
				exactSolution = new Expression(exactSolution);
				exactSolution.bindExpression(simSymbolTable);
				exactSolution = simSymbolTable.substituteFunctions(exactSolution);
				exactSolution.bindExpression(simSymbolTable);
				exactSolution = exactSolution.flatten();
				resultSet.addFunctionColumn(new FunctionColumnDescription(exactSolution,equation.getVariable().getName(),null,equation.getVariable().getName(),false));

				if (sensitivityParam != null) {
					exactSolution = equation.getExactSolution();
					Expression exactSensitivity = new Expression(exactSolution);
					exactSensitivity.bindExpression(simSymbolTable);
					exactSensitivity = simSymbolTable.substituteFunctions(exactSensitivity);
					exactSensitivity.bindExpression(simSymbolTable);
					exactSensitivity = exactSensitivity.differentiate(sensitivityParam.getName());
					exactSensitivity = exactSensitivity.flatten();
					VolVariable volVar = (VolVariable)equation.getVariable();
					String sensName = SensVariable.getSensName(volVar, sensitivityParam);
					resultSet.addFunctionColumn(new FunctionColumnDescription(exactSensitivity, sensName, null, sensName, false));
				}
					
			}else{
				throw new RuntimeException("variable "+equation.getVariable().getName()+" doesn't have an exact solution");
			}
		}
	}
	return resultSet;
}


/**
 * Insert the method's description here.
 * Creation date: (1/23/2003 10:30:23 PM)
 * @return cbit.vcell.parser.Expression
 * @param analyticSubDomainExp cbit.vcell.parser.Expression
 */
public static Expression[] getInsideOutsideFunctions(Expression analyticSubDomainExp) throws ExpressionException, MappingException {
	
	Expression analyticExp = new Expression(analyticSubDomainExp);
	analyticExp.bindExpression(null);
	analyticExp = analyticExp.flatten();
	java.util.Stack<Expression> unparsedExpressionStack = new java.util.Stack<Expression>();
	java.util.Vector<Expression> expList = new java.util.Vector<Expression>();
	unparsedExpressionStack.push(analyticExp);
	while (unparsedExpressionStack.size()>0){
		Expression exp = unparsedExpressionStack.pop();
		if (exp.isRelational()){
			ExpressionTerm expTerm = exp.extractTopLevelTerm();
			if (expTerm.getOperator().equals("<") || expTerm.getOperator().equals("<=")){
				expList.add(new Expression(expTerm.getOperands()[0].infix()+"-"+expTerm.getOperands()[1].infix()));
			}else if (expTerm.getOperator().equals(">") || expTerm.getOperator().equals(">=")){
				expList.add(new Expression(expTerm.getOperands()[1].infix()+"-"+expTerm.getOperands()[0].infix()));
			}else{
				throw new ExpressionException("relational expression '"+exp+"' is not an inequality");
			}
		}else if (exp.isLogical()){
			ExpressionTerm expTerm = exp.extractTopLevelTerm();
			for (int i = 0; i < expTerm.getOperands().length; i++){
				unparsedExpressionStack.push(expTerm.getOperands()[i]);
			}
		}else{
			throw new ExpressionException("expression '"+exp+"' is neither relational nor logical, bad analytic geometry");
		}
	}
	return (Expression[])BeanUtils.getArray(expList,Expression.class);
}


/**
 * Insert the method's description here.
 * Creation date: (1/23/2003 10:30:23 PM)
 * @return cbit.vcell.parser.Expression
 * @param analyticSubDomainExp cbit.vcell.parser.Expression
 */
public static Function[] getOutwardNormal(Expression analyticSubVolume, String baseName) throws ExpressionException, MappingException, MathException {

	VariableHash varHash = new VariableHash();
	
	Expression insideOutsideFunctions[] = getInsideOutsideFunctions(analyticSubVolume);
	StringBuffer normalBufferX = new StringBuffer("0.0");
	StringBuffer normalBufferY = new StringBuffer("0.0");
	StringBuffer normalBufferZ = new StringBuffer("0.0");
	for (int i = 0; i < insideOutsideFunctions.length; i++){
		//
		// each one gets a turn being the minimum
		//
		Function functions[] = getOutwardNormalFromInsideOutsideFunction(insideOutsideFunctions[i],baseName+i);
		for (int j = 0; j < functions.length; j++){
			varHash.addVariable(functions[j]);
		}
		String closestName = baseName+i+"_closest";
		StringBuffer closestBuffer = new StringBuffer("1.0");
		for (int j = 0; j < insideOutsideFunctions.length; j++){
			if (i!=j){
				closestBuffer.append(" && ("+baseName+i+"_distance < "+baseName+j+"_distance)");
			}
		}
		Expression closest = new Expression(closestBuffer.toString());
		varHash.addVariable(new Function(closestName,closest,null));
		normalBufferX.append(" + ("+baseName+i+"_closest * "+baseName+i+"_Nx)");
		normalBufferY.append(" + ("+baseName+i+"_closest * "+baseName+i+"_Ny)");
		normalBufferZ.append(" + ("+baseName+i+"_closest * "+baseName+i+"_Nz)");
	}
	varHash.addVariable(new Function(baseName+"_Nx",new Expression(normalBufferX.toString()),null));
	varHash.addVariable(new Function(baseName+"_Ny",new Expression(normalBufferY.toString()),null));
	varHash.addVariable(new Function(baseName+"_Nz",new Expression(normalBufferZ.toString()),null));

	
	Variable vars[] = varHash.getAlphabeticallyOrderedVariables();
	java.util.Vector<Variable> varList = new java.util.Vector<Variable>(java.util.Arrays.asList(vars));
	return (Function[])BeanUtils.getArray(varList,Function.class);
}


/**
 * Insert the method's description here.
 * Creation date: (1/23/2003 10:30:23 PM)
 * @return cbit.vcell.parser.Expression
 * @param analyticSubDomainExp cbit.vcell.parser.Expression
 */
public static Function[] getOutwardNormalFromInsideOutsideFunction(Expression insideOutsideFunction, String baseName) throws ExpressionException, MappingException {
	
	java.util.Vector<Function> varList = new java.util.Vector<Function>();
	Expression F = new Expression(insideOutsideFunction);
	F.bindExpression(null);
	F = F.flatten();
	
	if (insideOutsideFunction.isRelational() || insideOutsideFunction.isLogical()){
		throw new RuntimeException("expecting smooth function for analytic function");
	}
	String F_name = baseName+"_F";
	String F_dx_name = baseName+"_F_dx";
	Expression F_dx = F.differentiate("x").flatten();
	String F_dy_name = baseName+"_F_dy";
	Expression F_dy = F.differentiate("y").flatten();
	String F_dz_name = baseName+"_F_dz";
	Expression F_dz = F.differentiate("z").flatten();
	String normalLengthName = baseName+"_F_d_length";
	Expression normalLength = new Expression("sqrt(pow("+F_dx_name+",2)+pow("+F_dy_name+",2)+pow("+F_dz_name+",2))");
	String distanceToSurfaceName = baseName+"_distance";
	Expression distanceToSurface = new Expression("abs("+F_name+")/"+normalLengthName);
	String normalXName = baseName+"_Nx";
	Expression normalX = new Expression(F_dx_name+"/"+normalLengthName);
	String normalYName = baseName+"_Ny";
	Expression normalY = new Expression(F_dy_name+"/"+normalLengthName);
	String normalZName = baseName+"_Nz";
	Expression normalZ = new Expression(F_dz_name+"/"+normalLengthName);

	Domain domain = null;
	varList.add(new Function(F_name,F,domain));
	varList.add(new Function(F_dx_name,F_dx,domain));
	varList.add(new Function(F_dy_name,F_dy,domain));
	varList.add(new Function(F_dz_name,F_dz,domain));
	varList.add(new Function(normalLengthName,normalLength,domain));
	varList.add(new Function(normalXName,normalX,domain));
	varList.add(new Function(normalYName,normalY,domain));
	varList.add(new Function(normalZName,normalZ,domain));
	varList.add(new Function(distanceToSurfaceName,distanceToSurface,domain));

	return (Function[])BeanUtils.getArray(varList,Function.class);
}


/**
 * Insert the method's description here.
 * Creation date: (1/24/2003 10:18:14 AM)
 * @return cbit.vcell.parser.Expression
 * @param origExp cbit.vcell.parser.Expression
 * @param subDomain cbit.vcell.math.SubDomain
 */
private static Variable getSimVar(SimulationSymbolTable refSimSymbolTable, String testSimVarName) {

	Variable[] refSimVars = refSimSymbolTable.getVariables();

	for (int i = 0; i < refSimVars.length; i++) {
		if (refSimVars[i].getName().equals(testSimVarName)) {
			return refSimVars[i];
		}
	}
	return null;
}


/**
 * Insert the method's description here.
 * Creation date: (10/27/2003 5:07:42 PM)
 * @return double[]
 * @param data double[]
 * @param sourceMesh cbit.vcell.solvers.CartesianMesh
 * @param targetMesh cbit.vcell.solvers.CartesianMesh
 */
public static double[] resample1DSpatial(double[] sourceData, CartesianMesh sourceMesh, CartesianMesh refMesh) {
	if (sourceData.length!=sourceMesh.getNumVolumeElements()){
		throw new RuntimeException("must be volume data, data length doesn't match number of volume elements");
	}
	// for volume samples:
	//
	//  loop through volumeIndexes from refMesh
	//      Coordinate refCoordinate = refMesh.getCoordinate(volumeIndex);
	//          Coordinate fractionalIndex = sourceMesh.getFractionCoordinateIndex(Coordinate refCoordinate);
	//              ....interpolate in z
	//              start with integer portion of fractionIndex

	double resampledData[] = new double[refMesh.getSizeX()];
	for (int i = 0; i < resampledData.length; i++) {
		Coordinate refCoordinate = refMesh.getCoordinateFromVolumeIndex(i);
		Coordinate fractionalIndex = sourceMesh.getFractionalCoordinateIndex(refCoordinate);
		int ceil_x;
		int floor_x;

		if (fractionalIndex.getX() < 0) {
			floor_x = 0;			
			ceil_x = 1;
		} else if (fractionalIndex.getX() > sourceMesh.getSizeX()-1) {
			floor_x = sourceMesh.getSizeX()-2;
			ceil_x =  sourceMesh.getSizeX()-1;
		} else {
			ceil_x = (int)Math.ceil(fractionalIndex.getX());
			floor_x = (int)Math.floor(fractionalIndex.getX());
		}
		
		double fract_x = fractionalIndex.getX() - floor_x;
		
		CoordinateIndex coord_1 = new CoordinateIndex(floor_x, 0, 0);  // ***** SHOULD Y,Z BE 0 OR 1 ???? *****
		CoordinateIndex coord_2 = new CoordinateIndex(ceil_x, 0, 0);
		
		int volIndx1 = sourceMesh.getVolumeIndex(coord_1);
		int volIndx2 = sourceMesh.getVolumeIndex(coord_2);

		boolean bBoth1_2 = false;
		boolean bNoneOf1_2 = false;
		boolean bOne = false;
		boolean bTwo = false;
		boolean bOneOf1_2 = false;		
		
		// Get data values from 'data' using vol_indices.
		// use it to compute a, b, 	 ** Interpolation in X **
		if (sourceMesh.getSubVolumeFromVolumeIndex(volIndx1) == refMesh.getSubVolumeFromVolumeIndex(i) && 
			sourceMesh.getSubVolumeFromVolumeIndex(volIndx2) != refMesh.getSubVolumeFromVolumeIndex(i)) {
			bOneOf1_2 = true;
			bOne = true;
			volIndx2 = volIndx1;
		} else if (sourceMesh.getSubVolumeFromVolumeIndex(volIndx1) != refMesh.getSubVolumeFromVolumeIndex(i) && 
					sourceMesh.getSubVolumeFromVolumeIndex(volIndx2) == refMesh.getSubVolumeFromVolumeIndex(i)) {
			bOneOf1_2 = true;
			bTwo = true;
			volIndx1 = volIndx2;
		} else if (sourceMesh.getSubVolumeFromVolumeIndex(volIndx1) != refMesh.getSubVolumeFromVolumeIndex(i) &&
				sourceMesh.getSubVolumeFromVolumeIndex(volIndx2) != refMesh.getSubVolumeFromVolumeIndex(i)) {	
			// throw new RuntimeException("Either subvolume in sourceMesh not equal to refMesh subVol cannot be handled at this time!");
			bNoneOf1_2 = true;
		} else {
			bBoth1_2 = true;
		}
				
		// Interpolate in X - final resampledSourceData value
		if (bBoth1_2) {
			resampledData[i] = sourceData[volIndx1] + fract_x*(sourceData[volIndx2] - sourceData[volIndx1]);
		}
		
		if (bOneOf1_2) {
			if (bOne) {
				resampledData[i] = sourceData[volIndx1];
			} else if (bTwo) {
				resampledData[i] = sourceData[volIndx2];
			}
		}
		// Can there be a situation where bNoneOf1_2 is true, ie, both points are not 
		// in the specific subvolume of refMesh?? If so, what is the value of resampledData in sourceMesh?
		// Should we take it as the value of data at the point closest to this point within the specific region
		// in refMesh? If so, how does one find such a point?
		//
		if (bNoneOf1_2) {
			// resampledData[i] = BLAH_BLAH;
			throw new RuntimeException("Cannot handle the case where both points are not in ref subvolume");
		}		
	}
	return resampledData;
}


/**
 * Insert the method's description here.
 * Creation date: (10/27/2003 5:07:42 PM)
 * @return double[]
 * @param data double[]
 * @param sourceMesh cbit.vcell.solvers.CartesianMesh
 * @param targetMesh cbit.vcell.solvers.CartesianMesh
 */
public static double[] resample1DSpatialSimple(double[] sourceData, CartesianMesh sourceMesh, CartesianMesh refMesh) {
	if (sourceData.length!=sourceMesh.getNumVolumeElements()){
		throw new RuntimeException("must be volume data, data length doesn't match number of volume elements");
	}
	// for volume samples:
	//
	//  loop through volumeIndexes from refMesh
	//      Coordinate refCoordinate = refMesh.getCoordinate(volumeIndex);
	//          Coordinate fractionalIndex = sourceMesh.getFractionCoordinateIndex(Coordinate refCoordinate);
	//              ....interpolate in z
	//              start with integer portion of fractionIndex

	double resampledData[] = new double[refMesh.getSizeX()];
	for (int i = 0; i < resampledData.length; i++) {
		Coordinate refCoordinate = refMesh.getCoordinateFromVolumeIndex(i);
		Coordinate fractionalIndex = sourceMesh.getFractionalCoordinateIndex(refCoordinate);
		int ceil_x;
		int floor_x;

		if (fractionalIndex.getX() < 0) {
			floor_x = 0;			
			ceil_x = 1;
		} else if (fractionalIndex.getX() > sourceMesh.getSizeX()-1) {
			floor_x = sourceMesh.getSizeX()-2;
			ceil_x =  sourceMesh.getSizeX()-1;
		} else {
			ceil_x = (int)Math.ceil(fractionalIndex.getX());
			floor_x = (int)Math.floor(fractionalIndex.getX());
		}
		
		double fract_x = fractionalIndex.getX() - floor_x;
		
		CoordinateIndex coord_1 = new CoordinateIndex(floor_x, 0, 0);  // ***** SHOULD Y,Z BE 0 OR 1 ???? *****
		CoordinateIndex coord_2 = new CoordinateIndex(ceil_x, 0, 0);
		
		int volIndx1 = sourceMesh.getVolumeIndex(coord_1);
		int volIndx2 = sourceMesh.getVolumeIndex(coord_2);
				
		// Interpolate in X - final resampledSourceData value
		resampledData[i] = sourceData[volIndx1] + fract_x*(sourceData[volIndx2] - sourceData[volIndx1]);
	}
	return resampledData;
}


/**
 * Insert the method's description here.
 * Creation date: (10/27/2003 5:07:42 PM)
 * @return double[]
 * @param data double[]
 * @param sourceMesh cbit.vcell.solvers.CartesianMesh
 * @param targetMesh cbit.vcell.solvers.CartesianMesh
 */
public static double[] resample2DSpatial(double[] sourceData, CartesianMesh sourceMesh, CartesianMesh refMesh) {
	if (sourceData.length!=sourceMesh.getNumVolumeElements()){
		throw new RuntimeException("must be volume data, data length doesn't match number of volume elements");
	}
	// for volume samples:
	//
	//  loop through volumeIndexes from refMesh
	//      Coordinate refCoordinate = refMesh.getCoordinate(volumeIndex);
	//          Coordinate fractionalIndex = sourceMesh.getFractionCoordinateIndex(Coordinate refCoordinate);
	//              ....interpolate in y
	//              start with integer portion of fractionIndex

	double resampledData[] = new double[refMesh.getSizeX()*refMesh.getSizeY()*refMesh.getSizeZ()];
	for (int i = 0; i < resampledData.length; i++) {
		Coordinate refCoordinate = refMesh.getCoordinateFromVolumeIndex(i);
		Coordinate fractionalIndex = sourceMesh.getFractionalCoordinateIndex(refCoordinate);
		int ceil_x;
		int floor_x;
		int ceil_y;
		int floor_y;

		if (fractionalIndex.getX() < 0) {
			floor_x = 0;			
			ceil_x = 1;
		} else if (fractionalIndex.getX() > sourceMesh.getSizeX()-1) {
			floor_x = sourceMesh.getSizeX()-2;
			ceil_x =  sourceMesh.getSizeX()-1;
		} else {
			ceil_x = (int)Math.ceil(fractionalIndex.getX());
			floor_x = (int)Math.floor(fractionalIndex.getX());
		}
		if (fractionalIndex.getY() < 0) {
			floor_y = 0;			
			ceil_y = 1;
		} else if (fractionalIndex.getY() > sourceMesh.getSizeY()-1) {
			floor_y = sourceMesh.getSizeY()-2;
			ceil_y =  sourceMesh.getSizeY()-1;
		} else {
			ceil_y = (int)Math.ceil(fractionalIndex.getY());
			floor_y = (int)Math.floor(fractionalIndex.getY());
		}		
		
		double fract_x = fractionalIndex.getX() - floor_x;
		double fract_y = fractionalIndex.getY() - floor_y;
		
		CoordinateIndex coord_1 = new CoordinateIndex(floor_x, floor_y, 0);  // ***** SHOULD THIS BE 0 OR 1 ???? *****
		CoordinateIndex coord_2 = new CoordinateIndex(ceil_x, floor_y, 0);
		CoordinateIndex coord_3 = new CoordinateIndex(floor_x, ceil_y, 0);
		CoordinateIndex coord_4 = new CoordinateIndex(ceil_x, ceil_y, 0);
		
		int volIndx1 = sourceMesh.getVolumeIndex(coord_1);
		int volIndx2 = sourceMesh.getVolumeIndex(coord_2);
		int volIndx3 = sourceMesh.getVolumeIndex(coord_3);
		int volIndx4 = sourceMesh.getVolumeIndex(coord_4);

		boolean bBoth1_2 = false;
		boolean bNoneOf1_2 = false;
		boolean bOne = false;
		boolean bTwo = false;
		boolean bOneOf1_2 = false;

		boolean bBoth3_4 = false;
		boolean bNoneOf3_4 = false;
		boolean bThree = false;
		boolean bFour = false;
		boolean bOneOf3_4 = false;
			
		// Get data values from 'data' using vol_indices.
		// use it to compute a, b, 	 ** Interpolation in X **
		if (sourceMesh.getSubVolumeFromVolumeIndex(volIndx1) == refMesh.getSubVolumeFromVolumeIndex(i) && 
			sourceMesh.getSubVolumeFromVolumeIndex(volIndx2) != refMesh.getSubVolumeFromVolumeIndex(i)) {
			bOneOf1_2 = true;
			bOne = true;
			volIndx2 = volIndx1;
		} else if (sourceMesh.getSubVolumeFromVolumeIndex(volIndx1) != refMesh.getSubVolumeFromVolumeIndex(i) && 
					sourceMesh.getSubVolumeFromVolumeIndex(volIndx2) == refMesh.getSubVolumeFromVolumeIndex(i)) {
			bOneOf1_2 = true;
			bTwo = true;
			volIndx1 = volIndx2;
		} else if (sourceMesh.getSubVolumeFromVolumeIndex(volIndx1) != refMesh.getSubVolumeFromVolumeIndex(i) &&
				sourceMesh.getSubVolumeFromVolumeIndex(volIndx2) != refMesh.getSubVolumeFromVolumeIndex(i)) {	
			// throw new RuntimeException("Either subvolume in sourceMesh not equal to refMesh subVol cannot be handled at this time!");
			bNoneOf1_2 = true;
		} else {
			bBoth1_2 = true;
		}
				
		if (sourceMesh.getSubVolumeFromVolumeIndex(volIndx3) == refMesh.getSubVolumeFromVolumeIndex(i) && 
			sourceMesh.getSubVolumeFromVolumeIndex(volIndx4) != refMesh.getSubVolumeFromVolumeIndex(i)) {
			bOneOf3_4 = true;
			bThree = true;
			volIndx4 = volIndx3;
		} else if (sourceMesh.getSubVolumeFromVolumeIndex(volIndx3) != refMesh.getSubVolumeFromVolumeIndex(i) && 
					sourceMesh.getSubVolumeFromVolumeIndex(volIndx4) == refMesh.getSubVolumeFromVolumeIndex(i)) {
			bOneOf3_4 = true;
			bFour = true;
			volIndx3 = volIndx4;
		} else if (sourceMesh.getSubVolumeFromVolumeIndex(volIndx3) != refMesh.getSubVolumeFromVolumeIndex(i) &&
			sourceMesh.getSubVolumeFromVolumeIndex(volIndx4) != refMesh.getSubVolumeFromVolumeIndex(i)) {
			// throw new RuntimeException("Either subvolume in sourceMesh not equal to refMesh subVol cannot be handled at this time!");
			bNoneOf3_4 = true;
		} else {
			bBoth3_4 = true;
		}

		// First order interpolation if 4, 3, 2 points are within the ref subVolume.	
		if ( (bBoth1_2 && bBoth3_4) || (bBoth1_2 && bOneOf3_4) || (bOneOf1_2 && bBoth3_4) || (bOneOf1_2 && bOneOf3_4) ) {
			// Interpolate in X (first order)
			double val_a = sourceData[volIndx1] + fract_x*(sourceData[volIndx2] - sourceData[volIndx1]);
			double val_b = sourceData[volIndx3] + fract_x*(sourceData[volIndx4] - sourceData[volIndx3]);

			// Interpolate in Y - final resampledSourceData value
			resampledData[i] = val_a + fract_y*(val_b - val_a);
		}

		// 0th order interpolation if only one point is in ref subVol.
		if ( (bBoth1_2 && bNoneOf3_4) ) {
			resampledData[i] = sourceData[volIndx1] + fract_x*(sourceData[volIndx2] - sourceData[volIndx1]);
		}
		if ( (bNoneOf1_2 && bBoth3_4) ) {
			resampledData[i] = sourceData[volIndx3] + fract_x*(sourceData[volIndx4] - sourceData[volIndx3]);
		}
			
		if ( (bOneOf1_2 && bNoneOf3_4) ) {
			if (bOne) {
				resampledData[i] = sourceData[volIndx1];
			} else if (bTwo) {
				resampledData[i] = sourceData[volIndx2];
			}
		}

		if ( (bNoneOf1_2 && bOneOf3_4) ) {
			if (bThree) {
				resampledData[i] = sourceData[volIndx3];
			} else if (bFour) {
				resampledData[i] = sourceData[volIndx4];
			}
		}
		//
		// Can there be a situation where bNoneOf1_2 && bNoneOf3_4 are both true, ie, all four points are not 
		// in the specific subvolume of refMesh?? If so, what is the value of resampledData in sourceMesh?
		// Should we take it as the value of data at the point closest to this point within the specific region
		// in refMesh? If so, how does one find such a point?
		//
		if (bNoneOf1_2 && bNoneOf3_4) {
			// resampledData[i] = BLAH_BLAH;
			throw new RuntimeException("Cannot handle the case where all 4 points are not in ref subvolume1");
		}
	}
	return resampledData;
}


/**
 * Insert the method's description here.
 * Creation date: (10/27/2003 5:07:42 PM)
 * @return double[]
 * @param data double[]
 * @param sourceMesh cbit.vcell.solvers.CartesianMesh
 * @param targetMesh cbit.vcell.solvers.CartesianMesh
 */
public static double[] resample2DSpatialSimple(double[] sourceData, CartesianMesh sourceMesh, CartesianMesh refMesh) {
	if (sourceData.length!=sourceMesh.getNumVolumeElements()){
		throw new RuntimeException("must be volume data, data length doesn't match number of volume elements");
	}
	// for volume samples:
	//
	//  loop through volumeIndexes from refMesh
	//      Coordinate refCoordinate = refMesh.getCoordinate(volumeIndex);
	//          Coordinate fractionalIndex = sourceMesh.getFractionCoordinateIndex(Coordinate refCoordinate);
	//              ....interpolate in y
	//              start with integer portion of fractionIndex

	double resampledData[] = new double[refMesh.getSizeX()*refMesh.getSizeY()*refMesh.getSizeZ()];
	for (int i = 0; i < resampledData.length; i++) {
		Coordinate refCoordinate = refMesh.getCoordinateFromVolumeIndex(i);
		Coordinate fractionalIndex = sourceMesh.getFractionalCoordinateIndex(refCoordinate);
		int ceil_x;
		int floor_x;
		int ceil_y;
		int floor_y;

		if (fractionalIndex.getX() < 0) {
			floor_x = 0;			
			ceil_x = 1;
		} else if (fractionalIndex.getX() > sourceMesh.getSizeX()-1) {
			floor_x = sourceMesh.getSizeX()-2;
			ceil_x =  sourceMesh.getSizeX()-1;
		} else {
			ceil_x = (int)Math.ceil(fractionalIndex.getX());
			floor_x = (int)Math.floor(fractionalIndex.getX());
		}
		if (fractionalIndex.getY() < 0) {
			floor_y = 0;			
			ceil_y = 1;
		} else if (fractionalIndex.getY() > sourceMesh.getSizeY()-1) {
			floor_y = sourceMesh.getSizeY()-2;
			ceil_y =  sourceMesh.getSizeY()-1;
		} else {
			ceil_y = (int)Math.ceil(fractionalIndex.getY());
			floor_y = (int)Math.floor(fractionalIndex.getY());
		}		
		
		double fract_x = fractionalIndex.getX() - floor_x;
		double fract_y = fractionalIndex.getY() - floor_y;
		
		CoordinateIndex coord_1 = new CoordinateIndex(floor_x, floor_y, 0);  // ***** SHOULD THIS BE 0 OR 1 ???? *****
		CoordinateIndex coord_2 = new CoordinateIndex(ceil_x, floor_y, 0);
		CoordinateIndex coord_3 = new CoordinateIndex(floor_x, ceil_y, 0);
		CoordinateIndex coord_4 = new CoordinateIndex(ceil_x, ceil_y, 0);
		
		int volIndx1 = sourceMesh.getVolumeIndex(coord_1);
		int volIndx2 = sourceMesh.getVolumeIndex(coord_2);
		int volIndx3 = sourceMesh.getVolumeIndex(coord_3);
		int volIndx4 = sourceMesh.getVolumeIndex(coord_4);


		// Interpolate in X (first order)
		double val_a = sourceData[volIndx1] + fract_x*(sourceData[volIndx2] - sourceData[volIndx1]);
		double val_b = sourceData[volIndx3] + fract_x*(sourceData[volIndx4] - sourceData[volIndx3]);

		// Interpolate in Y - final resampledSourceData value
		resampledData[i] = val_a + fract_y*(val_b - val_a);
	}
	return resampledData;
}


/**
 * Insert the method's description here.
 * Creation date: (10/27/2003 5:07:42 PM)
 * @return double[]
 * @param data double[]
 * @param sourceMesh cbit.vcell.solvers.CartesianMesh
 * @param targetMesh cbit.vcell.solvers.CartesianMesh
 */
public static double[] resample3DSpatial(double[] sourceData, CartesianMesh sourceMesh, CartesianMesh refMesh) {
	if (sourceData.length!=sourceMesh.getNumVolumeElements()){
		throw new RuntimeException("must be volume data, data length doesn't match number of volume elements");
	}
	// for volume samples:
	//
	//  loop through volumeIndexes from refMesh
	//      Coordinate refCoordinate = refMesh.getCoordinate(volumeIndex);
	//          Coordinate fractionalIndex = sourceMesh.getFractionCoordinateIndex(Coordinate refCoordinate);
	//              ....interpolate in z
	//              start with integer portion of fractionIndex

	double resampledData[] = new double[refMesh.getSizeX()*refMesh.getSizeY()*refMesh.getSizeZ()];
	for (int i = 0; i < resampledData.length; i++) {
		Coordinate refCoordinate = refMesh.getCoordinateFromVolumeIndex(i);
		Coordinate fractionalIndex = sourceMesh.getFractionalCoordinateIndex(refCoordinate);
		int ceil_x;
		int floor_x;
		int ceil_y;
		int floor_y;
		int ceil_z;
		int floor_z;			

		if (fractionalIndex.getX() < 0) {
			floor_x = 0;			
			ceil_x = 1;
		} else if (fractionalIndex.getX() > sourceMesh.getSizeX()-1) {
			floor_x = sourceMesh.getSizeX()-2;
			ceil_x =  sourceMesh.getSizeX()-1;
		} else {
			ceil_x = (int)Math.ceil(fractionalIndex.getX());
			floor_x = (int)Math.floor(fractionalIndex.getX());
		}
		if (fractionalIndex.getY() < 0) {
			floor_y = 0;			
			ceil_y = 1;
		} else if (fractionalIndex.getY() > sourceMesh.getSizeY()-1) {
			floor_y = sourceMesh.getSizeY()-2;
			ceil_y =  sourceMesh.getSizeY()-1;
		} else {
			ceil_y = (int)Math.ceil(fractionalIndex.getY());
			floor_y = (int)Math.floor(fractionalIndex.getY());
		}
		if (fractionalIndex.getZ() < 0) {
			floor_z = 0;			
			ceil_z = 1;
		} else if (fractionalIndex.getZ() > sourceMesh.getSizeZ()-1) {
			floor_z = sourceMesh.getSizeZ()-2;
			ceil_z =  sourceMesh.getSizeZ()-1;
		} else {
			ceil_z = (int)Math.ceil(fractionalIndex.getZ());
			floor_z = (int)Math.floor(fractionalIndex.getZ());
		}			

		double fract_x = fractionalIndex.getX() - floor_x;
		double fract_y = fractionalIndex.getY() - floor_y;
		double fract_z = fractionalIndex.getZ() - floor_z;

		CoordinateIndex coord_1 = new CoordinateIndex(floor_x, floor_y, floor_z);
		CoordinateIndex coord_2 = new CoordinateIndex(ceil_x, floor_y, floor_z);
		CoordinateIndex coord_3 = new CoordinateIndex(floor_x, floor_y, ceil_z);
		CoordinateIndex coord_4 = new CoordinateIndex(ceil_x, floor_y, ceil_z);	
		CoordinateIndex coord_5 = new CoordinateIndex(floor_x, ceil_y, ceil_z);
		CoordinateIndex coord_6 = new CoordinateIndex(ceil_x, ceil_y, ceil_z);
		CoordinateIndex coord_7 = new CoordinateIndex(floor_x, ceil_y, floor_z);
		CoordinateIndex coord_8 = new CoordinateIndex(ceil_x, ceil_y, floor_z);
		
		int volIndx1 = sourceMesh.getVolumeIndex(coord_1);
		int volIndx2 = sourceMesh.getVolumeIndex(coord_2);
		int volIndx3 = sourceMesh.getVolumeIndex(coord_3);
		int volIndx4 = sourceMesh.getVolumeIndex(coord_4);
		int volIndx5 = sourceMesh.getVolumeIndex(coord_5);		
		int volIndx6 = sourceMesh.getVolumeIndex(coord_6);
		int volIndx7 = sourceMesh.getVolumeIndex(coord_7);
		int volIndx8 = sourceMesh.getVolumeIndex(coord_8);
		
		// Get data values from 'sourceData' using vol_indices.
		// use it to compute a, b, c, d, then e & f	 ** Interpolation in X **
		if (sourceMesh.getSubVolumeFromVolumeIndex(volIndx1) == refMesh.getSubVolumeFromVolumeIndex(i) && 
			sourceMesh.getSubVolumeFromVolumeIndex(volIndx2) != refMesh.getSubVolumeFromVolumeIndex(i)) {
			volIndx2 = volIndx1;
		} else if (sourceMesh.getSubVolumeFromVolumeIndex(volIndx1) != refMesh.getSubVolumeFromVolumeIndex(i) && 
					sourceMesh.getSubVolumeFromVolumeIndex(volIndx2) == refMesh.getSubVolumeFromVolumeIndex(i)) {
			volIndx1 = volIndx2;
		} else if (sourceMesh.getSubVolumeFromVolumeIndex(volIndx1) != refMesh.getSubVolumeFromVolumeIndex(i) &&
				sourceMesh.getSubVolumeFromVolumeIndex(volIndx2) != refMesh.getSubVolumeFromVolumeIndex(i)) {	
			throw new RuntimeException("Either subvolume in sourceMesh not equal to refMesh subVol cannot be handled at this time!");
		}
		if (sourceMesh.getSubVolumeFromVolumeIndex(volIndx3) == refMesh.getSubVolumeFromVolumeIndex(i) && 
			sourceMesh.getSubVolumeFromVolumeIndex(volIndx4) != refMesh.getSubVolumeFromVolumeIndex(i)) {
			volIndx4 = volIndx3;
		} else if (sourceMesh.getSubVolumeFromVolumeIndex(volIndx3) != refMesh.getSubVolumeFromVolumeIndex(i) && 
					sourceMesh.getSubVolumeFromVolumeIndex(volIndx4) == refMesh.getSubVolumeFromVolumeIndex(i)) {
			volIndx3 = volIndx4;
		} else if (sourceMesh.getSubVolumeFromVolumeIndex(volIndx3) != refMesh.getSubVolumeFromVolumeIndex(i) &&
			sourceMesh.getSubVolumeFromVolumeIndex(volIndx4) != refMesh.getSubVolumeFromVolumeIndex(i)) {
			throw new RuntimeException("Either subvolume in sourceMesh not equal to refMesh subVol cannot be handled at this time!");
		}
		if (sourceMesh.getSubVolumeFromVolumeIndex(volIndx5) == refMesh.getSubVolumeFromVolumeIndex(i) && 
			sourceMesh.getSubVolumeFromVolumeIndex(volIndx6) != refMesh.getSubVolumeFromVolumeIndex(i)) {
			volIndx6 = volIndx5;
		} else if (sourceMesh.getSubVolumeFromVolumeIndex(volIndx5) != refMesh.getSubVolumeFromVolumeIndex(i) && 
					sourceMesh.getSubVolumeFromVolumeIndex(volIndx6) == refMesh.getSubVolumeFromVolumeIndex(i)) {
			volIndx5 = volIndx6;
		} else if (sourceMesh.getSubVolumeFromVolumeIndex(volIndx5) != refMesh.getSubVolumeFromVolumeIndex(i) &&
			sourceMesh.getSubVolumeFromVolumeIndex(volIndx6) != refMesh.getSubVolumeFromVolumeIndex(i)) {
			throw new RuntimeException("Either subvolume in sourceMesh not equal to refMesh subVol cannot be handled at this time!");
		}
		if (sourceMesh.getSubVolumeFromVolumeIndex(volIndx7) == refMesh.getSubVolumeFromVolumeIndex(i) && 
			sourceMesh.getSubVolumeFromVolumeIndex(volIndx8) != refMesh.getSubVolumeFromVolumeIndex(i)) {
			volIndx8 = volIndx7;
		} else if (sourceMesh.getSubVolumeFromVolumeIndex(volIndx7) != refMesh.getSubVolumeFromVolumeIndex(i) && 
					sourceMesh.getSubVolumeFromVolumeIndex(volIndx8) == refMesh.getSubVolumeFromVolumeIndex(i)) {
			volIndx7 = volIndx8;
		} else if (sourceMesh.getSubVolumeFromVolumeIndex(volIndx7) != refMesh.getSubVolumeFromVolumeIndex(i) &&
			sourceMesh.getSubVolumeFromVolumeIndex(volIndx8) != refMesh.getSubVolumeFromVolumeIndex(i)) {
			throw new RuntimeException("Either subvolume in sourceMesh not equal to refMesh subVol cannot be handled at this time!");
		}
		double val_a = sourceData[volIndx1] + fract_x*(sourceData[volIndx2] - sourceData[volIndx1]);
		double val_b = sourceData[volIndx3] + fract_x*(sourceData[volIndx4] - sourceData[volIndx3]);
		double val_c = sourceData[volIndx5] + fract_x*(sourceData[volIndx6] - sourceData[volIndx5]);
		double val_d = sourceData[volIndx7] + fract_x*(sourceData[volIndx8] - sourceData[volIndx7]);

		// Interpolate in Y
		double val_e = val_a + fract_y*(val_d - val_a);
		double val_f = val_b + fract_y*(val_c - val_b);

		// Interpolate in Z - final resampledSourceData value
		resampledData[i] = val_e + fract_z*(val_f - val_e);
	}
	return resampledData;
}


/**
 * Insert the method's description here.
 * Creation date: (10/27/2003 5:07:42 PM)
 * @return double[]
 * @param data double[]
 * @param sourceMesh cbit.vcell.solvers.CartesianMesh
 * @param targetMesh cbit.vcell.solvers.CartesianMesh
 */
public static double[] resample3DSpatialSimple(double[] sourceData, CartesianMesh sourceMesh, CartesianMesh refMesh) {
	if (sourceData.length!=sourceMesh.getNumVolumeElements()){
		throw new RuntimeException("must be volume data, data length doesn't match number of volume elements");
	}
	// for volume samples:
	//
	//  loop through volumeIndexes from refMesh
	//      Coordinate refCoordinate = refMesh.getCoordinate(volumeIndex);
	//          Coordinate fractionalIndex = sourceMesh.getFractionCoordinateIndex(Coordinate refCoordinate);
	//              ....interpolate in z
	//              start with integer portion of fractionIndex

	double resampledData[] = new double[refMesh.getSizeX()*refMesh.getSizeY()*refMesh.getSizeZ()];
	for (int i = 0; i < resampledData.length; i++) {
		Coordinate refCoordinate = refMesh.getCoordinateFromVolumeIndex(i);
		Coordinate fractionalIndex = sourceMesh.getFractionalCoordinateIndex(refCoordinate);
		int ceil_x;
		int floor_x;
		int ceil_y;
		int floor_y;
		int ceil_z;
		int floor_z;			

		if (fractionalIndex.getX() < 0) {
			floor_x = 0;			
			ceil_x = 1;
		} else if (fractionalIndex.getX() > sourceMesh.getSizeX()-1) {
			floor_x = sourceMesh.getSizeX()-2;
			ceil_x =  sourceMesh.getSizeX()-1;
		} else {
			ceil_x = (int)Math.ceil(fractionalIndex.getX());
			floor_x = (int)Math.floor(fractionalIndex.getX());
		}
		if (fractionalIndex.getY() < 0) {
			floor_y = 0;			
			ceil_y = 1;
		} else if (fractionalIndex.getY() > sourceMesh.getSizeY()-1) {
			floor_y = sourceMesh.getSizeY()-2;
			ceil_y =  sourceMesh.getSizeY()-1;
		} else {
			ceil_y = (int)Math.ceil(fractionalIndex.getY());
			floor_y = (int)Math.floor(fractionalIndex.getY());
		}
		if (fractionalIndex.getZ() < 0) {
			floor_z = 0;			
			ceil_z = 1;
		} else if (fractionalIndex.getZ() > sourceMesh.getSizeZ()-1) {
			floor_z = sourceMesh.getSizeZ()-2;
			ceil_z =  sourceMesh.getSizeZ()-1;
		} else {
			ceil_z = (int)Math.ceil(fractionalIndex.getZ());
			floor_z = (int)Math.floor(fractionalIndex.getZ());
		}			

		double fract_x = fractionalIndex.getX() - floor_x;
		double fract_y = fractionalIndex.getY() - floor_y;
		double fract_z = fractionalIndex.getZ() - floor_z;

		CoordinateIndex coord_1 = new CoordinateIndex(floor_x, floor_y, floor_z);
		CoordinateIndex coord_2 = new CoordinateIndex(ceil_x, floor_y, floor_z);
		CoordinateIndex coord_3 = new CoordinateIndex(floor_x, floor_y, ceil_z);
		CoordinateIndex coord_4 = new CoordinateIndex(ceil_x, floor_y, ceil_z);	
		CoordinateIndex coord_5 = new CoordinateIndex(floor_x, ceil_y, ceil_z);
		CoordinateIndex coord_6 = new CoordinateIndex(ceil_x, ceil_y, ceil_z);
		CoordinateIndex coord_7 = new CoordinateIndex(floor_x, ceil_y, floor_z);
		CoordinateIndex coord_8 = new CoordinateIndex(ceil_x, ceil_y, floor_z);
		
		int volIndx1 = sourceMesh.getVolumeIndex(coord_1);
		int volIndx2 = sourceMesh.getVolumeIndex(coord_2);
		int volIndx3 = sourceMesh.getVolumeIndex(coord_3);
		int volIndx4 = sourceMesh.getVolumeIndex(coord_4);
		int volIndx5 = sourceMesh.getVolumeIndex(coord_5);		
		int volIndx6 = sourceMesh.getVolumeIndex(coord_6);
		int volIndx7 = sourceMesh.getVolumeIndex(coord_7);
		int volIndx8 = sourceMesh.getVolumeIndex(coord_8);		
		
		double val_a = sourceData[volIndx1] + fract_x*(sourceData[volIndx2] - sourceData[volIndx1]);
		double val_b = sourceData[volIndx3] + fract_x*(sourceData[volIndx4] - sourceData[volIndx3]);
		double val_c = sourceData[volIndx5] + fract_x*(sourceData[volIndx6] - sourceData[volIndx5]);
		double val_d = sourceData[volIndx7] + fract_x*(sourceData[volIndx8] - sourceData[volIndx7]);

		// Interpolate in Y
		double val_e = val_a + fract_y*(val_d - val_a);
		double val_f = val_b + fract_y*(val_c - val_b);

		// Interpolate in Z - final resampledSourceData value
		resampledData[i] = val_e + fract_z*(val_f - val_e);
	}
	return resampledData;
}


/**
 * Insert the method's description here.
 * Creation date: (1/24/2003 10:18:14 AM)
 * @return cbit.vcell.parser.Expression
 * @param origExp cbit.vcell.parser.Expression
 * @param subDomain cbit.vcell.math.SubDomain
 */
private static Expression substituteFunctions(Expression origExp, MathDescription exactMathDesc) throws ExpressionException {
	Expression substitutedExp = new Expression(origExp);
	substitutedExp.bindExpression(exactMathDesc);
	substitutedExp = MathUtilities.substituteFunctions(substitutedExp,exactMathDesc);
	substitutedExp.bindExpression(null);
	substitutedExp = substitutedExp.flatten();
	substitutedExp.bindExpression(exactMathDesc);
	// substitutedExp.bindExpression(null);
	return substitutedExp;
}


/**
 * Insert the method's description here.
 * Creation date: (1/24/2003 10:18:14 AM)
 * @return cbit.vcell.parser.Expression
 * @param origExp cbit.vcell.parser.Expression
 * @param subDomain cbit.vcell.math.SubDomain
 */
private static Expression substituteWithExactSolution(Expression origExp, CompartmentSubDomain subDomain, MathDescription exactMathDesc) throws ExpressionException {
	Expression substitutedExp = new Expression(origExp);
	substitutedExp.bindExpression(exactMathDesc);
	substitutedExp = MathUtilities.substituteFunctions(substitutedExp,exactMathDesc);
	substitutedExp.bindExpression(null);
	substitutedExp = substitutedExp.flatten();
	substitutedExp.bindExpression(exactMathDesc);
	String symbols[] = substitutedExp.getSymbols();
	for (int i = 0; i < symbols.length; i++){
		Variable var = (Variable)substitutedExp.getSymbolBinding(symbols[i]);
		if (var instanceof VolVariable){
			String exactVarName = var.getName()+"_"+subDomain.getName()+"_exact";
			substitutedExp.substituteInPlace(new Expression(var.getName()),new Expression(exactVarName));
		}else if (var instanceof VolumeRegionVariable 
					|| var instanceof MemVariable || var instanceof MembraneRegionVariable
					|| var instanceof FilamentVariable || var instanceof FilamentRegionVariable){
			throw new RuntimeException("variable substitution not yet implemented for Variable type "+var.getClass().getName()+"("+var.getName()+")");
		}
	}
	substitutedExp.bindExpression(null);
	return substitutedExp;
}


/**
 * Insert the method's description here.
 * Creation date: (1/24/2003 10:18:14 AM)
 * @return cbit.vcell.parser.Expression
 * @param origExp cbit.vcell.parser.Expression
 * @param subDomain cbit.vcell.math.SubDomain
 */
private static Expression substituteWithExactSolution(Expression origExp, MembraneSubDomain subDomain, MathDescription exactMathDesc) throws ExpressionException {
	Expression substitutedExp = new Expression(origExp);
	substitutedExp.bindExpression(exactMathDesc);
	substitutedExp = MathUtilities.substituteFunctions(substitutedExp,exactMathDesc);
	substitutedExp.bindExpression(null);
	substitutedExp = substitutedExp.flatten();
	substitutedExp.bindExpression(exactMathDesc);
	String symbols[] = substitutedExp.getSymbols();
	for (int i = 0; symbols!=null && i < symbols.length; i++){
		Variable var = (Variable)substitutedExp.getSymbolBinding(symbols[i]);
		if (var instanceof MemVariable){
			String exactVarName = var.getName()+"_"+subDomain.getName()+"_exact";
			substitutedExp.substituteInPlace(new Expression(var.getName()),new Expression(exactVarName));
		}else if (var instanceof InsideVariable){
			String exactVarName = var.getName()+"_"+subDomain.getInsideCompartment().getName()+"_exact";
			substitutedExp.substituteInPlace(new Expression(var.getName()),new Expression(exactVarName));
		}else if (var instanceof OutsideVariable){
			String exactVarName = var.getName()+"_"+subDomain.getOutsideCompartment().getName()+"_exact";
			substitutedExp.substituteInPlace(new Expression(var.getName()),new Expression(exactVarName));
		}else if (var instanceof VolumeRegionVariable 
					|| var instanceof MemVariable || var instanceof MembraneRegionVariable
					|| var instanceof FilamentVariable || var instanceof FilamentRegionVariable){
			throw new RuntimeException("variable substitution not yet implemented for Variable type "+var.getClass().getName()+"("+var.getName()+")");
		}
	}
	substitutedExp.bindExpression(null);
	return substitutedExp;
}

public static double taylorInterpolation(
    double reqdTimePt,
    double[] neighboringTimePts,
    double[] neighboringValues) {
    //
    // This method applies a Taylor's series approximation to interpolate the function value at a required time point.
    // 'reqdTimePt' is the point at which the value of the function is required.
    // The 'neighboringTimePts' array contains the time points before and after the 'reqdTimePt' at which the value
    // of the function is known, using which the value of fn at 'reqdTimePt' has to be interpolated.
    // The 'neighboringValues' array contains the values of function at the time points provided in 'neighboringTimePts'.
    //

    if (neighboringTimePts.length != neighboringValues.length) {
        throw new RuntimeException("Number of values provided in the 2 arrays are not equal, cannot proceed!");
    }

    // 
    // Create a matrix (A_matrix) with the neighboring time points. The matrix is of the form :
    //
    //		1	del_t1	(del_t1^2)/2!	(del_t1^3)/3!
    //		1	del_t2  (del_t2^2)/2!	(del_t2^3)/3!
    //		1	del_t3  (del_t3^2)/2!	(del_t3^3)/3!
    //		1	del_t4  (del_t4^2)/2!	(del_t4^3)/3!
    //
    // if interpolation is done using 4 points; 
    // where del_ti is the difference between reqdTimePt and time points used for interpolation.
    // 
    int dim = neighboringTimePts.length;
    Jama.Matrix A_matrix = new Jama.Matrix(dim, dim);
    for (int i = 0; i < dim; i++) {
        for (int j = 0; j < dim; j++) {
            double val = neighboringTimePts[i] - reqdTimePt;
            val = Math.pow(val, j);
            val = val / factorial(j);
            A_matrix.set(i, j, val);
        }
    }

    // B_matrix is a column matrix containing the values of functions at the known time points (neighboringTimePts).
    Jama.Matrix B_matrix = new Jama.Matrix(neighboringValues, dim);

    // Solve A_matrix * F = B_matrix to obtain F, which is the transpose of [ f(t)	f'(t)	f''(t)	f'''(t) ]
    Jama.Matrix solutionMatrix = A_matrix.solve(B_matrix);

    // The required interpolated value at 'reqdTimePt' is the first value in solutionMatrix (F)
    double reqdValue = solutionMatrix.get(0, 0);

    return reqdValue;
}

/**
 * Insert the method's description here.
 * Creation date: (12/28/2004 12:21:16 PM)
 * @return int
 * @param n int
 */
private static int factorial(int n) {
    if (n < 0) {
        throw new RuntimeException("Cannot evaluate factorial of negative number");
    }

    int factorial = 1;
    int index = 1;
    while (index <= n) {
        factorial *= index;
        index++;
    }

    return factorial;
}
}
