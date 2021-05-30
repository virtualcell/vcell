/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.export.server;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

import org.apache.commons.lang3.StringUtils;
import org.vcell.solver.smoldyn.SmoldynVCellMapper;
import org.vcell.solver.smoldyn.SmoldynVCellMapper.SmoldynKeyword;
import org.vcell.util.BeanUtils;
import org.vcell.util.Coordinate;
import org.vcell.util.DataAccessException;
import org.vcell.util.NumberUtils;
import org.vcell.util.VCAssert;
import org.vcell.util.document.TSJobResultsNoStats;
import org.vcell.util.document.TimeSeriesJobSpec;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDataIdentifier;
import org.vcell.util.document.VCDataJobID;

import cbit.vcell.export.server.FileDataContainerManager.FileDataContainerID;
import cbit.vcell.geometry.SinglePoint;
import cbit.vcell.math.VariableType;
import cbit.vcell.simdata.DataServerImpl;
import cbit.vcell.simdata.Hdf5Utils;
import cbit.vcell.simdata.OutputContext;
import cbit.vcell.simdata.ParticleDataBlock;
import cbit.vcell.simdata.SimDataBlock;
import cbit.vcell.simdata.SimulationData;
import cbit.vcell.simdata.SpatialSelection;
import cbit.vcell.simdata.SpatialSelectionMembrane;
import cbit.vcell.simdata.SpatialSelectionVolume;
import cbit.vcell.solver.ode.ODESimData;
import cbit.vcell.solvers.CartesianMesh;
import edu.uchc.connjur.wb.ExecutionTrace;
import ncsa.hdf.hdf5lib.H5;
import ncsa.hdf.hdf5lib.HDF5Constants;
import ncsa.hdf.hdf5lib.exceptions.HDF5Exception;
import ncsa.hdf.hdf5lib.exceptions.HDF5LibraryException;
/**
 * Insert the type's description here.
 * Creation date: (4/27/2004 1:38:59 PM)
 * @author: Ion Moraru
 */
public class ASCIIExporter implements ExportConstants {
	private ExportServiceImpl exportServiceImpl = null;

/**
 * Insert the method's description here.
 * Creation date: (4/27/2004 1:18:37 PM)
 * @param exportServiceImpl cbit.vcell.export.server.ExportServiceImpl
 */
public ASCIIExporter(ExportServiceImpl exportServiceImpl) {
	this.exportServiceImpl = exportServiceImpl;
}

/**
 * @throws IOException 
 * @deprecated
 */
private List<ExportOutput> exportODEData(OutputContext outputContext,long jobID, User user, DataServerImpl dataServerImpl, VCDataIdentifier vcdID, VariableSpecs variableSpecs, TimeSpecs timeSpecs, ASCIISpecs asciiSpecs,FileDataContainerManager fileDataContainerManager) throws DataAccessException, IOException {
	String simID = vcdID.getID();
	String dataType = ".csv";
	String dataID = "_";
	//StringBuilder data = new StringBuilder();
	ExportOutput exportOutput = new ExportOutput(true, dataType, simID, dataID, fileDataContainerManager);
	SimulationDescription simulationDescription = new SimulationDescription(outputContext, user, dataServerImpl,vcdID,true, null);
	fileDataContainerManager.append(exportOutput.getFileDataContainerID(),simulationDescription.getHeader(dataType));
	fileDataContainerManager.append(exportOutput.getFileDataContainerID(),getODEDataValues(jobID, user, dataServerImpl, vcdID, variableSpecs.getVariableNames(), timeSpecs.getBeginTimeIndex(), timeSpecs.getEndTimeIndex(), asciiSpecs.getSwitchRowsColumns(),fileDataContainerManager));
	dataID += variableSpecs.getModeID() == 0 ? variableSpecs.getVariableNames()[0] : "ManyVars";
	return Arrays.asList(exportOutput);
}
 
private static final SmoldynKeyword[] SMOLDYN_KEYWORDS_USED = {SmoldynVCellMapper.MAP_PARTICLE_TO_MEMBRANE, SmoldynKeyword.solution};
/**
 * enum to model first few rows of switched particle CSV data; ordinals correspond to line index of element
 */
private enum SwitchedRowsHeading {
	TIME,
	PARTICLE,
	XYZ,
	/**
	 * first line actual data goes on
	 */
	DATA
}

/**
 * @param size of array
 * @return StringBuilder array, with empty objects
 */
private StringBuilder[] stringBuilderArray(int size) {
	StringBuilder rval[] = new StringBuilder[size];
	for (int i = 0; i < size; i++) {
		rval[i] = new StringBuilder( );
	}
	return rval;
}

/**
 * Insert the method's description here.
 * Creation date: (1/12/00 5:00:28 PM)
 * @return cbit.vcell.export.server.ExportOutput[]
 * @param dsc cbit.vcell.server.DataSetController
 * @param timeSpecs cbit.vcell.export.server.TimeSpecs
 * @throws IOException 
 */

private List<ExportOutput> exportParticleData(OutputContext outputContext,long jobID, User user, DataServerImpl dataServerImpl, ExportSpecs exportSpecs,  
		ASCIISpecs asciiSpecs,FileDataContainerManager fileDataContainerManager) throws DataAccessException, IOException {

	VCDataIdentifier vcdID = exportSpecs.getVCDataIdentifier();
	TimeSpecs timeSpecs = exportSpecs.getTimeSpecs();
	String simID = vcdID.getID();
	String dataType = ".csv";
	final int N_PARTICLE_PIECES = 1; //in switched format, how many rows for each particle

	// get parameters
	boolean switchRowsColumns = asciiSpecs.getSwitchRowsColumns();
	double[] allTimes = timeSpecs.getAllTimes();
	int beginIndex = timeSpecs.getBeginTimeIndex();
	int endIndex = timeSpecs.getEndTimeIndex();
	ParticleDataBlock particleDataBlk = dataServerImpl.getParticleDataBlock(user, vcdID,allTimes[beginIndex]);
	VariableSpecs vs = exportSpecs.getVariableSpecs();
	VCAssert.assertValid(vs);
	String[] vnames = vs.getVariableNames();
	if (vnames.length == 0) {
		throw new IllegalArgumentException("No variables selected");
	}
	//need array for SimulationDescription, later
	final String currentVariableName[] = new String[1];
	
	ArrayList<ExportOutput> rval = new ArrayList<>(vnames.length);
	Set<String> species = particleDataBlk.getSpecies();
	ParticleProgress particleProgress = null;
	
	for (String vcellName : vnames) {
		String smoldynSpecies = null;
		for (int i = 0; smoldynSpecies == null && i < SMOLDYN_KEYWORDS_USED.length;i++) { 
			SmoldynKeyword kw = SMOLDYN_KEYWORDS_USED[i];
			String smoldynName = SmoldynVCellMapper.vcellToSmoldyn(vcellName, kw);
			if (species.contains(smoldynName)) {
				smoldynSpecies = smoldynName; 
			}
		}
		if (smoldynSpecies == null) {
			throw new DataAccessException("Unable to find match for variable name " + vcellName
					+ " in " + StringUtils.join(species,", ") );
		}
		

		List<Coordinate> particles = particleDataBlk.getCoordinates(smoldynSpecies);
		int numberOfParticles = particles.size(); 
		int numberOfTimes = endIndex - beginIndex + 1;
		if (particleProgress != null) {
			particleProgress.nextName();
		}
		else {
			particleProgress = new ParticleProgress(jobID, vcdID, vnames.length, numberOfTimes,numberOfParticles);
		}

		// now make csv formatted data
		StringBuilder header = new StringBuilder();
		StringBuilder[] dataLines = null;
		final int NUMBER_HEADING_LINES = SwitchedRowsHeading.DATA.ordinal();
		if (switchRowsColumns) {
			dataLines = stringBuilderArray(numberOfParticles * N_PARTICLE_PIECES + NUMBER_HEADING_LINES); 
			dataLines[SwitchedRowsHeading.TIME.ordinal()].append("Time,");
			final String particleLine = "Particle" + StringUtils.repeat(",x,y,z",numberOfTimes);
			dataLines[SwitchedRowsHeading.PARTICLE.ordinal()].append(particleLine);
			dataLines[SwitchedRowsHeading.XYZ.ordinal()].append("#,");
			final int FDL = SwitchedRowsHeading.DATA.ordinal(); //"first data line"
			for (int i=0;i<numberOfParticles;i++) {
				dataLines[FDL + N_PARTICLE_PIECES * i].append(i);
				dataLines[FDL + N_PARTICLE_PIECES * i].append(',');
			}
		} else {
			dataLines = stringBuilderArray(numberOfTimes);
		}
		currentVariableName[0] = vcellName;
		SimulationDescription simulationDescription = new SimulationDescription(outputContext,user, dataServerImpl,vcdID,false, currentVariableName);
		header.append(simulationDescription.getHeader(dataType));
		if (switchRowsColumns) {
			//implemented using first few data lines
		} else {
			header.append(",Time\n");
			header.append("Particle #,,");
			for (int k=0;k<numberOfParticles;k++) {
				header.append(k + StringUtils.repeat(',', N_PARTICLE_PIECES)  );
			}
			header.append("\n,,");
			for (int k=0;k<numberOfParticles;k++) {
				header.append("x,y,z,");
			}
		}
		final char COMMA  = ',';
		int nextTimeIndex = particleProgress.nextTimeIndex(0, false);
		for (int i=beginIndex;i<=endIndex;i++) {
			particleDataBlk = dataServerImpl.getParticleDataBlock(user, vcdID,allTimes[i]);
			particles = particleDataBlk.getCoordinates(smoldynSpecies);
			
			if (i >= nextTimeIndex) {
				nextTimeIndex = particleProgress.nextTimeIndex(i, true);
			}

			if (switchRowsColumns) {
				final int FDL = SwitchedRowsHeading.DATA.ordinal(); //"first data line"
				StringBuilder timeSb = dataLines[SwitchedRowsHeading.TIME.ordinal()];
				timeSb.append(allTimes[i]);
				timeSb.append(",,,");
				for (int j=0;j<numberOfParticles;j++) {
					StringBuilder sb = dataLines[FDL + N_PARTICLE_PIECES * j];
					Coordinate coordinate = particles.get(j);
					sb.append(coordinate.getX());
					sb.append(COMMA);
					sb.append(coordinate.getY());
					sb.append(COMMA);
					sb.append(coordinate.getZ());
					sb.append(COMMA);
				}
			} else {
		 		StringBuilder particleSb = dataLines[i - beginIndex];
		 		particleSb.append(COMMA);
		 		particleSb.append(allTimes[i]);
		 		particleSb.append(COMMA);
		 		for (int j=0;j<numberOfParticles;j++) {
		 			Coordinate coordinate = particles.get(j) ;
		 			particleSb.append( coordinate.getX() );
		 			particleSb.append(COMMA);
		 			particleSb.append( coordinate.getY() );
		 			particleSb.append(COMMA);
		 			particleSb.append( coordinate.getZ() );
		 			particleSb.append(COMMA);
		 		}
			}
		}
		particleProgress.endOfTimes();
		final String dataID = vcellName + "_Particles";
		ExportOutput exportOutputCSV = new ExportOutput(true, dataType, simID, dataID, fileDataContainerManager);
		fileDataContainerManager.append(exportOutputCSV.getFileDataContainerID(),header.toString());
		
		int nextDataIndex = particleProgress.nextDataIndex(0, false);
		
		StringBuilder all = new StringBuilder( );
		for (int i=0;i<dataLines.length;i++) {
			final char NEWLINE = '\n';
			all.append(dataLines[i]);
			dataLines[i] = null; //kill reference to allow garbage collection
			all.append(NEWLINE);
			
			if (i >= nextDataIndex) {
				nextDataIndex = particleProgress.nextDataIndex(i, true);
			}
		}
		fileDataContainerManager.append(exportOutputCSV.getFileDataContainerID(),all.toString());
		rval.add(exportOutputCSV);
	}
	return rval; 
}

private int[] getallSampleIndexes(GeometrySpecs geometrySpecs,CartesianMesh mesh) throws DataAccessException{
	ArrayList<Integer> sampleIndexes = new ArrayList<>();
	SpatialSelection[] spatialSelections = geometrySpecs.getSelections();
	for (int i = 0; i < spatialSelections.length; i++) {
		spatialSelections[i].setMesh(mesh);
	}
	//Add points
	if(geometrySpecs.getPointIndexes().length > 0){
		for(int i =0;i<geometrySpecs.getPointIndexes().length;i++){
			sampleIndexes.add(geometrySpecs.getPointIndexes()[i]);
		}
	}
	//Add curves
	if(geometrySpecs.getCurves().length != 0){
		for(int i =0;i<geometrySpecs.getCurves().length;i++){
			SpatialSelection curve = geometrySpecs.getCurves()[i];
			curve.setMesh(mesh);
			if (curve instanceof SpatialSelectionVolume){
				SpatialSelection.SSHelper ssh = ((SpatialSelectionVolume)curve).getIndexSamples(0.0,1.0);
				for(int j=0;j<ssh.getSampledIndexes().length;j++){
					sampleIndexes.add(ssh.getSampledIndexes()[j]);
				}
//				numSamplePoints+= ssh.getSampledIndexes().length;
//				pointIndexes = ssh.getSampledIndexes();
//				distances = ssh.getWorldCoordinateLengths();
//				crossingMembraneIndexes = ssh.getMembraneIndexesInOut();
			}else if(curve instanceof SpatialSelectionMembrane){
				SpatialSelection.SSHelper ssh = ((SpatialSelectionMembrane)curve).getIndexSamples();
				if(((SpatialSelectionMembrane)curve).getSelectionSource() instanceof SinglePoint){
					sampleIndexes.add(ssh.getSampledIndexes()[0]);
//					numSamplePoints++;
//					pointIndexes = new int[] {ssh.getSampledIndexes()[0]};
//					distances = new double[] {0};
				}else{
					for(int j=0;j<ssh.getSampledIndexes().length;j++){
						sampleIndexes.add(ssh.getSampledIndexes()[j]);
					}
//					numSamplePoints+= ssh.getSampledIndexes().length;
//					pointIndexes = ssh.getSampledIndexes();
//					distances = ssh.getWorldCoordinateLengths();
				}
			}
		}
	}
	if(sampleIndexes.size() > 0){
		int[] allSampleIndexes = new int[sampleIndexes.size()];
		for(int i=0;i<allSampleIndexes.length;i++){
			allSampleIndexes[i] = sampleIndexes.get(i);
		}
		return allSampleIndexes;
	}
	return null;
}

private ExportOutput sofyaFormat(OutputContext outputContext,long jobID, User user, DataServerImpl dataServerImpl,
		final VCDataIdentifier orig_vcdID, VariableSpecs variableSpecs, TimeSpecs timeSpecs, 
		GeometrySpecs geometrySpecs, ASCIISpecs asciiSpecs,String contextName,FileDataContainerManager fileDataContainerManager) throws DataAccessException,IOException{
	ExportSpecs.SimNameSimDataID[] simNameSimDataIDs = asciiSpecs.getSimNameSimDataIDs();
	CartesianMesh mesh = dataServerImpl.getMesh(user, orig_vcdID);//use mesh to calulate indexes
	final int SIM_COUNT = simNameSimDataIDs.length;
	final int PARAMSCAN_COUNT = (asciiSpecs.getExportMultipleParamScans() != null?asciiSpecs.getExportMultipleParamScans().length:1);
	final int TIME_COUNT = timeSpecs.getEndTimeIndex() - timeSpecs.getBeginTimeIndex() + 1;
	if(PARAMSCAN_COUNT > 1 || geometrySpecs.getModeID() != GEOMETRY_SELECTIONS/* || geometrySpecs.getCurves().length != 0*/){
		throw new DataAccessException("Alternate csv format cannot have parameter scans and must be 'point selection' type");
	}
	final long MESSAGE_LIMIT = 5000;//millisecodns
	final long MAX_DATA = 10000000;
	long totalPoints = SIM_COUNT*TIME_COUNT*variableSpecs.getVariableNames().length*geometrySpecs.getPointCount();
	if(totalPoints > MAX_DATA){
		throw new DataAccessException("Too much data, select fewer (sims or times or variables or samplepoints).  Exceeded limit by "+NumberUtils.formatNumber(100*(((double)totalPoints/(double)MAX_DATA)-1.0), 6)+"%");
	}
	ExportOutput exportOutput1 = new ExportOutput(true, ".csv",SIM_COUNT+"_multisims_", variableSpecs.getVariableNames().length+"_Vars_"+TIME_COUNT+"_times", fileDataContainerManager);
	fileDataContainerManager.append(exportOutput1.getFileDataContainerID(), "\"Model:'"+contextName+"'\"\n\n");
	
	int[] sampleIndexes = getallSampleIndexes(geometrySpecs,mesh);
	int[][] indexes = new int[variableSpecs.getVariableNames().length][];
	HashMap<Integer, TSJobResultsNoStats> simData = new HashMap<>();
	long lastTime = 0;
	double progressCounter = 0;
	for(int t=0;t<TIME_COUNT;t++){
		fileDataContainerManager.append(exportOutput1.getFileDataContainerID(), "Time,"+timeSpecs.getAllTimes()[timeSpecs.getBeginTimeIndex()+t]+"\n");
		for (int simIndex = 0; simIndex < SIM_COUNT; simIndex++) {
			progressCounter++;
			if((System.currentTimeMillis()-lastTime)>MESSAGE_LIMIT){
				lastTime = System.currentTimeMillis();
				exportServiceImpl.fireExportProgress(jobID, orig_vcdID, "multisim-point", progressCounter/(SIM_COUNT*TIME_COUNT));
			}
			int simJobIndex = simNameSimDataIDs[simIndex].getDefaultJobIndex();
			VCDataIdentifier vcdID = simNameSimDataIDs[simIndex].getVCDataIdentifier(simJobIndex);
			if(SIM_COUNT > 1){
				//check times are the same
				double[] currentTimes = dataServerImpl.getDataSetTimes(user, vcdID);
				if(currentTimes.length != timeSpecs.getAllTimes().length){
					throw new DataAccessException("time sets are different length");
				}
				for(int i=0;i<currentTimes.length;i++){
					if(timeSpecs.getAllTimes()[i] != currentTimes[i]){
						throw new DataAccessException("time sets have different values");
					}
				}
			}
			SpatialSelection[] spatialSelections = geometrySpecs.getSelections();
			 mesh = dataServerImpl.getMesh(user, vcdID);
			for (int i = 0; i < spatialSelections.length; i ++) {
				if(spatialSelections[i].getMesh() == null){
					spatialSelections[i].setMesh(mesh);
				}else if(!spatialSelections[i].getMesh().getISize().compareEqual(mesh.getISize()) ||
					spatialSelections[i].getMesh().getNumMembraneElements() != mesh.getNumMembraneElements()){//check just sizes not areas,normals,etc...
					//This will throw fail message
					spatialSelections[i].setMesh(mesh);
				}
			}
			if(simIndex == 0){
				fileDataContainerManager.append(exportOutput1.getFileDataContainerID(), "Variables-->,");
				for(int v=0;v<variableSpecs.getVariableNames().length;v++){
					fileDataContainerManager.append(exportOutput1.getFileDataContainerID(), "\""+variableSpecs.getVariableNames()[v]+"\"");
					for(int p=0;p<sampleIndexes.length;p++){
						fileDataContainerManager.append(exportOutput1.getFileDataContainerID(), ",");
					}
				}
				fileDataContainerManager.append(exportOutput1.getFileDataContainerID(), "\n\"Simulation Name : (point/line)Index-->\",");
				for(int v=0;v<variableSpecs.getVariableNames().length;v++){
					indexes[v] = sampleIndexes;
					for(int p=0;p<sampleIndexes.length;p++){
						fileDataContainerManager.append(exportOutput1.getFileDataContainerID(), sampleIndexes[p]+",");
					}					
				}
				fileDataContainerManager.append(exportOutput1.getFileDataContainerID(), "\n");
			}
			fileDataContainerManager.append(exportOutput1.getFileDataContainerID(),"\""+simNameSimDataIDs[simIndex].getSimulationName()+"\"");
			TSJobResultsNoStats timeSeriesJobResults = simData.get(simIndex);
			if(timeSeriesJobResults == null){
				TimeSeriesJobSpec timeSeriesJobSpec =
				new TimeSeriesJobSpec(
					variableSpecs.getVariableNames(),indexes,
					null,timeSpecs.getAllTimes()[timeSpecs.getBeginTimeIndex()],1,timeSpecs.getAllTimes()[timeSpecs.getEndTimeIndex()],
					VCDataJobID.createVCDataJobID(user, false));
				timeSeriesJobResults = (TSJobResultsNoStats)dataServerImpl.getTimeSeriesValues(outputContext,user, vcdID, timeSeriesJobSpec);
				simData.put(simIndex, timeSeriesJobResults);
			}
			// variableValues[0] is time array
			// variableValues[1] is values for 1st spatial point.
			// variableValues[2] is values for 2nd spatial point.
			// variableValues[n] (n>=1) is values for nth spatial point.
			// the length of variableValues should always be 1 + pointIndexes.length
			// the length of variableValues[n] is allTimes.length
			for(int v=0;v<variableSpecs.getVariableNames().length;v++){
				final double[][] variableValues = timeSeriesJobResults.getTimesAndValuesForVariable(variableSpecs.getVariableNames()[v]);
				for(int p=0;p<sampleIndexes.length;p++){
					fileDataContainerManager.append(exportOutput1.getFileDataContainerID(), ","+variableValues[p+1][t]);
				}
			}
			fileDataContainerManager.append(exportOutput1.getFileDataContainerID(), "\n");
		}
		fileDataContainerManager.append(exportOutput1.getFileDataContainerID(), "\n");
	}
	return exportOutput1;
}

public enum PCS {VCDID,VARNAME,POINTINFO,POINTVALS,TIMES,TIMEBOUNDS,CURVES,CURVEVALS,CURVEINDEXES,CURVEDISTANCES,CURVECROSSMEMBRINDEX};
private class PointsCurvesSlices {
	public TreeMap<PCS,Object> data = new TreeMap<PCS,Object>();
	public PointsCurvesSlices() {
		
	};
}
/**
 * This method was created in VisualAge.
 * @throws IOException 
 */
private List<ExportOutput> exportPDEData(OutputContext outputContext,long jobID, User user, DataServerImpl dataServerImpl,
		final VCDataIdentifier orig_vcdID, VariableSpecs variableSpecs, TimeSpecs timeSpecs, 
		GeometrySpecs geometrySpecs, ASCIISpecs asciiSpecs,String contextName,FileDataContainerManager fileDataContainerManager) 
						throws DataAccessException, IOException {
						
	
	ExportSpecs.SimNameSimDataID[] simNameSimDataIDs = asciiSpecs.getSimNameSimDataIDs();
	Vector<ExportOutput[]> exportOutputV = new Vector<ExportOutput[]>();
	double progressCounter = 0;
	final int SIM_COUNT = simNameSimDataIDs.length;
	final int PARAMSCAN_COUNT = (asciiSpecs.getExportMultipleParamScans() != null?asciiSpecs.getExportMultipleParamScans().length:1);
	final int endTimeIndex = timeSpecs.getEndTimeIndex();
	final int beginTimeIndex = timeSpecs.getBeginTimeIndex();
	final int TIME_COUNT = endTimeIndex - beginTimeIndex + 1;

	double TOTAL_EXPORTS_OPS = 0;
	switch (geometrySpecs.getModeID()) {
		case GEOMETRY_SELECTIONS:
			TOTAL_EXPORTS_OPS = SIM_COUNT*PARAMSCAN_COUNT*variableSpecs.getVariableNames().length*(geometrySpecs.getCurves().length+(geometrySpecs.getPointCount() > 0?1:0));
			break;
		case GEOMETRY_SLICE:
			TOTAL_EXPORTS_OPS = SIM_COUNT*PARAMSCAN_COUNT*variableSpecs.getVariableNames().length*TIME_COUNT;
			break;
	}
	File hdf5TempFile = null;
	if(asciiSpecs.getCSVRoiLayout() == ASCIISpecs.csvRoiLayout.time_sim_var){
		exportOutputV.add(new ExportOutput[] {sofyaFormat(outputContext, jobID, user, dataServerImpl, orig_vcdID, variableSpecs, timeSpecs, geometrySpecs, asciiSpecs, contextName, fileDataContainerManager)});
	}else{
		try {
		int hdf5FileID = -1;//Used if HDF5 format
		if(asciiSpecs.isHDF5()) {
			hdf5TempFile = File.createTempFile("pde", ".hdf5");
			hdf5FileID = H5.H5Fcreate(hdf5TempFile.getAbsolutePath(), HDF5Constants.H5F_ACC_TRUNC,HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
		}
//		TreeMap<VCDataIdentifier,TreeMap<String,PointsCurvesSlices>> simsVarnamesDataMap = new TreeMap<VCDataIdentifier,TreeMap<String,PointsCurvesSlices>>();
		PointsCurvesSlices[][] pointsCurvesSlices = new PointsCurvesSlices[SIM_COUNT][variableSpecs.getVariableNames().length];
		for(int i=0;i< pointsCurvesSlices.length;i++) {
			for(int j=0;j< pointsCurvesSlices[i].length;j++) {
				pointsCurvesSlices[i][j] = new PointsCurvesSlices();
			}
		}
		for (int v = 0; v < SIM_COUNT; v++) {
			int simJobIndex = simNameSimDataIDs[v].getDefaultJobIndex();
			VCDataIdentifier vcdID = simNameSimDataIDs[v].getVCDataIdentifier(simJobIndex);
			//3 states for parameter scan
			//1. simNameSimDataIDs[v].getExportParamScanInfo() == null, not a parameter scan
			//2. simNameSimDataIDs[v].getExportParamScanInfo() != null and asciiSpecs.getExportMultipleParamScans() == null, parameter scan use simNameSimDataIDs[v].getDefaultVCDataIdentifier()
			//3. simNameSimDataIDs[v].getExportParamScanInfo() != null and asciiSpecs.getExportMultipleParamScans() != null, parameter scan use simNameSimDataIDs[v].getExportParamScanInfo().getParamScanJobIndexes() loop through
			for (int ps = 0; ps < PARAMSCAN_COUNT; ps++) {
				if(asciiSpecs.getExportMultipleParamScans() != null){
					simJobIndex = simNameSimDataIDs[v].getExportParamScanInfo().getParamScanJobIndexes()[asciiSpecs.getExportMultipleParamScans()[ps]];
					vcdID = simNameSimDataIDs[v].getVCDataIdentifier(simJobIndex);
				}
				//Get times for each sim{paramscan} because they may be different
				final double[] allTimes = dataServerImpl.getDataSetTimes(user, vcdID);
				if(allTimes.length<= beginTimeIndex || allTimes.length<= endTimeIndex){
					throw new DataAccessException("Sim '"+simNameSimDataIDs[v].getSimulationName()+"' id="+vcdID.getID()+" simJob="+simJobIndex+", time array length="+allTimes.length+" has no endTimeIndex="+endTimeIndex);
				}
				String paramScanInfo = "";
				if(simNameSimDataIDs[v].getExportParamScanInfo() != null){
					for (int i = 0; i < simNameSimDataIDs[v].getExportParamScanInfo().getParamScanConstantNames().length; i++) {
						String psName = simNameSimDataIDs[v].getExportParamScanInfo().getParamScanConstantNames()[i];
						paramScanInfo = paramScanInfo+
						" '"+psName+"'="+simNameSimDataIDs[v].getExportParamScanInfo().getParamScanConstantValues()[simJobIndex][i];					
					}
				}
				int hdf5GroupID = -1;//Used if HDF5 format
				if(asciiSpecs.isHDF5()) {
					hdf5GroupID = H5.H5Gcreate(hdf5FileID, vcdID.toString(),HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
				}
				String simID = vcdID.getID();						
				String dataType = ".csv";
				FileDataContainerID fileDataContainerID_header = fileDataContainerManager.getNewFileDataContainerID();
				SimulationDescription simulationDescription = new SimulationDescription(outputContext,user, dataServerImpl,vcdID,false, null);
				fileDataContainerManager.append(fileDataContainerID_header,"\""+"Model: '"+contextName+"'\"\n\"Simulation: '"+simNameSimDataIDs[v].getSimulationName()+"' ("+paramScanInfo+")\"\n"+simulationDescription.getHeader(dataType));
				CartesianMesh mesh = dataServerImpl.getMesh(user, vcdID);

				if(hdf5GroupID != -1) {
					double[] subTimes = new double[endTimeIndex-beginTimeIndex+1];
					for(int st=beginTimeIndex;st<=endTimeIndex;st++) {
						subTimes[st-beginTimeIndex] = allTimes[st];
					}
					Hdf5Utils.writeHDF5Dataset(hdf5GroupID, PCS.TIMES.name(), new long[] {subTimes.length}, subTimes,false);
					Hdf5Utils.writeHDF5Dataset(hdf5GroupID, PCS.TIMEBOUNDS.name(), new long[] {2}, new int[] {beginTimeIndex,endTimeIndex},false);
				}

				switch (geometrySpecs.getModeID()) {
					case GEOMETRY_SELECTIONS: {
						// Set mesh on SpatialSelection because mesh is transient field because it's too big for messaging
						SpatialSelection[] spatialSelections = geometrySpecs.getSelections();
						for (int i = 0; i < spatialSelections.length; i ++) {
							if(spatialSelections[i].getMesh() == null){
								spatialSelections[i].setMesh(mesh);
							}else if(!spatialSelections[i].getMesh().getISize().compareEqual(mesh.getISize()) ||
								spatialSelections[i].getMesh().getNumMembraneElements() != mesh.getNumMembraneElements()){//check just sizes not areas,normals,etc...
								//This will throw fail message
								spatialSelections[i].setMesh(mesh);
							}
//							int hdf5DatasetID = -1;
//							int hdf5DataspaceID = -1;
//							if(hdf5GroupID != -1) {
//								long[] dims = new long[spatialSelections[i].getMesh().getGeometryDimension()];
//								String s = "";
//								for(int j=0; j<dims.length;j++) {
//									if(j!=0) {
//										s+="x";
//									}
//									dims[j] = (j==0?spatialSelections[j].getMesh().getISize().getX():(j==1?spatialSelections[j].getMesh().getISize().getY():spatialSelections[j].getMesh().getISize().getZ()));
//									s+=dims[j];
//								}
//								hdf5DataspaceID = H5.H5Screate_simple(dims.length, dims, null);
//								hdf5DataspaceIDs.add(H5.H5Dcreate(hdf5GroupID, dims.length+"D java double "+s,HDF5Constants.H5T_NATIVE_DOUBLE, hdf5DataspaceID,HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT));
//							}
						}

						Vector<ExportOutput> outputV = new Vector<ExportOutput>();
						if (geometrySpecs.getPointCount() > 0) {//assemble single point data together (uses more compact formatting)
							String dataID = "_Points_vars("+geometrySpecs.getPointCount()+")_times("+( endTimeIndex-beginTimeIndex+1)+")";
							//StringBuilder data1 = new StringBuilder(data.toString());
							ExportOutput exportOutput1 = new ExportOutput(true, dataType, simID, dataID/* + variableSpecs.getVariableNames()[i]*/, fileDataContainerManager);
							fileDataContainerManager.append(exportOutput1.getFileDataContainerID(), fileDataContainerID_header);
							int hdf5GroupPointID = -1;//Used if HDF5 format
							if(asciiSpecs.isHDF5()) {
								hdf5GroupPointID = H5.H5Gcreate(hdf5GroupID, "Points",HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
							}
							for (int varNameIndx=0;varNameIndx<variableSpecs.getVariableNames().length;varNameIndx++) {
								int hdf5GroupVarID = -1;//Used if HDF5 format
								if(asciiSpecs.isHDF5()) {
									hdf5GroupVarID = H5.H5Gcreate(hdf5GroupPointID, variableSpecs.getVariableNames()[varNameIndx],HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
								}
								fileDataContainerManager.append(exportOutput1.getFileDataContainerID(),
									getPointsTimeSeries(pointsCurvesSlices[v][varNameIndx],hdf5GroupVarID,outputContext,user, dataServerImpl, vcdID, variableSpecs.getVariableNames()[varNameIndx], geometrySpecs, allTimes, beginTimeIndex, endTimeIndex, asciiSpecs.getSwitchRowsColumns(),fileDataContainerManager));
								fileDataContainerManager.append(exportOutput1.getFileDataContainerID(),"\n");
								progressCounter++;
								exportServiceImpl.fireExportProgress(jobID, orig_vcdID, "CSV", progressCounter/TOTAL_EXPORTS_OPS);
								if(hdf5GroupVarID != -1) {
									H5.H5Gclose(hdf5GroupVarID);
								}
							}
							outputV.add(exportOutput1);
							if(hdf5GroupPointID != -1) {
								H5.H5Gclose(hdf5GroupPointID);
							}

						}
						if(geometrySpecs.getCurves().length != 0){//assemble curve (non-single point) data together
							String dataID = "_Curves_vars("+(geometrySpecs.getCurves().length)+")_times("+( endTimeIndex-beginTimeIndex+1)+")";
							//StringBuilder data1 = new StringBuilder(data.toString());
							ExportOutput exportOutput1 = new ExportOutput(true, dataType, simID, dataID/* + variableSpecs.getVariableNames()[i]*/, fileDataContainerManager);
							fileDataContainerManager.append(exportOutput1.getFileDataContainerID(), fileDataContainerID_header);
							int hdf5GroupPointID = -1;//Used if HDF5 format
							if(asciiSpecs.isHDF5()) {
								hdf5GroupPointID = H5.H5Gcreate(hdf5GroupID, "Curves",HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
								
							}
							for (int varNameIndx=0;varNameIndx<variableSpecs.getVariableNames().length;varNameIndx++) {
								int hdf5GroupVarID = -1;//Used if HDF5 format
								if(asciiSpecs.isHDF5()) {
									hdf5GroupVarID = H5.H5Gcreate(hdf5GroupPointID, variableSpecs.getVariableNames()[varNameIndx],HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
								}
								pointsCurvesSlices[v][varNameIndx].data.put(PCS.CURVES, new TreeMap<String,TreeMap<PCS,Object>>());
								for (int s = 0; s < geometrySpecs.getCurves().length; s++) {
									if(!GeometrySpecs.isSinglePoint(geometrySpecs.getCurves()[s])){
										fileDataContainerManager.append(exportOutput1.getFileDataContainerID(),getCurveTimeSeries(hdf5GroupVarID,pointsCurvesSlices[v][varNameIndx],outputContext,user, dataServerImpl, vcdID, variableSpecs.getVariableNames()[varNameIndx], geometrySpecs.getCurves()[s], allTimes, beginTimeIndex, endTimeIndex, asciiSpecs.getSwitchRowsColumns(),fileDataContainerManager));
										fileDataContainerManager.append(exportOutput1.getFileDataContainerID(),"\n");
										progressCounter++;
										exportServiceImpl.fireExportProgress(jobID, orig_vcdID, "CSV", progressCounter/TOTAL_EXPORTS_OPS);
									}
								}
								if(hdf5GroupVarID != -1) {
									H5.H5Gclose(hdf5GroupVarID);
								}
							}
							outputV.add(exportOutput1);
							if(hdf5GroupPointID != -1) {
								H5.H5Gclose(hdf5GroupPointID);
							}

						}
						exportOutputV.add(outputV.toArray(new ExportOutput[0]));
						break;
					}
					case GEOMETRY_SLICE: {
						String dataID = "_Slice_" + Coordinate.getNormalAxisPlaneName(geometrySpecs.getAxis()) + "_" + geometrySpecs.getSliceNumber() + "_";
						ExportOutput[] output = new ExportOutput[variableSpecs.getVariableNames().length * TIME_COUNT];
						SliceHelper sliceHelper = new SliceHelper(TIME_COUNT,(hdf5GroupID != -1),Coordinate.getNormalAxisPlaneName(geometrySpecs.getAxis()), geometrySpecs.getSliceNumber(), asciiSpecs.getSwitchRowsColumns(), mesh);
						for (int j=0;j<variableSpecs.getVariableNames().length;j++) {
							sliceHelper.setHDF5GroupVarID(hdf5GroupID,variableSpecs.getVariableNames()[j]);
							for (int i=0;i<TIME_COUNT;i++) {
								StringBuilder inset = new StringBuilder(Integer.toString(i + beginTimeIndex));
									inset.reverse();
									inset.append("000");
									inset.setLength(4);
									inset.reverse();
								String dataID1 = dataID + variableSpecs.getVariableNames()[j] + "_"+inset.toString();
								
								ExportOutput exportOutput1 = new ExportOutput(true, dataType, simID, dataID1/* + variableSpecs.getVariableNames()[i]*/, fileDataContainerManager);
								fileDataContainerManager.append(exportOutput1.getFileDataContainerID(), fileDataContainerID_header);
								fileDataContainerManager.append(exportOutput1.getFileDataContainerID(),
									getSlice(sliceHelper,mesh,allTimes,outputContext,user, dataServerImpl, vcdID, variableSpecs.getVariableNames()[j], i + beginTimeIndex, Coordinate.getNormalAxisPlaneName(geometrySpecs.getAxis()), geometrySpecs.getSliceNumber(), asciiSpecs.getSwitchRowsColumns(),fileDataContainerManager));
								output[j * TIME_COUNT + i] = exportOutput1;
//								if(sliceHelper.hdf5GroupVarID != -1) {
//									if(sliceHelper.isMembrane) {
//										
//									}else {
//										//Select next section of destination to copy-to
//										H5.H5Sselect_hyperslab(sliceHelper.hdf5DataspaceIDValues, HDF5Constants.H5S_SELECT_SET, new long[] {0,0,i}, null, new long[] {sliceHelper.sizeXYZ[sliceHelper.outerSizeIndex],sliceHelper.sizeXYZ[sliceHelper.innerSizeIndex],1},null);
//										//Copy from extracted sliceData to hdf5 file dataset
//										H5.H5Dwrite_double(sliceHelper.hdf5DatasetIDValues, HDF5Constants.H5T_NATIVE_DOUBLE, sliceHelper.hdf5DataspaceIDSlice, sliceHelper.hdf5DataspaceIDValues, HDF5Constants.H5P_DEFAULT, sliceHelper.sliceData);
//										H5.H5Sselect_none(sliceHelper.hdf5DataspaceIDValues);
//									}
//								}
								progressCounter++;
								exportServiceImpl.fireExportProgress(jobID, orig_vcdID, "CSV", progressCounter/TOTAL_EXPORTS_OPS);
							}
							sliceHelper.closeHDF5GroupAndValues();
						}
						if(hdf5GroupID != -1) {
							H5.H5Sclose(sliceHelper.hdf5DataspaceIDSlice);
						}
						exportOutputV.add(output);
						break;
					}
					default: {
						throw new DataAccessException("Unexpected geometry modeID");
					}
				}
				if(hdf5GroupID != -1) {
					H5.H5Gclose(hdf5GroupID);
				}
			}
		}
		if(hdf5FileID != -1) {
			H5.H5Fclose(hdf5FileID);
		}
		}catch(HDF5Exception e) {
			e.printStackTrace();
			throw new DataAccessException("HDF5 error:"+e.getMessage(),e);
		}
	}
	
	if(asciiSpecs.isHDF5()) {
		VCDataIdentifier vcdID = simNameSimDataIDs[0].getVCDataIdentifier(0);
		final ExportOutput exportOutput = new ExportOutput(true, ".hdf5", vcdID.getID(), ""+jobID, fileDataContainerManager);
		fileDataContainerManager.append(exportOutput.getFileDataContainerID(), hdf5TempFile.getAbsolutePath());
		return Arrays.asList(new ExportOutput[] {exportOutput});
	}
	
	if(exportOutputV.size() == 1){//geometry_slice
		return Arrays.asList(exportOutputV.elementAt(0) );
	}
	
	//geometry_selections (all are same length as first element)
	ArrayList<ExportOutput> combinedExportOutput = new ArrayList<>();
	for (int i = 0; i < exportOutputV.elementAt(0).length; i++) {
		String DATATYPE = exportOutputV.elementAt(0)[i].getDataType();
		String DATAID = exportOutputV.elementAt(0)[i].getDataID();
		ExportOutput eo = new ExportOutput(true, DATATYPE, "MultiSimulation", DATAID, fileDataContainerManager);
		//FileDataContainer container = fileDataContainerManager.getFileDataContainer(combinedExportOutput[i].getFileDataContainerID());
		for (int j = 0; j < exportOutputV.size(); j++) {
			fileDataContainerManager.append(eo.getFileDataContainerID(),exportOutputV.elementAt(j)[i].getFileDataContainerID()); 
			fileDataContainerManager.append(eo.getFileDataContainerID(),"\n");
		}
		combinedExportOutput.add(eo);
	}
	return combinedExportOutput;

}

/**
 * This method was created in VisualAge.
 * @return java.lang.String
 * @throws IOException 
 */
private FileDataContainerID getCurveTimeSeries(int hdf5GroupVarID,PointsCurvesSlices pointsCurvesSlices,OutputContext outputContext,User user, DataServerImpl dataServerImpl, VCDataIdentifier vcdID, String variableName, SpatialSelection curve, double[] allTimes, int beginIndex, int endIndex, boolean switchRowsColumns,FileDataContainerManager fileDataContainerManager) throws DataAccessException, IOException {
	int[] pointIndexes = null;
	double[] distances = null;
	int[] crossingMembraneIndexes = null;
	
	if (curve instanceof SpatialSelectionVolume){
		SpatialSelection.SSHelper ssh = ((SpatialSelectionVolume)curve).getIndexSamples(0.0,1.0);
		pointIndexes = ssh.getSampledIndexes();
		distances = ssh.getWorldCoordinateLengths();
		crossingMembraneIndexes = ssh.getMembraneIndexesInOut();
	}else if(curve instanceof SpatialSelectionMembrane){
		SpatialSelection.SSHelper ssh = ((SpatialSelectionMembrane)curve).getIndexSamples();
		if(((SpatialSelectionMembrane)curve).getSelectionSource() instanceof SinglePoint){
			pointIndexes = new int[] {ssh.getSampledIndexes()[0]};
			distances = new double[] {0};
		}else{
			pointIndexes = ssh.getSampledIndexes();
			distances = ssh.getWorldCoordinateLengths();
		}
	}

	pointsCurvesSlices.data.put(PCS.TIMES, new ArrayList<Double>());
	pointsCurvesSlices.data.put(PCS.TIMEBOUNDS, new int[] {beginIndex,endIndex});
	final TreeMap<PCS, Object> treePCS = new TreeMap<PCS,Object>();
	((TreeMap<String,TreeMap<PCS,Object>>)pointsCurvesSlices.data.get(PCS.CURVES)).put(getSpatialSelectionDescription(curve), treePCS);
	treePCS.put(PCS.CURVEINDEXES, pointIndexes);
	treePCS.put(PCS.CURVEDISTANCES, distances);
	if(crossingMembraneIndexes != null) {treePCS.put(PCS.CURVECROSSMEMBRINDEX, crossingMembraneIndexes);}
	treePCS.put(PCS.CURVEVALS, new ArrayList<Double>());
	
	org.vcell.util.document.TimeSeriesJobSpec timeSeriesJobSpec =
		new org.vcell.util.document.TimeSeriesJobSpec(
				new String[]{variableName},new int[][]{pointIndexes},new int[][]{crossingMembraneIndexes},allTimes[beginIndex],1,allTimes[endIndex],
				VCDataJobID.createVCDataJobID(user, false));
	org.vcell.util.document.TSJobResultsNoStats timeSeriesJobResults = (org.vcell.util.document.TSJobResultsNoStats)dataServerImpl.getTimeSeriesValues(outputContext,user, vcdID, timeSeriesJobSpec);

	// variableValues[0] is time array
	// variableValues[1] is values for 1st spatial point.
	// variableValues[2] is values for 2nd spatial point.
	// variableValues[n] (n>=1) is values for nth spatial point.
	// the length of variableValues should always be 1 + pointIndexes.length
	// the length of variableValues[n] is allTimes.length
	final double[][] variableValues = timeSeriesJobResults.getTimesAndValuesForVariable(variableName);
	
	//
	// put data in csv format
	//
	FileDataContainerID fileDataContainerID = fileDataContainerManager.getNewFileDataContainerID();
	
	fileDataContainerManager.append(fileDataContainerID,"\"variable ('" + variableName + "') times (" + allTimes[beginIndex] + " " + allTimes[endIndex] + ") "+getSpatialSelectionDescription(curve)+"\"\n");
	if (switchRowsColumns) {
		fileDataContainerManager.append(fileDataContainerID,",Distances\n");
		fileDataContainerManager.append(fileDataContainerID,"Times,");
		for (int i = beginIndex;i <= endIndex;i ++) {
			fileDataContainerManager.append(fileDataContainerID,"," + allTimes[i]);
			((ArrayList<Double>)pointsCurvesSlices.data.get(PCS.TIMES)).add(allTimes[i]);
		}
		fileDataContainerManager.append(fileDataContainerID,"\n");
		for (int j = 0;j < distances.length;j ++) {
			fileDataContainerManager.append(fileDataContainerID,"," + distances[j]);
			for (int i = beginIndex;i <= endIndex; i ++) {
				fileDataContainerManager.append(fileDataContainerID,"," + variableValues[j + 1][i - beginIndex]);
				((ArrayList<Double>)treePCS.get(PCS.CURVEVALS)).add(variableValues[j + 1][i - beginIndex]);
			}
			fileDataContainerManager.append(fileDataContainerID,"\n");
		}
	} else {
		fileDataContainerManager.append(fileDataContainerID,",Times\n");
		fileDataContainerManager.append(fileDataContainerID,"Distances,");
		for (int i = 0;i < distances.length;i ++) {
			fileDataContainerManager.append(fileDataContainerID,"," + distances[i]);
		}
		fileDataContainerManager.append(fileDataContainerID,"\n");
		for (int i = beginIndex;i <= endIndex;i ++) {
			fileDataContainerManager.append(fileDataContainerID,"," + allTimes[i]);
			((ArrayList<Double>)pointsCurvesSlices.data.get(PCS.TIMES)).add(allTimes[i]);
			for (int j = 0;j < distances.length;j ++) {
				fileDataContainerManager.append(fileDataContainerID,"," + variableValues[j + 1][i - beginIndex]);
				((ArrayList<Double>)treePCS.get(PCS.CURVEVALS)).add(variableValues[j + 1][i - beginIndex]);
			}
			fileDataContainerManager.append(fileDataContainerID,"\n");
		}
	}
	if(curve instanceof SpatialSelectionMembrane){
		fileDataContainerManager.append(fileDataContainerID,"\n");
		fileDataContainerManager.append(fileDataContainerID,"\"Centroid(XYZ):Times:Values[Times,Centroid]\",X,Y,Z,distance");
		for (int i = 0; i < variableValues[0].length; i++) {
			fileDataContainerManager.append(fileDataContainerID,","+variableValues[0][i]);
			((ArrayList<Double>)pointsCurvesSlices.data.get(PCS.TIMES)).add(variableValues[0][i]);
		}
		fileDataContainerManager.append(fileDataContainerID,"\n");
		double distance = 0;
		for (int i = 0; i < pointIndexes.length; i++) {
			if(pointIndexes.length > 1 && (i == (pointIndexes.length-1)) && pointIndexes[i] == pointIndexes[i-1]) {
				continue;
			}
			Coordinate coord = curve.getMesh().getCoordinateFromMembraneIndex(pointIndexes[i]);
	//		double value = variableValues[i+1][0];
			fileDataContainerManager.append(fileDataContainerID,"," + coord.getX()+","+coord.getY()+","+coord.getZ());
			if(i>0) {
				Coordinate prevCoord = curve.getMesh().getCoordinateFromMembraneIndex(pointIndexes[i-1]);
				distance+= coord.distanceTo(prevCoord);
			}
			fileDataContainerManager.append(fileDataContainerID,"," + distance);
			for (int t = 0; t < variableValues[t].length; t++) {
				fileDataContainerManager.append(fileDataContainerID,","+variableValues[i+1][t]);
				((ArrayList<Double>)treePCS.get(PCS.CURVEVALS)).add(variableValues[i+1][t]);
			}
	
			fileDataContainerManager.append(fileDataContainerID,"\n");
		}
	}
	
	if(hdf5GroupVarID != -1) {		
		try {
			int hdf5GroupCurveID = H5.H5Gcreate(hdf5GroupVarID, getSpatialSelectionDescription(curve),HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
			Hdf5Utils.writeHDF5Dataset(hdf5GroupCurveID, PCS.CURVEINDEXES.name(), new long[] {((int[])treePCS.get(PCS.CURVEINDEXES)).length}, (int[])treePCS.get(PCS.CURVEINDEXES),false);
			Hdf5Utils.writeHDF5Dataset(hdf5GroupCurveID, PCS.CURVEDISTANCES.name(), new long[] {((double[])treePCS.get(PCS.CURVEDISTANCES)).length}, (double[])treePCS.get(PCS.CURVEDISTANCES),false);
			if(treePCS.get(PCS.CURVECROSSMEMBRINDEX) != null) {
				Hdf5Utils.writeHDF5Dataset(hdf5GroupCurveID, PCS.CURVECROSSMEMBRINDEX.name(), new long[] {((int[])treePCS.get(PCS.CURVECROSSMEMBRINDEX)).length}, (int[])treePCS.get(PCS.CURVECROSSMEMBRINDEX),false);
				ArrayList<Integer> crossPoints = new ArrayList<Integer>();
				for(int i=0;i<crossingMembraneIndexes.length;i++) {
					if(crossingMembraneIndexes[i] != -1) {
						crossPoints.add(i);
					}
				}
				String attrText = PCS.CURVEVALS.name()+" columns "+crossPoints.get(0)+" and "+crossPoints.get(1)+" are added points of interpolation near membrane";
				Hdf5Utils.writeHDF5Dataset(hdf5GroupCurveID, PCS.CURVECROSSMEMBRINDEX.name()+" Info", null, attrText,true);
			}
			Hdf5Utils.writeHDF5Dataset(hdf5GroupCurveID, PCS.CURVEVALS.name(), new long[] {endIndex-beginIndex+1,((int[])treePCS.get(PCS.CURVEINDEXES)).length}, (ArrayList<Double>)treePCS.get(PCS.CURVEVALS),false);
			H5.H5Gclose(hdf5GroupCurveID);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataAccessException(e.getMessage(),e);
		}

	}
	return fileDataContainerID;   
}

private String getSpatialSelectionName(SpatialSelection spatialSelection){
	if(spatialSelection instanceof SpatialSelectionMembrane){
		return ((SpatialSelectionMembrane)spatialSelection).getSelectionSource().getDescription();
	}else if(spatialSelection instanceof SpatialSelectionVolume){
		return spatialSelection.getCurveSelectionInfo().getCurve().getDescription();
	}
	return null;
}
private String getSpatialSelectionCoordinate(SpatialSelection spatialSelection){
	if(spatialSelection instanceof SpatialSelectionMembrane){
		return ((SpatialSelectionMembrane)spatialSelection).getSelectionSource().getBeginningCoordinate().toString();
	}else if(spatialSelection instanceof SpatialSelectionVolume){
		return spatialSelection.getCurveSelectionInfo().getCurve().getBeginningCoordinate().toString();
	}
	return null;
}
private String getSpatialSelectionDescription(SpatialSelection spatialSelection){
	return "(roi=('" + getSpatialSelectionName(spatialSelection) + "') coord=("+getSpatialSelectionCoordinate(spatialSelection)+"))";
}
/**
 * Insert the method's description here.
 * Creation date: (1/17/00 6:02:37 PM)
 * @return java.lang.String
 * @param odeSimData cbit.vcell.simdata.ODESimData
 * @param variableNames java.lang.String[]
 * @param beginIndex int
 * @param endIndex int
 * @param switchRowsColumns boolean
 * @exception org.vcell.util.DataAccessException The exception description.
 * @throws IOException 
 */
private FileDataContainerID getODEDataValues(long jobID, User user, DataServerImpl dataServerImpl, VCDataIdentifier vcdID, String[] variableNames, int beginIndex, int endIndex, boolean switchRowsColumns,FileDataContainerManager fileDataContainerManager) throws DataAccessException, IOException {

	ODESimData odeSimData = dataServerImpl.getODEData(user, vcdID);
	double progress = 0.0;
	boolean isTimeSeries = (odeSimData.findColumn("t") > -1)? true:false;
	boolean isMultiTrial = (odeSimData.findColumn("TrialNo") > -1)? true:false;
	// get arrays
	double[] allTimes = null;
	try {
		if(isTimeSeries)
			allTimes = odeSimData.extractColumn(odeSimData.findColumn("t"));
		else if(isMultiTrial)
			allTimes = odeSimData.extractColumn(odeSimData.findColumn("TrialNo"));
	}catch (cbit.vcell.parser.ExpressionException e){
		e.printStackTrace(System.out);
	}
	double[][] variableValues = new double[variableNames.length][endIndex - beginIndex + 1];
	for (int k=0;k<variableNames.length;k++) {
		for (int i=beginIndex;i<=endIndex;i++) {
			progress = (double)(i + k * (endIndex - beginIndex + 1)) / (2 * variableNames.length * (endIndex - beginIndex + 1));
			exportServiceImpl.fireExportProgress(jobID, vcdID, "CSV", progress);
			try {
				variableValues[k][i - beginIndex] = odeSimData.extractColumn(odeSimData.findColumn(variableNames[k]))[i];
			}catch (cbit.vcell.parser.ExpressionException e){
				e.printStackTrace(System.out);
				throw new DataAccessException("error evaluating function in dataset: "+e.getMessage());
			}
		}
	}
	// put data in csv format
	FileDataContainerID fileDataContainerID = fileDataContainerManager.getNewFileDataContainerID();
 
	if(isTimeSeries)
	{
		fileDataContainerManager.append(fileDataContainerID,
		    "Variable values over the time range " + allTimes[beginIndex] + " to " + allTimes[endIndex] + "\n\n");
	}
	else if(isMultiTrial)
	{
		fileDataContainerManager.append(fileDataContainerID,
		    "Variable values over the Trials " + allTimes[beginIndex] + " to " + allTimes[endIndex] + "\n\n");
	}
	if (switchRowsColumns) {
		fileDataContainerManager.append(fileDataContainerID,",Variable\n");
		if(isTimeSeries) fileDataContainerManager.append(fileDataContainerID,"Time,");
		else if(isMultiTrial) fileDataContainerManager.append(fileDataContainerID,"Trial No,");
		for (int i=beginIndex;i<=endIndex;i++) {
			fileDataContainerManager.append(fileDataContainerID,"," + allTimes[i]);
		}
		fileDataContainerManager.append(fileDataContainerID,"\n");	
		for (int k=0;k<variableNames.length;k++) {
			fileDataContainerManager.append(fileDataContainerID,"," + variableNames[k]);
			for (int i=beginIndex;i<=endIndex;i++) {
				progress = 0.5 + (double)(i + k * (endIndex - beginIndex + 1)) / (2 * variableNames.length * (endIndex - beginIndex + 1));
				exportServiceImpl.fireExportProgress(jobID, vcdID, "CSV", progress);
				fileDataContainerManager.append(fileDataContainerID,"," + variableValues[k][i - beginIndex]);
			}
			fileDataContainerManager.append(fileDataContainerID,"\n");
		}
	} else {
		if(isTimeSeries) fileDataContainerManager.append(fileDataContainerID,",Time\n");
		else if(isMultiTrial) fileDataContainerManager.append(fileDataContainerID,",Trial No\n");		
		fileDataContainerManager.append(fileDataContainerID,"Variable,");
		for (int k=0;k<variableNames.length;k++) {
			fileDataContainerManager.append(fileDataContainerID,"," + variableNames[k]);
		}
		fileDataContainerManager.append(fileDataContainerID,"\n");	
		for (int i=beginIndex;i<=endIndex;i++) {
			fileDataContainerManager.append(fileDataContainerID,"," + allTimes[i]);
			for (int k=0;k<variableNames.length;k++) {
				progress = 0.5 + (double)(i + k * (endIndex - beginIndex + 1)) / (2 * variableNames.length * (endIndex - beginIndex + 1));
				exportServiceImpl.fireExportProgress(jobID, vcdID, "CSV", progress);
				fileDataContainerManager.append(fileDataContainerID,"," + variableValues[k][i - beginIndex]);
			}
			fileDataContainerManager.append(fileDataContainerID,"\n");
		}
	}
	return fileDataContainerID;
}


/**
 * This method was created in VisualAge.
 * @return java.lang.String
 * @throws IOException 
 */
private FileDataContainerID getPointsTimeSeries(PointsCurvesSlices pcs,int hdf5GroupID, OutputContext outputContext,User user,
		DataServerImpl dataServerImpl, VCDataIdentifier vcdID, String variableName, GeometrySpecs geometrySpecs,
		double[] allTimes, int beginIndex, int endIndex, boolean switchRowsColumns,FileDataContainerManager fileDataContainerManager) throws DataAccessException, IOException, HDF5Exception {
	
	org.vcell.util.document.TimeSeriesJobSpec timeSeriesJobSpec =
		new org.vcell.util.document.TimeSeriesJobSpec(
				new String[]{variableName},new int[][]{geometrySpecs.getPointIndexes()},null,allTimes[beginIndex],1,allTimes[endIndex],
				VCDataJobID.createVCDataJobID(user, false));
	org.vcell.util.document.TSJobResultsNoStats timeSeriesJobResults = (org.vcell.util.document.TSJobResultsNoStats)dataServerImpl.getTimeSeriesValues(outputContext,user, vcdID, timeSeriesJobSpec);

	// variableValues[0] is time array
	// variableValues[1] is values for 1st spatial point.
	// variableValues[2] is values for 2nd spatial point.
	// variableValues[n] (n>=1) is values for nth spatial point.
	// the length of variableValues should always be 1 + pointIndexes.length
	// the length of variableValues[n] is allTimes.length
	final double[][] variableValues = timeSeriesJobResults.getTimesAndValuesForVariable(variableName);

	pcs.data.put(PCS.POINTINFO, new ArrayList<String>());
	pcs.data.put(PCS.POINTVALS, new ArrayList<Double>());
	pcs.data.put(PCS.TIMES, new ArrayList<Double>());
	pcs.data.put(PCS.TIMEBOUNDS, new int[] {beginIndex,endIndex});

	//
	// put data in csv format
	//
	SpatialSelection[] pointSpatialSelections = geometrySpecs.getPointSpatialSelections();
	FileDataContainerID fileDataContainerID = fileDataContainerManager.getNewFileDataContainerID();
//	FileDataContainer container = fileDataContainerManager.getFileDataContainer(fileDataContainerID);
//	buffer.append(
//		"\"Time Series for variable '" + variableName + "'\" over the range " + allTimes[beginIndex] + " to " + allTimes[endIndex] + "\n");
	fileDataContainerManager.append(fileDataContainerID,"\"variable ('" + variableName + "') times (" + allTimes[beginIndex] + " " + allTimes[endIndex] + ") "/*+getSpatialSelectionDescription(curve)*/+"\"\n");
	if (switchRowsColumns) {
		fileDataContainerManager.append(fileDataContainerID,",Coordinates\n");
		fileDataContainerManager.append(fileDataContainerID,"Time,");
		for (int i=beginIndex;i<=endIndex;i++) {
			fileDataContainerManager.append(fileDataContainerID,"," + allTimes[i]);
			((ArrayList<Double>)pcs.data.get(PCS.TIMES)).add(allTimes[i]);
		}
		fileDataContainerManager.append(fileDataContainerID,"\n");	
		for (int k=0;k<pointSpatialSelections.length;k++) {
			final String spatialSelectionDescription = getSpatialSelectionDescription(pointSpatialSelections[k]);
			fileDataContainerManager.append(fileDataContainerID,"," + spatialSelectionDescription);
			((ArrayList<String>)pcs.data.get(PCS.POINTINFO)).add(spatialSelectionDescription);
			for (int i=beginIndex;i<=endIndex;i++) {
				fileDataContainerManager.append(fileDataContainerID,"," + variableValues[k+1][i - beginIndex]);
				((ArrayList<Double>)pcs.data.get(PCS.POINTVALS)).add(variableValues[k+1][i - beginIndex]);
			}
			fileDataContainerManager.append(fileDataContainerID,"\n");
		}
	} else {
		double[] hdfTimes = null;
		double[] hdfValues = null;
		if(hdf5GroupID != -1) {
			hdfTimes = new double[endIndex-beginIndex+1];
			hdfValues = new double[hdfTimes.length*pointSpatialSelections.length];
		}
		fileDataContainerManager.append(fileDataContainerID,",Time\n");
		fileDataContainerManager.append(fileDataContainerID,"Coordinates,");
		for (int k=0;k<pointSpatialSelections.length;k++) {
			final String spatialSelectionDescription = getSpatialSelectionDescription(pointSpatialSelections[k]);
			fileDataContainerManager.append(fileDataContainerID,"," + spatialSelectionDescription);
			((ArrayList<String>)pcs.data.get(PCS.POINTINFO)).add(spatialSelectionDescription);
		}
		fileDataContainerManager.append(fileDataContainerID,"\n");
		int c=0;
		for (int i=beginIndex;i<=endIndex;i++) {
			fileDataContainerManager.append(fileDataContainerID,"," + allTimes[i]);
			((ArrayList<Double>)pcs.data.get(PCS.TIMES)).add(allTimes[i]);
			hdfTimes[i-beginIndex] = allTimes[i];
			for (int k=0;k<pointSpatialSelections.length;k++) {
				fileDataContainerManager.append(fileDataContainerID,"," + variableValues[k+1][i - beginIndex]);
				((ArrayList<Double>)pcs.data.get(PCS.POINTVALS)).add(variableValues[k+1][i - beginIndex]);
				hdfValues[c] = variableValues[k+1][i - beginIndex];
				c++;
			}
			fileDataContainerManager.append(fileDataContainerID,"\n");
		}
		if(hdf5GroupID != -1) {			
			long[] dimsCoord = new long[] {1,pointSpatialSelections.length};
			Hdf5Utils.writeHDF5Dataset(hdf5GroupID, PCS.POINTINFO.name(), dimsCoord, pcs.data.get(PCS.POINTINFO),false);
			long[] dimsValues = new long[] {hdfTimes.length,pointSpatialSelections.length};
			Hdf5Utils.writeHDF5Dataset(hdf5GroupID, PCS.POINTVALS.name(), dimsValues, hdfValues,false);
		}
	}

	return fileDataContainerID;
}


private class SliceHelper {
	public int hdf5DataspaceIDSlice = -1;
	public int hdf5GroupVarID = -1;
	int hdf5DataspaceIDValues = -1;
	int hdf5DatasetIDValues = -1;

	public final String[] SLICE_PLANE_AXIS = {"X","Y","Z"};
	private final int[][][] loopIndexes = new int[][][] {{{1,2},{2,1}},{{0,2},{2,0}},{{0,1},{1,0}}};
	public String slicePlaneText;
	public String strideText;
	private int slicePlaneIndex;
	private int sliceNumber;
	public int[] sizeXYZ;
//	public double[] sliceData;
	boolean switchRowsColumns;
	public int outerSizeIndex;
	public int innerSizeIndex;
	public boolean isHDF5 = false;
	private Boolean isMembrane = null;
	private CartesianMesh mesh;
	private int timeCount;
	private int currentTimeIndex = 0;
	public SliceHelper (int timeCount,boolean isHDF5,String slicePlane,int sliceNumber,boolean switchRowsColumns,CartesianMesh mesh) {
		this.timeCount = timeCount;
		this.isHDF5 = isHDF5;
		this.sliceNumber = sliceNumber;
		this.switchRowsColumns = switchRowsColumns;
		this.sizeXYZ = new int[] {mesh.getSizeX(), mesh.getSizeY(), mesh.getSizeZ()};
		this.slicePlaneIndex = Arrays.asList(Coordinate.PLANENAMES).indexOf(slicePlane);
		if(slicePlaneIndex == -1) {
			throw new IllegalArgumentException("Unknown slicePlane '"+slicePlane+"'");
		}
		this.mesh = mesh;
		this.slicePlaneText = " in plane "+slicePlane+" at "+SLICE_PLANE_AXIS[slicePlaneIndex]+" = "+sliceNumber;
		this.strideText = slicePlane.substring(0, 1)+" in rows, "+slicePlane.substring(1,2)+" in columns";
		outerSizeIndex = loopIndexes[slicePlaneIndex][(switchRowsColumns?0:1)][0];
		innerSizeIndex = loopIndexes[slicePlaneIndex][(switchRowsColumns?0:1)][1];
	}
	public void setHDF5GroupVarID(int hdf5GroupID,String varName) throws HDF5LibraryException {
		if(isHDF5) {
			hdf5GroupVarID = H5.H5Gcreate(hdf5GroupID, varName,HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
			currentTimeIndex = 0;
			isMembrane = null;
			hdf5DataspaceIDSlice = -1;
		}
	}
	public void closeHDF5GroupAndValues() throws HDF5LibraryException {
		if(hdf5GroupVarID != -1) {
			H5.H5Dclose(hdf5DatasetIDValues);
			H5.H5Sclose(hdf5DataspaceIDValues);
			H5.H5Gclose(hdf5GroupVarID);
			hdf5DatasetIDValues = -1;
			hdf5DataspaceIDValues = -1;
			hdf5GroupVarID = -1;
		}
	}
	public void populate(FileDataContainerManager fileDataContainerManager,FileDataContainerID fileDataContainerID,double[] origData) throws IOException, HDF5Exception{
		if(isMembrane != null && SimulationData.getVariableTypeFromLength(mesh, origData.length).equals(VariableType.MEMBRANE) != isMembrane.booleanValue()) {
			throw new IllegalArgumentException("'isMembrane' conflict");
		}
		isMembrane = SimulationData.getVariableTypeFromLength(mesh, origData.length).equals(VariableType.MEMBRANE);

		if(isMembrane) {
			for	(int i = 0; i < origData.length; i ++) {
				fileDataContainerManager.append(fileDataContainerID,origData[i] + "\n");
			}
			if(isHDF5) {
				if(hdf5DataspaceIDSlice == -1) {							
					long[] dimsValues2 = new long[] {origData.length};
					hdf5DataspaceIDSlice = H5.H5Screate_simple(dimsValues2.length, dimsValues2, null);
					//Select the generated sliceData to copy-from
					H5.H5Sselect_hyperslab(hdf5DataspaceIDSlice, HDF5Constants.H5S_SELECT_SET, new long[] {0}, null, dimsValues2, null);
				}
				if(hdf5DataspaceIDValues == -1) {
					//Create dataset
					long[] dimsValues = new long[] {origData.length,timeCount};
					hdf5DataspaceIDValues = H5.H5Screate_simple(dimsValues.length, dimsValues, null);
					hdf5DatasetIDValues = H5.H5Dcreate(hdf5GroupVarID, "DataValues (M"+"T)",HDF5Constants.H5T_NATIVE_DOUBLE, hdf5DataspaceIDValues,HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
				}
				//Select next section of destination to copy-to
				H5.H5Sselect_hyperslab(hdf5DataspaceIDValues, HDF5Constants.H5S_SELECT_SET, new long[] {0,currentTimeIndex}, null, new long[] {origData.length,1},null);
				//Copy from extracted sliceData to hdf5 file dataset
				H5.H5Dwrite_double(hdf5DatasetIDValues, HDF5Constants.H5T_NATIVE_DOUBLE, hdf5DataspaceIDSlice, hdf5DataspaceIDValues, HDF5Constants.H5P_DEFAULT, origData);
				H5.H5Sselect_none(hdf5DataspaceIDValues);
				currentTimeIndex++;
			}
		}else {
			int start = -1;
			switch (slicePlaneIndex) {
				case Coordinate.Z_AXIS:
					start = sliceNumber*sizeXYZ[0]*sizeXYZ[1];
				break;
				case Coordinate.Y_AXIS:
					start = sliceNumber*sizeXYZ[0];
				break;
				case Coordinate.X_AXIS:
					start = sliceNumber;
				break;
			}
			fileDataContainerManager.append(fileDataContainerID,slicePlaneText+"\n\n");
			fileDataContainerManager.append(fileDataContainerID,strideText+"\n");
			double[] sliceData = new double[sizeXYZ[outerSizeIndex]*sizeXYZ[innerSizeIndex]];
			int cnt=0;
			for (int outer=0;outer<sizeXYZ[outerSizeIndex];outer++) {
				for (int inner=0;inner<sizeXYZ[innerSizeIndex];inner++) {
					switch (slicePlaneIndex) {
						case Coordinate.Z_AXIS:
							sliceData[cnt] = (switchRowsColumns?origData[start + inner*sizeXYZ[0] + outer]:origData[start + outer*sizeXYZ[0] + inner]);
						break;
						case Coordinate.Y_AXIS:
							sliceData[cnt] = (switchRowsColumns?origData[start +inner*sizeXYZ[0]*sizeXYZ[1] + outer]:origData[start +outer*sizeXYZ[0]*sizeXYZ[1] + inner]);
						break;
						case Coordinate.X_AXIS:
							sliceData[cnt] = (switchRowsColumns?origData[start +inner*sizeXYZ[0]*sizeXYZ[1] + outer*sizeXYZ[0]]:origData[start +outer*sizeXYZ[0]*sizeXYZ[1] + inner*sizeXYZ[0]]);
						break;
					}
					fileDataContainerManager.append(fileDataContainerID,sliceData[cnt] + ",");
					cnt++;
				}
				fileDataContainerManager.append(fileDataContainerID,"\n");
			}
			if(isHDF5) {
				if(hdf5DataspaceIDSlice == -1) {							
					long[] dimsValues2 = new long[] {sizeXYZ[outerSizeIndex],sizeXYZ[innerSizeIndex]};
					hdf5DataspaceIDSlice = H5.H5Screate_simple(dimsValues2.length, dimsValues2, null);
					//Select the generated sliceData to copy-from
					H5.H5Sselect_hyperslab(hdf5DataspaceIDSlice, HDF5Constants.H5S_SELECT_SET, new long[] {0,0}, null, dimsValues2, null);
				}
				if(hdf5DataspaceIDValues == -1) {
					//Create dataset
					long[] dimsValues = new long[] {sizeXYZ[outerSizeIndex],sizeXYZ[innerSizeIndex],timeCount};
					hdf5DataspaceIDValues = H5.H5Screate_simple(dimsValues.length, dimsValues, null);
					hdf5DatasetIDValues = H5.H5Dcreate(hdf5GroupVarID, "DataValues ("+SLICE_PLANE_AXIS[innerSizeIndex]+SLICE_PLANE_AXIS[outerSizeIndex]+"T)",HDF5Constants.H5T_NATIVE_DOUBLE, hdf5DataspaceIDValues,HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
				}
				//Select next section of destination to copy-to
				H5.H5Sselect_hyperslab(hdf5DataspaceIDValues, HDF5Constants.H5S_SELECT_SET, new long[] {0,0,currentTimeIndex}, null, new long[] {sizeXYZ[outerSizeIndex],sizeXYZ[innerSizeIndex],1},null);
				//Copy from extracted sliceData to hdf5 file dataset
				H5.H5Dwrite_double(hdf5DatasetIDValues, HDF5Constants.H5T_NATIVE_DOUBLE, hdf5DataspaceIDSlice, hdf5DataspaceIDValues, HDF5Constants.H5P_DEFAULT, sliceData);
				H5.H5Sselect_none(hdf5DataspaceIDValues);
				currentTimeIndex++;
			}

		}
	}
}
/**
 * This method was created in VisualAge.
 * @return java.io.File
 * @param variable java.lang.String
 * @param time double
 * @throws IOException 

 */
private FileDataContainerID getSlice(SliceHelper sliceHelper, CartesianMesh mesh,double[] allTimes,OutputContext outputContext,User user, DataServerImpl dataServerImpl, VCDataIdentifier vcdID, String variable, int timeIndex, String slicePlane, int sliceNumber, boolean switchRowsColumns,FileDataContainerManager fileDataContainerManager) throws HDF5Exception, DataAccessException, IOException {
	
	double timepoint = allTimes[timeIndex];
	SimDataBlock simDataBlock = dataServerImpl.getSimDataBlock(outputContext,user, vcdID,variable,timepoint);
	double[] data = simDataBlock.getData();
	
	FileDataContainerID fileDataContainerID = fileDataContainerManager.getNewFileDataContainerID();

	if (simDataBlock.getVariableType().equals(VariableType.VOLUME) || simDataBlock.getVariableType().equals(VariableType.POSTPROCESSING)) {
		//
		// put data in csv format
		//
		fileDataContainerManager.append(fileDataContainerID,"2D Slice for variable "+variable+" at time "+timepoint);
	} else {
		// membrane variable; we export the data by index
		// for 3D one gets the whole dataset for now... warning at the client level... will get more sophisticated later...
		fileDataContainerManager.append(fileDataContainerID,"Data for membrane variable "+variable+" at time "+timepoint+"\nEntire datablock by index\n\n");
	}
	sliceHelper.populate(fileDataContainerManager, fileDataContainerID,data);


	return fileDataContainerID;
}


/**
 * This method was created in VisualAge.
 * @throws IOException 
 */
public Collection<ExportOutput >makeASCIIData(OutputContext outputContext,JobRequest jobRequest, User user, DataServerImpl dataServerImpl, ExportSpecs exportSpecs,FileDataContainerManager fileDataContainerManager) 
		throws DataAccessException, IOException {
	FormatSpecificSpecs formatSpecs = exportSpecs.getFormatSpecificSpecs( );
	ASCIISpecs asciiSpecs = BeanUtils.downcast(ASCIISpecs.class, formatSpecs);
	if (asciiSpecs != null) {
		switch (asciiSpecs.getDataType()) {
		case PDE_VARIABLE_DATA:
			return exportPDEData(
					outputContext,
					jobRequest.getJobID(),
					user,
					dataServerImpl,
					exportSpecs.getVCDataIdentifier(),
					exportSpecs.getVariableSpecs(),
					exportSpecs.getTimeSpecs(),
					exportSpecs.getGeometrySpecs(),
					asciiSpecs,
					exportSpecs.getContextName(),
					fileDataContainerManager
					);
		case ODE_VARIABLE_DATA:
			return exportODEData(
					outputContext,
					jobRequest.getJobID(),
					user,
					dataServerImpl,
					exportSpecs.getVCDataIdentifier(),
					exportSpecs.getVariableSpecs(),
					exportSpecs.getTimeSpecs(),
					asciiSpecs,
					fileDataContainerManager
					);
		case PDE_PARTICLE_DATA:
			return exportParticleData(
					outputContext,
					jobRequest.getJobID(),
					user,
					dataServerImpl,
					exportSpecs,
					asciiSpecs,
					fileDataContainerManager
					);
		default:
			return Arrays.asList(new ExportOutput(false, null, null, null, fileDataContainerManager));
		}
	}
	throw new IllegalArgumentException("Export spec "  + ExecutionTrace.justClassName(formatSpecs) + " not instance of " + ExecutionTrace.justClassName(ASCIISpecs.class) );
}

/**
 * manage sending progress percent info for {@link ASCIIExporter#exportParticleData(OutputContext, long, User, DataServerImpl, ExportSpecs, ASCIISpecs, FileDataContainerManager)
 *
 */
private class ParticleProgress {				
	final static int PROGRESS_PERCENT_INCREMENT = 5; //%
	final static int TIMES_PERCENT = 80; //a total guess
	final static int LINES_PERCENT = 100 - TIMES_PERCENT; 
	final static String CSV_LABEL = "CSV";
	
	final long jobID;
	final VCDataIdentifier vcdID;
	//final int nNames;
	//final int nTimes;
	//final int nDataLines;
	final double nameIncr; //relative to 1.0
	final double timeIncr;
	final double dataIncr;
	final double stepIncr;
	
	int nameCounter;
	double nextIncr;
	boolean dataCalled;
	 
	
	ParticleProgress(long jobID, VCDataIdentifier vcdID, int nNames, int nTimes, int nDataLines) {
		super();
		this.jobID = jobID;
		this.vcdID = vcdID;
		//this.nNames = nNames;
		//this.nTimes = nTimes;
		//this.nDataLines = nDataLines;
		
		nameIncr = 1.0 / nNames;
		timeIncr = nameIncr * (TIMES_PERCENT / 100.0) / nTimes;
		dataIncr = nameIncr * (LINES_PERCENT / 100.0) / nDataLines;
		
		nameCounter = 0;
		nextIncr = stepIncr = PROGRESS_PERCENT_INCREMENT / 100.0;
		dataCalled = false;
		fire(0.00001);  //fire one just to get started
	}
	
	private void fire(double increment) {
		exportServiceImpl.fireExportProgress(jobID, vcdID, CSV_LABEL,increment);  
		while (increment >= nextIncr) {
			nextIncr += stepIncr;
		}
	}
	
	/**
	 * indicate have moved to next name
	 */
	void nextName( ) {
		dataCalled = false;
		double increment = ++nameCounter * nameIncr; 
		if (increment > nextIncr) {
			fire(increment);
		}
	}
	
	/**
	 * @param currentTimeStep time step currently on 
	 * @param fireNow send progress report if past time 
	 * @return time index to fire next notification at
	 */
	int nextTimeIndex(int currentTimeStep, boolean fireNow) {
		final double current = (nameCounter * nameIncr) + (currentTimeStep * timeIncr);
		if (fireNow  && current > nextIncr) {
			fire(current);
		}
		if (!dataCalled) {
			double distFromName = nextIncr - current; 
			VCAssert.assertTrue(distFromName >= 0, "positive step");
			int r = (int) (distFromName / timeIncr);
			return r + currentTimeStep;
		}
		throw new IllegalStateException("nextTimeIndex( ) must come before nextDataIndex( )");
	}
	
	/**
	 * indicate particle exporter is doing processing time steps
	 */
	void endOfTimes( ) {
		final double current =  (nameCounter * nameIncr)  + (TIMES_PERCENT / 100.0);
		if (current > nextIncr) {
			fire(current);
		}
	}
	
	/**
	 * @param currentDataIndex data step currently on 
	 * @param fireNow send progress report 
	 * @return data index to fire next notification at
	 */
	int nextDataIndex(int currentDataIndex, boolean fireNow) {
		dataCalled = true;
		final double current =  (nameCounter * nameIncr)  + (TIMES_PERCENT / 100.0) + (currentDataIndex * dataIncr);
		if (fireNow  && current > nextIncr) {
			fire(current);
		}
		double distFromName = nextIncr - current; 
		VCAssert.assertTrue(distFromName >= 0, "positive step");
		int r = (int) (distFromName / dataIncr);
		return r + currentDataIndex;
	}
	
	
	

	
}

}
