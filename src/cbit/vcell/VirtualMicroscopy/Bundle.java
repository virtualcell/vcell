package cbit.vcell.VirtualMicroscopy;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;

import sun.misc.URLClassPath;

public class Bundle {
	private URL[] classpathURLs = null;
	private PluginClassLoader classLoader = null;
	private String imageDatasetReaderClassname = "org.vcell.plugins.bioformats.BioFormatsImageDatasetReader";

	public Bundle(String argClasspath) throws MalformedURLException{
		classpathURLs = URLClassPath.pathToURLs(argClasspath);
		for (URL url : classpathURLs){
			System.out.println(url);
		}
	}
	
	public Bundle(URL[] argClasspath) {
		classpathURLs = argClasspath;
		for (URL url : classpathURLs){
			System.out.println(url);
		}
	}
	
	public void load() throws MalformedURLException {
		classLoader = new PluginClassLoader(classpathURLs);
	}
	
	public void checkClasspathExists() throws FileNotFoundException {
		for (URL url : classpathURLs){
			if (!new File(url.getPath()).exists()){
				throw new FileNotFoundException(url.getFile());
			}
		}
	}
	
	public void unload(){
		classLoader = null;
	}
	
	public ImageDatasetReader createImageDatasetReader() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		return (ImageDatasetReader)classLoader.loadClass(imageDatasetReaderClassname).newInstance();
	}
	
	public static void main(String[] args){
		try {
			final String BioformatsClasspath = "\\developer\\eclipse\\workspace\\VCBioformatsPlugin\\lib\\bioformats_omexml_locicommon_brnch41.jar;\\developer\\eclipse\\workspace\\VCBioformatsPlugin\\bin";

			Bundle bundle = new Bundle(BioformatsClasspath);
			bundle.load();
			
			ImageDatasetReader imageDatasetReader = null;
			
			imageDatasetReader = bundle.createImageDatasetReader();
			System.out.println(imageDatasetReader+", "+imageDatasetReader.getClass().getName()+", "+imageDatasetReader.getClass().getClassLoader());
			imageDatasetReader = bundle.createImageDatasetReader();
			System.out.println(imageDatasetReader+", "+imageDatasetReader.getClass().getName()+", "+imageDatasetReader.getClass().getClassLoader());
			imageDatasetReader = bundle.createImageDatasetReader();
			System.out.println(imageDatasetReader+", "+imageDatasetReader.getClass().getName()+", "+imageDatasetReader.getClass().getClassLoader());
			imageDatasetReader = bundle.createImageDatasetReader();
			System.out.println(imageDatasetReader+", "+imageDatasetReader.getClass().getName()+", "+imageDatasetReader.getClass().getClassLoader());
			
			
			bundle.unload();
			
		} catch (Throwable e) {
			e.printStackTrace(System.out);
		}
	}

}
