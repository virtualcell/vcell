package cbit.vcell.VirtualMicroscopy;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;

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
		classLoader = new PluginClassLoader(classpathURLs, Bundle.class.getClassLoader());
	}
	
	public void checkClasspathExists() throws FileNotFoundException {
		for (URL url : classpathURLs){
			if (!new File(URLDecoder.decode(  url.getPath())).exists()){
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
			final String BioformatsClasspath = "\\developer\\eclipse\\workspace\\VCBioformatsPlugin\\bin";
//			final String BioformatsClasspath = "d:\\temp\\vcu.jar";

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
			
			ImageDataset imageDataSet = imageDatasetReader.readImageDataset("\\Developer\\Eclipse\\workspace\\VCell\\resources\\icons\\vcell.gif", null);
//			ImageDataset imageDataSet = imageDatasetReader.readImageDataset("d:\\Developer\\Eclipse\\workspace\\VCell\\resources\\icons\\vcell.gif", null);
			System.out.println("size of vcell.gif : " + imageDataSet.getISize());
			bundle.unload();
			
		} catch (Throwable e) {
			e.printStackTrace(System.out);
		}
	}

}
