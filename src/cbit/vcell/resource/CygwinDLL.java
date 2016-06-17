package cbit.vcell.resource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * GPL licensed cygwin1.dll 
 * @author gweatherby
 *
 */
abstract class CygwinDLL implements VersionedLibrary {
	/**
	 * associated library names
	 */
	protected static final String[] LIB_64_NAMES = { "cyggcc_s-seh-1.dll","cygstdc++-6.dll","cyggfortran-3.dll", "cygquadmath-0.dll", "cygz.dll"};
	protected static final String[] LIB_32_NAMES = { "cyggcc_s-1.dll","cygstdc++-6.dll"};
	private final OperatingSystemInfo operatingSystemInfo;
	private Collection<ProvidedLibrary> libs;
	protected CygwinDLL( ) {
		operatingSystemInfo = OperatingSystemInfo.getInstance();
	     libs = null; 
	}
	public String libraryName() {
		return "cygwin1.dll"; 
	}
	
	@Override
	public Collection<ProvidedLibrary> getLibraries() {
		if (libs != null) {
			return libs;
		}
		if (operatingSystemInfo.isWindows()) {
			libs = new ArrayList<>();
			ProvidedLibrary pl = new ProvidedLibrary(version( ), "cygwin1.dll");
			libs.add( pl ) ; 
			Collection<String> bitLibs;
			if (operatingSystemInfo.is64bit()) {
				bitLibs = get64bit( );
			}
			else {
				bitLibs = get32bit( );
			}
			for (String supportingDll: bitLibs) {
				pl = new ProvidedLibrary(supportingDll);
				libs.add( pl ) ; 
			}
			return libs;
		}
		
		libs =  Collections.emptyList( ); 
		return libs; 
	}
	protected abstract String version( ); 
	
	protected Collection<String> get32bit() {
		return Collections.unmodifiableCollection(Arrays.asList(LIB_32_NAMES));
	}
	protected Collection<String> get64bit() {
		return Collections.unmodifiableCollection(Arrays.asList(LIB_64_NAMES));
	}

}
