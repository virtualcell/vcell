/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.models.sbbox.imp;

/*   SBBoxImp  --- by Oliver Ruebenacker, UCHC --- June 2008 to January 2010
 *   Organizes the RDF data and structures to edit it
 */

import java.io.Serializable;

import org.openrdf.model.Graph;
import org.vcell.sybil.models.sbbox.SBBox;
import org.vcell.sybil.models.sbbox.factories.Factories;

@SuppressWarnings("serial")
public class SBBoxImp implements SBBox, Serializable {
	
	protected Graph rdf;
	
	protected Factories facs;
		
	public SBBoxImp(Graph rdf) {
		this.rdf = rdf;
		facs = new FactoriesImp(this);
	}
	
	public Graph getRdf() { return rdf; }
	
	public Factories factories() { return facs; }

}
