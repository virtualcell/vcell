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
import cbit.vcell.solver.SimulationSymbolTable;
import cbit.vcell.solver.SolverException;
//import org.jdom.output.Format;
import cbit.vcell.xml.XMLTags;

public class LangevinLngvWriter {
	

	// TODO: various collections here for the intermediate stuff as we build the lngv file from math
	
//	static ArrayList<MappingOfReactionParticipants> currentMappingOfReactionParticipants = new ArrayList<MappingOfReactionParticipants>();
//	static HashSet<BondSites> reactionReactantBondSites = new HashSet<BondSites>();

	// main work being done here
	public static String writeLangevinLngv(SimulationTask origSimTask, long randomSeed, LangevinSimulationOptions langevinSimulationOptions, boolean bUseLocationMarks) throws SolverException {
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
					for (String location : locations) {
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
		
		String lngvString = "empty";
		return lngvString;
	}
	
	private static double evaluateConstant(Expression expression, SimulationSymbolTable simulationSymbolTable) throws MathException, ExpressionException{
		Expression subExp = simulationSymbolTable.substituteFunctions(expression);
		double value = subExp.evaluateConstant();
		return value;
	}
}
