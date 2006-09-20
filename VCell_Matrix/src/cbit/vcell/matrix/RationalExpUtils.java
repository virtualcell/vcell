package cbit.vcell.matrix;
import cbit.vcell.parser.ASTAddNode;
import cbit.vcell.parser.ASTAndNode;
import cbit.vcell.parser.ASTExpression;
import cbit.vcell.parser.ASTFloatNode;
import cbit.vcell.parser.ASTFuncNode;
import cbit.vcell.parser.ASTIdNode;
import cbit.vcell.parser.ASTInvertTermNode;
import cbit.vcell.parser.ASTLaplacianNode;
import cbit.vcell.parser.ASTMinusTermNode;
import cbit.vcell.parser.ASTMultNode;
import cbit.vcell.parser.ASTNotNode;
import cbit.vcell.parser.ASTOrNode;
import cbit.vcell.parser.ASTPowerNode;
import cbit.vcell.parser.ASTRelationalNode;
import cbit.vcell.parser.DerivativeNode;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.SimpleNode;
/**
 * This class may not exist in the future, and its functionality may be spread on one or more classes.
 
 * Creation date: (3/5/2004 4:37:34 PM)
 * @author: Rashad Badrawi
 */
public class RationalExpUtils {



	public static RationalExp getRationalExp(Expression exp) throws ExpressionException {
		return getRationalExp(exp.getRootNode());
	}


	private static RationalExp getRationalExp(SimpleNode node) throws ExpressionException {
		
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
			if (((ASTFuncNode)node).getFunction()==ASTFuncNode.POW){
				try {
					double constantExponent = node.jjtGetChild(1).evaluateConstant();
					if (constantExponent != Math.floor(constantExponent)){
						throw new ExpressionException("sub-expression "+node.infixString(SimpleNode.LANGUAGE_DEFAULT,null)+" cannot be translated to a Rational Expression, exponent is not an integer");
					}
					int intExponent = (int)constantExponent;
					if (intExponent == 0){
						return new RationalExp(1);
					}
					
					RationalExp base = getRationalExp((SimpleNode)node.jjtGetChild(0));
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
					return new RationalExp(1);
				}
				
				RationalExp base = getRationalExp((SimpleNode)node.jjtGetChild(0));
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
			RationalExp exp = new RationalExp(0);
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
			RationalExp exp = new RationalExp(1);
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
			cbit.vcell.matrix.RationalNumber r = cbit.vcell.matrix.RationalNumber.getApproximateFraction(((ASTFloatNode)node).getValue());
			return new RationalExp(r.getNum(),r.getDen());
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
			return new RationalExp(((ASTIdNode)node).getName());
		} else {
			throw new ExpressionException("node type "+node.getClass().toString()+" not supported yet");
		}
	}
}