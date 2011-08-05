/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.util.http.pathwaycommons.search;

/*   Response  --- by Oliver Ruebenacker, UCHC --- March 2009
 *   Response from a web request using command search from Pathway Commons
 */

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.vcell.sybil.util.http.pathwaycommons.PathwayCommonsRequest;
import org.vcell.sybil.util.http.pathwaycommons.PathwayCommonsResponse;
import org.vcell.sybil.util.xml.DOMUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class PCErrorResponse extends PathwayCommonsResponse {

	protected Error error;
		
	public PCErrorResponse(PathwayCommonsRequest request, Document document) { 
		this(request, errorElement(document));
	}
	
	public PCErrorResponse(PathwayCommonsRequest request, Element errorElement) { 
		super(request);
		if(errorElement != null) { error = new Error(errorElement); }
	}
	
	public static Element errorElement(String text) 
	throws SAXException, IOException, ParserConfigurationException {
		return errorElement(DOMUtil.parse(text));
	}
	
	public static Element errorElement(Document document) {
		return DOMUtil.firstChildElement(document, "error");
	}
	
	public Error error() { return error; }
	
	@Override
	public String toString() {
		return "[Error response: \n" + (error != null ? "error=" + error.toString() + ";\n" : "") + "]\n";
	}
}
