/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.mapping;

import java.util.Enumeration;
import java.util.Map;
import java.util.Vector;

import cbit.vcell.math.FastInvariant;
import cbit.vcell.math.FastRate;
import cbit.vcell.math.FastSystem;
import cbit.vcell.math.MathException;
import cbit.vcell.math.MathUtilities;
import cbit.vcell.math.MemVariable;
import cbit.vcell.math.PseudoConstant;
import cbit.vcell.math.ReservedVariable;
import cbit.vcell.math.Variable;
import cbit.vcell.math.VolVariable;
import cbit.vcell.math.Variable.Domain;
import cbit.vcell.matrix.MatrixException;
import cbit.vcell.matrix.RationalExp;
import cbit.vcell.matrix.RationalExpMatrix;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.RationalExpUtils;
import cbit.vcell.parser.SymbolTable;
import cbit.vcell.parser.SymbolTableEntry;
/**
 */
public class FastSystemAnalyzer implements SymbolTable {
	//
	// the first r variables are the dependent ones
	// the last N-r variables are the independent ones
	// the matrix is in the form:
	//
	//   |iiiccccc|
	//   |iiiccccc|  where (c)'s are the coefficients for constants of invariances
	//   |iiiccccc|        (i)'s are the coefficients for dependent vars in terms of independent vars
	//   |iiiccccc|         r   = 5 dependent vars
	//   |iiiccccc|         N-r = 3 independent vars 
	//
	private FastSystem fastSystem = null;
	private SymbolTable symbolTable = null;
	protected RationalExpMatrix dependencyMatrix = null;

	protected Vector<Expression> dependencyExpList = new Vector<Expression>();
	protected Vector<Variable> dependentVarList = new Vector<Variable>();
	protected Vector<Variable> fastVarList = new Vector<Variable>();
	protected Vector<Variable> independentVarList = new Vector<Variable>();
	protected Vector<PseudoConstant> pseudoConstantList = new Vector<PseudoConstant>();
	protected Vector<Expression> fastRateExpList = new Vector<Expression>();

/**
 * FastSystem constructor comment.
 */
public FastSystemAnalyzer(FastSystem argFastSystem, SymbolTable argSymbolTable) throws MathException, ExpressionException {
	super();
	this.fastSystem = argFastSystem;
	this.symbolTable = argSymbolTable;
	refreshAll();
}

public SymbolTableEntry getEntry(String id) {
	// combines mathDesc symbolTable and pseudoconstants from fastSysAnalyzer.
	SymbolTableEntry ste = symbolTable.getEntry(id);
	if (ste == null){
		ste = getPseudoConstant(id);
	}
	return ste;
}

/**
 * @exception java.lang.Exception The exception description.
 */
private void checkLinearity() throws MathException, ExpressionException {
	Enumeration<Variable> enum1 = fastVarList.elements();
	while (enum1.hasMoreElements()){
		Variable var = enum1.nextElement();
		//
		//                               d invariant
		// for each variable, make sure ------------- = constant;
		//                                  d Var
		//
		Enumeration<FastInvariant> enum_fi = fastSystem.getFastInvariants();
		while (enum_fi.hasMoreElements()){
			FastInvariant fi = enum_fi.nextElement();
			Expression exp = fi.getFunction().differentiate(var.getName());
			exp = MathUtilities.substituteFunctions(exp, this).flatten();
			
			if (!exp.isNumeric()) {
				// If expression is in terms of 'x','y','z' - then its ok - relax the constant requirement.
				String[] symbols = exp.getSymbols();
				for (int i = 0;  i < symbols.length; i++) {
					if (!symbols[i].equals(ReservedVariable.X.getName()) && 
						!symbols[i].equals(ReservedVariable.Y.getName()) && 
						!symbols[i].equals(ReservedVariable.Z.getName()) && 
						!symbols[i].equals(ReservedVariable.TIME.getName()) ) {
						throw new MathException("FastInvariant "+fi.getFunction().toString()+" isn't linear, d/d("+var.getName()+") = "+exp.toString());
					}
				}
			}
		}
	}
}


/**
 */
private void refreshAll() throws MathException, ExpressionException {
	refreshFastVarList();
	checkLinearity();
	refreshInvarianceMatrix();
	refreshSubstitutedRateExps();
}


/**
 * @return java.util.Vector
 */
private void refreshFastVarList() throws MathException, ExpressionException {
	fastVarList.clear();

	//
	// get list of unique (VolVariables and MemVariables) in fastRate expressions
	//
	Enumeration<FastRate> fastRatesEnum = fastSystem.getFastRates(); 
	while (fastRatesEnum.hasMoreElements()){
		FastRate fr  = fastRatesEnum.nextElement();
		Expression exp = fr.getFunction();
		Enumeration<Variable> enum1 = MathUtilities.getRequiredVariables(exp, this);
		while (enum1.hasMoreElements()) {
			Variable var = enum1.nextElement();
			if (var instanceof VolVariable || var instanceof MemVariable) {
				if (!fastVarList.contains(var)) {
					fastVarList.addElement(var);
					//System.out.println("FastSystemImplicit.refreshFastVarList(), FAST RATE VARIABLE: "+var.getName());
				}
			}
		}
	}	
	//
	// get list of all variables used in invariant expressions that are not used in fast rates
	//
	Enumeration<FastInvariant> fastInvariantsEnum = fastSystem.getFastInvariants(); 
	while (fastInvariantsEnum.hasMoreElements()) {
		FastInvariant fi = (FastInvariant) fastInvariantsEnum.nextElement();
		Expression exp = fi.getFunction();
		//System.out.println("FastSystemImplicit.refreshFastVarList(), ORIGINAL FAST INVARIANT: "+exp);
		Enumeration<Variable> enum1 = MathUtilities.getRequiredVariables(exp, this);
		while (enum1.hasMoreElements()) {
			Variable var = enum1.nextElement();
			if (var instanceof VolVariable || var instanceof MemVariable) {
				if (!fastVarList.contains(var)) {
					fastVarList.addElement(var);
				}
			}
		}
	}
	
	//
	// verify that there are N equations (rates+invariants) and N unknowns (fastVariables)
	//
	int numBoundFunctions = fastSystem.getNumFastInvariants() + fastSystem.getNumFastRates();
	if (fastVarList.size() != numBoundFunctions) {
		throw new MathException("FastSystem.checkDimension(), there are " + fastVarList.size() + " variables and " + numBoundFunctions + " FastInvariant's & FastRates");
	}
}


/**
 */
private void refreshInvarianceMatrix() throws MathException, ExpressionException {
	//
	// the invariance's are expressed in matrix form
	//
	//   |a a a a a -1  0  0|   |x1|   |0|
	//   |a a a a a  0 -1  0| * |x2| = |0|
	//   |a a a a a  0  0 -1|   |x3|   |0|
	//                          |x4|
	//                          |x5|
	//                          |c1|
	//                          |c2|
	//                          |c3|
	//
	Variable vars[] = new Variable[fastVarList.size()];
	fastVarList.copyInto(vars);
	int numVars = fastVarList.size();
	int rows = fastSystem.getNumFastInvariants();
	int cols = numVars + fastSystem.getNumFastInvariants();
	RationalExpMatrix matrix = new RationalExpMatrix(rows,cols);

	Enumeration<FastInvariant> fastInvariantsEnum = fastSystem.getFastInvariants(); 
	for (int i = 0; i < rows && fastInvariantsEnum.hasMoreElements(); i++){
		FastInvariant fi = (FastInvariant) fastInvariantsEnum.nextElement();
		Expression function = fi.getFunction();
		for (int j = 0; j < numVars; j++){
			Variable var = (Variable)fastVarList.elementAt(j);
			Expression exp = function.differentiate(var.getName());
			exp.bindExpression(null);
			exp = exp.flatten();
			RationalExp coeffRationalExp = RationalExpUtils.getRationalExp(exp);
			matrix.set_elem(i, j, coeffRationalExp);
		}
		matrix.set_elem(i,numVars+i,-1);
	}
	
	// Print
	System.out.println("origMatrix");
	matrix.show();
	
	//
	// gaussian elimination on the matrix give the following representation
	// note that some column pivoting (variable re-ordering) is sometimes required to 
	// determine N-r dependent vars
	//
	//   |10i0iccc|
	//   |01i0iccc|  where (c)'s are the coefficients for constants of invariances
	//   |00i1iccc|        (i)'s are the coefficients for dependent vars in terms of independent vars
	//
	// Print
	System.out.println("reducedMatrix");
	if (rows > 0){
		try {
			matrix.gaussianElimination(new RationalExpMatrix(rows,rows));
		}catch(MatrixException e){
			e.printStackTrace(System.out);
			throw new MathException(e.getMessage());
		}
	}
	matrix.show();
	for (int i=0;i<vars.length;i++){
		System.out.print(vars[i].getName()+"  ");
	}
	System.out.println("");

	//
	// re-ordering of columns (to get N-r x N-r identity matrix at left)
	//
	//   |100iiccc|                            T          T
	//   |010iiccc| * [x1 x2 x4 x3 x5 c1 c2 c3]  = [0 0 0] 
	//   |001iiccc| 
	//
	for (int i=0;i<rows;i++){
		for (int j=0;j<rows;j++){
			RationalExp rexp = matrix.get(i,j).simplify();
			matrix.set_elem(i,j,rexp);
		}
	}
	for (int i=0;i<rows;i++){
		//
		// if mat(i,i)!=1, rotate columns and swap variables (don't swap pseudo constants)
		//
		if (!matrix.get(i,i).isConstant() || matrix.get(i,i).getConstant().doubleValue() != 1){
			for (int j=i+1;j<numVars;j++){
				if (matrix.get(i,j).isConstant() && matrix.get(i,j).getConstant().doubleValue()==1.0){
					for (int ii=0;ii<rows;ii++){
						RationalExp temp = matrix.get(ii,i);
						matrix.set_elem(ii,i,matrix.get(ii,j));
						matrix.set_elem(ii,j,temp);
					}
					Variable tempVar = vars[i];
					vars[i] = vars[j];
					vars[j] = tempVar;
					break;
				}
			}
		}
	}
	// Print
	for (int i=0;i<vars.length;i++){
		System.out.print(vars[i].getName()+"  ");
	}
	System.out.println("");
	matrix.show();
	
	//
	// separate into dependent and indepent variables, and chop off identity matrix (left N-r columns)
	//
	//            T       |iiccc|                   T
	//  [x1 x2 x4] = -1 * |iiccc| * [x3 x5 c1 c2 c3]
	//                    |iiccc|
	//
	//
	int numInvariants = fastSystem.getNumFastInvariants();
	dependentVarList.removeAllElements();
	for (int i=0;i<numInvariants;i++){
		dependentVarList.addElement(vars[i]);
	}
	independentVarList.removeAllElements();
	for (int i=numInvariants;i<vars.length;i++){
		independentVarList.addElement(vars[i]);
	}
	int new_cols = independentVarList.size() + numInvariants;
	dependencyMatrix = new RationalExpMatrix(rows, new_cols);
	for (int i=0;i<rows;i++){
		for (int j=0;j<new_cols;j++){
			RationalExp rexp = matrix.get(i,j+dependentVarList.size()).simplify().minus();
			dependencyMatrix.set_elem(i,j,rexp);
		}
	}

	// Print
	System.out.println("\n\nDEPENDENCY MATRIX");
	dependencyMatrix.show();
	System.out.print("dependent vars: ");
	for (int i=0;i<dependentVarList.size();i++){
		System.out.print(((Variable)dependentVarList.elementAt(i)).getName()+"  ");
	}
	System.out.println("");			
	System.out.print("independent vars: ");
	for (int i=0;i<independentVarList.size();i++){
		System.out.print(((Variable)independentVarList.elementAt(i)).getName()+"  ");
	}
	System.out.println("");			

}


/**
 * 
 */
private void refreshSubstitutedRateExps() throws MathException, ExpressionException {
	//
	// refresh PseudoConstants (temp constants involved with fastInvariants)
	//
	pseudoConstantList.removeAllElements();
	Enumeration<FastInvariant> enum_fi = fastSystem.getFastInvariants();
	while (enum_fi.hasMoreElements()) {
		FastInvariant fi = enum_fi.nextElement();
		Domain domain = new Domain(fastSystem.getSubDomain());
		PseudoConstant pc = new PseudoConstant(getAvailablePseudoConstantName(),fi.getFunction(),domain);
		pseudoConstantList.addElement(pc);
		//System.out.println("FastSystem.refreshSubstitutedRateExps() __C"+i+" = "+fi.getFunction());
	}

	//
	// build expressions for dependent variables in terms of independent vars
	// and pseudoConstants
	//
	dependencyExpList.removeAllElements();
	for (int row=0;row<dependentVarList.size();row++){
		Expression exp = new Expression("0.0;");
		//
		// get terms involving independent variables
		//
		for (int col=0;col<independentVarList.size();col++){
			Variable indepVar = (Variable)independentVarList.elementAt(col);
			RationalExp coefExp = dependencyMatrix.get(row,col).simplify();
			if (!coefExp.isZero()){
				exp = Expression.add(exp, new Expression(coefExp.infixString()+"*"+indepVar.getName()));
			}
		}
		//
		// get terms involving pseudoConstants
		//
		for (int col=independentVarList.size();col<dependencyMatrix.getNumCols();col++){
			PseudoConstant pc = (PseudoConstant)pseudoConstantList.elementAt(col-independentVarList.size());
			RationalExp coefExp = dependencyMatrix.get(row,col);
			if (!coefExp.isZero()){
				exp = Expression.add(exp, new Expression(coefExp.infixString()+"*"+pc.getName()));
			}
		}
		exp.bindExpression(null);
		exp = exp.flatten();
		exp.bindExpression(this);
		//System.out.println("FastSystem.refreshSubstitutedRateExps() "+((Variable)dependentVarList.elementAt(row)).getName()+" = "+exp.toString()+";");
		dependencyExpList.addElement(exp);
	}
	
	//
	// flatten functions, then substitute expressions for dependent vars into rate expressions
	//
	fastRateExpList.removeAllElements();
	// VariableSymbolTable combinedSymbolTable = getCombinedSymbolTable();
	Enumeration<FastRate> enum_fr = fastSystem.getFastRates();	
	while (enum_fr.hasMoreElements()) {
		FastRate fr = enum_fr.nextElement();
		Expression exp = new Expression(MathUtilities.substituteFunctions(new Expression(fr.getFunction()), this));
		//System.out.println("FastSystem.refreshSubstitutedRateExps() fast rate before substitution = "+exp.toString());
		for (int j=0;j<dependentVarList.size();j++){
			Variable depVar = (Variable)dependentVarList.elementAt(j);
			Expression subExp = new Expression((Expression)dependencyExpList.elementAt(j));
			exp.substituteInPlace(new Expression(depVar.getName()),subExp);
		}
		exp.bindExpression(null);
		exp = exp.flatten();
		//System.out.println("FastSystem.refreshSubstitutedRateExps() fast rate after substitution  = "+exp.toString());
		exp.bindExpression(this);
		fastRateExpList.addElement(exp);
	}	
}

/**
 * Insert the method's description here.
 * Creation date: (1/29/2002 4:24:21 PM)
 * @return java.lang.String
 */
protected String getAvailablePseudoConstantName() throws ExpressionBindingException {
	String base = "__C";
	int i = 0;
	while (true){
		if (this.getEntry(base+i)==null){
			return base+i;
		}
		i++;
	}
}


/**
 * @return java.util.Enumeration
 */
public Enumeration<Expression> getDependencyExps() {
//	refreshAll();
	return dependencyExpList.elements();
}


/**
 * @return java.util.Enumeration
 */
public final Enumeration<Variable> getDependentVariables() {
//	refreshAll();
	return dependentVarList.elements();
}


/**
 * @return java.util.Enumeration
 */
public final Enumeration<Variable> getIndependentVariables() {
//	refreshAll();
	return independentVarList.elements();
}

public final Variable getIndependentVariable(int indx) {
//	refreshAll();
	return independentVarList.elementAt(indx);
}

/**
 * @return int
 */
public int getNumDependentVariables() {
//	refreshAll();
	return dependentVarList.size();
}


/**
 * @return int
 */
public int getNumIndependentVariables() {
//	refreshAll();
	return independentVarList.size();
}


/**
 * @return int
 */
public int getNumPseudoConstants() {
//	refreshAll();
	return pseudoConstantList.size();
}


/**
 * @return cbit.vcell.math.PseudoConstant
 * @param id java.lang.String
 */
public PseudoConstant getPseudoConstant(String id) {
//	refreshAll();
	for (int i=0;i<pseudoConstantList.size();i++){
		PseudoConstant pc = (PseudoConstant)pseudoConstantList.elementAt(i);
		if (pc.getName().equals(id)){
			return pc;
		}
	}
	return null;
}


/**
 * @return java.util.Enumeration
 */
public Enumeration<PseudoConstant> getPseudoConstants() {
//	refreshAll();
	return pseudoConstantList.elements();
}


private FastSystem getFastSystem() {
	return fastSystem;
}


/**
 * @return java.util.Enumeration
 */
public Enumeration<Expression> getFastRateExpressions() {
	return fastRateExpList.elements();
}

public void getEntries(Map<String, SymbolTableEntry> entryMap) {
	symbolTable.getEntries(entryMap);
	for (PseudoConstant pc : pseudoConstantList) {
		entryMap.put(pc.getName(), pc);
	}
}
}
