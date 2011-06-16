/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.models.sbbox.factories;

/*   SBBoxFactory  --- by Oliver Ruebenacker, UCHC --- October 2009
 *   Creates SBBox objects
 */

import org.vcell.sybil.models.SchemaFinder;
import org.vcell.sybil.models.sbbox.InferenceBox;
import org.vcell.sybil.models.sbbox.SBBox;
import org.vcell.sybil.models.sbbox.SBInferenceBox;
import org.vcell.sybil.models.sbbox.imp.InfBoxImp;
import org.vcell.sybil.models.sbbox.imp.SBBoxImp;
import org.vcell.sybil.models.sbbox.imp.SBBoxInferenceWrapper;
import org.vcell.sybil.models.sbbox.util.SBLabelGenerator;
import org.vcell.sybil.util.label.LabelMan;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class SBBoxFactory {

	public static SBBox create() {
		Model data = ModelFactory.createDefaultModel();
		return new SBBoxImp(data, "http://vcell.org/sbpax/",
	    		new LabelMan<SBBox.NamedThing>(new SBLabelGenerator()));
	}
	
	public static SBBox create(Model data) {
		return new SBBoxImp(data, "http://vcell.org/sbpax/",
	    		new LabelMan<SBBox.NamedThing>(new SBLabelGenerator()));
	}
	
	public static InferenceBox createInfBox(Model data) {
		return new InfBoxImp(data, SchemaFinder.schemaUnionExtended(data));
	}
	
	public static SBInferenceBox createSBInferenceBox(SBBox sbBox) {
		return new SBBoxInferenceWrapper(sbBox);
	}

	public static SBInferenceBox createSBInferenceBox() {
		return new SBBoxInferenceWrapper(create());
	}
}
