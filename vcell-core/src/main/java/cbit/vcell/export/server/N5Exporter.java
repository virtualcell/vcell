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

import cbit.vcell.math.VariableType;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.simdata.*;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solvers.CartesianMesh;
import com.google.gson.GsonBuilder;
import edu.uchc.connjur.wb.ExecutionTrace;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.janelia.saalfeldlab.n5.DataType;
import org.janelia.saalfeldlab.n5.DatasetAttributes;
import org.janelia.saalfeldlab.n5.DoubleArrayDataBlock;
import org.janelia.saalfeldlab.n5.N5FSWriter;
import org.vcell.util.DataAccessException;
import org.vcell.util.ISize;
import org.vcell.util.document.User;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


public class N5Exporter implements ExportConstants {
	private final static Logger lg = LogManager.getLogger(N5Exporter.class);

	private ExportServiceImpl exportServiceImpl = null;

	private DataServerImpl dataServer;

	private VCSimulationDataIdentifier vcDataID;
	public String n5BucketName = "n5Data";

	private User user;

	public static final ArrayList<VariableType> unsupportedTypes = new ArrayList<>(Arrays.asList(
			VariableType.MEMBRANE,
			VariableType.UNKNOWN,
			VariableType.POINT_VARIABLE,
			VariableType.NONSPATIAL,
			VariableType.CONTOUR,
			VariableType.CONTOUR_REGION
	));


	public N5Exporter(ExportServiceImpl exportServiceImpl) {
	this.exportServiceImpl = exportServiceImpl;
}

	private ExportOutput exportToN5(OutputContext outputContext, long jobID, N5Specs n5Specs, ExportSpecs exportSpecs, FileDataContainerManager fileDataContainerManager) throws Exception {
		double[] allTimes = dataServer.getDataSetTimes(user, vcDataID);
		TimeSpecs timeSpecs = exportSpecs.getTimeSpecs();
		String[] variableNames = exportSpecs.getVariableSpecs().getVariableNames();
		// output context expects a list of annotated functions, vcData seems to already have a set of annotated functions


		int numVariables = variableNames.length + 1; //the extra variable length is for the mask generated for every  N5 Export
		CartesianMesh mesh = dataServer.getMesh(user, vcDataID);
		int numTimes = timeSpecs.getEndTimeIndex() - timeSpecs.getBeginTimeIndex(); //end index is an actual index within the array and not representative of length
		long[] dimensions = {mesh.getSizeX(), mesh.getSizeY(), numVariables, mesh.getSizeZ(), numTimes + 1};
		// 51X, 51Y, 1Z, 1C, 2T
		int[] blockSize = {mesh.getSizeX(), mesh.getSizeY(), 1, mesh.getSizeZ(), 1};

		double[] mask = new double[mesh.getSizeX()*mesh.getSizeY()*mesh.getSizeZ()];
		for (int i =0; i < mesh.getSizeX() * mesh.getSizeY() * mesh.getSizeZ(); i++){
			mask[i] = (double) mesh.getSubVolumeFromVolumeIndex(i);
		}

		for (String variableName: variableNames){
			DataIdentifier specie = getSpecificDI(variableName, outputContext);
			if (specie != null){
				if (unsupportedTypes.contains(specie.getVariableType())){
					throw new RuntimeException("Tried to export a variable type that is not supported!");
				} else if (specie.getVariableType().equals(VariableType.POSTPROCESSING)) {
					File hdf5File = dataServer.getVCellSimFiles(vcDataID.getOwner(), vcDataID).postprocessingFile;
					Hdf5DataProcessingReaderPure hdf5DataProcessingReaderPure = new Hdf5DataProcessingReaderPure();
					DataOperationResults.DataProcessingOutputInfo dataProcessingOutputInfo =
							hdf5DataProcessingReaderPure.getDataProcessingOutput(new DataOperation.DataProcessingOutputInfoOP(vcDataID,false, outputContext), hdf5File);

					ISize iSize = dataProcessingOutputInfo.getVariableISize(variableName);
					dimensions = new long[]{iSize.getX(), iSize.getY(), numVariables, iSize.getZ(), numTimes + 1};
					blockSize = new int[]{iSize.getX(), iSize.getY(), 1, iSize.getZ(), 1};
                }
			}
		}


		// rewrite so that it still results in a tmp file does not raise File already exists error
		N5FSWriter n5FSWriter = new N5FSWriter(getN5FileAbsolutePath(), new GsonBuilder());
		DatasetAttributes datasetAttributes = new DatasetAttributes(dimensions, blockSize, org.janelia.saalfeldlab.n5.DataType.FLOAT64, n5Specs.getCompression());
		String dataSetName = n5Specs.dataSetName;
		n5FSWriter.createDataset(dataSetName, datasetAttributes);
		N5Specs.writeImageJMetaData(dimensions, blockSize, n5Specs.getCompression(),n5FSWriter, dataSetName, numVariables, blockSize[3], allTimes.length, exportSpecs.getHumanReadableExportData().subVolume);

		//Create mask
		for(int timeIndex = timeSpecs.getBeginTimeIndex(); timeIndex <= timeSpecs.getEndTimeIndex(); timeIndex++){
			int normalizedTimeIndex = timeIndex - timeSpecs.getBeginTimeIndex();
			DoubleArrayDataBlock doubleArrayDataBlock = new DoubleArrayDataBlock(blockSize, new long[]{0, 0, 0, 0, normalizedTimeIndex}, mask);
			n5FSWriter.writeBlock(dataSetName, datasetAttributes, doubleArrayDataBlock);
		}

		for (int variableIndex=1; variableIndex < numVariables; variableIndex++){
			for (int timeIndex=timeSpecs.getBeginTimeIndex(); timeIndex <= timeSpecs.getEndTimeIndex(); timeIndex++){

				int normalizedTimeIndex = timeIndex - timeSpecs.getBeginTimeIndex();
				double[] data = this.dataServer.getSimDataBlock(outputContext, user, this.vcDataID, variableNames[variableIndex - 1], allTimes[timeIndex]).getData();
				DoubleArrayDataBlock doubleArrayDataBlock = new DoubleArrayDataBlock(blockSize, new long[]{0, 0, variableIndex, 0, (normalizedTimeIndex)}, data);
				n5FSWriter.writeBlock(dataSetName, datasetAttributes, doubleArrayDataBlock);
				if(timeIndex % 3 == 0){
					double progress = (double) (variableIndex + normalizedTimeIndex) / (numVariables + (numTimes * numVariables));
					exportServiceImpl.fireExportProgress(jobID, vcDataID, N5Specs.n5Suffix.toUpperCase(), progress);
				}
			}
		}
		n5FSWriter.close();
		ExportOutput exportOutput = new ExportOutput(true, "." + N5Specs.n5Suffix, vcDataID.getID(), getN5FileNameHash(), fileDataContainerManager);
		return exportOutput;
	}

	public void initalizeDataControllers(User user, DataServerImpl dataServer, VCSimulationDataIdentifier vcSimulationDataIdentifier) throws IOException, DataAccessException {
		this.user = user;
		this.vcDataID = vcSimulationDataIdentifier;
		this.dataServer = dataServer;
	}


	public VCSimulationDataIdentifier getVcDataID(){return vcDataID;}

	public DataIdentifier getSpecificDI(String diName, OutputContext outputContext) throws IOException, DataAccessException {
		ArrayList<DataIdentifier> list = new ArrayList<>(Arrays.asList(dataServer.getDataIdentifiers(outputContext, user, vcDataID)));
		for(DataIdentifier dataIdentifier: list){
			if(dataIdentifier.getName().equals(diName)){
				list.remove(dataIdentifier);
				return dataIdentifier;
			}
		}
		return null;
	}

	public String getN5FileAbsolutePath(){
		File outPutDir = new File(PropertyLoader.getRequiredProperty(PropertyLoader.n5DataDir) + "/" + getN5FilePathSuffix());
		return outPutDir.getAbsolutePath();
	}

	public String getN5FilePathSuffix(){
		return n5BucketName + "/" + user.getName() + "/" + this.getN5FileNameHash() + "." + N5Specs.n5Suffix;
	}

	public String getN5FileNameHash(){
		return actualHash(vcDataID.getDataKey().toString(), String.valueOf(vcDataID.getJobIndex()));
	}

	public static String getN5FileNameHash(String simID, String jobID){
		return actualHash(simID, jobID);
	}

	private static String actualHash(String simID, String jobID) {
		MessageDigest sha256 = DigestUtils.getMd5Digest();
		sha256.update(simID.getBytes(StandardCharsets.UTF_8));
//		sha256.update(jobID.getBytes(StandardCharsets.UTF_8));

		return Hex.encodeHexString(sha256.digest());
	}


/**
 * This method was created in VisualAge.
 * @throws IOException
 */
	public ExportOutput makeN5Data(OutputContext outputContext, JobRequest jobRequest, ExportSpecs exportSpecs, FileDataContainerManager fileDataContainerManager)
			throws Exception {
		FormatSpecificSpecs formatSpecs = exportSpecs.getFormatSpecificSpecs( );
		if (formatSpecs instanceof N5Specs n5Specs){
			return exportToN5(
					outputContext,
					jobRequest.getJobID(),
					n5Specs,
					exportSpecs,
					fileDataContainerManager
			);
		}
		else {
			throw new IllegalArgumentException("Export spec " + ExecutionTrace.justClassName(formatSpecs) + " not instance of " + ExecutionTrace.justClassName(N5Specs.class));
		}
	}

	public ExportOutput makeN5Data(OutputContext outputContext, long jobID, ExportSpecs exportSpecs, FileDataContainerManager fileDataContainerManager)
			throws Exception {
		FormatSpecificSpecs formatSpecs = exportSpecs.getFormatSpecificSpecs( );
		if (formatSpecs instanceof N5Specs n5Specs) {
			return exportToN5(
					outputContext,
					jobID,
					n5Specs,
					exportSpecs,
					fileDataContainerManager
			);
		} else {
			throw new IllegalArgumentException("Export spec " + ExecutionTrace.justClassName(formatSpecs) + " not instance of " + ExecutionTrace.justClassName(N5Specs.class));
		}
	}

/**
 * manage sending progress percent info for {@link N5Exporter#exportParticleData(OutputContext, long, User, DataServerImpl, ExportSpecs, ASCIISpecs, FileDataContainerManager)
 *
 */

}
