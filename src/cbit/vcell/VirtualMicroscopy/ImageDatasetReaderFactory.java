package cbit.vcell.VirtualMicroscopy;

import java.lang.reflect.Constructor;

import javax.swing.JOptionPane;

import org.vcell.util.VCellClientClasspathUtils;
import org.vcell.util.gui.DialogUtils;

import cbit.vcell.client.server.VCellThreadChecker;

public class ImageDatasetReaderFactory {
	
	// needs to be called in the proper thread
	private static void loadPlugin() throws Exception{
		VCellThreadChecker.checkRemoteInvocation();
		VCellClientClasspathUtils.tryToLoadBioformatsLibrary();	
	}
	
	public static ImageDatasetReader createImageDatasetReader() throws Exception {
	
		try {	
			Class readerClass = Class.forName("cbit.vcell.VirtualMicroscopy.BioformatsImageDatasetReader");
			Constructor<ImageDatasetReader> constructor = readerClass.getConstructors()[0];
			return constructor.newInstance(null);
			}
		catch (Throwable e) {
			e.printStackTrace();
			
			try {
				loadPlugin();
				Class readerClass = Class
						.forName("cbit.vcell.VirtualMicroscopy.BioformatsImageDatasetReader");
				Constructor<ImageDatasetReader> constructor = readerClass
						.getConstructors()[0];
				return constructor.newInstance(null);
			} catch (Throwable e2) {
				e2.printStackTrace();
				DialogUtils.showWarningDialog(JOptionPane.getRootFrame(), "Do you want to load Bioformats?");
				throw new Exception("Failed to load the Bioformats Jar");
			}
		}
		
	}

	   

}
