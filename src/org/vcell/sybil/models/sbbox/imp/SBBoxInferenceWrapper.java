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

import org.vcell.sybil.models.sbbox.InferenceBox;
import org.vcell.sybil.models.sbbox.SBBox;
import org.vcell.sybil.models.sbbox.SBInferenceBox;
import org.vcell.sybil.models.sbbox.factories.Factories;
import org.vcell.sybil.models.sbbox.factories.SBBoxFactory;
import org.vcell.sybil.util.label.LabelMan;

import com.hp.hpl.jena.rdf.model.Model;

public class SBBoxInferenceWrapper implements SBInferenceBox {

	protected final SBBox sbBox;
	protected final InferenceBox infBox;
	
	public SBBoxInferenceWrapper(SBBox sbBox) {
		this.sbBox = sbBox;
		this.infBox = SBBoxFactory.createInfBox(sbBox.getRdf());
	}
	
	public LabelMan<NamedThing> labelMan() { return sbBox.labelMan(); }
	public String baseURI() { return sbBox.baseURI(); }
	public Factories factories() { return sbBox.factories(); }
	public Model getRdf() { return infBox.getRdf(); }
	public void performSYBREAMReasoning() { infBox.performSYBREAMReasoning(); }
	public Model getSchema() { return infBox.getSchema(); }
	public Model getData() { return infBox.getData(); }

}
