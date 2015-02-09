package cbit.vcell.resource;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.vcell.util.PropertyLoader;

/**
 * GPL licensed cygwin1.dll 
 * @author gweatherby
 *
 */
abstract class CygwinDLL implements LicensedLibrary {
	private final OperatingSystemInfo operatingSystemInfo = OperatingSystemInfo.getInstance();
	/**
	 * @return true if not Windows. If Windows, return whether user has accepted license
	 */
	@Override
	public boolean isLicensed() {
		if (operatingSystemInfo.isWindows()) {
			return LicenseManager.isLicensed(LibraryLicense.CYGWIN);
		}
		return true;
	}

	@Override
	public LibraryLicense licensedRequired() {
		return LibraryLicense.CYGWIN; 
	}

	@Override
	public String libraryName() {
		return "cygwin1.dll"; 
	}

	@Override
	public URL downloadUrl() {
		try {
			String urlS = PropertyLoader.getProperty(PropertyLoader.vcellDownloadDir,"http://vcell.org/webstart/");
			//urlS += LibraryLicense.CYGWIN.category.name() + '/' + ResourceUtil.NATIVE_LIB_DIR + '/' + version(); 
			urlS += LibraryLicense.CYGWIN.category.name() + '/' + operatingSystemInfo.getNativeLibDirectory() + version(); 
			return new URL(urlS);
		} catch (MalformedURLException mue) {
			throw new RuntimeException("bad code", mue);
		}
	}
	
	@Override
	public boolean isInstalled() {
		if (operatingSystemInfo.isWindows()) {
			return LicenseManager.isPresent(this);
		}
		return true;
	}

	@Override
	public void makePresentIn(File directory) {
		if (operatingSystemInfo.isWindows()) {
			if (!isLicensed()) {
				throw new IllegalStateException(getClass( ).getName() + ".makePresentIn called while not licensed");
			}
			try {
				if (!isInstalled()) {
					LicenseManager.install(this);
				}
				LicenseManager.copyLicensedLibraries(this, directory);

			} catch (IOException e) {
				throw new RuntimeException("exception installing " + libraryName(),e); 
			}
		}
	}
}
