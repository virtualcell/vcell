package org.vcell.sybil.models.graphcomponents.group;

/*   PatternGroup  --- by Oliver Ruebenacker, UCHC --- March 2008 to November 2009
 *   Tag for a SyCo created as a group (e.g. a ResourceNodeGroup matching a pattern)
 */

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.vcell.sybil.models.graph.UIGraph;
import org.vcell.sybil.models.graph.UIShape;
import org.vcell.sybil.models.graph.UIGraph.InvalidParentException;
import org.vcell.sybil.models.graphcomponents.RDFGraphComponent;
import org.vcell.sybil.models.graphcomponents.RDFGraphCompContainer;
import org.vcell.sybil.models.graphcomponents.RDFGraphCompThingGroup;
import org.vcell.sybil.models.graphcomponents.RDFGraphCompThing;
import org.vcell.sybil.models.graphcomponents.tag.RDFGraphCompTag;
import org.vcell.sybil.models.graphcomponents.tag.RDFGraphCompTagPatternGroup;
import org.vcell.sybil.models.ontology.ChainOneKey;
import org.vcell.sybil.models.ontology.Evaluator;
import org.vcell.sybil.models.ontology.NoEvaluatorException;
import org.vcell.sybil.models.sbbox.SBBox.NamedThing;
import org.vcell.sybil.rdf.schemas.BioPAX2;
import org.vcell.sybil.rdf.schemas.SBPAX;
import org.vcell.sybil.util.collections.MultiHashMap;
import org.vcell.sybil.util.collections.MultiMap;
import org.vcell.sybil.util.exception.CatchUtil;
import org.vcell.sybil.util.legoquery.QueryResult;
import org.vcell.sybil.util.legoquery.chains.ChainOneQuery;
import org.vcell.sybil.util.legoquery.chains.ChainOneQuery.Vars;

public class PatternGroup {
	
	static public final ChainOneKey INTERACTION_WITH_CONTROLLED = 
		new ChainOneKey(SBPAX.Interaction, BioPAX2.CONTROLLED, SBPAX.Interaction);

	static public <S extends UIShape<S>, G extends UIGraph<S, G>>
	boolean isInGroup(RDFGraphComponent comp, ChainOneKey group, UIGraph<S, G> graph) {
		while(comp != null) {
			if(comp instanceof RDFGraphCompThingGroup) {
				RDFGraphCompThingGroup compGroup = ((RDFGraphCompThingGroup)comp);
				RDFGraphCompTag tag = compGroup.tag();
				if(tag instanceof RDFGraphCompTagPatternGroup) {
					ChainOneKey groupComp = ((RDFGraphCompTagPatternGroup)tag).key();
					if(groupComp == group) { 
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
	void createPatternNodeGroup(UIGraph<S, G> graph, ChainOneKey query, boolean forward, 
			Evaluator evaluator) {
		MultiMap<RDFGraphCompThing, RDFGraphComponent> groupMap = new MultiHashMap<RDFGraphCompThing, RDFGraphComponent>();
		Map<RDFGraphComponent, RDFGraphComponent> parentMap = new HashMap<RDFGraphComponent, RDFGraphComponent>();
		Set<QueryResult<Vars>> results = evaluator.chainOneResults(query);
		for(QueryResult<ChainOneQuery.Vars> result : results) {
			NamedThing startNode = result.thing(ChainOneQuery.Node1);
			NamedThing endNode = result.thing(ChainOneQuery.Node2);
			NamedThing node1 = forward ? startNode : endNode;
			NamedThing node2 = forward ? endNode : startNode;
			Set<RDFGraphComponent> compSet1 = graph.model().thingToComponentMap().get(node1);
			Set<RDFGraphComponent> compSet2 = graph.model().thingToComponentMap().get(node2);				
			Set<RDFGraphComponent> intersection = new HashSet<RDFGraphComponent>(compSet1);
			intersection.retainAll(compSet2);
			compSet1.removeAll(intersection);
			compSet2.removeAll(intersection);
			for(RDFGraphComponent comp1 : compSet1) {
				if(comp1 instanceof RDFGraphCompThing) {
					RDFGraphCompThing nodeComp1 = (RDFGraphCompThing) comp1;
					RDFGraphComponent compParent1 = graph.parentComp(comp1);
					for(RDFGraphComponent comp2 : compSet2) {
						RDFGraphComponent compParent2 = graph.parentComp(comp2);
						if(!nodeComp1.equals(comp2) && compParent1 == compParent2) {
							groupMap.add(nodeComp1, comp2);
							parentMap.put(comp1, compParent1);
						}
					}
				}
			}
		}
		for(Entry<RDFGraphCompThing, Set<RDFGraphComponent>> entry : groupMap.entrySet()) { 
			RDFGraphCompThing comp1 = entry.getKey();
			if(!isInGroup(comp1, query, graph)) {
				try {
					graph.createNodeGroup(comp1, entry.getValue(), parentMap.get(comp1), 
							new RDFGraphCompTagPatternGroup(query));
				} catch (InvalidParentException e) {
					CatchUtil.handle(e);
				}
			}
		}
	}

	static public <S extends UIShape<S>, G extends UIGraph<S, G>>
	void createStandardNodeGroups(G graph, Evaluator evaluator) throws NoEvaluatorException {
		if(evaluator == null) { throw new NoEvaluatorException(); }
		createPatternNodeGroup(graph, PatternGroup.INTERACTION_WITH_CONTROLLED, false, evaluator);
	}
	
}
