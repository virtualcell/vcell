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

import java.util.Collection;
import java.util.Set;

import org.openrdf.model.URI;
import org.sbpax.util.StringUtil;
import org.vcell.uome.core.UOMEExpression;
import org.vcell.uome.core.UOMEUnit;

public class UOMEParserIssue {
	
	protected final String message;
	
	public UOMEParserIssue(String message) { this.message = message; }
	
	public String getMessage() { return message; }

	protected static String getUnitText(UOMEUnit unit) { return StringUtil.concat(unit.getNames(), "/"); }
	
	public static class UnknownExpressionClassIssue extends UOMEParserIssue {
		public UnknownExpressionClassIssue(UOMEExpression expression) {
			super("Don't know class " + expression.getClass() + " of expression " + UOMETextUtil.buildStringShort(expression));
		}
	}

	public static class MultipleEntriesForSameProperty extends UOMEParserIssue{
		public MultipleEntriesForSameProperty(UOMEExpression expression, URI property, Set<?> entries) {
			super("Expression " + UOMETextUtil.buildStringShort(expression) + " has multiple entries for property " + 
					property.getLocalName());
		}
	}
	
	public static class NoEntryForProperty extends UOMEParserIssue{
		public NoEntryForProperty(UOMEExpression expression, URI property) {
			super("Expression " + UOMETextUtil.buildStringShort(expression) + " has no entry for property " + 
					property.getLocalName());
		}
	}
	
	public static boolean checkExpressionClass(Collection<UOMEParserIssue> issues, UOMEExpressionPool pool, UOMEExpression expression) {
		if(!pool.canSuggestType(expression)) {
			issues.add(new UnknownExpressionClassIssue(expression));
			return false;
		}
		return true;
	}
	
	public static boolean checkSingularCardinality(Collection<UOMEParserIssue> issues, UOMEExpression expression, URI property, 
			Set<?> entries) {
		if(entries.size() > 1) { 
			issues.add(new UOMEParserIssue.MultipleEntriesForSameProperty(expression, property, entries)); 
			return false;
		} else if(entries.size() == 0) { 
			issues.add(new UOMEParserIssue.NoEntryForProperty(expression, property)); 
			return false;
		}
		return true;
	}
	
}
