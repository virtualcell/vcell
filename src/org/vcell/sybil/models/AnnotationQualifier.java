/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.models;

import java.net.URI;
import java.net.URISyntaxException;

import com.hp.hpl.jena.rdf.model.Property;

public interface AnnotationQualifier {
	public Property property();
	public URI getURI() throws URISyntaxException;
	public String getLocalName();
	public String getNameSpace();
	public String getDescription();

}
