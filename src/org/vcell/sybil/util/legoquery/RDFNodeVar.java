package org.vcell.sybil.util.legoquery;

/*   RDFNodeVar  --- by Oliver Ruebenacker, UCHC --- March 2009
 *   A variable for objects only in a query
 */

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.engine.binding.Binding;

public class RDFNodeVar {

	public static Model model = ModelFactory.createDefaultModel();
	
	protected Var var;

	public RDFNodeVar(Var varNew) { var = varNew; }
	public RDFNodeVar(String varNameNew) { this(Var.alloc(varNameNew)); }
	
	public Var var() { return var; }
	public RDFNode node(Binding binding) { return model.asRDFNode(binding.get(var)); }
	
}
