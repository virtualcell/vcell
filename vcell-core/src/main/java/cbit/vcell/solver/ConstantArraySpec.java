/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.solver;
import cbit.vcell.math.Constant;
import cbit.vcell.math.VCML;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;

import java.util.ArrayList;
import java.util.function.Function;

/**
 * Insert the type's description here.
 * Creation date: (9/23/2005 1:25:30 PM)
 * @author: Ion Moraru
 */
public class ConstantArraySpec implements org.vcell.util.Matchable, java.io.Serializable {
	public static final int TYPE_LIST = 1000;
	public static final int TYPE_INTERVAL = 1001;
	private int type;
	private int numValues;
	private String name;
	private Expression minValue;
	private Expression maxValue;
	private boolean logInterval;
	private Constant[] constants;

/**
 * Insert the method's description here.
 * Creation date: (9/23/2005 1:31:02 PM)
 */
private ConstantArraySpec() {}


/**
 * Insert the method's description here.
 * Creation date: (10/15/2005 6:09:19 PM)
 * @return cbit.vcell.solver.ConstantArraySpec
 * @param spec cbit.vcell.solver.ConstantArraySpec
 */
public static ConstantArraySpec clone(ConstantArraySpec spec) {
	ConstantArraySpec clone = new ConstantArraySpec();
	clone.logInterval = spec.logInterval;
	clone.maxValue = spec.maxValue;
	clone.minValue = spec.minValue;
	clone.name = spec.name;
	clone.numValues = spec.numValues;
	clone.type = spec.type;
	clone.constants = new Constant[spec.constants.length];
	for (int i = 0; i < clone.constants.length; i++){
		clone.constants[i] = new Constant(spec.constants[i].getName(), new Expression(spec.constants[i].getExpression()));
	}
	return clone;
}

public void applyFunctionToExpressions(Function<Expression, Expression> expressionFunction){
	if (type == ConstantArraySpec.TYPE_LIST){
		for (int i = 0; i < constants.length; i++){
			constants[i] = new Constant(constants[i].getName(), expressionFunction.apply(constants[i].getExpression()));
		}
	}
	if (type == ConstantArraySpec.TYPE_INTERVAL){
		minValue = expressionFunction.apply(minValue);
		maxValue = expressionFunction.apply(maxValue);
		for (int i = 0; i < constants.length; i++){
			constants[i] = new Constant(constants[i].getName(), expressionFunction.apply(constants[i].getExpression()));
		}
	}
}

public void changeName(String newName) {
	if (type == ConstantArraySpec.TYPE_LIST){
		for (int i = 0; i < constants.length; i++){
			constants[i] = new Constant(newName, constants[i].getExpression());
		}
	}
	if (type == ConstantArraySpec.TYPE_INTERVAL){
		for (int i = 0; i < constants.length; i++){
			constants[i] = new Constant(newName, constants[i].getExpression());
		}
	}
}

/**
 * Checks for internal representation of objects, not keys from database
 * @return boolean
 * @param obj java.lang.Object
 */
public boolean compareEqual(org.vcell.util.Matchable obj) {
	if (obj != null || obj instanceof ConstantArraySpec) {
		ConstantArraySpec spec = (ConstantArraySpec)obj;
		if (type != spec.type ||
			!org.vcell.util.Compare.isEqual(name, spec.name) ||
			!org.vcell.util.Compare.isEqual(getConstants(), spec.getConstants())){
			return false;
		}
		return true;
	}
	return false;
}


/**
 * Insert the method's description here.
 * Creation date: (9/27/2005 11:41:59 AM)
 * @return cbit.vcell.solver.ConstantArraySpec
 */
public static ConstantArraySpec createFromString(String name, String description, int type) {
	// parses toString() output to recreate object
	try {
		if (type == TYPE_LIST) {
			if (description.contains("\"")){
				// new style (each element surrounded by double-quotes and separated by commas)
				// for example:   "KMOLE", "KMOLE*2", "KMOLE*3"
				String tokens[] = description.split("\"");
				ArrayList<String> itemExpressions = new ArrayList<>();
				for (String token : tokens){
					token = token.trim();
					if (token.length()==0 || token.equals(",")){
						continue;
					}
					itemExpressions.add(token);
				}
				return createListSpec(name, itemExpressions.toArray(new String[0]));
			}else {
				// old style (each element is a double without quotes and separated by commas)
				// for example:   1, 2, 3
				final String delimiter = ",";
				java.util.StringTokenizer tokens = new java.util.StringTokenizer(description, delimiter);
				String[] values = new String[tokens.countTokens()];
				int i = 0;
				while (tokens.hasMoreElements()) {
					values[i] = tokens.nextToken();
					i++;
				}
				return createListSpec(name, values);
			}
		} else if (type == TYPE_INTERVAL) {
			int index_of_to = description.indexOf(" to ");
			if (index_of_to == -1){
				throw new RuntimeException("invalid format '"+description+"'");
			}
			String rest = description.substring(index_of_to+4);
			boolean logInterval = false;
			if (rest.contains("log,")){
				logInterval = true;
				rest = rest.replace("log,","");
			}
			int index_of_last_comma = rest.lastIndexOf(",");

			String[] parts = description.replace(" to ",",").split(",");
			String minValueExpStr = description.substring(0,index_of_to);
			String maxValueExpStr = rest.substring(0, index_of_last_comma);
			String rest2 = rest.substring(index_of_last_comma+1);
			java.util.StringTokenizer tokens = new java.util.StringTokenizer(rest2, " ");
			String token = tokens.nextToken();
			int numValues = Integer.parseInt(token);
			return createIntervalSpec(name, minValueExpStr, maxValueExpStr, numValues, logInterval);
		} else {
			throw new RuntimeException("unknown type");
		}
	} catch (Exception exc) {
		throw new RuntimeException("Failed to create from String, " + exc);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (9/23/2005 1:47:17 PM)
 * @return cbit.vcell.solver.ConstantArraySpec
 * @param name String
 * @param minValueExpStr String
 * @param maxValueExpStr String
 * @param numValues int
 * @param logInterval boolean
 */
public static ConstantArraySpec createIntervalSpec(String name, String minValueExpStr, String maxValueExpStr, int numValues, boolean logInterval) {
	Expression minValueExp = null;
	Expression maxValueExp = null;
	try {
		minValueExp = new Expression(minValueExpStr);
		maxValueExp = new Expression(maxValueExpStr);
	} catch (ExpressionException e) {
		throw new RuntimeException("failed to parse expressions '"+minValueExpStr+"' and '"+maxValueExpStr+"': "+e.getMessage(), e);
	}
	try {
		double minValue = minValueExp.evaluateConstant();
		double maxValue = maxValueExp.evaluateConstant();
		if (logInterval && (minValue * maxValue <= 0)) throw new RuntimeException("Min and Max values cannot be zero or have different signs for log interval");
	} catch (ExpressionException e){
	}
	ConstantArraySpec spec = new ConstantArraySpec();
	spec.type = TYPE_INTERVAL;
	spec.name = name;
	spec.minValue = minValueExp;
	spec.maxValue = maxValueExp;
	spec.numValues = numValues;
	spec.logInterval = logInterval;
	spec.constants = new Constant[numValues];
	if (logInterval) {
		for (int i = 0; i < numValues; i++){
			spec.constants[i] = new Constant(name, Expression.mult(
					minValueExp,
					Expression.power(Expression.div(maxValueExp, minValueExp),
							((double)i)/(numValues - 1))));
		}
	} else {
		for (int i = 0; i < numValues; i++){
			spec.constants[i] = new Constant(name, Expression.add(
					minValueExp,
					Expression.mult(Expression.add(maxValueExp, Expression.negate(minValueExp)),
							new Expression(((double)i)/(numValues - 1)))));
		}
	}
	return spec;
}


	/**
 * Insert the method's description here.
 * Creation date: (9/23/2005 1:33:00 PM)
 * @return cbit.vcell.solver.ConstantArraySpec
 */
public static ConstantArraySpec createListSpec(String name, String[] values) throws cbit.vcell.parser.ExpressionException {
	if (values.length < 2) throw new RuntimeException("List must contain at least two values");
	ConstantArraySpec spec = new ConstantArraySpec();
	spec.type = TYPE_LIST;
	spec.name = name;
	spec.constants = new Constant[values.length];
	spec.numValues = values.length;
	for (int i = 0; i < values.length; i++){
		spec.constants[i] = new Constant(name, new cbit.vcell.parser.Expression(values[i]));
	}
	return spec;
}


/**
 * Insert the method's description here.
 * Creation date: (9/23/2005 1:34:54 PM)
 * @return int
 */
public Constant[] getConstants() {
	return constants;
}


/**
 * Insert the method's description here.
 * Creation date: (9/23/2005 2:21:53 PM)
 * @return double
 */
public Expression getMaxValue() {
	return maxValue;
}


/**
 * Insert the method's description here.
 * Creation date: (9/23/2005 2:21:53 PM)
 * @return double
 */
public Expression getMinValue() {
	return minValue;
}


/**
 * Insert the method's description here.
 * Creation date: (9/23/2005 1:43:18 PM)
 * @return java.lang.String
 */
public java.lang.String getName() {
	return name;
}


/**
 * Insert the method's description here.
 * Creation date: (9/23/2005 2:21:53 PM)
 * @return int
 */
public int getNumValues() {
	return numValues;
}


/**
 * Insert the method's description here.
 * Creation date: (9/23/2005 1:34:54 PM)
 * @return int
 */
public int getType() {
	return type;
}


/**
 * Insert the method's description here.
 * Creation date: (10/15/2005 8:52:59 PM)
 * @return java.lang.String
 */
public String getVCML() {
	return (VCML.ConstantArraySpec + " " + getName() + " " + getType() + " " + toString() + ";");
}


/**
 * Insert the method's description here.
 * Creation date: (9/23/2005 2:21:53 PM)
 * @return boolean
 */
public boolean isLogInterval() {
	return logInterval;
}


/**
 * Insert the method's description here.
 * Creation date: (9/23/2005 4:44:23 PM)
 * @return java.lang.String
 */
public String toDatabaseString() {
	switch (type) {
		case TYPE_LIST: {
			cbit.vcell.math.Constant[] cs = getConstants();
			String list = "";
			for (int i = 0; i < cs.length; i++){
				list += "\"" + cs[i].getExpression().infix() + "\"" + ", ";
			}
			list = list.substring(0, list.length() - 2); // remove trailing ", "
			return list;
		}
		case TYPE_INTERVAL: {
			String interval = minValue.infix() + " to " + maxValue.infix() + ", ";
			if (logInterval) interval += "log, ";
			interval += numValues + " values";
			return interval;
		}
		default: {
			return "Unknown ConstantArrayType";
		}
	}
}

	public String toString() {
		return toDatabaseString();
	}

}
