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

import org.jdom.DataConversionException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.vcell.inversepde.InverseProblem;
import org.vcell.inversepde.LinearResponseModel;
import org.vcell.inversepde.MembraneBasis;
import org.vcell.inversepde.SpatialBasisFunctions;
import org.vcell.inversepde.VolumeBasis;
import org.vcell.inversepde.microscopy.ROIImage.ROIImageComponent;
import org.vcell.util.CommentStringTokenizer;
import org.vcell.util.Coordinate;
import org.vcell.util.Extent;
import org.vcell.util.Hex;
import org.vcell.util.Origin;
import org.vcell.util.document.ExternalDataIdentifier;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.VCellSoftwareVersion;

import cbit.image.ImageException;
import cbit.util.xml.XmlUtil;
import cbit.vcell.VirtualMicroscopy.ImageDataset;
import cbit.vcell.VirtualMicroscopy.UShortImage;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.microscopy.ExternalDataInfo;
import cbit.vcell.modelopt.ParameterEstimationTaskXMLPersistence;
import cbit.vcell.opt.Parameter;
import cbit.vcell.opt.SimpleReferenceData;
import cbit.vcell.simdata.DataSetControllerImpl;
import cbit.vcell.simdata.DataSetControllerImpl.ProgressListener;
import cbit.vcell.xml.XMLTags;
import cbit.vcell.xml.XmlBase;
import cbit.vcell.xml.XmlParseException;
/**
 * This class implements the translation of XML data into Java Vcell objects..
 * Creation date: (7/17/2000 12:22:50 PM)
 * @author schaff
 * @version $Revision: 1.0 $
 */
 
public class InverseProblemXmlReader {
	
	private cbit.vcell.xml.XmlReader vcellXMLReader = null;
	
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
public InverseProblemXmlReader(boolean readKeys) {
	super();
	this.vcellXMLReader = new cbit.vcell.xml.XmlReader(readKeys);
}

public static ExternalDataAndSimulationInfo getExternalDataAndSimulationInfo(File vfrapDocument) throws Exception{
	String xmlString = XmlUtil.getXMLString(vfrapDocument.getAbsolutePath());
	Document document  = XmlUtil.stringToXML(xmlString, null);
	Element param = document.getRootElement();
	InverseProblemXmlReader xmlReader = new InverseProblemXmlReader(true);
	
	KeyValue simulationKeyValue = null;
	ExternalDataInfo frapDataExtDatInfo = null;
	ExternalDataInfo roiExtDataInfo = null;
	Element bioModelElement = param.getChild(XMLTags.BioModelTag);
	if (bioModelElement!=null){
		BioModel bioModel  = xmlReader.vcellXMLReader.getBioModel(bioModelElement,VCellSoftwareVersion.fromSystemProperty());
		if(bioModel != null){
			simulationKeyValue = bioModel.getSimulations()[0].getKey();
		}
	}
	Element timeSeriesExternalDataElement = param.getChild(InverseProblemXMLTags.ImageDatasetExternalDataInfoTag);
	if (timeSeriesExternalDataElement!=null){
		frapDataExtDatInfo = getExternalDataInfo(timeSeriesExternalDataElement);
	}
	Element roiExternalDataElement = param.getChild(InverseProblemXMLTags.ROIExternalDataInfoTag);
	if (roiExternalDataElement!=null){
		roiExtDataInfo = getExternalDataInfo(roiExternalDataElement);
	}

	return new ExternalDataAndSimulationInfo(simulationKeyValue,frapDataExtDatInfo,roiExtDataInfo);

}

private SimpleReferenceData getSimpleReferenceData(Element referenceDataElement, Namespace ns){
	//
	// read ReferenceData
	//
	String numRowsText = referenceDataElement.getAttributeValue(ParameterEstimationTaskXMLPersistence.NumRowsAttribute);
	String numColsText = referenceDataElement.getAttributeValue(ParameterEstimationTaskXMLPersistence.NumColumnsAttribute);
	//int numRows = Integer.parseInt(numRowsText);
	int numCols = Integer.parseInt(numColsText);

	//
	// read columns
	//	
	String[] columnNames = new String[numCols];
	double[] columnWeights = new double[numCols];

	Element dataColumnListElement = referenceDataElement.getChild(ParameterEstimationTaskXMLPersistence.DataColumnListTag, ns);
	List<Element> dataColumnList = dataColumnListElement.getChildren(ParameterEstimationTaskXMLPersistence.DataColumnTag, ns);
	for (int i = 0; i < dataColumnList.size(); i++){
		Element dataColumnElement = dataColumnList.get(i);
		columnNames[i] = dataColumnElement.getAttributeValue(ParameterEstimationTaskXMLPersistence.NameAttribute);
		columnWeights[i] = Double.parseDouble(dataColumnElement.getAttributeValue(ParameterEstimationTaskXMLPersistence.WeightAttribute));
	}

	//
	// read rows
	//
	Vector<double[]> rowDataVector = new Vector<double[]>();
	Element dataRowListElement = referenceDataElement.getChild(ParameterEstimationTaskXMLPersistence.DataRowListTag, ns);
	List<Element> dataRowList = dataRowListElement.getChildren(ParameterEstimationTaskXMLPersistence.DataRowTag, ns);
	for (int i = 0; i < dataRowList.size(); i++){
		Element dataRowElement = dataRowList.get(i);
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
	
	cbit.vcell.opt.SimpleReferenceData referenceData = new cbit.vcell.opt.SimpleReferenceData(columnNames, columnWeights, rowDataVector);
	
	return referenceData;
}

/**
 * This method returns a VCIMage object from a XML representation.
 * Creation date: (3/16/2001 3:41:24 PM)
 * @param param org.jdom.Element
 * @return VCImage
 * @throws XmlParseException
 */
private FRAPModelParameters getFRAPModelParameters(Element param) throws XmlParseException{
	String bleachWhileMonitoringStr = param.getAttributeValue(InverseProblemXMLTags.BleachWhileMonitoringTauAttrTag);
	String recoveryDiffusionRateStr = param.getAttributeValue(InverseProblemXMLTags.RecoveryDiffusionRateAttrTag);
	String mobileFractionStr = param.getAttributeValue(InverseProblemXMLTags.MobileFractionAttrTag);
	String startingIndexForRecoveryStr = param.getAttributeValue(InverseProblemXMLTags.StartingIndexForRecoveryAttrTag);
	String secondRateStr = param.getAttributeValue(InverseProblemXMLTags.SecondRateAttrTag);
	String secondFractionStr = param.getAttributeValue(InverseProblemXMLTags.SecondFractionAttTag);
	return
		new FRAPModelParameters(
				startingIndexForRecoveryStr,
				recoveryDiffusionRateStr,
				bleachWhileMonitoringStr,
				mobileFractionStr,
				secondRateStr,
				secondFractionStr
			);
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
	byte[] rawBytes = Hex.toBytes(hexEncodedBytes);
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
		throw new XmlParseException("error reading image pixels: "+e.getMessage());
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
		throw new XmlParseException("error reading image: "+e.getMessage());
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
private ImageDataset getImageDataset(Element param,DataSetControllerImpl.ProgressListener progressListener) throws XmlParseException{

	
	List<Element> ushortImageElementList = param.getChildren(InverseProblemXMLTags.UShortImageTag);
	Iterator<Element> imageElementIter = ushortImageElementList.iterator();
	UShortImage[] images = new UShortImage[ushortImageElementList.size()];
	//added in Feb 2008, for counting loading progress
	int imageSize = ushortImageElementList.size();
	int imageCount = 0;
	while (imageElementIter.hasNext()){
		images[imageCount++] = getUShortImage(imageElementIter.next());
		if(progressListener != null){progressListener.updateProgress((double)imageCount/(double)imageSize);}
	}
	Element timeStampListElement = param.getChild(InverseProblemXMLTags.TimeStampListTag);
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
private ROIImageComponent getOldROI(Element param) throws XmlParseException{

	String roiTypeText = param.getAttributeValue(InverseProblemXMLTags.ROITypeAttrTag);
	
	List<Element> ushortImageElementList = param.getChildren(InverseProblemXMLTags.UShortImageTag);
	Iterator<Element> imageElementIter = ushortImageElementList.iterator();
	UShortImage[] images = new UShortImage[ushortImageElementList.size()];
	int imageIndex = 0;
	while (imageElementIter.hasNext()){
		images[imageIndex++] = getUShortImage(imageElementIter.next());
	}
	UShortImage image = null;
	if (images.length == 1){
		image = images[0];
	}else{
		short[] pixels = new short[images.length*images[0].getNumXYZ()];
		for (int i = 0; i < images.length; i++) {
			System.arraycopy(images[i].getPixels(), 0, pixels, i*images[0].getNumXYZ(), images[i].getNumXYZ());
		}
		Extent ex = images[0].getExtent();
		try {
			image = new UShortImage(pixels,images[0].getOrigin(),
					new Extent(ex.getX(),ex.getY(),ex.getZ()*images.length),
					images[0].getNumX(),images[0].getNumY(),images.length);
		}catch (ImageException e){
			e.printStackTrace(System.out);
			throw new RuntimeException(e.getMessage());
		}
	}
	ROIImage roiImage = new ROIImage(roiTypeText,image);
	ROIImageComponent rOIImageComponent = roiImage.addROI((short)-1, roiTypeText);

	return rOIImageComponent;
}


/**
 * This method returns a Biomodel object from a XML Element.
 * Creation date: (3/13/2001 12:35:00 PM)
 * @param param org.jdom.Element
 * @return cbit.vcell.biomodel.BioModel
 * @throws XmlParseException
 */
private ROIImage getROIImage(Element param) throws XmlParseException{

	String roiImageName = param.getAttributeValue(InverseProblemXMLTags.ROIImageNameAttrTag);
	
	Element ushortImageElement = param.getChild(InverseProblemXMLTags.UShortImageTag);
	UShortImage image = getUShortImage(ushortImageElement);

	ROIImage roiImage = new ROIImage(roiImageName,image);
	
	List<Element> roiElements = param.getChildren(InverseProblemXMLTags.ROITag);
	Iterator<Element> roiElementIter = roiElements.iterator();
	while (roiElementIter.hasNext()){
		Element roiElement = roiElementIter.next();
		String roiName = roiElement.getAttributeValue(InverseProblemXMLTags.ROINameAttrTag);
		String roiPixelValueString = roiElement.getAttributeValue(InverseProblemXMLTags.ROIPixelValueAttrTag);
		short pixelValue = Short.parseShort(roiPixelValueString);
		roiImage.addROI(pixelValue, roiName);
	}
	return roiImage;
}

/**
 * This method returns a Biomodel object from a XML Element.
 * Creation date: (3/13/2001 12:35:00 PM)
 * @param param org.jdom.Element
 * @return cbit.vcell.biomodel.BioModel
 * @throws XmlParseException
 */
private AnnotatedImageDataset_inv getAnnotatedImageDataset(Element param,DataSetControllerImpl.ProgressListener progressListener) throws XmlParseException{

	
	Element imageDatasetElement = param.getChild(InverseProblemXMLTags.ImageDatasetTag);
	ImageDataset imageDataset = null;
	if (imageDatasetElement!=null){
		imageDataset = getImageDataset(imageDatasetElement,progressListener);
	}
	List<Element> roiList = param.getChildren(InverseProblemXMLTags.ROIImageTag);
	ROIImage[] roiImages = null;
	
	int numROIImages = roiList.size();
	if (numROIImages>0){
		roiImages = new ROIImage[numROIImages];
		Iterator<Element> roiIter = roiList.iterator();
		int index = 0;
		while (roiIter.hasNext()){
			Element roiImageElement = roiIter.next();
			roiImages[index++] = getROIImage(roiImageElement);
		}
	}
	if (roiImages==null){
		//
		// try reading old style ROIs.
		//
		List<Element> oldRoiList = param.getChildren(InverseProblemXMLTags.ROITag);
		int numROIs = oldRoiList.size();
		roiImages = new ROIImage[numROIs];
		if (numROIs>0){
			Iterator<Element> roiIter = oldRoiList.iterator();
			int index = 0;
			while (roiIter.hasNext()){
				Element roiImageElement = roiIter.next();
				roiImages[index++] = getOldROI(roiImageElement).getROIImage();
			}
		}
	}
	AnnotatedImageDataset_inv frapData = new AnnotatedImageDataset_inv(imageDataset, roiImages);
	//Read pixel value scaling info
	Element originalGlobalScaleInfoElement = param.getChild(InverseProblemXMLTags.OriginalGlobalScaleInfoTag);
	if(originalGlobalScaleInfoElement != null){
		frapData.setOriginalGlobalScaleInfo(
			new OriginalGlobalScaleInfo(
				Integer.parseInt(originalGlobalScaleInfoElement.getAttributeValue(InverseProblemXMLTags.OriginalGlobalScaleInfoMinTag)),
				Integer.parseInt(originalGlobalScaleInfoElement.getAttributeValue(InverseProblemXMLTags.OriginalGlobalScaleInfoMaxTag)),
				Double.parseDouble(originalGlobalScaleInfoElement.getAttributeValue(InverseProblemXMLTags.OriginalGlobalScaleInfoScaleTag)),
				Double.parseDouble(originalGlobalScaleInfoElement.getAttributeValue(InverseProblemXMLTags.OriginalGlobalScaleInfoOffsetTag))
			)
		);
	}
	//After loading all the ROI rings, the progress should set to 100.
	if(progressListener != null){progressListener.updateProgress(1.0);}
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
	String ownerName = externalDataIDElement.getAttributeValue(InverseProblemXMLTags.OwnerNameAttrTag);
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
public FRAPStudy getFrapStudy(Element param,DataSetControllerImpl.ProgressListener progressListener) throws XmlParseException{

	FRAPStudy frapStudy = new FRAPStudy();
	
	//Set name
	frapStudy.setName(this.unMangle(param.getAttributeValue(XMLTags.NameAttrTag)));
	frapStudy.setOriginalImageFilePath(this.unMangle(param.getAttributeValue(InverseProblemXMLTags.OriginalImagePathAttrTag)));
	frapStudy.setDescription(param.getChildText(XMLTags.AnnotationTag));
	Element bioModelElement = param.getChild(XMLTags.BioModelTag);
	if (bioModelElement!=null){
		frapStudy.setBioModel(vcellXMLReader.getBioModel(bioModelElement,VCellSoftwareVersion.fromSystemProperty()));
	}
	Element frapDataElement = param.getChild(InverseProblemXMLTags.FRAPDataTag);
	if (frapDataElement!=null){
		frapStudy.setFrapData(getAnnotatedImageDataset(frapDataElement,progressListener));
	}
	Element frapModelParametersElement = param.getChild(InverseProblemXMLTags.FRAPModelParametersTag);
	if (frapModelParametersElement!=null){
		frapStudy.setFrapModelParameters(getFRAPModelParameters(frapModelParametersElement));
	}
	Element timeSeriesExternalDataElement = param.getChild(InverseProblemXMLTags.ImageDatasetExternalDataInfoTag);
	if (timeSeriesExternalDataElement!=null){
		frapStudy.setFrapDataExternalDataInfo(getExternalDataInfo(timeSeriesExternalDataElement));
	}
	Element roiExternalDataElement = param.getChild(InverseProblemXMLTags.ROIExternalDataInfoTag);
	if (roiExternalDataElement!=null){
		frapStudy.setRoiExternalDataInfo(getExternalDataInfo(roiExternalDataElement));
	}
	Element refExternalDataElement = param.getChild(InverseProblemXMLTags.RefExternalDataInfoTag);
	if (refExternalDataElement!=null){
		frapStudy.setRefExternalDataInfo(getExternalDataInfo(refExternalDataElement));
	}
	return frapStudy;
}

/**
 * This method returns a Biomodel object from a XML Element.
 * Creation date: (3/13/2001 12:35:00 PM)
 * @param param org.jdom.Element
 * @return cbit.vcell.biomodel.BioModel
 * @throws XmlParseException
 */
public InverseProblem getInverseProblem(Element param,DataSetControllerImpl.ProgressListener progressListener) throws XmlParseException{

	InverseProblem inverseProblem = new InverseProblem();
	
	//Set name
	inverseProblem.setName(this.unMangle(param.getAttributeValue(XMLTags.NameAttrTag)));
	inverseProblem.setDescription(param.getChildText(XMLTags.AnnotationTag));
	Element nonlinearModelElement = param.getChild(InverseProblemXMLTags.NonlinearModelTag);
	if (nonlinearModelElement!=null){
		Element mathModelElement = nonlinearModelElement.getChild(XMLTags.MathModelTag);
		if (mathModelElement!=null){
			inverseProblem.setNonlinearModel(vcellXMLReader.getMathModel(mathModelElement));
		}else{
			Element bioModelElement = nonlinearModelElement.getChild(XMLTags.BioModelTag);
			if (bioModelElement!=null){
				inverseProblem.setNonlinearModel(vcellXMLReader.getBioModel(bioModelElement,VCellSoftwareVersion.fromSystemProperty()));
			}
		}
	}
	Element linearResponseModelElement = param.getChild(InverseProblemXMLTags.LinearResponseModelTag);
	if (linearResponseModelElement!=null){
		inverseProblem.setLinearResponseModel(getLinearResponseModel(linearResponseModelElement, progressListener));
	}
	Element timeSeriesImageDataNode = param.getChild(InverseProblemXMLTags.TimeSeriesImageData_Tag);
	if (timeSeriesImageDataNode!=null){
		inverseProblem.getMicroscopyData().setTimeSeriesImageData(getAnnotatedImageDataset(timeSeriesImageDataNode,progressListener));
	}
	Element zStackImageDataNode = param.getChild(InverseProblemXMLTags.ZStackImageData_Tag);
	if (zStackImageDataNode!=null){
		inverseProblem.getMicroscopyData().setZStackImageData(getAnnotatedImageDataset(zStackImageDataNode,progressListener));
	}
	Element psfImageDataNode = param.getChild(InverseProblemXMLTags.PSFImageData_Tag);
	if (psfImageDataNode!=null){
		inverseProblem.getMicroscopyData().setPsfImageData(getAnnotatedImageDataset(psfImageDataNode,progressListener));
	}
	Element exactSolutionEDINode = param.getChild(InverseProblemXMLTags.ExactSolutionEDITag);
	if (exactSolutionEDINode!=null){
		Element externalDataIdentifierNode = exactSolutionEDINode.getChild(InverseProblemXMLTags.ExternalDataIdentifierTag);
		inverseProblem.setExactSolutionEDI(getExternalDataIdentifier(externalDataIdentifierNode));
	}
	Element listOfParametersNode = param.getChild(InverseProblemXMLTags.ListOfParametersTag);
	if (listOfParametersNode!=null){
		ArrayList<cbit.vcell.opt.Parameter> parameterList = new ArrayList<Parameter>();
		Iterator<Element> iter = listOfParametersNode.getChildren(InverseProblemXMLTags.ParameterTag).iterator();
		while (iter.hasNext()) {
			Element parameterNode = (Element) iter.next();
			try {
				String name = parameterNode.getAttributeValue(XMLTags.NameAttrTag);
				double initialGuess = parameterNode.getAttribute(InverseProblemXMLTags.ParameterInitialGuessAttrTag).getDoubleValue();
				double lowerBounds = parameterNode.getAttribute(InverseProblemXMLTags.ParameterLowerBoundAttrTag).getDoubleValue();
				double upperBounds = parameterNode.getAttribute(InverseProblemXMLTags.ParameterUpperBoundAttrTag).getDoubleValue();
				double scale = parameterNode.getAttribute(InverseProblemXMLTags.ParameterScaleAttrTag).getDoubleValue();
				cbit.vcell.opt.Parameter parameter = new cbit.vcell.opt.Parameter(name,lowerBounds,upperBounds,scale,initialGuess);
				parameterList.add(parameter);
			}catch (DataConversionException ex){
				ex.printStackTrace(System.out);
				throw new RuntimeException("error parsing parameter: "+ex.getMessage());
			}
		}
		inverseProblem.setOptParameters(parameterList.toArray(new Parameter[parameterList.size()]));
	}
	Element geometryNode = param.getChild(XMLTags.GeometryTag);
	if (geometryNode!=null){
		inverseProblem.setGeometry(vcellXMLReader.getGeometry(geometryNode));
	}
	Element matlabDataFileNode = param.getChild(InverseProblemXMLTags.MatlabDataFileTag);
	if (matlabDataFileNode!=null){
		String filename = matlabDataFileNode.getAttributeValue(XMLTags.NameAttrTag);
		inverseProblem.setMatlabDataFileName(filename);
	}
	return inverseProblem;
}

private SpatialBasisFunctions getBasisFunctions(Element param, ProgressListener progressListener) throws XmlParseException {
	SpatialBasisFunctions spatialBasisFunctions = new SpatialBasisFunctions();
	
	Element basisFieldDataEDINode = param.getChild(InverseProblemXMLTags.BasesFieldDataEDITag);
	if (basisFieldDataEDINode!=null){
		Element externalDataIdentifierNode = basisFieldDataEDINode.getChild(InverseProblemXMLTags.ExternalDataIdentifierTag);
		spatialBasisFunctions.setBasisFieldDataEDI(getExternalDataIdentifier(externalDataIdentifierNode));
	}

	Element listOfVolumeBasesNode = param.getChild(InverseProblemXMLTags.ListOfVolumeBasesTag);
	if (listOfVolumeBasesNode!=null){
		List<Element> volumeBasesElementList = listOfVolumeBasesNode.getChildren(InverseProblemXMLTags.VolumeBasisTag);
		ArrayList<VolumeBasis> volumeBases = new ArrayList<VolumeBasis>();
		for (int i = 0; i < volumeBasesElementList.size(); i++) {
			Element volumeBasisNode = volumeBasesElementList.get(i);
			VolumeBasis volumeBasis = new VolumeBasis();
			String name = volumeBasisNode.getAttributeValue(XMLTags.NameAttrTag);
			if (name!=null){
				volumeBasis.setName(name);
			}
			String basisIndexString = volumeBasisNode.getAttributeValue(InverseProblemXMLTags.BasisIndexAttrTag);
			if (basisIndexString!=null){
				volumeBasis.setBasisIndex(Integer.parseInt(basisIndexString));
			}
			String basisControlPointMeshIndexString = volumeBasisNode.getAttributeValue(InverseProblemXMLTags.BasisControlPointMeshIndexAttrTag);
			if (basisControlPointMeshIndexString!=null){
				volumeBasis.setControlPointIndex(Integer.parseInt(basisControlPointMeshIndexString));
			}
			Element coordNode = volumeBasisNode.getChild(XMLTags.CoordinateTag);
			if (coordNode!=null){
				Coordinate coord = vcellXMLReader.getCoordinate(coordNode);
				if (coord!=null){
					volumeBasis.setControlPointCoord(coord);
				}
			}
			String isWithinBleachString = volumeBasisNode.getAttributeValue(InverseProblemXMLTags.BasisIsWithinBleachAttrTag);
			if (isWithinBleachString!=null){
				volumeBasis.setWithinBleach(Boolean.parseBoolean(isWithinBleachString));
			}
			String basisImageValueString = volumeBasisNode.getAttributeValue(InverseProblemXMLTags.BasisImageValueAttrTag);
			if (basisImageValueString!=null){
				volumeBasis.setBasisImageValue(Integer.parseInt(basisImageValueString));
			}
			String basisSubvolumeNameString = volumeBasisNode.getAttributeValue(InverseProblemXMLTags.BasisSubvolumeNameAttrTag);
			if (basisSubvolumeNameString!=null){
				volumeBasis.setSubvolumeName(basisSubvolumeNameString);
			}
			volumeBases.add(volumeBasis);
		}
		spatialBasisFunctions.setVolumeBases(volumeBases.toArray(new VolumeBasis[volumeBases.size()]));
	}
	
	Element listOfMembraneBasesNode = param.getChild(InverseProblemXMLTags.ListOfMembraneBasesTag);
	if (listOfMembraneBasesNode!=null){
		List<Element> membraneBasisList = listOfMembraneBasesNode.getChildren(InverseProblemXMLTags.MembraneBasisTag);
		ArrayList<MembraneBasis> membraneBases = new ArrayList<MembraneBasis>();
		for (int i = 0; i < membraneBasisList.size(); i++) {
			Element membraneBasisNode = membraneBasisList.get(i);
			MembraneBasis membraneBasis = new MembraneBasis();
			String name = membraneBasisNode.getAttributeValue(XMLTags.NameAttrTag);
			if (name!=null){
				membraneBasis.setName(name);
			}
			String basisIndexString = membraneBasisNode.getAttributeValue(InverseProblemXMLTags.BasisIndexAttrTag);
			if (basisIndexString!=null){
				membraneBasis.setBasisIndex(Integer.parseInt(basisIndexString));
			}
			String basisControlPointMeshIndexString = membraneBasisNode.getAttributeValue(InverseProblemXMLTags.BasisControlPointMeshIndexAttrTag);
			if (basisControlPointMeshIndexString!=null){
				membraneBasis.setControlPointIndex(Integer.parseInt(basisControlPointMeshIndexString));
			}
			Element coordNode = membraneBasisNode.getChild(XMLTags.CoordinateTag);
			if (coordNode!=null){
				Coordinate coord = vcellXMLReader.getCoordinate(coordNode);
				if (coord!=null){
					membraneBasis.setControlPointCoord(coord);
				}
			}
			String isWithinBleachString = membraneBasisNode.getAttributeValue(InverseProblemXMLTags.BasisIsWithinBleachAttrTag);
			if (isWithinBleachString!=null){
				membraneBasis.setWithinBleach(Boolean.parseBoolean(isWithinBleachString));
			}
			String adjacentInsideVolumeBasisName = membraneBasisNode.getAttributeValue(InverseProblemXMLTags.AdjacentInsideVolumeBasisNameAttrTag);
			if (adjacentInsideVolumeBasisName!=null){
				VolumeBasis adjacentInsideVolumeBasis = spatialBasisFunctions.getVolumeBasesByName(adjacentInsideVolumeBasisName);
				membraneBasis.setAdjacentInsideVolumeBases(adjacentInsideVolumeBasis);
			}
			String insideSubvolumeName = membraneBasisNode.getAttributeValue(InverseProblemXMLTags.InsideSubvolumeNameAttrTag);
			if (insideSubvolumeName!=null){
				membraneBasis.setInsideSubvolumeName(insideSubvolumeName);
			}
			String outsideSubvolumeName = membraneBasisNode.getAttributeValue(InverseProblemXMLTags.OutsideSubvolumeNameAttrTag);
			if (outsideSubvolumeName!=null){
				membraneBasis.setOutsideSubvolumeName(outsideSubvolumeName);
			}
			membraneBases.add(membraneBasis);
		}
		spatialBasisFunctions.setMembraneBases(membraneBases.toArray(new MembraneBasis[membraneBases.size()]));
	}
	
	return spatialBasisFunctions;
}

private LinearResponseModel getLinearResponseModel(Element param, ProgressListener progressListener) throws XmlParseException {
		LinearResponseModel linearResponseModel = new LinearResponseModel();
	
	Element refVolumeMathModelElement = param.getChild(InverseProblemXMLTags.RefVolumeSimMathModelTag);
	if (refVolumeMathModelElement!=null){
		Element mathModelElement = refVolumeMathModelElement.getChild(XMLTags.MathModelTag);
		if (mathModelElement!=null){
			linearResponseModel.setRefVolumeSimMathModel(vcellXMLReader.getMathModel(mathModelElement));
		}
	}
	Element refMembraneMathModelElement = param.getChild(InverseProblemXMLTags.RefMembraneSimMathModelTag);
	if (refMembraneMathModelElement!=null){
		Element mathModelElement = refMembraneMathModelElement.getChild(XMLTags.MathModelTag);
		if (mathModelElement!=null){
			linearResponseModel.setRefMembraneSimMathModel(vcellXMLReader.getMathModel(mathModelElement));
		}
	}
	Element refFluxMathModelElement = param.getChild(InverseProblemXMLTags.RefFluxSimMathModelTag);
	if (refFluxMathModelElement!=null){
		Element mathModelElement = refFluxMathModelElement.getChild(XMLTags.MathModelTag);
		if (mathModelElement!=null){
			linearResponseModel.setRefFluxSimMathModel(vcellXMLReader.getMathModel(mathModelElement));
		}
	}
	
	Element psfFieldDataEDINode = param.getChild(InverseProblemXMLTags.PsfFieldDataEDITag);
	if (psfFieldDataEDINode!=null){
		Element externalDataIdentifierNode = psfFieldDataEDINode.getChild(InverseProblemXMLTags.ExternalDataIdentifierTag);
		linearResponseModel.setPsfFieldDataEDI(getExternalDataIdentifier(externalDataIdentifierNode));
	}
	
	Element imageROIFieldDataEDINode = param.getChild(InverseProblemXMLTags.ImageROIFieldDataEDITag);
	if (imageROIFieldDataEDINode!=null){
		Element externalDataIdentifierNode = imageROIFieldDataEDINode.getChild(InverseProblemXMLTags.ExternalDataIdentifierTag);
		linearResponseModel.setImageROIFieldDataEDI(getExternalDataIdentifier(externalDataIdentifierNode));
	}
	
	Element basisFunctionsNode = param.getChild(InverseProblemXMLTags.BasisFunctionsTag);
	if (basisFunctionsNode!=null){
		linearResponseModel.setBasisFunctions(getBasisFunctions(basisFunctionsNode,progressListener));
	}

	return linearResponseModel;
}

private static ExternalDataInfo getExternalDataInfo(Element extDataInfoElement){
	String filename = extDataInfoElement.getAttributeValue(InverseProblemXMLTags.FilenameAttrTag);
	Element externalDataIDElement = extDataInfoElement.getChild(InverseProblemXMLTags.ExternalDataIdentifierTag);
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
}