package org.jlibsedml.validation;

import java.util.ArrayList;
import java.util.List;

import org.jlibsedml.SedMLDataContainer;
import org.jlibsedml.SedMLDocument;
import org.jlibsedml.SedMLError;
import org.jlibsedml.XMLException;
/**
 * Class responsible for validating a SED-ML model against its XML schema.
 * @author radams
 *
 */
public class SEDMLSchemaValidator implements ISedMLValidator {

	private final SedMLDataContainer sedml;

	/**
	 * @param sedml A non-null {@link SedMLDataContainer} object.
	 * @throws IllegalArgumentException if <code>sedml</code> is null.
	 */
	public SEDMLSchemaValidator(SedMLDataContainer sedml) {
		super();
		if( sedml ==null){
			throw new IllegalArgumentException();
		}
		this.sedml = sedml;
	}

	/**
	 * @throws XMLException 
	 * @see  ISedMLValidator
	 */
	public List<SedMLError> validate() throws XMLException {
		final List<SedMLError> errors = new ArrayList<SedMLError>();
		String xmlAsString = new SedMLDocument(sedml).writeDocumentToString();
		new SchemaValidatorImpl().validateSedMLString(errors, xmlAsString);
		return errors;
	}   
}
