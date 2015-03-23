package cbit.vcell.resource;

import java.io.File;
import java.util.Collection;
/**
 * library (so / dll ) with multiple versions 
 * multiple versions of the same library are supported by making {@link #libraryName()} the same 
 * but {@link #version()} different. Clients should call {@link #makePresentIn(File)} immediate before use
 * to ensure the correct version is used.	
 * @author gweatherby
 *
 */
public interface VersionedLibrary {
	public final VersionedLibrary CYGWIN_DLL_CHOMBO = new ChomboCygwinDLL();
	public final VersionedLibrary CYGWIN_DLL_BIONETGEN = new BioNetGenCygwinDLL(); 
	public final VersionedLibrary CYGWIN_DLL_NFSIM = new ChomboCygwinDLL(); 
	public final VersionedLibrary NONE = new LicensedPlaceholder(); 
	
	/**
	 * make file present in directory.
	 * {@link #isLicensed()} must be true
	 * @param directory
	 * @throws IllegalStateException if not licensed
	 */
	public void makePresentIn(File directory) throws IllegalStateException;
	
	/**
	 * optional; required if {@link #isInstalled()} may return false
	 * @return name of library
	 * @throws UnsupportedOperationException 
	 */
	public String libraryName( ) throws UnsupportedOperationException;
	/**
	 * optional; required if {@link #isInstalled()} may return false
	 * @return specific version name of library; 
	 * @throws UnsupportedOperationException
	 */
	public String version( ) throws UnsupportedOperationException;
	
	
	/**
	 * unmodifiable list of library names bundled into deliverable
	 * @return collection of library names; may be empty but not null 
	 */
	public Collection<String> bundledLibraryNames( );
	

}
