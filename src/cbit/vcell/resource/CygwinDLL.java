package cbit.vcell.resource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * GPL licensed cygwin1.dll 
 * @author gweatherby
 *
 */
abstract class CygwinDLL implements VersionedLibrary {
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
	
	protected Collection<String> get32bit( ) {
		return Collections.emptyList( ); 
	}
	protected Collection<String> get64bit( ) {
		return Collections.emptyList( ); 
	}

}
