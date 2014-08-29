/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.field;

import org.vcell.util.Matchable;
import org.vcell.util.document.ExternalDataIdentifier;

import cbit.vcell.parser.ExpressionException;

/**
 * Insert the type's description here.
 * Creation date: (9/18/2006 12:55:46 PM)
 * @author: Jim Schaff
 */
public class FieldDataIdentifierSpec implements java.io.Serializable, Matchable  {
	private FieldFunctionArguments fieldFuncArgs;
	private ExternalDataIdentifier extDataID;

/**
 * FieldDataIdentifier constructor comment.
 */
public FieldDataIdentifierSpec(FieldFunctionArguments argFieldFuncArgs,ExternalDataIdentifier argExtDataID) {
	super();
	fieldFuncArgs = argFieldFuncArgs;
	extDataID = argExtDataID;
}

public ExternalDataIdentifier getExternalDataIdentifier(){
	return extDataID;
}

public String toString(){
	return "[FFA=" + fieldFuncArgs.toCSVString()+", EDI="+extDataID.toCSVString() + "]";

}

/**
 * Insert the method's description here.
 * Creation date: (9/21/2006 11:18:33 AM)
 * @return java.lang.String
 */
public static FieldDataIdentifierSpec fromCSVString(String inputString) throws ExpressionException{
	
	java.util.StringTokenizer st = new java.util.StringTokenizer(inputString, ",");
	return
		new FieldDataIdentifierSpec(
				FieldFunctionArguments.fromTokens(st),
				ExternalDataIdentifier.fromTokens(st)
				);
}


/**
 * Insert the method's description here.
 * Creation date: (9/21/2006 11:18:33 AM)
 * @return java.lang.String
 */
public String toCSVString() {
	return fieldFuncArgs.toCSVString()+","+extDataID.toCSVString();
}

public FieldFunctionArguments getFieldFuncArgs() {
	return fieldFuncArgs;
}

@Override
public boolean equals(Object obj) {
	if(!(obj instanceof FieldDataIdentifierSpec)){
		return false;
	}
	FieldDataIdentifierSpec fdiSpec = (FieldDataIdentifierSpec)obj;
	return
		getFieldFuncArgs().equals(fdiSpec.getFieldFuncArgs())
		&&
		getExternalDataIdentifier().equals(fdiSpec.getExternalDataIdentifier());
}

public boolean compareEqual(Matchable obj) {
	if (obj instanceof FieldDataIdentifierSpec){
		FieldDataIdentifierSpec other = (FieldDataIdentifierSpec)obj;
		if (other.toCSVString().equals(toCSVString())){
			return true;
		}
	}
	return false;
}
}
