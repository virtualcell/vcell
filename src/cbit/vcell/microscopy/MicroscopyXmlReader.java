package cbit.vcell.microscopy;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.zip.InflaterInputStream;

import org.jdom.Element;

import cbit.image.ImageException;
import cbit.sql.KeyValue;
import cbit.util.Extent;
import cbit.vcell.VirtualMicroscopy.ImageDataset;
import cbit.vcell.VirtualMicroscopy.UShortImage;
import cbit.vcell.microscopy.ROI.RoiType;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.server.User;
import cbit.vcell.simdata.DataSetControllerImpl;
import cbit.vcell.simdata.ExternalDataIdentifier;
import cbit.vcell.xml.XMLTags;
import cbit.vcell.xml.XmlBase;
import cbit.vcell.xml.XmlParseException;
/**
 * This class implements the translation of XML data into Java Vcell objects..
 * Creation date: (7/17/2000 12:22:50 PM)
 * @author schaff
 * @version $Revision: 1.0 $
 */
 
public class MicroscopyXmlReader {
	
	private cbit.vcell.xml.XmlReader vcellXMLReader = null;
    	
/**
 * This constructor takes a parameter to specify if the KeyValue should be ignored
 * Creation date: (3/13/2001 12:16:30 PM)
 * @param readKeys boolean
 */
public MicroscopyXmlReader(boolean readKeys) {
	super();
	this.vcellXMLReader = new cbit.vcell.xml.XmlReader(readKeys);
}



/**
 * This method returns a VCIMage object from a XML representation.
 * Creation date: (3/16/2001 3:41:24 PM)
 * @param param org.jdom.Element
 * @return VCImage
 * @throws XmlParseException
 */
private FRAPStudy.FRAPModelParameters getFRAPModelParameters(Element param) throws XmlParseException{
	String bleachWhileMonitoringStr = param.getAttributeValue(MicroscopyXMLTags.BleachWhileMonitoringTauAttrTag);
	String recoveryDiffusionRateStr = param.getAttributeValue(MicroscopyXMLTags.RecoveryDiffusionRateAttrTag);
	String mobileFractionStr = param.getAttributeValue(MicroscopyXMLTags.MobileFractionAttrTag);
	String startingIndexForRecoveryStr = param.getAttributeValue(MicroscopyXMLTags.StartingIndexForRecoveryAttrTag);
	String slowerRateStr = param.getAttributeValue(MicroscopyXMLTags.SlowerRateAttrTag);

	return
		new FRAPStudy.FRAPModelParameters(
				startingIndexForRecoveryStr,
				recoveryDiffusionRateStr,
				bleachWhileMonitoringStr,
				mobileFractionStr,
				slowerRateStr
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
	int UNCOMPRESSED_SIZE_BYTES = aNumX*aNumY*aNumZ*2;
	//getpixels
	String temp = tempelement.getText();
	byte[] data = cbit.util.Hex.toBytes(temp); //decode
	ByteArrayInputStream bis = new ByteArrayInputStream(data);
	InputStream iis = bis;
	if(compressSize != UNCOMPRESSED_SIZE_BYTES){
		iis  = new InflaterInputStream(bis);
	}
	DataInputStream dis = new DataInputStream(iis);
	short[] pixels = new short[aNumX*aNumY*aNumZ];
	for (int i = 0; i < pixels.length; i++) {
		try {
			pixels[i] = dis.readShort();
		} catch (IOException e) {
			e.printStackTrace();
			throw new XmlParseException("error reading image pixels: "+e.getMessage());
		}
	}
	
	Element extentElement = param.getChild(XMLTags.ExtentTag);
	Extent extent = null;
	if (extentElement!=null){
		extent = vcellXMLReader.getExtent(extentElement);
	}
	
	//set attributes
	String name = this.unMangle( param.getAttributeValue(XMLTags.NameAttrTag) );
	String annotation = param.getChildText(XMLTags.AnnotationTag);

	UShortImage newimage;
	try {
		newimage = new UShortImage(pixels,extent,aNumX,aNumY,aNumZ);
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

	
	List<Element> ushortImageElementList = param.getChildren(MicroscopyXMLTags.UShortImageTag);
	Iterator<Element> imageElementIter = ushortImageElementList.iterator();
	UShortImage[] images = new UShortImage[ushortImageElementList.size()];
	//added in Feb 2008, for counting loading progress
	int imageSize = ushortImageElementList.size();
	int imageCount = 0;
	while (imageElementIter.hasNext()){
		images[imageCount++] = getUShortImage(imageElementIter.next());
		if(progressListener != null){progressListener.updateProgress((double)imageCount/(double)imageSize);}
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

	String roiTypeText = param.getAttributeValue(MicroscopyXMLTags.ROITypeAttrTag);
	RoiType roiType = RoiType.valueOf(roiTypeText);
	
	List<Element> ushortImageElementList = param.getChildren(MicroscopyXMLTags.UShortImageTag);
	Iterator<Element> imageElementIter = ushortImageElementList.iterator();
	UShortImage[] images = new UShortImage[ushortImageElementList.size()];
	int imageIndex = 0;
	while (imageElementIter.hasNext()){
		images[imageIndex++] = getUShortImage(imageElementIter.next());
	}
	
	ROI roi = new ROI(images,roiType);

	return roi;
}

/**
 * This method returns a Biomodel object from a XML Element.
 * Creation date: (3/13/2001 12:35:00 PM)
 * @param param org.jdom.Element
 * @return cbit.vcell.biomodel.BioModel
 * @throws XmlParseException
 */
private FRAPData getFrapData(Element param,DataSetControllerImpl.ProgressListener progressListener) throws XmlParseException{

	
	Element imageDatasetElement = param.getChild(MicroscopyXMLTags.ImageDatasetTag);
	ImageDataset imageDataset = null;
	if (imageDatasetElement!=null){
		imageDataset = getImageDataset(imageDatasetElement,progressListener);
	}
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
	FRAPData frapData = new FRAPData(imageDataset, rois);
	//Read pixel value scaling info
	Element originalGlobalScaleInfoElement = param.getChild(MicroscopyXMLTags.OriginalGlobalScaleInfoTag);
	if(originalGlobalScaleInfoElement != null){
		frapData.setOriginalGlobalScaleInfo(
			new FRAPData.OriginalGlobalScaleInfo(
				Integer.parseInt(originalGlobalScaleInfoElement.getAttributeValue(MicroscopyXMLTags.OriginalGlobalScaleInfoMinTag)),
				Integer.parseInt(originalGlobalScaleInfoElement.getAttributeValue(MicroscopyXMLTags.OriginalGlobalScaleInfoMaxTag)),
				Double.parseDouble(originalGlobalScaleInfoElement.getAttributeValue(MicroscopyXMLTags.OriginalGlobalScaleInfoScaleTag)),
				Double.parseDouble(originalGlobalScaleInfoElement.getAttributeValue(MicroscopyXMLTags.OriginalGlobalScaleInfoOffsetTag))
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
private ExternalDataIdentifier getExternalDataIdentifier(Element externalDataIDElement){
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
public FRAPStudy getFrapStudy(Element param,DataSetControllerImpl.ProgressListener progressListener) throws XmlParseException{

	FRAPStudy frapStudy = new FRAPStudy();
	
	//Set name
	frapStudy.setName(this.unMangle(param.getAttributeValue(XMLTags.NameAttrTag)));
	frapStudy.setOriginalImageFilePath(this.unMangle(param.getAttributeValue(MicroscopyXMLTags.OriginalImagePathAttrTag)));
	frapStudy.setDescription(param.getChildText(XMLTags.AnnotationTag));
	Element bioModelElement = param.getChild(XMLTags.BioModelTag);
	if (bioModelElement!=null){
		frapStudy.setBioModel(vcellXMLReader.getBioModel(bioModelElement));
	}
	Element frapDataElement = param.getChild(MicroscopyXMLTags.FRAPDataTag);
	if (frapDataElement!=null){
		frapStudy.setFrapData(getFrapData(frapDataElement,progressListener));
	}
	Element frapModelParametersElement = param.getChild(MicroscopyXMLTags.FRAPModelParametersTag);
	if (frapModelParametersElement!=null){
		frapStudy.setFrapModelParameters(getFRAPModelParameters(frapModelParametersElement));
	}
	Element timeSeriesExternalDataElement = param.getChild(MicroscopyXMLTags.ImageDatasetExternalDataInfoTag);
	if (timeSeriesExternalDataElement!=null){
		String filename = timeSeriesExternalDataElement.getAttributeValue(MicroscopyXMLTags.FilenameAttrTag);
		Element externalDataIDElement = timeSeriesExternalDataElement.getChild(MicroscopyXMLTags.ExternalDataIdentifierTag);
		ExternalDataIdentifier externalDataID = getExternalDataIdentifier(externalDataIDElement);
		ExternalDataInfo timeSeriesExternalDataInfo = new ExternalDataInfo(externalDataID, filename);
		frapStudy.setFrapDataExternalDataInfo(timeSeriesExternalDataInfo);
	}
	Element roiExternalDataElement = param.getChild(MicroscopyXMLTags.ROIExternalDataInfoTag);
	if (roiExternalDataElement!=null){
		String filename = roiExternalDataElement.getAttributeValue(MicroscopyXMLTags.FilenameAttrTag);
		Element externalDataIDElement = roiExternalDataElement.getChild(MicroscopyXMLTags.ExternalDataIdentifierTag);
		ExternalDataIdentifier externalDataID = getExternalDataIdentifier(externalDataIDElement);
		ExternalDataInfo roiExternalDataInfo = new ExternalDataInfo(externalDataID, filename);
		frapStudy.setRoiExternalDataInfo(roiExternalDataInfo);
	}
	return frapStudy;
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