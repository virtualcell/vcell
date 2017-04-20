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

/*   Error  --- by Oliver Ruebenacker, UCHC --- March 2009
 *   Error message from a web request using command search from Pathway Commons
 */

import org.vcell.sybil.util.xml.DOMUtil;
import org.w3c.dom.Element;

public class Error { 
	protected String code = "", msg = "", details = ""; 
	
	public Error(Element element) {
		code = DOMUtil.firstChildContent(element, "error_code");
		msg = DOMUtil.firstChildContent(element, "error_msg");
		details = DOMUtil.firstChildContent(element, "error_details");				
	}
	
	public String code() { return code; }
	public String msg() { return msg; }
	public String details() { return details; }
	
	@Override
	public String toString() { return "[Error: code=\"" + code + "\"; msg=\"" + msg 
		+ "\"; details=\"" + details + "\""; }
}
