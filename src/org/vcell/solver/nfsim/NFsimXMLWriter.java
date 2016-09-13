package org.vcell.solver.nfsim;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.jdom.Comment;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.vcell.model.rbm.RbmUtils;
import org.vcell.model.rbm.RuleAnalysis;
import org.vcell.model.rbm.RuleAnalysisReport;
import org.vcell.util.BeanUtils;
import org.vcell.util.PropertyLoader;

import cbit.vcell.mapping.RulebasedTransformer;
import cbit.vcell.math.Action;
import cbit.vcell.math.CompartmentSubDomain;
import cbit.vcell.math.Constant;
import cbit.vcell.math.Function;
import cbit.vcell.math.JumpProcessRateDefinition;
import cbit.vcell.math.MacroscopicRateConstant;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.MathException;
import cbit.vcell.math.MathRuleFactory;
import cbit.vcell.math.MathRuleFactory.MathRuleEntry;
import cbit.vcell.math.ParticleComponentStateDefinition;
import cbit.vcell.math.ParticleComponentStatePattern;
import cbit.vcell.math.ParticleJumpProcess;
import cbit.vcell.math.ParticleMolecularComponent;
import cbit.vcell.math.ParticleMolecularComponentPattern;
import cbit.vcell.math.ParticleMolecularComponentPattern.ParticleBondType;
import cbit.vcell.math.ParticleMolecularType;
import cbit.vcell.math.ParticleMolecularTypePattern;
import cbit.vcell.math.ParticleObservable;
import cbit.vcell.math.ParticleProperties;
import cbit.vcell.math.ParticleProperties.ParticleInitialCondition;
import cbit.vcell.math.ParticleProperties.ParticleInitialConditionCount;
import cbit.vcell.math.ParticleSpeciesPattern;
import cbit.vcell.math.Variable;
import cbit.vcell.math.VolumeParticleSpeciesPattern;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.NFsimSimulationOptions;
import cbit.vcell.solver.SimulationSymbolTable;
import cbit.vcell.solver.SolverException;
//import org.jdom.output.Format;

public class NFsimXMLWriter {
	
	public static class Bond {
		public final String bondID;		// this is the ID we show in the xml file, format RR0_RP0_B0
//		public final int bondId;		// TODO: get rid of this
		public final ParticleSpeciesPattern speciesPattern;
		
//		Bond(ParticleSpeciesPattern speciesPattern, int bondId){
//			this("", speciesPattern, bondId);
//		}
		Bond(String bondID, ParticleSpeciesPattern speciesPattern){
			this.bondID = bondID;
			this.speciesPattern = speciesPattern;
//			this.bondId = bondId;
		}
		@Override
		public int hashCode() {
			return speciesPattern.hashCode() + bondID.hashCode();
		}
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Bond){
				Bond other = (Bond)obj;
				if(!bondID.equals(other.bondID)) {
					return false;
				}
				if (speciesPattern != other.speciesPattern){
					return false;
				}
				return true;
			}
			return false;
		}
		public String getId(){
			return bondID;
		}
	}

	public static class BondSites {
		public String component1 = "";
		public String component2 = "";
		
		public BondSites() {
			this.component1 = "";
			this.component2 = "";
		}
		public BondSites(String component1, String component2) {
			this.component1 = component1;
			this.component2 = component2;
		}
		
		@Override
		public boolean equals(Object o) {
			if(!(o instanceof BondSites)) {
				return false;
			}
			BondSites that = (BondSites)o;
			if(component1.equals(that.component1) && component2.equals(component2)) {
				return true;
			}
			return false;
		}
		static public String extractMoleculeId(String componentID) {		// we extract the molecule id (ex RR1_RP1_M2) from the element id (ex RR1_RP1_M2_C1)
			int pos = componentID.lastIndexOf("_C");
			if(pos == -1) {
				throw new RuntimeException("improper id " + componentID + " for component " + componentID); 
			}
			return componentID.substring(0, pos);
		}
	}
	
	// The next 3 classes are used to build the mapping between reactants and products of the current reaction
	public static class MolecularTypeOfReactionParticipant {
		public MolecularTypeOfReactionParticipant(String moleculeName, String elementID, String matchLabel) {
			this.moleculeName = moleculeName;
			this.elementID = elementID;
			this.matchLabel = matchLabel;
		}
		String moleculeName;
		String elementID;		// molecule element ID, for example RR0_RP3_M0
		String matchLabel;

		@Override
		public int hashCode() {
			return moleculeName.hashCode() + matchLabel.hashCode();
		}
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof MolecularTypeOfReactionParticipant){
				MolecularTypeOfReactionParticipant other = (MolecularTypeOfReactionParticipant)obj;
				if(!moleculeName.equals(other.moleculeName)) {
					return false;
				}
				if (!matchLabel.equals(other.matchLabel)){
					return false;
				}
				if (!elementID.equals(other.elementID)){
					return false;
				}
				return true;
			}
			return false;
		}
		public boolean find(Object obj) {
			if (obj instanceof MolecularTypeOfReactionParticipant){
				MolecularTypeOfReactionParticipant other = (MolecularTypeOfReactionParticipant)obj;
				if(!moleculeName.equals(other.moleculeName)) {
					return false;
				}
				if(!matchLabel.equals(other.matchLabel)) {
					return false;
				}
				return true;
			}
			return false;
		}
		public String extractReactantPatternId() {		// we extract the reactant pattern from the element id (ex RR0_RP3)
			int pos = elementID.lastIndexOf("_M");
			if(pos == -1) {
				throw new RuntimeException("improper id " + elementID + " for molecule " + moleculeName); 
			}
			return elementID.substring(0, pos);
		}
	}
	public static class ComponentOfMolecularTypeOfReactionParticipant {
		public ComponentOfMolecularTypeOfReactionParticipant(String moleculeName, String matchLabel, String componentName, String elementID, String state) {
			this.moleculeName = moleculeName;
			this.matchLabel = matchLabel;
			this.componentName = componentName;
			this.elementID = elementID;
			this.state = state;
		}
		String moleculeName;
		String matchLabel;
		String componentName;
		String elementID;		// component element ID, for example RR2_PP0_M1_C0
		String state;

		@Override
		public int hashCode() {
			return moleculeName.hashCode() + matchLabel.hashCode() + componentName.hashCode() + elementID.hashCode() + state.hashCode();
		}
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof ComponentOfMolecularTypeOfReactionParticipant){
				ComponentOfMolecularTypeOfReactionParticipant other = (ComponentOfMolecularTypeOfReactionParticipant)obj;
				if(!moleculeName.equals(other.moleculeName)) {
					return false;
				}
				if (!matchLabel.equals(other.matchLabel)){
					return false;
				}
				if(!componentName.equals(other.componentName)) {
					return false;
				}
				if (!elementID.equals(other.elementID)){
					return false;
				}
				if (!state.equals(other.state)){
					return false;
				}
				return true;
			}
			return false;
		}
		public boolean find(Object obj) {
			if (obj instanceof ComponentOfMolecularTypeOfReactionParticipant){
				ComponentOfMolecularTypeOfReactionParticipant other = (ComponentOfMolecularTypeOfReactionParticipant)obj;
				if(!moleculeName.equals(other.moleculeName)) {
					return false;
				}
				if (!matchLabel.equals(other.matchLabel)){
					return false;
				}
				if(!componentName.equals(other.componentName)) {
					return false;
				}
				return true;
			}
			return false;
		}
	}
	public static class MappingOfReactionParticipants {
		private String reactantElementID;
		private String productElementID;
		private String componentFinalState = "";
		public MappingOfReactionParticipants(String reactantElementID, String productElementID, String componentFinalState) {
			this.reactantElementID = reactantElementID;			// used in "Map"
			this.productElementID = productElementID;			// used in "Map"
			this.componentFinalState = componentFinalState;		// used in "ListOfOperations"
		}
		public static String findMatchingReactant(String productElementID, ArrayList<MappingOfReactionParticipants> map) {
			for(Iterator<MappingOfReactionParticipants> it = map.iterator(); it.hasNext();) {
				MappingOfReactionParticipants m = it.next();
				if(m.productElementID.equals(productElementID)) {
					return m.reactantElementID;
				}
			}
			return productElementID;	// if no match we return the input string, cross our fingers and pray for the best
		}
	}
	static ArrayList<MappingOfReactionParticipants> currentMappingOfReactionParticipants = new ArrayList<MappingOfReactionParticipants>();
	static HashSet<BondSites> patternProductBondSites = new HashSet<BondSites>();	// TODO: these are being reinitialized for each product pattern which is correct for the ListOfBonds (1 list per pattern)
																			// but it may be wrong for the list of operations (which is 1 list per reaction rule)
																			// we may want to rename these to patternProductBondSites and to keep adding them to a new reactionProductBondSites and
																			// use those for the LisOfOperations
	static HashSet<BondSites> patternReactantBondSites = new HashSet<BondSites>();
	static HashSet<BondSites> reactionProductBondSites = new HashSet<BondSites>();
	static HashSet<BondSites> reactionReactantBondSites = new HashSet<BondSites>();

	public static Element writeNFsimXML(SimulationTask origSimTask, long randomSeed, NFsimSimulationOptions nfsimSimulationOptions, boolean bUseLocationMarks) throws SolverException {
		try {
			System.out.println("VCML ORIGINAL .... START\n"+origSimTask.getSimulation().getMathDescription().getVCML_database()+"\nVCML ORIGINAL .... END\n====================\n");
		} catch (MathException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		SimulationTask clonedSimTask = null;
		try {
			clonedSimTask = (SimulationTask) BeanUtils.cloneSerializable(origSimTask);
		}catch (Exception eee){
			throw new SolverException("failed to clone mathDescription while preparing NFSim input: "+eee.getMessage(), eee);
		}
		MathDescription clonedMathDesc = clonedSimTask.getSimulation().getMathDescription();
		if (bUseLocationMarks){
			try {
	
				//
				// get list of Compartment Names (stored in locations).
				//
				ArrayList<String> locations = new ArrayList<String>();
				Enumeration<Variable> varEnum = clonedMathDesc.getVariables();
				ArrayList<VolumeParticleSpeciesPattern> volumeParticleSpeciesPatterns = new ArrayList<VolumeParticleSpeciesPattern>();
				while (varEnum.hasMoreElements()){
					Variable var = varEnum.nextElement();
					if (var instanceof VolumeParticleSpeciesPattern){
						VolumeParticleSpeciesPattern speciesPattern = (VolumeParticleSpeciesPattern)var;
						if (!locations.contains(speciesPattern.getLocationName())){
							locations.add(speciesPattern.getLocationName());
						}
						volumeParticleSpeciesPatterns.add(speciesPattern);
					}
				}
				
				//
				// add location site and mark site to each ParticleMolecularType (definition of the molecular type)
				//
				for (ParticleMolecularType particleMolecularType : clonedMathDesc.getParticleMolecularTypes()){
					String pmcLocationName = RbmUtils.SiteStruct;
					String pmcLocationId = particleMolecularType.getName() + "_" + RbmUtils.SiteStruct;
					ParticleMolecularComponent locationComponent = new ParticleMolecularComponent(pmcLocationId, pmcLocationName);
					for (String location : locations){
						locationComponent.addComponentStateDefinition(new ParticleComponentStateDefinition(location));
					}
					particleMolecularType.insertMolecularComponent(0,locationComponent);
					
					String pmcMarkName = RbmUtils.SiteProduct;
					String pmcMarkId = particleMolecularType.getName() + "_" + RbmUtils.SiteProduct;
					ParticleMolecularComponent markComponent = new ParticleMolecularComponent(pmcMarkId, pmcMarkName);
					markComponent.addComponentStateDefinition(new ParticleComponentStateDefinition("0"));
					markComponent.addComponentStateDefinition(new ParticleComponentStateDefinition("1"));
					particleMolecularType.insertMolecularComponent(1,markComponent);
				}
				
				//
				// for each VolumeParticleSpeciesPattern, add components for location and mark to Molecular Type Pattern
				//
				for (VolumeParticleSpeciesPattern speciesPattern : volumeParticleSpeciesPatterns){
					for (ParticleMolecularTypePattern molTypePattern : speciesPattern.getParticleMolecularTypePatterns()){
						//
						// add location component to pattern ... state=<location>
						//
						{
						final ParticleMolecularComponent locationComponentDefinition = molTypePattern.getMolecularType().getComponentList().get(0);
						ParticleMolecularComponentPattern locationPattern = new ParticleMolecularComponentPattern(locationComponentDefinition);
						ParticleComponentStateDefinition locationStateDefinition = null;
						for (ParticleComponentStateDefinition stateDef : locationComponentDefinition.getComponentStateDefinitions()){
							if (stateDef.getName().equals(speciesPattern.getLocationName())){
								locationStateDefinition = stateDef;
							}
						}
						ParticleComponentStatePattern locationStatePattern = new ParticleComponentStatePattern(locationStateDefinition);
						locationPattern.setComponentStatePattern(locationStatePattern);
						locationPattern.setBondType(ParticleBondType.None);
						locationPattern.setBondId(-1);
						molTypePattern.insertMolecularComponentPattern(0,locationPattern);
						}
						
						//
						// add mark component to pattern ... state="0" (for observables and reactants ... later we will clone and use "1" for products).
						{
						final ParticleMolecularComponent markComponentDefinition = molTypePattern.getMolecularType().getComponentList().get(1);
						ParticleMolecularComponentPattern markPattern = new ParticleMolecularComponentPattern(markComponentDefinition);
						final int clearStateIndex = 0;
						final int setStateIndex = 1;
						ParticleComponentStateDefinition markStateClearedDefinition = markComponentDefinition.getComponentStateDefinitions().get(clearStateIndex);
						ParticleComponentStatePattern markStatePattern = new ParticleComponentStatePattern(markStateClearedDefinition);
						markPattern.setComponentStatePattern(markStatePattern);
						markPattern.setBondType(ParticleBondType.None);
						markPattern.setBondId(-1);
						molTypePattern.insertMolecularComponentPattern(1,markPattern);
						}
					}
				}
				
				//
				// when processing ParticleJumpProcesses, we add a new "product" species pattern (by cloning the original speciesPattern)
				// and setting the mark site to "1", change name to name+"_PRODUCT", and add to math model if it doesn't already exist. 
				//
				// cloned the "standard" reactant/observable speciesPattern, set the mark for all molecules, and add to mathDesc.
				//
				CompartmentSubDomain subDomain = (CompartmentSubDomain)clonedMathDesc.getSubDomains().nextElement();
				for (ParticleJumpProcess particleJumpProcess : subDomain.getParticleJumpProcesses()){
					for (Action action : particleJumpProcess.getActions()){
						if (action.getOperation().equals(Action.ACTION_CREATE)){
							VolumeParticleSpeciesPattern volumeParticleSpeciesPattern = (VolumeParticleSpeciesPattern)action.getVar();
							String newSpeciesPatternName = volumeParticleSpeciesPattern.getName()+"_"+particleJumpProcess.getName();
							VolumeParticleSpeciesPattern productPattern = new VolumeParticleSpeciesPattern(volumeParticleSpeciesPattern, newSpeciesPatternName);
							//VolumeParticleSpeciesPattern productPattern = (VolumeParticleSpeciesPattern) BeanUtils.cloneSerializable(volumeParticleSpeciesPattern);
							for (ParticleMolecularTypePattern productMolTypePattern : productPattern.getParticleMolecularTypePatterns()){
								ParticleComponentStateDefinition markSet = productMolTypePattern.getMolecularType().getComponentList().get(1).getComponentStateDefinitions().get(1);
								productMolTypePattern.getMolecularComponentPatternList().get(1).setComponentStatePattern(new ParticleComponentStatePattern(markSet));
							}
							System.out.println(productPattern.getName());
							if (clonedMathDesc.getVariable(productPattern.getName())==null){
								clonedMathDesc.addVariable(productPattern);
							}
							action.setVar(productPattern);
						}
					}
				}
				try {
					System.out.println("===============================\n ----------- VCML HACKED .... START\n"+clonedMathDesc.getVCML_database()+"\nVCML HACKED .... END\n====================\n");
				} catch (MathException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}catch (Exception e){
				throw new SolverException("failed to apply location mark transformation: "+e.getMessage(), e);
			}
		}
		Element sbmlElement = new Element("sbml");
		Element modelElement = new Element("model");
		modelElement.setAttribute("id", "nameless");
		
		SimulationSymbolTable simulationSymbolTable = new SimulationSymbolTable(clonedSimTask.getSimulation(), clonedSimTask.getSimulationJob().getJobIndex());
		
		Element listOfParametersElement = getListOfParameters(clonedMathDesc, simulationSymbolTable);
		Element listOfMoleculeTypesElement = getListOfMoleculeTypes(clonedMathDesc);
		Element listOfSpeciesElement = getListOfSpecies(clonedMathDesc,simulationSymbolTable);
		
		CompartmentSubDomain compartmentSubDomain = (CompartmentSubDomain) clonedMathDesc.getSubDomains().nextElement();
		Element listOfReactionRules = new Element("ListOfReactionRules");
		for (int reactionRuleIndex =0; reactionRuleIndex < compartmentSubDomain.getParticleJumpProcesses().size(); reactionRuleIndex++) {
			ParticleJumpProcess particleJumpProcess = compartmentSubDomain.getParticleJumpProcesses().get(reactionRuleIndex);
			
			MathRuleFactory mathRuleFactory = new MathRuleFactory();
			MathRuleEntry rule = mathRuleFactory.createRuleEntry(particleJumpProcess, reactionRuleIndex);
			RuleAnalysisReport report = RuleAnalysis.analyze(rule, true);
			Element reactionRuleElement = RuleAnalysis.getNFSimXML(rule, report); // remember, we have to add RateLaw
			
//			ArrayList<MolecularTypeOfReactionParticipant> currentReactantElementsOfReaction = new ArrayList<MolecularTypeOfReactionParticipant>();
//			ArrayList<ComponentOfMolecularTypeOfReactionParticipant> currentComponentOfReactantElementsOfReaction = new ArrayList<ComponentOfMolecularTypeOfReactionParticipant>();
//			ArrayList<MolecularTypeOfReactionParticipant> currentProductElementsOfReaction = new ArrayList<MolecularTypeOfReactionParticipant>();
//			ArrayList<ComponentOfMolecularTypeOfReactionParticipant> currentComponentOfProductElementsOfReaction = new ArrayList<ComponentOfMolecularTypeOfReactionParticipant>();
//			currentMappingOfReactionParticipants.clear();
//			reactionProductBondSites.clear();
//			reactionReactantBondSites.clear();
//			
//			Element reactionRuleElement = new Element("ReactionRule");
//			String reactionRuleID = "RR" + (reactionRuleIndex + 1);
//			reactionRuleElement.setAttribute("id",reactionRuleID);
//			reactionRuleElement.setAttribute("name",particleJumpProcess.getName());
//			reactionRuleElement.setAttribute("symmetry_factor","1");
//			reactionRule.resolveBonds();
//			
//			ArrayList<VolumeParticleSpeciesPattern> selectedPatterns = new ArrayList<VolumeParticleSpeciesPattern>();
//			for (ParticleVariable particleVariable : particleJumpProcess.getParticleVariables()){
//				if (!(particleVariable instanceof VolumeParticleSpeciesPattern)){
//					throw new SolverException("expecting only "+VolumeParticleSpeciesPattern.class.getSimpleName()+"s for "+ParticleJumpProcess.class.getSimpleName()+" "+particleJumpProcess.getName());
//				}
//				selectedPatterns.add((VolumeParticleSpeciesPattern) particleVariable);
//			}
//			ArrayList<VolumeParticleSpeciesPattern> createdPatterns = new ArrayList<VolumeParticleSpeciesPattern>();
//			HashSet<VolumeParticleSpeciesPattern> destroyedPatterns = new HashSet<VolumeParticleSpeciesPattern>();
//			for (Action action : particleJumpProcess.getActions()){
//				if (!(action.getVar() instanceof VolumeParticleSpeciesPattern)){
//					throw new SolverException("expecting only "+VolumeParticleSpeciesPattern.class.getSimpleName()+"s for "+ParticleJumpProcess.class.getSimpleName()+" "+particleJumpProcess.getName());
//				}
//				if (action.getOperation().equals(Action.ACTION_CREATE)){
//					createdPatterns.add((VolumeParticleSpeciesPattern) action.getVar());
//				}else if (action.getOperation().equals(Action.ACTION_DESTROY)){
//					destroyedPatterns.add((VolumeParticleSpeciesPattern) action.getVar());
//				}else{
//					throw new RuntimeException("unexpected action operation "+action.getOperation()+" for jump process "+particleJumpProcess.getName());
//				}
//			}
//				
//			Element listOfReactantPatternsElement = new Element("ListOfReactantPatterns");
//			for(int reactantPatternIndex=0; reactantPatternIndex < selectedPatterns.size(); reactantPatternIndex++) {
//				VolumeParticleSpeciesPattern reactantSpeciesPattern = selectedPatterns.get(reactantPatternIndex);
//				String reactantPatternID = "RP" + (reactantPatternIndex + 1);
//				patternReactantBondSites.clear();
//				Element reactantPatternElement = getReactionParticipantPattern1(reactionRuleID, reactantPatternID, reactantSpeciesPattern, 
//						currentReactantElementsOfReaction, currentComponentOfReactantElementsOfReaction, "ReactantPattern");
//				listOfReactantPatternsElement.addContent(reactantPatternElement);
//				reactionReactantBondSites.addAll(patternReactantBondSites);
//			}
//			reactionRuleElement.addContent(listOfReactantPatternsElement);
//			
//			Element listOfProductPatternsElement = new Element("ListOfProductPatterns");
//			ArrayList<VolumeParticleSpeciesPattern> productSpeciesPatterns = new ArrayList<VolumeParticleSpeciesPattern>(selectedPatterns);
//			productSpeciesPatterns.removeAll(destroyedPatterns);
//			productSpeciesPatterns.addAll(createdPatterns);
//			// for products, add all "created" species from Actions and all "particles" that are selected but not destroyed
//			for(int productPatternIndex=0; productPatternIndex < productSpeciesPatterns.size(); productPatternIndex++) {
//				VolumeParticleSpeciesPattern productSpeciesPattern = productSpeciesPatterns.get(productPatternIndex);
//				String productPatternID = "PP" + (productPatternIndex + 1);
//				patternProductBondSites.clear();
//				Element productPatternElement = getReactionParticipantPattern1(reactionRuleID, productPatternID, productSpeciesPattern, 
//								currentProductElementsOfReaction, currentComponentOfProductElementsOfReaction, "ProductPattern");
//				listOfProductPatternsElement.addContent(productPatternElement);
//				reactionProductBondSites.addAll(patternProductBondSites);
//			}
//			reactionRuleElement.addContent(listOfProductPatternsElement);
			
	        //  <RateLaw id="RR1_RateLaw" type="Ele" totalrate="0">
	        //    <ListOfRateConstants>
	        //      <RateConstant value="kon"/>
	        //    </ListOfRateConstants>
	        //  </RateLaw>
			Element rateLawElement = new Element("RateLaw");
			rateLawElement.setAttribute("id", RuleAnalysis.getID(rule));
			
			String rateConstantValue = null;
			JumpProcessRateDefinition particleProbabilityRate = particleJumpProcess.getParticleRateDefinition();
			if(particleProbabilityRate.getExpressions().length > 0) {

				JumpProcessRateDefinition particleRateDefinition = particleJumpProcess.getParticleRateDefinition();
				Expression expression = null;
				if (particleRateDefinition instanceof MacroscopicRateConstant){
					expression = ((MacroscopicRateConstant)particleProbabilityRate).getExpression();
				}else{
					throw new SolverException("ParticleRateDefinition type "+particleRateDefinition.getClass().getSimpleName()+" not supported");
				}
				rateConstantValue = expression.infixBng();

				// all rates constants are being flattened and given reserved names
				Expression substitutedValExpr = null;
				try {
					substitutedValExpr = simulationSymbolTable.substituteFunctions(expression);
				}catch (MathException | ExpressionException e){
					e.printStackTrace(System.out);
					throw new SolverException("ParticleJumpProcess "+particleJumpProcess.getName()+" substitution failed : exp = \""+expression.infix()+"\": "+e.getMessage());
				}
				Double value = null;
				try {
					value = substitutedValExpr.evaluateConstant();
					Element parameterElement = new Element("Parameter");
					String id = "K_reserved_" + reactionRuleIndex;
					parameterElement.setAttribute("id", id);
					if (value!=null) {
						parameterElement.setAttribute("type","Constant");
						parameterElement.setAttribute("value",value.toString());
						parameterElement.addContent(new Comment(rateConstantValue));
						rateConstantValue = id;
						listOfParametersElement.addContent(parameterElement);
					}
				}catch (ExpressionException e){
					System.out.println("ParticleJumpProcess "+particleJumpProcess.getName()+" = "+substitutedValExpr.infix()+" does not have a constant value");
				}
			}
			
			if(isFunction(rateConstantValue, clonedMathDesc, simulationSymbolTable)) {
				rateLawElement.setAttribute("type", "Function");
				rateLawElement.setAttribute("totalrate", "0");
				rateLawElement.setAttribute("name", rateConstantValue);

			} else {
				rateLawElement.setAttribute("type", "Ele");
				rateLawElement.setAttribute("totalrate", "0");
				Element listOfRateConstantsElement = new Element("ListOfRateConstants");
				Element rateConstantElement = new Element("RateConstant");
				// System.out.println(" --- " + particleJumpProcess.getParticleRateDefinition().getVCML());
				// System.out.println(" --- " + particleJumpProcess.getParticleRateDefinition().getExpressions());
				if(particleProbabilityRate.getExpressions().length > 0) {
					rateConstantElement.setAttribute("value", rateConstantValue);
				}
				listOfRateConstantsElement.addContent(rateConstantElement);
				rateLawElement.addContent(listOfRateConstantsElement);
			}
			reactionRuleElement.addContent(rateLawElement);
			
//	        //  <Map>
//	        //    <MapItem sourceID="RR1_RP1_M1" targetID="RR1_PP1_M1"/>
//	        //    <MapItem sourceID="RR1_RP1_M1_C1" targetID="RR1_PP1_M1_C1"/>
//	        //    <MapItem sourceID="RR1_RP1_M1_C2" targetID="RR1_PP1_M1_C2"/>
//	        //    <MapItem sourceID="RR1_RP2_M1" targetID="RR1_PP1_M2"/>
//	        //    <MapItem sourceID="RR1_RP2_M1_C1" targetID="RR1_PP1_M2_C1"/>
//	        //  </Map>
//			Element mapElement = new Element("Map");
//			System.out.println("----------------------------------------------------------------------");
//			for(MolecularTypeOfReactionParticipant p : currentReactantElementsOfReaction) {
//				System.out.println(p.moleculeName + ", " + p.elementID);
//			}
//			for(ComponentOfMolecularTypeOfReactionParticipant c : currentComponentOfReactantElementsOfReaction) {
//				System.out.println(c.moleculeName + ", " + c.componentName + ", " + c.elementID);
//			}
//			System.out.println("----------------------------------------------------------------------");
//			for(MolecularTypeOfReactionParticipant p : currentProductElementsOfReaction) {
//				System.out.println(p.moleculeName + ", " + p.elementID);
//			}
//			for(ComponentOfMolecularTypeOfReactionParticipant c : currentComponentOfProductElementsOfReaction) {
//				System.out.println(c.moleculeName + ", " + c.componentName + ", " + c.elementID);
//			}
//			System.out.println("----------------------------------------------------------------------");
//			
//			List<MolecularTypeOfReactionParticipant> cloneOfReactants = new ArrayList<MolecularTypeOfReactionParticipant>(currentReactantElementsOfReaction);
//			List<MolecularTypeOfReactionParticipant> cloneOfProducts = new ArrayList<MolecularTypeOfReactionParticipant>(currentProductElementsOfReaction);
//			for(Iterator<MolecularTypeOfReactionParticipant> itReactant = cloneOfReactants.iterator(); itReactant.hasNext();) {	// participants
//				MolecularTypeOfReactionParticipant reactant = itReactant.next();
//				boolean foundProduct = false;
//				for(Iterator<MolecularTypeOfReactionParticipant> itProduct = cloneOfProducts.iterator(); itProduct.hasNext();) {
//					MolecularTypeOfReactionParticipant product = itProduct.next();
//					if(reactant.find(product)) {
//						MappingOfReactionParticipants m = new MappingOfReactionParticipants(reactant.elementID, product.elementID, "");
//						currentMappingOfReactionParticipants.add(m );
//						itProduct.remove();
//						foundProduct = true;
//						break;		// we exit inner loop if we find a match for current reactant
//					}
//				}
//				if(foundProduct == false) {
//					System.out.println("Did not found a match for reactant " + reactant.moleculeName + ", " + reactant.elementID);
//				}
//				itReactant.remove();		// found or not, we remove the reactant
//			}
//			if(!currentProductElementsOfReaction.isEmpty()) {
//				for(MolecularTypeOfReactionParticipant p : currentProductElementsOfReaction) {
//					System.out.println("Did not found a match for product " + p.moleculeName + ", " + p.elementID);
//				}
//			}
//			for(Iterator<ComponentOfMolecularTypeOfReactionParticipant> itReactant = currentComponentOfReactantElementsOfReaction.iterator(); itReactant.hasNext();) {	// components
//				ComponentOfMolecularTypeOfReactionParticipant reactant = itReactant.next();
//				boolean foundProduct = false;
//				for(Iterator<ComponentOfMolecularTypeOfReactionParticipant> itProduct = currentComponentOfProductElementsOfReaction.iterator(); itProduct.hasNext();) {
//					ComponentOfMolecularTypeOfReactionParticipant product = itProduct.next();
//					String state = "";
//					if(reactant.find(product)) {
//						if(!reactant.state.equals(product.state)) {
//							state = product.state;
//						}
//						MappingOfReactionParticipants m = new MappingOfReactionParticipants(reactant.elementID, product.elementID, state);
//						currentMappingOfReactionParticipants.add(m );
//						itProduct.remove();
//						foundProduct = true;
//						break;		// we exit inner loop if we find a match for current reactant
//					}
//				}
//				if(foundProduct == false) {
//					System.out.println("Did not found a match for reactant " + reactant.moleculeName + ", " + reactant.elementID);
//				}
//				itReactant.remove();		// found or not, we remove the reactant
//			}
//			if(!currentComponentOfProductElementsOfReaction.isEmpty()) {
//				for(ComponentOfMolecularTypeOfReactionParticipant p : currentComponentOfProductElementsOfReaction) {
//					System.out.println("Did not found a match for product " + p.moleculeName + ", " + p.elementID);
//				}
//			}
//			for(Iterator<MappingOfReactionParticipants> it = currentMappingOfReactionParticipants.iterator(); it.hasNext();) {
//				MappingOfReactionParticipants m = it.next();
//				Element mapItemElement = new Element("MapItem");
//				mapItemElement.setAttribute("sourceID", m.reactantElementID);
//				mapItemElement.setAttribute("targetID", m.productElementID);
//				mapElement.addContent(mapItemElement);
//			}
//			reactionRuleElement.addContent(mapElement);
//
//	        //  <ListOfOperations>
//	        //      <AddBond site1="RR1_RP1_M1_C1" site2="RR1_RP2_M1_C1"/>
//			//		<StateChange site="RR0_RP0_M0_C2" finalState="Y"/>
//	        //  </ListOfOperations>
//			Element listOfOperationsElement = new Element("ListOfOperations");
//			
//			// AddBond elements
//			// add any bond in the product which is not present in the reactant
//			Iterator<BondSites> it = patternProductBondSites.iterator();
//			while (it.hasNext()) {
//				BondSites bs = it.next();
//				String reactantS1 = MappingOfReactionParticipants.findMatchingReactant(bs.component1, currentMappingOfReactionParticipants);
//				String reactantS2 = MappingOfReactionParticipants.findMatchingReactant(bs.component2, currentMappingOfReactionParticipants);
//				// we check if the bonds in the product existed already in the reactant, in which case they were not "added" in this reaction
//				BondSites candidate = new BondSites(reactantS1, reactantS2);
//				boolean preExistent = false;
//				for(BondSites bsReactant : reactionReactantBondSites) {
//					if(bsReactant.equals(candidate)) {
//						preExistent = true;
//						break;
//					}
//				}
//				if(preExistent == true) {
//					continue;		// we don't add preexisting bonds
//				}
//				Element addBondElement = new Element("AddBond");
//				addBondElement.setAttribute("site1", reactantS1);
//				addBondElement.setAttribute("site2", reactantS2);
//				listOfOperationsElement.addContent(addBondElement);
//			}
//			// StateChange elements
//			for(Iterator<MappingOfReactionParticipants> it1 = currentMappingOfReactionParticipants.iterator(); it1.hasNext();) {
//				MappingOfReactionParticipants m = it1.next();
//				if(!m.componentFinalState.equals("")) {		// state has changed if it's different from ""
//					Element stateChangeElement = new Element("StateChange");
//					stateChangeElement.setAttribute("site", m.reactantElementID);
//					stateChangeElement.setAttribute("finalState", m.componentFinalState);
//					listOfOperationsElement.addContent(stateChangeElement);
//				}
//			}
//			// eliminate all the common entries (molecule types) in reactants and products
//			// what's left in reactants was deleted, what's left in products was added
//			List<MolecularTypeOfReactionParticipant> commonParticipants = new ArrayList<MolecularTypeOfReactionParticipant>();
//			for(Iterator<MolecularTypeOfReactionParticipant> itReactant = currentReactantElementsOfReaction.iterator(); itReactant.hasNext();) {	// participants
//				MolecularTypeOfReactionParticipant reactant = itReactant.next();
//				for(Iterator<MolecularTypeOfReactionParticipant> itProduct = currentProductElementsOfReaction.iterator(); itProduct.hasNext();) {
//					MolecularTypeOfReactionParticipant product = itProduct.next();
//					if(reactant.find(product)) {
//						// commonParticipants contains the reactant molecules with a equivalent molecule in the product (meaning they are not in the "Deleted" category)
//						commonParticipants.add(reactant);
//						itReactant.remove();
//						itProduct.remove();
//						break;		// we exit inner loop if we find a match for current reactant
//					}
//				}
//			}
//			// DeleteBond element
//			// there is no need to mention deletion of bond if the particleSpeciesPattern 
//			// or the MolecularType involved in the bond are deleted as well
//			// We only keep those "Deleted" bonds which belong to the molecules (of the reactant) present in commonParticipants
//			// Both components (sites) of the bond need to have their molecules in commonParticipants
//			boolean foundMoleculeForComponent1 = false;
//			boolean foundMoleculeForComponent2 = false;
//			HashSet<BondSites> cloneOfReactantBondSites = new HashSet<BondSites>(patternReactantBondSites);
//			Iterator<BondSites> itbs = cloneOfReactantBondSites.iterator();
//			while (itbs.hasNext()) {
//				BondSites bs = itbs.next();
//				String bondComponent1MoleculeId = BondSites.extractMoleculeId(bs.component1);
//				String bondComponent2MoleculeId = BondSites.extractMoleculeId(bs.component2);
//				for(MolecularTypeOfReactionParticipant commonReactionMoleculeule : commonParticipants) {
//					String commonReactantPatternId = commonReactionMoleculeule.elementID;
//					if(bondComponent1MoleculeId.equals(commonReactantPatternId)) {
//						foundMoleculeForComponent1 = true;
//					}
//					if(bondComponent2MoleculeId.equals(commonReactantPatternId)) {
//						foundMoleculeForComponent2 = true;
//					}
//				}
//				if(!foundMoleculeForComponent1 || !foundMoleculeForComponent2) {
//					// at least one of bond's molecule is not in common, hence we don't need to report the deletion of this bond
//					itbs.remove();
//				}
//			}
//			// the clone has now all the deleted bonds whose molecules have not been deleted
//			itbs = cloneOfReactantBondSites.iterator();
//			while (itbs.hasNext()) {
//				BondSites bs = itbs.next();
//				Element addBondElement = new Element("DeleteBond");
//				addBondElement.setAttribute("site1", bs.component1);
//				addBondElement.setAttribute("site2", bs.component2);
//				listOfOperationsElement.addContent(addBondElement);
//			}
//			// Add MolecularType element
//			for(MolecularTypeOfReactionParticipant molecule : currentProductElementsOfReaction) {
//				System.out.println("created molecule: " + molecule.elementID + "' " + molecule.moleculeName);
//				Element addMolecularTypePatternElement = new Element("Add");
//				addMolecularTypePatternElement.setAttribute("id", molecule.elementID);
//				listOfOperationsElement.addContent(addMolecularTypePatternElement);
//			}
//			// Delete MolecularType element
//			// if the reactant pattern of the molecule being deleted still exists as part of the common, then we only delete the molecule
//			// if the reactant pattern of the molecule being deleted is not as part of the common, then it's gone completely and we delete the reactant pattern
//			ArrayList<String> patternsToDelete = new ArrayList<String>();
//			for(MolecularTypeOfReactionParticipant molecule : currentReactantElementsOfReaction) {
//				String reactantPatternId = molecule.extractReactantPatternId();
//				boolean found = false;
//				for(MolecularTypeOfReactionParticipant common : commonParticipants) {
//					String commonId = common.extractReactantPatternId();
//					if(reactantPatternId.equals(commonId)) {
//						found = true;
//						break;		// some other molecule of this pattern still there, we don't delete the pattern
//					}
//				}
//				if(found == true) {		// some other molecule of this pattern still there, we don't delete the pattern
//					System.out.println("deleted molecule: " + molecule.elementID + "' " + molecule.moleculeName);
//					Element addMolecularTypePatternElement = new Element("Delete");
//					addMolecularTypePatternElement.setAttribute("id", molecule.elementID);
//					addMolecularTypePatternElement.setAttribute("DeleteMolecules", "0");
//					listOfOperationsElement.addContent(addMolecularTypePatternElement);
//				} else {				// no molecule of this pattern left, we delete the pattern
//					if(patternsToDelete.contains(reactantPatternId)) {
//						// nothing to do, we're already deleting this pattern
//						break;
//					} else {
//						patternsToDelete.add(reactantPatternId);
//					System.out.println("deleted pattern: " + reactantPatternId);
//					Element addParticleSpeciesPatternElement = new Element("Delete");
//					addParticleSpeciesPatternElement.setAttribute("id", reactantPatternId);
//					addParticleSpeciesPatternElement.setAttribute("DeleteMolecules", "0");
//					listOfOperationsElement.addContent(addParticleSpeciesPatternElement);
//					}
//				}
//			}
//			reactionRuleElement.addContent(listOfOperationsElement);
			listOfReactionRules.addContent(reactionRuleElement);
		}

		Element listOfObservablesElement = getListOfObservables(clonedMathDesc);
		Element listOfFunctionsElement = getListOfFunctions(clonedMathDesc, simulationSymbolTable);
		
		modelElement.addContent(listOfParametersElement);
		modelElement.addContent(listOfMoleculeTypesElement);
		modelElement.addContent(listOfSpeciesElement);
		modelElement.addContent(listOfReactionRules);
		modelElement.addContent(listOfObservablesElement);
		modelElement.addContent(listOfFunctionsElement);

		sbmlElement.addContent(modelElement);
		// for testing purposes
//		Element e1 = null;
//		try {
//			Element e = (Element)sbmlElement.clone();
//			FileWriter writer = new FileWriter("c:\\TEMP\\math2xml.xml");
//			Document doc = new Document();
//			doc.setRootElement(e);
//			XMLOutputter xmlOut = new XMLOutputter();
//			xmlOut.output(doc, writer);			// write to file
//			// String xmlString = xmlOut.outputString(doc);
//			// System.out.println(xmlString);		// write to console
//			SAXBuilder builder = new SAXBuilder();
//			File reader = new File("c:\\TEMP\\math2xml.xml");
//			Document document = (Document) builder.build(reader);
//			e1 = document.getRootElement();
//		} catch (IOException ex) {
//			ex.printStackTrace();
//		} catch (JDOMException ex) {
//			ex.printStackTrace();
//		}
////		return e1;
		return sbmlElement;
	}
	
	private static boolean isFunction(String candidate, MathDescription mathDesc, SimulationSymbolTable simulationSymbolTable) throws SolverException {
		
		Element listOfParametersElement = new Element("ListOfFunctions");
		
		for (Variable var : simulationSymbolTable.getVariables()){
			Double value = null;
			if (var instanceof Constant || var instanceof Function){
				Expression valExpression = var.getExpression();
				Expression substitutedValExpr = null;
				try {
					substitutedValExpr = simulationSymbolTable.substituteFunctions(valExpression);
				}catch (Exception e){
					e.printStackTrace(System.out);
					throw new SolverException("Constant or Function "+var.getName()+" substitution failed : exp = \""+var.getExpression().infix()+"\": "+e.getMessage());
				}
				try {
					value = substitutedValExpr.evaluateConstant();
				}catch (ExpressionException e){
					System.out.println("constant or function "+var.getName()+" = "+substitutedValExpr.infix()+" does not have a constant value");
				}
				if (value!=null) {
					continue;		// parameter, see getListOfParameters() above
				} else {
					String current = var.getName();
					if(candidate.equals(current)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private static Element getObservableParticipantPattern(String prefix0, String prefix1, VolumeParticleSpeciesPattern reactantSpeciesPattern, String patternElementName, ParticleObservable.Sequence sequence, Integer quantity) throws SolverException {
		Element reactionParticipantPatternElement = new Element(patternElementName);
		String patternID = prefix0 + "_" + prefix1;
		reactionParticipantPatternElement.setAttribute("id", patternID);
		//reactionParticipantPatternElement.setAttribute("name", reactantSpeciesPattern.getName());
		if(sequence != ParticleObservable.Sequence.Multimolecular) {
			String relation = sequence == ParticleObservable.Sequence.PolymerLengthEqual ? "==" : ">";
			reactionParticipantPatternElement.setAttribute("relation", relation);
			reactionParticipantPatternElement.setAttribute("quantity", quantity+"");
		}
		Element listOfMoleculesElement = new Element("ListOfMolecules");
		HashMap<Bond,BondSites> bondSiteMapping = new HashMap<Bond,BondSites>();
		for(int moleculeIndex = 0; moleculeIndex < reactantSpeciesPattern.getParticleMolecularTypePatterns().size(); moleculeIndex++) {
			ParticleMolecularTypePattern molecularTypePattern = reactantSpeciesPattern.getParticleMolecularTypePatterns().get(moleculeIndex);
			Element moleculeElement = new Element("Molecule");
			String moleculeID = patternID + "_M" + (moleculeIndex+1);
			moleculeElement.setAttribute("id", moleculeID);
			moleculeElement.setAttribute("name", molecularTypePattern.getMolecularType().getName());

			Element listOfComponentsElement = getListOfComponents(moleculeID, reactantSpeciesPattern, molecularTypePattern, bondSiteMapping);
			if(listOfComponentsElement != null) {
				moleculeElement.addContent(listOfComponentsElement);
			}
			listOfMoleculesElement.addContent(moleculeElement);
		}
		reactionParticipantPatternElement.addContent(listOfMoleculesElement);
		
		if (bondSiteMapping.size()>0){
			
			Element listOfBondsElement = getListOfBonds(bondSiteMapping);
			reactionParticipantPatternElement.addContent(listOfBondsElement);
			
		}
		return reactionParticipantPatternElement;
	}
	
	private static Element getListOfObservables(MathDescription mathDesc) throws SolverException {
		Element listOfObservablesElement = new Element("ListOfObservables");
		int observableIndex = 0;
		Enumeration<Variable> enum1 = mathDesc.getVariables();
		while (enum1.hasMoreElements()){
			Variable var = enum1.nextElement();
			if (var instanceof ParticleObservable){
				ParticleObservable particleObservable = (ParticleObservable)var;
				Element observableElement = new Element("Observable");
				String observableId = "O" + (observableIndex+1);
				observableElement.setAttribute("id", observableId);
				observableElement.setAttribute("name", particleObservable.getName());
				observableElement.setAttribute("type", particleObservable.getType().getText());
				Element listOfPatterns = getParticleSpeciesPatternList(observableId, particleObservable);
				observableElement.addContent(listOfPatterns);
				listOfObservablesElement.addContent(observableElement);
				observableIndex++;
			}
		}

        Format format = Format.getPrettyFormat();
		XMLOutputter outp = new XMLOutputter(format);
		String sOurs = outp.outputString(listOfObservablesElement);
//		System.out.println("==================== Our Observables ===================================================================");
//		System.out.println(sOurs);
//		System.out.println("========================================================================================================");
		//
		// Saving the observables, as produced by us
		// in debug configurations add to command line   -Ddebug.user=danv
		//
//		String debugUser = PropertyLoader.getProperty("debug.user", "not_defined");
//		if (debugUser.equals("danv") || debugUser.equals("mblinov")){
//			System.out.println("Saving our observables");
//			RulebasedTransformer.saveAsText("c:\\TEMP\\ddd\\ourObservables.txt", sOurs);
//		}

		return listOfObservablesElement;
	}
	
	private static Element getParticleSpeciesPatternList(String prefix0, ParticleObservable particleObservable) {
		Element listOfPatternsElement = new Element("ListOfPatterns");
		for(int molecularPatternIndex = 0; molecularPatternIndex < particleObservable.getParticleSpeciesPatterns().size(); molecularPatternIndex++) {
			ParticleSpeciesPattern pattern = particleObservable.getParticleSpeciesPatterns().get(molecularPatternIndex);
			if(pattern instanceof VolumeParticleSpeciesPattern) {
				Element patternElement;
				try {
					String molecularPatternID = "P" + (molecularPatternIndex+1);
					patternElement = getObservableParticipantPattern(prefix0, molecularPatternID, (VolumeParticleSpeciesPattern)pattern, "Pattern", particleObservable.getSequence(), particleObservable.getQuantity());
					listOfPatternsElement.addContent(patternElement);
				} catch (SolverException e) {
					e.printStackTrace();
				}
			}
		}
		return listOfPatternsElement;
	}

	private static Element getListOfSpecies(MathDescription mathDesc, SimulationSymbolTable simulationSymbolTable) throws SolverException {
		CompartmentSubDomain compartmentSubDomain = (CompartmentSubDomain)mathDesc.getSubDomains().nextElement();

		//
		// NFsim expects a list of seed species.  These are concrete species patterns that have a "ParticleProperties" element defined (for initial conditions).
		//
		Element listOfSpeciesElement = new Element("ListOfSpecies");
		for(int speciesIndex = 0; speciesIndex < compartmentSubDomain.getParticleProperties().size(); speciesIndex++) {
//				seedSpecies.getSpeciesPattern().resolveBonds();
			ParticleProperties particleProperties = compartmentSubDomain.getParticleProperties().get(speciesIndex);
			Element speciesElement = new Element("Species");
			String speciesID = "S" + (speciesIndex + 1);
			ParticleSpeciesPattern seedSpecies = (ParticleSpeciesPattern) particleProperties.getVariable();
			speciesElement.setAttribute("id", speciesID);
			speciesElement.setAttribute("name", seedSpecies.getName());
			
			List<ParticleInitialCondition> particleInitialConditions = particleProperties.getParticleInitialConditions();
			if (particleInitialConditions.size()!=1){
				throw new SolverException("multiple particle initial conditions not expected for "+ParticleSpeciesPattern.class.getSimpleName()+" "+seedSpecies.getName());
			}
			// the initial conditions must be a count in math (ParticleInitialConditionCount)
			if (!(particleInitialConditions.get(0) instanceof ParticleInitialConditionCount)){
				throw new SolverException("expecting initial count for "+ParticleSpeciesPattern.class.getSimpleName()+" "+seedSpecies.getName());
			}
			ParticleInitialConditionCount initialCount = (ParticleInitialConditionCount)particleInitialConditions.get(0);
			try {
				double value = evaluateConstant(initialCount.getCount(),simulationSymbolTable);
				Integer maxMoleculesPerType = simulationSymbolTable.getSimulation().getSolverTaskDescription().getNFSimSimulationOptions().getMaxMoleculesPerType();
				if (maxMoleculesPerType == null){
					maxMoleculesPerType = NFsimSimulationOptions.DefaultMaxMoleculesPerSpecies;
				}
				if (maxMoleculesPerType.doubleValue() < value){
					String eMessage = "The Initial count for Species '" + seedSpecies.getName() + "' is " + BigDecimal.valueOf(value).toBigInteger();
					eMessage += ", which is higher than the limit of " + maxMoleculesPerType + ".\n";
					eMessage += "Please do one of the following: \n- reduce the Initial Condition value for this Species or reduce the compartment size\n";
					eMessage += "- increase the maximal number of Molecules per Molecular Type in the Advanced Solver Options panel.";
					throw new RuntimeException(eMessage);
				}
				speciesElement.setAttribute("concentration",Double.toString(value));
			} catch (ExpressionException | MathException e) {
				e.printStackTrace();
				throw new SolverException("error processing initial count of "+ParticleSpeciesPattern.class.getSimpleName()+" "+seedSpecies.getName()+": "+e.getMessage());
			}
			HashMap<Bond,BondSites> bondSiteMapping = new HashMap<Bond,BondSites>();
			Element listOfMoleculesElement = getListOfMolecules(speciesID, seedSpecies, bondSiteMapping);
			speciesElement.addContent(listOfMoleculesElement);
			
			if (bondSiteMapping.size()>0){
				
				Element listOfBondsElement = getListOfBonds(bondSiteMapping);
				speciesElement.addContent(listOfBondsElement);
				
			}
			
			listOfSpeciesElement.addContent(speciesElement);
		}
		return listOfSpeciesElement;
	}

	private static Element getListOfMolecules(String prefix0, ParticleSpeciesPattern speciesPattern, HashMap<Bond, BondSites> bondSiteMapping) throws SolverException {
		Element listOfMoleculesElement = new Element("ListOfMolecules");
		for (int moleculeIndex = 0; moleculeIndex < speciesPattern.getParticleMolecularTypePatterns().size(); moleculeIndex++){
			ParticleMolecularTypePattern molecularTypePattern = speciesPattern.getParticleMolecularTypePatterns().get(moleculeIndex);
			Element moleculeElement = new Element("Molecule");
			String moleculeID = prefix0 + "_M" + (moleculeIndex+1);
			moleculeElement.setAttribute("id", moleculeID);
			moleculeElement.setAttribute("name", molecularTypePattern.getMolecularType().getName());
			
			Element listOfComponentsElement = getListOfComponents(moleculeID, speciesPattern, molecularTypePattern, bondSiteMapping);
			if(listOfComponentsElement != null) {
				moleculeElement.addContent(listOfComponentsElement);
			}
			listOfMoleculesElement.addContent(moleculeElement);
		}
		return listOfMoleculesElement;
	}

	private static Element getListOfComponents(String prefix1, ParticleSpeciesPattern speciesPattern, ParticleMolecularTypePattern particleMolecularTypePattern, 
			HashMap<Bond, BondSites> bondSitesMap) throws SolverException {
		if(particleMolecularTypePattern.getMolecularComponentPatternList().isEmpty()) {
			return null;
		}
		Element listOfComponentsElement = new Element("ListOfComponents");
		for (int patternIndex = 0; patternIndex < particleMolecularTypePattern.getMolecularComponentPatternList().size(); patternIndex++){
			ParticleMolecularComponentPattern particleMolecularComponentPattern = particleMolecularTypePattern.getMolecularComponentPatternList().get(patternIndex);
			Element componentElement = new Element("Component");
			ParticleMolecularComponent particleMolecularComponent = particleMolecularComponentPattern.getMolecularComponent();
			String componentID = prefix1 + "_C" + (patternIndex+1);
			componentElement.setAttribute("id", componentID);
			componentElement.setAttribute("name", particleMolecularComponent.getName());
			ParticleComponentStatePattern componentStatePattern = particleMolecularComponentPattern.getComponentStatePattern();
			ParticleComponentStateDefinition pcsd = componentStatePattern.getParticleComponentStateDefinition();
			
			boolean hasState = false;
			if(pcsd != null) {
				componentElement.setAttribute("state", pcsd.getName());
				hasState = true;
			}
//			String state = "";
//			if (componentState!=null){
//				state = componentState.getName();
//				if(!state.equals("*")) {
//					componentElement.setAttribute("state", state);
//				}
//			}
			// number of bonds is 0 or 1 for species (concrete species).  the bonds are listed later in the list of bonds
			ParticleBondType bondType = particleMolecularComponentPattern.getBondType();
			boolean ignoreFlag = false;
			switch (bondType){
				case Exists:{
					componentElement.setAttribute("numberOfBonds", bondType.symbol);
					break;
				}
				case None:{
					componentElement.setAttribute("numberOfBonds", "0");
					break;
				}
				case Possible:{
					componentElement.setAttribute("numberOfBonds", bondType.symbol);
					ignoreFlag = true;
					break;
				}
				case Specified:{
					if (particleMolecularComponentPattern.getBondId()>=0){
						componentElement.setAttribute("numberOfBonds", "1");
						String bondID = prefix1.substring(0, prefix1.lastIndexOf("_"));
						bondID = bondID + "_B" + particleMolecularComponentPattern.getBondId();
						Bond bond = new Bond(bondID, speciesPattern);
						BondSites bondSites = bondSitesMap.get(bond);
						if (bondSites == null){
							BondSites newBondSite = new BondSites();
							newBondSite.component1 = componentID;
							bondSitesMap.put(bond, newBondSite);
						}else{
							if (bondSites.component1.equals(componentID) || bondSites.component2.equals(componentID)){
								throw new SolverException("this molecularComponentPattern already set in bondSites");
							}
							if (bondSites.component2.equals("")){
								bondSites.component2 = componentID;
							}else{
								throw new SolverException("two other molecularComponentPatterns already set in bondSites");
							}
						}
					}else{
						componentElement.setAttribute("numberOfBonds","0");
					}
					break;
				}
			}
			if(!ignoreFlag || hasState) {
				listOfComponentsElement.addContent(componentElement);
			}
		}
		return listOfComponentsElement;
	}
	private static Element getListOfComponents1(String reactionRuleID, String patternID, String moleculeID, 
			ParticleSpeciesPattern speciesPattern, ParticleMolecularTypePattern particleMolecularTypePattern, 
			ArrayList<ComponentOfMolecularTypeOfReactionParticipant> currentComponentOfParticipant, HashMap<Bond, BondSites> bondSitesMap) throws SolverException {
		// while traversing Components of a MolecularTypePattern, it populates bondSiteMapping
		Element listOfComponentsElement = new Element("ListOfComponents");
		for (int componentId = 0; componentId < particleMolecularTypePattern.getMolecularComponentPatternList().size(); componentId++){
			ParticleMolecularComponentPattern particleMolecularComponentPattern = particleMolecularTypePattern.getMolecularComponentPatternList().get(componentId);
			Element componentElement = new Element("Component");
			ParticleMolecularComponent particleMolecularComponent = particleMolecularComponentPattern.getMolecularComponent();
			//componentElement.setAttribute("id", particleMolecularComponent.getId());
			String elementID = "C" + (componentId + 1);
			String componentID = reactionRuleID + "_" + patternID + "_" + moleculeID + "_" + elementID;
			componentElement.setAttribute("id", componentID);
			componentElement.setAttribute("name", particleMolecularComponent.getName());

			ParticleComponentStatePattern componentStatePattern = particleMolecularComponentPattern.getComponentStatePattern();
			ParticleComponentStateDefinition pcsd = componentStatePattern.getParticleComponentStateDefinition();
			String state = "";
			boolean ignoreFlagState = false;
			if(componentStatePattern.isAny()) {
				state = "*";
				ignoreFlagState = true;
			} else if(pcsd != null) {
				state = pcsd.getName();
				componentElement.setAttribute("state", state);
			} else {
				throw new RuntimeException("Invalid state for ParticleComponentStatePattern.");
			}
//			ParticleComponentState componentState = particleMolecularComponentPattern.getComponentState();
//			String state = "";
//			if (componentState!=null){
//				state = componentState.getName();
//				if(!state.equals("*")) {
//					componentElement.setAttribute("state", state);
//				}
//			}
			ComponentOfMolecularTypeOfReactionParticipant cper = new ComponentOfMolecularTypeOfReactionParticipant(particleMolecularTypePattern.getMolecularType().getName(), 
					particleMolecularTypePattern.getMatchLabel(), particleMolecularComponent.getName(), componentID, state);
			// number of bonds is 0 or 1 for species (concrete species).  the bonds are listed later in the list of bonds
			ParticleBondType bondType = particleMolecularComponentPattern.getBondType();
			boolean ignoreFlagBond = false;
			switch (bondType){
				case Exists:{
					componentElement.setAttribute("numberOfBonds",bondType.symbol);
					break;
				}
				case None:{
					componentElement.setAttribute("numberOfBonds","0");
					break;
				}
				case Possible:{
					componentElement.setAttribute("numberOfBonds",bondType.symbol);
					ignoreFlagBond = true;
					break;
				}
				case Specified:{
					if (particleMolecularComponentPattern.getBondId()>=0){
						componentElement.setAttribute("numberOfBonds","1");
						String bondID = reactionRuleID + "_" + patternID + "_B" + particleMolecularComponentPattern.getBondId();
						Bond bond = new Bond(bondID, speciesPattern);
						BondSites bondSites = bondSitesMap.get(bond);
						if (bondSites == null){
							BondSites newBondSite = new BondSites();
							newBondSite.component1 = componentID;
							bondSitesMap.put(bond, newBondSite);
						}else{
							if (bondSites.component1.equals(componentID) || bondSites.component2.equals(componentID)){
								throw new SolverException("this molecularComponentPattern already set in bondSites");
							}
							if (bondSites.component2.equals("")){
								bondSites.component2 = componentID;
							}else{
								throw new SolverException("two other molecularComponentPatterns already set in bondSites");
							}
						}
					}else{
						componentElement.setAttribute("numberOfBonds","0");
					}
					break;
				}
			}
			if(ignoreFlagState == false || ignoreFlagBond == false) {
				currentComponentOfParticipant.add(cper);
				listOfComponentsElement.addContent(componentElement);
			}
		}
		return listOfComponentsElement;
	}

	private static Element getListOfBonds(HashMap<Bond, BondSites> bondSiteMapping) throws SolverException {
		//
		// uses bondSiteMapping
		//
		Element listOfBondsElement = new Element("ListOfBonds");
		HashSet<String> uniqueMolecularComponentIds = new HashSet<String>();
		for (Bond bond : bondSiteMapping.keySet()) {
			BondSites bondSites = bondSiteMapping.get(bond);
			if (bondSites.component1==null || bondSites.component2==null){
				throw new SolverException("bond "+bond.getId()+" doesn't have two sites defined");
			}
			String molecularComponentId_1 = bondSites.component1;
			String molecularComponentId_2 = bondSites.component2;
			if (!uniqueMolecularComponentIds.contains(molecularComponentId_1) && !uniqueMolecularComponentIds.contains(molecularComponentId_2)){
				Element bondElement = new Element("Bond");
				bondElement.setAttribute("id",bond.getId());
				bondElement.setAttribute("site1",molecularComponentId_1);
				bondElement.setAttribute("site2",molecularComponentId_2);
				uniqueMolecularComponentIds.add(molecularComponentId_1);
				uniqueMolecularComponentIds.add(molecularComponentId_2);
				listOfBondsElement.addContent(bondElement);
			}
		}
		return listOfBondsElement;
	}

	private static Element getListOfMoleculeTypes(MathDescription mathDesc) {
		
		Element listOfMoleculeTypesElement = new Element("ListOfMoleculeTypes");
		for (ParticleMolecularType molecularType : mathDesc.getParticleMolecularTypes()){
			Element molecularTypeElement = new Element("MoleculeType");
			molecularTypeElement.setAttribute("id",molecularType.getName());
//TODO molecularTypeElement.setAttribute("population","1");
			
			Element listOfComponentTypesElement = getListOfComponentTypes(molecularType);
			if(listOfComponentTypesElement != null) {
				molecularTypeElement.addContent(listOfComponentTypesElement);
			}
			
			listOfMoleculeTypesElement.addContent(molecularTypeElement);
		}
		return listOfMoleculeTypesElement;
	}

	private static Element getListOfComponentTypes(ParticleMolecularType molecularType) {
		
		if(molecularType.getComponentList().isEmpty()) {
			return null;
		}
		Element listOfComponentTypesElement = new Element("ListOfComponentTypes");
		for (ParticleMolecularComponent molecularComponent : molecularType.getComponentList()){
			Element componentTypeElement = new Element("ComponentType");
			componentTypeElement.setAttribute("id",molecularComponent.getName());
			
			Element listOfAllowedStates = getListOfAllowedStates(molecularComponent);
			if(listOfAllowedStates != null) {
				componentTypeElement.addContent(listOfAllowedStates);
			}
			listOfComponentTypesElement.addContent(componentTypeElement);
		}
		return listOfComponentTypesElement;
	}

	private static Element getListOfAllowedStates(ParticleMolecularComponent molecularComponent) {
		
		if(molecularComponent.getComponentStateDefinitions().isEmpty()) {
			return null;
		}
		Element listOfAllowedStates = new Element("ListOfAllowedStates");
		for (ParticleComponentStateDefinition componentStateDefinition : molecularComponent.getComponentStateDefinitions()){
			Element allowedStateElement = new Element("AllowedState");
			allowedStateElement.setAttribute("id",componentStateDefinition.getName());
			listOfAllowedStates.addContent(allowedStateElement);
		}
		return listOfAllowedStates;
	}

	private static Element getListOfParameters(MathDescription mathDesc, SimulationSymbolTable simulationSymbolTable) throws SolverException {
		
		Element listOfParametersElement = new Element("ListOfParameters");
		
		for (Variable var : simulationSymbolTable.getVariables()){
			Double value = null;
			if (var instanceof Constant || var instanceof Function){
				Expression valExpression = var.getExpression();
				Expression substitutedValExpr = null;
				try {
					substitutedValExpr = simulationSymbolTable.substituteFunctions(valExpression);
				}catch (Exception e){
					e.printStackTrace(System.out);
					throw new SolverException("Constant or Function "+var.getName()+" substitution failed : exp = \""+var.getExpression().infix()+"\": "+e.getMessage());
				}
				try {
					value = substitutedValExpr.evaluateConstant();
				}catch (ExpressionException e){
					System.out.println("constant or function "+var.getName()+" = "+substitutedValExpr.infix()+" does not have a constant value");
				}
				Element parameterElement = new Element("Parameter");
				parameterElement.setAttribute("id",var.getName());
				if (value!=null) {
					parameterElement.setAttribute("type","Constant");
					parameterElement.setAttribute("value",value.toString());
				} else {
					continue;		// function, see getListOfFunctions() below
				}
				listOfParametersElement.addContent(parameterElement);
			}
		}
		return listOfParametersElement;
	}
	private static Element getListOfFunctions(MathDescription mathDesc, SimulationSymbolTable simulationSymbolTable) throws SolverException {
		
		Element listOfParametersElement = new Element("ListOfFunctions");
		
		for (Variable var : simulationSymbolTable.getVariables()){
			Double value = null;
			if (var instanceof Constant || var instanceof Function){
				Expression valExpression = var.getExpression();
				Expression substitutedValExpr = null;
				try {
					substitutedValExpr = simulationSymbolTable.substituteFunctions(valExpression);
				}catch (Exception e){
					e.printStackTrace(System.out);
					throw new SolverException("Constant or Function "+var.getName()+" substitution failed : exp = \""+var.getExpression().infix()+"\": "+e.getMessage());
				}
				try {
					value = substitutedValExpr.evaluateConstant();
				}catch (ExpressionException e){
					System.out.println("constant or function "+var.getName()+" = "+substitutedValExpr.infix()+" does not have a constant value");
				}
				Element functionElement = new Element("Function");
				functionElement.setAttribute("id",var.getName());
				if (value!=null) {
					continue;		// parameter, see getListOfParameters() above
				} else {
					Element listOfReferencesElement = new Element("ListOfReferences");
					String[] references = valExpression.getSymbols();
					for(int i=0; i<references.length; i++) {
						String reference = references[i];
						Element referenceElement = new Element("Reference");
						referenceElement.setAttribute("name", reference);
						
						Variable referenceVariable = simulationSymbolTable.getVariable(reference);
						
						Double referenceValue = null;
						Expression referenceExpression = referenceVariable.getExpression();
						Expression substitutedReferenceExpression = null;
						if(referenceExpression != null) {
							try {
								substitutedReferenceExpression = simulationSymbolTable.substituteFunctions(referenceExpression);
							}catch (Exception e){
								e.printStackTrace(System.out);
								throw new SolverException("Constant or Function "+var.getName()+" substitution failed : exp = \""+var.getExpression().infix()+"\": "+e.getMessage());
							}
							try {
								referenceValue = substitutedReferenceExpression.evaluateConstant();
							}catch (ExpressionException e){
								System.out.println("constant or function "+var.getName()+" = "+substitutedValExpr.infix()+" does not have a constant value");
							}
						}
						if(referenceVariable instanceof ParticleObservable) {
							referenceElement.setAttribute("type", "Observable");
						} else if(referenceVariable instanceof Function) {
							if(referenceValue != null) {
								referenceElement.setAttribute("type", "ConstantExpression");
							} else {
								referenceElement.setAttribute("type", "Function");
							}
						} else {	// constant
							referenceElement.setAttribute("type", "ConstantExpression");
						}
						listOfReferencesElement.addContent(referenceElement);
					}
					
					functionElement.addContent(listOfReferencesElement);
					
					Element expressionElement = new Element("Expression");
					String functionExpression = valExpression.infix();
					expressionElement.setText(functionExpression);
					
					functionElement.addContent(expressionElement);
				}
				listOfParametersElement.addContent(functionElement);
			}
		}
		return listOfParametersElement;
	}
	
	private static double evaluateConstant(Expression expression, SimulationSymbolTable simulationSymbolTable) throws MathException, ExpressionException{
		Expression subExp = simulationSymbolTable.substituteFunctions(expression);
		double value = subExp.evaluateConstant();
		return value;
	}
}
