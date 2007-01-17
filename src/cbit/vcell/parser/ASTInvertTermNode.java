package cbit.vcell.parser;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/* JJT: 0.2.2 */
import net.sourceforge.interval.ia_math.*;

public class ASTInvertTermNode extends SimpleNode {
  ASTInvertTermNode() {
    super(ExpressionParserTreeConstants.JJTINVERTTERMNODE);
  }
ASTInvertTermNode(int id) {
	super(id);
}
/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.parser.Node
 * @exception java.lang.Exception The exception description.
 */
public Node copyTree() {
	ASTInvertTermNode node = new ASTInvertTermNode();
	for (int i=0;i<jjtGetNumChildren();i++){
		node.jjtAddChild(jjtGetChild(i).copyTree());
	}
	return node;	
}
/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.parser.Node
 * @exception java.lang.Exception The exception description.
 */
public Node copyTreeBinary() {
	ASTInvertTermNode node = new ASTInvertTermNode();
	for (int i=0;i<jjtGetNumChildren();i++){
		node.jjtAddChild(jjtGetChild(i).copyTreeBinary());
	}
	return node;	
}
/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.parser.Expression
 * @param independentVariable java.lang.String
 * @exception java.lang.Exception The exception description.
 */
public Node differentiate(String independentVariable) throws ExpressionException {
	ASTMultNode multNode = new ASTMultNode();
	
	ASTMinusTermNode minusNode = new ASTMinusTermNode();
	minusNode.jjtAddChild(jjtGetChild(0).differentiate(independentVariable));
	
	ASTInvertTermNode invertNode = new ASTInvertTermNode(id);
	ASTFuncNode powNode = new ASTFuncNode();
	powNode.setFunction(ASTFuncNode.POW);
	powNode.jjtAddChild(jjtGetChild(0).copyTree());
	powNode.jjtAddChild(new ASTFloatNode(2.0));
	invertNode.jjtAddChild(powNode);
	
	multNode.jjtAddChild(minusNode);
	multNode.jjtAddChild(invertNode);
	
	return multNode;
}
public double evaluateConstant() throws ExpressionException, DivideByZeroException {
	double childValue = jjtGetChild(0).evaluateConstant();
	if (childValue==0.0){
		String childString = infixString(LANGUAGE_DEFAULT,NAMESCOPE_DEFAULT);
		throw new DivideByZeroException("divide by zero '"+childString+"'");
	}else{
		return (1.0 / childValue);
	}
}    
public RealInterval evaluateInterval(RealInterval intervals[]) throws ExpressionException, DivideByZeroException {

	RealInterval childInterval = jjtGetChild(0).evaluateInterval(intervals);

	if (childInterval.lo()==0.0 && childInterval.hi()==0.0){
		//
		// DIVIDE BY ZERO !!!!!
		//
		// form error message for user's consumption.
		//
		String errorMsg = "divide by zero, divisor is \""+jjtGetChild(0).infixString(LANGUAGE_DEFAULT,NAMESCOPE_DEFAULT)+"\"";
		String symbols[] = jjtGetChild(0).getSymbols(LANGUAGE_DEFAULT,NAMESCOPE_DEFAULT);
		if (symbols!=null && symbols.length>0){
			errorMsg += "\n  where:\n";
			SymbolTableEntry symbolTableEntries[] = new SymbolTableEntry[symbols.length];
			for (int i=0;i<symbols.length;i++){
				symbolTableEntries[i] = jjtGetChild(0).getBinding(symbols[i]);
				errorMsg += "      " + symbolTableEntries[i].getName() + " = " + intervals[symbolTableEntries[i].getIndex()] + "\n";
			}
		}
		throw new DivideByZeroException(errorMsg);
	}else{
		setInterval(IAMath.div(new RealInterval(1.0),childInterval),intervals);
		return getInterval(intervals);
	}
}    
public double evaluateVector(double values[]) throws ExpressionException, DivideByZeroException {

	double childValue = jjtGetChild(0).evaluateVector(values);

	if (childValue == 0.0){
		//
		// DIVIDE BY ZERO !!!!!
		//
		// form error message for user's consumption.
		//
		String errorMsg = "divide by zero, divisor is \""+jjtGetChild(0).infixString(LANGUAGE_DEFAULT,NAMESCOPE_DEFAULT)+"\"";
		String symbols[] = jjtGetChild(0).getSymbols(LANGUAGE_DEFAULT,NAMESCOPE_DEFAULT);
		if (symbols!=null && symbols.length>0){
			errorMsg += "\n  where:\n";
			SymbolTableEntry symbolTableEntries[] = new SymbolTableEntry[symbols.length];
			for (int i=0;i<symbols.length;i++){
				symbolTableEntries[i] = jjtGetChild(0).getBinding(symbols[i]);
				try {
					if (symbolTableEntries[i].getExpression()!=null){
						errorMsg += "      " + symbolTableEntries[i].getName() + " = " + symbolTableEntries[i].getExpression().evaluateVector(values) + "\n";
					}else if (symbolTableEntries[i].getIndex()>-1){
						errorMsg += "      " + symbolTableEntries[i].getName() + " = " + values[symbolTableEntries[i].getIndex()] + "\n";
					}else {
						errorMsg += "      " + symbolTableEntries[i].getName() + " = <<<UNBOUND IDENTIFIER>>>\n";
					}
				}catch (Throwable e){
					System.out.println("ASTInvertTermNode.evaluateVector() DIV-BY-ZERO, error evaluating "+symbols[i]);
					throw new ExpressionException(errorMsg + "      " + symbolTableEntries[i].getName() + " = <<<ERROR>>> " + e.getMessage()+"\n");
				}
			}
		}
		throw new DivideByZeroException(errorMsg);
	}else{
		return (1.0 / childValue);
	}
}    
/**
 * This method was created by a SmartGuide.
 * @exception java.lang.Exception The exception description.
 */
public Node flatten() throws ExpressionException {
	try {
		double value = evaluateConstant();
		if (value!=0.0){
			return new ASTFloatNode(value);
		}else{
			throw new DivideByZeroException("Divide by zero in "+toString());
		}
	}catch (DivideByZeroException e){
//		e.printStackTrace(System.out);
		throw e;
	}catch (ExpressionException e){
	}		

	if (jjtGetNumChildren()!=1){ 
		throw new Error("ASTInvertTermNode should have 1 child"); 
	}
	
	//
	// remove double invert
	//
	if (jjtGetChild(0) instanceof ASTInvertTermNode){
		return jjtGetChild(0).jjtGetChild(0).flatten();
	}
	
	ASTInvertTermNode invertNode = new ASTInvertTermNode();
	invertNode.jjtAddChild(jjtGetChild(0).flatten());	
	return invertNode;
}
/**
 * Insert the method's description here.
 * Creation date: (5/2/2003 2:17:08 PM)
 * @return java.lang.String
 * @param language int
 */
public String infixString(int language, NameScope nameScope) {
	// ASTMultNode handles division ... this node just passes it's child through
	return jjtGetChild(0).infixString(language,nameScope); 
}
/**
 * Insert the method's description here.
 * Creation date: (6/20/01 11:04:41 AM)
 * @return boolean
 */
public boolean narrow(RealInterval intervals[]) throws ExpressionBindingException{
	if (jjtGetNumChildren()!=1){
		throw new RuntimeException("Expected one child");
	}
	return IANarrow.narrow_div(getInterval(intervals),new RealInterval(1.0),jjtGetChild(0).getInterval(intervals))
			&& jjtGetChild(0).narrow(intervals)
			&& IANarrow.narrow_div(getInterval(intervals),new RealInterval(1.0),jjtGetChild(0).getInterval(intervals));
}
}
