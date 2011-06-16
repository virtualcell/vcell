/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.models.graphcomponents.tag;

/*   SyCoTagTool  --- by Oliver Ruebenacker, UCHC --- March 2008
 *   SyCoTag for SyCo created to support a tool
 */

public class RDFGraphCompTagTool implements RDFGraphCompTag {

	protected Object tool;
	
	public RDFGraphCompTagTool(Object tool) { this.tool = tool; }
	
	public Object tool() { return tool; }
	
}
