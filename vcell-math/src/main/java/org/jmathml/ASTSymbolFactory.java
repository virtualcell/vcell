package org.jmathml;

/**
 * Clients providing symbol functions should provide an implementation of this
 * class to create symbols for given urlEncodings.
 * 
 * @author radams
 *
 */
public abstract class ASTSymbolFactory {

	/**
	 * A factory should be created with a domain specific, unique id. This
	 * object's equality is based on its id.
	 * 
	 * @param id A non-null <code>String</code>
	 * @throws IllegalArgumentException
	 *             if <code>id </code> is <code>null</code> or empty.
	 */
	public ASTSymbolFactory(String id) {
		super();
		if (id == null || id.equals("")) {
			throw new IllegalArgumentException();
		}
		this.id = id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ASTSymbolFactory other = (ASTSymbolFactory) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	private String id;

	public final String getId() {
		return id;
	}

	/**
	 * Boolean test for whether this factory can create an {@link ASTSymbol}
	 * object from the specified URL encoding.
	 * 
	 * @param urlEncoding The URL of the symbol
	 * @return <code>true</code> if an {@link ASTSymbol} can be created,
	 *         <code>false</code> otherwise.
	 */
	protected abstract boolean canCreateSymbol(String urlEncoding);

	/**
	 * Clients implement this method to create their domain specific ASTSymbol.
	 * 
	 * @param urlEncoding The URL of the symbol
	 * @return An {@link ASTSymbol} or <code>null</code> if the object could not
	 *         be created.
	 */
	protected abstract ASTSymbol createSymbol(String urlEncoding);

}
