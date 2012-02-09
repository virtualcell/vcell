package org.vcell.util;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import sun.misc.URLClassPath;
import cbit.vcell.resource.ResourceUtil;

public class VCellClientClasspathUtils {

	public static URL[] getBioformatsClasspathURLs() throws MalformedURLException{
		String bioformatsClasspath = PropertyLoader.getProperty(PropertyLoader.bioformatsClasspath, null);
		if (bioformatsClasspath!=null && bioformatsClasspath.trim().length()>0){
			return URLClassPath.pathToURLs(bioformatsClasspath);
		}
		String bioformatsJarFileName = PropertyLoader.getProperty(PropertyLoader.bioformatsJarFileName, null);
		if (bioformatsJarFileName!=null){
			File jarFile = new File(getPluginFolder(),bioformatsJarFileName);
			return new URL[] { jarFile.toURI().toURL() };
		}
		throw new RuntimeException("bioformats plugin configuration not set (neither classpath nor jarfilename");
	}
	
	private static String getBioformatsJarDownloadURLString(){
		return PropertyLoader.getRequiredProperty(PropertyLoader.bioformatsJarDownloadURL);
	}
	
	private static File getPluginFolder(){
		return new File(ResourceUtil.getVcellHome(),"plugins");
	}
		
	public static void downloadBioformatsJar() throws MalformedURLException, IOException{
		boolean bPluginFolderExists = false;
		
		if (!(bPluginFolderExists = getPluginFolder().exists())) {
			bPluginFolderExists = getPluginFolder().mkdirs();
			if (bPluginFolderExists) {
//				getPluginFolder().setWritable(true);
			}
			else {
				throw new RuntimeException("not able to create plugin directory: "+getPluginFolder().getAbsolutePath());
			}
		}
		File jarFile = new File(getPluginFolder(),PropertyLoader.getRequiredProperty(PropertyLoader.bioformatsJarFileName));
		FileUtils.saveUrlToFile(jarFile.getAbsolutePath(), getBioformatsJarDownloadURLString());
	}
}