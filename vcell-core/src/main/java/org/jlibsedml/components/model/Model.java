package org.jlibsedml.components.model;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jlibsedml.*;
import org.jlibsedml.components.AbstractIdentifiableElement;
import org.jlibsedml.components.SedGeneralClass;
import org.jlibsedml.modelsupport.SUPPORTED_LANGUAGE;

import static org.jlibsedml.execution.ArchiveModelResolver.SPACE_URI_ESCAPE_SEQUENCE;

/**
 * Encapsulates a SED-ML Model element. Note that this object is not the
 * computational model itself, but holds information about the model within
 * SED-ML.
 * 
 * @author anu/radams
 * 
 */
public final class Model extends AbstractIdentifiableElement {
	private String language;
	private final String source_path_or_URI_string;
    private final List<Change> listOfChanges = new ArrayList<>();

	/**
     * Standard Constructor for Models
     *
	 * @param id
	 *            - a unique identifier for the model within this SED-ML
	 *            description.
	 * @param name
	 *            - optional, can be null.
	 * @param language
	 *            This should ideally be a URN, which may already be defined in
	 *            {@link SUPPORTED_LANGUAGE}. If no language describes this
	 *            model, the argument may be <code>null</code>.
	 * @param source_path_or_URI_string
	 *            A mandatory URI or local path to the model source.
	 * @throws IllegalArgumentException
	 *             if any argument except <code>name</code> is null or an empty
	 *             string.
	 */
	public Model(String id, String name, String language, String source_path_or_URI_string) {
		super(id, name);
		if (SedMLElementFactory.getInstance().isStrictCreation()) {
            SedGeneralClass.checkNoNullArgs(source_path_or_URI_string);
            SedGeneralClass.stringsNotEmpty(source_path_or_URI_string);
		}
		this.language = language;
		this.source_path_or_URI_string = source_path_or_URI_string;
	}

	/**
	 * Copy constructor for convenience of creating multiple model elements.
	 * This only performs a shallow copy of the argument's attributes, but not
	 * its elements.
	 * 
	 * @param toCopy
	 *            A non-null Model
	 * @param id
	 *            non-null new id. This id should differ from that of
	 *            <code>toCopy</code>
	 * @throws IllegalArgumentException
	 *             if any argument is null
	 */
	public Model(Model toCopy, String id) {
		this(id, toCopy.getName(), toCopy.getLanguage(), toCopy.getSourcePathOrURIString());
	}

    /**
     * Returns the model's language.
     *
     * @return A <code>String</code>
     */
    public String getLanguage() {
        return this.language;
    }

    /**
     * Sets the model  language that this element refers to.  This should ideally be a URN,
     *     which may already be defined in  {@link SUPPORTED_LANGUAGE}. If no language describes this
     *            model, the argument may be <code>null</code>.
     * @param language A modelling language name.
     * @since 1.2.0
     */
    public void setLanguage(String language) {
        this.language = language;
    }

	/**
	 * Gets a possibly empty but non-null unmodifiable <code>List</code> of
	 * {@link Change} objects.
	 * 
	 * @return List<Change>
	 */
	public List<Change> getListOfChanges() {
		return Collections.unmodifiableList(this.listOfChanges);
	}

	/**
	 * Boolean test for whether this model has any changes to be applied
	 * 
	 * @return <code>true</code> if this model has changes to be applied;
	 *         <code>false</code> otherwise.
	 */
	public boolean hasChanges() {
		return !this.listOfChanges.isEmpty();
	}

	/**
     * Adds a Change to this object's list of Changes, if not already
     * present.
     * @param change
     *            A non-null {@link Change} element to add
     */
	public void addChange(Change change) {
        if (this.listOfChanges.contains(change)) return;
        this.listOfChanges.add(change);
        this.listOfChanges.sort(new Change.ChangeComparator());
    }

	/**
	 * Removes a Change from this object's list of Change objects.
	 * @param change
	 *            A non-null {@link Change} element that to remove.
     *            If the change is not found in the list of changes, the function returns early.
	 */
	public void removeChange(Change change) {
        if (!this.listOfChanges.contains(change)) return;
        this.listOfChanges.remove(change);
        // no need to re-sort on removal
	}

	/**
	 * Returns the model's source, as a URI from where it can retrieved. This
	 * can be be a file location or a stable database identifier, for example.
	 * 
	 * @return A <code>String</code>
	 */
	public String getSourcePathOrURIString() {
		return this.source_path_or_URI_string;
	}

	/**
	 * Gets the model's source description as a {@link URI}. How this object is
	 * interpreted is up to clients, but here are some examples that are likely
	 * to be common:
	 * <ul>
	 * <li><span style=font-weight:bold> source="file.txt" </span> A relative
	 * URI to a model file in the same folder as the SED-ML file.
	 * 
	 * <li><span style=font-weight:bold> source=http://www.cellml.org/models/
	 * leloup_gonze_goldbeter_1999_version02 </span> A URL to a model at a
	 * remote location.
	 * <li><span style=font-weight:bold>
	 * source=urn:miriam:biomodels.db:BIOMD0000000003 </span> A URN to a unique
	 * identifier for a model, which requires further resolution. Calling
	 * isOpaque() on this URI will return <code>true</code>.
	 * </ul>
	 * <p/>
	 * The following table may be helpful in using the java.net.URI API to
	 * resolve these values:
	 * <table border="1" cellpadding="10" >
	 * <tr>
	 * <th>String</th>
	 * <th>Authority</th>
	 * <th>Fragment</th>
	 * <th>Host</th>
	 * <th>Scheme</th>
	 * <th>Path</th>
	 * <th>Query</th>
	 * </tr>
	 * <tr>
	 * <td>/a/b/c.txt</td>
	 * <td>null</td>
	 * <td>null</td>
	 * <td>null</td>
	 * <td><span style=font-weight:bold> file </span></td>
	 * <td><span style=font-weight:bold> /a/b/c.txt </span></td>
	 * <td>null</td>
	 * </tr>
	 * <tr>
	 * <tr>
	 * <td>file.txt</td>
	 * <td>null</td>
	 * <td>null</td>
	 * <td>null</td>
	 * <td>null</td>
	 * <td><span style=font-weight:bold> file.txt </span></td>
	 * <td>null</td>
	 * </tr>
	 * <tr>
	 * <td>urn:miriam:biomodels.db:BIOMD0000000003</td>
	 * <td>null</td>
	 * <td>null</td>
	 * <td>null</td>
	 * <td><span style=font-weight:bold>urn</span></td>
	 * <td>null</td>
	 * <td>null</td>
	 * </tr>
	 * <tr>
	 * <td>http://www.cellml.org/models/leloup_gonze_goldbeter_1999_version02</td>
	 * <td>www.cellml.org</td>
	 * <td>null</td>
	 * <td>null</td>
	 * <td><span style=font-weight:bold>http</span></td>
	 * <td>/models/leloup_gonze_goldbeter_1999_version02</td>
	 * <td>null</td>
	 * </tr>
	 * <tr>
	 * <td>
	 * http://roedy@www.mindprod.com:80/products/abc.html?type=all&colour=brown
	 * #DEF</td>
	 * <td>roedy@www.mindprod.com:80</td>
	 * <td>DEF</td>
	 * <td>roedy</td>
	 * <td><span style=font-weight:bold>http</span></td>
	 * <td>/products/abc.html</td>
	 * <td>type=all&colour=brown</td>
	 * </tr>
	 * </table>
	 * 
	 * @return A {@link URI} object
	 * @throws URISyntaxException
	 *             if the value of the the 'source' attribute of a model element
	 *             cannot be converted to a URI.
	 */
	public URI getSourceURI() throws URISyntaxException {
		return new URI(this.source_path_or_URI_string.replace(" ", SPACE_URI_ESCAPE_SEQUENCE));
	}

	/**
	 * Boolean test for whether the model source is a relative file location.
	 * 
	 * @return <code>true</code> if this URI is a relative file location,
	 *         <code>false</code> if URI is badly-formed or does not fulfill the
	 *         criteria listed in table described in getSourceURI().
	 * @link {@link Model#getSourceURI()};
	 */
	public boolean isSourceURIRelative() {
		URI srcURI;
		try {
			srcURI = this.getSourceURI();
		} catch (URISyntaxException e) {
			return false;
		}
        return srcURI.getAuthority() == null
                && srcURI.getFragment() == null
                && srcURI.getHost() == null
                && srcURI.getQuery() == null
                && srcURI.getScheme() == null
                && srcURI.getPath() != null;

    }

	/**
	 * Boolean test for whether the source attribute for this model is a valid
	 * URI.
	 * 
	 * @return <code>true</code>if a {@link URI} object can be generated from
	 *         this object, <code>false</code> otherwise.
	 */
	public boolean isSourceValidURI() {
		try {
			this.getSourceURI();
		} catch (URISyntaxException e) {
			return false;
		}
		return true;
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() {
        String formatString = "Model [id=%s, name=%s, language=%s, src=%s]";
        return String.format(formatString, this.getId(), this.getName(), this.language, this.source_path_or_URI_string);
	}

	@Override
	public String getElementName() {
		return SEDMLTags.MODEL_TAG;
	}

	public boolean accept(SEDMLVisitor visitor){
        if (!visitor.visit(this)) return false;
        for (Change c: this.getListOfChanges()){
            if(c.accept(visitor)) continue;
            return false;
        }
        return true;
    }
}
