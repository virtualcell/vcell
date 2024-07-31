package org.vcell.solver.langevin;

import java.util.*;

import cbit.vcell.geometry.AnalyticSubVolume;
import cbit.vcell.geometry.SubVolume;
import cbit.vcell.model.Structure;
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
import cbit.vcell.math.JumpProcessRateDefinition;
import cbit.vcell.math.LangevinParticleJumpProcess;
import cbit.vcell.math.LangevinParticleMolecularComponent;
import cbit.vcell.math.LangevinParticleMolecularType;
import cbit.vcell.math.MacroscopicRateConstant;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.MathException;
import cbit.vcell.math.MathUtilities;
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
	private static MathDescription mathDescription = null;
	
//	static ArrayList<MappingOfReactionParticipants> currentMappingOfReactionParticipants = new ArrayList<MappingOfReactionParticipants>();
//	static HashSet<BondSites> reactionReactantBondSites = new HashSet<BondSites>();

	// main work being done here
	public static String writeLangevinLngv(Simulation simulation, long randomSeed) throws SolverException, DivideByZeroException, ExpressionException {
//		try {
//			System.out.println("VCML ORIGINAL .... START\n"+simulation.getMathDescription().getVCML_database()+"\nVCML ORIGINAL .... END\n====================\n");
//		} catch (MathException e1) {
//			e1.printStackTrace();
//		}
		
		Geometry geometry = simulation.getMathDescription().getGeometry();
		GeometrySpec geometrySpec = geometry.getGeometrySpec();

		if(!simulation.getMathDescription().isLangevin()) {
			throw new RuntimeException("Math description must be langevin");
		}

		mathDescription = simulation.getMathDescription();
		particlePropertiesMap.clear();
		particleJumpProcessMap.clear();
		particleMolecularTytpeSet.clear();
		
		Enumeration<SubDomain> subDomainEnum = mathDescription.getSubDomains();
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
			
		for(ParticleMolecularType pmt : mathDescription.getParticleMolecularTypes()) {
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
		writeTimeInformation(sb, simulation);
		sb.append("\n");

		/* ********* WRITE THE SPATIAL INFORMATION **********/
		sb.append("*** " + SPATIAL_INFORMATION + " ***");
		sb.append("\n");
		writeSpatialInformation(geometrySpec, sb);
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

		/* ******* WRITE THE ALLOSTERIC REACTIONS **********/
		sb.append("*** " + ALLOSTERIC_REACTIONS + " ***");
		sb.append("\n");
		sb.append("\n");
		writeAllostericReactions(sb);
		sb.append("\n");

		/* ******* WRITE THE BINDING REACTIONS ************/
		sb.append("*** " + BINDING_REACTIONS + " ***");
		sb.append("\n");
		sb.append("\n");
		writeBindingReactions(sb);
		sb.append("\n");

		/* ****** WRITE THE MOLECULE COUNTERS **********/
		sb.append("*** " + MOLECULE_COUNTERS + " ***");
		sb.append("\n");
		sb.append("\n");
		writeMoleculeCounters(sb);	// everything here is initialized with default
		sb.append("\n");

		/* ******  WRITE THE STATE COUNTERS *************/
		sb.append("*** " + STATE_COUNTERS + " ***");
		sb.append("\n");
		sb.append("\n");
		writeStateCounters(sb);	// everything here is initialized with default
		sb.append("\n");

		// what follows is mainly placeholders for result statistics, obviously there's none before running the simulation
		/* ***** WRITE THE BOND COUNTERS ***************/
		sb.append("*** " + BOND_COUNTERS + " ***");
		sb.append("\n");
		sb.append("\n");
		writeBondCounters(sb);	// everything here is initialized with default
		sb.append("\n");

		/* ********  WRITE THE SITE PROPERTY COUNTERS ************/
		sb.append("*** " + SITE_PROPERTY_COUNTERS + " ***");
		sb.append("\n");
		sb.append("\n");
		writeSitePropertyCounters(sb);	// everything here is initialized with default
		sb.append("\n");

		/* *************** WRITE THE TRACK CLUSTERS BOOLEAN ***********/
		sb.append("*** " + CLUSTER_COUNTERS + " ***");
		sb.append("\n");
		sb.append("\n");
		sb.append("Track_Clusters: " + SpeciesContextSpec.TrackClusters);
		sb.append("\n");
		sb.append("\n");

		// TODO: I'm not sure what's the point of using annotations here
		/* ****** WRITE THE SYSTEM ANNOTATION ********************/
		sb.append("*** " + SYSTEM_ANNOTATION + " ***");
		sb.append("\n");
		sb.append("\n");
//		systemAnnotation.printAnnotation(sb);
		sb.append("\n");

		/* ****** WRITE THE MOLECULE ANNOTATIONS *****************/
		sb.append("*** " + MOLECULE_ANNOTATIONS + " ***");
		sb.append("\n");
		sb.append("\n");
//		writeMoleculeAnnotations(sb);
		sb.append("\n");

		/* ****** WRITE THE REACTION ANNOTATIONS *****************/
		sb.append("*** " + REACTION_ANNOTATIONS + " ***");
		sb.append("\n");
		sb.append("\n");
//		writeReactionAnnotations(sb);
		sb.append("\n");
			
		String ret = sb.toString();
//		System.out.println(ret);
		
		return ret;
	}
	
	private static void writeTimeInformation(StringBuilder sb, Simulation simulation) {
		if(!simulation.getMathDescription().isLangevin()) {
			throw new RuntimeException("Langevin Math expected.");
		}
		// general stuff is in solver task description
		simulation.getSolverTaskDescription().writeTimeInformation(sb);
		
		// for fast simulation for a simple transition state model, select the following time simulation options: 
		// - ending:				0.01	(langevin: total time)
		// - time step (default):	1E-7 	(langevin: dt)
		// - output interval:		1E-4	(langevin: dt_data)
		LangevinSimulationOptions lso = simulation.getSolverTaskDescription().getLangevinSimulationOptions();
		sb.append("dt_spring: " + lso.getIntervalSpring());		// 1.00E-9 default
		sb.append("\n");
		sb.append("dt_image: " + lso.getIntervalImage());		// 1.00E-4 default
		sb.append("\n");

	}
	
	private static void writeBindingReactions(StringBuilder sb) {
		Map<String, LangevinParticleJumpProcess> nameToProcessDirect = new LinkedHashMap<> ();
		Map<String, LangevinParticleJumpProcess> nameToProcessReverse = new LinkedHashMap<> ();		// need this only for reverse rate
		for(Map.Entry<LangevinParticleJumpProcess, SubDomain> entry : particleJumpProcessMap.entrySet()) {
			LangevinParticleJumpProcess lpjp = entry.getKey();
			Subtype subtype = lpjp.getSubtype();
			if(Subtype.BINDING == subtype) {
				if(!lpjp.getName().endsWith("_reverse")) {
					String lpjpName = lpjp.getName();
					nameToProcessDirect.put(lpjpName, lpjp);
				} else {
					String lpjpName = lpjp.getName().substring(0, lpjp.getName().lastIndexOf("_reverse"));
					nameToProcessReverse.put(lpjpName, lpjp);
				}
			}
		}

		for(Map.Entry<String, LangevinParticleJumpProcess> entry : nameToProcessDirect.entrySet()) {
			Map<ParticleMolecularTypePattern, ParticleMolecularComponentPattern> pmtpReactantBondMap = new LinkedHashMap<> ();
			String name = entry.getKey();
			LangevinParticleJumpProcess lpjpDirect = entry.getValue();
			
			for(Action action : lpjpDirect.getActions()) {
				String operation = action.getOperation();
				if(Action.ACTION_CREATE.equals(operation)) {
					; // don't need any product info
				} else if(Action.ACTION_DESTROY.equals(operation)) {
					Variable var = action.getVar();
					if(!(var instanceof ParticleSpeciesPattern)) {
						throw new RuntimeException("Binding reaction reactant variable must be ParticleSpeciesPattern");
					}
					ParticleSpeciesPattern pspReactant = (ParticleSpeciesPattern)var;
					if(pspReactant.getParticleMolecularTypePatterns().size() != 1) {
						throw new RuntimeException("Each reactant of a binding reaction must have exactly 1 molecule");
					}
					// we don't know yet which site will bind, so we initialize with null
					pmtpReactantBondMap.put(pspReactant.getParticleMolecularTypePatterns().get(0), null);
				}
			}
			if(pmtpReactantBondMap.size() != 2) {
				throw new RuntimeException("A binding reaction must have exactly 2 single molecule participants");
			}
			
			// we really only care about the reactants, each must have one single unbound site
			// which is the one that will create the bond with the other reactant's unbound site
			for(Map.Entry<ParticleMolecularTypePattern, ParticleMolecularComponentPattern> entry1 : pmtpReactantBondMap.entrySet()) {
				ParticleMolecularTypePattern pmtp = entry1.getKey();
				int numNonTrivialBonds = 0;
				for(ParticleMolecularComponentPattern pmcp : pmtp.getMolecularComponentPatternList()) {
					if(ParticleMolecularComponentPattern.ParticleBondType.Possible != pmcp.getBondType()) {
						if(ParticleMolecularComponentPattern.ParticleBondType.None != pmcp.getBondType()) {
							throw new RuntimeException("Each reactant of a binding reaction cannot have any 'Exists' or 'Specified' bond");
						}
						pmtpReactantBondMap.put(pmtp, pmcp);
						numNonTrivialBonds++;
					}
				}
				if(numNonTrivialBonds != 1) {
					throw new RuntimeException("Each reactant of a binding reaction must have exactly one non-trivial bond");
				}
			}
			
			// calculate onRate as string, from lpjpDirect
			String onRate = null;
			Expression kOn = null;
			MacroscopicRateConstant mrc = null;
			JumpProcessRateDefinition particleRateDefinition = lpjpDirect.getParticleRateDefinition();
			if(particleRateDefinition instanceof MacroscopicRateConstant) {
				mrc = (MacroscopicRateConstant)particleRateDefinition;
			} else {
				throw new RuntimeException("Rate definition must be MacroscopicRateConstant");
			}
			try {
				kOn = mrc.getExpression();
			} catch(Exception e) {
				throw new RuntimeException("Reaction Rate expression is wrong");
			}
			Expression exp = null;
			try {
				exp = MathUtilities.substituteFunctions(kOn, mathDescription, false);
			} catch (ExpressionException e) {
				throw new RuntimeException("kOn substitution failed, " + e.getMessage());
			}
			try {
				double rate = exp.evaluateConstant();
				onRate = Double.toString(rate);
			} catch (Exception e) {
				throw new RuntimeException("rate must be a number");
			}
			
			// calculate offRate as string, from lpjpReverse
			String offRate = null;
			LangevinParticleJumpProcess lpjpReverse = nameToProcessReverse.get(name);
			Expression kOff = null;
			mrc = null;
			particleRateDefinition = lpjpReverse.getParticleRateDefinition();
			if(particleRateDefinition instanceof MacroscopicRateConstant) {
				mrc = (MacroscopicRateConstant)particleRateDefinition;
			} else {
				throw new RuntimeException("Rate definition must be MacroscopicRateConstant");
			}
			try {
				kOff = mrc.getExpression();
			} catch(Exception e) {
				throw new RuntimeException("Reaction Rate expression is wrong");
			}
			exp = null;
			try {
				exp = MathUtilities.substituteFunctions(kOff, mathDescription, false);
			} catch (ExpressionException e) {
				throw new RuntimeException("kOff substitution failed, " + e.getMessage());
			}
			try {
				double rate = exp.evaluateConstant();
				offRate = Double.toString(rate);
			} catch (Exception e) {
				throw new RuntimeException("rate must be a number");
			}

			Set<Map.Entry<ParticleMolecularTypePattern, ParticleMolecularComponentPattern>> mapSet = pmtpReactantBondMap.entrySet();
			Map.Entry<ParticleMolecularTypePattern, ParticleMolecularComponentPattern> elementOne = 
					(new ArrayList<Map.Entry<ParticleMolecularTypePattern, ParticleMolecularComponentPattern>>(mapSet)).get(0);
			Map.Entry<ParticleMolecularTypePattern, ParticleMolecularComponentPattern> elementTwo = 
					(new ArrayList<Map.Entry<ParticleMolecularTypePattern, ParticleMolecularComponentPattern>>(mapSet)).get(1);
			String nameOne = "Any_State";
			String nameTwo = "Any_State";
			if(elementOne.getValue().getComponentStatePattern().getParticleComponentStateDefinition() != null) {
				nameOne = elementOne.getValue().getComponentStatePattern().getParticleComponentStateDefinition().getName();
			}
			if(elementTwo.getValue().getComponentStatePattern().getParticleComponentStateDefinition() != null) {
				nameTwo = elementTwo.getValue().getComponentStatePattern().getParticleComponentStateDefinition().getName();
			}

			// finally write the BINDING_REACTIONS block for each binding reaction
			sb.append("'").append(lpjpDirect.getName()).append("'       ");
			sb.append("'").append(elementOne.getKey().getMolecularType().getName()).append("' : '")
				.append(elementOne.getValue().getMolecularComponent().getName()).append("' : '")
				.append(nameOne);
				sb.append("'  +  '");
			sb.append(elementTwo.getKey().getMolecularType().getName()).append("' : '")
				.append(elementTwo.getValue().getMolecularComponent().getName()).append("' : '")
				.append(nameTwo);
			
			sb.append("'  kon  ").append(onRate);
			sb.append("  koff ").append(offRate);
			sb.append("  Bond_Length ").append(Double.toString(lpjpDirect.getBondLength()));
			sb.append("\n");
		}
		String ret = sb.toString();
		System.out.println(ret);
		return;
	}
	
	private static void writeAllostericReactions(StringBuilder sb) {
		for(Map.Entry<LangevinParticleJumpProcess, SubDomain> entry : particleJumpProcessMap.entrySet()) {
			LangevinParticleJumpProcess lpjp = entry.getKey();
			Subtype subtype = lpjp.getSubtype();
			if(Subtype.ALLOSTERIC == subtype) {
				ParticleSpeciesPattern pspReactant = null;
				ParticleSpeciesPattern pspProduct = null;
				for(Action action : lpjp.getActions()) {
					String operation = action.getOperation();
					if(Action.ACTION_CREATE.equals(operation)) {
						Variable var =action.getVar();
						if(!(var instanceof ParticleSpeciesPattern)) {
							throw new RuntimeException("Allosteric product variable must be ParticleSpeciesPattern");
						}
						pspProduct = (ParticleSpeciesPattern)var;
					} else if(Action.ACTION_DESTROY.equals(operation)) {
						Variable var = action.getVar();
						if(!(var instanceof ParticleSpeciesPattern)) {
							throw new RuntimeException("Allosteric reactant variable must be ParticleSpeciesPattern");
						}
						pspReactant = (ParticleSpeciesPattern)var;
					}
				}
				
				// calculate onRate as string
				Expression kOn = null;
				MacroscopicRateConstant mrc = null;
				JumpProcessRateDefinition particleRateDefinition = lpjp.getParticleRateDefinition();
				if(particleRateDefinition instanceof MacroscopicRateConstant) {
					mrc = (MacroscopicRateConstant)particleRateDefinition;
				} else {
					throw new RuntimeException("Rate definition must be MacroscopicRateConstant");
				}
				try {
					kOn = mrc.getExpression();
				} catch(Exception e) {
					throw new RuntimeException("Reaction Rate expression is wrong");
				}
				Expression exp = null;
				String onRate = null;
				try {
					exp = MathUtilities.substituteFunctions(kOn, mathDescription, false);
				} catch (ExpressionException e) {
					throw new RuntimeException("kOn substitution failed, " + e.getMessage());
				}
				try {
					double rate = exp.evaluateConstant();
					onRate = Double.toString(rate);
				} catch (Exception e) {
					throw new RuntimeException("rate must be a number");
				}
				
				// allosteric is a type of conditional transition
				ParticleMolecularTypePattern pmtpReactant = null;			// allosteric reactant molecule
				ParticleMolecularTypePattern pmtpProduct = null;
				ParticleMolecularComponentPattern pmcpTransitionReactant = null;	// transition reactant site
				ParticleComponentStateDefinition pcsdTransitionReactant = null;		// begin transition state
				ParticleComponentStateDefinition pcsdTransitionProduct = null;		// end transition state
				ParticleMolecularComponentPattern pmcpConditionReactant = null;		// condition reactant site
				ParticleComponentStateDefinition pcsdConditionReactant = null;		// condition state

				if(pspReactant.getParticleMolecularTypePatterns().size() == 1) {
					pmtpReactant = pspReactant.getParticleMolecularTypePatterns().get(0);
					pmtpProduct = pspProduct.getParticleMolecularTypePatterns().get(0);
				} else {
					throw new RuntimeException("Bound conditional transition reactant size must be 2");
				}

				// now identify the alosteric condition and transition
				for(ParticleMolecularComponentPattern pmcpReactant : pmtpReactant.getMolecularComponentPatternList()) {
					ParticleComponentStatePattern pcspReactant = pmcpReactant.getComponentStatePattern();
					if(pcspReactant.isAny()) {
						continue;
					}
					ParticleComponentStateDefinition pcsdReactant = pcspReactant.getParticleComponentStateDefinition();
					ParticleMolecularComponentPattern pmcpProduct = pmtpProduct.getMolecularComponentPattern(pmcpReactant.getMolecularComponent());
					ParticleComponentStateDefinition pcsdProduct = pmcpProduct.getComponentStatePattern().getParticleComponentStateDefinition();
					if(pcsdReactant.getName().equals(pcsdProduct.getName())) {		// allosteric condition
						pmcpConditionReactant = pmcpReactant;
						pcsdConditionReactant = pcsdReactant;
					} else {														// allosteric transition
						pmcpTransitionReactant = pmcpReactant;
						pcsdTransitionReactant = pcsdReactant;
						pcsdTransitionProduct = pcsdProduct;
					}
				}
				
				int transitionIndex = pmtpReactant.getMolecularType().getComponentList().indexOf(pmcpTransitionReactant.getMolecularComponent());
				int conditionIndex = pmtpReactant.getMolecularType().getComponentList().indexOf(pmcpConditionReactant.getMolecularComponent());
				// finally build the transition block
				sb.append("'").append(lpjp.getName()).append("' ::     ");
				sb.append("'").append(pmtpReactant.getMolecularType().getName()).append("' : ")
					.append("Site ").append(transitionIndex).append(" : '")
					.append(pcsdTransitionReactant.getName());
				sb.append("' --> '");
				sb.append(pcsdTransitionProduct.getName());
				sb.append("'  Rate ").append(onRate);
				sb.append(" Allosteric_Site ").append(conditionIndex);
				sb.append(" State '").append(pcsdConditionReactant.getName()).append("'");
				sb.append("\n");
			}
		}
		String ret = sb.toString();
		System.out.println(ret);
		return;
	}
	
	
	private static void writeTransitionReactions(StringBuilder sb) {
		for(Map.Entry<LangevinParticleJumpProcess, SubDomain> entry : particleJumpProcessMap.entrySet()) {
			LangevinParticleJumpProcess lpjp = entry.getKey();
//			SubDomain subDomain = entry.getValue();
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
				
				Expression kOn = null;
				MacroscopicRateConstant mrc = null;
				JumpProcessRateDefinition particleRateDefinition = lpjp.getParticleRateDefinition();
				if(particleRateDefinition instanceof MacroscopicRateConstant) {
					mrc = (MacroscopicRateConstant)particleRateDefinition;
				} else {
					throw new RuntimeException("Rate definition must be MacroscopicRateConstant");
				}
				try {
					kOn = mrc.getExpression();
				} catch(Exception e) {
					throw new RuntimeException("Reaction Rate expression is wrong");
				}
				Expression exp = null;
				String onRate = null;
				try {
					exp = MathUtilities.substituteFunctions(kOn, mathDescription, false);
				} catch (ExpressionException e) {
					throw new RuntimeException("kOn substitution failed, " + e.getMessage());
				}
				try {
					double rate = exp.evaluateConstant();
					onRate = Double.toString(rate);
				} catch (Exception e) {
					throw new RuntimeException("rate must be a number");
				}
				
				ParticleMolecularTypePattern pmtpTransitionReactant = null;			// transition reactant molecule
				ParticleMolecularTypePattern pmtpTransitionProduct = null;
				ParticleMolecularTypePattern pmtpConditionReactant = null;			// condition reactant molecule
				ParticleMolecularComponentPattern pmcpTransitionReactant = null;	// transition reactant site
				ParticleComponentStateDefinition pcsdTransitionReactant = null;		// begin transition state
				ParticleComponentStateDefinition pcsdTransitionProduct = null;		// end transition state
				ParticleMolecularComponentPattern pmcpConditionReactant = null;		// condition reactant site
				ParticleComponentStateDefinition pcsdConditionReactant = null;		// condition state

				TransitionCondition transitionCondition = lpjp.getTransitionCondition();
				if(TransitionCondition.BOUND == transitionCondition) {
					if(pspReactant.getParticleMolecularTypePatterns().size() == 1) {
						// illegal, bound transitions must have separate condition and transition molecules by convention
						throw new RuntimeException("Bound conditional transition reactant size must be 2");
					} else if(pspReactant.getParticleMolecularTypePatterns().size() == 2) {
						ParticleMolecularTypePattern pmtpReactant0 = pspReactant.getParticleMolecularTypePatterns().get(0);
						ParticleMolecularTypePattern pmtpProduct0 = pspProduct.getParticleMolecularTypePatterns().get(0);
						ParticleMolecularTypePattern pmtpReactant1 = pspReactant.getParticleMolecularTypePatterns().get(1);
						ParticleMolecularTypePattern pmtpProduct1 = pspProduct.getParticleMolecularTypePatterns().get(1);
						if(pmtpReactant0.getMolecularType() != pmtpProduct0.getMolecularType()) {
							// if the order of molecules in the reactant and product are inverted, match them properly
							pmtpProduct0 = pspProduct.getParticleMolecularTypePatterns().get(1);
							pmtpProduct1 = pspProduct.getParticleMolecularTypePatterns().get(0);
						}
						if(pmtpReactant0.compareEqual(pmtpProduct0)) {
							pmtpTransitionReactant = pmtpReactant1;				// transition reactant molecule
							pmtpTransitionProduct = pmtpProduct1;				// transition product molecule
							pmtpConditionReactant = pmtpReactant0;				// this is the condition molecule
						} else if(pmtpReactant1.compareEqual(pmtpProduct1)) {
							pmtpTransitionReactant = pmtpReactant0;				// transition reactant molecule
							pmtpTransitionProduct = pmtpProduct0;				// transition product molecule
							pmtpConditionReactant = pmtpReactant1;				// this is the condition molecule
						} else {
							throw new RuntimeException("No matching condition found for bound conditional transition");
						}
					} else {
						throw new RuntimeException("Bound conditional transition reactant size must be 2");
					}
					// identify the binding condition site and state 
					for(ParticleMolecularComponentPattern pmcp : pmtpConditionReactant.getMolecularComponentPatternList()) {
						if(pmcp.getComponentStatePattern().isAny()) {
							continue;
						}
						ParticleComponentStatePattern pcsp = pmcp.getComponentStatePattern();
						pmcpConditionReactant = pmcp;
						pcsdConditionReactant = pcsp.getParticleComponentStateDefinition();
						break;		// found the one and only condition
					}
				} else if(TransitionCondition.NONE == transitionCondition) {
					pmtpTransitionReactant = pspReactant.getParticleMolecularTypePatterns().get(0);
					pmtpTransitionProduct = pspProduct.getParticleMolecularTypePatterns().get(0);
				} else if(TransitionCondition.FREE == transitionCondition) {
					pmtpTransitionReactant = pspReactant.getParticleMolecularTypePatterns().get(0);
					pmtpTransitionProduct = pspProduct.getParticleMolecularTypePatterns().get(0);
				} else {
					throw new RuntimeException("Unexpected transition condition");
				}
				
				// identify the transition site and the starting and ending state 
				for(ParticleMolecularComponentPattern pmcp : pmtpTransitionReactant.getMolecularComponentPatternList()) {
					if(pmcp.getComponentStatePattern().isAny()) {
						continue;
					}
					ParticleComponentStatePattern pcsp = pmcp.getComponentStatePattern();
					pmcpTransitionReactant = pmcp;
					pcsdTransitionReactant = pcsp.getParticleComponentStateDefinition();
					break;		// found the one and only transition
				}
				for(ParticleMolecularComponentPattern pmcp : pmtpTransitionProduct.getMolecularComponentPatternList()) {
					if(pmcp.getComponentStatePattern().isAny()) {
						continue;
					}
					ParticleComponentStatePattern pcsp = pmcp.getComponentStatePattern();
					pcsdTransitionProduct = pcsp.getParticleComponentStateDefinition();
					break;		// found the one and only transition
				}

				// finally build the transition block
				sb.append("'").append(lpjp.getName()).append("' ::     ");
				sb.append("'").append(pmtpTransitionReactant.getMolecularType().getName()).append("' : '")
					.append(pmcpTransitionReactant.getMolecularComponent().getName()).append("' : '")
					.append(pcsdTransitionReactant.getName());
				sb.append("' --> '");
				sb.append(pcsdTransitionProduct.getName());
				sb.append("'  Rate ").append(onRate);
				sb.append("  Condition ").append(transitionCondition.lngvName);
				if(TransitionCondition.BOUND == transitionCondition) {
					sb.append(" '").append(pmtpConditionReactant.getMolecularType().getName()).append("' : '")
					.append(pmcpConditionReactant.getMolecularComponent().getName()).append("' : '")
					.append(pcsdConditionReactant);
				}
				sb.append("\n");
			}						// end if Subtype.TRANSITION
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
			Subtype subtype = lpjp.getSubtype();
			if(Subtype.CREATION == subtype) {
				for(Action action : lpjp.getActions()) {
					if(Action.ACTION_CREATE.equals(action.getOperation())) {	// species being created
						Pair<String, String> rates = null;
						Variable var = action.getVar();
						MacroscopicRateConstant mrc = null;
						JumpProcessRateDefinition particleRateDefinition = lpjp.getParticleRateDefinition();
						if(particleRateDefinition instanceof MacroscopicRateConstant) {
							mrc = (MacroscopicRateConstant)particleRateDefinition;
						} else {
							throw new RuntimeException("Rate definition must be MacroscopicRateConstant");
						}
						Expression kCreate = null;
						try {
							kCreate = mrc.getExpression();
						} catch(Exception e) {
							throw new RuntimeException("Reaction Rate expression is wrong");
						}
						if(creationDecayVariableMap.containsKey(var)) {
							rates = creationDecayVariableMap.get(var);
							if(!rates.one.equals("0")) {
								throw new RuntimeException("Cannot have multiple creation reactions for the same Variable");
							}
							Expression exp = null;
							String creationRate = null;
							try {
								exp = MathUtilities.substituteFunctions(kCreate, mathDescription, false);
							} catch (ExpressionException e) {
								throw new RuntimeException("kCreate substitution failed, " + e.getMessage());
							}
							try {
								double rate = exp.evaluateConstant();
								creationRate = Double.toString(rate);
							} catch (Exception e) {
								throw new RuntimeException("rate must be a number");
							}
							rates = new Pair<> (creationRate, rates.two);
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
						MacroscopicRateConstant mrc = null;
						JumpProcessRateDefinition particleRateDefinition = lpjp.getParticleRateDefinition();
						if(particleRateDefinition instanceof MacroscopicRateConstant) {
							mrc = (MacroscopicRateConstant)particleRateDefinition;
						} else {
							throw new RuntimeException("Rate definition must be MacroscopicRateConstant");
						}
						Expression kDecay = null;
						try {
							kDecay = mrc.getExpression();
						} catch(Exception e) {
							throw new RuntimeException("Reaction Rate expression is wrong");
						}
						if(creationDecayVariableMap.containsKey(var)) {
							rates = creationDecayVariableMap.get(var);
							if(!rates.two.equals("0")) {
								throw new RuntimeException("Cannot have multiple decay reactions for the same Variable");
							}
							Expression exp = null;
							String decayRate = null;
							try {
								exp = MathUtilities.substituteFunctions(kDecay, mathDescription, false);
							} catch (ExpressionException e) {
								throw new RuntimeException("kDecay substitution failed, " + e.getMessage());
							}
							try {
								double rate = exp.evaluateConstant();
								decayRate = Double.toString(rate);
							} catch (Exception e) {
								throw new RuntimeException("rate must be a number");
							}
							rates = new Pair<> (rates.one, decayRate);
						} else {
							if(var == null) {
								throw new RuntimeException("Variable in the variables map is 'null'");
							} else {
								throw new RuntimeException("Variable '" + var.getName() + "' missing in the variables map");
							}
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
			String scount;
			try {
				Expression exp = MathUtilities.substituteFunctions(count, mathDescription, true);
				exp = exp.flatten();
				double ddd = exp.evaluateConstant();
				scount = Integer.toString((int)ddd);
			} catch (Exception e) {
				throw new RuntimeException("Initial concentration must be a number");
			}

			sb.append("MOLECULE: \"" + lpmt.getName() + "\" " + subDomain.getName() +
					" Number " + scount + 
					// number of site types and number of sites is the same for the vcell implementation of springsalad
					" Site_Types " + lpmt.getComponentList().size() + " Total"  + "_Sites " + lpmt.getComponentList().size() + 
					" Total_Links " + lpmt.getInternalLinkSpec().size() + " is2D " + lpmt.getIs2D());
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
				// TODO: in vcell the index starts with 1, in ssld is zero
				// need to adjust when importing and exporting
				// see also MolecularInternalLinkSpec.writeLink() == the other export
				// see also SsldUtils.fromSsld() == import
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

	public static void writeSpatialInformation(GeometrySpec geometrySpec, StringBuilder sb) {    // SpringSaLaD exporting the time information
        if (geometrySpec.getDimension() != 3) {
            throw new RuntimeException("SpringSaLaD requires 3D geometry");
        }
        Double membrane_z;
		// test for the presence of the required subvolumes
		String subvolume_err_msg = "SpringSaLaD requires an Analytic Geometry with 2 SubVolumes, " +
				"the first one must be named '"+Structure.SpringStructureEnum.Intracellular+"' " +
				"and the second one named '"+Structure.SpringStructureEnum.Extracellular+"'.";
		if (geometrySpec.getNumSubVolumes()!=2
				|| !(geometrySpec.getSubVolumes(0) instanceof AnalyticSubVolume intracellularSubVolume)
				|| !(geometrySpec.getSubVolumes(1) instanceof AnalyticSubVolume extracellularSubVolume)
				|| !(geometrySpec.getSubVolumes(0).getName().equals(Structure.SpringStructureEnum.Intracellular.name()))
				|| !(geometrySpec.getSubVolumes(1).getName().equals(Structure.SpringStructureEnum.Extracellular.name()))) {
			throw new RuntimeException(subvolume_err_msg);
		}

        // test Intracellular for expression of the form "z < number"
		{
			var expression = intracellularSubVolume.getExpression();
			boolean bValidExpression = expression.isRelational()
					&& expression.extractTopLevelTerm().getOperator().equals("<")
					&& expression.extractTopLevelTerm().getOperands()[0].isIdentifier()
					&& expression.extractTopLevelTerm().getOperands()[0].infix().equals("z")
					&& expression.extractTopLevelTerm().getOperands()[1].isNumeric();
			if (bValidExpression) {
				try {
					membrane_z = expression.extractTopLevelTerm().getOperands()[1].evaluateConstant();
				} catch (ExpressionException e) {
					throw new RuntimeException("unexpected failure retrieving numeric value from Intracellular expression", e);
				}
			} else {
				throw new RuntimeException("Expecting expression for Geometry SubVolume " +
						"'" + Structure.SpringStructureEnum.Intracellular + "' to be of form " +
						"'z < number' (e.g. z < 0.09)), " +
						"found '" + expression.infix() + "'");
			}
		}

		// test Extracellular for expression 1.0
		{
			var expression = extracellularSubVolume.getExpression();
			boolean bValidExpression = expression.isNumeric() && expression.compareEqual(new Expression(1.0));
			if (!bValidExpression) {
				throw new RuntimeException("Expecting expression for Geometry SubVolume " +
						"'" + Structure.SpringStructureEnum.Extracellular + "' to be '1.0', " +
						"found '" + expression.infix() + "'");
			}
		}

		double min_Z = geometrySpec.getOrigin().getZ();
		double max_Z = min_Z + geometrySpec.getExtent().getZ();
		double Lz_extra = max_Z - membrane_z;
		double Lz_intra = membrane_z - min_Z;

		sb.append("L_x: " + geometrySpec.getExtent().getX());        // 0.1
        sb.append("\n");
        sb.append("L_y: " + geometrySpec.getExtent().getY());
        sb.append("\n");
        sb.append("L_z_out: " + Lz_extra);		// 0.01
        sb.append("\n");
        sb.append("L_z_in: " + Lz_intra);		// 0.09
        sb.append("\n");
        sb.append("Partition Nx: 10");			// TODO: make number of partitions on each axes part of Langevin Simulations Options
        sb.append("\n");
        sb.append("Partition Ny: 10");
        sb.append("\n");
        sb.append("Partition Nz: 10");
        sb.append("\n");
    }

	public static void writeMoleculeCounters(StringBuilder sb) {
		for(ParticleMolecularType pmt : particleMolecularTytpeSet) {
			if(SpeciesContextSpec.SourceMoleculeString.equals(pmt.getName()) || SpeciesContextSpec.SinkMoleculeString.equals(pmt.getName())) {
				continue;	// skip the Source and the Sink molecules
			}
			sb.append("'").append(pmt.getName()).append("' : ")
					.append("Measure Total Free Bound");
			sb.append("\n");
		}
	}
	public static void writeStateCounters(StringBuilder sb) {
		for(ParticleMolecularType pmt : particleMolecularTytpeSet) {
			if(SpeciesContextSpec.SourceMoleculeString.equals(pmt.getName()) || SpeciesContextSpec.SinkMoleculeString.equals(pmt.getName())) {
				continue;
			}
			List <ParticleMolecularComponent> pmcList = pmt.getComponentList();
			for(ParticleMolecularComponent pmc : pmcList)  {
				List <ParticleComponentStateDefinition> pcsdList = pmc.getComponentStateDefinitions();
				for(ParticleComponentStateDefinition pcsd : pcsdList) {
					sb.append("'").append(pmt.getName()).append("' : ")
							.append("'").append(pmc.getName()).append("' : ")
							.append("'").append(pcsd.getName()).append("' : ")
							.append("Measure Total Free Bound");
					sb.append("\n");
				}
			}
		}
	}
	public static void writeBondCounters(StringBuilder sb) {
		for (Map.Entry<LangevinParticleJumpProcess,SubDomain> entry : particleJumpProcessMap.entrySet()) {
			LangevinParticleJumpProcess lpjp = entry.getKey();
			if(lpjp.getSubtype() == ReactionRuleSpec.Subtype.BINDING) {
				if(lpjp.getName().endsWith("_reverse")) {
					continue;
				}
				sb.append("'").append(lpjp.getName()).append("' : ")
						.append("Counted");
				sb.append("\n");
			}
		}
	}
	public static void writeSitePropertyCounters(StringBuilder sb) {
		for(ParticleMolecularType pmt : particleMolecularTytpeSet) {
			if (SpeciesContextSpec.SourceMoleculeString.equals(pmt.getName()) || SpeciesContextSpec.SinkMoleculeString.equals(pmt.getName())) {
				continue;
			}
			List<ParticleMolecularComponent> pmcList = pmt.getComponentList();
			for(int i=0; i<pmcList.size(); i++) {
				ParticleMolecularComponent pmc = pmcList.get(i);
				sb.append("'").append(pmt.getName()).append("' ")
						.append("Site " + i).append(" : ")
						.append("Track Properties true");
				sb.append("\n");
			}
		}
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
