package edu.uchc.vcell.expression.internal;

import java.util.Random;
import java.util.StringTokenizer;

import org.jdom.Element;
import org.vcell.expression.ExpressionException;
import org.vcell.expression.ExpressionTerm;
import org.vcell.expression.IExpression;
import org.vcell.expression.IExpressionProvider;
import org.vcell.expression.LambdaFunction;
import org.vcell.expression.RationalExpression;
import org.vcell.expression.RationalNumber;
import org.vcell.expression.ExpressionTerm.Operator;

import edu.uchc.vcell.expression.internal.Expression;
import edu.uchc.vcell.expression.internal.ExpressionMathMLParser;
import edu.uchc.vcell.expression.internal.ExpressionUtils;

public class ExpressionFactory implements IExpressionProvider {

	public IExpression add(IExpression expression1, IExpression expression2) throws ExpressionException {
		return Expression.add((Expression)expression1, (Expression)expression2);
	}

	public IExpression invert(IExpression expression) throws ExpressionException {
		return Expression.invert((Expression)expression);
	}

	public IExpression mult(IExpression expression1, IExpression expression2) throws ExpressionException {
		return Expression.mult((Expression)expression1, (Expression)expression2);
	}

	public IExpression negate(IExpression expression) throws ExpressionException {
		return Expression.negate((Expression)expression);
	}

	public IExpression power(IExpression expression1, IExpression expression2) throws ExpressionException {
		return Expression.power((Expression)expression1, (Expression)expression2);
	}

	public IExpression createExpression(double value) {
		return new Expression(value);
	}

	public IExpression createExpression(IExpression exp) {
		return new Expression((Expression)exp);
	}

	public IExpression createExpression(String expString) throws ExpressionException {
		return new Expression(expString);
	}

	public IExpression createExpression(StringTokenizer tokens) throws ExpressionException {
		return new Expression(tokens);
	}

	public IExpression createRandomExpression(Random random, int maxDepth, boolean bIsConstraint) throws ExpressionException {
		return ExpressionUtils.generateExpression(random, maxDepth, bIsConstraint);
	}

	public IExpression createSubstitutedExpression(IExpression expression, IExpression origExp, IExpression newExp) throws ExpressionException {
		return ((Expression)expression).getSubstitutedExpression((Expression)origExp, (Expression)newExp);
	}

	public IExpression assign(IExpression lvalueExp, IExpression rvalueExp) throws ExpressionException {
		return Expression.assign((Expression)lvalueExp, (Expression)rvalueExp);
	}

	public IExpression derivative(String variable, IExpression expression) throws ExpressionException {
		return Expression.derivative(variable, (Expression)expression);
	}

	public IExpression laplacian(IExpression expression) throws ExpressionException {
		return Expression.laplacian((Expression)expression);
	}

	public IExpression fromMathML(Element mathElement, LambdaFunction[] lambdaFunctions) throws ExpressionException {
		return (new ExpressionMathMLParser(lambdaFunctions)).fromMathML(mathElement);
	}

	public IExpression fromMathML(Element mathElement) throws ExpressionException {
		return (new ExpressionMathMLParser(null)).fromMathML(mathElement);
	}
	
	public IExpression fromMathML(String mathMLString) throws ExpressionException {
		return (new ExpressionMathMLParser(null)).fromMathML(mathMLString);
	}

	/**
	 * Method getRationalExp.
	 * @param exp IExpression
	 * @return RationalExpression
	 * @throws ExpressionException
	 */
	public RationalExpression getRationalExpression(IExpression exp) throws ExpressionException {
		return getRationalExp(((Expression)exp).getRootNode());
	}

	public ExpressionTerm extractTopLevelTerm(IExpression exp) throws ExpressionException {
		return ((Expression)exp).extractTopLevelTerm();
	}
	/**
	 * Method getRationalExp.
	 * @param node SimpleNode
	 * @return RationalExpression
	 * @throws ExpressionException
	 */
	private static RationalExpression getRationalExp(SimpleNode node) throws ExpressionException {
		
		if (node == null) {
			return null;
		}
		if (node instanceof ASTAndNode || 
			node instanceof ASTOrNode || 
			node instanceof ASTNotNode || 
			node instanceof ASTRelationalNode ||
			node instanceof DerivativeNode ||
			node instanceof ASTLaplacianNode
			) {
			throw new ExpressionException("sub-expression "+node.infixString(SimpleNode.LANGUAGE_DEFAULT,null)+" cannot be translated to a Rational Expression");
		} else if (node instanceof ASTFuncNode) {
			if (((ASTFuncNode)node).getFunction()==Operator.POW){
				try {
					double constantExponent = node.jjtGetChild(1).evaluateConstant();
					if (constantExponent != Math.floor(constantExponent)){
						throw new ExpressionException("sub-expression "+node.infixString(SimpleNode.LANGUAGE_DEFAULT,null)+" cannot be translated to a Rational Expression, exponent is not an integer");
					}
					int intExponent = (int)constantExponent;
					if (intExponent == 0){
						return new RationalExpression(1);
					}
					
					RationalExpression base = getRationalExp((SimpleNode)node.jjtGetChild(0));
					if (intExponent < 0){
						base = base.inverse();
						intExponent = -intExponent;
					}
					for (int i = 1; i < intExponent; i++){
						base = base.mult(base);
					}
					return base;
				}catch (ExpressionException e){
					throw new ExpressionException("sub-expression "+node.infixString(SimpleNode.LANGUAGE_DEFAULT,null)+" cannot be translated to a Rational Expression, exponent is not constant");
				}
			}else{
				throw new ExpressionException("sub-expression "+node.infixString(SimpleNode.LANGUAGE_DEFAULT,null)+" cannot be translated to a Rational Expression");
			}
		} else if (node instanceof ASTPowerNode) {
			try {
				double constantExponent = node.jjtGetChild(1).evaluateConstant();
				if (constantExponent != Math.floor(constantExponent)){
					throw new ExpressionException("sub-expression "+node.infixString(SimpleNode.LANGUAGE_DEFAULT,null)+" cannot be translated to a Rational Expression, exponent is not an integer");
				}
				int intExponent = (int)constantExponent;
				if (intExponent == 0){
					return new RationalExpression(1);
				}
				
				RationalExpression base = getRationalExp((SimpleNode)node.jjtGetChild(0));
				if (intExponent < 0){
					base = base.inverse();
					intExponent = -intExponent;
				}
				for (int i = 1; i < intExponent; i++){
					base = base.mult(base);
				}
				return base;
			}catch (ExpressionException e){
				throw new ExpressionException("sub-expression "+node.infixString(SimpleNode.LANGUAGE_DEFAULT,null)+" cannot be translated to a Rational Expression, exponent is not constant");
			}
		} else if (node instanceof ASTAddNode) {
			RationalExpression exp = new RationalExpression(0);
			for (int i = 0; i < node.jjtGetNumChildren(); i++) {
				exp = exp.add(getRationalExp((SimpleNode)node.jjtGetChild(i)));
			}
			return exp;
		} else if (node instanceof ASTMinusTermNode) {
			return getRationalExp((SimpleNode)node.jjtGetChild(0)).minus();
		} else if (node instanceof ASTMultNode) {
			if (node.jjtGetNumChildren() == 1) {
				return getRationalExp((SimpleNode)node.jjtGetChild(0));
			}
			RationalExpression exp = new RationalExpression(1);
			for (int i = 0; i < node.jjtGetNumChildren(); i++) {             
				exp = exp.mult(getRationalExp((SimpleNode)node.jjtGetChild(i)));
			}
			return exp;
		} else if (node instanceof ASTInvertTermNode) {
			SimpleNode child = (SimpleNode)node.jjtGetChild(0);
			return getRationalExp(child).inverse();
	    } else if (node instanceof ASTExpression) {
			return getRationalExp((SimpleNode)node.jjtGetChild(0));
		} else if (node instanceof ASTFloatNode) {          //return TBD instead of dimensionless.
			RationalNumber r = RationalNumber.getApproximateFraction(((ASTFloatNode)node).getValue());
			return new RationalExpression(r.getNum(), r.getDen());
		//} else if (node instanceof ASTFuncNode) {   
			//String functionName = ((ASTFuncNode)node).getName();
			//if (functionName.equalsIgnoreCase("pow")) {       
				//SimpleNode child0 =  (SimpleNode)node.jjtGetChild(0);
				//SimpleNode child1 =  (SimpleNode)node.jjtGetChild(1);
				//VCUnitDefinition unit0 = getRationalExp(child0);
				//VCUnitDefinition unit1 = getRationalExp(child1);
				//if (!unit1.compareEqual(VCUnitDefinition.UNIT_DIMENSIONLESS) && !unit1.compareEqual(VCUnitDefinition.UNIT_TBD)){
					//throw new VCUnitException("exponent of '"+node.infixString(SimpleNode.LANGUAGE_DEFAULT,SimpleNode.NAMESCOPE_DEFAULT)+"' has units of "+unit0);
				//}
				//if (unit0.compareEqual(VCUnitDefinition.UNIT_DIMENSIONLESS) || unit0.isTBD()){
					//return unit0;
				//}
				//try {
					//double d = ((SimpleNode)node.jjtGetChild(1)).evaluateConstant();
					//RationalNumber rn = RationalNumber.getApproximateFraction(d);
					//return unit0.raiseTo(rn);
				//}catch(ExpressionException e){
					//return VCUnitDefinition.UNIT_TBD;  // ????? don't know the unit now
				//}
			//}
		//} else if (node instanceof ASTPowerNode) {
			//SimpleNode child0 =  (SimpleNode)node.jjtGetChild(0);
			//SimpleNode child1 =  (SimpleNode)node.jjtGetChild(1);
			//VCUnitDefinition unit0 = getRationalExp(child0);
			//VCUnitDefinition unit1 = getRationalExp(child1);
			//if (!unit1.compareEqual(VCUnitDefinition.UNIT_DIMENSIONLESS) && !unit1.compareEqual(VCUnitDefinition.UNIT_TBD)){
				//throw new VCUnitException("exponent of '"+node.infixString(SimpleNode.LANGUAGE_DEFAULT,SimpleNode.NAMESCOPE_DEFAULT)+"' has units of "+unit0);
			//}
			//if (unit0.compareEqual(VCUnitDefinition.UNIT_DIMENSIONLESS)){
				//return VCUnitDefinition.UNIT_DIMENSIONLESS;
			//}
			//boolean bConstantExponent = false;
			//double exponentValue = 1;
			//try {
				//exponentValue = child1.evaluateConstant();
				//bConstantExponent = true;
			//}catch(ExpressionException e){
				//bConstantExponent = false;
			//}
			//if (bConstantExponent){ //
				//if (unit0.isTBD()){
					//return VCUnitDefinition.UNIT_TBD;
				//}else{
					//RationalNumber rn = RationalNumber.getApproximateFraction(exponentValue);
					//return unit0.raiseTo(rn);
				//}
			//}else{
				//return VCUnitDefinition.UNIT_TBD;
			//}
		} else if (node instanceof ASTIdNode) {
			return new RationalExpression(((ASTIdNode)node).getName());
		} else {
			throw new ExpressionException("node type "+node.getClass().toString()+" not supported yet");
		}
	}

}
