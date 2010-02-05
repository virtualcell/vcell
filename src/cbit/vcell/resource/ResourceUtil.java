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
	public final static boolean bWindows = System.getProperty("os.name").indexOf("Windows") >= 0 ? true : false;
	public final static boolean bMac = System.getProperty("os.name").indexOf("Mac") >= 0 ? true : false;
	public final static boolean bLinux = System.getProperty("os.name").indexOf("Linux") >= 0 ? true : false;
	public static String osname = null;
	static {
		if (bWindows) {
			osname = "windows";
		} else if (bMac) {
			osname = "mac";
		} else if (bLinux) {
			osname = "linux";
		} else {
			throw new RuntimeException(System.getProperty("os.name") + " is not supported by the Virtual Cell.");
		}
	}
	public final static String EXE_SUFFIX = bWindows ? ".exe" : "";
	public final static String RES_PACKAGE = "/cbit/vcell/resource/" + osname;
	
	private static File userHome = null;
	private static File vcellHome = null;
	private static File libDir = null;
	
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
	
	
	public static void loadNativeSolverLibrary () {
		try {
	        System.loadLibrary("NativeSolvers");
	    } catch (Throwable ex1) {
	    	if (bMac) {
				try {
					System.loadLibrary("NativeSolversG5");
				} catch (Throwable ex2){					
					throw new RuntimeException("ResourceUtil::loadNativeSolverLibrary() : failed to load native solver library " + ex2.getMessage());					
				}
	    	} else {
	    		throw new RuntimeException("ResourceUtil::loadNativeSolverLibrary() : failed to load native solver library " + ex1.getMessage());
	    	}
		}
	}
	
	public static void loadlibSbmlLibray () {
		try {
			System.loadLibrary("expat");
			System.loadLibrary("sbml");
			System.loadLibrary("sbmlj");
		} catch (Throwable ex1){
			if (bMac) { // try again if Mac has power PC
				try {
					System.loadLibrary("expatG5");
					System.loadLibrary("sbmlG5");
					System.loadLibrary("sbmljG5");
				} catch (Throwable ex2){					
					throw new RuntimeException("ResourceUtil::loadlibSbmlLibray() : failed to load libsbml library " + ex2.getMessage());					
				}				
			} else {
				throw new RuntimeException("ResourceUtil::loadlibSbmlLibray() : failed to load libsbml library " + ex1.getMessage());
			}
		}
	}
}
