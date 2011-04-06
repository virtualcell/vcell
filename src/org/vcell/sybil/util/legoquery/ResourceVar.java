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

	@Override
	public Resource node(Binding binding) { return (Resource) super.node(binding); }

	
}
