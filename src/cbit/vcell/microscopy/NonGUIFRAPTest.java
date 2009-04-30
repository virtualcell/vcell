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
import loci.formats.out.TiffWriter;
import cbit.util.Extent;
import cbit.util.FileUtils;
import cbit.util.xml.XmlUtil;
import cbit.vcell.VirtualMicroscopy.ImageDataset;
import cbit.vcell.VirtualMicroscopy.ImageDatasetReader;
import cbit.vcell.VirtualMicroscopy.UShortImage;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.server.PDEDataManager;
import cbit.vcell.client.server.VCDataManager;
import cbit.vcell.desktop.controls.DataManager;
import cbit.vcell.field.FieldDataFileOperationSpec;
import cbit.vcell.microscopy.ROI.RoiType;
import cbit.vcell.modelopt.gui.DataReference;
import cbit.vcell.modelopt.gui.DataSource;
import cbit.vcell.modelopt.gui.MultisourcePlotListModel;
import cbit.vcell.opt.ReferenceData;
import cbit.vcell.server.StdoutSessionLog;
import cbit.vcell.simdata.DataSetControllerImpl;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.ode.ODESolverResultSet;
import loci.formats.AWTImageTools;

public class NonGUIFRAPTest {

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

	public static File getCanonicalFilename(String testDirectoryPath,ROI.RoiType roiType){
		if(roiType == null){
			return new File(testDirectoryPath,"testImageData.zip");
		}else if(roiType.equals(RoiType.ROI_CELL)){
			return new File(testDirectoryPath,"testCellROI.tif");
		}else if(roiType.equals(RoiType.ROI_BLEACHED)){
			return new File(testDirectoryPath,"testBleachROI.tif");
		}else if(roiType.equals(RoiType.ROI_BACKGROUND)){
			return new File(testDirectoryPath,"testBackgroundROI.tif");
		}
		throw new IllegalArgumentException("unknown type "+roiType.toString());
	}
	public static ExternalDataFileContents readExternalDataContents(
			String imageDataPathName,
			String cellROIPathName,String bleachROIPathName,
			String backgroundROIPathName) throws Exception{
		ExternalDataFileContents extDataFilecontents = new ExternalDataFileContents();
			extDataFilecontents.imageData =
				ImageDatasetReader.readImageDataset(imageDataPathName, null);
			extDataFilecontents.cellROIData =
				ImageDatasetReader.readImageDataset(cellROIPathName,null);
			extDataFilecontents.bleachROIData =
				ImageDatasetReader.readImageDataset(bleachROIPathName,null);
			extDataFilecontents.backgroundROIData =
				ImageDatasetReader.readImageDataset(backgroundROIPathName,null);

		return extDataFilecontents;
	}
	private static void checkExternalDataSameAsOriginal(FRAPStudy frapStudy,String testDirectoryPath) throws Exception{
		ImageDataset imageDataset = frapStudy.getFrapData().getImageDataset();
		ROI cellROI = frapStudy.getFrapData().getRoi(ROI.RoiType.ROI_CELL);
		ROI bleachROI = frapStudy.getFrapData().getRoi(ROI.RoiType.ROI_BLEACHED);
		ROI backgroundROI = frapStudy.getFrapData().getRoi(ROI.RoiType.ROI_BACKGROUND);
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
				getCanonicalFilename(testDirectoryPath, RoiType.ROI_CELL).getAbsolutePath(),
				getCanonicalFilename(testDirectoryPath, RoiType.ROI_BLEACHED).getAbsolutePath(),
				getCanonicalFilename(testDirectoryPath, RoiType.ROI_BACKGROUND).getAbsolutePath());
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
		FRAPStudy frapStudy = xmlReader.getFrapStudy(XmlUtil.stringToXML(xmlString, null),null);
		
		if(frapStudy == null || frapStudy.getFrapData() == null){
			throw new Exception("no FrapData in file "+xmlFileName);
		}
		if(frapStudy.getFrapData().getImageDataset().getSizeC() != 1 ||
				frapStudy.getFrapData().getImageDataset().getSizeZ() != 1){
			throw new Exception("Only single channel, single z test implemented");
		}
		
		File imageDataSetZipFile = getCanonicalFilename(testDirectoryPath,null);
		File cellROIFile = getCanonicalFilename(testDirectoryPath,RoiType.ROI_CELL);
		File bleachROIFile = getCanonicalFilename(testDirectoryPath,RoiType.ROI_BLEACHED);
		File backgroundROIFile =getCanonicalFilename(testDirectoryPath,RoiType.ROI_BACKGROUND);
		
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
		ROI cellROI = frapStudy.getFrapData().getRoi(ROI.RoiType.ROI_CELL);
		File cellTempF =
			writeTempTiff(cellROI.getRoiImages()[0].getPixels(),
				cellROI.getRoiImages()[0].getNumX(), cellROI.getRoiImages()[0].getNumY());
		FileUtils.copyFile(cellTempF, cellROIFile);
		cellTempF.delete();
//		writeUShortFile(cellROI.getRoiImages()[0], fos);
//		fos.close();
		
//		fos = new FileOutputStream(bleachROIFile);
		ROI bleachROI = frapStudy.getFrapData().getRoi(ROI.RoiType.ROI_BLEACHED);
		File bleachTempf =
			writeTempTiff(bleachROI.getRoiImages()[0].getPixels(),
				bleachROI.getRoiImages()[0].getNumX(), bleachROI.getRoiImages()[0].getNumY());
		FileUtils.copyFile(bleachTempf, bleachROIFile);
		bleachTempf.delete();
//		writeUShortFile(bleachROI.getRoiImages()[0],fos);
//		fos.close();
		
//		fos = new FileOutputStream(backgroundROIFile);
		ROI backgroundROI = frapStudy.getFrapData().getRoi(ROI.RoiType.ROI_BACKGROUND);
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
				frapStudy.getFrapModelParameters().startIndexForRecovery,
				frapStudy.getFrapModelParameters().diffusionRate,
				frapStudy.getFrapModelParameters().monitorBleachRate,
				frapStudy.getFrapModelParameters().mobileFraction,
				frapStudy.getFrapModelParameters().secondRate,
				frapStudy.getFrapModelParameters().secondFraction,
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
		TiffWriter tifWriter = new TiffWriter();
		File tempF = File.createTempFile("imageDataSetTest", ".tif");
		tempF.deleteOnExit();
		tifWriter.setId(tempF.getAbsolutePath());
		tifWriter.setCompression("Uncompressed");
		BufferedImage timePointBufferedImage =
			AWTImageTools.makeImage(shortPixels, width, height,false);
		tifWriter.saveImage(timePointBufferedImage, true);
		tifWriter.close();
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
		String recoveryDiffusionRateString = args[1];
		String bleachWhileMonitoringRateString = args[2];
		String mobileFractionString = args[3];
		String secondRateString = args[4];
		String secondFractionString = args[5];
		String workingDirectoryPath = args[6];
		String inputFRAPDataFileName = args[7];
		String inputCellROIFileName = args[8];
		String inputBleachROIFileName = args[9];
		String inputBackgroundROIFileName = args[10];
		String outputXMLFileName = args[11];
		String commaSepTimeStamps = args[12];
		String commaSepExtentXYZ = args[13];
		
		LocalWorkspace localWorkspace =
			new LocalWorkspace(new File(workingDirectoryPath));
		
		ExternalDataFileContents extDataFileContents =
			readExternalDataContents(
				inputFRAPDataFileName, inputCellROIFileName,
				inputBleachROIFileName, inputBackgroundROIFileName);
		
		ROI cellROI = new ROI(extDataFileContents.cellROIData.getImage(0, 0, 0),ROI.RoiType.ROI_CELL);
		ROI bleachROI = new ROI(extDataFileContents.bleachROIData.getImage(0, 0, 0),ROI.RoiType.ROI_BLEACHED);
		ROI backgroundROI = new ROI(extDataFileContents.backgroundROIData.getImage(0, 0, 0),ROI.RoiType.ROI_BACKGROUND);
		
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
		
		FRAPData frapData = FRAPData.importFRAPDataFromImageDataSet(frapDataImageDataSet);
		frapData.addReplaceRoi(bleachROI);
		frapData.addReplaceRoi(cellROI);
		frapData.addReplaceRoi(backgroundROI);	

		FRAPStudy frapStudy = new FRAPStudy();
		frapStudy.setFrapData(frapData);
		FRAPStudy.FRAPModelParameters frapModelParameters =
			new FRAPStudy.FRAPModelParameters(
					startingIndexForRecovery,
					recoveryDiffusionRateString,
					bleachWhileMonitoringRateString,
					mobileFractionString,
					secondRateString,
					secondFractionString
				);
		frapStudy.setFrapModelParameters(frapModelParameters);
		frapStudy.refreshDependentROIs();
		
		ExternalDataInfo imageDatasetExternalDataInfo = FRAPStudy.createNewExternalDataInfo(localWorkspace, FRAPStudy.IMAGE_EXTDATA_NAME);
		ExternalDataInfo roiExternalDataInfo = FRAPStudy.createNewExternalDataInfo(localWorkspace, FRAPStudy.ROI_EXTDATA_NAME);
		frapStudy.setFrapDataExternalDataInfo(imageDatasetExternalDataInfo);
		frapStudy.setRoiExternalDataInfo(roiExternalDataInfo);
		
		frapStudy.saveImageDatasetAsExternalData(
				localWorkspace,frapStudy.getFrapDataExternalDataInfo().getExternalDataIdentifier(),
				new Integer(frapModelParameters.startIndexForRecovery));
		frapStudy.saveROIsAsExternalData(
				localWorkspace, frapStudy.getRoiExternalDataInfo().getExternalDataIdentifier(),
				new Integer(frapModelParameters.startIndexForRecovery));

//		Double bleachWhileMonitoringRate =
//			(!bleachWhileMonitoringRateString.equals("-")
//				?Double.parseDouble(bleachWhileMonitoringRateString)
//				:null);
		BioModel bioModel =
			FRAPStudy.createNewBioModel(
				frapStudy,
				recoveryDiffusionRateString,
				bleachWhileMonitoringRateString,
				mobileFractionString,
				new Double(secondRateString),
				secondFractionString,
				null,
				LocalWorkspace.createNewKeyValue(),
				LocalWorkspace.getDefaultOwner(),
				new Integer(frapModelParameters.startIndexForRecovery));
		frapStudy.setBioModel(bioModel);
		DataSetControllerImpl.ProgressListener progressListener =
			new DataSetControllerImpl.ProgressListener(){
				public void updateProgress(double progress){
					System.out.println((int)Math.round(progress*100));
				}
				public void updateMessage(String message) {
					System.out.println(message);
				}
			};
		MicroscopyXmlproducer.writeXMLFile(frapStudy, new File(outputXMLFileName), true,progressListener,false);
		FRAPStudy.runFVSolverStandalone(
			new File(localWorkspace.getDefaultSimDataDirectory()),
			new StdoutSessionLog(LocalWorkspace.getDefaultOwner().getName()),
			bioModel.getSimulations(0),
			imageDatasetExternalDataInfo.getExternalDataIdentifier(),
			roiExternalDataInfo.getExternalDataIdentifier(),
			progressListener, false);
		
		VCSimulationDataIdentifier vcSimulationDataIdentifier =
			new VCSimulationDataIdentifier(
				bioModel.getSimulations()[0].getSimulationInfo().getAuthoritativeVCSimulationIdentifier(),
				FieldDataFileOperationSpec.JOBINDEX_DEFAULT);
		DataManager simulationDataManager =
			new PDEDataManager(localWorkspace.getVCDataManager(),vcSimulationDataIdentifier);
		double[] frapDataTimeStamps = frapData.getImageDataset().getImageTimeStamps();
		//
		VCDataManager testVCDataManager = localWorkspace.getVCDataManager();
		double[] prebleachAverage = testVCDataManager.getSimDataBlock(
				frapStudy.getRoiExternalDataInfo().getExternalDataIdentifier(), "prebleach_avg", 0).getData();
		//

		FRAPStudy.SpatialAnalysisResults spatialAnalysisResults =
			FRAPStudy.spatialAnalysis(
				simulationDataManager,
				new Integer(frapModelParameters.startIndexForRecovery),
				frapDataTimeStamps[new Integer(frapModelParameters.startIndexForRecovery)],
				recoveryDiffusionRateString,
				mobileFractionString,
				bleachWhileMonitoringRateString,
				/*bioModel.getSimulations()[0].getMathDescription().getSubDomain(FRAPStudy.CYTOSOL_NAME),*/
				frapData,
				prebleachAverage,
				progressListener);
		dumpSummaryReport(spatialAnalysisResults, frapDataTimeStamps,
				new Integer(startingIndexForRecovery).intValue(),
				new File(workingDirectoryPath,"nonguiSpatialResults.txt"));
//		dumpSpatialResults(spatialAnalysisResults, frapDataTimeStamps, new File(workingDirectoryPath,"nonguiSpatialResults.txt"));
		
	}
	public static void dumpSummaryReport(
			FRAPStudy.SpatialAnalysisResults spatialAnalysisResults,
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
		String summaryReportS =
			FRAPStudy.SpatialAnalysisResults.createCSVSummaryReport(
					FRAPStudy.SpatialAnalysisResults.getSummaryReportColumnNames(), summaryData);
		FileWriter fw = null;
		try{
			fw = new FileWriter(outputFile);
			fw.write(summaryReportS);
		}finally{
			try{if(fw !=null){fw.close();}}catch(Exception e){e.printStackTrace();}
		}
		
	}
	public static void dumpSpatialResults(
			FRAPStudy.SpatialAnalysisResults spatialAnalysisResults,
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
			DataSource expDataSource = new DataSource(referenceDataArr[i],"experiment");
			DataSource fitDataSource = new DataSource(odeSolverResultSetArr[i], "fit");
			MultisourcePlotListModel multisourcePlotListModel =
				new MultisourcePlotListModel();
			multisourcePlotListModel.setDataSources(new DataSource[] {expDataSource,fitDataSource});
			System.out.println("AnalysisParameters = "+spatialAnalysisResults.analysisParameters[i]);
			for (int j = 0; j < multisourcePlotListModel.getSize(); j++) {
				DataReference dataReference = (DataReference)multisourcePlotListModel.getElementAt(j);
				if(dataReference.getDataSource().getSource() instanceof ReferenceData){
					ReferenceData refData = (ReferenceData)dataReference.getDataSource().getSource();
					for (int k = 0; k < refData.getNumRows(); k++) {
						for (int k2 = 0; k2 < refData.getNumColumns(); k2++) {
							System.out.print(refData.getRowData(k)[k2]+" ");
							fw.write(refData.getRowData(k)[k2]+" ");
						}
						System.out.println();
						fw.write("\n");
					}
				}else{
					ODESolverResultSet odeRS = (ODESolverResultSet)dataReference.getDataSource().getSource();
					for (int k = 0; k < odeRS.getRowCount(); k++) {
						for (int k2 = 0; k2 < odeRS.getDataColumnCount(); k2++) {
							System.out.print(odeRS.getRow(k)[k2]+" ");
							fw.write(odeRS.getRow(k)[k2]+" ");
						}
						System.out.println();
						fw.write("\n");
					}
				}
			}
		}
		fw.close();
	}
}
