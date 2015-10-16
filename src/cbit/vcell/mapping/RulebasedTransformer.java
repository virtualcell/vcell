package cbit.vcell.mapping;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.vcell.model.rbm.FakeReactionRuleRateParameter;
import org.vcell.model.rbm.FakeSeedSpeciesInitialConditionsParameter;
import org.vcell.model.rbm.MolecularComponentPattern;
import org.vcell.model.rbm.MolecularType;
import org.vcell.model.rbm.MolecularTypePattern;
import org.vcell.model.rbm.RbmNetworkGenerator;
import org.vcell.model.rbm.RbmObject;
import org.vcell.model.rbm.RuleAnalysis;
import org.vcell.model.rbm.RuleAnalysis.MolecularComponentEntry;
import org.vcell.model.rbm.RuleAnalysis.RuleEntry;
import org.vcell.model.rbm.SpeciesPattern;
import org.vcell.util.BeanUtils;
import org.vcell.util.Pair;
import org.vcell.util.TokenMangler;

import cbit.vcell.bionetgen.BNGOutputSpec;
import cbit.vcell.mapping.ParameterContext.LocalParameter;
import cbit.vcell.mapping.ParameterContext.LocalProxyParameter;
import cbit.vcell.mapping.SimulationContext.MathMappingCallback;
import cbit.vcell.mapping.SimulationContext.NetworkGenerationRequirements;
import cbit.vcell.mapping.TaskCallbackMessage.TaskCallbackStatus;
import cbit.vcell.model.Kinetics;
import cbit.vcell.model.Kinetics.KineticsParameter;
import cbit.vcell.model.MassActionKinetics;
import cbit.vcell.model.Model;
import cbit.vcell.model.Model.ModelParameter;
import cbit.vcell.model.ModelRuleFactory.ModelMolecularComponentEntry;
import cbit.vcell.model.ModelException;
import cbit.vcell.model.Product;
import cbit.vcell.model.ProductPattern;
import cbit.vcell.model.RbmKineticLaw;
import cbit.vcell.model.RbmKineticLaw.RateLawType;
import cbit.vcell.model.RbmKineticLaw.RbmKineticLawParameterType;
import cbit.vcell.model.ModelRuleFactory;
import cbit.vcell.model.ProxyParameter;
import cbit.vcell.model.RbmObservable;
import cbit.vcell.model.Reactant;
import cbit.vcell.model.ReactantPattern;
import cbit.vcell.model.ReactionParticipant;
import cbit.vcell.model.ReactionRule;
import cbit.vcell.model.ReactionRuleParticipant;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.server.bionetgen.BNGExecutorService;
import cbit.vcell.server.bionetgen.BNGInput;
import cbit.vcell.server.bionetgen.BNGOutput;
import cbit.vcell.xml.XMLTags;

public class RulebasedTransformer implements SimContextTransformer {

	// we populate these by parsing the xml file produced by BioNetGen
	private Map<String, RbmObject> keyMap = new LinkedHashMap<String, RbmObject>();				// master key map, only use here
	private Map<ReactionRule, Double> ruleSymmetryForwardMap = new LinkedHashMap<ReactionRule, Double>();
	private Map<ReactionRule, Double> ruleSymmetryReverseMap = new LinkedHashMap<ReactionRule, Double>();
	private Map<ReactionRule, Pair<RbmObject, RbmObject>> ruleMappingForwardMap = new LinkedHashMap<ReactionRule, Pair<RbmObject, RbmObject>>();
	private Map<ReactionRule, Pair<RbmObject, RbmObject>> ruleMappingReverseMap = new LinkedHashMap<ReactionRule, Pair<RbmObject, RbmObject>>();
	
	@Override
	final public SimContextTransformation transform(SimulationContext originalSimContext, MathMappingCallback mathMappingCallback, NetworkGenerationRequirements netGenReq_NOT_USED) {
		SimulationContext transformedSimContext;
		keyMap.clear();
		ruleSymmetryForwardMap.clear();
		ruleSymmetryReverseMap.clear();
		ruleMappingForwardMap.clear();
		ruleMappingReverseMap.clear();
		try {
			transformedSimContext = (SimulationContext)BeanUtils.cloneSerializable(originalSimContext);
			transformedSimContext.getModel().refreshDependencies();
			transformedSimContext.refreshDependencies();
			transformedSimContext.compareEqual(originalSimContext);
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Unexpected transform exception: "+e.getMessage());
		}
		transformedSimContext.getModel().refreshDependencies();
		transformedSimContext.refreshDependencies1(false);

		ArrayList<ModelEntityMapping> entityMappings = new ArrayList<ModelEntityMapping>();
		
		try {
			transform(originalSimContext,transformedSimContext,entityMappings,mathMappingCallback);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
			throw new RuntimeException("Unexpected transform exception: "+e.getMessage());
		}
		
		ModelEntityMapping[] modelEntityMappings = entityMappings.toArray(new ModelEntityMapping[0]);
		
		return new SimContextTransformation(originalSimContext, transformedSimContext, modelEntityMappings);
	}
	
	Map<ReactionRule, Double> getRuleSymmetryForwardMap() {
		return ruleSymmetryForwardMap;
	}
	Map<ReactionRule, Double> getRuleSymmetryReverseMap() {
		return ruleSymmetryReverseMap;
	}
	Map<ReactionRule, Pair<RbmObject, RbmObject>> getRuleMappingForwardMap() {
		return ruleMappingForwardMap;
	}
	Map<ReactionRule, Pair<RbmObject, RbmObject>> getRuleMappingReverseMap() {
		return ruleMappingReverseMap;
	}
		
	private void transform(SimulationContext originalSimContext, SimulationContext transformedSimulationContext, ArrayList<ModelEntityMapping> entityMappings, MathMappingCallback mathMappingCallback) throws PropertyVetoException {
		Model newModel = transformedSimulationContext.getModel();
		Model originalModel = originalSimContext.getModel();
		ModelEntityMapping em = null;
		
//		for(ReactionRule newrr : newModel.getRbmModelContainer().getReactionRuleList()) {			// map new and old kinetic parameters of reaction rules
//			ReactionRule oldrr = originalModel.getRbmModelContainer().getReactionRule(newrr.getName());
//			for(Parameter newkp : newrr.getKineticLaw().getKineticsParameters()) {
//				Parameter oldkp = oldrr.getKineticLaw().getKineticsParameter(newkp.getName());
//				em = new ModelEntityMapping(oldkp, newkp);
//				entityMappings.add(em);
//			}
//		}
		
		for(SpeciesContext sc : newModel.getSpeciesContexts()) {
			em = new ModelEntityMapping(originalModel.getSpeciesContext(sc.getName()), sc);		// map new and old species contexts
			entityMappings.add(em);
			if(sc.hasSpeciesPattern()) {
				continue;	// it's perfect already and can't be improved
			}
			try {
				MolecularType newmt = newModel.getRbmModelContainer().createMolecularType();
				newModel.getRbmModelContainer().addMolecularType(newmt, false);
				MolecularTypePattern newmtp_sc = new MolecularTypePattern(newmt);
				SpeciesPattern newsp_sc = new SpeciesPattern();
				newsp_sc.addMolecularTypePattern(newmtp_sc);
				sc.setSpeciesPattern(newsp_sc);
				
				RbmObservable newo = new RbmObservable(newModel,"O0_"+newmt.getName()+"_tot",sc.getStructure(),RbmObservable.ObservableType.Molecules);
				MolecularTypePattern newmtp_ob = new MolecularTypePattern(newmt);
				SpeciesPattern newsp_ob = new SpeciesPattern();
				newsp_ob.addMolecularTypePattern(newmtp_ob);
				newo.addSpeciesPattern(newsp_ob);
				newModel.getRbmModelContainer().addObservable(newo);

				em = new ModelEntityMapping(originalModel.getSpeciesContext(sc.getName()), newo);	// map new observable to old species context
				entityMappings.add(em);
			} catch (ModelException e) {
				e.printStackTrace();
				throw new RuntimeException("unable to transform species context: "+e.getMessage());
			} catch (PropertyVetoException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		ReactionSpec[] reactionSpecs = transformedSimulationContext.getReactionContext().getReactionSpecs();
		for(ReactionSpec reactionSpec : reactionSpecs) {
			
			if (reactionSpec.isExcluded()){
				continue;	// we create rules only from those reactions which are not excluded
			}
			ReactionStep rs = reactionSpec.getReactionStep();
			String name = rs.getName();
			String mangled = TokenMangler.fixTokenStrict(name);
			mangled = newModel.getReactionName(mangled);
			Kinetics k = rs.getKinetics();
			if(!(k instanceof MassActionKinetics)) {
				throw new RuntimeException("Only Mass Action Kinetics supported at this time, reaction \""+rs.getName()+"\" uses kinetic law type \""+rs.getKinetics().getName()+"\"");
			}
			
			boolean bReversible = true;
			ReactionRule rr = new ReactionRule(newModel, mangled, rs.getStructure(), bReversible);
			rr.setStructure(rs.getStructure());
		
			MassActionKinetics massActionKinetics = (MassActionKinetics)k;
			Expression forwardRateExp = massActionKinetics.getForwardRateParameter().getExpression();
			String forwardRateName = massActionKinetics.getForwardRateParameter().getName();
			Expression reverseRateExp = massActionKinetics.getReverseRateParameter().getExpression();
			String reverseRateName = massActionKinetics.getReverseRateParameter().getName();
			RateLawType rateLawType = RateLawType.MassAction;
			RbmKineticLaw kineticLaw = new RbmKineticLaw(rr, rateLawType);
			try {
				LocalParameter fR = kineticLaw.getLocalParameter(RbmKineticLawParameterType.MassActionForwardRate);
				fR.setName(forwardRateName);
				LocalParameter rR = kineticLaw.getLocalParameter(RbmKineticLawParameterType.MassActionReverseRate);
				rR.setName(reverseRateName);
				kineticLaw.setParameterValue(fR, forwardRateExp, true);
				kineticLaw.setParameterValue(rR, reverseRateExp, true);
				
				//
				// set values of local user-defined parameters also (user defined).
				//
				for (KineticsParameter reaction_p : massActionKinetics.getKineticsParameters()){
					if (reaction_p.getRole() == Kinetics.ROLE_UserDefined){
						LocalParameter rule_p = kineticLaw.getLocalParameter(reaction_p.getName());
						if (rule_p == null){
							//
							// after lazy parameter creation we didn't find a user-defined rule parameter with this same name.
							// 
							// there must be a global symbol with the same name, that the local reaction parameter has overridden.
							//
							ParameterContext.LocalProxyParameter rule_proxy_parameter = null;
							for (ProxyParameter proxyParameter : kineticLaw.getProxyParameters()){
								if (proxyParameter.getName().equals(reaction_p.getName())){
									rule_proxy_parameter = (LocalProxyParameter) proxyParameter;
								}
							}
							if (rule_proxy_parameter != null){
								boolean bConvertToGlobal = false;  // we want to convert to local
								kineticLaw.convertParameterType(rule_proxy_parameter, bConvertToGlobal);
							}else{
								// could find neither local parameter nor proxy parameter
								throw new RuntimeException("user defined parameter "+reaction_p.getName()+" from reaction "+rs.getName()+" didn't map to a reactionRule parameter");
							}
						}else if (rule_p.getRole() == RbmKineticLawParameterType.UserDefined){
							kineticLaw.setParameterValue(rule_p, reaction_p.getExpression(), true);
							rule_p.setUnitDefinition(reaction_p.getUnitDefinition());
						}else{
							throw new RuntimeException("user defined parameter "+reaction_p.getName()+" from reaction "+rs.getName()+" mapped to a reactionRule parameter with unexpected role "+rule_p.getRole().getDescription());
						}
					}
				}
			} catch (ExpressionException e) {
				e.printStackTrace();
				throw new RuntimeException("Problem attempting to set RbmKineticLaw expression: "+ e.getMessage());
			}
			rr.setKineticLaw(kineticLaw);
			
			KineticsParameter[] kpList = k.getKineticsParameters();
			ModelParameter[] mpList = rs.getModel().getModelParameters();
			ModelParameter mp = rs.getModel().getModelParameter(kpList[0].getName());
			
			ReactionParticipant[] pList = rs.getReactionParticipants();
			for(ReactionParticipant p : pList) {
				if(p instanceof Reactant) {
					int stoichiometry = p.getStoichiometry();
					// TODO: must not reuse the SP of the species, must make new deep constructor
					// because the molecular type patterns are going to be different (each its own match for example)
					for(int i=0; i<stoichiometry; i++) {
						SpeciesPattern speciesPattern = new SpeciesPattern(p.getSpeciesContext().getSpeciesPattern());
						ReactantPattern reactantPattern = new ReactantPattern(speciesPattern, rr.getStructure());
						rr.addReactant(reactantPattern);
					}
					
				} else if(p instanceof Product) {
					int stoichiometry = p.getStoichiometry();
					for(int i=0; i<stoichiometry; i++) {
						SpeciesPattern speciesPattern = new SpeciesPattern(p.getSpeciesContext().getSpeciesPattern());
						ProductPattern productPattern = new ProductPattern(speciesPattern, rr.getStructure());
						rr.addProduct(productPattern);
					}
				}
			}
			// NFSim doesn't support more then 2 reactants / products
			if(rr.getReactantPatterns().size() > 2) {
				String message = "NFSim doesn't support more than 2 reactants within a reaction: " + name;
				throw new RuntimeException(message);
			}
			if(rr.getProductPatterns().size() > 2) {
				String message = "NFSim doesn't support more than 2 products within a reaction: " + name;
				throw new RuntimeException(message);
			}
			newModel.removeReactionStep(rs);
			newModel.getRbmModelContainer().addReactionRule(rr);
		}
		
		for(ReactionRuleSpec rrs : transformedSimulationContext.getReactionContext().getReactionRuleSpecs()) {
			if(rrs == null) {
				continue;
			}
			ReactionRule rr = rrs.getReactionRule();
			if(rrs.isExcluded()) {		// delete those rules which are disabled (excluded) in the Specifications / Reaction table
				newModel.getRbmModelContainer().removeReactionRule(rr);
				continue;
			}
			// NFSim doesn't support more then 2 reactants / products
			if(rr.getReactantPatterns().size() > 2) {
				String message = "NFSim doesn't support more than 2 reactants within a reaction rule: " + rr.getDisplayName();
				throw new RuntimeException(message);
			}
			if(rr.getProductPatterns().size() > 2) {
				String message = "NFSim doesn't support more than 2 products within a reaction rule: " + rr.getDisplayName();
				throw new RuntimeException(message);
			}
		}
		
		// now that we generated the rules we can delete the reaction steps they're coming from
		for(ReactionStep rs : newModel.getReactionSteps()) {
			newModel.removeReactionStep(rs);
		}
		
		// TODO; for debug only, can be commented out once this code gets stable enough
//		StringWriter bnglStringWriter = new StringWriter();
//		PrintWriter pw = new PrintWriter(bnglStringWriter);
//		RbmNetworkGenerator.writeBngl(transformedSimulationContext, pw, false, false);
//		String resultString = bnglStringWriter.toString();
//		System.out.println(resultString);
//		pw.close();
		
		try {
			// we invoke bngl just for the purpose of generating the xml file, which we'll then use to extract the symmetry factor
			generateNetwork(transformedSimulationContext, mathMappingCallback);
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Finished RuleBased Transformer.");
	}
	
	private Map<FakeSeedSpeciesInitialConditionsParameter, Pair<SpeciesContext, Expression>> speciesEquivalenceMap = new LinkedHashMap<FakeSeedSpeciesInitialConditionsParameter, Pair<SpeciesContext, Expression>>();
	private Map<FakeReactionRuleRateParameter, Expression> kineticsParameterMap = new LinkedHashMap<FakeReactionRuleRateParameter, Expression>();
	public String convertToBngl(SimulationContext simulationContext, boolean ignoreFunctions, MathMappingCallback mathMappingCallback, NetworkGenerationRequirements networkGenerationRequirements) {
		StringWriter bnglStringWriter = new StringWriter();
		PrintWriter pw = new PrintWriter(bnglStringWriter);
		RbmNetworkGenerator.writeBngl_internal(simulationContext, pw, kineticsParameterMap, speciesEquivalenceMap, networkGenerationRequirements);
		String bngl = bnglStringWriter.toString();
		pw.close();
		return bngl;
	}
	private void generateNetwork(SimulationContext simContext, MathMappingCallback mathMappingCallback) 
			throws ClassNotFoundException, IOException {
		TaskCallbackMessage tcm;
		BNGOutputSpec outputSpec;
		speciesEquivalenceMap.clear();
		kineticsParameterMap.clear();
		NetworkGenerationRequirements networkGenerationRequirements = NetworkGenerationRequirements.ComputeFullStandardTimeout;

		String input = convertToBngl(simContext, true, mathMappingCallback, networkGenerationRequirements);
//		System.out.println(input);		// TODO: uncomment to see the xml string
		for (Map.Entry<FakeSeedSpeciesInitialConditionsParameter, Pair<SpeciesContext, Expression>> entry : speciesEquivalenceMap.entrySet()) {
			FakeSeedSpeciesInitialConditionsParameter key = entry.getKey();
			Pair<SpeciesContext, Expression> value = entry.getValue();
			SpeciesContext sc = value.one;
			Expression initial = value.two;
			System.out.println("key: " + key.fakeParameterName + ",   species: " + sc.getName() + ", initial: " + initial.infix());
		}
		
		BNGInput bngInput = new BNGInput(input);
		BNGOutput bngOutput = null;
		try {
			final BNGExecutorService bngService = new BNGExecutorService(bngInput,networkGenerationRequirements.timeoutDurationMS);
			bngOutput = bngService.executeBNG();
		} catch (RuntimeException ex) {
			ex.printStackTrace(System.out);
			throw ex; //rethrow without losing context
		} catch (Exception ex) {
			ex.printStackTrace(System.out);
			throw new RuntimeException(ex.getMessage());
		}

		simContext.setInsufficientIterations(false);
		String bngConsoleString = bngOutput.getConsoleOutput();
		tcm = new TaskCallbackMessage(TaskCallbackStatus.DetailBatch, bngConsoleString);
//		simContext.appendToConsole(tcm);
		
		
//		String bngNetString = bngOutput.getNetFileContent();
//		outputSpec = BNGOutputFileParser.createBngOutputSpec(bngNetString);
//		//BNGOutputFileParser.printBNGNetOutput(outputSpec);			// prints all output to console
//
//		if (mathMappingCallback.isInterrupted()){
//			String msg = "Canceled by user.";
////			tcm = new TaskCallbackMessage(TaskCallbackStatus.Error, msg);
////			simContext.appendToConsole(tcm);
////			simContext.setMd5hash(null);					// clean the cache if the user interrupts
//			throw new UserCancelException(msg);
//		}
//		if(outputSpec.getBNGSpecies().length > SimulationConsolePanel.speciesLimit) {
//			String message = SimulationConsolePanel.getSpeciesLimitExceededMessage(outputSpec);
////			tcm = new TaskCallbackMessage(TaskCallbackStatus.Error, message);
////			simContext.appendToConsole(tcm);
////			simContext.setMd5hash(null);
//			throw new RuntimeException(message);
//		}
//		if(outputSpec.getBNGReactions().length > SimulationConsolePanel.reactionsLimit) {
//			String message = SimulationConsolePanel.getReactionsLimitExceededMessage(outputSpec);
////			tcm = new TaskCallbackMessage(TaskCallbackStatus.Error, message);
////			simContext.appendToConsole(tcm);
////			simContext.setMd5hash(null);
//			throw new RuntimeException(message);
//		}
		
		parseBngOutput(simContext, bngOutput);
	}

	private void parseBngOutput(SimulationContext simContext, BNGOutput bngOutput) {
		
		Model model = simContext.getModel();
		Document bngNFSimXMLDocument = bngOutput.getNFSimXMLDocument();
		Element sbmlElement = bngNFSimXMLDocument.getRootElement();
		Element modelElement = sbmlElement.getChild("model", Namespace.getNamespace("http://www.sbml.org/sbml/level3"));
		Element listOfReactionRulesElement = modelElement.getChild("ListOfReactionRules", Namespace.getNamespace("http://www.sbml.org/sbml/level3"));
		List<Element> reactionRuleChildren = new ArrayList<Element>();
		reactionRuleChildren = listOfReactionRulesElement.getChildren("ReactionRule", Namespace.getNamespace("http://www.sbml.org/sbml/level3"));
		for (Element reactionRuleElement : reactionRuleChildren) {
			boolean bForward = true;
			String rule_id_str = reactionRuleElement.getAttributeValue("id");
			String rule_name_str = reactionRuleElement.getAttributeValue("name");
			String symmetry_factor_str = reactionRuleElement.getAttributeValue("symmetry_factor");
			Double symmetry_factor_double = Double.parseDouble(symmetry_factor_str);
			ReactionRule rr = null;
			if(rule_name_str.startsWith("_reverse_")) {
				bForward = false;
				rule_name_str = rule_name_str.substring("_reverse_".length());
				rr = model.getRbmModelContainer().getReactionRule(rule_name_str);
				ruleSymmetryReverseMap.put(rr, symmetry_factor_double);
			} else {
				rr = model.getRbmModelContainer().getReactionRule(rule_name_str);
				ruleSymmetryForwardMap.put(rr, symmetry_factor_double);
			}
			System.out.println("rule id=" + rule_id_str + ", name=" + rule_name_str + ", symmetry factor=" + symmetry_factor_str);
			keyMap.put(rule_id_str, rr);
			
			Element listOfReactantPatternsElement = reactionRuleElement.getChild("ListOfReactantPatterns", Namespace.getNamespace("http://www.sbml.org/sbml/level3"));
			List<Element> reactantPatternChildren = new ArrayList<Element>();
			reactantPatternChildren = listOfReactantPatternsElement.getChildren("ReactantPattern", Namespace.getNamespace("http://www.sbml.org/sbml/level3"));
			for (int i = 0; i < reactantPatternChildren.size(); i++) {
				Element reactantPatternElement = reactantPatternChildren.get(i);
				String pattern_id_str = reactantPatternElement.getAttributeValue("id");
				ReactionRuleParticipant p = null;
				if(bForward) {
					p = rr.getReactantPattern(i);
				} else {
					p = rr.getProductPattern(i);
				}
				SpeciesPattern sp = p.getSpeciesPattern();
				System.out.println("  reactant id=" + pattern_id_str + ", name=" + sp.toString());
				keyMap.put(pattern_id_str, sp);

				// list of molecules
				extractMolecules(sp, model, reactantPatternElement);
				
				// list of bonds
				Element listOfBondsElement = reactantPatternElement.getChild("ListOfBonds", Namespace.getNamespace("http://www.sbml.org/sbml/level3"));
				if(listOfBondsElement != null) {
					List<Element> bondChildren = new ArrayList<Element>();
					bondChildren = listOfBondsElement.getChildren("Bond", Namespace.getNamespace("http://www.sbml.org/sbml/level3"));
					for (Element bondElement : bondChildren) {
						String bond_id_str = bondElement.getAttributeValue("id");
						String bond_site1_str = bondElement.getAttributeValue("site1");
						String bond_site2_str = bondElement.getAttributeValue("site2");
					
					}
				}
			}
			
			Element listOfProductPatternsElement = reactionRuleElement.getChild("ListOfProductPatterns", Namespace.getNamespace("http://www.sbml.org/sbml/level3"));
			List<Element> productPatternChildren = new ArrayList<Element>();
			productPatternChildren = listOfProductPatternsElement.getChildren("ProductPattern", Namespace.getNamespace("http://www.sbml.org/sbml/level3"));
			for (int i = 0; i < productPatternChildren.size(); i++) {
				Element productPatternElement = productPatternChildren.get(i);
				String pattern_id_str = productPatternElement.getAttributeValue("id");
				
				ReactionRuleParticipant p = null;
				if(bForward) {
					p = rr.getProductPattern(i);
				} else {
					p = rr.getReactantPattern(i);
				}
				SpeciesPattern sp = p.getSpeciesPattern();
				System.out.println("  product  id=" + pattern_id_str + ", name=" + sp.toString());
				keyMap.put(pattern_id_str, sp);

				// list of molecules
				extractMolecules(sp, model, productPatternElement);
				
				// list of bonds
				Element listOfBondsElement = productPatternElement.getChild("ListOfBonds", Namespace.getNamespace("http://www.sbml.org/sbml/level3"));
				if(listOfBondsElement != null) {
					List<Element> bondChildren = new ArrayList<Element>();
					bondChildren = listOfBondsElement.getChildren("Bond", Namespace.getNamespace("http://www.sbml.org/sbml/level3"));
					for (Element bondElement : bondChildren) {
						String bond_id_str = bondElement.getAttributeValue("id");
						String bond_site1_str = bondElement.getAttributeValue("site1");
						String bond_site2_str = bondElement.getAttributeValue("site2");
					
					}
				}
			}
			
			// extract the map for this rule
			Element listOfMapElement = reactionRuleElement.getChild("Map", Namespace.getNamespace("http://www.sbml.org/sbml/level3"));
			List<Element> mapChildren = new ArrayList<Element>();
			mapChildren = listOfMapElement.getChildren("MapItem", Namespace.getNamespace("http://www.sbml.org/sbml/level3"));
			for (Element mapElement : mapChildren) {
				String target_id_str = mapElement.getAttributeValue("targetID");
				String source_id_str = mapElement.getAttributeValue("sourceID");
				System.out.println("Map: target=" + target_id_str + " source=" + source_id_str);
				RbmObject target_object = keyMap.get(target_id_str);
				RbmObject source_object = keyMap.get(source_id_str);
				if(target_object == null) System.out.println("!!! Missing map object " + target_id_str);
				if(source_object == null) System.out.println("!!! Missing map object " + source_id_str);
				
				if(target_object != null && source_object != null) {
					System.out.println("      target=" + target_object + " source=" + source_object);
					Pair<RbmObject, RbmObject> mapEntry = new Pair<RbmObject, RbmObject> (target_object, source_object);
					if(bForward) {
						ruleMappingForwardMap.put(rr, mapEntry);
					} else {
						ruleMappingReverseMap.put(rr, mapEntry);
					}
				}
			}
		}
		System.out.println("done parsing xml file");
	}

	public void extractMolecules(SpeciesPattern sp, Model model, Element participantPatternElement) {
		
		Element listOfMoleculesElement = participantPatternElement.getChild("ListOfMolecules", Namespace.getNamespace("http://www.sbml.org/sbml/level3"));
		List<Element> moleculeChildren = new ArrayList<Element>();
		moleculeChildren = listOfMoleculesElement.getChildren("Molecule", Namespace.getNamespace("http://www.sbml.org/sbml/level3"));
		for (Element moleculeElement : moleculeChildren) {
			String molecule_id_str = moleculeElement.getAttributeValue("id");
			String molecule_name_str = moleculeElement.getAttributeValue("name");
			MolecularTypePattern mtp = sp.getMolecularTypePattern(molecule_name_str);
			if(mtp == null) System.out.println("!!! Missing molecule " + molecule_name_str);
			System.out.println("     molecule  id=" + molecule_id_str + ", name=" + molecule_name_str);
			keyMap.put(molecule_id_str, mtp);

			Element listOfComponentsElement = moleculeElement.getChild("ListOfComponents", Namespace.getNamespace("http://www.sbml.org/sbml/level3"));
			List<Element> componentChildren = new ArrayList<Element>();
			componentChildren = listOfComponentsElement.getChildren("Component", Namespace.getNamespace("http://www.sbml.org/sbml/level3"));
			for (Element componentElement : componentChildren) {
				String component_id_str = componentElement.getAttributeValue("id");
				String component_name_str = componentElement.getAttributeValue("name");
				MolecularComponentPattern mcp = mtp.getMolecularComponentPattern(component_name_str);
				if(mcp == null) System.out.println("!!! Missing component " + component_name_str);
				System.out.println("        component  id=" + component_id_str + ", name=" + component_name_str);
				keyMap.put(component_id_str, mcp);
			
			}
		}
	}
}


/*
			ReactionRule reactionRule = null;
			int reactionRuleIndex = 1;
			ModelRuleFactory modelRuleFactory = new ModelRuleFactory();
			RuleEntry ruleEntry = modelRuleFactory.createRuleEntry(reactionRule, reactionRuleIndex);
			//
			// looking for components
			//
			String componentNameToMatch = "RR1_PP1_M1_C1";
			MolecularComponentPattern foundComponentPattern = null;
			for (MolecularComponentEntry mce : ruleEntry.getProductMolecularComponentEntries()){
				if (RuleAnalysis.getID(mce).equals(componentNameToMatch)){
					foundComponentPattern = ((ModelMolecularComponentEntry)mce).getMolecularComponentPattern();
				}
			}
*/
