package org.jlibsedml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Base class of SEDML elements that can be annotated or contain notes.
 * @author anu/radams
 *
 */
public abstract class SEDBase {
	
	private String metaId = null;
	// this is deprecated from v2 onwards.
	private List<Notes> notes = new ArrayList<Notes>();
	
	// this is deprecated from v2 onwards.
	private List<Annotation> annotation = new ArrayList<Annotation>();
	
	private Notes note;
	
	public Notes getNote() {
        return note;
    }

	/**
	 * Setter for a {@link Notes}. 
	 * @param note Can be null, if the Notes are to be removed from this object. 
	 */
    public void setNote(Notes note) {
        this.note = note;
    }

    /**
	 * Getter for the metaid attribute of this element.
	 * @return A <code>String</code> of the meta_id attribute, or an empty <code>String</code> if not set.
	 */
	public String getMetaId() {
		return metaId;
	}

	/**
	 * Setter for the metaid attribute of this element. 
	 * @param metaId A non-null <code>String</code> of the meta_id attribute. 
	 */
	public void setMetaId(String metaId) {	
			this.metaId = metaId;	
	}

	/**
	 * Gets a read-only view of the Notes for this element.
	 * @return An unmodifiable <code>List</code> of <code>Notes</code>.
	 * @deprecated Use getNote() from now on. 
	 */
	public  List<Notes> getNotes() {
		return Collections.unmodifiableList(notes);
	}
	
	/**
	 * Adds a <code>Note</code>, if this note does not already exist.
	 * @param note A non-null {@link Notes}
	 * @return <code>true</code> if <code>note</code> added, <code>false</code> otherwise.
	 * @deprecated From now on, setNote(Note note) should be used. 
	 */
	public boolean addNote(Notes note) {
		if (!notes.contains(note)){
			return notes.add(note);
		}
		return false;
	}
	
	/**
	 * Removes a <code>Note</code> from this element's list of {@link Notes} objects.
	 * @param note A non-null {@link Notes}
	 * @return <code>true</code> if <code>note</code> removed, <code>false</code> otherwise.
	 *  @deprecated Use setNote(null) from now on.
	 */
	public boolean removeNote(Notes note) {
			return notes.remove(note);
	}

	/**
	 * Directly sets a list of Notes into this element.
	 * @param notes A non-null <code>List</code> of {@link Notes} objects.
	 * @throws IllegalArgumentException if <code>notes</code> is <code>null</code>.
	 * @deprecated Use setNote(Note n) from now on.
	 */
	public void setNotes(List<Notes> notes) {
		Assert.checkNoNullArgs(notes);
		this.notes = notes;
	}

	/**
	 * Gets a read-only view of the {@link Annotation}s for this element.
	 * @return An unmodifiable <code>List</code> of <code>Annotation</code>.
	 * @deprecated
	 */
	public  List<Annotation> getAnnotation() {
		return Collections.unmodifiableList(annotation);
	}
	
	/**
	 * Adds a <code>Annotation</code>, if this annotation does not already exist in this element's annotations.
	 * @param ann A non-null {@link Annotation}
	 * @return <code>true</code> if <code>ann</code> added, <code>false</code> otherwise.
	 */
	public boolean addAnnotation(Annotation ann) {
		if (!annotation.contains(ann)){
			return annotation.add(ann);
		}
		return false;
	}
	
	/**
	 * Removes a <code>Annotation</code> from this element's list of {@link Annotation} objects.
	 * @param ann A non-null {@link Annotation}
	 * @return <code>true</code> if <code>ann</code> removed, <code>false</code> otherwise.
	 */
	public boolean removeAnnotation(Annotation ann) {	
			return annotation.remove(ann);
	}

	/**
	 * Directly sets a list of Annotations into this element.
	 * @param annotations A non-null <code>List</code> of {@link Annotation} objects.
	 * @throws IllegalArgumentException if <code>annotations</code> is <code>null</code>.
	 */
	public void setAnnotation( List<Annotation> annotations) {
		Assert.checkNoNullArgs(annotations);
		this.annotation = annotations;
	}
	
	/**
	 * Provides a link between the object model and the XML element names
	 * @return A non-null <code>String</code> of the XML element name of the object.
	 */
	public abstract String getElementName();
	
	public  abstract boolean accept(SEDMLVisitor visitor);

}
