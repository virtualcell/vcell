package cbit.vcell.mapping;

import java.beans.PropertyVetoException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.output.XMLOutputter;
import org.vcell.model.rbm.FakeReactionRuleRateParameter;
import org.vcell.model.rbm.FakeSeedSpeciesInitialConditionsParameter;
import org.vcell.model.rbm.MolecularComponentPattern;
import org.vcell.model.rbm.MolecularType;
import org.vcell.model.rbm.MolecularTypePattern;
import org.vcell.model.rbm.RbmNetworkGenerator;
import org.vcell.model.rbm.RbmObject;
import org.vcell.model.rbm.RuleAnalysis;
import org.vcell.model.rbm.RuleAnalysisReport;
import org.vcell.model.rbm.SpeciesPattern;
import org.vcell.model.rbm.RbmNetworkGenerator.CompartmentMode;
import org.vcell.util.BeanUtils;
import org.vcell.util.Pair;
import org.vcell.util.TokenMangler;

import cbit.vcell.bionetgen.BNGOutputSpec;
import cbit.vcell.mapping.ParameterContext.LocalParameter;
import cbit.vcell.mapping.ParameterContext.LocalProxyParameter;
import cbit.vcell.mapping.SimulationContext.MathMappingCallback;
import cbit.vcell.mapping.SimulationContext.NetworkGenerationRequirements;
import cbit.vcell.mapping.TaskCallbackMessage.TaskCallbackStatus;
import cbit.vcell.math.MathRuleFactory.MathRuleEntry;
import cbit.vcell.model.Kinetics;
import cbit.vcell.model.Kinetics.KineticsParameter;
import cbit.vcell.model.MassActionKinetics;
import cbit.vcell.model.Model;
import cbit.vcell.model.Model.ModelParameter;
import cbit.vcell.model.ModelException;
import cbit.vcell.model.Product;
import cbit.vcell.model.ProductPattern;
import cbit.vcell.model.ProxyParameter;
import cbit.vcell.model.RbmKineticLaw;
import cbit.vcell.model.RbmKineticLaw.RateLawType;
import cbit.vcell.model.RbmKineticLaw.RbmKineticLawParameterType;
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

public class RulebasedTransformer implements SimContextTransformer {

	interface Operation {
	}
	class StateChangeOperation implements Operation {
		final private String finalState;
		final private String idSite;
		final private RbmObject objSite;			// MolecularComponentPattern
		public StateChangeOperation(String finalState, String idSite, RbmObject objSite) {
			super();
			this.finalState = finalState;
			this.idSite = idSite;
			this.objSite = objSite;
		}
		@Override
		public String toString(){
			return "CHANGE STATE ON " + idSite + " to " + finalState;
		}
	}
	class AddBondOperation implements Operation {
		final private String idSite1;
		final private String idSite2;
		final private RbmObject objSite1;		// MolecularComponentPattern
		final private RbmObject objSite2;
		public AddBondOperation(String idSite1, String idSite2, RbmObject objSite1, RbmObject objSite2) {
			super();
			this.idSite1 = idSite1;
			this.idSite2 = idSite2;
			this.objSite1 = objSite1;
			this.objSite2 = objSite2;
		}
		@Override
		public String toString(){
			return "ADD BOND from " + idSite1 + " to " + idSite2;
		}
	}
	class DeleteBondOperation implements Operation {
		final private String idSite1;
		final private String idSite2;
		final private RbmObject objSite1;		// MolecularComponentPattern
		final private RbmObject objSite2;
		public DeleteBondOperation(String idSite1, String idSite2, RbmObject objSite1, RbmObject objSite2) {
			super();
			this.idSite1 = idSite1;
			this.idSite2 = idSite2;
			this.objSite1 = objSite1;
			this.objSite2 = objSite2;
		}
		@Override
		public String toString(){
			return "DELETE BOND from " + idSite1 + " to " + idSite2;
		}
	}
	class AddOperation implements Operation {
		final private String idSite;
		final private RbmObject objSite;			// attribute is called id 
		public AddOperation(String idSite, RbmObject objSite) {
			super();
			this.idSite = idSite;
			this.objSite = objSite;	// can be MolecularTypePattern or SpeciesPattern
		}
		@Override
		public String toString() {
			if(objSite instanceof MolecularTypePattern) {
				return "ADD MOLECULE " + idSite;
			} else {
				return "ADD PARTICIPANT " + idSite;
			}
		}
	}
	class DeleteOperation implements Operation {
		final private String idSite;
		final private RbmObject objSite;			// attribute is called id 
		final private int deleteMolecules;
		public DeleteOperation(String idSite, RbmObject objSite, int deleteMolecules) {
			super();
			this.idSite = idSite;
			this.objSite = objSite;	// can be MolecularTypePattern or SpeciesPattern
			this.deleteMolecules = deleteMolecules;
		}
		@Override
		public String toString() {
			String strMolecules = "";
			if(deleteMolecules > 0) {
				strMolecules += "DeleteMolecules=" + deleteMolecules;
			}
			if(objSite instanceof MolecularTypePattern) {
				return "DELETE MOLECULE " + idSite + strMolecules;
			} else {
				return "DELETE PARTICIPANT " + idSite + strMolecules;
			}
		}
	}

	class ReactionRuleAnalysisReport {
		protected Double symmetryFactor = 1.0;
		protected List<Pair<RbmObject, RbmObject>> objmappingList = new ArrayList<Pair<RbmObject, RbmObject>>();
		protected List<Pair<String, String>> idmappingList = new ArrayList<Pair<String, String>>();
		protected List<Operation> operationsList = new ArrayList<Operation>();
		public Double getSymmetryFactor() {
			return symmetryFactor;
		}
		public List<Pair<RbmObject, RbmObject>> getObjectMappingList() {
			return objmappingList;
		}
		public List<Pair<String, String>> getIdMappingList() {
			return idmappingList;
		}
		public List<Operation> getOperationsList() {
			return operationsList;
		}
	}
	
	public class RulebasedTransformation extends SimContextTransformation {

		private Map<String, Element> ruleElementMap;
		private Map<ReactionRule, ReactionRuleAnalysisReport> rulesForwardMap;
		private Map<ReactionRule, ReactionRuleAnalysisReport> rulesReverseMap;

		public RulebasedTransformation(SimulationContext originalSimContext,
				SimulationContext transformedSimContext,
				ModelEntityMapping[] modelEntityMappings,
				Map<String, Element> ruleElementMap,
				Map<ReactionRule, ReactionRuleAnalysisReport> rulesForwardMap,
				Map<ReactionRule, ReactionRuleAnalysisReport> rulesReverseMap) {
			super(originalSimContext, transformedSimContext, modelEntityMappings);
			this.ruleElementMap = ruleElementMap;
			this.rulesForwardMap = rulesForwardMap;
			this.rulesReverseMap = rulesReverseMap;
		}
		Map<ReactionRule, ReactionRuleAnalysisReport> getRulesForwardMap() {
			return rulesForwardMap;
		}
		Map<ReactionRule, ReactionRuleAnalysisReport> getRulesReverseMap() {
			return rulesReverseMap;
		}
		public Map<String, Element> getRuleElementMap(){
			return ruleElementMap;
		}
		
		public void compareOutputs(MathRuleEntry mathRuleEntry) {
			// compare the xml from BioNetGen with the one we build
			XMLOutputter outp = new XMLOutputter();
				
			String identifier = RuleAnalysis.getID(mathRuleEntry);
			Element rreTheirs = ruleElementMap.get(identifier);
			
			RuleAnalysisReport report = RuleAnalysis.analyze(mathRuleEntry, true);
			Element rreOurs = RuleAnalysis.getNFSimXML(mathRuleEntry, report);

			compareOperations(rreTheirs, rreOurs);
			
			rreTheirs.removeChild("RateLaw", Namespace.getNamespace("http://www.sbml.org/sbml/level3"));
			rreTheirs.removeChild("ListOfOperations", Namespace.getNamespace("http://www.sbml.org/sbml/level3"));
			rreOurs.removeChild("ListOfOperations");
						
			String rule_id_str = rreTheirs.getAttributeValue("id");
			rule_id_str = rule_id_str.substring(2);
			int rrIndex = Integer.parseInt(rule_id_str);
			String sTheirs = outp.outputString(rreTheirs);
			sTheirs = sTheirs.replace("xmlns=\"http://www.sbml.org/sbml/level3\"", "");
			sTheirs = sTheirs.replaceAll("\\s+","");
			
//			String debugUser = PropertyLoader.getProperty("debug.user", "not_defined");
//			if (debugUser.equals("danv") || debugUser.equals("mblinov"))
//			{
//				System.out.println("Saving their rules");
//		        Format format = Format.getPrettyFormat();
//				XMLOutputter outpEx = new XMLOutputter(format);
//				String sTheirsEx = outpEx.outputString(rreTheirs);
//				RulebasedTransformer.saveAsText("c:\\TEMP\\ddd\\theirRules.txt", sTheirsEx);
//			}

			String sOurs = outp.outputString(rreOurs);
			sOurs = sOurs.replace(".0", "");
			sOurs = sOurs.replaceAll("\\s+","");
			
//			if (debugUser.equals("danv") || debugUser.equals("mblinov"))
//			{
//				System.out.println("Saving our rules");
//		        Format format = Format.getPrettyFormat();
//				XMLOutputter outpEx = new XMLOutputter(format);
//				String sOursEx = outpEx.outputString(rreOurs);
//				RulebasedTransformer.saveAsText("c:\\TEMP\\ddd\\ourRules.txt", sOursEx);
//			}
			
			if(!sTheirs.equals(sOurs)) {
				System.out.println(sTheirs);
				System.out.println(sOurs);
				char[] sTheirsChars = sTheirs.toCharArray();
				char[] sOursChars = sOurs.toCharArray();
				int firstMismatchIndex = -1;
				int minLength = Math.min(sOurs.length(),sTheirs.length());
				for (int i=0;i<minLength;i++){
					if (sTheirsChars[i] != sOursChars[i]){
						firstMismatchIndex = i;
						break;
					}
				}
				
				int startingIndex = Math.max(0, firstMismatchIndex-500);
				int endingIndexOurs = Math.min(sOurs.length(), firstMismatchIndex+500);
				int endingIndexTheirs = Math.min(sTheirs.length(), firstMismatchIndex+500);
//				System.out.println("not matching!!!");
				throw new RuntimeException("Rule not matching\n" + sTheirs.substring(startingIndex, endingIndexTheirs) + "\n" + sOurs.substring(startingIndex, endingIndexOurs));
//				BeanUtils.sendRemoteLogMessage(null, sTheirs + "\n" + sOurs);
			} else {
				System.out.println("good match ");
			}
		}
	}

	// we populate these by parsing the xml file produced by BioNetGen
	private Element bngRootElement = null;
	private Map<String, RbmObject> keyMap = new LinkedHashMap<String, RbmObject>();				// master key map, only used here
	private Map<String, Element> ruleElementMap = new LinkedHashMap<String, Element>();			// key map: rule id key, rule element
	private Map<ReactionRule, ReactionRuleAnalysisReport> rulesForwardMap = new LinkedHashMap<ReactionRule, ReactionRuleAnalysisReport>();
	private Map<ReactionRule, ReactionRuleAnalysisReport> rulesReverseMap = new LinkedHashMap<ReactionRule, ReactionRuleAnalysisReport>();
	
	@Override
	final public RulebasedTransformation transform(SimulationContext originalSimContext, MathMappingCallback mathMappingCallback, NetworkGenerationRequirements netGenReq_NOT_USED) {
		SimulationContext transformedSimContext;
		keyMap.clear();

		try {
			transformedSimContext = (SimulationContext)BeanUtils.cloneSerializable(originalSimContext);
			transformedSimContext.getModel().refreshDependencies();
			transformedSimContext.refreshDependencies();
			transformedSimContext.compareEqual(originalSimContext);
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Unexpected transform exception: "+e.getMessage());
		}
		final Model transformedModel = transformedSimContext.getModel();
		transformedModel.refreshDependencies();
		transformedSimContext.refreshDependencies1(false);

		ArrayList<ModelEntityMapping> entityMappings = new ArrayList<ModelEntityMapping>();
		try {
			transform(originalSimContext,transformedSimContext,entityMappings,mathMappingCallback);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
			throw new RuntimeException("Unexpected transform exception: "+e.getMessage());
		}
		ModelEntityMapping[] modelEntityMappings = entityMappings.toArray(new ModelEntityMapping[0]);
		return new RulebasedTransformation(originalSimContext, transformedSimContext, modelEntityMappings, ruleElementMap, rulesForwardMap, rulesReverseMap);
	}
	
	private void transform(SimulationContext originalSimContext, SimulationContext transformedSimulationContext, ArrayList<ModelEntityMapping> entityMappings, MathMappingCallback mathMappingCallback) throws PropertyVetoException {
		Model newModel = transformedSimulationContext.getModel();
		Model originalModel = originalSimContext.getModel();
		ModelEntityMapping em = null;
		// list of rules created from the reactions; we apply the symmetry factor computed by bionetgen only to these
		Set<ReactionRule> fromReactions = new HashSet<>();
		
//		for(ReactionRule newrr : newModel.getRbmModelContainer().getReactionRuleList()) {			// map new and old kinetic parameters of reaction rules
//			ReactionRule oldrr = originalModel.getRbmModelContainer().getReactionRule(newrr.getName());
//			for(Parameter newkp : newrr.getKineticLaw().getKineticsParameters()) {
//				Parameter oldkp = oldrr.getKineticLaw().getKineticsParameter(newkp.getName());
//				em = new ModelEntityMapping(oldkp, newkp);
//				entityMappings.add(em);
//			}
//		}
		
		for(SpeciesContext newSpeciesContext : newModel.getSpeciesContexts()) {
			final SpeciesContext originalSpeciesContext = originalModel.getSpeciesContext(newSpeciesContext.getName());
			em = new ModelEntityMapping(originalSpeciesContext, newSpeciesContext);		// map new and old species contexts
			entityMappings.add(em);
			if(newSpeciesContext.hasSpeciesPattern()) {
				continue;	// it's perfect already and can't be improved
			}
			try {
				MolecularType newmt = newModel.getRbmModelContainer().createMolecularType();
				newModel.getRbmModelContainer().addMolecularType(newmt, false);
				MolecularTypePattern newmtp_sc = new MolecularTypePattern(newmt);
				SpeciesPattern newsp_sc = new SpeciesPattern();
				newsp_sc.addMolecularTypePattern(newmtp_sc);
				newSpeciesContext.setSpeciesPattern(newsp_sc);
				
				RbmObservable newo = new RbmObservable(newModel,"O0_"+newmt.getName()+"_tot",newSpeciesContext.getStructure(),RbmObservable.ObservableType.Molecules);
				MolecularTypePattern newmtp_ob = new MolecularTypePattern(newmt);
				SpeciesPattern newsp_ob = new SpeciesPattern();
				newsp_ob.addMolecularTypePattern(newmtp_ob);
				newo.addSpeciesPattern(newsp_ob);
				newModel.getRbmModelContainer().addObservable(newo);

				em = new ModelEntityMapping(originalSpeciesContext, newo);	// map new observable to old species context
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
			
			boolean bReversible = rs.isReversible();
			ReactionRule rr = new ReactionRule(newModel, mangled, rs.getStructure(), bReversible);
			fromReactions.add(rr);
			MassActionKinetics massActionKinetics = (MassActionKinetics)k;
			
			List<Reactant> rList = rs.getReactants();
			List<Product> pList = rs.getProducts();
			int numReactants = 0;		// counting the stoichiometry - 2A+B means 3 reactants
			for(Reactant r : rList) {
				numReactants += r.getStoichiometry();
				if(numReactants > 2) {
					String message = "NFSim doesn't support more than 2 reactants within a reaction: " + name;
					throw new RuntimeException(message);
				}
			}
			int numProducts = 0;
			for(Product p : pList) {
				numProducts += p.getStoichiometry();
				if(bReversible && numProducts > 2) {
					String message = "NFSim doesn't support more than 2 products within a reversible reaction: " + name;
					throw new RuntimeException(message);
				}
			}
			
			RateLawType rateLawType = RateLawType.MassAction;
			RbmKineticLaw kineticLaw = new RbmKineticLaw(rr, rateLawType);
			try {
				String forwardRateName = massActionKinetics.getForwardRateParameter().getName();
				Expression forwardRateExp = massActionKinetics.getForwardRateParameter().getExpression();
				String reverseRateName = massActionKinetics.getReverseRateParameter().getName();
				Expression reverseRateExp = massActionKinetics.getReverseRateParameter().getExpression();
				LocalParameter fR = kineticLaw.getLocalParameter(RbmKineticLawParameterType.MassActionForwardRate);
				fR.setName(forwardRateName);
				LocalParameter rR = kineticLaw.getLocalParameter(RbmKineticLawParameterType.MassActionReverseRate);
				rR.setName(reverseRateName);
				if(rs.hasReactant()) {
					kineticLaw.setParameterValue(fR, forwardRateExp, true);
				}
				if(rs.hasProduct()) {
					kineticLaw.setParameterValue(rR, reverseRateExp, true);
				}
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
			
			ReactionParticipant[] rpList = rs.getReactionParticipants();
			for(ReactionParticipant p : rpList) {
				if(p instanceof Reactant) {
					int stoichiometry = p.getStoichiometry();
					for(int i=0; i<stoichiometry; i++) {
						SpeciesPattern speciesPattern = new SpeciesPattern(rs.getModel(), p.getSpeciesContext().getSpeciesPattern());
						ReactantPattern reactantPattern = new ReactantPattern(speciesPattern, p.getStructure());
						rr.addReactant(reactantPattern);
					}
					
				} else if(p instanceof Product) {
					int stoichiometry = p.getStoichiometry();
					for(int i=0; i<stoichiometry; i++) {
						SpeciesPattern speciesPattern = new SpeciesPattern(rs.getModel(), p.getSpeciesContext().getSpeciesPattern());
						ProductPattern productPattern = new ProductPattern(speciesPattern, p.getStructure());
						rr.addProduct(productPattern);
					}
				}
			}
// commented code below is probably obsolete, we verify (above) in the reaction the number of participants, 
// no need to do it again in the corresponding rule
//			if(rr.getReactantPatterns().size() > 2) {
//				String message = "NFSim doesn't support more than 2 reactants within a reaction: " + name;
//				throw new RuntimeException(message);
//			}
//			if(rr.getProductPatterns().size() > 2) {
//				String message = "NFSim doesn't support more than 2 products within a reaction: " + name;
//				throw new RuntimeException(message);
//			}
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
			generateNetwork(transformedSimulationContext, fromReactions, mathMappingCallback);
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Finished RuleBased Transformer.");
	}
	
	private Map<FakeSeedSpeciesInitialConditionsParameter, Pair<SpeciesContext, Expression>> speciesEquivalenceMap = new LinkedHashMap<FakeSeedSpeciesInitialConditionsParameter, Pair<SpeciesContext, Expression>>();
	private Map<FakeReactionRuleRateParameter, LocalParameter> kineticsParameterMap = new LinkedHashMap<FakeReactionRuleRateParameter, LocalParameter>();
	public String convertToBngl(SimulationContext simulationContext, boolean ignoreFunctions, MathMappingCallback mathMappingCallback, NetworkGenerationRequirements networkGenerationRequirements) {
		StringWriter bnglStringWriter = new StringWriter();
		PrintWriter pw = new PrintWriter(bnglStringWriter);

		Model model = simulationContext.getModel();
		if(model.getNumStructures() > 1) {
			//
			// we ignore compartment info and ask bionetgen to behave as if there's only one compartment 
			// instead, we add an extra site with the compartments as states
			// ATTENTION: we MUST use the extra sites, otherwise the symmetry factor will be wrong, the next 2 reactions give different symmetry factors:
			// @c:A.A -> @c:A + @c:A	flattened into AA -> 2*A
			// @c:A.A -> @m:A + @c:A    flattened into AA -> A + A (different species)
			//
			RbmNetworkGenerator.writeBngl_internal(simulationContext, pw, kineticsParameterMap, speciesEquivalenceMap, networkGenerationRequirements, CompartmentMode.asSite);
		} else {
			// for consistency, always make this call here the new way (CompartmentMode.asSite, never CompartmentMode.hide), so that we always have 
			// a compartment site, even when there's only 1 compartment
			// as it happens, since the compartment site has only one state (and so can't change between reactant and product) here we can make the 
			// call either CompartmentMode.asSite or CompartmentMode.hide
			//
			RbmNetworkGenerator.writeBngl_internal(simulationContext, pw, kineticsParameterMap, speciesEquivalenceMap, networkGenerationRequirements, CompartmentMode.asSite);
		}
		String bngl = bnglStringWriter.toString();
		pw.close();
		return bngl;
	}
	private void generateNetwork(SimulationContext simContext, Set<ReactionRule> fromReactions, MathMappingCallback mathMappingCallback) 
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
			// for the writeXML command we don't want to run iteration by iteration - it wouldn't even make sense since we don't flatten anything
			// so we run bionetgen the "old" way
			final BNGExecutorService bngService = BNGExecutorService.getInstanceOld(bngInput,networkGenerationRequirements.timeoutDurationMS);
			bngOutput = bngService.executeBNG();
		} catch (RuntimeException ex) {
			ex.printStackTrace(System.out);
			throw ex; //rethrow without losing context
		} catch (Exception ex) {
			ex.printStackTrace(System.out);
			throw new RuntimeException(ex.getMessage());
		}

		simContext.setInsufficientIterations(false);
		simContext.setInsufficientMaxMolecules(false);
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
		
		// TODO: uncomment here to parse the xml file!!!
		parseBngOutput(simContext, fromReactions, bngOutput);
		
		//
		// Saving the observables, as produced by bionetgen
		// in debug configurations add to command line   -Ddebug.user=danv
		//
//		String debugUser = PropertyLoader.getProperty("debug.user", "not_defined");
//		if (debugUser.equals("danv") || debugUser.equals("mblinov")){
//			System.out.println("Saving their observables");
//			parseObservablesBngOutput(simContext, bngOutput);
//		}
	
//		compareOutputs(simContext);
	}
	
	
	private void extractMolecules(SpeciesPattern sp, Model model, Element participantPatternElement) {
		List <MolecularTypePattern> mtpUsedAlreadyList = new ArrayList <MolecularTypePattern>();
		Element listOfMoleculesElement = participantPatternElement.getChild("ListOfMolecules", Namespace.getNamespace("http://www.sbml.org/sbml/level3"));
		List<Element> moleculeChildren = new ArrayList<Element>();
		moleculeChildren = listOfMoleculesElement.getChildren("Molecule", Namespace.getNamespace("http://www.sbml.org/sbml/level3"));
		for (Element moleculeElement : moleculeChildren) {
			String molecule_id_str = moleculeElement.getAttributeValue("id");
			String molecule_name_str = moleculeElement.getAttributeValue("name");
			List<MolecularTypePattern> mtpList = sp.getMolecularTypePatterns(molecule_name_str);
			if(mtpList.isEmpty()) System.out.println("!!! Missing molecule " + molecule_name_str);
			MolecularTypePattern mtp = null;
			for(MolecularTypePattern mtpCandidate : mtpList) {
				if(mtpUsedAlreadyList.contains(mtpCandidate)) {
					continue;
				}
				mtp = mtpCandidate;		// this mtp is the next in line unused, so we associate it with this id
				mtpUsedAlreadyList.add(mtpCandidate);
				break;
			}
			System.out.println("     molecule  id=" + molecule_id_str + ", name=" + molecule_name_str);
			keyMap.put(molecule_id_str, mtp);

			Element listOfComponentsElement = moleculeElement.getChild("ListOfComponents", Namespace.getNamespace("http://www.sbml.org/sbml/level3"));
			if(listOfComponentsElement == null) {
				continue;
			}
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
	private void extractBonds(Element reactantPatternElement) {		// does nothing
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

//	private void compareOutputs(SimulationContext simContext) {
//		// compare the xml from BioNetGen with the one we build
//		Model model = simContext.getModel();
//		XMLOutputter outp = new XMLOutputter();
//		Element modelElement = bngRootElement.getChild("model", Namespace.getNamespace("http://www.sbml.org/sbml/level3"));
//		Element listOfReactionRulesElement = modelElement.getChild("ListOfReactionRules", Namespace.getNamespace("http://www.sbml.org/sbml/level3"));
//		List<Element> reactionRuleChildren = new ArrayList<Element>();
//		reactionRuleChildren = listOfReactionRulesElement.getChildren("ReactionRule", Namespace.getNamespace("http://www.sbml.org/sbml/level3"));
//		for (Element rreTheirs : reactionRuleChildren) {
//			rreTheirs.removeChild("RateLaw", Namespace.getNamespace("http://www.sbml.org/sbml/level3"));
//			String rule_id_str = rreTheirs.getAttributeValue("id");
//			rule_id_str = rule_id_str.substring(2);
//			int rrIndex = Integer.parseInt(rule_id_str);
//			String sTheirs = outp.outputString(rreTheirs);
//			sTheirs = sTheirs.replace("xmlns=\"http://www.sbml.org/sbml/level3\"", "");
//			sTheirs = sTheirs.replaceAll("\\s+","");
//
//			String rule_name_str = rreTheirs.getAttributeValue("name");
//			ReactionRule rr = null;
//			ReactionRuleDirection rd;
//			if(rule_name_str.startsWith("_reverse_")) {
//				rd = ReactionRuleDirection.reverse;
//				rule_name_str = rule_name_str.substring("_reverse_".length());
//				rr = model.getRbmModelContainer().getReactionRule(rule_name_str);
//			} else {
//				rd = ReactionRuleDirection.forward;
//				rr = model.getRbmModelContainer().getReactionRule(rule_name_str);
//			}
//
//			MathRuleFactory modelRuleFactory = new ModelRuleFactory();
//			RuleEntry ruleEntry = modelRuleFactory.createRuleEntry(rr, rrIndex-1, rd);
//			RuleAnalysisReport report = RuleAnalysis.analyze(ruleEntry);
//			Element rreOurs = RuleAnalysis.getNFSimXML(ruleEntry, report);
//			String sOurs = outp.outputString(rreOurs);
//			sOurs = sOurs.replace(".0", "");
//			sOurs = sOurs.replaceAll("\\s+","");
//			
//			if(!sTheirs.equals(sOurs)) {
//				System.out.println(sTheirs);
//				System.out.println(sOurs);
//				System.out.println("not matching!!!" + rule_name_str);
//			} else {
//				System.out.println("good match for " + rule_name_str);
//			}
//		}
//	}

	public static void saveAsText(String filename, String text) {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(filename));
			writer.write( text);
		} catch ( IOException e) {
		} finally {
			try {
				if ( writer != null) {
					writer.close( );
				}
			} catch ( IOException e) {
			}
		}
	}
	private void parseObservablesBngOutput(SimulationContext simContext, BNGOutput bngOutput) {
		Model model = simContext.getModel();
		Document bngNFSimXMLDocument = bngOutput.getNFSimXMLDocument();
		bngRootElement = bngNFSimXMLDocument.getRootElement();
		Element modelElement = bngRootElement.getChild("model", Namespace.getNamespace("http://www.sbml.org/sbml/level3"));
		Element listOfObservablesElement = modelElement.getChild("ListOfObservables", Namespace.getNamespace("http://www.sbml.org/sbml/level3"));

		XMLOutputter outp = new XMLOutputter();
		String sTheirs = outp.outputString(listOfObservablesElement);
		sTheirs = sTheirs.replace("xmlns=\"http://www.sbml.org/sbml/level3\"", "");
//		System.out.println("==================== Their Observables ===================================================================");
//		System.out.println(sTheirs);
//		System.out.println("=======================================================================================");
		saveAsText("c:\\TEMP\\ddd\\theirObservables.txt", sTheirs);
		
//		sTheirs = sTheirs.replaceAll("\\s+","");
	}
	
	
	private static void compareOperations(Element eTheirs, Element eOurs) {
		XMLOutputter outp = new XMLOutputter();
		
		Set<String> theirOperations = new HashSet<String>();
		Set<String> ourOperations = new HashSet<String>();
		Element le = eTheirs.getChild("ListOfOperations", Namespace.getNamespace("http://www.sbml.org/sbml/level3"));
		List<Element> children = new ArrayList<Element>();
		children = le.getChildren();
		for(Element e : children) {
			String s = outp.outputString(e);
			s = s.replace("xmlns=\"http://www.sbml.org/sbml/level3\"", "");
			s = s.replaceAll("\\s+","");
			if(theirOperations.add(s) == false) {
				System.out.println("Duplicate their operation: " + s);
			}
			System.out.println(s);
		}
		le = eOurs.getChild("ListOfOperations");
		children = le.getChildren();
		for(Element e : children) {
			String s = outp.outputString(e);
			s = s.replace(".0", "");
			s = s.replaceAll("\\s+","");
			if(ourOperations.add(s) == false) {
				System.out.println("Duplicate our operation: " + s);
			}
			System.out.println(s);
		}
		
		List<String> theirUnmatched = new ArrayList<String>();
		for(String their : theirOperations) {
			if(!ourOperations.contains(their)) {
				theirUnmatched.add(their);
			}
		}
		List<String> ourUnmatched = new ArrayList<String>();
		for(String our : ourOperations) {
			if(!theirOperations.contains(our)) {
				ourUnmatched.add(our);
			}
		}
		
		if(!theirUnmatched.isEmpty() || !ourUnmatched.isEmpty()) {
			String s1 = "theirs: ";
			String s2 = "ours  : ";
			for(String their : theirUnmatched) {
				s1 += their + "' ";
			}
			for(String our : ourUnmatched) {
				s2 += our + "' ";
			}
			throw new RuntimeException("Operations not matching for rule\n" + s1 + "\n" + s2 + "\n");
		} else {
			System.out.println("Operations matching for rule");
		}
	}
	
	private void parseBngOutput(SimulationContext simContext, Set<ReactionRule> fromReactions, BNGOutput bngOutput) {
		Model model = simContext.getModel();
		Document bngNFSimXMLDocument = bngOutput.getNFSimXMLDocument();
		bngRootElement = bngNFSimXMLDocument.getRootElement();
		Element modelElement = bngRootElement.getChild("model", Namespace.getNamespace("http://www.sbml.org/sbml/level3"));
		Element listOfReactionRulesElement = modelElement.getChild("ListOfReactionRules", Namespace.getNamespace("http://www.sbml.org/sbml/level3"));
		List<Element> reactionRuleChildren = new ArrayList<Element>();
		reactionRuleChildren = listOfReactionRulesElement.getChildren("ReactionRule", Namespace.getNamespace("http://www.sbml.org/sbml/level3"));
		for (Element reactionRuleElement : reactionRuleChildren) {
			ReactionRuleAnalysisReport rar = new ReactionRuleAnalysisReport();
			boolean bForward = true;
			String rule_id_str = reactionRuleElement.getAttributeValue("id");
			ruleElementMap.put(rule_id_str, reactionRuleElement);
			String rule_name_str = reactionRuleElement.getAttributeValue("name");
			String symmetry_factor_str = reactionRuleElement.getAttributeValue("symmetry_factor");
			Double symmetry_factor_double = Double.parseDouble(symmetry_factor_str);
			ReactionRule rr = null;
			if(rule_name_str.startsWith("_reverse_")) {
				bForward = false;
				rule_name_str = rule_name_str.substring("_reverse_".length());
				rr = model.getRbmModelContainer().getReactionRule(rule_name_str);
				RbmKineticLaw rkl = rr.getKineticLaw();
				try {
					if(symmetry_factor_double != 1.0 && fromReactions.contains(rr)) {
						Expression expression = rkl.getLocalParameterValue(RbmKineticLawParameterType.MassActionReverseRate);
						expression = Expression.div(expression, new Expression(symmetry_factor_double));
						rkl.setLocalParameterValue(RbmKineticLawParameterType.MassActionReverseRate, expression);
					}
				} catch (ExpressionException | PropertyVetoException exc) {
					exc.printStackTrace();
					throw new RuntimeException("Unexpected transform exception: "+exc.getMessage());
				}
				rulesReverseMap.put(rr, rar);
			} else {
				rr = model.getRbmModelContainer().getReactionRule(rule_name_str);
				RbmKineticLaw rkl = rr.getKineticLaw();
				try {
					if(symmetry_factor_double != 1.0 && fromReactions.contains(rr)) {
						Expression expression = rkl.getLocalParameterValue(RbmKineticLawParameterType.MassActionForwardRate);
						expression = Expression.div(expression, new Expression(symmetry_factor_double));
						rkl.setLocalParameterValue(RbmKineticLawParameterType.MassActionForwardRate, expression);
					}
				} catch (ExpressionException | PropertyVetoException exc) {
					exc.printStackTrace();
					throw new RuntimeException("Unexpected transform exception: "+exc.getMessage());
				}
				rulesForwardMap.put(rr, rar);
			}
			rar.symmetryFactor = symmetry_factor_double;
			System.out.println("rule id=" + rule_id_str + ", name=" + rule_name_str + ", symmetry factor=" + symmetry_factor_str);
			keyMap.put(rule_id_str, rr);
			
			Element listOfReactantPatternsElement = reactionRuleElement.getChild("ListOfReactantPatterns", Namespace.getNamespace("http://www.sbml.org/sbml/level3"));
			List<Element> reactantPatternChildren = new ArrayList<Element>();
			reactantPatternChildren = listOfReactantPatternsElement.getChildren("ReactantPattern", Namespace.getNamespace("http://www.sbml.org/sbml/level3"));
			for (int i = 0; i < reactantPatternChildren.size(); i++) {
				Element reactantPatternElement = reactantPatternChildren.get(i);
				String pattern_id_str = reactantPatternElement.getAttributeValue("id");
				
				ReactionRuleParticipant p = bForward ? rr.getReactantPattern(i) : rr.getProductPattern(i);
				SpeciesPattern sp = p.getSpeciesPattern();
				System.out.println("  reactant id=" + pattern_id_str + ", name=" + sp.toString());
				keyMap.put(pattern_id_str, sp);
				
				extractMolecules(sp, model, reactantPatternElement);	// list of molecules
				extractBonds(reactantPatternElement);		// list of bonds (not implemented)
			}
			
			Element listOfProductPatternsElement = reactionRuleElement.getChild("ListOfProductPatterns", Namespace.getNamespace("http://www.sbml.org/sbml/level3"));
			List<Element> productPatternChildren = new ArrayList<Element>();
			productPatternChildren = listOfProductPatternsElement.getChildren("ProductPattern", Namespace.getNamespace("http://www.sbml.org/sbml/level3"));
			for (int i = 0; i < productPatternChildren.size(); i++) {
				Element productPatternElement = productPatternChildren.get(i);
				String pattern_id_str = productPatternElement.getAttributeValue("id");
				
				ReactionRuleParticipant p = bForward ? rr.getProductPattern(i) : rr.getReactantPattern(i);
				SpeciesPattern sp = p.getSpeciesPattern();
				System.out.println("  product  id=" + pattern_id_str + ", name=" + sp.toString());
				keyMap.put(pattern_id_str, sp);

				extractMolecules(sp, model, productPatternElement);
				extractBonds(productPatternElement);
			}
			
			// extract the Map for this rule
			Element listOfMapElement = reactionRuleElement.getChild("Map", Namespace.getNamespace("http://www.sbml.org/sbml/level3"));
			List<Element> mapChildren = new ArrayList<Element>();
			mapChildren = listOfMapElement.getChildren("MapItem", Namespace.getNamespace("http://www.sbml.org/sbml/level3"));
			for (Element mapElement : mapChildren) {
				String target_id_str = mapElement.getAttributeValue("targetID");
				String source_id_str = mapElement.getAttributeValue("sourceID");
				
				System.out.println("Map: target=" + target_id_str + " source=" + source_id_str);
				RbmObject target_object = keyMap.get(target_id_str);
				RbmObject source_object = keyMap.get(source_id_str);
				if(target_object == null) System.out.println("!!! Missing map target  " + target_id_str);
				if(source_object == null) System.out.println("!!! Missing map source " + source_id_str);
				
				if(source_object != null) {		//  target_object may be null
					System.out.println("      target=" + target_object + " source=" + source_object);
					Pair<RbmObject, RbmObject> mapEntry = new Pair<RbmObject, RbmObject> (target_object, source_object);
					rar.objmappingList.add(mapEntry);
				}
				Pair<String, String> idmapEntry = new Pair<String, String> (target_id_str, source_id_str);
				rar.idmappingList.add(idmapEntry);
			}
			
			// ListOfOperations
			Element listOfOperationsElement = reactionRuleElement.getChild("ListOfOperations", Namespace.getNamespace("http://www.sbml.org/sbml/level3"));
			List<Element> operationsChildren = new ArrayList<Element>();
			operationsChildren = listOfOperationsElement.getChildren("StateChange", Namespace.getNamespace("http://www.sbml.org/sbml/level3"));
			System.out.println("ListOfOperations");
			for (Element operationsElement : operationsChildren) {
				String finalState_str = operationsElement.getAttributeValue("finalState");
				String site_str = operationsElement.getAttributeValue("site");
				RbmObject site_object = keyMap.get(site_str);
				if(site_object == null) System.out.println("!!! Missing map object " + site_str);
				if(site_object != null) {
					System.out.println("   finalState=" + finalState_str + " site=" + site_object);
					StateChangeOperation sco = new StateChangeOperation(finalState_str, site_str, site_object);
					rar.operationsList.add(sco);
				}
			}
			operationsChildren = listOfOperationsElement.getChildren("AddBond", Namespace.getNamespace("http://www.sbml.org/sbml/level3"));
			for (Element operationsElement : operationsChildren) {
				String site1_str = operationsElement.getAttributeValue("site1");
				String site2_str = operationsElement.getAttributeValue("site2");
				RbmObject site1_object = keyMap.get(site1_str);
				RbmObject site2_object = keyMap.get(site2_str);
				if(site1_object == null) System.out.println("!!! Missing map object " + site1_str);
				if(site2_object == null) System.out.println("!!! Missing map object " + site2_str);
				if(site1_object != null && site2_object != null) {
					System.out.println("   site1=" + site1_object + " site2=" + site2_object);
					AddBondOperation abo = new AddBondOperation(site1_str,site2_str,site1_object,site2_object);
					rar.operationsList.add(abo);
				}
			}
			operationsChildren = listOfOperationsElement.getChildren("DeleteBond", Namespace.getNamespace("http://www.sbml.org/sbml/level3"));
			for (Element operationsElement : operationsChildren) {
				String site1_str = operationsElement.getAttributeValue("site1");
				String site2_str = operationsElement.getAttributeValue("site2");
				RbmObject site1_object = keyMap.get(site1_str);
				RbmObject site2_object = keyMap.get(site2_str);
				if(site1_object == null) System.out.println("!!! Missing map object " + site1_str);
				if(site2_object == null) System.out.println("!!! Missing map object " + site2_str);
				if(site1_object != null && site2_object != null) {
					System.out.println("   site1=" + site1_object + " site2=" + site2_object);
					DeleteBondOperation dbo = new DeleteBondOperation(site1_str,site2_str,site1_object,site2_object);
					rar.operationsList.add(dbo);
				}
			}
			operationsChildren = listOfOperationsElement.getChildren("Add", Namespace.getNamespace("http://www.sbml.org/sbml/level3"));
			for (Element operationsElement : operationsChildren) {
				String id_str = operationsElement.getAttributeValue("id");
				RbmObject id_object = keyMap.get(id_str);
				if(id_object == null) System.out.println("!!! Missing map object " + id_str);
				if(id_object != null) {
					System.out.println("   id=" + id_str);
					AddOperation ao = new AddOperation(id_str,id_object);
					rar.operationsList.add(ao);
				}
			}
			operationsChildren = listOfOperationsElement.getChildren("Delete", Namespace.getNamespace("http://www.sbml.org/sbml/level3"));
			for (Element operationsElement : operationsChildren) {
				String id_str = operationsElement.getAttributeValue("id");
				String delete_molecules_str = operationsElement.getAttributeValue("DeleteMolecules");
				RbmObject id_object = keyMap.get(id_str);
				if(id_object == null) System.out.println("!!! Missing map object " + id_str);
				int delete_molecules_int = 0;
				if(delete_molecules_str != null) {
					delete_molecules_int = Integer.parseInt(delete_molecules_str);
				}
				if(id_object != null) {
					System.out.println("   id=" + id_str + ", DeleteMolecules=" + delete_molecules_str);
					DeleteOperation dop = new DeleteOperation(id_str,id_object, delete_molecules_int);
					rar.operationsList.add(dop);
				}
			}
		}
		System.out.println("done parsing xml file");
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
