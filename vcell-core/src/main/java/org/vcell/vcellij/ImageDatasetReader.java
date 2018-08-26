package org.vcell.vcellij;

import java.io.File;

import org.scijava.plugin.SciJavaPlugin;
import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.util.ISize;

import cbit.image.ImageSizeInfo;
import cbit.vcell.VirtualMicroscopy.ImageDataset;

public interface ImageDatasetReader extends SciJavaPlugin {

	public abstract ImageSizeInfo getImageSizeInfoForceZ(
			String fileName, Integer forceZSize) throws Exception;

	public abstract ImageDataset readImageDataset(String imageID,
			ClientTaskStatusSupport status) throws Exception;

	public abstract ImageDataset[] readImageDatasetChannels(String imageID,
			ClientTaskStatusSupport status, boolean bMergeChannels,
			Integer timeIndex, ISize resize) throws Exception;

	public abstract ImageDataset readImageDatasetFromMultiFiles(File[] files, ClientTaskStatusSupport status, boolean isTimeSeries, double timeInterval) throws Exception;
	
}