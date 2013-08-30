package cbit.vcell.VirtualMicroscopy;

import java.io.FileNotFoundException;

import javax.swing.JOptionPane;

import org.vcell.util.UserCancelException;
import org.vcell.util.VCellClientClasspathUtils;
import org.vcell.util.VCellThreadChecker;
import org.vcell.util.gui.DialogUtils;


public class ImageDatasetReaderFactory {
	private static Bundle bundle = null;
	private static final String BIOFORMATS_YES = "yes";
	private static final String BIOFORMATS_NO = "no";
	
	public static ImageDatasetReader createImageDatasetReader() throws Exception {
		
		VCellThreadChecker.checkRemoteInvocation();

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
		
		return bundle.createImageDatasetReader();
	}
}
