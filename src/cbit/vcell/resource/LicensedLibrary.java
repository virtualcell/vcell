package cbit.vcell.resource;

import java.io.File;
import java.net.URL;
import java.util.Collection;

/**
 * library (so / dll ) with special license
 * multiple versions of the same library are supported by making {@link #libraryName()} the same 
 * but {@link #version()} different. Clients should call {@link #makePresentIn(File)} immediate before use
 * to ensure the correct version is used.	
 * @author gweatherby
 *
 */
public interface LicensedLibrary {
	public final LicensedLibrary DependentSolverLibraries = new DependentLibrariesVCell();
	public final LicensedLibrary CYGWIN_DLL_BIONETGEN = new DependentLibrariesBioNetGen(); 
	public final LicensedLibrary NONE = new LicensedPlaceholder(); 
	
	/**
	 * is library licensed?
	 */
	public boolean isLicensed( );
	/**
	 * is library installed 
	 */
	public boolean isInstalled( );
	
	/**
	 * make file present in directory.
	 * {@link #isLicensed()} must be true
	 * @param directory
	 * @throws IllegalStateException if not licensed
	 */
	public void makePresentIn(File directory) throws IllegalStateException;
	
	/**
	 * optional; required if {@link #isLicensed()} may return false
	 * @return license required for this particular library
	 * @throws UnsupportedOperationException 
	 */
	public LibraryLicense licensedRequired( ) throws UnsupportedOperationException;
	
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
	 * optional; required if {@link #isInstalled()} may return false
	 * @return URL to download from  
	 * @throws UnsupportedOperationException 
	 */
	public URL downloadUrl( ) throws UnsupportedOperationException;
	
	/**
	 * unmodifiable list of library names bundled into deliverable
	 * @return collection of library names; may be empty but not null 
	 */
	public Collection<String> bundledLibraryNames( );
	

}
