package org.vcell.expression;

public interface IRationalExpression {

	/**
	 * Insert the method's description here.
	 * Creation date: (3/27/2003 12:50:27 PM)
	 * @return cbit.vcell.mapping.RationalNumber
	 * @param num cbit.vcell.mapping.RationalNumber
	 */
	public abstract IRationalExpression add(IRationalExpression rational);

	/**
	 * Insert the method's description here.
	 * Creation date: (3/27/2003 12:50:27 PM)
	 * @return cbit.vcell.mapping.RationalNumber
	 * @param num cbit.vcell.mapping.RationalNumber
	 */
	public abstract IRationalExpression div(IRationalExpression rational);

	/**
	 * Insert the method's description here.
	 * Creation date: (3/31/2003 2:57:06 PM)
	 * @return boolean
	 */
	public abstract RationalNumber getConstant();

	/**
	 * Insert the method's description here.
	 * Creation date: (3/28/2003 5:48:52 PM)
	 * @return java.lang.String
	 */
	public abstract String infixString();

	/**
	 * Insert the method's description here.
	 * Creation date: (3/30/2003 11:38:27 PM)
	 * @return cbit.vcell.matrixtest.RationalExp
	 */
	public abstract IRationalExpression inverse();

	/**
	 * Insert the method's description here.
	 * Creation date: (3/31/2003 2:57:06 PM)
	 * @return boolean
	 */
	public abstract boolean isConstant();

	/**
	 * Insert the method's description here.
	 * Creation date: (3/30/2003 11:42:59 PM)
	 * @return boolean
	 */
	public abstract boolean isZero();

	/**
	 * Insert the method's description here.
	 * Creation date: (3/30/2003 11:52:52 PM)
	 * @return cbit.vcell.matrixtest.RationalExp
	 */
	public abstract IRationalExpression minus();

	/**
	 * Insert the method's description here.
	 * Creation date: (3/27/2003 12:50:27 PM)
	 * @return cbit.vcell.mapping.RationalNumber
	 * @param num cbit.vcell.mapping.RationalNumber
	 */
	public abstract IRationalExpression mult(IRationalExpression rational);

	/**
	 * Insert the method's description here.
	 * Creation date: (4/22/2006 2:09:08 PM)
	 */
	public abstract IRationalExpression simplify();

	/**
	 * Insert the method's description here.
	 * Creation date: (3/27/2003 12:50:27 PM)
	 * @return cbit.vcell.mapping.RationalNumber
	 * @param num cbit.vcell.mapping.RationalNumber
	 */
	public abstract IRationalExpression sub(IRationalExpression rational);

}