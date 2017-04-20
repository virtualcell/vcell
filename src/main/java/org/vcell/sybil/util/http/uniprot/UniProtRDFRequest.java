/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.util.http.uniprot;

import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import org.openrdf.model.Graph;
import org.openrdf.rio.RDFFormat;
import org.sbpax.impl.HashGraph;
import org.sbpax.impl.IndexedGraph;
import org.sbpax.util.SesameRioUtil;

/*   UniProtRDFRequest  --- by Oliver Ruebenacker, UCHC --- January 2010
 *   Launch a web request to UniProt to get RDF for an id
 */

public class UniProtRDFRequest {

	public static class Response {
		UniProtRDFRequest request;
		public Response(UniProtRDFRequest request) { this.request = request; }
		public UniProtRDFRequest request() { return request; }
	}
	
	public static class ModelResponse extends Response {
		protected Graph model;
		public ModelResponse(UniProtRDFRequest request, Graph model) { 
			super(request); this.model = model; 
		}
		public Graph model() { return model; }
	}
	
	public static class ExceptionResponse extends Response {
		protected Exception exception;
		public ExceptionResponse(UniProtRDFRequest request, Exception exception) {
			super(request);
			this.exception = exception;
		}
		public Exception exception() { return exception; }
	}
	
	public String url() { return UniProtConstants.url(id); }
	public String urlRDF() { return UniProtConstants.urlRDF(id); }
	public String uri() { return UniProtConstants.uri(id); }
	
	protected String id;
	
	public UniProtRDFRequest(String id) { this.id = id; }
	
	public Response response() {
		Response response = null;
		try {
			Graph model = new HashGraph();
			// print out debug messsage to console -------- can be uncommented later.
			// Debug.message(urlRDF());
			URL url = new URL(urlRDF());
			URLConnection connection = url.openConnection();
			Map<String, String> nsMap = new HashMap<String, String>();
			SesameRioUtil.readRDFFromStream(connection.getInputStream(), model, nsMap, RDFFormat.RDFXML, uri());
			response = new ModelResponse(this, model);
		} catch (Exception exception) {
			response = new ExceptionResponse(this, exception);
		}
		return response;
	}
	
}
