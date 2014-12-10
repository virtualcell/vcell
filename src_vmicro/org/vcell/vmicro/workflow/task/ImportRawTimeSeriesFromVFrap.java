package org.vcell.vmicro.workflow.task;

import java.io.File;

import org.jdom.Element;
import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.vmicro.workflow.data.ImageTimeSeries;
import org.vcell.workflow.DataHolder;
import org.vcell.workflow.DataInput;
import org.vcell.workflow.Task;

import cbit.util.xml.XmlUtil;
import cbit.vcell.VirtualMicroscopy.ImageDataset;
import cbit.vcell.VirtualMicroscopy.UShortImage;
import cbit.vcell.VirtualMicroscopy.importer.AnnotatedImageDataset;
import cbit.vcell.VirtualMicroscopy.importer.MicroscopyXmlReader;

public class ImportRawTimeSeriesFromVFrap extends Task {
	
	//
	// inputs
	//
	public final DataInput<File> vfrapFile;
	
	//
	// outputs
	//
	public final DataHolder<ImageTimeSeries> rawTimeSeriesImages;
	

	public ImportRawTimeSeriesFromVFrap(String id){
		super(id);
		vfrapFile = new DataInput<File>(File.class,"vfrapFile",this);
		rawTimeSeriesImages = new DataHolder<ImageTimeSeries>(ImageTimeSeries.class,"rawTimeSeriesImages",this);
		addInput(vfrapFile);
		addOutput(rawTimeSeriesImages);
	}

	@Override
	protected void compute0(ClientTaskStatusSupport clientTaskStatusSupport) throws Exception {

		String xmlString = XmlUtil.getXMLString(vfrapFile.getData().getAbsolutePath());
		MicroscopyXmlReader xmlReader = new MicroscopyXmlReader(true);
		Element vFrapRoot = XmlUtil.stringToXML(xmlString, null).getRootElement();

		// loading frap images and a ROIs subset for display purposes only (see next task)
		AnnotatedImageDataset annotatedImages = xmlReader.getAnnotatedImageDataset(vFrapRoot, null);
		ImageDataset imageDataset = annotatedImages.getImageDataset();
		UShortImage[] allImages = imageDataset.getAllImages();
		double[] imageTimeStamps = imageDataset.getImageTimeStamps();
		ImageTimeSeries imageTimeSeries = new ImageTimeSeries<UShortImage>(UShortImage.class,allImages,imageTimeStamps,1);
		
		rawTimeSeriesImages.setData(imageTimeSeries);
	}

}
