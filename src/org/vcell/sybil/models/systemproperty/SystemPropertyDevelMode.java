/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.models.systemproperty;

/*   SystemPropertyDevelMode  --- by Oliver Ruebenacker, UCHC --- March 2010
 *   Stores a global model write style
 */

public class SystemPropertyDevelMode {

	public static final String key = SystemPropertySybil.key + ".mode.devel";
	public static final boolean develModeDefault = false;
	
	public static void setDevelMode(boolean develMode) { 
		System.setProperty(key, Boolean.toString(develMode)); 
	}
	
	public static boolean develMode() { 
		String value = System.getProperty(key);
		boolean rdfFormdevelModeat;
		if(value != null) { rdfFormdevelModeat = Boolean.getBoolean(key); } 
		else { rdfFormdevelModeat = develModeDefault; }
		return rdfFormdevelModeat;
	}
	
}
