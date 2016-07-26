package cbit.vcell.resource;

import java.util.Collection;
import java.util.Collections;

/**
 * cygwin library packaged with BioNetGen (run_network) 
 * @author gweatherby
 *
 */
public class DependentLibrariesBioNetGen extends DependentLibraries {

	@Override
	public String version() throws UnsupportedOperationException {
		return "cygwin1.bionetgen"; 
	}

	/**
	 * return empty collection 
	 */
	@Override
	public Collection<String> bundledLibraryNames() {
		return Collections.emptyList();
	}

}
