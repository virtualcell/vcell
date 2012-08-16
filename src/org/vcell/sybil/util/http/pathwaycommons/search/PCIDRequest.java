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

/*   PCIDRequest  --- by Oliver Ruebenacker, UCHC --- March 2009
 *   Launch a web request using command get_record_by_cpathID from Pathway Commons
 */

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import org.openrdf.model.Graph;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.sbpax.impl.HashGraph;
import org.sbpax.impl.IndexedGraph;
import org.sbpax.util.SesameRioUtil;
import org.sbpax.util.StringUtil;
import org.vcell.sybil.util.http.pathwaycommons.PCExceptionResponse;
import org.vcell.sybil.util.http.pathwaycommons.PCRParameter;
import org.vcell.sybil.util.http.pathwaycommons.PathwayCommonsRequest;
import org.vcell.sybil.util.http.pathwaycommons.PathwayCommonsResponse;

public class PCIDRequest extends PathwayCommonsRequest {
	
	protected String id;
	protected String str;
	public PCIDRequest(String id)  { 
		super(PCRParameter.CmdVersion.getRecordByCPID); 
		this.id = id;
		paras().add(new PCRParameter.Q(id));
		paras().add(PCRParameter.Output.biopax);
	}

	@Override
	public PathwayCommonsResponse response() {
		try { 
			URL url = url();
			URLConnection connection = url.openConnection();
			String text = StringUtil.textFromInputStream(connection.getInputStream());
			Graph graph = new HashGraph();
			Map<String, String> nsMap = new HashMap<String, String>();
			SesameRioUtil.readRDFFromString(text, graph, nsMap, RDFFormat.RDFXML, uriBase);
			try { return new PCTextModelResponse(this, text, graph); } 
			catch(Throwable t) { return new PCTextResponse(this, text); }
		} 
		catch (MalformedURLException e) { return new PCExceptionResponse(this, e); } 
		catch (IOException e) { return new PCExceptionResponse(this, e); } 
		catch (RDFParseException e) { return new PCExceptionResponse(this, e); } 
		catch (RDFHandlerException e) { return new PCExceptionResponse(this, e); } 
	}

	@Override
	public String description() { return "cpath id search for " + id; }
	@Override public String shortTitle() { return "ID " + id; }
	
}
