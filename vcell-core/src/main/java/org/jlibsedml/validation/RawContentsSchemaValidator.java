package org.jlibsedml.validation;

import java.util.ArrayList;
import java.util.List;

import org.jlibsedml.SedMLError;
import org.jlibsedml.XMLException;


/**
 * Class responsible for validating a SED-ML file against its XML schema.
 * @author radams
 *
 */
public class RawContentsSchemaValidator implements ISedMLValidator {

   public  RawContentsSchemaValidator(String rawSEDML) {
        super();
        this.rawSEDML = rawSEDML;
    }
    private String rawSEDML;
    public List<SedMLError> validate() throws XMLException {
        List<SedMLError> errs = new ArrayList<SedMLError>();
       new SchemaValidatorImpl().validateSedMLString(errs, rawSEDML);
       return errs;
    }
}
