package org.vcell.physics.math;

import cbit.vcell.matrix.RationalExp;
import cbit.vcell.matrix.RationalExpMatrix;
import cbit.vcell.matrix.MatrixException;
import cbit.vcell.mapping.MappingException;
import cbit.vcell.mapping.MembraneMapping;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.model.Membrane;
import cbit.vcell.model.Model;
import cbit.vcell.mapping.FeatureMapping;
import cbit.vcell.mapping.MathMapping;
import cbit.vcell.mapping.potential.MembraneElectricalDevice;
import cbit.vcell.mapping.potential.ElectricalDevice;
import cbit.vcell.mapping.potential.CurrentClampElectricalDevice;
import cbit.vcell.mapping.potential.VoltageClampElectricalDevice;
import cbit.util.graph.*;

/**
 * Insert the type's description here.
 * Creation date: (2/19/2002 11:22:17 AM)
 * @author: Jim Schaff
 */
public class PotentialMappingVCELL {
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
	//private Expression fieldCapacitiveCurrent[] = new Expression[0];
	//private Expression fieldTotalCurrent[] = new Expression[0];
	//private Expression fieldDependentVoltageExpression[] = new Expression[0];
	private Edge fieldEdges[] = new Edge[0];
	private SimulationContext fieldSimContext = null;
	private MathMapping fieldMathMapping = null;

	public static boolean bSilent = true;

	static {
		System.out.println("PotentialMapping.getTotalMembraneCurrent(): ASSUMING NO MEMBRANE CURRENTS IF 'CALCULATE_VOLTAGE' == false");
	};
/**
 * PotentialMapping constructor comment.
 */
public PotentialMappingVCELL(SimulationContext argSimContext, MathMapping argMathMapping) {
	super();
	this.fieldSimContext = argSimContext;
	this.fieldMathMapping = argMathMapping;
}
/**
 * Insert the method's description here.
 * Creation date: (6/8/2004 5:04:39 PM)
 */
public void computeMath() throws cbit.vcell.math.MathException, MappingException, ExpressionException, MatrixException {
	double tempK = fieldSimContext.getTemperatureKelvin();
	determineLumpedEquations(getCircuitGraph(fieldSimContext, fieldMathMapping),tempK);
}
/**
 * Insert the method's description here.
 * Creation date: (2/20/2002 9:02:42 AM)
 * @return cbit.vcell.mathmodel.MathModel
 * @param circuitGraph cbit.vcell.mapping.potential.Graph
 */
private void determineLumpedEquations(Graph graph, double temperatureKelvin) throws ExpressionException, MatrixException, cbit.vcell.mapping.MappingException, cbit.vcell.math.MathException {
	//
	// traverses graph and calculates RHS expressions for all capacitive devices (dV/dt)
	// 
	// calculate dependent voltages as functions of independent voltages.
	//
	fieldEdges = graph.getEdges();

	//================================================================
	// SOLVE FOR DEPENDENT VOLTAGES IN TERMS OF INDEPENDENT VOLTAGES
	//================================================================
	
	//----------------------------------------------------------------
	//
	// 1) solve for constraints on potentials (sum of potentials along a loop is zero) for full graph.
	//    This will get all linear relationships among membrane potentials
	//
	//       V1 + V2 - V3 = 0
	//       V4 - V2 = 0
	//
	//   and build voltageMatrix equivalent to the above system of linear equations.
	//   in the case above, the matrix looks like this:
	//
	//    voltageMatrix = | 1   1  -1  0 |
	//                    | 0  -1   0  1 |
	//
	//   NOTE: graph assumes that edges are ordered so that columns (voltages) are in order of increasing "causality" (not really a scalar measure).
	//
	//       e.g. 1) voltages across current clamps are "lowest causality" ... caused by other things (e.g. applied current and load)
	//            2) voltages across capacitive membranes are "medium causality" ... these are "storage devices" given next highest priority.
	//            3) voltages across voltage clamps are "highest causality" ... completely determined, inputs).
	//
	//   in this order, lower causality will be assumed to be dependent on properties of higher causality, so solve for them in terms of other things.
	//
	//----------------------------------------------------------------
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
	//----------------------------------------------------------------
	//
	// 2) perform gaussian elimination on voltageMatrix to solve for independent voltages   ....    
	//  
	//      V2 = V4
	//      V1 = V3 - V4
	//
	//   reduced Vmatrix =     | 1  0  -1  1 |
	//                         | 0  1   0  1 |
	//
	//
	//   (note: to be used to reduce the number of independent equations.)
	//
	//----------------------------------------------------------------
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
	//----------------------------------------------------------------
	//
	// 3) declare dependent voltages as functions of independent voltages (i.e. device.setDependentVoltageExpression(expression))
	//
	//       i) Always use Voltage-Clamps as independent voltages
	//       ii) Try to use Capacitive devices as independent voltages
	//       iii) solve for current-clamp voltages and redundant/constrained capacitive voltages as function of (i) and (ii).
	//
	Expression dependentVoltageExpressions[] = new Expression[fieldEdges.length];

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
			dependentVoltageExpressions[column] = (new Expression(buffer.toString())).flatten();
			dependentVoltageExpressions[column].bindExpression(device);
			device.setDependentVoltageExpression(dependentVoltageExpressions[column]);
		}
	}
	if (!bSilent){
		for (int i = 0; i < dependentVoltageExpressions.length; i++){
			System.out.println("dependentVoltageExpressions["+i+"] = "+dependentVoltageExpressions[i]);
		}
	}
	

	
	//=================================================================================
	// SOLVE FOR TOTAL CURRENTS IN TERMS OF APPLIED CURRENTS
	//=================================================================================
	
	//---------------------------------------------------------------------------------
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
	//---------------------------------------------------------------------------------
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
				String Cname = ((MembraneElectricalDevice)device).getCapName();
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
	Expression totalCurrents[] = new Expression[fieldEdges.length];
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
				VoltageClampElectricalDevice colDevice = (VoltageClampElectricalDevice)fieldEdges[cIndex[j]].getData();
				Expression timeDeriv = colDevice.getVoltageClampStimulus().getVoltageParameter().getExpression().differentiate("t");
				timeDeriv = timeDeriv.flatten();
				timeDeriv.bindExpression(colDevice);
				buffer.append(" + "+coefficient.minus()+"*"+timeDeriv.infix(colDevice.getNameScope().getParent()));
			}	
		}
		totalCurrents[cIndex[i]] = (new Expression(buffer.toString())).flatten();
		totalCurrents[cIndex[i]].bindExpression(device.getNameScope().getParent().getScopedSymbolTable());
		try {
			device.getParameterFromRole(device.ROLE_TotalCurrentDensity).setExpression(totalCurrents[cIndex[i]]);
		}catch (java.beans.PropertyVetoException e){
			e.printStackTrace(System.out);
			throw new MappingException("failed to set total current density: "+e.getMessage());
		}
	}
	if (!bSilent){
		for (int i = 0; i < totalCurrents.length; i++){
			System.out.println("totalCurrents["+i+"] = "+totalCurrents[cIndex[i]].toString());
		}
	}
	//
	// print out capacitive currents
	//
	if (!bSilent){
		System.out.println("\n\n capacitive currents for each device\n");
	}
	Expression capacitiveCurrents[] = new Expression[fieldEdges.length];
	for (int i = 0; i < fieldEdges.length; i++){
		ElectricalDevice device = (ElectricalDevice)fieldEdges[i].getData();
		if (device instanceof MembraneElectricalDevice){
			MembraneElectricalDevice membraneElectricalDevice = (MembraneElectricalDevice)device;
			capacitiveCurrents[i] = Expression.add(totalCurrents[i],new Expression("-"+membraneElectricalDevice.getSourceName()));
			capacitiveCurrents[i].bindExpression(membraneElectricalDevice);
			//membraneElectricalDevice.setCapacitiveCurrentExpression(capacitiveCurrents[i]);
		}else{
			//device.setCapacitiveCurrentExpression(new Expression(0.0));
		}
	}
	if (!bSilent){
		for (int i = 0; i < capacitiveCurrents.length; i++){
			System.out.println("capacitiveCurrents["+i+"] = "+((capacitiveCurrents[cIndex[i]]==null)?"0.0":capacitiveCurrents[cIndex[i]].infix()));
		}
	}

	
	//
	// display equations for independent voltages.
	//
	//if (!bSilent){
		//for (int i = 0; i < graph.getNumEdges(); i++){
			//ElectricalDevice device = (ElectricalDevice)graph.getEdges()[i].getData();
			////
			//// membrane ode
			////
			//if (device.hasCapacitance() && dependentVoltageExpressions[i]==null){
				//Expression initExp = new Expression(0.0);
				//System.out.println(device.getInitialVoltageFunction().getVCML());
				//System.out.println((new cbit.vcell.math.OdeEquation(new cbit.vcell.math.VolVariable(device.getVName()),new Expression(device.getInitialVoltageFunction().getName()),new Expression(fieldCapacitiveCurrent[i].flatten().toString()+"*"+cbit.vcell.model.ReservedSymbol.KMILLIVOLTS.getName()+"/"+device.getCapName()))).getVCML());
			//}
			////
			//// membrane forced potential
			////
			//if (device.hasCapacitance() && dependentVoltageExpressions[i]!=null){
				//System.out.println((new Function(device.getVName(),dependentVoltageExpressions[i])).getVCML());
				//System.out.println((new Function(device.getSourceName(),device.getCurrentSourceExpression()).getVCML()));
			//}
			////
			//// current clamp
			////
			//if (!device.hasCapacitance() && !device.isVoltageSource()){
				//System.out.println((new Function(device.getSourceName(),device.getCurrentSourceExpression()).getVCML()));
				//System.out.println((new Function(device.getVName(),dependentVoltageExpressions[i])).getVCML());
			//}
			////
			//// voltage clamp
			////
			//if (!device.hasCapacitance() && device.isVoltageSource()){
				//System.out.println((new Function(device.getIName(),totalCurrents[i])).getVCML());
				//System.out.println((new Function(device.getVName(),device.getInitialVoltageFunction().getExpression())).getVCML());
			//}
		//}
	//}
}
/**
 * Insert the method's description here.
 * Creation date: (3/2/2002 11:10:22 PM)
 * @return cbit.vcell.mapping.potential.Edge[]
 */
public MembraneElectricalDevice getCapacitiveDevice(Membrane membrane) {
	Edge edgesForMembrane[] = getEdges(membrane);
	java.util.Vector capDeviceList = new java.util.Vector();
	for (int i = 0; i < edgesForMembrane.length; i++){
		ElectricalDevice device = (ElectricalDevice)edgesForMembrane[i].getData();
		if (device instanceof MembraneElectricalDevice){
			capDeviceList.add(device);
		}
	}
	if (capDeviceList.size()==0){
		throw new RuntimeException("no capacitive device found for membrane "+membrane.getName());
	}else if (capDeviceList.size()==1){
		return (MembraneElectricalDevice)capDeviceList.elementAt(0);
	}else {
		throw new RuntimeException("("+capDeviceList.size()+") capacitive devices found for membrane "+membrane.getName());
	}
}
/**
 * Insert the method's description here.
 * Creation date: (2/19/2002 11:24:04 AM)
 * @return cbit.vcell.mapping.potential.Graph
 * @param simContext cbit.vcell.mapping.SimulationContext
 */
private static Graph getCircuitGraph(SimulationContext simContext, MathMapping mathMapping) throws ExpressionException {
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
	cbit.vcell.mapping.ElectricalStimulus stimuli[] = simContext.getElectricalStimuli();
	cbit.vcell.mapping.Electrode groundElectrode = simContext.getGroundElectrode();
	for (int i = 0; i < stimuli.length; i++){
		cbit.vcell.mapping.ElectricalStimulus stimulus = stimuli[i];
		//
		// get electrodes
		//
		cbit.vcell.mapping.Electrode probeElectrode = stimulus.getElectrode();
		if (probeElectrode == null){
			throw new RuntimeException("null electrode for electrical stimulus");
		}
		if (groundElectrode == null){
			throw new RuntimeException("null ground electrode for electrical stimulus");
		}
//if (!membraneMapping.getResolved()){
		Node groundNode = graph.getNode(groundElectrode.getFeature().getName());
		Node probeNode = graph.getNode(probeElectrode.getFeature().getName());
		if (stimulus instanceof cbit.vcell.mapping.CurrentClampStimulus){
			cbit.vcell.mapping.CurrentClampStimulus ccStimulus = (cbit.vcell.mapping.CurrentClampStimulus)stimulus;
			ElectricalDevice device = new CurrentClampElectricalDevice(ccStimulus,mathMapping);
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
				Expression currentSource = getTotalMembraneCurrent(simContext,membrane,mathMapping);
				MembraneElectricalDevice device = new MembraneElectricalDevice(membraneMapping,mathMapping);
				try {
					device.getParameterFromRole(ElectricalDevice.ROLE_TransmembraneCurrentDensity).setExpression(getTotalMembraneCurrent(simContext,membrane,mathMapping));
				}catch (java.beans.PropertyVetoException e){
					e.printStackTrace(System.out);
					throw new RuntimeException(e.getMessage());
				}
				Edge edge = new Edge(insideNode,outsideNode,device);
				graph.addEdge(edge);
//			}
		}
	}
	//
	// add edges for all voltage clamp electrodes (ALWAYS independent voltages)
	//
	for (int i = 0; i < stimuli.length; i++){
		cbit.vcell.mapping.ElectricalStimulus stimulus = stimuli[i];
		//
		// get electrodes
		//
		cbit.vcell.mapping.Electrode probeElectrode = stimulus.getElectrode();
		if (probeElectrode == null){
			throw new RuntimeException("null electrode for electrical stimulus");
		}
		if (groundElectrode == null){
			throw new RuntimeException("null ground electrode for electrical stimulus");
		}
//if (!membraneMapping.getResolved()){
		Node groundNode = graph.getNode(groundElectrode.getFeature().getName());
		Node probeNode = graph.getNode(probeElectrode.getFeature().getName());
		if (stimulus instanceof cbit.vcell.mapping.VoltageClampStimulus){
			cbit.vcell.mapping.VoltageClampStimulus vcStimulus = (cbit.vcell.mapping.VoltageClampStimulus)stimulus;
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
 * Creation date: (2/19/2002 12:58:13 PM)
 * @return double
 * @param simContext cbit.vcell.mapping.SimulationContext
 * @param membrane cbit.vcell.model.Membrane
 */
private static Expression getMembraneSurfaceAreaExpression(SimulationContext simContext, Membrane membrane) throws ExpressionException {
	MembraneMapping membraneMapping = (MembraneMapping)simContext.getGeometryContext().getStructureMapping(membrane);
	FeatureMapping featureMapping = (FeatureMapping)simContext.getGeometryContext().getStructureMapping(membrane);
	Expression totalVolFract = featureMapping.getResidualVolumeFraction(simContext);
	Expression surfaceToVolume = membraneMapping.getSurfaceToVolumeParameter().getExpression();
	Expression surfaceArea = Expression.mult(surfaceToVolume,totalVolFract);

	return surfaceArea;
}
/**
 * Insert the method's description here.
 * Creation date: (4/24/2002 10:45:35 AM)
 * @return cbit.vcell.parser.Expression
 * @param capacitiveDevice cbit.vcell.mapping.potential.ElectricalDevice
 */
public Expression getOdeRHS(MembraneElectricalDevice capacitiveDevice, MathMapping mathMapping) throws ExpressionException {
	int index = getIndex(capacitiveDevice);
	Expression exp = new Expression("1000.0 / "+capacitiveDevice.getCapName()+" * ("+capacitiveDevice.getIName()+" - "+capacitiveDevice.getParameterFromRole(ElectricalDevice.ROLE_TransmembraneCurrentDensity).getExpression().infix(mathMapping.getNameScope())+")");
	exp.bindExpression(mathMapping); 
	return exp;
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
private static Expression getTotalMembraneCapacitance(SimulationContext simContext, Membrane membrane) throws ExpressionException {
	MembraneMapping membraneMapping = (MembraneMapping)simContext.getGeometryContext().getStructureMapping(membrane);
	return Expression.mult(membraneMapping.getSpecificCapacitanceParameter().getExpression(),new Expression(getSurfaceArea(simContext,membrane)));
}
/**
 * Insert the method's description here.
 * Creation date: (2/19/2002 12:56:13 PM)
 * @return cbit.vcell.parser.Expression
 * @param simContext cbit.vcell.mapping.SimulationContext
 * @param membrane cbit.vcell.model.Membrane
 */
private static Expression getTotalMembraneCurrent(SimulationContext simContext, Membrane membrane, MathMapping mathMapping) throws ExpressionException {
	MembraneMapping membraneMapping = (MembraneMapping)simContext.getGeometryContext().getStructureMapping(membrane);
	if (!membraneMapping.getCalculateVoltage()){
		return new Expression(0.0);
	}
	//
	// gather current terms
	//
	Expression currentExp = new Expression(0.0);
	cbit.vcell.mapping.ReactionSpec reactionSpecs[] = simContext.getReactionContext().getReactionSpecs();
	for (int i = 0; i < reactionSpecs.length; i++){
		//
		// only include currents from this membrane from reactions that are not disabled ("excluded")
		//
		if (reactionSpecs[i].isExcluded()){
			continue;
		}
		cbit.vcell.model.ReactionStep rs = reactionSpecs[i].getReactionStep();
		if (rs.getStructure() == membrane){
			Expression current = new Expression(rs.getKinetics().getCurrentParameter().getExpression().infix(mathMapping.getNameScope()));
			if (!current.compareEqual(new Expression(0.0))){
				//
				// change sign convension from inward current to outward current (which is consistent with voltage convension)
				//
				currentExp = Expression.add(currentExp, Expression.negate(new Expression(mathMapping.getNameScope().getSymbolName(rs.getKinetics().getCurrentParameter()))));
			}
		}
	}
	currentExp.bindExpression(mathMapping);
	return currentExp.flatten();
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
