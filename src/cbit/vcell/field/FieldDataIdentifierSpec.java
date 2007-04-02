package cbit.vcell.field;

import cbit.util.TokenMangler;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.server.VCDataIdentifier;
import cbit.vcell.simdata.ExternalDataIdentifier;

/**
 * Insert the type's description here.
 * Creation date: (9/18/2006 12:55:46 PM)
 * @author: Jim Schaff
 */
public class FieldDataIdentifierSpec implements java.io.Serializable  {
	private FieldFunctionArguments fieldFuncArgs;
//	private java.lang.String fieldName = null;
//	private java.lang.String variableName = null;
//	private Expression time = null;
	private ExternalDataIdentifier vcDataID;


/**
 * FieldDataIdentifier constructor comment.
 */
public FieldDataIdentifierSpec(FieldFunctionArguments argFieldFuncArgs,VCDataIdentifier argVCDataID) {
	super();
	fieldFuncArgs = argFieldFuncArgs;
	vcDataID = (ExternalDataIdentifier)argVCDataID;
}

public VCDataIdentifier getVCDataIdentifier(){
	return vcDataID;
}

public String toString(){
	return "[FFA=" + fieldFuncArgs.toCSVString()+", EDI="+vcDataID.toCSVString() + "]";

}


/**
 * Insert the method's description here.
 * Creation date: (9/21/2006 2:51:03 PM)
 * @return java.lang.String
 */
public String getDefaultFieldDataFileNameForSimulation() {
	return getDefaultFieldDataFileNameForSimulation(fieldFuncArgs);
}



/**
 * Insert the method's description here.
 * Creation date: (9/21/2006 2:51:03 PM)
 * @return java.lang.String
 */
public String getGlobalVariableName_C() {
	return getGlobalVariableName_C(fieldFuncArgs.getFieldName(), fieldFuncArgs.getVariableName(), fieldFuncArgs.getTime());
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
	return fieldFuncArgs.toCSVString()+","+vcDataID.toCSVString();
}


/**
 * Insert the method's description here.
 * Creation date: (9/21/2006 2:51:03 PM)
 * @return java.lang.String
 */
public static String getGlobalVariableName_C(FieldFunctionArguments fieldFuncArgs){
	return getGlobalVariableName_C(fieldFuncArgs.getFieldName(), fieldFuncArgs.getVariableName(), fieldFuncArgs.getTime());
}


/**
 * Insert the method's description here.
 * Creation date: (9/21/2006 2:51:03 PM)
 * @return java.lang.String
 */
private static String getGlobalVariableName_C(String fieldname, String varname, Expression timeExp) {
	return TokenMangler.fixTokenStrict("field_" + fieldname + "_" + varname + "_" + timeExp.infix());
}


/**
 * Insert the method's description here.
 * Creation date: (9/21/2006 2:51:03 PM)
 * @return java.lang.String
 */
public static String getLocalVariableName_C(FieldFunctionArguments fieldFuncArgs) {
	return TokenMangler.fixTokenStrict(fieldFuncArgs.getFieldName() + "_" + fieldFuncArgs.getVariableName()+"_"+fieldFuncArgs.getTime().infix());
}

public FieldFunctionArguments getFieldFuncArgs() {
	return fieldFuncArgs;
}

/**
 * Insert the method's description here.
 * Creation date: (9/21/2006 2:51:03 PM)
 * @return java.lang.String
 */
public static String getDefaultFieldDataFileNameForSimulation(FieldFunctionArguments ffa) {
	return getLocalVariableName_C(ffa) + ".fdat";
}
}