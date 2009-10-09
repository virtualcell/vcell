package cbit.vcell.microscopy;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.zip.DeflaterOutputStream;
import org.jdom.Element;
import org.vcell.util.document.ExternalDataIdentifier;

import cbit.util.xml.XmlUtil;
import cbit.vcell.VirtualMicroscopy.ImageDataset;
import cbit.vcell.VirtualMicroscopy.ROI;
import cbit.vcell.VirtualMicroscopy.UShortImage;
import cbit.vcell.modelopt.ParameterEstimationTaskXMLPersistence;
import cbit.vcell.opt.SimpleReferenceData;
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
public class MicroscopyXmlproducer {

public static void writeXMLFile(FRAPStudy frapStudy,File outputFile,boolean bPrintKeys,
		DataSetControllerImpl.ProgressListener progressListener,boolean bSaveCompressed) throws Exception{
	
	Xmlproducer vcellXMLProducer = new Xmlproducer(bPrintKeys);
	Element root = MicroscopyXmlproducer.getXML(frapStudy,vcellXMLProducer,progressListener,bSaveCompressed);
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
		org.jdom.Element image = new org.jdom.Element(MicroscopyXMLTags.UShortImageTag);

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
 * @return org.jdom.Element
 * @throws XmlParseException
 * @throws cbit.vcell.parser.ExpressionException
 */
private static org.jdom.Element getXML(ROI param,Xmlproducer vcellXMLProducer,boolean bSaveCompressed) throws XmlParseException, cbit.vcell.parser.ExpressionException {
	org.jdom.Element roiNode = new org.jdom.Element(MicroscopyXMLTags.ROITag);
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
 * @return org.jdom.Element
 * @throws XmlParseException
 * @throws cbit.vcell.parser.ExpressionException
 */
private static org.jdom.Element getXML(ImageDataset param,Xmlproducer vcellXMLProducer,DataSetControllerImpl.ProgressListener progressListener,boolean bSaveCompressed) throws XmlParseException, cbit.vcell.parser.ExpressionException {
	org.jdom.Element imageDatasetNode = new org.jdom.Element(MicroscopyXMLTags.ImageDatasetTag);
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
 * @return org.jdom.Element
 * @throws XmlParseException
 * @throws cbit.vcell.parser.ExpressionException
 */
private static org.jdom.Element getXML(FRAPData param,Xmlproducer vcellXMLProducer,DataSetControllerImpl.ProgressListener progressListener, boolean bSaveCompressed) throws XmlParseException, cbit.vcell.parser.ExpressionException {
	//Creation of BioModel Node
//	private ImageDataset imageDataset = null;
//	private ArrayList<ROI> rois = new ArrayList<ROI>();

	org.jdom.Element frapDataNode = new org.jdom.Element(MicroscopyXMLTags.FRAPDataTag);
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
	if(param.getOriginalGlobalScaleInfo() != null){
		org.jdom.Element originalGlobalScaleInfoNode =
			new org.jdom.Element(MicroscopyXMLTags.OriginalGlobalScaleInfoTag);
		originalGlobalScaleInfoNode.setAttribute(
				MicroscopyXMLTags.OriginalGlobalScaleInfoMinTag,
			String.valueOf(param.getOriginalGlobalScaleInfo().originalGlobalScaledMin));
		originalGlobalScaleInfoNode.setAttribute(
				MicroscopyXMLTags.OriginalGlobalScaleInfoMaxTag,
				String.valueOf(param.getOriginalGlobalScaleInfo().originalGlobalScaledMax));
		originalGlobalScaleInfoNode.setAttribute(
				MicroscopyXMLTags.OriginalGlobalScaleInfoScaleTag,
				String.valueOf(param.getOriginalGlobalScaleInfo().originalScaleFactor));
		originalGlobalScaleInfoNode.setAttribute(
				MicroscopyXMLTags.OriginalGlobalScaleInfoOffsetTag,
				String.valueOf(param.getOriginalGlobalScaleInfo().originalOffsetFactor));
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
private static org.jdom.Element getXML(FRAPStudy.FRAPModelParameters param) throws XmlParseException, cbit.vcell.parser.ExpressionException {

	org.jdom.Element frapModelParametersNode = new org.jdom.Element(MicroscopyXMLTags.FRAPModelParametersTag);
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

private static org.jdom.Element getXML(FRAPStudy.InitialModelParameters param) throws XmlParseException, cbit.vcell.parser.ExpressionException
{
	org.jdom.Element iniParamNode = new org.jdom.Element(MicroscopyXMLTags.FRAPInitialParametersTag);
	
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

private static org.jdom.Element getXML(FRAPStudy.PureDiffusionModelParameters param) throws XmlParseException, cbit.vcell.parser.ExpressionException
{
	org.jdom.Element pureDiffParamNode = new org.jdom.Element(MicroscopyXMLTags.FRAPPureDiffusionParametersTag);
	
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

private static org.jdom.Element getXML(FRAPStudy.ReactionDiffusionModelParameters param) throws XmlParseException, cbit.vcell.parser.ExpressionException
{
	org.jdom.Element reacDiffParamNode = new org.jdom.Element(MicroscopyXMLTags.FRAPReactionDiffusionParametersTag);

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
}
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
 * @return org.jdom.Element
 * @throws XmlParseException
 * @throws cbit.vcell.parser.ExpressionException
 */
private static org.jdom.Element getXML(FRAPStudy param,Xmlproducer vcellXMLProducer,DataSetControllerImpl.ProgressListener progressListener,boolean bSaveCompressed) throws XmlParseException, cbit.vcell.parser.ExpressionException {
	//Creation of BioModel Node
	org.jdom.Element frapStudyNode = new org.jdom.Element(MicroscopyXMLTags.FRAPStudyTag);
	if (param.getName()!=null){
		String name = param.getName();
		frapStudyNode.setAttribute(XMLTags.NameAttrTag, name);
	}
	if (param.getOriginalImageFilePath()!=null && param.getOriginalImageFilePath().length()>0) {
		String name = param.getOriginalImageFilePath();
		frapStudyNode.setAttribute(MicroscopyXMLTags.OriginalImagePathAttrTag, name);
	}
	if (param.getDescription()!=null && param.getDescription().length()>0) {
		org.jdom.Element annotationElem = new org.jdom.Element(XMLTags.AnnotationTag);
		annotationElem.setText(mangle(param.getDescription()));
		frapStudyNode.addContent(annotationElem);
	}
	
	//Get model parameters

	//Get ExternalDataIdentifier (for timeseries)
	if (param.getFrapDataExternalDataInfo()!=null){
		Element imageDatasetEDINode = new Element(MicroscopyXMLTags.ImageDatasetExternalDataInfoTag);
		imageDatasetEDINode.setAttribute(MicroscopyXMLTags.FilenameAttrTag,param.getFrapDataExternalDataInfo().getFilename());
		imageDatasetEDINode.addContent( getXML(param.getFrapDataExternalDataInfo().getExternalDataIdentifier()) );
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
		frapStudyNode.addContent( getXML(param.getStoredRefData()) );
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

private static org.jdom.Element getXML(SimpleReferenceData referenceData)
{
    Element referenceDataElement = new Element(MicroscopyXMLTags.ReferenceDataTag);
    referenceDataElement.setAttribute(ParameterEstimationTaskXMLPersistence.NumRowsAttribute,Integer.toString(referenceData.getNumRows()));
    referenceDataElement.setAttribute(ParameterEstimationTaskXMLPersistence.NumColumnsAttribute,Integer.toString(referenceData.getNumColumns()));

    Element dataColumnListElement = new Element(ParameterEstimationTaskXMLPersistence.DataColumnListTag);
    for (int i = 0; i < referenceData.getColumnNames().length; i++){
          Element dataColumnElement = new Element(ParameterEstimationTaskXMLPersistence.DataColumnTag);
          dataColumnElement.setAttribute(ParameterEstimationTaskXMLPersistence.NameAttribute,referenceData.getColumnNames()[i]);
          dataColumnElement.setAttribute(ParameterEstimationTaskXMLPersistence.WeightAttribute,Double.toString(referenceData.getColumnWeights()[i]));
          dataColumnListElement.addContent(dataColumnElement);
    }
    referenceDataElement.addContent(dataColumnListElement);

    Element dataRowListElement = new Element(ParameterEstimationTaskXMLPersistence.DataRowListTag);
    for (int i = 0; i < referenceData.getNumRows(); i++){
          Element dataRowElement = new Element(ParameterEstimationTaskXMLPersistence.DataRowTag);
          String rowText = "";
          for (int j = 0; j < referenceData.getNumColumns(); j++){
                if (j>0){
                      rowText += " ";
                }
                rowText += referenceData.getRowData(i)[j];
          }
          dataRowElement.addContent(rowText);
          dataRowListElement.addContent(dataRowElement);
    }
    referenceDataElement.addContent(dataRowListElement);

    return referenceDataElement;
}//get simple reference data element


}