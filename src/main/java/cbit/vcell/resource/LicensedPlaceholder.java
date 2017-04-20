package cbit.vcell.resource;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * empty LicensedLibrary
 * avoids having to check variables for null
 * @author gweatherby
 *
 */
class LicensedPlaceholder implements VersionedLibrary {
	
	@Override
	public Collection<ProvidedLibrary> getLibraries() {
		List<ProvidedLibrary> elist = Collections.emptyList();
		return elist;
	}
	
}
