/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.pathway.kinetics;

public class SBOTerm {

	protected final String id;
	protected final String symbol;
	protected final String name;
	protected final String description;
	
	public SBOTerm(String id, String symbol, String name, String description) {
		this.id = id;
		this.symbol = symbol;
		this.name = name;
		this.description = description;
	}
	
	public String getId() { return id; }
	public String getSymbol() { return symbol; }
	public String getName() { return name; }
	public String getDescription() { return description; }
	
}
