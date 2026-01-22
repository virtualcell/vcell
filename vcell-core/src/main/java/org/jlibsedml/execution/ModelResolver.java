package org.jlibsedml.execution;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jlibsedml.SedMLDataContainer;
import org.jlibsedml.components.SId;
import org.jlibsedml.components.model.Model;
import org.jlibsedml.SedMLDocument;


/**
 * Utility class to obtain models from SEDML Model source attributes
 * 
 * @author radams
 *
 */
public class ModelResolver {
    private final static Logger logger = LogManager.getLogger(ModelResolver.class);

    public ModelResolver(SedMLDataContainer sedml) {
        super();
        this.sedml = sedml;
    }

    static final String BASE_MODEL_CANNOT_BE_FOUND = "Provided model `%s` could not resolve to a \"base\" model.";
    static final String SUB_MODEL_CANNOT_BE_FOUND = "Could not resolve provided model `%s`to its \"sub\" model.";
    static final String MODEL_CANNOT_BE_RESOLVED_MSG = " The model could not be resolved from its source reference. ";
    static final String MODEL_SRC_NOT_VALID_URI = "The model 'source' attribute  is not a valid URI.";

    private final SedMLDataContainer sedml;
	private String message = "";
    private final List<IModelResolver> resolvers = new ArrayList<>();
    private final Map<SId, String> modelCache = new HashMap<>();

    /**
     * Adds an {@link IModelResolver} to the list of those to be used to obtain
     * a model.
     * 
     * @param modelResolver
     */
    public void add(IModelResolver modelResolver) {
        this.resolvers.add(modelResolver);

    }

    String getMessage() {
        return this.message;
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
    public String getReferenceModelAsXMLString(Model m) {
        this.sedml.getBaseModel(m.getId());
        String baseModelAsStr;
        try {
            baseModelAsStr = this.resolveURIToModelXMLAsString(m.getSourceURI());
            if (baseModelAsStr == null) throw new URISyntaxException(m.getSourceAsString(), "Unable to determine base model");
        } catch (URISyntaxException e){
            this.message = MODEL_CANNOT_BE_RESOLVED_MSG + "(Model queried: " + m + ")";
            return null;
        }
        return baseModelAsStr;
    }

    /**
     * The main public method of this class.
     * 
     * @param startingModel
     *            A SED-ML model element whose model we want to resolve
     * @return The model, with changes applied, or <code>null</code> if the
     *         model could not be found.
     */
    public String getXMLStringRepresentationOfModel(Model startingModel) {
        // We need to do this level by level
        SId modelID = startingModel.getId();
        // First, did we already do this?
        if (this.modelCache.containsKey(modelID)) return this.modelCache.get(modelID);

        String resolvedModelStr;
        // Okay, is this a base model? If so, we can just process it right away
        if (!startingModel.getSourceAsString().startsWith("#")){
            resolvedModelStr = this.attemptToResolveModel(startingModel);
        } else {
            // Not a base model? Then we need to go one deeper!
            Model subModel = this.sedml.getSubModel(modelID);
            if (subModel == null) {
                this.message = String.format(SUB_MODEL_CANNOT_BE_FOUND, modelID.string());
                logger.error(this.message);
                return null;
            }
            resolvedModelStr = this.getXMLStringRepresentationOfModel(subModel);
        }
        String changedModel = this.applyModelChanges(modelID, resolvedModelStr);
        this.modelCache.put(modelID, changedModel);
        return changedModel;
    }

    /**
     * Order of model refs must be from base to top!
     * @param modelRefs
     * @param unChangedModelAsXMLStr
     * @return
     */
    String applyModelChanges(List<SId> modelRefs, String unChangedModelAsXMLStr) {
        for (SId modelRef : modelRefs) {
            unChangedModelAsXMLStr = this.applyModelChanges(modelRef, unChangedModelAsXMLStr);
        }
        return unChangedModelAsXMLStr;
    }

    String applyModelChanges(SId modelRef, String unChangedModelAsXMLStr) {
        try {
            return SedMLDocument.getChangedModel(this.sedml, modelRef, unChangedModelAsXMLStr);
        } catch (Exception e) {
            String message = "Could not apply XPath changes for model id[" + modelRef + "]";
            logger.error( message, e);
            throw new RuntimeException(message, e);
        }
    }

    /**
     * @param modelSrc
     *            A non-null URI object
     * @return A <code>String</code> of the model XML, or <code>null</code> if
     *         the model could not be found.
     */
    final String resolveURIToModelXMLAsString(URI modelSrc) {
        for (IModelResolver resolver : this.resolvers) {
            String modelAsXML = resolver.getModelXMLFor(modelSrc);
            if (modelAsXML == null) continue;
            return modelAsXML;
        }
        return null;
    }

    private String attemptToResolveModel(Model m) {
        URI source, relativeSource;
        try {
            source = m.getSourceURI();
        } catch (URISyntaxException ignored) {
            this.message = MODEL_SRC_NOT_VALID_URI;
            return null;
        }

        String baseModelAsStr = this.resolveURIToModelXMLAsString(source);
        if (baseModelAsStr != null) return baseModelAsStr;
        // try again with relative path to sedml
        try {
            relativeSource = new URI(this.sedml.getPathForURI() + source);
        } catch (URISyntaxException e) {
            this.message = MODEL_SRC_NOT_VALID_URI;
            return null;
        }
        baseModelAsStr = this.resolveURIToModelXMLAsString(relativeSource);
        if (baseModelAsStr != null) return baseModelAsStr;
        this.message = MODEL_CANNOT_BE_RESOLVED_MSG + "(Using uri: " + source + ")";
        return null;
    }
}
