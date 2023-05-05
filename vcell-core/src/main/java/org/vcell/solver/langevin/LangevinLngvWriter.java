package org.vcell.solver.langevin;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.vcell.util.Pair;

import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometrySpec;
import cbit.vcell.mapping.GeometryContext;
import cbit.vcell.mapping.ReactionRuleSpec;
import cbit.vcell.mapping.ReactionRuleSpec.Subtype;
import cbit.vcell.mapping.ReactionRuleSpec.TransitionCondition;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SpeciesContextSpec;
import cbit.vcell.mapping.SimulationContext.Application;
import cbit.vcell.math.Action;
import cbit.vcell.math.LangevinParticleJumpProcess;
import cbit.vcell.math.LangevinParticleMolecularComponent;
import cbit.vcell.math.LangevinParticleMolecularType;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.MathException;
import cbit.vcell.math.ParticleComponentStateDefinition;
import cbit.vcell.math.ParticleComponentStatePattern;
import cbit.vcell.math.ParticleJumpProcess;
import cbit.vcell.math.ParticleMolecularComponent;
import cbit.vcell.math.ParticleMolecularComponentPattern;
import cbit.vcell.math.ParticleMolecularType;
import cbit.vcell.math.ParticleMolecularTypePattern;
import cbit.vcell.math.ParticleProperties;
import cbit.vcell.math.ParticleProperties.ParticleInitialCondition;
import cbit.vcell.math.ParticleProperties.ParticleInitialConditionCount;
import cbit.vcell.math.ParticleSpeciesPattern;
import cbit.vcell.math.SubDomain;
import cbit.vcell.math.Variable;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.parser.DivideByZeroException;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.LangevinSimulationOptions;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationOwner;
import cbit.vcell.solver.SimulationSymbolTable;
import cbit.vcell.solver.SolverException;
//import org.jdom.output.Format;

public class LangevinLngvWriter {
	
	public static final boolean TrackClusters = true;			// SpringSaLaD specific
	public static final boolean InitialLocationRandom = true;
	private static final String InitialLocationRandomString = "Random";
	private static final String InitialLocationSetString = "Set";
	public static final String SourceMoleculeString = "Source";	// molecule used in creation reaction subtype (reserved name)
	public static final String SinkMoleculeString = "Sink";		// molecule used in decay reaction subtype (reserved name)
	private static final String AnchorSiteString = "Anchor";		// required name for reserved special Site of membrane species
	private static final String AnchorStateString = "Anchor";		// required name for reserved special State of the Anchor site

	public final static String SPATIAL_INFORMATION = "SYSTEM INFORMATION";
	public final static String TIME_INFORMATION = "TIME INFORMATION";
	public final static String MOLECULES = "MOLECULES";
	public final static String MOLECULE_FILES = "MOLECULE FILES";
	public final static String DECAY_REACTIONS = "CREATION/DECAY REACTIONS";
	public final static String TRANSITION_REACTIONS = "STATE TRANSITION REACTIONS";
	public final static String ALLOSTERIC_REACTIONS = "ALLOSTERIC REACTIONS";
	public final static String BINDING_REACTIONS = "BIMOLECULAR BINDING REACTIONS";
	public final static String MOLECULE_COUNTERS = "MOLECULE COUNTERS";
	public final static String STATE_COUNTERS = "STATE COUNTERS";
	public final static String BOND_COUNTERS = "BOND COUNTERS";
	public final static String SITE_PROPERTY_COUNTERS = "SITE PROPERTY COUNTERS";
	public final static String CLUSTER_COUNTERS = "CLUSTER COUNTERS";
	public final static String SYSTEM_ANNOTATION = "SYSTEM ANNOTATIONS";
	public final static String MOLECULE_ANNOTATIONS = "MOLECULE ANNOTATIONS";
	public final static String REACTION_ANNOTATIONS = "REACTION ANNOTATIONS";

	public final static String DEFAULT_FOLDER = "Default Folder";
	private String systemName;		// The system name (same as the file name, usually)
	public final static String DEFAULT_SYSTEM_NAME = "New Model";

	// various collections here for the intermediate stuff as we build the lngv file from math
	private static Map<ParticleProperties, SubDomain> particlePropertiesMap = new LinkedHashMap<> ();			// initial conditions for seed species
	private static Map<LangevinParticleJumpProcess, SubDomain> particleJumpProcessMap = new LinkedHashMap<> ();	// list of reactions
	private static Set<LangevinParticleMolecularType> particleMolecularTytpeSet = new LinkedHashSet<> ();		// molecular types
	private static GeometryContext geometryContext = null;
	
//	static ArrayList<MappingOfReactionParticipants> currentMappingOfReactionParticipants = new ArrayList<MappingOfReactionParticipants>();
//	static HashSet<BondSites> reactionReactantBondSites = new HashSet<BondSites>();

	// main work being done here
	public static String writeLangevinLngv(SimulationTask origSimTask, long randomSeed, LangevinSimulationOptions langevinSimulationOptions) throws SolverException, DivideByZeroException, ExpressionException {
		try {
			System.out.println("VCML ORIGINAL .... START\n"+origSimTask.getSimulation().getMathDescription().getVCML_database()+"\nVCML ORIGINAL .... END\n====================\n");
		} catch (MathException e1) {
			e1.printStackTrace();
		}
		
		Simulation simulation = origSimTask.getSimulation();
		SimulationOwner so = simulation.getSimulationOwner();
		if(so instanceof MathModel) {
			MathModel mm = (MathModel)so;	// TODO: must make this code compatible to math model too
			// how do I get GeometryContext for a math model?
		}
		Geometry geometry = so.getGeometry();
		GeometrySpec geometrySpec = geometry.getGeometrySpec();
		if(!(so instanceof SimulationContext)) {
			throw new RuntimeException("SimulationOwner must be instance of SimulationContext");
		}
		SimulationContext simContext = (SimulationContext)so;
		geometryContext = simContext.getGeometryContext();
		
		if(simContext.getApplicationType() != Application.SPRINGSALAD) {
			throw new RuntimeException("SpringSaLaD application type expected.");
		}
		if(simContext.getMostRecentlyCreatedMathMapping() == null) {
			throw new RuntimeException("Refresh MathDescription required");
		}

//		LangevinMathMapping mathMapping = (LangevinMathMapping)simContext.getMostRecentlyCreatedMathMapping();
//		Structure struct = null;
//		StructureMapping sm = simContext.getGeometryContext().getStructureMapping(struct);
//		GeometryClass reactionStepGeometryClass = sm.getGeometryClass();
		
		MathDescription mathDesc = simulation.getMathDescription();
		particlePropertiesMap.clear();
		particleJumpProcessMap.clear();
		particleMolecularTytpeSet.clear();
		
		Enumeration<SubDomain> subDomainEnum = mathDesc.getSubDomains();
		while (subDomainEnum.hasMoreElements()) {
			SubDomain subDomain = subDomainEnum.nextElement();
			for(ParticleProperties pp : subDomain.getParticleProperties()) {
				particlePropertiesMap.put(pp, subDomain);
			}
			
			for(ParticleJumpProcess pjp : subDomain.getParticleJumpProcesses()) {
				if(!(pjp instanceof LangevinParticleJumpProcess)) {
					throw new RuntimeException("LangevinParticleJumpProcess expected.");
				}
				LangevinParticleJumpProcess lpjp = (LangevinParticleJumpProcess)pjp;
				particleJumpProcessMap.put(lpjp, subDomain);
			}
		}
			
		for(ParticleMolecularType pmt : mathDesc.getParticleMolecularTypes()) {
			if(!(pmt instanceof LangevinParticleMolecularType)) {
				throw new RuntimeException("LangevinParticleMolecularType expected.");
			}
			LangevinParticleMolecularType lpmt = (LangevinParticleMolecularType)pmt;
			particleMolecularTytpeSet.add(lpmt);
		}
		
		StringBuilder sb = new StringBuilder();
		/* ********* BEGIN BY WRITING THE TIMES *********/
		sb.append("*** " + TIME_INFORMATION + " ***");
		sb.append("\n");
		simulation.getSolverTaskDescription().writeData(sb);	// TODO: need proper sim
		sb.append("\n");

		/* ********* WRITE THE SPATIAL INFORMATION **********/
		sb.append("*** " + SPATIAL_INFORMATION + " ***");
		sb.append("\n");
		geometryContext.writeData(sb);
		sb.append("\n");

		/* ******* WRITE THE SPECIES INFORMATION ***********/
		sb.append("*** " + MOLECULES + " ***");
		sb.append("\n");
		sb.append("\n");
		writeSpeciesInfo(sb);

		/* ******* WRITE THE MOLECULE INFORMATION FILES ***********/
		sb.append("*** " + MOLECULE_FILES + " ***");
		sb.append("\n");
		sb.append("\n");
		writeSpeciesFileInfo(sb);
		sb.append("\n");
		
		/* ******* WRITE THE CREATION / DECAY REACTIONS ***************/
		sb.append("*** " + DECAY_REACTIONS + " ***");
		sb.append("\n");
		sb.append("\n");
		writeCreationDecayReactions(sb);
		sb.append("\n");
		
		/* ******* WRITE THE TRANSITION REACTIONS **********/
		sb.append("*** " + TRANSITION_REACTIONS + " ***");
		sb.append("\n");
		sb.append("\n");

		writeTransitionReactions(sb);

		sb.append("\n");

		
			
		String ret = sb.toString();
		System.out.println(ret);
		
		return ret;
	}
	
	private static void writeTransitionReactions(StringBuilder sb) {
		for(Map.Entry<LangevinParticleJumpProcess, SubDomain> entry : particleJumpProcessMap.entrySet()) {
			LangevinParticleJumpProcess lpjp = entry.getKey();
			SubDomain subDomain = entry.getValue();
			Subtype subtype = lpjp.getSubtype();
			if(Subtype.TRANSITION == subtype) {
				ParticleSpeciesPattern pspReactant = null;
				ParticleSpeciesPattern pspProduct = null;
				for(Action action : lpjp.getActions()) {
					String operation = action.getOperation();
					if(Action.ACTION_CREATE.equals(operation)) {
						Variable var =action.getVar();
						if(!(var instanceof ParticleSpeciesPattern)) {
							throw new RuntimeException("Transition product variable must be ParticleSpeciesPattern");
						}
						pspProduct = (ParticleSpeciesPattern)var;
					} else if(Action.ACTION_DESTROY.equals(operation)) {
						Variable var = action.getVar();
						if(!(var instanceof ParticleSpeciesPattern)) {
							throw new RuntimeException("Transition reactant variable must be ParticleSpeciesPattern");
						}
						pspReactant = (ParticleSpeciesPattern)var;
					}
				}
				
				// TODO: some of these we won't need, get rid of them at the end 
				ParticleMolecularTypePattern pmtpTransitionReactant = null;
				ParticleMolecularType pmtTransitionReactant = null;
				ParticleMolecularTypePattern pmtpTransitionProduct = null;
				ParticleMolecularType pmtTransitionProduct = null;
				ParticleMolecularTypePattern pmtpConditionReactant = null;
				ParticleMolecularType pmtConditionReactant = null;
				TransitionCondition transitionCondition = lpjp.getTransitionCondition();
				if(TransitionCondition.BOUND == transitionCondition) {
					if(pspReactant.getParticleMolecularTypePatterns().size() == 1) {
						// illegal at this point, but we may change our mind
						throw new RuntimeException("Bound conditional transition reactant size must be 2");
//						pmtpReactant = pspReactant.getParticleMolecularTypePatterns().get(0);
//						pmtReactant = pmtpReactant.getMolecularType();
//						pmtpProduct = pspProduct.getParticleMolecularTypePatterns().get(0);
//						pmtProduct = pmtpProduct.getMolecularType();
					} else if(pspReactant.getParticleMolecularTypePatterns().size() == 2) {
						ParticleMolecularTypePattern pmtpReactant0 = pspReactant.getParticleMolecularTypePatterns().get(0);
						ParticleMolecularType pmtReactant0 = pmtpReactant0.getMolecularType();
						ParticleMolecularTypePattern pmtpProduct0 = pspProduct.getParticleMolecularTypePatterns().get(0);
						ParticleMolecularType pmtProduct0 = pmtpProduct0.getMolecularType();
						ParticleMolecularTypePattern pmtpReactant1 = pspReactant.getParticleMolecularTypePatterns().get(1);
						ParticleMolecularType pmtReactant1 = pmtpReactant1.getMolecularType();
						ParticleMolecularTypePattern pmtpProduct1 = pspProduct.getParticleMolecularTypePatterns().get(1);
						ParticleMolecularType pmtProduct1 = pmtpProduct1.getMolecularType();
						if(pmtReactant0 != pmtProduct0) {
							// if the order of molecules in the reactant and product are inverted, match them properly
							pmtpProduct0 = pspProduct.getParticleMolecularTypePatterns().get(1);
							pmtProduct0 = pmtpProduct0.getMolecularType();
							pmtpProduct1 = pspProduct.getParticleMolecularTypePatterns().get(0);
							pmtProduct1 = pmtpProduct1.getMolecularType();
						}
						if(pmtpReactant0.compareEqual(pmtpProduct0)) {			// this is the condition molecule
							pmtpTransitionReactant = pmtpReactant1;				// this is the transitioning molecule
							pmtTransitionReactant = pmtReactant1;
							pmtpConditionReactant = pmtpReactant0;				// this is the condition molecule
							pmtConditionReactant = pmtReactant0;
						} else if(pmtpReactant1.compareEqual(pmtpProduct1)) {	// this is the condition molecule
							pmtpTransitionReactant = pmtpReactant0;				// this is the transitioning molecule
							pmtTransitionReactant = pmtReactant0;
							pmtpConditionReactant = pmtpReactant1;				// this is the condition molecule
							pmtConditionReactant = pmtReactant1;
						} else {
							throw new RuntimeException("No matching condition found for bound conditional transition");
						}
					} else {
						throw new RuntimeException("Bound conditional transition reactant size must be 2");
					}
					// TODO: now that we know which molecule is the transitional and which is the binding condition,
					// extract the site and the state for both
					
					System.out.println("here");
				} else if(TransitionCondition.NONE == transitionCondition) {
					pmtpTransitionReactant = pspReactant.getParticleMolecularTypePatterns().get(0);
					pmtTransitionReactant = pmtpTransitionReactant.getMolecularType();
					pmtpTransitionProduct = pspProduct.getParticleMolecularTypePatterns().get(0);
					pmtTransitionProduct = pmtpTransitionProduct.getMolecularType();
					// TODO: extract the transition site and state
					
					System.out.println("here");
				} else if(TransitionCondition.FREE == transitionCondition) {
					pmtpTransitionReactant = pspReactant.getParticleMolecularTypePatterns().get(0);
					pmtTransitionReactant = pmtpTransitionReactant.getMolecularType();
					pmtpTransitionProduct = pspProduct.getParticleMolecularTypePatterns().get(0);
					pmtTransitionProduct = pmtpTransitionProduct.getMolecularType();
					// TODO: extract the transition site and state
					
					System.out.println("here");
				} else {
					throw new RuntimeException("Unexpected transition condition");
				}
				


				
				
			}
		}
		
		String ret = sb.toString();
		System.out.println(ret);
		return;
	}
	
	private static void writeCreationDecayReactions(StringBuilder sb) {
		Map<Variable, Pair<String, String>> creationDecayVariableMap = new LinkedHashMap<> ();
		for( Map.Entry<ParticleProperties, SubDomain> entry : particlePropertiesMap.entrySet()) {
			ParticleProperties pp = entry.getKey();
			Variable var = pp.getVariable();
			Pair<String, String> rates = new Pair<> ("0", "0");
			creationDecayVariableMap.put(var, rates);
		}
		for(Map.Entry<LangevinParticleJumpProcess, SubDomain> entry : particleJumpProcessMap.entrySet()) {
			LangevinParticleJumpProcess lpjp = entry.getKey();
			SubDomain subDomain = entry.getValue();
			Subtype subtype = lpjp.getSubtype();
			if(Subtype.CREATION == subtype) {
				for(Action action : lpjp.getActions()) {
					if(Action.ACTION_CREATE.equals(action.getOperation())) {	// species being created
						Pair<String, String> rates = null;
						Variable var = action.getVar();
						Expression kCreate = null;
						try {
							kCreate = lpjp.getExpressions()[0];
						} catch(Exception e) {
							throw new RuntimeException("Reaction Rate is wrong");
						}
						if(creationDecayVariableMap.containsKey(var)) {
							rates = creationDecayVariableMap.get(var);
							if(!rates.one.equals("0")) {
								throw new RuntimeException("Cannot have multiple creation reactions for the same Variable");
							}
							rates = new Pair<> (kCreate.infix(), rates.two);
						} else {
							throw new RuntimeException("Variable missing in the variables map");
						}
						creationDecayVariableMap.put(var, rates);
					}
				}
			} else if(Subtype.DECAY == subtype) {
				for(Action action : lpjp.getActions()) {
					if(Action.ACTION_DESTROY.equals(action.getOperation())) {	// species being destroyed
						Pair<String, String> rates = null;
						Variable var = action.getVar();
						Expression kDecay = null;
						try {
							kDecay = lpjp.getExpressions()[0];
						} catch(Exception e) {
							throw new RuntimeException("Reaction Rate is wrong");
						}
						if(creationDecayVariableMap.containsKey(var)) {
							rates = creationDecayVariableMap.get(var);
							if(!rates.two.equals("0")) {
								throw new RuntimeException("Cannot have multiple decay reactions for the same Variable");
							}
							rates = new Pair<> (rates.one, kDecay.infix());
						} else {
							throw new RuntimeException("Variable missing in the variables map");
						}
						creationDecayVariableMap.put(var, rates);
					}
				}
			}
		}
		for (Map.Entry<Variable, Pair<String, String>> entry : creationDecayVariableMap.entrySet()) {
			Variable var = entry.getKey();
			if(!(var instanceof ParticleSpeciesPattern)) {
				continue;
			}
			ParticleSpeciesPattern psp = (ParticleSpeciesPattern)var;
			ParticleMolecularTypePattern particleMolecularTypePattern = psp.getParticleMolecularTypePatterns().get(0);
			ParticleMolecularType pmt = particleMolecularTypePattern.getMolecularType();
			Pair<String, String> rates = entry.getValue();
			sb.append("'").append(pmt.getName()).append("' : ")
				.append("kcreate  ").append(rates.one).append("  ")
				.append("kdecay  ").append(rates.two);
			sb.append("\n");
		}
		return;
	}
	
	private static void writeSpeciesFileInfo(StringBuilder sb) {
		for( Map.Entry<ParticleProperties, SubDomain> entry : particlePropertiesMap.entrySet()) {
			ParticleProperties pp = entry.getKey();
			Variable var = pp.getVariable();
			if(!(var instanceof ParticleSpeciesPattern)) {
				continue;		// only interested in ParticleSpeciesPattern
			}
			ParticleSpeciesPattern psp = (ParticleSpeciesPattern)var;
			ParticleMolecularTypePattern particleMolecularTypePattern = psp.getParticleMolecularTypePatterns().get(0);
			ParticleMolecularType pmt = particleMolecularTypePattern.getMolecularType();
			if(SpeciesContextSpec.SourceMoleculeString.equals(pmt.getName()) || SpeciesContextSpec.SinkMoleculeString.equals(pmt.getName())) {
				continue;
			}
			sb.append("MOLECULE: " + pmt.getName() + " " + getFilename());
			sb.append("\n");
		}
	}
	
	private static void writeSpeciesInfo(StringBuilder sb) {
		for( Map.Entry<ParticleProperties, SubDomain> entry : particlePropertiesMap.entrySet()) {
			ParticleProperties pp = entry.getKey();
			SubDomain subDomain = entry.getValue();
			
			ArrayList<ParticleInitialCondition> particleInitialConditions = pp.getParticleInitialConditions();
			Variable var = pp.getVariable();
			if(!(var instanceof ParticleSpeciesPattern)) {
				continue;
			}
			ParticleSpeciesPattern psp = (ParticleSpeciesPattern)var;
			if(psp.getParticleMolecularTypePatterns().size() != 1) {
				throw new RuntimeException("A seed species size must be exactly one molecule");
			}
			ParticleMolecularTypePattern particleMolecularTypePattern = psp.getParticleMolecularTypePatterns().get(0);
			if(!(particleMolecularTypePattern.getMolecularType() instanceof LangevinParticleMolecularType)) {
				throw new RuntimeException("LangevinParticleMolecularType expected");
			}
			LangevinParticleMolecularType lpmt = (LangevinParticleMolecularType)particleMolecularTypePattern.getMolecularType();
			String spName = var.getName();
			String moleculeName = lpmt.getName();
			
			if(particleInitialConditions.size() != 1) {
				throw new RuntimeException("One initial condition per variable is required");
			}
			if(!(particleInitialConditions.get(0) instanceof ParticleInitialConditionCount)) {
				throw new RuntimeException("Count initial condition is required");
			}
			ParticleInitialConditionCount pic = (ParticleInitialConditionCount)particleInitialConditions.get(0);
			Expression count = pic.getCount();
			
			int dimension = geometryContext.getGeometry().getDimension();

			sb.append("MOLECULE: \"" + lpmt.getName() + "\" " + subDomain.getName() + 
					" Number " + pic.getCount().infix() + 
					// number of site types and number of sites is the same for the vcell implementation of springsalad
					" Site_Types " + lpmt.getComponentList().size() + " Total"  + "_Sites " + lpmt.getComponentList().size() + 
					" Total_Links " + lpmt.getInternalLinkSpec().size() + " is2D " + (dimension == 2 ? true : false));
			sb.append("\n");
			sb.append("{");
			sb.append("\n");

			for(ParticleMolecularComponent pmc : lpmt.getComponentList()) {
				if(!(pmc instanceof LangevinParticleMolecularComponent)) {
					throw new RuntimeException("LangevinParticleMolecularComponent required");
				}
				LangevinParticleMolecularComponent lpmc = (LangevinParticleMolecularComponent)pmc;
				sb.append("     ");
				lpmc.writeType(sb);
			}
			sb.append("\n");
			for(ParticleMolecularComponent pmc : lpmt.getComponentList()) {
				// a few lines that follow are needed to extract the initial state from the ParticleMolecularComponentPattern
				ParticleMolecularComponentPattern pmcp = particleMolecularTypePattern.getMolecularComponentPattern(pmc);
				ParticleComponentStatePattern pcsp = pmcp.getComponentStatePattern();
				ParticleComponentStateDefinition pcsd = pcsp.getParticleComponentStateDefinition();
				String initialState = pcsd.getName();
				LangevinParticleMolecularComponent lpmc = (LangevinParticleMolecularComponent)pmc;
				sb.append("     ");
				lpmc.writeSite(sb, lpmt.getComponentList().indexOf(lpmc), initialState);
			}
			sb.append("\n");
			Set<Pair<LangevinParticleMolecularComponent, LangevinParticleMolecularComponent>> internalLinkSpec = lpmt.getInternalLinkSpec();
			for(Pair<LangevinParticleMolecularComponent, LangevinParticleMolecularComponent> pair : lpmt.getInternalLinkSpec()) {
				sb.append("     ");
				// we take advantage of the fact that there is one to one relationship between molecular component (site type) and site
				int indexOne = lpmt.getComponentList().indexOf(pair.one);
				int indexTwo = lpmt.getComponentList().indexOf(pair.two);
				sb.append("LINK: Site " + indexOne + " ::: Site " + indexTwo);
				sb.append("\n");
			}
			
			sb.append("\n");
			sb.append("     Initial_Positions: ");
			if(InitialLocationRandom) {
				sb.append(InitialLocationRandomString);
				sb.append("\n");
			}
			sb.append("}");
			sb.append("\n");
			sb.append("\n");
		}
		System.out.println(sb.toString());
		return;
	}
	
	private static String getFilename() {	// SpringSaLaD specific, external file with molecule information
		return null;	// not implemented
	}
	
	private static double evaluateConstant(Expression expression, SimulationSymbolTable simulationSymbolTable) throws MathException, ExpressionException{
		Expression subExp = simulationSymbolTable.substituteFunctions(expression);
		double value = subExp.evaluateConstant();
		return value;
	}
}
