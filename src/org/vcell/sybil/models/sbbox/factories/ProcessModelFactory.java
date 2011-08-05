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

/*   ProcessModelFactory  --- by Oliver Ruebenacker, UCHC --- June to December 2009
 *   A factory for process models
 */

import org.openrdf.model.Resource;
import org .vcell.sybil.models.sbbox.SBBox;
import org.vcell.sybil.models.sbbox.imp.ProcessModelImp;

@SuppressWarnings("serial")
public class ProcessModelFactory extends ThingFactory<SBBox.NamedThing> {

	public ProcessModelFactory(SBBox box) { super(box); }
	@Override
	public SBBox.NamedThing newThing(Resource node) { return new ProcessModelImp(box, node); }

}
