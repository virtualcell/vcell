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
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.zip.InflaterInputStream;

import org.jdom.Element;
import org.vcell.optimization.ProfileData;
import org.vcell.optimization.ProfileDataElement;
import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.util.CommentStringTokenizer;
import org.vcell.util.Extent;
import org.vcell.util.Origin;
import org.vcell.util.document.ExternalDataIdentifier;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.VCellSoftwareVersion;

import cbit.image.ImageException;
import cbit.util.xml.XmlUtil;
import cbit.vcell.VirtualMicroscopy.ImageDataset;
import cbit.vcell.VirtualMicroscopy.ROI;
import cbit.vcell.VirtualMicroscopy.UShortImage;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.modelopt.ParameterEstimationTaskXMLPersistence;
import cbit.vcell.opt.OptXmlTags;
import cbit.vcell.opt.Parameter;
import cbit.vcell.opt.SimpleReferenceData;
import cbit.vcell.xml.XMLTags;
import cbit.vcell.xml.XmlBase;
import cbit.vcell.xml.XmlParseException;
import cbit.vcell.xml.XmlReader;
/**
 * This class implements the translation of XML data into Java Vcell objects..
 * Creation date: (7/17/2000 12:22:50 PM)
 * @author schaff
 * @version $Revision: 1.0 $
 */
 
public class MicroscopyXmlReader {
	
	private XmlReader vcellXMLReader = null;
	
	public static class ExternalDataAndSimulationInfo{
		public final ExternalDataInfo frapDataExtDataInfo;
		public final ExternalDataInfo roiExtDataInfo;
		public final KeyValue simulationKey;
		public ExternalDataAndSimulationInfo(
				KeyValue simulationKey,
				ExternalDataInfo frapDataExtDataInfo,
				ExternalDataInfo roiExtDataInfo){
			this.simulationKey = simulationKey;
			this.frapDataExtDataInfo = frapDataExtDataInfo;
			this.roiExtDataInfo = roiExtDataInfo;
		}
	};
    	
	/**
	 * This constructor takes a parameter to specify if the KeyValue should be ignored
	 * Creation date: (3/13/2001 12:16:30 PM)
	 * @param readKeys boolean
	 */
	public MicroscopyXmlReader(boolean readKeys) {
		super();
		this.vcellXMLReader = new XmlReader(readKeys);
	}
	
	public static ExternalDataAndSimulationInfo getExternalDataAndSimulationInfo(File vfrapDocument) throws Exception{
		String xmlString = XmlUtil.getXMLString(vfrapDocument.getAbsolutePath());
		Element param  = XmlUtil.stringToXML(xmlString, null).getRootElement();
		MicroscopyXmlReader xmlReader = new MicroscopyXmlReader(true);
		
		KeyValue simulationKeyValue = null;
		ExternalDataInfo frapDataExtDatInfo = null;
		ExternalDataInfo roiExtDataInfo = null;
		Element bioModelElement = param.getChild(XMLTags.BioModelTag);
		if (bioModelElement!=null){
			String docSoftwareVersion = param.getAttributeValue(XMLTags.SoftwareVersionAttrTag);
			BioModel bioModel  = xmlReader.vcellXMLReader.getBioModel(bioModelElement,(docSoftwareVersion==null?null:VCellSoftwareVersion.fromString(docSoftwareVersion)));
			if(bioModel != null && bioModel.getSimulations()!=null && bioModel.getSimulations().length > 0){
				simulationKeyValue = bioModel.getSimulations()[0].getKey();
			}
		}
		Element timeSeriesExternalDataElement = param.getChild(MicroscopyXMLTags.ImageDatasetExternalDataInfoTag);
		if (timeSeriesExternalDataElement!=null){
			frapDataExtDatInfo = getExternalDataInfo(timeSeriesExternalDataElement);
		}
		Element roiExternalDataElement = param.getChild(MicroscopyXMLTags.ROIExternalDataInfoTag);
		if (roiExternalDataElement!=null){
			roiExtDataInfo = getExternalDataInfo(roiExternalDataElement);
		}
	
		return new ExternalDataAndSimulationInfo(simulationKeyValue,frapDataExtDatInfo,roiExtDataInfo);
	
	}
	
	/**
	 * This method returns a VCIMage object from a XML representation.
	 * Creation date: (3/16/2001 3:41:24 PM)
	 * @param param org.jdom.Element
	 * @return VCImage
	 * @throws XmlParseException
	 */
	private UShortImage getUShortImage(Element param) throws XmlParseException{
		//get the attributes
		Element tempelement= param.getChild(XMLTags.ImageDataTag);
		int aNumX = Integer.parseInt( tempelement.getAttributeValue(XMLTags.XAttrTag) );
		int aNumY = Integer.parseInt( tempelement.getAttributeValue(XMLTags.YAttrTag) );
		int aNumZ = Integer.parseInt( tempelement.getAttributeValue(XMLTags.ZAttrTag) );
		int compressSize =
			Integer.parseInt( tempelement.getAttributeValue(XMLTags.CompressedSizeTag) );
		final int BYTES_PER_SHORT = 2;
		int UNCOMPRESSED_SIZE_BYTES = aNumX*aNumY*aNumZ*BYTES_PER_SHORT;
		//getpixels
		String hexEncodedBytes = tempelement.getText();
		byte[] rawBytes = org.vcell.util.Hex.toBytes(hexEncodedBytes);
		ByteArrayInputStream rawByteArrayInputStream = new ByteArrayInputStream(rawBytes);
		InputStream rawInputStream = rawByteArrayInputStream;
		if(compressSize != UNCOMPRESSED_SIZE_BYTES){
			rawInputStream  = new InflaterInputStream(rawByteArrayInputStream);
		}
		byte[] shortsAsBytes = new byte[UNCOMPRESSED_SIZE_BYTES];
		int readCount = 0;
		try{
			while((readCount+= rawInputStream.read(shortsAsBytes, readCount, shortsAsBytes.length-readCount)) != shortsAsBytes.length){}
		}catch(Exception e){
			e.printStackTrace();
			throw new XmlParseException("error reading image pixels: ", e);
		}finally{
			if(rawInputStream != null){try{rawInputStream.close();}catch(Exception e2){e2.printStackTrace();}}
		}
	
		ByteBuffer byteBuffer = ByteBuffer.wrap(shortsAsBytes);
		short[] shortPixels = new short[aNumX*aNumY*aNumZ];
		for (int i = 0; i < shortPixels.length; i++) {
			shortPixels[i] = byteBuffer.getShort();
		}	
		Element extentElement = param.getChild(XMLTags.ExtentTag);
		Extent extent = null;
		if (extentElement!=null){
			extent = vcellXMLReader.getExtent(extentElement);
		}
		
		Element originElement = param.getChild(XMLTags.OriginTag);
		Origin origin = null;
		if (originElement!=null){
			origin = vcellXMLReader.getOrigin(originElement);
		}
	
		
	//	//set attributes
	//	String name = this.unMangle( param.getAttributeValue(XMLTags.NameAttrTag) );
	//	String annotation = param.getChildText(XMLTags.AnnotationTag);
	
		UShortImage newimage;
		try {
			newimage = new UShortImage(shortPixels,origin,extent,aNumX,aNumY,aNumZ);
		} catch (ImageException e) {
			e.printStackTrace();
			throw new XmlParseException("error reading image", e);
		}
		return newimage;
	}
	
	
	/**
	 * This method returns a Biomodel object from a XML Element.
	 * Creation date: (3/13/2001 12:35:00 PM)
	 * @param param org.jdom.Element
	 * @return cbit.vcell.biomodel.BioModel
	 * @throws XmlParseException
	 */
	private ImageDataset getImageDataset(Element param, ClientTaskStatusSupport progressListener) throws XmlParseException{
	
		@SuppressWarnings("unchecked")
		List<Element> ushortImageElementList = param.getChildren(MicroscopyXMLTags.UShortImageTag);
		Iterator<Element> imageElementIter = ushortImageElementList.iterator();
		UShortImage[] images = new UShortImage[ushortImageElementList.size()];
		//added in Feb 2008, for counting loading progress
		int imageSize = ushortImageElementList.size();
		int imageCount = 0;
		while (imageElementIter.hasNext()){
			images[imageCount++] = getUShortImage(imageElementIter.next());
			if(progressListener != null){progressListener.setProgress((int)((imageCount*100.0)/imageSize));}
		}
		Element timeStampListElement = param.getChild(MicroscopyXMLTags.TimeStampListTag);
		double[] timestamps = null;
		if (timeStampListElement!=null){
			String timeStampListText = timeStampListElement.getTextTrim();
			StringTokenizer tokens = new StringTokenizer(timeStampListText,",\n\r\t ",false);
			ArrayList<Double> times = new ArrayList<Double>();
			while (tokens.hasMoreTokens()){
				String token = tokens.nextToken();
				times.add(Double.parseDouble(token));
			}
			timestamps = new double[times.size()];
			for (int i = 0; i < timestamps.length; i++) {
				timestamps[i] = times.get(i);
			}
		}
		int numTimeSteps = (timestamps!=null)?(timestamps.length):(1);
		int numZ = images.length/numTimeSteps;
		ImageDataset imageDataset = new ImageDataset(images, timestamps, numZ);
		return imageDataset;
	}
	
	/**
	 * This method returns a Biomodel object from a XML Element.
	 * Creation date: (3/13/2001 12:35:00 PM)
	 * @param param org.jdom.Element
	 * @return cbit.vcell.biomodel.BioModel
	 * @throws XmlParseException
	 */
	private ROI getROI(Element param) throws XmlParseException{
	
		String roiName = param.getAttributeValue(MicroscopyXMLTags.ROITypeAttrTag);
		@SuppressWarnings("unchecked")
		List<Element> ushortImageElementList = param.getChildren(MicroscopyXMLTags.UShortImageTag);
		Iterator<Element> imageElementIter = ushortImageElementList.iterator();
		UShortImage[] images = new UShortImage[ushortImageElementList.size()];
		int imageIndex = 0;
		while (imageElementIter.hasNext()){
			images[imageIndex++] = getUShortImage(imageElementIter.next());
		}
		
		ROI roi = new ROI(images,roiName);
	
		return roi;
	}
	
	/**
	 * This method returns a Biomodel object from a XML Element.
	 * Creation date: (3/13/2001 12:35:00 PM)
	 * @param param org.jdom.Element
	 * @return cbit.vcell.biomodel.BioModel
	 * @throws XmlParseException
	 */
	private FRAPData getFrapData(Element param, ClientTaskStatusSupport progressListener) throws XmlParseException{
	
		
		Element imageDatasetElement = param.getChild(MicroscopyXMLTags.ImageDatasetTag);
		ImageDataset imageDataset = null;
		if (imageDatasetElement!=null){
			imageDataset = getImageDataset(imageDatasetElement,progressListener);
		}
		@SuppressWarnings("unchecked")
		List<Element> roiList = param.getChildren(MicroscopyXMLTags.ROITag);
		ROI[] rois = null;
		int numROIs = roiList.size();
		if (numROIs>0){
			rois = new ROI[numROIs];
			Iterator<Element> roiIter = roiList.iterator();
			int index = 0;
			while (roiIter.hasNext()){
				Element roiElement = roiIter.next();
				rois[index++] = getROI(roiElement);
			}
		}
		//reorder ROIs according to the order of FRAPData.VFRAP_ROI_ENUM
		ROI[] reorderedROIs = AnnotatedImageDataset.reorderROIs(rois);
		FRAPData frapData = new FRAPData(imageDataset, reorderedROIs);
		//Read pixel value scaling info
//		Element originalGlobalScaleInfoElement = param.getChild(MicroscopyXMLTags.OriginalGlobalScaleInfoTag);
//		if(originalGlobalScaleInfoElement != null){
//			frapData.setOriginalGlobalScaleInfo(
//				new FRAPData.OriginalGlobalScaleInfo(
//					Integer.parseInt(originalGlobalScaleInfoElement.getAttributeValue(MicroscopyXMLTags.OriginalGlobalScaleInfoMinTag)),
//					Integer.parseInt(originalGlobalScaleInfoElement.getAttributeValue(MicroscopyXMLTags.OriginalGlobalScaleInfoMaxTag)),
//					Double.parseDouble(originalGlobalScaleInfoElement.getAttributeValue(MicroscopyXMLTags.OriginalGlobalScaleInfoScaleTag)),
//					Double.parseDouble(originalGlobalScaleInfoElement.getAttributeValue(MicroscopyXMLTags.OriginalGlobalScaleInfoOffsetTag))
//				)
//			);
//		}
		//After loading all the ROI rings, the progress should set to 100.
		if(progressListener != null){progressListener.setProgress(100);}
		return frapData;
	}
	
	/**
	 * Method getExternalDataIdentifier.
	 * @param externalDataIDElement Element
	 * @return ExternalDataIdentifier
	 */
	private static ExternalDataIdentifier getExternalDataIdentifier(Element externalDataIDElement){
		String name = externalDataIDElement.getAttributeValue(XMLTags.NameAttrTag);
		String keyValueStr = externalDataIDElement.getAttributeValue(XMLTags.KeyValueAttrTag);
		String ownerName = externalDataIDElement.getAttributeValue(MicroscopyXMLTags.OwnerNameAttrTag);
		String ownerKey = externalDataIDElement.getAttributeValue(XMLTags.OwnerKeyAttrTag);
		
		return new ExternalDataIdentifier(new KeyValue(keyValueStr), new User(ownerName,new KeyValue(ownerKey)), name);
	}
	
	/**
	 * This method returns a Biomodel object from a XML Element.
	 * Creation date: (3/13/2001 12:35:00 PM)
	 * @param param org.jdom.Element
	 * @return cbit.vcell.biomodel.BioModel
	 * @throws XmlParseException
	 */
	public FRAPStudy getFrapStudy(Element param, ClientTaskStatusSupport progressListener) throws XmlParseException{
	
		FRAPStudy frapStudy = new FRAPStudy();
		
		//set name
		frapStudy.setName(this.unMangle(param.getAttributeValue(XMLTags.NameAttrTag)));
		//original image path
		frapStudy.setOriginalImageFilePath(this.unMangle(param.getAttributeValue(MicroscopyXMLTags.OriginalImagePathAttrTag)));
		//description
		frapStudy.setDescription(param.getChildText(XMLTags.AnnotationTag));
		//starting index for recovery
		String startIndexStr = param.getChildText(MicroscopyXMLTags.StartingIndexForRecoveryTag); 
		if(startIndexStr != null && startIndexStr.length() > 0)
		{
			frapStudy.setStartingIndexForRecovery(new Integer(startIndexStr));
		}
		//best model index
		String bestModexIndexStr = param.getChildText(MicroscopyXMLTags.BestModelIndexTag);
		if(bestModexIndexStr != null && bestModexIndexStr.length() > 0)
		{
			frapStudy.setBestModelIndex(new Integer(bestModexIndexStr));
		}
		//selected ROIs
		Element selectedROIsElement = param.getChild(MicroscopyXMLTags.SelectedROIsTag);
		if(selectedROIsElement != null)
		{
		    String roiBooleanText = selectedROIsElement.getText();
		    if(roiBooleanText != null)
		    {
			    CommentStringTokenizer tokens = new CommentStringTokenizer(roiBooleanText);
			    int arrayLen = FRAPData.VFRAP_ROI_ENUM.values().length;
			    boolean[] selectedROIsBooleanArray = new boolean[arrayLen];
			    for (int i = 0; i < arrayLen; i++)
			    {
			        if (tokens.hasMoreTokens())
			        {
			            String token = tokens.nextToken();
			            selectedROIsBooleanArray[i] = Boolean.parseBoolean(token);
			        }else{
			            throw new RuntimeException("failed to read boolean value for selected ROIs for error calculation.");
			        }
			    }
			    frapStudy.setSelectedROIsForErrorCalculation(selectedROIsBooleanArray);
		    }
		}
		//frap models
	    Element frapModelElement = param.getChild(MicroscopyXMLTags.FrapModelsTag);
		if(frapModelElement != null)
		{
			frapStudy.setModels(getFrapModels(frapModelElement));
		}
		else //try to read old model parameters
		{
			
		}
	    //bioModel
		Element bioModelElement = param.getChild(XMLTags.BioModelTag);
		if (bioModelElement!=null){
			String docSoftwareVersion = param.getAttributeValue(XMLTags.SoftwareVersionAttrTag);
			frapStudy.setBioModel(vcellXMLReader.getBioModel(bioModelElement,(docSoftwareVersion==null?null:VCellSoftwareVersion.fromString(docSoftwareVersion))));
		}
		Element frapDataElement = param.getChild(MicroscopyXMLTags.FRAPDataTag);
		if (frapDataElement!=null){
			frapStudy.setFrapData(getFrapData(frapDataElement,progressListener));
		}
		Element timeSeriesExternalDataElement = param.getChild(MicroscopyXMLTags.ImageDatasetExternalDataInfoTag);
		if (timeSeriesExternalDataElement!=null){
			frapStudy.setFrapDataExternalDataInfo(getExternalDataInfo(timeSeriesExternalDataElement));
		}
		Element roiExternalDataElement = param.getChild(MicroscopyXMLTags.ROIExternalDataInfoTag);
		if (roiExternalDataElement!=null){
			frapStudy.setRoiExternalDataInfo(getExternalDataInfo(roiExternalDataElement));
		}
		//stored reference data
		Element refDataElement = param.getChild(MicroscopyXMLTags.ReferenceDataTag);
		if (refDataElement!=null){
			frapStudy.setStoredRefData(getSimpleReferenceData(refDataElement));
		}
		//stored confidence intervals
		Element profileData_oneDiffElement = param.getChild(MicroscopyXMLTags.ListOfProfileData_OneDiffTag);
		if(profileData_oneDiffElement != null)
		{
			frapStudy.setProfileData_oneDiffComponent(getProfileDataList(profileData_oneDiffElement));
		}
		Element profileData_twoDiffElement = param.getChild(MicroscopyXMLTags.ListOfProfileData_TwoDiffTag);
		if(profileData_twoDiffElement != null)
		{
			frapStudy.setProfileData_twoDiffComponents(getProfileDataList(profileData_twoDiffElement));
		}
		Element profileData_reacOffRateElement = param.getChild(MicroscopyXMLTags.ListOfProfileData_ReacOffRateTag);
		if(profileData_reacOffRateElement != null)
		{
			frapStudy.setProfileData_reactionOffRate(getProfileDataList(profileData_reacOffRateElement));
		}
		return frapStudy;
	}
	
	private FRAPModel[] getFrapModels(Element frapModelElement) 
	{
		FRAPModel[] frapModels = new FRAPModel[FRAPModel.NUM_MODEL_TYPES];
		Element diffOneElement = frapModelElement.getChild(MicroscopyXMLTags.DiffustionWithOneComponentModelTag);
		if(diffOneElement != null)
		{
			String id = diffOneElement.getAttributeValue(MicroscopyXMLTags.ModelIdentifierAttTag);
			frapModels[FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT] = new FRAPModel(id, null, null, null);
			Element paramElement = diffOneElement.getChild(MicroscopyXMLTags.ModelParametersTag);
			if(paramElement != null)
			{
				frapModels[FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT].setModelParameters(getModelParameters(paramElement, FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT));
			}
		}
		Element diffTwoElement = frapModelElement.getChild(MicroscopyXMLTags.DiffustionWithTwoComponentsModelTag);
		if(diffTwoElement != null)
		{
			String id = diffTwoElement.getAttributeValue(MicroscopyXMLTags.ModelIdentifierAttTag);
			frapModels[FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS] = new FRAPModel(id, null, null, null);
			Element paramElement = diffTwoElement.getChild(MicroscopyXMLTags.ModelParametersTag);
			if(paramElement != null)
			{
				frapModels[FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS].setModelParameters(getModelParameters(paramElement, FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS));
			}
		}
		Element reacOffRateElement = frapModelElement.getChild(MicroscopyXMLTags.ReactionDominantOffRateModelTag);
		if(reacOffRateElement != null)
		{
			String id = reacOffRateElement.getAttributeValue(MicroscopyXMLTags.ModelIdentifierAttTag);
			frapModels[FRAPModel.IDX_MODEL_REACTION_OFF_RATE] = new FRAPModel(id, null, null, null);
			Element paramElement = reacOffRateElement.getChild(MicroscopyXMLTags.ModelParametersTag);
			if(paramElement != null)
			{
				frapModels[FRAPModel.IDX_MODEL_REACTION_OFF_RATE].setModelParameters(getModelParameters(paramElement, FRAPModel.IDX_MODEL_REACTION_OFF_RATE));
			}
		}
//		Element diffReacElement = frapModelElement.getChild(MicroscopyXMLTags.DiffustionReactionModelTag);
//		if(diffReacElement != null)
//		{
//			String id = diffReacElement.getAttributeValue(MicroscopyXMLTags.ModelIdentifierAttTag);
//			frapModels[FRAPModel.IDX_MODEL_DIFF_BINDING] = new FRAPModel(id, null, null, null);
//			Element paramElement = diffReacElement.getChild(MicroscopyXMLTags.ModelParametersTag);
//			if(paramElement != null)
//			{
//				frapModels[FRAPModel.IDX_MODEL_DIFF_BINDING].setModelParameters(getModelParameters(paramElement, FRAPModel.IDX_MODEL_DIFF_BINDING));
//			}
//			Element timePointsElement = diffReacElement.getChild(MicroscopyXMLTags.ModelTimePointsTag);
//			if(timePointsElement != null)
//			{
//				frapModels[FRAPModel.IDX_MODEL_DIFF_BINDING].setTimepoints(getBindingReacTimePoints(timePointsElement));
//			}
//			Element modelDataElement = diffReacElement.getChild(MicroscopyXMLTags.ModelDataTag);
//			if(modelDataElement != null)
//			{
//				SimpleReferenceData refModelData = getSimpleReferenceData(modelDataElement);
//				int roiLen = FRAPData.VFRAP_ROI_ENUM.values().length;
//				double[][] result = new double[roiLen][];
//				if(refModelData.getNumColumns() == (1+roiLen))
//				{
//					for(int i=0; i<roiLen; i++)
//					{
//						result[i]=refModelData.getColumnData(i+1); //first column is t, so the roi data starts from second column
//					}
//				}
//				frapModels[FRAPModel.IDX_MODEL_DIFF_BINDING].setData(result);
//			}
//		}
		return frapModels;
	}
	
	private Parameter[] getModelParameters(Element paramElement, int modelType) {
		if(modelType == FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT)
		{
			Parameter[] params = new Parameter[FRAPModel.NUM_MODEL_PARAMETERS_ONE_DIFF];
			double primaryDiffRate = Double.parseDouble(paramElement.getAttributeValue(MicroscopyXMLTags.PrimaryRateAttrTag));
			params[FRAPModel.INDEX_PRIMARY_DIFF_RATE] = new Parameter(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_PRIMARY_DIFF_RATE],
														FRAPModel.REF_DIFFUSION_RATE_PARAM.getLowerBound(), 
														FRAPModel.REF_DIFFUSION_RATE_PARAM.getUpperBound(),
														FRAPModel.REF_DIFFUSION_RATE_PARAM.getScale(),
	                									primaryDiffRate);
			double primaryFraction = Double.parseDouble(paramElement.getAttributeValue(MicroscopyXMLTags.PrimaryFractionAttTag));
			params[FRAPModel.INDEX_PRIMARY_FRACTION] = new Parameter(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_PRIMARY_FRACTION],
														FRAPModel.REF_MOBILE_FRACTION_PARAM.getLowerBound(), 
														FRAPModel.REF_MOBILE_FRACTION_PARAM.getUpperBound(),
														FRAPModel.REF_MOBILE_FRACTION_PARAM.getScale(),
														primaryFraction);
			double bwmRate = Double.parseDouble(paramElement.getAttributeValue(MicroscopyXMLTags.BleachWhileMonitoringTauAttrTag));
			params[FRAPModel.INDEX_BLEACH_MONITOR_RATE] = new Parameter(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_BLEACH_MONITOR_RATE],
														FRAPModel.REF_BLEACH_WHILE_MONITOR_PARAM.getLowerBound(),
														FRAPModel.REF_BLEACH_WHILE_MONITOR_PARAM.getUpperBound(),
														FRAPModel.REF_BLEACH_WHILE_MONITOR_PARAM.getScale(),
										                bwmRate); 
			return params;
		}
		else if(modelType ==FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS)
		{
			Parameter[] params = new Parameter[FRAPModel.NUM_MODEL_PARAMETERS_TWO_DIFF];
			double primaryDiffRate = Double.parseDouble(paramElement.getAttributeValue(MicroscopyXMLTags.PrimaryRateAttrTag));
			params[FRAPModel.INDEX_PRIMARY_DIFF_RATE] = new Parameter(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_PRIMARY_DIFF_RATE], 
														FRAPModel.REF_DIFFUSION_RATE_PARAM.getLowerBound(),
														FRAPModel.REF_DIFFUSION_RATE_PARAM.getUpperBound(),
														FRAPModel.REF_DIFFUSION_RATE_PARAM.getScale(), 
										                primaryDiffRate);
			double primaryFraction = Double.parseDouble(paramElement.getAttributeValue(MicroscopyXMLTags.PrimaryFractionAttTag));
			params[FRAPModel.INDEX_PRIMARY_FRACTION] = new Parameter(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_PRIMARY_FRACTION],
					    								FRAPModel.REF_MOBILE_FRACTION_PARAM.getLowerBound(),
					    								FRAPModel.REF_MOBILE_FRACTION_PARAM.getUpperBound(),
					    								FRAPModel.REF_MOBILE_FRACTION_PARAM.getScale(), 
										                primaryFraction);
			double bwmRate = Double.parseDouble(paramElement.getAttributeValue(MicroscopyXMLTags.BleachWhileMonitoringTauAttrTag));
			params[FRAPModel.INDEX_BLEACH_MONITOR_RATE] = new Parameter(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_BLEACH_MONITOR_RATE], 
														FRAPModel.REF_BLEACH_WHILE_MONITOR_PARAM.getLowerBound(),
														FRAPModel.REF_BLEACH_WHILE_MONITOR_PARAM.getUpperBound(),
														FRAPModel.REF_BLEACH_WHILE_MONITOR_PARAM.getScale(), 
										                bwmRate);
			double secDiffRate = Double.parseDouble(paramElement.getAttributeValue(MicroscopyXMLTags.SecondRateAttrTag));
			params[FRAPModel.INDEX_SECONDARY_DIFF_RATE] = new Parameter(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_SECONDARY_DIFF_RATE],
														FRAPModel.REF_SECOND_DIFFUSION_RATE_PARAM.getLowerBound(),
														FRAPModel.REF_SECOND_DIFFUSION_RATE_PARAM.getUpperBound(),
														FRAPModel.REF_SECOND_DIFFUSION_RATE_PARAM.getScale(), 
										                secDiffRate);
			double secFraction = Double.parseDouble(paramElement.getAttributeValue(MicroscopyXMLTags.SecondFractionAttTag));
			params[FRAPModel.INDEX_SECONDARY_FRACTION]= new Parameter(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_SECONDARY_FRACTION],
														FRAPModel.REF_SECOND_MOBILE_FRACTION_PARAM.getLowerBound(),
														FRAPModel.REF_SECOND_MOBILE_FRACTION_PARAM.getUpperBound(),
														FRAPModel.REF_SECOND_MOBILE_FRACTION_PARAM.getScale(),
										                secFraction);
			
			return params;
		}
		else if(modelType ==FRAPModel.IDX_MODEL_REACTION_OFF_RATE)
		{ 
			Parameter[] params = new Parameter[FRAPModel.NUM_MODEL_PARAMETERS_REACTION_OFF_RATE];
			double bwmRate = Double.parseDouble(paramElement.getAttributeValue(MicroscopyXMLTags.BleachWhileMonitoringTauAttrTag));
			params[FRAPModel.INDEX_BLEACH_MONITOR_RATE] = new Parameter(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_BLEACH_MONITOR_RATE], 
														FRAPModel.REF_BLEACH_WHILE_MONITOR_PARAM.getLowerBound(),
										                FRAPModel.REF_BLEACH_WHILE_MONITOR_PARAM.getUpperBound(),
										                FRAPModel.REF_BLEACH_WHILE_MONITOR_PARAM.getScale(), 
										                bwmRate); 
			double fittingParam = Double.parseDouble(paramElement.getAttributeValue(MicroscopyXMLTags.BindingSiteConcentrationAttTag));
			params[FRAPModel.INDEX_BINDING_SITE_CONCENTRATION] = new Parameter(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_BINDING_SITE_CONCENTRATION], 
										                FRAPModel.REF_BS_CONCENTRATION_OR_A.getLowerBound(),
										                FRAPModel.REF_BS_CONCENTRATION_OR_A.getUpperBound(),
										                FRAPModel.REF_BS_CONCENTRATION_OR_A.getScale(), 
										                fittingParam);
			double offRate = Double.parseDouble(paramElement.getAttributeValue(MicroscopyXMLTags.ReactionOffRateAttTag));
			params[FRAPModel.INDEX_OFF_RATE] = new Parameter(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_OFF_RATE], 
										                FRAPModel.REF_REACTION_OFF_RATE.getLowerBound(),
										                FRAPModel.REF_REACTION_OFF_RATE.getUpperBound(),
										                FRAPModel.REF_REACTION_OFF_RATE.getScale(), 
										                offRate);
			params[FRAPModel.INDEX_PRIMARY_DIFF_RATE] = null;
			params[FRAPModel.INDEX_PRIMARY_FRACTION] = null;
			params[FRAPModel.INDEX_SECONDARY_DIFF_RATE] =  null;
			params[FRAPModel.INDEX_SECONDARY_FRACTION] = null;
			params[FRAPModel.INDEX_ON_RATE] = null;
			
			return params;
		}
		else if(modelType ==FRAPModel.IDX_MODEL_DIFF_BINDING)
		{
			Parameter[] params = new Parameter[FRAPModel.NUM_MODEL_PARAMETERS_BINDING];
			double primaryDiffRate = Double.parseDouble(paramElement.getAttributeValue(MicroscopyXMLTags.PrimaryRateAttrTag));
			params[FRAPModel.INDEX_PRIMARY_DIFF_RATE] = new Parameter(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_PRIMARY_DIFF_RATE], 
														FRAPModel.REF_DIFFUSION_RATE_PARAM.getLowerBound(),
														FRAPModel.REF_DIFFUSION_RATE_PARAM.getUpperBound(),
														FRAPModel.REF_DIFFUSION_RATE_PARAM.getScale(), 
										                primaryDiffRate);
			double primaryFraction = Double.parseDouble(paramElement.getAttributeValue(MicroscopyXMLTags.PrimaryFractionAttTag));
			params[FRAPModel.INDEX_PRIMARY_FRACTION] = new Parameter(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_PRIMARY_FRACTION],
														FRAPModel.REF_MOBILE_FRACTION_PARAM.getLowerBound(),
														FRAPModel.REF_MOBILE_FRACTION_PARAM.getUpperBound(),
														FRAPModel.REF_MOBILE_FRACTION_PARAM.getScale(), 
										                primaryFraction);
			double bwmRate = Double.parseDouble(paramElement.getAttributeValue(MicroscopyXMLTags.BleachWhileMonitoringTauAttrTag));
			params[FRAPModel.INDEX_BLEACH_MONITOR_RATE] = new Parameter(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_BLEACH_MONITOR_RATE], 
														FRAPModel.REF_BLEACH_WHILE_MONITOR_PARAM.getLowerBound(),
										                FRAPModel.REF_BLEACH_WHILE_MONITOR_PARAM.getUpperBound(),
										                FRAPModel.REF_BLEACH_WHILE_MONITOR_PARAM.getScale(), 
										                bwmRate);
			double secDiffRate = Double.parseDouble(paramElement.getAttributeValue(MicroscopyXMLTags.SecondRateAttrTag));
			params[FRAPModel.INDEX_SECONDARY_DIFF_RATE] = new Parameter(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_SECONDARY_DIFF_RATE],
														FRAPModel.REF_SECOND_DIFFUSION_RATE_PARAM.getLowerBound(),
										                FRAPModel.REF_SECOND_DIFFUSION_RATE_PARAM.getUpperBound(),
										                FRAPModel.REF_SECOND_DIFFUSION_RATE_PARAM.getScale(), 
										                secDiffRate);
			double secFraction = Double.parseDouble(paramElement.getAttributeValue(MicroscopyXMLTags.SecondFractionAttTag));
			params[FRAPModel.INDEX_SECONDARY_FRACTION]= new Parameter(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_SECONDARY_FRACTION],
														FRAPModel.REF_SECOND_MOBILE_FRACTION_PARAM.getLowerBound(),
														FRAPModel.REF_SECOND_MOBILE_FRACTION_PARAM.getUpperBound(),
														FRAPModel.REF_SECOND_MOBILE_FRACTION_PARAM.getScale(),
										                secFraction);
			double bsConcentration = Double.parseDouble(paramElement.getAttributeValue(MicroscopyXMLTags.BindingSiteConcentrationAttTag));
			params[FRAPModel.INDEX_BINDING_SITE_CONCENTRATION] = new Parameter(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_BINDING_SITE_CONCENTRATION],
										                0,
										                1,
										                1, 
										                bsConcentration);
			double onRate = Double.parseDouble(paramElement.getAttributeValue(MicroscopyXMLTags.ReactionOnRateAttTag));
			params[FRAPModel.INDEX_ON_RATE] = new Parameter(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_ON_RATE], 
										                0,
										                1e6,
										                1, 
										                onRate);
			double offRate = Double.parseDouble(paramElement.getAttributeValue(MicroscopyXMLTags.ReactionOffRateAttTag));
			params[FRAPModel.INDEX_OFF_RATE] = new Parameter(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_OFF_RATE], 
										                0,
										                1e6,
										                1, 
										                offRate); 
			return params;
		}
		return null;
	}
	
	private static ExternalDataInfo getExternalDataInfo(Element extDataInfoElement){
		String filename = extDataInfoElement.getAttributeValue(MicroscopyXMLTags.FilenameAttrTag);
		Element externalDataIDElement = extDataInfoElement.getChild(MicroscopyXMLTags.ExternalDataIdentifierTag);
		ExternalDataIdentifier externalDataID = getExternalDataIdentifier(externalDataIDElement);
		ExternalDataInfo externalDataInfo = new ExternalDataInfo(externalDataID, filename);
		return externalDataInfo;
	
	}
	/**
	 * Method unMangle.
	 * @param str String
	 * @return String
	 */
	private String unMangle(String str){
		return XmlBase.unMangle(str);
	}
	
	private SimpleReferenceData getSimpleReferenceData(Element referenceDataElement/*, Namespace ns*/){
	    String numColsText = referenceDataElement.getAttributeValue(ParameterEstimationTaskXMLPersistence.NumColumnsAttribute);
	    int numCols = Integer.parseInt(numColsText);
	    //
	    // read columns
	    //    
	    String[] columnNames = new String[numCols];
	    double[] columnWeights = new double[numCols];
	    Element dataColumnListElement = referenceDataElement.getChild(ParameterEstimationTaskXMLPersistence.DataColumnListTag/*, ns*/);
	    @SuppressWarnings("unchecked")
	    List<Element> dataColumnList = dataColumnListElement.getChildren(ParameterEstimationTaskXMLPersistence.DataColumnTag/*, ns*/);
	    int colCounter = 0;
	    for (Element dataColumnElement : dataColumnList){
	          columnNames[colCounter] = dataColumnElement.getAttributeValue(ParameterEstimationTaskXMLPersistence.NameAttribute);
	          columnWeights[colCounter] = Double.parseDouble(dataColumnElement.getAttributeValue(ParameterEstimationTaskXMLPersistence.WeightAttribute));
	          colCounter ++;
	    }
	    //
	    // read rows
	    //
	    Vector<double[]> rowDataVector = new Vector<double[]>();
	    Element dataRowListElement = referenceDataElement.getChild(ParameterEstimationTaskXMLPersistence.DataRowListTag/*, ns*/);
	    @SuppressWarnings("unchecked")
	    List<Element> dataRowList = dataRowListElement.getChildren(ParameterEstimationTaskXMLPersistence.DataRowTag/*, ns*/);
	    for (Element dataRowElement : dataRowList){
	          String rowText = dataRowElement.getText();
	          CommentStringTokenizer tokens = new CommentStringTokenizer(rowText);
	          double[] rowData = new double[numCols];
	          for (int j = 0; j < numCols; j++){
	                if (tokens.hasMoreTokens()){
	                      String token = tokens.nextToken();
	                      rowData[j] = Double.parseDouble(token);
	                }else{
	                      throw new RuntimeException("failed to read row data for ReferenceData");
	                }
	          }
	          rowDataVector.add(rowData);
	    }
	    SimpleReferenceData referenceData = new SimpleReferenceData(columnNames, columnWeights, rowDataVector);
	    return referenceData;
	}
	
	//get a list of profile data for either diffusion with one diffusing component/or with two diffusing components
	private ProfileData[] getProfileDataList(Element profileDataListElement)
	{
		ProfileData[] profileDataArray = null;
		//read the list of profile data
		if(profileDataListElement !=  null)
		{
			@SuppressWarnings("unchecked")
			List<Element> profileDataList = profileDataListElement.getChildren(MicroscopyXMLTags.ProfileDataTag);
			profileDataArray = new ProfileData[profileDataList.size()];
			int profileCounter = 0;
			for(Element profileElement : profileDataList)//loop through each profile data
			{
				profileDataArray[profileCounter] = getProfileData(profileElement);
				profileCounter ++;
			}
		}
		
		return profileDataArray;
	}

	private ProfileData getProfileData(Element profileDataElement) 
	{
		ProfileData profileData = null;
		if(profileDataElement != null)
		{
			profileData = new ProfileData();
			@SuppressWarnings("unchecked")
			List<Element> profileDataElementList = profileDataElement.getChildren(MicroscopyXMLTags.ProfieDataElementTag);
			for(int i=0; i < profileDataElementList.size(); i++)//loop through each profile data element
			{
				profileData.addElement(getProfileDataElement(profileDataElementList.get(i)));
			}
		}
		return profileData;
	}

	private ProfileDataElement getProfileDataElement(Element profileDataElementElement) 
	{
		ProfileDataElement profileDataElement = null;
		if(profileDataElementElement != null)
		{
			String paramName = unMangle(profileDataElementElement.getAttributeValue(MicroscopyXMLTags.profileDataElementParameterNameAttrTag));
			double paramVal = new Double(unMangle(profileDataElementElement.getAttributeValue(MicroscopyXMLTags.profileDataElementParameterValueAttrTag)));
			double likelihood = new Double(unMangle(profileDataElementElement.getAttributeValue(MicroscopyXMLTags.profileDataElementLikelihoodAttrTag)));
			@SuppressWarnings("unchecked")
			List<Element> parameterElementList = profileDataElementElement.getChildren(OptXmlTags.Parameter_Tag);
			Parameter[] parameters = new Parameter[parameterElementList.size()];
			int paramCounter = 0;
			for(Element paramElement : parameterElementList)
			{
				parameters[paramCounter] = getParameter(paramElement);
				paramCounter ++;
			}
			
			profileDataElement = new ProfileDataElement(paramName, paramVal, likelihood, parameters);
		}
		return profileDataElement;
	}

	private Parameter getParameter(Element paramElement) 
	{
		Parameter parameter = null;
		if(paramElement != null)
		{
			String name = unMangle(paramElement.getAttributeValue(OptXmlTags.ParameterName_Attr));
			double lowerBound = new Double(unMangle(paramElement.getAttributeValue(OptXmlTags.ParameterLow_Attr)));
			double upperBound = new Double(unMangle(paramElement.getAttributeValue(OptXmlTags.ParameterHigh_Attr)));
			double iniGuess = new Double(unMangle(paramElement.getAttributeValue(OptXmlTags.ParameterInit_Attr)));
			double scale = new Double(unMangle(paramElement.getAttributeValue(OptXmlTags.ParameterScale_Attr)));
			parameter = new Parameter(name, lowerBound, upperBound, scale, iniGuess);
		}
		return parameter;
	}
	
}
