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
private ExportOutput[] exportODEData(OutputContext outputContext,long jobID, User user, DataServerImpl dataServerImpl, VCDataIdentifier vcdID, VariableSpecs variableSpecs, TimeSpecs timeSpecs, ASCIISpecs asciiSpecs) throws DataAccessException, IOException {
	String simID = vcdID.getID();
	ExportOutput[] output = new ExportOutput[] {new ExportOutput(false, null, null, null, (FileBackedDataContainer)null)};
	String dataType = ".csv";
	String dataID = "_";
	//StringBuffer data = new StringBuffer();
	FileBackedDataContainer data = new FileBackedDataContainer();
	SimulationDescription simulationDescription = new SimulationDescription(outputContext, user, dataServerImpl,vcdID,true);
	data.append(simulationDescription.getHeader(dataType));
	data.append(getODEDataValues(jobID, user, dataServerImpl, vcdID, variableSpecs.getVariableNames(), timeSpecs.getBeginTimeIndex(), timeSpecs.getEndTimeIndex(), asciiSpecs.getSwitchRowsColumns()));
	dataID += variableSpecs.getModeID() == 0 ? variableSpecs.getVariableNames()[0] : "ManyVars";
	output = new ExportOutput[] {new ExportOutput(true, dataType, simID, dataID, data)};
	//data.cleanup();
	return output;	
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
private ExportOutput[] exportParticleData(OutputContext outputContext,long jobID, User user, DataServerImpl dataServerImpl, VCDataIdentifier vcdID, TimeSpecs timeSpecs, ASCIISpecs asciiSpecs) throws DataAccessException, IOException {

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
	FileBackedDataContainer csvOutput = new FileBackedDataContainer();
	StringBuffer header = new StringBuffer();
	FileBackedDataContainer[] dataLines = null;
	if (switchRowsColumns) {
		dataLines = new FileBackedDataContainer[numberOfParticles * 5];
		for (int j=0;j<dataLines.length;j++) dataLines[j] = new FileBackedDataContainer();
		for (int i=0;i<numberOfParticles;i++) {
			dataLines[5 * i].append ("x," + i + ",");
			dataLines[5 * i + 1].append ("y,,");
			dataLines[5 * i + 2].append ("z,,");
			dataLines[5 * i + 3].append ("state,,");
			dataLines[5 * i + 4].append ("context,,");
		}
	} else {
		dataLines = new FileBackedDataContainer[numberOfTimes];
		for (int j=0;j<dataLines.length;j++) dataLines[j] = new FileBackedDataContainer();
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
				dataLines[5 * j].append(coordinate.getX() + ",");
				dataLines[5 * j + 1].append(coordinate.getY() + ",");
				dataLines[5 * j + 2].append(coordinate.getZ() + ",");
				dataLines[5 * j + 3].append(particleData[j].getState() + ",");
				dataLines[5 * j + 4].append(particleData[j].getContext() + ",");
			}
		} else {
			dataLines[i - beginIndex].append("," + allTimes[i] + ",");
			for (int j=0;j<numberOfParticles;j++) {
				Coordinate coordinate = particleData[j].getCoordinate();
				dataLines[i - beginIndex].append(coordinate.getX() + "," + coordinate.getY() + "," + coordinate.getZ() + ",");
				dataLines[i - beginIndex].append(particleData[j].getState() + ",");
				dataLines[i - beginIndex].append(particleData[j].getContext() + ",");
			}
		}
	}
	csvOutput.append(header.toString());
	for (int i=0;i<dataLines.length;i++) {
		progress = (double)i / dataLines.length;
		exportServiceImpl.fireExportProgress(jobID, vcdID, "CSV", progress);
		csvOutput.append("\n");
		csvOutput.append(dataLines[i]);
		//dataLines[i].cleanup();
	}
	
	return new ExportOutput[] {new ExportOutput(true, dataType, simID, dataID, csvOutput)};    
}


/**
 * This method was created in VisualAge.
 * @throws IOException 
 */
private ExportOutput[] exportPDEData(OutputContext outputContext,long jobID, User user, DataServerImpl dataServerImpl,
		final VCDataIdentifier orig_vcdID, VariableSpecs variableSpecs, TimeSpecs timeSpecs, 
		GeometrySpecs geometrySpecs, ASCIISpecs asciiSpecs,String contextName) 
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
			FileBackedDataContainer data = new FileBackedDataContainer();
			SimulationDescription simulationDescription = new SimulationDescription(outputContext,user, dataServerImpl,vcdID,false);
			data.append("\""+"Model: '"+contextName+"'\"\n\"Simulation: '"+simNameSimDataIDs[v].getSimulationName()+"' ("+paramScanInfo+")\"\n"+simulationDescription.getHeader(dataType));
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
						FileBackedDataContainer data1 = new FileBackedDataContainer();
						data1.append(data);
						
						for (int i=0;i<variableSpecs.getVariableNames().length;i++) {
							data1.append(getPointsTimeSeries(outputContext,user, dataServerImpl, vcdID, variableSpecs.getVariableNames()[i], geometrySpecs, timeSpecs.getAllTimes(), timeSpecs.getBeginTimeIndex(), timeSpecs.getEndTimeIndex(), asciiSpecs.getSwitchRowsColumns()));
							data1.append("\n");
							progressCounter++;
							exportServiceImpl.fireExportProgress(jobID, vcdID, "CSV", progressCounter/TOTAL_EXPORTS_OPS);
						}
						outputV.add(new ExportOutput(true, dataType, simID, dataID/* + variableSpecs.getVariableNames()[i]*/, data1));
					}
					if(geometrySpecs.getCurves().length != 0){//assemble curve (non-single point) data together
						String dataID = "_Curves_vars("+(geometrySpecs.getCurves().length)+")_times("+( timeSpecs.getEndTimeIndex()-timeSpecs.getBeginTimeIndex()+1)+")";
						//StringBuffer data1 = new StringBuffer(data.toString());
						FileBackedDataContainer data1 = new FileBackedDataContainer(data);
						for (int i=0;i<variableSpecs.getVariableNames().length;i++) {
							for (int s = 0; s < geometrySpecs.getCurves().length; s++) {
								if(!GeometrySpecs.isSinglePoint(geometrySpecs.getCurves()[s])){
									data1.append(getCurveTimeSeries(outputContext,user, dataServerImpl, vcdID, variableSpecs.getVariableNames()[i], geometrySpecs.getCurves()[s], timeSpecs.getAllTimes(), timeSpecs.getBeginTimeIndex(), timeSpecs.getEndTimeIndex(), asciiSpecs.getSwitchRowsColumns()));
									data1.append("\n");
									progressCounter++;
									exportServiceImpl.fireExportProgress(jobID, vcdID, "CSV", progressCounter/TOTAL_EXPORTS_OPS);
								}
							}
						}
						data.cleanup();
						outputV.add(new ExportOutput(true, dataType, simID, dataID/* + variableSpecs.getVariableNames()[i]*/, data1));
					}
					exportOutputV.add(outputV.toArray(new ExportOutput[0]));
					break;
				}
				case GEOMETRY_SLICE: {
					String dataID = "_Slice_" + Coordinate.getNormalAxisPlaneName(geometrySpecs.getAxis()) + "_" + geometrySpecs.getSliceNumber() + "_";
					ExportOutput[] output = new ExportOutput[variableSpecs.getVariableNames().length * TIME_COUNT];
					for (int j=0;j<variableSpecs.getVariableNames().length;j++) {
						for (int i=0;i<TIME_COUNT;i++) {
							FileBackedDataContainer data2 = getSlice(outputContext,user, dataServerImpl, vcdID, variableSpecs.getVariableNames()[j], i + timeSpecs.getBeginTimeIndex(), Coordinate.getNormalAxisPlaneName(geometrySpecs.getAxis()), geometrySpecs.getSliceNumber(), asciiSpecs.getSwitchRowsColumns());
							FileBackedDataContainer data1 = new FileBackedDataContainer(data);
							data1.append(data2);
							StringBuffer inset = new StringBuffer(Integer.toString(i + timeSpecs.getBeginTimeIndex()));
								inset.reverse();
								inset.append("000");
								inset.setLength(4);
								inset.reverse();
							String dataID1 = dataID + variableSpecs.getVariableNames()[j] + "_"+inset.toString();
							output[j * TIME_COUNT + i] = new ExportOutput(true, dataType, simID, dataID1, data1);
							
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
		FileBackedDataContainer container = new FileBackedDataContainer();
		
		//StringBuffer combinedDataSB = new StringBuffer();
		String DATATYPE = exportOutputV.elementAt(0)[i].getDataType();
		String DATAID = exportOutputV.elementAt(0)[i].getDataID();
		for (int j = 0; j < exportOutputV.size(); j++) {
//			String currentCSV = new String(exportOutputV.elementAt(j)[i].getData());
			container.append(exportOutputV.elementAt(j)[i].getDataContainer()); 
			container.append("\n");
		}

		combinedExportOutput[i] =
			new ExportOutput(true, DATATYPE, "MultiSimulation", DATAID, container); 
		//container.cleanup();
			
	}
	return combinedExportOutput;

}


/**
 * This method was created in VisualAge.
 * @return java.lang.String
 * @throws IOException 
 */
private FileBackedDataContainer getCurveTimeSeries(OutputContext outputContext,User user, DataServerImpl dataServerImpl, VCDataIdentifier vcdID, String variableName, SpatialSelection curve, double[] allTimes, int beginIndex, int endIndex, boolean switchRowsColumns) throws DataAccessException, IOException {
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
	FileBackedDataContainer container = new FileBackedDataContainer();
	
	container.append("\"variable ('" + variableName + "') times (" + allTimes[beginIndex] + " " + allTimes[endIndex] + ") "+getSpatialSelectionDescription(curve)+"\"\n");
	if (switchRowsColumns) {
		container.append(",Distances\n");
		container.append("Times,");
		for (int i = beginIndex;i <= endIndex;i ++) {
			container.append("," + allTimes[i]);
		}
		container.append("\n");
		for (int j = 0;j < distances.length;j ++) {
			container.append("," + distances[j]);
			for (int i = beginIndex;i <= endIndex; i ++) {
				container.append("," + variableValues[j + 1][i - beginIndex]);
			}
			container.append("\n");
		}
	} else {
		container.append(",Times\n");
		container.append("Distances,");
		for (int i = 0;i < distances.length;i ++) {
			container.append("," + distances[i]);
		}
		container.append("\n");
		for (int i = beginIndex;i <= endIndex;i ++) {
			container.append("," + allTimes[i]);
			for (int j = 0;j < distances.length;j ++) {
				container.append("," + variableValues[j + 1][i - beginIndex]);
			}
			container.append("\n");
		}
	}

	return container;   
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
private FileBackedDataContainer getODEDataValues(long jobID, User user, DataServerImpl dataServerImpl, VCDataIdentifier vcdID, String[] variableNames, int beginIndex, int endIndex, boolean switchRowsColumns) throws DataAccessException, IOException {

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
	FileBackedDataContainer container = new FileBackedDataContainer();
 
	if(isTimeSeries)
	{
		container.append(
		    "Variable values over the time range " + allTimes[beginIndex] + " to " + allTimes[endIndex] + "\n\n");
	}
	else if(isMultiTrial)
	{
		container.append(
		    "Variable values over the Trials " + allTimes[beginIndex] + " to " + allTimes[endIndex] + "\n\n");
	}
	if (switchRowsColumns) {
		container.append(",Variable\n");
		if(isTimeSeries) container.append("Time,");
		else if(isMultiTrial) container.append("Trial No,");
		for (int i=beginIndex;i<=endIndex;i++) {
			container.append("," + allTimes[i]);
		}
		container.append("\n");	
		for (int k=0;k<variableNames.length;k++) {
			container.append("," + variableNames[k]);
			for (int i=beginIndex;i<=endIndex;i++) {
				progress = 0.5 + (double)(i + k * (endIndex - beginIndex + 1)) / (2 * variableNames.length * (endIndex - beginIndex + 1));
				exportServiceImpl.fireExportProgress(jobID, vcdID, "CSV", progress);
				container.append("," + variableValues[k][i - beginIndex]);
			}
			container.append("\n");
		}
	} else {
		if(isTimeSeries) container.append(",Time\n");
		else if(isMultiTrial) container.append(",Trial No\n");		
		container.append("Variable,");
		for (int k=0;k<variableNames.length;k++) {
			container.append("," + variableNames[k]);
		}
		container.append("\n");	
		for (int i=beginIndex;i<=endIndex;i++) {
			container.append("," + allTimes[i]);
			for (int k=0;k<variableNames.length;k++) {
				progress = 0.5 + (double)(i + k * (endIndex - beginIndex + 1)) / (2 * variableNames.length * (endIndex - beginIndex + 1));
				exportServiceImpl.fireExportProgress(jobID, vcdID, "CSV", progress);
				container.append("," + variableValues[k][i - beginIndex]);
			}
			container.append("\n");
		}
	}
	return container;
}


/**
 * This method was created in VisualAge.
 * @return java.lang.String
 * @throws IOException 
 */
private FileBackedDataContainer getPointsTimeSeries(OutputContext outputContext,User user, DataServerImpl dataServerImpl, VCDataIdentifier vcdID, String variableName, GeometrySpecs geometrySpecs, double[] allTimes, int beginIndex, int endIndex, boolean switchRowsColumns) throws DataAccessException, IOException {
	
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
	FileBackedDataContainer container = new FileBackedDataContainer();

//	buffer.append(
//		"\"Time Series for variable '" + variableName + "'\" over the range " + allTimes[beginIndex] + " to " + allTimes[endIndex] + "\n");
	container.append("\"variable ('" + variableName + "') times (" + allTimes[beginIndex] + " " + allTimes[endIndex] + ") "/*+getSpatialSelectionDescription(curve)*/+"\"\n");
	if (switchRowsColumns) {
		container.append(",Coordinates\n");
		container.append("Time,");
		for (int i=beginIndex;i<=endIndex;i++) {
			container.append("," + allTimes[i]);
		}
		container.append("\n");	
		for (int k=0;k<pointSpatialSelections.length;k++) {
			container.append("," + getSpatialSelectionDescription(pointSpatialSelections[k]));
			for (int i=beginIndex;i<=endIndex;i++) {
				container.append("," + variableValues[k+1][i - beginIndex]);
			}
			container.append("\n");
		}
	} else {
		container.append(",Time\n");
		container.append("Coordinates,");
		for (int k=0;k<pointSpatialSelections.length;k++) {
			container.append("," + getSpatialSelectionDescription(pointSpatialSelections[k]));
		}
		container.append("\n");	
		for (int i=beginIndex;i<=endIndex;i++) {
			container.append("," + allTimes[i]);
			for (int k=0;k<pointSpatialSelections.length;k++) {
				container.append("," + variableValues[k+1][i - beginIndex]);
			}
			container.append("\n");
		}
	}

	return container;
}


/**
 * This method was created in VisualAge.
 * @return java.io.File
 * @param variable java.lang.String
 * @param time double
 * @throws IOException 

 */
private FileBackedDataContainer getSlice(OutputContext outputContext,User user, DataServerImpl dataServerImpl, VCDataIdentifier vcdID, String variable, int timeIndex, String slicePlane, int sliceNumber, boolean switchRowsColumns) throws DataAccessException, IOException {
	
	double[] allTimes = dataServerImpl.getDataSetTimes(user, vcdID);
	double timepoint = allTimes[timeIndex];
	SimDataBlock simDataBlock = dataServerImpl.getSimDataBlock(outputContext,user, vcdID,variable,timepoint);
	double[] data = simDataBlock.getData();
	
	cbit.vcell.solvers.CartesianMesh mesh = dataServerImpl.getMesh(user, vcdID);
	int[] sizeXYZ = {mesh.getSizeX(), mesh.getSizeY(), mesh.getSizeZ()};
	FileBackedDataContainer container = new FileBackedDataContainer();

	
//	StringBuffer buffer = new StringBuffer();

	if (simDataBlock.getVariableType().equals(VariableType.VOLUME) || simDataBlock.getVariableType().equals(VariableType.POSTPROCESSING)) {
		//
		// put data in csv format
		//
		container.append("2D Slice for variable "+variable+" at time "+timepoint);

		if (slicePlane.equals(Coordinate.getNormalAxisPlaneName(Coordinate.Z_AXIS))) {
			container.append(" in plane XY at Z = "+sliceNumber+"\n\n");
			int start = sliceNumber*sizeXYZ[0]*sizeXYZ[1];
			if (switchRowsColumns) {
				container.append("X in rows, Y in columns\n");
				for (int j=0;j<sizeXYZ[0];j++) {
					for (int i=0;i<sizeXYZ[1];i++) {
						container.append(data[start + i*sizeXYZ[0] + j] + ",");
					}
					container.append("\n");
				}		
			} else {
				container.append("X in columns, Y in rows\n");
				for (int i=0;i<sizeXYZ[1];i++) {
					for (int j=0;j<sizeXYZ[0];j++) {
						container.append(data[start + i*sizeXYZ[0] + j] + ",");
					}
					container.append("\n");
				}
			}
		}
		
		if (slicePlane.equals(Coordinate.getNormalAxisPlaneName(Coordinate.Y_AXIS))) {
			container.append(" in plane XZ at Y = "+sliceNumber+"\n\n");
			int start = sliceNumber*sizeXYZ[0];
			if (switchRowsColumns) {
				container.append("X in rows, Z in columns\n");
				for (int i=0;i<sizeXYZ[0];i++) {
					for (int j=0;j<sizeXYZ[2];j++) {
						container.append(data[start +j*sizeXYZ[0]*sizeXYZ[1] + i] + ",");
					}
					container.append("\n");
				}
			} else {
				container.append("X in columns, Z in rows\n");
				for (int j=0;j<sizeXYZ[2];j++) {
					for (int i=0;i<sizeXYZ[0];i++) {
						container.append(data[start +j*sizeXYZ[0]*sizeXYZ[1] + i] + ",");
					}
					container.append("\n");
				}
			}
		}

		if (slicePlane.equals(Coordinate.getNormalAxisPlaneName(Coordinate.X_AXIS))) {
			container.append(" in plane YZ at X = "+sliceNumber+"\n\n");
			int start = sliceNumber;
			if (switchRowsColumns) {
				container.append("Y in rows, Z in columns\n");
				for (int j=0;j<sizeXYZ[1];j++) {
					for (int i=0;i<sizeXYZ[2];i++) {
						container.append(data[start +i*sizeXYZ[0]*sizeXYZ[1] + j*sizeXYZ[0]] + ",");
					}
					container.append("\n");
				}
			} else {
				container.append("Y in columns, Z in rows\n");
				for (int i=0;i<sizeXYZ[2];i++) {
					for (int j=0;j<sizeXYZ[1];j++) {
						container.append(data[start +i*sizeXYZ[0]*sizeXYZ[1] + j*sizeXYZ[0]] + ",");
					}
					container.append("\n");
				}
			}
		}
//	} else if (mesh.getGeometryDimension() < 3) {
	} else {
		// membrane variable; we export the data by index
		// for 3D one gets the whole dataset for now... warning at the client level... will get more sophisticated later...
		container.append("Data for membrane variable "+variable+" at time "+timepoint+"\nEntire datablock by index\n\n");
		for	(int i = 0; i < data.length; i ++) {
			container.append(data[i] + "\n");
		}
//		buffer.append("\n");
//	} else {
//		throw new RuntimeException("3D export for membrane or region variables not supported yet");
	}


	return container;
}


/**
 * This method was created in VisualAge.
 * @throws IOException 
 */
public ExportOutput[] makeASCIIData(OutputContext outputContext,JobRequest jobRequest, User user, DataServerImpl dataServerImpl, ExportSpecs exportSpecs) 
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
				exportSpecs.getContextName()
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
				(ASCIISpecs)exportSpecs.getFormatSpecificSpecs()
			);
		case PDE_PARTICLE_DATA:
			return exportParticleData(
					outputContext,
				jobRequest.getJobID(),
				user,
				dataServerImpl,
				exportSpecs.getVCDataIdentifier(),
				exportSpecs.getTimeSpecs(),
				(ASCIISpecs)exportSpecs.getFormatSpecificSpecs()
			);
		default:
			return new ExportOutput[] {new ExportOutput(false, null, null, null, (FileBackedDataContainer)null)};
	}
}
}
