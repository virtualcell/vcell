package org.jlibsedml;

import java.util.Collections;
import java.util.List;

/**
 * Provides a list of errors produced by validate()
 *  together with a String representation of the SED-ML document formatted
 *   so that error line numbers are correct.
 * @author radams
 *
 */
public class SedMLValidationReport {

	
	SedMLValidationReport(List<SedMLError> errors, String prettyXML) {
		super();
		this.errors = errors;
		this.prettyXML = prettyXML;
	}

	private List<SedMLError> errors;
	
	private String prettyXML;

	/**
	 * Gets an unmodifiable, possibly empty but non-null  <code>List</code> of 
	 *     SedML errors.
	 * @return the error list
	 */
	public List<SedMLError> getErrors() {
		return  Collections.unmodifiableList(errors);
	}

	/**
	 * Returns the SEDML content as a pretty-printed <code>String</code>, one element per line.
	 * @return a <code>String</code> of XML.
	 */
	public String getPrettyXML() {
		return prettyXML;
	}	
}
