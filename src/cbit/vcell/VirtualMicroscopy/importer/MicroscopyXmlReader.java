/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.VirtualMicroscopy.importer;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.zip.InflaterInputStream;

import org.jdom.Element;
import org.jdom.Namespace;
import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.util.Extent;
import org.vcell.util.Origin;
import org.vcell.util.document.ExternalDataIdentifier;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

import cbit.image.ImageException;
import cbit.vcell.VirtualMicroscopy.ImageDataset;
import cbit.vcell.VirtualMicroscopy.ROI;
import cbit.vcell.VirtualMicroscopy.UShortImage;
import cbit.vcell.biomodel.BioModel;
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
	
   	
/**
 * This constructor takes a parameter to specify if the KeyValue should be ignored
 * Creation date: (3/13/2001 12:16:30 PM)
 * @param readKeys boolean
 */
public MicroscopyXmlReader(boolean readKeys) {
	super();
	this.vcellXMLReader = new XmlReader(readKeys);
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
		throw new XmlParseException("error reading image: ", e);
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
 * This method is only called by DataSymbolsPanel to get the frap image dataset
 */
public List <BioModel> getBioModels(Element vFrapRoot, Object object) {
	// TODO Auto-generated method stub
	return null;
}
public AnnotatedImageDataset getAnnotatedImageDataset(Element param/*root, frapstudy element*/, ClientTaskStatusSupport progressListener) throws XmlParseException
{
	//get frapData element
	Element frapDataElement = param.getChild(MicroscopyXMLTags.FRAPDataTag);
	
	Element imageDatasetElement = frapDataElement.getChild(MicroscopyXMLTags.ImageDatasetTag);
	ImageDataset imageDataset = null;
	if (imageDatasetElement!=null){
		imageDataset = getImageDataset(imageDatasetElement,progressListener);
	}
	
	Namespace ns = param.getNamespace();
	List<Element> roiList = frapDataElement.getChildren(MicroscopyXMLTags.ROITag);
	ROI[] rois = new ROI[0];
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
	AnnotatedImageDataset annotatedImages = new AnnotatedImageDataset(imageDataset, reorderedROIs);
	//After loading all the ROI rings, the progress should set to 100.
	if(progressListener != null){progressListener.setProgress(100);}
	return annotatedImages;
}

/**
 * The method is only called by DataSymbolsPanel to get the time point when the bleach starts
 * @param param
 * @return
 * @throws XmlParseException
 */
public int getStartindIndexForRecovery(Element param/*root, frapstudy element*/) throws XmlParseException
{
	String startIndexStr = param.getChildText(MicroscopyXMLTags.StartingIndexForRecoveryTag); 
	return new Integer(startIndexStr);
}
	
/**
* This method is only called by DataSymbolsPanel to get the primary ROIs
*/
public ROI[] getPrimaryROIs(Element param/*root, frapstudy element*/, ClientTaskStatusSupport progressListener) throws XmlParseException
{
	//get frapData element
	Element frapDataElement = param.getChild(MicroscopyXMLTags.FRAPDataTag);
	
	Namespace ns = param.getNamespace();
	List<Element> roiList = frapDataElement.getChildren(MicroscopyXMLTags.ROITag);
	ROI[] rois = null;
	int numROIs = 3;
	if (numROIs>0){
		rois = new ROI[numROIs];
		Iterator<Element> roiIter = roiList.iterator();
		int index = 0;
		while (roiIter.hasNext()){
			Element roiElement = roiIter.next();
			if(roiElement.getAttributeValue(MicroscopyXMLTags.ROITypeAttrTag).equals(AnnotatedImageDataset.VFRAP_ROI_ENUM.ROI_BLEACHED.name()) ||
			   roiElement.getAttributeValue(MicroscopyXMLTags.ROITypeAttrTag).equals(AnnotatedImageDataset.VFRAP_ROI_ENUM.ROI_BACKGROUND.name()) ||
			   roiElement.getAttributeValue(MicroscopyXMLTags.ROITypeAttrTag).equals(AnnotatedImageDataset.VFRAP_ROI_ENUM.ROI_CELL.name()))
			{
				rois[index++] = getROI(roiElement);
				if(index == 3)
				{
					break;
				}
			}
		}
	}
	//reorder ROIs according to the order of FRAPData.VFRAP_ROI_ENUM
	ROI[] reorderedROIs = AnnotatedImageDataset.reorderROIs(rois);
	return reorderedROIs;
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
 * Method unMangle.
 * @param str String
 * @return String
 */
private String unMangle(String str){
	return XmlBase.unMangle(str);
}


}
