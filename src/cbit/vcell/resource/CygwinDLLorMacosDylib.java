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
abstract class CygwinDLLorMacosDylib implements VersionedLibrary {
	/**
	 * associated library names
	 */
	protected static final String[] MACOS_LIB_NAMES 	= { "libgcc_s.1.dylib", "libstdc++.6.dylib", "libgfortran.3.dylib", "libquadmath.0.dylib"};
	protected static final String[] CYGWIN_LIB_64_NAMES = { "cyggcc_s-seh-1.dll","cygstdc++-6.dll","cyggfortran-3.dll", "cygquadmath-0.dll", "cygz.dll"};
	protected static final String[] CYGWIN_LIB_32_NAMES = { "cyggcc_s-1.dll","cygstdc++-6.dll"};
	private final OperatingSystemInfo operatingSystemInfo;
	private Collection<ProvidedLibrary> libs;
	protected CygwinDLLorMacosDylib( ) {
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
			ProvidedLibrary pl = new ProvidedLibrary(cygwinSourceFilename( ), "cygwin1.dll");
			libs.add( pl ) ; 
			Collection<String> bitLibs;
			if (operatingSystemInfo.is64bit()) {
				bitLibs = getCygwin64bit( );
			}
			else {
				bitLibs = getCygwin32bit( );
			}
			for (String supportingDll: bitLibs) {
				pl = new ProvidedLibrary(supportingDll);
				libs.add( pl ) ; 
			}
			return libs;
		}else if (operatingSystemInfo.isMac()) {
			libs = new ArrayList<>();
			for (String supportingDll: MACOS_LIB_NAMES) {
				libs.add(new ProvidedLibrary(supportingDll));
			}
			return libs;
		}
		
		libs =  Collections.emptyList( ); 
		return libs; 
	}
	protected abstract String cygwinSourceFilename( ); 
	
	protected Collection<String> getCygwin32bit() {
		return Collections.unmodifiableCollection(Arrays.asList(CYGWIN_LIB_32_NAMES));
	}
	protected Collection<String> getCygwin64bit() {
		return Collections.unmodifiableCollection(Arrays.asList(CYGWIN_LIB_64_NAMES));
	}

}
