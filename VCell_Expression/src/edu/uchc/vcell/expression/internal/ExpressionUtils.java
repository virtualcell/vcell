package edu.uchc.vcell.expression.internal;


import org.vcell.expression.ExpressionException;
import org.vcell.expression.ExpressionFactory;
import org.vcell.expression.IExpression;
import org.vcell.expression.ExpressionTerm.Operator;
/**
 * Insert the type's description here.
 * Creation date: (12/27/2002 1:37:29 PM)
 * @author schaff
 * @version $Revision: 1.0 $
 */
public class ExpressionUtils {
/**
 * Insert the method's description here.
 * Creation date: (12/22/2002 3:41:17 PM)
 * @param random java.util.Random
 * @param bIsConstraint boolean
 * @return int
 */
private static SimpleNode createNode(java.util.Random random, boolean bIsConstraint) {
	final int AddNode = 0;
	final int AndNode = 1;
	final int FloatNode = 2;
	final int FuncNode = 3;
	final int IDNode = 4;
	final int InvertNode = 5;
	final int MinusNode = 6;
	final int MultNode = 7;
	final int NotNode = 8;
	final int OrNode = 9;
	final int PowerNode = 10;
	final int RelationalNode = 11;

	final double nodeProbabilityNormal[] = {
		0.5,	// ADD
		0.25,	// AND
		0.5,	// FLOAT
		10.0,	// FUNCTION
		0.2,	// ID
		1.0,	// INVERT
		1.0,	// MINUS
		2.0,	// MULT
		0.3,	// NOT
		0.3,	// OR
		1,		// POWER
		0.3,	// RELATIONAL
	};
	final double nodeProbabilityConstraint[] = {
		2.0,	// ADD
		0.0,	// AND
		2.0,	// FLOAT
		1.0,	// FUNCTION
		1.0,	// ID
		1.0,	// INVERT
		1.0,	// MINUS
		2.0,	// MULT
		0.0,	// NOT
		0.0,	// OR
		1,		// POWER
		0.0,	// RELATIONAL
	};
		
	final Operator[] functionIDs = {
		Operator.ABS,
		Operator.ACOS,
		Operator.ASIN,
		Operator.ATAN,
		Operator.ATAN2,
		Operator.COS,
		Operator.EXP,
		Operator.LOG,
		Operator.MAX,
		Operator.MIN,
		Operator.POW,
		Operator.SIN,
		Operator.SQRT,
		Operator.TAN,
		Operator.CEIL,
		Operator.FLOOR,
		Operator.CSC,
		Operator.COT,
		Operator.SEC,
		Operator.ACSC,
		Operator.ACOT,
		Operator.ASEC,
		Operator.SINH,
		Operator.COSH,
		Operator.TANH,
		Operator.CSCH,
		Operator.COTH,
		Operator.SECH,
		Operator.ASINH,
		Operator.ACOSH,
		Operator.ATANH,
		Operator.ACSCH,
		Operator.ACOTH,
		Operator.ASECH,
		Operator.FACTORIAL
	};
	double nodeProbability[] = null;
	if (bIsConstraint){
		nodeProbability = nodeProbabilityConstraint;
	}else{
		nodeProbability = nodeProbabilityNormal;
	}
	double totalProb = 0;
	for (int i = 0; i < nodeProbability.length; i++){
		totalProb += nodeProbability[i];
	}
	double f = random.nextDouble()*totalProb;
	int nodeChoice = -1;
	double cumulativeProb = 0;
	for (int i = 0; i < nodeProbability.length; i++){
		cumulativeProb += nodeProbability[i];
		if (f < cumulativeProb){
			nodeChoice = i;
			break;
		}
	}
	switch (nodeChoice){
		case AddNode: {
			return new ASTAddNode();
		}
		case AndNode: {
			return new ASTAndNode();
		}
		case FloatNode: {
			if (random.nextFloat() > 0.1){
				return new ASTFloatNode(random.nextDouble());
			}else{
				return new ASTFloatNode(0.0);
			}
		}
		case FuncNode: {
			ASTFuncNode fn = new ASTFuncNode();
			double ftype = random.nextDouble();
			int index = (int)Math.min(functionIDs.length-1,Math.floor(functionIDs.length*ftype));
			fn.setFunction(functionIDs[index]);
			return fn;
		}
		case IDNode: {
			ASTIdNode idNode = new ASTIdNode();
			idNode.name = "id_"+((int)Math.abs(random.nextInt())%10);
			return idNode;
		}
		case InvertNode: {
			return new ASTInvertTermNode();
		}
		case MinusNode: {
			return new ASTMinusTermNode();
		}
		case MultNode: {
			return new ASTMultNode();
		}
		case NotNode: {
			return new ASTNotNode();
		}
		case OrNode: {
			return new ASTOrNode();
		}
		case PowerNode: {
			return new ASTPowerNode();
		}
		case RelationalNode: {
			ASTRelationalNode relNode = new ASTRelationalNode();
			double rtype = random.nextDouble();
			final String ops[] = {
				">",
				"<",
				"<=",
				">=",
				"==",
				"!="
			};
			relNode.setOperationFromToken(ops[(int)Math.floor(rtype*6)]);
			return relNode;
		}
		default: {
			throw new RuntimeException("f = "+f+", couldn't create a node");
		}
	}
}
/**
 * Insert the method's description here.
 * Creation date: (12/22/2002 4:23:34 PM)
 * @param random java.util.Random
 * @param bIsConstraint boolean
 * @return cbit.vcell.parser.SimpleNode
 */
private static SimpleNode createTerminalNode(java.util.Random random, boolean bIsConstraint) {
	double choice = random.nextDouble();
	double percentIdentifiers = (bIsConstraint)?(0.8):(0.5);

	if (choice < percentIdentifiers){
		ASTIdNode idNode = new ASTIdNode();
		idNode.name = "id_"+((int)Math.abs(random.nextInt())%10);
		return idNode;
	}else if (choice > 0.95){
		return new ASTFloatNode(0.0);
	}else{
		return new ASTFloatNode(random.nextDouble());
	}
}
/**
 * Insert the method's description here.
 * Creation date: (12/22/2002 5:02:53 PM)
 * @param random java.util.Random
 * @param maxDepth int
 * @param bIsConstraint boolean
 * @return cbit.vcell.parser.Expression
 * @throws ExpressionException
 */
public static IExpression generateExpression(java.util.Random random, int maxDepth, boolean bIsConstraint) throws ExpressionException {
	SimpleNode node = generateSubtree(0,maxDepth,random,bIsConstraint);
	if (bIsConstraint){
		node = (SimpleNode)node.copyTreeBinary();
	}
	return ExpressionFactory.createExpression(node.infixString(node.LANGUAGE_DEFAULT,node.NAMESCOPE_DEFAULT));
}
/**
 * Insert the method's description here.
 * Creation date: (12/22/2002 3:16:19 PM)
 * @param depth int
 * @param maxDepth int
 * @param random java.util.Random
 * @param bIsConstraint boolean
 * @return cbit.vcell.parser.Expression
 */
private static SimpleNode generateSubtree(int depth, int maxDepth, java.util.Random random, boolean bIsConstraint) {
	SimpleNode newNode = null;
	if (depth == 0 && bIsConstraint){
		ASTRelationalNode relNode = new ASTRelationalNode();
		double rtype = random.nextDouble();
		final String ops[] = {
			">",
			"<",
			"<=",
			">=",
			"==",
			//"!="
		};
		relNode.setOperationFromToken(ops[(int)Math.floor(rtype*ops.length)]);
		newNode = relNode;
	}else if (depth >= maxDepth){
		newNode = createTerminalNode(random,bIsConstraint);
	}else{
		newNode = createNode(random,bIsConstraint);
	}
	int numChildren = getNumberOfChildren(newNode);
	for (int i = 0; i < numChildren; i++){
		if (newNode instanceof ASTMultNode && i>0){  // InvertTerm is only ok if it's a second or later child of a mult node
			newNode.jjtAddChild(generateSubtree(depth+1,maxDepth,random,bIsConstraint));
		}else{
			SimpleNode child = null;
			while ((child = generateSubtree(depth+1,maxDepth,random,bIsConstraint)) instanceof ASTInvertTermNode){
			}
			newNode.jjtAddChild(child);
		}
	}
	return newNode;
}
/**
 * Insert the method's description here.
 * Creation date: (12/22/2002 3:41:17 PM)
 * @param node cbit.vcell.parser.SimpleNode
 * @return int
 */
private static int getNumberOfChildren(SimpleNode node) {
	if (node instanceof ASTAddNode){
		return 3;
	}else if (node instanceof ASTAndNode){
		return 2;
	}else if (node instanceof ASTFloatNode){
		return 0;
	}else if (node instanceof ASTFuncNode){
		ASTFuncNode fn = (ASTFuncNode)node;
		switch (fn.getFunction()){
			case ABS: {
				return 1;
			}
			case ACOS: {
				return 1;
			}
			case ASIN: {
				return 1;
			}
			case ATAN: {
				return 1;
			}
			case ATAN2: {
				return 2;
			}
			case COS: {
				return 1;
			}
			case EXP: {
				return 1;
			}
			case LOG: {
				return 1;
			}
			case MAX: {
				return 2;
			}
			case MIN: {
				return 2;
			}
			case POW: {
				return 2;
			}
			case SIN: {
				return 1;
			}
			case SQRT: {
				return 1;
			}
			case TAN: {
				return 1;
			}
			case CEIL: {
				return 1;
			}
			case FLOOR: {
				return 1;
			} 			
			case CSC: {
				return 1;
			}
			case COT: {
				return 1;
			}
			case SEC: {
				return 1;
			}
			case ACSC: {
				return 1;
			}
			case ACOT: {
				return 1;
			}
			case ASEC: {
				return 1;
			}
			case SINH: {
				return 1;
			}
			case COSH: {
				return 1;
			}
			case TANH: {
				return 1;
			}
			case CSCH: {
				return 1;
			}
			case COTH: {
				return 1;
			}
			case SECH: {
				return 1;
			}
			case ASINH: {
				return 1;
			}
			case ACOSH: {
				return 1;
			}
			case ATANH: {
				return 1;
			}
			case ACSCH: {
				return 1;
			}
			case ACOTH: {
				return 1;
			}
			case ASECH: {
				return 1;
			}			
			default:{
				throw new RuntimeException("unknown function type : "+fn.getFunction());
			}
		}
	}else if (node instanceof ASTIdNode){
		return 0;
	}else if (node instanceof ASTInvertTermNode){
		return 1;
	}else if (node instanceof ASTMinusTermNode){
		return 1;
	}else if (node instanceof ASTMultNode){
		return 3;
	}else if (node instanceof ASTNotNode){
		return 1;
	}else if (node instanceof ASTOrNode){
		return 2;
	}else if (node instanceof ASTPowerNode){
		return 2;
	}else if (node instanceof ASTRelationalNode){
		return 2;
	}else{
		throw new RuntimeException("unknown node type");
	}
}
}
