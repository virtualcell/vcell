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
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.zip.DeflaterOutputStream;

import org.jdom.Element;
import org.vcell.optimization.ProfileData;
import org.vcell.optimization.ProfileDataElement;
import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.util.document.ExternalDataIdentifier;
import org.vcell.util.document.VCellSoftwareVersion;

import cbit.util.xml.XmlUtil;
import cbit.vcell.VirtualMicroscopy.ImageDataset;
import cbit.vcell.VirtualMicroscopy.ROI;
import cbit.vcell.VirtualMicroscopy.UShortImage;
import cbit.vcell.modelopt.ParameterEstimationTaskXMLPersistence;
import cbit.vcell.opt.OptXmlTags;
import cbit.vcell.opt.Parameter;
import cbit.vcell.opt.SimpleReferenceData;
import cbit.vcell.xml.XMLTags;
import cbit.vcell.xml.XmlParseException;
import cbit.vcell.xml.Xmlproducer;

/**
 * This class concentrates all the XML production code from Java objects.
 * Creation date: (2/14/2001 3:40:30 PM)
 * @author schaff
 * @version $Revision: 1.0 $
 */
public class MicroscopyXmlproducer {

	public static void writeXMLFile(FRAPStudy frapStudy,File outputFile,boolean bPrintKeys, ClientTaskStatusSupport progressListener,boolean bSaveCompressed) throws Exception{

		Xmlproducer vcellXMLProducer = new Xmlproducer(bPrintKeys);
		Element root = MicroscopyXmlproducer.getXML(frapStudy,vcellXMLProducer,progressListener,bSaveCompressed);
		java.io.FileOutputStream fileOutStream = new java.io.FileOutputStream(outputFile);
		BufferedOutputStream bufferedStream = new BufferedOutputStream(fileOutStream);
		XmlUtil.writeXmlToStream(root, true, bufferedStream);
		fileOutStream.flush();
		fileOutStream.close();

	}

	/**
	 * Method mangle.
	 * @param str String
	 * @return String
	 */
	private static String mangle(String str){
		return cbit.vcell.xml.XmlBase.mangle(str);
	}

	/**
	 * This methods returns a XML represnetation of a VCImage object.
	 * Creation date: (3/1/2001 3:02:37 PM)
	 * @param param cbit.image.VCImage
	 * @return Element
	 * @throws XmlParseException
	 */
	private static Element getXML(UShortImage param,Xmlproducer vcellXMLProducer,boolean bSaveCompressed) throws XmlParseException{
		Element image = new Element(MicroscopyXMLTags.UShortImageTag);

		//add atributes
		if (param.getName()!=null && param.getName().length()>0){
			image.setAttribute(XMLTags.NameAttrTag, mangle(param.getName()));
		}

		//Add annotation
		if (param.getDescription()!=null && param.getDescription().length()>0) {
			Element annotationElement = new Element(XMLTags.AnnotationTag);
			annotationElement.setText( mangle(param.getDescription()) );
			image.addContent(annotationElement);
		}

		//Add extent
		if (param.getExtent()!=null) {
			image.addContent(vcellXMLProducer.getXML(param.getExtent()));
		}
		//Add Origin
		if (param.getOrigin()!=null) {
			image.addContent(vcellXMLProducer.getXML(param.getOrigin()));
		}

		final int BYTES_PER_SHORT = 2;
		int aNumX = param.getNumX();
		int aNumY = param.getNumY();
		int aNumZ = param.getNumZ();
		//Add Imagedata subelement
		int UNCOMPRESSED_SIZE_BYTES = aNumX*aNumY*aNumZ*BYTES_PER_SHORT;
		short[] pixelsShort = param.getPixels();
		byte[] compressedPixels = null;
		boolean bForceUncompressed = false;
		while(true){
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			OutputStream dos = bos;
			if(bSaveCompressed && !bForceUncompressed){
				dos = new DeflaterOutputStream(bos);
			}
			ByteBuffer byteBuffer = ByteBuffer.allocate(UNCOMPRESSED_SIZE_BYTES);
			for (int i = 0; i < pixelsShort.length; i++) {
				byteBuffer.putShort(pixelsShort[i]);
			}
			try {
				dos.write(byteBuffer.array());
			}catch (IOException e){
				e.printStackTrace(System.out);
				throw new XmlParseException("failed to create compressed pixel data");
			}finally{
				if(dos != null){try{dos.close();}catch(Exception e2){e2.printStackTrace();}}
			}
			compressedPixels = bos.toByteArray();
			if(!bSaveCompressed || bForceUncompressed){
				if(UNCOMPRESSED_SIZE_BYTES != compressedPixels.length){
					throw new XmlParseException("failed to create uncompressed pixel data");
				}
			}else if(!bForceUncompressed){
				if(UNCOMPRESSED_SIZE_BYTES <= compressedPixels.length){
					//Compression didn't compress
					//Save uncompressed so reader not confused
					bForceUncompressed = true;
					continue;
				}
			}
			break;
		}
		Element imagedata = new Element(XMLTags.ImageDataTag);

		//Get imagedata attributes
		imagedata.setAttribute(XMLTags.XAttrTag, String.valueOf(aNumX));
		imagedata.setAttribute(XMLTags.YAttrTag, String.valueOf(aNumY));
		imagedata.setAttribute(XMLTags.ZAttrTag, String.valueOf(aNumZ));
		imagedata.setAttribute(XMLTags.CompressedSizeTag, String.valueOf(compressedPixels.length));
		//Get imagedata content
		imagedata.addContent(org.vcell.util.Hex.toString(compressedPixels)); //encode
		//Add imagedata to VCImage element
		image.addContent(imagedata);
		//Add PixelClass elements

		return image;
	}
	/**
	 * This method returns a XML representation for a Biomodel object.
	 * Creation date: (2/14/2001 3:41:13 PM)
	 * @param param cbit.vcell.biomodel.BioModel
	 * @return Element
	 * @throws XmlParseException
	 * @throws cbit.vcell.parser.ExpressionException
	 */
	private static Element getXML(ROI param,Xmlproducer vcellXMLProducer,boolean bSaveCompressed) throws XmlParseException, cbit.vcell.parser.ExpressionException {
		Element roiNode = new Element(MicroscopyXMLTags.ROITag);
		UShortImage[] images = param.getRoiImages();
		for (int i = 0; i < images.length; i++) {
			roiNode.addContent( getXML(param.getRoiImages()[i],vcellXMLProducer,bSaveCompressed) );
		}
		roiNode.setAttribute(MicroscopyXMLTags.ROITypeAttrTag, param.getROIName());
		return roiNode;
	}

	/**
	 * This method returns a XML representation for a Biomodel object.
	 * Creation date: (2/14/2001 3:41:13 PM)
	 * @param param cbit.vcell.biomodel.BioModel
	 * @return Element
	 * @throws XmlParseException
	 * @throws cbit.vcell.parser.ExpressionException
	 */
	public static Element getXML(ImageDataset param,Xmlproducer vcellXMLProducer, ClientTaskStatusSupport progressListener,boolean bSaveCompressed) throws XmlParseException, cbit.vcell.parser.ExpressionException {
		Element imageDatasetNode = new Element(MicroscopyXMLTags.ImageDatasetTag);
		//Get ImageDataset
		UShortImage[] images = param.getAllImages();
		//added in Feb 2008, for counting saving progress
		int imageSize = images.length;
		int imageCount = 0;
		for (int i = 0; i < images.length; i++) {
			imageDatasetNode.addContent( getXML(images[i],vcellXMLProducer,bSaveCompressed) );
			imageCount++;
			//suppose image data set takes 95% and roi takes 5% of total progress.
			if(progressListener != null){progressListener.setProgress(((int)((imageCount*95)/(double)imageSize)));}	
		}
		if (param.getImageTimeStamps()!=null){
			Element timeStampListNode = new Element(MicroscopyXMLTags.TimeStampListTag);
			double[] timeStamps = param.getImageTimeStamps();
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append(timeStamps[0]);
			for (int i = 1; i < timeStamps.length; i++) {
				stringBuilder.append(",");
				stringBuilder.append(timeStamps[i]);
			}
			timeStampListNode.addContent(stringBuilder.toString());
			imageDatasetNode.addContent(timeStampListNode);
		}
		return imageDatasetNode;
	}


	/**
	 * This method returns a XML representation for a Biomodel object.
	 * Creation date: (2/14/2001 3:41:13 PM)
	 * @param param cbit.vcell.biomodel.BioModel
	 * @return Element
	 * @throws XmlParseException
	 * @throws cbit.vcell.parser.ExpressionException
	 */
	private static Element getXML(FRAPData param,Xmlproducer vcellXMLProducer, ClientTaskStatusSupport progressListener, boolean bSaveCompressed) throws XmlParseException, cbit.vcell.parser.ExpressionException 
	{
		Element frapDataNode = new Element(MicroscopyXMLTags.FRAPDataTag);
		//Get ImageDataset
		if (param.getImageDataset()!=null){
			frapDataNode.addContent( getXML(param.getImageDataset(),vcellXMLProducer,progressListener,bSaveCompressed) );
		}
		//Get ROIs
		ROI[] rois = param.getRois();
		for (int i = 0; i < rois.length; i++) {
			frapDataNode.addContent( getXML(rois[i],vcellXMLProducer,bSaveCompressed) );
		}
		//Write pixel value scaling info
		//	if(param.getOriginalGlobalScaleInfo() != null){
		//		Element originalGlobalScaleInfoNode =
		//			new Element(MicroscopyXMLTags.OriginalGlobalScaleInfoTag);
		//		originalGlobalScaleInfoNode.setAttribute(
		//				MicroscopyXMLTags.OriginalGlobalScaleInfoMinTag,
		//			String.valueOf(param.getOriginalGlobalScaleInfo().originalGlobalScaledMin));
		//		originalGlobalScaleInfoNode.setAttribute(
		//				MicroscopyXMLTags.OriginalGlobalScaleInfoMaxTag,
		//				String.valueOf(param.getOriginalGlobalScaleInfo().originalGlobalScaledMax));
		//		originalGlobalScaleInfoNode.setAttribute(
		//				MicroscopyXMLTags.OriginalGlobalScaleInfoScaleTag,
		//				String.valueOf(param.getOriginalGlobalScaleInfo().originalScaleFactor));
		//		originalGlobalScaleInfoNode.setAttribute(
		//				MicroscopyXMLTags.OriginalGlobalScaleInfoOffsetTag,
		//				String.valueOf(param.getOriginalGlobalScaleInfo().originalOffsetFactor));
		//		frapDataNode.addContent(originalGlobalScaleInfoNode);
		//	}
		// We assume saving ROI rings takes 5% of the total progress
		if(progressListener != null){progressListener.setProgress(100);}
		return frapDataNode;
	}

	//commented loading old parameters
	/*private static Element getXML(FRAPStudy.FRAPModelParameters param) throws XmlParseException, cbit.vcell.parser.ExpressionException {

	Element frapModelParametersNode = new Element(MicroscopyXMLTags.FRAPModelParametersTag);
	//get initial parameters
	if (param.getIniModelParameters()!=null){
		frapModelParametersNode.addContent( getXML(param.getIniModelParameters()));
	}
	//get pure diffusion parameters
	if (param.getPureDiffModelParameters() != null) {
		frapModelParametersNode.addContent( getXML(param.getPureDiffModelParameters()));
	}
	//get reaction diffusion parameters
	if (param.getReacDiffModelParameters() != null) {
		frapModelParametersNode.addContent( getXML(param.getReacDiffModelParameters()));
	}
	return frapModelParametersNode;
}

private static Element getXML(FRAPStudy.InitialModelParameters param) throws XmlParseException, cbit.vcell.parser.ExpressionException
{
	Element iniParamNode = new Element(MicroscopyXMLTags.FRAPInitialParametersTag);

	if (param.monitorBleachRate!=null){
		iniParamNode.setAttribute(MicroscopyXMLTags.BleachWhileMonitoringTauAttrTag, param.monitorBleachRate.toString());
	}
	if (param.diffusionRate!=null){
		iniParamNode.setAttribute(MicroscopyXMLTags.RecoveryDiffusionRateAttrTag, param.diffusionRate.toString());
	}
	if (param.mobileFraction!=null){
		iniParamNode.setAttribute(MicroscopyXMLTags.MobileFractionAttrTag, param.mobileFraction.toString());
	}
	if (param.startingIndexForRecovery!=null){
		iniParamNode.setAttribute(MicroscopyXMLTags.StartingIndexForRecoveryAttrTag, param.startingIndexForRecovery.toString());
	}
	return iniParamNode;
}

private static Element getXML(FRAPStudy.PureDiffusionModelParameters param) throws XmlParseException, cbit.vcell.parser.ExpressionException
{
	Element pureDiffParamNode = new Element(MicroscopyXMLTags.FRAPPureDiffusionParametersTag);

	if (param.monitorBleachRate!=null){
		pureDiffParamNode.setAttribute(MicroscopyXMLTags.BleachWhileMonitoringTauAttrTag, param.monitorBleachRate.toString());
	}
	if (param.primaryDiffusionRate!=null){
		pureDiffParamNode.setAttribute(MicroscopyXMLTags.PrimaryRateAttrTag, param.primaryDiffusionRate.toString());
	}
	if (param.primaryMobileFraction!=null){
		pureDiffParamNode.setAttribute(MicroscopyXMLTags.PrimaryFractionAttTag, param.primaryMobileFraction.toString());
	}
	if (param.isSecondaryDiffusionApplied != null && param.isSecondaryDiffusionApplied.booleanValue())
	{
		pureDiffParamNode.setAttribute(MicroscopyXMLTags.isSecondDiffAppliedAttTag, new Boolean(true).toString());
		if(param.secondaryDiffusionRate != null)
		{
			pureDiffParamNode.setAttribute(MicroscopyXMLTags.SecondRateAttrTag, param.secondaryDiffusionRate.toString());
		}
		if(param.secondaryMobileFraction != null)
		{
			pureDiffParamNode.setAttribute(MicroscopyXMLTags.SecondFractionAttTag, param.secondaryMobileFraction.toString());
		}
	}
	else
	{
		pureDiffParamNode.setAttribute(MicroscopyXMLTags.isSecondDiffAppliedAttTag, new Boolean(false).toString());
	}
	return pureDiffParamNode;
}

private static Element getXML(FRAPStudy.ReactionDiffusionModelParameters param) throws XmlParseException, cbit.vcell.parser.ExpressionException
{
	Element reacDiffParamNode = new Element(MicroscopyXMLTags.FRAPReactionDiffusionParametersTag);

	if (param.freeDiffusionRate!=null){
		reacDiffParamNode.setAttribute(MicroscopyXMLTags.FreeDiffusionRateAttTag, param.freeDiffusionRate.toString());
	}
	if (param.freeMobileFraction!=null){
		reacDiffParamNode.setAttribute(MicroscopyXMLTags.FreeMobileFractionAttTag, param.freeMobileFraction.toString());
	}
	if (param.complexDiffusionRate!=null){
		reacDiffParamNode.setAttribute(MicroscopyXMLTags.ComplexDiffusionRateAttTag, param.complexDiffusionRate.toString());
	}
	if (param.complexMobileFraction!=null){
		reacDiffParamNode.setAttribute(MicroscopyXMLTags.ComplexMobileFractionAttTag, param.complexMobileFraction.toString());
	}
	if (param.monitorBleachRate!=null){
		reacDiffParamNode.setAttribute(MicroscopyXMLTags.BleachWhileMonitoringTauAttrTag, param.monitorBleachRate.toString());
	}
	if (param.bsConcentration!=null){
		reacDiffParamNode.setAttribute(MicroscopyXMLTags.BindingSiteConcentrationAttTag, param.bsConcentration.toString());
	}
	if (param.reacOnRate!=null){
		reacDiffParamNode.setAttribute(MicroscopyXMLTags.ReactionOnRateAttTag, param.reacOnRate.toString());
	}
	if (param.reacOffRate!=null){
		reacDiffParamNode.setAttribute(MicroscopyXMLTags.ReactionOffRateAttTag, param.reacOffRate.toString());
	}
	return reacDiffParamNode;
}*/
	/**
	 * Method getXML.
	 * @param param ExternalDataIdentifier
	 * @return Element
	 */
	private static Element getXML(ExternalDataIdentifier param){
		Element externalDataIdentifierNode = new Element(MicroscopyXMLTags.ExternalDataIdentifierTag);
		externalDataIdentifierNode.setAttribute(XMLTags.NameAttrTag,param.getName());
		externalDataIdentifierNode.setAttribute(XMLTags.KeyValueAttrTag,param.getKey().toString());
		externalDataIdentifierNode.setAttribute(MicroscopyXMLTags.OwnerNameAttrTag,param.getOwner().getName());
		externalDataIdentifierNode.setAttribute(XMLTags.OwnerKeyAttrTag,param.getOwner().getID().toString());
		return externalDataIdentifierNode;
	}

	/**
	 * This method returns a XML representation for a Biomodel object.
	 * Creation date: (2/14/2001 3:41:13 PM)
	 * @param param cbit.vcell.biomodel.BioModel
	 * @return Element
	 * @throws XmlParseException
	 * @throws cbit.vcell.parser.ExpressionException
	 */
	private static Element getXML(FRAPStudy param,Xmlproducer vcellXMLProducer, ClientTaskStatusSupport progressListener,boolean bSaveCompressed) throws XmlParseException, cbit.vcell.parser.ExpressionException {

		Element frapStudyNode = new Element(MicroscopyXMLTags.FRAPStudyTag);
		//name
		if (param.getName()!=null){
			String name = param.getName();
			frapStudyNode.setAttribute(XMLTags.NameAttrTag, name);
		}
		VCellSoftwareVersion vCellSoftwareVersion = VCellSoftwareVersion.fromSystemProperty();
		if(vCellSoftwareVersion != null && vCellSoftwareVersion.getSoftwareVersionString() != null){
			frapStudyNode.setAttribute(XMLTags.SoftwareVersionAttrTag,vCellSoftwareVersion.getSoftwareVersionString());
		}
		//originalImageFilePath
		if (param.getOriginalImageFilePath()!=null && param.getOriginalImageFilePath().length()>0) {
			String name = param.getOriginalImageFilePath();
			frapStudyNode.setAttribute(MicroscopyXMLTags.OriginalImagePathAttrTag, name);
		}
		//Description
		if (param.getDescription()!=null && param.getDescription().length()>0) {
			Element annotationElem = new Element(XMLTags.AnnotationTag);
			annotationElem.setText(mangle(param.getDescription()));
			frapStudyNode.addContent(annotationElem);
		}
		//starting index for recovery
		if (param.getStartingIndexForRecovery() != null) {
			Element indexElem = new Element(MicroscopyXMLTags.StartingIndexForRecoveryTag);
			indexElem.setText(mangle(param.getStartingIndexForRecovery().toString()));
			frapStudyNode.addContent(indexElem);
		}
		//Get frap best model index
		if(param.getBestModelIndex() != null)
		{
			Element bestModelIdxElem = new Element(MicroscopyXMLTags.BestModelIndexTag);
			bestModelIdxElem.setText(mangle(param.getBestModelIndex().toString()));
			frapStudyNode.addContent(bestModelIdxElem);
		}
		//Get selected ROIs
		if(param.getSelectedROIsForErrorCalculation() != null && param.getSelectedROIsForErrorCalculation().length >0)
		{
			Element selectedROIsNode = new Element(MicroscopyXMLTags.SelectedROIsTag);
			boolean[] selectedROIs = param.getSelectedROIsForErrorCalculation();
			String rowText = "";
			for (int i = 0; i < selectedROIs.length; i++){
				if (i > 0){
					rowText += " ";
				}
				rowText += selectedROIs[i];
			}
			selectedROIsNode.addContent(rowText);
			frapStudyNode.addContent(selectedROIsNode);
		}
		//Get frap models
		if(param.getModels() != null && param.getModels().length > 0)
		{
			Element frapModelsNode = new Element(MicroscopyXMLTags.FrapModelsTag);
			if(param.getModels()[FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT] != null)
			{
				frapModelsNode.addContent(getXML(param.getModels()[FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT]));
			}
			if(param.getModels()[FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS] != null)
			{
				frapModelsNode.addContent(getXML(param.getModels()[FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS]));
			}
			if(param.getModels()[FRAPModel.IDX_MODEL_REACTION_OFF_RATE] != null)
			{
				frapModelsNode.addContent(getXML(param.getModels()[FRAPModel.IDX_MODEL_REACTION_OFF_RATE]));
			}
			if(param.getModels()[FRAPModel.IDX_MODEL_DIFF_BINDING] != null)
			{
				frapModelsNode.addContent(getXML(param.getModels()[FRAPModel.IDX_MODEL_DIFF_BINDING]));
			}
			frapStudyNode.addContent(frapModelsNode);
		}

		//Get ExternalDataIdentifier (for timeseries)
		if (param.getFrapDataExternalDataInfo()!=null){
			Element imageDatasetEDINode = new Element(MicroscopyXMLTags.ImageDatasetExternalDataInfoTag);
			imageDatasetEDINode.setAttribute(MicroscopyXMLTags.FilenameAttrTag,param.getFrapDataExternalDataInfo().getFilename());
			imageDatasetEDINode.addContent(getXML(param.getFrapDataExternalDataInfo().getExternalDataIdentifier()) );
			frapStudyNode.addContent( imageDatasetEDINode );
		}
		//Get ExternalDataIdentifier (for ROIs)
		if (param.getRoiExternalDataInfo()!=null){
			Element roiEDINode = new Element(MicroscopyXMLTags.ROIExternalDataInfoTag);
			roiEDINode.setAttribute(MicroscopyXMLTags.FilenameAttrTag,param.getRoiExternalDataInfo().getFilename());
			roiEDINode.addContent( getXML(param.getRoiExternalDataInfo().getExternalDataIdentifier()) );
			frapStudyNode.addContent( roiEDINode );
		}
		//Get Reference Data (for reference data)
		if (param.getStoredRefData()!=null){
			frapStudyNode.addContent( getXML(param.getStoredRefData(), MicroscopyXMLTags.ReferenceDataTag) );
		}
		//Get profile data for one diffusing component (list of profile data, one for each parameter)
		if(param.getProfileData_oneDiffComponent()!= null)
		{
			frapStudyNode.addContent(getXML(param.getProfileData_oneDiffComponent(), MicroscopyXMLTags.ListOfProfileData_OneDiffTag));
		}
		if(param.getProfileData_twoDiffComponents()!= null)
		{
			frapStudyNode.addContent(getXML(param.getProfileData_twoDiffComponents(), MicroscopyXMLTags.ListOfProfileData_TwoDiffTag));
		}
		if(param.getProfileData_reactionOffRate()!= null)
		{
			frapStudyNode.addContent(getXML(param.getProfileData_reactionOffRate(), MicroscopyXMLTags.ListOfProfileData_ReacOffRateTag));
		}
		//Get BioModel
		if (param.getBioModel()!=null){
			frapStudyNode.addContent( vcellXMLProducer.getXML(param.getBioModel()) );
		}
		//Get FrapData
		if ( param.getFrapData()!=null ){
			frapStudyNode.addContent( getXML(param.getFrapData(),vcellXMLProducer,progressListener,bSaveCompressed) );
		}

		return frapStudyNode;
	}

	private static Element getXML(FRAPModel param)
	{
		if(param.getModelIdentifer().equals(FRAPModel.MODEL_TYPE_ARRAY[FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT]))
		{
			Element diffOneModelNode = new Element(MicroscopyXMLTags.DiffustionWithOneComponentModelTag);
			diffOneModelNode.setAttribute(MicroscopyXMLTags.ModelIdentifierAttTag, param.getModelIdentifer());
			//add model parameters.
			if(param.getModelParameters() != null)
			{
				diffOneModelNode.addContent(getXML(param.getModelParameters()));
			}
			return diffOneModelNode;
		}
		else if(param.getModelIdentifer().equals(FRAPModel.MODEL_TYPE_ARRAY[FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS]))
		{
			Element diffTwoModelNode = new Element(MicroscopyXMLTags.DiffustionWithTwoComponentsModelTag);
			diffTwoModelNode.setAttribute(MicroscopyXMLTags.ModelIdentifierAttTag, param.getModelIdentifer());
			if(param.getModelParameters() != null)
			{
				diffTwoModelNode.addContent(getXML(param.getModelParameters()));
			}
			return diffTwoModelNode;
		}
		else if(param.getModelIdentifer().equals(FRAPModel.MODEL_TYPE_ARRAY[FRAPModel.IDX_MODEL_REACTION_OFF_RATE]))
		{
			Element reacOffRateModelNode = new Element(MicroscopyXMLTags.ReactionDominantOffRateModelTag);
			reacOffRateModelNode.setAttribute(MicroscopyXMLTags.ModelIdentifierAttTag, param.getModelIdentifer());
			if(param.getModelParameters() != null)
			{
				reacOffRateModelNode.addContent(getXML(param.getModelParameters()));
			}
			return reacOffRateModelNode;
		}
		else if(param.getModelIdentifer().equals(FRAPModel.MODEL_TYPE_ARRAY[FRAPModel.IDX_MODEL_DIFF_BINDING]))
		{
			Element reacDiffModelNode = new Element(MicroscopyXMLTags.DiffustionReactionModelTag);
			reacDiffModelNode.setAttribute(MicroscopyXMLTags.ModelIdentifierAttTag, param.getModelIdentifer());
			if(param.getModelParameters() != null)
			{
				reacDiffModelNode.addContent(getXML(param.getModelParameters()));
			}
			if(param.getTimepoints() != null && param.getTimepoints().length > 0)
			{
				reacDiffModelNode.addContent(getXML(param.getTimepoints()));
			}
			if(param.getData() != null && param.getData().length > 0)
			{
				boolean[] selectedROIs = new boolean[FRAPData.VFRAP_ROI_ENUM.values().length];
				Arrays.fill(selectedROIs, true);
				SimpleReferenceData diffReacData = FRAPOptimizationUtils.doubleArrayToSimpleRefData(param.getData(), param.getTimepoints(), 0, selectedROIs);
				reacDiffModelNode.addContent(getXML(diffReacData, MicroscopyXMLTags.ModelDataTag)); //TODO: store as simple reference data
			}
			return reacDiffModelNode;
		}

		return null;
	}

	private static Element getXML(Parameter[] params)
	{
		Element parametersNode = new Element(MicroscopyXMLTags.ModelParametersTag);
		if(params.length == FRAPModel.NUM_MODEL_PARAMETERS_ONE_DIFF)
		{
			parametersNode.setAttribute(MicroscopyXMLTags.PrimaryRateAttrTag, params[FRAPModel.INDEX_PRIMARY_DIFF_RATE].getInitialGuess() + "");
			parametersNode.setAttribute(MicroscopyXMLTags.PrimaryFractionAttTag, params[FRAPModel.INDEX_PRIMARY_FRACTION].getInitialGuess() + "");
			parametersNode.setAttribute(MicroscopyXMLTags.BleachWhileMonitoringTauAttrTag, params[FRAPModel.INDEX_BLEACH_MONITOR_RATE].getInitialGuess() + "");
		}
		else if(params.length == FRAPModel.NUM_MODEL_PARAMETERS_TWO_DIFF)
		{
			parametersNode.setAttribute(MicroscopyXMLTags.PrimaryRateAttrTag, params[FRAPModel.INDEX_PRIMARY_DIFF_RATE].getInitialGuess() + "");
			parametersNode.setAttribute(MicroscopyXMLTags.PrimaryFractionAttTag, params[FRAPModel.INDEX_PRIMARY_FRACTION].getInitialGuess() + "");
			parametersNode.setAttribute(MicroscopyXMLTags.BleachWhileMonitoringTauAttrTag, params[FRAPModel.INDEX_BLEACH_MONITOR_RATE].getInitialGuess() + "");
			parametersNode.setAttribute(MicroscopyXMLTags.SecondRateAttrTag, params[FRAPModel.INDEX_SECONDARY_DIFF_RATE].getInitialGuess() + "");
			parametersNode.setAttribute(MicroscopyXMLTags.SecondFractionAttTag, params[FRAPModel.INDEX_SECONDARY_FRACTION].getInitialGuess() + "");
		}else if(params.length == FRAPModel.NUM_MODEL_PARAMETERS_REACTION_OFF_RATE)
		{
			parametersNode.setAttribute(MicroscopyXMLTags.BleachWhileMonitoringTauAttrTag, params[FRAPModel.INDEX_BLEACH_MONITOR_RATE].getInitialGuess() + "");
			parametersNode.setAttribute(MicroscopyXMLTags.BindingSiteConcentrationAttTag, params[FRAPModel.INDEX_BINDING_SITE_CONCENTRATION].getInitialGuess() + "");
			parametersNode.setAttribute(MicroscopyXMLTags.ReactionOffRateAttTag, params[FRAPModel.INDEX_OFF_RATE].getInitialGuess() + "");
		}
		else if(params.length == FRAPModel.NUM_MODEL_PARAMETERS_BINDING)
		{
			parametersNode.setAttribute(MicroscopyXMLTags.PrimaryRateAttrTag, params[FRAPModel.INDEX_PRIMARY_DIFF_RATE].getInitialGuess() + "");
			parametersNode.setAttribute(MicroscopyXMLTags.PrimaryFractionAttTag, params[FRAPModel.INDEX_PRIMARY_FRACTION].getInitialGuess() + "");
			parametersNode.setAttribute(MicroscopyXMLTags.BleachWhileMonitoringTauAttrTag, params[FRAPModel.INDEX_BLEACH_MONITOR_RATE].getInitialGuess() + "");
			parametersNode.setAttribute(MicroscopyXMLTags.SecondRateAttrTag, params[FRAPModel.INDEX_SECONDARY_DIFF_RATE].getInitialGuess() + "");
			parametersNode.setAttribute(MicroscopyXMLTags.SecondFractionAttTag, params[FRAPModel.INDEX_SECONDARY_FRACTION].getInitialGuess() + "");
			parametersNode.setAttribute(MicroscopyXMLTags.BindingSiteConcentrationAttTag, params[FRAPModel.INDEX_BINDING_SITE_CONCENTRATION].getInitialGuess() + "");
			parametersNode.setAttribute(MicroscopyXMLTags.ReactionOnRateAttTag, params[FRAPModel.INDEX_ON_RATE].getInitialGuess() + "");
			parametersNode.setAttribute(MicroscopyXMLTags.ReactionOffRateAttTag, params[FRAPModel.INDEX_OFF_RATE].getInitialGuess() + "");
		}
		return parametersNode;
	}

	private static Element getXML(double[] timePoints)
	{
		Element timePointsNode = new Element(MicroscopyXMLTags.ModelTimePointsTag);
		timePointsNode.setAttribute(MicroscopyXMLTags.ModelTimePointsLengthAttTag, Integer.toString(timePoints.length));
		String timeText = "";
		for(int i=0; i<timePoints.length; i++)
		{
			if (i>0){
				timeText += " ";
			}
			timeText += timePoints[i];
		}
		timePointsNode.addContent(timeText);
		return timePointsNode;
	}

	private static Element getXML(ProfileData[] profileData, String profileDataTag)
	{
		Element listProfileData = new Element(profileDataTag);

		for(int i=0; i<profileData.length; i++)
		{
			listProfileData.addContent(getXML(profileData[i]));
		}
		return listProfileData;
	}

	private static Element getXML(ProfileData profileData)
	{
		Element profileDataNode = new Element(MicroscopyXMLTags.ProfileDataTag);
		ArrayList<ProfileDataElement> profileDataElements = profileData.getProfileDataElements();
		for(int i=0; i<profileDataElements.size(); i++)
		{
			profileDataNode.addContent(getXML(profileDataElements.get(i)));
		}
		return profileDataNode;
	}

	private static Element getXML(ProfileDataElement profileDataElement)
	{
		Element profileDataElementNode = new Element(MicroscopyXMLTags.ProfieDataElementTag);
		profileDataElementNode.setAttribute(MicroscopyXMLTags.profileDataElementParameterNameAttrTag, profileDataElement.getParamName());
		profileDataElementNode.setAttribute(MicroscopyXMLTags.profileDataElementParameterValueAttrTag, profileDataElement.getParameterValue()+"");
		profileDataElementNode.setAttribute(MicroscopyXMLTags.profileDataElementLikelihoodAttrTag, profileDataElement.getLikelihood()+"");
		Parameter[] parameters = profileDataElement.getBestParameters();
		for(int i = 0; i < parameters.length; i++)
		{
			if(parameters[i] != null)//some of parameters in reaction off rate model are null.
			{
				profileDataElementNode.addContent(getXML(parameters[i]));
			}
		}
		return profileDataElementNode;
	}

	private static Element getXML(Parameter parameter)
	{
		Element parameterNode = new Element(OptXmlTags.Parameter_Tag);
		parameterNode.setAttribute(OptXmlTags.ParameterName_Attr, parameter.getName());
		parameterNode.setAttribute(OptXmlTags.ParameterLow_Attr, parameter.getLowerBound()+"");
		parameterNode.setAttribute(OptXmlTags.ParameterHigh_Attr, parameter.getUpperBound()+"");
		parameterNode.setAttribute(OptXmlTags.ParameterInit_Attr, parameter.getInitialGuess()+"");
		parameterNode.setAttribute(OptXmlTags.ParameterScale_Attr, parameter.getScale()+"");
		return parameterNode;
	}

	private static Element getXML(SimpleReferenceData referenceData, String referenDataTag)
	{
		Element referenceDataElement = new Element(referenDataTag);
		referenceDataElement.setAttribute(ParameterEstimationTaskXMLPersistence.NumRowsAttribute,Integer.toString(referenceData.getNumDataRows()));
		referenceDataElement.setAttribute(ParameterEstimationTaskXMLPersistence.NumColumnsAttribute,Integer.toString(referenceData.getNumDataColumns()));

		Element dataColumnListElement = new Element(ParameterEstimationTaskXMLPersistence.DataColumnListTag);
		for (int i = 0; i < referenceData.getColumnNames().length; i++){
			Element dataColumnElement = new Element(ParameterEstimationTaskXMLPersistence.DataColumnTag);
			dataColumnElement.setAttribute(ParameterEstimationTaskXMLPersistence.NameAttribute,referenceData.getColumnNames()[i]);
			dataColumnElement.setAttribute(ParameterEstimationTaskXMLPersistence.WeightAttribute,Double.toString(referenceData.getColumnWeights()[i]));
			dataColumnListElement.addContent(dataColumnElement);
		}
		referenceDataElement.addContent(dataColumnListElement);

		Element dataRowListElement = new Element(ParameterEstimationTaskXMLPersistence.DataRowListTag);
		for (int i = 0; i < referenceData.getNumDataRows(); i++){
			Element dataRowElement = new Element(ParameterEstimationTaskXMLPersistence.DataRowTag);
			String rowText = "";
			for (int j = 0; j < referenceData.getNumDataColumns(); j++){
				if (j>0){
					rowText += " ";
				}
				rowText += referenceData.getDataByRow(i)[j];
			}
			dataRowElement.addContent(rowText);
			dataRowListElement.addContent(dataRowElement);
		}
		referenceDataElement.addContent(dataRowListElement);

		return referenceDataElement;
	}//get simple reference data element

}
