package org.vcell.sybil.models.graph.manipulator.categorizer;

/*   ReactionsManip  --- by Oliver Ruebenacker, UCHC --- January 2008 to October 2009
 *   An EvaluatorManip showing only nodes related to processes. Customizable.
 */

import java.util.HashSet;
import java.util.Set;

import org.vcell.sybil.models.graph.UIGraph;
import org.vcell.sybil.models.graph.UIShape;
import org.vcell.sybil.models.graph.Visibility;
import org.vcell.sybil.models.graph.manipulator.GraphManipulationException;
import org.vcell.sybil.models.graphcomponents.RDFGraphComponent;
import org.vcell.sybil.models.graphcomponents.RDFGraphCompContainer;
import org.vcell.sybil.models.graphcomponents.RDFGraphCompEdge;
import org.vcell.sybil.models.graphcomponents.RDFGraphCompEdgeChain;
import org.vcell.sybil.models.graphcomponents.RDFGraphCompThingGroup;
import org.vcell.sybil.models.graphcomponents.RDFGraphCompThing;
import org.vcell.sybil.models.graphcomponents.group.PatternChain;
import org.vcell.sybil.models.graphcomponents.group.PatternGroup;
import org.vcell.sybil.models.graphcomponents.tag.RDFGraphCompTag;
import org.vcell.sybil.models.graphcomponents.tag.RDFGraphCompTagPatternChain;
import org.vcell.sybil.models.graphcomponents.tag.RDFGraphCompTagPatternGroup;
import org.vcell.sybil.models.ontology.ChainOneKey;
import org.vcell.sybil.models.ontology.ChainTwoKey;
import org.vcell.sybil.models.ontology.Evaluator;
import org.vcell.sybil.models.ontology.NoEvaluatorException;
import org.vcell.sybil.models.sbbox.SBBox.NamedThing;
import org.vcell.sybil.models.sbbox.imp.SBWrapper;
import org.vcell.sybil.rdf.schemas.BioPAX2;
import org.vcell.sybil.rdf.schemas.SBPAX;
import org.vcell.sybil.util.legoquery.QueryResult;
import org.vcell.sybil.util.legoquery.chains.ChainOneQuery;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.RDF;

public class ReactionsManipulator<S extends UIShape<S>, G extends UIGraph<S, G>> 
extends EvaluatorGraphManipulator<S, G> {

	protected Set<RDFGraphComponent> visibleComps = new HashSet<RDFGraphComponent>();
	protected Set<Resource> visibles = new HashSet<Resource>();

	protected boolean withReactants = true;
	protected boolean withComponents = true;
	protected boolean collapseSubProcesses = true;
	protected boolean collapseParticipants = true;

	public ReactionsManipulator() { }
	public ReactionsManipulator(Evaluator evaluator) { super(evaluator); }

	public void setWithReactants(boolean withReactants) { this.withReactants = withReactants; }
	public void setWithComponents(boolean withComponents) { this.withComponents = withComponents; }

	public void setCollapseSubProcesses(boolean collapseSubProcesses) { 
		this.collapseSubProcesses = collapseSubProcesses; 
	}

	public void setCollapseParticipants(boolean collapseParticipants) { 
		this.collapseParticipants = collapseParticipants; 
	}

	public boolean withReactants() { return withReactants; }
	public boolean withComponents() { return withComponents; }
	public boolean collapseSubProcesses() { return collapseSubProcesses; }
	public boolean collapseParticipants() { return collapseParticipants; }

	protected void addNodes(UIGraph<S, G> graph, Resource nodeType) {
		ResIterator resIter = 
			evaluator.view().box().getRdf().listResourcesWithProperty(RDF.type, nodeType);
		while(resIter.hasNext()) { 
			visibles.add(resIter.nextResource()); 
		}
		for(Resource node : visibles) {
			NamedThing thing = new SBWrapper(evaluator.view().box(), node);
			for(RDFGraphComponent comp : graph.model().thingToComponentMap().get(thing)) {
				if(comp instanceof RDFGraphCompThing || comp instanceof RDFGraphCompContainer) {
					visibleComps.add(comp);					
				}
			}
		}

	}

	protected void addRelations(UIGraph<S, G> graph, Resource type1, Property predicate, Resource type2) {
		Set<Statement> statements = new HashSet<Statement>();
		Set<QueryResult<ChainOneQuery.Vars>> results = 
			evaluator.chainOneResults(type1, predicate, type2);
		for(QueryResult<ChainOneQuery.Vars> result : results) {
			Resource startNode = result.resource(ChainOneQuery.Node1);
			Property pred = result.property(ChainOneQuery.Relation);
			Resource endNode = result.resource(ChainOneQuery.Node2);
			if(visibles.contains(startNode)) { 
				statements.add(ResourceFactory.createStatement(startNode, pred, endNode));
			}
		}
		Set<Resource> newVisibles = new HashSet<Resource>();
		for(RDFGraphComponent comp : graph.shapeMap().keySet()) {
			if(comp instanceof RDFGraphCompEdge) {
				RDFGraphCompEdge edgeComp = (RDFGraphCompEdge) comp;
				RDFGraphComponent comp1 = edgeComp.startComp();
				RDFGraphComponent comp2 = edgeComp.endComp();
				Statement statement = edgeComp.statement();
				if(statements.contains(statement)) {
					newVisibles.add(statement.getSubject());
					newVisibles.add((Resource)statement.getObject());
					visibleComps.add(comp1);
					visibleComps.add(comp2);
					visibleComps.add(edgeComp);					
				}
			}
		}
		visibles.addAll(newVisibles);
	}

	protected void collapse(UIGraph<S, G> graph, ChainOneKey key, boolean collapse) {
		Set<RDFGraphComponent> compSet = graph.shapeMap().keySet();
		for(RDFGraphComponent comp : compSet) {
			if(comp instanceof RDFGraphCompThingGroup) {
				RDFGraphCompThingGroup compGroup = (RDFGraphCompThingGroup) comp;
				RDFGraphCompTag tag = compGroup.tag();
				if(tag instanceof RDFGraphCompTagPatternGroup) {
					ChainOneKey tagKey = ((RDFGraphCompTagPatternGroup) tag).key();
					if(tagKey == key) {
						if(collapse) { graph.collapse(compGroup, Visibility.hiderFilter); } 
						else { graph.explode(compGroup, Visibility.hiderFilter); }
					}
				}
			}
		}
	}

	protected void collapse(UIGraph<S, G> graph, ChainTwoKey query, boolean collapse) {
		Set<RDFGraphComponent> compSet = graph.shapeMap().keySet();
		for(RDFGraphComponent comp : compSet) {
			if(comp instanceof RDFGraphCompEdgeChain) {
				RDFGraphCompEdgeChain compChain = (RDFGraphCompEdgeChain) comp;
				RDFGraphCompTag tag = compChain.tag();
				if(tag instanceof RDFGraphCompTagPatternChain) {
					ChainTwoKey tagQuery = ((RDFGraphCompTagPatternChain) tag).key();
					if(tagQuery == query) {
						if(collapse) { graph.collapse(compChain, Visibility.hiderFilter); } 
						else { graph.explode(compChain, Visibility.hiderFilter); }
					}
				}
			}
		}
	}

	@Override
	public void applyToGraph(G graph) throws GraphManipulationException {
		if(evaluator == null) { throw new GraphManipulationException(new NoEvaluatorException()); }
		collapse(graph, PatternGroup.INTERACTION_WITH_CONTROLLED, collapseSubProcesses);
		collapse(graph, PatternChain.interactionParticipantEntity, collapseParticipants);
		collapse(graph, PatternChain.entityParticipantEntity, collapseParticipants);
		visibles.clear();
		visibleComps.clear();
		graph.hideAll(Visibility.hiderFilter);
		addNodes(graph, SBPAX.Interaction);
		addRelations(graph, SBPAX.Interaction, BioPAX2.CONTROLLED, SBPAX.Interaction);
		if(withReactants) {
			addRelations(graph, SBPAX.Interaction, BioPAX2.PARTICIPANTS, SBPAX.Substance);
			addRelations(graph, SBPAX.Interaction, BioPAX2.PARTICIPANTS, SBPAX.ProcessParticipant);
			addRelations(graph, SBPAX.ProcessParticipant, BioPAX2.PHYSICAL_ENTITY, SBPAX.Substance);
		}
		if(withComponents) {
			int numOfVisibleComps = -1;
			while(numOfVisibleComps != visibleComps.size()) {
				numOfVisibleComps = visibleComps.size();			
				addRelations(graph, SBPAX.Substance, BioPAX2.COMPONENTS, SBPAX.Substance);
				addRelations(graph, SBPAX.Substance, BioPAX2.COMPONENTS, SBPAX.ProcessParticipant);
				addRelations(graph, SBPAX.ProcessParticipant, BioPAX2.PHYSICAL_ENTITY, SBPAX.Substance);				
			}
		}
		for(RDFGraphComponent comp : visibleComps) { graph.unhide(comp, Visibility.hiderFilter); }
	}
}
