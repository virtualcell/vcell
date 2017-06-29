package cbit.vcell.resource;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.vcell.util.FileUtils;

import sun.misc.URLClassPath;

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
		cleanupOldBioFormatsJarfiles();
		File jarFile = new File(getPluginFolder(),PropertyLoader.getRequiredProperty(PropertyLoader.bioformatsJarFileName));
		FileUtils.saveUrlToFile(jarFile.getAbsolutePath(), getBioformatsJarDownloadURLString());
	}
	
	private static void cleanupOldBioFormatsJarfiles(){
		//cleanup client side BioFormats .jar files that have old naming scheme or
		//have a SITENAME that matches the newer version we will download
		final String[] bioformatsJarSplit = PropertyLoader.getProperty(PropertyLoader.bioformatsJarFileName, "").split("_");
		//Assume naming format "bioformats_SITENAME_Major_Minor_Build.jar" as defined in vcell/branches/DeployVCell project in DeployVCell.DeploymentProperties.getBioformatsJarName();
		final String vcellSiteName = (bioformatsJarSplit != null && bioformatsJarSplit.length == 5?bioformatsJarSplit[1]:null);
		File[] legacyBioFormatsJarFileNames = getPluginFolder().listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				if(name.equals("bioformats.jar") || (name.startsWith("bioformats_omexml_locicommon") && name.endsWith(".jar")) ){
					return true;
				}
				if(vcellSiteName != null && name.startsWith("bioformats"+"_"+vcellSiteName+"_") && name.endsWith(".jar")){
					return true;
				}
				return false;
			}
		});
		for (int i = 0; legacyBioFormatsJarFileNames != null && i < legacyBioFormatsJarFileNames.length; i++) {
			if(legacyBioFormatsJarFileNames[i].isFile()){
				System.out.println("Removing file '"+legacyBioFormatsJarFileNames[i].getAbsolutePath()+"' success="+legacyBioFormatsJarFileNames[i].delete());
			}
		}
	}
	
	public static boolean anyBioFormatsLocalDownloadsExists() throws Exception{
		if(VCellClientClasspathUtils.getPluginFolder() != null){
			File[] bioformatsPluginFiles = VCellClientClasspathUtils.getPluginFolder().listFiles(new FileFilter() {
				public boolean accept(File pathname) {
					return pathname.isFile() && pathname.getName().startsWith("bioformats_") && pathname.getName().endsWith(".jar");
				}
			});
			return bioformatsPluginFiles != null && bioformatsPluginFiles.length > 0;
		}
		return false;
	}

}