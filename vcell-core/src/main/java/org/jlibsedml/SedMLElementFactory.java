package org.jlibsedml;

/*
 * Factory for creation of SEDML elements. Since constructors are already public
 *  for SED-ML elements, this class currently just holds information on a strictness
 *   policy for object creation, to allow reading in of SEDML files with possible errors
 *   into the object model so they can be validated, by suppressing IllegalArgumentExceptions
 *   thrown through public API.
 */
public class SedMLElementFactory {

	private static SedMLElementFactory instance;
	private boolean isStrictCreation=true;

	public boolean isStrictCreation() {
		return this.isStrictCreation;
	}

	public void setStrictCreation(boolean isStrictCreation) {
		this.isStrictCreation = isStrictCreation;
	}

	public static SedMLElementFactory getInstance() {
		if(instance == null) instance = new SedMLElementFactory();
		return instance;
	}
	
	private SedMLElementFactory(){}
	
	
	
}
