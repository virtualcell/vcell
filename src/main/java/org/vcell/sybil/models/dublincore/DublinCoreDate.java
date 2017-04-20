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

public class DublinCoreDate {
	private String dateString = null;
	
	public DublinCoreDate(String argDateString){
		this.dateString = argDateString;
	}
	
	public String getDateString(){
		return dateString;
	}
}
