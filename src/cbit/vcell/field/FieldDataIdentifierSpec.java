package cbit.vcell.field;

import cbit.vcell.parser.ExpressionException;
import cbit.vcell.simdata.ExternalDataIdentifier;

/**
 * Insert the type's description here.
 * Creation date: (9/18/2006 12:55:46 PM)
 * @author: Jim Schaff
 */
public class FieldDataIdentifierSpec implements java.io.Serializable  {
	private FieldFunctionArguments fieldFuncArgs;
	private ExternalDataIdentifier extDataID;
	private static String FIELDDATA_FILEEXT = ".fdat";

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

/**
 * Insert the method's description here.
 * Creation date: (9/21/2006 2:51:03 PM)
 * @return java.lang.String
 */
public static String getDefaultFieldDataFileNameForSimulation(FieldFunctionArguments fieldFuncArgs) {
	return fieldFuncArgs.getUniqueID() + FIELDDATA_FILEEXT;
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
}