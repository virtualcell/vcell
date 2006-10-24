package org.vcell.physics.component;

import org.vcell.expression.IExpression;

import cbit.vcell.units.VCUnitDefinition;

/**
 * Insert the type's description here.
 * Creation date: (11/17/2005 3:48:12 PM)
 * @author: Jim Schaff
 */
public class Symbol implements org.vcell.expression.SymbolTableEntry {
	public final static String INITIAL_SUFFIX = ".initial";
	public final static String DERIVATIVE_SUFFIX = ".prime"; 

	private String name = null;
	private org.vcell.expression.IExpression expression = null;
	private VCUnitDefinition unit = null;
	
	public Symbol(String argName) {
		this.name = argName;
		this.expression = null;
		this.unit = null;
	}
	public double getConstantValue() throws org.vcell.expression.ExpressionException {
		if (expression == null) {
			throw new RuntimeException("no value");
		} else {
			return expression.evaluateConstant();
		}
	}
	public IExpression getExpression() {
		return expression;
	}
	public int getIndex() {
		throw new RuntimeException("shouldn't evaluate these equations");
	}
	public String getName() {
		return name;
	}
	public org.vcell.expression.NameScope getNameScope() {
		return null;
	}
	public cbit.vcell.units.VCUnitDefinition getUnitDefinition() {
		return null;
	}
	public boolean isConstant() {
		if (expression == null) {
			return false;
		} else {
			try {
				expression.evaluateConstant();
				return true;
			} catch (org.vcell.expression.ExpressionException e) {
				return false;
			}
		}
	}
	public String toString() {
		return getClass().getName()
			+ "@"
			+ Integer.toHexString(hashCode())
			+ " "
			+ getName();
	}
}
