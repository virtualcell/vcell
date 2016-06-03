package cbit.vcell.bionetgen;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;

import org.jgrapht.EdgeFactory;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.isomorphism.VF2GraphIsomorphismInspector;
import org.jgrapht.graph.SimpleGraph;
import org.vcell.model.bngl.ASTBondState;
import org.vcell.model.bngl.ASTMolecularComponentPattern;
import org.vcell.model.bngl.ASTMolecularTypePattern;
import org.vcell.model.bngl.ASTSpeciesPattern;
import org.vcell.model.bngl.BNGLParser;
import org.vcell.model.bngl.Node;
import org.vcell.model.bngl.ParseException;
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
		public String getName(){
			return name;
		}
	}
	static class BNGEdge {
		BNGVertex sourceVertex;
		BNGVertex targetVertex;
		String name;
		public BNGEdge(BNGVertex sourceVertex, BNGVertex targetVertex) {
			this.sourceVertex = sourceVertex;
			this.targetVertex = targetVertex;
			//
			// sort edge name (because it should be undirected)
			//
			String name1 = sourceVertex.getName();
			String name2 = targetVertex.getName();
			if (name1.compareTo(name2)<0){
				this.name = name1+"_"+name2;
			}else{
				this.name = name2+"_"+name1;
			}
		}
		public String getName(){
			return this.name;
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
			BNGMultiStateSpecies molecularPattern = (BNGMultiStateSpecies)ss;
			String molecularPatternSignature = molecularPattern.extractMolecularPatternSignature();
//			speciesName = speciesName.substring(0, speciesName.indexOf("("));
			BNGVertex speciesVertex  = new BNGVertex(molecularPatternSignature);			// a species vertex
			speciesGraph.addVertex(speciesVertex);
			for(BNGSpeciesComponent c : molecularPattern.getComponents()) {
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
		Comparator<BNGVertex> vertexComparator = new Comparator<BNGVertex>() {

			@Override
			public int compare(BNGVertex o1, BNGVertex o2) {
				return o1.getName().compareTo(o2.getName());
			}
			
		};
		Comparator<BNGEdge> edgeComparator = new Comparator<BNGEdge>() {

			@Override
			public int compare(BNGEdge e1, BNGEdge e2) {
				return e1.getName().compareTo(e2.getName());
			}
			
		};
		boolean bCacheEdges = true;
		VF2GraphIsomorphismInspector<BNGVertex, BNGEdge> inspector = new VF2GraphIsomorphismInspector<BNGVertex, BNGEdge>(ourGraph, theirGraph, vertexComparator, edgeComparator, bCacheEdges);
		boolean ret = inspector.isomorphismExists();
		return ret;
	}
	
//	public static class SpeciesPatternExpression {
//		private final ASTSpeciesPattern astSpeciesPattern;
//		private final MolecularPatternExpression[] molecularPatternExpression;
//		
//		public SpeciesPatternExpression(String speciesPatternString) throws ParseException{
//			BNGLParser parser = new BNGLParser(new StringReader(speciesPatternString));
//			this.astSpeciesPattern = parser.SpeciesPattern();
//			.... create
//			this.molecularPatternExpression = new MolecularPatternExpression[num];
//			ddldl
//		}
//		
//		public MolecularPatternExpression[] getMolecularPatternExpressions(){
//			return this.molecularPatternExpression;
//		}
//	}
//
//	public static class MolecularPatternExpression {
//		
//		private final ComponentPatternExpression[] componentPatternExpression;
//
//		public MolecularPatternExpression(String speciesPatternString) throws ParseException{
//			BNGLParser parser = new BNGLParser(new StringReader(speciesPatternString));
//			this.astSpeciesPattern = parser.SpeciesPattern();
//		}
//		
//		public String getName(){
//			return ...
//		}
//		
//	}
//
//	public static class ComponentPatternExpression {
//		ASTMolecularComponentPattern pattern;
//		
//		private final MolecularPatternExpression[] molecularPatternExpression;
//
//		public MolecularPatternExpression(String speciesPatternString) throws ParseException{
//			BNGLParser parser = new BNGLParser(new StringReader(speciesPatternString));
//			this.astSpeciesPattern = parser.SpeciesPattern();
//		}
//		
//		public String getName(){
//			return pattern.getName();
//		}
//		
//		public boolean hasBond(){
//			for (int i=0;i<pattern.jjtGetNumChildren(); i++){
//				Node child = pattern.jjtGetChild(i);
//				if (child instanceof ASTBondState){
//					((ASTBondState)child).getState()
//				}
//			}
//			
//		}
//		
//		public boolean hasState(){
//			
//		}
//	}

	// =================================================================================================================
	public static void main(String[] argv)
	{
//		permutingArray(java.util.Arrays.asList(9, 8, 7, 6, 4), 0);
		
		try {
			String a = "EGF(rb!1).EGF(rb!2).EGFR(ecd!1,tmd!3,y1068~p,y1173~u).EGFR(ecd!2,tmd!3,y1068~u,y1173~p)";   // match (exact)
			String b = "EGF(rb!1).EGF(rb!2).EGFR(ecd!1,tmd!3,y1068~u,y1173~p).EGFR(ecd!2,tmd!3,y1068~p,y1173~u)";	// match (iso)
			String c = "EGF(rb!1).EGF(rb!2).EGFR(ecd!1,tmd!3,y1068~u,y1173~p).EGFR(ecd!2,tmd!3,y1068~p,y1173~v)";	// no match (slightly different in state)
//			String c = "EGF(rb~Y!1).EGF(rb~pY!1).EGFR(ecd!2,tmd!3,y1068~u,y1173~p).EGFR(ecd!2,tmd!3,y1068~p,y1173~u)";
			String d = "A(s,t!+,v!1).B(s~Y,t~Y!+,u~Y!?,v~Y!1)";		// no match (very different)
//			String d = "A(v!1).B(v~Y!1)";

			List<BNGSpecies> list = new ArrayList<>();
			
			BNGLParser parser = new BNGLParser(new StringReader(a));
			//SpeciesPatternExpression spExpression = new SpeciesPatternExpression(a);
			
			
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
