package org.vcell.sbml;

import cbit.vcell.resource.ResourceUtil;

/**
 * base class to set current working directory as vcell.installDir, if not already set
 * @author GWeatherby
 *
 */
public class CurrentWorkingAsInstall {
	private static final String IPROP = "vcell.installDir";

	/**
	 *  set current working directory as vcell.installDir, if not already set
	 */
	public CurrentWorkingAsInstall() {
		init( );
	}
	
	/**
	 * set current working directory as vcell.installDir, if not already set
	 */
	public static void init( ) {
		if (System.getProperty(IPROP) == null) {
			System.setProperty(IPROP, "cwd");
		}
		ResourceUtil.setNativeLibraryDirectory();
		
	}
}
