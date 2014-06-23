package cbit.vcell.resource;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * cygwin library Chombo compiled against
 * @author gweatherby
 *
 */
public class ChomboCygwinDLL extends CygwinDLL {
	/**
	 * associated library names
	 */
	protected static final String[] LIB_NAMES = { "cyggcc_s-seh-1.dll","cygstdc++-6.dll","cyggfortran-3.dll", "cygquadmath-0.dll"};

	/**
	 * lazily evaluated collection of names
	 */
	private Collection<String> bundledNames = null;

	@Override
	public String version() throws UnsupportedOperationException {
		return "cygwin1.chombo"; 
	}

	/**
	 * return collection containing {@link #LIB_NAMES}
	 */
	@Override
	public Collection<String> bundledLibraryNames() {
		if (bundledNames != null) {
			return bundledNames;
		}
		bundledNames = Collections.unmodifiableCollection(Arrays.asList(LIB_NAMES));
		return bundledNames; 
	}

}
