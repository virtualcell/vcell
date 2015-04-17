/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */
package org.vcell.inversepde.microscopy;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.zip.DeflaterOutputStream;

import org.jdom.Element;
import org.vcell.inversepde.InverseProblem;
import org.vcell.inversepde.LinearResponseModel;
import org.vcell.inversepde.MembraneBasis;
import org.vcell.inversepde.SpatialBasisFunctions;
import org.vcell.inversepde.VolumeBasis;
import org.vcell.inversepde.microscopy.ROIImage.ROIImageComponent;
import org.vcell.util.Hex;
import org.vcell.util.document.ExternalDataIdentifier;
import org.vcell.util.document.VCDocument;

import cbit.image.ImageException;
import cbit.util.xml.XmlUtil;
import cbit.vcell.VirtualMicroscopy.ImageDataset;
import cbit.vcell.VirtualMicroscopy.UShortImage;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.modelopt.ParameterEstimationTaskXMLPersistence;
import cbit.vcell.opt.SimpleReferenceData;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.simdata.DataSetControllerImpl;
import cbit.vcell.xml.XMLTags;
import cbit.vcell.xml.XmlParseException;
import cbit.vcell.xml.Xmlproducer;

/**
 * This class concentrates all the XML production code from Java objects.
 * Creation date: (2/14/2001 3:40:30 PM)
 * @author schaff
 * @version $Revision: 1.0 $
 */
public class InverseProblemXmlproducer {

	public static void writeXMLFile(FRAPStudy frapStudy,File outputFile,boolean bPrintKeys,
			DataSetControllerImpl.ProgressListener progressListener,boolean bSaveCompressed) throws Exception{
		
		Xmlproducer vcellXMLProducer = new Xmlproducer(bPrintKeys);
		Element root = InverseProblemXmlproducer.getXML(frapStudy,vcellXMLProducer,progressListener,bSaveCompressed);
		String xmlString = XmlUtil.xmlToString(root);
		java.io.FileWriter fileWriter = new java.io.FileWriter(outputFile);
		fileWriter.write(xmlString);
		fileWriter.flush();
		fileWriter.close();

	}

	public static void writeXMLFile(InverseProblem inverseProblem,File outputFile,boolean bPrintKeys,
			DataSetControllerImpl.ProgressListener progressListener,boolean bSaveCompressed) throws Exception{
		
		Xmlproducer vcellXMLProducer = new Xmlproducer(bPrintKeys);
		Element root = InverseProblemXmlproducer.getXML(inverseProblem,vcellXMLProducer,progressListener,bSaveCompressed);
		String xmlString = XmlUtil.xmlToString(root);
		java.io.FileWriter fileWriter = new java.io.FileWriter(outputFile);
		fileWriter.write(xmlString);
		fileWriter.flush();
		fileWriter.close();

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
 * @return org.jdom.Element
 * @throws XmlParseException
 */
private static org.jdom.Element getXML(UShortImage param,Xmlproducer vcellXMLProducer,boolean bSaveCompressed) throws XmlParseException{
		org.jdom.Element image = new org.jdom.Element(InverseProblemXMLTags.UShortImageTag);

		//add atributes
		if (param.getName()!=null && param.getName().length()>0){
			image.setAttribute(XMLTags.NameAttrTag, mangle(param.getName()));
		}

		//Add annotation
		if (param.getDescription()!=null && param.getDescription().length()>0) {
			org.jdom.Element annotationElement = new org.jdom.Element(XMLTags.AnnotationTag);
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
		org.jdom.Element imagedata = new org.jdom.Element(XMLTags.ImageDataTag);
		
		//Get imagedata attributes
		imagedata.setAttribute(XMLTags.XAttrTag, String.valueOf(aNumX));
		imagedata.setAttribute(XMLTags.YAttrTag, String.valueOf(aNumY));
		imagedata.setAttribute(XMLTags.ZAttrTag, String.valueOf(aNumZ));
		imagedata.setAttribute(XMLTags.CompressedSizeTag, String.valueOf(compressedPixels.length));
		//Get imagedata content
		imagedata.addContent(Hex.toString(compressedPixels)); //encode
		//Add imagedata to VCImage element
		image.addContent(imagedata);
		//Add PixelClass elements

		return image;
}
/**
 * This method returns a XML representation for a Biomodel object.
 * Creation date: (2/14/2001 3:41:13 PM)
 * @param param cbit.vcell.biomodel.BioModel
 * @return org.jdom.Element
 * @throws XmlParseException
 * @throws cbit.vcell.parser.ExpressionException
 */
private static org.jdom.Element getXML(ROIImage param,Xmlproducer vcellXMLProducer,boolean bSaveCompressed) throws XmlParseException, ImageException {
	
	ROIImageComponent[] rois = param.getROIs();
	//
	// write out in "VFRAP-classic" style?
	//
	if (rois.length==1 && param.getName().equals(rois[0].getName())){
		Element roiNode = new Element(InverseProblemXMLTags.ROITag);
		roiNode.setAttribute(InverseProblemXMLTags.ROITypeAttrTag,rois[0].getName());
		roiNode.addContent( getXML(param.getMaskImage(),vcellXMLProducer,bSaveCompressed) );
		return roiNode;
	}else{
		org.jdom.Element roiImageNode = new org.jdom.Element(InverseProblemXMLTags.ROIImageTag);
		roiImageNode.addContent( getXML(param.getMaskImage(),vcellXMLProducer,bSaveCompressed) );
		roiImageNode.setAttribute(InverseProblemXMLTags.ROIImageNameAttrTag, param.getName());
		for (int i = 0; i < rois.length; i++) {
			Element roiNode = new Element(InverseProblemXMLTags.ROITag);
			roiNode.setAttribute(InverseProblemXMLTags.ROINameAttrTag,rois[i].getName());
			roiNode.setAttribute(InverseProblemXMLTags.ROIPixelValueAttrTag,Short.toString(rois[i].getPixelValue()));
			roiImageNode.addContent(roiNode);
		}
		return roiImageNode;
	}
}

/**
 * This method returns a XML representation for a Biomodel object.
 * Creation date: (2/14/2001 3:41:13 PM)
 * @param param cbit.vcell.biomodel.BioModel
 * @return org.jdom.Element
 * @throws XmlParseException
 * @throws cbit.vcell.parser.ExpressionException
 */
private static org.jdom.Element getXML(ImageDataset param,Xmlproducer vcellXMLProducer,DataSetControllerImpl.ProgressListener progressListener,boolean bSaveCompressed) throws XmlParseException, cbit.vcell.parser.ExpressionException {
	org.jdom.Element imageDatasetNode = new org.jdom.Element(InverseProblemXMLTags.ImageDatasetTag);
	//Get ImageDataset
	UShortImage[] images = param.getAllImages();
	//added in Feb 2008, for counting saving progress
	int imageSize = images.length;
	int imageCount = 0;
	for (int i = 0; i < images.length; i++) {
		imageDatasetNode.addContent( getXML(images[i],vcellXMLProducer,bSaveCompressed) );
		imageCount++;
		if(progressListener != null){progressListener.updateProgress((double)imageCount/(double)imageSize);}	
	}
	if (param.getImageTimeStamps()!=null){
		Element timeStampListNode = new Element(InverseProblemXMLTags.TimeStampListTag);
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
 * @return org.jdom.Element
 * @throws XmlParseException
 * @throws cbit.vcell.parser.ExpressionException
 */
private static org.jdom.Element getXML(AnnotatedImageDataset_inv param,String elementTagName, Xmlproducer vcellXMLProducer,DataSetControllerImpl.ProgressListener progressListener, boolean bSaveCompressed) throws XmlParseException, ExpressionException, ImageException {
	//Creation of BioModel Node
//	private ImageDataset imageDataset = null;
//	private ArrayList<ROI> rois = new ArrayList<ROI>();

	org.jdom.Element frapDataNode = new org.jdom.Element(elementTagName);
	//Get ImageDataset
	if (param.getImageDataset()!=null){
		frapDataNode.addContent( getXML(param.getImageDataset(),vcellXMLProducer,progressListener,bSaveCompressed) );
	}
	//Get ROIs
	ROIImage[] roiImages = param.getROIImages();
	for (int i = 0; i < roiImages.length; i++) {
		frapDataNode.addContent( getXML(roiImages[i],vcellXMLProducer,bSaveCompressed) );
	}
	//Write pixel value scaling info
	if(param.getOriginalGlobalScaleInfo() != null){
		org.jdom.Element originalGlobalScaleInfoNode =
			new org.jdom.Element(InverseProblemXMLTags.OriginalGlobalScaleInfoTag);
		originalGlobalScaleInfoNode.setAttribute(
				InverseProblemXMLTags.OriginalGlobalScaleInfoMinTag,
			String.valueOf(param.getOriginalGlobalScaleInfo().getOriginalGlobalScaledMin()));
		originalGlobalScaleInfoNode.setAttribute(
				InverseProblemXMLTags.OriginalGlobalScaleInfoMaxTag,
				String.valueOf(param.getOriginalGlobalScaleInfo().getOriginalGlobalScaledMax()));
		originalGlobalScaleInfoNode.setAttribute(
				InverseProblemXMLTags.OriginalGlobalScaleInfoScaleTag,
				String.valueOf(param.getOriginalGlobalScaleInfo().getOriginalScaleFactor()));
		originalGlobalScaleInfoNode.setAttribute(
				InverseProblemXMLTags.OriginalGlobalScaleInfoOffsetTag,
				String.valueOf(param.getOriginalGlobalScaleInfo().getOriginalOffsetFactor()));
		frapDataNode.addContent(originalGlobalScaleInfoNode);
	}
	// We assume saving ROI rings takes 5% of the total progress
	if(progressListener != null){progressListener.updateProgress(1.0);}
	return frapDataNode;
}

/**
 * This method returns a XML representation for a Biomodel object.
 * Creation date: (2/14/2001 3:41:13 PM)
 * @param param cbit.vcell.biomodel.BioModel
 * @return org.jdom.Element
 * @throws XmlParseException
 * @throws cbit.vcell.parser.ExpressionException
 */
private static org.jdom.Element getXML(FRAPModelParameters param) throws XmlParseException, cbit.vcell.parser.ExpressionException {

	org.jdom.Element frapModelParametersNode = new org.jdom.Element(InverseProblemXMLTags.FRAPModelParametersTag);
//	if (param.getRecoveryTau()!=null){
//		dataAnalysisResultsNode.setAttribute(MicroscopyXMLTags.RecoveryTauAttrTag, param.getRecoveryTau().toString());
//	}
//	if (param.getBleachWidth()!=null){
//		dataAnalysisResultsNode.setAttribute(MicroscopyXMLTags.BleachWidthAttrTag, param.getBleachWidth().toString());
//	}
	if (param.getMonitorBleachRate()!=null){
		frapModelParametersNode.setAttribute(InverseProblemXMLTags.BleachWhileMonitoringTauAttrTag, param.getMonitorBleachRate().toString());
	}
	if (param.getDiffusionRate()!=null){
		frapModelParametersNode.setAttribute(InverseProblemXMLTags.RecoveryDiffusionRateAttrTag, param.getDiffusionRate().toString());
	}
	if (param.getMobileFraction()!=null){
		frapModelParametersNode.setAttribute(InverseProblemXMLTags.MobileFractionAttrTag, param.getMobileFraction().toString());
	}
//	if (param.getBleachType()!=null){
//		dataAnalysisResultsNode.setAttribute(MicroscopyXMLTags.BleachTypeAttrTag, FrapDataAnalysisResults.BLEACH_TYPE_NAMES[param.getBleachType()]);
//	}
	if (param.getStartIndexForRecovery()!=null){
		frapModelParametersNode.setAttribute(InverseProblemXMLTags.StartingIndexForRecoveryAttrTag, param.getStartIndexForRecovery().toString());
	}
//	if (param.getFitExpression()!=null){
//		dataAnalysisResultsNode.setAttribute(MicroscopyXMLTags.FitExpressionAttrTag, param.getFitExpression().infix());
//	}
	if (param.getSecondRate()!=null){
		frapModelParametersNode.setAttribute(InverseProblemXMLTags.SecondRateAttrTag, param.getSecondRate().toString());
	}
	if (param.getSecondFraction()!=null){
		frapModelParametersNode.setAttribute(InverseProblemXMLTags.SecondFractionAttTag, param.getSecondFraction().toString());
	}
	return frapModelParametersNode;
}

/**
 * Method getXML.
 * @param param ExternalDataIdentifier
 * @return Element
 */
private static Element getXML(ExternalDataIdentifier param){
	Element externalDataIdentifierNode = new Element(InverseProblemXMLTags.ExternalDataIdentifierTag);
	externalDataIdentifierNode.setAttribute(XMLTags.NameAttrTag,param.getName());
	externalDataIdentifierNode.setAttribute(XMLTags.KeyValueAttrTag,param.getKey().toString());
	externalDataIdentifierNode.setAttribute(InverseProblemXMLTags.OwnerNameAttrTag,param.getOwner().getName());
	externalDataIdentifierNode.setAttribute(XMLTags.OwnerKeyAttrTag,param.getOwner().getID().toString());
	return externalDataIdentifierNode;
}

/**
 * This method returns a XML representation for a Biomodel object.
 * Creation date: (2/14/2001 3:41:13 PM)
 * @param param cbit.vcell.biomodel.BioModel
 * @return org.jdom.Element
 * @throws XmlParseException
 * @throws cbit.vcell.parser.ExpressionException
 */
private static org.jdom.Element getXML(FRAPStudy param,Xmlproducer vcellXMLProducer,DataSetControllerImpl.ProgressListener progressListener,boolean bSaveCompressed) throws XmlParseException, ImageException, cbit.vcell.parser.ExpressionException {
	//Creation of BioModel Node
	org.jdom.Element frapStudyNode = new org.jdom.Element(InverseProblemXMLTags.FRAPStudyTag);
	if (param.getName()!=null){
		String name = param.getName();
		frapStudyNode.setAttribute(XMLTags.NameAttrTag, name);
	}
	if (param.getOriginalImageFilePath()!=null && param.getOriginalImageFilePath().length()>0) {
		String name = param.getOriginalImageFilePath();
		frapStudyNode.setAttribute(InverseProblemXMLTags.OriginalImagePathAttrTag, name);
	}
	if (param.getDescription()!=null && param.getDescription().length()>0) {
		org.jdom.Element annotationElem = new org.jdom.Element(XMLTags.AnnotationTag);
		annotationElem.setText(mangle(param.getDescription()));
		frapStudyNode.addContent(annotationElem);
	}
	
	//Get AnalysisResults
	if ( param.getFrapModelParameters()!=null ){
		frapStudyNode.addContent( getXML(param.getFrapModelParameters()) );
	}
	//Get ExternalDataIdentifier (for timeseries)
	if (param.getFrapDataExternalDataInfo()!=null){
		Element imageDatasetEDINode = new Element(InverseProblemXMLTags.ImageDatasetExternalDataInfoTag);
		imageDatasetEDINode.setAttribute(InverseProblemXMLTags.FilenameAttrTag,param.getFrapDataExternalDataInfo().getFilename());
		imageDatasetEDINode.addContent( getXML(param.getFrapDataExternalDataInfo().getExternalDataIdentifier()) );
		frapStudyNode.addContent( imageDatasetEDINode );
	}
	//Get ExternalDataIdentifier (for ROIs)
	if (param.getRoiExternalDataInfo()!=null){
		Element roiEDINode = new Element(InverseProblemXMLTags.ROIExternalDataInfoTag);
		roiEDINode.setAttribute(InverseProblemXMLTags.FilenameAttrTag,param.getRoiExternalDataInfo().getFilename());
		roiEDINode.addContent( getXML(param.getRoiExternalDataInfo().getExternalDataIdentifier()) );
		frapStudyNode.addContent( roiEDINode );
	}
	//Get ExternalDataIdentifier (for reference data)
	if (param.getRefExternalDataInfo()!=null){
		Element refEDINode = new Element(InverseProblemXMLTags.RefExternalDataInfoTag);
		refEDINode.setAttribute(InverseProblemXMLTags.FilenameAttrTag,param.getRefExternalDataInfo().getFilename());
		refEDINode.addContent( getXML(param.getRefExternalDataInfo().getExternalDataIdentifier()) );
		frapStudyNode.addContent( refEDINode );
	}
	//Get BioModel
	if (param.getBioModel()!=null){
		frapStudyNode.addContent( vcellXMLProducer.getXML(param.getBioModel()) );
	}
	//Get FrapData
	if ( param.getFrapData()!=null ){
		frapStudyNode.addContent( getXML(param.getFrapData(),InverseProblemXMLTags.FRAPDataTag,vcellXMLProducer,progressListener,bSaveCompressed) );
	}
	return frapStudyNode;
}

private static org.jdom.Element getXML(SimpleReferenceData referenceData){
	Element referenceDataElement = new Element(ParameterEstimationTaskXMLPersistence.ReferenceDataTag);
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
		double[] rowData = referenceData.getDataByRow(i);
		for (int j = 0; j < referenceData.getNumDataColumns(); j++){
			if (j>0){
				rowText += " ";
			}
			rowText += rowData[j];
		}
		dataRowElement.addContent(rowText);
		dataRowListElement.addContent(dataRowElement);
	}
	referenceDataElement.addContent(dataRowListElement);

	
	return referenceDataElement;
}

private static org.jdom.Element getXML(InverseProblem inverseProblem,Xmlproducer vcellXMLProducer,DataSetControllerImpl.ProgressListener progressListener,boolean bSaveCompressed) throws XmlParseException, ImageException, cbit.vcell.parser.ExpressionException {
	//Creation of BioModel Node
	org.jdom.Element inverseProblemNode = new org.jdom.Element(InverseProblemXMLTags.InverseProblemTag);
	if (inverseProblem.getName()!=null){
		String name = inverseProblem.getName();
		inverseProblemNode.setAttribute(XMLTags.NameAttrTag, name);
	}
	if (inverseProblem.getDescription()!=null && inverseProblem.getDescription().length()>0) {
		org.jdom.Element annotationElem = new org.jdom.Element(XMLTags.AnnotationTag);
		annotationElem.setText(mangle(inverseProblem.getDescription()));
		inverseProblemNode.addContent(annotationElem);
	}
	if ( inverseProblem.getMicroscopyData().getTimeSeriesImageData()!=null ){
		inverseProblemNode.addContent( getXML(inverseProblem.getMicroscopyData().getTimeSeriesImageData(),InverseProblemXMLTags.TimeSeriesImageData_Tag,vcellXMLProducer,progressListener,bSaveCompressed) );
	}
	if ( inverseProblem.getMicroscopyData().getPsfImageData()!=null ){
		inverseProblemNode.addContent( getXML(inverseProblem.getMicroscopyData().getPsfImageData(),InverseProblemXMLTags.PSFImageData_Tag,vcellXMLProducer,progressListener,bSaveCompressed) );
	}
	if ( inverseProblem.getMicroscopyData().getZStackImageData()!=null ){
		inverseProblemNode.addContent( getXML(inverseProblem.getMicroscopyData().getZStackImageData(),InverseProblemXMLTags.ZStackImageData_Tag,vcellXMLProducer,progressListener,bSaveCompressed) );
	}
	if (inverseProblem.getGeometry()!=null){
		inverseProblemNode.addContent( vcellXMLProducer.getXML(inverseProblem.getGeometry()) );
	}
	if (inverseProblem.getNonlinearModel()!=null){
		Element nonlinearModelNode = new Element(InverseProblemXMLTags.NonlinearModelTag);
		VCDocument nonlinearModel = inverseProblem.getNonlinearModel();
		if (nonlinearModel instanceof BioModel){
			nonlinearModelNode.addContent( vcellXMLProducer.getXML((BioModel)inverseProblem.getNonlinearModel()) );
		}else if (nonlinearModel instanceof MathModel){
			nonlinearModelNode.addContent( vcellXMLProducer.getXML((MathModel)inverseProblem.getNonlinearModel()) );
		}else{
			throw new RuntimeException("unexpected nonlinear model type "+nonlinearModel.getClass().getName());
		}
		inverseProblemNode.addContent(nonlinearModelNode);
	}
	if (inverseProblem.getLinearResponseModel()!=null){
		inverseProblemNode.addContent(getXML(inverseProblem.getLinearResponseModel(), vcellXMLProducer, progressListener, bSaveCompressed));
	}
	if (inverseProblem.getExactSolutionEDI()!=null){
		Element exactSolutionNode = new Element(InverseProblemXMLTags.ExactSolutionEDITag);
		exactSolutionNode.addContent( getXML(inverseProblem.getExactSolutionEDI()) );
		inverseProblemNode.addContent( exactSolutionNode );
	}
	if (inverseProblem.getOptParameters()!=null && inverseProblem.getOptParameters().length>0){
		Element listOfOptParametersNode = new Element(InverseProblemXMLTags.ListOfParametersTag);
		cbit.vcell.opt.Parameter[] optParameters = inverseProblem.getOptParameters();
		for (int i = 0; i < optParameters.length; i++) {
			Element parameterNode = new Element(InverseProblemXMLTags.ParameterTag);
			parameterNode.setAttribute(XMLTags.NameAttrTag, optParameters[i].getName());
			parameterNode.setAttribute(InverseProblemXMLTags.ParameterLowerBoundAttrTag, ""+optParameters[i].getLowerBound());
			parameterNode.setAttribute(InverseProblemXMLTags.ParameterUpperBoundAttrTag, ""+optParameters[i].getUpperBound());
			parameterNode.setAttribute(InverseProblemXMLTags.ParameterInitialGuessAttrTag, ""+optParameters[i].getInitialGuess());
			parameterNode.setAttribute(InverseProblemXMLTags.ParameterScaleAttrTag, ""+optParameters[i].getScale());
			listOfOptParametersNode.addContent(parameterNode);
		}
		inverseProblemNode.addContent( listOfOptParametersNode );
	}
	if (inverseProblem.getMatlabDataFileName()!=null){
		Element matlabDataNode = new Element(InverseProblemXMLTags.MatlabDataFileTag);
		matlabDataNode.setAttribute(XMLTags.NameAttrTag, inverseProblem.getMatlabDataFileName());
		inverseProblemNode.addContent(matlabDataNode);
	}
	return inverseProblemNode;
}

private static org.jdom.Element getXML(LinearResponseModel linearResponseModel,Xmlproducer vcellXMLProducer,DataSetControllerImpl.ProgressListener progressListener,boolean bSaveCompressed) throws XmlParseException, ImageException, cbit.vcell.parser.ExpressionException {
	//Creation of BioModel Node
	org.jdom.Element linearResponseModelNode = new org.jdom.Element(InverseProblemXMLTags.LinearResponseModelTag);
	if (linearResponseModel.getRefVolumeSimMathModel()!=null){
		Element refVolumeSimMathModelNode = new Element(InverseProblemXMLTags.RefVolumeSimMathModelTag);
		refVolumeSimMathModelNode.addContent( vcellXMLProducer.getXML(linearResponseModel.getRefVolumeSimMathModel()) );
		linearResponseModelNode.addContent(refVolumeSimMathModelNode);
	}
	if (linearResponseModel.getRefMembraneSimMathModel()!=null){
		Element refMembraneSimMathModelNode = new Element(InverseProblemXMLTags.RefMembraneSimMathModelTag);
		refMembraneSimMathModelNode.addContent( vcellXMLProducer.getXML(linearResponseModel.getRefMembraneSimMathModel()) );
		linearResponseModelNode.addContent(refMembraneSimMathModelNode);
	}
	if (linearResponseModel.getRefFluxSimMathModel()!=null){
		Element refFluxSimMathModelNode = new Element(InverseProblemXMLTags.RefFluxSimMathModelTag);
		refFluxSimMathModelNode.addContent( vcellXMLProducer.getXML(linearResponseModel.getRefFluxSimMathModel()) );
		linearResponseModelNode.addContent(refFluxSimMathModelNode);
	}
	if (linearResponseModel.getPsfFieldDataEDI()!=null){
		Element psfNode = new Element(InverseProblemXMLTags.PsfFieldDataEDITag);
		psfNode.addContent( getXML(linearResponseModel.getPsfFieldDataEDI()) );
		linearResponseModelNode.addContent( psfNode );
	}
	if (linearResponseModel.getImageROIFieldDataEDI()!=null){
		Element imageROINode = new Element(InverseProblemXMLTags.ImageROIFieldDataEDITag);
		imageROINode.addContent( getXML(linearResponseModel.getImageROIFieldDataEDI()) );
		linearResponseModelNode.addContent( imageROINode );
	}
	if (linearResponseModel.getBasisFunctions()!=null){
		linearResponseModelNode.addContent(getXML(linearResponseModel.getBasisFunctions(),vcellXMLProducer,progressListener,bSaveCompressed));
	}
	return linearResponseModelNode;
}


private static org.jdom.Element getXML(SpatialBasisFunctions spatialBasisFunctions,Xmlproducer vcellXMLProducer,DataSetControllerImpl.ProgressListener progressListener,boolean bSaveCompressed) throws XmlParseException, ImageException, cbit.vcell.parser.ExpressionException {
	//Creation of BioModel Node
	org.jdom.Element basisFunctionsNode = new org.jdom.Element(InverseProblemXMLTags.BasisFunctionsTag);
	if (spatialBasisFunctions.getBasisFieldDataEDI()!=null){
		Element basisFieldDataNode = new Element(InverseProblemXMLTags.BasesFieldDataEDITag);
		basisFieldDataNode.addContent( getXML(spatialBasisFunctions.getBasisFieldDataEDI()) );
		basisFunctionsNode.addContent( basisFieldDataNode );
	}
	if (spatialBasisFunctions.getVolumeBases()!=null && spatialBasisFunctions.getVolumeBases().length>0){
		Element listOfVolumeBasesNode = new Element(InverseProblemXMLTags.ListOfVolumeBasesTag);
		VolumeBasis[] volumeBases = spatialBasisFunctions.getVolumeBases();
		for (int i = 0; i < volumeBases.length; i++) {
			Element volumeBasisElement = new Element(InverseProblemXMLTags.VolumeBasisTag);
			if (volumeBases[i].getName()!=null){
				volumeBasisElement.setAttribute(XMLTags.NameAttrTag,volumeBases[i].getName());
			}
			volumeBasisElement.setAttribute(InverseProblemXMLTags.BasisIndexAttrTag,Integer.toString(volumeBases[i].getBasisIndex()));
			volumeBasisElement.setAttribute(InverseProblemXMLTags.BasisControlPointMeshIndexAttrTag,Integer.toString(volumeBases[i].getControlPointMeshIndex()));
			if (volumeBases[i].getControlPointCoord()!=null){
				Element coordElement = vcellXMLProducer.getXML(volumeBases[i].getControlPointCoord());
				volumeBasisElement.addContent(coordElement);
			}
			volumeBasisElement.setAttribute(InverseProblemXMLTags.BasisIsWithinBleachAttrTag,Boolean.toString(volumeBases[i].isWithinBleach()));
			volumeBasisElement.setAttribute(InverseProblemXMLTags.BasisImageValueAttrTag,Integer.toString(volumeBases[i].getBasisImageValue()));
			if (volumeBases[i].getSubvolumeName()!=null){
				volumeBasisElement.setAttribute(InverseProblemXMLTags.BasisSubvolumeNameAttrTag,volumeBases[i].getSubvolumeName());
			}
			listOfVolumeBasesNode.addContent(volumeBasisElement);
		}
		basisFunctionsNode.addContent(listOfVolumeBasesNode);
	}
	if (spatialBasisFunctions.getMembraneBases()!=null && spatialBasisFunctions.getMembraneBases().length>0){
		Element listOfMembraneBasesNode = new Element(InverseProblemXMLTags.ListOfMembraneBasesTag);
		MembraneBasis[] membraneBases = spatialBasisFunctions.getMembraneBases();
		for (int i = 0; i < membraneBases.length; i++) {
			Element membraneBasisElement = new Element(InverseProblemXMLTags.MembraneBasisTag);
			if (membraneBases[i].getName()!=null){
				membraneBasisElement.setAttribute(XMLTags.NameAttrTag,membraneBases[i].getName());
			}
			membraneBasisElement.setAttribute(InverseProblemXMLTags.BasisIndexAttrTag,Integer.toString(membraneBases[i].getBasisIndex()));
			membraneBasisElement.setAttribute(InverseProblemXMLTags.BasisControlPointMeshIndexAttrTag,Integer.toString(membraneBases[i].getControlPointMeshIndex()));
			if (membraneBases[i].getControlPointCoord()!=null){
				Element coordElement = vcellXMLProducer.getXML(membraneBases[i].getControlPointCoord());
				membraneBasisElement.addContent(coordElement);
			}
			membraneBasisElement.setAttribute(InverseProblemXMLTags.BasisIsWithinBleachAttrTag,Boolean.toString(membraneBases[i].isWithinBleach()));
			if (membraneBases[i].getAdjacentInsideVolumeBases()!=null){
				membraneBasisElement.setAttribute(InverseProblemXMLTags.AdjacentInsideVolumeBasisNameAttrTag,membraneBases[i].getAdjacentInsideVolumeBases().getName());
			}
			if (membraneBases[i].getInsideSubvolumeName()!=null){
				membraneBasisElement.setAttribute(InverseProblemXMLTags.InsideSubvolumeNameAttrTag,membraneBases[i].getInsideSubvolumeName());
			}
			if (membraneBases[i].getOutsideSubvolumeName()!=null){
				membraneBasisElement.setAttribute(InverseProblemXMLTags.OutsideSubvolumeNameAttrTag,membraneBases[i].getOutsideSubvolumeName());
			}
			listOfMembraneBasesNode.addContent(membraneBasisElement);
		}
		basisFunctionsNode.addContent(listOfMembraneBasesNode);
	}
	return basisFunctionsNode;
}

}