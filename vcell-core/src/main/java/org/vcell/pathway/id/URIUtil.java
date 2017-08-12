/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.pathway.id;

public class URIUtil {

	public static boolean isAbsoluteURI(String uri) {
		return uri.matches("[A-Za-z][A-Za-z0-9.+-]*:.*");
	}
	
	public static String relativizeURI(String uri, String baseURI) {
		return uri.startsWith(baseURI) ? uri.replaceFirst(baseURI, "") : uri;				
	}
	
	public static String unRelativizeURI(String uri, String baseURI) {
		return !isAbsoluteURI(uri) ? (baseURI + uri) : uri;				
	}
	
	public static String abbreviateURI(String uri, String baseURI) {
		return uri.startsWith(baseURI) ? uri.replaceFirst(baseURI + "#", "") : uri;		
	}
	
	public static String unAbbreviateURI(String uri, String baseURI) {
		return !isAbsoluteURI(uri) ? (baseURI + "#" + uri) : uri;		
	}

	// the algorithm is taken from OpenRDF Sesame
	public static int getURISplitPosition(String uri) {
		int iSharp = uri.indexOf("#");
		if(iSharp >= 0) {
			return iSharp + 1;
		} else {
			int iSlash = uri.lastIndexOf("/");
			if(iSlash >= 0) {
				return iSlash + 1;				
			} else {
				int iColon = uri.lastIndexOf(":");
				if(iColon >= 0) {
					return iColon + 1;				
				} else {
					return 0;
				}				
			}
		}
	}
		
	public static String getLocalName(String uri) {
		return uri.substring(getURISplitPosition(uri));
	}
		
	public static String getNameSpace(String uri) {
		return uri.substring(0, getURISplitPosition(uri));
	}
		
}
