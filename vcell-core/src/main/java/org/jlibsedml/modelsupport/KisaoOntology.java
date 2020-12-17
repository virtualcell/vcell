package org.jlibsedml.modelsupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Singleton class which encapsulates the KISAO ontology. This class provides
 * the public method: <br/>
 * 
 * <pre>
 * KisaoTerm getTermById(String id);
 * </pre>
 * 
 * which provides access to a particular KisaoTerm.
 * 
 * @link http://www.ebi.ac.uk/compneur-srv/kisao/
 * @author radams
 */
public class KisaoOntology {

    /**
     * Predefined constant corresponding to KISAO:0000035 describing a generic
     * deterministic simulation.
     */
    public static KisaoTerm ALGORITHM_WITH_DETERMINISTIC_RULES = getInstance()
            .getTermById("KISAO:0000035");
    private List<KisaoTerm> terms = new ArrayList<KisaoTerm>();
    private static KisaoOntology instance;

    /**
     * Gets the singleton instance of this class.
     * 
     * @return A non-null <code>KisaoOntology</code> object.
     */
    public static KisaoOntology getInstance() {
        if (instance == null) {
            instance = new KisaoTermParser().parse();
        }
        return instance;
    }

    void add(KisaoTerm curr) {
        terms.add(curr);
    }

    /**
     * Returns an unordered, unmodifiable list of {@link KisaoTerm}s.
     * 
     * @return a List of <KisaoTerm>
     */
    public List<KisaoTerm> getTerms() {
        return Collections.unmodifiableList(terms);
    }

    /**
     * Finds a {@link KisaoTerm} with the specified Id.
     * 
     * @param id
     *            A KISAO identifier such as 'KISAO:0000003'
     * @return the {@link KisaoTerm} with that id, or null if not found.
     */
    public KisaoTerm getTermById(String id) {
        for (KisaoTerm term : terms) {
            if (term.getId().equalsIgnoreCase(id)) {
                return term;
            }
        }
        return null;
    }

    // creates is-a tree after parsing the obo file.
    void createRelations() {
        for (KisaoTerm term : terms) {
            List<String> isas = term.getIsaRef();
            for (String isa : isas) {
                KisaoTerm termISA = getTermById(isa);
                if (termISA != null) {
                    term.addISA(termISA);
                }
            }
        }
    }
}
