package org.vcell.ncbc.physics.engine;

import org.vcell.ncbc.physics.engine.ElectricalDevice;

import cbit.vcell.matrix.RationalExp;
import cbit.vcell.matrix.RationalExpMatrix;
import cbit.vcell.matrix.MatrixException;
import cbit.vcell.math.Function;
import cbit.vcell.math.Variable;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.math.MathDescription;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.model.Membrane;
import cbit.vcell.model.Model;
import cbit.vcell.modelapp.FeatureMapping;
import cbit.vcell.modelapp.MembraneMapping;
import cbit.vcell.modelapp.SimulationContext;

import cbit.util.graph.Edge;
import cbit.util.graph.Graph;
import cbit.util.graph.Path;
import cbit.util.graph.Node;

/**
 * Insert the type's description here.
 * Creation date: (2/19/2002 11:22:17 AM)
 * @author: Jim Schaff
 */
public class PotentialMapping {
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
	private Expression fieldCapacitiveCurrent[] = new Expression[0];
	private Expression fieldTotalCurrent[] = new Expression[0];
	private Expression fieldDependentVoltageExpression[] = new Expression[0];
	private Edge fieldEdges[] = new Edge[0];

	public static boolean bSilent = false;

	static {
		System.out.println("PotentialMapping.getTotalMembraneCurrent(): ASSUMING NO MEMBRANE CURRENTS IF 'CALCULATE_VOLTAGE' == false");
	};
/**
 * PotentialMapping constructor comment.
 */
PotentialMapping(Graph graph) throws cbit.vcell.math.MathException, ExpressionException, MappingException, MatrixException {
	super();
	determineLumpedEquations(graph);
}
/**
 * Insert the method's description here.
 * Creation date: (2/20/2002 9:02:42 AM)
 * @return cbit.vcell.mathmodel.MathModel
 * @param circuitGraph cbit.vcell.mapping.potential.Graph
 */
private void determineLumpedEquations(Graph graph) throws ExpressionException, MatrixException, MappingException, cbit.vcell.math.MathException {
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
	fieldEdges = graph.getEdges();
	if (!bSilent){
		System.out.println("\n\n basic information for each device\n");
		for (int i = 0; i < fieldEdges.length; i++){
			ElectricalDevice device = (ElectricalDevice)fieldEdges[i].getData();
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


	//
	// SOLVE FOR DEPENDENT VOLTAGES IN TERMS OF INDEPENDENT VOLTAGES
	//
	//
	// solve for constraints on potentials (sum of potentials along a loop is zero) for full graph.
	// This will get all linear relationships amoung membrane potentials
	//
	//     V1 + V2 - V3 = 0
	//     V4 - V2 = 0
	//
	//   then solve for independent voltages   ....    
	// 
	//     V2 = V4
	//     V1 = V3 - V4
	//
	//   use these to reduce the number of independent equations.
	//
	if (!bSilent){
		System.out.println("\n\n applying KVL to <<ALL>> fundamental cycles\n");
	}
	Path graphCycles[] = graph.getFundamentalCycles();
	RationalExpMatrix voltageMatrix = new RationalExpMatrix(graphCycles.length,graph.getNumEdges());
	for (int i = 0; i < graphCycles.length; i++){
		Edge cycleEdges[] = graphCycles[i].getEdges();
		Node nodesTraversed[] = graphCycles[i].getNodesTraversed();

		StringBuffer buffer = new StringBuffer();
		buffer.append("0.0");
		//
		// add potentials that follow cycle.
		//
		for (int j = 0; j < cycleEdges.length; j++){
			int edgeIndex = graph.getIndex(cycleEdges[j]);
			if (cycleEdges[j].getNode1().equals(nodesTraversed[j])){
				buffer.append(" + "); // going same direction
				voltageMatrix.set_elem(i,edgeIndex,1);
			}else{
				buffer.append(" - "); // going opposite direction
				voltageMatrix.set_elem(i,edgeIndex,-1);
			}
			buffer.append(((ElectricalDevice)cycleEdges[j].getData()).getVName());
		}
		Expression exp = new Expression(buffer.toString());
		if (!bSilent){ 
			System.out.println(exp.flatten() + " = 0.0");
		}
	}
	if (!bSilent){
		voltageMatrix.show();
	}
	if (voltageMatrix.getNumRows()>0){
		RationalExpMatrix vPermutationMatrix = new RationalExpMatrix(voltageMatrix.getNumRows(),voltageMatrix.getNumRows());
		voltageMatrix.gaussianElimination(vPermutationMatrix);
		if (!bSilent){
			System.out.println("reduced matrix");
			voltageMatrix.show();
		}
	}else{
		voltageMatrix = null;
	}
	//
	// declare dependent voltages as functions of independent voltages
	//
	//    1) Always use Voltage-Clamps as independent voltages
	//    2) Try to use Capacitive devices as independent voltages
	//
	//    solve for current-clamp voltages and redundant/constrained capacitive voltages as function of (1) and (2).
	//
	fieldDependentVoltageExpression = new Expression[fieldEdges.length];

	//
	// make sure assumptions hold regarding edge ordering, otherwise wrong dependent voltages will be selected
	//
	verifyEdgeOrdering();
	
	for (int i = 0; voltageMatrix!=null && i < voltageMatrix.getNumRows(); i++){
		//
		// find first '1.0' element, this column is the next 'dependent' voltage
		//
		int column = -1;
		for (int j = i; j < voltageMatrix.getNumCols(); j++){
			RationalExp elem = voltageMatrix.get(i,j);
			if (elem.isConstant() && elem.getConstant().doubleValue()==1.0){
				column = j;
				break;
			}
		}
		if (column != -1){
			//
			// get electrical device of dependent voltage
			//
			ElectricalDevice device = (ElectricalDevice)fieldEdges[column].getData();
			//
			// form dependency expression
			//
			StringBuffer buffer = new StringBuffer();
			for (int j = column+1; j < graph.getNumEdges(); j++){
				if (!voltageMatrix.get_elem(i,j).isZero()){
					ElectricalDevice colDevice = (ElectricalDevice)fieldEdges[j].getData();
					buffer.append(" + "+voltageMatrix.get(i,j).minus().infixString()+'*'+colDevice.getVName());
				}	
			}
			fieldDependentVoltageExpression[column] = (new Expression(buffer.toString())).flatten();
		}
	}
	if (!bSilent){
		for (int i = 0; i < fieldDependentVoltageExpression.length; i++){
			System.out.println("fieldDependentVoltageExpression["+i+"] = "+fieldDependentVoltageExpression[i]);
		}
	}
	

	
	//
	// SOLVE FOR TOTAL CURRENTS IN TERMS OF APPLIED CURRENTS
	//
	//  1) solve for constraints on capacitor potentials (sum of potentials along a loop is zero)
	//     to get linear relationships amoung capacitor voltages.
	//
	//     V1 + V2 - V3 = 0
	//     V4 - V2 = 0
	//
	//   then differentiate wrt time   ....    
	// 
	//     dV1/dt + dV2/dt - dV3/dt = 0
	//     dV4/dt - dV3/dt = 0
	//
	//   if all V's are across capacitors, substitue the known functions dVi/dt = 1000/Ci (Ii - Fi) 
	//   to get linear relationships amoung total currents (I's) and applied currents (F's).
	//
	//                                      |            |
	// form of capacitorGraphVoltageMatrix: | V1  V2  V3 | = 0
	//		                                |            |
	//
	if (!bSilent){
		System.out.println("\n\nSOLVE FOR TOTAL CURRENTS IN TERMS OF APPLIED CURRENTS");
		System.out.println("\n\n  1)  applying KVL to all fundamental \"capacitive\" and Voltage-clamp cycles (after current-clamp edges are removed)\n");
	}
	Graph capacitorGraph = new Graph();
	for (int i = 0; i < fieldEdges.length; i++){
		ElectricalDevice device = (ElectricalDevice)fieldEdges[i].getData();
		if (device.hasCapacitance() || device.isVoltageSource()){
			capacitorGraph.addEdge(fieldEdges[i]);
		}
	}
	Path capacitorGraphCycles[] = capacitorGraph.getFundamentalCycles();
	RationalExpMatrix capacitorGraphVoltageMatrix = new RationalExpMatrix(capacitorGraphCycles.length,graph.getNumEdges());
	for (int i = 0; i < capacitorGraphCycles.length; i++){
		Edge cycleEdges[] = capacitorGraphCycles[i].getEdges();
		Node nodesTraversed[] = capacitorGraphCycles[i].getNodesTraversed();
		StringBuffer buffer = new StringBuffer();
		buffer.append("0.0");
		//
		// add potentials that follow cycle.
		//
		for (int j = 0; j < cycleEdges.length; j++){
			int edgeIndex = graph.getIndex(cycleEdges[j]);
			if (cycleEdges[j].getNode1().equals(nodesTraversed[j])){
				buffer.append(" + "); // going same direction
				capacitorGraphVoltageMatrix.set_elem(i,edgeIndex,1);
			}else{
				buffer.append(" - "); // going opposite direction
				capacitorGraphVoltageMatrix.set_elem(i,edgeIndex,-1);
			}
			buffer.append(((ElectricalDevice)cycleEdges[j].getData()).getVName());
		}
		Expression exp = new Expression(buffer.toString());
		if (!bSilent){
			System.out.println(exp.flatten() + " = 0.0");
		}
	}
	if (!bSilent){
		capacitorGraphVoltageMatrix.show();
	}


	//
	//  2) apply KCL to all nodes (except one) .. n-1 nodes of full graph  --- CONSERVATION OF TOTAL CURRENT
	//     This relates total currents to each other.
	//
	//
	//                               |             |
	// form KCL matrix of form:      | i1  i2  i3  |
	//                               |             |
	//
	if (!bSilent){
		System.out.println("\n\n  2)  applying KCL to all nodes (except one) .. n-1 nodes of full graph  --- CONSERVATION OF TOTAL CURRENT\n");
	}
	Node nodes[] = graph.getNodes();
	RationalExpMatrix kclMatrix = new RationalExpMatrix(graph.getNumNodes()-1,graph.getNumEdges());
	for (int i = 0; i < nodes.length-1; i++){
		if (graph.getDegree(nodes[i])>0){
			Edge adjacentEdges[] = graph.getAdjacentEdges(nodes[i]);
			StringBuffer buffer = new StringBuffer();
			buffer.append("0.0");
			//
			// add currents that "enter" this node. (sum is zero)
			//
			for (int j = 0; j < adjacentEdges.length; j++){
				int edgeIndex = graph.getIndex(adjacentEdges[j]);
				if (adjacentEdges[j].getNode1().equals(nodes[i])){
					buffer.append(" - ");
					kclMatrix.set_elem(i,edgeIndex,-1);
				}else{
					buffer.append(" + ");
					kclMatrix.set_elem(i,edgeIndex,1);
				}
				buffer.append(((ElectricalDevice)adjacentEdges[j].getData()).getIName());
			}
			Expression exp = new Expression(buffer.toString());
			if (!bSilent){
				System.out.println(exp.flatten() + " = 0.0");
			}
		}else{
			throw new MappingException("compartment node is isolated in PotentialMapping, not supported");
		}
	}
	if (!bSilent){
		kclMatrix.show();
	}
	//                               |                                                |
	// form current matrix of form:  | i1  i2  i3  F1  F2  F3  dV1/dt  dV2/dt  dV3/dt |
	//                               |                                                |
	//
	// where dVi/dt is the derivative of a given voltage clamp signal
	//
	if (!bSilent){
		System.out.println("\n\n  3)  form total 'current' matrix\n");
	}
	int numNonCapacitiveEdges = 0;
	for (int i = 0; i < fieldEdges.length; i++){
		ElectricalDevice device = (ElectricalDevice)fieldEdges[i].getData();
		if (!device.hasCapacitance()){
			numNonCapacitiveEdges++;
		}
	}
	int cmat_rows = kclMatrix.getNumRows() + capacitorGraphVoltageMatrix.getNumRows() + numNonCapacitiveEdges;
	RationalExpMatrix currentMatrix = new RationalExpMatrix(cmat_rows,3*graph.getNumEdges());

	//
	// order edges for current elimination (unknown voltage-clamp currents as well as all total currents).
	//
	int cIndex[] = new int[fieldEdges.length];
	int ci = 0;
	for (int i = 0; i < fieldEdges.length; i++){
		if (((ElectricalDevice)fieldEdges[i].getData()).isVoltageSource()){
			cIndex[ci++] = i;
		}
	}
	for (int i = 0; i < fieldEdges.length; i++){
		if (!((ElectricalDevice)fieldEdges[i].getData()).isVoltageSource()){
			cIndex[ci++] = i;
		}
	}
	if (ci!=fieldEdges.length){
		throw new RuntimeException("error computing current indexes");
	}
	if (!bSilent){
		System.out.println("reordered devices for current matrix elimination");
		for (int i = 0; i < cIndex.length; i++){
			System.out.println(i+") = device["+cIndex[i]+"] = "+((ElectricalDevice)fieldEdges[cIndex[i]].getData()).getVName());
		}
	}
	
	int row = 0;
	//
	// add KCL relations from above.
	//
	for (int i = 0; i < kclMatrix.getNumRows(); i++){
		for (int j = 0; j < kclMatrix.getNumCols(); j++){
			currentMatrix.set_elem(row,j,kclMatrix.get(i,cIndex[j]));  // entry for i's
		}
		row++;
	}
	//
	// add non-capacitive "basic" current equations from above. (I_PROBE - F_PROBE = 0)
	//
	for (int i = 0; i < fieldEdges.length; i++){
		ElectricalDevice device = (ElectricalDevice)fieldEdges[cIndex[i]].getData();
		if (!device.hasCapacitance()){
			currentMatrix.set_elem(row,i,1);
			currentMatrix.set_elem(row,i+graph.getNumEdges(),-1);
			row++;
		}
	}
	//
	// add current terms of (i - F)*1000/C form using KVL relationships from "capacitor graph"
	//
	for (int i = 0; i < capacitorGraphVoltageMatrix.getNumRows(); i++){
		for (int j = 0; j < capacitorGraphVoltageMatrix.getNumCols(); j++){
			ElectricalDevice device = (ElectricalDevice)fieldEdges[cIndex[j]].getData();
			RationalExp coefficient = capacitorGraphVoltageMatrix.get(i,cIndex[j]);
			if (device.hasCapacitance()){			
				//
				// replace dVi/dt  with   1000/Ci * Ii  +  1000/Ci * Fi
				//
				String Cname = device.getCapName();
				currentMatrix.set_elem(row,j,coefficient.mult(new RationalExp(1000)).div(new RationalExp(Cname)));  // entry for i's
				currentMatrix.set_elem(row,j+graph.getNumEdges(),coefficient.minus().mult(new RationalExp(1000)).div(new RationalExp(Cname))); // entry for F's
			}else if (device.isVoltageSource()){
				//
				// directly insert "symbolic" dVi/dt into the new matrix
				//
				currentMatrix.set_elem(row,j+2*graph.getNumEdges(),coefficient);
			}
		}
		row++;
	}
	if (!bSilent){
		currentMatrix.show();
	}
	if (currentMatrix.getNumRows()>0){
		RationalExpMatrix cPermutationMatrix = new RationalExpMatrix(currentMatrix.getNumRows(),currentMatrix.getNumRows());
		currentMatrix.gaussianElimination(cPermutationMatrix);
		if (!bSilent){
			System.out.println("reduced matrix");
			currentMatrix.show();
		}
	}
	
	//
	// print out total currents
	//
	if (!bSilent){
		System.out.println("\n\n total currents for each device\n");
	}
	fieldTotalCurrent = new Expression[fieldEdges.length];
	for (int i = 0; i < fieldEdges.length; i++){
		ElectricalDevice device = (ElectricalDevice)fieldEdges[cIndex[i]].getData();
		StringBuffer buffer = new StringBuffer("0.0");
		//
		// include Fi terms
		//
		for (int j = 0; j < graph.getNumEdges(); j++){
			RationalExp coefficient = currentMatrix.get(i,j+graph.getNumEdges());
			if (!coefficient.isZero()){
				ElectricalDevice colDevice = (ElectricalDevice)fieldEdges[cIndex[j]].getData();
				buffer.append(" + "+coefficient.minus()+"*"+colDevice.getSourceName());
			}	
		}
		//
		// include analytic dVi/dt terms from voltage sources
		//
		for (int j = 0; j < graph.getNumEdges(); j++){
			RationalExp coefficient = currentMatrix.get(i,j+2*graph.getNumEdges());
			if (!coefficient.isZero()){
				ElectricalDevice colDevice = (ElectricalDevice)fieldEdges[cIndex[j]].getData();
				Expression timeDeriv = colDevice.getInitialVoltageFunction().getExpression().differentiate("t");
				buffer.append(" + "+coefficient.minus()+"*"+timeDeriv.infix());
			}	
		}
		fieldTotalCurrent[cIndex[i]] = (new Expression(buffer.toString())).flatten();
	}
	if (!bSilent){
		for (int i = 0; i < fieldTotalCurrent.length; i++){
			System.out.println("fieldTotalCurrent["+i+"] = "+fieldTotalCurrent[cIndex[i]].toString());
		}
	}
	//
	// print out capacitive currents
	//
	if (!bSilent){
		System.out.println("\n\n capacitive currents for each device\n");
	}
	fieldCapacitiveCurrent = new Expression[fieldEdges.length];
	for (int i = 0; i < fieldEdges.length; i++){
		ElectricalDevice device = (ElectricalDevice)fieldEdges[i].getData();
		if (device.hasCapacitance()){
			fieldCapacitiveCurrent[i] = Expression.add(fieldTotalCurrent[i],new Expression("-"+device.getSourceName()));
		}else{
			fieldCapacitiveCurrent[i] = new Expression(0.0);
		}
	}
	if (!bSilent){
		for (int i = 0; i < fieldCapacitiveCurrent.length; i++){
			System.out.println("fieldCapacitiveCurrent["+i+"] = "+fieldCapacitiveCurrent[cIndex[i]].toString());
		}
	}

	
	//
	// display equations for independent voltages.
	//
	if (!bSilent){
		for (int i = 0; i < graph.getNumEdges(); i++){
			ElectricalDevice device = (ElectricalDevice)graph.getEdges()[i].getData();
			//
			// membrane ode
			//
			if (device.hasCapacitance() && fieldDependentVoltageExpression[i]==null){
				Expression initExp = new Expression(0.0);
				System.out.println(device.getInitialVoltageFunction().getVCML());
				System.out.println((new cbit.vcell.math.OdeEquation(new cbit.vcell.math.VolVariable(device.getVName()),new Expression(device.getInitialVoltageFunction().getName()),new Expression(fieldCapacitiveCurrent[i].flatten().toString()+"*"+cbit.vcell.model.ReservedSymbol.KMILLIVOLTS.getName()+"/"+device.getCapName()))).getVCML());
			}
			//
			// membrane forced potential
			//
			if (device.hasCapacitance() && fieldDependentVoltageExpression[i]!=null){
				System.out.println((new Function(device.getVName(),fieldDependentVoltageExpression[i])).getVCML());
				System.out.println((new Function(device.getSourceName(),device.getCurrentSourceExpression()).getVCML()));
			}
			//
			// current clamp
			//
			if (!device.hasCapacitance() && !device.isVoltageSource()){
				System.out.println((new Function(device.getSourceName(),device.getCurrentSourceExpression()).getVCML()));
				System.out.println((new Function(device.getVName(),fieldDependentVoltageExpression[i])).getVCML());
			}
			//
			// voltage clamp
			//
			if (!device.hasCapacitance() && device.isVoltageSource()){
				System.out.println((new Function(device.getIName(),fieldTotalCurrent[i])).getVCML());
				System.out.println((new Function(device.getVName(),device.getInitialVoltageFunction().getExpression())).getVCML());
			}
		}
	}
}
/**
 * Insert the method's description here.
 * Creation date: (3/2/2002 9:57:56 PM)
 * @return cbit.vcell.parser.Expression[]
 */
public Expression getCapacitiveCurrent(ElectricalDevice device) {
	for (int i = 0; i < fieldEdges.length; i++){
		if (fieldEdges[i].getData() == device){
			return fieldCapacitiveCurrent[i];
		}
	}
	return null;
}
/**
 * Insert the method's description here.
 * Creation date: (3/2/2002 11:10:22 PM)
 * @return cbit.vcell.mapping.potential.Edge[]
 */
public ElectricalDevice getCapacitiveDevice(Membrane membrane) {
	Edge edgesForMembrane[] = getEdges(membrane);
	java.util.Vector capDeviceList = new java.util.Vector();
	for (int i = 0; i < edgesForMembrane.length; i++){
		ElectricalDevice device = (ElectricalDevice)edgesForMembrane[i].getData();
		if (device.getCapacitance_pF()!=0.0){
			capDeviceList.add(device);
		}
	}
	if (capDeviceList.size()==0){
		throw new RuntimeException("no capacitive device found for membrane "+membrane.getName());
	}else if (capDeviceList.size()==1){
		return (ElectricalDevice)capDeviceList.elementAt(0);
	}else {
		throw new RuntimeException("("+capDeviceList.size()+") capacitive devices found for membrane "+membrane.getName());
	}
}
/**
 * Insert the method's description here.
 * Creation date: (4/24/2002 11:13:29 AM)
 * @return cbit.vcell.parser.Expression
 * @param device cbit.vcell.mapping.potential.ElectricalDevice
 */
public Expression getDependentVoltageExpression(ElectricalDevice device) {
	int index = getIndex(device);
	return fieldDependentVoltageExpression[index];
}
/**
 * Insert the method's description here.
 * Creation date: (3/2/2002 11:10:22 PM)
 * @return cbit.vcell.mapping.potential.Edge[]
 */
public Edge[] getEdges() {
	return fieldEdges;
}
/**
 * Insert the method's description here.
 * Creation date: (3/2/2002 11:10:22 PM)
 * @return cbit.vcell.mapping.potential.Edge[]
 */
private Edge[] getEdges(Membrane membrane) {
	java.util.Vector edgeList = new java.util.Vector();
	for (int i = 0; i < fieldEdges.length; i++){
		if (membrane.getInsideFeature().compareEqual((cbit.vcell.model.Feature)fieldEdges[i].getNode1().getData()) &&
			membrane.getOutsideFeature().compareEqual((cbit.vcell.model.Feature)fieldEdges[i].getNode2().getData())){
			edgeList.add(fieldEdges[i]);
		}
		if (membrane.getInsideFeature().compareEqual((cbit.vcell.model.Feature)fieldEdges[i].getNode2().getData()) &&
			membrane.getOutsideFeature().compareEqual((cbit.vcell.model.Feature)fieldEdges[i].getNode1().getData())){
			edgeList.add(fieldEdges[i]);
		}			
	}
	Edge edgesForMembrane[] = new Edge[edgeList.size()];
	edgeList.copyInto(edgesForMembrane);
	return edgesForMembrane;
}
/**
 * Insert the method's description here.
 * Creation date: (4/24/2002 10:58:55 AM)
 * @return cbit.vcell.mapping.potential.ElectricalDevice[]
 */
public ElectricalDevice[] getElectricalDevices() {
	java.util.Vector deviceList = new java.util.Vector();
	for (int i = 0; i < fieldEdges.length; i++){
		deviceList.add((ElectricalDevice)fieldEdges[i].getData());
	}
	ElectricalDevice devices[] = new ElectricalDevice[deviceList.size()];
	deviceList.copyInto(devices);
	return devices;
}
/**
 * Insert the method's description here.
 * Creation date: (3/2/2002 11:10:22 PM)
 * @return cbit.vcell.mapping.potential.Edge[]
 */
public ElectricalDevice[] getElectricalDevices(Membrane membrane) {
	Edge edges[] = getEdges(membrane);
	java.util.Vector deviceList = new java.util.Vector();
	for (int i = 0; i < edges.length; i++){
		deviceList.add((ElectricalDevice)edges[i].getData());
	}
	ElectricalDevice devices[] = new ElectricalDevice[deviceList.size()];
	deviceList.copyInto(devices);
	return devices;
}
/**
 * Insert the method's description here.
 * Creation date: (4/24/2002 10:40:17 AM)
 * @return int
 * @param device cbit.vcell.mapping.potential.ElectricalDevice
 */
private int getIndex(ElectricalDevice device) {
	for (int i = 0; i < fieldEdges.length; i++){
		ElectricalDevice d = (ElectricalDevice)fieldEdges[i].getData();
		if (d == device){
			return i;
		}
	}
	throw new RuntimeException("device "+device.getName()+" not found");
}
/**
 * Insert the method's description here.
 * Creation date: (4/24/2002 10:45:35 AM)
 * @return cbit.vcell.parser.Expression
 * @param capacitiveDevice cbit.vcell.mapping.potential.ElectricalDevice
 */
public Expression getOdeRHS(ElectricalDevice capacitiveDevice) throws ExpressionException {
	int index = getIndex(capacitiveDevice);
	return new Expression("1000.0 / "+capacitiveDevice.getCapName()+" * ("+capacitiveDevice.getIName()+" - "+capacitiveDevice.getCurrentSourceExpression().infix()+")");
}
/**
 * Insert the method's description here.
 * Creation date: (4/24/2002 11:06:46 AM)
 * @return cbit.vcell.parser.Expression
 * @param device cbit.vcell.mapping.potential.ElectricalDevice
 */
public Expression getTotalCurrent(ElectricalDevice device) {
	int index = getIndex(device);
	return fieldTotalCurrent[index];
}
/**
 * Insert the method's description here.
 * Creation date: (4/24/2002 10:39:51 AM)
 * @return boolean
 * @param device cbit.vcell.mapping.potential.ElectricalDevice
 */
public boolean isVoltageIndependent(ElectricalDevice device) {
	int index = getIndex(device);
	return (fieldDependentVoltageExpression[index]==null);
}
/**
 * Insert the method's description here.
 * Creation date: (4/23/2002 4:36:39 PM)
 */
private void verifyEdgeOrdering() {
	//
	// verify constraint that edges must be ordered
	//      current-clamp
	//      membranes
	//      voltage-clamp
	//
	final int CURRENT_CLAMP = 0;
	final int MEMBRANES = 1;
	final int VOLTAGE_CLAMP = 2;
	int edgeType = CURRENT_CLAMP;
	for (int i = 0; i < fieldEdges.length; i++){
		ElectricalDevice device = (ElectricalDevice)fieldEdges[i].getData();
		//
		// current clamp devices
		//
		if (!device.hasCapacitance() && !device.isVoltageSource()){
			if (edgeType==MEMBRANES){
				throw new RuntimeException("current-clamp edges must preceed membrane edges in electrical graph");
			}
			if (edgeType==VOLTAGE_CLAMP){
				throw new RuntimeException("current-clamp edges must preceed voltage-clamp edges in electrical graph");
			}
		}
		//
		// membrane devices
		//
		if (device.hasCapacitance()){
			if (edgeType==CURRENT_CLAMP){
				edgeType = MEMBRANES;
			}else if (edgeType == VOLTAGE_CLAMP){
				throw new RuntimeException("membrane edges must preceed voltage clamp edges in electrical graph");
			}
		}
		//
		// voltage clamp devices
		//
		if (!device.hasCapacitance() && device.isVoltageSource()){
			edgeType = VOLTAGE_CLAMP;
		}
	}

}
}
