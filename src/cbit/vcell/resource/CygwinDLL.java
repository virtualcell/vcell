package cbit.vcell.resource;

import java.io.File;

/**
 * GPL licensed cygwin1.dll 
 * @author gweatherby
 *
 */
abstract class CygwinDLL implements VersionedLibrary {
	private final OperatingSystemInfo operatingSystemInfo = OperatingSystemInfo.getInstance();
	@Override
	public String libraryName() {
		return "cygwin1.dll"; 
	}

	@Override
	public void makePresentIn(File directory) {
		if (operatingSystemInfo.isWindows()) {
			String resourcePathName = operatingSystemInfo.getResourcePackage() + "/" + version();
			try {
				File dest = new File(directory, libraryName());
				ResourceUtil.writeFileFromResource(resourcePathName,dest); 
			} catch (Exception e) {
				throw new RuntimeException("error providing support library " + libraryName() + " from " + resourcePathName
						+ " " + e.getMessage(),e);
			}
		}
	}
}
