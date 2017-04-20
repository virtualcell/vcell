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
	public final VersionedLibrary DEPENDENT_LIBS_VCELL = new VCellDependentLibraries();
	public final VersionedLibrary DEPENDENT_LIBS_BNG = new BioNetGenDependentLibraries(); 
	public final VersionedLibrary NONE = new LicensedPlaceholder(); 
	/**
	 * collection of zero to many libraries which should be extracted from  resources
	 * @return non-null , possibly empty collection
	 */
	public Collection<ProvidedLibrary> getLibraries( );
	

}
