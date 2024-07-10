/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.models.miriam;

/*   MIRIAMRef  --- by Oliver Ruebenacker, UCHC --- March 2010
 *   A MIRIAM reference consisting of a datatype and a value or id
 */

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.sybil.util.keys.KeyOfTwo;

public class MIRIAMRef extends KeyOfTwo<String, String> {
	private final static Logger lg = LogManager.getLogger(MIRIAMRef.class);
	
	public MIRIAMRef(String type, String id) { super(type, id); }

	public String type() { return a(); }
	public String id() { return b(); }
	
	public static final String encoding = "UTF-8";

	public static String encode(String raw) { 
		try {return URLEncoder.encode(raw, encoding); } 
		catch (UnsupportedEncodingException e) { lg.error(e); return null; }
	}
	
	public static String decode(String encoded) {
		try { return URLDecoder.decode(encoded, encoding); } 
		catch (UnsupportedEncodingException e) { lg.error(e); return null; }
	}
	
	public String urn() { return "urn:miriam:" + encode(type()) + ":" + encode(id()); }
	
	@SuppressWarnings("serial")
	public static class URNParseFailureException extends Exception {
		public URNParseFailureException(String message) { super(message); }
	}
	
	public static MIRIAMRef createFromURN(String urn) throws URNParseFailureException {
		if(urn.startsWith("http://")) {
			URL url = null;
			try{
				url = new URL(urn);
			}catch(Exception e) {
				throw new URNParseFailureException(e.getMessage());
			}
			String[] pathParts = url.getPath().split("/");
			if(pathParts.length == 3) {				// post processing when importing from SBML (from BMDB database for example)
				if(pathParts[1].equalsIgnoreCase("go")) {
					pathParts[1] = "obo.go";
				} else if(pathParts[1].equalsIgnoreCase("obo.chebi")) {
					pathParts[1] = "chebi";
				} else if(pathParts[1].contains("omim")) {
					pathParts[1] = "omim";
				} else if(pathParts[1].contains("biomodels.sbo")) {
					pathParts[1] = "sbo";
				} else if(pathParts[1].contains("uniprot.isoform")) {
					pathParts[1] = "uniprot";
				} else if(pathParts[1].equalsIgnoreCase("obo.psi-mod")) {
					pathParts[1] = "mod";
				}
				return new MIRIAMRef(pathParts[1],pathParts[2]);
			} else if(pathParts.length == 4) {
				if(pathParts[1].equalsIgnoreCase("doi")) {
					String suffix = pathParts[2] + "/" + pathParts[3];
					return new MIRIAMRef(pathParts[1], suffix);
				}
			} else if(pathParts.length == 5) {
				if(pathParts[1].equalsIgnoreCase("doi")) {
					String suffix = pathParts[2] + "/" + pathParts[3] + "/" + pathParts[4];
					return new MIRIAMRef(pathParts[1], suffix);
				}
			} else {
				throw new URNParseFailureException("couldn't interpret urn " + urn);
			}
		}
		
		String[] split = urn.split(":");
		if(!split[0].equals("urn")) {
			throw new URNParseFailureException(urn + ": First component needs to be 'urn', but is '" + split[0] + "'");
		}
		if(!split[1].equals("miriam")) {
			throw new URNParseFailureException(urn + "Second component needs to be 'miriam', but is '" + split[1] + "'");
		}
		String type = decode(split[2]);
		type = type.toLowerCase();
		String id = "";
		if(split.length == 4) {
			id += decode(split[3]);
		} else if(split.length == 5) {
			id += decode(split[3]);
			if(id != null && (id.equalsIgnoreCase("GO") || id.equalsIgnoreCase("CHEBI") ||
					id.equalsIgnoreCase("PR") || id.equalsIgnoreCase("SBO") ||
					id.equalsIgnoreCase("CL") || id.equalsIgnoreCase("BTO") ||
					id.equalsIgnoreCase("PATO"))) {
				id += ":";
				id += decode(split[4]);
			} else {
				throw new URNParseFailureException(urn + ": Unrecognized fourth component.");
			}
		} else {
			throw new URNParseFailureException(urn + ": Wrong number of components " + split.length);
		}
		if(id.startsWith("OMIT:")) {
			id = id.replace("OMIT:", "OMIT_");		// OMIT format for id is, surprisingly  "OMIT_xxxxxxx" instead of "OMIT:xxxxxxx"
		}
		return new MIRIAMRef(type, id);
	}
	
}
