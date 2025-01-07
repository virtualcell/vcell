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

import cbit.vcell.export.MeshToImage;
import cbit.vcell.math.VariableType;
import cbit.vcell.model.ModelUnitSystem;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.simdata.*;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solvers.CartesianMesh;
import cbit.vcell.units.VCUnitDefinition;
import com.google.gson.GsonBuilder;
import edu.uchc.connjur.wb.ExecutionTrace;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

	private final DataServerImpl dataServer;

	private final VCSimulationDataIdentifier vcDataID;
	public String n5BucketName = "n5Data";

	private final User user;

	public static final ArrayList<VariableType> unsupportedTypes = new ArrayList<>(Arrays.asList(
			VariableType.MEMBRANE,
			VariableType.UNKNOWN,
			VariableType.POINT_VARIABLE,
			VariableType.NONSPATIAL,
			VariableType.CONTOUR,
			VariableType.CONTOUR_REGION,
			VariableType.MEMBRANE_REGION,
			VariableType.VOLUME_REGION
	));


	public N5Exporter(ExportServiceImpl exportServiceImpl, User user, DataServerImpl dataServer, VCSimulationDataIdentifier vcSimulationDataIdentifier) {
		this.exportServiceImpl = exportServiceImpl;
		this.user = user;
		this.dataServer = dataServer;
		this.vcDataID = vcSimulationDataIdentifier;
	}

	private ExportOutput exportToN5(OutputContext outputContext, long jobID, N5Specs n5Specs, ExportSpecs exportSpecs, FileDataContainerManager fileDataContainerManager) throws Exception {
		VCUnitDefinition lengthUnit = ModelUnitSystem.createDefaultVCModelUnitSystem().getLengthUnit();
		double[] allTimes = dataServer.getDataSetTimes(user, vcDataID);
		TimeSpecs timeSpecs = exportSpecs.getTimeSpecs();
		String[] variableNames = exportSpecs.getVariableSpecs().getVariableNames();
		// output context expects a list of annotated functions, vcData seems to already have a set of annotated functions


		CartesianMesh mesh = dataServer.getMesh(user, vcDataID);
		int sizeX = MeshToImage.newNumElements(mesh.getSizeX()), sizeY = MeshToImage.newNumElements(mesh.getSizeY()), sizeZ = MeshToImage.newNumElements(mesh.getSizeZ());
		double lengthPerPixelX = mesh.getExtent().getX() / sizeX,
				lengthPerPixelY = mesh.getExtent().getY() / sizeY,
				lengthPerPixelZ = mesh.getExtent().getZ() / sizeZ;
		int numVariables = variableNames.length + 1; //the extra variable length is for the mask generated for every  N5 Export
		int numTimes = timeSpecs.getEndTimeIndex() - timeSpecs.getBeginTimeIndex(); //end index is an actual index within the array and not representative of length
		long[] dimensions = {sizeX, sizeY, numVariables, sizeZ, numTimes + 1};
		int[] blockSize = {sizeX, sizeY, 1, 1, 1};

		// rewrite so that it still results in a tmp file does not raise File already exists error
		N5FSWriter n5FSWriter = new N5FSWriter(getN5FileSystemPath(), new GsonBuilder());
		DatasetAttributes datasetAttributes = new DatasetAttributes(dimensions, blockSize, org.janelia.saalfeldlab.n5.DataType.FLOAT64, n5Specs.getCompression());
		n5FSWriter.createDataset(String.valueOf(jobID), datasetAttributes);


		//Create mask
		double[] mask = new double[mesh.getSizeX()*mesh.getSizeY()*mesh.getSizeZ()];
		for (int i =0; i < mesh.getSizeX() * mesh.getSizeY() * mesh.getSizeZ(); i++){
			mask[i] = (double) mesh.getSubVolumeFromVolumeIndex(i);
		}

		HashMap<Integer, Object> channelInfo = new HashMap<>();

		boolean containsPostProcessed = containsPostProcessedVariable(variableNames, outputContext);

		if (containsPostProcessed){
			for (String variableName : variableNames){
				if (getSpecificDI(variableName, outputContext).getVariableType().compareEqual(VariableType.POSTPROCESSING)){
					File hdf5File = dataServer.getVCellSimFiles(vcDataID.getOwner(), vcDataID).postprocessingFile;
					Hdf5DataProcessingReaderPure hdf5DataProcessingReaderPure = new Hdf5DataProcessingReaderPure();
					DataOperationResults.DataProcessingOutputInfo dataProcessingOutputInfo =
							hdf5DataProcessingReaderPure.getDataProcessingOutput(new DataOperation.DataProcessingOutputInfoOP(vcDataID,false, outputContext), hdf5File);

					ISize iSize = dataProcessingOutputInfo.getVariableISize(variableName);
					lengthPerPixelX = mesh.getExtent().getX() / iSize.getX(); lengthPerPixelY = mesh.getExtent().getY() / iSize.getY(); lengthPerPixelZ = mesh.getExtent().getZ() / iSize.getZ();
					sizeX = iSize.getX(); sizeY = iSize.getY(); sizeZ = iSize.getZ();
					dimensions = new long[]{sizeX, sizeY, numVariables, sizeZ, numTimes + 1};
					blockSize = new int[]{sizeX, sizeY, 1, 1, 1};
				} else {
					throw new RuntimeException("All variable types must be of POST-PROCESSING if you want to export a post-processed variable.");
				}
			}
		} else {
			for (int i = 0; i < variableNames.length; i++){
				String variableName = variableNames[i];
				DataIdentifier specie = getSpecificDI(variableName, outputContext);
				HashMap<String, Object> variableInfo = new HashMap<>();
				variableInfo.put("Name", variableName);
				variableInfo.put("Domain", specie.getDomain().getName());
				channelInfo.put(i, variableInfo);
			}
			mask = MeshToImage.convertMeshIntoImage(mask, mesh).data();
		}


		N5Specs.writeImageJMetaData(jobID, dimensions, blockSize, n5Specs.getCompression(), n5FSWriter,
				n5Specs.dataSetName, numVariables, sizeZ, allTimes.length,
				exportSpecs.getHumanReadableExportData().subVolume,
				lengthPerPixelY, lengthPerPixelX, lengthPerPixelZ, lengthUnit.getSymbol(), channelInfo);

		int timeLoops = 1;
		double progress = 0;

		for (int variableIndex=0; variableIndex < (numVariables -1); variableIndex++){
			for (int timeIndex=timeSpecs.getBeginTimeIndex(); timeIndex <= timeSpecs.getEndTimeIndex(); timeIndex++){
				int normalizedTimeIndex = timeIndex - timeSpecs.getBeginTimeIndex();

				double[] data = this.dataServer.getSimDataBlock(outputContext, user, this.vcDataID, variableNames[variableIndex], allTimes[timeIndex]).getData();
				data = containsPostProcessed ? data : MeshToImage.convertMeshIntoImage(data, mesh).data();
				for (int z=0; z < sizeZ; z++){
					double[] dataToWrite = MeshToImage.getXYFromXYZArray(data, sizeX, sizeY, z);
					DoubleArrayDataBlock doubleArrayDataBlock = new DoubleArrayDataBlock(blockSize, new long[]{0, 0, variableIndex, z, (normalizedTimeIndex)}, dataToWrite);
					n5FSWriter.writeBlock(String.valueOf(jobID), datasetAttributes, doubleArrayDataBlock);
				}


				if(timeIndex % 2 == 0){
					progress = (double) (variableIndex + timeLoops) / (numVariables + numTimes + numTimes);
					exportServiceImpl.fireExportProgress(jobID, vcDataID, N5Specs.n5Suffix.toUpperCase(), progress);
				}
				timeLoops += 1;
			}
		}

		// write mask
		for(int timeIndex = timeSpecs.getBeginTimeIndex(); timeIndex <= timeSpecs.getEndTimeIndex(); timeIndex++){
			int normalizedTimeIndex = timeIndex - timeSpecs.getBeginTimeIndex();

			for (int z = 0; z < sizeZ; z++){
				double[] writeData = MeshToImage.getXYFromXYZArray(mask, sizeX, sizeY, z);
				DoubleArrayDataBlock doubleArrayDataBlock = new DoubleArrayDataBlock(blockSize, new long[]{0, 0, (numVariables - 1), z, normalizedTimeIndex}, writeData);
				n5FSWriter.writeBlock(String.valueOf(jobID), datasetAttributes, doubleArrayDataBlock);
			}
			if(timeIndex % 2 == 0){
				progress = (double) (progress + timeLoops) / (numVariables + numTimes + numTimes);
				exportServiceImpl.fireExportProgress(jobID, vcDataID, N5Specs.n5Suffix.toUpperCase(), progress);
			}
			timeLoops += 1;
		}
		n5FSWriter.close();
		exportSpecs.getHumanReadableExportData().numChannels = (int) dimensions[2];
		exportSpecs.getHumanReadableExportData().zSlices = (int) dimensions[3];
		exportSpecs.getHumanReadableExportData().tSlices = (int) dimensions[4];
		ExportOutput exportOutput = new ExportOutput(true, "." + N5Specs.n5Suffix, vcDataID.getID(), getN5FileHash(), fileDataContainerManager);
		return exportOutput;
	}

	private boolean containsPostProcessedVariable(String[] variableNames, OutputContext outputContext) throws IOException, DataAccessException {
		for (String variableName: variableNames){
			DataIdentifier specie = getSpecificDI(variableName, outputContext);
			if(specie.getVariableType().compareEqual(VariableType.POSTPROCESSING)){
				return true;
			} else if (unsupportedTypes.contains(specie.getVariableType())) {
				throw new RuntimeException("Attempting to export unsupported type of: " + specie.getVariableType().getTypeName() + ". The variable with this type is: " + variableName);
			}
		}
		return false;
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

	public String getN5FileSystemPath(){
		File outPutDir = new File(PropertyLoader.getRequiredProperty(PropertyLoader.n5DataDir) + "/" + getN5BucketPath());
		return outPutDir.getAbsolutePath();
	}

	public String getN5BucketPath(){
		return n5BucketName + "/" + user.getName() + "/" + this.getN5FileHash() + "." + N5Specs.n5Suffix;
	}

	public String getN5FileHash(){
		return hashFunction(vcDataID.getDataKey().toString());
	}

	private static String hashFunction(String simID) {
		MessageDigest sha256 = DigestUtils.getMd5Digest();
		sha256.update(simID.getBytes(StandardCharsets.UTF_8));
		String hashString = Hex.encodeHexString(sha256.digest());
		return hashString.substring(17);
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
					jobRequest.getExportJobID(),
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
