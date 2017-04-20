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

import java.io.Serializable;
import java.util.StringTokenizer;

import org.vcell.util.TokenMangler;

import cbit.vcell.math.FieldFunctionDefinition;
import cbit.vcell.math.VariableType;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.FunctionInvocation;

public class FieldFunctionArguments implements Serializable {

	private String fieldName;
	private String variableName;
	private Expression time;
	private VariableType variableType;
	
	public FieldFunctionArguments(String fieldName, String variableName, Expression time, VariableType vt) {
		super();
		this.fieldName = fieldName;
		this.variableName = variableName;
		this.time = time;
		this.variableType = vt;
	}
	
	public FieldFunctionArguments(FunctionInvocation functionInvocation){
		if (!functionInvocation.getFunctionName().equals(FieldFunctionDefinition.FUNCTION_name)) {
			throw new IllegalArgumentException("'"+functionInvocation.getFunctionName()+"' is not a FieldData function invocation");
		}
		Expression[] arguments = functionInvocation.getArguments();
		if (arguments==null || arguments.length<3 || arguments.length>4){
			throw new RuntimeException("field function invalid, expecting field(fieldname,varname,time,variabletype) where variabletype is optional");
		}
		this.fieldName = getSymbol(arguments[0]);
		this.variableName = getSymbol(arguments[1]);
		this.time = new Expression(arguments[2]);
		if (arguments.length==4){
			this.variableType = VariableType.getVariableTypeFromVariableTypeName(getSymbol(arguments[3]));
		}else{
			this.variableType = VariableType.UNKNOWN;
		}
	}
	

	private String getSymbol(Expression exp){
		if (exp.isIdentifier()){
			return exp.infix();
		}
		if (exp.isLiteral()){
			return exp.infix().replace("'","");
		}
		throw new IllegalArgumentException("field function argument '"+exp.infix()+"' is unexpected");
	}

	public static FieldFunctionArguments fromTokens(StringTokenizer st) throws ExpressionException{
		return
			new FieldFunctionArguments(
					st.nextToken(),
					st.nextToken(),
					new Expression(st.nextToken()),
					VariableType.getVariableTypeFromVariableTypeName(st.nextToken()));
	}
	
	public String infix(){
		return FieldFunctionDefinition.FUNCTION_name+"('"+fieldName+"','"+variableName+"',"+time.infix()+",'"+variableType.getTypeName()+"')";
	}
	
	public String toCSVString(){
		return fieldName+","+variableName+","+time.infix()+","+variableType.getTypeName();
	}
	
	public String getFieldName() {
		return fieldName;
	}
	public Expression getTime() {
		return time;
	}
	public String getVariableName() {
		return variableName;
	}
	public static String getUniqueID(String fieldname, String varname, Expression timeExp, String varType) {
		return TokenMangler.fixTokenStrict(fieldname + "_" + varname + "_" + timeExp.infix() + "_" + varType);
	}

	public String getUniqueID() {
		return getUniqueID(fieldName, variableName, time, variableType.getTypeName());
	}	
	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((fieldName == null) ? 0 : fieldName.hashCode());
		result = PRIME * result + ((time == null) ? 0 : time.hashCode());
		result = PRIME * result + ((variableName == null) ? 0 : variableName.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final FieldFunctionArguments other = (FieldFunctionArguments) obj;
		if (fieldName == null) {
			if (other.fieldName != null)
				return false;
		} else if (!fieldName.equals(other.fieldName))
			return false;
		if (time == null) {
			if (other.time != null)
				return false;
		} else if (!time.equals(other.time))
			return false;
		if (variableName == null) {
			if (other.variableName != null)
				return false;
		} else if (!variableName.equals(other.variableName))
			return false;
		if (variableType == null) {
			if (other.variableType != null)
				return false;
		} else if (!variableType.equals(other.variableType))
			return false;
		return true;
	}

	public VariableType getVariableType() {
		return variableType;
	}	
}
