package org.vcell.solver.langevin;

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

import cbit.vcell.export.SpringSaLaDExporter;
import cbit.vcell.mapping.GeometryContext;
import cbit.vcell.mapping.SimulationContext;
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
	public static String writeLangevinLngv(SimulationTask origSimTask, long randomSeed, LangevinSimulationOptions langevinSimulationOptions) throws SolverException {
		try {
			System.out.println("VCML ORIGINAL .... START\n"+origSimTask.getSimulation().getMathDescription().getVCML_database()+"\nVCML ORIGINAL .... END\n====================\n");
		} catch (MathException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		Simulation simulation = origSimTask.getSimulation();
		SimulationOwner so = simulation.getSimulationOwner();
		if(!(so instanceof SimulationContext)) {
			throw new RuntimeException("SimulationOwner must be instance of SimulationContext");
		}
		SimulationContext simContext = (SimulationContext)so;
		GeometryContext geometryContext = simContext.getGeometryContext();
//		langevinSimulationOptions.getSimulationOptions();
		
		
// we don't need to clone anything since we don't have to add the extra location / mark sites (part of the trick to run multi-compartment NFSim)	
//		SimulationTask clonedSimTask = null;
//		try {
//			clonedSimTask = (SimulationTask) BeanUtils.cloneSerializable(origSimTask);
//		}catch (Exception eee){
//			throw new SolverException("failed to clone mathDescription while preparing NFSim input: "+eee.getMessage(), eee);
//		}
//		MathDescription clonedMathDesc = clonedSimTask.getSimulation().getMathDescription();
		
		
		
			// TODO: start converting the math into lgvn format (input format for the solver)
		
		StringBuilder sb = new StringBuilder();
		/* ********* BEGIN BY WRITING THE TIMES *********/
		sb.append("*** " + SpringSaLaDExporter.TIME_INFORMATION + " ***");
		sb.append("\n");
		simulation.getSolverTaskDescription().writeData(sb);	// TODO: need proper sim
		sb.append("\n");

		/* ********* WRITE THE SPATIAL INFORMATION **********/
		sb.append("*** " + SpringSaLaDExporter.SPATIAL_INFORMATION + " ***");
		sb.append("\n");
		geometryContext.writeData(sb);	// TODO: need geometry
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
