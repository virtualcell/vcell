/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.sbpax.impl;

import org.openrdf.model.BNode;

@SuppressWarnings("serial")
public class BNodeImpl implements BNode {

	protected final String id;
	
	public BNodeImpl(String id) { this.id = id; }
	
	public String stringValue() { return id; }
	public String getID() { return id; }
	public int hashCode() { return id.hashCode(); }
	public boolean equals(Object o) { return o instanceof BNode && ((BNode) o).getID().equals(id); }
	public String toString() { return id; }
	
}
