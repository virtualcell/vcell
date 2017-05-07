/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.microscopy;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.media.jai.operator.FileStoreDescriptor;

import org.vcell.util.Extent;
import org.vcell.util.FileUtils;
import org.vcell.util.StdoutSessionLog;

import cbit.util.xml.XmlUtil;
import cbit.vcell.VirtualMicroscopy.ImageDataset;
import cbit.vcell.VirtualMicroscopy.ImageDatasetReaderFactory;
import cbit.vcell.VirtualMicroscopy.ROI;
import cbit.vcell.VirtualMicroscopy.UShortImage;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.field.io.FieldDataFileOperationSpec;
import cbit.vcell.microscopy.server.FrapDataUtils;
import cbit.vcell.modelopt.DataReference;
import cbit.vcell.modelopt.DataSource;
import cbit.vcell.modelopt.gui.MultisourcePlotListModel;
import cbit.vcell.opt.Parameter;
import cbit.vcell.opt.ReferenceData;
import cbit.vcell.simdata.PDEDataManager;
import cbit.vcell.simdata.VCDataManager;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.ode.ODESolverResultSet;

public class NonGUIFRAPTest {

	public static String STR_FREE_DIFF_RATE = "Free particle diffusion rate";
	public static String STR_FREE_FRACTION = "Free particle fraction";
	public static String STR_BLEACH_MONITOR_RATE = "Bleach while monitoring rate";
	public static String STR_COMPLEX_DIFF_RATE = "Complex diffusion rate";
	public static String STR_COMPLEX_FRACTION = "Complex fraction";
	public static String STR_IMMOBILE_FRACTION = "Immobile fraction";
	public static String STR_BINDING_SITE_CONCENTRATION = "Binding site concentration";
	public static String STR_ON_RATE = "Reaction on rate";
	public static String STR_OFF_RATE = "Reaction off rate";

	private static class ExternalDataFileContents{
		public ImageDataset imageData;
		public ImageDataset cellROIData;
		public ImageDataset bleachROIData;
		public ImageDataset backgroundROIData;
	};
	
//	public static void writeUShortFile(UShortImage uShortImage,OutputStream outputStream){
////		short[] shortPixels = uShortImage.getPixels();
////		byte[] convertedPixels = new byte[shortPixels.length];
////		for (int i = 0; i < convertedPixels.length; i++) {
////			convertedPixels[i] = (byte)(i%16);//(byte)(0x00FF&shortPixels[i]);
////		}
//		writeBufferedImageFile(
//			ImageTools.makeImage(uShortImage.getPixels(),uShortImage.getNumX(), uShortImage.getNumY()),outputStream);
//	}
//	public static void writeBufferedImageFile(BufferedImage bufferedImage,OutputStream outputStream){
//		try{
//		ImageIO.write(
//			FormatDescriptor.create(bufferedImage, DataBuffer.TYPE_BYTE,null).createInstance(),
//			"bmp",outputStream);
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//	}

	public static File getCanonicalFilename(String testDirectoryPath,String roiName){
		if(roiName == null){
			return new File(testDirectoryPath,"testImageData.zip");
		}else if(roiName.equals(FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name())){
			return new File(testDirectoryPath,"testCellROI.tif");
		}else if(roiName.equals(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED.name())){
			return new File(testDirectoryPath,"testBleachROI.tif");
		}else if(roiName.equals(FRAPData.VFRAP_ROI_ENUM.ROI_BACKGROUND.name())){
			return new File(testDirectoryPath,"testBackgroundROI.tif");
		}
		throw new IllegalArgumentException("unknown name "+roiName);
	}
	public static ExternalDataFileContents readExternalDataContents(
			String imageDataPathName,
			String cellROIPathName,String bleachROIPathName,
			String backgroundROIPathName) throws Exception{
		ExternalDataFileContents extDataFilecontents = new ExternalDataFileContents();
			extDataFilecontents.imageData =
				ImageDatasetReaderFactory.createImageDatasetReader().readImageDataset(imageDataPathName, null);
			extDataFilecontents.cellROIData =
				ImageDatasetReaderFactory.createImageDatasetReader().readImageDataset(cellROIPathName,null);
			extDataFilecontents.bleachROIData =
				ImageDatasetReaderFactory.createImageDatasetReader().readImageDataset(bleachROIPathName,null);
			extDataFilecontents.backgroundROIData =
				ImageDatasetReaderFactory.createImageDatasetReader().readImageDataset(backgroundROIPathName,null);

		return extDataFilecontents;
	}
	private static void checkExternalDataSameAsOriginal(FRAPStudy frapStudy,String testDirectoryPath) throws Exception{
		ImageDataset imageDataset = frapStudy.getFrapData().getImageDataset();
		ROI cellROI = frapStudy.getFrapData().getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name());
		ROI bleachROI = frapStudy.getFrapData().getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED.name());
		ROI backgroundROI = frapStudy.getFrapData().getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BACKGROUND.name());
//		FileInputStream fis = new FileInputStream(getCanonicalFilename(testDirectoryPath, null));
//		BufferedInputStream bis = new BufferedInputStream(fis);
//		ZipInputStream zis = new ZipInputStream(bis);
//		ZipEntry zipEntry = null;
//		int timeIndex = 0;
//		while((zipEntry =zis.getNextEntry()) != null){
//			byte[] timePointData = new byte[(int)zipEntry.getSize()];
//			int offset = 0;
//			while((offset+= zis.read(timePointData,offset,timePointData.length-offset)) != timePointData.length){}
//			short[] shortPixels = imageDataset.getImage(0, 0, timeIndex).getPixels();
//			checkPixels(shortPixels, timePointData);
//			timeIndex++;
//		}
//		zis.close();
		ExternalDataFileContents extDataFileContents =
			readExternalDataContents(
				getCanonicalFilename(testDirectoryPath, null).getAbsolutePath(),
				getCanonicalFilename(testDirectoryPath, FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name()).getAbsolutePath(),
				getCanonicalFilename(testDirectoryPath, FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED.name()).getAbsolutePath(),
				getCanonicalFilename(testDirectoryPath, FRAPData.VFRAP_ROI_ENUM.ROI_BACKGROUND.name()).getAbsolutePath());
		ImageDataset imageData = extDataFileContents.imageData;
		UShortImage[] allIDiskmages = imageData.getAllImages();
		UShortImage[] allIFrapmages = imageDataset.getAllImages();
		if(allIDiskmages.length != allIFrapmages.length){
			throw new Exception("different number of times");
		}
		for (int i = 0; i < allIDiskmages.length; i++) {
			short[] allDiskPixels = allIDiskmages[i].getPixels();
			short[] allFrapPixels = allIFrapmages[i].getPixels();
			checkPixels(allDiskPixels, allFrapPixels);
		}
		checkPixels(extDataFileContents.cellROIData.getPixelsZ(0, 0), cellROI.getPixelsXYZ());
		checkPixels(extDataFileContents.bleachROIData.getPixelsZ(0, 0), bleachROI.getPixelsXYZ());
		checkPixels(extDataFileContents.backgroundROIData.getPixelsZ(0, 0), backgroundROI.getPixelsXYZ());
	}
	private static void checkPixels(short[] allDiskPixels,short[] allFrapPixels) throws Exception{
		if(allDiskPixels.length != allFrapPixels.length){
			throw new Exception("different dimensions");
		}
		for (int j = 0; j < allFrapPixels.length; j++) {
//			if((int)(allDiskPixels[j]&0x0000FFFF) != (int)(allFrapPixels[j]&0x0000FFFF)){
			if(allDiskPixels[j] != allFrapPixels[j]){
				throw new Exception(
						"pixel values not equal disk="+Integer.toHexString(allDiskPixels[j]&0x0000FFFF)+
						" frapdata="+Integer.toHexString(allFrapPixels[j]&0x0000FFFF));
			}
		}
	}
	public static void runXMLFile(String xmlFileName,String testDirectoryPath) throws Exception{
		
		String xmlString = XmlUtil.getXMLString(xmlFileName);
		//System.out.println(xmlString);
		MicroscopyXmlReader xmlReader = new MicroscopyXmlReader(true);
		FRAPStudy frapStudy = xmlReader.getFrapStudy(XmlUtil.stringToXML(xmlString, null).getRootElement(),null);
		
		if(frapStudy == null || frapStudy.getFrapData() == null){
			throw new Exception("no FrapData in file "+xmlFileName);
		}
		if(frapStudy.getFrapData().getImageDataset().getSizeC() != 1 ||
				frapStudy.getFrapData().getImageDataset().getSizeZ() != 1){
			throw new Exception("Only single channel, single z test implemented");
		}
		
		File imageDataSetZipFile = getCanonicalFilename(testDirectoryPath,null);
		File cellROIFile = getCanonicalFilename(testDirectoryPath,FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name());
		File bleachROIFile = getCanonicalFilename(testDirectoryPath,FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED.name());
		File backgroundROIFile =getCanonicalFilename(testDirectoryPath,FRAPData.VFRAP_ROI_ENUM.ROI_BACKGROUND.name());
		
		FileOutputStream fos = new FileOutputStream(imageDataSetZipFile);
		BufferedOutputStream bos = new BufferedOutputStream(fos);
		ZipOutputStream zos = new ZipOutputStream(bos);
		ImageDataset imageDataset = frapStudy.getFrapData().getImageDataset();
		System.out.println(imageDataset.getISize());
		StringBuffer timeStampsSB = new StringBuffer();
		for (int i = 0; i < imageDataset.getSizeT(); i++) {
//			ZipEntry zipEntry = new ZipEntry((i<10?"00":(i<100?"0":""))+i+".bmp");
			ZipEntry zipEntry = new ZipEntry((i<10?"00":(i<100?"0":""))+i+".tif");
			zos.putNextEntry(zipEntry);
			UShortImage timePointImage = imageDataset.getImage(0, 0, i);
			File tempF = writeTempTiff(timePointImage.getPixels(), timePointImage.getNumX(),timePointImage.getNumY());
			FileInputStream fis = new FileInputStream(tempF);
			byte[] tempbytes = new byte[(int)tempF.length()];
			int offset = 0;
			while((offset+= fis.read(tempbytes, offset, tempbytes.length-offset)) != tempbytes.length){}
			zos.write(tempbytes);			
//			writeUShortFile(timePointImage, zos);
			timeStampsSB.append((i!=0?",":"")+imageDataset.getImageTimeStamps()[i]);
			zos.closeEntry();
			fis.close();
			tempF.delete();
		}
		zos.close();
		fos.close();
		
//		fos = new FileOutputStream(cellROIFile);
		ROI cellROI = frapStudy.getFrapData().getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name());
		File cellTempF =
			writeTempTiff(cellROI.getRoiImages()[0].getPixels(),
				cellROI.getRoiImages()[0].getNumX(), cellROI.getRoiImages()[0].getNumY());
		FileUtils.copyFile(cellTempF, cellROIFile);
		cellTempF.delete();
//		writeUShortFile(cellROI.getRoiImages()[0], fos);
//		fos.close();
		
//		fos = new FileOutputStream(bleachROIFile);
		ROI bleachROI = frapStudy.getFrapData().getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED.name());
		File bleachTempf =
			writeTempTiff(bleachROI.getRoiImages()[0].getPixels(),
				bleachROI.getRoiImages()[0].getNumX(), bleachROI.getRoiImages()[0].getNumY());
		FileUtils.copyFile(bleachTempf, bleachROIFile);
		bleachTempf.delete();
//		writeUShortFile(bleachROI.getRoiImages()[0],fos);
//		fos.close();
		
//		fos = new FileOutputStream(backgroundROIFile);
		ROI backgroundROI = frapStudy.getFrapData().getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BACKGROUND.name());
		File backgroundTempf =
			writeTempTiff(backgroundROI.getRoiImages()[0].getPixels(),
					backgroundROI.getRoiImages()[0].getNumX(), backgroundROI.getRoiImages()[0].getNumY());
		FileUtils.copyFile(backgroundTempf, backgroundROIFile);
		backgroundTempf.delete();
//		writeUShortFile(backgroundROI.getRoiImages()[0],fos);
//		fos.close();
		
		checkExternalDataSameAsOriginal(frapStudy,testDirectoryPath);
		
//		if(true){System.exit(0);}
		
		String[] args =
			new String[]{
//				frapStudy.getFrapModelParameters().getIniModelParameters().startingIndexForRecovery,
//				frapStudy.getFrapModelParameters().getIniModelParameters().diffusionRate,
//				frapStudy.getFrapModelParameters().getIniModelParameters().monitorBleachRate,
//				frapStudy.getFrapModelParameters().getIniModelParameters().mobileFraction,
//				frapStudy.getFrapModelParameters().getPureDiffModelParameters().secondaryDiffusionRate,
//				frapStudy.getFrapModelParameters().getPureDiffModelParameters().secondaryMobileFraction,
				testDirectoryPath,
				imageDataSetZipFile.getAbsolutePath(),
				cellROIFile.getAbsolutePath(),
				bleachROIFile.getAbsolutePath(),
				backgroundROIFile.getAbsolutePath(),
				new File(testDirectoryPath,"testCreated.xml").getAbsolutePath(),
				timeStampsSB.toString(),
				imageDataset.getExtent().getX()+","+imageDataset.getExtent().getY()+","+imageDataset.getExtent().getZ()
			};
		
		NonGUIFRAPTest.runSolver(args);
	}
	

	public static File writeTempTiff(short[] shortPixels,int width,int height) throws Exception{
		File tempF = File.createTempFile("imageDataSetTest", ".tif");
		tempF.deleteOnExit();
		BufferedImage bufferedImage = UShortImage.createUnsignedBufferedImage(shortPixels, width, height);
		FileStoreDescriptor.create(bufferedImage, tempF.getAbsolutePath(), "tiff", null, true, null).getRendering();
		return tempF;
	}
	public static void main(String[] args) {
		try{
			runXMLFile(args[0], args[1]);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	/**
	 * @param args
	 */
	public static void runSolver(String[] args) throws Exception{
		String startingIndexForRecovery = args[0];
		String freeDiffusionRateStr= args[1];
		String freeMobileFractionStr = args[2];
		String complexDiffusionRateStr = args[3];
		String complexMobileFractionStr = args[4];
		String bleachWhileMonitoringRateString = args[5];
		String immobileFractionStr = args[6];
		String bindingSiteConcentrationStr = args[7];
		String reacOnRateStr = args[8];
		String reacOffRateStr  = args[9];
		String workingDirectoryPath = args[10];
		String inputFRAPDataFileName = args[11];
		String inputCellROIFileName = args[12];
		String inputBleachROIFileName = args[13];
		String inputBackgroundROIFileName = args[14];
		String outputXMLFileName = args[15];
		String commaSepTimeStamps = args[16];
		String commaSepExtentXYZ = args[17];
		
		LocalWorkspace localWorkspace =
			new LocalWorkspace(new File(workingDirectoryPath));
		
		ExternalDataFileContents extDataFileContents =
			readExternalDataContents(
				inputFRAPDataFileName, inputCellROIFileName,
				inputBleachROIFileName, inputBackgroundROIFileName);
		
		ROI cellROI = new ROI(extDataFileContents.cellROIData.getImage(0, 0, 0),FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name());
		ROI bleachROI = new ROI(extDataFileContents.bleachROIData.getImage(0, 0, 0),FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED.name());
		ROI backgroundROI = new ROI(extDataFileContents.backgroundROIData.getImage(0, 0, 0),FRAPData.VFRAP_ROI_ENUM.ROI_BACKGROUND.name());
		
		//Insert Time information
		double[] timeStamps = new double[extDataFileContents.imageData.getAllImages().length];
		StringTokenizer commaStringTokenizer = new StringTokenizer(commaSepTimeStamps,",");
		int timeCount = 0;
		while(commaStringTokenizer.hasMoreTokens()){
			timeStamps[timeCount] = Double.parseDouble(commaStringTokenizer.nextToken());
			timeCount++;
		}
		ImageDataset frapDataImageDataSet =
			new ImageDataset(extDataFileContents.imageData.getAllImages(),timeStamps,1);
		//Insert Extent information
		commaStringTokenizer = new StringTokenizer(commaSepExtentXYZ,",");
		Extent extent =
			new Extent(Double.parseDouble(commaStringTokenizer.nextToken()),
					Double.parseDouble(commaStringTokenizer.nextToken()),
					Double.parseDouble(commaStringTokenizer.nextToken()));
		frapDataImageDataSet.setExtent(extent);
		bleachROI.getRoiImages()[0].setExtent(extent);
		cellROI.getRoiImages()[0].setExtent(extent);
		backgroundROI.getRoiImages()[0].setExtent(extent);
		
		FRAPData frapData = FrapDataUtils.importFRAPDataFromImageDataSet(frapDataImageDataSet);
		frapData.addReplaceRoi(bleachROI);
		frapData.addReplaceRoi(cellROI);
		frapData.addReplaceRoi(backgroundROI);	

		FRAPStudy frapStudy = new FRAPStudy();
		frapStudy.setFrapData(frapData);
		
		//old model parameters, need to rewrite
//		FRAPStudy.FRAPModelParameters frapModelParameters = new FRAPStudy.FRAPModelParameters( 
//				  new FRAPStudy.InitialModelParameters(freeDiffusionRateStr, freeMobileFractionStr, bleachWhileMonitoringRateString, startingIndexForRecovery),
//				  null,
//				  null);
		
//		frapStudy.setFrapModelParameters(frapModelParameters);
		frapStudy.refreshDependentROIs();
		
		ExternalDataInfo imageDatasetExternalDataInfo = FRAPStudy.createNewExternalDataInfo(localWorkspace, FRAPStudy.IMAGE_EXTDATA_NAME);
		ExternalDataInfo roiExternalDataInfo = FRAPStudy.createNewExternalDataInfo(localWorkspace, FRAPStudy.ROI_EXTDATA_NAME);
		frapStudy.setFrapDataExternalDataInfo(imageDatasetExternalDataInfo);
		frapStudy.setRoiExternalDataInfo(roiExternalDataInfo);
		
		frapStudy.saveImageDatasetAsExternalData(
				localWorkspace,frapStudy.getFrapDataExternalDataInfo().getExternalDataIdentifier(),
				frapStudy.getStartingIndexForRecovery());
		frapStudy.saveROIsAsExternalData(
				localWorkspace, frapStudy.getRoiExternalDataInfo().getExternalDataIdentifier(),
				frapStudy.getStartingIndexForRecovery());

//		Double bleachWhileMonitoringRate =
//			(!bleachWhileMonitoringRateString.equals("-")
//				?Double.parseDouble(bleachWhileMonitoringRateString)
//				:null);
		
		double fd,ff,bwmr,cd,cf,imf,bs,on,off;
		try
		{
			fd = Double.parseDouble(freeDiffusionRateStr);
			ff = Double.parseDouble(freeMobileFractionStr);
			bwmr = Double.parseDouble(bleachWhileMonitoringRateString);
			cd = Double.parseDouble(complexDiffusionRateStr);
			cf = Double.parseDouble(complexMobileFractionStr);
			imf = Double.parseDouble(immobileFractionStr);
			bs = Double.parseDouble(bindingSiteConcentrationStr);
			on = Double.parseDouble(reacOnRateStr);
			off = Double.parseDouble(reacOffRateStr);
			
		}catch(NumberFormatException e)
		{
			throw new Exception("Input parameters are not all valid. Check if they are empty or in illegal forms.");
		}
		

		BioModel bioModel =
			FRAPStudy.createNewSimBioModel(
				frapStudy,
				createParameterArray(fd, ff, bwmr, cd, cf, imf, bs, on, off),
				null,
				LocalWorkspace.createNewKeyValue(),
				LocalWorkspace.getDefaultOwner(),
				new Integer(frapStudy.getStartingIndexForRecovery()));
		frapStudy.setBioModel(bioModel);
		MicroscopyXmlproducer.writeXMLFile(frapStudy, new File(outputXMLFileName), true, null,false);//no progress listener, need to change
		FRAPStudy.runFVSolverStandalone(
			new File(localWorkspace.getDefaultSimDataDirectory()),
			new StdoutSessionLog(LocalWorkspace.getDefaultOwner().getName()),
			bioModel.getSimulation(0),
			imageDatasetExternalDataInfo.getExternalDataIdentifier(),
			roiExternalDataInfo.getExternalDataIdentifier(),
			null);
		
		VCSimulationDataIdentifier vcSimulationDataIdentifier =
			new VCSimulationDataIdentifier(
				bioModel.getSimulations()[0].getSimulationInfo().getAuthoritativeVCSimulationIdentifier(),
				FieldDataFileOperationSpec.JOBINDEX_DEFAULT);
		PDEDataManager simulationDataManager = new PDEDataManager(null, localWorkspace.getVCDataManager(),vcSimulationDataIdentifier);
		double[] frapDataTimeStamps = frapData.getImageDataset().getImageTimeStamps();
		VCDataManager testVCDataManager = localWorkspace.getVCDataManager();
		double[] prebleachAverage = testVCDataManager.getSimDataBlock(null,frapStudy.getRoiExternalDataInfo().getExternalDataIdentifier(), "prebleach_avg", 0).getData();
		//TODO: need to create parameters here.
		Parameter[] parameters = null;
		SpatialAnalysisResults spatialAnalysisResults =
			FRAPStudy.spatialAnalysis(
				simulationDataManager,
				new Integer(frapStudy.getStartingIndexForRecovery()),
				frapDataTimeStamps[new Integer(frapStudy.getStartingIndexForRecovery())],
				parameters,
				frapData,
				prebleachAverage);
		dumpSummaryReport(spatialAnalysisResults, frapDataTimeStamps,
				new Integer(startingIndexForRecovery).intValue(),
				new File(workingDirectoryPath,"nonguiSpatialResults.txt"));
		dumpSpatialResults(spatialAnalysisResults, frapDataTimeStamps, new File(workingDirectoryPath,"nonguiSpatialResults.txt"));
		
	}
	public static void dumpSummaryReport(
			SpatialAnalysisResults spatialAnalysisResults,
			double[] frapDataTimeStamps,
			int startingIndexForRecovery,
			File outputFile) throws Exception{
		
		Object[][] summaryData =
			spatialAnalysisResults.createSummaryReportTableData(frapDataTimeStamps,startingIndexForRecovery);

		//Sort by diffusion
		Arrays.sort(summaryData,
				new Comparator<Object[]>(){
					public int compare(Object[] o1, Object[] o2) {
						return (int)Math.signum((Double)o1[0]-(Double)o2[0]);
					}
				}
		);
		String summaryReportS = SpatialAnalysisResults.createCSVSummaryReport(SpatialAnalysisResults.getSummaryReportColumnNames(), summaryData);
		FileWriter fw = null;
		try{
			fw = new FileWriter(outputFile);
			fw.write(summaryReportS);
		}finally{
			try{if(fw !=null){fw.close();}}catch(Exception e){e.printStackTrace();}
		}
		
	}
	public static void dumpSpatialResults(
			SpatialAnalysisResults spatialAnalysisResults,
			double[] frapDataTimeStamps,
			File outputFile) throws Exception{
		
		FileWriter fw = new FileWriter(outputFile);
//		FileOutputStream fos = new FileOutputStream(outputFile);
//		BufferedOutputStream bos = new BufferedOutputStream(fos);
		ReferenceData[] referenceDataArr =
			spatialAnalysisResults.createReferenceDataForAllDiffusionRates(frapDataTimeStamps);
		ODESolverResultSet[] odeSolverResultSetArr =
			spatialAnalysisResults.createODESolverResultSetForAllDiffusionRates();
		for (int i = 0; i < spatialAnalysisResults.analysisParameters.length; i++) {
			DataSource expDataSource = new DataSource.DataSourceReferenceData("experiment", referenceDataArr[i]);
			DataSource fitDataSource = new DataSource.DataSourceRowColumnResultSet("fit", odeSolverResultSetArr[i]);
			MultisourcePlotListModel multisourcePlotListModel =
				new MultisourcePlotListModel();
			multisourcePlotListModel.setDataSources(new DataSource[] {expDataSource,fitDataSource});
			System.out.println("AnalysisParameters = "+spatialAnalysisResults.analysisParameters[i]);
			for (int j = 0; j < multisourcePlotListModel.getSize(); j++) {
				DataReference dataReference = (DataReference)multisourcePlotListModel.getElementAt(j);
				DataSource dataSource = dataReference.getDataSource();
				for (int k = 0; k < dataSource.getNumRows(); k++) {
					for (int k2 = 0; k2 < dataSource.getNumColumns(); k2++) {
						System.out.print(dataSource.getRowData(k)[k2]+" ");
						fw.write(dataSource.getRowData(k)[k2]+" ");
					}
					System.out.println();
					fw.write("\n");
				}
//				if(dataReference.getDataSource().getSource() instanceof ReferenceData){
//					ReferenceData refData = (ReferenceData)dataReference.getDataSource().getSource();
//					for (int k = 0; k < refData.getNumRows(); k++) {
//						for (int k2 = 0; k2 < refData.getNumColumns(); k2++) {
//							System.out.print(refData.getRowData(k)[k2]+" ");
//							fw.write(refData.getRowData(k)[k2]+" ");
//						}
//						System.out.println();
//						fw.write("\n");
//					}
//				}else{
//					ODESolverResultSet odeRS = (ODESolverResultSet)dataReference.getDataSource().getSource();
//					for (int k = 0; k < odeRS.getRowCount(); k++) {
//						for (int k2 = 0; k2 < odeRS.getDataColumnCount(); k2++) {
//							System.out.print(odeRS.getRow(k)[k2]+" ");
//							fw.write(odeRS.getRow(k)[k2]+" ");
//						}
//						System.out.println();
//						fw.write("\n");
//					}
//				}
			}
		}
		fw.close();
	}
	
	private static Parameter[] createParameterArray(double freeDiffRate, double freeFraction, double monitorBleachRate, double complexDifffRate, double complexFraction, double immFraction, double bsConc, double onRate, double offRate)
	{
		Parameter[] params = null;
		
		Parameter freeDiff =
			new cbit.vcell.opt.Parameter(STR_FREE_DIFF_RATE,
										 FRAPModel.REF_DIFFUSION_RATE_PARAM.getLowerBound(), 
										 FRAPModel.REF_DIFFUSION_RATE_PARAM.getUpperBound(),
										 FRAPModel.REF_DIFFUSION_RATE_PARAM.getScale(),
					                     freeDiffRate);
		Parameter freeFrac =
			new cbit.vcell.opt.Parameter(STR_FREE_FRACTION,
										 FRAPModel.REF_MOBILE_FRACTION_PARAM.getLowerBound(),
										 FRAPModel.REF_MOBILE_FRACTION_PARAM.getUpperBound(),
										 FRAPModel.REF_MOBILE_FRACTION_PARAM.getScale(),
					                     freeFraction);
		Parameter bleachWhileMonitoringRate =
			new cbit.vcell.opt.Parameter(STR_BLEACH_MONITOR_RATE,
										 FRAPModel.REF_BLEACH_WHILE_MONITOR_PARAM.getLowerBound(),
										 FRAPModel.REF_BLEACH_WHILE_MONITOR_PARAM.getUpperBound(),
										 FRAPModel.REF_BLEACH_WHILE_MONITOR_PARAM.getScale(),
					                     monitorBleachRate);
		Parameter complexdiff = new Parameter(STR_COMPLEX_DIFF_RATE, 
										 FRAPModel.REF_DIFFUSION_RATE_PARAM.getLowerBound(),
										 FRAPModel.REF_DIFFUSION_RATE_PARAM.getUpperBound(),
										 FRAPModel.REF_DIFFUSION_RATE_PARAM.getScale(), 
				                         complexDifffRate);
		Parameter complexFrac = new Parameter(STR_COMPLEX_FRACTION,
										 FRAPModel.REF_MOBILE_FRACTION_PARAM.getLowerBound(),
										 FRAPModel.REF_MOBILE_FRACTION_PARAM.getUpperBound(),
										 FRAPModel.REF_MOBILE_FRACTION_PARAM.getScale(), 
                                         complexFraction);
		Parameter immFrac = new Parameter(STR_IMMOBILE_FRACTION,
										 FRAPModel.REF_MOBILE_FRACTION_PARAM.getLowerBound(),
										 FRAPModel.REF_MOBILE_FRACTION_PARAM.getUpperBound(),
										 FRAPModel.REF_MOBILE_FRACTION_PARAM.getScale(), 
						                 immFraction);
		Parameter bsConcentration = new Parameter(STR_BINDING_SITE_CONCENTRATION,
                                         0,
                                         1,
                                         1, 
                                         bsConc);
		Parameter onReacRate = new Parameter(STR_ON_RATE, 
                                         0,
                                         1e6,
                                         1, 
                                         onRate);
		Parameter offReacRate = new Parameter(STR_OFF_RATE, 
						                 0,
						                 1e6,
						                 1, 
						                 offRate);

		params = new Parameter[]{freeDiff, freeFrac,bleachWhileMonitoringRate, complexdiff, complexFrac, immFrac, bsConcentration, onReacRate, offReacRate};
		
		return params;
	}
}
