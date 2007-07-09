package org.vcell.ncbc.physics.engine;

import org.vcell.expression.ExpressionException;
import org.vcell.expression.ExpressionFactory;
import org.vcell.expression.IExpression;

import cbit.util.graph.Edge;
import cbit.util.graph.Graph;
import cbit.util.graph.Node;
import cbit.util.graph.Path;
import cbit.vcell.math.Function;
import cbit.vcell.model.Membrane;
import cbit.vcell.model.Model;
import cbit.vcell.modelapp.FeatureMapping;
import cbit.vcell.modelapp.MembraneMapping;
import cbit.vcell.modelapp.SimulationContext;

/**
 * Insert the type's description here.
 * Creation date: (2/19/2002 11:22:17 AM)
 * @author: Jim Schaff
 */
public class ElectricalGraphGenerator {
	//
	// all indexing done by edge index.
	//
	//  For example:  Epithelial Cell Model with current clamp 
	//
	//                               extra                                  
	//                                 |  \\                                
	//                                 |    \\                              
	//                               PM|     | PROBE                        
	//                                 |    /                               
	//                                 |  /                                 
	//                                cyt                                   
	//
	//    node[0] = extra
	//    node[1] = cyt
	//    edge[0] = PROBE
	//    edge[1] = PM
	//    fieldDependentVoltageExpression[0] = V_PM
	//    fieldDependentVoltageExpression[1] = null
	//    fieldTotalCurrent[0] = F_PROBE
	//    fieldTotalCurrent[1] = -F_PROBE
	//    fieldCapacitiveCurrent[0] = 0.0;
	//    fieldCapacitiveCurrent[1] = -F_PROBE - F_PM;
	//    fieldAppliedCurrent[0] = F_PROBE;
	//    fieldAppliedCurrent[1] = F_PM;
	//
	//
	//     so....      d[V_PM]/dt = (F_PM-I_PROBE)/capacitance   (= fieldCapacitiveCurrent[0]/Capacitance_0)
	//                 
	//
	//
	//  For example:  Epithelial Cell Model with voltage clamp 
	//
	//                               extra                                  
	//                                 |  \\                                
	//                                 |    \\                              
	//                               PM|     | PROBE                        
	//                                 |    /                               
	//                                 |  /                                 
	//                                cyt                                   
	//
	//    node[0] = extra
	//    node[1] = cyt
	//    edge[0] = PM
	//    edge[1] = PROBE
	//    fieldDependentVoltageExpression[0] = V_PROBE
	//    fieldDependentVoltageExpression[1] = null
	//    fieldTotalCurrent[0] = 0.001*Capacitance * d[V_PROBE(t)]/dt + F_PM;
	//    fieldTotalCurrent[1] = -0.001*Capacitance * d[V_PROBE(t)]/dt - F_PM;
	//    fieldCapacitiveCurrent[0] = 0.001*Capacitance * d[V_PROBE(t)]/dt
	//    fieldCapacitiveCurrent[1] = 0.0
	//    fieldAppliedCurrent[0] = F_PM;
	//    fieldAppliedCurrent[1] = -0.001*Capacitance * d[V_PROBE(t)]/dt - F_PM;
	//
	public static boolean bSilent = false;
/**
 * Insert the method's description here.
 * Creation date: (2/19/2002 11:24:04 AM)
 * @return cbit.vcell.mapping.potential.Graph
 * @param simContext cbit.vcell.mapping.SimulationContext
 */
public static Graph getCircuitGraph(SimulationContext simContext) throws ExpressionException {
	Graph graph = new Graph();
	
	Model model = simContext.getModel();
	//
	// add nodes to the graph (one for each Feature)
	//
	cbit.vcell.model.Structure structures[] = model.getStructures();
	for (int i = 0; i < structures.length; i++){
		if (structures[i] instanceof cbit.vcell.model.Feature){
			graph.addNode(new Node(structures[i].getName(),structures[i]));		
		}
	}
	//
	// add edges for all current clamp electrodes (always have dependent voltages)
	//
	cbit.vcell.modelapp.ElectricalStimulus stimuli[] = simContext.getElectricalStimuli();
	cbit.vcell.modelapp.Electrode groundElectrode = simContext.getGroundElectrode();
	for (int i = 0; i < stimuli.length; i++){
		cbit.vcell.modelapp.ElectricalStimulus stimulus = stimuli[i];
		//
		// get electrodes
		//
		cbit.vcell.modelapp.Electrode probeElectrode = stimulus.getElectrode();
		if (probeElectrode == null){
			throw new RuntimeException("null electrode for electrical stimulus");
		}
		if (groundElectrode == null){
			throw new RuntimeException("null ground electrode for electrical stimulus");
		}
//if (!membraneMapping.getResolved()){
		Node groundNode = graph.getNode(groundElectrode.getFeature().getName());
		Node probeNode = graph.getNode(probeElectrode.getFeature().getName());
		if (stimulus instanceof cbit.vcell.modelapp.CurrentClampStimulus){
			cbit.vcell.modelapp.CurrentClampStimulus ccStimulus = (cbit.vcell.modelapp.CurrentClampStimulus)stimulus;
			ElectricalDevice device = new SimpleElectricalDevice(org.vcell.util.TokenMangler.fixToken(ccStimulus.getName()),
																org.vcell.util.TokenMangler.fixToken(ccStimulus.getVoltageParameter().getName()),
																null,  // no specified initial voltage
																0.0,   // no capacitance
																ccStimulus.getCurrentParameter().getExpression(),
																false, // ??????? resolved membrane???
																true,  // ??????? calculate voltage???
																false); // not a voltage source
			Edge edge = new Edge(probeNode,groundNode,device);
			graph.addEdge(edge);
		}
//}
	}
	//
	// add edges for all membranes
	//	
	for (int i = 0; i < structures.length; i++){
		if (structures[i] instanceof Membrane){
			Membrane membrane = (Membrane)structures[i];
			MembraneMapping membraneMapping = (MembraneMapping)simContext.getGeometryContext().getStructureMapping(membrane);
			
//			if (!membraneMapping.getResolved()){
				Node insideNode = graph.getNode(membrane.getInsideFeature().getName());
				Node outsideNode = graph.getNode(membrane.getOutsideFeature().getName());
				double capacitance = getTotalMembraneCapacitance(simContext,membrane).evaluateConstant();
				//
				// getTotalMembraneCurrent() already converts to "outwardCurrent" so that same convension as voltage
				//
				IExpression currentSource = getTotalMembraneCurrent(simContext,membrane);
				Function initialVoltageFunction = new Function(membraneMapping.getInitialVoltageParameter().getName(),membraneMapping.getInitialVoltageParameter().getExpression());
				ElectricalDevice device = new SimpleElectricalDevice(org.vcell.util.TokenMangler.fixTokenStrict(membrane.getName()),
																	org.vcell.util.TokenMangler.fixTokenStrict(membrane.getMembraneVoltage().getName()),
																	initialVoltageFunction,
																	capacitance,
																	currentSource,
																	membraneMapping.getResolved(simContext),
																	membraneMapping.getCalculateVoltage(),
																	false);
				Edge edge = new Edge(insideNode,outsideNode,device);
				graph.addEdge(edge);
//			}
		}
	}
	//
	// add edges for all voltage clamp electrodes (ALWAYS independent voltages)
	//
	for (int i = 0; i < stimuli.length; i++){
		cbit.vcell.modelapp.ElectricalStimulus stimulus = stimuli[i];
		//
		// get electrodes
		//
		cbit.vcell.modelapp.Electrode probeElectrode = stimulus.getElectrode();
		if (probeElectrode == null){
			throw new RuntimeException("null electrode for electrical stimulus");
		}
		if (groundElectrode == null){
			throw new RuntimeException("null ground electrode for electrical stimulus");
		}
//if (!membraneMapping.getResolved()){
		Node groundNode = graph.getNode(groundElectrode.getFeature().getName());
		Node probeNode = graph.getNode(probeElectrode.getFeature().getName());
		if (stimulus instanceof cbit.vcell.modelapp.VoltageClampStimulus){
			cbit.vcell.modelapp.VoltageClampStimulus vcStimulus = (cbit.vcell.modelapp.VoltageClampStimulus)stimulus;
			ElectricalDevice device = new SimpleElectricalDevice(org.vcell.util.TokenMangler.fixTokenStrict(vcStimulus.getName()),
																org.vcell.util.TokenMangler.fixTokenStrict(vcStimulus.getVoltageParameter().getName()),
																new Function(vcStimulus.getVoltageParameter().getName(),vcStimulus.getVoltageParameter().getExpression()),
																0.0,   // no capacitance
																null,   // no current specified
																false,  // ?????? resolved membrane
																true,   // ?????? calculate voltage???
																true);  // is a voltage source
			Edge edge = new Edge(probeNode,groundNode,device);
			graph.addEdge(edge);
		}
//}
	}
	
	//System.out.println(graph);
	if (!bSilent){
		show(graph);
	}
	return graph;
}
/**
 * Insert the method's description here.
 * Creation date: (2/19/2002 12:58:13 PM)
 * @return double
 * @param simContext cbit.vcell.mapping.SimulationContext
 * @param membrane cbit.vcell.model.Membrane
 */
private static IExpression getMembraneSurfaceAreaExpression(SimulationContext simContext, Membrane membrane) throws ExpressionException {
	MembraneMapping membraneMapping = (MembraneMapping)simContext.getGeometryContext().getStructureMapping(membrane);
	FeatureMapping featureMapping = (FeatureMapping)simContext.getGeometryContext().getStructureMapping(membrane);
	IExpression totalVolFract = featureMapping.getResidualVolumeFraction(simContext);
	IExpression surfaceToVolume = membraneMapping.getSurfaceToVolumeParameter().getExpression();
	IExpression surfaceArea = ExpressionFactory.mult(surfaceToVolume, totalVolFract);

	return surfaceArea;
}
/**
 * Insert the method's description here.
 * Creation date: (2/19/2002 2:12:28 PM)
 * @return double
 * @param membraneMapping cbit.vcell.mapping.MembraneMapping
 */
private static double getSurfaceArea(SimulationContext simContext, Membrane membrane) {
	return 1.0;
}
/**
 * Insert the method's description here.
 * Creation date: (2/19/2002 12:58:13 PM)
 * @return double
 * @param simContext cbit.vcell.mapping.SimulationContext
 * @param membrane cbit.vcell.model.Membrane
 */
private static IExpression getTotalMembraneCapacitance(SimulationContext simContext, Membrane membrane) throws ExpressionException {
	MembraneMapping membraneMapping = (MembraneMapping)simContext.getGeometryContext().getStructureMapping(membrane);
	return ExpressionFactory.mult(membraneMapping.getSpecificCapacitanceParameter().getExpression(), ExpressionFactory.createExpression(getSurfaceArea(simContext,membrane)));
}
/**
 * Insert the method's description here.
 * Creation date: (2/19/2002 12:56:13 PM)
 * @return cbit.vcell.parser.Expression
 * @param simContext cbit.vcell.mapping.SimulationContext
 * @param membrane cbit.vcell.model.Membrane
 */
private static IExpression getTotalMembraneCurrent(SimulationContext simContext, Membrane membrane) throws ExpressionException {
	MembraneMapping membraneMapping = (MembraneMapping)simContext.getGeometryContext().getStructureMapping(membrane);
	if (!membraneMapping.getCalculateVoltage()){
		return ExpressionFactory.createExpression(0.0);
	}
	//
	// gather current terms
	//
	IExpression currentExp = ExpressionFactory.createExpression(0.0);
	cbit.vcell.modelapp.ReactionSpec reactionSpecs[] = simContext.getReactionContext().getReactionSpecs();
	for (int i = 0; i < reactionSpecs.length; i++){
		//
		// only include currents from this membrane from reactions that are not disabled ("excluded")
		//
		if (reactionSpecs[i].isExcluded()){
			continue;
		}
		cbit.vcell.model.ReactionStep rs = reactionSpecs[i].getReactionStep();
		if (rs.getStructure() == membrane){
			IExpression current = rs.getKinetics().getCurrentParameter().getExpression();
			if (!current.compareEqual(ExpressionFactory.createExpression(0.0))){
				//
				// change sign convension from inward current to outward current (which is consistent with voltage convension)
				//
				currentExp = ExpressionFactory.add(currentExp, ExpressionFactory.negate(ExpressionFactory.createExpression(rs.getKinetics().getCurrentParameter().getName())));
			}
		}
	}
	return currentExp.flatten();
}
/**
 * Insert the method's description here.
 * Creation date: (1/2/2004 4:10:52 PM)
 * @param graph cbit.vcell.mapping.potential.Graph
 */
private static void show(Graph graph) {
	//
	// traverses graph and calculates RHS expressions for all capacitive devices (dV/dt)
	// 
	// calculate dependent voltages as functions of independent voltages.
	//
	//
	Graph spanningTrees[] = graph.getSpanningForest();
	if (!bSilent){ 
		System.out.println("spanning tree(s):");
		for (int i = 0; i < spanningTrees.length; i++){
			System.out.println(i+") "+spanningTrees[i]);
		}
	}

	Path fundamentalCycles[] = graph.getFundamentalCycles();
	if (!bSilent){
		System.out.println("fundamental cycles:");
		for (int i = 0; i < fundamentalCycles.length; i++){
			System.out.println("   "+fundamentalCycles[i]);
		}
	}

	//
	// print out basic device information
	//
	Edge edges[] = graph.getEdges();
	if (!bSilent){
		System.out.println("\n\n basic information for each device\n");
		for (int i = 0; i < edges.length; i++){
			ElectricalDevice device = (ElectricalDevice)edges[i].getData();
			if (device.hasCapacitance()){
				System.out.println("device["+i+"].voltageName = "+device.getVName()+",  INITIAL = "+device.getInitialVoltageFunction().getExpression());
				System.out.println("device["+i+"].currentName = "+device.getIName());
				System.out.println("device["+i+"].sourceName = "+device.getSourceName()+" = "+device.getCurrentSourceExpression());
				System.out.println("device["+i+"].capacitance = "+device.getCapName()+" = "+device.getCapacitance_pF());
			}else{
				System.out.println("device["+i+"].voltageName = "+device.getVName());
				System.out.println("device["+i+"].currentName = "+device.getIName());
				System.out.println("device["+i+"].sourceName = "+device.getSourceName()+" = "+device.getCurrentSourceExpression());
				if (device.isVoltageSource()){
					System.out.println("device["+i+"].voltageExpression = "+device.getInitialVoltageFunction().getExpression());
				}
			}
			System.out.println("device["+i+"].isResolved = "+device.getResolved());
			System.out.println("device["+i+"].hasCapacitance = "+device.hasCapacitance());
			System.out.println("device["+i+"].isVoltageSource = "+device.isVoltageSource());
			System.out.println("device["+i+"].calculateVoltage = "+device.getCalculateVoltage());
			System.out.println();
		}
	}

}
}
