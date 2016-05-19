package cbit.vcell.bionetgen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import cbit.function.DefaultScalarFunction;
import cbit.function.DynamicScalarFunction;
import cbit.vcell.parser.Expression;

public class SpeciesIsomorphismInspector {
	
	class ComponentVertex {
		String prefix = null;
		int bond = 0;
		public ComponentVertex(String prefix, int bond) {
			this.prefix = prefix;
			this.bond = bond;
		}
		@Override
		public String toString() {
			String s = prefix;
			if(bond != 0) {
				s+= "!" + bond;
			}
			return s;
		}
		public void extractBonds(Set<Integer> bondsSet) {
			if(bond != 0) {
				bondsSet.add(bond);
			}
		}
	}
	class SimpleSpeciesVertex {
		String name;
		List<ComponentVertex> componentVertexList = new ArrayList<>();		// DO NOT REARRANGE OR SORT THIS!
		public SimpleSpeciesVertex(String name) {
			this.name = name;
		}
		public void extractBonds(Set<Integer> bondsSet) {
			for(ComponentVertex cv : componentVertexList) {
				cv.extractBonds(bondsSet);
			}
		}
		public String getIsomer(List<SimpleSpeciesVertex> speciesVertexList, Map<Integer, Integer> bondsIndexesMap, List<Integer> permutation) {
			List<String> entities = new ArrayList<>();
			for(ComponentVertex cv : componentVertexList) {
				String componentName = cv.prefix;
				if(cv.bond != 0) {
					Integer originalBondValue = cv.bond;
					Integer index = bondsIndexesMap.get(originalBondValue);
					Integer newBondValue = permutation.get(index);		// the 'originalBondValue' takes the value 'newBondValue' for this permutation
					componentName += newBondValue;
				}
				entities.add(componentName);
			}
			Collections.sort(entities);
			String signature = name + "(";
			for(int i=0; i<entities.size(); i++) {
				if(i>0) {
					signature += ",";
				}
				signature += entities.get(i);
			}
			signature += ")";
			return signature;
		}
		public String getSignature() {
			List<String> entities = new ArrayList<>();
			for(ComponentVertex cv : componentVertexList) {
				entities.add(cv.prefix);
			}
			Collections.sort(entities);
			String signature = name + "(";
			for(int i=0; i<entities.size(); i++) {
				if(i>0) {
					signature += ",";
				}
				signature += entities.get(i);
			}
			signature += ")";
			return signature;
		}
		@Override
		public String toString() {		// return everything in its original order, with original bonds
			String s = name + "(";
			for(int i=0; i<componentVertexList.size(); i++) {
				if(i>0) {
					s += ",";
				}
				s += componentVertexList.get(i).toString();
			}
			s += ")";
			return s;
		}
	}
	
	// -------------------------------------------------------------------------------------------------------
	
	private BNGSpecies ours = null;
	private boolean isInitialized = false;
	private List<SimpleSpeciesVertex> ourVertexList = new ArrayList<>();
	
	Set<Integer> ourBondsSet = null;		// ordered list of all the bonds
	private Map<Integer, Integer> bondsIndexesMap = new HashMap<>();

//	15 EGF(rb!1).EGF(rb!2).EGFR(ecd!1,tmd!3,y1068~p,y1173~u).EGFR(ecd!2,tmd!3,y1068~u,y1173~p) 0.0
//	16 EGF(rb!1).EGF(rb!2).EGFR(ecd!1,tmd!3,y1068~u,y1173~p).EGFR(ecd!2,tmd!3,y1068~p,y1173~u) 0.0

	public void initialize(BNGSpecies ours) {
		this.ours = ours;
		List<BNGSpecies> ourList = new ArrayList<>(); 
		if(ours instanceof BNGComplexSpecies) {
			ourList.addAll(Arrays.asList(ours.parseBNGSpeciesName()));
		} else {
			ourList.add(ours);		// if it's a simple species to begin with we'll only have one element in this list
		}
		for(BNGSpecies ss : ourList) {
			// must be multi state (actually multi-site!), we have at least 2 components (sites): RbmUtils.SiteStruct and RbmUtils.SiteProduct
			if(!(ss instanceof BNGMultiStateSpecies)) {
				throw new RuntimeException("Species " + ss.getName() + " must be instance of BNGMultiStateSpecies");
			}
			BNGMultiStateSpecies mss = (BNGMultiStateSpecies)ss;
			SimpleSpeciesVertex ssv = extractVertex(mss);
			ourVertexList.add(ssv);
		}
		// extract the bonds
		Set<Integer> bondsSet = new TreeSet<>();
		for(SimpleSpeciesVertex ssv : ourVertexList) {
			ssv.extractBonds(bondsSet);
		}
		ourBondsSet = bondsSet;
		List<Integer> bondsList = new ArrayList<>(ourBondsSet);
		for(int i=0; i<bondsList.size(); i++) {
			bondsIndexesMap.put(bondsList.get(i), i);
		}
		isInitialized = true;
	}
	
	
	public boolean isIsomorphism(BNGSpecies their) {
		if(!isInitialized) {
			throw new RuntimeException("SpeciesIsomorphismInspector initialization failed, cannot continue.");
		}

		List<BNGSpecies> theirList = new ArrayList<>();
		if(their instanceof BNGComplexSpecies) {
			theirList.addAll(Arrays.asList(their.parseBNGSpeciesName()));
		} else {
			theirList.add(their);
		}
		if(ourVertexList.size() != theirList.size()) {
			return false;
		}

		List<SimpleSpeciesVertex> theirVertexList = new ArrayList<>();
		for(BNGSpecies ss : theirList) {
			if(!(ss instanceof BNGMultiStateSpecies)) {
				throw new RuntimeException("Species " + ss.getName() + " must be instance of BNGMultiStateSpecies");
			}
			BNGMultiStateSpecies mss = (BNGMultiStateSpecies)ss;
			SimpleSpeciesVertex simpleSpeciesVertex = extractVertex(mss);
			theirVertexList.add(simpleSpeciesVertex);
		}
		if(!getSignature(ourVertexList).equals(getSignature(theirVertexList))) {
			return false;	// signatures don't match, lucky us  :)
		}
		
		String theirsSorted = their.getName();		// TODO: this should be sorted already
		
		List<Integer> bondsList = new ArrayList<>(ourBondsSet);
		boolean ret = isIsomorphismInternal(theirsSorted, bondsList, 0);
		return ret;
	}
	
	private boolean isIsomorphismInternal(String theirsSorted, List<Integer> arrayList, int element) {
		for (int i = element; i < arrayList.size(); i++) {
			java.util.Collections.swap(arrayList, i, element);
			boolean ret = isIsomorphismInternal(theirsSorted, arrayList, element + 1);
			if(ret == true) {
				return true;	// if match is found we don't continue
			}
			java.util.Collections.swap(arrayList, element, i);
		}
		if (element == arrayList.size() - 1) {
//			System.out.println(java.util.Arrays.toString(arrayList.toArray()));
			String oursSorted = getIsomer(arrayList);
			if(oursSorted.equals(theirsSorted)) {
				return true;
			}
		}
		return false;
	}
	private String getIsomer(List<Integer> permutation) {
		// almost identical with getting the signature, but we rebuild the full text sorted representation of the species
		// using the current array of bond permutations and the bond map (key = bond, value = index)
		// ex: bond map: 1,0 2,1 3,2 4,3  and current array of permutations is 2143 (2 at index 0, 1 at index 1, 4 at index 2, etc)
		// say we need to replace bond 3, its value is 2 (from the bonds map), so 2 is the index where its permutation is in the 
		// current array of permutations, there we find 4. Result: we replace 3 with 4 in the places where we find bond 3
		List<String> entities = new ArrayList<>();
		for(SimpleSpeciesVertex sv : ourVertexList) {
			entities.add(sv.getIsomer(ourVertexList, bondsIndexesMap, permutation));
		}
		Collections.sort(entities);
		String signature = "";
		for(int i=0; i<entities.size(); i++) {
			if(i>0) {
				signature += ".";
			}
			signature += entities.get(i);
		}
		return signature;
	}
	private static String getSignature(List<SimpleSpeciesVertex> speciesVertexList) {
		List<String> entities = new ArrayList<>();
		for(SimpleSpeciesVertex sv : speciesVertexList) {
			entities.add(sv.getSignature());
		}
		Collections.sort(entities);
		String signature = "";
		for(int i=0; i<entities.size(); i++) {
			if(i>0) {
				signature += ".";
			}
			signature += entities.get(i);
		}
		return signature;
	}
	private SimpleSpeciesVertex extractVertex(BNGMultiStateSpecies mss) {
		String expression = mss.getName();
//		System.out.println(expression);
		String name = mss.getName().substring(0, mss.getName().indexOf("("));
		SimpleSpeciesVertex ssv = new SimpleSpeciesVertex(name);
		for(BNGSpeciesComponent c : mss.getComponents()) {
			name = c.getComponentName();
			if(c.getCurrentState() != null) {
				name += "~" + c.getCurrentState();
			}
//			System.out.println("   " + name);
			String prefix;
			int bond = 0;		// 0 means there is no bond; real bond is > 0
			if(name.contains("!") && !(name.contains("!+") || name.contains("!?"))) {	// we have explicit bond
				prefix = name.substring(0, name.indexOf("!")+1);
				bond = Integer.parseInt(name.substring(name.indexOf("!")+1));
			} else {
				prefix = name;
			}
			ComponentVertex cv = new ComponentVertex(prefix, bond);
			ssv.componentVertexList.add(cv);
		}
		return ssv;
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
			String d = "A(s,t!+,v!1).B(s~Y,t~Y!+,u~Y!?,v~!1)";

			List<BNGSpecies> list = new ArrayList<>();
			
			BNGSpecies aa = new  BNGComplexSpecies(a, new Expression("0.0"), 1);
			BNGSpecies bb = new  BNGComplexSpecies(b, new Expression("0.0"), 2);
			BNGSpecies cc = new  BNGComplexSpecies(c, new Expression("0.0"), 3);
			BNGSpecies dd = new  BNGComplexSpecies(d, new Expression("0.0"), 4);
			list.add(aa);
			list.add(bb);
			list.add(cc);
			
			
			SpeciesIsomorphismInspector sii = new SpeciesIsomorphismInspector();
			sii.initialize(aa);
			for(BNGSpecies theirs : list) {
				if(sii.isIsomorphism(theirs)) {
					System.out.println("Found match: " + sii.ours.getName() + " and " + theirs.getName());
				} else {
					System.out.println("Mismatch: " + sii.ours.getName() + " and " + theirs.getName());

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
