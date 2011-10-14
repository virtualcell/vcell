/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.sbpax.schemas.util;

public class NameSpace {

	public final String prefix;
	public final String uri;

	public NameSpace(String prefix, String uri) { 
		this.prefix = prefix; this.uri = uri; 
	}
	
	@Override
	public String toString() { return uri; }

}
