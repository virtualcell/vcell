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

import cbit.vcell.math.MathException;
import cbit.vcell.math.VariableType;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.simdata.*;
import cbit.vcell.solver.AnnotatedFunction;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationIdentifier;
import com.google.gson.GsonBuilder;
import edu.uchc.connjur.wb.ExecutionTrace;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.janelia.saalfeldlab.n5.DatasetAttributes;
import org.janelia.saalfeldlab.n5.DoubleArrayDataBlock;
import org.janelia.saalfeldlab.n5.N5FSWriter;
import org.vcell.util.*;
import org.vcell.util.document.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;


public class N5Exporter implements ExportConstants {
	private final static Logger lg = LogManager.getLogger(N5Exporter.class);

	private ExportServiceImpl exportServiceImpl = null;

	private DataSetControllerImpl dataSetController;

	private VCSimulationDataIdentifier vcDataID;
	public String n5BucketName = "n5Data";

	public static final ArrayList<VariableType> unsupportedTypes = new ArrayList<>(Arrays.asList(
			VariableType.MEMBRANE,
			VariableType.VOLUME_REGION,
			VariableType.MEMBRANE_REGION,
			VariableType.UNKNOWN,
			VariableType.POINT_VARIABLE,
			VariableType.NONSPATIAL,
			VariableType.CONTOUR,
			VariableType.CONTOUR_REGION,
			VariableType.POSTPROCESSING
	));

	private VCData vcData;


	public N5Exporter(ExportServiceImpl exportServiceImpl) {
	this.exportServiceImpl = exportServiceImpl;
}

	private ExportOutput exportToN5(OutputContext outputContext, long jobID, N5Specs n5Specs, ExportSpecs exportSpecs, FileDataContainerManager fileDataContainerManager) throws MathException, DataAccessException, IOException {
		double[] allTimes = vcData.getDataTimes();
		// output context expects a list of annotated functions, vcData seems to already have a set of annotated functions

		// With Dex's dataset ID get the data block during the first time instance t

		// All variables can be visually represented, its just that some have its data already computed, and others are derived
		// from that data, so if we want to visualize that data we need to compute the results.

		// according to Jim what should happen is that the VCdata should automatically derive the data for me since it knows everything
		// and the only reason why this wouldn't work is due to some misconfiguration, but what is that misconfig

		ArrayList<DataIdentifier> species = new ArrayList<>();
		for (String specie: exportSpecs.getVariableSpecs().getVariableNames()){
			species.add(getSpecificDI(specie));
		}


		for (DataIdentifier specie: species) {
			if (unsupportedTypes.contains(specie.getVariableType())) {
				throw new RuntimeException("Tried to export a variable type that is not supported!");
			}
		}
	//        DexDataIdentifier.getVariableType();

		int numVariables = species.size();
		int numTimes = exportSpecs.getTimeSpecs().getEndTimeIndex() - exportSpecs.getTimeSpecs().getBeginTimeIndex();
		long[] dimensions = {vcData.getMesh().getSizeX(), vcData.getMesh().getSizeY(), numVariables, vcData.getMesh().getSizeZ(), numTimes};
		// 51X, 51Y, 1Z, 1C, 2T
		int[] blockSize = {vcData.getMesh().getSizeX(), vcData.getMesh().getSizeY(), 1, vcData.getMesh().getSizeZ(), 1};


		// rewrite so that it still results in a tmp file does not raise File already exists error
		N5FSWriter n5FSWriter = new N5FSWriter(getN5FileAbsolutePath(), new GsonBuilder());
		DatasetAttributes datasetAttributes = new DatasetAttributes(dimensions, blockSize, org.janelia.saalfeldlab.n5.DataType.FLOAT64, n5Specs.getCompression());
		HashMap<String, Object> additionalMetaData = new HashMap<>();

		String dataSetName = getN5DataSetTemplatedName(n5Specs.dataSetName);

		n5FSWriter.createDataset(dataSetName, datasetAttributes);
		N5Specs.imageJMetaData(n5FSWriter, dataSetName, vcData, numVariables, additionalMetaData);


		for (int variableIndex=0; variableIndex < numVariables; variableIndex++){
			//place to add tracking, each variable can be measured in tracking
			double varFrac = (double) variableIndex / numVariables;
			for (int timeIndex=exportSpecs.getTimeSpecs().getBeginTimeIndex(); timeIndex < exportSpecs.getTimeSpecs().getEndTimeIndex(); timeIndex++){
				//another place to add tracking, each time index can be used to determine how much has been exported
				// data does get returned, but it does not seem to cover the entire region of space, but only returns regions where there is activity
				double[] data = this.dataSetController.getSimDataBlock(outputContext, this.vcDataID, species.get(variableIndex).getName(), allTimes[timeIndex]).getData();
				DoubleArrayDataBlock doubleArrayDataBlock = new DoubleArrayDataBlock(blockSize, new long[]{0, 0, variableIndex, 0, timeIndex}, data);
				n5FSWriter.writeBlock(dataSetName, datasetAttributes, doubleArrayDataBlock);
				if(timeIndex % 5 == 0){
					double timeFrac = (double) timeIndex / exportSpecs.getTimeSpecs().getEndTimeIndex();
					double progress = (varFrac + timeFrac) / (numVariables + exportSpecs.getTimeSpecs().getEndTimeIndex());
					exportServiceImpl.fireExportProgress(jobID, vcDataID, "N5", progress);
				}
			}
		}
		n5FSWriter.close();
		ExportOutput exportOutput = new ExportOutput(true, ".n5", vcDataID.getID(), getN5FileNameHash(), fileDataContainerManager);
		return exportOutput;
	}

	public void initalizeDataControllers(String simKeyID, String userName, String userKey, long jobIndex) throws IOException, DataAccessException {
		// Set my user ID associated with VC database
		// set simulation key
		// make an object that ties the user to that simulation key
		// make an object that then identifies the simulation
		User user = new User(userName, new KeyValue(userKey));

		KeyValue simKey = new KeyValue(simKeyID);
		VCSimulationIdentifier vcSimID = new VCSimulationIdentifier(simKey, user);
		this.vcDataID = new VCSimulationDataIdentifier(vcSimID, (int)jobIndex);

		// Point a data controller to the directory where the sim data is and use the vcdID to retrieve information regarding the sim, need to ask about what size this should be
		Cachetable cachetable = new Cachetable(10 * Cachetable.minute, Long.parseLong(PropertyLoader.getRequiredProperty(PropertyLoader.simdataCacheSizeProperty)));
		File primaryDir = new File(PropertyLoader.getRequiredProperty(PropertyLoader.primarySimDataDirInternalProperty));
		File secodaryDir = new File(PropertyLoader.getRequiredProperty(PropertyLoader.secondarySimDataDirInternalProperty));
		this.dataSetController = new DataSetControllerImpl(cachetable, primaryDir, secodaryDir);

		// get dataset identifier from the simulation

		this.vcData = this.dataSetController.getVCData(vcDataID);
	}

	public void initalizeDataControllers(){

	}

	public VCData getVCData(){
		return vcData;
	}

	public VCSimulationDataIdentifier getVcDataID(){return vcDataID;}

	public DataSetControllerImpl getDataSetController() {
		return dataSetController;
	}



	public DataIdentifier getRandomDI() throws IOException, DataAccessException {
		ArrayList<DataIdentifier> list = new ArrayList<>(Arrays.asList(dataSetController.getDataIdentifiers(new OutputContext(new AnnotatedFunction[0]), vcDataID)));
		Random random = new Random();
		DataIdentifier df = list.remove(random.nextInt(list.size()));
		while (unsupportedTypes.contains(df.getVariableType())){
			df = list.remove(random.nextInt(list.size()));
		}
		return df;
	}

	public DataIdentifier getSpecificDI(String diName) throws IOException, DataAccessException {
		ArrayList<DataIdentifier> list = new ArrayList<>(Arrays.asList(dataSetController.getDataIdentifiers(new OutputContext(new AnnotatedFunction[0]), vcDataID)));
		for(DataIdentifier dataIdentifier: list){
			if(dataIdentifier.getName().equals(diName)){
				list.remove(dataIdentifier);
				return dataIdentifier;
			}
		}
		return null;
	}

	public ArrayList<String> getSupportedSpecies() throws IOException, DataAccessException {
		OutputContext outputContext = new OutputContext(new AnnotatedFunction[0]);
		DataIdentifier[] dataSetIdentifiers = this.vcData.getVarAndFunctionDataIdentifiers(outputContext);

		ArrayList<String> supportedSpecies = new ArrayList<>();

		for(DataIdentifier specie: dataSetIdentifiers){
			if(!unsupportedTypes.contains(specie.getVariableType())){
				supportedSpecies.add(specie.getName());
			}
		}

		return supportedSpecies;
	}

	public String getN5FileAbsolutePath(){
		File outPutDir = new File(PropertyLoader.getRequiredProperty(PropertyLoader.n5DataDir) + "/" + n5BucketName + "/" + this.getN5FileNameHash() + ".n5");
		return outPutDir.getAbsolutePath();
	}

	public String getN5FileNameHash(){
		return actualHash(vcDataID.getDataKey().toString(), String.valueOf(vcDataID.getJobIndex()));
	}

	public String getN5DataSetTemplatedName(String nonTemplatedName){
		return nonTemplatedName + "_" + vcDataID.getJobIndex();
	}

	public static String getN5FileNameHash(String simID, String jobID){
		return actualHash(simID, jobID);
	}

	private static String actualHash(String simID, String jobID) {
		MessageDigest sha256 = DigestUtils.getSha256Digest();
		sha256.update(simID.getBytes(StandardCharsets.UTF_8));
//		sha256.update(jobID.getBytes(StandardCharsets.UTF_8));

		return Hex.encodeHexString(sha256.digest());
	}


/**
 * This method was created in VisualAge.
 * @throws IOException
 */
	public ExportOutput makeN5Data(OutputContext outputContext, JobRequest jobRequest, ExportSpecs exportSpecs, FileDataContainerManager fileDataContainerManager)
			throws DataAccessException, IOException, MathException {
		FormatSpecificSpecs formatSpecs = exportSpecs.getFormatSpecificSpecs( );
		N5Specs n5Specs = (N5Specs) formatSpecs;
		if (n5Specs != null) {
				return exportToN5(
						outputContext,
						jobRequest.getJobID(),
						n5Specs,
						exportSpecs,
						fileDataContainerManager
						);
		}
		throw new IllegalArgumentException("Export spec "  + ExecutionTrace.justClassName(formatSpecs) + " not instance of " + ExecutionTrace.justClassName(ASCIISpecs.class) );
	}

	public ExportOutput makeN5Data(OutputContext outputContext, long jobID, ExportSpecs exportSpecs, FileDataContainerManager fileDataContainerManager)
			throws DataAccessException, IOException, MathException {
		FormatSpecificSpecs formatSpecs = exportSpecs.getFormatSpecificSpecs( );
		N5Specs n5Specs = (N5Specs) formatSpecs;
		if (n5Specs != null) {
			return exportToN5(
					outputContext,
					jobID,
					n5Specs,
					exportSpecs,
					fileDataContainerManager
			);
		}
		throw new IllegalArgumentException("Export spec "  + ExecutionTrace.justClassName(formatSpecs) + " not instance of " + ExecutionTrace.justClassName(ASCIISpecs.class) );
	}

/**
 * manage sending progress percent info for {@link N5Exporter#exportParticleData(OutputContext, long, User, DataServerImpl, ExportSpecs, ASCIISpecs, FileDataContainerManager)
 *
 */

}
