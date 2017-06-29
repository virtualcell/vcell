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

/*   PCIDNeighboursRequest  --- by Oliver Ruebenacker, UCHC --- March 2009 to February 2010
 *   Launch a web request using command get_neighbors from Pathway Commons
 */

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.openrdf.model.Graph;
import org.openrdf.rio.RDFFormat;
import org.sbpax.impl.HashGraph;
import org.sbpax.util.SesameRioUtil;
import org.sbpax.util.StringUtil;
import org.vcell.sybil.util.http.pathwaycommons.PCExceptionResponse;
import org.vcell.sybil.util.http.pathwaycommons.PCRParameter;
import org.vcell.sybil.util.http.pathwaycommons.PathwayCommonsRequest;
import org.vcell.sybil.util.http.pathwaycommons.PathwayCommonsResponse;
import org.xml.sax.SAXException;

public class PCNeighborsRequest extends PathwayCommonsRequest {
	
	protected String id;
	
	public PCNeighborsRequest(int id) { this("" + id); }
	
	public PCNeighborsRequest(String idNew)  { 
		super(PCRParameter.CmdVersion.getNeighbours); 
		id = idNew;
		paras().add(new PCRParameter.Q(id));
		paras().add(PCRParameter.Output.biopax);
	}

	@Override
	public PathwayCommonsResponse response() {
		PathwayCommonsResponse response = null;
		try { 
			URL url = url();
			URLConnection connection = url.openConnection();
			String text = StringUtil.textFromInputStream(connection.getInputStream());
			if(PathwayCommonsUtil.isErrorResponse(text)) {
				response = new PCErrorResponse(this, PCErrorResponse.errorElement(text));
			} else {
				try { 
					Graph graph = new HashGraph();
					Map<String, String> nsMap = new HashMap<String, String>();
					SesameRioUtil.readRDFFromString(text, graph, nsMap, RDFFormat.RDFXML, uriBase);
					response = new PCTextModelResponse(this, text, graph); 
				} catch(Throwable t) { response = new PCTextResponse(this, text); }				
			}
		} 
		catch (MalformedURLException e) { response = new PCExceptionResponse(this, e); } 
		catch (IOException e) { response = new PCExceptionResponse(this, e); } 
		catch (SAXException e) { response = new PCExceptionResponse(this, e); } 
		catch (ParserConfigurationException e) { response = new PCExceptionResponse(this, e); } 
		return response;
	}

	@Override
	public String description() { return "neighbor search for " + id; }
	@Override public String shortTitle() { return "neighbors of " + id; }
	
}
