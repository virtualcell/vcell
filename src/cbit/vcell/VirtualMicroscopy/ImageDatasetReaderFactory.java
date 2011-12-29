package cbit.vcell.VirtualMicroscopy;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JOptionPane;

import org.vcell.util.UserCancelException;
import org.vcell.util.VCellClientClasspathUtils;
import org.vcell.util.gui.DialogUtils;

import cbit.vcell.client.server.VCellThreadChecker;

public class ImageDatasetReaderFactory {
	
	// needs to be called in the proper thread
	private static void loadPlugin() throws Exception{
		VCellThreadChecker.checkRemoteInvocation();
		VCellClientClasspathUtils.tryToLoadBioformatsLibrary();	
	}
	private static final String BIOFORMATS_YES = "yes";
	private static final String BIOFORMATS_NO = "no";
	public static ImageDatasetReader createImageDatasetReader() throws Exception {
		
		
		String[] options = {BIOFORMATS_YES , BIOFORMATS_NO};
		
	    if (!VCellClientClasspathUtils.getBioformatsJarPath().exists()) {
	    	String downloadApproval = DialogUtils.showWarningDialog(JOptionPane.getRootFrame(), "In order to perform this operation, the BioFormats library plugin, licensed under the General Public License (GPL) must be downloaded.  This only needs to be done once. Do you want to download and install the BioFormats library plugin?", options, BIOFORMATS_YES);
	    	if (!BIOFORMATS_YES.equals(downloadApproval)) {
	    			DialogUtils.showInfoDialog(JOptionPane.getRootFrame(), "BioFormats Library Plugin will not be loaded", "If you would like to use the BioFormats Library in the future, just retry the operation and agree to downloading the BioFormats Library under the General Public License.");
	    			//bail
	    			throw UserCancelException.CANCEL_GENERIC;
	    	}
	    	VCellClientClasspathUtils.downloadBioformatsJar();
	    }
	    
		try {	
			return constructImageDatasetReader();
			}
		catch (Throwable e) {
			e.printStackTrace();
			
			try {
				loadPlugin();
				return constructImageDatasetReader();
			} catch (Throwable e2) {
				e2.printStackTrace();
				throw new Exception(e2.getMessage());
	
			}
		}
		
	}

	private static ImageDatasetReader constructImageDatasetReader()
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException, InvocationTargetException {
		Class readerClass = Class
				.forName("cbit.vcell.VirtualMicroscopy.BioformatsImageDatasetReader");
		//System.out.println("--------------Starting getConstructors()");
		Constructor<ImageDatasetReader> constructor = readerClass
				.getConstructors()[0];
		//System.out.println("-------------Actually created the Reader after getting the jar from the web");
		return constructor.newInstance(null);
	}
}
