package org.jlibsedml.validation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jdom2.Document;
import org.jlibsedml.SedMLDataContainer;
import org.jlibsedml.components.SId;
import org.jlibsedml.components.SedBase;
import org.jlibsedml.components.SedML;
import org.jlibsedml.components.model.Model;
import org.jlibsedml.SedMLTags;
import org.jlibsedml.SedMLError;
import org.jlibsedml.SedMLError.ERROR_SEVERITY;
import org.jlibsedml.XMLException;

/**
 * Detects cycles in the source references of {@link Model} elements. For
 * example, <br/>
 * 
 * <pre>
 * &lt;model id="model2" source="model1" language="sbml"/&gt;
 * &lt;model id="model1" source="model2" language="sbml"/&gt;
 * </pre>
 * 
 * is invalid as both models use each other as source references.
 */
public class ModelCyclesDetector extends AbstractDocumentValidator {
    private final SedMLDataContainer sedml;

    public ModelCyclesDetector(SedMLDataContainer sedml, Document doc) {
        super(doc);
        this.sedml = sedml;
    }

    /**
     * @see ISedMLValidator
     */
    public List<SedMLError> validate() throws XMLException {
        SedML sedML = this.sedml.getSedML();
        List<SedMLError> errs = new ArrayList<>();
        List<Model> models = sedML.getModels();
        for (Model model : models) {
            SedMLError err = this.detectCycles(model);
            if (err == null) continue;
            errs.add(err);
        }
        return errs;
    }

    private SedMLError detectCycles(Model model) {
        Set<SId> ids = new HashSet<>();
        Model currentModel = model;
        while (currentModel != null) {
            if (ids.contains(currentModel.getId())){
                int line = this.getLineNumberOfError(SedMLTags.MODEL_TAG, model);
                String message = "Cycle detected with model: " + currentModel.getIdAsString();
                return new SedMLError(line, message, ERROR_SEVERITY.ERROR);
            }
            ids.add(currentModel.getId());
            currentModel = this.sedml.getSubModel(model.getId());
        }
        return null;
    }
}
