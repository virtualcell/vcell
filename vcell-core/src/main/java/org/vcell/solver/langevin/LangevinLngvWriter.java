package org.vcell.solver.langevin;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jdom.Comment;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.vcell.model.rbm.RbmUtils;
import org.vcell.model.rbm.RuleAnalysis;
import org.vcell.model.rbm.RuleAnalysisReport;
import org.vcell.util.BeanUtils;

import cbit.vcell.export.SpringSaLaDExporter;
import cbit.vcell.geometry.GeometryClass;
import cbit.vcell.mapping.GeometryContext;
import cbit.vcell.mapping.LangevinMathMapping;
import cbit.vcell.mapping.MathMapping;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.StructureMapping;
import cbit.vcell.mapping.SimulationContext.Application;
import cbit.vcell.math.Action;
import cbit.vcell.math.CompartmentSubDomain;
import cbit.vcell.math.Constant;
import cbit.vcell.math.Function;
import cbit.vcell.math.JumpProcessRateDefinition;
import cbit.vcell.math.LangevinParticleJumpProcess;
import cbit.vcell.math.LangevinParticleMolecularType;
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
import cbit.vcell.math.SubDomain;
import cbit.vcell.math.Variable;
import cbit.vcell.math.VolumeParticleSpeciesPattern;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.model.Structure;
import cbit.vcell.parser.DivideByZeroException;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.LangevinSimulationOptions;
import cbit.vcell.solver.NFsimSimulationOptions;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationOwner;
import cbit.vcell.solver.SimulationSymbolTable;
import cbit.vcell.solver.SolverException;
//import org.jdom.output.Format;
import cbit.vcell.xml.XMLTags;

public class LangevinLngvWriter {
	

	// TODO: various collections here for the intermediate stuff as we build the lngv file from math
	
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
		if(!(so instanceof SimulationContext)) {
			throw new RuntimeException("SimulationOwner must be instance of SimulationContext");
		}
		SimulationContext simContext = (SimulationContext)so;
		GeometryContext geometryContext = simContext.getGeometryContext();
		
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

		Map<ParticleProperties, SubDomain> particlePropertiesMap = new LinkedHashMap<> ();		// initial conditions for seed species
		Map<LangevinParticleJumpProcess, SubDomain> particleJumpProcessMap = new LinkedHashMap<> ();	// list of reactions
		Set<LangevinParticleMolecularType> particleMolecularTytpeSet = new LinkedHashSet<> ();			// molecular types
		
		Enumeration<SubDomain> subDomainEnum = mathDesc.getSubDomains();
		while (subDomainEnum.hasMoreElements()) {
			SubDomain subDomain = subDomainEnum.nextElement();
			for(ParticleProperties pp : subDomain.getParticleProperties()) {
				particlePropertiesMap.put(pp, subDomain);
				ArrayList<ParticleInitialCondition> particleInitialConditions = pp.getParticleInitialConditions();
				Variable variable = pp.getVariable();
				if(!(variable instanceof VolumeParticleSpeciesPattern)) {
					continue;		// only interested in VolumeParticleSpeciesPattern
				}
				VolumeParticleSpeciesPattern vpsp = (VolumeParticleSpeciesPattern)variable;
				if(vpsp.getParticleMolecularTypePatterns().size() != 1) {
					throw new RuntimeException("A seed species size must be exactly one molecule");
				}
				if(!(vpsp.getParticleMolecularTypePatterns().get(0).getMolecularType() instanceof LangevinParticleMolecularType)) {
					throw new RuntimeException("LangevinParticleMolecularType expected");
				}
				LangevinParticleMolecularType lpmt = (LangevinParticleMolecularType)vpsp.getParticleMolecularTypePatterns().get(0).getMolecularType();
				String spName = variable.getName();
				String moleculeName = lpmt.getName();
				
				if(particleInitialConditions.size() != 1) {
					throw new RuntimeException("One initial condition per variable is required");
				}
				if(!(particleInitialConditions.get(0) instanceof ParticleInitialConditionCount)) {
					throw new RuntimeException("Count initial condition is required");
				}
				ParticleInitialConditionCount pic = (ParticleInitialConditionCount)particleInitialConditions.get(0);
				Expression count = pic.getCount();
				System.out.println(count + "");
			}
			
			
			for(ParticleJumpProcess pjp : subDomain.getParticleJumpProcesses()) {
				if(!(pjp instanceof LangevinParticleJumpProcess)) {
					throw new RuntimeException("LangevinParticleJumpProcess expected.");
				}
				LangevinParticleJumpProcess lpjp = (LangevinParticleJumpProcess)pjp;
				particleJumpProcessMap.put(lpjp, subDomain);
			}
			
			
			for(ParticleMolecularType pmt : mathDesc.getParticleMolecularTypes()) {
				if(!(pmt instanceof LangevinParticleMolecularType)) {
					throw new RuntimeException("LangevinParticleMolecularType expected.");
				}
				LangevinParticleMolecularType lpmt = (LangevinParticleMolecularType)pmt;
				particleMolecularTytpeSet.add(lpmt);
			}
		}
		
		
		StringBuilder sb = new StringBuilder();
		/* ********* BEGIN BY WRITING THE TIMES *********/
		sb.append("*** " + SpringSaLaDExporter.TIME_INFORMATION + " ***");
		sb.append("\n");
		simulation.getSolverTaskDescription().writeData(sb);	// TODO: need proper sim
		sb.append("\n");

		/* ********* WRITE THE SPATIAL INFORMATION **********/
		sb.append("*** " + SpringSaLaDExporter.SPATIAL_INFORMATION + " ***");
		sb.append("\n");
		geometryContext.writeData(sb);
		sb.append("\n");

		/* ******* WRITE THE SPECIES INFORMATION ***********/
		sb.append("*** " + SpringSaLaDExporter.MOLECULES + " ***");
		sb.append("\n");
		sb.append("\n");



			
		String ret = sb.toString();
		System.out.println(ret);
		
		return ret;
	}
	
	private static double evaluateConstant(Expression expression, SimulationSymbolTable simulationSymbolTable) throws MathException, ExpressionException{
		Expression subExp = simulationSymbolTable.substituteFunctions(expression);
		double value = subExp.evaluateConstant();
		return value;
	}
}
