package cbit.vcell.resource;

import java.io.File;
import java.util.Collection;
import java.util.Collections;

/**
 * empty LicensedLibrary
 * avoids having to check variables for null
 * @author gweatherby
 *
 */
class LicensedPlaceholder implements VersionedLibrary {
	
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
	 * @return empty list
	 */
	@Override
	public Collection<String> bundledLibraryNames() {
		return Collections.emptyList(); 
	}

	/**
	 * no op
	 */
	@Override
	public void makePresentIn(File directory) {
	}

}
