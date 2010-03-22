package cbit.vcell.geometry.gui;

import cbit.vcell.VirtualMicroscopy.ImageDataset;
import cbit.vcell.VirtualMicroscopy.ROI;

public interface ROISourceData {

	void addReplaceRoi(ROI originalROI);
	
	ROI getCurrentlyDisplayedROI();

	ImageDataset getImageDataset();

}
