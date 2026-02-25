package cbit.vcell.parser;

public class FunctionRangeException extends ExpressionException{
public FunctionRangeException() { super(); }
/**
 * ExpressionException constructor comment.
 * @param s java.lang.String
 */
public FunctionRangeException(String s) {
	super(s);
}

public FunctionRangeException(Throwable cause){
	super(cause);
}

public FunctionRangeException(String s, Throwable e){
	super(s, e);
}
}
