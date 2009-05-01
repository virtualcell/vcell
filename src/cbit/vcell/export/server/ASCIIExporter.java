package cbit.vcell.export.server;
import cbit.vcell.solver.ode.*;
import cbit.vcell.math.*;
import cbit.plot.*;
import cbit.vcell.simdata.gui.*;
import cbit.vcell.geometry.*;
import java.rmi.*;

import org.vcell.util.Coordinate;
import org.vcell.util.DataAccessException;
import org.vcell.util.VCDataIdentifier;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDataJobID;

import cbit.vcell.simdata.*;
import cbit.vcell.server.*;
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


private ExportOutput[] exportODEData(long jobID, User user, DataServerImpl dataServerImpl, VCDataIdentifier vcdID, VariableSpecs variableSpecs, TimeSpecs timeSpecs, ASCIISpecs asciiSpecs) throws DataAccessException, RemoteException {
	String simID = vcdID.getID();
	ExportOutput[] output = new ExportOutput[] {new ExportOutput(false, null, null, null, null)};
	String dataType = ".csv";
	String dataID = "_";
	StringBuffer data = new StringBuffer();
	SimulationDescription simulationDescription = new SimulationDescription(user, dataServerImpl,vcdID);
	data.append(simulationDescription.getHeader(dataType));
	data.append(getODEDataValues(jobID, user, dataServerImpl, vcdID, variableSpecs.getVariableNames(), timeSpecs.getBeginTimeIndex(), timeSpecs.getEndTimeIndex(), asciiSpecs.getSwitchRowsColumns()));
	dataID += variableSpecs.getModeID() == 0 ? variableSpecs.getVariableNames()[0] : "ManyVars";
	output = new ExportOutput[] {new ExportOutput(true, dataType, simID, dataID, data.toString().getBytes())};
	return output;	
}


/**
 * Insert the method's description here.
 * Creation date: (1/12/00 5:00:28 PM)
 * @return cbit.vcell.export.server.ExportOutput[]
 * @param dsc cbit.vcell.server.DataSetController
 * @param timeSpecs cbit.vcell.export.server.TimeSpecs
 */
private ExportOutput[] exportParticleData(long jobID, User user, DataServerImpl dataServerImpl, VCDataIdentifier vcdID, TimeSpecs timeSpecs, ASCIISpecs asciiSpecs) throws RemoteException, DataAccessException {

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
	StringBuffer csvOutput = new StringBuffer();
	StringBuffer header = new StringBuffer();
	StringBuffer[] dataLines = null;
	if (switchRowsColumns) {
		dataLines = new StringBuffer[numberOfParticles * 5];
		for (int j=0;j<dataLines.length;j++) dataLines[j] = new StringBuffer();
		for (int i=0;i<numberOfParticles;i++) {
			dataLines[5 * i].append ("x," + i + ",");
			dataLines[5 * i + 1].append ("y,,");
			dataLines[5 * i + 2].append ("z,,");
			dataLines[5 * i + 3].append ("state,,");
			dataLines[5 * i + 4].append ("context,,");
		}
	} else {
		dataLines = new StringBuffer[numberOfTimes];
		for (int j=0;j<dataLines.length;j++) dataLines[j] = new StringBuffer();
	}
	SimulationDescription simulationDescription = new SimulationDescription(user, dataServerImpl,vcdID);
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
		csvOutput.append("\n" + dataLines[i].toString());
	}
	
	return new ExportOutput[] {new ExportOutput(true, dataType, simID, dataID, csvOutput.toString().getBytes())};
}


/**
 * This method was created in VisualAge.
 */
private ExportOutput[] exportPDEData(long jobID, User user, DataServerImpl dataServerImpl, VCDataIdentifier vcdID, VariableSpecs variableSpecs, TimeSpecs timeSpecs, GeometrySpecs geometrySpecs, ASCIISpecs asciiSpecs) 
						throws RemoteException, DataAccessException {
							
	double progress = 0.0;
	
	String simID = vcdID.getID();							
	ExportOutput[] output = new ExportOutput[] {new ExportOutput(false, null, null, null, null)};
	String dataType = ".csv";
	String dataID = "_";
	StringBuffer data = new StringBuffer();
	SimulationDescription simulationDescription = new SimulationDescription(user, dataServerImpl,vcdID);
	data.append(simulationDescription.getHeader(dataType));
	switch (geometrySpecs.getModeID()) {
		case GEOMETRY_SELECTIONS: {
			// Set mesh on SpatialSelection because mesh is transient field because it's too big for messaging
			SpatialSelection[] spatialSelections = geometrySpecs.getSelections();
			cbit.vcell.solvers.CartesianMesh mesh = dataServerImpl.getMesh(user, vcdID);
			for (int i = 0; i < spatialSelections.length; i ++) {
				spatialSelections[i].setMesh(mesh);
			}
			int required = variableSpecs.getVariableNames().length * geometrySpecs.getCurves().length;
			if (geometrySpecs.getPointIndexes().length > 0) {
				required += variableSpecs.getVariableNames().length;
			}
			output = new ExportOutput[required];
			String dataIDroot = dataID;
			int pointOffset = 0;
			if (geometrySpecs.getPointIndexes().length > 0) {
				pointOffset = variableSpecs.getVariableNames().length;
				dataID += "Points_Time_";
				for (int i=0;i<variableSpecs.getVariableNames().length;i++) {
					progress = (double)i / required;
					exportServiceImpl.fireExportProgress(jobID, vcdID, "CSV", progress);
					StringBuffer data1 = new StringBuffer(data.toString());
					data1.append(getPointsTimeSeries(user, dataServerImpl, vcdID, variableSpecs.getVariableNames()[i], geometrySpecs.getPointIndexes(), timeSpecs.getAllTimes(), timeSpecs.getBeginTimeIndex(), timeSpecs.getEndTimeIndex(), asciiSpecs.getSwitchRowsColumns()));
					output[i] = new ExportOutput(true, dataType, simID, dataID + variableSpecs.getVariableNames()[i], data1.toString().getBytes());
				}
			}
			for (int s = 0; s < geometrySpecs.getCurves().length; s++) {
				int done = geometrySpecs.getPointIndexes().length > 0 ? variableSpecs.getVariableNames().length : 0;
				dataID = dataIDroot;
				dataID += "Curve_" + s + "_Time_";
				for (int i=0;i<variableSpecs.getVariableNames().length;i++) {
					progress = (double)(done + i + s * variableSpecs.getVariableNames().length) / required;
					exportServiceImpl.fireExportProgress(jobID, vcdID, "CSV", progress);
					StringBuffer data1 = new StringBuffer(data.toString());
					data1.append(getCurveTimeSeries(user, dataServerImpl, vcdID, variableSpecs.getVariableNames()[i], geometrySpecs.getCurves()[s], timeSpecs.getAllTimes(), timeSpecs.getBeginTimeIndex(), timeSpecs.getEndTimeIndex(), asciiSpecs.getSwitchRowsColumns()));
					output[s * variableSpecs.getVariableNames().length + i+pointOffset] = new ExportOutput(true, dataType, simID, dataID + variableSpecs.getVariableNames()[i], data1.toString().getBytes());
				}
			}
			break;
		}
		case GEOMETRY_SLICE: {
			dataID += "Slice_" + Coordinate.getNormalAxisPlaneName(geometrySpecs.getAxis()) + "_" + geometrySpecs.getSliceNumber() + "_";
			output = new ExportOutput[variableSpecs.getVariableNames().length * (timeSpecs.getEndTimeIndex() - timeSpecs.getBeginTimeIndex() + 1)];
			int required = output.length;
			for (int j=0;j<variableSpecs.getVariableNames().length;j++) {
				//String[] data2 = new String[timeSpecs.getEndTimeIndex() - timeSpecs.getBeginTimeIndex() + 1];
				int data2Length = timeSpecs.getEndTimeIndex() - timeSpecs.getBeginTimeIndex() + 1;
				for (int i=0;i<data2Length;i++) {
					progress = (double)(i + j * data2Length) / required;
					exportServiceImpl.fireExportProgress(jobID, vcdID, "CSV", progress);
					String data2 = getSlice(user, dataServerImpl, vcdID, variableSpecs.getVariableNames()[j], i + timeSpecs.getBeginTimeIndex(), Coordinate.getNormalAxisPlaneName(geometrySpecs.getAxis()), geometrySpecs.getSliceNumber(), asciiSpecs.getSwitchRowsColumns());
					StringBuffer data1 = new StringBuffer(data.toString());
					data1.append(data2);
					StringBuffer inset = new StringBuffer(Integer.toString(i + timeSpecs.getBeginTimeIndex()));
						inset.reverse();
						inset.append("000");
						inset.setLength(4);
						inset.reverse();
					String dataID1 = dataID + variableSpecs.getVariableNames()[j] + inset.toString();
					output[j * data2Length + i] = new ExportOutput(true, dataType, simID, dataID1, data1.toString().getBytes());
				}
			}
			break;
		}
		default: {
			throw new DataAccessException("Undexpected geometry modeID");
		}
	}
	return output;	
}


/**
 * This method was created in VisualAge.
 * @return java.lang.String
 * @exception java.rmi.RemoteException The exception description.
 */
private String getCurveTimeSeries(User user, DataServerImpl dataServerImpl, VCDataIdentifier vcdID, String variableName, SpatialSelection curve, double[] allTimes, int beginIndex, int endIndex, boolean switchRowsColumns) throws DataAccessException, RemoteException {
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
		pointIndexes = ssh.getSampledIndexes();
		distances = ssh.getWorldCoordinateLengths();
	}

	org.vcell.util.TimeSeriesJobSpec timeSeriesJobSpec =
		new org.vcell.util.TimeSeriesJobSpec(
				new String[]{variableName},new int[][]{pointIndexes},new int[][]{crossingMembraneIndexes},allTimes[beginIndex],1,allTimes[endIndex],
				VCDataJobID.createVCDataJobID(user, false));
	org.vcell.util.TSJobResultsNoStats timeSeriesJobResults = (org.vcell.util.TSJobResultsNoStats)dataServerImpl.getTimeSeriesValues(user, vcdID, timeSeriesJobSpec);

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
	StringBuffer buffer = new StringBuffer();
	buffer.append("Time Series for variable " + variableName + "\n" + "over the time " + allTimes[beginIndex] + " to " + allTimes[endIndex] + "\n" + "of " + curve + "\n\n");
	if (switchRowsColumns) {
		buffer.append(",Distances\n");
		buffer.append("Times,");
		for (int i = beginIndex;i <= endIndex;i ++) {
			buffer.append("," + allTimes[i]);
		}
		buffer.append("\n");
		for (int j = 0;j < distances.length;j ++) {
			buffer.append("," + distances[j]);
			for (int i = beginIndex;i <= endIndex; i ++) {
				buffer.append("," + variableValues[j + 1][i - beginIndex]);
			}
			buffer.append("\n");
		}
	} else {
		buffer.append(",Times\n");
		buffer.append("Distances,");
		for (int i = 0;i < distances.length;i ++) {
			buffer.append("," + distances[i]);
		}
		buffer.append("\n");
		for (int i = beginIndex;i <= endIndex;i ++) {
			buffer.append("," + allTimes[i]);
			for (int j = 0;j < distances.length;j ++) {
				buffer.append("," + variableValues[j + 1][i - beginIndex]);
			}
			buffer.append("\n");
		}
	}

	return buffer.toString();
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
 */
private String getODEDataValues(long jobID, User user, DataServerImpl dataServerImpl, VCDataIdentifier vcdID, String[] variableNames, int beginIndex, int endIndex, boolean switchRowsColumns) throws DataAccessException, RemoteException {

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
	StringBuffer buffer = new StringBuffer();
	if(isTimeSeries)
	{
		buffer.append(
		    "Variable values over the time range " + allTimes[beginIndex] + " to " + allTimes[endIndex] + "\n\n");
	}
	else if(isMultiTrial)
	{
		buffer.append(
		    "Variable values over the Trials " + allTimes[beginIndex] + " to " + allTimes[endIndex] + "\n\n");
	}
	if (switchRowsColumns) {
		buffer.append(",Variable\n");
		if(isTimeSeries) buffer.append("Time,");
		else if(isMultiTrial) buffer.append("Trial No,");
		for (int i=beginIndex;i<=endIndex;i++) {
			buffer.append("," + allTimes[i]);
		}
		buffer.append("\n");	
		for (int k=0;k<variableNames.length;k++) {
			buffer.append("," + variableNames[k]);
			for (int i=beginIndex;i<=endIndex;i++) {
				progress = 0.5 + (double)(i + k * (endIndex - beginIndex + 1)) / (2 * variableNames.length * (endIndex - beginIndex + 1));
				exportServiceImpl.fireExportProgress(jobID, vcdID, "CSV", progress);
				buffer.append("," + variableValues[k][i - beginIndex]);
			}
			buffer.append("\n");
		}
	} else {
		if(isTimeSeries) buffer.append(",Time\n");
		else if(isMultiTrial) buffer.append(",Trial No\n");		
		buffer.append("Variable,");
		for (int k=0;k<variableNames.length;k++) {
			buffer.append("," + variableNames[k]);
		}
		buffer.append("\n");	
		for (int i=beginIndex;i<=endIndex;i++) {
			buffer.append("," + allTimes[i]);
			for (int k=0;k<variableNames.length;k++) {
				progress = 0.5 + (double)(i + k * (endIndex - beginIndex + 1)) / (2 * variableNames.length * (endIndex - beginIndex + 1));
				exportServiceImpl.fireExportProgress(jobID, vcdID, "CSV", progress);
				buffer.append("," + variableValues[k][i - beginIndex]);
			}
			buffer.append("\n");
		}
	}

	return buffer.toString();
}


/**
 * This method was created in VisualAge.
 * @return java.lang.String
 * @exception java.rmi.RemoteException The exception description.
 */
private String getPointsTimeSeries(User user, DataServerImpl dataServerImpl, VCDataIdentifier vcdID, String variableName, int[] pointIndexes, double[] allTimes, int beginIndex, int endIndex, boolean switchRowsColumns) throws DataAccessException, RemoteException {
	
	org.vcell.util.TimeSeriesJobSpec timeSeriesJobSpec =
		new org.vcell.util.TimeSeriesJobSpec(
				new String[]{variableName},new int[][]{pointIndexes},null,allTimes[beginIndex],1,allTimes[endIndex],
				VCDataJobID.createVCDataJobID(user, false));
	org.vcell.util.TSJobResultsNoStats timeSeriesJobResults = (org.vcell.util.TSJobResultsNoStats)dataServerImpl.getTimeSeriesValues(user, vcdID, timeSeriesJobSpec);

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
	StringBuffer buffer = new StringBuffer();
	buffer.append(
		"Time Series for variable " + variableName + " over the range " + allTimes[beginIndex] + " to " + allTimes[endIndex] + "\n\n");
	if (switchRowsColumns) {
		buffer.append(",Coordinates\n");
		buffer.append("Time,");
		for (int i=beginIndex;i<=endIndex;i++) {
			buffer.append("," + allTimes[i]);
		}
		buffer.append("\n");	
		for (int k=0;k<pointIndexes.length;k++) {
			buffer.append("," + "Index_"+pointIndexes[k]);
			for (int i=beginIndex;i<=endIndex;i++) {
				buffer.append("," + variableValues[k+1][i - beginIndex]);
			}
			buffer.append("\n");
		}
	} else {
		buffer.append(",Time\n");
		buffer.append("Coordinates,");
		for (int k=0;k<pointIndexes.length;k++) {
			buffer.append("," + "Index_"+pointIndexes[k]);
		}
		buffer.append("\n");	
		for (int i=beginIndex;i<=endIndex;i++) {
			buffer.append("," + allTimes[i]);
			for (int k=0;k<pointIndexes.length;k++) {
				buffer.append("," + variableValues[k+1][i - beginIndex]);
			}
			buffer.append("\n");
		}
	}

	return buffer.toString();
}


/**
 * This method was created in VisualAge.
 * @return java.lang.String
 * @param variable java.lang.String
 * @param time double
 * @deprecated - see comments on membrane variable at the end
 */
private String getSlice(User user, DataServerImpl dataServerImpl, VCDataIdentifier vcdID, String variable, int timeIndex, String slicePlane, int sliceNumber, boolean switchRowsColumns) throws DataAccessException, RemoteException {
	
	double[] allTimes = dataServerImpl.getDataSetTimes(user, vcdID);
	double timepoint = allTimes[timeIndex];
	SimDataBlock simDataBlock = dataServerImpl.getSimDataBlock(user, vcdID,variable,timepoint);
	double[] data = simDataBlock.getData();
	
	cbit.vcell.solvers.CartesianMesh mesh = dataServerImpl.getMesh(user, vcdID);
	int[] sizeXYZ = {mesh.getSizeX(), mesh.getSizeY(), mesh.getSizeZ()};
	
	StringBuffer buffer = new StringBuffer();

	if (simDataBlock.getVariableType().equals(VariableType.VOLUME)) {
		//
		// put data in csv format
		//
		buffer.append("2D Slice for variable "+variable+" at time "+timepoint);

		if (slicePlane.equals(Coordinate.getNormalAxisPlaneName(Coordinate.Z_AXIS))) {
			buffer.append(" in plane XY at Z = "+sliceNumber+"\n\n");
			int start = sliceNumber*sizeXYZ[0]*sizeXYZ[1];
			if (switchRowsColumns) {
				buffer.append("X in rows, Y in columns\n");
				for (int j=0;j<sizeXYZ[0];j++) {
					for (int i=0;i<sizeXYZ[1];i++) {
						buffer.append(data[start + i*sizeXYZ[0] + j] + ",");
					}
					buffer.append("\n");
				}		
			} else {
				buffer.append("X in columns, Y in rows\n");
				for (int i=0;i<sizeXYZ[1];i++) {
					for (int j=0;j<sizeXYZ[0];j++) {
						buffer.append(data[start + i*sizeXYZ[0] + j] + ",");
					}
					buffer.append("\n");
				}
			}
		}
		
		if (slicePlane.equals(Coordinate.getNormalAxisPlaneName(Coordinate.Y_AXIS))) {
			buffer.append(" in plane XZ at Y = "+sliceNumber+"\n\n");
			int start = sliceNumber*sizeXYZ[0];
			if (switchRowsColumns) {
				buffer.append("X in rows, Z in columns\n");
				for (int i=0;i<sizeXYZ[0];i++) {
					for (int j=0;j<sizeXYZ[2];j++) {
						buffer.append(data[start +j*sizeXYZ[0]*sizeXYZ[1] + i] + ",");
					}
					buffer.append("\n");
				}
			} else {
				buffer.append("X in columns, Z in rows\n");
				for (int j=0;j<sizeXYZ[2];j++) {
					for (int i=0;i<sizeXYZ[0];i++) {
						buffer.append(data[start +j*sizeXYZ[0]*sizeXYZ[1] + i] + ",");
					}
					buffer.append("\n");
				}
			}
		}

		if (slicePlane.equals(Coordinate.getNormalAxisPlaneName(Coordinate.X_AXIS))) {
			buffer.append(" in plane YZ at X = "+sliceNumber+"\n\n");
			int start = sliceNumber;
			if (switchRowsColumns) {
				buffer.append("Y in rows, Z in columns\n");
				for (int j=0;j<sizeXYZ[1];j++) {
					for (int i=0;i<sizeXYZ[2];i++) {
						buffer.append(data[start +i*sizeXYZ[0]*sizeXYZ[1] + j*sizeXYZ[0]] + ",");
					}
					buffer.append("\n");
				}
			} else {
				buffer.append("Y in columns, Z in rows\n");
				for (int i=0;i<sizeXYZ[2];i++) {
					for (int j=0;j<sizeXYZ[1];j++) {
						buffer.append(data[start +i*sizeXYZ[0]*sizeXYZ[1] + j*sizeXYZ[0]] + ",");
					}
					buffer.append("\n");
				}
			}
		}
//	} else if (mesh.getGeometryDimension() < 3) {
	} else {
		// membrane variable; we export the data by index
		// for 3D one gets the whole dataset for now... warning at the client level... will get more sophisticated later...
		buffer.append("Data for membrane variable "+variable+" at time "+timepoint+"\nEntire datablock by index\n\n");
		for	(int i = 0; i < data.length; i ++) {
			buffer.append(data[i] + "\n");
		}
//		buffer.append("\n");
//	} else {
//		throw new RuntimeException("3D export for membrane or region variables not supported yet");
	}


	return buffer.toString();
}


/**
 * This method was created in VisualAge.
 */
public ExportOutput[] makeASCIIData(JobRequest jobRequest, User user, DataServerImpl dataServerImpl, ExportSpecs exportSpecs) 
						throws RemoteException, DataAccessException {
							
	switch (((ASCIISpecs)exportSpecs.getFormatSpecificSpecs()).getDataType()) {
		case PDE_VARIABLE_DATA:
			return exportPDEData(
				jobRequest.getJobID(),
				user,
				dataServerImpl,
				exportSpecs.getVCDataIdentifier(),
				exportSpecs.getVariableSpecs(),
				exportSpecs.getTimeSpecs(),
				exportSpecs.getGeometrySpecs(),
				(ASCIISpecs)exportSpecs.getFormatSpecificSpecs()
			);
		case ODE_VARIABLE_DATA:
			return exportODEData(
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
				jobRequest.getJobID(),
				user,
				dataServerImpl,
				exportSpecs.getVCDataIdentifier(),
				exportSpecs.getTimeSpecs(),
				(ASCIISpecs)exportSpecs.getFormatSpecificSpecs()
			);
		default:
			return new ExportOutput[] {new ExportOutput(false, null, null, null, null)};
	}
}
}