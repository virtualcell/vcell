package org.vcell.sybil.models.bpimport.edges;

/*   QueryEdgeSBBox  --- by Oliver Ruebenacker, UCHC --- July to November 2009
 *   An SBBox containing process edges from a process query
 */


import java.util.List;
import java.util.Vector;

import org.vcell.sybil.models.arq.ProcessQuery;
import org.vcell.sybil.models.sbbox.SBInferenceBox;

import com.hp.hpl.jena.datatypes.DatatypeFormatException;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.algebra.Algebra;
import com.hp.hpl.jena.sparql.algebra.Op;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.engine.QueryIterator;
import com.hp.hpl.jena.sparql.engine.binding.Binding;

public class QueryEdgeSBBox extends EdgeSBTray {

	public QueryEdgeSBBox(SBInferenceBox box) {
		super(box);
		Op op = ProcessQuery.op();
		QueryIterator qIter = Algebra.exec(op, box().getRdf());
		List<Binding> bindings = new Vector<Binding>();
		while(qIter.hasNext()) { bindings.add(qIter.nextBinding()); }
		for(Binding binding : bindings) {
			MutableEdge edge = new MutableEdge(this);
			RDFNode nodeProc = getRDFNode(binding, ProcessVars.PROCESS);
			if(nodeProc instanceof Resource) { 
				edge.setProcess(box().factories().processFactory().create((Resource) nodeProc));
			}
			RDFNode nodePart = getRDFNode(binding, ProcessVars.PARTICIPANT);
			if(nodePart instanceof Resource) { 
				edge.setParticipant(box().factories().participantFactory().create((Resource) nodePart));
			}
			RDFNode nodeSpec = getRDFNode(binding, ProcessVars.SPECIES);
			if(nodeSpec instanceof Resource) { 
				edge.setSpecies(box().factories().speciesFactory().create((Resource) nodeSpec));
			}
			RDFNode nodeEnti = getRDFNode(binding, ProcessVars.ENTITY);
			if(nodeEnti instanceof Resource) { 
				edge.setEntity(box().factories().substanceFactory().create((Resource) nodeEnti));
			}
			RDFNode nodeType = getRDFNode(binding, ProcessVars.ENTITY_TYPE);
			if(nodeType instanceof Resource) { 
				edge.setEntityType(box().factories().typeFactory().create((Resource) nodeType));
			}
			RDFNode nodeSubs = getRDFNode(binding, ProcessVars.SUBSTANCE);
			if(nodeSubs instanceof Resource) { 
				edge.setSubstance(box().factories().substanceFactory().create((Resource) nodeSubs));
			}
			RDFNode nodeLocation = getRDFNode(binding, ProcessVars.LOCATION);
			if(nodeLocation instanceof Resource) { 
				edge.setLocation(box().factories().locationFactory().create((Resource) nodeLocation));
			}
			RDFNode nodeStoi = getRDFNode(binding, ProcessVars.STOICHIOMETRY);
			if(nodeStoi instanceof Resource) { 
				edge.setStoichiometry(box().factories().stoichiometryFactory().create((Resource) nodeStoi));
			}
			RDFNode nodeSC = getRDFNode(binding, ProcessVars.STOICHCOEFF);
			if(nodeSC instanceof Literal) { 
				Literal literal = (Literal) nodeSC;
				try {
					edge.setSC(literal.getFloat());					
				} catch(DatatypeFormatException exception) {
					String lexicalForm = literal.getLexicalForm();
					try {
						edge.setSC(Float.valueOf(lexicalForm).floatValue());							
					} catch (Throwable t) {
						try {
							edge.setSC((float) literal.getDouble());	
							System.out.println(
							"Converting stoichiometric coefficient from double to float");
						} catch(DatatypeFormatException exception2) {
							edge.setSC((float) Double.valueOf(lexicalForm).doubleValue());								
							System.out.println(
									"Converting stoichiometric coefficient from double to float");
						}
					}
				}
			}
			edges.add(edge);
		}
	}
	
	protected RDFNode getRDFNode(Binding binding, Var var) {
		RDFNode rdfNode = null;
		Node node = binding.get(var);
		if(node != null) { rdfNode = box().getData().asRDFNode(node); }
		return rdfNode;
	}

}
