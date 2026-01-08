package org.jlibsedml.components;

import org.jlibsedml.IIdentifiable;
import org.jlibsedml.SEDMLVisitor;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Base class of SEDML elements that can be annotated or contain notes.
 * @author anu/radams
 *
 */
public abstract class SedBase implements SedGeneralClass, IIdentifiable, Comparable<SedBase>{
	private String metaId = null;
    private final SId id;
    private String name;
	
	private Notes notes;
    private Annotation annotation;


    public SedBase(SId id, String name) {
        super();
        this.id = id; // Since id is optional, we use `null` to indicate "not present"
        this.name = name;
    }

    public abstract boolean accept(SEDMLVisitor visitor);
    /**
     * Getter for the metaid attribute of this element.
     * @return If present, a non-empty string. Otherwise, null is returned
     */
    public String getMetaId() {
        return this.metaId;
    }

    /**
     * Setter for the metaid attribute of this element.
     * @param metaId A non-null <code>String</code> of the meta_id attribute.
     */
    public void setMetaId(String metaId) {
        this.metaId = metaId;
    }

    /**
     * Getter for the id attribute.
     * @return If present, a non-empty string. Otherwise, null is returned
     */
    public String getIdAsString() {
        if (this.id == null) return null;
        return this.id.string();
    }

    /**
     * Getter for the id attribute.
     * @return If present, a non-empty string. Otherwise, null is returned
     */
    public SId getId() {
        return this.id;
    }

    /**
     * @return If present, a non-empty string. Otherwise, null is returned
     */
    public String getName() {
        return this.name;
    }

    /**
     * Setter for the name of this object.
     * @param name A short human-readable <code>String</code>. Can be <code>null</code>.
     * @since 1.2.0
     */
    public void setName(String name) {
        this.name = name;
    }


    /**
     * Getter for the {@link Notes} object associated with this <code>SedBase</code> object.
     * @return The {@link Notes} for this object, or null if no {@link Notes} object exists
     */
	public Notes getNotes() {
        return this.notes;
    }

	/**
	 * Setter for a {@link Notes}. 
	 * @param notes Can be null, if the {@link Notes} are to be removed from this object.
	 */
    public void setNotes(Notes notes) {
        this.notes = notes;
    }


    /**
     * Getter for the {@link Annotation} object associated with this <code>SedBase</code> object.
     * @return The {@link Annotation} for this object, or null if no {@link Annotation} object exists
     */
    public Annotation getAnnotations() { return this.annotation; }

	
	/**
	 * Sets the {@link Annotation} for this <code>SedBase</code> object.
	 * @param ann Can be null, if the {@link Annotation} is to be removed from this object.
	 */
	public void setAnnotation(Annotation ann) {
        if (null == ann) throw new IllegalArgumentException("Annotation provided is null");
        this.annotation = ann;
	}


    /**
     * Returns the parameters that are used in <code>this.equals()</code> to evaluate equality.
     * Needs to be returned as `member_name=value.toString(), ` segments, and it should be appended to a `super` call to this function.
     * <br\>
     * e.g.: `super.parametersToString() + ", " + String.format(...)`
     * @return the parameters and their values, listed in string form
     */
    @OverridingMethodsMustInvokeSuper
    public String parametersToString(){
        List<String> params = new ArrayList<>();
        if (this.id != null) params.add(String.format("id=%s", this.id.string()));
        if (this.name != null) params.add(String.format("name=%s", this.name));
        if (this.metaId != null) params.add(String.format("metaId=%s", this.metaId));
        return String.join(", ", params); // We're the top level call! No super call to make here!
    }

    @Override
    public String toString(){
        return String.format("%s [%s]", this.getClass().getSimpleName(), this.parametersToString());
    }

    @Override
    public boolean equals(Object obj){
        if (obj == this)
            return true;
        if (!(obj instanceof SedBase otherSedBase))
            return false;

        return  //Objects.equals(this.getMetaId(), otherSedBase.getMetaId()) &&
                Objects.equals(this.getId(), otherSedBase.getId()) &&
                Objects.equals(this.getName(), otherSedBase.getName());

    }

    //TODO: Verify this is the hash code we want!
    @Override
    public int hashCode() { // written by Ion
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    /**
     * Compares identifiable elements based on <code>String</code> comparison
     *  of their identifiers.
     */
    public int compareTo(SedBase o) {
        return this.getIdAsString().compareTo(o.getIdAsString());
    }


}
