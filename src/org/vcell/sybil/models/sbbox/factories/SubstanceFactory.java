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

/*   LocationFactory  --- by Oliver Ruebenacker, UCHC --- June to November 2009
 *   A factory for locations
 */

import org.openrdf.model.Resource;
import org.vcell.sybil.models.sbbox.SBBox;
import org.vcell.sybil.models.sbbox.imp.SubstanceImp;

@SuppressWarnings("serial")
public class SubstanceFactory extends ThingFactory<SBBox.NamedThing> {

	public SubstanceFactory(SBBox box) { super(box); }
	@Override
	public SBBox.NamedThing newThing(Resource node) { return new SubstanceImp(box, node); }

}
