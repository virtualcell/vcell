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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.rmi.RemoteException;
import java.util.Vector;

import org.vcell.util.Coordinate;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDataIdentifier;
import org.vcell.util.document.VCDataJobID;

import cbit.vcell.client.data.OutputContext;
import cbit.vcell.export.server.FileDataContainerManager.FileDataContainerID;
import cbit.vcell.geometry.SinglePoint;
import cbit.vcell.math.VariableType;
import cbit.vcell.simdata.DataServerImpl;
import cbit.vcell.simdata.ParticleData;
import cbit.vcell.simdata.SimDataBlock;
import cbit.vcell.simdata.gui.SpatialSelection;
import cbit.vcell.simdata.gui.SpatialSelectionMembrane;
import cbit.vcell.simdata.gui.SpatialSelectionVolume;
import cbit.vcell.solver.ode.ODESimData;
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
private ExportOutput[] exportODEData(OutputContext outputContext,long jobID, User user, DataServerImpl dataServerImpl, VCDataIdentifier vcdID, VariableSpecs variableSpecs, TimeSpecs timeSpecs, ASCIISpecs asciiSpecs,FileDataContainerManager fileDataContainerManager) throws DataAccessException, IOException {
	String simID = vcdID.getID();
	String dataType = ".csv";
	String dataID = "_";
	//StringBuffer data = new StringBuffer();
	ExportOutput exportOutput = new ExportOutput(true, dataType, simID, dataID, fileDataContainerManager);
	SimulationDescription simulationDescription = new SimulationDescription(outputContext, user, dataServerImpl,vcdID,true);
	fileDataContainerManager.append(exportOutput.getFileDataContainerID(),simulationDescription.getHeader(dataType));
	fileDataContainerManager.append(exportOutput.getFileDataContainerID(),getODEDataValues(jobID, user, dataServerImpl, vcdID, variableSpecs.getVariableNames(), timeSpecs.getBeginTimeIndex(), timeSpecs.getEndTimeIndex(), asciiSpecs.getSwitchRowsColumns(),fileDataContainerManager));
	dataID += variableSpecs.getModeID() == 0 ? variableSpecs.getVariableNames()[0] : "ManyVars";
	return new ExportOutput[] {exportOutput};	
}
 

/**
 * Insert the method's description here.
 * Creation date: (1/12/00 5:00:28 PM)
 * @return cbit.vcell.export.server.ExportOutput[]
 * @param dsc cbit.vcell.server.DataSetController
 * @param timeSpecs cbit.vcell.export.server.TimeSpecs
 * @throws IOException 
 * @deprecated
 */
private ExportOutput[] exportParticleData(OutputContext outputContext,long jobID, User user, DataServerImpl dataServerImpl, VCDataIdentifier vcdID, TimeSpecs timeSpecs, ASCIISpecs asciiSpecs,FileDataContainerManager fileDataContainerManager) throws DataAccessException, IOException {

	String simID = vcdID.getID();
	String dataType = ".csv";
	String dataID = "_Particles";

	// get parameters
	boolean switchRowsColumns = asciiSpecs.getSwitchRowsColumns();
	double[] allTimes = timeSpecs.getAllTimes();
	int beginIndex = timeSpecs.getBeginTimeIndex();
	int endIndex = timeSpecs.getEndTimeIndex();
	ParticleData[] particleData = dataServerImpl.getParticleDataBlock(user, vcdID,allTimes[beginIndex]).getParticleData();
	int numberOfParticles = particleData.length;
	int numberOfTimes = endIndex - beginIndex + 1;

	// now make csv formatted data
	StringBuffer header = new StringBuffer();
	FileDataContainerID[] dataLines = null;
	if (switchRowsColumns) {
		dataLines = new FileDataContainerID[numberOfParticles * 5];
		for (int j=0;j<dataLines.length;j++){
			dataLines[j] = fileDataContainerManager.getNewFileDataContainerID();
		}
		for (int i=0;i<numberOfParticles;i++) {
			fileDataContainerManager.append(dataLines[5 * i],"x," + i + ",");
			fileDataContainerManager.append (dataLines[5 * i + 1],"y,,");
			fileDataContainerManager.append (dataLines[5 * i + 2],"z,,");
			fileDataContainerManager.append (dataLines[5 * i + 3],"state,,");
			fileDataContainerManager.append (dataLines[5 * i + 4],"context,,");
		}
	} else {
		dataLines = new FileDataContainerID[numberOfTimes];
		for (int j=0;j<dataLines.length;j++){
			dataLines[j] = fileDataContainerManager.getNewFileDataContainerID();
		}
	}
	SimulationDescription simulationDescription = new SimulationDescription(outputContext,user, dataServerImpl,vcdID,false);
	header.append(simulationDescription.getHeader(dataType));
	if (switchRowsColumns) {
		header.append(",Particle #\n");
		header.append("Time,,");
		for (int i=beginIndex;i<=endIndex;i++) {
			header.append(allTimes[i] + ",");
		}
	} else {
		header.append(",Time\n");
		header.append("Particle #,,");
		for (int k=0;k<numberOfParticles;k++) {
			header.append(k + ",,,,,");
		}
		header.append("\n,,");
		for (int k=0;k<numberOfParticles;k++) {
			header.append("x,y,z,state,context,");
		}
	}
	double progress = 0.0;
	for (int i=beginIndex;i<=endIndex;i++) {
		progress = (double)(i - beginIndex) / numberOfTimes;
		exportServiceImpl.fireExportProgress(jobID, vcdID, "CSV", progress);
		particleData = dataServerImpl.getParticleDataBlock(user, vcdID,allTimes[i]).getParticleData();
		if (switchRowsColumns) {
			for (int j=0;j<numberOfParticles;j++) {
				Coordinate coordinate = particleData[j].getCoordinate();
				fileDataContainerManager.append(dataLines[5 * j],coordinate.getX() + ",");
				fileDataContainerManager.append(dataLines[5 * j + 1],coordinate.getY() + ",");
				fileDataContainerManager.append(dataLines[5 * j + 2],coordinate.getZ() + ",");
				fileDataContainerManager.append(dataLines[5 * j + 3],particleData[j].getState() + ",");
				fileDataContainerManager.append(dataLines[5 * j + 4],particleData[j].getContext() + ",");
			}
		} else {
			fileDataContainerManager.append(dataLines[i - beginIndex],"," + allTimes[i] + ",");
			for (int j=0;j<numberOfParticles;j++) {
				Coordinate coordinate = particleData[j].getCoordinate();
				fileDataContainerManager.append(dataLines[i - beginIndex],coordinate.getX() + "," + coordinate.getY() + "," + coordinate.getZ() + ",");
				fileDataContainerManager.append(dataLines[i - beginIndex],particleData[j].getState() + ",");
				fileDataContainerManager.append(dataLines[i - beginIndex],particleData[j].getContext() + ",");
			}
		}
	}
	ExportOutput exportOutputCSV = new ExportOutput(true, dataType, simID, dataID, fileDataContainerManager);
	fileDataContainerManager.append(exportOutputCSV.getFileDataContainerID(),header.toString());
	for (int i=0;i<dataLines.length;i++) {
		progress = (double)i / dataLines.length;
		exportServiceImpl.fireExportProgress(jobID, vcdID, "CSV", progress);
		fileDataContainerManager.append(exportOutputCSV.getFileDataContainerID(),"\n");
		fileDataContainerManager.append(exportOutputCSV.getFileDataContainerID(),dataLines[i]);
	}
	
	return new ExportOutput[] {exportOutputCSV};    
}


/**
 * This method was created in VisualAge.
 * @throws IOException 
 */
private ExportOutput[] exportPDEData(OutputContext outputContext,long jobID, User user, DataServerImpl dataServerImpl,
		final VCDataIdentifier orig_vcdID, VariableSpecs variableSpecs, TimeSpecs timeSpecs, 
		GeometrySpecs geometrySpecs, ASCIISpecs asciiSpecs,String contextName,FileDataContainerManager fileDataContainerManager) 
						throws DataAccessException, IOException {
						
	ExportSpecs.SimNameSimDataID[] simNameSimDataIDs = asciiSpecs.getSimNameSimDataIDs();
	Vector<ExportOutput[]> exportOutputV = new Vector<ExportOutput[]>();
	double progressCounter = 0;
	final int SIM_COUNT = simNameSimDataIDs.length;
	final int PARAMSCAN_COUNT = (asciiSpecs.getExportMultipleParamScans() != null?asciiSpecs.getExportMultipleParamScans().length:1);
	final int TIME_COUNT = timeSpecs.getEndTimeIndex() - timeSpecs.getBeginTimeIndex() + 1;

	double TOTAL_EXPORTS_OPS = 0;
	switch (geometrySpecs.getModeID()) {
		case GEOMETRY_SELECTIONS:
			TOTAL_EXPORTS_OPS = SIM_COUNT*PARAMSCAN_COUNT*variableSpecs.getVariableNames().length*(geometrySpecs.getCurves().length+(geometrySpecs.getPointCount() > 0?1:0));
			break;
		case GEOMETRY_SLICE:
			TOTAL_EXPORTS_OPS = SIM_COUNT*PARAMSCAN_COUNT*variableSpecs.getVariableNames().length*TIME_COUNT;
			break;
	}
	for (int v = 0; v < SIM_COUNT; v++) {
		//3 states for parameter scan
		//1. simNameSimDataIDs[v].getExportParamScanInfo() == null, not a parameter scan
		//2. simNameSimDataIDs[v].getExportParamScanInfo() != null and asciiSpecs.getExportMultipleParamScans() == null, parameter scan use simNameSimDataIDs[v].getDefaultVCDataIdentifier()
		//3. simNameSimDataIDs[v].getExportParamScanInfo() != null and asciiSpecs.getExportMultipleParamScans() != null, parameter scan use simNameSimDataIDs[v].getExportParamScanInfo().getParamScanJobIndexes() loop through
		for (int ps = 0; ps < PARAMSCAN_COUNT; ps++) {
			int simJobIndex = simNameSimDataIDs[v].getDefaultJobIndex();
			VCDataIdentifier vcdID = simNameSimDataIDs[v].getVCDataIdentifier(simJobIndex);
			if(asciiSpecs.getExportMultipleParamScans() != null){
				simJobIndex = simNameSimDataIDs[v].getExportParamScanInfo().getParamScanJobIndexes()[asciiSpecs.getExportMultipleParamScans()[ps]];
				vcdID = simNameSimDataIDs[v].getVCDataIdentifier(simJobIndex);
			}
			String paramScanInfo = "";
			if(simNameSimDataIDs[v].getExportParamScanInfo() != null){
				for (int i = 0; i < simNameSimDataIDs[v].getExportParamScanInfo().getParamScanConstantNames().length; i++) {
					String psName = simNameSimDataIDs[v].getExportParamScanInfo().getParamScanConstantNames()[i];
					paramScanInfo = paramScanInfo+
					" '"+psName+"'="+simNameSimDataIDs[v].getExportParamScanInfo().getParamScanConstantValues()[simJobIndex][i];					
				}
			}
			String simID = vcdID.getID();							
			String dataType = ".csv";
			FileDataContainerID fileDataContainerID_header = fileDataContainerManager.getNewFileDataContainerID();
			SimulationDescription simulationDescription = new SimulationDescription(outputContext,user, dataServerImpl,vcdID,false);
			fileDataContainerManager.append(fileDataContainerID_header,"\""+"Model: '"+contextName+"'\"\n\"Simulation: '"+simNameSimDataIDs[v].getSimulationName()+"' ("+paramScanInfo+")\"\n"+simulationDescription.getHeader(dataType));
			switch (geometrySpecs.getModeID()) {
				case GEOMETRY_SELECTIONS: {
					// Set mesh on SpatialSelection because mesh is transient field because it's too big for messaging
					SpatialSelection[] spatialSelections = geometrySpecs.getSelections();
					cbit.vcell.solvers.CartesianMesh mesh = dataServerImpl.getMesh(user, vcdID);
					for (int i = 0; i < spatialSelections.length; i ++) {
						spatialSelections[i].setMesh(mesh);
					}
					Vector<ExportOutput> outputV = new Vector<ExportOutput>();
					if (geometrySpecs.getPointCount() > 0) {//assemble single point data together (uses more compact formatting)
						String dataID = "_Points_vars("+geometrySpecs.getPointCount()+")_times("+( timeSpecs.getEndTimeIndex()-timeSpecs.getBeginTimeIndex()+1)+")";
						//StringBuffer data1 = new StringBuffer(data.toString());
						ExportOutput exportOutput1 = new ExportOutput(true, dataType, simID, dataID/* + variableSpecs.getVariableNames()[i]*/, fileDataContainerManager);
						fileDataContainerManager.append(exportOutput1.getFileDataContainerID(), fileDataContainerID_header);
						
						for (int i=0;i<variableSpecs.getVariableNames().length;i++) {
							fileDataContainerManager.append(exportOutput1.getFileDataContainerID(),
								getPointsTimeSeries(outputContext,user, dataServerImpl, vcdID, variableSpecs.getVariableNames()[i], geometrySpecs, timeSpecs.getAllTimes(), timeSpecs.getBeginTimeIndex(), timeSpecs.getEndTimeIndex(), asciiSpecs.getSwitchRowsColumns(),fileDataContainerManager));
							fileDataContainerManager.append(exportOutput1.getFileDataContainerID(),"\n");
							progressCounter++;
							exportServiceImpl.fireExportProgress(jobID, vcdID, "CSV", progressCounter/TOTAL_EXPORTS_OPS);
						}
						outputV.add(exportOutput1);
					}
					if(geometrySpecs.getCurves().length != 0){//assemble curve (non-single point) data together
						String dataID = "_Curves_vars("+(geometrySpecs.getCurves().length)+")_times("+( timeSpecs.getEndTimeIndex()-timeSpecs.getBeginTimeIndex()+1)+")";
						//StringBuffer data1 = new StringBuffer(data.toString());
						ExportOutput exportOutput1 = new ExportOutput(true, dataType, simID, dataID/* + variableSpecs.getVariableNames()[i]*/, fileDataContainerManager);
						fileDataContainerManager.append(exportOutput1.getFileDataContainerID(), fileDataContainerID_header);
						for (int i=0;i<variableSpecs.getVariableNames().length;i++) {
							for (int s = 0; s < geometrySpecs.getCurves().length; s++) {
								if(!GeometrySpecs.isSinglePoint(geometrySpecs.getCurves()[s])){
									fileDataContainerManager.append(exportOutput1.getFileDataContainerID(),getCurveTimeSeries(outputContext,user, dataServerImpl, vcdID, variableSpecs.getVariableNames()[i], geometrySpecs.getCurves()[s], timeSpecs.getAllTimes(), timeSpecs.getBeginTimeIndex(), timeSpecs.getEndTimeIndex(), asciiSpecs.getSwitchRowsColumns(),fileDataContainerManager));
									fileDataContainerManager.append(exportOutput1.getFileDataContainerID(),"\n");
									progressCounter++;
									exportServiceImpl.fireExportProgress(jobID, vcdID, "CSV", progressCounter/TOTAL_EXPORTS_OPS);
								}
							}
						}
						outputV.add(exportOutput1);
					}
					exportOutputV.add(outputV.toArray(new ExportOutput[0]));
					break;
				}
				case GEOMETRY_SLICE: {
					String dataID = "_Slice_" + Coordinate.getNormalAxisPlaneName(geometrySpecs.getAxis()) + "_" + geometrySpecs.getSliceNumber() + "_";
					ExportOutput[] output = new ExportOutput[variableSpecs.getVariableNames().length * TIME_COUNT];
					for (int j=0;j<variableSpecs.getVariableNames().length;j++) {
						for (int i=0;i<TIME_COUNT;i++) {
							StringBuffer inset = new StringBuffer(Integer.toString(i + timeSpecs.getBeginTimeIndex()));
								inset.reverse();
								inset.append("000");
								inset.setLength(4);
								inset.reverse();
							String dataID1 = dataID + variableSpecs.getVariableNames()[j] + "_"+inset.toString();
							
							ExportOutput exportOutput1 = new ExportOutput(true, dataType, simID, dataID1/* + variableSpecs.getVariableNames()[i]*/, fileDataContainerManager);
							fileDataContainerManager.append(exportOutput1.getFileDataContainerID(), fileDataContainerID_header);
							fileDataContainerManager.append(exportOutput1.getFileDataContainerID(),
								getSlice(outputContext,user, dataServerImpl, vcdID, variableSpecs.getVariableNames()[j], i + timeSpecs.getBeginTimeIndex(), Coordinate.getNormalAxisPlaneName(geometrySpecs.getAxis()), geometrySpecs.getSliceNumber(), asciiSpecs.getSwitchRowsColumns(),fileDataContainerManager));
							output[j * TIME_COUNT + i] = exportOutput1;
							
							progressCounter++;
							exportServiceImpl.fireExportProgress(jobID, vcdID, "CSV", progressCounter/TOTAL_EXPORTS_OPS);
							//data1.cleanup();
						//data2.cleanup();
						}
					}
					
					exportOutputV.add(output);
					break;
				}
				default: {
					throw new DataAccessException("Unexpected geometry modeID");
				}
			}
		}
	}
	
	if(exportOutputV.size() == 1){
		return exportOutputV.elementAt(0);
	}
	
	ExportOutput[] combinedExportOutput = new ExportOutput[exportOutputV.elementAt(0).length];
	for (int i = 0; i < combinedExportOutput.length; i++) {
		String DATATYPE = exportOutputV.elementAt(0)[i].getDataType();
		String DATAID = exportOutputV.elementAt(0)[i].getDataID();
		combinedExportOutput[i] = new ExportOutput(true, DATATYPE, "MultiSimulation", DATAID, fileDataContainerManager);
		//FileDataContainer container = fileDataContainerManager.getFileDataContainer(combinedExportOutput[i].getFileDataContainerID());
		for (int j = 0; j < exportOutputV.size(); j++) {
			fileDataContainerManager.append(combinedExportOutput[i].getFileDataContainerID(),exportOutputV.elementAt(j)[i].getFileDataContainerID()); 
			fileDataContainerManager.append(combinedExportOutput[i].getFileDataContainerID(),"\n");
		}
	}
	return combinedExportOutput;

}


/**
 * This method was created in VisualAge.
 * @return java.lang.String
 * @throws IOException 
 */
private FileDataContainerID getCurveTimeSeries(OutputContext outputContext,User user, DataServerImpl dataServerImpl, VCDataIdentifier vcdID, String variableName, SpatialSelection curve, double[] allTimes, int beginIndex, int endIndex, boolean switchRowsColumns,FileDataContainerManager fileDataContainerManager) throws DataAccessException, IOException {
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
		}
		fileDataContainerManager.append(fileDataContainerID,"\n");
		for (int j = 0;j < distances.length;j ++) {
			fileDataContainerManager.append(fileDataContainerID,"," + distances[j]);
			for (int i = beginIndex;i <= endIndex; i ++) {
				fileDataContainerManager.append(fileDataContainerID,"," + variableValues[j + 1][i - beginIndex]);
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
			for (int j = 0;j < distances.length;j ++) {
				fileDataContainerManager.append(fileDataContainerID,"," + variableValues[j + 1][i - beginIndex]);
			}
			fileDataContainerManager.append(fileDataContainerID,"\n");
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
private FileDataContainerID getPointsTimeSeries(OutputContext outputContext,User user, DataServerImpl dataServerImpl, VCDataIdentifier vcdID, String variableName, GeometrySpecs geometrySpecs, double[] allTimes, int beginIndex, int endIndex, boolean switchRowsColumns,FileDataContainerManager fileDataContainerManager) throws DataAccessException, IOException {
	
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
		}
		fileDataContainerManager.append(fileDataContainerID,"\n");	
		for (int k=0;k<pointSpatialSelections.length;k++) {
			fileDataContainerManager.append(fileDataContainerID,"," + getSpatialSelectionDescription(pointSpatialSelections[k]));
			for (int i=beginIndex;i<=endIndex;i++) {
				fileDataContainerManager.append(fileDataContainerID,"," + variableValues[k+1][i - beginIndex]);
			}
			fileDataContainerManager.append(fileDataContainerID,"\n");
		}
	} else {
		fileDataContainerManager.append(fileDataContainerID,",Time\n");
		fileDataContainerManager.append(fileDataContainerID,"Coordinates,");
		for (int k=0;k<pointSpatialSelections.length;k++) {
			fileDataContainerManager.append(fileDataContainerID,"," + getSpatialSelectionDescription(pointSpatialSelections[k]));
		}
		fileDataContainerManager.append(fileDataContainerID,"\n");	
		for (int i=beginIndex;i<=endIndex;i++) {
			fileDataContainerManager.append(fileDataContainerID,"," + allTimes[i]);
			for (int k=0;k<pointSpatialSelections.length;k++) {
				fileDataContainerManager.append(fileDataContainerID,"," + variableValues[k+1][i - beginIndex]);
			}
			fileDataContainerManager.append(fileDataContainerID,"\n");
		}
	}

	return fileDataContainerID;
}


/**
 * This method was created in VisualAge.
 * @return java.io.File
 * @param variable java.lang.String
 * @param time double
 * @throws IOException 

 */
private FileDataContainerID getSlice(OutputContext outputContext,User user, DataServerImpl dataServerImpl, VCDataIdentifier vcdID, String variable, int timeIndex, String slicePlane, int sliceNumber, boolean switchRowsColumns,FileDataContainerManager fileDataContainerManager) throws DataAccessException, IOException {
	
	double[] allTimes = dataServerImpl.getDataSetTimes(user, vcdID);
	double timepoint = allTimes[timeIndex];
	SimDataBlock simDataBlock = dataServerImpl.getSimDataBlock(outputContext,user, vcdID,variable,timepoint);
	double[] data = simDataBlock.getData();
	
	cbit.vcell.solvers.CartesianMesh mesh = dataServerImpl.getMesh(user, vcdID);
	int[] sizeXYZ = {mesh.getSizeX(), mesh.getSizeY(), mesh.getSizeZ()};
	FileDataContainerID fileDataContainerID = fileDataContainerManager.getNewFileDataContainerID();

	
//	StringBuffer buffer = new StringBuffer();

	if (simDataBlock.getVariableType().equals(VariableType.VOLUME) || simDataBlock.getVariableType().equals(VariableType.POSTPROCESSING)) {
		//
		// put data in csv format
		//
		fileDataContainerManager.append(fileDataContainerID,"2D Slice for variable "+variable+" at time "+timepoint);

		if (slicePlane.equals(Coordinate.getNormalAxisPlaneName(Coordinate.Z_AXIS))) {
			fileDataContainerManager.append(fileDataContainerID," in plane XY at Z = "+sliceNumber+"\n\n");
			int start = sliceNumber*sizeXYZ[0]*sizeXYZ[1];
			if (switchRowsColumns) {
				fileDataContainerManager.append(fileDataContainerID,"X in rows, Y in columns\n");
				for (int j=0;j<sizeXYZ[0];j++) {
					for (int i=0;i<sizeXYZ[1];i++) {
						fileDataContainerManager.append(fileDataContainerID,data[start + i*sizeXYZ[0] + j] + ",");
					}
					fileDataContainerManager.append(fileDataContainerID,"\n");
				}		
			} else {
				fileDataContainerManager.append(fileDataContainerID,"X in columns, Y in rows\n");
				for (int i=0;i<sizeXYZ[1];i++) {
					for (int j=0;j<sizeXYZ[0];j++) {
						fileDataContainerManager.append(fileDataContainerID,data[start + i*sizeXYZ[0] + j] + ",");
					}
					fileDataContainerManager.append(fileDataContainerID,"\n");
				}
			}
		}
		
		if (slicePlane.equals(Coordinate.getNormalAxisPlaneName(Coordinate.Y_AXIS))) {
			fileDataContainerManager.append(fileDataContainerID," in plane XZ at Y = "+sliceNumber+"\n\n");
			int start = sliceNumber*sizeXYZ[0];
			if (switchRowsColumns) {
				fileDataContainerManager.append(fileDataContainerID,"X in rows, Z in columns\n");
				for (int i=0;i<sizeXYZ[0];i++) {
					for (int j=0;j<sizeXYZ[2];j++) {
						fileDataContainerManager.append(fileDataContainerID,data[start +j*sizeXYZ[0]*sizeXYZ[1] + i] + ",");
					}
					fileDataContainerManager.append(fileDataContainerID,"\n");
				}
			} else {
				fileDataContainerManager.append(fileDataContainerID,"X in columns, Z in rows\n");
				for (int j=0;j<sizeXYZ[2];j++) {
					for (int i=0;i<sizeXYZ[0];i++) {
						fileDataContainerManager.append(fileDataContainerID,data[start +j*sizeXYZ[0]*sizeXYZ[1] + i] + ",");
					}
					fileDataContainerManager.append(fileDataContainerID,"\n");
				}
			}
		}

		if (slicePlane.equals(Coordinate.getNormalAxisPlaneName(Coordinate.X_AXIS))) {
			fileDataContainerManager.append(fileDataContainerID," in plane YZ at X = "+sliceNumber+"\n\n");
			int start = sliceNumber;
			if (switchRowsColumns) {
				fileDataContainerManager.append(fileDataContainerID,"Y in rows, Z in columns\n");
				for (int j=0;j<sizeXYZ[1];j++) {
					for (int i=0;i<sizeXYZ[2];i++) {
						fileDataContainerManager.append(fileDataContainerID,data[start +i*sizeXYZ[0]*sizeXYZ[1] + j*sizeXYZ[0]] + ",");
					}
					fileDataContainerManager.append(fileDataContainerID,"\n");
				}
			} else {
				fileDataContainerManager.append(fileDataContainerID,"Y in columns, Z in rows\n");
				for (int i=0;i<sizeXYZ[2];i++) {
					for (int j=0;j<sizeXYZ[1];j++) {
						fileDataContainerManager.append(fileDataContainerID,data[start +i*sizeXYZ[0]*sizeXYZ[1] + j*sizeXYZ[0]] + ",");
					}
					fileDataContainerManager.append(fileDataContainerID,"\n");
				}
			}
		}
//	} else if (mesh.getGeometryDimension() < 3) {
	} else {
		// membrane variable; we export the data by index
		// for 3D one gets the whole dataset for now... warning at the client level... will get more sophisticated later...
		fileDataContainerManager.append(fileDataContainerID,"Data for membrane variable "+variable+" at time "+timepoint+"\nEntire datablock by index\n\n");
		for	(int i = 0; i < data.length; i ++) {
			fileDataContainerManager.append(fileDataContainerID,data[i] + "\n");
		}
//		buffer.append("\n");
//	} else {
//		throw new RuntimeException("3D export for membrane or region variables not supported yet");
	}


	return fileDataContainerID;
}


/**
 * This method was created in VisualAge.
 * @throws IOException 
 */
public ExportOutput[] makeASCIIData(OutputContext outputContext,JobRequest jobRequest, User user, DataServerImpl dataServerImpl, ExportSpecs exportSpecs,FileDataContainerManager fileDataContainerManager) 
						throws DataAccessException, IOException {
							
	switch (((ASCIISpecs)exportSpecs.getFormatSpecificSpecs()).getDataType()) {
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
				(ASCIISpecs)exportSpecs.getFormatSpecificSpecs(),
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
				(ASCIISpecs)exportSpecs.getFormatSpecificSpecs(),
				fileDataContainerManager
			);
		case PDE_PARTICLE_DATA:
			return exportParticleData(
					outputContext,
				jobRequest.getJobID(),
				user,
				dataServerImpl,
				exportSpecs.getVCDataIdentifier(),
				exportSpecs.getTimeSpecs(),
				(ASCIISpecs)exportSpecs.getFormatSpecificSpecs(),
				fileDataContainerManager
			);
		default:
			return new ExportOutput[] {new ExportOutput(false, null, null, null, fileDataContainerManager)};
	}
}
}
