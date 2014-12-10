package org.vcell.vmicro.workflow.task;

import java.awt.event.WindowListener;

import javax.swing.JFrame;

import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.util.Range;
import org.vcell.workflow.DataHolder;
import org.vcell.workflow.DataInput;
import org.vcell.workflow.Task;

import cbit.image.DisplayAdapterService;
import cbit.image.SourceDataInfo;
import cbit.image.gui.ImagePlaneManagerPanel;
import cbit.vcell.VirtualMicroscopy.Image;

public class DisplayImage extends Task {
	
	//
	// inputs
	//
	public final DataInput<Image> image;
	public final DataInput<String> title;
		
	//
	// outputs
	//
	public final DataHolder<Boolean> displayed;

	
	public DisplayImage(String id){
		super(id);
		image = new DataInput<Image>(Image.class,"image",this);
		title = new DataInput<String>(String.class,"title",this);
		displayed = new DataHolder<Boolean>(Boolean.class,"displayed",this);
		addInput(image);
		addInput(title);
		addOutput(displayed);
	}

	@Override
	protected void compute0(final ClientTaskStatusSupport clientTaskStatusSupport) throws Exception {
		WindowListener listener = new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				displayed.setDirty();
				updateStatus();
			};
		};
		displayImage(image.getData(), title.getData(), listener);
		displayed.setData(true);
	}
	
	public static void displayImage(Image image, String title, WindowListener listener) {
		ImagePlaneManagerPanel imagePanel = new ImagePlaneManagerPanel();
		double[] doublePixels = image.getDoublePixels();
		double minPixel = Double.MAX_VALUE;
		double maxPixel = -Double.MAX_VALUE;
		for (int i=0;i<doublePixels.length;i++){
			double pixel = doublePixels[i];
			doublePixels[i] = pixel;
			minPixel = Math.min(minPixel,pixel);
			maxPixel = Math.max(maxPixel,pixel);
		}
		Range newRange = new Range(minPixel,maxPixel);
		SourceDataInfo source = new SourceDataInfo(
				SourceDataInfo.RAW_VALUE_TYPE, 
				doublePixels, 
				image.getExtent(), 
				image.getOrigin(), 
				newRange, 
				0, 
				image.getNumX(), 
				1, 
				image.getNumY(), 
				image.getNumX(), 
				image.getNumZ(), 
				image.getNumX()*image.getNumY());
		imagePanel.setDisplayAdapterServicePanelVisible(true);
		DisplayAdapterService das = imagePanel.getDisplayAdapterServicePanel().getDisplayAdapterService();
		das.setValueDomain(null);
		das.addColorModelForValues(
			DisplayAdapterService.createGrayColorModel(), 
			DisplayAdapterService.createGraySpecialColors(),
			DisplayAdapterService.GRAY);
		das.addColorModelForValues(
			DisplayAdapterService.createBlueRedColorModel(),
			DisplayAdapterService.createBlueRedSpecialColors(),
			DisplayAdapterService.BLUERED);
		das.setActiveColorModelID(DisplayAdapterService.BLUERED);

		JFrame jframe = new JFrame();
		jframe.setTitle(title);
		jframe.getContentPane().add(imagePanel);
		jframe.setSize(500,500);
		jframe.addWindowListener(listener);
		jframe.setVisible(true);
		
		imagePanel.setSourceDataInfo(source);
	}
	
}
