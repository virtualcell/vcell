package org.vcell.vmicro.workflow.task;

import java.io.BufferedOutputStream;
import java.io.File;

import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.vmicro.workflow.data.ImageTimeSeries;
import org.vcell.workflow.DataInput;
import org.vcell.workflow.DataOutput;
import org.vcell.workflow.Task;
import org.vcell.workflow.TaskContext;

import cbit.util.xml.XmlUtil;
import cbit.vcell.VirtualMicroscopy.Image;
import cbit.vcell.VirtualMicroscopy.ImageDataset;
import cbit.vcell.VirtualMicroscopy.UShortImage;
import cbit.vcell.microscopy.MicroscopyXMLTags;
import cbit.vcell.microscopy.MicroscopyXmlproducer;
import cbit.vcell.xml.Xmlproducer;

public class ExportRawTimeSeriesToVFrap extends Task {
	
	//
	// inputs
	//
	public final DataInput<File> vfrapFile;
	public final DataInput<ImageTimeSeries> rawTimeSeriesImages;
	
	//
	// outputs
	//
	public final DataOutput<Boolean> written;
	
	public ExportRawTimeSeriesToVFrap(String id){
		super(id);
		vfrapFile = new DataInput<File>(File.class,"vfrapFile",this);
		rawTimeSeriesImages = new DataInput<ImageTimeSeries>(ImageTimeSeries.class,"rawTimeSeriesImages",this);
		addInput(vfrapFile);
		addInput(rawTimeSeriesImages);
		written = new DataOutput<Boolean>(Boolean.class,"written",this);
		addOutput(written);
	}

	@Override
	protected void compute0(TaskContext context, ClientTaskStatusSupport clientTaskStatusSupport) throws Exception {

		ImageTimeSeries imageTimeSeries = context.getData(rawTimeSeriesImages);
		File outputFile = context.getData(vfrapFile);

		Xmlproducer vcellXMLProducer = new Xmlproducer(false);
		boolean bSaveCompressed = true;
		
		Image[] images = imageTimeSeries.getAllImages();
		UShortImage[] usImages = new UShortImage[images.length];
		for (int i=0;i<usImages.length;i++){
			if (!(images[i] instanceof UShortImage)){
				throw new Exception("type mismatch, expecting only UShortImages in time series");
			}
			usImages[i] = (UShortImage)images[i];
		}
		ImageDataset imageData = new ImageDataset(usImages,imageTimeSeries.getImageTimeStamps(),imageTimeSeries.getSizeZ());
		
		Element root = new Element(MicroscopyXMLTags.FRAPStudyTag);
		Element next = new Element(MicroscopyXMLTags.FRAPDataTag);
		root.addContent(next);
		Element imageDataXML = MicroscopyXmlproducer.getXML(imageData,vcellXMLProducer,clientTaskStatusSupport,bSaveCompressed);
		next.addContent(imageDataXML);
		java.io.FileOutputStream fileOutStream = new java.io.FileOutputStream(outputFile);
		BufferedOutputStream bufferedStream = new BufferedOutputStream(fileOutStream);

		//XmlUtil.writeXmlToStream(root, false, bufferedStream);
		
		XMLOutputter xmlOut = new XMLOutputter(Format.getPrettyFormat());
    	xmlOut.getFormat().setTextMode(Format.TextMode.PRESERVE);
		xmlOut.output(root, bufferedStream);
		
		bufferedStream.flush();
		fileOutStream.flush();
		fileOutStream.close();
		
		context.setData(written, true);
	}

}
