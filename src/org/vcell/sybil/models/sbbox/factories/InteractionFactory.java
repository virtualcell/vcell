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

/*   InteractionFactory  --- by Oliver Ruebenacker, UCHC --- June to December 2009
 *   A factory for interactions
 */

import org.vcell.sybil.models.sbbox.SBBox;
import org.vcell.sybil.models.sbbox.imp.InteractionImp;
import org.vcell.sybil.rdf.schemas.SBPAX;

import com.hp.hpl.jena.rdf.model.Resource;

public class InteractionFactory extends ThingFactory<SBBox.MutableInteraction> {

	public InteractionFactory(SBBox box) { super(box, SBPAX.Interaction); }
	public SBBox.MutableInteraction newThing(Resource node) { return new InteractionImp(box, node); }
	public String baseLabel() { return "Interaction"; }

}
