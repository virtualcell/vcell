/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.uome;

import java.util.ArrayList;
import java.util.List;

import org.sbpax.util.sets.SetUtil;
import org.vcell.uome.core.UOMEExpression;
import org.vcell.uome.core.UOMEUnit;

public class UOMETextUtil {

	public static String buildSymbol(UOMEUnit unit) {
		String symbol = SetUtil.pickAny(unit.getSymbols());
		if(symbol != null) { return symbol; }
		String name = SetUtil.pickAny(unit.getNames());
		if(name != null) { return "[" + name + "]"; }
		return "[?]";		
	}
	
	public static String buildString(UOMEUnit unit, int depth, boolean expandKnownSymbols) {
		if(expandKnownSymbols) {
			if(depth > 0) {
				UOMEExpression expression = SetUtil.pickAny(unit.getExpressions());
				if(expression != null) {
					return "(" + buildString(expression, depth - 1, expandKnownSymbols) + ")";
				}			
			}
			return buildSymbol(unit);
		} else {
			String symbol = SetUtil.pickAny(unit.getSymbols());
			if(symbol != null) { return symbol; }
			if(depth > 0) {
				UOMEExpression expression = SetUtil.pickAny(unit.getExpressions());
				if(expression != null) {
					return "(" + buildString(expression, depth - 1, expandKnownSymbols) + ")";
				}			
			}			
		}
		return buildSymbol(unit);
	}
	
	public static String buildString(UOMEUnit unit) { return buildString(unit, 30, false); }
	
	public static String buildString(UOMEExpression expression, int depth, boolean expandKnownSymbols) {
		List<String> strings = new ArrayList<String>();
		for(UOMEUnit unit : expression.getUnits()) {
			strings.add(buildString(unit, depth, expandKnownSymbols));
		}
		return expression.buildString(strings);			
	}
	
	public static String buildString(UOMEExpression expression) { return buildString(expression, 30, true); }
	public static String buildStringShort(UOMEExpression expression) { return buildString(expression, 3, false); }

}
