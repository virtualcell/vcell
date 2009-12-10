package org.vcell.sybil.util.http.pathwaycommons;

/*   PathwayCommonsRequest  --- by Oliver Ruebenacker, UCHC --- March 2009
 *   A web request from Pathway Commons
 */

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

import org.vcell.sybil.util.text.StringUtil;


public abstract class PathwayCommonsRequest {

	public static final String defaultBaseURL = "http://www.pathwaycommons.org/pc/webservice.do";

	protected Vector<PCRParameter> paras = new Vector<PCRParameter>();
	
	public PathwayCommonsRequest(PCRParameter.CmdVersion cmdVersionNew) {
		this(cmdVersionNew.cmd(), cmdVersionNew.version());
	}

	public PathwayCommonsRequest(PCRParameter.Command cmdNew, PCRParameter.Version versionNew) {
		paras.add(cmdNew);
		paras.add(versionNew);
	}

	public Vector<PCRParameter> paras() { return paras; }
	
	public URL url(String baseURL) throws MalformedURLException { 
		return new URL(baseURL + "?" + StringUtil.concat(paras, "&")); 
	}
	
	public URL url() throws MalformedURLException { return url(defaultBaseURL); }

	public abstract PathwayCommonsResponse response();
	public abstract String description();
		
}
