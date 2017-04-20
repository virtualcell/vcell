package cbit.vcell.mapping.potential;

import cbit.util.graph.Edge;
import cbit.util.graph.Graph;
import cbit.util.graph.Node;
import cbit.vcell.geometry.CompartmentSubVolume;
import cbit.vcell.mapping.AbstractMathMapping;
import cbit.vcell.mapping.CurrentDensityClampStimulus;
import cbit.vcell.mapping.ElectricalStimulus;
import cbit.vcell.mapping.Electrode;
import cbit.vcell.mapping.MembraneMapping;
import cbit.vcell.mapping.ReactionSpec;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.StructureMapping.StructureMappingParameter;
import cbit.vcell.mapping.TotalCurrentClampStimulus;
import cbit.vcell.mapping.VoltageClampStimulus;
import cbit.vcell.model.DistributedKinetics;
import cbit.vcell.model.Feature;
import cbit.vcell.model.LumpedKinetics;
import cbit.vcell.model.Membrane;
import cbit.vcell.model.Model;
import cbit.vcell.model.Model.ElectricalTopology;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.Structure;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;

public class ElectricalCircuitGraph {

	/**
	 * Insert the method's description here.
	 * Creation date: (2/19/2002 11:24:04 AM)
	 * @return cbit.vcell.mapping.potential.Graph
	 * @param simContext cbit.vcell.mapping.SimulationContext
	 */
	public static Graph getCircuitGraph(SimulationContext simContext, AbstractMathMapping mathMapping) throws ExpressionException {
		Graph graph = new Graph();
		
		Model model = simContext.getModel();
		//
		// add nodes to the graph (one for each Feature)
		//
		Structure structures[] = model.getStructures();
		for (int i = 0; i < structures.length; i++){
			if (structures[i] instanceof Feature){
				graph.addNode(new Node(structures[i].getName(),structures[i]));		
			}
		}
		//
		// add edges for all current clamp electrodes (always have dependent voltages)
		//
		ElectricalStimulus stimuli[] = simContext.getElectricalStimuli();
		Electrode groundElectrode = simContext.getGroundElectrode();
		for (int i = 0; i < stimuli.length; i++){
			ElectricalStimulus stimulus = stimuli[i];
			//
			// get electrodes
			//
			Electrode probeElectrode = stimulus.getElectrode();
			if (probeElectrode == null){
				throw new RuntimeException("null electrode for electrical stimulus");
			}
			if (groundElectrode == null){
				throw new RuntimeException("null ground electrode for electrical stimulus");
			}
			//if (!membraneMapping.getResolved()){
			Node groundNode = graph.getNode(groundElectrode.getFeature().getName());
			Node probeNode = graph.getNode(probeElectrode.getFeature().getName());
			if (stimulus instanceof CurrentDensityClampStimulus){
				CurrentDensityClampStimulus ccStimulus = (CurrentDensityClampStimulus)stimulus;
				ElectricalDevice device = new CurrentClampElectricalDevice(ccStimulus, mathMapping);
				Edge edge = new Edge(probeNode,groundNode,device);
				graph.addEdge(edge);
			}else if (stimulus instanceof TotalCurrentClampStimulus){
				TotalCurrentClampStimulus ccStimulus = (TotalCurrentClampStimulus)stimulus;
				ElectricalDevice device = new CurrentClampElectricalDevice(ccStimulus,mathMapping);
				Edge edge = new Edge(probeNode,groundNode,device);
				graph.addEdge(edge);
			}
	//}
		}
		//
		// add edges for all membranes
		//	
		ElectricalTopology electricalTopology = simContext.getModel().getElectricalTopology();
		for (int i = 0; i < structures.length; i++){
			if (structures[i] instanceof Membrane){
				Membrane membrane = (Membrane)structures[i];
				MembraneMapping membraneMapping = (MembraneMapping)simContext.getGeometryContext().getStructureMapping(membrane);
				
				Feature positiveFeature = electricalTopology.getPositiveFeature(membrane);
				Feature negativeFeature = electricalTopology.getNegativeFeature(membrane);
				if (positiveFeature!=null && negativeFeature!=null){
					Node insideNode = graph.getNode(positiveFeature.getName());
					Node outsideNode = graph.getNode(negativeFeature.getName());
					//
					// getTotalMembraneCurrent() already converts to "outwardCurrent" so that same convention as voltage
					//
					Expression currentSource = getTotalMembraneCurrent(simContext,membrane,mathMapping);
					MembraneElectricalDevice device = new MembraneElectricalDevice(membraneMapping,mathMapping);
					device.getParameterFromRole(ElectricalDevice.ROLE_TransmembraneCurrent).setExpression(currentSource);
					Edge edge = new Edge(insideNode,outsideNode,device);
					graph.addEdge(edge);
				}
			}
		}
		//
		// add edges for all voltage clamp electrodes (ALWAYS independent voltages)
		//
		for (int i = 0; i < stimuli.length; i++){
			ElectricalStimulus stimulus = stimuli[i];
			//
			// get electrodes
			//
			Electrode probeElectrode = stimulus.getElectrode();
			if (probeElectrode == null){
				throw new RuntimeException("null electrode for electrical stimulus");
			}
			if (groundElectrode == null){
				throw new RuntimeException("null ground electrode for electrical stimulus");
			}
	//if (!membraneMapping.getResolved()){
			Node groundNode = graph.getNode(groundElectrode.getFeature().getName());
			Node probeNode = graph.getNode(probeElectrode.getFeature().getName());
			if (stimulus instanceof VoltageClampStimulus){
				VoltageClampStimulus vcStimulus = (VoltageClampStimulus)stimulus;
				ElectricalDevice device = new VoltageClampElectricalDevice(vcStimulus,mathMapping);
				Edge edge = new Edge(probeNode,groundNode,device);
				graph.addEdge(edge);
			}
	//}
		}
		
		//System.out.println(graph);
		return graph;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (2/19/2002 12:56:13 PM)
	 * @return cbit.vcell.parser.Expression
	 * @param simContext cbit.vcell.mapping.SimulationContext
	 * @param membrane cbit.vcell.model.Membrane
	 */
	private static Expression getTotalMembraneCurrent(SimulationContext simContext, Membrane membrane, AbstractMathMapping mathMapping) throws ExpressionException {
		MembraneMapping membraneMapping = (MembraneMapping)simContext.getGeometryContext().getStructureMapping(membrane);
		if (!membraneMapping.getCalculateVoltage()){
			return new Expression(0.0);
		}
		//
		// gather current terms
		//
		Expression currentExp = new Expression(0.0);
		ReactionSpec reactionSpecs[] = simContext.getReactionContext().getReactionSpecs();
		StructureMappingParameter sizeParameter = membraneMapping.getSizeParameter();
		Expression area = null;
		if (simContext.getGeometry().getDimension()==0 && (sizeParameter.getExpression()==null || sizeParameter.getExpression().isZero())){
			System.out.println("size not set for membrane \""+membrane.getName()+"\", refer to Structure Mapping in Application \""+mathMapping.getSimulationContext().getName()+"\"");
			area = membraneMapping.getNullSizeParameterValue();
		} else { 
			area = new Expression(sizeParameter, mathMapping.getNameScope());
		}
	
		for (int i = 0; i < reactionSpecs.length; i++){
			//
			// only include currents from this membrane from reactions that are not disabled ("excluded")
			//
			if (reactionSpecs[i].isExcluded()){
				continue;
			}
			if (reactionSpecs[i].getReactionStep().getKinetics() instanceof DistributedKinetics){
				ReactionStep rs = reactionSpecs[i].getReactionStep();
				DistributedKinetics distributedKinetics = (DistributedKinetics)rs.getKinetics();
				if (rs.getStructure() == membrane){
					if (!distributedKinetics.getCurrentDensityParameter().getExpression().isZero()){
						//
						// change sign convension from inward current to outward current (which is consistent with voltage convension)
						//
						currentExp = Expression.add(currentExp, Expression.negate(Expression.mult(new Expression(distributedKinetics.getCurrentDensityParameter(), mathMapping.getNameScope()),area)));
					}
				}
			}else{
				ReactionStep rs = reactionSpecs[i].getReactionStep();
				LumpedKinetics lumpedKinetics = (LumpedKinetics)rs.getKinetics();
				if (rs.getStructure() == membrane){
					if (!lumpedKinetics.getLumpedCurrentParameter().getExpression().isZero()){
						//
						// change sign convension from inward current to outward current (which is consistent with voltage convension)
						//
						if (!(membraneMapping.getGeometryClass() instanceof CompartmentSubVolume)){
							throw new RuntimeException("math generation for total currents within spatial electrophysiology not yet implemented");
						}
						Expression lumpedCurrentSymbolExp = new Expression(lumpedKinetics.getLumpedCurrentParameter(), mathMapping.getNameScope());
						currentExp = Expression.add(currentExp, Expression.negate(lumpedCurrentSymbolExp));
					}
				}
			}
		}
	//	currentExp.bindExpression(mathMapping);	StructureMappingParameter sizeParameter = capacitiveDevice.getMembraneMapping().getSizeParameter();
	
		return currentExp.flatten();
	}

}
