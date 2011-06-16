/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.models.io.selection;

/*   ModelSelector  --- by Oliver Ruebenacker, UCHC --- November 2009
 *   Extract statements of a model based on selected resources
 */

import java.util.Set;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

public interface ModelSelector {

	public Model createSelection(Model model, Set<Resource> selectedResources);	
}
