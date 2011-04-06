package org.vcell.sybil.util.legoquery;

/*   PropertyVar  --- by Oliver Ruebenacker, UCHC --- March 2009
 *   A variable in a query usable for predicates
 */

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.engine.binding.Binding;

public class PropertyVar extends ResourceVar {

	public PropertyVar(Var varNew) { super(varNew); }
	public PropertyVar(String varNameNew) { super(varNameNew); }

	@Override
	public Property node(Binding binding) { 
		return model.asRDFNode(binding.get(var)).as(Property.class); 
	}

	
}
