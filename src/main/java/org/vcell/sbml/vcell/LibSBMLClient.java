package org.vcell.sbml.vcell;

import org.vcell.sbml.LibSBMLConstantsAdapter;
import org.vcell.sbml.SbmlException;

/**
 * common base for libsbml using classes
 * @author GWeatherby
 *
 */
public class LibSBMLClient {

	/**
	 * throw exception if invalid return code
	 * @param code
	 * @throws SbmlException 
	 */
	protected static void validate(int code) throws SbmlException {
		LibSBMLConstantsAdapter.throwIfError(code);
	}
	
	/**
	 * check return code
	 * @param code
	 * @return true if valid operation
	 */
	protected static boolean valid(int code) {
		return LibSBMLConstantsAdapter.validReturn(code);
	}
}
