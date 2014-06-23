package cbit.vcell.resource;

import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;

/**
 * empty LicensedLibrary
 * avoids having to check variables for null
 * @author gweatherby
 *
 */
class LicensedPlaceholder implements LicensedLibrary {
	
	/**
	 * @return true always
	 */
	@Override
	public boolean isLicensed() {
		return true; 
	}

	/**
	 * @throws UnsupportedOperationException
	 */
	@Override
	public LibraryLicense licensedRequired() {
		throw new UnsupportedOperationException();
	}

	/**
	 * @throws UnsupportedOperationException
	 */
	@Override
	public String libraryName() {
		throw new UnsupportedOperationException();
	}

	/**
	 * @throws UnsupportedOperationException
	 */
	@Override
	public String version() throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	/**
	 * @throws UnsupportedOperationException
	 */
	@Override
	public URL downloadUrl() {
		throw new UnsupportedOperationException();
	}

	/**
	 * @return empty list
	 */
	@Override
	public Collection<String> bundledLibraryNames() {
		return Collections.emptyList(); 
	}

	/**
	 * @return true 
	 */
	@Override
	public boolean isInstalled() {
		return true;
	}

	/**
	 * no op
	 */
	@Override
	public void makePresentIn(File directory) {
	}

}
