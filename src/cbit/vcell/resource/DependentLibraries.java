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
abstract class DependentLibraries implements LicensedLibrary {
	/**
	 * @return true if not Windows. If Windows, return whether user has accepted license
	 */
	@Override
	public boolean isLicensed() {
		if (ResourceUtil.bWindows) {
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
			urlS += LibraryLicense.CYGWIN.category.name() + '/' + ResourceUtil.NATIVE_LIB_DIR + '/' + version(); 
			return new URL(urlS);
		} catch (MalformedURLException mue) {
			throw new RuntimeException("bad code", mue);
		}
	}
	
	@Override
	public boolean isInstalled() {
		if (ResourceUtil.bWindows) {
			return LicenseManager.isPresent(this);
		}
		return true;
	}

	@Override
	public void makePresentIn(File directory) {
		if (ResourceUtil.bWindows) {
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
