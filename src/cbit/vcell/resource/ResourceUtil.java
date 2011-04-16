package cbit.vcell.resource;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ResourceUtil {
	private static final String system_osname = System.getProperty("os.name");
	public final static boolean bWindows = system_osname.contains("Windows");
	public final static boolean bMac = system_osname.contains("Mac");
	public final static boolean bLinux = system_osname.contains("Linux");
	public final static String osname;
	static {
		if (bWindows) {
			osname = "windows";
		} else if (bMac) {
			osname = "mac";
		} else if (bLinux) {
			osname = "linux";
		} else {
			throw new RuntimeException(system_osname + " is not supported by the Virtual Cell.");
		}
	}
	public final static String EXE_SUFFIX = bWindows ? ".exe" : "";
	public final static String RES_PACKAGE = "/cbit/vcell/resource/" + osname;
	
	private static File userHome = null;
	private static File vcellHome = null;
	private static File libDir = null;
	private static File localSimDir = null;
	
	private final static String DLL_GLUT;
	static {
		if (bWindows) {
			DLL_GLUT = "glut32.dll";
		} else {
			DLL_GLUT = "libglut.so";
		}
	}
	private final static String EXE_SMOLDYN = "smoldyn" + EXE_SUFFIX;
	private static File smoldynExecutable = null;
	private final static String RES_EXE_SMOLDYN = RES_PACKAGE + "/" + EXE_SMOLDYN;
	private final static String RES_DLL_GLUT = RES_PACKAGE + "/" + DLL_GLUT;
	
	private static File solversDirectory = null;
	private final static String EXE_COMBINED_SUNDIALS = "SundialsSolverStandalone" + EXE_SUFFIX;
	private final static String RES_EXE_COMBINED_SUNDIALS = RES_PACKAGE + "/" + EXE_COMBINED_SUNDIALS;
	private static File combinedSundialsExecutable = null;
	
	private static List<String> libList = null;

	public static File getUserHomeDir()
	{
		if(userHome == null)
		{
			userHome = new File(System.getProperty("user.home"));
			if (!userHome.exists()) {
				userHome = new File(".");
			}
		}
		
		return userHome; 
	}
	
	public static File getLocalSimDir()
	{
		if(localSimDir == null)
		{
			localSimDir = new File(getVcellHome(), "simdata");
			if (!localSimDir.exists()) {
				localSimDir.mkdirs();
			}
		}
		
		return localSimDir; 
	}
	
	public static void writeFileFromResource(String resname, File file) throws IOException {
		java.net.URL url = ResourceUtil.class.getResource(resname);
		if (url == null) {
			throw new RuntimeException("ResourceUtil::writeFileFromResource() : Can't get resource for " + resname);
		}
		
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;

		try {
			bis = new BufferedInputStream(url.openConnection().getInputStream());
			bos = new BufferedOutputStream(new FileOutputStream(file));
			byte byteArray[] = new byte[10000];
			while (true) {
				int numRead = bis.read(byteArray, 0, byteArray.length);
				if (numRead == -1) {
					break;
				}
				
				bos.write(byteArray, 0, numRead);
			}
			if (!bWindows) {
				System.out.println("Make " + file + " executable");
				Runtime.getRuntime().exec("chmod 755 " + file);
			}
		} finally {
			try {
				if (bis != null) {
					bis.close();
				}
				if (bos != null) {
					bos.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace(System.out);
			}
		}			
	}
	
	public static void loadLibrary(String libname) {
		if (libDir == null) {
			libDir = new File(vcellHome, "lib");
			if (!libDir.exists()) {
				libDir.mkdirs();
			}
			libList = Collections.synchronizedList(new ArrayList<String>());
		}
		
		if (libList.contains(libname)) {
			return;
		}
		String lib = System.mapLibraryName(libname);	// platform dependent dynamic library name
		File libfile = new File(libDir, lib);
		try {
			writeFileFromResource(RES_PACKAGE + "/" + lib, libfile);
			System.out.println("loading " + libfile);
			System.load(libfile.getAbsolutePath());
			libList.add(libname);
		} catch (IOException ex) {
			throw new RuntimeException("ResourceUtil::loadLibrary() : failed to write library " + libname + " (" + ex.getMessage() + "). Please try again.");
		}		
	}

	public static File getVcellHome() 
	{
		if(vcellHome == null)
		{
			vcellHome = new File(getUserHomeDir(), ".vcell");
			if (!vcellHome.exists()) {
				vcellHome.mkdirs();
			}
		}
		return vcellHome;
	}	
		
	public static File getSolversDirectory() 
	{
		if(solversDirectory == null)
		{
			solversDirectory = new File(getVcellHome(), "solvers");
			if (!solversDirectory.exists()) {
				solversDirectory.mkdirs();
			}
		}
		return solversDirectory;
	}	
	
	public static void loadNativeSolverLibrary () {
		try {
	        System.loadLibrary("NativeSolvers");
	    } catch (Throwable ex1) {
    		throw new RuntimeException("ResourceUtil::loadNativeSolverLibrary() : failed to load native solver library " + ex1.getMessage());
		}
	}
	
	public static void loadlibSbmlLibray () {
		try {
//			System.loadLibrary("expat");
//			System.loadLibrary("sbml");
//			System.loadLibrary("sbml-requiredElements");
//			System.loadLibrary("sbml-spatial");
			System.loadLibrary("libsbml");
			System.loadLibrary("sbmlj");
		} catch (Throwable ex1){
			throw new RuntimeException("ResourceUtil::loadlibSbmlLibray() : failed to load libsbml libraries " + ex1.getMessage());
		}
	}
	
	private static boolean bFirstTimeSmoldyn = true;
	public static File getSmoldynExecutable() throws IOException {
		if (smoldynExecutable == null) {
			smoldynExecutable = new java.io.File(getSolversDirectory(), EXE_SMOLDYN);
		}
		if (bFirstTimeSmoldyn || !smoldynExecutable.exists()) {
			ResourceUtil.writeFileFromResource(RES_EXE_SMOLDYN, smoldynExecutable);
		}
		File file_glut_dll = new java.io.File(getSolversDirectory(), DLL_GLUT);
		if (!bMac && (bFirstTimeSmoldyn || !file_glut_dll.exists())) {
			ResourceUtil.writeFileFromResource(RES_DLL_GLUT, file_glut_dll);
		}
		bFirstTimeSmoldyn = false;
		return smoldynExecutable;
	}
	private static boolean bFirstTimeQuickOdeRun = true;
	public static File getCombinedSundialsExecutable() throws IOException {
		if (combinedSundialsExecutable == null) {
			combinedSundialsExecutable = new java.io.File(getSolversDirectory(), EXE_COMBINED_SUNDIALS);
		}
		if (bFirstTimeQuickOdeRun || !combinedSundialsExecutable.exists()) {
			ResourceUtil.writeFileFromResource(RES_EXE_COMBINED_SUNDIALS, combinedSundialsExecutable);
		}
		bFirstTimeQuickOdeRun = false;
		return combinedSundialsExecutable;
	}
}
