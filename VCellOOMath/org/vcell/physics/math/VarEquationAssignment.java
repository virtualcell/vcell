package org.vcell.physics.math;

import org.vcell.physics.component.Symbol;

/**
 * Insert the type's description here.
 * Creation date: (1/17/2006 5:40:46 PM)
 * @author: Jim Schaff
 */
public class VarEquationAssignment {
	private Symbol symbol = null;
	private boolean stateVariable = false;
	private cbit.vcell.parser.Expression equation = null;
	private cbit.vcell.parser.Expression solution = null;

/**
 * VarEquationAssignment constructor comment.
 */
public VarEquationAssignment(Symbol argSymbol, cbit.vcell.parser.Expression argEquation) {
	super();
	this.symbol = argSymbol;
	this.equation = argEquation;
}


/**
 * Insert the method's description here.
 * Creation date: (1/17/2006 7:15:56 PM)
 * @return boolean
 * @param o java.lang.Object
 */
public boolean equals(Object o) {
	if (o instanceof VarEquationAssignment){
		VarEquationAssignment vea = (VarEquationAssignment)o;
		if (!vea.equation.equals(equation)){
			return false;
		}
		if (!vea.symbol.equals(symbol)){
			return false;
		}
		return true;
	}
	return false;
}


/**
 * Insert the method's description here.
 * Creation date: (1/30/2006 11:35:03 AM)
 * @return cbit.vcell.parser.Expression
 */
public cbit.vcell.parser.Expression getEquation() {
	return equation;
}


/**
 * Insert the method's description here.
 * Creation date: (1/19/2006 2:40:07 PM)
 * @return cbit.vcell.parser.Expression
 */
public cbit.vcell.parser.Expression getSolution() {
	return solution;
}


/**
 * Insert the method's description here.
 * Creation date: (1/17/2006 5:42:09 PM)
 * @return ncbc.physics2.component.Symbol
 */
public Symbol getSymbol() {
	return symbol;
}


/**
 * Insert the method's description here.
 * Creation date: (1/17/2006 7:18:07 PM)
 * @return int
 */
public int hashCode() {
	return symbol.hashCode()+equation.hashCode();
}


/**
 * Insert the method's description here.
 * Creation date: (1/20/2006 8:47:54 AM)
 * @return boolean
 */
public boolean isStateVariable() {
	return stateVariable;
}


/**
 * Insert the method's description here.
 * Creation date: (1/30/2006 11:35:03 AM)
 * @param newEquation cbit.vcell.parser.Expression
 */
public void setEquation(cbit.vcell.parser.Expression newEquation) {
	equation = newEquation;
}


/**
 * Insert the method's description here.
 * Creation date: (1/19/2006 2:40:07 PM)
 * @param newSolution cbit.vcell.parser.Expression
 */
public void setSolution(cbit.vcell.parser.Expression newSolution) {
	solution = newSolution;
}


/**
 * Insert the method's description here.
 * Creation date: (1/20/2006 8:47:54 AM)
 * @param newStateVariable boolean
 */
public void setStateVariable(boolean newStateVariable) {
	stateVariable = newStateVariable;
}


/**
 * Insert the method's description here.
 * Creation date: (1/17/2006 5:42:25 PM)
 * @return java.lang.String
 */
public String toString() {
	if (solution!=null){
		if (stateVariable){
			return symbol.getName()+Symbol.DERIVATIVE_SUFFIX+" = "+solution.infix();
		}else{
			return symbol.getName()+" = "+solution.infix();
		}
	}else{
		return symbol.getName()+"|"+equation.infix();
	}
}
}