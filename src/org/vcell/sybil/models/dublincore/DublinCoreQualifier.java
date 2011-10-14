/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.models.dublincore;

import org.openrdf.model.URI;
import org.sbpax.schemas.ProtegeDC;
import org.vcell.sybil.models.AnnotationQualifier;
import org.vcell.sybil.util.keys.KeyOfOne;

public class DublinCoreQualifier extends KeyOfOne<URI> implements AnnotationQualifier {

	public static DublinCoreQualifier.DateQualifier created = 
		new DublinCoreQualifier.DateQualifier(ProtegeDC.created, "creation date");

	public static class DateQualifier extends DublinCoreQualifier {
		public DateQualifier(URI property, String description) {
			super(property,description);
		}
	}
	
	private String description = null;
	
	public URI getProperty() { return a(); }
	
	public DublinCoreQualifier(URI property, String description) {
		super(property);
		this.description = description;
	}

	public String getLocalName(){
		return getProperty().getLocalName();
	}

	public String getNameSpace(){
		return getProperty().getNamespace();
	}
	
	public String getDescription(){
		return description;
	}

	
}
