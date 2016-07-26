package cbit.vcell.resource;


/**
 * immutable class to map URLs to destination library names
 */
public class ProvidedLibrary {

	/**
	 * resource url to extract from, without package name
	 */
	public final String resourceUrl;
	/**
	 * named of library on file system, without path name
	 */
	public final String libraryName;
	/**
	 * see {@link #isCacheable()}
	 */
	private final boolean cacheable;
	/**
	 * @param resourceUrl may not be null
	 * @param libraryName may not be null
	 * sets cacheable false
	 */
	public ProvidedLibrary(String resourceUrl, String libraryName) {
		assert(resourceUrl != null);
		assert(libraryName != null);

		this.resourceUrl = resourceUrl;
		this.libraryName = libraryName;
		cacheable = false;
	}
	/**
	 * @param libraryName resource and file name are the same may not be null
	 * sets cacheable true
	 */
	public ProvidedLibrary(String libraryName) {
		assert(libraryName != null);
		this.resourceUrl = libraryName;
		this.libraryName = libraryName;
		cacheable = true;
	}

	/**
	 * is library sufficiently unique, for the given OperatingSystem that it can be cached?
	 * @return true if it is
	 */
	public boolean isCacheable( ) {
		return cacheable;
	}
	/**
	 * compare {@link #resourceUrl}
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProvidedLibrary other = (ProvidedLibrary) obj;
		return resourceUrl.equals(other.resourceUrl);
	}
	@Override
	public int hashCode() {
		return resourceUrl.hashCode();
	}


}