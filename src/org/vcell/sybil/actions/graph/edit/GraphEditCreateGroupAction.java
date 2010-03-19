package org.vcell.sybil.actions.graph.edit;

/*  GraphEditCreateGroupAction  ---  Oliver Ruebenacker, UCHC  ---  August 2007 to November 2009
 *  Creates a group form selected node
 */

import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.vcell.sybil.actions.ActionSpecs;
import org.vcell.sybil.actions.CoreManager;
import org.vcell.sybil.actions.graph.ModelGraphManager;
import org.vcell.sybil.actions.graph.GraphAction;
import org.vcell.sybil.models.graph.UIGraph;
import org.vcell.sybil.models.graph.UIShape;
import org.vcell.sybil.models.graph.Visibility;
import org.vcell.sybil.models.graphcomponents.RDFGraphComponent;
import org.vcell.sybil.models.graphcomponents.RDFGraphCompThing;
import org.vcell.sybil.models.graphcomponents.tag.RDFGraphCompTagUserSelection;
import org.vcell.sybil.models.ontology.Evaluator;
import org.vcell.sybil.rdf.schemas.SBPAX;
import org.vcell.sybil.util.iterators.BufferedIterator;

import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

public class GraphEditCreateGroupAction<S extends UIShape<S>, G extends UIGraph<S, G>> 
extends GraphAction<S, G> {

	private static final long serialVersionUID = 4909472259298743214L;

	public GraphEditCreateGroupAction(ActionSpecs newSpecs, CoreManager coreManager,
			ModelGraphManager<S, G> graphManager) {
		super(newSpecs, coreManager, graphManager);
	}
	
	public static final int rankMin = 1;
	public static final int rankMax = 6;

	// TODO remove evaluator
	
	public void actionPerformed(ActionEvent event) {
		if(graph() != null) {
			if(! (graph().model().selectedComps().isEmpty())) {
				Iterator<RDFGraphComponent> compIter = 
					new BufferedIterator<RDFGraphComponent>(graph().model().selectedComps().iterator());
				HashSet<RDFGraphComponent> comps = new HashSet<RDFGraphComponent>();
				while(compIter.hasNext()) { comps.add(compIter.next()); }
				RDFGraphCompThing primaryNode = null;
				Evaluator evaluator = coreManager().fileManager().evaluator();
				int bestRank = rankMax + 1;
				for(RDFGraphComponent comp : comps) {
					if(comp instanceof RDFGraphCompThing) { 
						RDFGraphCompThing node = (RDFGraphCompThing) comp;
						int thisRank = rank(comp, evaluator);
						if(primaryNode == null || thisRank < bestRank) {
							primaryNode = node;
							bestRank = thisRank;
						}
					}
				}
				if(primaryNode != null && comps.size() > 1) {
					S nodeGroupShape = 
						graph().createNodeGroup(primaryNode, comps, new RDFGraphCompTagUserSelection());
					graph().collapse(nodeGroupShape, Visibility.hiderSelection);
				}
				updateUI();
			} 
		} 
	}

	protected int rank(RDFGraphComponent comp, Evaluator evaluator) {
		if(comp instanceof RDFGraphCompThing) {
			RDFGraphCompThing node = (RDFGraphCompThing) comp;
			Resource resource = node.thing().resource();
			NodeIterator nodeIter = 
				evaluator.view().box().getRdf().listObjectsOfProperty(resource, RDF.type);
			Set<Resource> typeNodes = new HashSet<Resource>();
			while(nodeIter.hasNext()) {
				RDFNode typeNode = nodeIter.nextNode();
				if(typeNode instanceof Resource) { typeNodes.add((Resource) typeNode); }
			}
			if(typeNodes.contains(SBPAX.Process)) { return 1; }
			else if(typeNodes.contains(SBPAX.ProcessParticipant)) { return 2; }
			else if(typeNodes.contains(SBPAX.Substance)) { return 3; }
			else { return 4; }
		} else {
			return rankMax;
		}
	}

}
