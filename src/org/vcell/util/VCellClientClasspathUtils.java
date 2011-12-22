package org.vcell.util;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;


import cbit.vcell.resource.ResourceUtil;

public class VCellClientClasspathUtils {

	public static String bioformatsJarFileName = null;
	
	private static File getBioformatsJarPath(){
		return new File(ResourceUtil.getVcellHome(),"plugins"+File.separator+"bioformats_omexml_locicommon_brnch41.jar");
	}
	private static boolean bBioformatsJarExists() {
		return getBioformatsJarPath().exists();
	}
	
	public static void addBioformatsJarToPath(File jarFilePath) throws Exception {
		//  That a valid version exists already should be checked, but be careful anyway
		
		if (jarFilePath !=null) {
			
			URL u = jarFilePath.toURL();
			//URL u = f.toURL();
			System.out.println(u.getPath());
		
			URLClassLoader urlClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
			Class urlClass = URLClassLoader.class;
			Method method = urlClass.getDeclaredMethod("addURL", new Class[]{URL.class});
			method.setAccessible(true);
			method.invoke(urlClassLoader, new Object[]{u});
		}
	}
	
	public static void tryToLoadBioformatsLibrary() throws Exception {
		
			try {
				File f = getBioformatsJarPath();
				addBioformatsJarToPath(f);
				System.out.println("Apparently succeeded in adding bioformats library");

			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Apparantly failed to add bioformats library");
				//  Alert user that jar loading failed 
			}
	}
	
	public static boolean downloadBioformatsJar(){
		boolean bSuccess = false;
		
		
		return bSuccess;
		
	}
}