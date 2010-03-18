package org.vcell.sybil.models.graphcomponents.group;

/*   PatternChain  --- by Oliver Ruebenacker, UCHC --- April 2008 to November 2009
 *   For a tag for a SyCo created as a group matching a two step pattern
 */

import java.util.HashSet;
import java.util.Set;
import org.vcell.sybil.models.graph.UIGraph;
import org.vcell.sybil.models.graph.UIShape;
import org.vcell.sybil.models.graph.UIGraph.InvalidParentException;
import org.vcell.sybil.models.graphcomponents.RDFGraphComponent;
import org.vcell.sybil.models.graphcomponents.RDFGraphCompContainer;
import org.vcell.sybil.models.graphcomponents.RDFGraphCompEdge;
import org.vcell.sybil.models.graphcomponents.RDFGraphCompEdgeChain;
import org.vcell.sybil.models.graphcomponents.tag.RDFGraphCompTag;
import org.vcell.sybil.models.graphcomponents.tag.RDFGraphCompTagPatternChain;
import org.vcell.sybil.models.ontology.ChainTwoKey;
import org.vcell.sybil.models.ontology.Evaluator;
import org.vcell.sybil.models.ontology.NoEvaluatorException;
import org.vcell.sybil.models.sbbox.SBBox.NamedThing;
import org.vcell.sybil.rdf.schemas.BioPAX2;
import org.vcell.sybil.rdf.schemas.SBPAX;
import org.vcell.sybil.util.exception.CatchUtil;
import org.vcell.sybil.util.legoquery.QueryResult;
import org.vcell.sybil.util.legoquery.chains.ChainTwoQuery;
import org.vcell.sybil.util.legoquery.chains.ChainTwoQuery.Vars;

public class PatternChain {

	// TODO: use RDF abstraction
	
	static public final ChainTwoKey interactionParticipantEntity = 
		new ChainTwoKey(SBPAX.Interaction, BioPAX2.PARTICIPANTS, SBPAX.ProcessParticipant,
				BioPAX2.PHYSICAL_ENTITY, SBPAX.Substance);
	
	static public final ChainTwoKey entityParticipantEntity = 
		new ChainTwoKey(SBPAX.Substance, BioPAX2.COMPONENTS, SBPAX.ProcessParticipant,
				BioPAX2.PHYSICAL_ENTITY, SBPAX.Substance);

	static public <S extends UIShape<S>, G extends UIGraph<S, G>> boolean 
	isInChain(RDFGraphComponent comp, ChainTwoKey key, G graph) {
		while(comp != null) {
			if(comp instanceof RDFGraphCompEdgeChain) {
				RDFGraphCompEdgeChain compChain = ((RDFGraphCompEdgeChain)comp);
				RDFGraphCompTag tag = compChain.tag();
				if(tag instanceof RDFGraphCompTagPatternChain) {
					ChainTwoKey tagChain = ((RDFGraphCompTagPatternChain)tag).key();
					if(tagChain == key) { 
						return true; 
					} 
				} 
			} else if(comp instanceof RDFGraphCompContainer) {
				return false;
			} 
			comp = graph.parentComp(comp);
		}
		return false;
	}
	
	static public <S extends UIShape<S>, G extends UIGraph<S, G>>
	void createPatternEdgeChain(G graph, ChainTwoKey key, Evaluator evaluator) {
		Set<NamedThing> resSet = new HashSet<NamedThing>();
		resSet.addAll(graph.model().thingToComponentMap().keySet());
		Set<QueryResult<Vars>> results = evaluator.chainTwoResults(key);
		for(QueryResult<ChainTwoQuery.Vars> result : results) {
			Set<RDFGraphComponent> nodeCompSet1 = 
				graph.model().thingToComponentMap().get(result.thing(ChainTwoQuery.Node1));
			Set<RDFGraphComponent> nodeCompSet2 = 
				graph.model().thingToComponentMap().get(result.thing(ChainTwoQuery.Node2));
			Set<RDFGraphComponent> nodeCompSet3 = 
				graph.model().thingToComponentMap().get(result.thing(ChainTwoQuery.Node3));
			Set<RDFGraphComponent> edgeCompSet1 = new HashSet<RDFGraphComponent>(nodeCompSet1);
			edgeCompSet1.retainAll(nodeCompSet2);
			nodeCompSet1.removeAll(edgeCompSet1);
			nodeCompSet2.removeAll(edgeCompSet1);
			nodeCompSet3.removeAll(edgeCompSet1);
			Set<RDFGraphComponent> edgeCompSet2 = new HashSet<RDFGraphComponent>(nodeCompSet2);
			edgeCompSet2.retainAll(nodeCompSet3);
			nodeCompSet1.removeAll(edgeCompSet2);
			nodeCompSet2.removeAll(edgeCompSet2);
			nodeCompSet3.removeAll(edgeCompSet2);
			for(RDFGraphComponent comp1 : edgeCompSet1) {
				if(comp1 instanceof RDFGraphCompEdge) {
					RDFGraphCompEdge edgeComp1 = (RDFGraphCompEdge) comp1;
					RDFGraphComponent nodeComp1 = edgeComp1.startComp();
					RDFGraphComponent nodeComp2 = edgeComp1.endComp();
					RDFGraphComponent edgeCompParent1 = graph.parentComp(edgeComp1);
					if(nodeCompSet1.contains(nodeComp1) && nodeCompSet2.contains(nodeComp2) && 
							(!isInChain(edgeComp1, key, graph))) {
						for(RDFGraphComponent comp2 : edgeCompSet2) {
							if(comp2 instanceof RDFGraphCompEdge) {
								RDFGraphCompEdge edgeComp2 = (RDFGraphCompEdge) comp2;
								RDFGraphComponent nodeComp3 = edgeComp2.endComp();
								RDFGraphComponent edgeCompParent2 = graph.parentComp(edgeComp2);
								if(nodeComp2.equals(edgeComp2.startComp()) 
										&& nodeCompSet3.contains(nodeComp3) 
										&& (!isInChain(edgeComp2, key, graph)) 
										&& edgeCompParent1.equals(edgeCompParent2)) {
									try {
										graph.createEdgeChain(edgeComp1, edgeComp2, edgeCompParent1, 
												new RDFGraphCompTagPatternChain(key));
									} catch (InvalidParentException e) {
										CatchUtil.handle(e);
									}
								}
							}
						}						
					}
				}
			}
		}
	}

	static public <S extends UIShape<S>, G extends UIGraph<S, G>>
	void createStandardEdgeChains(G graph, Evaluator evaluator) throws NoEvaluatorException {
		if(evaluator == null) { throw new NoEvaluatorException(); }
		createPatternEdgeChain(graph, PatternChain.interactionParticipantEntity, evaluator);
		createPatternEdgeChain(graph, PatternChain.entityParticipantEntity, evaluator);
	}
	
}
