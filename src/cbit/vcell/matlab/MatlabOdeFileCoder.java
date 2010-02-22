package cbit.vcell.matlab;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.parser.*;
import java.util.*;
import cbit.vcell.math.*;
import cbit.vcell.solver.Simulation;
/**
 * Insert the type's description here.
 * Creation date: (3/8/00 10:29:24 PM)
 * @author: 
 */
public class MatlabOdeFileCoder {
	private Simulation simulation = null;
	private cbit.vcell.matrix.RationalMatrixFast stoichMatrix = null;
/**
 * OdeFileCoder constructor comment.
 */
public MatlabOdeFileCoder(Simulation argSimulation) {
	this(argSimulation,null);
}
/**
 * OdeFileCoder constructor comment.
 */
public MatlabOdeFileCoder(Simulation argSimulation, cbit.vcell.matrix.RationalMatrixFast argStoichMatrix) {
	this.simulation = argSimulation;
	this.stoichMatrix = argStoichMatrix;
}
/**
 * Insert the method's description here.
 * Creation date: (3/8/00 10:31:52 PM)
 */
public void write_V5_OdeFile(java.io.PrintWriter pw,String odeFileName) throws MathException, ExpressionException {
	MathDescription mathDesc = simulation.getMathDescription();
	if (!mathDesc.isValid()){
		throw new MathException("invalid math description\n" + mathDesc.getWarning());
	}
	if (mathDesc.isSpatial()){
		throw new MathException("spatial math description, cannot create ode file");
	}
	if (mathDesc.hasFastSystems()){
		throw new MathException("math description contains algebraic constraints, cannot create ode file");
	}

	//
	// print function declaration
	//
	pw.println("function varargout = "+odeFileName+"(t,y,flag)");
	
	//
	// print constants
	//
	pw.println("% Constants");
	Variable variables[] = simulation.getVariables();
	for (int i = 0; i < variables.length; i++){
		Variable var = (Variable)variables[i];
		if (var instanceof Constant){
			Constant constant = (Constant)var;
			pw.println(cbit.util.TokenMangler.getEscapedTokenMatlab(constant.getName())+" = "+constant.getExpression().infix_Matlab()+";");
		}
	}

	//pw.println("SIZEY = size(y);");
	//pw.println("if SIZEY ~= 0");
	Vector volVarList = new Vector();
	for (int i = 0; i < variables.length; i++){
		if (variables[i] instanceof VolVariable){
			volVarList.addElement(variables[i]);
		}
	}
	VolVariable volVars[] = (VolVariable[])cbit.util.BeanUtils.getArray(volVarList,VolVariable.class);
	CompartmentSubDomain subDomain = (CompartmentSubDomain)mathDesc.getSubDomains().nextElement();
	//
	// print volVariables (in order and assign to var vector)
	//
	pw.println("% Variables");
	pw.println("% need this for time=0 (initial conditions) when y is empty");
	for (int i = 0; i < volVars.length; i++){
		Expression initialExp = subDomain.getEquation(volVars[i]).getInitialExpression();
		pw.println(cbit.util.TokenMangler.getEscapedTokenMatlab(volVars[i].getName())+" = "+initialExp.infix_Matlab()+";\t\t% initial condition for '"+volVars[i].getName()+"'");
	}
	pw.println("SIZEY = size(y);");
	pw.println("if SIZEY ~= 0\t\t% when y is not empty");
	for (int i = 0; i < volVars.length; i++){
		pw.println("\t"+cbit.util.TokenMangler.getEscapedTokenMatlab(volVars[i].getName())+" = "+"y("+(i+1)+");");
	}
	pw.println("end");

	//
	// print functions
	//
	pw.println("% Functions");
	for (int i = 0; i < variables.length; i++){
		Variable var = (Variable)variables[i];
		if (var instanceof Function){
			Function function = (Function)var;
			pw.println(cbit.util.TokenMangler.getEscapedTokenMatlab(function.getName())+" = "+function.getExpression().infix_Matlab()+";");
		}
	}
	
	//
	// print switch statement
	//
	pw.println("switch flag");
	
	//
	// print ode-rate
	//
	pw.println("\tcase ''");
	pw.println("\t\tdydt = [");
	for (int i=0;i<volVars.length;i++){
		pw.println("\t\t\t"+subDomain.getEquation(volVars[i]).getRateExpression().infix_Matlab()+";    % rate for "+cbit.util.TokenMangler.getEscapedTokenMatlab(volVars[i].getName()));
	}
	pw.println("\t\t];");
	pw.println("\t\tvarargout{1} = dydt;");

	//
	// print initial values
	//
	pw.println("\tcase 'init'");
	double beginTime = 0.0;
	double endTime = simulation.getSolverTaskDescription().getTimeBounds().getEndingTime();
		pw.println("\t\ttspan = ["+beginTime+" "+endTime+"];");
	pw.print("\t\ty0 = [");
	for (int j=0;j<volVars.length;j++){
		pw.println("\t\t\t"+cbit.util.TokenMangler.getEscapedTokenMatlab(volVars[j].getName())+",\t\t% set above under variables");
	}
	pw.println("\t\t];");
	pw.println("\t\toptions = odeset('maxorder',5);");

	pw.println("\t\tvarargout{1} = tspan;");
	pw.println("\t\tvarargout{2} = y0;");
	pw.println("\t\tvarargout{3} = options;");
		
	//
	// print jacobian
	//
	pw.println("\tcase 'jacobian'");
	pw.println("\t\tdfdy = [");
	for (int i=0;i<volVars.length;i++){
		for (int j=0;j<volVars.length;j++){
			Expression rate = subDomain.getEquation(volVars[i]).getRateExpression();
			rate.bindExpression(simulation);
			rate = simulation.substituteFunctions(rate);
			Expression differential = rate.differentiate(volVars[j].getName());
			differential.bindExpression(simulation);
			differential = differential.flatten();
			pw.println("\t\t\t"+differential.infix_Matlab()+","); //    % d "+volVars[i].getName())+"' / d "+volVars[j].getName()));
		}
		pw.println("\t\t\t;");
	}
	pw.println("\t\t];");
	pw.println("\t\tvarargout{1} = dfdy;");

	if (stoichMatrix != null){
		//
		// print stoichiometry matrix if supplied
		//
		pw.println("\tcase 'stoichiometry'");
		pw.println("\t\tstoich = [");
		int numRows = stoichMatrix.getNumRows();
		int numCols = stoichMatrix.getNumCols();
		for (int i=0;i<numRows;i++){
			for (int j=0;j<numCols;j++){
				pw.print(stoichMatrix.get_elem(i,j)+",");
			}
			pw.println(";");
		}
		pw.println("\t\t];");
		pw.println("\t\tvarargout{1} = stoich;");
	}
	//
	// else unhandled
	//
	pw.println("\totherwise");
	pw.println("\t\terror(['Unknown flag ''' flag '''.']);");
	pw.println("\tend");
	pw.println("");
}
/**
 * Insert the method's description here.
 * Creation date: (3/8/00 10:31:52 PM)
 */
public void write_V6_MFile(java.io.PrintWriter pw, String functionName) throws MathException, ExpressionException {
	MathDescription mathDesc = simulation.getMathDescription();
	if (!mathDesc.isValid()){
		throw new MathException("invalid math description\n" + mathDesc.getWarning());
	}
	if (mathDesc.isSpatial()){
		throw new MathException("spatial math description, cannot create ode file");
	}
	if (mathDesc.hasFastSystems()){
		throw new MathException("math description contains algebraic constraints, cannot create .m file");
	}

	//
	// print function declaration
	//
	pw.println("function [T,Y,yinit,param] = "+functionName+"(argTimeSpan,argYinit,argParam)");
	pw.println("% [T,Y,yinit,param] = "+functionName+"(argTimeSpan,argYinit,argParam)");
	pw.println("%");
	pw.println("% input:");
	pw.println("%     argTimeSpan is a vector of start and stop times (e.g. timeSpan = [0 10.0])");
	pw.println("%     argYinit is a vector of initial conditions for the state variables (optional)");
	pw.println("%     argParam is a vector of values for the parameters (optional)");
	pw.println("%");
	pw.println("% output:");
	pw.println("%     T is the vector of times");
	pw.println("%     Y is the vector of state variables");
	pw.println("%     yinit is the initial conditions that were used");
	pw.println("%     param is the parameter vector that was used");
	pw.println("%");
	
	Variable variables[] = simulation.getVariables();
	CompartmentSubDomain subDomain = (CompartmentSubDomain)mathDesc.getSubDomains().nextElement();

	//
	// collect "true" constants (Constants without identifiers)
	//
	Vector constantList = new Vector();
	for (int i = 0; i < variables.length; i++){
		if (variables[i] instanceof Constant){
			Constant constant = (Constant)variables[i];
			String[] symbols = constant.getExpression().getSymbols();
			if (symbols==null || symbols.length==0){
				constantList.addElement(variables[i]);
			}
		}
	}
	Constant constants[] = (Constant[])cbit.util.BeanUtils.getArray(constantList,Constant.class);
	
	//
	// collect "variables" (VolVariables only)
	//
	Vector volVarList = new Vector();
	for (int i = 0; i < variables.length; i++){
		if (variables[i] instanceof VolVariable){
			volVarList.addElement(variables[i]);
		}
	}
	VolVariable volVars[] = (VolVariable[])cbit.util.BeanUtils.getArray(volVarList,VolVariable.class);
	
	//
	// collect "functions" (Functions and Constants with identifiers)
	//
	Vector functionList = new Vector();
	for (int i = 0; i < variables.length; i++){
		if (variables[i] instanceof Constant){
			Constant constant = (Constant)variables[i];
			String[] symbols = constant.getExpression().getSymbols();
			if (symbols!=null && symbols.length>0){
				functionList.add(constant);
			}
		}
	}
	for (int i = 0; i < variables.length; i++){
		if (variables[i] instanceof Function){
			functionList.addElement(variables[i]);
		}
	}
	Variable functions[] = (Variable[])cbit.util.BeanUtils.getArray(functionList,Variable.class);

	
	pw.println("");
	pw.println("%");
	pw.println("% Default time span");
	pw.println("%");
	double beginTime = 0.0;
	double endTime = simulation.getSolverTaskDescription().getTimeBounds().getEndingTime();
	pw.println("timeSpan = ["+beginTime+" "+endTime+"];");
	pw.println("");
	pw.println("if nargin >= 1");
	pw.println("\tif length(argTimeSpan) > 0");
	pw.println("\t\t%");
	pw.println("\t\t% TimeSpan overridden by function arguments");
	pw.println("\t\t%");
	pw.println("\t\ttimeSpan = argTimeSpan;");
	pw.println("\tend");
	pw.println("end");

	
	pw.println("%");
	pw.println("% Default Initial Conditions");
	pw.println("%");
	pw.println("yinit = [");
	for (int j=0;j<volVars.length;j++){
		Expression initial = subDomain.getEquation(volVars[j]).getInitialExpression();
		double defaultInitialCondition = 0;
		try {
			initial.bindExpression(mathDesc);
			defaultInitialCondition = initial.evaluateConstant();
		} catch (ExpressionException e) {
			e.printStackTrace(System.out);
			throw new RuntimeException("error evaluating initial condition for variable "+volVars[j].getName());
		}
		pw.println("\t"+defaultInitialCondition+";\t\t% yinit("+(j+1)+") is the initial condition for '"+cbit.util.TokenMangler.getEscapedTokenMatlab(volVars[j].getName())+"'");
	}
	pw.println("];");
	pw.println("if nargin >= 2");
	pw.println("\tif length(argYinit) > 0");
	pw.println("\t\t%");
	pw.println("\t\t% initial conditions overridden by function arguments");
	pw.println("\t\t%");
	pw.println("\t\tyinit = argYinit;");
	pw.println("\tend");
	pw.println("end");


	pw.println("%");
	pw.println("% Default Parameters");
	pw.println("%   constants are only those \"Constants\" from the Math Description that are just floating point numbers (no identifiers)");
	pw.println("%   note: constants of the form \"A_init\" are really initial conditions and are treated in \"yinit\"");
	pw.println("%");
	pw.println("param = [");
	int paramIndex = 0;
	for (int i = 0; i < constants.length; i++){
		boolean isInitialCondition = false;
		for (int j = 0; j < volVars.length; j++){
			if (constants[i].getName().equals(volVars[j].getName()+"_init")){
				isInitialCondition = true;
			}
		}
		if (!isInitialCondition){
			pw.println("\t"+constants[i].getExpression().infix_Matlab()+";\t\t% param("+(paramIndex+1)+") is '"+cbit.util.TokenMangler.getEscapedTokenMatlab(constants[i].getName())+"'");
			paramIndex++;
		}
	}
	pw.println("];");
	pw.println("if nargin >= 3");
	pw.println("\tif length(argParam) > 0");
	pw.println("\t\t%");
	pw.println("\t\t% parameter values overridden by function arguments");
	pw.println("\t\t%");
	pw.println("\t\tparam = argParam;");
	pw.println("\tend");
    pw.println("end");
    

	pw.println("%");
	pw.println("% invoke the integrator");
	pw.println("%");
	pw.println("[T,Y] = ode15s(@f,timeSpan,yinit,odeset('OutputFcn',@odeplot),param,yinit);");
	pw.println("% done");
	
	//
	// print ode-rate
	//
	pw.println("");
	pw.println("% -------------------------------------------------------");
	pw.println("");
	pw.println("function dydt = f(t,y,p,y0)");
	//
	// print volVariables (in order and assign to var vector)
	//
	pw.println("\t% State Variables");
	for (int i = 0; i < volVars.length; i++){
		pw.println("\t"+cbit.util.TokenMangler.getEscapedTokenMatlab(volVars[i].getName())+" = y("+(i+1)+");");
	}
	//
	// print constants
	//
	pw.println("\t% Constants");
	paramIndex = 0;
	for (int i = 0; i < constants.length; i++){
		//
		// check for initial value constants (e.g. A_init) and replace with A|t=0
		//
		int initialConditionIndex = -1;
		for (int j = 0; j < volVars.length; j++){
			if (constants[i].getName().equals(volVars[j].getName()+"_init")){
				initialConditionIndex = j;
			}
		}
		if (initialConditionIndex>=0){
			//
			// this constant is not a parameter, it is really the initial condition of one of the state variables
			//
			pw.println("\t"+cbit.util.TokenMangler.getEscapedTokenMatlab(constants[i].getName())+" = y0("+(initialConditionIndex+1)+");\t\t% note: initial condition used as a constant");
		}else{
			//
			// this constant is a regular parameter, resolve to the parameter vector
			//
			pw.println("\t"+cbit.util.TokenMangler.getEscapedTokenMatlab(constants[i].getName())+" = p("+(paramIndex+1)+");");
			paramIndex++;
		}
	}
	//
	// print variables
	//
	pw.println("\t% Functions");
	for (int i = 0; i < functions.length; i++){
		pw.println("\t"+cbit.util.TokenMangler.getEscapedTokenMatlab(functions[i].getName())+" = "+functions[i].getExpression().infix_Matlab()+";");
	}
	pw.println("\t% Rates");
	pw.println("\tdydt = [");
	for (int i=0;i<volVars.length;i++){
		pw.println("\t\t"+subDomain.getEquation(volVars[i]).getRateExpression().infix_Matlab()+";    % rate for "+cbit.util.TokenMangler.getEscapedTokenMatlab(volVars[i].getName()));
	}
	pw.println("\t];");
}
}
