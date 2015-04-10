package org.vcell.vmicro.op;

import java.io.File;

import org.jdom.Element;
import org.vcell.vmicro.workflow.data.ImageTimeSeries;

import cbit.util.xml.XmlUtil;
import cbit.vcell.VirtualMicroscopy.ImageDataset;
import cbit.vcell.VirtualMicroscopy.UShortImage;
import cbit.vcell.VirtualMicroscopy.importer.AnnotatedImageDataset;
import cbit.vcell.VirtualMicroscopy.importer.MicroscopyXmlReader;

public class ImportRawTimeSeriesFromVFrapOp {
	
	public ImageTimeSeries<UShortImage> importRawTimeSeriesFromVFrap(File vfrapFile) throws Exception {

		String xmlString = XmlUtil.getXMLString(vfrapFile.getAbsolutePath());
		MicroscopyXmlReader xmlReader = new MicroscopyXmlReader(true);
		Element vFrapRoot = XmlUtil.stringToXML(xmlString, null).getRootElement();

		// loading frap images and a ROIs subset for display purposes only (see next task)
		AnnotatedImageDataset annotatedImages = xmlReader.getAnnotatedImageDataset(vFrapRoot, null);
		ImageDataset imageDataset = annotatedImages.getImageDataset();
		UShortImage[] allImages = imageDataset.getAllImages();
		double[] imageTimeStamps = imageDataset.getImageTimeStamps();
		ImageTimeSeries<UShortImage> imageTimeSeries = new ImageTimeSeries<UShortImage>(UShortImage.class,allImages,imageTimeStamps,1);
		
		return imageTimeSeries;
	}

}
