package org.vcell.util;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;


import cbit.vcell.resource.ResourceUtil;

public class VCellClientClasspathUtils {

	public static String bioformatsJarFileName = null;
	
	public static File getBioformatsJarPath(){
		//return new File(ResourceUtil.getVcellHome(),"plugins"+File.separator+"bioformats_omexml_locicommon_brnch41.jar");
		return new File(ResourceUtil.getVcellHome(),"plugins"+File.separator+PropertyLoader.getRequiredProperty(PropertyLoader.bioformatsJarFileName));
	}
	
	private static String getBioformatsJarDownloadURLString(){
		
		//TODO: get the file URL from a property
		//return "http://vcell.org/webstart/Alpha/bioformats_omexml_locicommon_brnch41.jar";
		return PropertyLoader.getRequiredProperty(PropertyLoader.bioformatsJarDownloadURL);
	}
	
	private static File getPluginFolder(){
		return new File(ResourceUtil.getVcellHome(),"plugins");
	}
	
	private static boolean bBioformatsJarExists() {
		return getBioformatsJarPath().exists();
	}
	
	public static void addBioformatsJarToPath(File jarFilePath) throws Exception {
		//  That a valid version exists already should be checked, but be careful anyway
		
		if (jarFilePath !=null) {
			
			URL u = jarFilePath.toURL();
			//URL u = f.toURL();
			//System.out.println(u.getPath());
		
			URLClassLoader urlClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
			Class urlClass = URLClassLoader.class;
			Method method = urlClass.getDeclaredMethod("addURL", new Class[]{URL.class});
			method.setAccessible(true);
			method.invoke(urlClassLoader, new Object[]{u});
		}
	}
	
	public static void tryToLoadBioformatsLibrary() throws Exception {
		
			
				File f = getBioformatsJarPath();
				addBioformatsJarToPath(f);
				//System.out.println("Apparently succeeded in adding bioformats library");

	}
	
	public static boolean downloadBioformatsJar(){
		boolean bSuccess = false;
		boolean bPluginFolderExists = false;
		
		try {
			
			if (!(bPluginFolderExists = getPluginFolder().exists())) {
				bPluginFolderExists = getPluginFolder().mkdir();
				if (bPluginFolderExists) {
//					getPluginFolder().setWritable(true);
				}
				else {
					return false;
				}
			}
			FileUtils.saveUrlToFile(getBioformatsJarPath(), getBioformatsJarDownloadURLString());
		} catch (Exception e) {
			e.printStackTrace();
			//System.out.println("There was a problem either accessing plugin folder, creating the plugin, making it writable, or downloading the jar.");
			return false;
		}
		
		bSuccess = true;
		return bSuccess;
		
		
		
	}
}