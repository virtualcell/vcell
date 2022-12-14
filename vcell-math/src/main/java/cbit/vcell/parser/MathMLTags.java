/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.parser;
/**
 * Insert the type's description here.
 * Creation date: (2/8/2002 2:17:53 PM)
 * @author: Jim Schaff
 */
public interface MathMLTags {

	// token elements
	//
	public final static String CONSTANT				= "cn";
	public final static String IDENTIFIER			= "ci";
	public final static String CSYMBOL				= "csymbol";
	// Attributes for CSYMBOL
	public final static String ENCODING				= "encoding";
	public final static String DEFINITIONURL		= "definitionURL";
	
	//
	// basic content elements
	//
	public final static String APPLY				= "apply";
	public final static String MAX					= "max";
	public final static String MIN					= "min";
	public final static String PIECEWISE			= "piecewise";
	public final static String PIECE				= "piece";
	public final static String OTHERWISE			= "otherwise";
	public final static String LAMBDA				= "lambda";
	//
	// relational operators
	//
	public final static String EQUAL				= "eq";
	public final static String NOT_EQUAL			= "neq";
	public final static String GREATER				= "gt";
	public final static String LESS					= "lt";
	public final static String GREATER_OR_EQUAL		= "geq";
	public final static String LESS_OR_EQUAL		= "leq";
	//
	// arithmetic operators
	//
	public final static String PLUS					= "plus";
	public final static String MINUS				= "minus";
	public final static String TIMES				= "times";
	public final static String DIVIDE				= "divide";
	public final static String POWER				= "power";
	public final static String ROOT					= "root";
	public final static String ABS					= "abs";
	public final static String EXP					= "exp";
	public final static String LN					= "ln";
	public final static String LOG_10				= "log";
	public final static String FLOOR				= "floor";
	public final static String CEILING				= "ceiling";
	public final static String FACTORIAL			= "factorial";
	//
	// logical operators
	//
	public final static String AND					= "and";
	public final static String OR					= "or";
	public final static String XOR					= "xor";
	public final static String NOT					= "not";
	//
	// calculus elements
	//
	public final static String DIFFERENTIAL			= "diff";
	//
	// qualifier elements
	//
	public final static String DEGREE				= "degree";
	public final static String BVAR					= "bvar";
	public final static String LOGBASE				= "logbase";
	//
	// trigonometric functions
	//
	public final static String SINE					= "sin";
	public final static String COSINE				= "cos";
	public final static String TANGENT				= "tan";
	public final static String SECANT				= "sec";
	public final static String COSECANT				= "csc";
	public final static String COTANGENT			= "cot";
	public final static String HYP_SINE				= "sinh";
	public final static String HYP_COSINE			= "cosh";
	public final static String HYP_TANGENT			= "tanh";
	public final static String HYP_SECANT			= "sech";
	public final static String HYP_COSECANT			= "csch";
	public final static String HYP_COTANGENT		= "coth";
	public final static String INV_SINE				= "arcsin";
	public final static String INV_COSINE			= "arccos";
	public final static String INV_TANGENT			= "arctan";
	public final static String INV_SECANT			= "arcsec";
	public final static String INV_COSECANT			= "arccsc";
	public final static String INV_COTANGENT		= "arccot";
	public final static String INV_HYP_SINE			= "arcsinh";
	public final static String INV_HYP_COSINE		= "arccosh";
	public final static String INV_HYP_TANGENT		= "arctanh";
	public final static String INV_HYP_SECANT		= "arcsech";
	public final static String INV_HYP_COSECANT		= "arccsch";
	public final static String INV_HYP_COTANGENT	= "arccoth";
	//
	// constants
	//
	public final static String TRUE					= "true";
	public final static String FALSE				= "false";
	public final static String NOT_A_NUMBER			= "notanumber";
	public final static String PI					= "pi";
	public final static String INFINITY				= "infinity";
	public final static String E					= "exponentiale";
	//
	// semantics and annotation elements
	//
	public final static String SEMANTICS			= "semantics";
	public final static String ANNOTATION			= "annotation";
	public final static String ANNOTATION_XML		= "annotation-xml";
	// the root element
	public final static String MATH                 = "math";
}
