/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.xml;
import org.apache.log4j.Logger;

import cbit.vcell.math.MathFunctionDefinitions;
import cbit.vcell.parser.*;
/**
 * Insert the type's description here.
 * Creation date: (3/22/2001 11:17:04 AM)
 * @author: Daniel Lucio
 */
public abstract class XmlBase {
	
	/**
	 * Log4j logger
	 */
	protected static final Logger lg = Logger.getLogger(XmlBase.class);

/**
 * Default XmlBase constructor .
 */
public XmlBase() {
	super();
}


/**
 * This method currently does nothing 
 * @return param 
 */
public static String mangle(String param) {
	return param;
}


/**
 * This method returns a mangled String.
 * Creation date: (3/22/2001 11:18:39 AM)
 * @return java.lang.String
 * @param param java.lang.String
 */
public static String mangleExpression(Expression expression) {
	return mangle(expression.infix());
}


/**
 * This method currently does nothing
 * @return param 
 */
public static String unMangle(String param) {
	return param; 
}


	public static Expression unMangleExpression(String expStr) {

		Expression tempExp = null;

		//
		// if parsing fails, that's a show-stopper.
		//
		try {
			tempExp = new Expression(unMangle(expStr)); 
			tempExp = MathFunctionDefinitions.fixFunctionSyntax(tempExp);
		}catch (ExpressionException e){
			e.printStackTrace(System.out);
			throw new RuntimeException(e.getMessage());
		}
		
		return tempExp;
	}
}
