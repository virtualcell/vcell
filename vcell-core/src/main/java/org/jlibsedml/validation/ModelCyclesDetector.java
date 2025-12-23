package org.jlibsedml.validation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jdom2.Document;
import org.jlibsedml.components.model.Model;
import org.jlibsedml.SEDMLTags;
import org.jlibsedml.SedMLDataClass;
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
    private SedMLDataClass sedml;

    public ModelCyclesDetector(SedMLDataClass sedml, Document doc) {
        super(doc);
        this.sedml = sedml;
    }

    /**
     * @see ISedMLValidator
     */
    public List<SedMLError> validate() throws XMLException {
        List<SedMLError> errs = new ArrayList<SedMLError>();
        List<Model> models = sedml.getModels();
        for (Model model : models) {
            String src = model.getSourcePathOrURIString();
            String id = model.getId();
            Set<String> ids = new HashSet<String>();
            ids.add(id);
            while (sedml.getModelWithId(src) != null) {
                String newID = sedml.getModelWithId(src).getId();
                if (ids.contains(newID)) {
                    int line = getLineNumberOfError(SEDMLTags.MODEL_TAG, model);
                    errs.add(new SedMLError(line,
                            "Cycles detected in source references for model "
                                    + newID + " and "
                                    + sedml.getModelWithId(newID).getSourcePathOrURIString(),
                            ERROR_SEVERITY.ERROR));
                    return errs;
                } else {
                    ids.add(newID);
                    src = sedml.getModelWithId(src).getSourcePathOrURIString();
                }
            }
        }
        return errs;
    }
}
