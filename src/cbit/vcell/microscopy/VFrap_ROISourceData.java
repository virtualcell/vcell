package cbit.vcell.microscopy;

import cbit.vcell.VirtualMicroscopy.ImageDataset;
import cbit.vcell.VirtualMicroscopy.ROI;

public interface VFrap_ROISourceData {

	enum VFRAP_ROI_ENUM { ROI_CELL, ROI_BLEACHED, ROI_BACKGROUND};

	void addReplaceRoi(ROI originalROI);

	ROI getCurrentlyDisplayedROI();

	ImageDataset getImageDataset();

	ROI getRoi(String name);

}

