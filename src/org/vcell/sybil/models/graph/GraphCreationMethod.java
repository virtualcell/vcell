/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.models.graph;

/*   GraphCreationMethod  --- by Oliver Ruebenacker, UCHC --- April to October 2007
 *   A label to describe the method used to turn an RDF (Jena) model into a graph.
 */

public class GraphCreationMethod {

	protected String name;
	
	public GraphCreationMethod(String name) { this.name = name; }
	public String name() { return name; }
	
}
