package cbit.vcell.opt;
import cbit.vcell.parser.SimpleSymbolTable;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.math.CommentStringTokenizer;
import cbit.vcell.parser.Expression;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.util.*;
import cbit.util.*;
/**
 * Insert the type's description here.
 * Creation date: (3/3/00 12:06:21 AM)
 * @author: 
 */
public class OptimizationSpec implements java.io.Serializable {
	private ObjectiveFunction objectiveFunction = null;
	private Vector constraintList = new Vector();
	private Vector parameterList = new Vector();

	public final static String SCALED_VARIABLE_SUFFIX = "_scaled";

/**
 * OptimizationSpec constructor comment.
 */
public OptimizationSpec() {
	super();
}


/**
 * OptimizationSpec constructor comment.
 */
public OptimizationSpec(String vcml) {
	CommentStringTokenizer tokens = new CommentStringTokenizer(vcml);
	read(tokens);
}


/**
 * Insert the method's description here.
 * Creation date: (3/3/00 12:36:57 AM)
 * @param constant cbit.vcell.math.Constant
 */
public void addConstraint(Constraint constraint) throws ExpressionBindingException {
	if (!constraintList.contains(constraint)){
		cbit.vcell.parser.SimpleSymbolTable symbolTable = new cbit.vcell.parser.SimpleSymbolTable(getParameterNames());
		constraint.getExpression().bindExpression(symbolTable);
		constraintList.addElement(constraint);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (3/3/00 12:36:57 AM)
 * @param constant cbit.vcell.math.Constant
 */
public void addParameter(Parameter optVar) {
	if (!parameterList.contains(optVar)){
		parameterList.addElement(optVar);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (8/22/2005 1:06:33 PM)
 * @return boolean
 */
public void gatherIssues(Vector issueList) {
	try {
		for (int i=0;i<this.constraintList.size();i++){
//			((Constraint)constraintList.elementAt(i)).gatherIssues(issueList);
		}
	}catch (Throwable e){
		issueList.add(new Issue(this,"Constraint","unexpected exception: "+e.getMessage(),Issue.SEVERITY_INFO));
	}
	
	try {
		//
		// determine unit consistency for each expression
		//
		for (int i = 0; i < this.parameterList.size(); i++){
			Parameter parameter = (Parameter)parameterList.elementAt(i);
			if (parameter.getLowerBound()>parameter.getUpperBound()){
				issueList.add(new Issue(parameter, "Parameter","lower bound is higher than upper bound for parameter '"+parameter.getName()+"'",Issue.SEVERITY_ERROR));
			}
			if (parameter.getInitialGuess()<parameter.getLowerBound() || parameter.getInitialGuess()>parameter.getUpperBound()){
				issueList.add(new Issue(parameter, "Parameter","initial guess is outside of bounds for parameter '"+parameter.getName()+"'",Issue.SEVERITY_ERROR));
			}
		}
	}catch (Throwable e){
		issueList.add(new Issue(this,"Parameter","unexpected exception: "+e.getMessage(),Issue.SEVERITY_INFO));
	}
	//
	// check for validity of objective function
	//
	objectiveFunction.gatherIssues(issueList);

	//
	// check that there is at least one parameter to optimize
	//
	if (parameterList.size()==0){
		issueList.add(new Issue(this,"Parameter","no parameters to optimize",Issue.SEVERITY_ERROR));
	}
}


/**
 * Insert the method's description here.
 * Creation date: (3/3/00 3:25:58 PM)
 * @return java.util.Enumeration
 */
public Constraint[] getConstraints() {
	return (Constraint[])BeanUtils.getArray(constraintList,Constraint.class);
}


/**
 * Insert the method's description here.
 * Creation date: (10/19/2005 10:40:46 AM)
 * @return cbit.vcell.opt.Constraint[]
 * @param constraintType cbit.vcell.opt.ConstraintType
 */
public Constraint[] getConstraints(ConstraintType constraintType) {
	Vector typedConstraintList = new Vector();
	for (int i=0;i<this.constraintList.size();i++){
		Constraint constraint = (Constraint)this.constraintList.elementAt(i);
		if (constraint.getConstraintType().equals(constraintType)){
			typedConstraintList.add(constraint);
		}
	}
	return (Constraint[])BeanUtils.getArray(typedConstraintList,Constraint.class);
}


/**
 * Insert the method's description here.
 * Creation date: (3/3/00 1:08:49 PM)
 * @return int
 * @param constraintType cbit.vcell.opt.ConstraintType
 */
public int getNumConstraints() {
	return constraintList.size();
}


/**
 * Insert the method's description here.
 * Creation date: (3/3/00 1:08:49 PM)
 * @return int
 * @param constraintType cbit.vcell.opt.ConstraintType
 */
public int getNumConstraints(ConstraintType constraintType) {
	int count = 0;
	for (int i=0;i<constraintList.size();i++){
		Constraint constraint = (Constraint)constraintList.elementAt(i);
		if (constraint.getConstraintType().equals(constraintType)){
			count++;
		}
	}
	return count;
}


/**
 * Insert the method's description here.
 * Creation date: (3/3/00 1:00:08 PM)
 * @return int
 */
public int getNumParameters() {
	return parameterList.size();
}


/**
 * Insert the method's description here.
 * Creation date: (3/3/00 1:02:36 PM)
 * @return java.util.Enumeration
 */
public ObjectiveFunction getObjectiveFunction() {
	return this.objectiveFunction;
}


/**
 * Insert the method's description here.
 * Creation date: (3/3/00 12:59:40 PM)
 * @return java.util.Enumeration
 */
public String[] getParameterNames() {
	String[] parameterNames = new String[parameterList.size()];
	for (int i = 0; i < parameterNames.length; i++){
		parameterNames[i] = ((Parameter)parameterList.elementAt(i)).getName();
	}
	return parameterNames;
}


/**
 * Insert the method's description here.
 * Creation date: (3/3/00 12:59:40 PM)
 * @return java.util.Enumeration
 */
public Parameter[] getParameters() {
	return (Parameter[])BeanUtils.getArray(parameterList,Parameter.class);
}


/**
 * Insert the method's description here.
 * Creation date: (3/3/00 12:59:40 PM)
 * @return java.util.Enumeration
 */
public String[] getScaledParameterNames() {
	String[] parameterNames = new String[parameterList.size()];
	for (int i = 0; i < parameterNames.length; i++){
		parameterNames[i] = ((Parameter)parameterList.elementAt(i)).getName()+SCALED_VARIABLE_SUFFIX;
	}
	return parameterNames;
}


/**
 * Insert the method's description here.
 * Creation date: (3/3/00 12:59:40 PM)
 * @return java.util.Enumeration
 */
public double[] getScaleFactors() {
	double[] scaleFactors = new double[parameterList.size()];
	for (int i = 0; i < scaleFactors.length; i++){
		scaleFactors[i] = ((Parameter)parameterList.elementAt(i)).getScale();
	}
	return scaleFactors;
}


/**
 * Insert the method's description here.
 * Creation date: (3/3/00 12:50:32 AM)
 * @return java.lang.String
 */
public String getVCML() {
	StringBuffer buffer = new StringBuffer();

	//
	// print parameters
	//
	for (int i=0;i<parameterList.size();i++){
		Parameter parameter = (Parameter)parameterList.elementAt(i);
		buffer.append("Parameter\t"+parameter.getName()+" "+parameter.getLowerBound()+" "+parameter.getUpperBound()+" "+parameter.getScale()+" "+parameter.getInitialGuess()+"\n");
	}
	buffer.append("\n");
	
	//
	// print objective function
	//
	if (this.objectiveFunction!=null){
		buffer.append(objectiveFunction.getVCML());
	}
	buffer.append("\n");

	//
	// print constraints
	//
	for (int i=0;i<constraintList.size();i++){
		Constraint constraint = (Constraint)constraintList.elementAt(i);
		buffer.append(constraint.getConstraintType()+"\t"+constraint.getExpression().infix()+";\n");
	}

	return buffer.toString();
}


/**
 * Insert the method's description here.
 * Creation date: (3/3/00 3:59:31 PM)
 * @return cbit.vcell.opt.OptimizationSpec
 * @param vcmlString java.lang.String
 */
public void read(CommentStringTokenizer tokens) {
	try {
		String token = null;
		while (tokens.hasMoreTokens()){
			token = tokens.nextToken();
			if (token.equalsIgnoreCase("Parameter")){
				String name = tokens.nextToken();
				String lowerBound = tokens.nextToken();
				double lowerBoundDouble = 0.0;
				try {
					lowerBoundDouble = Double.parseDouble(lowerBound);
				}catch (NumberFormatException e){
					if (lowerBound.equalsIgnoreCase("-Infinity") || lowerBound.equalsIgnoreCase("-Inf")){
						lowerBoundDouble = Double.NEGATIVE_INFINITY;
					}
				}
				String upperBound = tokens.nextToken();
				double upperBoundDouble = 0.0;
				try {
					upperBoundDouble = Double.parseDouble(upperBound);
				}catch (NumberFormatException e){
					if (upperBound.equalsIgnoreCase("Infinity") || upperBound.equalsIgnoreCase("Inf")){
						upperBoundDouble = Double.POSITIVE_INFINITY;
					}
				}
				String scale = tokens.nextToken();
				String initialValue = tokens.nextToken();
				Parameter parameter = new Parameter(name,lowerBoundDouble,upperBoundDouble,Double.parseDouble(scale),Double.parseDouble(initialValue));
				addParameter(parameter);
				continue;
			}
			if (token.equalsIgnoreCase("ExplicitObjectiveFunction")){
				ObjectiveFunction objFunction = ExplicitObjectiveFunction.fromVCML(tokens);
				setObjectiveFunction(objFunction);
				continue;
			}
			if (token.equalsIgnoreCase("OdeObjectiveFunction")){
				ObjectiveFunction objFunction = OdeObjectiveFunction.fromVCML(tokens);
				setObjectiveFunction(objFunction);
				continue;
			}
			if (token.equalsIgnoreCase(ConstraintType.LinearEquality.toString())){
				Expression exp = new Expression(tokens);
				SimpleSymbolTable symbolTable = new SimpleSymbolTable(getParameterNames());
				exp.bindExpression(symbolTable);
				Constraint constraint = new Constraint(ConstraintType.LinearEquality,exp);
				addConstraint(constraint);
				continue;
			}
			if (token.equalsIgnoreCase(ConstraintType.LinearInequality.toString())){
				Expression exp = new Expression(tokens);
				SimpleSymbolTable symbolTable = new SimpleSymbolTable(getParameterNames());
				exp.bindExpression(symbolTable);
				Constraint constraint = new Constraint(ConstraintType.LinearInequality,exp);
				addConstraint(constraint);
				continue;
			}
			if (token.equalsIgnoreCase(ConstraintType.NonlinearInequality.toString())){
				Expression exp = new Expression(tokens);
				SimpleSymbolTable symbolTable = new SimpleSymbolTable(getParameterNames());
				exp.bindExpression(symbolTable);
				Constraint constraint = new Constraint(ConstraintType.NonlinearInequality,exp);
				addConstraint(constraint);
				continue;
			}
			if (token.equalsIgnoreCase(ConstraintType.NonlinearEquality.toString())){
				Expression exp = new Expression(tokens);
				SimpleSymbolTable symbolTable = new SimpleSymbolTable(getParameterNames());
				exp.bindExpression(symbolTable);
				Constraint constraint = new Constraint(ConstraintType.NonlinearEquality,exp);
				addConstraint(constraint);
				continue;
			}
			throw new RuntimeException("unexpected identifier "+token);
		}
	}catch (Throwable e){
		e.printStackTrace(System.out);
		throw new RuntimeException("line #"+(tokens.lineIndex()+1)+" Exception: "+e.getMessage());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (3/3/00 12:36:57 AM)
 * @param constant cbit.vcell.math.Constant
 */
public void setObjectiveFunction(ObjectiveFunction argObjectiveFunction) {
	this.objectiveFunction = argObjectiveFunction;
}
}