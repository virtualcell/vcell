package cbit.vcell.parser;

public class ExpressionArithmeticException extends ExpressionException {
public ExpressionArithmeticException() { super(); }
/**
 * ExpressionException constructor comment.
 * @param s java.lang.String
 */
public ExpressionArithmeticException(String s) {
	super(s);
}

public ExpressionArithmeticException(Throwable cause){
	super(cause);
}

public ExpressionArithmeticException(String s, Throwable e){
	super(s, e);
}
}
