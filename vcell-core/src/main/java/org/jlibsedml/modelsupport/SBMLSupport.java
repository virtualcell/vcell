package org.jlibsedml.modelsupport;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jlibsedml.SEDMLDocument;
import org.jlibsedml.execution.IXPathToVariableIDResolver;

/**
 * <p>
 * Class to provide general support for working with SBML level 2 and level 3
 * core models, especially for the construction of XPath strings for use in
 * Change and DataGenerator elements. Currently this class provides support for
 * identifying global parameters, species and compartments, and some mutable
 * attributes.
 * </p>
 * <p>
 * This class does not apply XPath changes, that is achieved through
 * {@link SEDMLDocument}:<br/>
 * <code> String getChangedModel(String model_ID, final String originalModel);</code>
 * 
 * @author radams
 *
 */
public class SBMLSupport implements IXPathToVariableIDResolver {

    public static final Pattern XPATH_ID_RESOLVER = Pattern
            .compile("@\\S+='(\\S+)'");

    public enum CompartmentAttribute {
        size, spatialDimensions;
    }

    public enum ParameterAttribute {
        value;

    }

    public enum SpeciesAttribute {
        initialAmount, initialConcentration, constant, substanceUnits, hasOnlySubstanceUnits;
    }

    public enum ReactionAttribute {
        reversible, fast;
    }

    /**
     * Returns a {@link SUPPORTED_LANGUAGE} constant corresponding to a
     * level/version combination supplied as arguments.
     * 
     * @param level
     *            An SBML level
     * @param version
     *            An SBML version
     * @return The {@link SUPPORTED_LANGUAGE} constant referring to the supplied
     *         values. If the level and version do not correpsond to a correct
     *         SBML Version,SUPPORTED_LANGUAGE.SBML_GENERIC will be returned.
     */
    public SUPPORTED_LANGUAGE getLanguageFor(int level, int version) {
        switch (level) {
        case 1:
            switch (version) {
            case 1:
                return SUPPORTED_LANGUAGE.SBML_L1V1;

            case 2:
                return SUPPORTED_LANGUAGE.SBML_L1V2;

            default:
                return SUPPORTED_LANGUAGE.SBML_GENERIC;
            }
        case 2:
            switch (version) {
            case 1:
                return SUPPORTED_LANGUAGE.SBML_L2V1;

            case 2:
                return SUPPORTED_LANGUAGE.SBML_L2V2;
            case 3:
                return SUPPORTED_LANGUAGE.SBML_L2V3Rel1;
            case 4:
                return SUPPORTED_LANGUAGE.SBML_L2V4;

            default:
                return SUPPORTED_LANGUAGE.SBML_GENERIC;
            }

        case 3:
            switch (version) {
            case 1:
                return SUPPORTED_LANGUAGE.SBML_L3V1;
            default:
                return SUPPORTED_LANGUAGE.SBML_GENERIC;
            }

        default:
            return SUPPORTED_LANGUAGE.SBML_GENERIC;
        }
    }

    /**
     * For an Xpath argument string which identifies an element with an 'id'
     * attribute, this method returns the id for that element. For the case
     * where an XPath expression contains multiple @id attributes, the value for
     * the last attribute in the expression is returned: For example, <br/>
     * 
     * <pre>
     * String xpath = &quot;/sbml:sbml/sbml:model/sbml:listOfSpecies/sbml:species[@id='X']&quot;;
     * String id = support.getIdFromXPathIdentifer(xpath);
     * // id == &quot;X&quot;.
     * </pre>
     * 
     * @param Xpath
     *            A non-null XPath
     * @return The identifier referred to by the XPath, or <code>null</code> if
     *         not found. If the Xpath does not contain an [@id='myid'] element,
     *         this method will also return <code>null</code>.
     */
    public String getIdFromXPathIdentifer(String Xpath) {
        Matcher m = XPATH_ID_RESOLVER.matcher(Xpath);
        if (m.find()) {
            return m.group(1);
        }
        return null;
    }

    /**
     * Gets XPath expression to identify a global SBML parameter
     * 
     * @param paramID
     *            The parameter's Id.
     * @return String An XPath string which can be set into the 'target' field
     *         of an XPath-containing element in SED-ML.
     */
    public String getXPathForGlobalParameter(String paramID) {
        return getXPathForListOfParameters() + "/sbml:parameter[@id='"
                + paramID + "']";
    }

    /**
     * Gets XPath expression to identify an attribute of a global SBML parameter
     * 
     * @param paramID
     *            The parameter's Id.
     * @param paramAttributeName
     *            A {@link ParameterAttribute} constant identifying an attribute
     *            name
     * @return An XPath string pointing to the parameter's attribute which can
     *         be set into the 'target' field of an XPath-containing element in
     *         SED-ML.
     */
    public String getXPathForGlobalParameter(String paramID,
            ParameterAttribute paramAttributeName) {
        return getXPathForListOfParameters() + "/sbml:parameter[@id='"
                + paramID + "']" + "/@" + paramAttributeName;
    }

    /**
     * Gets XPath expression to identify a global SBML species.
     * 
     * @param speciesID
     *            The species Id.
     * @return An XPath string which can be set into the 'target' field of an
     *         XPath-containing element in SED-ML.
     */
    public String getXPathForSpecies(String speciesID) {
        return getXPathForListOfSpecies() + "/sbml:species[@id='" + speciesID
                + "']";
    }

    /**
     * Gets XPath expression to identify an attribute of an SBML species.
     * 
     * @param speciesID
     *            The species Id.
     * @param speciesAttributeName
     *            A {@link SpeciesAttribute} identifying an attribute of the
     *            species.
     * @return An XPath string which can be set into the 'target' field of an
     *         XPath-containing element in SED-ML.
     */
    public String getXPathForSpecies(String speciesID,
            SpeciesAttribute speciesAttributeName) {
        return getXPathForListOfSpecies() + "/sbml:species[@id='" + speciesID
                + "']" + "/@" + speciesAttributeName;
    }

    /**
     * Gets XPath expression to identify an SBML compartment.
     * 
     * @param compartmentID
     *            The compartment Id.
     * @return An XPath string which can be set into the 'target' field of an
     *         XPath-containing element in SED-ML.
     */
    public String getXPathForCompartment(String compartmentID) {
        return getXPathForListOfCompartments() + "/sbml:compartment[@id='"
                + compartmentID + "']";
    }

    /**
     * Gets XPath expression to identify an element of an SBML compartment
     * attribute using an identifier. E.g.,
     * 
     * <pre>
     * getXPathForCompartment("nucleus", {@link CompartmentAttribute#size});
     * </pre>
     * 
     * returns
     * 
     * <pre>
     * 	  /sbml:sbml/sbml:model/sbml:listOfCompartments/compartment[@id='nucleus']/@size
     * </pre>
     * 
     * @param compartmentID
     *            The compartment Id.
     * @param compartmentAttribute
     *            A {@link CompartmentAttribute} identifying an attribute of the
     *            compartment.
     * @return An XPath string which can be set into the 'target' field of an
     *         XPath-containing element in SED-ML.
     */
    public String getXPathForCompartment(String compartmentID,
            CompartmentAttribute compartmentAttribute) {
        return getXPathForListOfCompartments() + "/sbml:compartment[@id='"
                + compartmentID + "']" + "/@" + compartmentAttribute;
    }

    String getXPathForListOfCompartments() {
        return "/sbml:sbml/sbml:model/sbml:listOfCompartments";
    }

    String getXPathForListOfParameters() {
        return "/sbml:sbml/sbml:model/sbml:listOfParameters";
    }

    String getXPathForListOfSpecies() {
        return "/sbml:sbml/sbml:model/sbml:listOfSpecies";
    }

    String getXPathForListOfReactions() {
        return "/sbml:sbml/sbml:model/sbml:listOfReactions";
    }

    String getXPathForListOfKineticLawParameters(String reactionID) {
        return getXPathForReaction(reactionID)
                + "/sbml:kineticLaw/sbml:listOfParameters";
    }

    /**
     * Gets XPath expression to identify a global SBML reaction.
     * 
     * @param reactionID
     *            The reaction Id.
     * @return An XPath string which can be set into the 'target' field of an
     *         XPath-containing element in SED-ML.
     */
    public String getXPathForReaction(String reactionID) {
        return getXPathForListOfReactions() + "/sbml:reaction[@id='"
                + reactionID + "']";
    }

    /**
     * Gets XPath expression to identify an attribute of an SBML reaction.
     * 
     * @param reactionID
     *            The reaction Id.
     * @param reactionAttributeName
     *            A {@link ReactionAttribute} identifying an attribute of the
     *            reaction.
     * @return An XPath string which can be set into the 'target' field of an
     *         XPath-containing element in SED-ML.
     */
    public String getXPathForReaction(String reactionID,
            ReactionAttribute reactionAttributeName) {
        return getXPathForListOfReactions() + "/sbml:reaction[@id='"
                + reactionID + "']" + "/@" + reactionAttributeName;
    }

    /**
     * Gets XPath expression to identify a local reaction kineticLaw parameter.
     * 
     * @param reactionID
     *            The reaction Id.
     * @param parameterID
     *            The localParameter Id.
     * @return An XPath string which can be set into the 'target' field of an
     *         XPath-containing element in SED-ML.
     */
    public String getXPathForKineticLawParameter(String reactionID,
            String parameterID) {
        return getXPathForListOfKineticLawParameters(reactionID)
                + "/sbml:parameter[@id='" + parameterID + "']";
    }

    /**
     * Gets XPath expression to identify an attribute of a local reaction
     * kineticLaw parameter.
     * 
     * @param reactionID
     *            The reaction Id.
     * @param parameterID
     *            The localParameter Id.
     * @param parameterAttributeName
     *            A {@link ParameterAttribute} identifying an attribute of the
     *            reaction.
     * @return An XPath string which can be set into the 'target' field of an
     *         XPath-containing element in SED-ML.
     */
    public String getXPathForKineticLawParameter(String reactionID,
            String parameterID, ParameterAttribute parameterAttributeName) {
        return getXPathForKineticLawParameter(reactionID, parameterID) + "/@"
                + parameterAttributeName;
    }

}
