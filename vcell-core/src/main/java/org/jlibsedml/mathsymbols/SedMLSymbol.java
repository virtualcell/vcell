package org.jlibsedml.mathsymbols;

import org.jmathml.ASTSymbol;
import org.jmathml.IEvaluationContext;

 /*
  * Base class for SED-ML symbols.
  */
 abstract class  SedMLSymbol extends ASTSymbol{

	public SedMLSymbol(String id) {
		super(id);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * All SED-ML symbols are vector operations, i.e., they operate on all of the values for a given variable.
	 */
	public boolean isVectorOperation() {
		// TODO Auto-generated method stub
		return true;
	}
	

	
	/**
	 * A SedML symbol currently has only one child, which must be an ASTCi node referring to time-series value.
	 */
	public boolean hasCorrectNumberChildren() {
		return getNumChildren() ==1;
	}

	protected boolean subclassCanEvaluate(IEvaluationContext context){
		if(firstChild().isVariable() && context.hasValueFor(firstChild().getName())) {
			return true;
		}
		return false;
	}
}
