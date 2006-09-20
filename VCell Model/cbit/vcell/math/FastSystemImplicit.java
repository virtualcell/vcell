package cbit.vcell.math;
import cbit.vcell.solver.Simulation;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.util.*;
import java.io.*;
import cbit.vcell.parser.*;
import cbit.vcell.matrix.RationalExpMatrix;
import cbit.vcell.matrix.RationalExp;
import cbit.vcell.matrix.RationalExpUtils;
/**
 * This type was created in VisualAge.
 */
public class FastSystemImplicit extends FastSystem {
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
	protected RationalExpMatrix dependencyMatrix = null;

/**
 * FastSystem constructor comment.
 */
public FastSystemImplicit(MathDescription mathDesc) {
	super(mathDesc);
}


/**
 * This method was created in VisualAge.
 * @exception java.lang.Exception The exception description.
 */
private void checkLinearity() throws MathException, ExpressionException {
	int varCount=0;
	Enumeration enum1 = fastVarList.elements();
	while (enum1.hasMoreElements()){
		Variable var = (Variable)enum1.nextElement();
		//
		//                               d invariant
		// for each variable, make sure ------------- = constant;
		//                                  d Var
		//
		Enumeration enum_fi = getFastInvariants();
		while (enum_fi.hasMoreElements()){
			FastInvariant fi = (FastInvariant)enum_fi.nextElement();
			Expression exp = fi.getFunction().differentiate(var.getName());
			exp.bindExpression(mathDesc);
			try {
				exp.evaluateConstant();
			}catch (Exception e){
				throw new MathException("FastInvariant "+fi.getFunction().toString()+" isn't linear, d/d("+var.getName()+") = "+exp.toString());
			}
		}
	}
}


/**
 * Insert the method's description here.
 * Creation date: (10/10/2002 11:15:31 AM)
 * @param sim cbit.vcell.solver.Simulation
 */
void flatten(cbit.vcell.solver.Simulation sim, boolean bRoundCoefficients) throws ExpressionException, MathException {
	//
	// replace fastRates with flattended and substituted fastRates
	//
	for (int i = 0; i < this.fastRateList.size(); i++){
		Expression oldExp = ((FastRate)fastRateList.elementAt(i)).getFunction();
		fastRateList.setElementAt(new FastRate(getFlattenedExpression(sim,oldExp,bRoundCoefficients)),i);
	}
	
	//
	// replace fastInvariants with flattended and substituted fastInvariants
	//
	for (int i = 0; i < this.fastInvariantList.size(); i++){
		Expression oldExp = ((FastInvariant)fastInvariantList.elementAt(i)).getFunction();
		fastInvariantList.setElementAt(new FastInvariant(getFlattenedExpression(sim,oldExp,bRoundCoefficients)),i);
	}

	//
	// flag that all needs to be recalculated
	//
	bNeedsRefresh = true;
}


/**
 * This method was created by a SmartGuide.
 * @return java.lang.String
 */
public String getVCML() {
//	try {
//		refreshAll();
//	}catch (Exception e){
//		e.printStackTrace(System.out);
//	}
	StringBuffer buffer = new StringBuffer();
	buffer.append("\t"+VCML.FastSystem+" {\n");

	Enumeration enum_fi = getFastInvariants();
	while (enum_fi.hasMoreElements()){
		FastInvariant fi = (FastInvariant)enum_fi.nextElement();
		buffer.append("\t\t"+VCML.FastInvariant+"\t"+fi.getFunction().infix()+";\n");
	}	
		
	Enumeration enum_fr = fastRateList.elements();
	while (enum_fr.hasMoreElements()){
		FastRate fr = (FastRate)enum_fr.nextElement();
		buffer.append("\t\t"+VCML.FastRate+"\t"+fr.getFunction().infix()+";\n");
	}	
		
	buffer.append("\t}\n");
	return buffer.toString();		
}


/**
 * This method was created by a SmartGuide.
 * @param tokens java.util.StringTokenizer
 * @exception java.lang.Exception The exception description.
 */
public void read(CommentStringTokenizer tokens) throws MathException, ExpressionException {
	String token = null;
	token = tokens.nextToken();
	if (!token.equalsIgnoreCase(VCML.BeginBlock)){
		throw new MathFormatException("unexpected token "+token+" expecting "+VCML.BeginBlock);
	}			
	while (tokens.hasMoreTokens()){
		token = tokens.nextToken();
		if (token.equalsIgnoreCase(VCML.EndBlock)){
			break;
		}			
		if (token.equalsIgnoreCase(VCML.FastRate)){
			Expression rate = new Expression(tokens);
			FastRate fr = new FastRate(rate);
			addFastRate(fr);
			continue;
		}			
		if (token.equalsIgnoreCase(VCML.FastInvariant)){
			Expression invariant = new Expression(tokens);
			FastInvariant fi = new FastInvariant(invariant);
			addFastInvariant(fi);
			continue;
		}			
		throw new MathFormatException("unexpected identifier "+token);
	}
	bNeedsRefresh = true;
}


/**
 * This method was created in VisualAge.
 */
protected void refreshAll() throws MathException, ExpressionException {
	if (bNeedsRefresh){
		bNeedsRefresh = false;
		refreshFastVarList();
		checkLinearity();
		refreshInvarianceMatrix();
		refreshSubstitutedRateExps();
	}
}


/**
 * This method was created in VisualAge.
 * @return java.util.Vector
 */
private void refreshFastVarList() throws MathException, ExpressionException {
	fastVarList = new Vector();

	//
	// get list of unique (VolVariables and MemVariables) in fastRate expressions
	//
	for (int i = 0; i < fastRateList.size(); i++) {
		FastRate fr = (FastRate) fastRateList.elementAt(i);
		Expression exp = fr.getFunction();
		Enumeration enum1 = MathUtilities.getRequiredVariables(exp,mathDesc);
		while (enum1.hasMoreElements()) {
			Variable var = (Variable) enum1.nextElement();
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
	// these variables may be eligible for elimination from the fastSystem.
	//
	// make a list of the fastInvariants that reference each of these variables,
	//
	Hashtable hashtable = new Hashtable();
	for (int i = 0; i < fastInvariantList.size(); i++) {
		FastInvariant fi = (FastInvariant) fastInvariantList.elementAt(i);
		Expression exp = fi.getFunction();
//System.out.println("FastSystemImplicit.refreshFastVarList(), ORIGINAL FAST INVARIANT: "+exp);
		Enumeration enum1 = MathUtilities.getRequiredVariables(exp,mathDesc);
		while (enum1.hasMoreElements()) {
			Variable var = (Variable) enum1.nextElement();
			if (var instanceof VolVariable || var instanceof MemVariable) {
				if (!fastVarList.contains(var)) {
					Vector fiList = null;
					if (hashtable.containsKey(var)) {
						fiList = (Vector) hashtable.get(var);
					}else{
						fiList = new Vector();
					}
					fiList.addElement(fi);
					hashtable.put(var, fiList);
				}
			}
		}
	}

	//
	// go through the list of potentially extraneous variables
	//
	// delete those fastInvariants that are the only reference to one of these variables
	//
	Enumeration hashEnum = hashtable.keys();
	while (hashEnum.hasMoreElements()){
		Variable var = (Variable)hashEnum.nextElement();
		Vector fiList = (Vector)hashtable.get(var);
		//
		// if there is only one reference, then delete this fast Invariant
		//
		if (fiList.size()==1){
			FastInvariant fi = (FastInvariant)fiList.elementAt(0);
			if (fastInvariantList.contains(fi)){
				fastInvariantList.removeElement(fi);
//System.out.println("FastSystemImplicit.refreshFastVarList(), variable("+var.getName()+") not referenced, removing fast invariant: exp = "+fi.getFunction());
//if (!fastVarList.contains(var)){
//fastVarList.addElement(var);
//}
			}
		//
		// if there is more than one reference, then add to the fastVariable list 
		//
		}else if (fiList.size()>1){
			if (!fastVarList.contains(var)){
				fastVarList.addElement(var);
//System.out.println("FastSystemImplicit.refreshFastVarList(), variable("+var.getName()+") referenced in multiple fast invariants, adding to fast variables: var = "+var.getName());
			}
		}
	}

	//
	// verify that there are N equations (rates+invariants) and N unknowns (fastVariables)
	//
	int numBoundFunctions = fastInvariantList.size() + fastRateList.size();
	if (fastVarList.size() != numBoundFunctions) {
		throw new MathException("FastSystem.checkDimension(), there are " + fastVarList.size() + " variables and " + numBoundFunctions + " FastInvariant's & FastRates");
	}
}


/**
 * This method was created in VisualAge.
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
	int rows = fastInvariantList.size();
	int cols = numVars + fastInvariantList.size();
	cbit.vcell.matrix.RationalExpMatrix matrix = new cbit.vcell.matrix.RationalExpMatrix(rows,cols);

	for (int i=0;i<rows;i++){
		Expression function = ((FastInvariant)fastInvariantList.elementAt(i)).getFunction();
		for (int j=0;j<numVars;j++){
			Variable var = (Variable)fastVarList.elementAt(j);
			Expression exp = function.differentiate(var.getName()).flatten();
			RationalExp coeffRationalExp = RationalExpUtils.getRationalExp(exp);
			matrix.set_elem(i,j,coeffRationalExp);
		}
		matrix.set_elem(i,numVars+i,-1);
	}
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
System.out.println("reducedMatrix");
	if (rows > 0){
		try {
			matrix.gaussianElimination(new cbit.vcell.matrix.RationalExpMatrix(rows,rows));
		}catch(cbit.vcell.matrix.MatrixException e){
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
		// if mat(i,i)!=1, rotate columns and swap variables
		//
		if (!matrix.get(i,i).isConstant() || matrix.get(i,i).getConstant().doubleValue() != 1){
			for (int j=i+1;j<cols;j++){
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
	int numInvariants = fastInvariantList.size();
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
			cbit.vcell.matrix.RationalExp rexp = matrix.get(i,j+dependentVarList.size()).simplify().minus();
			dependencyMatrix.set_elem(i,j,rexp);
		}
	}

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
 * This method was created in VisualAge.
 */
private void refreshSubstitutedRateExps() throws MathException, ExpressionException {
	//
	// refresh PseudoConstants (temp constants involved with fastInvariants)
	//
	pseudoConstantList.removeAllElements();
	for (int i=0;i<fastInvariantList.size();i++){
		FastInvariant fi = (FastInvariant)fastInvariantList.elementAt(i);
		PseudoConstant pc = new PseudoConstant(getAvailablePseudoConstantName(),fi.getFunction());
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
			cbit.vcell.matrix.RationalExp coefExp = dependencyMatrix.get(row,col);
			if (!coefExp.isZero()){
				exp = Expression.add(exp, new Expression(coefExp.infixString()+"*"+indepVar.getName()));
			}
		}
		//
		// get terms involving pseudoConstants
		//
		for (int col=independentVarList.size();col<dependencyMatrix.getNumCols();col++){
			PseudoConstant pc = (PseudoConstant)pseudoConstantList.elementAt(col-independentVarList.size());
			cbit.vcell.matrix.RationalExp coefExp = dependencyMatrix.get(row,col);
			if (!coefExp.isZero()){
				exp = Expression.add(exp, new Expression(coefExp.infixString()+"*"+pc.getName()));
			}
		}
		exp = exp.flatten();
//System.out.println("FastSystem.refreshSubstitutedRateExps() "+((Variable)dependentVarList.elementAt(row)).getName()+" = "+exp.toString()+";");
		dependencyExpList.addElement(exp);
	}
	
	//
	// flatten functions, then substitute expressions for dependent vars into rate expressions
	//
	fastRateExpList.removeAllElements();
	for (int i=0;i<fastRateList.size();i++){
		FastRate fr = (FastRate)fastRateList.elementAt(i);
		Expression exp = new Expression(MathUtilities.substituteFunctions(new Expression(fr.getFunction()),mathDesc));
//System.out.println("FastSystem.refreshSubstitutedRateExps() fast rate before substitution = "+exp.toString());
		for (int j=0;j<dependentVarList.size();j++){
			Variable depVar = (Variable)dependentVarList.elementAt(j);
			Expression subExp = new Expression((Expression)dependencyExpList.elementAt(j));
			exp.substituteInPlace(new Expression(depVar.getName()),subExp);
		}
		exp.bindExpression(null);
		exp = exp.flatten();
//System.out.println("FastSystem.refreshSubstitutedRateExps() fast rate after substitution  = "+exp.toString());
		exp.bindExpression(mathDesc);
		fastRateExpList.addElement(exp);
	}	
}
}