/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.pathway.sbo;

import java.util.HashSet;
import java.util.Set;

import cbit.vcell.parser.Expression;

public class SBOTerm {
	
	public static final String SBO_BASE_URI = "http://biomodels.net/SBO/";
	public static final String SBO_ID_PREFIX = "SBO_";

	protected final String index;
	protected final Set<String> labels = new HashSet<String>();
	protected final Set<String> comments = new HashSet<String>();
	@Deprecated
	protected final Set<SBOTerm> subClasses = new HashSet<SBOTerm>();
	@Deprecated
	protected final Set<SBOTerm> superClasses = new HashSet<SBOTerm>();
	protected Expression expression;
	protected String symbol;
	protected String name;
	protected String description;
	protected String isA;
	
	public SBOTerm(String index) { this.index = index; }
	
	public String getIndex() { return index; }
	public String getId() { return SBO_ID_PREFIX + index; }
	public String getURI() { return SBO_BASE_URI + getId(); }
	
	public int hashCode() { return index.hashCode(); }
	
	public boolean equals(Object obj) {
		if(obj instanceof SBOTerm) { 
			return getIndex().equals(((SBOTerm) obj).getIndex()); 
		}
		return false;
	}

	public Set<String> getLabels() { return labels; }
	public Set<String> getComments() { return comments; }
	@Deprecated
	public Set<SBOTerm> getSubClasses() { return subClasses; }
	@Deprecated
	public Set<SBOTerm> getSuperClasses() { return superClasses; }
	
	public void setExpression(Expression expression) { this.expression = expression; }
	public Expression getExpression() { return expression; }
	public void setSymbol(String symbol) { this.symbol = symbol; }
	public String getSymbol() { return symbol; }
	public void setName(String name) { this.name = name; }
	public String getName() { return name; }
	public void setDescription(String description) { this.description = description; }
	public String getDescription() { return description; }
	public void setIsA(String isA) { this.isA = isA; }
	public String getIsA() { return isA; }

	
}
