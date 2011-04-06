package org.vcell.sybil.models.graph.manipulator.categorizer;

/*   EvaluatorManip  --- by Oliver Ruebenacker, UCHC --- January 2008 to March 2010
 *   An EvaluatorManip showing only nodes containing a PaxNode.PROCESS
 */

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.vcell.sybil.models.graph.UIGraph;
import org.vcell.sybil.models.graph.UIShape;
import org.vcell.sybil.models.graph.Visibility;
import org.vcell.sybil.models.graph.manipulator.GraphManipulationException;
import org.vcell.sybil.models.graphcomponents.RDFGraphComponent;
import org.vcell.sybil.models.graphcomponents.RDFGraphCompContainer;
import org.vcell.sybil.models.graphcomponents.RDFGraphCompThing;
import org.vcell.sybil.models.ontology.Evaluator;
import org.vcell.sybil.models.ontology.NoEvaluatorException;
import org.vcell.sybil.models.sbbox.SBBox.NamedThing;
import org.vcell.sybil.rdf.schemas.SBPAX;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

public class ProcessesOnly<S extends UIShape<S>, G extends UIGraph<S, G>> 
extends EvaluatorGraphManipulator<S, G> {

	public ProcessesOnly() {};
	public ProcessesOnly(Evaluator evaluator) { super(evaluator); }
	
	public boolean shouldBeMadeVisible(RDFGraphComponent comp) {
		if(comp instanceof RDFGraphCompThing || comp instanceof RDFGraphCompContainer) {
			Set<NamedThing> things = comp.things();
			Set<Resource> typeNodes = new HashSet<Resource>();
			Model defaultModel = evaluator.view().box().getRdf();
			for(NamedThing thing : things) {
				NodeIterator nodeIter = defaultModel.listObjectsOfProperty(thing.resource(), RDF.type);
				while(nodeIter.hasNext()) {
					RDFNode typeNode = nodeIter.nextNode();
					if(typeNode instanceof Resource) { typeNodes.add((Resource) typeNode); }
				}
				if(typeNodes.contains(SBPAX.Process)) { return true; }
			}
		}
		return false;
	}
	
	@Override
	public void applyToGraph(G graph) throws GraphManipulationException {
		if(evaluator == null) { throw new GraphManipulationException(new NoEvaluatorException()); }
		Iterator<S> shapeIter = graph.shapeIter();
		while(shapeIter.hasNext()) {
			S shape = shapeIter.next();
			RDFGraphComponent comp = shape.graphComp();
			if(shouldBeMadeVisible(comp)) { graph.unhide(comp, Visibility.hiderFilter); }
			else { graph.hideComp(comp, Visibility.hiderFilter); }
		}
	}

}
