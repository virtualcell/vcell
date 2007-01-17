package cbit.vcell.mapping;
import java.util.Hashtable;
import java.util.Vector;
import cbit.vcell.math.Variable;
import cbit.util.graph.Graph;
import cbit.util.graph.Node;
import cbit.util.graph.Edge;
import cbit.vcell.math.ReservedVariable;
/**
 * Insert the type's description here.
 * Creation date: (2/24/2002 9:12:36 AM)
 * @author: Jim Schaff
 */
//
// temporarily place all variables in a hashtable (before binding) and discarding duplicates (check for equality)
//
public class VariableHash {
	private Hashtable hash = new Hashtable();

	private class UnresolvedException extends RuntimeException {
		private String name = null;
		UnresolvedException(String argName, String message){
			super(message);
			name = argName;
		}
	}

/**
 * Insert the method's description here.
 * Creation date: (2/24/2002 9:18:38 AM)
 */
public VariableHash(){
}


/**
 * Insert the method's description here.
 * Creation date: (2/24/2002 9:18:38 AM)
 */
public void addVariable(Variable var) throws MappingException {
	if (var==null){
		throw new IllegalArgumentException("var can't be null");
	}
//if (var.getName().indexOf(".")>=0){
	//System.out.println(var.toString()+" found");
//}
	//if (var.getName().indexOf(".")>=0){
		//System.out.println(var.toString()+" name is scoped");
	//}
	//if (var.getExpression() != null){
		//String tokens[] = var.getExpression().getSymbols();
		//for (int i = 0; tokens!=null && i < tokens.length; i++){
			//if (tokens[i].indexOf(".")>=0){
				//System.out.println("MathDescription warning: "+var.toString()+" symbol '"+tokens[i]+"' is scoped");
			//}
		//}
	//}
	Variable existingVar = (Variable)hash.get(var.getName());
	if (existingVar!=null){
		//
		// if var already exists, check that it is "compareEqual()"
		//
		if (!var.compareEqual(existingVar)){
			throw new MappingException("new variable ("+var.toString()+") conflicts with existing var ("+existingVar.toString());
		}
	}else{
		hash.put(var.getName(),var);
		if (var instanceof cbit.vcell.math.VolVariable){
			//
			// for Volume Variables, also create an InsideVariable and an OutsideVariable for use in JumpConditions
			//
			cbit.vcell.math.InsideVariable inVar = new cbit.vcell.math.InsideVariable(var.getName()+"_INSIDE", var.getName());
			addVariable(inVar);
			cbit.vcell.math.OutsideVariable outVar = new cbit.vcell.math.OutsideVariable(var.getName()+"_OUTSIDE", var.getName());
			addVariable(outVar);
		}
	}
}


/**
 * Insert the method's description here.
 * Creation date: (2/24/2002 9:18:38 AM)
 */
public String getFirstUnresolvedSymbol(){
	try {
		reorderVariables(getVariables());
		return null;
	}catch (VariableHash.UnresolvedException e){
		return e.name;
	}
}


/**
 * Insert the method's description here.
 * Creation date: (2/24/2002 9:18:38 AM)
 */
public Variable[] getReorderedVariables(){
	return reorderVariables(getVariables());
}


/**
 * Insert the method's description here.
 * Creation date: (2/24/2002 9:18:38 AM)
 */
public Variable getVariable(String varName){
	return (Variable)hash.get(varName);
}


/**
 * Insert the method's description here.
 * Creation date: (2/24/2002 9:18:38 AM)
 */
public Variable[] getVariables(){
	Vector varList = new Vector();
	varList.addAll(hash.values());
	Variable variables[] = new Variable[varList.size()];
	varList.copyInto(variables);
	return variables;
}


	public void removeVariable(Variable var) {

		if (var == null) {
			throw new IllegalArgumentException("Invalid argument: null Variable");
		}
		hash.remove(var.getName());
	}


	public void removeVariable(String variableName) {

		if (variableName == null || variableName.length() == 0) {
			throw new IllegalArgumentException("Invalid variable name: " + variableName);
		}
		hash.remove(variableName);
	}


/**
 * Insert the method's description here.
 * Creation date: (4/12/2002 3:45:17 PM)
 * @return cbit.vcell.math.Variable[]
 * @param variables cbit.vcell.math.Variable[]
 */
private Variable[] reorderVariables(Variable[] variables) throws VariableHash.UnresolvedException {
	
	//
	// add the nodes for each "Variable" (VolVariable, etc...) ... no dependencies
	//
	Graph variableGraph = new Graph();
	for (int i = 0; i < variables.length; i++){
		if (!(variables[i] instanceof cbit.vcell.math.Constant) && !(variables[i] instanceof cbit.vcell.math.Function)){
			Node node = new Node(variables[i].getName(),variables[i]);
			variableGraph.addNode(node);
		}
	}
	Node variableNodes[] = variableGraph.getNodes();
	//
	// add the nodes for each Constant
	//
	Graph constantGraph = new Graph();
	for (int i = 0; i < variables.length; i++){
		if (variables[i] instanceof cbit.vcell.math.Constant){
			Node node = new Node(variables[i].getName(),variables[i]);
			constantGraph.addNode(node);
		}
	}
	
	//
	// add the nodes for each Function
	//
	Graph functionGraph = new Graph();
	for (int i = 0; i < variables.length; i++){
		if (variables[i] instanceof cbit.vcell.math.Function){
			Node node = new Node(variables[i].getName(),variables[i]);
			functionGraph.addNode(node);
		}
	}

	//
	// add an edge for each dependency of a Constant
	//
	Node constantNodes[] = constantGraph.getNodes();
	for (int i = 0; i < constantNodes.length; i++){
		cbit.vcell.math.Constant constant = (cbit.vcell.math.Constant)constantNodes[i].getData();
		String symbols[] = constant.getExpression().getSymbols();
		for (int j = 0; symbols!=null && j < symbols.length; j++){
			//
			// find node that this symbol references (it better find this in the Constants).
			//
			Node symbolNode = constantGraph.getNode(symbols[j]);
			if (symbolNode == null){
				if (functionGraph.getNode(symbols[j])!=null){
					throw new RuntimeException("Constant "+constant.getName()+" references function '"+symbols[j]+"'");
				}else if (variableGraph.getNode(symbols[j])!=null){
					throw new RuntimeException("Constant "+constant.getName()+" references variable '"+symbols[j]+"'");
				}else{
					throw new RuntimeException("Constant "+constant.getName()+" references unknown or symbol '"+symbols[j]+"'");
				}
			} 
			Edge dependency = new Edge(constantNodes[i],symbolNode,constant.getName()+"->"+((Variable)symbolNode.getData()).getName());
			constantGraph.addEdge(dependency);
		}
	}
	Node sortedConstantNodes[] = constantGraph.topologicalSort();
	
	//
	// add an edge for each dependency of a Function
	//
	Node functionNodes[] = functionGraph.getNodes();
	for (int i = 0; i < functionNodes.length; i++){
		cbit.vcell.math.Function function = (cbit.vcell.math.Function)functionNodes[i].getData();
		String symbols[] = function.getExpression().getSymbols();
		for (int j = 0; symbols!=null && j < symbols.length; j++){
			//
			// find node that this symbol references (better find it in FunctionGraph, ConstantGraph, or VariableGraph).
			//
			Node symbolNode = functionGraph.getNode(symbols[j]);
			if (symbolNode!=null){
				Edge dependency = new Edge(functionNodes[i],symbolNode,function.getName()+"->"+((Variable)symbolNode.getData()).getName());
				functionGraph.addEdge(dependency);
			}else if (ReservedVariable.fromString(symbols[j])==null && constantGraph.getNode(symbols[j])==null && variableGraph.getNode(symbols[j])==null){
				throw new VariableHash.UnresolvedException(symbols[j],"Unable to sort, unknown identifier "+symbols[j]);
			}
		}
	}
	Node sortedFunctionNodes[] = functionGraph.topologicalSort();

	//
	// reassemble in the proper order
	//		Constants
	//		Variables
	//		Functions
	//
	Vector newVariableOrder = new Vector();
	for (int i = 0; i < sortedConstantNodes.length; i++){
		newVariableOrder.add(sortedConstantNodes[i].getData());
	}
	for (int i = 0; i < variableNodes.length; i++){
		if (!(variableNodes[i].getData() instanceof cbit.vcell.math.InsideVariable) && !(variableNodes[i].getData() instanceof cbit.vcell.math.OutsideVariable)){
			newVariableOrder.add(variableNodes[i].getData());
		}
	}
	for (int i = 0; i < sortedFunctionNodes.length; i++){
		newVariableOrder.add(sortedFunctionNodes[i].getData());
	}

	Variable sortedVars[] = new Variable[newVariableOrder.size()];
	newVariableOrder.copyInto(sortedVars);
	
	return sortedVars;
}


/**
 * Insert the method's description here.
 * Creation date: (2/24/2002 9:18:38 AM)
 */
public void show() {
	Variable vars[] = getVariables();
	for (int i = 0; i < vars.length; i++){
		System.out.println(i+") "+vars[i].toString());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (2/24/2002 9:20:38 AM)
 * @return java.lang.String
 */
public String toString() {
	return "VariableHash: numEntries="+hash.size();
}
}