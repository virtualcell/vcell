package cbit.vcell.VirtualMicroscopy.bioformats;

import java.awt.HeadlessException;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;

import javax.swing.JOptionPane;

import org.scijava.Context;
import org.scijava.plugin.Plugin;
import org.scijava.plugin.PluginInfo;
import org.scijava.service.AbstractService;
import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.util.ISize;
import org.vcell.util.UserCancelException;
import org.vcell.util.VCellClientClasspathUtils;
import org.vcell.util.VCellThreadChecker;
import org.vcell.util.gui.DialogUtils;

import cbit.image.ImageSizeInfo;
import cbit.vcell.VirtualMicroscopy.Bundle;
import cbit.vcell.VirtualMicroscopy.ImageDataset;
import cbit.vcell.VirtualMicroscopy.ImageDatasetReader;

//@Plugin(type = ImageDatasetReader.class)
public class ImageDatasetReaderFactory /* extends AbstractService */ implements ImageDatasetReader {
	
	private ImageDatasetReader imageDatasetReader = null;
	
	public ImageDatasetReaderFactory(){
		Bundle bundle = null;
		final String BIOFORMATS_YES = "yes";
		final String BIOFORMATS_NO = "no";
	
		VCellThreadChecker.checkRemoteInvocation();

		try {
			if (bundle==null){
				bundle = new Bundle(VCellClientClasspathUtils.getBioformatsClasspathURLs());
				bundle.load();
			}
			
			try {
				bundle.checkClasspathExists();
			}catch (FileNotFoundException e){
				e.printStackTrace(System.out);

				String[] options = {BIOFORMATS_YES , BIOFORMATS_NO};
				String downloadApproval = null;
				try{
					//check if there are any "bioformats_xxx.jar" and if so assume user has already consented to using BioFormats library
					downloadApproval = (VCellClientClasspathUtils.anyBioFormatsLocalDownloadsExists()?BIOFORMATS_YES:null);
				}catch(Exception e2){
					//ignore, continue anyway
					e2.printStackTrace();
				}
				if(downloadApproval == null){
					downloadApproval = DialogUtils.showWarningDialog(JOptionPane.getRootFrame(), "In order to perform this operation, the BioFormats library plugin, licensed under the General Public License (GPL) must be downloaded.  This only needs to be done once. Do you want to download and install the BioFormats library plugin?", options, BIOFORMATS_YES);
				}
				if (!BIOFORMATS_YES.equals(downloadApproval)) {
					DialogUtils.showInfoDialog(JOptionPane.getRootFrame(), "BioFormats Library Plugin will not be loaded", "If you would like to use the BioFormats Library in the future, just retry the operation and agree to downloading the BioFormats Library under the General Public License.");
					//bail
					throw UserCancelException.CANCEL_GENERIC;
				}else{
					try {
						VCellClientClasspathUtils.downloadBioformatsJar();
					}catch (Exception ex){
						ex.printStackTrace(System.out);
						throw new RuntimeException("unable to download and install bioformats plugin",ex);
					}
			    }
			}
			
			imageDatasetReader = bundle.createImageDatasetReader();
		} catch (HeadlessException | MalformedURLException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException("failed to load bioformats plugin: "+e.getMessage(),e);
		}
	}

	@Override
	public ImageSizeInfo getImageSizeInfo(String fileName, Integer forceZSize) throws Exception {
		return imageDatasetReader.getImageSizeInfo(fileName, forceZSize);
	}

	@Override
	public ImageDataset readImageDataset(String imageID, ClientTaskStatusSupport status) throws Exception {
		return imageDatasetReader.readImageDataset(imageID, status);
	}

	@Override
	public ImageDataset[] readImageDatasetChannels(String imageID, ClientTaskStatusSupport status,
			boolean bMergeChannels, Integer timeIndex, ISize resize) throws Exception {
		return imageDatasetReader.readImageDatasetChannels(imageID, status, bMergeChannels, timeIndex, resize);
	}

	@Override
	public ImageDataset readImageDatasetFromMultiFiles(File[] files, ClientTaskStatusSupport status,
			boolean isTimeSeries, double timeInterval) throws Exception {
		return imageDatasetReader.readImageDatasetFromMultiFiles(files, status, isTimeSeries, timeInterval);
	}

	@Override
	public Context context() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Context getContext() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getPriority() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setPriority(double priority) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public PluginInfo<?> getInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setInfo(PluginInfo<?> info) {
		// TODO Auto-generated method stub
		
	}
}
