package org.jlibsedml.modelsupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Encapsulates a single KISAO term.
 * 
 * @author radams
 *
 */
public class KisaoTerm {

    private String id, name, def;
    private List<KisaoTerm> isaList = new ArrayList<KisaoTerm>();
    private List<String> exactSynonyms = new ArrayList<String>();
    private List<String> narrowSynonyms = new ArrayList<String>();
    private List<String> isaRef = new ArrayList<String>();

    private boolean isObsolete = false;

    /**
     * Boolean test for whether this term is obsolete or not.
     * 
     * @return <code>true</code> if term is obsolete, <code>false</code>
     *         otherwise.
     */
    public boolean isObsolete() {
        return isObsolete;
    }

    void setObsolete(boolean isObsolete) {
        this.isObsolete = isObsolete;
    }

    void setId(String id) {
        this.id = id;
    }

    void setName(String name) {
        this.name = name;
    }

    void setDef(String def) {
        this.def = def;
    }

    KisaoTerm() {

    }

    @Override
    public String toString() {
        return "KisaoTerm [id=" + id + ", name=" + name + ", def=" + def + "]";
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
        KisaoTerm other = (KisaoTerm) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    /**
     * Getter for the KISAO id.
     * 
     * @return A non-null <code>String</code> in the form 'KISAO:0000003'
     */
    public String getId() {
        return id;
    }

    /**
     * Getter for descriptive name of this term.
     * 
     * @return A non-null <code>String</code>.
     */
    public String getName() {
        return name;
    }

    /**
     * Getter for a paragraph of description about this simulation algorithm.
     * 
     * @return A possibly null <code>String</code>.
     */
    public String getDefinition() {
        return def;
    }

    /**
     * Gets the list of more general terms, of which this term IS-A direct
     * sub-type.
     * 
     * @return A possibly empty but non-null <code>List</code> of terms
     */
    public List<KisaoTerm> getIsa() {
        return Collections.unmodifiableList(isaList);
    }

    /**
     * Gets the list of exact synonyms for this term.
     * 
     * @return A possibly empty but non-null <code>List</code> of strings.
     */
    public List<String> getExactSynonyms() {
        return Collections.unmodifiableList(exactSynonyms);
    }

    /**
     * Gets the list of narrow synonyms for this term.
     * 
     * @return A possibly empty but non-null <code>List</code> of strings.
     */
    public List<String> getNarrowSynonyms() {
        return Collections.unmodifiableList(narrowSynonyms);
    }

    /**
     * Boolean test for whether this term IS-A subtype of the
     * <code>otherTerm</code> argument. If this.equals(otherTerm), this method
     * returns <code>true</code>.<br/>
     * So, if <code>otherTerm</code> is the root term (KISAO:0000000), for any
     * subtype,
     * 
     * <pre>
     *    anyterm.is_a(otherTerm) == true;
     * </pre>
     * 
     * @param otherTerm
     *            The {@link KisaoTerm} under test.
     * @return <code>true</code> if this object IS-A <code>term</code>,
     *         <code>false</code> otherwise.
     */
    public boolean is_a(KisaoTerm otherTerm) {
        if (this.equals(otherTerm)) {
            return true;
        }
        for (KisaoTerm isa : isaList) {
            if (isa.equals(otherTerm)) {
                return true;
            }
        }
        for (KisaoTerm isa : isaList) {
            return isa.is_a(otherTerm);
        }
        return false;

    }

    void addExactSynonym(String synonym) {
        exactSynonyms.add(synonym);
    }

    void addNarrowSynonym(String synonym) {
        narrowSynonyms.add(synonym);
    }

    void addIsaRef(String synonym) {
        isaRef.add(synonym);
    }

    void addISA(KisaoTerm is_a) {
        isaList.add(is_a);
    }

    List<String> getIsaRef() {
        return isaRef;
    }

}
