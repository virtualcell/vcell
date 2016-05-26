package cbit.vcell.bionetgen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;

import org.jgrapht.EdgeFactory;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.isomorphism.VF2GraphIsomorphismInspector;
import org.jgrapht.graph.SimpleGraph;
import org.vcell.util.Pair;

import cbit.vcell.parser.Expression;



// detects if 2 species are isomorphic (identical except for the bond numbering)
// implementation using jgrapht (library)
public class SpeciesGraphIsomorphismInspector {
	
	static class BNGVertex {
		String name;
		public BNGVertex(String name) {
			this.name = name;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (!(obj instanceof BNGVertex))
				return false;
			BNGVertex other = (BNGVertex) obj;
			if (name == null) {
				if (other.name != null) {
					return false;
				}
			} else if (!name.equals(other.name)) {
				return false;
			}
			return true;
		}
	}
	static class BNGEdge {
		BNGVertex sourceVertex;
		BNGVertex targetVertex;
		public BNGEdge(BNGVertex sourceVertex, BNGVertex targetVertex) {
			this.sourceVertex = sourceVertex;
			this.targetVertex = targetVertex;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((sourceVertex == null) ? 0 : sourceVertex.hashCode());
			result = prime * result	+ ((targetVertex == null) ? 0 : targetVertex.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (!(obj instanceof BNGEdge))
				return false;
			BNGEdge other = (BNGEdge) obj;
			if (sourceVertex == null) {
				if (other.sourceVertex != null) {
					return false;
				}
			} else if (!sourceVertex.equals(other.sourceVertex)) {
				return false;
			}
			if (targetVertex == null) {
				if (other.targetVertex != null) {
					return false;
				}
			} else if (!targetVertex.equals(other.targetVertex)) {
				return false;
			}
			return true;
		}
	}
	
	public UndirectedGraph<BNGVertex, BNGEdge> initialize(BNGSpecies bngSpecies) {
		List<BNGSpecies> bngSpeciesList = new ArrayList<>(); 
		if(bngSpecies instanceof BNGComplexSpecies) {
			bngSpeciesList.addAll(Arrays.asList(bngSpecies.parseBNGSpeciesName()));
		} else {
			bngSpeciesList.add(bngSpecies);		// if it's a simple species to begin with we'll only have one element in this list
		}
		
		EdgeFactory<BNGVertex, BNGEdge> edgeFactory = new EdgeFactory<BNGVertex, BNGEdge>() {
			@Override
			public BNGEdge createEdge(
					BNGVertex sourceVertex,
					BNGVertex targetVertex) {
				return new BNGEdge(sourceVertex,targetVertex);
			}
		};
		UndirectedGraph<BNGVertex, BNGEdge> speciesGraph = new SimpleGraph<BNGVertex, BNGEdge>(edgeFactory);
		Map <Integer, Pair<BNGVertex, BNGVertex>> bondMap = new LinkedHashMap<>();
		for(BNGSpecies ss : bngSpeciesList) {
			// must be multi state (actually multi-site!), we have at least 2 components (sites): RbmUtils.SiteStruct and RbmUtils.SiteProduct
			if(!(ss instanceof BNGMultiStateSpecies)) {
				throw new RuntimeException("Species " + ss.getName() + " must be instance of BNGMultiStateSpecies");
			}
			BNGMultiStateSpecies mss = (BNGMultiStateSpecies)ss;
			String speciesName = mss.extractName();
			speciesName = speciesName.substring(0, speciesName.indexOf("("));
			BNGVertex speciesVertex  = new BNGVertex(speciesName);			// a species vertex
			speciesGraph.addVertex(speciesVertex);
			for(BNGSpeciesComponent c : mss.getComponents()) {
				Integer bondIndex = 0;
				String componentName = c.getComponentName();
				String currentState = c.getCurrentState();
				if(componentName.contains("!") && !(componentName.contains("!+") || componentName.contains("!?"))) {
					bondIndex = Integer.parseInt(componentName.substring(componentName.indexOf("!")+1));
					componentName = componentName.substring(0, componentName.indexOf("!"));
				} else if(currentState != null && currentState.contains("!") && !(currentState.contains("!+") || currentState.contains("!?"))) {
					bondIndex = Integer.parseInt(currentState.substring(currentState.indexOf("!")+1));
				} else {
					continue;		// don't need graph elements without explicit bond
				}
				if(componentName.contains("~")) {
					componentName = componentName.substring(0, componentName.indexOf("~"));
				}
				BNGVertex componentVertex  = new BNGVertex(componentName);			// a component (site) vertex
				speciesGraph.addVertex(componentVertex);
				speciesGraph.addEdge(speciesVertex, componentVertex);
				
				if(bondIndex > 0) {							// found an explicit bond between components (sites)
					if(bondMap.containsKey(bondIndex)) {	// first half of the bond there already, adding the second
						Pair<BNGVertex, BNGVertex> p = bondMap.remove(bondIndex);
						p = new Pair<BNGVertex, BNGVertex>(p.one, componentVertex);
						bondMap.put(bondIndex, p);
					} else {								// creating the bond, adding the 1st half
						Pair<BNGVertex, BNGVertex> p = new Pair<BNGVertex, BNGVertex>(componentVertex, null);
						bondMap.put(bondIndex, p);
					}
				}
			}
		}
		// add the bonds (between components) as edges
		for( Integer key : bondMap.keySet() ){
			Pair<BNGVertex, BNGVertex> p = bondMap.get(key);
			System.out.println(p.one + ", " + p.two);
			speciesGraph.addEdge(p.one, p.two);
		}
		return speciesGraph;
	}

	public boolean isIsomorphism(BNGSpecies ours, BNGSpecies theirs) {
		UndirectedGraph<BNGVertex, BNGEdge> ourGraph = initialize(ours);
		UndirectedGraph<BNGVertex, BNGEdge> theirGraph = initialize(theirs);
		VF2GraphIsomorphismInspector<BNGVertex, BNGEdge> inspector = new VF2GraphIsomorphismInspector<BNGVertex, BNGEdge>(ourGraph, theirGraph);
		boolean ret = inspector.isomorphismExists();
		return ret;
	}

	// =================================================================================================================
	public static void main(String[] argv)
	{
//		permutingArray(java.util.Arrays.asList(9, 8, 7, 6, 4), 0);
		
		try {
			String a = "EGF(rb!1).EGF(rb!2).EGFR(ecd!1,tmd!3,y1068~p,y1173~u).EGFR(ecd!2,tmd!3,y1068~u,y1173~p)";
			String b = "EGF(rb!1).EGF(rb!2).EGFR(ecd!1,tmd!3,y1068~u,y1173~p).EGFR(ecd!2,tmd!3,y1068~p,y1173~u)";
			String c = "EGF(rb!1).EGF(rb!2).EGFR(ecd!1,tmd!3,y1068~u,y1173~p).EGFR(ecd!2,tmd!3,y1068~p,y1173~v)";	// slightly different
//			String c = "EGF(rb~Y!1).EGF(rb~pY!1).EGFR(ecd!2,tmd!3,y1068~u,y1173~p).EGFR(ecd!2,tmd!3,y1068~p,y1173~u)";
			String d = "A(s,t!+,v!1).B(s~Y,t~Y!+,u~Y!?,v~Y!1)";
//			String d = "A(v!1).B(v~Y!1)";

			List<BNGSpecies> list = new ArrayList<>();
			
			BNGSpecies aa = new  BNGComplexSpecies(a, new Expression("0.0"), 1);
			BNGSpecies bb = new  BNGComplexSpecies(b, new Expression("0.0"), 2);
			BNGSpecies cc = new  BNGComplexSpecies(c, new Expression("0.0"), 3);
			BNGSpecies dd = new  BNGComplexSpecies(d, new Expression("0.0"), 4);
			list.add(aa);
			list.add(bb);
			list.add(cc);
			list.add(dd);
			
			SpeciesGraphIsomorphismInspector sii = new SpeciesGraphIsomorphismInspector();
			BNGSpecies ours = aa;
			for(BNGSpecies theirs : list) {
				if(sii.isIsomorphism(ours, theirs)) {
					System.out.println("Found match: " + ours.getName() + " and " + theirs.getName());
				} else {
					System.out.println("Mismatch: " + ours.getName() + " and " + theirs.getName());

				}
			}
			System.out.println("done");
			
		} catch (Throwable e) {
			System.out.println("Uncaught exception in SpeciesIsomorphismInspector.main()");
			e.printStackTrace(System.out);
		}
	}

	private static void permutingArray(List<Integer> arrayList, int element) {
		for (int i = element; i < arrayList.size(); i++) {
			java.util.Collections.swap(arrayList, i, element);
			permutingArray(arrayList, element + 1);
			java.util.Collections.swap(arrayList, element, i);
		}
		if (element == arrayList.size() - 1) {
			System.out.println(java.util.Arrays.toString(arrayList.toArray()));
		}
	}

}
