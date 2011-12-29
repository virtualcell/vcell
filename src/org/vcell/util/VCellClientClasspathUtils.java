package org.vcell.util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

import cbit.vcell.resource.ResourceUtil;

public class VCellClientClasspathUtils {

	public static String bioformatsJarFileName = null;
	
	public static File getBioformatsJarPath(){
		return new File(getPluginFolder()+File.separator+PropertyLoader.getRequiredProperty(PropertyLoader.bioformatsJarFileName));
	}
	
	private static String getBioformatsJarDownloadURLString(){
		return PropertyLoader.getRequiredProperty(PropertyLoader.bioformatsJarDownloadURL);
	}
	
	private static File getPluginFolder(){
		return new File(ResourceUtil.getVcellHome(),"plugins");
	}
		
	public static void addBioformatsJarToPath() throws Exception {
		URL u = getBioformatsJarPath().toURL();
		URLClassLoader urlClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
		Class<URLClassLoader> urlClass = URLClassLoader.class;
		Method method = urlClass.getDeclaredMethod("addURL", new Class[]{URL.class});
		method.setAccessible(true);
		method.invoke(urlClassLoader, new Object[]{u});
	}
	
	public static void  downloadBioformatsJar() throws Exception{
		if (!getPluginFolder().exists()) {
			if (!getPluginFolder().mkdir()) {
				throw new IOException("Creating directory "+getPluginFolder()+" failed.");
			}
		}
		FileUtils.saveUrlToFile(getBioformatsJarPath(), getBioformatsJarDownloadURLString());
	}
}