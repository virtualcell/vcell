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