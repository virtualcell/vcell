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

/*   ProcessModelImp  --- by Oliver Ruebenacker, UCHC --- June to November 2009
 *   A view of a resource representing an SBPAX process model
 */

import org.vcell.sybil.models.sbbox.SBBox;
import org.vcell.sybil.rdf.schemas.SBPAX;

import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public class ProcessModelImp extends SBWrapper implements SBBox.MutableProcessModel {

	public ProcessModelImp(SBBox box, Resource resource) { super(box, resource); }

	public SBBox.MutableProcess process() { 
		Resource proc = null;
		StmtIterator stmtIter = 
			box().getRdf().listStatements(this.resource(), SBPAX.modelsProcess, (RDFNode) null);
		while(stmtIter.hasNext() && proc == null) { 
			RDFNode procNode = stmtIter.nextStatement().getObject(); 
			if(procNode instanceof Resource) { proc = (Resource) procNode; }
		}
		SBBox.MutableProcess process = null;
		if(proc != null) { process = box().factories().processFactory().open(proc); }
		return process; 
	}

	public ProcessModelImp setProcess(SBBox.Process process) {
		box().getRdf().removeAll(this.resource(), SBPAX.modelsProcess, (RDFNode) null);
		if(process != null) { 
			box().getRdf().add(this.resource(), SBPAX.modelsProcess, process.resource()); 
		}
		return this;
	}

}
