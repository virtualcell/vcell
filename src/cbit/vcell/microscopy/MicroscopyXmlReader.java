package cbit.vcell.microscopy;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.zip.InflaterInputStream;

import org.jdom.Element;
import java.util.Vector;
import cbit.vcell.modelopt.ParameterEstimationTaskXMLPersistence;
import cbit.vcell.opt.SimpleReferenceData;
import cbit.image.ImageException;

import org.vcell.util.CommentStringTokenizer;
import org.vcell.util.Extent;
import org.vcell.util.Origin;
import org.vcell.util.document.KeyValue;
import cbit.util.xml.XmlUtil;
import cbit.vcell.VirtualMicroscopy.ImageDataset;
import cbit.vcell.VirtualMicroscopy.UShortImage;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.microscopy.ROI.RoiType;
import org.vcell.util.document.User;
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
	this.vcellXMLReader = new cbit.vcell.xml.XmlReader(readKeys);
}

public static ExternalDataAndSimulationInfo getExternalDataAndSimulationInfo(File vfrapDocument) throws Exception{
	String xmlString = XmlUtil.getXMLString(vfrapDocument.getAbsolutePath());
	Element param  = XmlUtil.stringToXML(xmlString, null);
	MicroscopyXmlReader xmlReader = new MicroscopyXmlReader(true);
	
	KeyValue simulationKeyValue = null;
	ExternalDataInfo frapDataExtDatInfo = null;
	ExternalDataInfo roiExtDataInfo = null;
	Element bioModelElement = param.getChild(XMLTags.BioModelTag);
	if (bioModelElement!=null){
		BioModel bioModel  = xmlReader.vcellXMLReader.getBioModel(bioModelElement);
		if(bioModel != null){
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
private FRAPStudy.FRAPModelParameters getFRAPModelParameters(Element param) throws XmlParseException{
	String bleachWhileMonitoringStr = param.getAttributeValue(MicroscopyXMLTags.BleachWhileMonitoringTauAttrTag);
	String recoveryDiffusionRateStr = param.getAttributeValue(MicroscopyXMLTags.RecoveryDiffusionRateAttrTag);
	String mobileFractionStr = param.getAttributeValue(MicroscopyXMLTags.MobileFractionAttrTag);
	String startingIndexForRecoveryStr = param.getAttributeValue(MicroscopyXMLTags.StartingIndexForRecoveryAttrTag);
	String secondRateStr = param.getAttributeValue(MicroscopyXMLTags.SecondRateAttrTag);
	String secondFractionStr = param.getAttributeValue(MicroscopyXMLTags.SecondFractionAttTag);
	return
		new FRAPStudy.FRAPModelParameters(
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
		frapStudy.setFrapDataExternalDataInfo(getExternalDataInfo(timeSeriesExternalDataElement));
	}
	Element roiExternalDataElement = param.getChild(MicroscopyXMLTags.ROIExternalDataInfoTag);
	if (roiExternalDataElement!=null){
		frapStudy.setRoiExternalDataInfo(getExternalDataInfo(roiExternalDataElement));
	}
	Element refDataElement = param.getChild(MicroscopyXMLTags.ReferenceDataTag);
	if (refDataElement!=null){
		frapStudy.setStoredRefData(getSimpleReferenceData(refDataElement));
	}
	return frapStudy;
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
    //
    // read ReferenceData
    //
    String numRowsText = referenceDataElement.getAttributeValue(ParameterEstimationTaskXMLPersistence.NumRowsAttribute);
    String numColsText = referenceDataElement.getAttributeValue(ParameterEstimationTaskXMLPersistence.NumColumnsAttribute);
    int numCols = Integer.parseInt(numColsText);
    //
    // read columns
    //    
    String[] columnNames = new String[numCols];
    double[] columnWeights = new double[numCols];
    Element dataColumnListElement = referenceDataElement.getChild(ParameterEstimationTaskXMLPersistence.DataColumnListTag/*, ns*/);
    List<Element> dataColumnList = dataColumnListElement.getChildren(ParameterEstimationTaskXMLPersistence.DataColumnTag/*, ns*/);
    for (int i = 0; i < dataColumnList.size(); i++){
          Element dataColumnElement = dataColumnList.get(i);
          columnNames[i] = dataColumnElement.getAttributeValue(ParameterEstimationTaskXMLPersistence.NameAttribute);
          columnWeights[i] = Double.parseDouble(dataColumnElement.getAttributeValue(ParameterEstimationTaskXMLPersistence.WeightAttribute));
    }
    //
    // read rows
    //
    Vector<double[]> rowDataVector = new Vector<double[]>();
    Element dataRowListElement = referenceDataElement.getChild(ParameterEstimationTaskXMLPersistence.DataRowListTag/*, ns*/);
    List<Element> dataRowList = dataRowListElement.getChildren(ParameterEstimationTaskXMLPersistence.DataRowTag/*, ns*/);
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
}