package org.vcell.model.rbm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.jdom.Element;
import org.vcell.model.rbm.RuleAnalysisReport.AddBondOperation;
import org.vcell.model.rbm.RuleAnalysisReport.AddMolecularTypeOperation;
import org.vcell.model.rbm.RuleAnalysisReport.ChangeStateOperation;
import org.vcell.model.rbm.RuleAnalysisReport.DeleteBondOperation;
import org.vcell.model.rbm.RuleAnalysisReport.DeleteMolecularTypeOperation;
import org.vcell.model.rbm.RuleAnalysisReport.DeleteParticipantOperation;
import org.vcell.model.rbm.RuleAnalysisReport.Operation;

public class RuleAnalysis {
		
	public static int INDEX_OFFSET = 1;
	
	private RuleAnalysis(){
	}
	
	public enum ParticipantType {
		Reactant,
		Product
	};
	
	public interface RuleEntry {
		public String getRuleName();
		
		public int getRuleIndex();
		
		public List<? extends ParticipantEntry> getReactantEntries();		
		public List<? extends MolecularTypeEntry> getReactantMolecularTypeEntries();
		public List<? extends MolecularComponentEntry> getReactantMolecularComponentEntries();
		public List<ReactantBondEntry> getReactantBondEntries();
		
		public List<? extends ParticipantEntry> getProductEntries();
		public List<? extends MolecularTypeEntry> getProductMolecularTypeEntries();
		public List<? extends MolecularComponentEntry> getProductMolecularComponentEntries();
		public List<ProductBondEntry> getProductBondEntries();

		public String getReactionBNGLShort();

		public Double getSymmetryFactor();
	}
	
	public interface ParticipantEntry {
		public RuleEntry getRule();
		
		public int getParticipantIndex();
		
		public ParticipantType getParticipantType();
		
		public List<? extends MolecularTypeEntry> getMolecularTypeEntries();
		
	}
	
	public interface MolecularTypeEntry {
		
		public ParticipantEntry getParticipantEntry();

		public int getMoleculeIndex();

		public boolean matches(MolecularTypeEntry productMTE);

		public String getMolecularTypeName();
		
		public List<? extends MolecularComponentEntry> getMolecularComponentEntries();

		public String toBngl();

		public String getMatchLabel();

		public String getMolecularTypeBNGL();
	}
	
	public interface MolecularComponentEntry {
		public int getComponentIndex();
		
		MolecularTypeEntry getMolecularTypeEntry();

		String getMolecularComponentName();

		String getExplicitState();

		boolean isBoundTo(MolecularComponentEntry productComponent);

		public boolean hasBond();
		public boolean isBondPossible();
		public boolean isBondExists();
	}
	
	public static class ReactantBondEntry {
		public final MolecularComponentEntry reactantComponent1;
		public final MolecularComponentEntry reactantComponent2;

		public ReactantBondEntry(MolecularComponentEntry reactantComponent1, MolecularComponentEntry reactantComponent2) {
			if (reactantComponent1.getMolecularTypeEntry().getParticipantEntry().getParticipantType() != ParticipantType.Reactant){
				throw new RuntimeException("reactantComponent1 should be from a reactant pattern");
			}
			if (reactantComponent2.getMolecularTypeEntry().getParticipantEntry().getParticipantType() != ParticipantType.Reactant){
				throw new RuntimeException("reactantComponent2 should be from a reactant pattern");
			}
			this.reactantComponent1 = reactantComponent1;
			this.reactantComponent2 = reactantComponent2;
		}
	}
	
	public static class ProductBondEntry {
		public final MolecularComponentEntry productComponent1;
		public final MolecularComponentEntry productComponent2;

		public ProductBondEntry(MolecularComponentEntry productComponent1, MolecularComponentEntry productComponent2) {
			if (productComponent1.getMolecularTypeEntry().getParticipantEntry().getParticipantType() != ParticipantType.Product){
				throw new RuntimeException("component1 should be from a product pattern");
			}
			if (productComponent2.getMolecularTypeEntry().getParticipantEntry().getParticipantType() != ParticipantType.Product){
				throw new RuntimeException("component2 should be from a product pattern");
			}
			this.productComponent1 = productComponent1;
			this.productComponent2 = productComponent2;
		}
	}
	
	private static class BondEdge {
		public final MoleculeVertex molecule1;
		public final MoleculeVertex molecule2;
		
		private BondEdge(MoleculeVertex molecule1, MoleculeVertex molecule2) {
			this.molecule1 = molecule1;
			this.molecule2 = molecule2;
		}
		@Override
		public String toString(){
			return molecule1+"<->"+molecule2;
		}
	}
	
	private static class MoleculeVertex {
		public final MolecularTypeEntry molecule;
		
		private MoleculeVertex(MolecularTypeEntry molecule) {
			this.molecule = molecule;
		}
		@Override
		public boolean equals(Object obj){
			if (obj instanceof MoleculeVertex){
				MoleculeVertex other = (MoleculeVertex)obj;
				if (other.molecule != molecule){
					return false;
				}
				return true;
			}
			return false;
		}
		@Override
		public int hashCode(){
			return molecule.hashCode();
		}
		@Override
		public String toString(){
			return getID(molecule);
		}
	}
	
//	public static double analyzeSymmetry(RuleEntry rule){
//		EdgeFactory<MoleculeVertex, BondEdge> edgeFactory = new EdgeFactory<MoleculeVertex, BondEdge>() {
//
//			@Override
//			public BondEdge createEdge(
//					MoleculeVertex sourceVertex,
//					MoleculeVertex targetVertex) {
//				return new BondEdge(sourceVertex,targetVertex);
//			}
//		};
////		{
//        UndirectedGraph<MoleculeVertex, BondEdge> reactantsGraph = new SimpleGraph<MoleculeVertex, BondEdge>(edgeFactory);
//        for (MolecularTypeEntry molecule : rule.getReactantMolecularTypeEntries()){
//        	reactantsGraph.addVertex(new MoleculeVertex(molecule));
//		}
//        for (ReactantBondEntry bond : rule.getReactantBondEntries()){
//        	MoleculeVertex molecule1 = new MoleculeVertex(bond.reactantComponent1.getMolecularTypeEntry());
//        	MoleculeVertex molecule2 = new MoleculeVertex(bond.reactantComponent2.getMolecularTypeEntry());
//			reactantsGraph.addEdge(molecule1, molecule2);
//		}
//        
//        VF2SubgraphIsomorphismInspector<MoleculeVertex, BondEdge> reactionAutomorphisms =
//                new VF2SubgraphIsomorphismInspector<MoleculeVertex, BondEdge>(reactantsGraph, reactantsGraph);
//        
//        Iterator<GraphMapping<MoleculeVertex, BondEdge>> reactantMappings = reactionAutomorphisms.getMappings();
//        int reactantSymmetryFactor = 0;
//        while (reactantMappings.hasNext()){
//        	GraphMapping<MoleculeVertex, BondEdge> mapping = reactantMappings.next();
//        	System.out.println("mapping "+mapping);
//        	reactantSymmetryFactor++;
//        }
////		}  
//		
//		
//        UndirectedGraph<MoleculeVertex, BondEdge> productsGraph = new SimpleGraph<MoleculeVertex, BondEdge>(edgeFactory);
//        for (MolecularTypeEntry molecule : rule.getProductMolecularTypeEntries()){
//        	productsGraph.addVertex(new MoleculeVertex(molecule));
//		}
//        for (ProductBondEntry bond : rule.getProductBondEntries()){
//        	MoleculeVertex molecule1 = new MoleculeVertex(bond.productComponent1.getMolecularTypeEntry());
//        	MoleculeVertex molecule2 = new MoleculeVertex(bond.productComponent2.getMolecularTypeEntry());
//        	productsGraph.addEdge(molecule1, molecule2);
//		}
//        
//        VF2SubgraphIsomorphismInspector<MoleculeVertex, BondEdge> productAutomorphisms =
//                new VF2SubgraphIsomorphismInspector<MoleculeVertex, BondEdge>(productsGraph, productsGraph);
//        
//        Iterator<GraphMapping<MoleculeVertex, BondEdge>> productMappings = productAutomorphisms.getMappings();
//        int productSymmetryFactor = 0;
//        while (productMappings.hasNext()){
//        	GraphMapping<MoleculeVertex, BondEdge> mapping = productMappings.next();
//        	System.out.println("mapping "+mapping);
//        	productSymmetryFactor++;
//        }
//        
//        System.out.println("reactant symmetry factor = "+reactantSymmetryFactor);
//        System.out.println("product symmetry factor = "+productSymmetryFactor);
//        double symmetryFactor = ((double)reactantSymmetryFactor)/((double)productSymmetryFactor);
//		System.out.println("overall reaction symmetry_factor = "+symmetryFactor);
//        return symmetryFactor;
//	}
	

//	public static double analyzeSymmetryBNG(RuleEntry rule){
//		
//		//
//		// create bngl input for this rule
//		//
//		StringWriter bnglStringWriter = new StringWriter();
//		PrintWriter pw = new PrintWriter(bnglStringWriter);
//		RbmNetworkGenerator.writeBngl_internal(rule, pw);
//		String input = bnglStringWriter.toString();
//		pw.close();
//
//		//
//		// caching recent results - if input already processed, use previous results.
//		//
////		String md5hash = MD5.md5(input);
////		if(isBngHashValid(input, md5hash, simContext)) {
////			String s = "Previously saved outputSpec is up-to-date, no need to generate network.";
////			System.out.println(s);
////			tcm = new TaskCallbackMessage(TaskCallbackStatus.Error, s);	// not an error, we just want to show it in red
////			simContext.appendToConsole(tcm);
////			if(simContext.isInsufficientIterations()) {
////				s = SimulationConsolePanel.getInsufficientIterationsMessage();
////				System.out.println(s);
////				tcm = new TaskCallbackMessage(TaskCallbackStatus.Error, s);
////				simContext.appendToConsole(tcm);
////			}
////			outputSpec = simContext.getMostRecentlyCreatedOutputSpec();
////			return (BNGOutputSpec)BeanUtils.cloneSerializable(outputSpec);
////		}
//		
//		BNGInput bngInput = new BNGInput(input);
//		BNGOutput bngOutput = null;
//		try {
//			final BNGExecutorService bngService = new BNGExecutorService(bngInput,NetworkGenerationRequirements.StandardTimeoutMS);
//			bngOutput = bngService.executeBNG();
//		} catch (RuntimeException ex) {
//			ex.printStackTrace(System.out);
//			throw ex; //rethrow without losing context
//		} catch (Exception ex) {
//			ex.printStackTrace(System.out);
//			throw new RuntimeException(ex.getMessage());
//		}
//		
//		String bngConsoleString = bngOutput.getConsoleOutput();
//System.out.println(bngConsoleString);
//
//		Document bngNFSimXMLDocument = bngOutput.getNFSimXMLDocument();
//		Element sbmlElement = bngNFSimXMLDocument.getRootElement();
//		Element modelElement = sbmlElement.getChild("model", Namespace.getNamespace("http://www.sbml.org/sbml/level3"));
//		Element listOfReactionRulesElement = modelElement.getChild("ListOfReactionRules", Namespace.getNamespace("http://www.sbml.org/sbml/level3"));
//		Element reactionRuleElement = listOfReactionRulesElement.getChild("ReactionRule", Namespace.getNamespace("http://www.sbml.org/sbml/level3"));
//		String symmetry_factor_str = reactionRuleElement.getAttributeValue("symmetry_factor");
//		
//		return Double.parseDouble(symmetry_factor_str);
//	}
	
	public static RuleAnalysisReport analyze(RuleEntry rule, boolean bThrowExceptions) {
		
		RuleAnalysisReport report = new RuleAnalysisReport();

		Double symmetryFactor = rule.getSymmetryFactor();
		if (symmetryFactor == null){
			throw new RuntimeException("expecting non-null symmetry factor");
		}
		
		report.setSymmetryFactor(symmetryFactor);
		
		ArrayList<MolecularTypeEntry> unmatchedReactantMolecularTypeEntries = new ArrayList<MolecularTypeEntry>(rule.getReactantMolecularTypeEntries());
		ArrayList<MolecularTypeEntry> unmatchedProductMolecularTypeEntries = new ArrayList<MolecularTypeEntry>(rule.getProductMolecularTypeEntries());
		//
		// compute molecular type mappings (must be unique)
		//
		for (MolecularTypeEntry reactantMTE : rule.getReactantMolecularTypeEntries()){
			for (MolecularTypeEntry productMTE : rule.getProductMolecularTypeEntries()){
				if (reactantMTE.matches(productMTE)){
					report.addMapping(reactantMTE,productMTE);
					unmatchedReactantMolecularTypeEntries.remove(reactantMTE);
					unmatchedProductMolecularTypeEntries.remove(productMTE);
				}
			}
		}
		
		//
		// print unmatched entries
		//
		for (MolecularTypeEntry mte : unmatchedReactantMolecularTypeEntries){
			System.out.println("unmatched reactant "+getID(mte));
		}
		for (MolecularTypeEntry mte : unmatchedProductMolecularTypeEntries){
			System.out.println("unmatched product "+getID(mte));
		}
		//
		// verify unique match and print matches
		//
		for (MolecularTypeEntry reactantMTE : report.getMappedReactantMolecules()){
			List<MolecularTypeEntry> matchingProductMTEs = report.getMappedProductMolecules(reactantMTE);
			if (matchingProductMTEs.size() > 1){
				throw new RuntimeException("reactant molecular pattern id: "+getID(reactantMTE)+" matched more than one product pattern "+matchingProductMTEs);
			}else{
//				System.out.println("matched sourceID="+getID(reactantMTE)+", targetID="+getID(matchingProductMTEs.get(0)));
			}
		}
		
		//
		// find all component mappings (looking in the matched molecules).
		//
		for (MolecularTypeEntry mappedReactantMolecularTypeEntry : report.getMappedReactantMolecules()){
			List<MolecularTypeEntry> mappedProductMolecularTypeList = report.getMappedProductMolecules(mappedReactantMolecularTypeEntry);
			if (mappedProductMolecularTypeList.size() != 1){
				// if not checked earlier
				throw new RuntimeException("reactant molecular pattern id: "+getID(mappedReactantMolecularTypeEntry)+" matched more than one product pattern "+mappedProductMolecularTypeList);
			}
			MolecularTypeEntry mappedProductMolecularTypeEntry = mappedProductMolecularTypeList.get(0);
			List<? extends MolecularComponentEntry> reactantMolecularComponents = mappedReactantMolecularTypeEntry.getMolecularComponentEntries();
			List<? extends MolecularComponentEntry> productMolecularComponents = mappedProductMolecularTypeEntry.getMolecularComponentEntries();
			if (reactantMolecularComponents.size() != productMolecularComponents.size()){
				throw new RuntimeException("different number of molecular components for mapped molecules");
			}
			for (int componentIndex=0; componentIndex<reactantMolecularComponents.size(); componentIndex++){
				MolecularComponentEntry reactantComponentEntry = reactantMolecularComponents.get(componentIndex);
				MolecularComponentEntry productComponentEntry = productMolecularComponents.get(componentIndex);
				boolean trivial = true;		// the pairs of trivial components (no bond, any state) must not be added to the map
				if(!reactantComponentEntry.isBondPossible()) {
					trivial = false;
				}
				if(reactantComponentEntry.getExplicitState() != null) {
					trivial = false;
				}
				if(!productComponentEntry.isBondPossible()) {
					trivial = false;
				}
				if(productComponentEntry.getExplicitState() != null) {
					trivial = false;
				}
				if(trivial == false) {
					report.map(reactantComponentEntry, productComponentEntry);
				}
			}
		}
		
		//
		// all the unmatched reactant molecules also must be mapped! (null target)
		//
		for (MolecularTypeEntry mte : unmatchedReactantMolecularTypeEntries){
			List<? extends MolecularComponentEntry> mceList = mte.getMolecularComponentEntries();
			report.addMapping(mte,null);
			for (MolecularComponentEntry mce : mceList){
				boolean trivial = true;		// the pairs of trivial components (no bond, any state) must not be added to the map
				if(!mce.isBondPossible()) {
					trivial = false;
				}
				if(mce.getExplicitState() != null) {
					trivial = false;
				}
				if(trivial == false) {
					report.map(mce, null);
				}
			}
		}

		
		//
		// go through component mappings and look for state changes (and verify unique mapping).
		//
		for (MolecularComponentEntry reactantComponentEntry : report.getMappedReactantComponents()){
			MolecularComponentEntry productComponentEntry = report.getMappedProductComponent(reactantComponentEntry);
			if(productComponentEntry == null) {
				continue;	// if a reactant is getting destroyed we don't generate operation
			}
			String reactantState = reactantComponentEntry.getExplicitState();
			String productState = productComponentEntry.getExplicitState();
			if (reactantState != null || productState != null){
				if (reactantState == null || productState == null){
					String errMsg = "reactant state = "+reactantState+" and product state = "+productState+", not allowed, null not allowed.";
					if (bThrowExceptions){
						throw new RuntimeException(errMsg);
					}else{
						report.addError(errMsg);
						continue;
					}
				}
				if (!reactantState.equals(productState)){
					report.addOperation(new RuleAnalysisReport.ChangeStateOperation(reactantComponentEntry, productComponentEntry, productState));
				}
			}
		}

		
		//
		// find unmatched reactant bonds and unmatched product bonds.
		// note that the two "unmatched" arrays are initialized differently.
		// we add unmatched reactant bonds to "unmatchedReactantBond"
		// we remove matched product bonds from "unmatchedProductBond"
		//
		ArrayList<ReactantBondEntry> unmatchedReactantBonds = new ArrayList<ReactantBondEntry>();
		ArrayList<ProductBondEntry> unmatchedProductBonds = new ArrayList<ProductBondEntry>(rule.getProductBondEntries());
		for (ReactantBondEntry reactantBondEntry : rule.getReactantBondEntries()){
			//
			// find corresponding product components
			//
			MolecularComponentEntry productComponent1 = report.getMapping(reactantBondEntry.reactantComponent1);
			MolecularComponentEntry productComponent2 = report.getMapping(reactantBondEntry.reactantComponent2);
			
			if (productComponent1 == null || productComponent2 == null || !productComponent1.isBoundTo(productComponent2)){
				// match not found
				unmatchedReactantBonds.add(reactantBondEntry);
			}else{
				// match found
				ProductBondEntry matchingProductBondEntry = null;
				for (ProductBondEntry productBondEntry : rule.getProductBondEntries()){
					if ((productBondEntry.productComponent1 == productComponent1 && productBondEntry.productComponent2 == productComponent2) ||
						(productBondEntry.productComponent1 == productComponent2 && productBondEntry.productComponent2 == productComponent1)){
						matchingProductBondEntry = productBondEntry;
						break;
					}
				}
				if (matchingProductBondEntry != null){
					unmatchedProductBonds.remove(matchingProductBondEntry);
				}
			}
		}
		
		//
		// add AddBond operations
		//
		for (ProductBondEntry addedProductBond : unmatchedProductBonds){
			//
			// addBonds are initially "ProductBondEntry" objects ... have to create the corresponding "ReactantBondEntry".
			//
			MolecularComponentEntry reactantComponent1 = report.getReactantComponentEntry(addedProductBond.productComponent1);
			MolecularComponentEntry reactantComponent2 = report.getReactantComponentEntry(addedProductBond.productComponent2);
			if(reactantComponent1 == null || reactantComponent2 == null) {
				// need to find out what's causing this, for now we just catch it
				// this is caused by AB -> AC
				String errMsg = "Null reactant component(s) for bond entry while generating an addBond operation";
				if (bThrowExceptions){
					throw new RuntimeException(errMsg);
				}else{
					report.addError(errMsg);
					continue;
				}
			}
			ReactantBondEntry addedReactantBond = new ReactantBondEntry(reactantComponent1, reactantComponent2);
			report.addOperation(new RuleAnalysisReport.AddBondOperation(addedReactantBond));
		}
		
		//
		// add AddMolecularType (unmatched product molecule)
		//
		for (MolecularTypeEntry unmatchedProductMoleculeEntry : unmatchedProductMolecularTypeEntries){
			report.addOperation(new RuleAnalysisReport.AddMolecularTypeOperation(unmatchedProductMoleculeEntry));
		}
		
		//
		// delete entire reactant pattern if all molecules in it are unmapped
		//
		for (ParticipantEntry reactantEntry : rule.getReactantEntries()){
			
			int nbrOfMaches = reactantEntry.getMolecularTypeEntries().size();
			for (MolecularTypeEntry moleculeEntry : reactantEntry.getMolecularTypeEntries()){
				if(unmatchedReactantMolecularTypeEntries.contains(moleculeEntry)) {
					nbrOfMaches--;
				}
			}
			
			if(nbrOfMaches == 0) {		// all molecular type patterns of this reactant are being removed
				//
				// remove species pattern (and disregard all unmatched bonds and molecules belonging to that species pattern)
				//
				//
				report.addOperation(new RuleAnalysisReport.DeleteParticipantOperation(reactantEntry));
				for (MolecularTypeEntry moleculesEntry : reactantEntry.getMolecularTypeEntries()){
					unmatchedReactantMolecularTypeEntries.remove(moleculesEntry);
					for (ReactantBondEntry reactantBond : rule.getReactantBondEntries()){
						if ((reactantBond.reactantComponent1.getMolecularTypeEntry().getParticipantEntry() == reactantEntry) &&
							(reactantBond.reactantComponent2.getMolecularTypeEntry().getParticipantEntry() == reactantEntry)){
							unmatchedReactantBonds.remove(reactantBond);
						}
					}
				}
			}
		}
		
		
		//
		// delete molecule
		// first we build a set of all the molecular type IDs getting deleted and then create the delete bond operations
		//
		List<String> deletedMolecules = new ArrayList<String>();
		for (MolecularTypeEntry unmatchedMolecularTypeEntry : unmatchedReactantMolecularTypeEntries){
			deletedMolecules.add(getID(unmatchedMolecularTypeEntry));
			report.addOperation(new RuleAnalysisReport.DeleteMolecularTypeOperation(unmatchedMolecularTypeEntry));
		}
		//
		// add DeleteBond operations
		// we make sure to skip any bond for which the molecule at EITHER end gets deleted
		//
		for (ReactantBondEntry removedBond : unmatchedReactantBonds){
			String component1id = getID(removedBond.reactantComponent1);
			String molecule1id = component1id.substring(0, component1id.lastIndexOf("_"));
			String component2id = getID(removedBond.reactantComponent2);
			String molecule2id = component2id.substring(0, component2id.lastIndexOf("_"));
			
			if(deletedMolecules.contains(molecule1id) || deletedMolecules.contains(molecule2id)) {
				;	// do nothing if at least one of the 2 molecules of the bond gets deleted
			} else {
				report.addOperation(new RuleAnalysisReport.DeleteBondOperation(removedBond));
			}
		}
		return report;
	}

	public static String getID(RuleEntry ruleEntry){
		int ruleIndex = ruleEntry.getRuleIndex();
		return "RR"+(ruleIndex+INDEX_OFFSET);
	}	

	public static String getID(ParticipantEntry participantEntry){
		int participantIndex = participantEntry.getParticipantIndex();
		if (participantEntry.getParticipantType() == ParticipantType.Reactant){
			return getID(participantEntry.getRule())+"_RP"+(participantIndex+INDEX_OFFSET);
		}else{
			return getID(participantEntry.getRule())+"_PP"+(participantIndex+INDEX_OFFSET);
		}
	}	

	public static String getID(MolecularTypeEntry mte){
		int molecularTypeIndex = mte.getMoleculeIndex();
		return getID(mte.getParticipantEntry())+"_"+"M"+(molecularTypeIndex+INDEX_OFFSET);
	}
	
	public static String getID(MolecularComponentEntry mce){
		int componentIndex = mce.getComponentIndex();
		return getID(mce.getMolecularTypeEntry())+"_"+"C"+(componentIndex+INDEX_OFFSET);
	}
	
	public static Element getNFSimXML(RuleEntry rule, RuleAnalysisReport report){
		Element root = new Element("ReactionRule");
		root.setAttribute("id",getID(rule));
		root.setAttribute("name",rule.getRuleName());
		root.setAttribute("symmetry_factor",""+report.getSymmetryFactor());
		
		Element listOfReactantPatterns = new Element("ListOfReactantPatterns");
		root.addContent(listOfReactantPatterns);
		for (ParticipantEntry reactantEntry : rule.getReactantEntries()){
			Element reactantElement = getParticipantEntry(rule,reactantEntry);
			listOfReactantPatterns.addContent(reactantElement);
		}
		if(rule.getReactantEntries().isEmpty()) {
			listOfReactantPatterns.addContent(" ");	// we want <ListOfReactantPatterns> </ListOfReactantPatterns> rather than <ListOfReactantPatterns/>
		}
		
		Element listOfProductPatterns = new Element("ListOfProductPatterns");
		root.addContent(listOfProductPatterns);
		for (ParticipantEntry productEntry : rule.getProductEntries()){
			Element productElement = getParticipantEntry(rule,productEntry);
			listOfProductPatterns.addContent(productElement);
		}
		if(rule.getProductEntries().isEmpty()) {
			listOfProductPatterns.addContent(" ");	// we want <ListOfProductPatterns> </ListOfProductPatterns> rather than <ListOfProductPatterns/>
		}

		
//		Element rateLaw = new Element("RateLaw");
//		root.addContent(rateLaw);
//		Element listOfRateConstants = new Element("ListOfRateConstants");
//		rateLaw.addContent(listOfRateConstants);
//		Element rateConstant = new Element("RateConstant");
//		listOfRateConstants.addContent(rateConstant);
//		rateConstant.setAttribute("value",rule.getForwardRateConstantName());
		
		// we build a map of strings with the source ID as keys and target ID as content
		Map<String, String> idMap = new LinkedHashMap<String, String>();
		for (MolecularTypeEntry ractantMolecule : report.getMappedReactantMolecules()){
			MolecularTypeEntry productMolecule = report.getMappedProductMolecules(ractantMolecule).get(0);
			if(productMolecule == null) {
				idMap.put(getID(ractantMolecule), null);
			} else {
				idMap.put(getID(ractantMolecule), getID(productMolecule));
			}
			for (MolecularComponentEntry reactantComponent : ractantMolecule.getMolecularComponentEntries()){
				MolecularComponentEntry productComponent = report.getMappedProductComponent(reactantComponent);
				if(productComponent == null) {
					idMap.put(getID(reactantComponent), null);
				} else {
					idMap.put(getID(reactantComponent), getID(productComponent));
				}
			}
		}
		Element map = new Element("Map");
		root.addContent(map);
		SortedSet<String> keys = new TreeSet<String>(idMap.keySet());	// we create the map sorted by sourceID
		for (String sourceID : keys) {
			String targetID = idMap.get(sourceID);
			Element mapItem = new Element("MapItem");
			map.addContent(mapItem);
			mapItem.setAttribute("sourceID", sourceID);
			if(targetID != null) {
				mapItem.setAttribute("targetID", targetID);
			}
		}
		if(keys.isEmpty()) {
			map.addContent(" ");	// we want <Map> </Map> rather than <Map/>
		}
	
		
//		Element map = new Element("Map");
//		root.addContent(map);
//		for (MolecularTypeEntry mappedReactantMolecule : report.getMappedReactantMolecules()){
//			Element moleculeMapEntry = new Element("MapItem");
//			map.addContent(moleculeMapEntry);
//			MolecularTypeEntry productMolecule = report.getMappedProductMolecules(mappedReactantMolecule).get(0);
//			moleculeMapEntry.setAttribute("sourceID",getID(mappedReactantMolecule));
//			if(productMolecule != null) {
//				moleculeMapEntry.setAttribute("targetID",getID(productMolecule));
//			}
//			for (MolecularComponentEntry reactantComponent : mappedReactantMolecule.getMolecularComponentEntries()){
//				Element componentMapEntry = new Element("MapItem");
//				map.addContent(componentMapEntry);
//				MolecularComponentEntry productComponent = report.getMappedProductComponent(reactantComponent);
//				componentMapEntry.setAttribute("sourceID",getID(reactantComponent));
//				if(productComponent != null) {
//					componentMapEntry.setAttribute("targetID",getID(productComponent));
//				}
//			}
//		}
		
		Element listOfOperations = new Element("ListOfOperations");
		root.addContent(listOfOperations);
		if(report.getOperations().isEmpty()) {
			listOfOperations.addContent(" ");	// we want <ListOfOperations> </ListOfOperations> rather than <ListOfOperations/>
		}
		
		for (Operation op : report.getOperations()){
			if (op instanceof ChangeStateOperation){
				ChangeStateOperation changeStateOp = (ChangeStateOperation)op;
				Element changeState = new Element("StateChange");
				listOfOperations.addContent(changeState);
				changeState.setAttribute("site",getID(changeStateOp.reactantComponentEntry));
				changeState.setAttribute("finalState",changeStateOp.newState);
			}
		}
		for (Operation op : report.getOperations()){
			if (op instanceof DeleteBondOperation){
				DeleteBondOperation deleteBondOp = (DeleteBondOperation)op;
				Element deleteBond = new Element("DeleteBond");
				listOfOperations.addContent(deleteBond);
				deleteBond.setAttribute("site1",getID(deleteBondOp.removedBondEntry.reactantComponent1));
				deleteBond.setAttribute("site2",getID(deleteBondOp.removedBondEntry.reactantComponent2));
			}
		}
		for (Operation op : report.getOperations()){
			if (op instanceof AddBondOperation){
				AddBondOperation addBondOp = (AddBondOperation)op;
				Element addBond = new Element("AddBond");
				listOfOperations.addContent(addBond);
				addBond.setAttribute("site1",getID(addBondOp.addedProductBondEntry.reactantComponent1));
				addBond.setAttribute("site2",getID(addBondOp.addedProductBondEntry.reactantComponent2));
			}
		}
		for (Operation op : report.getOperations()){
			if (op instanceof AddMolecularTypeOperation){
				AddMolecularTypeOperation addMoleculeOp = (AddMolecularTypeOperation)op;
				Element addMolecule = new Element("Add");
				listOfOperations.addContent(addMolecule);
				addMolecule.setAttribute("id",getID(addMoleculeOp.unmatchedProductMoleculeEntry));
			}
		}
		for (Operation op : report.getOperations()){
			if (op instanceof DeleteMolecularTypeOperation){
				DeleteMolecularTypeOperation deleteMolecule = (DeleteMolecularTypeOperation)op;
				Element delete = new Element("Delete");
				listOfOperations.addContent(delete);
				delete.setAttribute("id",getID(deleteMolecule.removedReactantMolecularEntry));
				delete.setAttribute("DeleteMolecules","1");
			}
		}
		for (Operation op : report.getOperations()){
			if (op instanceof DeleteParticipantOperation){
				DeleteParticipantOperation deleteParticipant = (DeleteParticipantOperation)op;
				Element delete = new Element("Delete");
				listOfOperations.addContent(delete);
				delete.setAttribute("id",getID(deleteParticipant.removedParticipantEntry));
				delete.setAttribute("DeleteMolecules","0");
			}
		}
		return root;
	}

	private static Element getParticipantEntry(RuleEntry rule, ParticipantEntry participant) {
		Element root = null;
		if (participant.getParticipantType() == ParticipantType.Reactant){
			root = new Element("ReactantPattern");
		}else{
			root = new Element("ProductPattern");
		}
		root.setAttribute("id", getID(participant));
		Element listOfMolecules = new Element("ListOfMolecules");
		root.addContent(listOfMolecules);
		for (MolecularTypeEntry molecule : participant.getMolecularTypeEntries()){
			Element moleculeElement = new Element("Molecule");
			listOfMolecules.addContent(moleculeElement);
			moleculeElement.setAttribute("id",getID(molecule));
			moleculeElement.setAttribute("name",molecule.getMolecularTypeName());
			if (molecule.getMatchLabel()!=null){
				moleculeElement.setAttribute("label",molecule.getMatchLabel());
			}
			if (molecule.getMolecularComponentEntries().size()>0){
				Element listOfComponents = new Element("ListOfComponents");
				moleculeElement.addContent(listOfComponents);
				for (MolecularComponentEntry component : molecule.getMolecularComponentEntries()){
					boolean bStateSpecified = false;
					boolean bBondSpecified = false;
					Element componentElement = new Element("Component");
					componentElement.setAttribute("id",getID(component));
					componentElement.setAttribute("name",component.getMolecularComponentName());
					String state = component.getExplicitState();
					if (state != null){
						bStateSpecified = true;
						componentElement.setAttribute("state",state);
					}
					if(component.isBondExists()) {
						componentElement.setAttribute("numberOfBonds","+");
						bBondSpecified = true;
					} else if (component.isBondPossible()){
						componentElement.setAttribute("numberOfBonds","?");
					} else if (component.hasBond()){
						componentElement.setAttribute("numberOfBonds","1");
						bBondSpecified = true;
					} else {
						componentElement.setAttribute("numberOfBonds","0");
						bBondSpecified = true;
					}
					if (bStateSpecified || bBondSpecified){
						listOfComponents.addContent(componentElement);
					}
				}
			}
		}
		Element listOfBonds = new Element("ListOfBonds");
		boolean bAnyBonds = false;
		if (participant.getParticipantType() == ParticipantType.Reactant){
			int count = 0;
			Map<String,String> bondSiteMap = new HashMap<String, String>();
			for (ReactantBondEntry bond : rule.getReactantBondEntries()){
				if (bond.reactantComponent1.getMolecularTypeEntry().getParticipantEntry() == participant){
					if(bond.reactantComponent1 == null || bond.reactantComponent2 == null) {
						throw new RuntimeException("Bond site(s) are null when trying to build the bond map.");
					}
					bondSiteMap.put(getID(bond.reactantComponent1), getID(bond.reactantComponent2));
				}
			}
			SortedSet<String> keys = new TreeSet<String>(bondSiteMap.keySet());
			for (String key : keys) { 
				String value = bondSiteMap.get(key);
				Element bondElement = new Element("Bond");
				listOfBonds.addContent(bondElement);
				bondElement.setAttribute("id",getID(participant)+"_B"+(count+INDEX_OFFSET));
				bondElement.setAttribute("site1", key);
				bondElement.setAttribute("site2", value);
				bAnyBonds = true;
				count++;
			}
		}else{
			int count = 0;
			Map<String,String> bondSiteMap = new HashMap<String, String>();
			for (ProductBondEntry bond : rule.getProductBondEntries()){
				if (bond.productComponent1.getMolecularTypeEntry().getParticipantEntry() == participant){
					if(bond.productComponent1 == null || bond.productComponent2 == null) {
						throw new RuntimeException("Bond site(s) are null when trying to build the bond map.");
					}
					bondSiteMap.put(getID(bond.productComponent1), getID(bond.productComponent2));
				}
			}
			SortedSet<String> keys = new TreeSet<String>(bondSiteMap.keySet());
			for (String key : keys) { 
				String value = bondSiteMap.get(key);
				Element bondElement = new Element("Bond");
				listOfBonds.addContent(bondElement);
				bondElement.setAttribute("id",getID(participant)+"_B"+(count+INDEX_OFFSET));
				bondElement.setAttribute("site1", key);
				bondElement.setAttribute("site2", value);
				bAnyBonds = true;
				count++;
			}
		}
		if (bAnyBonds){
			root.addContent(listOfBonds);
		}
		return root;
	}
	
}
