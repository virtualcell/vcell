package org.jlibsedml;

/**
 * Base class for all elements that have a mandatory Id attribute and a human readable name.
 * Object equality is based on  the ID element. Default sorting is also based on the sort-order of the 
 * <code>String</code> id attribute.
 * Clients should note this is NOT an API class and is not intended  to be sub-classed or referenced by clients.
 * @author radams
 *
 */
 public abstract class AbstractIdentifiableElement extends SEDBase implements IIdentifiable, Comparable<AbstractIdentifiableElement> {
	
    private SId id;
	private String name;



	/**
	 * Setter for the name of this object. 
	 * @param name A short human-readable <code>String</code>. Can be <code>null</code>.
	 * @since 1.2.0
	 */
    public void setName(String name) {
        this.name = name;
    }

    /**
	 * @return the name for this element, may be <code>null</code> or empty.
	 */
	public String getName() {
		return name;
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
		AbstractIdentifiableElement other = (AbstractIdentifiableElement) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	/**
	 * 
	 * @param id A non-null, non empty String.
	 * @param name An optional <code>String</code> for a human readable descriptor of the element. This can be
	 *   <code>null</code> or empty.
	 */
	public AbstractIdentifiableElement(String id, String name) {
		super();
		if(SEDMLElementFactory.getInstance().isStrictCreation())
			Assert.checkNoNullArgs(id);
		this.id = new SId(id);
		this.name=name;
	}

	/**
	 * Getter for the id attribute.
	 * @return A non-null, non-empty ID string.
	 */
	public String getId() {
		return id.getString();
	}

	/**
	 * Compares identifiable elements based on <code>String</code> comparison  
	 *  of their identifiers.
	 */
	public int compareTo(AbstractIdentifiableElement o) {
		return id.getString().compareTo(o.getId());
	}

}
