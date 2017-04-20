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

/*   PathwayCommonsUtil  --- by Oliver Ruebenacker, UCHC --- January 2010
 *   Useful functions for web requests to Pathway Commons
 */

public class PathwayCommonsUtil {

	
	public static final String cpathDefaultNS = "http://cbio.mskcc.org/cpath#";
	public static final int tooLongToBeErrorMessage = 2000;
	protected static final String cpathIDBaseURI = cpathDefaultNS + "CPATH-";
	
	public static boolean isErrorResponse(String text) {
		boolean isError = false;
		if(text.length() < tooLongToBeErrorMessage) { isError = (text.indexOf("<error") >= 0); }
		return isError;
	}

	public static String getURI(int id) { return cpathIDBaseURI + id; }
	
	public static int getID(String uri) {
		boolean allWereDigits = true;
		String id = "";
		for(int ind = uri.length() - 1; ind >= 0 && allWereDigits; --ind) {
			char character = uri.charAt(ind);
			if(Character.isDigit(character)) {
				id = character + id;
			} else {
				allWereDigits = false;
			}
		}
		return Integer.parseInt("0" + id);
	}
	
}
