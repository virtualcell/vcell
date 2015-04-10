package org.vcell.vmicro.op.display;

import java.awt.event.WindowListener;

import javax.swing.JFrame;

import org.vcell.util.Range;

import cbit.image.DisplayAdapterService;
import cbit.image.SourceDataInfo;
import cbit.image.gui.ImagePlaneManagerPanel;
import cbit.vcell.VirtualMicroscopy.Image;

public class DisplayImageOp {
	
	public void displayImage(Image image, String title, WindowListener listener) {
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
