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

import org.vcell.sybil.models.sbbox.SBBox;
import org.vcell.sybil.models.sbbox.factories.Factories;
import org.vcell.sybil.models.sbbox.factories.LocationFactory;
import org.vcell.sybil.models.sbbox.factories.SubstanceFactory;
import org.vcell.sybil.util.label.LabelMan;
import com.hp.hpl.jena.rdf.model.Model;

public class SBBoxImp extends InfBoxImp implements SBBox, Serializable {
	
	protected String baseURI;
	protected LabelMan<SBBox.NamedThing> labelMan;
	
	protected Factories facs;
	
	protected LocationFactory locaFac = new LocationFactory(this);
	protected SubstanceFactory subsFac = new SubstanceFactory(this);
	
	public SBBoxImp(Model coreNew, Model schemaNew, String baseURI, LabelMan<SBBox.NamedThing> labelMan) {
		super(coreNew, schemaNew);
		this.baseURI = baseURI;
		this.labelMan = labelMan;
		facs = new FactoriesImp(this);
	}
	
	public String baseURI() { return baseURI; }
	public LabelMan<SBBox.NamedThing> labelMan() { return labelMan; }
	
	public Factories factories() { return facs; }

}
