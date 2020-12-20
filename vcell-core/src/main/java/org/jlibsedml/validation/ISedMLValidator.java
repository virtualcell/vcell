package org.jlibsedml.validation;

import java.util.List;

import org.jlibsedml.SedMLError;
import org.jlibsedml.XMLException;
/**
 * Interface for different sorts of validation that can be performed on a SED-ML model
 * or file.<br>
 * Implementations should not alter or change the model elements in any way.
 * @author radams
 *
 */
public interface ISedMLValidator {

	/**
	 * Performs some validation on a SEDML document, file or object model.
	 * @return A Possibly empty but never null <code>List</code> of {@link SedMLError}
	 *    objects.
	 * @throws XMLException  if validator cannot function due to XML errors.
	 */
	public  List<SedMLError> validate () throws XMLException;
}
