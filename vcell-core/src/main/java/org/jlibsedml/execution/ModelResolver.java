package org.jlibsedml.execution;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jlibsedml.Model;
import org.jlibsedml.SEDMLDocument;
import org.jlibsedml.SedML;

/**
 * Utility class to obtain models from SEDML Model source attributes
 * 
 * @author radams
 *
 */
public class ModelResolver {
    public ModelResolver(SedML sedml) {
        super();
        this.sedml = sedml;
    }

    static final String MODEL_CANNOT_BE_RESOLVED_MSG = " The model could not be resolved from its source reference. ";
    static final String MODEL_SRC_NOT_VALID_URI = "The model 'source' attribute  is not a valid URI.";

    private SedML sedml;
	private String message = "";
    private List<IModelResolver> resolvers = new ArrayList<IModelResolver>();

    /**
     * Adds an {@link IModelResolver} to the list of those to be used to obtain
     * a model.
     * 
     * @param modelResolver
     */
    public void add(IModelResolver modelResolver) {
        resolvers.add(modelResolver);

    }

    String getMessage() {
        return message;
    }

    /**
     * For a given model, obtains the reference model, before any changes
     * defined in this model are applied. <br/>
     * If this model <b>is</b> a reference model, then this method behaves
     * exactly the same as for <br/>
     * 
     * <pre>
     * public String getModelString (Model m)
     * </pre>
     * 
     * @return A <code> String</code> of the model or <code>null</code> if no
     *         model could be resolved
     */
    public String getReferenceModelString(Model m) {
        List<String> modelRefs = new ArrayList<String>();

        String baseModelRef = getBaseModelRef(m, modelRefs);
        if (baseModelRef == null) {
            return null;
        }
        // resolve base model if we've not already done so
        URI srcURI = null;
        try {
            srcURI = sedml.getModelWithId(baseModelRef).getSourceURI();
        } catch (URISyntaxException e) {

            message = MODEL_SRC_NOT_VALID_URI;
            return null;
        }

        String baseModelAsStr = getBaseModel(srcURI);
        if (baseModelAsStr == null) {
            message = MODEL_CANNOT_BE_RESOLVED_MSG + "(Using uri: " + srcURI
                    + ")";
            return null;
        }
        return baseModelAsStr;
    }

    String getBaseModelRef(Model m, List<String> modelRefs) {
        getModelModificationTree(m, modelRefs);
        if (modelRefs.isEmpty()) {
            message = MODEL_CANNOT_BE_RESOLVED_MSG;
            return null;
        }
        String baseModelRef = modelRefs.get(0);
        return baseModelRef;
    }

    /**
     * The main public method of this class.
     * 
     * @param m
     *            A SED-ML model element whose model we want to resolve
     * @return The model, with changes applied, or <code>null</code> if the
     *         model could not be found.
     */
    public String getModelString(Model m) {
        List<String> modelRefs = new ArrayList<String>();
        Map<String, String> modelID2ModelStr = new HashMap<String, String>();

        String baseModelRef = getBaseModelRef(m, modelRefs);
        // resolve base model if we've not already done so
        URI srcURI = null;
        try {
            srcURI = sedml.getModelWithId(baseModelRef).getSourceURI();
        } catch (URISyntaxException e) {

            message = MODEL_SRC_NOT_VALID_URI;
            return null;
        }
        if (!modelID2ModelStr.containsKey(baseModelRef)) {

            String baseModelAsStr = getBaseModel(srcURI);
            if (baseModelAsStr == null) {
            	// try again with relative path to sedml
            	try {
					srcURI = new URI(sedml.getPathForURI() + srcURI.toString());
				} catch (URISyntaxException e) {
		            message = MODEL_SRC_NOT_VALID_URI;
		            return null;
				}
            	baseModelAsStr = getBaseModel(srcURI);
	            if (baseModelAsStr == null) {
	                message = MODEL_CANNOT_BE_RESOLVED_MSG + "(Using uri: "
	                        + srcURI + ")";
	                return null;
	            }
	        }
            modelID2ModelStr.put(srcURI.toString(), baseModelAsStr);
        }

        // now apply model changes sequentially
        String changedModel = applyModelChanges(modelRefs,
                modelID2ModelStr.get(srcURI.toString()));
        return changedModel;

    }

    String applyModelChanges(List<String> modelRefs, String baseModelAsStr) {
        for (int i = 0; i < modelRefs.size(); i++) {
            try {
                baseModelAsStr = new SEDMLDocument(sedml).getChangedModel(
                        modelRefs.get(i), baseModelAsStr);
            } catch (Exception e) {
                message = "Could not apply XPath changes for model id["
                        + modelRefs.get(i) + "]";
            }
        }
        return baseModelAsStr;
    }

    /**
     * @param modelSrc
     *            A non-null URI object
     * @return A <code>String</code> of the model XML, or <code>null</code> if
     *         the model could not be found.
     */
    final String getBaseModel(URI modelSrc) {
        for (IModelResolver resolver : resolvers) {
            String modelAsXML = resolver.getModelXMLFor(modelSrc);
            if (modelAsXML != null) {
                return modelAsXML;
            }
        }
        return null;
    }

    void getModelModificationTree(Model m, List<String> modelRefs2) {
        String modelSrcRef = m.getSource();

        modelRefs2.add(m.getId());
        if (sedml.getModelWithId(modelSrcRef) != null) {
            getModelModificationTree(sedml.getModelWithId(modelSrcRef),
                    modelRefs2);
        } else {
            Collections.reverse(modelRefs2);
        }
    }
}
