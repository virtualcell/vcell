package org.vcell.vmicro.op;

import java.io.BufferedOutputStream;
import java.io.File;

import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.vmicro.workflow.data.ImageTimeSeries;

import cbit.vcell.VirtualMicroscopy.ImageDataset;
import cbit.vcell.VirtualMicroscopy.UShortImage;
import cbit.vcell.microscopy.MicroscopyXMLTags;
import cbit.vcell.microscopy.MicroscopyXmlproducer;
import cbit.vcell.xml.Xmlproducer;

public class ExportRawTimeSeriesToVFrapOp {
		
	public void exportToVFRAP(File vfrapFile, ImageTimeSeries<UShortImage> imageTimeSeries, ClientTaskStatusSupport clientTaskStatusSupport) throws Exception {

		Xmlproducer vcellXMLProducer = new Xmlproducer(false);
		boolean bSaveCompressed = true;
		
		ImageDataset imageData = new ImageDataset(imageTimeSeries.getAllImages(),imageTimeSeries.getImageTimeStamps(),imageTimeSeries.getSizeZ());
		
		Element root = new Element(MicroscopyXMLTags.FRAPStudyTag);
		Element next = new Element(MicroscopyXMLTags.FRAPDataTag);
		root.addContent(next);
		Element imageDataXML = MicroscopyXmlproducer.getXML(imageData,vcellXMLProducer,clientTaskStatusSupport,bSaveCompressed);
		next.addContent(imageDataXML);
		java.io.FileOutputStream fileOutStream = new java.io.FileOutputStream(vfrapFile);
		BufferedOutputStream bufferedStream = new BufferedOutputStream(fileOutStream);

		//XmlUtil.writeXmlToStream(root, false, bufferedStream);
		
		XMLOutputter xmlOut = new XMLOutputter(Format.getPrettyFormat());
    	xmlOut.getFormat().setTextMode(Format.TextMode.PRESERVE);
		xmlOut.output(root, bufferedStream);
		
		bufferedStream.flush();
		fileOutStream.flush();
		fileOutStream.close();
	}

}
