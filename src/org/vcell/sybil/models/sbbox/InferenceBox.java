/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.models.sbbox;

/*   DataTray  --- by Oliver Ruebenacker, UCHC --- June 2008 to December 2009
 *   Organizes the RDF data and structures to edit it
 */

import org.vcell.sybil.rdf.RDFBox;

import com.hp.hpl.jena.rdf.model.Model;

public interface InferenceBox extends RDFBox {
	
	public void performSYBREAMReasoning();
	
	public Model getSbpax();
	public Model getSchema();
	public Model getData();
	public Model getRdf();
}
