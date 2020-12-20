package org.jlibsedml.validation;

import java.util.ArrayList;
import java.util.List;

import org.jdom.Document;
import org.jlibsedml.SEDMLDocument;
import org.jlibsedml.SedML;
import org.jlibsedml.SedMLError;
import org.jlibsedml.XMLException;

/**
 * Access point to validators. Clients should access validation through
 * {@link SEDMLDocument#validate()}.
 * 
 * @author radams
 *
 */
public class ValidatorController {

    /**
     * 
     * @param sedml
     *            A non-null {@link SedML} object
     * @param doc
     *            An org.doc
     * @return A possibly empty but non-null <code>List</code> of SedML errors.
     * @throws XMLException
     */
    public List<SedMLError> validate(SedML sedml, Document doc)
            throws XMLException {
        List<SedMLError> errors = new ArrayList<SedMLError>();
        // should be first validation
        errors.addAll(new SEDMLSchemaValidator(sedml).validate());
        // if does not validate against schema, no point in continuing -
        // required fields may be null,
        // etc
        if (errors.size() > 0) {
            return errors;
        }
        errors.addAll(new ModelCyclesDetector(sedml, doc).validate());
        errors.addAll(new SchematronValidator(doc, sedml).validate());
        return errors;
    }
}
