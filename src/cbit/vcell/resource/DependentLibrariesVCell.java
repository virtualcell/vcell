package cbit.vcell.resource;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * cygwin library Chombo compiled against
 * @author gweatherby
 *
 */
public class DependentLibrariesVCell extends DependentLibraries {
	/**
	 * associated library names
	 */
	protected static final String[] MACOS_LIB_NAMES 	= { "libgcc_s.1.dylib", "libstdc++.6.dylib", "libgfortran.3.dylib", "libquadmath.0.dylib"};
	protected static final String[] CYGWIN_LIB_64_NAMES = { "cyggcc_s-seh-1.dll","cygstdc++-6.dll","cyggfortran-3.dll", "cygquadmath-0.dll", "cygz.dll"};
	protected static final String[] CYGWIN_LIB_32_NAMES = { "cyggcc_s-1.dll","cygstdc++-6.dll"};

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
		OperatingSystemInfo operatingSystemInfo = OperatingSystemInfo.getInstance();
		if (operatingSystemInfo.isWindows()){
			if (operatingSystemInfo.is64bit()){
				bundledNames = Collections.unmodifiableCollection(Arrays.asList(CYGWIN_LIB_64_NAMES));
			}else{
				bundledNames = Collections.unmodifiableCollection(Arrays.asList(CYGWIN_LIB_32_NAMES));
			}
		}else if (operatingSystemInfo.isMac()){
			bundledNames = Collections.unmodifiableCollection(Arrays.asList(MACOS_LIB_NAMES));
		}
		return bundledNames; 
	}

}
