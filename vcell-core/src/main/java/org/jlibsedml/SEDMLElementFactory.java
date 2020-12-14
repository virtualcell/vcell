package org.jlibsedml;

/*
 * Factory for creation of SEDML elements. Since constructors are already public
 *  for SED-ML elements, this class currently just holds information on a strictness
 *   policy for object creation, to allow reading in of SEDML files with possible errors
 *   into the object model so they can be validated, by suppressing IllegalArgumentExceptions
 *   thrown through public API.
 */
class SEDMLElementFactory {

	private static SEDMLElementFactory instance;
	private boolean isStrictCreation=true;

	boolean isStrictCreation() {
		return isStrictCreation;
	}

	void setStrictCreation(boolean isStrictCreation) {
		this.isStrictCreation = isStrictCreation;
	}

	static SEDMLElementFactory getInstance() {
		if(instance == null){
			instance=new SEDMLElementFactory();
		}
		return instance;
	}
	
	private SEDMLElementFactory(){}
	
	
	
}
