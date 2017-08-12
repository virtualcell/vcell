/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.util.miriam;

/*   XRefToURN  --- by Oliver Ruebenacker, UCHC --- December 2009
 *   Create a MIRIAM URN from a id and db string (e.g. from BioPAX, Pathway Commons)
 */

public class XRefToURN {
	
	public static String createURN(String db, String id) {
		return "urn:miriam:" + urnifyDB(db) + ":" + id;
	}
	
	public static String urnifyDB(String db) {
		String dblc = db.toLowerCase().replaceAll("\\s+", ".");
		if(dblc.indexOf("refseq") > -1) { dblc = "refseq"; }
		else if(dblc.indexOf("entrez") > -1 && dblc.indexOf("gene") > -1) { dblc = "entrez.gene"; }
		else if(dblc.indexOf("biomodels") > -1) { dblc = "biomodels.db"; }
		else if(dblc.indexOf("chebi") > -1) { dblc = "obo.chebi"; }
		return dblc;
	}

}
