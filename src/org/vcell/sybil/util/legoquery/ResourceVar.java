/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.util.legoquery;

/*   ResourceVar  --- by Oliver Ruebenacker, UCHC --- March 2009
 *   A variable for objects or subjects only in a query
 */

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.engine.binding.Binding;

public class ResourceVar extends RDFNodeVar {

	public ResourceVar(Var varNew) { super(varNew); }
	public ResourceVar(String varNameNew) { super(varNameNew); }

	public Resource node(Binding binding) { return (Resource) super.node(binding); }

	
}
