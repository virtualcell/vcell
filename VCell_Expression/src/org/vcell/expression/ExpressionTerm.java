package org.vcell.expression;


/**
 * Insert the type's description here.
 * Creation date: (1/23/2003 7:26:12 PM)
 * @author: Jim Schaff
 */
public class ExpressionTerm {
	public enum OperatorType { ARITHMETIC, DIFFERENTIAL, FUNCTION, RELATIONAL, LOGICAL, MISC };
	public enum Operator {
		ADD(OperatorType.ARITHMETIC),
		MINUS(OperatorType.ARITHMETIC),
		MULT(OperatorType.ARITHMETIC),
		INVERT(OperatorType.ARITHMETIC),
		PARENTHESIS(OperatorType.ARITHMETIC),
		POWEROP(OperatorType.ARITHMETIC),
		FLOAT(OperatorType.ARITHMETIC),
		IDENTIFIER(OperatorType.ARITHMETIC),
		
		DER(OperatorType.DIFFERENTIAL),
		LAPLACIAN(OperatorType.DIFFERENTIAL),
		
		EXP(OperatorType.FUNCTION,"exp"),
		SQRT(OperatorType.FUNCTION,"sqrt"),
		ABS(OperatorType.FUNCTION,"abs"),
		POW(OperatorType.FUNCTION,"pow"),
		LOG(OperatorType.FUNCTION,"log"),
		SIN(OperatorType.FUNCTION,"sin"),
		COS(OperatorType.FUNCTION,"cos"),
		TAN(OperatorType.FUNCTION,"tan"),
		ASIN(OperatorType.FUNCTION,"asin"),
		ACOS(OperatorType.FUNCTION,"acos"),
		ATAN(OperatorType.FUNCTION,"atan"),
		ATAN2(OperatorType.FUNCTION,"atan2"),
		MAX(OperatorType.FUNCTION,"max"),
		MIN(OperatorType.FUNCTION,"min"),
		CEIL(OperatorType.FUNCTION,"ceil"),
		FLOOR(OperatorType.FUNCTION,"floor"),
		CSC(OperatorType.FUNCTION,"csc"),
		COT(OperatorType.FUNCTION,"cot"),
		SEC(OperatorType.FUNCTION,"sec"),
		ACSC(OperatorType.FUNCTION,"acsc"),
		ACOT(OperatorType.FUNCTION,"acot"),
		ASEC(OperatorType.FUNCTION,"asec"),
		SINH(OperatorType.FUNCTION,"sinh"),
		COSH(OperatorType.FUNCTION,"cosh"),
		TANH(OperatorType.FUNCTION,"tanh"),
		CSCH(OperatorType.FUNCTION,"csch"),
		COTH(OperatorType.FUNCTION,"coth"),
		SECH(OperatorType.FUNCTION,"sech"),
		ASINH(OperatorType.FUNCTION,"asinh"),
		ACOSH(OperatorType.FUNCTION,"acosh"),
		ATANH(OperatorType.FUNCTION,"atanh"),
		ACSCH(OperatorType.FUNCTION,"acsch"),
		ACOTH(OperatorType.FUNCTION,"acoth"),
		ASECH(OperatorType.FUNCTION,"asech"),
		FACTORIAL(OperatorType.FUNCTION,"factorial"),
		LOG_10(OperatorType.FUNCTION,"log10"),
		LOGBASE(OperatorType.FUNCTION,"logbase"),
		FIELD(OperatorType.FUNCTION,"logbase"),
		
		
		AND(OperatorType.LOGICAL),
		OR(OperatorType.LOGICAL),
		NOT(OperatorType.LOGICAL),
		
		LT(OperatorType.RELATIONAL),
		LE(OperatorType.RELATIONAL),
		GT(OperatorType.RELATIONAL),
		GE(OperatorType.RELATIONAL),
		EQ(OperatorType.RELATIONAL),
		NE(OperatorType.RELATIONAL),
		
		ASSIGN(OperatorType.MISC);

		public final OperatorType opType;
		public final String functionName;
		Operator(OperatorType argOpType){
			opType = argOpType;
			functionName = null;
		}
		Operator(OperatorType argOpType,String argFunctionName){
			opType = argOpType;
			functionName = argFunctionName;
		}
	};
	private Operator fieldOperator = null;
	private java.lang.String fieldOpString = null;
	private IExpression[] fieldOperands = null;
	/**
	 * ExpressionTerm constructor comment.
	 */
	public ExpressionTerm(Operator operator, String opString, IExpression operands[]) {
		super();
		this.fieldOpString = opString;
		this.fieldOperands = operands;
		this.fieldOperator = operator;
	}
/**
 * Gets the operands property (cbit.vcell.parser.Expression[]) value.
 * @return The operands property value.
 * @see #setOperands
 */
public IExpression[] getOperands() {
	return fieldOperands;
}
/**
 * Gets the operator property (java.lang.String) value.
 * @return The operator property value.
 * @see #setOperator
 */
public Operator getOperator() {
	return fieldOperator;
}
public String getOpString() {
	return fieldOpString;
}
}
