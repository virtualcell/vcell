package cbit.vcell.VirtualMicroscopy;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JOptionPane;

import org.vcell.util.UserCancelException;
import org.vcell.util.VCellClientClasspathUtils;
import org.vcell.util.gui.DialogUtils;

import cbit.vcell.client.UserMessage;
import cbit.vcell.client.server.VCellThreadChecker;

public class ImageDatasetReaderFactory {
	public static ImageDatasetReader createImageDatasetReader() throws Exception,UserCancelException {
		String[] options = {UserMessage.OPTION_YES , UserMessage.OPTION_NO};
		//See if plugin exists in the expected place on the users machine, if not ask user if they want to use BioFormats
	    if (!VCellClientClasspathUtils.getBioformatsJarPath().exists()) {
	    	String downloadApproval = DialogUtils.showWarningDialog(JOptionPane.getRootFrame(),
	    		"In order to perform this operation, the BioFormats library plugin, licensed under the General Public License (GPL) must be downloaded.  This only needs to be done once. Do you want to download and install the BioFormats library plugin?", options, UserMessage.OPTION_YES);
	    	if (!UserMessage.OPTION_YES.equals(downloadApproval)) {
	    			DialogUtils.showInfoDialog(JOptionPane.getRootFrame(),
	    			"BioFormats Library Plugin will not be loaded", "If you would like to use the BioFormats Library in the future, just retry the operation and agree to downloading the BioFormats Library under the General Public License.");
	    			//bail
	    			throw UserCancelException.CANCEL_GENERIC;
	    	}
	    	VCellThreadChecker.checkRemoteInvocation();
	    	VCellClientClasspathUtils.downloadBioformatsJar();
	    }
		try {
			//Assume plugin downloaded (must be at this point) and classloader has path (might not yet).  try to construct an imageDataSetReader
			return constructImageDatasetReader();
		}catch (Exception e) {
			e.printStackTrace();
			//Ignore exception, add Bioformats plugin to classpath and try to create again
			VCellClientClasspathUtils.addBioformatsJarToPath();	
			return constructImageDatasetReader();
		}
		
	}

	private static ImageDatasetReader constructImageDatasetReader() throws Exception{
		Class readerClass = Class.forName("cbit.vcell.VirtualMicroscopy.BioformatsImageDatasetReader");
		//System.out.println("--------------Starting getConstructors()");
		Constructor<ImageDatasetReader> constructor = readerClass.getConstructors()[0];
		//System.out.println("-------------Actually created the Reader after getting the jar from the web");
		return constructor.newInstance();
	}
}
