/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.util.http.pathwaycommons;

/*   PathwayCommonsRequest  --- by Oliver Ruebenacker, UCHC --- March 2009
 *   A web request from Pathway Commons
 */

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

import org.sbpax.util.StringUtil;


public abstract class PathwayCommonsRequest {

	public static final String defaultBaseURL = "http://www.pathwaycommons.org/pc/webservice.do";
	public static final String uriBase = "";
	

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
	public abstract String shortTitle();
	public abstract String description();
		
}
